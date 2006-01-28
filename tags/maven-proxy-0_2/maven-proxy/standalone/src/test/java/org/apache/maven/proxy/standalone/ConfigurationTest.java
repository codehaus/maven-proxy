package org.apache.maven.proxy.standalone;


import junit.framework.TestCase;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class ConfigurationTest extends TestCase
{
    public void testCVSNameParse()
    {
        //String concat done so CVS doesn't change my test case all the time
        {
            String input = "$" + "Name: MAVENPROXY-TEMP " + "$";
            assertEquals( "MAVENPROXY-TEMP", Standalone.extractName( input ) );
        }

        {
            String input = "$" + "Name:  " + "$";
            assertEquals( "", Standalone.extractName( input ) );
        }

        {
            String input = "$" + "Name: A " + "$";
            assertEquals( "A", Standalone.extractName( input ) );
        }
    }
}