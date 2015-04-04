/*
 * Copyright (c) 2004-2005, by OpenXource, LLC. All rights reserved.
 * 
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF OPENXOURCE
 *  
 * The copyright notice above does not evidence any          
 * actual or intended publication of such source code. 
 */
package org.apache.maven.proxy.components;


public interface Startable
{
    void stop() throws Exception;
    void start() throws Exception;
}
