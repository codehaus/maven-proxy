package org.apache.maven.proxy.standalone;

import java.io.IOException;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class ValidationException extends Exception
{

    /**
     * @param string
     */
    public ValidationException(String msg)
    {
        super(msg);
    }

    /**
     * @param t
     */
    public ValidationException(Throwable t)
    {
        super(t);
    }

}
