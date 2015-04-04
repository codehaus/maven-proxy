package org.apache.maven.proxy.testrepo;

import java.util.Properties;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.util.InetAddrPort;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class TestRepository
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(TestRepository.class);

    private int port = 8080;
    private HttpServer server = null;
    private final Properties properties = new Properties();

    public TestRepository() {
        setConfiguration("test-a");
    }
    /**
     * @return
     */
    public String getConfiguration()
    {
        return properties.getProperty("configuration");
    }

    /**
     * @param configuration
     */
    public void setConfiguration(String configuration)
    {
        properties.setProperty("configuration", configuration);
    }

    /**
     * @return
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    public void start() throws Exception
    {
        LOGGER.info("Starting...");

        server = new HttpServer();
        InetAddrPort iaport = new InetAddrPort("127.0.0.1", port);
        SocketListener listener = new SocketListener(iaport);
        listener.setPort(port);
        server.addListener(listener);

        ServletHandler sh = new ServletHandler();
        sh.addServlet("TestRepositoryServlet", "/*", TestRepositoryServlet.class.getName());

        HttpContext context = new HttpContext();
        context.setAttribute("properties", properties);
        context.setContextPath("/");
        context.addHandler(sh);
        server.addContext(context);

        server.start();
        LOGGER.info("Started.");
    }

    public void stop() throws InterruptedException
    {
        server.stop();
    }
}
