package org.apache.maven.proxy.standalone;

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

import org.apache.maven.proxy.standalone.http.AbstractHttpServer;
import org.apache.maven.proxy.standalone.http.Request;
import org.apache.maven.proxy.standalone.http.Response;


/**
 * The ProxyRepoServer implements an HttpServer that serves files from a
 * virtual maven repository.
 */
public class ProxyRepoServer extends AbstractHttpServer
{

	/* (non-Javadoc)
	 * @see org.apache.maven.proxy.standalone.http.AbstractHttpServer#getResponse(org.apache.maven.proxy.standalone.http.Request)
	 */
	public Response getResponse( Request request ) throws IOException
	{
        Response response = new Response();
        return response;
    }

	

}