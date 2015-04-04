package org.apache.maven.proxy.servlets;

import java.io.File;
import java.util.Date;

import org.apache.maven.proxy.config.RepoConfiguration;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class FileElement
{
    private final File file;
    private final String relativePath;
    private final RepoConfiguration repo;

    public FileElement( File file, String relativePath, RepoConfiguration repo )
    {
        this.file = file;
        this.relativePath = relativePath;
        //Could get away with passing the repo in - just need repo ordering key and description
        this.repo = repo;
    }

    public boolean isDirectory()
    {
        return getFile().isDirectory();
    }

    public long length()
    {
        if ( isDirectory() )
        {
            return -1;
        }

        return getFile().length();
    }

    /**
     * @return Returns the file.
     */
    public File getFile()
    {
        return file;
    }

    public String getRelativePath()
    {
        return relativePath;
    }

    public String getName()
    {
        return getFile().getName();
    }

    /**
     * @return Returns the repo.
     */
    public RepoConfiguration getRepo()
    {
        return repo;
    }

    public Date lastModifiedDate()
    {
        if ( isDirectory() )
        {
            return null;
        }

        return new Date( getFile().lastModified() );
    }

}