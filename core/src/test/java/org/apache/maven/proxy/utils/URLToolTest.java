package org.apache.maven.proxy.utils;

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

/**
 * @author Ben Walding
 */
public class URLToolTest extends TestCase
{
    public void testRetrace()
    {
        assertEquals( "retrace(a/b.jar)", "..", URLTool.getRetrace( "a/b.jar" ) );
        assertEquals( "retrace(a/b/c.jar)", "../..", URLTool.getRetrace( "a/b/c.jar" ) );
        assertEquals( "retrace(a/b/)", "../..", URLTool.getRetrace( "a/b/" ) );
        assertEquals( "retrace(a)", ".", URLTool.getRetrace( "a" ) );
    }

    public void testSnapshot()
    {
        assertFalse( URLTool.isSnapshot( "bert-snapshot.jar" ) );
        assertTrue( URLTool.isSnapshot( "bert-SNAPSHOT.jar" ) );
        assertFalse( URLTool.isSnapshot( "bert-SNAPSHOT" ) );
        assertFalse( URLTool.isSnapshot( "bert-SNAPSHOT-1.0.jar" ) );
        assertFalse( URLTool.isSnapshot( "bertSNAPSHOT-1.0.jar" ) );

        //This isn't a snapshot... it's a dodgy edge case test!
        assertFalse( URLTool.isSnapshot( "-SNAPSHOT.jar" ) );

    }

    public void testMetaData() {
        assertTrue( URLTool.isMetaData( "maven-metadata.xml" ) );
        assertTrue( URLTool.isMetaData( "/maven-metadata.xml" ) );
        assertTrue( URLTool.isMetaData( "maven-metadata.xml.sha1" ) );
        assertTrue( URLTool.isMetaData( "/maven-metadata.xml.sha1" ) );

        //This isn't a metadata file... it's a dodgy edge case test!
        assertFalse( URLTool.isMetaData( "maven-metadata.xml2" ) );
        assertFalse( URLTool.isMetaData( "/maven-metadata.xml2" ) );
    }

    public void testPOM() {
        assertTrue( URLTool.isPOM( "a.pom" ) );
        assertTrue( URLTool.isPOM( "a.pom.sha1" ) );
        assertFalse( URLTool.isPOM( "maven-pom-plugin-1.0.jar" ) );

        //This isn't a POM... it's a dodgy edge case test!
        assertFalse( URLTool.isPOM( ".pom" ) );
        assertFalse( URLTool.isPOM( "a.pomX" ) );
    }
}