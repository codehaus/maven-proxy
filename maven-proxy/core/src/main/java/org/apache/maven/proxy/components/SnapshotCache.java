package org.apache.maven.proxy.components;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author Ben Walding
 */
public interface SnapshotCache extends Startable
{
    ProxyArtifact getSnapshot( String path );

    void setSnapshot( String path, ProxyArtifact snapshot );

}