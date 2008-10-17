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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import javax.portlet.PortletMode;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.persistence.store.LockFailedException;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.engine.MockJetspeedEngine;
import org.apache.jetspeed.om.common.JetspeedServiceReference;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.UserAttributeRef;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.impl.JetspeedServiceReferenceImpl;
import org.apache.jetspeed.om.impl.UserAttributeImpl;
import org.apache.jetspeed.om.impl.UserAttributeRefImpl;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.impl.SupportsImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.pluto.om.portlet.PreferenceSetCtrl;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;

/**
 * <p>
 * TestPortletRegistryDAO
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: TestPortletRegistryDAO.java 506825 2007-02-13 02:47:07Z taylor $
 */
public class TestRegistryCache extends DatasourceEnabledSpringTestCase
{
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

    // impl not complete
    public void xtestProxy() throws Exception
    {
        PortletApplication app = portletRegistry.getPortletApplication("PA-001");
        PortletApplication cached = PortletApplicationProxyImpl.createProxy(app);
        if (cached instanceof PortletApplication)
            System.out.println("ISA Mutable");
        if (cached instanceof PortletApplicationProxy)
            System.out.println("ISA Mutable Proxy");
        PortletApplicationProxy proxy = (PortletApplicationProxy)cached;
        PortletApplication two = proxy.getRealApplication();
        proxy.setRealApplication(two);
        System.out.println("Two is " + two);
        assertEquals(app, two);
    }
    
    public void testCache() throws Exception
    {
        assertNotNull(portletRegistry);
        PortletApplication one = portletRegistry.getPortletApplication("PA-001");
        PortletApplication two = portletRegistry.getPortletApplication("PA-001");
        assertEquals(one, two);
        PortletDefinition def = portletRegistry.getPortletDefinitionByUniqueName("PA-001::Portlet-1");
        assertNotNull(def);
        assertEquals(def.getPortletApplicationDefinition(), one);
        assertEquals(def, two.getPortletDefinitions().iterator().next());
        PortletApplication o = (PortletApplication)portletRegistry.getPortletApplications().iterator().next();
        assertEquals(one, o);
        assertEquals(portletRegistry.getAllPortletDefinitions().iterator().next(), def);
    }
    
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
        app.setName("PA-001");
        UserAttributeRef uaRef = new UserAttributeRefImpl("user-name-family", "user.name.family");
        app.addUserAttributeRef(uaRef);

        UserAttribute ua = new UserAttributeImpl("user.name.family", "User Last Name");
        app.addUserAttribute(ua);

        JetspeedServiceReference service1 = new JetspeedServiceReferenceImpl("PortletEntityAccessComponent");
        app.addJetspeedService(service1);
        JetspeedServiceReference service2 = new JetspeedServiceReferenceImpl("PortletRegistryComponent");
        app.addJetspeedService(service2);

        WebApplicationDefinitionImpl webApp = new WebApplicationDefinitionImpl();
        webApp.setContextRoot("/pa-001");
        webApp.addDescription(Locale.FRENCH, "Description: Le fromage est dans mon pantalon!");
        webApp.addDisplayName(Locale.FRENCH, "Display Name: Le fromage est dans mon pantalon!");

        PortletDefinition portlet = new PortletDefinitionImpl();
        portlet.setPortletClass("org.apache.Portlet");
        portlet.setPortletName("Portlet-1");
        portlet.addDescription(Locale.getDefault(), "POrtlet description.");
        portlet.addDisplayName(Locale.getDefault(), "Portlet display Name.");

        portlet.addInitParameter("testparam", "test value", "This is a test portlet parameter", Locale.getDefault());

        // PreferenceComposite pc = new PrefsPreference();
        app.addPortletDefinition(portlet);

        PreferenceSetCtrl prefSetCtrl = (PreferenceSetCtrl) portlet.getPreferenceSet();
        PreferenceComposite pc = (PreferenceComposite) prefSetCtrl.add("preference 1", Arrays.asList(new String[]
        { "value 1", "value 2" }));
        pc.addDescription(JetspeedLocale.getDefaultLocale(), "Preference Description");

        portlet.addLanguage(portletRegistry.createLanguage(Locale.getDefault(), "Portlet 1", "Portlet 1",
                "This is Portlet 1", null));

        ContentTypeComposite html = new SupportsImpl();
        html.setContentType("html/text");
        ContentTypeComposite wml = new SupportsImpl();
        html.addPortletMode(new PortletMode("EDIT"));
        html.addPortletMode(new PortletMode("VIEW"));
        html.addPortletMode(new PortletMode("HELP"));
        wml.setContentType("wml");
        wml.addPortletMode(new PortletMode("HELP"));
        wml.addPortletMode(new PortletMode("VIEW"));
        portlet.addContentType(html);
        portlet.addContentType(wml);

        app.setWebApplicationDefinition(webApp);
        portletRegistry.updatePortletApplication(app);
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
        Jetspeed.setEngine(null);
        super.tearDown();
    }
    
    protected String getBeanDefinitionFilterCategories()
    {
        return "registry,transaction,cache,jdbcDS";
    }
    
    protected String[] getConfigurations()
    {
        return new String[]
        { "transaction.xml", "registry-test.xml", "cache.xml", "static-bean-references.xml" };
    }
    
}
