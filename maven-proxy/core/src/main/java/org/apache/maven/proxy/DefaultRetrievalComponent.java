package org.apache.maven.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.maven.fetch.FetchRequest;
import org.apache.maven.fetch.FetchTool;
import org.apache.maven.fetch.exceptions.FetchException;
import org.apache.maven.proxy.config.*;

/**
 * This component should be stateless and hence multithreadable.
 * @author  Ben Walding
 * @version $Id$
 */
public class DefaultRetrievalComponent implements RetrievalComponent
{
    

    public InputStream retrieveArtifact(RepoConfiguration rc, File out, String url) throws FetchException, FileNotFoundException
    {
        FetchRequest fr = new FetchRequest(rc.getUrl() + "/" + url);
        if (rc.getProxy() != null)
        {
            fr.setProxyHost(rc.getProxy().getHost());
            fr.setProxyPort(rc.getProxy().getPort());
            if (rc.getProxy().getUsername() != null)
            {
                fr.setProxyUser(rc.getProxy().getUsername());
                fr.setProxyPass(rc.getProxy().getPassword());
            }
        }
        FetchTool bean = new FetchTool();

        fr.setOutputFile(out);

        //FetchResponse dresp = 
        bean.performDownload(fr);
        //Don't really care about the response (No exception thrown == downloaded ok!)
        return new FileInputStream(out);
    }
    

}
