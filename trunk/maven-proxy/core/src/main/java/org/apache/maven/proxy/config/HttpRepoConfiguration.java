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
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateParser;
import org.apache.maven.proxy.DownloadEngine;
import org.apache.maven.proxy.RetrievalDetails;
import org.apache.maven.proxy.components.ProxyArtifact;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class HttpRepoConfiguration extends RepoConfiguration
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
                    .getLogger( HttpRepoConfiguration.class );

    private final String username;
    private final String password;
    private final ProxyConfiguration proxy;

    public HttpRepoConfiguration( String key, String url, String description, String username, String password,
                    boolean hardFail, ProxyConfiguration proxy )
    {
        super( key, url, description, true, hardFail );
        this.username = username;
        this.password = password;
        this.proxy = proxy;
    }

    /**
     * @return
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @return
     */
    public String getUsername()
    {
        return username;
    }

    /**
     * @return
     */
    public ProxyConfiguration getProxy()
    {
        return proxy;
    }

    private HttpClient createHttpClient()
    {
        HttpClient client = new HttpClient();
        HostConfiguration hostConf = new HostConfiguration();
        ProxyConfiguration proxy = getProxy();

        if ( proxy != null )
        {
            hostConf.setProxy( proxy.getHost(), proxy.getPort() );
            client.setHostConfiguration( hostConf );
            if ( proxy.getUsername() != null )
            {
                Credentials creds = new UsernamePasswordCredentials( proxy.getUsername(), proxy.getPassword() );
                client.getState().setProxyCredentials( null, null, creds );
            }
        }
        return client;
    }

    public ProxyArtifact getSnapshot( String url )
    {
        try
        {
            HttpClient client = createHttpClient();
            String fullUrl = getUrl() + url;
            LOGGER.info( this + ": Checking last modified time for " + fullUrl );
            HeadMethod method = new HeadMethod( fullUrl );
            client.executeMethod( method );

            if ( method.getStatusCode() == HttpServletResponse.SC_NOT_FOUND )
            {
                return null;
            }

            if ( method.getStatusCode() != HttpServletResponse.SC_OK )
            {
                LOGGER.info( this + ": Unable to find " + fullUrl + " because of [" + method.getStatusCode() + "] = "
                                + method.getStatusText() );
                return null;
            }

            long lastModified = getLastModified( method );
            long size = getContentLength( method );
            ProxyArtifact snapshot = new ProxyArtifact( this, url );
            snapshot.setLastModified( lastModified );
            snapshot.setSize( size );
            return snapshot;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    /**
     * @param method
     * @return
     */
    private long getContentLength( HeadMethod method )
    {
        Header contentLengthHeader = method.getResponseHeader( "Content-Length" );
        if ( contentLengthHeader == null )
        {
            return -1L;
        }
        String lastModifiedString = contentLengthHeader.getValue();
        return Long.parseLong( lastModifiedString );
    }

    public RetrievalDetails retrieveArtifact( File out, String url ) throws IOException
    {
        LOGGER.info( "Retrieving " + url );
        HttpClient client = createHttpClient();

        String fullUrl = getUrl() + url;
        GetMethod method = new GetMethod( fullUrl );

        client.executeMethod( method );

        if ( method.getStatusCode() == HttpServletResponse.SC_NOT_FOUND )
        {
            throw new FileNotFoundException( "Can't find " + fullUrl );
        }

        if ( method.getStatusCode() != HttpServletResponse.SC_OK )
        {
            LOGGER.info( this + ": Unable to find " + fullUrl );
            throw new FileNotFoundException( "Error " + fullUrl );
        }

        DownloadEngine.download( out, method.getResponseBodyAsStream(), getLastModified( method ) );

        return new RetrievalDetails( out );

    }

    private static long getLastModified( HttpMethod method )
    {
        Header lastModifiedHeader = method.getResponseHeader( "Last-Modified" );
        if ( lastModifiedHeader == null )
        {
            return -1;
        }
        String lastModifiedString = lastModifiedHeader.getValue();

        try
        {
            return DateParser.parseDate( lastModifiedString ).getTime();
        }
        catch ( DateParseException e )
        {
            LOGGER.warn( "Unable to parse Last-Modified header : " + lastModifiedString );
            return System.currentTimeMillis();
        }
    }
}