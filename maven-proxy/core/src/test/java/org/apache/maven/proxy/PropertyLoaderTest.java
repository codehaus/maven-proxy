package org.apache.maven.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class PropertyLoaderTest extends TestCase
{
    public void testSimple() throws IOException
    {
        InputStream is = PropertyLoaderTest.class.getResourceAsStream("PropertyLoaderTest1.properties");
        PropertyLoader loader = new PropertyLoader();

        RetrievalComponentConfiguration rcc = loader.load(is);

        /////////////////////// Check Proxies ////////////////////////
        assertEquals("rcc.getProxies().size()", 3, rcc.getProxies().size());
        ProxyConfiguration pcOne = rcc.getProxy("one");
        ProxyConfiguration pcTwo = rcc.getProxy("two");
        ProxyConfiguration pcThree = rcc.getProxy("three");

        assertNotNull("pcOne", pcOne);
        assertEquals("pcOne.host", "proxy1.example.com", pcOne.getHost());
        assertEquals("pcOne.port", 3128, pcOne.getPort());

        assertNotNull("pcTwo", pcTwo);
        assertEquals("pcTwo.host", "proxy2.example.org", pcTwo.getHost());
        assertEquals("pcTwo.port", 80, pcTwo.getPort());
        assertEquals("pcTwo.username", "username2", pcTwo.getUsername());
        assertEquals("pcTwo.password", "password2", pcTwo.getPassword());

        assertNotNull("pcThree", pcThree);
        assertEquals("pcThree.host", "proxy3.example.net", pcThree.getHost());
        assertEquals("pcThree.port", 3129, pcThree.getPort());
        assertEquals("pcThree.username", "username3", pcThree.getUsername());
        assertEquals("pcThree.password", "password3", pcThree.getPassword());

        assertNull("rcc.getProxy(snuffleuffigus)", rcc.getProxy("snuffleuffigus"));

        /////////////////////// Check Repos ////////////////////////
        List repos = rcc.getRepos();
        assertEquals("repos.size()", 3, repos.size());

        RepoConfiguration rcIbiblio = (RepoConfiguration) repos.get(0);
        assertNotNull("rcIbiblio", rcIbiblio);
        assertEquals("rcIbiblio.url", "http://www.ibiblio.org/maven", rcIbiblio.getUrl());
        assertNull("rcIbiblio.username", rcIbiblio.getUsername());
        assertNull("rcIbiblio.password", rcIbiblio.getPassword());
        assertEquals("rcIbiblio.proxy", "one", rcIbiblio.getProxyKey());

        RepoConfiguration rcDist = (RepoConfiguration) repos.get(1);
        assertNotNull("rcDist", rcDist);
        assertEquals("rcDist.url", "http://dist.codehaus.org", rcDist.getUrl());
        assertNull("rcDist.username", rcDist.getUsername());
        assertNull("rcDist.password", rcDist.getPassword());
        assertEquals("rcDist.proxy", "two", rcDist.getProxyKey());

        RepoConfiguration rcPrivate = (RepoConfiguration) repos.get(2);
        assertNotNull("rcPrivate", rcPrivate);
        assertEquals("rcPrivate.url", "http://private.example.com/internal", rcPrivate.getUrl());
        assertEquals("rcPrivate.username", "username1", rcPrivate.getUsername());
        assertEquals("rcPrivate.password", "password1", rcPrivate.getPassword());
        assertEquals("rcPrivate.proxy", "three", rcPrivate.getProxyKey());
    }
}
