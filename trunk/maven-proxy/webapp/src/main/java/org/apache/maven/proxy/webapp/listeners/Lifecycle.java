/*
 * Copyright (c) 2004-2005, by OpenXource, LLC. All rights reserved.
 * 
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF OPENXOURCE
 *  
 * The copyright notice above does not evidence any          
 * actual or intended publication of such source code. 
 */
package org.apache.maven.proxy.webapp.listeners;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.maven.proxy.config.PropertyLoader;
import org.apache.maven.proxy.config.RetrievalComponentConfiguration;
import org.apache.maven.proxy.utils.IOUtility;
import org.apache.velocity.app.Velocity;

public class Lifecycle implements ServletContextListener
{

    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger( Lifecycle.class );

    public void contextInitialized( ServletContextEvent event )
    {
        initMavenProxyConfiguration( event );
        initVelocityConfiguration( event );
    }

    private void initVelocityConfiguration( ServletContextEvent event )
    {
        InputStream is = getClass().getResourceAsStream( "/velocity.properties" );
        try
        {
            Properties props = new Properties();
            props.load( is );
            Velocity.init( props );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
        finally
        {
            IOUtility.close( is );
        }
    }

    /**
     * @param event
     */
    private void initMavenProxyConfiguration( ServletContextEvent event )
    {
        LOGGER.info( "Starting..." );
        String filename = event.getServletContext().getInitParameter( "maven-proxy.properties" );
        if ( filename == null )
        {
            LOGGER.warn( "maven-proxy.properties not set" );
            throw new RuntimeException( "failed" );
        }
        LOGGER.info( "Loading " + filename + "..." );
        File file = new File( filename );
        RetrievalComponentConfiguration rcc;
        try
        {
            rcc = (new PropertyLoader()).load( new FileInputStream( file ) );
            event.getServletContext().setAttribute( "config", rcc );
        }
        catch ( FileNotFoundException ex )
        {
            System.err.println( "No such file: " + file.getAbsolutePath() );
        }
        catch ( Exception ex )
        {
            throw new RuntimeException( ex );
        }
    }

    public void contextDestroyed( ServletContextEvent arg0 )
    {
        // TODO Auto-generated method stub

    }

}
