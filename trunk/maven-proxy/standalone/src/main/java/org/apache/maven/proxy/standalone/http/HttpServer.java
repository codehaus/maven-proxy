package org.apache.maven.proxy.standalone.http;

import org.apache.avalon.framework.activity.Startable;
import org.apache.avalon.framework.configuration.Configurable;

/**
 * Configuration: Accepts "port" as an integer specifying the port to listen on
 * @author  Ben Walding
 * @version $Id$
 */
public interface HttpServer extends Startable, Configurable
{
	String ROLE = HttpServer.class.getName();
}
