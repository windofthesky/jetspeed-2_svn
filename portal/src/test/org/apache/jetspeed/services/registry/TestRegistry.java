/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.services.registry;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletMode;

import junit.framework.AssertionFailedError;
import junit.framework.Test;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.persistence.store.impl.LockFailedException;
import org.apache.jetspeed.components.portletregsitry.PortletRegistryComponent;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.om.common.DublinCore;
import org.apache.jetspeed.om.common.portlet.ContentTypeComposite;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.persistence.TransactionStateException;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;
import org.apache.ojb.otm.lock.LockingException;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.ParameterSetCtrl;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.servlet.WebApplicationDefinition;
/**
 * 
 * TestRegistry runs a suite tests creating, updating, retreiving and deleting
 * portlet information from the registry.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class TestRegistry extends JetspeedTest
{
    private static final String PORTLET_0_CLASS = "com.portlet.MyClass0";
    private static final String PORTLET_0_NAME = "Portlet 0";
    private static final String PORTLET_1_CLASS = "com.portlet.MyClass";
    private static final String PORTLET_1_NAME = "Portlet 1";
    private static final String PORTLET_1_UID = "com.portlet.MyClass.Portlet 1";
    private static final String PORTLET_0_UID = "com.portlet.MyClass0.Portlet 0";
    private static final String MODE_HELP = "HELP";
    private static final String MODE_VIEW = "VIEW";
    private static final String MODE_EDIT = "EDIT";

    private static int testPasses = 0;

    PersistencePlugin plugin;
    
	/**
	 * @param testName
	 */
	public TestRegistry(String testName)
	{
		super(testName);
		// TODO Auto-generated constructor stub
	}
    

