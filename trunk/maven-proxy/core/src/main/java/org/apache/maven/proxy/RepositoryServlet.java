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
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.proxy.config.FileRepoConfiguration;
import org.apache.maven.proxy.config.RepoConfiguration;
import org.apache.maven.proxy.request.HttpProxyRequest;
import org.apache.maven.proxy.request.HttpProxyResponse;
import org.apache.maven.proxy.request.ProxyRequest;
import org.apache.maven.proxy.request.ProxyResponse;
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

    private static DownloadEngine downloadEngine;

    public void init() throws ServletException
    {
        super.init();
        downloadEngine = new DownloadEngine( getRCC() );
    }

    public static void clearSnapshotCache() throws Exception
    {
        downloadEngine.clearSnapshotCache();
    }

    public void destroy()
    {
        downloadEngine = null;
        super.destroy();
    }

    public Template handleRequestInternal( HttpServletRequest request, HttpServletResponse response, Context context )
                    throws Exception
    {
        final String pathInfo = request.getPathInfo();
        LOGGER.info( "Received request: " + pathInfo );

        //We were called with something like http://localhost:9999/repository  (need a trailing slash)
        if ( pathInfo == null )
        {
            response.sendRedirect( "./" + getRCC().getPrefix() + "/" );
            return null;
        }

        if ( pathInfo.endsWith( "/" ) )
        {
            if ( !getRCC().isBrowsable() )
            {
                response.sendError( HttpServletResponse.SC_FORBIDDEN, "maven-proxy is not browsable" );
                return null;
            }

            return handleBrowseRequest( request, response, context );
        }

        handleDownloadRequest( request, response );
        return null;
    }

    private void handleDownloadRequest( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        ProxyRequest proxyRequest = new HttpProxyRequest( request );
        ProxyResponse proxyResponse = new HttpProxyResponse( response );
        downloadEngine.process( proxyRequest, proxyResponse );
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
        context.put( "pathInfo", pathInfo );

        if ( getRCC().getPrefix().length() == 0 )
        {
            context.put( "retrace", URLTool.getRetrace( pathInfo ) );
        }
        else
        {
            context.put( "retrace", URLTool.getRetrace( "/" + getRCC().getPrefix() + pathInfo ) );
        }

        Set fileElements = new TreeSet( new FileElementComparator( getRCC().getRepos() ) );
        for ( Iterator iter = getRCC().getRepos().iterator(); iter.hasNext(); )
        {
            RepoConfiguration repoConfig = (RepoConfiguration) iter.next();
            LOGGER.info( "Browsing " + repoConfig.getUrl() );
            if ( repoConfig instanceof FileRepoConfiguration )
            {
                FileRepoConfiguration frc = (FileRepoConfiguration) repoConfig;
                File repoPath = new File( frc.getBasePath(), pathInfo );
                File[] newfiles = repoPath.listFiles();
                fileElements.addAll( MergedFileList.filenames( newfiles, pathInfo, repoConfig ) );
            }
        }

        context.put( "fileElements", fileElements );
        context.put( "ab", new ABToggler() );
        context.put( "dateFormat", getRCC().getLastModifiedDateFormatForThread() );
        return getTemplate( "RepositoryServlet.vtl" );
    }

    public String getTopLevel()
    {
        return "REPOSITORY";
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