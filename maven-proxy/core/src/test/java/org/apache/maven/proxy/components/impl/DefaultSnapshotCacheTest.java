package org.apache.maven.proxy.components.impl;

import junit.framework.TestCase;

import org.apache.maven.proxy.components.SnapshotCache;

/**
 * @author Ben Walding
 */
public class DefaultSnapshotCacheTest extends TestCase
{
    //Under heavy load this might fail - if it can't execute the first set and check in less than a second
    public void testCache() throws InterruptedException
    {
        SnapshotCache cache = new DefaultSnapshotCache( 1000 );
        //long now = System.currentTimeMillis();
        cache.setLastModified( "a", 10101L );
        assertEquals( 10101L, cache.getLastModified( "a" ) );
        Thread.sleep( 1500 );
        assertEquals( -1, cache.getLastModified( "a" ) );
    }
}