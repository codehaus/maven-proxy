package org.apache.maven.proxy.standalone.http;

import java.io.IOException;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class MockHttpServer extends AbstractHttpServer
{

	/* (non-Javadoc)
	 * @see org.apache.maven.proxy.standalone.http.AbstractHttpServer#getResponse(org.apache.maven.proxy.standalone.http.Request)
	 */
	public Response getResponse(Request request) throws IOException
	{
		return null;
	}

}
