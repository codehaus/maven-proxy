package org.apache.maven.proxy.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Ben Walding
 */
public class MimeTypes
{
    private static Map mimeTypesByExtension = new HashMap();

    public static class MimeType
    {
        private final String mimeType;

        public MimeType( String mimeType )
        {
            this.mimeType = mimeType;
        }

        public String getMimeType()
        {
            return mimeType;
        }
    }

    static
    {
        mimeTypesByExtension.put( ".jar", new MimeType( "application/octet-stream" ) );
        mimeTypesByExtension.put( ".md5", new MimeType( "text/plain" ) );
    }

    public static MimeType getMimeType( String extension )
    {
        return (MimeType) mimeTypesByExtension.get( extension );
    }
}