package org.apache.maven.proxy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;
import org.apache.velocity.servlet.VelocityServlet;

/**
 * @author Ben Walding
 */
public class StyleServlet extends VelocityServlet
{

    protected Template handleRequest( HttpServletRequest arg0, HttpServletResponse response, Context arg2 )
                    throws Exception
    {
        response.setContentType("text/css");
        return getTemplate("StyleServlet.vtl");
    }
}
