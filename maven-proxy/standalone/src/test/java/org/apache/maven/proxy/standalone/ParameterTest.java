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
public class ParameterTest extends TestCase
{
	public void testSimple()
	{
		Parameter p;

		p = new Parameter("a");
		assertEquals("a.name", "a", p.getName());
		assertEquals("a.value", null, p.getValue());

		p = new Parameter("a=");
		assertEquals("a=.name", "a", p.getName());
		assertEquals("a=.value", "", p.getValue());

		p = new Parameter("a=b");
		assertEquals("a=b.name", "a", p.getName());
		assertEquals("a=b.value", "b", p.getValue());

		p = new Parameter("=b");
		assertEquals("=b.name", "", p.getName());
		assertEquals("=b.value", "b", p.getValue());
	}
}
