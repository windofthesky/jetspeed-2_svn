/**
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment;

import java.io.File;
import java.util.HashMap;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.deployment.fs.FileSystemScanner;
import org.apache.jetspeed.deployment.fs.JARObjectHandlerImpl;
import org.apache.jetspeed.deployment.impl.DeployDecoratorEventListener;
import org.apache.jetspeed.deployment.impl.DeployPortletAppEventListener;
import org.apache.jetspeed.deployment.simpleregistry.SimpleRegistry;
import org.apache.jetspeed.deployment.simpleregistry.impl.InMemoryRegistryImpl;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;
import org.apache.jetspeed.tools.pamanager.FileSystemPAM;
import org.apache.jetspeed.tools.pamanager.PortletApplicationException;
import org.apache.jetspeed.util.DirectoryUtils;

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
    protected static final String TEST_PORTLET_APP_NAME = "HW_App";
    protected String webAppsDir;

    protected String delpoySrc;

    protected File deployRootFile;

    protected String testDb;

    protected String deployRoot;

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
        return new JetspeedTestSuite(TestSimpleDeployment.class);
    }

//    public void overrideProperties(Configuration properties)
//    {
//        super.overrideProperties(properties);
//    }

    public void testDeploy() throws Exception
    {

		
        if (deployRootFile.exists())
        {
            assertTrue("Unable to clean deployment directory.", DirectoryUtils.rmdir(deployRootFile));
        }
        else
        {
            deployRootFile.mkdirs();
        }
        System.out.println("Deployment src: " + delpoySrc);
        DeploymentEventDispatcher ded = new DeploymentEventDispatcher(deployRoot);
        SimpleRegistry registry = new InMemoryRegistryImpl();
        DeployDecoratorEventListener ddel = new DeployDecoratorEventListener(registry);
        DeployPortletAppEventListener dpal = new DeployPortletAppEventListener(webAppsDir, new FileSystemPAM());
        ded.addDeploymentListener(ddel);
        ded.addDeploymentListener(dpal);
        HashMap handlers = new HashMap();
        handlers.put("jar", JARObjectHandlerImpl.class);
        handlers.put("war", JARObjectHandlerImpl.class);
        FileSystemScanner fScanner = new FileSystemScanner(delpoySrc, handlers, ded, 500);
        fScanner.start();
        Thread.sleep(10000);
        fScanner.safeStop();        

        File decoratorVm =
            new File(deployRoot + File.separator + "html" + File.separator + "portletstd" + File.separator + "decorator.vm");

        assertTrue(decoratorVm.getCanonicalPath() + " was not created!", decoratorVm.exists());
		PortletRegistryComponent portletRegistry = (PortletRegistryComponent) Jetspeed.getComponentManager().getComponent(PortletRegistryComponent.class);
        assertNotNull(TEST_PORTLET_APP_NAME+" was not registered into the portlet registery.", portletRegistry.getPortletApplicationByIdentifier(TEST_PORTLET_APP_NAME));
        assertTrue(TEST_PORTLET_APP_NAME+" directory was not created, app not deployed.", new File(webAppsDir+"/"+TEST_PORTLET_APP_NAME).exists());
        assertNotNull("jetspeed was not registered into the portlet registery.", portletRegistry.getPortletApplicationByIdentifier("jetspeed"));
        assertFalse("local app, jetspeed, got deployed when it should have only been registered.", new File(webAppsDir+"/jetspeed").exists());
        

    }



    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp()
    {
	
		try
        {
			super.setUp();
		    // set up test data
            delpoySrc = new File("./test/deployment/deploy").getCanonicalPath();
            deployRootFile = new File("./test/deployment/templates/decorator");
            deployRoot = deployRootFile.getCanonicalPath();
            webAppsDir = new File("./test/deployment/webapps").getCanonicalPath();
            testDb = new File("./test/db/hsql/Registry").getCanonicalPath();
            // remove any prior left overs
            
			FileSystemPAM pam = new FileSystemPAM();
			
			pam.undeploy(webAppsDir, TEST_PORTLET_APP_NAME);	
        }
        catch (Exception e)
        {
			e.printStackTrace();
            throw new AssertionFailedError("Unable to set up test environment "+e.toString());          
        }
        
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        
        super.tearDown();
		FileSystemPAM pam = new FileSystemPAM();
		try
        {
            pam.undeploy(webAppsDir, TEST_PORTLET_APP_NAME);
            pam.unregister(webAppsDir, "jetspeed");
        }
        catch (PortletApplicationException e)
        {            
            e.printStackTrace();
        }
		
    }

}
