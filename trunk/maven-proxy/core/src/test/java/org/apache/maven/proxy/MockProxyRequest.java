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

import org.apache.maven.proxy.request.BaseProxyRequest;

/**
 * @author Ben Walding
 */
public class MockProxyRequest extends BaseProxyRequest
{
    private final String path;
    private final long lastModified;
    private final boolean headOnly;

    public MockProxyRequest( String path, long lastModified, boolean headOnly )
    {
        this.path = path;
        this.lastModified = lastModified;
        this.headOnly = headOnly;
    }

    public long getLastModified()
    {
        return lastModified;
    }

    public String getPath()
    {
        return path;
    }

    public boolean isHeadOnly()
    {
        return headOnly;
    }

}