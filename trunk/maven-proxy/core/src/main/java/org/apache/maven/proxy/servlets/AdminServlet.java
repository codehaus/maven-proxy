package org.apache.maven.proxy.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;

/**
 * @author Ben Walding
 */
public class AdminServlet extends MavenProxyServlet
{

    public String getTopLevel()
    {
        return "ADMIN";
    }

    public Template handleRequestInternal( HttpServletRequest request, HttpServletResponse response, Context context )
                    throws Exception
    {
        if ( request.getParameter( "clearSnapshotCache" ) != null )
        {
            RepositoryServlet.clearSnapshotCache();            
        }
        return getTemplate( "AdminServlet.vtl" );
    }

}