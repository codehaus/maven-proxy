package org.apache.maven.proxy.components.impl;

/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import junit.framework.TestCase;

import org.apache.maven.proxy.components.ProxyArtifact;
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
        ProxyArtifact pArtifact = new ProxyArtifact( null, "a" );
        pArtifact.setLastModified( 10101L );
        cache.setSnapshot( pArtifact.getPath(), pArtifact );
        assertEquals( 10101L, cache.getSnapshot( "a" ).getLastModified() );
        Thread.sleep( 1500 );
        assertNull( cache.getSnapshot( "a" ) );
        assertNull( cache.getSnapshot( "b" ) );
    }
    
    //Under heavy load this might fail - if it can't execute the first set and check in less than a second
    public void testCacheClear() throws Exception
    {
        SnapshotCache cache = new DefaultSnapshotCache( 1000 );
        ProxyArtifact pArtifact = new ProxyArtifact( null, "a" );
        pArtifact.setLastModified( 10101L );
        cache.setSnapshot( pArtifact.getPath(), pArtifact );
        assertEquals( 10101L, cache.getSnapshot( "a" ).getLastModified() );
        cache.stop();
        cache.start();
        assertNull( cache.getSnapshot( "a" ) );
        assertNull( cache.getSnapshot( "b" ) );
    }


    //  Under heavy load this might fail - if it can't execute the first set and check in less than a second
    public void testCacheNull() throws InterruptedException
    {
        SnapshotCache cache = new DefaultSnapshotCache( 1000 );
        ProxyArtifact pArtifact = new ProxyArtifact( null, "a" );
        pArtifact.setLastModified( 10101L );
        cache.setSnapshot( pArtifact.getPath(), pArtifact );
        assertEquals( 10101L, cache.getSnapshot( "a" ).getLastModified() );
        Thread.sleep( 1500 );
        assertNull( cache.getSnapshot( "a" ) );
        assertNull( cache.getSnapshot( "b" ) );
    }
}