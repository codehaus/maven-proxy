package org.apache.maven.proxy.standalone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

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
    private Properties properties;
    private String repository;
    private int port;
    public static void main(String args[]) throws FileNotFoundException, IOException, MultiException
    {
        Standalone launcher = new Standalone();
        launcher.doMain(args);
    }

    public void doMain(String args[]) throws FileNotFoundException, IOException, MultiException
    {
        properties = new Properties();
        properties.load(new FileInputStream(new File(args[0])));

        if (properties.getProperty("port") == null)
        {
            port = 8080;
        }
        else
        {
            port = Integer.parseInt(properties.getProperty("port"));
        }

        repository = properties.getProperty("repository");

        System.out.println("Launched");
        System.out.println("Saving repository at " + repository);

        HttpServer server = new HttpServer();
        SocketListener listener = new SocketListener();
        listener.setPort(port);
        server.addListener(listener);

        HttpContext context = new HttpContext();
        context.setContextPath("/");
        context.setResourceBase("./docroot/");
        ServletHandler sh = new ServletHandler();
        sh.addServlet("Repository", "/*", RepositoryServlet.class.getName());
        context.setAttribute("properties", properties);
        context.addHandler(sh);
        context.addHandler(new ResourceHandler());
        server.addContext(context);

        server.start();
    }
}
