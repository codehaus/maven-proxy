/*
 * Created on 21/10/2003
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.apache.maven.proxy.standalone;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Ben Walding
 * @version $Id$
 */
public class Request
{
    private final String request;
    private final String resource;
    private final String httpVersion;
    private final Parameter[] parameters;

    //Expects something like
    //GET /fred.txt?a=d HTTP/1.1
    //Don't feed it anything too complicated (i.e. escaped things)
    public Request(String requestString)
    {
        request = requestString;
        if (requestString.startsWith("GET "))
        {
            requestString = requestString.substring(4);
        }
        else
        {
            throw new IllegalArgumentException("Must start with GET<space>");
        }

        //find last space
        int lastSpacePos = requestString.lastIndexOf(' ');
        if (lastSpacePos == -1)
        {
            throw new IllegalArgumentException("Must end with HTTP/<#.#>");
        }

        httpVersion = requestString.substring(lastSpacePos + 1);
        requestString = requestString.substring(0, lastSpacePos);

        //find first ?
        int questionPos = requestString.indexOf('?');
        String parameterString;
        if (questionPos == -1)
        {
            resource = requestString;
            parameterString = "";
        }
        else
        {
            resource = requestString.substring(0, questionPos);
            parameterString = requestString.substring(questionPos + 1);
        }

        StringTokenizer st = new StringTokenizer(parameterString, "&");
        List toks = new ArrayList();
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            Parameter param = new Parameter(token);
            System.out.println("token:" + token);
            toks.add(param);
        }
        parameters = (Parameter[]) toks.toArray(new Parameter[toks.size()]);
    }

    /**
	 * @return Returns the resource.
	 */
    public String getResource()
    {
        return resource;
    }
    /**
	 * @return Returns the httpVer.
	 */
    public String getHttpVersion()
    {
        return httpVersion;
    }
    /**
	 * @return Returns the parameters.
	 */
    public Parameter[] getParameters()
    {
        return parameters;
    }
    
    public String toString() {
        return request;
    }
}