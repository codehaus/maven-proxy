package org.apache.maven.proxy.components;

/**
 * @author Ben Walding
 */
public interface SnapshotCache
{
    long getLastModified( String item );

    void setLastModified( String item, long lastModified );
}