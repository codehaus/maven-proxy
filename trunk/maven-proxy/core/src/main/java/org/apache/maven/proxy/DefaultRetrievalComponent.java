package org.apache.maven.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.maven.fetch.FetchRequest;
import org.apache.maven.fetch.FetchTool;
import org.apache.maven.fetch.exceptions.FetchException;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class DefaultRetrievalComponent implements RetrievalComponent
{
    private String baseUrl;

    /**
     * @return
     */
    public String getBaseUrl()
    {
        return baseUrl;
    }

    /**
     * @param baseUrl
     */
    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public InputStream retrieveArtifact(File out, String url) throws FetchException, FileNotFoundException
    {
        FetchRequest fr = new FetchRequest(baseUrl + "/" + url);
        if (proxyHost != null)
        {
            fr.setProxyHost(proxyHost);
            fr.setProxyPort(proxyPort);
            fr.setProxyUser(proxyUsername);
            fr.setProxyPass(proxyPassword);
        }
        FetchTool bean = new FetchTool();

        fr.setOutputFile(out);

        //FetchResponse dresp = 
        bean.performDownload(fr);
        //Don't really care about the response (No exception thrown == downloaded ok!)
        return new FileInputStream(out);
    }

    /**
     * @param string
     */
    private String proxyHost = null;
    public void setProxyHost(String proxyHost)
    {
        this.proxyHost = proxyHost;
    }

    /**
     * @param string
     */
    private int proxyPort = -1;
    public void setProxyPort(int proxyPort)
    {
        this.proxyPort = proxyPort;
    }

    /**
     * @param string
     */
    private String proxyUsername;
    public void setProxyUsername(String proxyUsername)
    {
        this.proxyUsername = proxyUsername;
    }

    /**
     * @param string
     */
    private String proxyPassword = null;
    public void setProxyPassword(String proxyPassword)
    {
        this.proxyPassword = proxyPassword;
    }

}
