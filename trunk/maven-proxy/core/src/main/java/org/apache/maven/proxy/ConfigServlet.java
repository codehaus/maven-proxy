package org.apache.maven.proxy;

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
public class ConfigServlet extends MavenProxyServlet
{
    private RetrievalComponentConfiguration rcc;

    public void init() throws ServletException
    {
        rcc = (RetrievalComponentConfiguration) getServletContext().getAttribute( "config" );
    }

    public Template handleRequestInternal( HttpServletRequest request, HttpServletResponse response, Context context )
                    throws Exception
    {
        context.put( "rcc", rcc );
        context.put( "retrace", ".." );
        context.put( "ab", new ABToggler() );
        return getTemplate( "RootServlet.vtl" );
    }

    public String getTopLevel()
    {
        return "CONFIG";
    }

}