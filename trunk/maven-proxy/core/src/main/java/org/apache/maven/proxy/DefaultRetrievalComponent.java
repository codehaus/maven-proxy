package org.apache.maven.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.maven.fetch.FetchRequest;
import org.apache.maven.fetch.FetchResponse;
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
        FetchTool bean = new FetchTool();

        fr.setOutputFile(out);

        FetchResponse dresp = bean.performDownload(fr);

        return new FileInputStream(out);
    }

    /**
     * @param baseDir
     */
    public void setBaseDir(File baseDir)
    {
        // TODO Auto-generated method stub

    }

}
