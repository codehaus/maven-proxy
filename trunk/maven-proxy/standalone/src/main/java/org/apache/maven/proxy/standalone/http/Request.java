/*
 * Created on 21/10/2003
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.apache.maven.proxy.standalone.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
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
	private final Header[] headers;

	//Expects something like
	//GET /fred.txt?a=d HTTP/1.1
	//Don't feed it anything too complicated (i.e. escaped things)
	public Request(Reader reader) throws IOException
	{
		String lines[] = getLines(reader);

		String requestString = lines[0];
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

		parameters = processParameters(parameterString);
		headers = processHeaders(lines, 1);
	}

	private Parameter[] processParameters(String parameterString)
	{
		StringTokenizer st = new StringTokenizer(parameterString, "&");
		List toks = new ArrayList();
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			toks.add(new Parameter(token));
		}
		return (Parameter[]) toks.toArray(new Parameter[toks.size()]);
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
	 * Note: Performance critical code should hold onto the returned parameters.
	 * @return Returns the parameters.
	 */
	public Parameter[] getParameters()
	{
		return (Parameter[]) parameters.clone();
	}

	public String toString()
	{
		return request;
	}

	public void dump(PrintStream ps)
	{
		ps.println("Request:     " + request);
		ps.println("  Resource:  " + resource);
		ps.println("  HTTP Ver:  " + httpVersion);
		for (int i = 0; i < parameters.length; i++)
		{
			ps.println("  Param[" + i + "] = " + parameters[i].getName() + " = " + parameters[i].getValue());
		}

		for (int i = 0; i < headers.length; i++)
		{
			ps.println("  Header[" + i + "] = " + headers[i].getName() + " = " + headers[i].getValue());
		}

	}

	private static Header[] processHeaders(String[] lines, int start)
	{
		List headers = new ArrayList();
		for (int i = start; i < lines.length; i++)
		{
			headers.add(new Header(lines[i]));
		}
		return (Header[]) headers.toArray(new Header[headers.size()]);
	}

	private static String[] getLines(Reader in) throws IOException
	{
		BufferedReader br = new BufferedReader(in);
		List lines = new ArrayList();
		while (true)
		{
			String line = br.readLine();
			if (line == null || line.length() == 0 || line.charAt(0) == '\r' || line.charAt(0) == '\n')
			{
				break;
			}
			lines.add(line);
		}

		return (String[]) lines.toArray(new String[lines.size()]);
	}
	/**
	 * Note: Performance critical code should hold onto the returned headers.
	 * @return Returns the headers.
	 */
	public Header[] getHeaders()
	{
		return (Header[]) headers.clone();
	}

}