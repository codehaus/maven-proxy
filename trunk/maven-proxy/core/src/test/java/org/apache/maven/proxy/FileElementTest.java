package org.apache.maven.proxy;

import java.io.File;

import junit.framework.TestCase;

import org.apache.maven.proxy.config.GlobalRepoConfiguration;
import org.apache.maven.proxy.config.RepoConfiguration;

/**
 * @author Ben Walding
 */
public class FileElementTest extends TestCase
{
    public void testSimple()
    {
        RepoConfiguration repo = new GlobalRepoConfiguration( "./target" );
        File f = new File( "target/clown.txt" );

        FileElement fe = new FileElement( f, "/", repo );
        assertEquals( "/", fe.getRelativePath() );
    }
}