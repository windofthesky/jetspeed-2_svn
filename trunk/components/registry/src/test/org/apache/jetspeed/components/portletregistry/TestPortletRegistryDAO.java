/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.portletregistry;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.portlet.PortletMode;

import org.apache.jetspeed.components.persistence.store.LockFailedException;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.om.common.DublinCore;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.JetspeedServiceReference;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.UserAttributeRef;
import org.apache.jetspeed.om.common.portlet.ContentTypeComposite;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.impl.DublinCoreImpl;
import org.apache.jetspeed.om.impl.JetspeedServiceReferenceImpl;
import org.apache.jetspeed.om.impl.UserAttributeImpl;
import org.apache.jetspeed.om.impl.UserAttributeRefImpl;
import org.apache.jetspeed.om.portlet.impl.ContentTypeImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.pluto.om.common.PreferenceSetCtrl;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;

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

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        this.portletRegistry = (PortletRegistry) ctx.getBean("portletRegistry");

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
            portletRegistry.removeApplication((PortletApplicationDefinition) itr.next());
        }
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
        // start clean
        Iterator itr = portletRegistry.getPortletApplications().iterator();
        while (itr.hasNext())
        {
            portletRegistry.removeApplication((PortletApplicationDefinition) itr.next());
        }

        // Create an Application and a Web app

        PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
        app.setName("App_1");
        app.setApplicationIdentifier("App_1");

        UserAttributeRef uaRef = new UserAttributeRefImpl("user-name-family", "user.name.family");
        app.addUserAttributeRef(uaRef);

        UserAttribute ua = new UserAttributeImpl("user.name.family", "User Last Name");
        app.addUserAttribute(ua);

        JetspeedServiceReference service1 = new JetspeedServiceReferenceImpl("PortletEntityAccessComponent");
        app.addJetspeedService(service1);
        JetspeedServiceReference service2 = new JetspeedServiceReferenceImpl("PortletRegistryComponent");
        app.addJetspeedService(service2);

        addDublinCore(app.getMetadata());

        WebApplicationDefinitionImpl webApp = new WebApplicationDefinitionImpl();
        webApp.setContextRoot("/app1");
        webApp.addDescription(Locale.FRENCH, "Description: Le fromage est dans mon pantalon!");
        webApp.addDisplayName(Locale.FRENCH, "Display Name: Le fromage est dans mon pantalon!");

        PortletDefinitionComposite portlet = new PortletDefinitionImpl();
        portlet.setClassName("org.apache.Portlet");
        portlet.setName("Portlet 1");
        portlet.addDescription(Locale.getDefault(), "POrtlet description.");
        portlet.addDisplayName(Locale.getDefault(), "Portlet display Name.");

        portlet.addInitParameter("testparam", "test value", "This is a test portlet parameter", Locale.getDefault());

        addDublinCore(portlet.getMetadata());

        // PreferenceComposite pc = new PrefsPreference();
        app.addPortletDefinition(portlet);
        PreferenceSetCtrl prefSetCtrl = (PreferenceSetCtrl) portlet.getPreferenceSet();
        PreferenceComposite pc = (PreferenceComposite) prefSetCtrl.add("preference 1", Arrays.asList(new String[]
        { "value 1", "value 2" }));
        pc.addDescription(JetspeedLocale.getDefaultLocale(), "Preference Description");

        assertNotNull(pc.getValueAt(0));

        portlet.addLanguage(portletRegistry.createLanguage(Locale.getDefault(), "Portlet 1", "Portlet 1",
                "This is Portlet 1", null));

        ContentTypeComposite html = new ContentTypeImpl();
        html.setContentType("html/text");
        ContentTypeComposite wml = new ContentTypeImpl();
        html.addPortletMode(new PortletMode(MODE_EDIT));
        html.addPortletMode(new PortletMode(MODE_VIEW));
        html.addPortletMode(new PortletMode(MODE_HELP));
        wml.setContentType("wml");
        wml.addPortletMode(new PortletMode(MODE_HELP));
        wml.addPortletMode(new PortletMode(MODE_VIEW));
        portlet.addContentType(html);
        portlet.addContentType(wml);

        app.setWebApplicationDefinition(webApp);
        portletRegistry.updatePortletApplication(app);
    }

    protected void verifyData(boolean afterUpdates) throws Exception
    {
        MutablePortletApplication app;
        WebApplicationDefinitionImpl webApp;
        PortletDefinitionComposite portlet;

        // Now makes sure everthing got persisted

        app = null;

        app = (PortletApplicationDefinitionImpl) portletRegistry.getPortletApplication("App_1");

        assertNotNull(app);

        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
        portlet = (PortletDefinitionImpl) app.getPortletDefinitionByName("Portlet 1");

        assertNotNull("Failed to reteive portlet application", app);

        validateDublinCore(app.getMetadata());

        Collection services = app.getJetspeedServices();
        assertNotNull("jetspeed services is null", services);
        System.out.println("services is " + services);

        assertNotNull("Failed to reteive portlet application via registry", portletRegistry
                .getPortletApplication("App_1"));
        assertNotNull("Web app was not saved along with the portlet app.", webApp);
        assertNotNull("Portlet was not saved along with the portlet app.", app.getPortletDefinitionByName("Portlet 1"));
        if (!afterUpdates)
        {
            assertTrue("\"user.name.family\" user attribute was not found.", app.getUserAttributes().size() == 1);
        }
        else
        {
            assertTrue("\"user.name.family\" and user.pets user attributes were not found.", app.getUserAttributes()
                    .size() == 2);

        }

        portlet = (PortletDefinitionComposite) portletRegistry.getPortletDefinitionByUniqueName("App_1::Portlet 1");

        assertNotNull("Portlet could not be retreived by unique name.", portlet);

        validateDublinCore(portlet.getMetadata());

        assertNotNull("Portlet Application was not set in the portlet defintion.", portlet
                .getPortletApplicationDefinition());
        assertNotNull("French description was not materialized for the web app.", webApp.getDescription(Locale.FRENCH));
        assertNotNull("French display name was not materialized for the web app.", webApp.getDisplayName(Locale.FRENCH));
        assertNotNull("description was not materialized for the portlet.", portlet.getDescription(Locale.getDefault()));
        assertNotNull("display name was not materialized for the portlet.", portlet.getDisplayName(Locale.getDefault()));
        assertNotNull("\"testparam\" portlet parameter was not saved", portlet.getInitParameterSet().get("testparam"));
        assertNotNull("\"preference 1\" was not found.", portlet.getPreferenceSet().get("preference 1"));
        assertNotNull("Language information not found for Portlet 1", portlet.getLanguageSet().get(Locale.getDefault()));
        assertNotNull("Content Type html not found.", portlet.getContentTypeSet().get("html/text"));
        assertNotNull("Content Type wml not found.", portlet.getContentTypeSet().get("wml"));
        Iterator itr = portlet.getPreferenceSet().get("preference 1").getValues();
        int valueCount = 0;

        while (itr.hasNext())
        {
            itr.next();
            valueCount++;
        }
        assertEquals("\"preference 1\" did not have 2 values.", 2, valueCount);

        // Pull out our Web app and add a Description to it
        webApp = null;

        app = portletRegistry.getPortletApplication("App_1");

        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();
        assertNotNull("Web app was not located by query.", webApp);
        webApp.addDescription(Locale.getDefault(), "Web app description");

        webApp = null;

        app = portletRegistry.getPortletApplication("App_1");
        webApp = (WebApplicationDefinitionImpl) app.getWebApplicationDefinition();

        assertNotNull("Web app was not located by query.", webApp);
        assertNotNull("Web app did NOT persist its description", webApp.getDescription(Locale.FRENCH));

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

    protected String[] getConfigurations()
    {
        return new String[]
        { "transaction.xml", "registry.xml", "prefs.xml" };
    }
}
