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
    private File baseDir;

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#getLastModified(javax.servlet.http.HttpServletRequest)
     */
    protected long getLastModified(HttpServletRequest request)
    {
        LOGGER.info("Checking getLastModified(): " + request.getPathInfo());
        File f = new File(baseDir + request.getPathInfo());
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
        Properties props = (Properties) getServletContext().getAttribute("properties");
        baseDir = new File(props.getProperty(ProxyProperties.REPOSITORY_LOCAL));

        if (!baseDir.exists())
        {
            LOGGER.info("Local Repository (" + baseDir.getAbsolutePath() + ") does not exist");
        }

        rc.setBaseUrl(props.getProperty("repository.remote"));
        //rc.setBaseDir(baseDir);
        rc.setProxyHost(props.getProperty(ProxyProperties.PARENT_PROXY_HOST));
        rc.setProxyPort(Integer.parseInt(props.getProperty(ProxyProperties.PARENT_PROXY_PORT)));
        rc.setProxyUsername(props.getProperty(ProxyProperties.PARENT_PROXY_USERNAME));
        rc.setProxyPassword(props.getProperty(ProxyProperties.PARENT_PROXY_PASSWORD));
    }

    public File getFileForRequest(HttpServletRequest request)
    {
        return new File(baseDir + request.getPathInfo());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        LOGGER.info("Received request: " + request.getPathInfo());
        try
        {

            File f = getFileForRequest(request);
            f.getParentFile().mkdirs();

            rc.retrieveArtifact(f, request.getPathInfo());
            InputStream is = new FileInputStream(f);

            //TODO could tailor the mime type
            response.setContentType("application/octet-stream");
            OutputStream os = response.getOutputStream();
            IOUtility.transferStream(is, os);
            IOUtility.close(os);
            IOUtility.close(is);
        }
        catch (FetchException e)
        {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getLocalizedMessage());
        }
    }

}
