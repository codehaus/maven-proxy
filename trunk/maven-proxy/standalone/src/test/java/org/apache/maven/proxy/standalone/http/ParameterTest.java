package org.apache.maven.proxy.standalone.http;

import junit.framework.TestCase;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class ParameterTest extends TestCase
{
	public void testEmpty()
	{
		try
		{
			new Parameter("");
			fail("Should have thrown illegal argument exception");
		}
		catch (IllegalArgumentException e)
		{
			//Success   
		}
	}

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
