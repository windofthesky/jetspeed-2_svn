/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.deployment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.AbstractRequestContextTestCase;
import org.apache.jetspeed.deployment.impl.StandardDeploymentManager;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.tools.pamanager.servletcontainer.ApplicationServerManager;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.JarHelper;
import org.apache.jetspeed.om.portlet.PortletDefinition;

/**
 * <p>
 * TestSimpleDeployment
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class TestSimpleDeployment extends AbstractRequestContextTestCase
{
    protected static final String TEST_PORTLET_APP_NAME = "HW_App";
    protected String webAppsDir;

    protected File deploySrc;

    protected File deployRootFile;

    protected String testDb;
    protected File webAppsDirFile;
    protected File copyFrom;
    protected PortletFactory portletFactory;
    protected ApplicationServerManager manager;
 

    /**
     * Start the tests.
     * 
     * @param args
     *            the arguments. Not used
     */
    public static void main( String args[] )
    {
        junit.awtui.TestRunner.main(new String[]{TestSimpleDeployment.class.getName()});
    }

    /**
     * Creates the test suite.
     * 
     * @return a test suite (<code>TestSuite</code>) that includes all
     *         methods starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        // return new JetspeedTestSuite(TestSimpleDeployment.class);
        return new TestSuite(TestSimpleDeployment.class);
    }
    public void testFileSystemHelperOnWar() throws Exception
    {
        File demoApp = new File(deploySrc, "demo.war");
           
        JarHelper jarHelper = new JarHelper(demoApp, true);
        File rootDirectory = jarHelper.getRootDirectory();
        File webXml = new File(rootDirectory, "WEB-INF/web.xml");
        assertTrue(webXml.exists());
        jarHelper.close();          
        assertFalse(webXml.exists());
        
        // Test for keeping jar temp files around
        jarHelper = new JarHelper(demoApp, false);
        assertTrue(webXml.exists());
        jarHelper.close();          
        assertTrue(webXml.exists());
    }
    
    public void testFileSystemManagerOnDir() throws Exception
    {
        File demoApp = new File(getBaseDir()+"src/test/testdata/deploy/webapp");
        assertTrue(demoApp.exists());
        
        DirectoryHelper dirHelper = new DirectoryHelper(demoApp);
        File webXml = new File(dirHelper.getRootDirectory(), "WEB-INF/web.xml");
        assertTrue(webXml.exists());
             
    }
    
    /*    
    
    public void testDeploy() throws Exception
    {

        System.out.println("Deployment src: " + deploySrc);
        manager = new TomcatManager("", "", 0, "", 0, "", "");
        SimpleRegistry simpleRegistry = new InMemoryRegistryImpl();
        DeployDecoratorEventListener ddel = new DeployDecoratorEventListener(simpleRegistry, deployRootFile
                .getAbsolutePath());

        DeployPortletAppEventListener dpal = new DeployPortletAppEventListener(webAppsDir, new FileSystemPAM(
                webAppsDir, portletRegistry, entityAccess, windowAccess, portletCache, portletFactory, manager), portletRegistry );
        ArrayList eventListeners = new ArrayList(2);
        eventListeners.add(ddel);
        eventListeners.add(dpal);
        // Use a -1 delay to disable auto scan
        StandardDeploymentManager autoDeployment = new StandardDeploymentManager(deploySrc.getAbsolutePath(), -1, eventListeners );
        
        autoDeployment.start();
        autoDeployment.fireDeploymentEvent();

        File decoratorVm = new File(deployRootFile.getAbsolutePath() + File.separator + "generic" + File.separator + "html" + File.separator
                + "portletstd" + File.separator + "decorator.vm");
        
        File demoAppDeployed = new File(webAppsDirFile, TEST_PORTLET_APP_NAME);
        File demoApp = demoAppDeployed;
        File securityApp = new File(webAppsDirFile, "TestSecurityRoles");

        assertTrue(decoratorVm.getCanonicalPath() + " was not created!", decoratorVm.exists());

        verifyDemoAppCreated(TEST_PORTLET_APP_NAME, demoApp);
        verifyDemoAppCreated("TestSecurityRoles", securityApp);
       
        PortletApplication jetspeedApp = portletRegistry.getPortletApplicationByIdentifier("jetspeed");
        assertNotNull("jetspeed was not registered into the portlet registery.", jetspeedApp);
        assertFalse("local app, jetspeed, got deployed when it should have only been registered.", new File(webAppsDir
                + "/jetspeed").exists());

        //make sure we can load registered app's classes
        Iterator portletDefItr = jetspeedApp.getPortletDefinitions().iterator();
        while (portletDefItr.hasNext())
        {
            PortletDefinition def = (PortletDefinition) portletDefItr.next();
            try
            {
                Portlet portlet = JetspeedPortletFactoryProxy.loadPortletClass(def.getClassName());
                assertNotNull("Could not load portlet class: "+def.getClassName(), portlet);
            }
            catch (Exception e)
            {
                assertNull("Unable to load registered portlet class, " + def.getClassName(), e);
            }

        }
        
        // test undeploy
        File demoWar = new File(deploySrc, "demo.war");
        demoWar.delete();
        autoDeployment.fireUndeploymentEvent();        
        verifyDemoAppDeleted(TEST_PORTLET_APP_NAME, demoApp);    
        
        // test deploy again        
        copyDeployables();
        autoDeployment.fireDeploymentEvent();
        verifyDemoAppCreated(TEST_PORTLET_APP_NAME, demoApp);
        demoWar.delete();
        autoDeployment.fireUndeploymentEvent();
        verifyDemoAppDeleted(TEST_PORTLET_APP_NAME, demoApp);
        
        // test redeploy
        
        // So, first deploy the typical demo.war we have been using before.
        copyDeployables();
        autoDeployment.fireDeploymentEvent();
        verifyDemoAppCreated(TEST_PORTLET_APP_NAME, demoApp);
        DirectoryHelper demoAppDeployedDir = new DirectoryHelper(demoAppDeployed);
        long beforeSize = new File(demoAppDeployedDir.getRootDirectory(), "WEB-INF/portlet.xml").length();
        
        // Trigger re-deployment using a demo.war that has a slightly larger portlet.xml
        // then the one we just deployed.  We will use size comparisons as or litmus test.
        File redeployDemoWar = new File("./test/deployment/redeploy/demo.war");
        FileChannel srcDemoWarChannel = new FileInputStream(redeployDemoWar).getChannel();
        FileChannel dstDemoWarChannel = new FileOutputStream(demoWar).getChannel();
        dstDemoWarChannel.transferFrom(srcDemoWarChannel, 0, srcDemoWarChannel.size());
        srcDemoWarChannel.close();
        dstDemoWarChannel.close();
        
        // Make sure the demo.war that will trigger redeploy has a larger portlet.xml then the current one
        JarHelper rdDemoWar = new JarHelper(demoWar, true);
        assertTrue(new File(rdDemoWar.getRootDirectory(), "WEB-INF/portlet.xml").length() > beforeSize);
        
        // Need to slow it down so the timestamp check works
        Thread.sleep(500);
        demoWar.setLastModified(System.currentTimeMillis());
        autoDeployment.fireRedeploymentEvent();
      
        long afterSize = new File(demoAppDeployedDir.getRootDirectory(), "WEB-INF/portlet.xml").length();
        // The portlet.xml in re-deploy has an additional portlet entry in portlet.xml, so it should be bigger
        assertTrue(afterSize > beforeSize);
        autoDeployment.stop();
        
    }
    
    
    public void testUndeployVersusRedeploy() throws Exception
    {
        manager = new TomcatManager("", "", 0, "", 0, "", "");
        
        DeployPortletAppEventListener dpal = new DeployPortletAppEventListener(webAppsDir, new FileSystemPAM(
                webAppsDir, portletRegistry, entityAccess, windowAccess, portletCache, portletFactory, manager), portletRegistry );
        ArrayList eventListeners = new ArrayList(1);
        
        eventListeners.add(dpal);
        
        // Use a -1 delay to disable auto scan
        StandardDeploymentManager autoDeployment = new StandardDeploymentManager(deploySrc.getAbsolutePath(), -1, eventListeners );        
        autoDeployment.start();

        buildEntityTestData(autoDeployment);       
        
        
        MutablePortletEntity entity = entityAccess.getPortletEntity("testEnity");
        
        PreferenceSetCtrl prefs = (PreferenceSetCtrl) entity.getPreferenceSet();
        List values = new ArrayList(1);
        values.add("some value");
        prefs.add("pref1", values);
        
        entity.store();
        
        assertNotNull(entity);
        
        Preference pref = entity.getPreferenceSet().get("pref1");
        
        assertNotNull(pref);
        
        //test entity removal via undeploy
        File demoWar = new File(deploySrc, "demo.war");
        demoWar.delete();
        
        autoDeployment.fireUndeploymentEvent();
        
                
        entity = entityAccess.getPortletEntity("testEnity");
        
        assertNull(entity);
        
        // Now test that redploy DOES NOT kill the entity
        buildEntityTestData(autoDeployment);
        
        entity = entityAccess.getPortletEntity("testEnity");
        
        assertNotNull(entity);
        
        pref = entity.getPreferenceSet().get("pref1");
        
        assertNull("Preference was not deleted with last undeploy",pref);
        
        demoWar.setLastModified(System.currentTimeMillis());
        
        autoDeployment.fireRedeploymentEvent();        
        
        entity = entityAccess.getPortletEntity("testEnity");
        
        assertNotNull(entity);
        
    }
*/   

    /**
     * <p>
     * buildEntityTestData
     * </p>
     *
     * @param autoDeployment
     * @throws IOException
     * @throws PortletEntityNotStoredException
     */
    protected void buildEntityTestData( StandardDeploymentManager autoDeployment ) throws Exception
    {
        copyDeployables();
        
        File demoApp = new File(webAppsDirFile, TEST_PORTLET_APP_NAME);
                
        autoDeployment.fireDeploymentEvent();
        
        verifyDemoAppCreated(TEST_PORTLET_APP_NAME, demoApp);
        
        PortletApplication app = portletRegistry.getPortletApplication(TEST_PORTLET_APP_NAME);
        
        PortletDefinition portlet = app.getPortlets().iterator().next();
    }

    /**
     * <p>
     * verifyDemoAppCreated
     * </p>
     *
     * @param demoApp
     */
    private void verifyDemoAppCreated( String appName, File appFile )
    {
        assertNotNull(appName + " was not registered into the portlet registery.", portletRegistry
                .getPortletApplication(TEST_PORTLET_APP_NAME));
        assertTrue(appName + " directory was not created, app not deployed.", appFile.exists());
    }
    

    
    

    /**
     * <p>
     * verifyDemoAppDeleted
     * </p>
     *
     * @param demoApp
     *
    private void verifyDemoAppDeleted( String appName, File appFile )
    {
        assertNull(appName + " was not removed from the registry.", portletRegistry
                .getPortletApplicationByIdentifier(TEST_PORTLET_APP_NAME));
        assertFalse(appName+" directory was not deleted.", appFile.exists());
    }
    */

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp()
    {

        try
        {
            super.setUp();
            copyFrom = new File(getBaseDir()+"src/test/deployment/deploy");
            deploySrc = new File(getBaseDir()+"target/deployment/deploy");
            deploySrc.mkdirs();
            deployRootFile = new File(getBaseDir()+"target/deployment/templates/decorators");
            deployRootFile.mkdirs();
            webAppsDirFile = new File(getBaseDir()+"target/deployment/webapps");
            webAppsDirFile.mkdirs();

            webAppsDir = webAppsDirFile.getCanonicalPath();
//            testDb = new File("src/test/db/hsql/Registry").getCanonicalPath();           
            
            copyDeployables();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new AssertionFailedError("Unable to set up test environment " + e.toString());
        }

    }

    /**
     * <p>
     * copyDeployables
     * </p>
     * @throws IOException
     */
    protected void copyDeployables() throws IOException
    {      
        
  
        copyFiles(copyFrom, deploySrc);
        
    }
    
    

    /**
     * <p>
     * copyFiles
     * </p>
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    protected void copyFiles(File srcDir, File dstDir) throws IOException, FileNotFoundException
    {
        File[] children = srcDir.listFiles();
        for(int i=0; i<children.length; i++)
        {
            File child = children[i];
            if(child.isFile())
            {
                File toFile = new File(dstDir, child.getName());
                toFile.createNewFile();
                FileChannel srcChannel = new FileInputStream(child).getChannel();
                FileChannel dstChannel = new FileOutputStream(toFile).getChannel();
                dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
                srcChannel.close();
                dstChannel.close();
            }
            else
            {
                File newSubDir = new File(dstDir, child.getName());
                newSubDir.mkdir();
                copyFiles(child, newSubDir);
            }
        }
    }
}
