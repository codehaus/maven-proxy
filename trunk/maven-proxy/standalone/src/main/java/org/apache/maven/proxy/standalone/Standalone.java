package org.apache.maven.proxy.standalone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.maven.proxy.ProxyProperties;
import org.apache.maven.proxy.RepositoryServlet;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.http.handler.ResourceHandler;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.util.MultiException;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class Standalone
{
    private Properties props;
    private String repository;
    private int port;

    public static void main(String args[])
    {
        Standalone launcher;

        try
        {
            launcher = new Standalone();
            launcher.doMain(args);
        }
        catch (MultiException e)
        {
            System.err.println("Internal error:");
            e.printStackTrace();
        }
    }

    public void doMain(String args[]) throws MultiException
    {
        if (args.length != 1)
        {
            System.err.println("Usage:");
            System.err.println("  java -jar maven-proxy-SNAPSHOT-uber.jar maven-proxy.properties");
            return;
        }

        props = loadAndValidateProperties(args[0]);
        // a error message should have been displayed
        if (props == null)
            return;

        if (props.getProperty("port") == null)
        {
            port = 8080;
        }
        else
        {
            try
            {
                port = Integer.parseInt(props.getProperty("port"));
            }
            catch (NumberFormatException ex)
            {
                System.err.println("Error in properyfile: port must be a integer");
                return;
            }
        }

        repository = props.getProperty("repository.local");

        System.out.println("Saving repository at " + repository);
        System.out.println("Starting");

        HttpServer server = new HttpServer();
        SocketListener listener = new SocketListener();
        listener.setPort(port);
        server.addListener(listener);

        HttpContext context = new HttpContext();
        context.setContextPath("/");
        context.setResourceBase("./docroot/");
        ServletHandler sh = new ServletHandler();
        sh.addServlet("Repository", "/*", RepositoryServlet.class.getName());
        context.setAttribute("properties", props);
        context.addHandler(sh);
        context.addHandler(new ResourceHandler());
        server.addContext(context);

        server.start();
        System.out.println("Started");
    }

    /**
     * This method will load and validate the properties.
     * @todo make it throw a validation exception and defer
     *       logging to the handler of the exception.
     * @param filename The name of the properties file.
     * @return Returns a <code>Properties</code> object if the load and validation was successfull.
     */
    private Properties loadAndValidateProperties(String filename)
    {
        File file;
        Properties p;
        String tmp;

        file = new File(filename);

        // load
        try
        {
            p = new Properties();
            p.load(new FileInputStream(file));
        }
        catch (FileNotFoundException ex)
        {
            System.err.println("No such file: " + file.getAbsolutePath());
            return null;
        }
        catch (IOException ex)
        {
            Throwable t = ex;

            System.err.println("Error while loading properties:");

            while (t != null)
            {
                System.err.println("  " + t.getLocalizedMessage());
                t = t.getCause();
            }

            return null;
        }

        // validate
        tmp = p.getProperty(ProxyProperties.REPOSITORY_LOCAL);
        if (tmp == null)
        {
            System.err.println("Missing property '" + ProxyProperties.REPOSITORY_LOCAL + "'");
            return null;
        }

        file = new File(tmp);
        if (!file.exists())
        {
            System.err.println("The local repository doesn't exist: " + file.getAbsolutePath());
            return null;
        }

        if (!file.isDirectory())
        {
            System.err.println("The local repository must be a directory: " + file.getAbsolutePath());
            return null;
        }

        tmp = p.getProperty("repository.remote");
        if (tmp == null)
        {
            System.err.println("Missing property 'repository.remote'");
            return null;
        }

        // all ok
        return p;
    }
}
