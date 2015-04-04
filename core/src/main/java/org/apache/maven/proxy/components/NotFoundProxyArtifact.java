package org.apache.maven.proxy.components;

import org.apache.maven.proxy.config.RepoConfiguration;

/**
 * @author Ben Walding
 */
public class NotFoundProxyArtifact extends ProxyArtifact
{

    /**
     * @param repo
     * @param path
     */
    public NotFoundProxyArtifact( RepoConfiguration repo, String path )
    {
        super( repo, path );
    }
}