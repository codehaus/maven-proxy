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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.maven.proxy.components.ProxyArtifact;
import org.apache.maven.proxy.config.RepoConfiguration;
import org.apache.maven.proxy.engine.RetrievalDetails;

/**
 * @author Ben Walding
 */
public class FlakyRepoConfiguration extends RepoConfiguration
{

    private final boolean flakyStatus;

    /**
     * The retrieval is always flaky, the snapshot is only flaky if you want it to be.
     */
    public FlakyRepoConfiguration( boolean flakyStatus, boolean copy, boolean hardFail, boolean cacheFailures, long cachePeriod )
    {
        super( "flaky", "file://///", "Flaky Repository", copy, hardFail, cacheFailures, cachePeriod );
        this.flakyStatus = flakyStatus;
    }

    public ProxyArtifact getMetaInformationInternal( String url ) throws FileNotFoundException
    {
        if ( flakyStatus )
        {
            throw new RuntimeException( "It's flaky I tell ya" );
        }

        if ( url.equals( "/a/a-1.1.jar" ) )
        {
            ProxyArtifact pa = new ProxyArtifact( this, url );
            pa.setLastModified( 3000L );
            pa.setSize( 100 );
            return pa;
        }

        if ( url.equals( "/a/a-SNAPSHOT.jar" ) )
        {
            ProxyArtifact pa = new ProxyArtifact( this, url );
            pa.setLastModified( 3300L );
            pa.setSize( 105 );
            return pa;
        }

        return null;
    }

    public RetrievalDetails retrieveArtifact( File out, String url ) throws IOException
    {
        throw new RuntimeException( "It's flaky I tell ya" );
    }

}