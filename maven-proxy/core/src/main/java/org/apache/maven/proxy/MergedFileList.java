package org.apache.maven.proxy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.proxy.config.RepoConfiguration;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class MergedFileList
{
    public static List filenames(File[] input, RepoConfiguration repo)
    {
        List array = new ArrayList();
        if (input != null)
        {
            for (int i = 0; i < input.length; i++)
            {
                File file = input[i];
                FileElement fe = new FileElement(file, repo);
                array.add(fe);
            }
        }
        return array;
    }
}
