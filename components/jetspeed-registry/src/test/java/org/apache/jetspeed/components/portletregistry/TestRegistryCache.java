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
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.jetspeed.om.portlet.Supports;
import org.apache.jetspeed.om.portlet.UserAttribute;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.ojb.broker.Identity;

import java.util.Iterator;
import java.util.Locale;

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
        this.portletRegistry = scm.lookupComponent("portletRegistry");

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
        assertEquals(def.getApplication(), one);
        assertEquals(def, two.getPortlets().iterator().next());
        PortletApplication o = (PortletApplication)portletRegistry.getPortletApplications().iterator().next();
        assertEquals(one, o);
        assertEquals(portletRegistry.getAllDefinitions().iterator().next(), def);
    }
    
    public void testCacheDirectly() {
        assertNotNull(portletRegistry);
        PortletDefinition def = portletRegistry.getPortletDefinitionByUniqueName("PA-001::Portlet-1");
        assertNotNull(def);
        
        Identity testPortletDefOid = new Identity(PortletDefinitionImpl.class, PortletApplicationDefinitionImpl.class, new Object [] { "PA-001::Portlet-1" });
        RegistryPortletCache.cacheAdd(testPortletDefOid, def);
        assertNotNull(RegistryPortletCache.cacheLookup(testPortletDefOid));
        assertEquals(def, RegistryPortletCache.cacheLookup(testPortletDefOid));
        
        RegistryPortletCache.cacheRemoveQuiet(def.getUniqueName(), null);
        assertNull(RegistryPortletCache.cacheLookup(testPortletDefOid));
    }
    
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
        app.setName("PA-001");
        app.setContextPath("/pa-001");

        UserAttributeRef uaRef = app.addUserAttributeRef("user-name-family");
        uaRef.setNameLink("user.name.family");
        
        UserAttribute ua = app.addUserAttribute("user.name.family");
        ua.addDescription(lang).setDescription("User Last Name");
        
        app.addJetspeedServiceReference("PortletEntityAccessComponent");
        app.addJetspeedServiceReference("PortletRegistryComponent");

        PortletDefinition portlet = app.addPortlet("Portlet-1");
        portlet.setPortletClass("org.apache.Portlet");
        portlet.addDescription(lang).setDescription("Portlet description.");
        portlet.addDisplayName(lang).setDisplayName("Portlet display Name.");
        
        InitParam initParam = portlet.addInitParam("testparam");
        initParam.setParamValue("test value");
        initParam.addDescription(lang).setDescription("This is a test portlet parameter");

        Preferences prefs = portlet.getPortletPreferences();
        Preference pref = prefs.addPreference("preference 1");
        pref.addValue("value 1");
        pref.addValue("value 2");

        Language language = portlet.addLanguage(Locale.getDefault());
        language.setTitle("Portlet 1");
        language.setShortTitle("Portlet 1");

        
        Supports supports = portlet.addSupports("html/text");
        supports.addPortletMode("EDIT");
        supports.addPortletMode("VIEW");
        supports.addPortletMode("HELP");
        
        supports = portlet.addSupports("wml");
        supports.addPortletMode("HELP");
        supports.addPortletMode("VIEW");

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
            portletRegistry.removeApplication((PortletApplication) itr.next());
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
        { "transaction.xml", "registry-test.xml", "cache-test.xml", "static-bean-references.xml" };
    }
    
}
