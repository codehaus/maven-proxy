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
 * Immutable.
 * @author  Ben Walding
 * @version $Id$
 */
public class Header
{
    private final String name;
    private final String value;
    
	public Header(String header) {
        int colonPos =header.indexOf(": ");
        if (colonPos == -1) {
         throw new IllegalArgumentException("Headers must have a ': ' (I think - check the spec"); //TODO check the spec to see if this is true   
        }
        name = header.substring(0, colonPos);
        value = header.substring(colonPos + 2); // ": "
    }
    
	/**
	 * @return Returns the header name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return Returns the header value.
	 */
	public String getValue()
	{
		return value;
	}

}
