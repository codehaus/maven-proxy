package org.apache.maven.proxy.components;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;

/**
 * @author Ben Walding
 */
public interface SnapshotCache extends Startable
{
    long getLastModified( String item );

    void setLastModified( String item, long lastModified );
}