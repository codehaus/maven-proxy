package org.apache.maven.proxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.maven.fetch.exceptions.FetchException;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public interface RetrievalComponent
{
    public static final String ROLE = RetrievalComponent.ROLE;
    public InputStream retrieveArtifact(RepoConfiguration rc, File out, String url) throws FetchException, FileNotFoundException;
}