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

/**
 * Immutable.
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

    public RepoConfiguration( String key, String url, String description, boolean copy )
    {
        this.key = key;
        this.url = url;
        this.description = description;
        this.copy = copy;
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
     * @return
     */
    public boolean getCopy()
    {
        return copy;
    }

    public String getDescription()
    {
        return description;
    }

    public String toString()
    {
        return "Repo[" + getKey() + "]";
    }

}