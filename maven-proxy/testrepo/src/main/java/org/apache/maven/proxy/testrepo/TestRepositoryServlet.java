package org.apache.maven.proxy.testrepo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.fetch.util.IOUtility;

class Entry
{
    String url;
    String actual;

    Entry(String url, String actual)
    {
        this.url = url;
        this.actual = actual;
    }
}

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class TestRepositoryServlet extends HttpServlet
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER =
        org.apache.log4j.Logger.getLogger(TestRepositoryServlet.class);

    Map entries = new HashMap();

    /**
     * @see javax.servlet.GenericServlet#init()
     */
    private String configuration;
    public void init() throws ServletException
    {
        try
        {
            ServletContext ctx = getServletContext();
            Properties ctxProperties = (Properties) ctx.getAttribute("properties");
            configuration = ctxProperties.getProperty("configuration");
            File props = new File(getResourceBase(), "testrepo.properties");
            Properties properties = new Properties();
            properties.load(new FileInputStream(props));

            for (int i = 0; i < 1000; i++)
            {
                String url = properties.getProperty("file" + i + ".url");
                String actual = properties.getProperty("file" + i + ".actual");
                Entry e = new Entry(url, actual);
                entries.put(url, e);
            }
        }
        catch (Exception e)
        {
            throw new ServletException(e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        LOGGER.info("request.getPathInfo() = " + request.getPathInfo());
        Entry e = getEntry(request);
        if (e == null)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        InputStream is = new FileInputStream(getFile(e));
        OutputStream os = response.getOutputStream();
        IOUtility.transferStream(is, os);

    }

    private File getFile(Entry e)
    {
        return new File(getResourceBase(), e.actual);
    }

    private File getResourceBase()
    {
        String basedir = System.getProperty("basedir");
        if (basedir == null)
        {
            basedir = ".";
        }
        File baseDir = new File(basedir);
        File mainDir = new File(baseDir, "src/test-resources/" + configuration + "/");
        return mainDir;
    }

    private Entry getEntry(HttpServletRequest request)
    {
        String path = request.getPathInfo();
        LOGGER.info("Finding entry - " + path);
        Entry e = (Entry) entries.get(path);
        return e;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#getLastModified(javax.servlet.http.HttpServletRequest)
     */
    protected long getLastModified(HttpServletRequest request)
    {
        try
        {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            if (request.getPathInfo().equals("/test-data-1.txt"))
            {
                return sdf.parse("19990101").getTime();
            }

            LOGGER.info("Unhandled request for last mod of resource: " + request.getPathInfo());
            return -1;
        }
        catch (ParseException e)
        {
            //XXX can fix when jdk1.4 only
            e.printStackTrace();
            throw new RuntimeException(e.getLocalizedMessage());
        }
    }
}
