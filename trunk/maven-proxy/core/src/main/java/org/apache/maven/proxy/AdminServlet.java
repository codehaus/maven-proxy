package org.apache.maven.proxy;

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
            RepositoryServlet.snapshotCache.stop();
            RepositoryServlet.snapshotCache.start();
        }
        return getTemplate( "AdminServlet.vtl" );
    }

}