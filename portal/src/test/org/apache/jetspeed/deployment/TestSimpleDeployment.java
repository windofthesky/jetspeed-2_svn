/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.util.ArrayList;
import java.util.Iterator;

import javax.portlet.Portlet;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.impl.StandardFileSystemManager;
import org.apache.jetspeed.components.util.RegistrySupportedTestCase;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.container.window.impl.PortletWindowAccessorImpl;
import org.apache.jetspeed.deployment.impl.AutoDeploymentManager;
import org.apache.jetspeed.deployment.impl.DeployDecoratorEventListener;
import org.apache.jetspeed.deployment.impl.DeployPortletAppEventListener;
import org.apache.jetspeed.deployment.simpleregistry.SimpleRegistry;
import org.apache.jetspeed.deployment.simpleregistry.impl.InMemoryRegistryImpl;
import org.apache.jetspeed.factory.JetspeedPortletFactory;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.tools.pamanager.FileSystemPAM;
import org.apache.jetspeed.util.DirectoryUtils;
import org.apache.jetspeed.util.descriptor.PortletApplicationWar;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * <p>
 * TestSimpleDeployment
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class TestSimpleDeployment extends RegistrySupportedTestCase
{
    protected static final String TEST_PORTLET_APP_NAME = "HW_App";
    protected String webAppsDir;

    protected File deploySrc;

    protected File deployRootFile;

    protected String testDb;
    private PortletApplicationWar paWar1;
    private PortletApplicationWar paWar2;
    private PortletApplicationWar paWar3;
    protected File webAppsDirFile;
    protected File copyFrom;
    protected StandardFileSystemManager fsManager;
    protected PortletWindowAccessor windowAccess;

    /**
     * @param testName
     */
    public TestSimpleDeployment( String testName )
    {
        super(testName);
    }

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
    
    public void testFileSystemManagerOnWar() throws Exception
    {
        File demoApp = new File(deploySrc, "demo.war");
        
        FileObject fsObject = fsManager.toFileObject(demoApp);
        assertEquals(FileType.FILE, fsObject.getType());
        FileObject testObj = fsManager.createFileSystem(fsObject);
        assertEquals(FileType.FOLDER, testObj.getType());
        // assertEquals("org.apache.commons.vfs.provider.jar.JarFileSystem", testObj.getFileSystem().getClass().getName());
        FileObject webXml = testObj.resolveFile("WEB-INF/web.xml");
        assertNotNull(webXml);
        assertNotNull(webXml.getContent().getInputStream());
        //testObj.close();
        //fsManager.close();
        
        //demoApp.delete();
        System.out.println("Done");
    }
    
    public void testFileSystemManagerOnDir() throws Exception
    {
        File demoApp = new File("./test/testdata/deploy/webapp");
        assertTrue(demoApp.exists());
        FileObject testObj = fsManager.toFileObject(demoApp);
        assertEquals(FileType.FOLDER, testObj.getType());
        // assertEquals("org.apache.commons.vfs.provider.jar.JarFileSystem", testObj.getFileSystem().getClass().getName());
        FileObject webXml = testObj.resolveFile("WEB-INF/web.xml");
        assertNotNull(webXml);
        assertNotNull(webXml.getContent().getInputStream());
        fsManager.getFilesCache().close();        
    }
    
    public void testFileSystemManagerForJars() throws Exception
    {
        File demoApp = new File(deploySrc, "portletstd.jar");
        FileObject testObj = fsManager.createFileSystem(fsManager.toFileObject(demoApp));
        assertEquals("org.apache.commons.vfs.provider.jar.JarFileSystem", testObj.getFileSystem().getClass().getName());
        fsManager.getFilesCache().close();
    }

    public void testDeploy() throws Exception
    {

        System.out.println("Deployment src: " + deploySrc);

        SimpleRegistry simpleRegistry = new InMemoryRegistryImpl();
        DeployDecoratorEventListener ddel = new DeployDecoratorEventListener(simpleRegistry, deployRootFile
                .getAbsolutePath());

        DeployPortletAppEventListener dpal = new DeployPortletAppEventListener(webAppsDir, new FileSystemPAM(
                webAppsDir, portletRegistry, VFS.getManager(), entityAccess, windowAccess), portletRegistry, VFS.getManager());
        ArrayList eventListeners = new ArrayList(2);
        eventListeners.add(ddel);
        eventListeners.add(dpal);
        // Use a -1 delay to disable auto scan
        AutoDeploymentManager autoDeployment = new AutoDeploymentManager(deploySrc.getAbsolutePath(), -1, eventListeners, fsManager);
        
        autoDeployment.start();
        autoDeployment.fireDeploymentEvent();

        File decoratorVm = new File(deployRootFile.getAbsolutePath() + File.separator + "html" + File.separator
                + "portletstd" + File.separator + "decorator.vm");
        
        File demoApp = new File(webAppsDirFile, TEST_PORTLET_APP_NAME);
        File securityApp = new File(webAppsDirFile, "TestSecurityRoles");

        assertTrue(decoratorVm.getCanonicalPath() + " was not created!", decoratorVm.exists());

        verifyDemoAppCreated(TEST_PORTLET_APP_NAME, demoApp);
        verifyDemoAppCreated("TestSecurityRoles", securityApp);
       
        MutablePortletApplication jetspeedApp = portletRegistry.getPortletApplicationByIdentifier("jetspeed");
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
                Portlet portlet = JetspeedPortletFactory.loadPortletClass(def.getClassName());
                assertNotNull(portlet);
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
        
        autoDeployment.stop();
        
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
                .getPortletApplicationByIdentifier(TEST_PORTLET_APP_NAME));
        assertTrue(appName + " directory was not created, app not deployed.", appFile.exists());
    }
    

    
    

    /**
     * <p>
     * verifyDemoAppDeleted
     * </p>
     *
     * @param demoApp
     */
    private void verifyDemoAppDeleted( String appName, File appFile )
    {
        assertNull(appName + " was not removed from the registry.", portletRegistry
                .getPortletApplicationByIdentifier(TEST_PORTLET_APP_NAME));
        assertFalse(appName+" directory was not deleted.", appFile.exists());
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp()
    {

        try
        {
            super.setUp();
            copyFrom = new File("./test/deployment/deploy");
            deploySrc = new File("./target/deployment/deploy");
            deploySrc.mkdirs();
            deployRootFile = new File("./target/deployment/templates/decorators");
            deployRootFile.mkdirs();
            webAppsDirFile = new File("./target/deployment/webapps");
            webAppsDirFile.mkdirs();

            webAppsDir = webAppsDirFile.getCanonicalPath();
            testDb = new File("./test/db/hsql/Registry").getCanonicalPath();
            fsManager = new StandardFileSystemManager();
            fsManager.setConfiguration("./src/webapp/WEB-INF/conf/vfs-providers.xml");
            fsManager.init();
            
            copyDeployables();
            windowAccess = new PortletWindowAccessorImpl(entityAccess);         
            

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
     *
     * @throws FileSystemException
     */
    protected void copyDeployables() throws FileSystemException
    {      
        
        FileObject copyFromObj = fsManager.toFileObject(copyFrom);
        FileObject deploySrcObj = fsManager.toFileObject(deploySrc);
        fsManager.getFilesCache().clear(deploySrcObj.getFileSystem());
        deploySrcObj.copyFrom(copyFromObj, new AllFileSelector());
        copyFromObj.close();
        deploySrcObj.close();  
        fsManager.getFilesCache().removeFile(deploySrcObj.getFileSystem(), deploySrcObj.getName());
        fsManager.getFilesCache().removeFile(copyFromObj.getFileSystem(), copyFromObj.getName());
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        FileSystemPAM pam = new FileSystemPAM(webAppsDir, portletRegistry, VFS.getManager(), entityAccess, windowAccess);

        try
        {
            paWar1 = new PortletApplicationWar(webAppsDir + "/" + TEST_PORTLET_APP_NAME, TEST_PORTLET_APP_NAME, "/"
                    + TEST_PORTLET_APP_NAME, VFS.getManager());
            pam.undeploy(paWar1);
        }
        catch (Exception e1)
        {

        }

        pam.unregister("jetspeed");

        try
        {
            paWar3 = new PortletApplicationWar(webAppsDir + "/TestSecurityRoles", "TestSecurityRoles", "/TestSecurityRoles", VFS.getManager());

            pam.undeploy(paWar3);
        }
        catch (Exception e3)
        {

        }

        DirectoryUtils.rmdir(new File("./target/deployment"));
        fsManager.getFilesCache().close();
        super.tearDown();

    }

}