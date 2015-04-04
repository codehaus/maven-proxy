package org.apache.maven.proxy.utils;

/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.StringTokenizer;

/**
 * @author Ben Walding
 */
public class URLTool
{
    final private static String METADATA_FILE = "maven-metadata.xml";

    /**
     * Retraces up a path to generate a relative link. Without this, we have to do all kinds of hacks to
     * find out where the base of the app server is.
     * @param pathInfo
     * @return
     */
    public static String getRetrace( String pathInfo )
    {
        int length = new StringTokenizer( pathInfo, "/" ).countTokens();
        String result = "";

        if ( pathInfo.endsWith( "/" ) )
        {
            length++;
        }

        if ( length == 1 )
        {
            return ".";
        }

        for ( int i = 0; i < length - 1; i++ )
        {
            if ( result.length() > 0 )
            {
                result += "/";
            }
            result += "..";
        }
        return result;
    }

    public static boolean isSnapshot( String pathInfo )
    {
        return pathInfo.indexOf( "-SNAPSHOT." ) > 0;
    }

    public static boolean isMetaData( String pathInfo )
    {
        int idx = pathInfo.indexOf( METADATA_FILE );
        return idx >= 0
            && (     pathInfo.length() - METADATA_FILE.length() == idx
                    || pathInfo.charAt( idx + METADATA_FILE.length() ) == '.'  );
    }

    public static boolean isPOM( String pathInfo ) {
        int idx = pathInfo.indexOf( ".pom" );
        return idx > 0
        && (     pathInfo.length() - 4 == idx
                || pathInfo.charAt( idx + 4 ) == '.'  );
    }
}