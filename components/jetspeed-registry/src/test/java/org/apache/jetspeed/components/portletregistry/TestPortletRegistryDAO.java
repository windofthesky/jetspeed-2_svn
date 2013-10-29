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

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.persistence.store.LockFailedException;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.engine.MockJetspeedEngine;
import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.DublinCore;
import org.apache.jetspeed.om.portlet.EventDefinition;
import org.apache.jetspeed.om.portlet.EventDefinitionReference;
import org.apache.jetspeed.om.portlet.Filter;
import org.apache.jetspeed.om.portlet.FilterMapping;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.Listener;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.jetspeed.om.portlet.PublicRenderParameter;
import org.apache.jetspeed.om.portlet.SecurityConstraint;
import org.apache.jetspeed.om.portlet.Supports;
import org.apache.jetspeed.om.portlet.UserAttribute;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.om.portlet.impl.DublinCoreImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.pluto.container.om.portlet.UserDataConstraint;

import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

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
        this.portletRegistry = scm.lookupComponent("portletRegistry");

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
    
    public void testCloningOfPortletDefinition() throws Exception
    {
        final String appName = "App_1";
        final String sourcePortletName = "Portlet 1";
        final String clonedPortletName = "ClonedPortlet 1";

        PortletApplication app = portletRegistry.getPortletApplication(appName);
        assertNotNull("Portlet application, " + appName + ", is not found.", app);
        
        PortletDefinition sourcePortlet = app.getPortlet(sourcePortletName);
        assertNotNull("Portlet definition, " + sourcePortletName + ", is not found.", sourcePortlet);
        
        PortletDefinition clonedPortlet = app.getPortlet(clonedPortletName);
        assertNull("A portlet definition with cloned portlet name, " + clonedPortletName + ", shouldn't be there.", 
                   clonedPortlet);
        
        try
        {
            Collection<LocalizedField> sourceFields = sourcePortlet.getMetadata().getFields();
            portletRegistry.clonePortletDefinition(sourcePortlet, clonedPortletName);
            clonedPortlet = app.getClone(clonedPortletName);
            assertNotNull("Cloned portlet is not found after invoking cloning method.", clonedPortlet);

            assertTrue("Portlet should've been a clone", clonedPortlet.isClone());
            assertEquals("Portlet clone parent not matching parent app", clonedPortlet.getCloneParent(), app.getName());
            
            Collection<LocalizedField> clonedFields = clonedPortlet.getMetadata().getFields();
            
            assertEquals("The metadata fields count is not equals.", sourceFields.size(), clonedFields.size());
            
            for (LocalizedField sourceField : sourceFields)
            {
                List<LocalizedField> foundClonedFields = findLocalizedFieldsByNameAndLocale(clonedFields, sourceField.getName(), sourceField.getLocale());
                assertFalse("There's no matching metadata field in cloned portlet definition.", foundClonedFields.isEmpty());
                assertNotNull("The metadata field values from the source metadata not found.", findLocalizedFieldByValue(foundClonedFields, sourceField.getValue()));
            }
            
            for (LocalizedField clonedField : clonedFields)
            {
                clonedField.setValue("Cloned value of " + clonedField.getValue());
            }
            
            for (LocalizedField sourceField : sourceFields)
            {
                List<LocalizedField> foundClonedFields = findLocalizedFieldsByNameAndLocale(clonedFields, sourceField.getName(), sourceField.getLocale());
                assertFalse("There's no matching metadata field in cloned portlet definition.", foundClonedFields.isEmpty());
                assertNull("The metadata field values from the source metadata should not be found because the ones of cloned stuff have been changed.", 
                           findLocalizedFieldByValue(foundClonedFields, sourceField.getValue()));
            }
        }
        finally
        {
            if (clonedPortlet != null)
            {
                List<PortletDefinition> clones = app.getClones();
                assertEquals("count of clones off", 1, clones.size());
                clones.remove(clonedPortlet);
                portletRegistry.updatePortletApplication(app);
                PortletApplication testApp = portletRegistry.getPortletApplication("App_1");
                clones = testApp.getClones();
                assertEquals("count of clones should be zero", 0, clones.size());
            }
        }
        // test restoring clones
        try
        {
            createApplicationAndPortlet("cloneTest", "/cloneTest", "SourcePortlet", "Title Source Portlet", false);
            PortletApplication testApp = portletRegistry.getPortletApplication("cloneTest");
            assertNotNull("test app is null", testApp);
            assertEquals("test App name not what expected", "cloneTest", testApp.getName());
            PortletDefinition srcPortlet = testApp.getPortlet("SourcePortlet");
            assertNotNull("src portlet is null", srcPortlet);
            assertEquals("src portlet title is not what expected", "Title Source Portlet", srcPortlet.getPortletInfo().getTitle() );

            PortletDefinition myClone = portletRegistry.clonePortletDefinition(srcPortlet, "restorePortlet");
            assertNotNull("myClone portlet is null", myClone);
            assertEquals("myClone portlet name is not what expected", myClone.getPortletName(), "restorePortlet");
            assertEquals("expecting one clone ", 1,  testApp.getClones().size());

            PortletDefinition myClone2 = portletRegistry.clonePortletDefinition(srcPortlet, "restorePortlet2");
            assertNotNull("myClone2 portlet is null", myClone2);
            assertEquals("myClone2 portlet name is not what expected", myClone2.getPortletName(), "restorePortlet2");
            assertEquals("expecting two clones ", 2,  testApp.getClones().size());
            
            portletRegistry.removeApplication(testApp);
            testApp = portletRegistry.getPortletApplication("cloneTest");
            assertNull("test app should be null", testApp);

            createApplicationAndPortlet("cloneTest", "/cloneTest", "SourcePortlet", "Title Source Portlet", false);
            PortletApplication recreated = portletRegistry.getPortletApplication("cloneTest");
            assertNotNull("recreated test app is null", recreated);
            int count = portletRegistry.restoreClones(recreated);
            assertEquals("Expected to restore one clone", 2, count);
            PortletDefinition cpd = recreated.getClone("restorePortlet");
            assertEquals("Expected clone to be named 'restorePortlet' ", cpd.getPortletName(), "restorePortlet");
            PortletDefinition cpd2 = recreated.getClone("restorePortlet2");
            assertEquals("Expected clone to be named 'restorePortlet2' ", cpd2.getPortletName(), "restorePortlet2");
        }
        finally
        {
            PortletApplication cleanup = portletRegistry.getPortletApplication("cloneTest");
            assertNotNull("cleanup app is null", cleanup);
            assertEquals("expecting one clone ", 2, cleanup.getClones().size());
            portletRegistry.removeClone(cleanup.getClone("restorePortlet"));
            assertEquals("expecting one clone ", 1, cleanup.getClones().size());
            portletRegistry.removeAllClones(cleanup);
            assertEquals("expecting zero clones ", 0, cleanup.getClones().size());
            portletRegistry.removeApplication(cleanup);
        }
    }
    
    private static List<LocalizedField> findLocalizedFieldsByNameAndLocale(final Collection<LocalizedField> sourceFields, final String name, final Locale locale)
    {
        List<LocalizedField> localizedFields = new LinkedList<LocalizedField>();
        
        for (LocalizedField sourceField : sourceFields)
        {
            if (name.equals(sourceField.getName()) && locale.equals(sourceField.getLocale()))
            {
                localizedFields.add(sourceField);
            }
        }
        
        return localizedFields;
    }
    
    private static LocalizedField findLocalizedFieldByValue(final Collection<LocalizedField> sourceFields, String value)
    {
        for (LocalizedField sourceField : sourceFields)
        {
            if (value.equals(sourceField.getValue()))
            {
                return sourceField;
            }
        }

        return null;
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
        createApplicationAndPortlet("App_1", "/app1", "Portlet 1", "Portlet 1", true);
    }

    private void createApplicationAndPortlet(String appName, String appContextPath, String portletName, String title, boolean create20Data)
            throws RegistryException, LockFailedException
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
        app.setName(appName);
        app.setContextPath(appContextPath);

        app.addDescription(Locale.FRENCH.toString()).setDescription("Description: Le fromage est dans mon pantalon!");
        app.addDisplayName(Locale.FRENCH.toString()).setDisplayName("Display Name: Le fromage est dans mon pantalon!");
        
        UserAttributeRef uaRef = app.addUserAttributeRef("user-name-family");
        uaRef.setNameLink("user.name.family");

        UserAttribute ua = app.addUserAttribute("user.name.family");
        ua.addDescription(lang).setDescription("User Last Name");

        app.addJetspeedServiceReference("PortletEntityAccessComponent");
        app.addJetspeedServiceReference("PortletRegistryComponent");
        
        addDublinCore(app.getMetadata());

        PortletDefinition portlet = app.addPortlet(portletName);
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
        language.setTitle(title);
        language.setShortTitle(title);

        Supports supports = portlet.addSupports("html/text");
        supports.addPortletMode(MODE_EDIT);
        supports.addPortletMode(MODE_VIEW);
        supports.addPortletMode(MODE_HELP);
        
        supports = portlet.addSupports("wml");
        supports.addPortletMode(MODE_HELP);
        supports.addPortletMode(MODE_VIEW);

        if (create20Data)
        {
            build20TestData(app, portlet);
        }
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
        
        verifyPortlet20Data(app, portlet);        
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
    
    public static void build20TestData(PortletApplication app, PortletDefinition portlet)
    throws RegistryException
    {
        // Portlet 2.0 Support        
        app.setDefaultNamespace("http:apache.org/events");
        portlet.setCacheScope("private");
        portlet.setExpirationCache(-1);
        EventDefinition event = app.addEventDefinition("plainOldEvent");
        event.setValueType("java.lang.String");
        Description en1 = event.addDescription("en");
        en1.setDescription("The Plain Old Event");
        Description fr1 = event.addDescription("fr");
        fr1.setDescription("Le Vieux Ordinaire �v�nement");        
        
        QName q2 = new QName("http:portals.apache.org/events", "qualifiedEvent");
        EventDefinition event2 = app.addEventDefinition(q2);
        event2.setValueType("java.lang.String");       

        QName q3 = new QName("http:portals.apache.org/events", "prefixedEvent", "x");
        EventDefinition event3 = app.addEventDefinition(q3);
        event3.setValueType("java.lang.String");       
        event3.addAlias(new QName("local-1"));
        event3.addAlias(new QName("http:2portals.apache.org/events", "local-2"));
        event3.addAlias(new QName("http:3portals.apache.org/events", "local-3", "p"));
        // test dupes
        event3.addAlias(new QName("local-1"));            
        event3.addAlias(new QName("http:2portals.apache.org/events", "local-2"));            
        event3.addAlias(new QName("http:3portals.apache.org/events", "local-3", "p"));            
        assertEquals(event3.getAliases().size(), 3);

        portlet.addSupportedProcessingEvent("plainOldEvent");
        portlet.addSupportedProcessingEvent(q3);
        portlet.addSupportedPublishingEvent("local-1");
        portlet.addSupportedPublishingEvent(q2);
        
        portlet.addSupportedPublicRenderParameter("city");
        portlet.addSupportedPublicRenderParameter("zipcode");
        
        ContainerRuntimeOption opt1 = portlet.addContainerRuntimeOption("PortletOption1");
        opt1.addValue("p-value-1");
        opt1.addValue("p-value-2");
        opt1.addValue("p-value-3");
        ContainerRuntimeOption opt2 = portlet.addContainerRuntimeOption("PortletOption2");
        opt2.addValue("p-value-4");
        opt2.addValue("p-value-5");

        ContainerRuntimeOption opt3 = app.addContainerRuntimeOption("AppOption1");
        opt3.addValue("a-value-1");
        opt3.addValue("a-value-2");
        opt3.addValue("a-value-3");
        ContainerRuntimeOption opt4 = app.addContainerRuntimeOption("AppOption2");        
        opt4.addValue("a-value-4");
        
        PublicRenderParameter prp1 = app.addPublicRenderParameter("prp1", "prp1-id");
        Description d1 = prp1.addDescription("en");
        d1.setDescription("dog");
        Description d2 = prp1.addDescription("fr");
        d2.setDescription("chien");
        prp1.addAlias(new QName("alias-1"));
        
        Filter filter = app.addFilter("filter-1");
        filter.setFilterClass("org.apache.filter.FilterOne");
        Description d3 = filter.addDescription("en");
        d3.setDescription("My Filter 1");
        Description d4 = filter.addDescription("fr");
        d4.setDescription("Mon Filtre 1");
        DisplayName dn3 = filter.addDisplayName("en");
        dn3.setDisplayName("This is my filter 1");
        DisplayName dn4 = filter.addDisplayName("fr");
        dn4.setDisplayName("Ceci est mon filtre 1");
        InitParam ip3 = filter.addInitParam("filter init param 1");
        ip3.setParamValue("value for filter init param 1");
        InitParam ip4 = filter.addInitParam("filter init param 2");
        ip4.setParamValue("value for filter init param 2");
        filter.addLifecycle("lifecycle-1");
        filter.addLifecycle("lifecycle-2");
        filter.addLifecycle("lifecycle-3");
        
        FilterMapping mapping1 = app.addFilterMapping("filter-1");
        mapping1.addPortletName("Portlet 1");
        mapping1.addPortletName("Portlet 2");
        mapping1.addPortletName("Portlet 3");
        FilterMapping mapping2 = app.addFilterMapping("filter-2");
        mapping2.addPortletName("Portlet 1");
        mapping2.addPortletName("Portlet 2");
        mapping2.addPortletName("Portlet 3");
        
        Listener listener1 = app.addListener("org.apache.listener.ListenerOne");
        Description d5 = listener1.addDescription("en");
        d5.setDescription("Listen to me once");
        Description d6 = listener1.addDescription("fr");
        d6.setDescription("�coutez moi une fois");
        DisplayName dn5 = listener1.addDisplayName("en");
        dn5.setDisplayName("Listen to me twice");
        DisplayName dn6 = listener1.addDisplayName("fr");
        dn6.setDisplayName("�coutez moi deux fois");
        Listener listener2 = app.addListener("org.apache.listener.ListenerTwo");
        Description d7 = listener2.addDescription("en");
        d7.setDescription("Don't listen to me");
        Description d8 = listener2.addDescription("fr");
        d8.setDescription("N'�coutez pas moi");
        DisplayName dn7 = listener2.addDisplayName("en");
        dn7.setDisplayName("Listen!");
        DisplayName dn8 = listener2.addDisplayName("fr");
        dn8.setDisplayName("�coutez!");
        
        SecurityConstraint sc = app.addSecurityConstraint(UserDataConstraint.INTEGRAL);
        DisplayName scdn1 = sc.addDisplayName("en");
        scdn1.setDisplayName("Integral Security Transport");
        DisplayName scdn2 = sc.addDisplayName("fr");
        scdn2.setDisplayName("Transport Int�gral de S�curit�");
        sc.addPortletName("PortletOne");
        sc.addPortletName("PortletTwo");
        sc.addPortletName("PortletThree");
        Description des1 = sc.getUserDataConstraint().addDescription("en");
        des1.setDescription("This is the Integral Security Transport");
        Description des2 = sc.getUserDataConstraint().addDescription("fr");
        des2.setDescription("Ceci est le Transport Int�gral de S�curit�");
        SecurityConstraint sc2 = app.addSecurityConstraint(UserDataConstraint.CONFIDENTIAL);
        scdn1 = sc2.addDisplayName("en");
        scdn1.setDisplayName("Confidential Security Transport");
        scdn2 = sc2.addDisplayName("fr");
        scdn2.setDisplayName("Transport Confidentiel de S�curit�");
        sc2.addPortletName("PortletA");
        sc2.addPortletName("PortletB");
        des1 = sc2.getUserDataConstraint().addDescription("en");
        des1.setDescription("This is the Confidential Security Transport");
        des2 = sc2.getUserDataConstraint().addDescription("fr");
        des2.setDescription("Ceci est le Transport Confidentiel de S�curit�");
    }

    public static void verifyPortlet20Data(PortletApplication app, PortletDefinition portlet)
    throws Exception
    {   
        // Portlet 2.0 Support
        assertEquals(app.getDefaultNamespace(), "http:apache.org/events");
        assertEquals(portlet.getCacheScope(), "private");
        assertEquals(portlet.getExpirationCache(), -1);
        
        List<EventDefinition> events = app.getEventDefinitions();
        assertNotNull(events);
        assertTrue(events.size() == 3);
        EventDefinition event1 = events.get(0);
        assertNotNull(event1);
        assertEquals(event1.getName(), "plainOldEvent");
        assertEquals(event1.getValueType(), "java.lang.String");
        QName q = event1.getQName();
        // if the event definition is registered without QName, then the default namespace of app will be used.
        assertEquals(q.getNamespaceURI(), "http:apache.org/events");
        assertEquals(q.getPrefix(), "");
        Description en = event1.getDescription(new Locale("en"));
        assertEquals(en.getDescription(), "The Plain Old Event");
        Description fr = event1.getDescription(new Locale("fr"));
        assertEquals(fr.getDescription(), "Le Vieux Ordinaire �v�nement");        
        
        EventDefinition event2 = events.get(1);
        assertNotNull(event2);
        QName qname = event2.getQName();
        assertEquals(qname.getNamespaceURI(), "http:portals.apache.org/events");
        assertEquals(qname.getLocalPart(), "qualifiedEvent");
        assertEquals(event2.getValueType(), "java.lang.String");        
        assertEquals(qname.getPrefix(), "");

        EventDefinition event3 = events.get(2);
        assertNotNull(event3);
        QName qname3 = event3.getQName();
        assertEquals(qname3.getNamespaceURI(), "http:portals.apache.org/events");
        assertEquals(qname3.getLocalPart(), "prefixedEvent");
        assertEquals(qname3.getPrefix(), "x");
        assertEquals(event3.getValueType(), "java.lang.String");        
        
        QName alias1 = event3.getAliases().get(0);
        assertEquals(alias1.getLocalPart(), "local-1");
        QName alias2 = event3.getAliases().get(1);
        assertEquals(alias2.getLocalPart(), "local-2");
        assertEquals(alias2.getNamespaceURI(), "http:2portals.apache.org/events");
        QName alias3 = event3.getAliases().get(2);
        assertEquals(alias3.getLocalPart(), "local-3");
        assertEquals(alias3.getNamespaceURI(), "http:3portals.apache.org/events");
        assertEquals(alias3.getPrefix(), "p");        

        List<EventDefinitionReference> refs = portlet.getSupportedProcessingEvents();
        assertEquals(refs.size(), 2);
        EventDefinitionReference ref1 = refs.get(0);
        assertEquals(ref1.getName(), "plainOldEvent");
        EventDefinitionReference ref2 = refs.get(1);
        QName ref2QName = ref2.getQName();
        assertEquals(ref2QName, new QName("http:portals.apache.org/events", "prefixedEvent", "x"));

        List<EventDefinitionReference> pubRefs = portlet.getSupportedPublishingEvents();
        assertEquals(pubRefs.size(), 2);
        EventDefinitionReference pubRef1 = pubRefs.get(0);
        assertEquals(pubRef1.getName(), "local-1");
        EventDefinitionReference pubRef2 = pubRefs.get(1);
        QName pubRef2QName = pubRef2.getQName();
        assertEquals(pubRef2QName, new QName("http:portals.apache.org/events", "qualifiedEvent"));

        List<String> supportedRenderParams = portlet.getSupportedPublicRenderParameters();
        assertEquals(supportedRenderParams.size(), 2);
        String p1 = supportedRenderParams.get(0);
        assertEquals(p1, "city");
        String p2 = supportedRenderParams.get(1);
        assertEquals(p2, "zipcode");

        List<ContainerRuntimeOption> portletOptions = portlet.getContainerRuntimeOptions();
        assertEquals(portletOptions.size(), 2);
        ContainerRuntimeOption opt1 = portlet.getContainerRuntimeOption("PortletOption1");
        assertEquals(opt1.getName(), "PortletOption1");
        assertEquals(opt1.getValues().size(), 3);
        assertEquals(opt1.getValues().get(0), "p-value-1");
        assertEquals(opt1.getValues().get(1), "p-value-2");
        assertEquals(opt1.getValues().get(2), "p-value-3");
        ContainerRuntimeOption opt2 = portlet.getContainerRuntimeOption("PortletOption2");
        assertEquals(opt2.getName(), "PortletOption2");
        assertEquals(opt2.getValues().size(), 2);
        assertEquals(opt2.getValues().get(0), "p-value-4");
        assertEquals(opt2.getValues().get(1), "p-value-5");
        
        List<ContainerRuntimeOption> appOptions = app.getContainerRuntimeOptions();
        assertEquals(appOptions.size(), 2);
        ContainerRuntimeOption opt3 = app.getContainerRuntimeOption("AppOption1");
        assertEquals(opt3.getName(), "AppOption1");
        assertEquals(opt3.getValues().size(), 3);
        assertEquals(opt3.getValues().get(0), "a-value-1");
        assertEquals(opt3.getValues().get(1), "a-value-2");
        assertEquals(opt3.getValues().get(2), "a-value-3");
        ContainerRuntimeOption opt4 = app.getContainerRuntimeOption("AppOption2");
        assertEquals(opt4.getName(), "AppOption2");
        assertEquals(opt4.getValues().size(), 1);
        assertEquals(opt4.getValues().get(0), "a-value-4");
        
        PublicRenderParameter x = app.getPublicRenderParameter("prp1-id");
        assertNotNull(x);
        assertEquals(x.getName(), "prp1");
        List<PublicRenderParameter> xs = app.getPublicRenderParameters();
        assertEquals(xs.size(), 1);
        Description d1 = x.getDescription(new Locale("en"));
        assertEquals(d1.getDescription(), "dog");
        Description d2 = x.getDescription(new Locale("fr"));
        assertEquals(d2.getDescription(), "chien");
        List<QName> aliases = x.getAliases();
        assertEquals(aliases.size(), 1);
        assertEquals(aliases.get(0).getLocalPart(), "alias-1");

        List<Filter> filters = app.getFilters();
        assertEquals(filters.size(), 1);        
        Filter filter = filters.get(0);
        assertEquals(filter.getFilterName(), "filter-1");
        assertEquals(filter.getFilterClass(), "org.apache.filter.FilterOne");
        List<Description> descs = filter.getDescriptions();
        assertEquals(descs.size(), 2);        
        Description d3 = descs.get(0);
        assertEquals(d3.getLang(), "en");
        assertEquals(d3.getDescription(), "My Filter 1");
        Description d4 = descs.get(1);
        assertEquals(d4.getLang(), "fr");
        assertEquals(d4.getDescription(), "Mon Filtre 1");
        List<DisplayName> dnames = filter.getDisplayNames();        
        assertEquals(dnames.size(), 2);        
        DisplayName dn3 = dnames.get(0);
        assertEquals(dn3.getLang(), "en");
        assertEquals(dn3.getDisplayName(), "This is my filter 1");
        DisplayName dn4 = dnames.get(1);        
        assertEquals(dn4.getLang(), "fr");
        assertEquals(dn4.getDisplayName(), "Ceci est mon filtre 1");
        List<InitParam> iparams = filter.getInitParams();        
        assertEquals(iparams.size(), 2);        
        InitParam ip3 = iparams.get(0);
        assertEquals(ip3.getParamName(),"filter init param 1");
        assertEquals(ip3.getParamValue(), "value for filter init param 1");
        InitParam ip4 = iparams.get(1);
        assertEquals(ip4.getParamName(), "filter init param 2");
        assertEquals(ip4.getParamValue(), "value for filter init param 2");
        List<String> lcycles = filter.getLifecycles();        
        assertEquals(lcycles.size(), 3);        
        assertEquals(lcycles.get(0), "lifecycle-1");
        assertEquals(lcycles.get(1), "lifecycle-2");
        assertEquals(lcycles.get(2), "lifecycle-3");        
        
        List<Listener> listeners = app.getListeners();
        assertEquals(listeners.size(), 2);
        Listener listener1 = listeners.get(0);
        assertEquals(listener1.getListenerClass(), "org.apache.listener.ListenerOne");
        descs = listener1.getDescriptions();
        assertEquals(descs.size(), 2);
        Description d5 = descs.get(0);
        assertEquals(d5.getLang(), "en");
        assertEquals(d5.getDescription(), "Listen to me once");
        Description d6 = descs.get(1);
        assertEquals(d6.getLang(), "fr");
        assertEquals(d6.getDescription(), "�coutez moi une fois");
        dnames = listener1.getDisplayNames();
        assertEquals(dnames.size(), 2);
        DisplayName dn5 = dnames.get(0);
        assertEquals(dn5.getLang(), "en");
        assertEquals(dn5.getDisplayName(), "Listen to me twice");
        DisplayName dn6 = dnames.get(1);
        assertEquals(dn6.getLang(), "fr");
        assertEquals(dn6.getDisplayName(), "�coutez moi deux fois");

        Listener listener2 = listeners.get(1);
        assertEquals(listener2.getListenerClass(), "org.apache.listener.ListenerTwo");
        descs = listener2.getDescriptions();
        assertEquals(descs.size(), 2);
        d5 = descs.get(0);
        assertEquals(d5.getLang(), "en");
        assertEquals(d5.getDescription(), "Don't listen to me");
        d6 = descs.get(1);
        assertEquals(d6.getLang(), "fr");
        assertEquals(d6.getDescription(), "N'�coutez pas moi");
        dnames = listener2.getDisplayNames();
        assertEquals(dnames.size(), 2);
        dn5 = dnames.get(0);
        assertEquals(dn5.getLang(), "en");
        assertEquals(dn5.getDisplayName(), "Listen!");
        dn6 = dnames.get(1);
        assertEquals(dn6.getLang(), "fr");
        assertEquals(dn6.getDisplayName(), "�coutez!");
        
        List<SecurityConstraint> scs = app.getSecurityConstraints();
        assertEquals(scs.size(), 2);
        SecurityConstraint sc1 = scs.get(0);
        assertEquals(sc1.getUserDataConstraint().getTransportGuarantee(), UserDataConstraint.INTEGRAL);
        DisplayName scdn1 = sc1.getDisplayName(new Locale("en"));
        assertEquals(scdn1.getLang(), "en");
        assertEquals(scdn1.getDisplayName(), "Integral Security Transport");
        DisplayName scdn2 = sc1.getDisplayName(new Locale("fr"));
        assertEquals(scdn2.getDisplayName(), "Transport Int�gral de S�curit�");
        assertEquals(sc1.getPortletNames().get(0), "PortletOne");
        assertEquals(sc1.getPortletNames().get(1), "PortletTwo");
        assertEquals(sc1.getPortletNames().get(2), "PortletThree");
        Description des1 = sc1.getUserDataConstraint().getDescription(new Locale("en"));
        assertEquals(des1.getLang(), "en");
        assertEquals(des1.getDescription(), "This is the Integral Security Transport");
        Description des2 = sc1.getUserDataConstraint().getDescription(new Locale("fr"));
        assertEquals(des2.getLang(), "fr");
        assertEquals(des2.getDescription(), "Ceci est le Transport Int�gral de S�curit�");

        SecurityConstraint sc2 = scs.get(1);
        assertEquals(sc2.getUserDataConstraint().getTransportGuarantee(), UserDataConstraint.CONFIDENTIAL);
        scdn1 = sc2.getDisplayName(new Locale("en"));
        assertEquals(scdn1.getLang(), "en");
        assertEquals(scdn1.getDisplayName(), "Confidential Security Transport");
        scdn2 = sc2.getDisplayName(new Locale("fr"));
        assertEquals(scdn2.getDisplayName(), "Transport Confidentiel de S�curit�");
        assertEquals(sc2.getPortletNames().get(0), "PortletA");
        assertEquals(sc2.getPortletNames().get(1), "PortletB");
        des1 = sc2.getUserDataConstraint().getDescription(new Locale("en"));
        assertEquals(des1.getLang(), "en");
        assertEquals(des1.getDescription(), "This is the Confidential Security Transport");
        des2 = sc2.getUserDataConstraint().getDescription(new Locale("fr"));
        assertEquals(des2.getLang(), "fr");
        assertEquals(des2.getDescription(), "Ceci est le Transport Confidentiel de S�curit�");        
    }

}
