package org.apache.maven.proxy.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class PropertyLoader
{
    private static final String REPO_LOCAL_STORE = "repo.local.store";
    private static final String PORT = "port";

    public RetrievalComponentConfiguration load(Properties props) throws IOException, ValidationException
    {
        RetrievalComponentConfiguration rcc = new RetrievalComponentConfiguration();

        rcc.setLocalStore(props.getProperty(REPO_LOCAL_STORE));
        
        if (props.getProperty(PORT) == null)
        {
            rcc.setPort(8080);
        }
        else
        {
            try
            {
                rcc.setPort(Integer.parseInt(props.getProperty(PORT)));
            }
            catch (NumberFormatException ex)
            {
                throw new ValidationException("Property " + PORT + " must be a integer");
            }
        }

        {
            String propertyList = props.getProperty("proxy.list");
            StringTokenizer tok = new StringTokenizer(propertyList, ",");
            while (tok.hasMoreTokens())
            {
                String key = tok.nextToken();
                String host = props.getProperty("proxy." + key + ".host");
                int port = Integer.parseInt(props.getProperty("proxy." + key + ".port"));
                String username = props.getProperty("proxy." + key + ".username");
                String password = props.getProperty("proxy." + key + ".password");
                ProxyConfiguration pc = new ProxyConfiguration(key, host, port, username, password);
                rcc.addProxy(pc);
            }
        }

        {
            String repoList = props.getProperty("repo.list");

            StringTokenizer tok = new StringTokenizer(repoList, ",");
            while (tok.hasMoreTokens())
            {
                String key = tok.nextToken();
                String url = props.getProperty("repo." + key + ".url");
                String username = props.getProperty("repo." + key + ".username");
                String password = props.getProperty("repo." + key + ".password");
                String proxyKey = props.getProperty("repo." + key + ".proxy");

                ProxyConfiguration proxy = null;
                if (proxyKey != null)
                {
                    proxy = rcc.getProxy(proxyKey);
                }

                RepoConfiguration rc = new RepoConfiguration(url, username, password, proxy);
                rcc.addRepo(rc);
            }
        }
        return rcc;

    }
    
    public RetrievalComponentConfiguration load(InputStream is) throws IOException, ValidationException
    {
        Properties props = new Properties();
        props.load(is);
        return load(props);
    }
}
