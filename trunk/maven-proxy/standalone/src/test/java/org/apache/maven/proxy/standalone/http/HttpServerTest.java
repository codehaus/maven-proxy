package org.apache.maven.proxy.standalone.http;

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

import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.DefaultConfiguration;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class HttpServerTest extends TestCase
{
	public void testRun() throws Exception
	{
		AbstractHttpServer server = new MockHttpServer();
		DefaultConfiguration c = new DefaultConfiguration("test");
        c.addAttribute("port", "9999");
		server.configure(c);
		server.start();
		Thread.sleep(1000);
		server.stop();
	}

}
