package org.apache.maven.proxy;

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
import java.util.Date;

import org.apache.maven.fetch.FetchRequest;
import org.apache.maven.fetch.FetchTool;
import org.apache.maven.fetch.exceptions.FetchException;
import org.apache.maven.fetch.exceptions.ResourceNotFoundFetchException;
import org.apache.maven.proxy.config.FileRepoConfiguration;
import org.apache.maven.proxy.config.HttpRepoConfiguration;
import org.apache.maven.proxy.config.RepoConfiguration;

/**
 * This component should be stateless and hence multithreadable.
 * 
 * @author Ben Walding
 * @version $Id: DefaultRetrievalComponent.java,v 1.11 2004/02/25 14:25:55
 *          bwalding Exp $
 */
public class DefaultRetrievalComponent implements RetrievalComponent
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
                    .getLogger( DefaultRetrievalComponent.class );

    public RetrievalDetails retrieveArtifact( RepoConfiguration rc, File out, String url, boolean checkModified )
                    throws FetchException, FileNotFoundException
    {
        FetchRequest fr = new FetchRequest( rc.getUrl() + "/" + url );
        LOGGER.info( "Retrieving URL: " + fr.getUrl() );
        if ( rc instanceof HttpRepoConfiguration )
        {
            HttpRepoConfiguration hrc = (HttpRepoConfiguration) rc;
            if ( hrc.getProxy() != null )
            {
                fr.setProxyHost( hrc.getProxy().getHost() );
                fr.setProxyPort( hrc.getProxy().getPort() );
                if ( hrc.getProxy().getUsername() != null )
                {
                    fr.setProxyUser( hrc.getProxy().getUsername() );
                    fr.setProxyPass( hrc.getProxy().getPassword() );
                }
            }
        }

        if ( rc instanceof FileRepoConfiguration && !rc.getCopy() )
        {
            FileRepoConfiguration frc = (FileRepoConfiguration) rc;
            //XXX this is a mess, we should not be writing this kind of code
            // here!
            //If we're not copying, we have to handle this specially.

            File fPath = frc.getLocalFile( url );
            if ( fPath.exists() )
            {
                return new RetrievalDetails( fPath );

            }
            else
            {
                throw new ResourceNotFoundFetchException( fPath.getAbsolutePath() );
            }
        }

        FetchTool bean = new FetchTool();
        fr.setOutputFile( out );

        if ( checkModified )
        {
            fr.setOnlyIfModifiedSinceDate( new Date( out.lastModified() ) );
        }

        //FetchResponse dresp =
        bean.performDownload( fr );
        //Don't really care about the response (No exception thrown ==
        // downloaded ok!)
        return new RetrievalDetails( out );
    }

}