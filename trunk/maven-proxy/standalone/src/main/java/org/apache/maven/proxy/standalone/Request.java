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

public class Request
{
    
    
    String resource;
    String[] parameterNames;
    String[] parameterValues;
    
	//Expects something like
	//GET /fred.txt?a=d HTTP/1.1
	//Don't feed it anything too complicated    
	public Request(String requestString)
	{

	}
}
