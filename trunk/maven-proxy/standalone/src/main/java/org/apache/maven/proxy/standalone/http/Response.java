package org.apache.maven.proxy.standalone.http;

/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
import java.io.InputStream;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class Response
{
	private InputStream inputStream;
    private String contentType = "application/octet-stream";
    private long contentLength = -1;
    /**
	 * @return Returns the inputStream.
	 */
	public InputStream getInputStream()
	{
		return inputStream;
	}

	/**
	 * @param inputStream The inputStream to set.
	 */
	public void setInputStream(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}

	/**
	 * @return Returns the mimeType.
	 */
	public String getContentType()
	{
		return contentType;
	}

	/**
	 * @param mimeType The mimeType to set.
	 */
	public void setContentType(String mimeType)
	{
		this.contentType = mimeType;
	}

	/**
	 * @return Returns the size.
	 */
	public long getContentLength()
	{
		return contentLength;
	}

	/**
	 * @param size The size to set.
	 */
	public void setContentLength(long contentLength)
	{
		this.contentLength = contentLength;
	}

}
