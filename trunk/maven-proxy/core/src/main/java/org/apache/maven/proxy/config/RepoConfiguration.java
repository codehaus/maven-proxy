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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.maven.proxy.components.NotFoundProxyArtifact;
import org.apache.maven.proxy.components.ProxyArtifact;
import org.apache.maven.proxy.components.SnapshotCache;
import org.apache.maven.proxy.components.impl.DefaultSnapshotCache;
import org.apache.maven.proxy.components.impl.NoCacheSnapshotCache;
import org.apache.maven.proxy.engine.RetrievalDetails;

/**
 * Immutable.
 *
 * hardfail - if a repository is set to hard fail, then the download engine will terminate the whole download
 *            process (with a status 500) if any of the repositories have unexpected errors.
 *  
 *            if a repository expects an error - eg. 400 (not found) - then it is not required to terminate the
 *            download process. 
 *  
 * @author  Ben Walding
 * @version $Id$
 */
public abstract class RepoConfiguration
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger( RepoConfiguration.class );

    private final String key;
    private final String description;
    private final String url;
    private final boolean copy;
    private final boolean hardFail;
    private final boolean cacheFailures;
    private final long cachePeriod;
    private final SnapshotCache snapshotCache;

    public RepoConfiguration( String key, String url, String description, boolean copy, boolean hardFail,
                    boolean cacheFailures, long cachePeriod )
    {
        this.key = key;
        this.url = url;
        this.description = description;
        this.copy = copy;
        this.hardFail = hardFail;
        this.cacheFailures = cacheFailures;
        this.cachePeriod = cachePeriod;

        if ( getCachePeriod() > 0 )
        {
            //Multiply by 1000 because the config file is in seconds, but the cache is in milliseconds
            LOGGER.info( this + ": Enabling cache with period of " + getCachePeriod() + " seconds" );
            snapshotCache = new DefaultSnapshotCache( getCachePeriod() * 1000 );
        }
        else
        {
            LOGGER.info( "Disabling snapshot cache" );
            snapshotCache = new NoCacheSnapshotCache();
        }
    }

    /**
     * @return
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * @return
     */
    public String getKey()
    {
        return key;
    }

    /**
     * If a file repository is set to "copy" mode, it will copy the found files into 
     * the main repository store.
     */
    public boolean getCopy()
    {
        return copy;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean getHardFail()
    {
        return hardFail;
    }

    public boolean getCacheFailures()
    {
        return cacheFailures;
    }

    public long getCachePeriod()
    {
        return cachePeriod;
    }

    public String toString()
    {
        return "Repo[" + getKey() + "]";
    }

    public abstract RetrievalDetails retrieveArtifact( File out, String url ) throws IOException;

    /**
     * 
     * @param url
     * @return
     * @throws FileNotFoundException
     */
    protected abstract ProxyArtifact getMetaInformationInternal( String url ) throws FileNotFoundException;

    public final ProxyArtifact getMetaInformation( String url ) throws FileNotFoundException
    {
        ProxyArtifact pa = getSnapshotCache().getSnapshot( url );

        if ( pa == null )
        {
            pa = getMetaInformationInternal( url );
            if ( pa == null )
            {
                pa = new NotFoundProxyArtifact( this, url );
            }
        }

        getSnapshotCache().setSnapshot( url, pa );

        if ( pa instanceof NotFoundProxyArtifact )
        {
            return null;
        }

        return pa;
    }

    /**
     * There is really no need for clients to know they are being cached
     * @return
     */
    private SnapshotCache getSnapshotCache()
    {
        return this.snapshotCache;
    }

    public void clearSnapshotCache() throws Exception
    {
        this.snapshotCache.stop();
        this.snapshotCache.start();
    }
}