package org.apache.maven.proxy.config;

/**
 * Immutable.
 * 
 * @author  Ben Walding
 * @version $Id$
 */
public class RepoConfiguration
{
    private final String url;
    private final String username;
    private final String password;
    private final ProxyConfiguration proxy;

    public RepoConfiguration(String url, String username, String password, ProxyConfiguration proxy)
    {
        this.url = url;
        this.username = username;
        this.password = password;
        this.proxy = proxy;
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
    public ProxyConfiguration getProxy()
    {
        return proxy;
    }

    /**
     * @return
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @return
     */
    public String getUsername()
    {
        return username;
    }

}