//    public static Test suite()
//    {
//        // All methods starting with "test" will be executed in the test suite.
//		JetspeedTestSuite testSuite = new JetspeedTestSuite(TestRegistry.class);
//		registry = (PortletRegistryComponent) Jetspeed.getComponentManager().getComponent(PortletRegistryComponent.class);
//		PersistenceStoreContainer pContainer = (PersistenceStoreContainer) Jetspeed.getComponentManager().getComponent(PersistenceStoreContainer.class);
//		store = pContainer.getStoreForThread("jetspeed");
//        try
//        {
//            
//            List allPortletDefinitions = registry.getAllPortletDefinitions();
//            List allPortletApps = registry.getPortletApplications();
//            removeCollection(allPortletApps, store);
//            removeCollection(allPortletDefinitions, store);
//            store.getTransaction().commit();
//        }
//        catch (Exception e)
//        {
//			store.getTransaction().rollback();
//
//            throw new RuntimeException("Test suite failed: " + e.toString());
//        }
//        return testSuite;
//
//    }
//
//    protected static void removeCollection(Collection col, PersistenceStore store) throws LockingException, LockFailedException
//    {
//
//        Iterator itr = col.iterator();
//
//        while (itr.hasNext())
//        {
//
//            store.deletePersistent(itr.next());
//
//        }
//    }
//
//    public static final String APP_1_NAME = "RegistryTestPortlet";
//
//    private static PortletRegistryComponent registry;
//    private static PersistenceStore store;
//    /**
//     * @param testName
//     */
//    public TestRegistry(String testName)
//    {
//        super(testName);
//    }
//    /**
//     * @see org.apache.jetspeed.test.JetspeedTest#overrideProperties(org.apache.commons.configuration.Configuration)
//     */
//    //    public void overrideProperties(Configuration properties)
//    //    {
//    //        super.overrideProperties(properties);
//    //    }
//
//    protected void buildTestPortletApp()
//    {
//        try
//        {
//            
//            MutablePortletApplication pac = new PortletApplicationDefinitionImpl();
//                
//            MutableWebApplication wac = new  WebApplicationDefinitionImpl();
//                
//
//            pac.setName(APP_1_NAME);
//            pac.setDescription("This is a Registry Test Portlet.");
//            pac.setVersion("1.0");
//            
//            DublinCore dc = pac.getDublinCore();
//            dc.addTitle(Jetspeed.getDefaultLocale(), "Test title 1");
//            dc.addTitle(Jetspeed.getDefaultLocale(), "Test title 2");
//            dc.addTitle(Jetspeed.getDefaultLocale(), "Test title 3");
//            dc.addContributor(Jetspeed.getDefaultLocale(), "Contrib 1");
//            dc.addCoverage(Jetspeed.getDefaultLocale(), "Coverage 1");
//            dc.addCoverage(Jetspeed.getDefaultLocale(), "Coverage 2");
//            dc.addCreator(Jetspeed.getDefaultLocale(), "Creator 1");
//            dc.addDescription(Jetspeed.getDefaultLocale(), "Description 1");
//            dc.addFormat(Jetspeed.getDefaultLocale(), "Format 1");
//            dc.addIdentifier(Jetspeed.getDefaultLocale(), "Identifier 1");
//            dc.addLanguage(Jetspeed.getDefaultLocale(), "Language 1");
//            dc.addPublisher(Jetspeed.getDefaultLocale(), "Publisher 1");
//            dc.addRelation(Jetspeed.getDefaultLocale(), "Relation 1");
//            dc.addRight(Jetspeed.getDefaultLocale(), "Right 1");
//            dc.addSource(Jetspeed.getDefaultLocale(), "Source 1");
//            dc.addSubject(Jetspeed.getDefaultLocale(), "Subject 1");
//            dc.addType(Jetspeed.getDefaultLocale(), "Type 1");
//
//            wac.setContextRoot("/root");
//            wac.addDescription(Jetspeed.getDefaultLocale(), "This is an english desrcitpion");
//            wac.addDisplayName(Jetspeed.getDefaultLocale(), "This is an english display name");
//
//            pac.setWebApplicationDefinition(wac);
//
//            //add a portlet
//            PortletDefinitionComposite portlet0 = new PortletDefinitionImpl();
//                
//
//            portlet0.setClassName(PORTLET_0_CLASS);
//            portlet0.setPortletIdentifier(PORTLET_0_UID);
//            portlet0.setName(PORTLET_0_NAME);
//            pac.addPortletDefinition(portlet0);
//
//            plugin.makePersistent(pac);
//            //registry.registerPortletApplication(pac);
//            registry.commitTransaction();
//            //            plugin.invalidateObject(pac);
//            //			plugin.invalidateObject(portlet0);
//
//        }
//        catch (Throwable e)
//        {
//            e.printStackTrace();
//            try
//            {
//                registry.rollbackTransaction();
//            }
//            catch (TransactionStateException e1)
//            {
//                e1.printStackTrace();
//            }
//
//            throw new AssertionFailedError(e.toString());
//        }
//    }
//
//    public void testAddApplication() throws Throwable
//    {
//        // registry.clearCache();
//        // test that portlet application exists
//        // plugin.clearCache();
//        try
//        {
//            registry.beginTransaction();
//            MutablePortletApplication appExists = registry.getPortletApplication(APP_1_NAME);
//            assertNotNull(appExists);
//
//            // test that the web app exists
//            WebApplicationDefinition wad = appExists.getWebApplicationDefinition();
//            assertNotNull(wad);
//
//            assertNotNull(wad.getDescription(Jetspeed.getDefaultLocale()));
//            assertNotNull(wad.getDisplayName(Jetspeed.getDefaultLocale()));
//
//            PortletDefinition checkPd = appExists.getPortletDefinitionByName(PORTLET_0_NAME);
//
//            assertNotNull(appExists.getName() + " did not have a portlet named \"" + PORTLET_0_NAME + "\"", checkPd);
//
//            String checkName = checkPd.getName();
//
//            checkPd = null;
//
//            // registry.clearCache();
//
//            PortletDefinitionComposite pdc0 =
//                (PortletDefinitionComposite) registry.getPortletDefinitionByIndetifier(PORTLET_0_UID);
//
//            PortletDefinitionComposite pdc2 =
//                (PortletDefinitionComposite) registry.getPortletDefinitionByUniqueName(
//                    APP_1_NAME + "::" + PORTLET_0_NAME);
//
//            assertNotNull(
//                "Could not locate PortletDefinition with unique name \"" + APP_1_NAME + "::" + PORTLET_0_UID + "\"",
//                pdc2);
//
//            assertNotNull(pdc0);
//            assertEquals(checkName, pdc0.getName());
//            assertNotNull(
//                pdc0.getName() + " does not have a PortletApplicationDefinition.",
//                pdc0.getPortletApplicationDefinition());
//            registry.commitTransaction();
//        }
//        catch (Throwable e)
//        {
//            registry.rollbackTransaction();
//            throw e;
//        }
//
//    }
//    
//    public void testDublinCore() throws Throwable
//    {
//        // registry.clearCache();
//        // test that portlet application exists
//        // plugin.clearCache();
//        try
//        {
//            registry.beginTransaction();
//            MutablePortletApplication appExists = registry.getPortletApplication(APP_1_NAME);
//            assertNotNull(appExists);
//            
//            DublinCore dc = appExists.getDublinCore();
//            
//            assertEquals(dc.getTitles().size(), 3);
//            assertEquals(dc.getContributors().size(), 1);
//            assertEquals(dc.getCoverages().size(), 2);
//            assertEquals(dc.getCreators().size(), 1);
//            assertEquals(dc.getDescriptions().size(), 1);
//            assertEquals(dc.getFormats().size(), 1);
//            assertEquals(dc.getIdentifiers().size(), 1);
//            assertEquals(dc.getLanguages().size(), 1);
//            assertEquals(dc.getPublishers().size(), 1);
//            assertEquals(dc.getRelations().size(), 1);
//            assertEquals(dc.getRights().size(), 1);
//            assertEquals(dc.getSources().size(), 1);
//            assertEquals(dc.getSubjects().size(), 1);
//            assertEquals(dc.getTypes().size(), 1);
//            
//            
//            registry.commitTransaction();
//        }
//        catch(Throwable e)
//        {
//            registry.rollbackTransaction();
//            throw e;
//        }
//    }
//
//    public void testAddingPortlet() throws Throwable
//    {
//        try
//        {
//            // registry.clearCache();
//            registry.beginTransaction();
//            MutablePortletApplication app = registry.getPortletApplication(APP_1_NAME);
//
//            assertNotNull(app);
//
//            registry.writeLock(app);
//
//            //add a portlet
//            PortletDefinitionComposite portlet1 =
//                (PortletDefinitionComposite) registry.getNewObjectInstance(PortletDefinition.class, true);
//
//            portlet1.setClassName(PORTLET_1_CLASS);
//            portlet1.setName(PORTLET_1_NAME);
//            portlet1.setPortletIdentifier(PORTLET_1_UID);
//            app.addPortletDefinition(portlet1);
//
//            portlet1.addDisplayName(Jetspeed.getDefaultLocale(), "Portlet 1 Display Name");
//            portlet1.addDescription(Jetspeed.getDefaultLocale(), "Portlet 1 Description");
//
//            registry.commitTransaction();
//
//            // registry.clearCache();
//            //  plugin.invalidateObject(app);
//            // plugin.invalidateObject(portlet1);
//        }
//        catch (Throwable e)
//        {
//            e.printStackTrace();
//            registry.rollbackTransaction();
//            throw e;
//        }
//
//        try
//        {
//            registry.beginTransaction();
//            //		test that portlet application exists
//            MutablePortletApplication app = registry.getPortletApplication(APP_1_NAME);
//            assertNotNull(app);
//            int count = 0;
//            Iterator countItr = app.getPortletDefinitionList().iterator();
//            while (countItr.hasNext())
//            {
//                countItr.next();
//                count++;
//            }
//            assertEquals(2, count);
//
//            PortletDefinitionComposite portlet1 = (PortletDefinitionComposite) app.getPortletDefinitionByName(PORTLET_1_NAME);
//
//            PortletDefinitionComposite portlet1_2 = registry.getPortletDefinitionByIndetifier(PORTLET_1_UID);
//
//            assertNotNull(portlet1);
//
//            DisplayName displayName = portlet1.getDisplayName(Jetspeed.getDefaultLocale());
//
//            assertNotNull("DisplayName for portlet definition was null.", displayName);
//
//            System.out.println("Default local displayName  for Portlet 1 is " + displayName.getDisplayName());
//
//            Description desc = portlet1.getDescription(Jetspeed.getDefaultLocale());
//
//            assertNotNull("Description for portlet definition was null.", desc);
//
//            System.out.println("Default local description for Portlet 1 is " + desc.getDescription());
//
//            registry.commitTransaction();
//        }
//        catch (Throwable e)
//        {
//            e.printStackTrace();
//            registry.rollbackTransaction();
//            throw e;
//        }
//
//    }
//
//    public void testAddPortletInfo() throws Throwable
//    {
//        try
//        {
//            // begin a transaction
//            registry.beginTransaction();
//
//            MutablePortletApplication app = registry.getPortletApplication(APP_1_NAME);
//
//            assertNotNull(app);
//            // Mark portlet app for update
//            registry.updatePortletApplication(app);
//            PortletDefinitionComposite pdc = (PortletDefinitionComposite) app.getPortletDefinitionByName(PORTLET_0_NAME);
//            assertNotNull(pdc);
//
//            // add 2 parameters
//             ((ParameterSetCtrl) pdc.getInitParameterSet()).add("param 1", "value 1");
//            ((ParameterSetCtrl) pdc.getInitParameterSet()).add("param 2", "value 2");
//
//            // add a language
//            pdc.addLanguage(
//                registry.createLanguage(
//                    Locale.getDefault(),
//                    "Test Portlet 0",
//                    PORTLET_0_NAME,
//                    "This is Portlet 0",
//                    null));
//
//            //add content types
//            ContentTypeComposite html =
//                (ContentTypeComposite) registry.getNewObjectInstance(ContentType.class, true);
//            html.setContentType("html/text");
//            ContentTypeComposite wml = (ContentTypeComposite) registry.getNewObjectInstance(ContentType.class, true);
//            html.addPortletMode(new PortletMode(MODE_EDIT));
//            html.addPortletMode(new PortletMode(MODE_VIEW));
//            html.addPortletMode(new PortletMode(MODE_HELP));
//            wml.setContentType("wml");
//            wml.addPortletMode(new PortletMode(MODE_HELP));
//            wml.addPortletMode(new PortletMode(MODE_VIEW));
//            pdc.addContentType(html);
//            pdc.addContentType(wml);
//
//            PreferenceComposite pref = pdc.addPreference("preference 1", new String[] { "pref 1 values 1", "pref 1 value 2" });
//
//            pref.addDescription(Jetspeed.getDefaultLocale(), "Preference 1 description");
//
//            pdc.addDisplayName(Jetspeed.getDefaultLocale(), "Portlet 0 Display Name");
//            pdc.addDescription(Jetspeed.getDefaultLocale(), "Portlet 0 Description");
//
//            registry.commitTransaction();
//
//        }
//        catch (Throwable e)
//        {
//            try
//            {
//                registry.rollbackTransaction();
//            }
//            catch (TransactionStateException e1)
//            {
//                e1.printStackTrace();
//            }
//            throw e;
//        }
//
//      
//            doTestContentType();
//            doTestParameters();
//        
//        // doTestPreferences();
//    }
//
//    protected void doTestContentType() throws Throwable
//    {
//        try
//        {
//            registry.beginTransaction();
//            // registry.clearCache();
//            MutablePortletApplication app = registry.getPortletApplication(APP_1_NAME);
//            assertNotNull(app);
//            PortletDefinitionComposite pdc = (PortletDefinitionComposite) app.getPortletDefinitionByName(PORTLET_0_NAME);
//            assertNotNull(pdc);
//
//            ContentType html = pdc.getContentTypeSet().get("html/text");
//            assertNotNull(html);
//            if (html == null)
//                return;
//            System.out.println("Content Type : " + html.getContentType());
//            Iterator modes = html.getPortletModes();
//
//            boolean modeView = false;
//            boolean modeHelp = false;
//            boolean modeEdit = false;
//
//            while (modes.hasNext())
//            {
//                PortletMode mode = (PortletMode) modes.next();
//                if (!modeView)
//                {
//                    modeView = mode.toString().equals(PortletMode.VIEW.toString());
//                }
//
//                if (!modeHelp)
//                {
//                    modeHelp = mode.toString().equals(PortletMode.HELP.toString());
//                }
//
//                if (!modeEdit)
//                {
//                    modeEdit = mode.toString().equals(PortletMode.EDIT.toString());
//                }
//                System.out.println("   - Available Mode: " + mode);
//            }
//
//            assertTrue("All 3 portlet modes for \"html/text\" were not found.", (modeEdit && modeView & modeHelp));
//            registry.commitTransaction();
//        }
//        catch (Throwable e)
//        {
//            try
//            {
//                registry.rollbackTransaction();
//            }
//            catch (TransactionStateException e1)
//            {
//                e1.printStackTrace();
//            }
//            throw e;
//        }
//    }
//
//    protected void doTestParameters() throws Throwable
//    {
//        try
//        {
//            // registry.clearCache();
//            registry.beginTransaction();
//            System.out.println("Number of parameters in the DB " + registry.getPortletInitParameters(null).size());
//            MutablePortletApplication app = registry.getPortletApplication(APP_1_NAME);
//            assertNotNull(app);
//            PortletDefinitionComposite pdc = (PortletDefinitionComposite) app.getPortletDefinitionByName(PORTLET_0_NAME);
//            assertNotNull(pdc);
//
//            Iterator itr = pdc.getInitParameterSet().iterator();
//            int count = 0;
//            for (; itr.hasNext(); count++)
//                itr.next();
//
//            assertTrue(count == 2);
//            System.out.println("Testing cascading delete of parameters.  Removing Portlet Application now...");
//
//            registry.removeApplication(app);
//            registry.commitTransaction();
//
//            registry.beginTransaction();
//            int paramSize = registry.getPortletInitParameters(null).size();
//            assertEquals("Not all parameters were deleted.  " + paramSize + " left remain.", 0, paramSize);
//            registry.commitTransaction();
//        }
//        catch (Throwable e)
//        {
//            try
//            {
//                registry.rollbackTransaction();
//            }
//            catch (TransactionStateException e1)
//            {
//                e1.printStackTrace();
//            }
//            throw e;
//        }
//
//    }
//
//    public void testPreferences() throws Exception
//    {
//        try
//        {
//			registry.beginTransaction();
//            PortletDefinitionComposite pdc = null;
//            PortletDefinitionComposite pdc1 =
//                (PortletDefinitionComposite) registry.getPortletDefinitionByIndetifier(PORTLET_0_UID);
//
//            // registry.clearCache();
//
//            pdc =
//                (PortletDefinitionComposite) registry.getPortletDefinitionByUniqueName(
//                    APP_1_NAME + "::" + PORTLET_0_NAME);
//
//            assertNotNull("Could not locate PortletDefinition with unique name \"" + APP_1_NAME + "::" + PORTLET_0_UID + "\"", pdc);
//
//            
//
//            registry.writeLock(pdc);
//
//            // ((ParameterSetCtrl) pdc.getInitParameterSet()).add("param 1", "value 1");
//            // ((ParameterSetCtrl) pdc.getInitParameterSet()).add("param 2", "value 2");
//            PreferenceComposite pc =
//                (PreferenceComposite) registry.getNewObjectInstance(PreferenceComposite.DEFAULT_PREFERENCE, true);
//            pc.setName("preference 1");
//            pc.addValue("value 1");
//            pc.addValue("value 2");
//            pc.addDescription(Jetspeed.getDefaultLocale(), "Preference Description");
//            pdc.addPreference(pc);
//
//            registry.commitTransaction();
//			
//			registry.beginTransaction();
//            // plugin.invalidateObject(pdc);
//            // plugin.invalidateObject(pc);
//            pdc = (PortletDefinitionComposite) registry.getPortletDefinitionByIndetifier(PORTLET_0_UID);
//            assertNotNull("Portlet definition \"com.portlet.MyClass0.Portlet 0\" does not exist.", pdc);
//            assertNotNull("PreferenceSet for \"com.portlet.MyClass0.Portlet 0\" should not be null", pdc.getPreferenceSet());
//            Iterator itr = pdc.getPreferenceSet().iterator();
//            int count = 0;
//            while (itr.hasNext())
//            {
//                count++;
//                PreferenceComposite pref = (PreferenceComposite) itr.next();
//                System.out.println("Preference: " + pref.getName());
//
//                assertNotNull(pref.getDescription(Jetspeed.getDefaultLocale()));
//
//                System.out.println("Preference Description: " + pref.getDescription(Jetspeed.getDefaultLocale()));
//
//                Iterator prefValues = pref.getValues();
//                while (prefValues.hasNext())
//                {
//                    System.out.println("   -value:" + prefValues.next().toString());
//                }
//            }
//
//            PreferenceComposite pref1 = (PreferenceComposite) pdc.getPreferenceSet().get("preference 1");
//
//            assertNotNull("could not locate \"preference 1\" ", pref1);
//
//            Iterator valItr = pref1.getValues();
//            int valueCount = 0;
//            while (valItr.hasNext())
//            {
//                valueCount++;
//                valItr.next();
//            }
//
//            assertTrue("\"preference 1\" should have 2 values not " + valueCount, valueCount == 2);
//
//            assertTrue(count == 1);
//			registry.commitTransaction();
//        }
//        catch (Exception e)
//        {
//            registry.rollbackTransaction();
//            throw e;
//        }
//    }
//
//    public void testCascadeDelete()
//    {
//        clean();
//        List allPortletDefinitions = registry.getAllPortletDefinitions();
//        Iterator itr = allPortletDefinitions.iterator();
//        while (itr.hasNext())
//        {
//            PortletDefinition pd = (PortletDefinition) itr.next();
//            System.err.println("Rogue PortletDefinition: " + pd.getId() + ":" + pd.getName());
//        }
//        assertEquals("Cascade delete failed, some PortletDefinitions exist.", 0, allPortletDefinitions.size());
//    }
//
//    /**
//     * @see junit.framework.TestCase#tearDown()
//     */
//    public void tearDown() throws Exception
//    {
//        clean();
//        super.tearDown();
//    }
//
//    protected void clean()
//    {
//        // // registry.clearCache();
//
//        try
//        {
//            registry.beginTransaction();
//            Iterator itr = registry.getPortletApplications().iterator();
//
//            while (itr.hasNext())
//            {
//                MutablePortletApplication pac = (MutablePortletApplication) itr.next();
//
//                registry.removeApplication(pac);
//
//            }
//            registry.commitTransaction();
//
//        }
//        catch (Exception e)
//        {
//            try
//            {
//                registry.rollbackTransaction();
//            }
//            catch (TransactionStateException e1)
//            {
//                e1.printStackTrace();
//            }
//            System.out.println("Unable to tear down test.");
//            e.printStackTrace();
//        }
//
//        Iterator pitr = registry.getAllPortletDefinitions().iterator();
//        while (pitr.hasNext())
//        {
//            PortletDefinition pd = (PortletDefinition) pitr.next();
//            System.err.println("Test pass [" + testPasses + "]: Left over PortletDefinition: " + pd.getId() + ":" + pd.getName());
//        }
//
//    }
//
//    /**
//     * @see junit.framework.TestCase#setUp()
//     */
//    public void setUp() throws Exception
//    {
//        super.setUp();
//        
//        clean();
//        buildTestPortletApp();
//        testPasses++;
//    }
//
//    protected void clearExtent(Class clazz)
//    {
//        LookupCriteria c = plugin.newLookupCriteria();
//
//        Collection aColl = plugin.getCollectionByQuery(clazz, plugin.generateQuery(clazz, c));
//
//        Iterator anItr = aColl.iterator();
//
//        try
//        {
//            plugin.beginTransaction();
//            while (anItr.hasNext())
//            {
//                plugin.prepareForDelete(anItr.next());
//            }
//            plugin.commitTransaction();
//        }
//        catch (TransactionStateException e)
//        {
//            System.err.println("Unable to tear down test case!");
//            e.printStackTrace();
//        }
//
//    }



}
