package org.apache.maven.proxy.standalone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.maven.proxy.RepositoryServlet;
import org.apache.maven.proxy.config.*;
import org.apache.maven.proxy.config.PropertyLoader;
import org.apache.maven.proxy.config.RetrievalComponentConfiguration;
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
    private static final String VERSION = "SNAPSHOT";
    
    public static void main(String args[])
    {
        Standalone launcher;

        try
        {
            launcher = new Standalone();
            launcher.doMain(args);
        }
        catch (Exception e)
        {
            System.err.println("Internal error:");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void doMain(String args[]) throws MultiException, IOException, ValidationException
    {
        System.err.println("maven-proxy " + Standalone.VERSION);
        
        if (args.length != 1)
        {
            System.err.println("Usage:");
            System.err.println("  java -jar maven-proxy-SNAPSHOT-uber.jar maven-proxy.properties");
            return;
        }

        RetrievalComponentConfiguration rcc = null;
        try
        {
            rcc = loadAndValidateConfiguration(args[0]);
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
        

        System.out.println("Saving repository at " + rcc.getLocalStore());
        for (Iterator iter = rcc.getRepos().iterator(); iter.hasNext();) {
            RepoConfiguration repo = (RepoConfiguration) iter.next();
            System.out.println("Scanning repository: " + repo.getUrl());
        } 
        System.out.println("Starting...");
        
        HttpServer server = new HttpServer();
        SocketListener listener = new SocketListener();
        listener.setPort(rcc.getPort());
        server.addListener(listener);

        HttpContext context = new HttpContext();
        context.setContextPath("/");
        ServletHandler sh = new ServletHandler();
        sh.addServlet("Repository", "/*", RepositoryServlet.class.getName());
        context.setAttribute("config", rcc);
        context.addHandler(sh);
        server.addContext(context);

        server.start();
        System.out.println("Started.");
        System.out.println("Add the following to your ~/build.properties file:");
        System.out.println("   maven.repo.remote=http://<external ip>:" + rcc.getPort());
        if (rcc.isBrowsable())  {
            System.out.println("The repository can be browsed at http://<external ip>:" + rcc.getPort() + "/");
        } else  {
            System.out.println("Repository browsing is not enabled.");
        }

    }

    /**
     * This method will load and validate the properties.
     * @todo make it throw a validation exception and defer
     *       logging to the handler of the exception.
     * @param filename The name of the properties file.
     * @return Returns a <code>Properties</code> object if the load and validation was successfull.
     * @throws ValidationException If there was any problem validating the properties
     */
    private RetrievalComponentConfiguration loadAndValidateConfiguration(String filename) throws ValidationException
    {
        RetrievalComponentConfiguration rcc;
        File file = new File(filename);

        try
        {
            rcc = (new PropertyLoader()).load(new FileInputStream(file));
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
            String tmp = checkSet(rcc.getLocalStore(), PropertyLoader.REPO_LOCAL_STORE);

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
            //only warn if missing
            if (rcc.getRepos().size() < 1)
            {
                throw new ValidationException("At least one remote repository must be configured.");
            }
        }

        // all ok
        return rcc;
    }

    private String checkSet(String value, String propertyName) throws ValidationException
    {
        if (value == null)
        {
            throw new ValidationException("Missing property '" + propertyName + "'");
        }

        return value;
    }
}
