package org.apache.maven.proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Iterator;

import javax.servlet.http.HttpServletResponse;

import org.apache.maven.proxy.components.NotFoundProxyArtifact;
import org.apache.maven.proxy.components.ProxyArtifact;
import org.apache.maven.proxy.components.SnapshotCache;
import org.apache.maven.proxy.components.impl.DefaultSnapshotCache;
import org.apache.maven.proxy.components.impl.NoCacheSnapshotCache;
import org.apache.maven.proxy.config.FileRepoConfiguration;
import org.apache.maven.proxy.config.GlobalRepoConfiguration;
import org.apache.maven.proxy.config.RepoConfiguration;
import org.apache.maven.proxy.config.RetrievalComponentConfiguration;
import org.apache.maven.proxy.request.ProxyRequest;
import org.apache.maven.proxy.request.ProxyResponse;

/**
 * @author Ben Walding
 */
public class DownloadEngine
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger( DownloadEngine.class );

    private final RetrievalComponentConfiguration rcc;
    private final SnapshotCache snapshotCache;

    public DownloadEngine( RetrievalComponentConfiguration rcc )
    {
        this.rcc = rcc;

        if ( rcc.getSnapshotUpdate() )
        {
            //Multiply by 1000 because the config file is in seconds, but the cache is in milliseconds
            LOGGER.info( "Enabling snapshot cache with update interval of " + rcc.getSnapshotUpdateInterval()
                            + " seconds" );
            snapshotCache = new DefaultSnapshotCache( rcc.getSnapshotUpdateInterval() * 1000 );
        }
        else
        {
            LOGGER.info( "Disabling snapshot cache" );
            snapshotCache = new NoCacheSnapshotCache();
        }
    }

    public void process( ProxyRequest request, ProxyResponse response ) throws IOException
    {
        LOGGER.debug( "Request: source=" + request.getSourceDescription() + ", path=" + request.getPath()
                        + ", lastModified=" + request.getLastModified() + ", headOnly=" + request.isHeadOnly() );
        try
        {
            //If we try to update snapshots, and this is a snapshot
            if ( rcc.getSnapshotUpdate() && request.isSnapshot() )
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
        GlobalRepoConfiguration globalRepo = rcc.getGlobalRepo();

        for ( Iterator iter = rcc.getRepos().iterator(); iter.hasNext(); )
        {
            RepoConfiguration repo = (RepoConfiguration) iter.next();

            ProxyArtifact snapshot = repo.getMetaInformation( request.getPath() );

            if ( snapshot != null )
            {
                if ( request.isHeadOnly() )
                {
                    response.setLastModified( snapshot.getLastModified() );
                    response.setContentLength( (int) snapshot.getSize() );
                    response.sendOK();
                    return;
                }

                if ( snapshot.getLastModified() < request.getLastModified() )
                {
                    response.setLastModified( snapshot.getLastModified() );
                    response.setContentLength( (int) snapshot.getSize() );
                    response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
                    return;
                }

                File targetFile = null;
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

                //FIXME Send content type
                response.setLastModified( targetFile.lastModified() );
                response.setContentLength( (int) targetFile.length() );
                response.sendFile( targetFile );
                return;
            }
        }
        //Found nothing - 404
        response.sendError( HttpServletResponse.SC_NOT_FOUND );
    }

    /*
     * Finds the most up-to-date repository for a snapshot. Will use the cache
     */
    private ProxyArtifact findBestSnapshotProxyArtifact( ProxyRequest request, ProxyResponse response )
    {
        ProxyArtifact latestArtifact = snapshotCache.getSnapshot( request.getPath() );
        if ( latestArtifact != null )
        {
            LOGGER.info( latestArtifact.getRepo() + ": [CACHED] Contains the latest snapshot." );
            return latestArtifact;
        }

        //Find latest snapshot
        for ( Iterator iter = rcc.getRepos().iterator(); iter.hasNext(); )
        {
            final RepoConfiguration repo = (RepoConfiguration) iter.next();
            final ProxyArtifact pArtifact;
            try
            {
                pArtifact = repo.getMetaInformation( request.getPath() );
            }
            catch ( FileNotFoundException e )
            {
                LOGGER.debug( repo + ": Unable to find " + request.getPath() );
                continue;
            }
            catch ( Exception e )
            {
                if ( repo.getHardFail() )
                {
                    LOGGER.error( repo + ": Failure getting meta information for " + request.getPath() );
                    throw new RuntimeException( e );
                }

                LOGGER.warn( repo + ": Failure getting meta information for " + request.getPath() );
                continue;
            }

            if ( pArtifact == null )
            {
                continue;
            }

            //If we haven't found one, or this one is newer - it's the best
            if ( latestArtifact == null || pArtifact.getLastModified() > latestArtifact.getLastModified() )
            {
                latestArtifact = pArtifact;
                continue;
            }
        }
        return latestArtifact;
    }

    private void processSnapshot( ProxyRequest request, ProxyResponse response ) throws IOException
    {
        ProxyArtifact latestArtifact = findBestSnapshotProxyArtifact( request, response );

        if ( latestArtifact == null )
        {
            if ( rcc.getSnapshotCacheFailures() )
            {
                LOGGER.info( "Caching fact that " + request.getPath() + " was not found." );
                NotFoundProxyArtifact npa = new NotFoundProxyArtifact( rcc.getGlobalRepo(), request.getPath() );
                snapshotCache.setSnapshot( request.getPath(), npa );
            }
            response.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        snapshotCache.setSnapshot( request.getPath(), latestArtifact );

        if ( latestArtifact instanceof NotFoundProxyArtifact )
        {
            response.sendError( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        response.setLastModified( latestArtifact.getLastModified() );

        if ( request.isHeadOnly() )
        {
            //response.setLastModified( latestArtifact.getLastModified() );
            response.setContentLength( (int) latestArtifact.getSize() );
            response.sendOK();
            return;
        }

        /*
         * This will occur if the client has a newer version of the artifact than we can provide.
         */
        if ( request.getLastModified() >= latestArtifact.getLastModified() )
        {
            //response.setLastModified( latestArtifact.getLastModified() );
            response.setContentLength( (int) latestArtifact.getSize() );
            response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
            return;
        }

        GlobalRepoConfiguration globalRepo = rcc.getGlobalRepo();

        if ( !latestArtifact.getRepo().getCopy() )
        {
            try
            {
                File targetFile = ( (FileRepoConfiguration) latestArtifact.getRepo() ).getLocalFile( request.getPath() );
                LOGGER.info( latestArtifact + ": Sending " + request.getPath() );
                latestArtifact.getRepo().retrieveArtifact( targetFile, request.getPath() );
                //FIXME Send content type
                response.setLastModified( targetFile.lastModified() );
                response.setContentLength( (int) targetFile.length() );
                response.sendFile( targetFile );
                return;
            }
            catch ( Exception e )
            {
                e.printStackTrace();
            }
        }

        if ( latestArtifact.getRepo().getCopy() )
        {
            LOGGER.info( latestArtifact.getRepo() + " is set to copy, so copying in from source file" );
            LOGGER.info( "Copying " + request.getPath() + " from " + latestArtifact.getRepo() + " to " + globalRepo );

            //XXX need to encapsulate this reading call into something more common across repositories
            //File target = fileLatestRepo.getLocalFile( request.getPath() );
            File target = globalRepo.getLocalFile( request.getPath() );
            RetrievalDetails rd = download( latestArtifact.getRepo(), target, request );
            response.setLastModified( rd.getLastModified() );
            response.setContentLength( (int) rd.getLength() );
            response.sendFile( target );
            response.sendOK();
            return;
        }

        //LOGGER.info( "Got to the end of processing without doing anything useful!" );
        //globalRepo.
    }

    private RetrievalDetails download( RepoConfiguration source, File target, ProxyRequest request ) throws IOException

    {
        LOGGER.info( "Downloading " + request.getPath() + " from " + source );
        if ( request.isHeadOnly() )
        {
            throw new IllegalStateException( "The request was head only and you're trying to do a download" );
        }

        if ( !source.getCopy() )
        {
            LOGGER.info( source + ": Skipping copy phase." );
            return null;
        }
        return source.retrieveArtifact( target, request.getPath() );
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
        snapshotCache.stop();
        snapshotCache.start();
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