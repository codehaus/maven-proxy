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
public class ProxyConfiguration
{
    private final String key;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    
    public ProxyConfiguration(String key, String host, int port, String username, String password) {
        this.key = key;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    
    
    
    /**
     * @return
     */
    public String getHost()
    {
        return host;
    }

    /**
     * @return
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @return
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @return
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @return
     */
    public String getUsername()
    {
        return username;
    }

    

}
