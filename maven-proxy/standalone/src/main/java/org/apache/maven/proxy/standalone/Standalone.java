package org.apache.maven.proxy.standalone;

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
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.apache.maven.proxy.RepositoryServlet;
import org.apache.maven.proxy.config.*;
import org.apache.maven.proxy.config.PropertyLoader;
import org.apache.maven.proxy.config.RetrievalComponentConfiguration;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.util.MultiException;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class Standalone
{
    private static final String VERSION = "SNAPSHOT";
    
    public static void main(String args[])
    {
        Standalone launcher;

        try
        {
            launcher = new Standalone();
            launcher.doMain(args);
        }
        catch (Exception e)
        {
            System.err.println("Internal error:");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void doMain(String args[]) throws MultiException, IOException, ValidationException
    {
        System.err.println("maven-proxy " + Standalone.VERSION);
        
        if (args.length != 1)
        {
            System.err.println("Usage:");
            System.err.println("  java -jar maven-proxy-SNAPSHOT-uber.jar maven-proxy.properties");
            return;
        }

        RetrievalComponentConfiguration rcc = null;
        try
        {
            rcc = loadAndValidateConfiguration(args[0]);
        }
        catch (ValidationException e)
        {
            Throwable t = e;

            System.err.println("Error while loading properties:");

            while (t != null)
            {
                System.err.println("  " + t.getLocalizedMessage());
                t = t.getCause();
            }
        }
        

        System.out.println("Saving repository at " + rcc.getLocalStore());
        for (Iterator iter = rcc.getRepos().iterator(); iter.hasNext();) {
            RepoConfiguration repo = (RepoConfiguration) iter.next();
            System.out.println("Scanning repository: " + repo.getUrl());
        } 
        System.out.println("Starting...");
        
        HttpServer server = new HttpServer();
        SocketListener listener = new SocketListener();
        listener.setPort(rcc.getPort());
        server.addListener(listener);

        HttpContext context = new HttpContext();
        context.setContextPath("/");
        ServletHandler sh = new ServletHandler();
        sh.addServlet("Repository", "/*", RepositoryServlet.class.getName());
        context.setAttribute("config", rcc);
        context.addHandler(sh);
        server.addContext(context);

        server.start();
        System.out.println("Started.");
        System.out.println("Add the following to your ~/build.properties file:");
        System.out.println("   maven.repo.remote=http://<external ip>:" + rcc.getPort());
        if (rcc.isBrowsable())  {
            System.out.println("The repository can be browsed at http://<external ip>:" + rcc.getPort() + "/");
        } else  {
            System.out.println("Repository browsing is not enabled.");
        }

    }

    /**
     * This method will load and validate the properties.
     * @todo make it throw a validation exception and defer
     *       logging to the handler of the exception.
     * @param filename The name of the properties file.
     * @return Returns a <code>Properties</code> object if the load and validation was successfull.
     * @throws ValidationException If there was any problem validating the properties
     */
    private RetrievalComponentConfiguration loadAndValidateConfiguration(String filename) throws ValidationException
    {
        RetrievalComponentConfiguration rcc;
        File file = new File(filename);

        try
        {
            rcc = (new PropertyLoader()).load(new FileInputStream(file));
        }
        catch (FileNotFoundException ex)
        {
            System.err.println("No such file: " + file.getAbsolutePath());
            return null;
        }
        catch (IOException ex)
        {
            throw new ValidationException(ex);
        }

        {
            //Verify local repository set
            String tmp = checkSet(rcc.getLocalStore(), PropertyLoader.REPO_LOCAL_STORE);

            file = new File(tmp);
            if (!file.exists())
            {
                throw new ValidationException("The local repository doesn't exist: " + file.getAbsolutePath());
            }

            if (!file.isDirectory())
            {
                throw new ValidationException("The local repository must be a directory: " + file.getAbsolutePath());
            }
        }

        {
            //Verify remote repository set
            //only warn if missing
            if (rcc.getRepos().size() < 1)
            {
                throw new ValidationException("At least one remote repository must be configured.");
            }
        }

        // all ok
        return rcc;
    }

    private String checkSet(String value, String propertyName) throws ValidationException
    {
        if (value == null)
        {
            throw new ValidationException("Missing property '" + propertyName + "'");
        }

        return value;
    }
}
