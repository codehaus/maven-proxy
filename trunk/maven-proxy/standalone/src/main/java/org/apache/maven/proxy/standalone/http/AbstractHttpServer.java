package org.apache.maven.proxy.standalone.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

/**
 * AbstractHttpServer is an abstract class that provides the
 * basic functionality of a mini-webserver. An
 * AbstractHttpServer must be extended
 * and the concrete subclass should define the <b>getResponse</b>
 * method which is responsible for handling the request<p>
 *
 * The HttpServer creates a thread that listens on a socket
 * and accepts  HTTP GET requests. The HTTP response contains the
 * byte for the resource that requested in the GET header. <p>
 */
public abstract class AbstractHttpServer implements Runnable, HttpServer
{
	private ServerSocket server = null;
	private int port = -1; //Lifecycle contract says that configure() will set this up correctly

	/** So it knows not to print exceptions about server closing */
	private boolean stopping = false;

	/**
	 */
	public abstract Response getResponse(Request request) throws IOException;

	/**
	 * The "listen" thread that accepts a connection to the
	 * server, parses the header to obtain the class file name
	 * and sends back the bytecodes for the class (or error
	 * if the class is not found or the response was malformed).
	 */
	public void run()
	{
		final Socket socket;
		try
		{
			socket = server.accept();
		}
		catch (IOException e)
		{
			if (!stopping)
			{
				System.out.println("HttpServer died: " + e.getMessage());
				e.printStackTrace();
			}
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
				Request request = new Request(in);
				request.dump(System.err);
				Response response = getResponse(request);
				// send bytecodes in response (assumes HTTP/1.0 or later)
				try
				{
					out.writeBytes("HTTP/1.0 200 OK\r\n");
					if (response.getContentLength() != -1)
					{
						out.writeBytes("Content-Length: " + response.getContentLength() + "\r\n");
					}
					out.writeBytes("Content-Type: " + response.getContentType() + "\r\n\r\n");
					transferStream(response.getInputStream(), out);
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
	//inherit javadoc
	public void start() throws Exception
	{
		stopping = false;
		server = new ServerSocket(port);
		newListener();
	}

	//inherit javadoc
	public void stop() throws Exception
	{
		stopping = true;
		server.close();
	}

	private void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * Accepts "port" as an integer specifying the port to listen on
	 */
	public void configure(Configuration conf) throws ConfigurationException
	{
		setPort(conf.getAttributeAsInteger("port", 80));
	}

}
