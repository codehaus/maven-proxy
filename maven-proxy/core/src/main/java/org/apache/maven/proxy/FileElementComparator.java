package org.apache.maven.proxy;

import java.util.Comparator;
import java.util.List;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class FileElementComparator implements Comparator
{

    private List repoConfigurations;

    public FileElementComparator(List repoConfigurations) {
        this.repoConfigurations = repoConfigurations;
    }

    public int compare(Object o1, Object o2)
    {
        FileElement fe1 = (FileElement) o1;
        FileElement fe2 = (FileElement) o2;

        if (fe1.isDirectory() && !fe2.isDirectory()) {
            return -1;
        }
        
        if (!fe1.isDirectory() && fe2.isDirectory()) {
            return 1;
        }
        
        
        int nameCompare = fe1.getFile().getName().compareTo(fe2.getFile().getName());
        if (nameCompare != 0) { return nameCompare; }

        if (fe1.isDirectory() && fe2.isDirectory()) {
            return 0;
        }

        if (fe1.getRepo() == null) { return -1; }

        if (fe2.getRepo() == null) { return 1; }

        int ri1 = repoConfigurations.indexOf(fe1.getRepo());
        int ri2 = repoConfigurations.indexOf(fe2.getRepo());
        if (ri1 == ri2) { return 0; }
        if (ri1 < ri2) { return -1; }
        return 1;

    }

}
