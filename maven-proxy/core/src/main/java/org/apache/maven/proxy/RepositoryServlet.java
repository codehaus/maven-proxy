package org.apache.maven.proxy;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Maven" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Maven", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ====================================================================
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.fetch.exceptions.FetchException;
import org.apache.maven.fetch.exceptions.ResourceNotFoundFetchException;
import org.apache.maven.fetch.util.IOUtility;
import org.apache.maven.proxy.config.*;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class RepositoryServlet extends HttpServlet
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(RepositoryServlet.class);

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

    protected long getLastModified(HttpServletRequest request)
    {
        LOGGER.debug("Checking getLastModified(): " + request.getPathInfo());
        final File f = getFileForRequest(request);

        if (f.exists() && f.isFile())
        {
            return f.lastModified();
        }
        else
        {
            return super.getLastModified(request);
        }
    }

    public void init() throws ServletException
    {
        rcc = (RetrievalComponentConfiguration) getServletContext().getAttribute("config");

        localStoreDir = new File(rcc.getLocalStore());
        if (!localStoreDir.exists())
        {
            LOGGER.info("Local Repository (" + localStoreDir.getAbsolutePath() + ") does not exist");
        }

    }

    public File getFileForRequest(HttpServletRequest request)
    {
        return new File(localStoreDir, request.getPathInfo());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        final String pathInfo = request.getPathInfo();
        LOGGER.info("Received request: " + pathInfo);

        if (rcc.isBrowsable())
        {
            if (pathInfo.equalsIgnoreCase("/favicon.ico"))
            {
                handleImageRequest("favicon.ico", "image/x-ico", response);
                return;
            }

            if (pathInfo.equalsIgnoreCase("/jar.png"))
            {
                handleImageRequest("jar.png", "image/png", response);
                return;
            }

            if (pathInfo.equalsIgnoreCase("/parent.png"))
            {
                handleImageRequest("parent.png", "image/png", response);
                return;
            }
        }
        
        if (pathInfo.endsWith("/"))
        {
            if (rcc.isBrowsable())
            {
                handleBrowseRequest(request, response);
            }
            else
            {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
            return;
        }
        else
        {
            handleDownloadRequest(request, response);
            return;
        }
    }

    /**
     * @param string
     * @param response
     */
    private void handleImageRequest(String image, String type, HttpServletResponse response) throws IOException
    {
        response.setContentType(type);
        OutputStream os = response.getOutputStream();
        InputStream is = getClass().getResourceAsStream(image);
        IOUtility.transferStream(is, os);
        IOUtility.close(is);
    }

    private void handleDownloadRequest(HttpServletRequest request, HttpServletResponse response)
        throws FileNotFoundException, IOException
    {
        try
        {
            boolean done = false;
            List repos = rcc.getRepos();
            RetrievalComponent rc = new DefaultRetrievalComponent();
            //This whole thing is inside out.  It should only check repos if local file not found
            for (int i = 0; i < repos.size(); i++)
            {
                RepoConfiguration repoConfig = (RepoConfiguration) repos.get(i);

                try
                {
                    File f = new File(localStoreDir, request.getPathInfo());
                    f.getParentFile().mkdirs();
                    if (f.exists())
                    {
                        LOGGER.info("Retrieving from cache: " + f.getAbsolutePath());
                    }
                    else
                    {
                        LOGGER.info("Retrieving from upstream (" + repoConfig.getKey() + "): " + f.getAbsolutePath());
                        rc.retrieveArtifact(repoConfig, f, request.getPathInfo());
                    }

                    InputStream is = new FileInputStream(f);

                    response.setContentType("application/x-jar");
                    OutputStream os = response.getOutputStream();
                    IOUtility.transferStream(is, os);
                    IOUtility.close(is);
                    done = true;
                    break;
                }
                catch (ResourceNotFoundFetchException ex)
                {
                    // if not found, just move on
                }
            }

            if (!done)
            {
                LOGGER.warn("Could not find upstream resource :" + request.getPathInfo());
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Could not find " + request.getPathInfo());
            }
        }
        catch (FetchException e)
        {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

    /**
     * @param request
     * @param response
     */
    private void handleBrowseRequest(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        final String pathInfo = request.getPathInfo();

        PrintWriter pw = response.getWriter();

        pw.println("<html>");
        pw.println("<head>");
        pw.println("  <title>maven-proxy</title>");
        pw.println("  <style type='text/css'>");
        pw.println("    * { font-family: tahoma,verdana,arial; }");
        pw.println("    tr.dir { left-margin: 1cm; }");
        pw.println("  </style>");
        pw.println("</head>");
        pw.println("<body>");
        pw.println("<div>Browsing " + pathInfo + "</div>");
        File dir = new File(localStoreDir, pathInfo);
        File[] files = dir.listFiles();
        if (files == null)
        {
            files = new File[0];
        }

        pw.println("<table width='100%'>");
        pw.println("<colgroup>");
        pw.println("  <col width='20px'>");
        pw.println("  <col width='*'>");
        pw.println("  <col width='5*'>");
        pw.println("</colgroup>");
        pw.println(
            "<tr class='dir'><td><img src='/parent.png' alt=''/></td><td/><td><a href='..'>..</a></td><td></td></tr>");
        for (int i = 0; i < files.length; i++)
        {
            File theFile = files[i];
            if (theFile.isDirectory())
            {
                pw.println(
                    "<tr class='dir'><td><a href='"
                        + pathInfo
                        + theFile.getName()
                        + "/'>"
                        + theFile.getName()
                        + "</a></td><td></td></tr>");
            }
            else
            {
                pw.println(
                    "<tr class='file'><td><img src='/jar.png' alt=''/></td><td>"
                        + theFile.length()
                        + "</td><td><a href='"
                        + pathInfo
                        + theFile.getName()
                        + "'>"
                        + theFile.getName()
                        + "</a></td></tr>");
            }
        }
        pw.println("</table>");
        pw.println("</body>");
        pw.println("</html>");
        return;

    }

}
