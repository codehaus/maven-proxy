package org.apache.maven.proxy.request;

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

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ben Walding
 */
public class HttpProxyRequest extends BaseProxyRequest
{
    private final HttpServletRequest httpRequest;

    public HttpProxyRequest( HttpServletRequest httpRequest )
    {
        this.httpRequest = httpRequest;
    }

    public long getLastModified()
    {
        return httpRequest.getDateHeader( "Last-Modified" );
    }

    public String getPath()
    {
        return httpRequest.getPathInfo();
    }

    public boolean isHeadOnly()
    {
        return httpRequest.getMethod().equalsIgnoreCase( "HEAD" );
    }
}