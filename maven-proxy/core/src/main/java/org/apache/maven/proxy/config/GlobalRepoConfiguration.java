package org.apache.maven.proxy.config;

/**
 * @author Ben Walding
 */
public class GlobalRepoConfiguration extends FileRepoConfiguration
{
    public GlobalRepoConfiguration( String basePath )
    {
        super( "global", "file:///" + basePath, "Global Repository", false, true );
    }

}