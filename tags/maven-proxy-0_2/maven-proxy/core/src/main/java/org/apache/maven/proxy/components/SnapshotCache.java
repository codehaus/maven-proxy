package org.apache.maven.proxy.components;


/**
 * @author Ben Walding
 */
public interface SnapshotCache extends Startable
{
    ProxyArtifact getSnapshot( String path );

    void setSnapshot( String path, ProxyArtifact snapshot );

}