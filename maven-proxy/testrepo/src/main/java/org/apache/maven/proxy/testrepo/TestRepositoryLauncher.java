package org.apache.maven.proxy.testrepo;

/**
 * @author  Ben Walding
 * @version $Id$
 */
public class TestRepositoryLauncher
{
    /** log4j logger */
    private static final org.apache.log4j.Logger LOGGER =
        org.apache.log4j.Logger.getLogger(TestRepositoryLauncher.class);

    public static void main(String args[]) throws Exception
    {
        TestRepository tr = new TestRepository();
        LOGGER.info("Starting...");
        tr.start();
        LOGGER.info("Started.");
        LOGGER.info("Sleeping...");
        for (int i = 6; i > 0; i--)
        {
            int sleepsize = 10;
            LOGGER.info("Sleeping for another " + (i * sleepsize) + " seconds");
            Thread.sleep(1000 * sleepsize);
            
        }
        LOGGER.info("Stopping...");
        tr.stop();
        LOGGER.info("Stopped.");
    }

}
