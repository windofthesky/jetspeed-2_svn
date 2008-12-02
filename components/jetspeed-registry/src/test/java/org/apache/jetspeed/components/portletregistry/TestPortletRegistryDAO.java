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
package org.apache.jetspeed.components.portletregistry;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.persistence.store.LockFailedException;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.engine.MockJetspeedEngine;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.Supports;
import org.apache.jetspeed.om.portlet.UserAttribute;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.jetspeed.om.portlet.DublinCore;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.om.portlet.impl.DublinCoreImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.util.JetspeedLocale;

/**
 * <p>
 * TestPortletRegistryDAO
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class TestPortletRegistryDAO extends DatasourceEnabledSpringTestCase
{
    public static final String APP_1_NAME = "RegistryTestPortlet";

    protected static final String MODE_EDIT = "EDIT";

    protected static final String MODE_HELP = "HELP";

    protected static final String MODE_VIEW = "VIEW";

    protected static final String PORTLET_0_CLASS = "com.portlet.MyClass0";

    protected static final String PORTLET_0_NAME = "Portlet 0";

    protected static final String PORTLET_0_UID = "com.portlet.MyClass0.Portlet 0";

    protected static final String PORTLET_1_CLASS = "com.portlet.MyClass";

    protected static final String PORTLET_1_NAME = "Portlet 1";

    protected static final String PORTLET_1_UID = "com.portlet.MyClass.Portlet 1";

    protected PortletRegistry portletRegistry;

    private static MockJetspeedEngine mockEngine = new MockJetspeedEngine();

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        mockEngine.setComponentManager(scm);
        Jetspeed.setEngine(mockEngine);
        this.portletRegistry = (PortletRegistry) scm.getComponent("portletRegistry");

        buildTestData();

    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        Iterator itr = portletRegistry.getPortletApplications().iterator();
        while (itr.hasNext())
        {
            portletRegistry.removeApplication((PortletApplication) itr.next());
        }
        Jetspeed.setEngine(null);
        super.tearDown();
    }

    public void test1() throws Exception
    {
        assertNotNull(portletRegistry);
    }

    public void testData() throws Exception
    {
        verifyData(false);
    }

    private void addDublinCore(GenericMetadata metadata)
    {
        DublinCore dc = new DublinCoreImpl(metadata);
        dc.addTitle(JetspeedLocale.getDefaultLocale(), "Test title 1");
        dc.addTitle(JetspeedLocale.getDefaultLocale(), "Test title 2");
        dc.addTitle(JetspeedLocale.getDefaultLocale(), "Test title 3");
        dc.addContributor(JetspeedLocale.getDefaultLocale(), "Contrib 1");
        dc.addCoverage(JetspeedLocale.getDefaultLocale(), "Coverage 1");
        dc.addCoverage(JetspeedLocale.getDefaultLocale(), "Coverage 2");
        dc.addCreator(JetspeedLocale.getDefaultLocale(), "Creator 1");
        dc.addDescription(JetspeedLocale.getDefaultLocale(), "Description 1");
        dc.addFormat(JetspeedLocale.getDefaultLocale(), "Format 1");
        dc.addIdentifier(JetspeedLocale.getDefaultLocale(), "Identifier 1");
        dc.addLanguage(JetspeedLocale.getDefaultLocale(), "Language 1");
        dc.addPublisher(JetspeedLocale.getDefaultLocale(), "Publisher 1");
        dc.addRelation(JetspeedLocale.getDefaultLocale(), "Relation 1");
        dc.addRight(JetspeedLocale.getDefaultLocale(), "Right 1");
        dc.addSource(JetspeedLocale.getDefaultLocale(), "Source 1");
        dc.addSubject(JetspeedLocale.getDefaultLocale(), "Subject 1");
        dc.addType(JetspeedLocale.getDefaultLocale(), "Type 1");
    }

    /**
     * <p>
     * buildTestData
     * </p>
     * 
     * @throws RegistryException
     * @throws LockFailedException
     */
    private void buildTestData() throws RegistryException, LockFailedException
    {
        String lang = Locale.getDefault().toString();
        
        // start clean
        Iterator itr = portletRegistry.getPortletApplications().iterator();
        while (itr.hasNext())
        {
            portletRegistry.removeApplication((PortletApplication) itr.next());
        }

        // Create an Application and a Web app

        PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
        app.setName("App_1");
        app.setContextRoot("/app1");

        app.addDescription(Locale.FRENCH.toString()).setDescription("Description: Le fromage est dans mon pantalon!");
        app.addDisplayName(Locale.FRENCH.toString()).setDisplayName("Display Name: Le fromage est dans mon pantalon!");
        
        UserAttributeRef uaRef = app.addUserAttributeRef("user-name-family");
        uaRef.setNameLink("user.name.family");

        UserAttribute ua = app.addUserAttribute("user.name.family");
        ua.addDescription(lang).setDescription("User Last Name");

        app.addJetspeedServiceReference("PortletEntityAccessComponent");
        app.addJetspeedServiceReference("PortletRegistryComponent");
        
        addDublinCore(app.getMetadata());

        PortletDefinition portlet = app.addPortlet("Portlet 1");
        portlet.setPortletClass("org.apache.Portlet");
        portlet.addDescription(lang).setDescription("Portlet description.");
        portlet.addDisplayName(lang).setDisplayName("Portlet display Name.");
        
        InitParam initParam = portlet.addInitParam("testparam");
        initParam.setParamValue("test value");
        initParam.addDescription(lang).setDescription("This is a test portlet parameter");

        addDublinCore(portlet.getMetadata());

        Preferences prefs = portlet.getPortletPreferences();
        Preference pref = prefs.addPreference("preference 1");
        pref.addValue("value 1");
        pref.addValue("value 2");
        
        Language language = portlet.addLanguage(Locale.getDefault());
        language.setTitle("Portlet 1");
        language.setShortTitle("Portlet 1");

        Supports supports = portlet.addSupports("html/text");
        supports.addPortletMode(MODE_EDIT);
        supports.addPortletMode(MODE_VIEW);
        supports.addPortletMode(MODE_HELP);
        
        supports = portlet.addSupports("wml");
        supports.addPortletMode(MODE_HELP);
        supports.addPortletMode(MODE_VIEW);
        
        portletRegistry.updatePortletApplication(app);
    }

    protected void verifyData(boolean afterUpdates) throws Exception
    {
        PortletApplication app;
        PortletDefinition portlet;

        // Now makes sure everthing got persisted

        app = null;

        app = portletRegistry.getPortletApplication("App_1");

        assertNotNull(app);

        portlet = (PortletDefinitionImpl) app.getPortlet("Portlet 1");

        assertNotNull("Failed to reteive portlet application", app);

        validateDublinCore(app.getMetadata());

        Collection services = app.getJetspeedServices();
        assertNotNull("jetspeed services is null", services);
        System.out.println("services is " + services);

        assertNotNull("Failed to reteive portlet application via registry", portletRegistry
                .getPortletApplication("App_1"));
        assertNotNull("Portlet was not saved along with the portlet app.", app.getPortlet("Portlet 1"));
        if (!afterUpdates)
        {
            assertTrue("\"user.name.family\" user attribute was not found.", app.getUserAttributes().size() == 1);
        }
        else
        {
            assertTrue("\"user.name.family\" and user.pets user attributes were not found.", app.getUserAttributes()
                    .size() == 2);

        }

        portlet = portletRegistry.getPortletDefinitionByUniqueName("App_1::Portlet 1");

        assertNotNull("Portlet could not be retreived by unique name.", portlet);

        validateDublinCore(portlet.getMetadata());

        assertNotNull("Portlet Application was not set in the portlet defintion.", portlet
                .getApplication());
        assertNotNull("French description was not materialized for the app.", app.getDescription(Locale.FRENCH));
        assertNotNull("French display name was not materialized for the app.", app.getDisplayName(Locale.FRENCH));
        assertNotNull("description was not materialized for the portlet.", portlet.getDescription(Locale.getDefault()));
        assertNotNull("display name was not materialized for the portlet.", portlet.getDisplayName(Locale.getDefault()));
        assertNotNull("\"testparam\" portlet parameter was not saved", portlet.getInitParam("testparam"));
        // TODO: fix the following line.
        //assertNotNull("\"preference 1\" was not found.", portlet.getPortletPreferences().getPortletPreference("preference 1"));
        assertNotNull("Language information not found for Portlet 1", portlet.getLanguage(Locale.getDefault()));
        assertNotNull("Content Type html not found.", portlet.getSupports("html/text"));
        assertNotNull("Content Type wml not found.", portlet.getSupports("wml"));
        // TODO: fix the following lines.
        //Iterator itr = portlet.getPortletPreferences().getPortletPreference("preference 1").getValues().iterator();
        //int valueCount = 0;
        //while (itr.hasNext())
        //{
        //    itr.next();
        //    valueCount++;
        //}
        //assertEquals("\"preference 1\" did not have 2 values.", 2, valueCount);

        app = portletRegistry.getPortletApplication("App_1");

        app.addDescription(Locale.getDefault().toString()).setDescription("Web app description");

        app = portletRegistry.getPortletApplication("App_1");

        assertNotNull("App did NOT persist its description", app.getDescription(Locale.FRENCH));

    }

    protected void validateDublinCore(GenericMetadata metadata)
    {
        DublinCore dc = new DublinCoreImpl(metadata);
        assertEquals(dc.getTitles().size(), 3);
        assertEquals(dc.getContributors().size(), 1);
        assertEquals(dc.getCoverages().size(), 2);
        assertEquals(dc.getCreators().size(), 1);
        assertEquals(dc.getDescriptions().size(), 1);
        assertEquals(dc.getFormats().size(), 1);
        assertEquals(dc.getIdentifiers().size(), 1);
        assertEquals(dc.getLanguages().size(), 1);
        assertEquals(dc.getPublishers().size(), 1);
        assertEquals(dc.getRelations().size(), 1);
        assertEquals(dc.getRights().size(), 1);
        assertEquals(dc.getSources().size(), 1);
        assertEquals(dc.getSubjects().size(), 1);
        assertEquals(dc.getTypes().size(), 1);
    }

    protected String getBeanDefinitionFilterCategories()
    {
        return "registry,transaction,cache,jdbcDS";
    }
    
    protected String[] getConfigurations()
    {
        return new String[]
        { "transaction.xml", "registry-test.xml", "cache-test.xml", "static-bean-references.xml" };
    }
}
