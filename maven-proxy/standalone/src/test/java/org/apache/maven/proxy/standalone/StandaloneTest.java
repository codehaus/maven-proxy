package org.apache.maven.proxy.standalone;

import junit.framework.TestCase;

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

/**
 * @author Ben Walding
 */
public class StandaloneTest extends TestCase
{
    public void testSimple() throws InterruptedException
    {
        String[] args = new String[]
            {
                "src/test/test.properties"
            };
        Standalone.main( args );
        Thread.sleep( 60000 );
    }
}