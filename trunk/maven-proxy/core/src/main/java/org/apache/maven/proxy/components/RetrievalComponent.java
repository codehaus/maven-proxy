package org.apache.maven.proxy.components;

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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.maven.proxy.RetrievalDetails;
import org.apache.maven.proxy.config.RepoConfiguration;

/**
 * @author Ben Walding
 * @version $Id$
 */
public interface RetrievalComponent
{
    String ROLE = RetrievalComponent.ROLE;

    /**
     * 
     * @param rc
     * @param out
     * @param url
     * @param checkModified
     * @param download if false, the file will NOT be downloaded
     * @return
     * @throws FetchException
     * @throws FileNotFoundException
     */
    RetrievalDetails retrieveArtifact( RepoConfiguration rc, File out, String url ) throws IOException;

    long getLastModified( RepoConfiguration rc, String url );

}