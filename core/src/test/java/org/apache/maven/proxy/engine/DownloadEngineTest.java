package org.apache.maven.proxy.engine;

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

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.maven.proxy.config.GlobalRepoConfiguration;
import org.apache.maven.proxy.config.RetrievalComponentConfiguration;

/**
 * This is the core test of how the proxy works with snapshots, partial downloads etc.
 *
 * I tried implementing without abstracting it all apart for testing, but it's just too fragile.
 *
 *
 * @author Ben Walding
 */
public class DownloadEngineTest extends TestCase
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger( DownloadEngineTest.class );

    private RetrievalComponentConfiguration createRCC_A()
    {
        RetrievalComponentConfiguration rcc = new RetrievalComponentConfiguration();
        rcc.addRepo( new GlobalRepoConfiguration( "target/repo" ) );
        rcc.addRepo( new MockRepoConfiguration( "MockA", "target/mock-a", "MockA", true, true, false, 1000 ) );
        rcc.setSnapshotUpdate( true );
        rcc.setMetaDataUpdate( true );
        rcc.setPOMUpdate( true );
        return rcc;
    }

    public void testRoundTime()
    {
        //-1 is special
        assertEquals( -1, DownloadEngine.round( -1 ) );

        assertEquals( 3000, DownloadEngine.round( 3010 ) );
        assertEquals( 3000, DownloadEngine.round( 3000 ) );
        assertEquals( 3000, DownloadEngine.round( 2999 ) );
        assertEquals( 2000, DownloadEngine.round( 2500 ) );
        assertEquals( 3000, DownloadEngine.round( 2501 ) );
        assertEquals( 4000, DownloadEngine.round( 3500 ) );
        assertEquals( 4000, DownloadEngine.round( 3501 ) );
    }

    private RetrievalComponentConfiguration createRCC_Flaky_A()
    {
        RetrievalComponentConfiguration rcc = new RetrievalComponentConfiguration();
        rcc.addRepo( new GlobalRepoConfiguration( "target/repo" ) );
        rcc.addRepo( new FlakyRepoConfiguration( true, true, true, false, 10 ) );
        rcc.setSnapshotUpdate( true );
        return rcc;
    }

    public void setUp() throws IOException
    {
        File targetRepo = new File( "target/repo" );
        deleteRecursive( targetRepo );
    }

    private void deleteRecursive( File target ) throws IOException
    {
        if ( !target.exists() )
        {
            LOGGER.debug( "Skipping deletion of non-existent target: " + target );
            return;
        }

        LOGGER.debug( "Starting removal of: " + target );
        File children[] = target.listFiles();
        for ( int i = 0; i < children.length; i++ )
        {
            File child = children[i];
            if ( child.isDirectory() )
            {
                deleteRecursive( child );
                if ( !child.delete() )
                {
                    throw new IOException( "Unable to delete:" + child );
                }
            }

            if ( child.isFile() )
            {
                if ( !child.delete() )
                {
                    throw new IOException( "Unable to delete:" + child );
                }
            }
        }
        LOGGER.debug( "Finished removal of: " + target );
    }

    /**
     * The client has requested a SNAPSHOT download and has provided an older Last-Modified entry.
     * If there is an update, they want the whole lot.
     */
    public void testHaveOlderSnapshot() throws IOException
    {
        LOGGER.info( "testHaveOldersnapshot: Client has older version of SNAPSHOT and will take the whole thing." );
        MockProxyRequest pRequest = new MockProxyRequest( "/a/a-SNAPSHOT.jar", 1000L, false, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( "application/octet-stream", pResponse.getContentType() );
        assertEquals( 100, pResponse.getContentLength() );
        assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );

        String s = pResponse.getContent();
        assertEquals( 100, s.length() );
        assertEquals( MockRepoConfiguration.MOCK_DATA_100, s );
    }

    /**
     * The client has requested a SNAPSHOT download and has provided a newer Last-Modified entry.
     * If there is an update, they want the whole lot.
     */
    public void testHaveNewerSnapshot() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/a/a-SNAPSHOT.jar", 10020000L, false, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( 100, pResponse.getContentLength() );
        assertEquals( "application/octet-stream", pResponse.getContentType() );
        assertEquals( HttpServletResponse.SC_NOT_MODIFIED, pResponse.getStatusCode() );
        assertNull( pResponse.getContent() );
    }

    /**
     * The client has requested a SNAPSHOT download and has provided a Last-Modified entry.
     * If there is an update, they don't want the whole lot. Just want to know when and how big
     */
    public void testOlderSnapshotHeadOnly() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/a/a-SNAPSHOT.jar", 1006000L, true, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( "application/octet-stream", pResponse.getContentType() );
        assertEquals( 100, pResponse.getContentLength() );
        assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );
        assertNull( pResponse.getContent() );
    }

    /**
     * The client has requested a SNAPSHOT download and has provided a Last-Modified entry.
     * If there is an update, they don't want the whole lot. Just want to know when and how big
     */
    public void testNewerSnapshotHeadOnly() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/a/a-SNAPSHOT.jar", 3005000L, true, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( "application/octet-stream", pResponse.getContentType() );
        assertEquals( 100, pResponse.getContentLength() );
        assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );
        assertNull( pResponse.getContent() );
    }

    /**
     * With non snapshot artifacts, the server simply tries to satisfy the download request -
     * it doesn't take into account the last modified values etc.
     *
     * It will respect a head only download request
     */
    public void testArtifactGet() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/b/b-1.1.jar", 1005000L, false, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );
        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( "application/octet-stream", pResponse.getContentType() );
        assertEquals( 100, pResponse.getContentLength() );
        assertEquals( MockRepoConfiguration.MOCK_DATA_100, pResponse.getContent() );
    }

    /**
     * With non snapshot artifacts, the server simply tries to satisfy the download request -
     * it doesn't take into account the last modified values etc.
     *
     * Having the latest version of a static artifact doesn't really make much sense, but we support it anyway.
     */
    public void testArtifactGetAlreadyHaveLatest() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/b/b-1.1.jar", 3005000L, false, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( HttpServletResponse.SC_NOT_MODIFIED, pResponse.getStatusCode() );
        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( 100, pResponse.getContentLength() );
        assertNull( pResponse.getContent() );
    }

    /**
     * With non snapshot artifacts, the server simply tries to satisfy the download request -
     * it doesn't take into account the last modified values etc.
     *
     * It will respect a head only download request
     */
    public void testArtifactHeadOnly() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/b/b-1.1.jar", 2000L, true, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );
        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( "application/octet-stream", pResponse.getContentType() );
        assertEquals( 100, pResponse.getContentLength() );
        assertNull( pResponse.getContent() );
    }

    /**
     * With non snapshot artifacts, the server simply tries to satisfy the download request -
     * it doesn't take into account the last modified values etc.
     *
     * It will respect a head only download request
     */
    public void testNonArtifactHeadOnly() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/b/b-1.1.jar.md5", 2000L, true, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );
        assertEquals( 1006000L, pResponse.getLastModified() );
        assertEquals( "text/plain", pResponse.getContentType() );
        assertEquals( 33, pResponse.getContentLength() );
        assertNull( pResponse.getContent() );
    }

    public void testSnapshotCaching() throws IOException
    {
        RetrievalComponentConfiguration rcc = new RetrievalComponentConfiguration();
        GlobalRepoConfiguration globalRepo = new GlobalRepoConfiguration( "target/repo" );
        MockRepoConfiguration mockRepo = new MockRepoConfiguration( "MockA", "target/mock-a", "MockA", true, true,
                        true, 3600 );
        rcc.setSnapshotUpdate( true );
        rcc.setLastModifiedDateFormat( "yyyy/MM/dd HH:mm:ss" );
        rcc.addRepo( globalRepo );
        rcc.addRepo( mockRepo );
        DownloadEngine engine = new DownloadEngine( rcc );

        {
            MockProxyRequest pRequest = new MockProxyRequest( "/a/a-SNAPSHOT.jar", 1001000L, false, -1 );
            MockProxyResponse pResponse = new MockProxyResponse();
            engine.process( pRequest, pResponse );

            assertEquals( 1005000L, pResponse.getLastModified() );
            assertEquals( "application/octet-stream", pResponse.getContentType() );
            assertEquals( 100, pResponse.getContentLength() );
            assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );

            String s = pResponse.getContent();
            assertEquals( 100, s.length() );
            assertEquals( MockRepoConfiguration.MOCK_DATA_100, s );
        }

        //1 hit for the head request, 1 hit for the download
        assertEquals( 2, mockRepo.getHits() );

        {
            MockProxyRequest pRequest = new MockProxyRequest( "/a/a-SNAPSHOT.jar", 1005000L, false, -1 );
            MockProxyResponse pResponse = new MockProxyResponse();
            engine.process( pRequest, pResponse );

            assertEquals( 1005000L, pResponse.getLastModified() );
            assertEquals( "application/octet-stream", pResponse.getContentType() );
            assertEquals( 100, pResponse.getContentLength() );
            assertEquals( HttpServletResponse.SC_NOT_MODIFIED, pResponse.getStatusCode() );

            assertNull( pResponse.getContent() );
        }

        //No extras, the snapshot cache should have protected it.
        assertEquals( "The snapshot should have been cached, but the engine rechecked the repository", 2, mockRepo
                        .getHits() );
    }

    public void testSnapshotCaching2() throws IOException
    {
        MockRepoConfiguration mockRepo = exerciseCache( true );

        assertEquals( "The snapshot should have been cached, but the engine rechecked the repository", 2, mockRepo
                        .getHits() );
    }

    public void testSnapshotCaching2SnapshotUpdateDisabled() throws IOException
    {
        MockRepoConfiguration mockRepo = exerciseCache( false );

        assertEquals( "The engine should have just downloaded it", 2, mockRepo.getHits() );
    }

    /**
     * @return
     * @throws IOException
     */
    private MockRepoConfiguration exerciseCache( boolean snapshotUpdate ) throws IOException
    {
        RetrievalComponentConfiguration rcc = new RetrievalComponentConfiguration();
        GlobalRepoConfiguration globalRepo = new GlobalRepoConfiguration( "target/repo" );
        MockRepoConfiguration mockRepo = new MockRepoConfiguration( "MockA", "target/mock-a", "MockA", true, true,
                        false, 3600 );
        rcc.setSnapshotUpdate( snapshotUpdate );

        rcc.setLastModifiedDateFormat( "yyyy/MM/dd HH:mm:ss" );
        rcc.addRepo( globalRepo );
        rcc.addRepo( mockRepo );
        DownloadEngine engine = new DownloadEngine( rcc );

        {
            MockProxyRequest pRequest = new MockProxyRequest( "/a/a-SNAPSHOT.jar", -1L, false, -1 );
            MockProxyResponse pResponse = new MockProxyResponse();
            engine.process( pRequest, pResponse );

            assertEquals( 1005000L, pResponse.getLastModified() );
            assertEquals( "application/octet-stream", pResponse.getContentType() );
            assertEquals( 100, pResponse.getContentLength() );
            assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );

            String s = pResponse.getContent();
            assertEquals( 100, s.length() );
            assertEquals( MockRepoConfiguration.MOCK_DATA_100, s );
        }

        //1 hit for the head request, 1 hit for the download
        assertEquals( 2, mockRepo.getHits() );

        {
            MockProxyRequest pRequest = new MockProxyRequest( "/a/a-SNAPSHOT.jar", -1L, true, -1 );
            MockProxyResponse pResponse = new MockProxyResponse();
            engine.process( pRequest, pResponse );

            assertEquals( 1005000L, pResponse.getLastModified() );
            assertEquals( "application/octet-stream", pResponse.getContentType() );
            assertEquals( 100, pResponse.getContentLength() );
            assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );
            assertNull( pResponse.getContent() );
        }
        return mockRepo;
    }

    /**
     * Test non existent SNAPSHOT
     */
    public void testNonExistentSnapshot() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/gubba/gubba-SNAPSHOT.jar", 2000L, true, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( HttpServletResponse.SC_NOT_FOUND, pResponse.getStatusCode() );
    }

    /**
     * Test non existent SNAPSHOT
     */
    public void testNonExistentSnapshotWithFailuresCached() throws IOException
    {
        RetrievalComponentConfiguration rcc = new RetrievalComponentConfiguration();
        GlobalRepoConfiguration globalRepo = new GlobalRepoConfiguration( "target/repo" );
        MockRepoConfiguration mockRepo = new MockRepoConfiguration( "MockA", "target/mock-a", "MockA", true, true,
                        true, 3600 );
        rcc.addRepo( globalRepo );
        rcc.addRepo( mockRepo );

        rcc.setSnapshotUpdate( true );

        MockProxyRequest pRequest = new MockProxyRequest( "/gubba/gubba-SNAPSHOT.jar", 2000L, true, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( rcc );
        engine.process( pRequest, pResponse );
        assertEquals( HttpServletResponse.SC_NOT_FOUND, pResponse.getStatusCode() );
        assertEquals( 1, mockRepo.getHits() );

        pResponse = new MockProxyResponse();
        engine.process( pRequest, pResponse );
        assertEquals( 1, mockRepo.getHits() );
    }

    /**
     * Test non existent artifact
     */
    public void testNonExistentArtifact() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/gubba/gubba-1.0.jar", 2000L, true, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( HttpServletResponse.SC_NOT_FOUND, pResponse.getStatusCode() );
    }

    /**
     * TODO: Do we want to know if the upstream repos are flaking out?
     * Perhaps an option per repo - hardfail?
     */
    public void testUnreliableRepository() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/a/a-1.1.jar", 3000L, false, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_Flaky_A() );
        engine.process( pRequest, pResponse );

        assertEquals( 500, pResponse.getStatusCode() );
    }

    /**
     * The client has requested a SNAPSHOT download and has provided an older If-Modified-Since entry.
     * If there is an update, they want the whole lot.
     */
    public void testHaveOlderSnapshotUsingIfModifiedSince() throws IOException
    {
        LOGGER.info( "Client has older version of SNAPSHOT and will take the whole thing." );
        MockProxyRequest pRequest = new MockProxyRequest( "/a/a-SNAPSHOT.jar", -1L, false, 1000L );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( "application/octet-stream", pResponse.getContentType() );
        assertEquals( 100, pResponse.getContentLength() );
        assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );

        String s = pResponse.getContent();
        assertEquals( 100, s.length() );
        assertEquals( MockRepoConfiguration.MOCK_DATA_100, s );
    }

    /**
     * The client has requested a SNAPSHOT download and has provided a newer If-Modified-Since entry.
     * If there is an update, they want the whole lot.
     */
    public void testHaveNewerSnapshotUsingIfModifiedSince() throws IOException
    {
        LOGGER.info( "Client has older version of SNAPSHOT and will take the whole thing." );
        MockProxyRequest pRequest = new MockProxyRequest( "/a/a-SNAPSHOT.jar", -1L, false, 2005000L );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( "application/octet-stream", pResponse.getContentType() );
        assertEquals( 100, pResponse.getContentLength() );
        assertEquals( HttpServletResponse.SC_NOT_MODIFIED, pResponse.getStatusCode() );

        assertNull( pResponse.getContent() );
    }

    /**
     * Test behaviour when a repository is hit twice before the server has a chance to download from first attempt
     */
    public void testSlowRepositoryGettingHitTwice() throws IOException
    {
        RetrievalComponentConfiguration rcc = new RetrievalComponentConfiguration();
        GlobalRepoConfiguration globalRepo = new GlobalRepoConfiguration( "target/repo" );
        SlowRepoConfiguration mockRepo = new SlowRepoConfiguration( "MockA", "target/mock-a", "MockA", true, true,
                        true, 3600, 1000 );
        rcc.addRepo( globalRepo );
        rcc.addRepo( mockRepo );

        rcc.setSnapshotUpdate( true );

        MockProxyRequest pRequest = new MockProxyRequest( "/gubba/gubba-SNAPSHOT.jar", 2000L, true, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( rcc );
        engine.process( pRequest, pResponse );
        assertEquals( HttpServletResponse.SC_NOT_FOUND, pResponse.getStatusCode() );
        assertEquals( 1, mockRepo.getHits() );

        pResponse = new MockProxyResponse();
        engine.process( pRequest, pResponse );
        assertEquals( 1, mockRepo.getHits() );
    }

    /**
     * The client has requested a maven-metadata.xml download and has provided a Last-Modified entry.
     */
    public void testOlderMetaData() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/b/maven-metadata.xml", 1006000L, true, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( "application/octet-stream", pResponse.getContentType() );
        assertEquals( 100, pResponse.getContentLength() );
        assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );
        assertNull( pResponse.getContent() );
    }

    /**
     * The client has requested a maven-metadata.xml download and has provided a Last-Modified entry.
     */
    public void testNewerMetaData() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/b/maven-metadata.xml", 1002000L, true, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( "application/octet-stream", pResponse.getContentType() );
        assertEquals( 100, pResponse.getContentLength() );
        assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );
        assertNull( pResponse.getContent() );
    }

    /**
     * The client has requested a maven-metadata.xml download and has provided a Last-Modified entry.
     */
    public void testOlderPOM() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/b/b-1.1.pom", 1006000L, true, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( "application/octet-stream", pResponse.getContentType() );
        assertEquals( 100, pResponse.getContentLength() );
        assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );
        assertNull( pResponse.getContent() );
    }

    /**
     * The client has requested a maven-metadata.xml download and has provided a Last-Modified entry.
     */
    public void testNewerPOM() throws IOException
    {
        MockProxyRequest pRequest = new MockProxyRequest( "/b/b-1.1.pom", 1002000L, true, -1 );
        MockProxyResponse pResponse = new MockProxyResponse();
        DownloadEngine engine = new DownloadEngine( createRCC_A() );
        engine.process( pRequest, pResponse );

        assertEquals( 1005000L, pResponse.getLastModified() );
        assertEquals( "application/octet-stream", pResponse.getContentType() );
        assertEquals( 100, pResponse.getContentLength() );
        assertEquals( HttpServletResponse.SC_OK, pResponse.getStatusCode() );
        assertNull( pResponse.getContent() );
    }

}