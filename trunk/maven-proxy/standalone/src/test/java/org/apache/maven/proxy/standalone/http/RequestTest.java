/*
 * Created on 21/10/2003
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.apache.maven.proxy.standalone.http;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import junit.framework.TestCase;

/**
 * @author Ben Walding
 * @version $Id$
 */
public class RequestTest extends TestCase
{
	protected Request getRequest(String header) throws IOException
	{
		StringReader reader = new StringReader(header);
		Request r = new Request(reader);
		reader.close();
		return r;
	}

	public void testSimple() throws IOException
	{
		String rs = "GET /fred.txt HTTP/1.1";
		Request r = getRequest(rs);
		assertEquals(rs + " / resource", "/fred.txt", r.getResource());
		assertEquals(rs + " / httpversion", "HTTP/1.1", r.getHttpVersion());
		assertEquals(rs + " / params.size", 0, r.getParameters().length);
	}

	public void testHttpVersion() throws IOException
	{
		String rs = "GET /fred.txt HTTP/1.3";
		Request r = getRequest(rs);
		assertEquals(rs + " / resource", "/fred.txt", r.getResource());
		assertEquals(rs + " / httpversion", "HTTP/1.3", r.getHttpVersion());
		assertEquals(rs + " / params.size", 0, r.getParameters().length);
	}

	public void testParameters() throws IOException
	{
		String rs = "GET /fred.txt?a=b HTTP/1.3";
		Request r = getRequest(rs);
		assertEquals(rs + " / resource", "/fred.txt", r.getResource());
		assertEquals(rs + " / httpversion", "HTTP/1.3", r.getHttpVersion());
		assertEquals(rs + " / params.size", 1, r.getParameters().length);
		Parameter p0 = r.getParameters()[0];
		assertEquals("p0.name", "a", p0.getName());
		assertEquals("p0.value", "b", p0.getValue());
	}

	public void testParameters2() throws IOException
	{
		String rs = "GET /fred.txt?a=b&c=d HTTP/1.3";
		Request r = getRequest(rs);
		assertEquals(rs + " / resource", "/fred.txt", r.getResource());
		assertEquals(rs + " / httpversion", "HTTP/1.3", r.getHttpVersion());
		assertEquals(rs + " / params.size", 2, r.getParameters().length);

		Parameter p0 = r.getParameters()[0];
		assertEquals("p0.name", "a", p0.getName());
		assertEquals("p0.value", "b", p0.getValue());

		Parameter p1 = r.getParameters()[1];
		assertEquals("p1.name", "c", p1.getName());
		assertEquals("p1.value", "d", p1.getValue());
	}

	public void testHeader1() throws IOException
	{
		Reader reader = new InputStreamReader(getClass().getResourceAsStream("RequestTest1.txt"));
		Request request = new Request(reader);
		assertEquals("resource", "/fred.txt", request.getResource());
		assertEquals("httpversion", "HTTP/1.1", request.getHttpVersion());

		assertEquals("params.size", 1, request.getParameters().length);

		Parameter p0 = request.getParameters()[0];
		assertEquals("p0.name", "a", p0.getName());
		assertEquals("p0.value", "d", p0.getValue());

		assertEquals("headers.size", 8, request.getHeaders().length);
		Header h0 = request.getHeaders()[0];
		assertEquals("h0.name", "Host", h0.getName());
		assertEquals("h0.value", "localhost:9999", h0.getValue());

	}
}