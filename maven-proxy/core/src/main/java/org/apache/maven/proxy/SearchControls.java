package org.apache.maven.proxy;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ben Walding
 */
public class SearchControls
{
    private String search;

    public SearchControls( HttpServletRequest request )
    {
        this.search = request.getParameter( "search" );
    }

    public String getSearch()
    {
        return search;
    }
}