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
package org.apache.jetspeed.components.portletregistry;

import junit.framework.TestCase;

/**
 * 
 * TestRegistry runs a suite tests creating, updating, retreiving and deleting
 * portlet information from the registry.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class TestRegistry extends TestCase
{
    /*private static final String PORTLET_0_CLASS = "com.portlet.MyClass0";
    private static final String PORTLET_0_NAME = "Portlet 0";
    private static final String PORTLET_1_CLASS = "com.portlet.MyClass";
    private static final String PORTLET_1_NAME = "Portlet 1";
    private static final String PORTLET_1_UID = "com.portlet.MyClass.Portlet 1";
    private static final String PORTLET_0_UID = "com.portlet.MyClass0.Portlet 0";
    private static final String MODE_HELP = "HELP";
    private static final String MODE_VIEW = "VIEW";
    private static final String MODE_EDIT = "EDIT";

    private static int testPasses = 0;
    
    private OMFactory omFactory;

    

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        JetspeedTestSuite testSuite = new JetspeedTestSuite(TestRegistry.class);
        
        PersistenceStore store = Jetspeed.getPersistenceStore();
        try
        {
            store.getTransaction().begin();
            List allPortletDefinitions = Jetspeed.getPortletRegistry().getAllPortletDefinitions();
            List allPortletApps = Jetspeed.getPortletRegistry().getPortletApplications();
            removeCollection(allPortletApps, store);
            removeCollection(allPortletDefinitions, store);
            store.getTransaction().commit();
        }
        catch (Exception e)
        {
            try
            {
				store.getTransaction().rollback();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }

            throw new RuntimeException("Test suite failed: " + e.toString());
        }
        return testSuite;

    }

    protected static void removeCollection(Collection col, PersistenceStore store) throws Exception
    {

        Iterator itr = col.iterator();

        while (itr.hasNext())
        {

			store.deletePersistent(itr.next());

        }
    }

    public static final String APP_1_NAME = "RegistryTestPortlet";

    *//**
     * @param testName
     *//*
    public TestRegistry(String testName)
    {
        super(testName);
    }
    *//**
     * @see org.apache.jetspeed.test.JetspeedTest#overrideProperties(org.apache.commons.configuration.Configuration)
     *//*
    //    public void overrideProperties(Configuration properties)
    //    {
    //        super.overrideProperties(properties);
    //    }

    protected void buildTestPortletApp()
    {
        try
        {
            
            MutablePortletApplication pac =
                (MutablePortletApplication) omFactory.newInstance(PortletApplicationDefinition.class);
            MutableWebApplication wac =
                (MutableWebApplication) omFactory.newInstance(WebApplicationDefinition.class);

            pac.setName(APP_1_NAME);
            pac.setDescription("This is a Registry Test Portlet.");
            pac.setVersion("1.0");

            wac.setContextRoot("/root");
            wac.addDescription(Jetspeed.getDefaultLocale(), "This is an english desrcitpion");
            wac.addDisplayName(Jetspeed.getDefaultLocale(), "This is an english display name");

            pac.setWebApplicationDefinition(wac);

            //add a portlet
            PortletDefinitionComposite portlet0 =
                (PortletDefinitionComposite) omFactory.newInstance(PortletDefinition.class);

            portlet0.setClassName(PORTLET_0_CLASS);
            portlet0.setPortletIdentifier(PORTLET_0_UID);
            portlet0.setName(PORTLET_0_NAME);
            pac.addPortletDefinition(portlet0);


			Jetspeed.getPortletRegistry().registerPortletApplication(pac);
			Jetspeed.getPersistenceStore().getTransaction().commit();
			
			// Makes sure that subsequent calls will use the DB
			invalidateAll(new Object[] {pac, portlet0, wac});

        }
        catch (Throwable e)
        {
            e.printStackTrace();
            try
            {
                Jetspeed.getPersistenceStore().getTransaction().rollback();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }

            throw new AssertionFailedError(e.toString());
        }
    }

    public void testAddApplication() throws Throwable
    {
     
        try
        {
            
            MutablePortletApplication appExists = Jetspeed.getPortletRegistry().getPortletApplication(APP_1_NAME);
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

            // Jetspeed.getPortletRegistry().clearCache();

            PortletDefinitionComposite pdc0 =
                (PortletDefinitionComposite) Jetspeed.getPortletRegistry().getPortletDefinitionByIndetifier(PORTLET_0_UID);

            PortletDefinitionComposite pdc2 =
                (PortletDefinitionComposite) Jetspeed.getPortletRegistry().getPortletDefinitionByUniqueName(
                    APP_1_NAME + "::" + PORTLET_0_NAME);

            assertNotNull(
                "Could not locate PortletDefinition with unique name \"" + APP_1_NAME + "::" + PORTLET_0_UID + "\"",
                pdc2);

            assertNotNull(pdc0);
            assertEquals(checkName, pdc0.getName());
            assertNotNull(
                pdc0.getName() + " does not have a PortletApplicationDefinition.",
                pdc0.getPortletApplicationDefinition());
            Jetspeed.getPersistenceStore().getTransaction().commit();
        }
        catch (Throwable e)
        {
            Jetspeed.getPersistenceStore().getTransaction().rollback();
            throw e;
        }

    }

    public void testAddingPortlet() throws Exception
    {
        try
        {
                        
            MutablePortletApplication app = Jetspeed.getPortletRegistry().getPortletApplication(APP_1_NAME);

            assertNotNull(app);
            
            //add a portlet
            PortletDefinitionComposite portlet1 =
                (PortletDefinitionComposite) omFactory.newInstance(PortletDefinition.class);

            portlet1.setClassName(PORTLET_1_CLASS);
            portlet1.setName(PORTLET_1_NAME);
            portlet1.setPortletIdentifier(PORTLET_1_UID);
            app.addPortletDefinition(portlet1);

            portlet1.addDisplayName(Jetspeed.getDefaultLocale(), "Portlet 1 Display Name");
            portlet1.addDescription(Jetspeed.getDefaultLocale(), "Portlet 1 Description");

            Jetspeed.getPersistenceStore().getTransaction().commit();
            
            // Invalidate all test objects within the cache
            invalidateAll(new Object[]{portlet1, app});		
      
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Jetspeed.getPersistenceStore().getTransaction().rollback();
            throw e;
        }

        try
        {
            
            //		test that portlet application exists
            MutablePortletApplication app = Jetspeed.getPortletRegistry().getPortletApplication(APP_1_NAME);
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

            PortletDefinitionComposite portlet1_2 = Jetspeed.getPortletRegistry().getPortletDefinitionByIndetifier(PORTLET_1_UID);

            assertNotNull(portlet1);
            
			Description desc = portlet1.getDescription(Jetspeed.getDefaultLocale());

			assertNotNull("Description for portlet definition was null.", desc);

            DisplayName displayName = portlet1.getDisplayName(Jetspeed.getDefaultLocale());

            assertNotNull("DisplayName for portlet definition was null.", displayName);

            System.out.println("Default local displayName  for Portlet 1 is " + displayName.getDisplayName());


            System.out.println("Default local description for Portlet 1 is " + desc.getDescription());

            Jetspeed.getPersistenceStore().getTransaction().commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Jetspeed.getPersistenceStore().getTransaction().rollback();
            throw e;
        }

    }

    public void testAddPortletInfo() throws Throwable
    {
        try
        {

            MutablePortletApplication app = Jetspeed.getPortletRegistry().getPortletApplication(APP_1_NAME);

            assertNotNull(app);
            
            PortletDefinitionComposite pdc = (PortletDefinitionComposite) app.getPortletDefinitionByName(PORTLET_0_NAME);
            assertNotNull(pdc);

            // add 2 parameters
             ((ParameterSetCtrl) pdc.getInitParameterSet()).add("param 1", "value 1");
            ((ParameterSetCtrl) pdc.getInitParameterSet()).add("param 2", "value 2");

            // add a language
            pdc.addLanguage(
                Jetspeed.getPortletRegistry().createLanguage(
                    Locale.getDefault(),
                    "Test Portlet 0",
                    PORTLET_0_NAME,
                    "This is Portlet 0",
                    null));

            //add content types
            ContentTypeComposite html =
                (ContentTypeComposite) omFactory.newInstance(ContentType.class);
            html.setContentType("html/text");
            ContentTypeComposite wml = (ContentTypeComposite) omFactory.newInstance(ContentType.class);
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

            Jetspeed.getPersistenceStore().getTransaction().commit();
            
            // Invalidate cached objects
            invalidateAll(new Object[] {app, pdc, html, wml, pref});

        }
        catch (Throwable e)
        {
            try
            {
                Jetspeed.getPersistenceStore().getTransaction().rollback();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
            throw e;
        }

      
            doTestContentType();
            doTestParameters();
        
        
    }

    protected void doTestContentType() throws Throwable
    {
        try
        {
        
            MutablePortletApplication app = Jetspeed.getPortletRegistry().getPortletApplication(APP_1_NAME);
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
            Jetspeed.getPersistenceStore().getTransaction().commit();
        }
        catch (Throwable e)
        {
            try
            {
                Jetspeed.getPersistenceStore().getTransaction().rollback();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
            throw e;
        }
    }

    protected void doTestParameters() throws Throwable
    {
        try
        {         
        	Jetspeed.getPersistenceStore().getTransaction().begin();
            System.out.println("Number of parameters in the DB " + Jetspeed.getPersistenceStore().getExtent(PortletInitParameterImpl.class).size());
            Jetspeed.getPersistenceStore().getTransaction().commit();
            MutablePortletApplication app = Jetspeed.getPortletRegistry().getPortletApplication(APP_1_NAME);
            assertNotNull(app);
            PortletDefinitionComposite pdc = (PortletDefinitionComposite) app.getPortletDefinitionByName(PORTLET_0_NAME);
            assertNotNull(pdc);

            Iterator itr = pdc.getInitParameterSet().iterator();
            int count = 0;
            for (; itr.hasNext(); count++)
                itr.next();

            assertTrue(count == 2);
            System.out.println("Testing cascading delete of parameters.  Removing Portlet Application now...");

            Jetspeed.getPortletRegistry().removeApplication(app);
            Jetspeed.getPersistenceStore().getTransaction().commit();

            Jetspeed.getPersistenceStore().getTransaction().begin();
            int paramSize = Jetspeed.getPersistenceStore().getExtent(PortletInitParameterImpl.class).size();
            assertEquals("Not all parameters were deleted.  " + paramSize + " left remain.", 0, paramSize);
            Jetspeed.getPersistenceStore().getTransaction().commit();
        }
        catch (Throwable e)
        {
            try
            {
                Jetspeed.getPersistenceStore().getTransaction().rollback();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
            throw e;
        }

    }

    public void testPreferences() throws Exception
    {
        try
        {
			
            PortletDefinitionComposite pdc = null;
            PortletDefinitionComposite pdc1 =
                (PortletDefinitionComposite) Jetspeed.getPortletRegistry().getPortletDefinitionByIndetifier(PORTLET_0_UID);

            
            pdc =
                (PortletDefinitionComposite) Jetspeed.getPortletRegistry().getPortletDefinitionByUniqueName(
                    APP_1_NAME + "::" + PORTLET_0_NAME);

            assertNotNull("Could not locate PortletDefinition with unique name \"" + APP_1_NAME + "::" + PORTLET_0_UID + "\"", pdc);

            PreferenceComposite pc =
                (PreferenceComposite) OMHelper.getOMFactory("pluto.om").newInstance(PreferenceComposite.DEFAULT_PREFERENCE);
            pc.setName("preference 1");
            pc.addValue("value 1");
            pc.addValue("value 2");
            pc.addDescription(Jetspeed.getDefaultLocale(), "Preference Description");
            pdc.addPreference(pc);

            Jetspeed.getPersistenceStore().getTransaction().commit();
            
            invalidateAll(new Object[]{pdc, pc});
			
			Jetspeed.getPersistenceStore().getTransaction().begin();
          
            pdc = (PortletDefinitionComposite) Jetspeed.getPortletRegistry().getPortletDefinitionByIndetifier(PORTLET_0_UID);
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
			Jetspeed.getPersistenceStore().getTransaction().commit();
        }
        catch (Exception e)
        {
            Jetspeed.getPersistenceStore().getTransaction().rollback();
            throw e;
        }
    }

    public void testCascadeDelete()
    {
        clean();
        List allPortletDefinitions = Jetspeed.getPortletRegistry().getAllPortletDefinitions();
        Iterator itr = allPortletDefinitions.iterator();
        while (itr.hasNext())
        {
            PortletDefinition pd = (PortletDefinition) itr.next();
            System.err.println("Rogue PortletDefinition: " + pd.getId() + ":" + pd.getName());
        }
        assertEquals("Cascade delete failed, some PortletDefinitions exist.", 0, allPortletDefinitions.size());
    }

    *//**
     * @see junit.framework.TestCase#tearDown()
     *//*
    public void tearDown() throws Exception
    {
        clean();
        super.tearDown();
    }

    protected void clean()
    {
        // // Jetspeed.getPortletRegistry().clearCache();

        try
        {            
            Iterator itr = Jetspeed.getPortletRegistry().getPortletApplications().iterator();

            while (itr.hasNext())
            {
                MutablePortletApplication pac = (MutablePortletApplication) itr.next();

                Jetspeed.getPortletRegistry().removeApplication(pac);

            }
            Jetspeed.getPersistenceStore().getTransaction().commit();

        }
        catch (Exception e)
        {
            try
            {
                Jetspeed.getPersistenceStore().getTransaction().rollback();
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
            System.out.println("Unable to tear down test.");
            e.printStackTrace();
        }

        Iterator pitr = Jetspeed.getPortletRegistry().getAllPortletDefinitions().iterator();
        while (pitr.hasNext())
        {
            PortletDefinition pd = (PortletDefinition) pitr.next();
            System.err.println("Test pass [" + testPasses + "]: Left over PortletDefinition: " + pd.getId() + ":" + pd.getName());
        }

    }

    *//**
     * @see junit.framework.TestCase#setUp()
     *//*
    public void setUp() throws Exception
    {
        super.setUp();
		omFactory = OMHelper.getOMFactory("pluto.om");       
        clean();
        buildTestPortletApp();
        testPasses++;
    }

    protected void clearExtent(Class clazz)
    {
    	
		PersistenceStore store = Jetspeed.getPersistenceStore();
		
        Filter c = store.newFilter();

        Collection aColl = store.getCollectionByQuery(store.newQuery(clazz, c));

        Iterator anItr = aColl.iterator();

        try
        {
			store.getTransaction().begin();
            while (anItr.hasNext())
            {
				store.deletePersistent(anItr.next());
            }
			store.getTransaction().commit();
        }
        catch (Exception e)
        {
            System.err.println("Unable to tear down test case!");
            e.printStackTrace();
        }

    }
    
    private void invalidateAll(Object [] objs) throws LockFailedException
    {
		Jetspeed.getPersistenceStore().getTransaction().begin();				
    	for(int i=0; i<objs.length; i++)
    	{
    		Object obj = Jetspeed.getPersistenceStore().getObjectByIdentity(objs[i]);
    		
    	}
		Jetspeed.getPersistenceStore().getTransaction().commit();
    }*/
    

}
