/*
 * Created on 21/10/2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.maven.proxy.standalone.http;

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.maven.proxy.standalone.ProxyRepoServer;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class HttpServerTest extends TestCase
{
	public void testX()
	{

	}

	public void testRun() throws Exception
	{
		AbstractHttpServer server = new MockHttpServer();
		DefaultConfiguration c = new DefaultConfiguration("test");
		c.addAttribute("port", "9999");
		server.configure(c);
		server.start();
		Thread.sleep(60000);
		server.stop();
	}

}
