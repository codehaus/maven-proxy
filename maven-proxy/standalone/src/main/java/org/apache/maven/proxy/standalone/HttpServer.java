package org.apache.maven.proxy.standalone;

import java.io.*;
import java.net.*;

/**
 * HttpServer is an abstract class that provides the
 * basic functionality of a mini-webserver. An
 * HttpServer must be extended
 * and the concrete subclass should define the <b>getBytes</b>
 * method which is responsible for retrieving the bytecodes
 * for a class.<p>
 *
 * The HttpServer creates a thread that listens on a socket
 * and accepts  HTTP GET requests. The HTTP response contains the
 * byte for the resource that requested in the GET header. <p>
 */
public abstract class HttpServer implements Runnable
{
	private ServerSocket server = null;

	/**
	 * Constructs a ClassServer that listens on <b>port</b> and
	 * obtains a class's bytecodes using the method <b>getBytes</b>.
	 *
	 * @param port the port number
	 * @exception IOException if the ClassServer could not listen
	 *            on <b>port</b>.
	 */
	protected HttpServer(int port) throws IOException
	{
		server = new ServerSocket(port);
		newListener();
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
	 * @exception IOException if error occurs reading the class
	 */
	public abstract Response getResult(String path) throws IOException;

	/**
	 * The "listen" thread that accepts a connection to the
	 * server, parses the header to obtain the class file name
	 * and sends back the bytecodes for the class (or error
	 * if the class is not found or the response was malformed).
	 */
	public void run()
	
	{
		Socket socket;

		// accept a connection
		try
		{
			socket = server.accept();
		}
		catch (IOException e)
		{
			System.out.println("Class Server died: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		// create a new thread to accept the next connection
		newListener();

		try
		{
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			try
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String path = getPath(in);
				Response rr = getResult(path);
				// send bytecodes in response (assumes HTTP/1.0 or later)
				try
				{
					out.writeBytes("HTTP/1.0 200 OK\r\n");
					if (rr.getContentLength() != -1)
					{
						out.writeBytes("Content-Length: " + rr.getContentLength() + "\r\n");
					}
					out.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
					transferStream(rr.getInputStream(), out);
					out.flush();
				}
				catch (IOException ie)
				{
					return;
				}

			}
			catch (Exception e)
			{
				// write out error response
				out.writeBytes("HTTP/1.0 400 " + e.getMessage() + "\r\n");
				out.writeBytes("Content-Type: text/html\r\n\r\n");
				out.flush();
			}

		}
		catch (IOException ex)
		{
			// eat exception (could log error to log file, but
			// write out to stdout for now).
			System.out.println("error writing response: " + ex.getMessage());
			ex.printStackTrace();

		}
		finally
		{
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
			}
		}
	}

	/**
	 * Transfers all remaining data in the input stream to the output stream
	 * 
	 * Neither stream will be closed at completion.
	 */
	public static void transferStream(InputStream is, OutputStream os) throws IOException
	{
		final byte[] buffer = new byte[1024];
		while (true)
		{
			int bytesRead = is.read(buffer, 0, buffer.length);
			if (bytesRead == -1)
				break;
			os.write(buffer, 0, bytesRead);
		}
	}

	/**
	 * Create a new thread to listen.
	 */
	private void newListener()
	{
		(new Thread(this)).start();
	}

	/**
	 * Returns the path to the class file obtained from
	 * parsing the HTML header.
	 */
	private static String getPath(BufferedReader in) throws IOException
	{
		final String PREFIX = "GET /";

		String line = in.readLine();
		String path = "";
		System.out.println("line:" + line);
		// extract class from GET line
		if (line.startsWith(PREFIX))
		{
			path = line.substring(PREFIX.length(), line.length() - 1).trim();
		}

		// eat the rest of header
		do
		{
			line = in.readLine();
			System.out.println("line:" + line);
		}
		while ((line.length() != 0) && (line.charAt(0) != '\r') && (line.charAt(0) != '\n'));

		if (path.length() != 0)
		{
			return path;
		}

		throw new IOException("Malformed Header");
	}
}
