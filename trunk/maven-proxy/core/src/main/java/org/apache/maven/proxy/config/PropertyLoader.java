package org.apache.maven.proxy.config;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Maven" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Maven", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 * ====================================================================
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class PropertyLoader
{
    public static final String REPO_LOCAL_STORE = "repo.local.store";
    public static final String PORT = "port";
    public static final int DEFAULT_PORT = 4321;
    public static final String BROWSABLE = "browsable";

    public RetrievalComponentConfiguration load(Properties props) throws IOException, ValidationException
    {
        RetrievalComponentConfiguration rcc = new RetrievalComponentConfiguration();

        rcc.setLocalStore(props.getProperty(REPO_LOCAL_STORE));
        
        if (props.getProperty(PORT) == null)
        {
            rcc.setPort(DEFAULT_PORT);
        }
        else
        {
            try
            {
                rcc.setPort(Integer.parseInt(props.getProperty(PORT)));
            }
            catch (NumberFormatException ex)
            {
                throw new ValidationException("Property " + PORT + " must be a integer");
            }
        }
        
        rcc.setBrowsable(Boolean.valueOf(props.getProperty(BROWSABLE)).booleanValue());

        {
            String propertyList = props.getProperty("proxy.list");
            StringTokenizer tok = new StringTokenizer(propertyList, ",");
            while (tok.hasMoreTokens())
            {
                String key = tok.nextToken();
                String host = props.getProperty("proxy." + key + ".host");
                int port = Integer.parseInt(props.getProperty("proxy." + key + ".port"));
                String username = props.getProperty("proxy." + key + ".username");
                String password = props.getProperty("proxy." + key + ".password");
                ProxyConfiguration pc = new ProxyConfiguration(key, host, port, username, password);
                rcc.addProxy(pc);
            }
        }

        {
            String repoList = props.getProperty("repo.list");

            StringTokenizer tok = new StringTokenizer(repoList, ",");
            while (tok.hasMoreTokens())
            {
                String key = tok.nextToken();
                String url = props.getProperty("repo." + key + ".url");
                String username = props.getProperty("repo." + key + ".username");
                String password = props.getProperty("repo." + key + ".password");
                String proxyKey = props.getProperty("repo." + key + ".proxy");

                ProxyConfiguration proxy = null;
                if (proxyKey != null)
                {
                    proxy = rcc.getProxy(proxyKey);
                }

                RepoConfiguration rc = new RepoConfiguration(url, username, password, proxy);
                rcc.addRepo(rc);
            }
        }
        return rcc;

    }
    
    public RetrievalComponentConfiguration load(InputStream is) throws IOException, ValidationException
    {
        Properties props = new Properties();
        props.load(is);
        return load(props);
    }
}
