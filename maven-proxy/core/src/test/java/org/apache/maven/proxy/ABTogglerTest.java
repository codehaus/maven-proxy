package org.apache.maven.proxy;

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
        assertEquals( "t.next() 1", "A", t.getNext() );
        assertEquals( "t.next() 2", "B", t.getNext() );
        assertEquals( "t.next() 3", "A", t.getNext() );
        assertEquals( "t.next() 4", "B", t.getNext() );
        assertEquals( "t.next() 5", "A", t.getNext() );
        assertEquals( "t.next() 6", "B", t.getNext() );
        assertEquals( "t.next() 7", "A", t.getNext() );
        assertEquals( "t.next() 8", "B", t.getNext() );
        assertEquals( "t.next() 9", "A", t.getNext() );
    }

}