package org.apache.maven.proxy;

import java.io.File;

/**
 * @author <a href="bwalding@apache.org">Ben Walding</a>
 * @version $Id$
 */
public class MockFile extends File
{
	private boolean dir;

	/**
	 * @param pathname
	 */
	public MockFile(String pathname, boolean dir) {
		super( pathname );
		this.dir = dir;
	}

	public boolean isDirectory()
	{
		return dir;
	}

}
