package org.apache.maven.proxy.standalone;

import java.io.IOException;

import org.apache.maven.proxy.standalone.http.*;

/**
 * The ProxyRepoServer implements an HttpServer that serves files from a
 * virtual maven repository.
 */
public class ProxyRepoServer extends AbstractHttpServer
{
	/**
	 * @return the bytecodes for the class
	 */
	public Response getResponse(Request request) throws IOException
	{
		Response response = new Response();
		return response;
	}

}