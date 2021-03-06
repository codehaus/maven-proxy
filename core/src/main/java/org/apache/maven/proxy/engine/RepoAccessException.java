package org.apache.maven.proxy.engine;

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

import org.apache.maven.proxy.config.RepoConfiguration;

/**
 * @author Ben Walding
 */
public class RepoAccessException extends RuntimeException
{
    private final RepoConfiguration repo;
    private final String path;
    private final String message;
    private final Throwable cause;

    public RepoAccessException( RepoConfiguration repo, String path, String message, Throwable cause )
    {
        this.repo = repo;
        this.path = path;
        this.message = message;
        this.cause = cause;
    }

    public Throwable getCause()
    {
        return cause;
    }

    public String getMessage()
    {
        return message;
    }

    public String getPath()
    {
        return path;
    }

    public RepoConfiguration getRepo()
    {
        return repo;
    }
}