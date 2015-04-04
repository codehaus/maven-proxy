package org.apache.maven.proxy.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Ben Walding
 */
public class RetrievalDetails
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger( RetrievalDetails.class );

    private File file;
    private long lastModified;
    private long length;

    public RetrievalDetails( File file, long length, long lastModified )
    {
        this.file = file;
        this.length = length;
        this.lastModified = lastModified;
    }

    public RetrievalDetails( File path )
    {
        this( path, path.length(), path.lastModified() );
    }

    public InputStream getInputStream() throws FileNotFoundException
    {
        LOGGER.info( "Some bastard is creating an inputstream" );
        return new FileInputStream( file );
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