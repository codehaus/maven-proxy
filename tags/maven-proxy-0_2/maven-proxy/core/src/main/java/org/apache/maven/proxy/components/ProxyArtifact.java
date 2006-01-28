package org.apache.maven.proxy.components;

import org.apache.maven.proxy.config.RepoConfiguration;

/**
 * @author Ben Walding
 */
public class ProxyArtifact
{
    private final RepoConfiguration repo;
    private final String path;
    private long lastModified;
    private long size;

    public ProxyArtifact( RepoConfiguration repo, String path )
    {
        this.repo = repo;
        this.path = path;
    }
    
    public RepoConfiguration getRepo()
    {
        return repo;
    }

    public long getLastModified()
    {
        return lastModified;
    }

    public void setLastModified( long lastModified )
    {
        this.lastModified = lastModified;
    }

    public String getPath()
    {
        return path;
    }

    public long getSize()
    {
        return size;
    }

    public void setSize( long size )
    {
        this.size = size;
    }
}