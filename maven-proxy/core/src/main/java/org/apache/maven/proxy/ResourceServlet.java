package org.apache.maven.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.fetch.util.IOUtility;

/**
 * @author Ben Walding
 */
public class ResourceServlet extends HttpServlet
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger( ResourceServlet.class );

    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
                    IOException
    {
        final String pathInfo = request.getPathInfo();
        if ( pathInfo.equalsIgnoreCase( "/favicon.ico" ) )
        {
            handleImageRequest( "favicon.ico", "image/x-ico", response );
            return;
        }

        if ( pathInfo.equalsIgnoreCase( "/jar.png" ) )
        {
            handleImageRequest( "jar.png", "image/png", response );
            return;
        }

        if ( pathInfo.equalsIgnoreCase( "/folder.png" ) )
        {
            handleImageRequest( "folder.png", "image/png", response );
            return;
        }

        if ( pathInfo.equalsIgnoreCase( "/parent.png" ) )
        {
            handleImageRequest( "parent.png", "image/png", response );
            return;
        }

        response.sendError( HttpServletResponse.SC_NOT_FOUND, pathInfo + " not known." );
    }

    /**
     * @param string
     * @param response
     */
    private void handleImageRequest( String image, String type, HttpServletResponse response ) throws IOException
    {
        response.setContentType( type );
        //7 day expiry for images
        response.setDateHeader( "Expires", System.currentTimeMillis() + 7 * 60 * 60 * 24 * 1000 );
        OutputStream os = response.getOutputStream();
        InputStream is = getClass().getResourceAsStream( image );
        IOUtility.transferStream( is, os );
        IOUtility.close( is );
    }
}