package org.apache.maven.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Ben Walding
 *  
 */
public class RetrievalDetails {
	private InputStream is;

	private long length;

	public RetrievalDetails(InputStream is) {
		this(is, -1);
	}

	public RetrievalDetails(InputStream is, long length) {
		this.is = is;
		this.length = length;
	}

	/**
	 * @param path
	 * @throws FileNotFoundException
	 */
	public RetrievalDetails(File path) throws FileNotFoundException {

		this(new FileInputStream(path), path.length());
	}

	/**
	 * @return
	 */
	public InputStream getInputStream() {
		return is;
	}

	/**
	 * @return
	 */
	public long getLength() {
		return length;
	}

}