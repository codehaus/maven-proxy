package org.apache.maven.proxy.servlets;

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

import junit.framework.TestCase;

import org.apache.maven.proxy.config.GlobalRepoConfiguration;
import org.apache.maven.proxy.config.RepoConfiguration;
import org.apache.maven.proxy.servlets.FileElement;

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