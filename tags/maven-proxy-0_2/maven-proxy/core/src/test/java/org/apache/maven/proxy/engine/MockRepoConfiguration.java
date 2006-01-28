package org.apache.maven.proxy.engine;

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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.maven.proxy.components.ProxyArtifact;
import org.apache.maven.proxy.config.RepoConfiguration;

/**
 * @author Ben Walding
 */
public class MockRepoConfiguration extends RepoConfiguration
{
    public static final String MOCK_DATA_100 = "abcdefghij1234567890klmnopqrst1112131415abcdefghij1234567890klmnopqrst1112131415abcdefghij1234567890";
    public static final String MOCK_DATA_33 = "abcdefghij1234567890klmnopqrst111";

    private long hits = 0L;

    public long getHits()
    {
        return hits;
    }

    private void incrementHits()
    {
        hits++;
    }

    public MockRepoConfiguration( String key, String url, String description, boolean copy, boolean hardFail, boolean cacheFailures, long cachePeriod )
    {
        super( key, url, description, copy, hardFail, cacheFailures, cachePeriod );
    }

    public RetrievalDetails retrieveArtifact( File out, String url ) throws IOException
    {
        incrementHits();

        if ( url.equals( "/a/a-SNAPSHOT.jar" ) || url.equals( "/b/b-1.1.jar" ) || url.equals("/b/maven-metadata.xml") || url.equals( "/b/b-1.1.pom" ) )
        {
            DownloadEngine.download( out, new ByteArrayInputStream( MOCK_DATA_100.getBytes() ), 1005000L );
            return new RetrievalDetails( out );
        }

        if ( url.equals( "/b/b-1.1.jar.md5" ) )
        {
            DownloadEngine.download( out, new ByteArrayInputStream( MOCK_DATA_33.getBytes() ), 1006000L );
            return new RetrievalDetails( out );
        }

        throw new FileNotFoundException( "Can't find " + url );
    }

    public ProxyArtifact getMetaInformationInternal( String url ) throws FileNotFoundException
    {
        incrementHits();

        if ( url.equals( "/a/a-SNAPSHOT.jar" ) )
        {
            ProxyArtifact snapshot = new ProxyArtifact( this, url );
            snapshot.setSize( 100 );
            snapshot.setLastModified( 1005000L );
            return snapshot;
        }

        if ( url.equals( "/b/b-1.1.jar" ) )
        {
            ProxyArtifact snapshot = new ProxyArtifact( this, url );
            snapshot.setSize( 100 );
            snapshot.setLastModified( 1005000L );
            return snapshot;
        }

        if ( url.equals( "/b/b-1.1.jar.md5" ) )
        {
            ProxyArtifact snapshot = new ProxyArtifact( this, url );
            snapshot.setSize( 33 );
            snapshot.setLastModified( 1006000L );
            return snapshot;
        }

        if ( url.equals( "/b/maven-metadata.xml" ) )
        {
            ProxyArtifact snapshot = new ProxyArtifact( this, url );
            snapshot.setSize( 100 );
            snapshot.setLastModified( 1005000L );
            return snapshot;
        }

        if ( url.equals( "/b/b-1.1.pom" ) )
        {
            ProxyArtifact snapshot = new ProxyArtifact( this, url );
            snapshot.setSize( 100 );
            snapshot.setLastModified( 1005000L );
            return snapshot;
        }

        return null;
    }

}