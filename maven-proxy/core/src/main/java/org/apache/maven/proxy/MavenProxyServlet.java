package org.apache.maven.proxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.servlet.VelocityServlet;

/**
 * @author Ben Walding
 */
public abstract class MavenProxyServlet extends VelocityServlet
{
    public abstract String getTopLevel();

    public final Template handleRequest( HttpServletRequest request, HttpServletResponse response, Context context )
                    throws Exception
    {
        context.put( "topLevel", getTopLevel() );
        context.put( "version", getServletContext().getAttribute( "version" ) );
        return handleRequestInternal( request, response, context );
    }

    public abstract Template handleRequestInternal( HttpServletRequest request, HttpServletResponse response, Context context )
                    throws Exception;

}