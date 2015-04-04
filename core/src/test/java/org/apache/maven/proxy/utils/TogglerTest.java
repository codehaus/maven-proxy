package org.apache.maven.proxy.utils;

import org.apache.maven.proxy.utils.Toggler;

import junit.framework.TestCase;

/**
 * @author Ben Walding
 *
 */
public class TogglerTest extends TestCase
{

    public void testSimple()
    {
        String[] s =
            {
                    "A", "B", "C"
            };

        Toggler t = new Toggler( s, 0 );
        assertEquals( "t.next() 1", "A", t.getNext() );
        assertEquals( "t.next() 2", "B", t.getNext() );
        assertEquals( "t.next() 3", "C", t.getNext() );
        assertEquals( "t.next() 4", "A", t.getNext() );
        assertEquals( "t.next() 5", "B", t.getNext() );
        assertEquals( "t.next() 6", "C", t.getNext() );
        assertEquals( "t.next() 7", "A", t.getNext() );
        assertEquals( "t.next() 8", "B", t.getNext() );
        assertEquals( "t.next() 9", "C", t.getNext() );
    }

}