package org.apache.maven.proxy.components;

/**
 * @author Ben Walding
 */
public class Promotion
{
    private final String path;

    public Promotion( String path )
    {
        this.path = path;
    }

    public String getPath()
    {
        return path;
    }
}
