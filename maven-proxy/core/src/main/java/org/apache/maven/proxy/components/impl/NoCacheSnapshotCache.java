package org.apache.maven.proxy.components.impl;

import org.apache.maven.proxy.components.SnapshotCache;

/**
 * @author Ben Walding
 */
public class NoCacheSnapshotCache implements SnapshotCache
{

    public long getLastModified( String item )
    {
        return -1;
    }

    public void setLastModified( String item, long lastModified )
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