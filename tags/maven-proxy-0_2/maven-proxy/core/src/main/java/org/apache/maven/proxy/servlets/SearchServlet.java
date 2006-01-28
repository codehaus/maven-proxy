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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.proxy.config.FileRepoConfiguration;
import org.apache.maven.proxy.config.RepoConfiguration;
import org.apache.maven.proxy.utils.ABToggler;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;

/**
 * @author Ben Walding
 */
public class SearchServlet extends MavenProxyServlet
{
    public Template handleRequestInternal( HttpServletRequest request, HttpServletResponse response, Context context )
                    throws Exception
    {
        SearchControls searchControls = new SearchControls( request );

        context.put( "retrace", ".." );
        context.put( "searchControls", searchControls );

        if ( searchControls.getSearch() != null )
        {
            List results = new ArrayList();

            for ( Iterator iter = getRCC().getRepos().iterator(); iter.hasNext(); )
            {
                RepoConfiguration repo = (RepoConfiguration) iter.next();
                if ( repo instanceof FileRepoConfiguration )
                {
                    FileRepoConfiguration fileRepo = (FileRepoConfiguration) repo;
                    List allResults = scan( new File( fileRepo.getBasePath() ), "", fileRepo );
                    for ( Iterator resultIter = allResults.iterator(); resultIter.hasNext(); )
                    {
                        FileElement file = (FileElement) resultIter.next();
                        if ( file.getName().indexOf( searchControls.getSearch() ) >= 0 )
                        {
                            results.add( file );
                        }
                    }
                }
            }
            context.put( "searchResults", results );
        }

        context.put( "ab", new ABToggler() );
        context.put( "dateFormat", getRCC().getLastModifiedDateFormatForThread() );
        context.put( "rcc", getRCC() );
        return getTemplate( "SearchServlet.vtl" );
    }

    /**
     * 
     * @param base
     * @return a List of FileElements - no directories
     */
    protected List scan( File base, String relativePath, RepoConfiguration repo )
    {
        File[] childFiles = base.listFiles();
        List results = new ArrayList();
        for ( int i = 0; i < childFiles.length; i++ )
        {
            File childFile = childFiles[i];
            if ( childFile.isFile() )
            {
                results.add( new FileElement( childFile, relativePath, repo ) );
            }

            if ( childFile.isDirectory() )
            {
                results.addAll( scan( childFile, relativePath + "/" + childFile.getName(), repo ) );
            }
        }
        return results;
    }

    public String getTopLevel()
    {
        return "SEARCH";
    }
}