package org.apache.maven.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Ben Walding
 */
public class RetrievalDetails
{
    private InputStream is;
    private long lastModified;
    private long length;

    public RetrievalDetails( InputStream is )
    {
        this( is, -1, -1 );
    }

    public RetrievalDetails( InputStream is, long length, long lastModified )
    {
        this.is = is;
        this.length = length;
        this.lastModified = lastModified;
    }

    public RetrievalDetails( File path ) throws FileNotFoundException
    {

        this( new FileInputStream( path ), path.length(), path.lastModified() );
    }

    public InputStream getInputStream()
    {
        return is;
    }

    public long getLength()
    {
        return length;
    }

    public long getLastModified()
    {
        return lastModified;
    }

}