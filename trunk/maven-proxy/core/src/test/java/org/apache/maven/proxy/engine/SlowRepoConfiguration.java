package org.apache.maven.proxy.engine;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.maven.proxy.components.ProxyArtifact;

/**
 * @author Ben Walding
 */
public class SlowRepoConfiguration extends MockRepoConfiguration
{
    public static final String MOCK_DATA_100 = "abcdefghij1234567890klmnopqrst1112131415abcdefghij1234567890klmnopqrst1112131415abcdefghij1234567890";
    public static final String MOCK_DATA_33 = "abcdefghij1234567890klmnopqrst111";

    private final long sleep;


    public SlowRepoConfiguration( String key, String url, String description, boolean copy, boolean hardFail,
                    boolean cacheFailures, long cachePeriod, long sleep )
    {
        super( key, url, description, copy, hardFail, cacheFailures, cachePeriod );
        this.sleep = sleep;
    }

    public RetrievalDetails retrieveArtifact( File out, String url ) throws IOException
    {
        sleep();
        return super.retrieveArtifact( out, url );
    }

    public ProxyArtifact getMetaInformationInternal( String url ) throws FileNotFoundException
    {
        sleep();
        return super.getMetaInformationInternal( url );
    }

    private void sleep()
    {
        try
        {
            //Thread.sleep(0) yields execution, but this is not important for this
            Thread.sleep( sleep );
        }
        catch ( InterruptedException e )
        {
            throw new RuntimeException( e );
        }
    }

}