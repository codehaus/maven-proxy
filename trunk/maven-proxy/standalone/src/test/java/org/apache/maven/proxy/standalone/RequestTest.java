/*
 * Created on 21/10/2003
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.apache.maven.proxy.standalone;

import junit.framework.TestCase;

/**
 * @author Ben Walding
 * @version $Id$
 */
public class RequestTest extends TestCase
{
    public void testSimple()
    {
        String rs = "GET /fred.txt HTTP/1.1";
        Request r = new Request(rs);
        assertEquals(rs + " / resource", "/fred.txt", r.getResource());
        assertEquals(rs + " / httpversion", "HTTP/1.1", r.getHttpVersion());
        assertEquals(rs + " / params.size", 0, r.getParameters().length);
    }

    public void testHttpVersion()
    {
        String rs = "GET /fred.txt HTTP/1.3";
        Request r = new Request(rs);
        assertEquals(rs + " / resource", "/fred.txt", r.getResource());
        assertEquals(rs + " / httpversion", "HTTP/1.3", r.getHttpVersion());
        assertEquals(rs + " / params.size", 0, r.getParameters().length);
    }

    public void testParameters()
    {
        String rs = "GET /fred.txt?a=b HTTP/1.3";
        Request r = new Request(rs);
        assertEquals(rs + " / resource", "/fred.txt", r.getResource());
        assertEquals(rs + " / httpversion", "HTTP/1.3", r.getHttpVersion());
        assertEquals(rs + " / params.size", 1, r.getParameters().length);
        Parameter p0 = r.getParameters()[0];
        assertEquals("p0.name", "a", p0.getName());
        assertEquals("p0.value", "b", p0.getValue());
    }

    public void testParameters2()
    {
        String rs = "GET /fred.txt?a=b&c=d HTTP/1.3";
        Request r = new Request(rs);
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
}