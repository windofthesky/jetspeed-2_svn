/**
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment;

import java.io.File;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.deployment.fs.FileSystemScanner;
import org.apache.jetspeed.deployment.fs.JARObjectHandlerImpl;
import org.apache.jetspeed.deployment.impl.DeployDecoratorEventListener;
import org.apache.jetspeed.deployment.simpleregistry.SimpleRegistry;
import org.apache.jetspeed.deployment.simpleregistry.impl.InMemoryRegistryImpl;
import org.apache.jetspeed.test.JetspeedTest;

/**
 * <p>
 * TestSimpleDeployment
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class TestSimpleDeployment extends JetspeedTest
{
    /**
     * @param testName
     */
    public TestSimpleDeployment(String testName)
    {
        super(testName);
    }

    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestSimpleDeployment.class.getName()});
    }

    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSimpleDeployment.class);
    }

    public void testDecorators() throws Exception
    {
        String deploySrc = new File("./test/deployment/deploy").getCanonicalPath();
        File deployRootFile = new File("./test/deployment/templates/decorator");
        String deployRoot = deployRootFile.getCanonicalPath();
        if (deployRootFile.exists())
        {
        	assertTrue("Unable to clean deployment directory.", deleteDir(deployRootFile));
        }
        else 
        {
        	deployRootFile.mkdirs();			
        }
        System.out.println("Deployment src: " + deploySrc);
        DeploymentEventDispatcher ded = new DeploymentEventDispatcher(deployRoot);
        SimpleRegistry registry = new InMemoryRegistryImpl();
        DeployDecoratorEventListener ddel = new DeployDecoratorEventListener(registry);
        ded.addDeploymentListener(ddel);
        HashMap handlers = new HashMap();
        handlers.put("jar", JARObjectHandlerImpl.class);
        FileSystemScanner fScanner = new FileSystemScanner(deploySrc, handlers, ded, 500, "decorator.properties");
        fScanner.start();
        Thread.sleep(10000);
        fScanner.safeStop();
        
        File decoratorVm = new File(deployRoot+File.separator+"html"+File.separator+"portletstd"+File.separator+"decorator.vm");
        
        assertTrue(decoratorVm.getCanonicalPath()+" was not created!", decoratorVm.exists());
        
        
    }

    public static boolean deleteDir(File dir)
    {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success)
                {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

}
