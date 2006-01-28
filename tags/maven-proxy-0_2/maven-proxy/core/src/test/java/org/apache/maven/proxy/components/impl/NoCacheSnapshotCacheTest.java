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
import org.apache.maven.proxy.config.GlobalRepoConfiguration;
import org.apache.maven.proxy.config.RepoConfiguration;

/**
 * @author Ben Walding
 */
public class NoCacheSnapshotCacheTest extends TestCase
{
    public void testA()
    {
        NoCacheSnapshotCache nc = new NoCacheSnapshotCache();
        RepoConfiguration rc = new GlobalRepoConfiguration( "target" );
        nc.setSnapshot( "a", new ProxyArtifact( rc, "a" ) );
        assertNull( nc.getSnapshot( "a" ) );
    }

    public void testStopStart() throws Exception
    {
        NoCacheSnapshotCache nc = new NoCacheSnapshotCache();
        RepoConfiguration rc = new GlobalRepoConfiguration( "target" );
        nc.setSnapshot( "a", new ProxyArtifact( rc, "a" ) );
        assertNull( nc.getSnapshot( "a" ) );
        nc.stop();
        nc.start();
        assertNull( nc.getSnapshot( "a" ) );        
    }

}