package org.apache.maven.proxy.config;

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
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author Ben Walding
 * @version $Id$
 */
public class PropertyLoader
{
    public static final String REPO_LOCAL_STORE = "repo.local.store";

    public static final String REPO_CUSTOM_STORE = "repo.custom.store";

    public static final String PORT = "port";
    public static final String SNAPSHOT_UPDATE = "snapshot.update";
    public static final String SNAPSHOT_UPDATE_INTERVAL = "snapshot.update.interval";

    public static final int DEFAULT_PORT = 4321;
    public static final String LAST_MODIFIED_DATE_FORMAT = "lastModifiedDateFormat";
    public static final String DEFAULT_LAST_MODIFIED_DATE_FORMAT = null;

    public static final String BROWSABLE = "browsable";
    public static final String SEARCHABLE = "searchable";

    public static final String PREFIX = "prefix";

    private static final String SERVERNAME = "serverName";

    public RetrievalComponentConfiguration load( Properties props ) throws ValidationException
    {
        RetrievalComponentConfiguration rcc = new RetrievalComponentConfiguration();

        String localStore = getMandatoryProperty( props, REPO_LOCAL_STORE );
        GlobalRepoConfiguration globalRepo = new GlobalRepoConfiguration( localStore );
        rcc.setLocalStore( localStore );
        rcc.addRepo( globalRepo );

        if ( props.getProperty( PORT ) == null )
        {
            rcc.setPort( DEFAULT_PORT );
        }
        else
        {
            try
            {
                rcc.setPort( Integer.parseInt( props.getProperty( PORT ) ) );
            }
            catch ( NumberFormatException ex )
            {
                throw new ValidationException( "Property " + PORT + " must be a integer" );
            }
        }

        rcc.setSnapshotUpdate( Boolean.valueOf( getMandatoryProperty( props, SNAPSHOT_UPDATE ) ).booleanValue() );
        if ( props.getProperty( SNAPSHOT_UPDATE_INTERVAL ) == null )
        {
            rcc.setSnapshotUpdateInterval( 0 );
        }
        else
        {
            try
            {
                rcc.setSnapshotUpdateInterval( Integer.parseInt( props.getProperty( SNAPSHOT_UPDATE_INTERVAL ) ) );
            }
            catch ( NumberFormatException ex )
            {
                throw new ValidationException( "Property " + SNAPSHOT_UPDATE_INTERVAL + " must be a integer" );
            }
        }

        rcc.setBrowsable( Boolean.valueOf( getMandatoryProperty( props, BROWSABLE ) ).booleanValue() );
        rcc.setSearchable( Boolean.valueOf( getOptionalProperty( props, SEARCHABLE, "true" ) ).booleanValue() );
        rcc.setServerName( props.getProperty( SERVERNAME ) );
        rcc.setPrefix( getMandatoryProperty( props, PREFIX ) );
        rcc.setLastModifiedDateFormat( props.getProperty( LAST_MODIFIED_DATE_FORMAT ) );

        if ( rcc.getPrefix().length() == 0 )
        {
            System.err.println( "Using an empty 'prefix' is deprecated behaviour.  Please set a prefix." );
        }

        {
            String propertyList = props.getProperty( "proxy.list" );
            if ( propertyList != null )
            {
                StringTokenizer tok = new StringTokenizer( propertyList, "," );
                while ( tok.hasMoreTokens() )
                {
                    String key = tok.nextToken();
                    String host = getMandatoryProperty( props, "proxy." + key + ".host" );
                    int port = Integer.parseInt( getMandatoryProperty( props, "proxy." + key + ".port" ) );
                    // the username and password isn't required
                    String username = props.getProperty( "proxy." + key + ".username" );
                    String password = props.getProperty( "proxy." + key + ".password" );
                    ProxyConfiguration pc = new ProxyConfiguration( key, host, port, username, password );
                    rcc.addProxy( pc );
                }
            }
        }

        {
            String repoList = getMandatoryProperty( props, "repo.list" );

            StringTokenizer tok = new StringTokenizer( repoList, "," );
            while ( tok.hasMoreTokens() )
            {
                String key = tok.nextToken();

                Properties repoProps = getSubset( props, "repo." + key + "." );
                String url = getMandatoryProperty( props, "repo." + key + ".url" );
                // the username, password and proxy are not mandatory
                String username = repoProps.getProperty( "username" );
                String password = repoProps.getProperty( "password" );
                String description = repoProps.getProperty( "description" );
                String proxyKey = repoProps.getProperty( "proxy" );

                ProxyConfiguration proxy = null;
                if ( proxyKey != null )
                {
                    proxy = rcc.getProxy( proxyKey );
                }

                if ( description == null || description.trim().length() == 0 )
                {
                    description = key;
                }

                RepoConfiguration rc = null;

                if ( url.startsWith( "http://" ) )
                {
                    rc = new HttpRepoConfiguration( key, url, description, username, password, proxy );
                }

                if ( url.startsWith( "file:///" ) )
                {
                    boolean copy = "true".equalsIgnoreCase( repoProps.getProperty( "copy" ) );
                    rc = new FileRepoConfiguration( key, url, description, copy );
                }

                if ( rc == null )
                {
                    throw new ValidationException( "Unknown upstream repository type: " + url );
                }

                rcc.addRepo( rc );
            }
        }
        validateDirectories( rcc );
        return rcc;

    }

    private Properties getSubset( Properties props, String prefix )
    {
        Enumeration keys = props.keys();
        Properties result = new Properties();
        while ( keys.hasMoreElements() )
        {
            String key = (String) keys.nextElement();
            String value = props.getProperty( key );
            if ( key.startsWith( prefix ) )
            {
                String newKey = key.substring( prefix.length() );
                result.setProperty( newKey, value );
            }
        }
        return result;
    }

    public RetrievalComponentConfiguration load( InputStream is ) throws IOException, ValidationException
    {
        Properties props = new Properties();
        props.load( is );
        return load( props );
    }

    private String getMandatoryProperty( Properties props, String key ) throws ValidationException
    {
        final String value = props.getProperty( key );

        if ( value == null )
            throw new ValidationException( "Missing property: " + key );

        return value;
    }

    private String getOptionalProperty( Properties props, String key, String defaultValue )
    {
        final String value = props.getProperty( key );

        if ( value == null )
        {
            return defaultValue;
        }

        return value;
    }

    private void validateDirectories( RetrievalComponentConfiguration rcc ) throws ValidationException
    {
        File f = new File( rcc.getLocalStore() );
        if ( !f.exists() )
        {
            throw new ValidationException( "Specified directory does not exist: " + f.getAbsolutePath() );
        }

        List repos = rcc.getRepos();
        for ( Iterator iter = repos.iterator(); iter.hasNext(); )
        {
            RepoConfiguration repo = (RepoConfiguration) iter.next();
            if ( repo instanceof FileRepoConfiguration )
            {
                FileRepoConfiguration fileRepo = (FileRepoConfiguration) repo;
                File f2 = new File( fileRepo.getBasePath() );
                if ( !f2.exists() )
                {
                    throw new ValidationException( "Specified directory does not exist: " + f2.getAbsolutePath() );
                }
            }

        }
    }

}