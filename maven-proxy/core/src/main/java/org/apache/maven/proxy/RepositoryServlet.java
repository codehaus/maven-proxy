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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.fetch.exceptions.FetchException;
import org.apache.maven.fetch.exceptions.NotModifiedFetchException;
import org.apache.maven.fetch.exceptions.ResourceNotFoundFetchException;
import org.apache.maven.fetch.util.IOUtility;
import org.apache.maven.proxy.config.FileRepoConfiguration;
import org.apache.maven.proxy.config.RepoConfiguration;
import org.apache.maven.proxy.config.RetrievalComponentConfiguration;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class RepositoryServlet extends HttpServlet
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger( RepositoryServlet.class );

    private RetrievalComponentConfiguration rcc = null;

    private File localStoreDir;

    /* (non-Javadoc)
     * @see javax.servlet.Servlet#destroy()
     */
    public void destroy()
    {
        rcc = null;
        localStoreDir = null;
        super.destroy();
    }

    protected long getLastModified( HttpServletRequest request )
    {
        if (request.getPathInfo() == null) {
            return -1;
        }
        
        LOGGER.debug( "Checking getLastModified(): " + request.getPathInfo() );
        
        final File f = getFileForRequest( request );

        if ( f.exists() && f.isFile() )
        {
            return f.lastModified();
        }
        else
        {
            return super.getLastModified( request );
        }
    }

    public void init() throws ServletException
    {
        rcc = (RetrievalComponentConfiguration) getServletContext().getAttribute( "config" );

        localStoreDir = new File( rcc.getLocalStore() );
        if ( !localStoreDir.exists() )
        {
            LOGGER.info( "Local Repository (" + localStoreDir.getAbsolutePath() + ") does not exist" );
        }

    }

    public File getFileForRequest( HttpServletRequest request )
    {
        return new File( localStoreDir, request.getPathInfo() );
    }

    public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        final String pathInfo = request.getPathInfo();
        LOGGER.info( "Received request: " + pathInfo );

        //We were called with something like http://localhost:9999/repository  (need a trailing slash)
        if (pathInfo == null) {
            response.sendRedirect("./" + rcc.getPrefix() + "/");
            return;
        }
        
        
        if ( pathInfo.endsWith( "/" ) )
        {
            if ( rcc.isBrowsable() )
            {
                handleBrowseRequest( request, response );
            }
            else
            {
                response.sendError( HttpServletResponse.SC_FORBIDDEN );
            }
            return;
        }
        else
        {
            handleDownloadRequest( request, response );
            return;
        }
    }

    private void handleDownloadRequest( HttpServletRequest request, HttpServletResponse response )
                    throws FileNotFoundException, IOException
    {
        try
        {
            boolean done = false;
            List repos = rcc.getRepos();
            RetrievalComponent rc = new DefaultRetrievalComponent();
            //This whole thing is inside out.  It should only check repos if local file not found
            String pathInfo = request.getPathInfo();
            boolean isSnapshot = URLTool.isSnapshot( pathInfo );
            if ( isSnapshot )
            {
                System.out.println( "Ideally download a new snapshot of this file" );
            }

            File f = getFileForRequest(request);
            
            //Basically, we were asked for /repository/a but "a" is a directory, so we need to go to /repository/a/ 
            if (f.isDirectory()) {
                LOGGER.info("Redirecting /repository/a -> /repository/a/");
                response.sendRedirect(request.getRequestURI() + "/");
                return;
            }
            f.getParentFile().mkdirs();
            
            
            for ( int i = 0; i < repos.size(); i++ )
            {
                RepoConfiguration repoConfig = (RepoConfiguration) repos.get( i );

                try
                {
                    long size = -1;
                    long lastModified = -1;

                    InputStream is;
                    if ( !isSnapshot && f.exists() )
                    {
                        LOGGER.info( "Retrieving from cache: " + f.getAbsolutePath() );
                        is = new FileInputStream( f );
                        size = f.length();
                        lastModified = f.lastModified();
                    }
                    else
                    {
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
                            LOGGER.info( "Snapshot update not required : " + f.getAbsolutePath() );
                            is = new FileInputStream( f );
                            size = f.length();
                            lastModified = f.lastModified();
                        }
                    }

                    response.setContentType( "application/x-jar" );
                    response.setDateHeader( "Last-Modified", lastModified );
                    if ( size != -1 )
                    {
                        response.setContentLength( (int) size );
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
    }

    /**
     * @param request
     * @param response
     */
    private void handleBrowseRequest( HttpServletRequest request, HttpServletResponse response ) throws IOException
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

        PrintWriter pw = response.getWriter();

        pw.println( "<html>" );
        pw.println( "<head>" );
        pw.println( "  <title>maven-proxy</title>" );
        pw.println( "  <link type='text/css' rel='stylesheet' href='" + retrace + "/styles/style.css'/>" );
        pw.println( "</head>" );
        pw.println( "<body>" );
        pw.println( "<div>Browsing " + pathInfo + "</div>" );
        File dir = new File( localStoreDir, pathInfo );
        File[] files = dir.listFiles();
        if ( files == null )
        {
            files = new File[0];
        }
        List repos = rcc.getRepos();
        Set fileList = new TreeSet( new FileElementComparator( repos ) );

        fileList.addAll( MergedFileList.filenames( files, null ) );

        for ( int i = 0; i < repos.size(); i++ )
        {
            RepoConfiguration repoConfig = (RepoConfiguration) repos.get( i );
            if ( repoConfig instanceof FileRepoConfiguration )
            {
                //FileRepoConfiguration frc = (FileRepoConfiguration) repoConfig;
                String path = repoConfig.getUrl().substring( 8 );
                File fPath = new File( path, pathInfo );
                File[] newfiles = fPath.listFiles();
                fileList.addAll( MergedFileList.filenames( newfiles, repoConfig ) );
            }
        }

        pw.println( "<table width='100%'>" );
        pw.println( "<colgroup>" );
        pw.println( "  <col width='20px'>" ); //Icon
        pw.println( "  <col width='50px'>" ); //Size
        pw.println( "  <col width='50px'>" ); //Last Mod
        pw.println( "  <col width='*'>" ); //URL / name
        pw.println( "  <col width='*'>" ); //Something else?
        pw.println( "  <col width='5*'>" ); //Something else?
        pw.println( "</colgroup>" );

        pw.println( "<tr class='dir-a'><td></td><td>Size</td><td>Name</td><td>Repository</td><td></td></tr>" );

        char toggle = 'a';

        if ( !pathInfo.equals( "/" ) )
        {
            toggle = ( toggle == 'a' ? 'b' : 'a' );
            pw
                            .println( "<tr class='dir-"
                                            + toggle
                                            + "'><td><img src='"
                                            + retrace
                                            + "/images/parent.png' alt=''/></td><td></td><td></td><td><a href='..'>..</a></td><td></td><td></td></tr>" );
        }
        else
        {
            //            pw.println(
            //                            "<tr class='dir-" + toggle + "'><td><img src='/parent.png' alt=''/></td><td></td><td>Already at root folder</td><td></td></tr>");
        }

        //Collections.sort(fileArray, new FileComparator());
        DateFormat df = new SimpleDateFormat();
        for ( Iterator fileIter = fileList.iterator(); fileIter.hasNext(); )
        {
            FileElement fe = (FileElement) fileIter.next();

            toggle = ( toggle == 'a' ? 'b' : 'a' );
            File theFile = fe.getFile();
            String repoDescription;

            if ( fe.getRepo() != null )
            {
                repoDescription = fe.getRepo().getDescription();
            }
            else
            {
                repoDescription = "Global Repository";
            }

            if ( fe.getFile().isDirectory() )
            {
                pw.println( "<tr class='dir-" + toggle + "'><td><img src='" + retrace
                                + "/images/folder.png' alt=''/><td></td><td></td></td><td><a href='" + theFile.getName() + "/'>"
                                + theFile.getName() + "</a></td><td>" + repoDescription + "</td><td></td></tr>" );
            }
            else
            {
                pw.println( "<tr class='file-" + toggle + "'><td><img src='" + retrace
                                + "/images/jar.png' alt=''/></td><td>" + theFile.length() + "</td><td>" + df.format(new Date(theFile.lastModified())) + "</td><td><a href='"
                                + theFile.getName() + "'>" + theFile.getName() + "</a></td><td>" + repoDescription
                                + "</td><td></td></tr>" );
            }
        }
        pw.println( "</table>" );
        pw.println( "</body>" );
        pw.println( "</html>" );
        return;

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