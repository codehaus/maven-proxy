package org.apache.maven.proxy.standalone;

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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;

import org.apache.log4j.PropertyConfigurator;
import org.apache.maven.proxy.RepositoryServlet;
import org.apache.maven.proxy.ResourceServlet;
import org.apache.maven.proxy.config.PropertyLoader;
import org.apache.maven.proxy.config.RepoConfiguration;
import org.apache.maven.proxy.config.RetrievalComponentConfiguration;
import org.apache.maven.proxy.config.ValidationException;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.util.MultiException;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class Standalone
{
    public static final String CVS_NAME = "$Name$";

    public static String getTag()
    {
        String name = extractName( CVS_NAME );
        if ( name == null || name.trim().length() == 0 )
        {
            return "Unversioned";
        }
        else
        {
            return name;
        }
    }

    static String extractName( String input )
    {
        String tmp = input;
        tmp = tmp.substring( 7, tmp.length() - 2 );
        return tmp;
    }

    public static void main( String args[] )
    {

        PropertyConfigurator.configure( Standalone.class.getResource( "/log4j.properties" ) );

        try
        {
            Standalone launcher = new Standalone();
            launcher.doMain( args );
        }
        catch ( Exception e )
        {
            System.err.println( "Internal error:" );
            e.printStackTrace();
            System.exit( -1 );
        }
    }

    public void doMain( String args[] ) throws MultiException
    {
        System.err.println( "maven-proxy " + Standalone.getTag() );

        if ( args.length != 1 )
        {
            System.err.println( "Usage:" );
            System.err.println( "  java -jar maven-proxy-SNAPSHOT-uber.jar maven-proxy.properties" );
            return;
        }

        RetrievalComponentConfiguration rcc = null;
        try
        {
            rcc = loadAndValidateConfiguration( args[0] );
        }
        catch ( ValidationException e )
        {
            Throwable t = e;

            System.err.println( "Error while loading properties:" );

            while ( t != null )
            {
                System.err.println( "  " + t.getLocalizedMessage() );
                t = t.getCause();
            }

            return;
        }

        System.out.println( "Saving repository at " + rcc.getLocalStore() );
        for ( Iterator iter = rcc.getRepos().iterator(); iter.hasNext(); )
        {
            RepoConfiguration repo = (RepoConfiguration) iter.next();
            System.out.println( "Scanning repository: " + repo.getUrl() );
        }
        System.out.println( "Starting..." );

        HttpServer server = new HttpServer();
        SocketListener listener = new SocketListener();
        listener.setPort( rcc.getPort() );
        server.addListener( listener );

        HttpContext context = new HttpContext();

        context.setContextPath( "/" );

        ServletHandler sh = new ServletHandler();
        System.out.println( "Prefix: '" + rcc.getPrefix() + "'" );
        if ( rcc.getPrefix().length() == 0 )
        {
            sh.addServlet( "Repository", "/*", RepositoryServlet.class.getName() );
        }
        else
        {
            sh.addServlet( "Repository", "/" + rcc.getPrefix() + "/*", RepositoryServlet.class.getName() );
        }

        sh.addServlet( "images", "/images/*", ResourceServlet.class.getName() );
        sh.addServlet( "styles", "/styles/*", ResourceServlet.class.getName() );
        //sh.addServlet( "webdav", "/webdav/*", ResourceServlet.class.getName() );

        context.setAttribute( "config", rcc );
        context.addHandler( sh );
        server.addContext( context );

        server.start();
        System.out.println( "Started." );

        final String externalAddress;

        if ( rcc.getServerName() == null )
        {
            externalAddress = "http://" + getExternalIP() + ":" + rcc.getPort();
        }
        else
        {
            externalAddress = rcc.getServerName();
        }

        System.out.println( "Add the following to your ~/build.properties file:" );

        System.out.println( "   maven.repo.remote=" + externalAddress );
        if ( rcc.isBrowsable() )
        {
            System.out.println( "The repository can be browsed at " + externalAddress );
        }
        else
        {
            System.out.println( "Repository browsing is not enabled." );
        }
    }

    private String getExternalIP()
    {
        try
        {
            InetAddress ia = InetAddress.getLocalHost();
            return ia.getCanonicalHostName();
        }
        catch ( Exception e )
        {
            return "[external IP address]";
        }
    }

    /**
     * This method will load and validate the properties.
     * @todo make it throw a validation exception and defer
     *       logging to the handler of the exception.
     * @param filename The name of the properties file.
     * @return Returns a <code>Properties</code> object if the load and validation was successfull.
     * @throws ValidationException If there was any problem validating the properties
     */
    private RetrievalComponentConfiguration loadAndValidateConfiguration( String filename ) throws ValidationException
    {
        RetrievalComponentConfiguration rcc;
        File file = new File( filename );

        try
        {
            rcc = ( new PropertyLoader() ).load( new FileInputStream( file ) );
        }
        catch ( FileNotFoundException ex )
        {
            System.err.println( "No such file: " + file.getAbsolutePath() );
            return null;
        }
        catch ( IOException ex )
        {
            throw new ValidationException( ex );
        }

        {
            //Verify local repository set
            String tmp = checkSet( rcc.getLocalStore(), PropertyLoader.REPO_LOCAL_STORE );

            file = new File( tmp );
            if ( !file.exists() )
            {
                throw new ValidationException( "The local repository doesn't exist: " + file.getAbsolutePath() );
            }

            if ( !file.isDirectory() )
            {
                throw new ValidationException( "The local repository must be a directory: " + file.getAbsolutePath() );
            }
        }

        {
            //Verify remote repository set
            //only warn if missing
            if ( rcc.getRepos().size() < 1 )
            {
                throw new ValidationException( "At least one remote repository must be configured." );
            }
        }

        // all ok
        return rcc;
    }

    private String checkSet( String value, String propertyName ) throws ValidationException
    {
        if ( value == null )
        {
            throw new ValidationException( "Missing property '" + propertyName + "'" );
        }

        return value;
    }
}