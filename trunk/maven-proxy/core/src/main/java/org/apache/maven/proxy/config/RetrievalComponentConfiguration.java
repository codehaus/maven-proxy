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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class RetrievalComponentConfiguration
{
    private final Map proxies = new HashMap();
    private final List repos = new ArrayList();
    private String localStore;
    private String serverName;
    private boolean searchable;
    private boolean browsable;
    private int port;
    private String prefix;

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix( String prefix )
    {
        this.prefix = prefix;
    }

    /**
     * @return
     */
    public boolean isBrowsable()
    {
        return browsable;
    }

    /**
     * @param browsable
     */
    public void setBrowsable( boolean browsable )
    {
        this.browsable = browsable;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort( int port )
    {
        this.port = port;
    }

    /**
     * @return
     */
    public String getLocalStore()
    {
        return localStore;
    }

    /**
     * @param localStore
     */
    public void setLocalStore( String localStore )
    {
        this.localStore = localStore;
    }

    public void addProxy( ProxyConfiguration pc )
    {
        proxies.put( pc.getKey(), pc );
    }

    public void removeProxy( String key )
    {
        proxies.remove( key );
    }

    public ProxyConfiguration getProxy( String key )
    {
        return (ProxyConfiguration) proxies.get( key );
    }

    /**
     * There is no specific order to proxy configuration.
     * @return
     */
    public List getProxies()
    {
        return new ArrayList( proxies.values() );
    }

    public void addRepo( RepoConfiguration repo )
    {
        repos.add( repo );
    }

    public List getRepos()
    {
        return Collections.unmodifiableList( repos );
    }

    public String getServerName()
    {
        return serverName;
    }

    public void setServerName( String serverName )
    {
        this.serverName = serverName;
    }

    private int snapshotUpdateInterval;
    private boolean snapshotUpdate;
    private String lastModifiedDateFormat;

    public int getSnapshotUpdateInterval()
    {
        return snapshotUpdateInterval;
    }

    public void setSnapshotUpdateInterval( int snapshotUpdateInterval )
    {
        this.snapshotUpdateInterval = snapshotUpdateInterval;
    }

    public void setSnapshotUpdate( boolean snapshotUpdate )
    {
        this.snapshotUpdate = snapshotUpdate;
    }

    public boolean getSnapshotUpdate()
    {
        return snapshotUpdate;
    }

    public String getLastModifiedDateFormat()
    {
        return lastModifiedDateFormat;
    }

    public void setLastModifiedDateFormat( String lastModifiedDateFormat )
    {
        this.lastModifiedDateFormat = lastModifiedDateFormat;
    }

    public boolean isSearchable()
    {
        return searchable;
    }

    public void setSearchable( boolean searchable )
    {
        this.searchable = searchable;
    }
}