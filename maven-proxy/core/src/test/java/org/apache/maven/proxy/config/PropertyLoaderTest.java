package org.apache.maven.proxy.config;

/*
 * ==================================================================== The
 * Apache Software License, Version 1.1
 * 
 * Copyright (c) 2003 The Apache Software Foundation. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 1.
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgment: "This product includes software developed by the Apache
 * Software Foundation (http://www.apache.org/)." Alternately, this
 * acknowledgment may appear in the software itself, if and wherever such
 * third-party acknowledgments normally appear. 4. The names "Apache" and
 * "Apache Software Foundation" and "Apache Maven" must not be used to endorse
 * or promote products derived from this software without prior written
 * permission. For written permission, please contact apache@apache.org. 5.
 * Products derived from this software may not be called "Apache", "Apache
 * Maven", nor may "Apache" appear in their name, without prior written
 * permission of the Apache Software Foundation.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals
 * on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/> .
 * 
 * ====================================================================
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author Ben Walding
 * @version $Id$
 */
public class PropertyLoaderTest extends TestCase
{
    public void testSimple() throws IOException, ValidationException
    {
        InputStream is = PropertyLoaderTest.class.getResourceAsStream("PropertyLoaderTest1.properties");
        PropertyLoader loader = new PropertyLoader();

        RetrievalComponentConfiguration rcc = loader.load(is);

        /////////////////////// Check Globals ////////////////////////
        assertEquals("rcc.getLocalStore()", "/var/tmp/proxy-repo", rcc.getLocalStore());
        assertEquals("rcc.getPort()", 9999, rcc.getPort());
        assertTrue("rcc.isBrowsable()", rcc.isBrowsable());

        /////////////////////// Check Proxies ////////////////////////
        assertEquals("rcc.getProxies().size()", 3, rcc.getProxies().size());
        verifyProxyOne(rcc.getProxy("one"));
        verifyProxyTwo(rcc.getProxy("two"));
        verifyProxyThree(rcc.getProxy("three"));

        assertNull("rcc.getProxy(snuffleuffigus)", rcc.getProxy("snuffleuffigus"));

        /////////////////////// Check Repos ////////////////////////
        List repos = rcc.getRepos();
        assertEquals("repos.size()", 4, repos.size());
        verifyRepoLocal((FileRepoConfiguration) repos.get(0));
        verifyRepoIbiblio((HttpRepoConfiguration) repos.get(1));
        verifyRepoDist((HttpRepoConfiguration) repos.get(2));
        verifyRepoPrivate((HttpRepoConfiguration) repos.get(3));
    }

    /**
	 * @param configuration
	 */
    private void verifyRepoLocal(FileRepoConfiguration configuration)
    {
        assertNotNull("configuration", configuration);
        assertEquals("configuration.getUrl()", "file:///usr/local/custom-repo", configuration.getUrl());
    }

    private void verifyProxyOne(ProxyConfiguration pcOne)
    {
        assertNotNull("pcOne", pcOne);
        assertEquals("pcOne.host", "proxy1.example.com", pcOne.getHost());
        assertEquals("pcOne.port", 3128, pcOne.getPort());
    }

    private void verifyProxyTwo(ProxyConfiguration pcTwo)
    {
        assertNotNull("pcTwo", pcTwo);
        assertEquals("pcTwo.host", "proxy2.example.org", pcTwo.getHost());
        assertEquals("pcTwo.port", 80, pcTwo.getPort());
        assertEquals("pcTwo.username", "username2", pcTwo.getUsername());
        assertEquals("pcTwo.password", "password2", pcTwo.getPassword());
    }

    private void verifyProxyThree(ProxyConfiguration pcThree)
    {
        assertNotNull("pcThree", pcThree);
        assertEquals("pcThree.host", "proxy3.example.net", pcThree.getHost());
        assertEquals("pcThree.port", 3129, pcThree.getPort());
        assertEquals("pcThree.username", "username3", pcThree.getUsername());
        assertEquals("pcThree.password", "password3", pcThree.getPassword());
    }

    private void verifyRepoIbiblio(HttpRepoConfiguration rcIbiblio)
    {
        assertNotNull("rcIbiblio", rcIbiblio);
        assertEquals("www-ibiblio-org", rcIbiblio.getKey());
        assertEquals("rcIbiblio.url", "http://www.ibiblio.org/maven", rcIbiblio.getUrl());
        assertNull("rcIbiblio.username", rcIbiblio.getUsername());
        assertNull("rcIbiblio.password", rcIbiblio.getPassword());
        assertEquals("rcIbiblio.proxy", "one", rcIbiblio.getProxy().getKey());
    }

    private void verifyRepoDist(HttpRepoConfiguration rcDist)
    {
        assertNotNull("rcDist", rcDist);
        assertEquals("dist-codehaus-org", rcDist.getKey());
        assertEquals("rcDist.url", "http://dist.codehaus.org", rcDist.getUrl());
        assertNull("rcDist.username", rcDist.getUsername());
        assertNull("rcDist.password", rcDist.getPassword());
        assertEquals("rcDist.proxy", "two", rcDist.getProxy().getKey());
    }

    private void verifyRepoPrivate(HttpRepoConfiguration rcPrivate)
    {
        assertNotNull("rcPrivate", rcPrivate);
        assertEquals("private-example-com", rcPrivate.getKey());
        assertEquals("rcPrivate.url", "http://private.example.com/internal", rcPrivate.getUrl());
        assertEquals("rcPrivate.username", "username1", rcPrivate.getUsername());
        assertEquals("rcPrivate.password", "password1", rcPrivate.getPassword());
        assertEquals("rcPrivate.proxy", "three", rcPrivate.getProxy().getKey());
    }

    public void testNullPointers() throws Exception
    {
        InputStream is;
        Properties props = new Properties();
        PropertyLoader loader = new PropertyLoader();

        is = PropertyLoaderTest.class.getResourceAsStream("PropertyLoaderTest1.properties");

        props.load(is);
        props.remove(PropertyLoader.REPO_LOCAL_STORE);
        assertThrowsValidationException(loader, props);
        props.clear();

        props.load(is);
        props.remove(PropertyLoader.BROWSABLE);
        assertThrowsValidationException(loader, props);
        props.clear();

        props.load(is);
        props.remove("repo.list");
        assertThrowsValidationException(loader, props);
        props.clear();
    }

    private void assertThrowsValidationException(PropertyLoader loader, Properties props) throws IOException
    {
        try
        {
            loader.load(props);
            fail("Expected ValidationException");
        }
        catch (ValidationException ex)
        {
            // expected
        }
    }

}