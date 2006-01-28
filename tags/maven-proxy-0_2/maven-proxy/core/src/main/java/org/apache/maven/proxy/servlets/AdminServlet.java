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

        if ( request.getParameter( "shutdown" ) != null )
        {
            //FIXME need to stop accepting connections, finish ones that are going, then shutdown Jetty.
            System.exit( 0 );
        }

        return getTemplate( "AdminServlet.vtl" );
    }

}