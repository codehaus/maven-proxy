package org.apache.maven.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.fetch.exceptions.FetchException;
import org.apache.maven.fetch.util.IOUtility;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class RepositoryServlet extends HttpServlet
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(RepositoryServlet.class);
    
    private final DefaultRetrievalComponent rc = new DefaultRetrievalComponent();
    File baseDir;
    public void init() throws ServletException
    {
        Properties props = (Properties) getServletContext().getAttribute("properties");
        baseDir = new File(props.getProperty("repository.local"));
        rc.setBaseUrl(props.getProperty("repository.remote"));
        rc.setBaseDir(baseDir);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse arg1) throws ServletException, IOException
    {
        LOGGER.info("Received request for " + req.getPathInfo());
        try
        {
            
            File f = new File(baseDir + req.getPathInfo());
            f.getParentFile().mkdirs();
            
            rc.retrieveArtifact(f, req.getPathInfo());
            InputStream is = new FileInputStream(f);
            arg1.setContentType("x-binary");
            OutputStream os = arg1.getOutputStream();
            IOUtility.transferStream(is, os);
            IOUtility.close(os);
            IOUtility.close(is);
        }
        catch (FetchException e)
        {
            e.printStackTrace();
            arg1.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

}
