package org.apache.maven.proxy.standalone;

import java.io.*;

/**
 * The ClassFileServer implements a ClassServer that
 * reads class files from the file system. See the
 * doc for the "Main" method for how to run this
 * server.
 */
public class ProxyRepoServer extends HttpServer
{
	private String classpath;

	
	/**
	 * Constructs a ClassFileServer.
	 *
	 * @param classpath the classpath where the server locates classes
	 */
	public ProxyRepoServer(int port, String classpath) throws IOException
	{
		super(port);
		this.classpath = classpath;
	}

	/**
	 * Returns an array of bytes containing the bytecodes for
	 * the class represented by the argument <b>path</b>.
	 * The <b>path</b> is a dot separated class name with
	 * the ".class" extension removed.
	 *
	 * @return the bytecodes for the class
	 * @exception ClassNotFoundException if the class corresponding
	 * to <b>path</b> could not be loaded.
	 */
	public Response getResult(Request request) throws IOException
	{
		System.out.println("reading: " + request);
		
        
        
        int length = (int) (f.length());

		if (length == 0)
		{
			System.out.println("Zero length file");
			throw new IOException("File length is zero: " + path);
		}
		else
		{
			FileInputStream fin = new FileInputStream(f);
			DataInputStream in = new DataInputStream(fin);

			byte[] bytecodes = new byte[length];
			in.readFully(bytecodes);
			return null;
		}
	}

	
}
