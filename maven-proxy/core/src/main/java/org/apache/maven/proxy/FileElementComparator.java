package org.apache.maven.proxy;

/*
 * Copyright 2003-2004 Ben Walding
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

import java.util.Comparator;
import java.util.List;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class FileElementComparator implements Comparator
{

    private List repoConfigurations;

    public FileElementComparator( List repoConfigurations )
    {
        this.repoConfigurations = repoConfigurations;
    }

    public int compare( Object o1, Object o2 )
    {
        FileElement fe1 = (FileElement) o1;
        FileElement fe2 = (FileElement) o2;

        if ( fe1.isDirectory() && !fe2.isDirectory() )
        {
            return -1;
        }

        if ( !fe1.isDirectory() && fe2.isDirectory() )
        {
            return 1;
        }

        int nameCompare = fe1.getFile().getName().compareTo( fe2.getFile().getName() );
        if ( nameCompare != 0 )
        {
            return nameCompare;
        }

        if ( fe1.isDirectory() && fe2.isDirectory() )
        {
            return 0;
        }

        if ( fe1.getRepo() == null )
        {
            return -1;
        }

        if ( fe2.getRepo() == null )
        {
            return 1;
        }

        int ri1 = repoConfigurations.indexOf( fe1.getRepo() );
        int ri2 = repoConfigurations.indexOf( fe2.getRepo() );
        if ( ri1 == ri2 )
        {
            return 0;
        }
        if ( ri1 < ri2 )
        {
            return -1;
        }
        return 1;

    }

}