package org.apache.maven.proxy.components.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.proxy.components.SnapshotCache;

class CacheElement
{
    //Full name of artifact
    String item;

    //When was the artifact modified
    long lastModified;

    //When was this cache entry last updated
    long lastUpdated;
}

/**
 * @author Ben Walding
 */

public class DefaultSnapshotCache implements SnapshotCache
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
                    .getLogger( DefaultSnapshotCache.class );

    private final Map cache = new HashMap();
    //Milliseconds 
    private long snapshotUpdateInterval;

    public DefaultSnapshotCache( long snapshotUpdateInterval )
    {
        this.snapshotUpdateInterval = snapshotUpdateInterval;
    }

    public long getLastModified( String item )
    {
        synchronized ( cache )
        {

            CacheElement cacheElement = (CacheElement) cache.get( item );

            if ( cacheElement == null )
            {
                LOGGER.debug( "Unable to find " + item + " in snapshot cache" );
                return -1;
            }

            long age = System.currentTimeMillis() - cacheElement.lastUpdated;
            if ( age > snapshotUpdateInterval )
            {
                LOGGER.info( "Expiring " + cacheElement.item + " from snapshot cache (" + age + " > "
                                + snapshotUpdateInterval + ")" );
                cache.remove( item );
                return -1;
            }

            return cacheElement.lastUpdated;
        }
    }

    public void setLastModified( String item, long lastModified )
    {
        synchronized ( cache )
        {
            CacheElement cacheElement = (CacheElement) cache.get( item );
            if ( cacheElement == null )
            {
                cacheElement = new CacheElement();
                cacheElement.item = item;
                cache.put( item, cacheElement );
                LOGGER.info( "Adding " + item + " to snapshot cache" );
            }
            else
            {
                LOGGER.info( "Updating " + item + " in snapshot cache" );
            }

            cacheElement.lastModified = lastModified;
            cacheElement.lastUpdated = System.currentTimeMillis();
        }
    }

}