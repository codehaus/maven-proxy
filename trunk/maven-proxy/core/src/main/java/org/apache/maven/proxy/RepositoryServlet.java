package org.apache.maven.proxy;

/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.fetch.exceptions.FetchException;
import org.apache.maven.fetch.exceptions.NotModifiedFetchException;
import org.apache.maven.fetch.exceptions.ResourceNotFoundFetchException;
import org.apache.maven.fetch.util.IOUtility;
import org.apache.maven.proxy.components.RetrievalComponent;
import org.apache.maven.proxy.components.SnapshotCache;
import org.apache.maven.proxy.components.impl.DefaultRetrievalComponent;
import org.apache.maven.proxy.components.impl.DefaultSnapshotCache;
import org.apache.maven.proxy.components.impl.NoCacheSnapshotCache;
import org.apache.maven.proxy.config.FileRepoConfiguration;
import org.apache.maven.proxy.config.RepoConfiguration;
import org.apache.maven.proxy.config.RetrievalComponentConfiguration;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class RepositoryServlet extends MavenProxyServlet
{

    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger( RepositoryServlet.class );

    private RetrievalComponentConfiguration rcc = null;

    static SnapshotCache snapshotCache;

    private File localStoreDir;

    public void destroy()
    {
        rcc = null;
        localStoreDir = null;
        super.destroy();
    }

    protected long getLastModified( HttpServletRequest request )
    {
        if ( request.getPathInfo() == null )
        {
            return -1;
        }

        LOGGER.debug( "Checking getLastModified(): " + request.getPathInfo() );

        final File f = getFileForRequest( request );

        if ( f.exists() && f.isFile() )
        {
            return f.lastModified();
        }

        return super.getLastModified( request );
    }

    private ThreadLocal dateFormatThreadLocal = new ThreadLocal()
    {
        protected synchronized Object initialValue()
        {
            DateFormat df;

            if ( rcc.getLastModifiedDateFormat() == null || rcc.getLastModifiedDateFormat() == "" )
            {
                df = new SimpleDateFormat();
            }
            else
            {
                df = new SimpleDateFormat( rcc.getLastModifiedDateFormat() );
            }

            df.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
            return df;
        }
    };

    public void init() throws ServletException
    {
        rcc = (RetrievalComponentConfiguration) getServletContext().getAttribute( "config" );

        localStoreDir = new File( rcc.getLocalStore() );

        if ( rcc.getSnapshotUpdate() )
        {
            //Multiply by 1000 because the config file is in seconds, but the cache is in milliseconds
            snapshotCache = new DefaultSnapshotCache( rcc.getSnapshotUpdateInterval() * 1000 );
        }
        else
        {
            snapshotCache = new NoCacheSnapshotCache();
        }

        if ( !localStoreDir.exists() )
        {
            LOGGER.info( "Local Repository (" + localStoreDir.getAbsolutePath() + ") does not exist" );
        }

    }

    public File getFileForRequest( HttpServletRequest request )
    {
        return new File( localStoreDir, request.getPathInfo() );
    }

    public Template handleRequestInternal( HttpServletRequest request, HttpServletResponse response, Context context )
                    throws Exception
    {
        final String pathInfo = request.getPathInfo();
        LOGGER.info( "Received request: " + pathInfo );

        //We were called with something like http://localhost:9999/repository  (need a trailing slash)
        if ( pathInfo == null )
        {
            response.sendRedirect( "./" + rcc.getPrefix() + "/" );
            return null;
        }

        if ( pathInfo.endsWith( "/" ) )
        {
            if ( rcc.isBrowsable() )
            {
                return handleBrowseRequest( request, response, context );
            }

            response.sendError( HttpServletResponse.SC_FORBIDDEN );
            return null;
        }

        handleDownloadRequest( request, response );
        return null;
    }

    private void handleDownloadRequest( HttpServletRequest request, HttpServletResponse response )
                    throws FileNotFoundException, IOException
    {
        String pathInfo = request.getPathInfo();
        DateFormat df = getDateFormat();

        try
        {
            boolean done = false;
            List repos = rcc.getRepos();
            RetrievalComponent rc = new DefaultRetrievalComponent();
            //This whole thing is inside out.  It should only check repos if local file not found
            boolean isSnapshot = URLTool.isSnapshot( pathInfo );
            if ( isSnapshot )
            {
                System.out.println( "Ideally download a new snapshot of this file" );
            }

            File f = getFileForRequest( request );

            //Basically, we were asked for /repository/a but "a" is a directory, so we need to go to /repository/a/ 
            if ( f.isDirectory() )
            {
                LOGGER.info( "Redirecting /repository/a -> /repository/a/" );
                response.sendRedirect( request.getRequestURI() + "/" );
                return;
            }

            //XXX This should really delay directory creation until we have a verified file downloaded.
            f.getParentFile().mkdirs();

            for ( int i = 0; i < repos.size(); i++ )
            {
                RepoConfiguration repoConfig = (RepoConfiguration) repos.get( i );

                try
                {
                    long size = -1;
                    long lastModified = -1;

                    InputStream is;/*
                     if ( !isSnapshot && f.exists() )
                     {
                     LOGGER.info( "Retrieving from cache: " + f.getAbsolutePath() );
                     is = new FileInputStream( f );
                     size = f.length();
                     lastModified = f.lastModified();
                     }
                     else
                     {*/
                    //So they've asked for a snapshot... lets check their headers and 304 them if they don't really want it
                    if ( isSnapshot )
                    {
                        long clientLastModified = request.getDateHeader( "Last-Modified" );
                        long cacheLastModified = snapshotCache.getLastModified( pathInfo );

                        LOGGER.debug( "Client requested a snapshot newer than "
                                        + df.format( new Date( clientLastModified ) ) );

                        if ( cacheLastModified > -1 )
                        {
                            LOGGER.debug( "Our snapsnot was modified at " + df.format( new Date( cacheLastModified ) ) );

                            if ( clientLastModified > cacheLastModified )
                            {
                                //Cache hit - no snapshot update required.
                                LOGGER.info( "Client already has latest version of snapshot. Sending SC_NOT_MODIFIED" );
                                response.setDateHeader( "Last-Modified", cacheLastModified );
                                response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
                                return;
                            }
                        }
                        else
                        {
                            LOGGER.debug( "We haven't seen this snapshot before, or it was last checked too long ago" );
                        }
                    }

                    //Now handle non snapshot requests
                    LOGGER.info( "Retrieving from upstream (" + repoConfig.getKey() + " " + f.getAbsolutePath() );
                    try
                    {
                        RetrievalDetails rd = rc.retrieveArtifact( repoConfig, f, pathInfo, isSnapshot );
                        is = rd.getInputStream();
                        size = rd.getLength();
                        lastModified = rd.getLastModified();
                    }
                    catch ( NotModifiedFetchException e )
                    {
                        LOGGER.info( "Update not required : " + f.getAbsolutePath() );
                        response.setDateHeader( "Last-Modified", f.lastModified() );
                        response.sendError( HttpServletResponse.SC_NOT_MODIFIED );
                        return;
                    }
                    /*
                     }
                     */
                    response.setContentType( "application/x-jar" );
                    response.setDateHeader( "Last-Modified", lastModified );
                    if ( size != -1 )
                    {
                        response.setContentLength( (int) size );
                    }

                    if ( isSnapshot )
                    {
                        snapshotCache.setLastModified( pathInfo, lastModified );
                    }

                    OutputStream os = response.getOutputStream();
                    IOUtility.transferStream( is, os );
                    IOUtility.close( is );
                    done = true;
                    break;
                }
                catch ( ResourceNotFoundFetchException ex )
                {
                    // if not found, just move on
                }
            }

            if ( !done )
            {
                LOGGER.warn( "Could not find upstream resource :" + pathInfo );
                response.sendError( HttpServletResponse.SC_NOT_FOUND, "Could not find " + pathInfo );
            }
        }
        catch ( FetchException e )
        {
            e.printStackTrace();
            response.sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage() );
        }
        finally
        {
            LOGGER.info( "Download request complete: " + pathInfo );
        }

    }

    /**
     * @param request
     * @param response
     * @param context
     * @throws Exception
     * @throws ParseErrorException
     * @throws Exception
     */
    private Template handleBrowseRequest( HttpServletRequest request, HttpServletResponse response, Context context )
                    throws Exception
    {
        final String pathInfo = request.getPathInfo();
        final String retrace;

        if ( rcc.getPrefix().length() == 0 )
        {
            retrace = URLTool.getRetrace( pathInfo );
        }
        else
        {
            retrace = URLTool.getRetrace( "/" + rcc.getPrefix() + pathInfo );
        }

        context.put( "retrace", retrace );
        context.put( "pathInfo", pathInfo );

        List repos = rcc.getRepos();
        Set fileElements = new TreeSet( new FileElementComparator( repos ) );

        /*
         File dir = new File( localStoreDir, pathInfo );
         File[] files = dir.listFiles();
         if ( files == null )
         {
         files = new File[0];
         }
         Set fileElements = new TreeSet( new FileElementComparator( repos ) );

         fileElements.addAll( MergedFileList.filenames( files, pathInfo, null ) );
         */

        //Now we merge in any local file repositories that might be configured
        for ( int i = 0; i < repos.size(); i++ )
        {
            RepoConfiguration repoConfig = (RepoConfiguration) repos.get( i );
            LOGGER.info( "Browsing " + repoConfig.getUrl() );
            if ( repoConfig instanceof FileRepoConfiguration )
            {
                //FileRepoConfiguration frc = (FileRepoConfiguration) repoConfig;
                String path = repoConfig.getUrl().substring( 8 );
                File fPath = new File( path, pathInfo );
                File[] newfiles = fPath.listFiles();
                fileElements.addAll( MergedFileList.filenames( newfiles, pathInfo, repoConfig ) );
            }
        }

        context.put( "fileElements", fileElements );
        context.put( "ab", new ABToggler() );
        context.put( "dateFormat", getDateFormat() );
        return getTemplate( "RepositoryServlet.vtl" );
    }

    public String getTopLevel()
    {
        return "REPOSITORY";
    }

    /**
     * Retrieves and casts the appropriate DateFormat object from the ThreadLocal
     * @return
     */
    private DateFormat getDateFormat()
    {
        return (DateFormat) dateFormatThreadLocal.get();
    }

}

/* A bit of code that slows down the transfer so you can see what is going on
 while (true) {
 byte[] b = new byte[1024];
 int read = is.read(b);
 if (read == -1) {
 break;
 }
 os.write(b, 0, read);
 try {
 Thread.sleep(1000);
 } catch (InterruptedException e1) {
 // TODO Auto-generated catch block
 e1.printStackTrace();
 }
 }
 */