/**
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.portlet.Portlet;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.apache.jetspeed.components.util.RegistrySupportedTestCase;
import org.apache.jetspeed.deployment.fs.FileSystemScanner;
import org.apache.jetspeed.deployment.fs.JARObjectHandlerImpl;
import org.apache.jetspeed.deployment.impl.DeployDecoratorEventListener;
import org.apache.jetspeed.deployment.impl.DeployPortletAppEventListener;
import org.apache.jetspeed.deployment.simpleregistry.SimpleRegistry;
import org.apache.jetspeed.deployment.simpleregistry.impl.InMemoryRegistryImpl;
import org.apache.jetspeed.factory.JetspeedPortletFactory;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.test.JetspeedTestSuite;
import org.apache.jetspeed.tools.pamanager.FileSystemPAM;
import org.apache.jetspeed.util.DirectoryUtils;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * <p>
 * TestSimpleDeployment
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class TestSimpleDeployment extends RegistrySupportedTestCase
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
        SimpleRegistry simpleRegistry = new InMemoryRegistryImpl();
        DeployDecoratorEventListener ddel = new DeployDecoratorEventListener(simpleRegistry);
        
        DeployPortletAppEventListener dpal = new DeployPortletAppEventListener(webAppsDir, new FileSystemPAM(portletRegistry, Locale.getDefault()), portletRegistry, Locale.getDefault());
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
		
        assertNotNull(TEST_PORTLET_APP_NAME+" was not registered into the portlet registery.", portletRegistry.getPortletApplicationByIdentifier(TEST_PORTLET_APP_NAME));
        assertTrue(TEST_PORTLET_APP_NAME+" directory was not created, app not deployed.", new File(webAppsDir+"/"+TEST_PORTLET_APP_NAME).exists());
        MutablePortletApplication jetspeedApp = portletRegistry.getPortletApplicationByIdentifier("jetspeed");
        assertNotNull("jetspeed was not registered into the portlet registery.", jetspeedApp);
        assertFalse("local app, jetspeed, got deployed when it should have only been registered.", new File(webAppsDir+"/jetspeed").exists());
        
        //make sure we can load registered app's classed
        Iterator portletDefItr =  jetspeedApp.getPortletDefinitions().iterator();
        while(portletDefItr.hasNext())
        {
            PortletDefinition def = (PortletDefinition) portletDefItr.next();
            try
            {
                Portlet portlet = JetspeedPortletFactory.loadPortletClass(def.getClassName());
                assertNotNull(portlet);
            }
            catch (Exception e)
            {
                assertNull("Unable to load registered portlet class, "+def.getClassName(), e);
            }
           
        }
        

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
            File webAppsDirFile = new File("./target/webapps");
            
            if(!webAppsDirFile.exists())
            { 
               webAppsDirFile.mkdirs();
            }
            webAppsDir = webAppsDirFile.getCanonicalPath();
            testDb = new File("./test/db/hsql/Registry").getCanonicalPath();
            // remove any prior left overs
            
			FileSystemPAM pam = new FileSystemPAM(portletRegistry, Locale.getDefault());
			

	            pam.undeploy(webAppsDir, TEST_PORTLET_APP_NAME);
            pam.unregister(webAppsDir, "jetspeed");
            pam.undeploy(webAppsDir, "struts-demo");
	        
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
		
		
    }

}
