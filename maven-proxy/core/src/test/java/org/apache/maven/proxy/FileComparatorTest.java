package org.apache.maven.proxy;

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

import junit.framework.TestCase;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class FileComparatorTest extends TestCase
{
    private FileComparator fc;

    protected void setUp()
    {
        fc = new FileComparator();
    }

    protected void tearDown()
    {
        fc = null;
    }

    public void testBasic()
    {
        File fa = new File("a");
        File fb = new File("b");
        assertTrue("fa vs fb", fc.compare(fa, fb) < 0);
        assertTrue("fb vs fa", fc.compare(fb, fa) > 0);
        assertTrue("fa vs fa", fc.compare(fa, fa) == 0);
        assertTrue("fb vs fb", fc.compare(fb, fb) == 0);
    }
}
