package org.apache.maven.proxy.config;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.maven.proxy.components.ProxyArtifact;
import org.apache.maven.proxy.engine.DownloadEngine;
import org.apache.maven.proxy.engine.RetrievalDetails;

/**
 * Strips file:/// off the front of the configured URL and uses that to find files locally.
 * 
 * @author  Ben Walding
 * @version $Id$
 */
public class FileRepoConfiguration extends RepoConfiguration
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
                    .getLogger( FileRepoConfiguration.class );

    private final String basePath;

    public FileRepoConfiguration( String key, String url, String description, boolean copy, boolean hardFail,
                    boolean cacheFailures, long cachePeriod )
    {
        super( key, url, description, copy, hardFail, cacheFailures, cachePeriod );
        basePath = url.substring( 8 );
    }

    public String getBasePath()
    {
        return basePath;
    }

    /**
     * Given a relative path, returns the absolute file in the repository
     */
    public File getLocalFile( String path )
    {
        return new File( basePath + path );
    }

    public RetrievalDetails retrieveArtifact( File out, String url ) throws IOException
    {
        try
        {
            File file = getLocalFile( url );
            if ( !file.exists() )
            {
                throw new FileNotFoundException();
            }

            if ( getCopy() )
            {
                InputStream is = new FileInputStream( file );
                DownloadEngine.download( out, is, file.lastModified() );
            }
            return new RetrievalDetails( out );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public ProxyArtifact getMetaInformationInternal( String url )
    {
        final File file = getLocalFile( url );
        LOGGER.debug( this + ": Checking last modified time for " + file );

        if ( file.exists() )
        {
            ProxyArtifact snapshot = new ProxyArtifact( this, url );
            snapshot.setSize( file.length() );
            snapshot.setLastModified( file.lastModified() );
            return snapshot;
        }

        return null;
    }
}