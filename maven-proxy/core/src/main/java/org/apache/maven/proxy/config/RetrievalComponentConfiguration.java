package org.apache.maven.proxy.config;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Maven" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Maven", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ====================================================================
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
