package org.apache.maven.proxy.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.apache.maven.proxy.components.NotFoundProxyArtifact;
import org.apache.maven.proxy.components.ProxyArtifact;
import org.apache.maven.proxy.config.FileRepoConfiguration;
import org.apache.maven.proxy.config.GlobalRepoConfiguration;
import org.apache.maven.proxy.config.RepoConfiguration;
import org.apache.maven.proxy.config.RetrievalComponentConfiguration;
import org.apache.maven.proxy.request.ProxyRequest;
import org.apache.maven.proxy.request.ProxyResponse;
import org.apache.maven.proxy.utils.IOUtility;
import org.apache.maven.proxy.utils.MimeTypes;

/**
 * @author Ben Walding
 */
public class DownloadEngine
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger( DownloadEngine.class );

    private final RetrievalComponentConfiguration rcc;

    public DownloadEngine( RetrievalComponentConfiguration rcc )
    {
        this.rcc = rcc;
    }

    public void process( ProxyRequest request, ProxyResponse response ) throws IOException
    {
        LOGGER.debug( "Request: source=" + request.getSourceDescription() + ", path=" + request.getPath()
                        + ", lastModified=" + request.getLastModified() + ", headOnly=" + request.isHeadOnly()
                        + ", ifModifiedSince=" + request.getIfModifiedSince() );
        try
        {
            //If we try to update snapshots, and this is a snapshot
            if ( ( rcc.getSnapshotUpdate() && request.isSnapshot() )
                    || ( rcc.getMetaDataUpdate() && request.isMetaData() )
                    || ( rcc.getPOMUpdate() && request.isPOM() ) )
            {
                processSnapshot( request, response );
                return;
            }

            processStandard( request, response );
        }
        catch ( Exception e )
        {
            LOGGER.error( e );
            response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }
    }

    private void processStandard( ProxyRequest request, ProxyResponse response ) throws IOException
    {
        String mimeType = getMimeType( request.getPath() );
        GlobalRepoConfiguration globalRepo = rcc.getGlobalRepo();

        for ( Iterator iter = rcc.getRepos().iterator(); iter.hasNext(); )
        {
            RepoConfiguration repo = (RepoConfiguration) iter.next();

            ProxyArtifact artifact = repo.getMetaInformation( request.getPath() );

            if ( artifact != null )
            {
                if ( request.isHeadOnly() )
                {
                    sendHeadResponse( response, mimeType, artifact );
                    return;
                }

                if ( artifact.getLastModified() < request.getLastModified() )
                {
                    sendNotModifiedResponse( response, mimeType, artifact );
                    return;
                }

                final File targetFile;
                if ( repo.getCopy() )
                {
                    LOGGER.info( "Copying " + request.getPath() + " from " + globalRepo + " to " + repo );
                    targetFile = globalRepo.getLocalFile( request.getPath() );
                    repo.retrieveArtifact( targetFile, request.getPath() );
                }
                else
                {
                    LOGGER.info( "Transferring " + request.getPath() + " directly to user from " + repo );
                    FileRepoConfiguration fileRepo = (FileRepoConfiguration) repo;
                    targetFile = fileRepo.getLocalFile( request.getPath() );
                }

                sendBodyResponse( response, mimeType, targetFile );
                return;
            }
        }
        //Found nothing - 404
        response.sendError( HttpServletResponse.SC_NOT_FOUND );
    }

    /**
     * @param response
     * @param mimeType
     * @param targetFile
     * @throws IOException
     */
    private void sendBodyResponse( ProxyResponse response, String mimeType, File targetFile ) throws IOException
    {
        response.setLastModified( targetFile.lastModified() );
        response.setContentType( mimeType );
        response.setContentLength( (int) targetFile.length() );
        response.sendFile( targetFile );
        response.sendOK();
    }

    /**
     * @param response
     * @param mimeType
     * @param artifact
     */
    private void sendHeadResponse( ProxyResponse response, String mimeType, ProxyArtifact artifact )
    {
        LOGGER.info( artifact.getRepo() + ": Sending HEAD meta-information" );
        response.setLastModified( artifact.getLastModified() );
        response.setContentType( mimeType );
        response.setContentLength( (int) artifact.getSize() );
        response.sendOK();
    }

    static String getMimeType( String path )
    {
        MimeTypes.MimeType mimeType = MimeTypes.getMimeTypeByPath( path );
        if ( mimeType == null )
        {
            return "application/octet-stream";
        }

        return mimeType.getMimeType();
    }

    /*
     * Finds the most up-to-date repository for a snapshot. Will use the cache
     */
    private ProxyArtifact findLatestProxyArtifact( ProxyRequest request, ProxyResponse response )
    {
        ProxyArtifact latestArtifact = null;

        //Find latest snapshot
        for ( Iterator iter = rcc.getRepos().iterator(); iter.hasNext(); )
        {
            final RepoConfiguration repo = (RepoConfiguration) iter.next();
            final ProxyArtifact pArtifact = repo.getMetaInformation( request.getPath() );

            if ( pArtifact == null )
            {
                continue;
            }

            LOGGER.info( repo + ": " + pArtifact.getPath() + " last modified " + pArtifact.getLastModified() );

            //If we haven't found one, or this one is newer - it's the best
            if ( latestArtifact == null || pArtifact.getLastModified() > latestArtifact.getLastModified() )
            {
                latestArtifact = pArtifact;
                continue;
            }
        }

        if ( latestArtifact == null )
        {
            latestArtifact = new NotFoundProxyArtifact( rcc.getGlobalRepo(), request.getPath() );
        }

        return latestArtifact;
    }

    private void processSnapshot( ProxyRequest request, ProxyResponse response ) throws IOException
    {
        String mimeType = getMimeType( request.getPath() );
        long modificationTime = retrieveModificationTime( request );

        ProxyArtifact latestArtifact = findLatestProxyArtifact( request, response );

        if ( latestArtifact instanceof NotFoundProxyArtifact )
        {
            LOGGER.info( "Artifact not found: " + request.getPath() );
            response.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        LOGGER.info( latestArtifact.getRepo() + ": Found most up-to-date version of " + request.getPath() );
        response.setLastModified( latestArtifact.getLastModified() );

        if ( request.isHeadOnly() )
        {
            sendHeadResponse( response, mimeType, latestArtifact );
            return;
        }

        /*
         * This will occur if the client has a newer version of the artifact than we can provide.
         */
        if ( modificationTime >= latestArtifact.getLastModified() )
        {
            sendNotModifiedResponse( response, mimeType, latestArtifact );
            return;
        }

        GlobalRepoConfiguration globalRepo = rcc.getGlobalRepo();
        final File downloadFile;

        if ( !latestArtifact.getRepo().getCopy() )
        {
            downloadFile = ( (FileRepoConfiguration) latestArtifact.getRepo() ).getLocalFile( request.getPath() );
            LOGGER.info( latestArtifact.getRepo() + ": Sending " + request.getPath() );
        }
        else
        {
            downloadFile = globalRepo.getLocalFile( request.getPath() );
            LOGGER.info( latestArtifact.getRepo() + ": Copying " + request.getPath() + " to " + globalRepo );
            latestArtifact.getRepo().retrieveArtifact( downloadFile, request.getPath() );
            LOGGER.info( globalRepo + ": Sending " + request.getPath() );
        }

        sendBodyResponse( response, mimeType, downloadFile );
        return;
    }

    /**
     * @param response
     * @param mimeType
     * @param artifact
     * @throws IOException
     */
    private void sendNotModifiedResponse( ProxyResponse response, String mimeType, ProxyArtifact artifact )
                    throws IOException
    {
        LOGGER.info( artifact.getRepo() + ": Sending NOT-MODIFIED response" );
        response.setContentType( mimeType );
        response.setContentLength( (int) artifact.getSize() );
        response.setLastModified( artifact.getLastModified() );
        response.sendError( HttpServletResponse.SC_NOT_MODIFIED );

    }

    /**
     * @param request
     * @return
     */
    private long retrieveModificationTime( ProxyRequest request )
    {
        if ( request.getLastModified() < 0 && request.getIfModifiedSince() < 0 )
        {
            LOGGER.warn( "Neither If-Modified-Since nor Last-Modified are set" );
            return -1;
        }

        if ( request.getLastModified() >= 0 && request.getIfModifiedSince() >= 0
                        && request.getLastModified() != request.getIfModifiedSince() )
        {
            LOGGER.warn( "If-Modified-Since (" + request.getIfModifiedSince() + ") AND Last-Modified ("
                            + request.getLastModified() + ") both set and unequal" );
        }

        return Math.max( request.getLastModified(), request.getIfModifiedSince() );
    }

    /**
     * Downloads a stream to a file, using a temporary file to ensure any problems are not written to target file
     *
     * I'm sure this code is required...
     * @param target
     * @param is
     */
    public static void download( File target, InputStream is, long lastModified )
    {
        File dir = target.getParentFile();
        dir.mkdirs();

        File tmpTarget = null;
        OutputStream os = null;
        try
        {
            tmpTarget = File.createTempFile( "tmp", ".tmp", dir );
            os = new FileOutputStream( tmpTarget );
            IOUtility.transferStream( is, os );
            IOUtility.close( os );
            IOUtility.close( is );
            target.delete();
            tmpTarget.renameTo( target );

            if ( !target.setLastModified( lastModified ) )
            {
                LOGGER.warn( target + ".setLastModified(" + lastModified + ") failed" );
            }

            if ( target.lastModified() != lastModified )
            {
                LOGGER.warn( target + ".setLastModified(" + lastModified + ") didn't stick - now "
                                + target.lastModified() );
            }
        }
        catch ( Exception e )
        {
            IOUtility.close( os );
            if ( tmpTarget != null )
            {
                tmpTarget.delete();
            }
        }
    }

    public void clearSnapshotCache() throws Exception
    {
        for ( Iterator iter = rcc.getRepos().iterator(); iter.hasNext(); )
        {
            RepoConfiguration repo = (RepoConfiguration) iter.next();
            repo.clearSnapshotCache();
        }
    }

    /**
     * Rounds a long value to the nearest 1000
     * It appears that Unix style systems don't store last modified times down to milliseconds. As such
     * we're going to have to strip off the final
     * This is possibly the most anal way of doing it, but I can't be bothered about thinking about the implications.
     */
    private static final BigDecimal DIVISOR = new BigDecimal( 1000 );

    public static long round( long input )
    {
        if ( input % DIVISOR.longValue() != 0 && input != -1 )
        {
            LOGGER.info( "Rounding a last modified value..." );
            BigDecimal bd = new BigDecimal( input );
            BigDecimal rounded = bd.divide( DIVISOR, BigDecimal.ROUND_HALF_EVEN );
            return rounded.multiply( DIVISOR ).longValue();
        }

        return input;
    }

}