package org.apache.maven.proxy.standalone;

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