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

/**
 * @author  Ben Walding
 * @version $Id$
 */
class Parameter
{
	private String name;
	private String value;
	public Parameter(String s)
	{
		if (s == null || s.length() == 0)
		{
			throw new IllegalArgumentException("Empty parameter string");
		}

		int equalPos = s.indexOf('=');

		if (equalPos == -1)
		{
			name = s;
			value = null;
		}
		else
		{
			name = s.substring(0, equalPos); //TODO decode
			value = s.substring(equalPos + 1); //TODO decode
		}
	}

	/**
	 * @return Returns the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return Returns the value.
	 */
	public String getValue()
	{
		return value;
	}
}
