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

import org.apache.maven.proxy.components.ProxyArtifact;
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
    private final String key;
    private final String description;
    private final String url;
    private final boolean copy;
    private final boolean hardFail;

    public RepoConfiguration( String key, String url, String description, boolean copy, boolean hardFail )
    {
        this.key = key;
        this.url = url;
        this.description = description;
        this.copy = copy;
        this.hardFail = hardFail;
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

    public String toString()
    {
        return "Repo[" + getKey() + "]";
    }

    public abstract RetrievalDetails retrieveArtifact( File out, String url ) throws IOException;

    public abstract ProxyArtifact getMetaInformation( String url ) throws FileNotFoundException;

}