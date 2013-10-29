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
package org.apache.jetspeed.components.portletentity;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.container.PortletEntity;
import org.apache.jetspeed.engine.MockJetspeedEngine;
import org.apache.jetspeed.om.page.impl.ContentFragmentImpl;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.pluto.container.om.portlet.PortletApplicationDefinition;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * <p>
 * TestPortletEntityDAO
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: TestPortletEntityDAO.java,v 1.3 2005/05/24 14:43:19 ate Exp $
 */
public class TestPortletEntityDAO extends DatasourceEnabledSpringTestCase
{
    private static final String TEST_APP = "EntityTestApp";

    private static final String TEST_PORTLET = "EntityTestPortlet";

    private static final String TEST_ENTITY = "user5/entity-9";

    private static MockJetspeedEngine mockEngine = new MockJetspeedEngine();

    private PortletEntityAccessComponent entityAccess = null;

    private PortletRegistry registry;
    
    protected void setUp() throws Exception
    {
        super.setUp();
        mockEngine.setComponentManager(scm);
        Jetspeed.setEngine(mockEngine);
        this.registry = scm.lookupComponent("portletRegistry");
        this.entityAccess = scm.lookupComponent("portletEntityAccess");

        teardownTestData();
        setupTestData();
    }

    protected void tearDown() throws Exception
    {
        teardownTestData();
        Jetspeed.setEngine(null);
        super.tearDown();
    }

    public void test1() throws Exception
    {
        assertNotNull(this.entityAccess);
        assertNotNull(this.registry);
    }

