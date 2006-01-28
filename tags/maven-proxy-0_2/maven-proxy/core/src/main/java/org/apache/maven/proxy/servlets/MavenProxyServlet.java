package org.apache.maven.proxy.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.proxy.config.RetrievalComponentConfiguration;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.servlet.VelocityServlet;

/**
 * @author Ben Walding
 */
public abstract class MavenProxyServlet extends VelocityServlet
{
    public abstract String getTopLevel();

    private RetrievalComponentConfiguration rcc;

    public void init() throws ServletException
    {
        super.init();
        rcc = (RetrievalComponentConfiguration) getServletContext().getAttribute( "config" );
    }

    protected RetrievalComponentConfiguration getRCC()
    {
        return rcc;
    }

    public final Template handleRequest( HttpServletRequest request, HttpServletResponse response, Context context )
                    throws Exception
    {
        context.put( "rcc", getRCC() );
        context.put( "topLevel", getTopLevel() );
        context.put( "stylesheet", getRCC().getStylesheet() );
        context.put( "version", getServletContext().getAttribute( "version" ) );
        return handleRequestInternal( request, response, context );
    }

    public abstract Template handleRequestInternal( HttpServletRequest request, HttpServletResponse response, Context context )
                    throws Exception;

}