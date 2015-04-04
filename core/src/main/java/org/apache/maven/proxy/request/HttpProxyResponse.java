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

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Ben Walding
 */
public class HttpProxyResponse extends BaseProxyResponse
{
    private final HttpServletResponse response;

    public HttpProxyResponse( HttpServletResponse response )
    {
        this.response = response;
    }

    public void setLastModified( long lastModified )
    {
        response.setDateHeader( "Last-Modified", lastModified );
    }

    public void sendError( int statusCode ) throws IOException
    {
        response.sendError( statusCode );
    }

    public void setContentLength( int length )
    {
        response.setContentLength( length );
    }

    public OutputStream getOutputStream() throws IOException
    {
        return response.getOutputStream();
    }

    public void sendOK()
    {
        //this is default in http
    }

    public void setContentType( String contentType )
    {
        response.setContentType( contentType );
    }

}