    public void testEntities() throws Exception
    {
        PortletApplicationDefinition pa = registry.getPortletApplication(TEST_APP);
        assertNotNull("Portlet Application", pa);
        System.out.println("pa = " + pa.getName());
        List<PortletDefinition> portlets = (List<PortletDefinition>) pa.getPortlets(); // .get(JetspeedObjectID.createFromString(TEST_PORTLET));
        Iterator pi = portlets.iterator();
        PortletDefinition pd = null;
        while (pi.hasNext())
        {
            pd = (PortletDefinition) pi.next();
            assertTrue("Portlet Def not found", pd.getPortletName().equals("EntityTestPortlet"));
        }
        assertNotNull("Portlet Def is null", pd);

        ContentFragmentImpl f1 = new ContentFragmentImpl(TEST_ENTITY);
        f1.setName(pd.getUniqueName());
        PortletEntity entity = entityAccess.generateEntityFromFragment(f1);

        // TODO: how to access prefs of entity??
        /*
        PreferenceSetComposite prefs = (PreferenceSetComposite) entity.getPreferenceSet();
        prefs.remove("pref1");
        assertNotNull(prefs);
        assertNull(prefs.get("pref1"));

        // test adding a pref
        prefs.add("pref1", Arrays.asList(new String[]
        { "1" }));
        assertNotNull(prefs.get("pref1"));

        // Remove should return the deleted pref
        assertNotNull(prefs.remove("pref1"));

        // Should be gone
        assertNull(prefs.get("pref1"));

        // Add it back so we can test tole back
        prefs.add("pref1", Arrays.asList(new String[]
        { "1" }));

        entityAccess.storePortletEntity(entity);

        prefs = (PreferenceSetComposite) entity.getPreferenceSet();

        assertNotNull(prefs.get("pref1"));
        
        PreferenceComposite pref = (PreferenceComposite) prefs.get("pref1");
        
        List<String> prefValues = pref.getValuesList();
        
        assertEquals(1, prefValues.size());
               
        prefValues.set(0, "2");
        pref.setValues(prefValues);

        prefValues = pref.getValuesList();
        assertEquals("2", prefValues.get(0));

        prefValues.add("3");
        prefs.add("pref2", prefValues);

        entity.store();

        prefs = (PreferenceSetComposite)entity.getPreferenceSet();
        PreferenceComposite pref2 = (PreferenceComposite) prefs.get("pref2");

        assertNotNull(pref2);

        prefValues = pref2.getValuesList();
        assertEquals(2, prefValues.size());

        pref2.addValue("4");
        prefValues = pref2.getValuesList();
        assertEquals(3, prefValues.size());
        assertEquals("2", prefValues.get(0));
        assertEquals("3", prefValues.get(1));
        assertEquals("4", prefValues.get(2));

        // testing preferences null values assignments fix, issue JS2-607
        prefValues.set(0,null);
        pref2.setValues(prefValues);
        entity.store();
        
        prefs = (PreferenceSetComposite)entity.getPreferenceSet();
        pref2 = (PreferenceComposite) prefs.get("pref2");
        
        prefValues = pref2.getValuesList();
        assertNull("pref2.value[0] should be null", prefValues.get(0));
        
        assertEquals(3, prefValues.size());
        assertEquals("3", prefValues.get(1));
        prefValues.set(0, "2");
        prefValues.set(1, null);
        prefValues.set(2, "3");
        pref2.setValues(prefValues);
        
        entity.store();

        prefs = (PreferenceSetComposite)entity.getPreferenceSet();
        pref2 = (PreferenceComposite) prefs.get("pref2");
        
        prefValues = pref2.getValuesList();
        assertNull("pref2.value[1] should be null", prefValues.get(1));
        
        assertTrue(pref2.isValueSet());
        pref2.setValues(null);        
        assertFalse(pref2.isValueSet());

        entity.store();

        prefs = (PreferenceSetComposite)entity.getPreferenceSet();
        pref2 = (PreferenceComposite) prefs.get("pref2");

        assertNull(pref2);
        */
        
        PortletEntity entity2 = entityAccess.getPortletEntityForFragment(f1);
        assertTrue("entity id ", entity2.getId().toString().equals(TEST_ENTITY));
        assertNotNull("entity's portlet ", entity2.getPortletDefinition());

        PortletEntity entity5 = entityAccess.newPortletEntityInstance(pd);
        System.out.println("before storing entity: " + entity5.getId());

        entityAccess.storePortletEntity(entity5);
        System.out.println("store done: " + entity5.getId());

        ContentFragmentImpl f2 = new ContentFragmentImpl(TEST_ENTITY);
        f2.setName(pd.getUniqueName());

        PortletEntity entity6 = entityAccess.getPortletEntityForFragment(f2);
        assertNotNull(entity6);
        System.out.println("reget : " + entity6.getId());

        entityAccess.removePortletEntity(entity6);
    }

    private void teardownTestData() throws Exception
    {

        PortletEntity entity = entityAccess.getPortletEntity(TEST_ENTITY);
        System.out.println("entity == " + entity);

        if (entity != null)
        {
            entityAccess.removePortletEntity(entity);
        }

        PortletApplication pa = registry.getPortletApplication(TEST_APP);
        System.out.println("pa == " + pa);
        if (pa != null)
        {
            registry.removeApplication(pa);
        }
        
        // TODO: remove portletPreferences...
    }

    private void setupTestData() throws Exception
    {
        String lang = Locale.getDefault().toString();

        PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
        app.setName(TEST_APP);
        app.setContextPath("/app1");

        PortletDefinition portlet = app.addPortlet(TEST_PORTLET);
        portlet.setPortletClass("org.apache.Portlet");
        portlet.addDescription(lang).setDescription("Portlet description.");
        portlet.addDisplayName(lang).setDisplayName("Portlet display Name.");
        
        InitParam initParam = portlet.addInitParam("testparam");
        initParam.setParamValue("test value");
        initParam.addDescription(lang).setDescription("This is a test portlet parameter");
        
        Preferences prefs = portlet.getPortletPreferences();
        Preference pref = prefs.addPreference("pref1");
        pref.addValue("1");

        registry.registerPortletApplication(app);
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
