/*
 * Created on 22/10/2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.maven.proxy.standalone.http;

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
