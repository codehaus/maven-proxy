package org.apache.maven.proxy;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.maven.proxy.components.ProxyArtifact;
import org.apache.maven.proxy.config.RepoConfiguration;
import org.codehaus.plexus.util.StringInputStream;

/**
 * @author Ben Walding
 */
public class MockRepoConfiguration extends RepoConfiguration
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
                    .getLogger( MockRepoConfiguration.class );
    public static final String MOCK_DATA_100 = "abcdefghij1234567890klmnopqrst1112131415abcdefghij1234567890klmnopqrst1112131415abcdefghij1234567890";

    private long hits = 0L;

    public long getHits()
    {
        return hits;
    }

    private void incrementHits()
    {
        hits++;
    }

    public MockRepoConfiguration( String key, String url, String description, boolean copy, boolean hardFail )
    {
        super( key, url, description, copy, hardFail );
    }

    public RetrievalDetails retrieveArtifact( File out, String url ) throws IOException
    {
        incrementHits();

        if ( url.equals( "/a/a-SNAPSHOT.jar" ) || url.equals( "/b/b-1.1.jar" ) )
        {
            DownloadEngine.download( out, new StringInputStream( MOCK_DATA_100 ) );
            LOGGER.info( "lastmodbefore:" + out.lastModified() );
            if ( !out.setLastModified( 1010L ) )
            {
                //If we can't set the last modified time, then none of our not modified checking will work.
                LOGGER.error( "Failure setting the last modified time on " + out );
            }
            LOGGER.info( "lastmodafter:" + out.lastModified() );
            return new RetrievalDetails( out );
        }

        throw new FileNotFoundException( "Can't find " + url );
    }

    public ProxyArtifact getSnapshot( String url ) throws FileNotFoundException
    {
        incrementHits();

        if ( url.equals( "/a/a-SNAPSHOT.jar" ) )
        {
            ProxyArtifact snapshot = new ProxyArtifact( this, url );
            snapshot.setSize( 100 );
            snapshot.setLastModified( 1010L );
            return snapshot;
        }

        if ( url.equals( "/b/b-1.1.jar" ) )
        {
            ProxyArtifact snapshot = new ProxyArtifact( this, url );
            snapshot.setSize( 100 );
            snapshot.setLastModified( 1010L );
            return snapshot;
        }

        return null;
    }

}