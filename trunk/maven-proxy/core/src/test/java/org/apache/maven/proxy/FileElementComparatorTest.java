package org.apache.maven.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.maven.proxy.config.FileRepoConfiguration;
import org.apache.maven.proxy.config.HttpRepoConfiguration;
import org.apache.maven.proxy.config.RepoConfiguration;

/**
 * @author <a href="bwalding@apache.org">Ben Walding</a>
 * @version $Id$
 */
public class FileElementComparatorTest extends TestCase
{
    private List repos;
    private RepoConfiguration repoA;
    private RepoConfiguration repoB;
    private FileElementComparator fec;

    protected void setUp()
    {
        repos = new ArrayList();
        repoA = new HttpRepoConfiguration( "A", "http://example.com", "Repo A", null, null, null );
        repoB = new FileRepoConfiguration( "B", "file:///target", "Repo B", false );
        repos.add( repoA );
        repos.add( repoB );
        fec = new FileElementComparator( repos );
    }

    public void testDirDir()
    {
        File d1 = new MockFile( "a", true );
        FileElement fe1 = new FileElement( d1, "", repoB );
        File d2 = new MockFile( "b", true );
        FileElement fe2 = new FileElement( d2, "", repoB );

        assertEquals( "d-a vs d-b", -1, fec.compare( fe1, fe2 ) );
        assertEquals( "d-a vs d-a", 0, fec.compare( fe1, fe1 ) );
        assertEquals( "d-b vs d-a", 1, fec.compare( fe2, fe1 ) );
        assertEquals( "d-b vs d-b", 0, fec.compare( fe2, fe2 ) );
    }

    public void testDirDirDifferentRepo()
    {
        File d1 = new MockFile( "a", true );
        FileElement fe1 = new FileElement( d1, "", repoA );
        File d2 = new MockFile( "a", true );
        FileElement fe2 = new FileElement( d2, "", repoB );

        assertEquals( "d-a vs d-b", 0, fec.compare( fe1, fe2 ) );
        assertEquals( "d-a vs d-a", 0, fec.compare( fe1, fe1 ) );
        assertEquals( "d-b vs d-a", 0, fec.compare( fe2, fe1 ) );
        assertEquals( "d-b vs d-b", 0, fec.compare( fe2, fe2 ) );
    }

    public void testFileFileDifferentRepo()
    {
        File d1 = new MockFile( "a", true );
        FileElement fe1 = new FileElement( d1, "", repoA );
        File d2 = new MockFile( "a", true );
        FileElement fe2 = new FileElement( d2, "", repoB );

        assertEquals( "d-a vs d-b", 0, fec.compare( fe1, fe2 ) );
        assertEquals( "d-a vs d-a", 0, fec.compare( fe1, fe1 ) );
        assertEquals( "d-b vs d-a", 0, fec.compare( fe2, fe1 ) );
        assertEquals( "d-b vs d-b", 0, fec.compare( fe2, fe2 ) );
    }

    public void testDirFile()
    {
        File d1 = new MockFile( "a", true );
        FileElement fe1 = new FileElement( d1, "", repoB );
        File d2 = new MockFile( "b", false );
        FileElement fe2 = new FileElement( d2, "", repoB );

        assertEquals( "d-a vs d-b", -1, fec.compare( fe1, fe2 ) );
        assertEquals( "d-a vs d-a", 0, fec.compare( fe1, fe1 ) );
        assertEquals( "d-b vs d-a", 1, fec.compare( fe2, fe1 ) );
        assertEquals( "d-b vs d-b", 0, fec.compare( fe2, fe2 ) );
    }

    public void testFileDir()
    {
        File f1 = new MockFile( "a", false );
        FileElement fe1 = new FileElement( f1, "", repoB );
        File d2 = new MockFile( "b", true );
        FileElement fe2 = new FileElement( d2, "", repoB );

        assertEquals( "d-a vs d-b", 1, fec.compare( fe1, fe2 ) );
        assertEquals( "d-a vs d-a", 0, fec.compare( fe1, fe1 ) );
        assertEquals( "d-b vs d-a", -1, fec.compare( fe2, fe1 ) );
        assertEquals( "d-b vs d-b", 0, fec.compare( fe2, fe2 ) );
    }

    public void testFileFile()
    {
        File f1 = new MockFile( "a", false );
        FileElement fe1 = new FileElement( f1, "", repoB );
        File d2 = new MockFile( "b", true );
        FileElement fe2 = new FileElement( d2, "", repoB );

        assertEquals( "d-a vs d-b", 1, fec.compare( fe1, fe2 ) );
        assertEquals( "d-a vs d-a", 0, fec.compare( fe1, fe1 ) );
        assertEquals( "d-b vs d-a", -1, fec.compare( fe2, fe1 ) );
        assertEquals( "d-b vs d-b", 0, fec.compare( fe2, fe2 ) );
    }
}