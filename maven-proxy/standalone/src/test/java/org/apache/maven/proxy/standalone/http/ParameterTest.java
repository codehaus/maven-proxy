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
