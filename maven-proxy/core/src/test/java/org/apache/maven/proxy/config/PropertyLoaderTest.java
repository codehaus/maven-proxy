package org.apache.maven.proxy.config;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.maven.proxy.utils.ResourceUtil;

/**
 * @author Ben Walding
 * @version $Id$
 */
public class PropertyLoaderTest extends TestCase
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger( PropertyLoaderTest.class );

    public void testSimple() throws IOException, ValidationException
    {
        InputStream is = PropertyLoaderTest.class.getResourceAsStream( "PropertyLoaderTest1.properties" );
        PropertyLoader loader = new PropertyLoader();

        RetrievalComponentConfiguration rcc = loader.load( is );

        /////////////////////// Check Globals ////////////////////////
        assertEquals( "rcc.getLocalStore()", "./target/repo", rcc.getLocalStore() );
        assertEquals( "rcc.getPort()", 9999, rcc.getPort() );
        assertEquals( "rcc.getPrefix()", "repository", rcc.getPrefix() );
        assertEquals( "rcc.getServerName()", "http://localhost:9999", rcc.getServerName() );
        assertEquals( "rcc.getSnapshotUpdate()", true, rcc.getSnapshotUpdate() );
        assertEquals( "rcc.getLastModifiedDateFormat()", "yyyy/MM/dd HH:mm:ss", rcc.getLastModifiedDateFormat() );
        assertTrue( "rcc.isBrowsable()", rcc.isBrowsable() );
        assertTrue( "rcc.isSearchable()", rcc.isSearchable() );
        assertEquals( "rcc.bgColor", rcc.getBgColor(), "#14B" );
        assertEquals( "rcc.bgColorHighlight", rcc.getBgColorHighlight(), "#9BF" );
        assertEquals( "rcc.rowColor", rcc.getRowColor(), "#CCF" );
        assertEquals( "rcc.rowColorHighlight", rcc.getRowColorHighlight(), "#DDF" );
        assertEquals( "rcc.styleSheet", rcc.getStylesheet(), "/maven-proxy/style.css" );

        /////////////////////// Check Proxies ////////////////////////
        assertEquals( "rcc.getProxies().size()", 3, rcc.getProxies().size() );
        verifyProxyOne( rcc.getProxy( "one" ) );
        verifyProxyTwo( rcc.getProxy( "two" ) );
        verifyProxyThree( rcc.getProxy( "three" ) );

        assertNull( "rcc.getProxy(snuffleuffigus)", rcc.getProxy( "snuffleuffigus" ) );

        /////////////////////// Check Repos ////////////////////////
        List repos = rcc.getRepos();
        assertEquals( "repos.size()", 5, repos.size() );
        verifyRepoGlobal( (GlobalRepoConfiguration) repos.get( 0 ) );
        verifyRepoGlobal( rcc.getGlobalRepo() );
        assertSame( rcc.getGlobalRepo(), repos.get( 0 ) );

        verifyRepoLocal( (FileRepoConfiguration) repos.get( 1 ) );
        verifyRepoIbiblio( (HttpRepoConfiguration) repos.get( 2 ) );
        verifyRepoDist( (HttpRepoConfiguration) repos.get( 3 ) );
        verifyRepoPrivate( (HttpRepoConfiguration) repos.get( 4 ) );
    }

    private void verifyRepoGlobal( GlobalRepoConfiguration configuration )
    {
        assertNotNull( "configuration", configuration );
        assertEquals( "configuration.getUrl()", "file:///./target/repo", configuration.getUrl() );
        assertEquals( "configuration.getDescription()", "Global Repository", configuration.getDescription() );
        assertEquals( "configuration.getHardFail()", true, configuration.getHardFail() );
        assertEquals( "configuration.getCacheFailures()", true, configuration.getCacheFailures() );
        assertEquals( "configuration.getCachePeriod()", 3600, configuration.getCachePeriod() );
    }

    private void verifyRepoLocal( FileRepoConfiguration configuration )
    {
        assertNotNull( "configuration", configuration );
        assertEquals( "configuration.getUrl()", "file:///./target/repo-local", configuration.getUrl() );
        assertEquals( "configuration.getDescription()", "Super Secret Custom Repository", configuration
                        .getDescription() );
        assertEquals( "configuration.getHardFail()", true, configuration.getHardFail() );
    }

    private void verifyProxyOne( ProxyConfiguration pcOne )
    {
        assertNotNull( "pcOne", pcOne );
        assertEquals( "pcOne.host", "proxy1.example.com", pcOne.getHost() );
        assertEquals( "pcOne.port", 3128, pcOne.getPort() );
    }

    private void verifyProxyTwo( ProxyConfiguration pcTwo )
    {
        assertNotNull( "pcTwo", pcTwo );
        assertEquals( "pcTwo.host", "proxy2.example.org", pcTwo.getHost() );
        assertEquals( "pcTwo.port", 80, pcTwo.getPort() );
        assertEquals( "pcTwo.username", "username2", pcTwo.getUsername() );
        assertEquals( "pcTwo.password", "password2", pcTwo.getPassword() );
    }

    private void verifyProxyThree( ProxyConfiguration pcThree )
    {
        assertNotNull( "pcThree", pcThree );
        assertEquals( "pcThree.host", "proxy3.example.net", pcThree.getHost() );
        assertEquals( "pcThree.port", 3129, pcThree.getPort() );
        assertEquals( "pcThree.username", "username3", pcThree.getUsername() );
        assertEquals( "pcThree.password", "password3", pcThree.getPassword() );
    }

    private void verifyRepoIbiblio( HttpRepoConfiguration rcIbiblio )
    {
        assertNotNull( "rcIbiblio", rcIbiblio );
        assertEquals( "www-ibiblio-org", rcIbiblio.getKey() );
        assertEquals( "rcIbiblio.url", "http://www.ibiblio.org/maven", rcIbiblio.getUrl() );
        assertEquals( "rcIbiblio.description", "www.ibiblio.org", rcIbiblio.getDescription() );
        assertEquals( "rcIbiblio.hardfail", true, rcIbiblio.getHardFail() );
        assertNull( "rcIbiblio.username", rcIbiblio.getUsername() );
        assertNull( "rcIbiblio.password", rcIbiblio.getPassword() );
        assertEquals( "rcIbiblio.proxy", "one", rcIbiblio.getProxy().getKey() );
    }

    private void verifyRepoDist( HttpRepoConfiguration rcDist )
    {
        assertNotNull( "rcDist", rcDist );
        assertEquals( "dist-codehaus-org", rcDist.getKey() );
        assertEquals( "rcDist.url", "http://dist.codehaus.org", rcDist.getUrl() );
        assertEquals( "rcDist.description", "dist-codehaus-org", rcDist.getDescription() );
        assertEquals( "rcDist.hardfail", false, rcDist.getHardFail() );
        assertNull( "rcDist.username", rcDist.getUsername() );
        assertNull( "rcDist.password", rcDist.getPassword() );
        assertEquals( "rcDist.proxy", "two", rcDist.getProxy().getKey() );
    }

    private void verifyRepoPrivate( HttpRepoConfiguration rcPrivate )
    {
        assertNotNull( "rcPrivate", rcPrivate );
        assertEquals( "private-example-com", rcPrivate.getKey() );
        assertEquals( "rcPrivate.url", "http://private.example.com/internal", rcPrivate.getUrl() );
        assertEquals( "rcPrivate.description", "Commercial In Confidence Repository", rcPrivate.getDescription() );
        assertEquals( "rcPrivate.hardfail", true, rcPrivate.getHardFail() );
        assertEquals( "rcPrivate.username", "username1", rcPrivate.getUsername() );
        assertEquals( "rcPrivate.password", "password1", rcPrivate.getPassword() );
        assertEquals( "rcPrivate.proxy", "three", rcPrivate.getProxy().getKey() );
    }

    public void testNullPointers() throws Exception
    {
        InputStream is;
        Properties props = new Properties();
        PropertyLoader loader = new PropertyLoader();

        is = PropertyLoaderTest.class.getResourceAsStream( "PropertyLoaderTest1.properties" );
        props.load( is );
        props.remove( PropertyLoader.REPO_LOCAL_STORE );
        assertThrowsValidationException( loader, props );
        props.clear();

        is = PropertyLoaderTest.class.getResourceAsStream( "PropertyLoaderTest1.properties" );
        props.load( is );
        props.remove( PropertyLoader.BROWSABLE );
        assertThrowsValidationException( loader, props );
        props.clear();

        is = PropertyLoaderTest.class.getResourceAsStream( "PropertyLoaderTest1.properties" );
        props.load( is );
        props.remove( "repo.list" );
        assertThrowsValidationException( loader, props );
        props.clear();
    }

    private void assertThrowsValidationException( PropertyLoader loader, Properties props )
    {
        try
        {
            loader.load( props );
            fail( "Expected ValidationException" );
        }
        catch ( ValidationException ex )
        {
            LOGGER.info( "Received expected validation exception : " + ex );
            // expected
        }
    }

    public void testSimple2() throws IOException
    {
        InputStream is = PropertyLoaderTest.class.getResourceAsStream( "PropertyLoaderTest2.properties" );
        try
        {
            PropertyLoader loader = new PropertyLoader();
            Properties props = new Properties();
            props.load( is );
            assertThrowsValidationException( loader, props );
        }
        finally
        {
            ResourceUtil.close( is );
        }

    }

    public void testSimple3() throws Exception
    {
        InputStream is = PropertyLoaderTest.class.getResourceAsStream( "PropertyLoaderTest3.properties" );
        try
        {
            PropertyLoader loader = new PropertyLoader();
            Properties props = new Properties();
            props.load( is );
            assertThrowsValidationException( loader, props );
        }
        finally
        {
            ResourceUtil.close( is );
        }
    }

    public void testSimple4() throws Exception
    {
        InputStream is = PropertyLoaderTest.class.getResourceAsStream( "PropertyLoaderTest4.properties" );
        try
        {
            PropertyLoader loader = new PropertyLoader();
            Properties props = new Properties();
            props.load( is );
            loader.load( props );
            //All good
        }
        finally
        {
            ResourceUtil.close( is );
        }
    }

}