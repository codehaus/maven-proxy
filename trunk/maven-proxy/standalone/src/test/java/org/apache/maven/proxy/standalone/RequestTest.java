/*
 * Created on 21/10/2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.maven.proxy.standalone;

import junit.framework.TestCase;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class RequestTest extends TestCase
{
	public void testSimple() {
		 Request r = new Request("GET /fred.txt HTTP/1.1");
    }
}
