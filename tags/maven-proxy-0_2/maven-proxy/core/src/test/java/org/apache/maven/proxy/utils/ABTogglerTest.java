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

import org.apache.maven.proxy.utils.ABToggler;

import junit.framework.TestCase;

/**
 * @author Ben Walding
 *
 */
public class ABTogglerTest extends TestCase
{

    public void testSimple()
    {
        ABToggler t = new ABToggler();
        assertEquals( "t.next() 1", "a", t.getNext() );
        assertEquals( "t.next() 2", "b", t.getNext() );
        assertEquals( "t.next() 3", "a", t.getNext() );
        assertEquals( "t.next() 4", "b", t.getNext() );
        assertEquals( "t.next() 5", "a", t.getNext() );
        assertEquals( "t.next() 6", "b", t.getNext() );
        assertEquals( "t.next() 7", "a", t.getNext() );
        assertEquals( "t.next() 8", "b", t.getNext() );
        assertEquals( "t.next() 9", "a", t.getNext() );
        assertEquals( "t.next() 10", "b", t.getNext() );
    }

}