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
import java.util.Set;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class RetrievalComponentConfiguration
{
    private final Map proxies = new HashMap();
    private final List repos = new ArrayList();
    private String localStore;
    private boolean browsable;
    private int port;

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
    public void setBrowsable(boolean browsable)
    {
        this.browsable = browsable;
    }

    /**
     * @return
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @param port
     */
    public void setPort(int port)
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
    public void setLocalStore(String localStore)
    {
        this.localStore = localStore;
    }

    public void addProxy(ProxyConfiguration pc)
    {
        proxies.put(pc.getKey(), pc);
    }

    public void removeProxy(String key)
    {
        proxies.remove(key);
    }

    public ProxyConfiguration getProxy(String key)
    {
        return (ProxyConfiguration) proxies.get(key);
    }

    /**
     * There is no specific order to proxy configuration.
     * @return
     */
    public Set getProxies()
    {
        return Collections.unmodifiableSet(proxies.entrySet());
    }

    public void addRepo(RepoConfiguration repo)
    {
        repos.add(repo);
    }

    public List getRepos()
    {
        return Collections.unmodifiableList(repos);
    }

}