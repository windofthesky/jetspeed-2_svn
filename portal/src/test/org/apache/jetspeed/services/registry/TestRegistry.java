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
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.om.common.portlet.ContentTypeComposite;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;

import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.persistence.TransactionStateException;

import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;

import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.ParameterSetCtrl;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.servlet.WebApplicationDefinition;
import org.apache.jetspeed.registry.JetspeedPortletRegistry;
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

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        JetspeedTestSuite testSuite = new JetspeedTestSuite(TestRegistry.class);
        List allPortletDefinitions = JetspeedPortletRegistry.getAllPortletDefinitions();
        List allPortletApps = JetspeedPortletRegistry.getPortletApplications();
        removeCollection(allPortletApps);
        removeCollection(allPortletDefinitions);
        return testSuite;

    }

    protected static void removeCollection(Collection col)
    {
        PersistenceService ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
        PersistencePlugin plugin = ps.getDefaultPersistencePlugin();
        Iterator itr = col.iterator();

        while (itr.hasNext())
        {

            try
            {
                plugin.beginTransaction();
                plugin.prepareForDelete(itr.next());
                plugin.commitTransaction();
            }
            catch (Exception e)
            {
                try
                {
                    plugin.rollbackTransaction();
                }
                catch (TransactionStateException e1)
                {
                    e1.printStackTrace();
                }
                System.out.println("Suite initialization failed");
                e.printStackTrace();
            }
        }
    }

    public static final String APP_1_NAME = "RegistryTestPortlet";

    /**
     * @param testName
     */
    public TestRegistry(String testName)
    {
        super(testName);
    }
    /**
     * @see org.apache.jetspeed.test.JetspeedTest#overrideProperties(org.apache.commons.configuration.Configuration)
     */
    //    public void overrideProperties(Configuration properties)
    //    {
    //        super.overrideProperties(properties);
    //    }

    protected void buildTestPortletApp()
    {
        try
        {
            JetspeedPortletRegistry.beginTransaction();
            MutablePortletApplication pac =
                (MutablePortletApplication) JetspeedPortletRegistry.getNewObjectInstance(PortletApplicationDefinition.class, true);
            MutableWebApplication wac =
                (MutableWebApplication) JetspeedPortletRegistry.getNewObjectInstance(WebApplicationDefinition.class, true);

            pac.setName(APP_1_NAME);
            pac.setDescription("This is a Registry Test Portlet.");
            pac.setVersion("1.0");

            wac.setContextRoot("/root");
            wac.addDescription(Jetspeed.getDefaultLocale(), "This is an english desrcitpion");
            wac.addDisplayName(Jetspeed.getDefaultLocale(), "This is an english display name");

            pac.setWebApplicationDefinition(wac);

            //add a portlet
            PortletDefinitionComposite portlet0 =
                (PortletDefinitionComposite) JetspeedPortletRegistry.getNewObjectInstance(PortletDefinition.class, true);

            portlet0.setClassName(PORTLET_0_CLASS);
            portlet0.setPortletIdentifier(PORTLET_0_UID);
            portlet0.setName(PORTLET_0_NAME);
            pac.addPortletDefinition(portlet0);

            plugin.makePersistent(pac);
            //JetspeedPortletRegistry.registerPortletApplication(pac);
            JetspeedPortletRegistry.commitTransaction();
            //            plugin.invalidateObject(pac);
            //			plugin.invalidateObject(portlet0);

        }
        catch (Throwable e)
        {
            e.printStackTrace();
            try
            {
                JetspeedPortletRegistry.rollbackTransaction();
            }
            catch (TransactionStateException e1)
            {
                e1.printStackTrace();
            }

            throw new AssertionFailedError(e.toString());
        }
    }

    public void testAddApplication() throws Throwable 
    {
        // JetspeedPortletRegistry.clearCache();
        // test that portlet application exists
        // plugin.clearCache();
        try
        {
            JetspeedPortletRegistry.beginTransaction();			
            MutablePortletApplication appExists = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);
            assertNotNull(appExists);

            // test that the web app exists
            WebApplicationDefinition wad = appExists.getWebApplicationDefinition();
            assertNotNull(wad);

            assertNotNull(wad.getDescription(Jetspeed.getDefaultLocale()));
            assertNotNull(wad.getDisplayName(Jetspeed.getDefaultLocale()));

            PortletDefinition checkPd = appExists.getPortletDefinitionByName(PORTLET_0_NAME);

            assertNotNull(appExists.getName() + " did not have a portlet named \"" + PORTLET_0_NAME + "\"", checkPd);

            String checkName = checkPd.getName();

            checkPd = null;

            // JetspeedPortletRegistry.clearCache();

            PortletDefinitionComposite pdc0 =
                (PortletDefinitionComposite) JetspeedPortletRegistry.getPortletDefinitionByIndetifier(PORTLET_0_UID);

            PortletDefinitionComposite pdc2 =
                (PortletDefinitionComposite) JetspeedPortletRegistry.getPortletDefinitionByUniqueName(
                    APP_1_NAME + "::" + PORTLET_0_NAME);

            assertNotNull(
                "Could not locate PortletDefinition with unique name \"" + APP_1_NAME + "::" + PORTLET_0_UID + "\"",
                pdc2);

            assertNotNull(pdc0);
            assertEquals(checkName, pdc0.getName());
            assertNotNull(
                pdc0.getName() + " does not have a PortletApplicationDefinition.",
                pdc0.getPortletApplicationDefinition());
            JetspeedPortletRegistry.commitTransaction();
        }
        catch (Throwable e)
        {
            JetspeedPortletRegistry.rollbackTransaction();
            throw e;
        }

    }

    public void testAddingPortlet() throws Throwable 
    {
        try
        {
            // JetspeedPortletRegistry.clearCache();

            MutablePortletApplication app = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);

            assertNotNull(app);
            JetspeedPortletRegistry.beginTransaction();
           JetspeedPortletRegistry.writeLock(app);

            //add a portlet
            PortletDefinitionComposite portlet1 =
                (PortletDefinitionComposite) JetspeedPortletRegistry.getNewObjectInstance(PortletDefinition.class, true);

            portlet1.setClassName(PORTLET_1_CLASS);
            portlet1.setName(PORTLET_1_NAME);
            portlet1.setPortletIdentifier(PORTLET_1_UID);
            app.addPortletDefinition(portlet1);

            portlet1.addDisplayName(Jetspeed.getDefaultLocale(), "Portlet 1 Display Name");
            portlet1.addDescription(Jetspeed.getDefaultLocale(), "Portlet 1 Description");

            JetspeedPortletRegistry.commitTransaction();

            // JetspeedPortletRegistry.clearCache();
            //  plugin.invalidateObject(app);
            // plugin.invalidateObject(portlet1);
        }
        catch (Throwable e)
        {
            e.printStackTrace();            
           JetspeedPortletRegistry.rollbackTransaction(); 
            throw e;
        }

        //		test that portlet application exists
        MutablePortletApplication app = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);
        assertNotNull(app);
        int count = 0;
        Iterator countItr = app.getPortletDefinitionList().iterator();
        while (countItr.hasNext())
        {
            countItr.next();
            count++;
        }
        assertEquals(2, count);

        PortletDefinitionComposite portlet1 = (PortletDefinitionComposite) app.getPortletDefinitionByName(PORTLET_1_NAME);

        PortletDefinitionComposite portlet1_2 = JetspeedPortletRegistry.getPortletDefinitionByIndetifier(PORTLET_1_UID);

        assertNotNull(portlet1);

        DisplayName displayName = portlet1.getDisplayName(Jetspeed.getDefaultLocale());

        assertNotNull("DisplayName for portlet definition was null.", displayName);

        System.out.println("Default local displayName  for Portlet 1 is " + displayName.getDisplayName());

        Description desc = portlet1.getDescription(Jetspeed.getDefaultLocale());

        assertNotNull("Description for portlet definition was null.", desc);

        System.out.println("Default local description for Portlet 1 is " + desc.getDescription());

    }

    public void testAddPortletInfo() throws Throwable
    {
        try
        {
            // begin a transaction
            JetspeedPortletRegistry.beginTransaction();

            MutablePortletApplication app = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);

            assertNotNull(app);
            // Mark portlet app for update
            JetspeedPortletRegistry.updatePortletApplication(app);
            PortletDefinitionComposite pdc = (PortletDefinitionComposite) app.getPortletDefinitionByName(PORTLET_0_NAME);
            assertNotNull(pdc);

            // add 2 parameters
             ((ParameterSetCtrl) pdc.getInitParameterSet()).add("param 1", "value 1");
            ((ParameterSetCtrl) pdc.getInitParameterSet()).add("param 2", "value 2");

            // add a language
            pdc.addLanguage(
                JetspeedPortletRegistry.createLanguage(
                    Locale.getDefault(),
                    "Test Portlet 0",
                    PORTLET_0_NAME,
                    "This is Portlet 0",
                    null));

            //add content types
            ContentTypeComposite html =
                (ContentTypeComposite) JetspeedPortletRegistry.getNewObjectInstance(ContentType.class, true);
            html.setContentType("html/text");
            ContentTypeComposite wml = (ContentTypeComposite) JetspeedPortletRegistry.getNewObjectInstance(ContentType.class, true);
            html.addPortletMode(new PortletMode(MODE_EDIT));
            html.addPortletMode(new PortletMode(MODE_VIEW));
            html.addPortletMode(new PortletMode(MODE_HELP));
            wml.setContentType("wml");
            wml.addPortletMode(new PortletMode(MODE_HELP));
            wml.addPortletMode(new PortletMode(MODE_VIEW));
            pdc.addContentType(html);
            pdc.addContentType(wml);

            PreferenceComposite pref = pdc.addPreference("preference 1", new String[] { "pref 1 values 1", "pref 1 value 2" });

            pref.addDescription(Jetspeed.getDefaultLocale(), "Preference 1 description");

            pdc.addDisplayName(Jetspeed.getDefaultLocale(), "Portlet 0 Display Name");
            pdc.addDescription(Jetspeed.getDefaultLocale(), "Portlet 0 Description");

            JetspeedPortletRegistry.commitTransaction();

        }
        catch (Throwable e)
        {            
            try
            {
                JetspeedPortletRegistry.rollbackTransaction();
            }
            catch (TransactionStateException e1)
            {
                e1.printStackTrace();
            }
            throw e;
        }

        // JetspeedPortletRegistry.clearCache();
        doTestContentType();
        doTestParameters();
        // doTestPreferences();
    }

    protected void doTestContentType()
    {
        // JetspeedPortletRegistry.clearCache();
        MutablePortletApplication app = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);
        assertNotNull(app);
        PortletDefinitionComposite pdc = (PortletDefinitionComposite) app.getPortletDefinitionByName(PORTLET_0_NAME);
        assertNotNull(pdc);

        ContentType html = pdc.getContentTypeSet().get("html/text");
        assertNotNull(html);
        if (html == null)
            return;
        System.out.println("Content Type : " + html.getContentType());
        Iterator modes = html.getPortletModes();

        boolean modeView = false;
        boolean modeHelp = false;
        boolean modeEdit = false;

        while (modes.hasNext())
        {
            PortletMode mode = (PortletMode) modes.next();
            if (!modeView)
            {
                modeView = mode.toString().equals(PortletMode.VIEW.toString());
            }

            if (!modeHelp)
            {
                modeHelp = mode.toString().equals(PortletMode.HELP.toString());
            }

            if (!modeEdit)
            {
                modeEdit = mode.toString().equals(PortletMode.EDIT.toString());
            }
            System.out.println("   - Available Mode: " + mode);
        }

        assertTrue("All 3 portlet modes for \"html/text\" were not found.", (modeEdit && modeView & modeHelp));
    }

    protected void doTestParameters() throws Exception
    {
        // JetspeedPortletRegistry.clearCache();
        System.out.println("Number of parameters in the DB " + JetspeedPortletRegistry.getPortletInitParameters(null).size());
        MutablePortletApplication app = JetspeedPortletRegistry.getPortletApplication(APP_1_NAME);
        assertNotNull(app);
        PortletDefinitionComposite pdc = (PortletDefinitionComposite) app.getPortletDefinitionByName(PORTLET_0_NAME);
        assertNotNull(pdc);

        Iterator itr = pdc.getInitParameterSet().iterator();
        int count = 0;
        for (; itr.hasNext(); count++)
            itr.next();

        assertTrue(count == 2);
        System.out.println("Testing cascading delete of parameters.  Removing Portlet Application now...");
        JetspeedPortletRegistry.beginTransaction();
        JetspeedPortletRegistry.removeApplication(app);
        JetspeedPortletRegistry.commitTransaction();

        int paramSize = JetspeedPortletRegistry.getPortletInitParameters(null).size();
        assertEquals("Not all parameters were deleted.  " + paramSize + " left remain.", 0, paramSize);

    }

    public void testPreferences() throws Exception
    {
        try
        {

            PortletDefinitionComposite pdc = null;
            PortletDefinitionComposite pdc1 =
                (PortletDefinitionComposite) JetspeedPortletRegistry.getPortletDefinitionByIndetifier(PORTLET_0_UID);

            // JetspeedPortletRegistry.clearCache();

            pdc =
                (PortletDefinitionComposite) JetspeedPortletRegistry.getPortletDefinitionByUniqueName(
                    APP_1_NAME + "::" + PORTLET_0_NAME);

            assertNotNull("Could not locate PortletDefinition with unique name \"" + APP_1_NAME + "::" + PORTLET_0_UID + "\"", pdc);

            JetspeedPortletRegistry.beginTransaction();

            JetspeedPortletRegistry.writeLock(pdc);

            // ((ParameterSetCtrl) pdc.getInitParameterSet()).add("param 1", "value 1");
            // ((ParameterSetCtrl) pdc.getInitParameterSet()).add("param 2", "value 2");
            PreferenceComposite pc =
                (PreferenceComposite) JetspeedPortletRegistry.getNewObjectInstance(PreferenceComposite.DEFAULT_PREFERENCE, true);
            pc.setName("preference 1");
            pc.addValue("value 1");
            pc.addValue("value 2");
            pc.addDescription(Jetspeed.getDefaultLocale(), "Preference Description");
            pdc.addPreference(pc);

            JetspeedPortletRegistry.commitTransaction();

            // plugin.invalidateObject(pdc);
            // plugin.invalidateObject(pc);
            pdc = (PortletDefinitionComposite) JetspeedPortletRegistry.getPortletDefinitionByIndetifier(PORTLET_0_UID);
            assertNotNull("Portlet definition \"com.portlet.MyClass0.Portlet 0\" does not exist.", pdc);
            assertNotNull("PreferenceSet for \"com.portlet.MyClass0.Portlet 0\" should not be null", pdc.getPreferenceSet());
            Iterator itr = pdc.getPreferenceSet().iterator();
            int count = 0;
            while (itr.hasNext())
            {
                count++;
                PreferenceComposite pref = (PreferenceComposite) itr.next();
                System.out.println("Preference: " + pref.getName());

                assertNotNull(pref.getDescription(Jetspeed.getDefaultLocale()));

                System.out.println("Preference Description: " + pref.getDescription(Jetspeed.getDefaultLocale()));

                Iterator prefValues = pref.getValues();
                while (prefValues.hasNext())
                {
                    System.out.println("   -value:" + prefValues.next().toString());
                }
            }

            PreferenceComposite pref1 = (PreferenceComposite) pdc.getPreferenceSet().get("preference 1");

            assertNotNull("could not locate \"preference 1\" ", pref1);

            Iterator valItr = pref1.getValues();
            int valueCount = 0;
            while (valItr.hasNext())
            {
                valueCount++;
                valItr.next();
            }

            assertTrue("\"preference 1\" should have 2 values not " + valueCount, valueCount == 2);

            assertTrue(count == 1);
        }
        catch (Exception e)
        {
            JetspeedPortletRegistry.rollbackTransaction();
            throw e;
        }
    }

    public void testCascadeDelete()
    {
        clean();
        List allPortletDefinitions = JetspeedPortletRegistry.getAllPortletDefinitions();
        Iterator itr = allPortletDefinitions.iterator();
        while (itr.hasNext())
        {
            PortletDefinition pd = (PortletDefinition) itr.next();
            System.err.println("Rogue PortletDefinition: " + pd.getId() + ":" + pd.getName());
        }
        assertEquals("Cascade delete failed, some PortletDefinitions exist.", 0, allPortletDefinitions.size());
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        clean();
        super.tearDown();
    }

    protected void clean()
    {
        // // JetspeedPortletRegistry.clearCache();

        Iterator itr = JetspeedPortletRegistry.getPortletApplications().iterator();
        try
        {
            JetspeedPortletRegistry.beginTransaction();
            while (itr.hasNext())
            {
                MutablePortletApplication pac = (MutablePortletApplication) itr.next();

                JetspeedPortletRegistry.removeApplication(pac);

            }
			JetspeedPortletRegistry.commitTransaction();

        }
        catch (Exception e)
        {
            try
            {
                JetspeedPortletRegistry.rollbackTransaction();
            }
            catch (TransactionStateException e1)
            {
                e1.printStackTrace();
            }
            System.out.println("Unable to tear down test.");
            e.printStackTrace();
        }

        Iterator pitr = JetspeedPortletRegistry.getAllPortletDefinitions().iterator();
        while (pitr.hasNext())
        {
            PortletDefinition pd = (PortletDefinition) pitr.next();
            System.err.println("Test pass [" + testPasses + "]: Left over PortletDefinition: " + pd.getId() + ":" + pd.getName());
        }

    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        PersistenceService ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
        plugin = ps.getDefaultPersistencePlugin();
        clean();
        buildTestPortletApp();
        testPasses++;
    }

    protected void clearExtent(Class clazz)
    {
        LookupCriteria c = plugin.newLookupCriteria();

        Collection aColl = plugin.getCollectionByQuery(clazz, plugin.generateQuery(clazz, c));

        Iterator anItr = aColl.iterator();

        try
        {
            plugin.beginTransaction();
            while (anItr.hasNext())
            {
                plugin.prepareForDelete(anItr.next());
            }
            plugin.commitTransaction();
        }
        catch (TransactionStateException e)
        {
            System.err.println("Unable to tear down test case!");
            e.printStackTrace();
        }

    }

}
