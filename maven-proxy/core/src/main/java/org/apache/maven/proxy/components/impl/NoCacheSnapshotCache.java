package org.apache.maven.proxy.components.impl;

import org.apache.maven.proxy.components.ProxyArtifact;
import org.apache.maven.proxy.components.SnapshotCache;

/**
 * @author Ben Walding
 */
public class NoCacheSnapshotCache implements SnapshotCache
{

    public ProxyArtifact getSnapshot( String path )
    {
        return null;
    }

    public void setSnapshot( String item, ProxyArtifact snapshot )
    {
        //Nothing to do
    }

    public void start() throws Exception
    {
        //Nothing to do
    }

    public void stop() throws Exception
    {
        //Nothing to do
    }
}