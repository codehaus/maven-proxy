package org.apache.maven.proxy.config;

/**
 * Immutable.
 * 
 * @author  Ben Walding
 * @version $Id$
 */
public class ProxyConfiguration
{
    private final String key;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    
    public ProxyConfiguration(String key, String host, int port, String username, String password) {
        this.key = key;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    
    
    
    /**
     * @return
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @return
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @return
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @return
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @return
     */
    public String getUsername()
    {
        return username;
    }

    

}
