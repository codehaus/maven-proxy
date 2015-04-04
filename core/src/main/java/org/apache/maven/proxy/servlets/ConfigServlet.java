package org.apache.maven.proxy.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.maven.proxy.utils.ABToggler;
import org.apache.velocity.Template;
import org.apache.velocity.context.Context;

/**
 * @author Ben Walding
 */
public class ConfigServlet extends MavenProxyServlet
{


    public Template handleRequestInternal( HttpServletRequest request, HttpServletResponse response, Context context )
                    throws Exception
    {
        context.put( "rcc", getRCC() );
        context.put( "retrace", ".." );
        context.put( "ab", new ABToggler() );
        return getTemplate( "ConfigServlet.vtl" );
    }

    public String getTopLevel()
    {
        return "CONFIG";
    }

}