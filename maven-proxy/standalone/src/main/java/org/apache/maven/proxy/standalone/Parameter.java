/*
 * Created on 21/10/2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.maven.proxy.standalone;

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
