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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.maven.proxy.IOUtility;

/**
 * @author Ben Walding
 */
public abstract class BaseProxyResponse implements ProxyResponse
{
    //Nothing to do here yet
    public void sendFile( File file ) throws IOException
    {
        InputStream is = new FileInputStream( file );
        OutputStream os = getOutputStream();
        try
        {
            IOUtility.transferStream( is, os );
            sendOK();
        }
        catch ( Exception e )
        {
            sendError( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
        }
        finally
        {
            IOUtility.close( os );
            IOUtility.close( is );
        }
    }
}