package org.apache.maven.proxy;

import java.io.File;

import org.apache.maven.proxy.config.RepoConfiguration;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class FileElement
{
    private final File file;
    private final RepoConfiguration repo;

    public FileElement(File file, RepoConfiguration repo) {
        this.file = file;
        this.repo = repo;
    }
    
    public boolean isDirectory() {
        return getFile().isDirectory();
    }
    
    /**
     * @return Returns the file.
     */
    public File getFile()
    {
        return file;
    }
    
    public String getName() {
        return getFile().getName();
    }
    /**
     * @return Returns the repo.
     */
    public RepoConfiguration getRepo()
    {
        return repo;
    }
}
