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

        try
        {
            props = loadAndValidateProperties(args[0]);
        }
        catch (ValidationException e)
        {
            Throwable t = e;

            System.err.println("Error while loading properties:");

            while (t != null)
            {
                System.err.println("  " + t.getLocalizedMessage());
                t = t.getCause();
            }
        }
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

        repository = props.getProperty(ProxyProperties.REPOSITORY_LOCAL);

        System.out.println("Saving repository at " + repository);
        System.out.println("Starting...");

        HttpServer server = new HttpServer();
        SocketListener listener = new SocketListener();
        listener.setPort(port);
        server.addListener(listener);

        HttpContext context = new HttpContext();
        context.setContextPath("/");
        ServletHandler sh = new ServletHandler();
        sh.addServlet("Repository", "/*", RepositoryServlet.class.getName());
        context.setAttribute("properties", props);
        context.addHandler(sh);
        server.addContext(context);

        server.start();
        System.out.println("Started.");
    }

    /**
     * This method will load and validate the properties.
     * @todo make it throw a validation exception and defer
     *       logging to the handler of the exception.
     * @param filename The name of the properties file.
     * @return Returns a <code>Properties</code> object if the load and validation was successfull.
     * @throws ValidationException If there was any problem validating the properties
     */
    private Properties loadAndValidateProperties(String filename) throws ValidationException
    {
        File file;
        Properties p;

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

            throw new ValidationException(ex);
        }

        {
            //Verify local repository set
            String tmp = checkPropertySet(p, ProxyProperties.REPOSITORY_LOCAL);

            file = new File(tmp);
            if (!file.exists())
            {
                throw new ValidationException("The local repository doesn't exist: " + file.getAbsolutePath());
            }

            if (!file.isDirectory())
            {
                throw new ValidationException("The local repository must be a directory: " + file.getAbsolutePath());
            }
        }

        {
            //Verify remote repository set
            checkPropertySet(p, ProxyProperties.REPOSITORY_REMOTE);
        }

        // all ok
        return p;
    }

    private String checkPropertySet(Properties p, String value) throws ValidationException
    {
        String prop = p.getProperty(value);
        if (prop == null)
        {
            throw new ValidationException("Missing property '" + value + "'");
        }

        return prop;
    }
}
