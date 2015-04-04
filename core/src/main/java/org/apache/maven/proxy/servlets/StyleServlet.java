package org.apache.maven.proxy.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.context.Context;

/**
 * @author Ben Walding
 */
public class StyleServlet extends MavenProxyServlet
{

    public Template handleRequestInternal( HttpServletRequest request, HttpServletResponse response, Context context )
                    throws Exception
    {
        context.put( "bgColor", getRCC().getBgColor() );
        context.put( "bgColorHighlight", getRCC().getBgColorHighlight() );
        context.put( "rowColor", getRCC().getRowColor() );
        context.put( "rowColorHighlight", getRCC().getRowColorHighlight() );
        response.setContentType( "text/css" );
        return getTemplate( "StyleServlet.vtl" );
    }

    /** 
     * Not used.
     */
    public String getTopLevel()
    {
        return "MISC";
    }
}