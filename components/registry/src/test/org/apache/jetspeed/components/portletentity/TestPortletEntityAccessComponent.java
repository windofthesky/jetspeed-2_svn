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
package org.apache.jetspeed.components.portletentity;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.util.PersistenceSupportedTestCase;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponentImpl;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.prefs.impl.PreferencesProviderImpl;
import org.apache.jetspeed.prefs.impl.PropertyManagerImpl;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.jmock.Mock;
import org.jmock.core.matcher.InvokeAtLeastOnceMatcher;
import org.jmock.core.stub.ReturnStub;

/**
 * Test Portlet Entity Accessor
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestPortletEntityAccessComponent extends PersistenceSupportedTestCase 
{
    
    private  PortletEntityAccessComponent entityAccess = null;
    private  PortletRegistryComponent registry = null;
    private static final String TEST_APP = "EntityTestApp";
    private static final String TEST_PORTLET = "EntityTestPortlet";
    private static final String TEST_ENTITY = "user5/entity-9";
    
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {        
        super.setUp();

        registry = new PortletRegistryComponentImpl(persistenceStore);
        entityAccess = new PortletEntityAccessComponentImpl(persistenceStore, registry);
        
        PropertyManagerImpl pms = new PropertyManagerImpl(persistenceStore);
        PreferencesProviderImpl provider = new PreferencesProviderImpl(persistenceStore, "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl", false);
        provider.start();
        setupTestData();                   
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {                
        teardownTestData();
       //super.tearDown();
    }

   public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPortletEntityAccessComponent.class);
    }
    

    /**
     * @param testName
     */
    public TestPortletEntityAccessComponent(String testName)
    {
        super(testName);
    }
        
    public void testEntities() throws Exception
    {
        
        PersistenceStore store = registry.getPersistenceStore();
        store.getTransaction().begin();
        
        PortletApplicationDefinition pa = registry.getPortletApplication(TEST_APP);
        assertNotNull("Portlet Application", pa);
        System.out.println("pa = " + pa.getId());
        PortletDefinitionList portlets = pa.getPortletDefinitionList(); //.get(JetspeedObjectID.createFromString(TEST_PORTLET));
        Iterator pi = portlets.iterator();
        PortletDefinitionComposite pd = null;
        while (pi.hasNext())
        {
            pd = (PortletDefinitionComposite)pi.next();
            assertTrue("Portlet Def not found", pd.getName().equals("EntityTestPortlet"));
        }
        assertNotNull("Portlet Def is null", pd);
        
        
        
        Mock mockf1 = new Mock(Fragment.class);
        mockf1.expects(new InvokeAtLeastOnceMatcher()).method("getName").will(new ReturnStub(pd.getUniqueName()));
        mockf1.expects(new InvokeAtLeastOnceMatcher()).method("getId").will(new ReturnStub(TEST_ENTITY));
        Fragment f1 =(Fragment) mockf1.proxy();
            
        MutablePortletEntity entity = entityAccess.generateEntityFromFragment(f1);
        PreferenceSetComposite prefs = (PreferenceSetComposite) entity.getPreferenceSet();
        prefs.remove("pref1");
        assertNotNull(prefs);
        assertNull(prefs.get("pref1"));
        
        // test adding a pref
        prefs.add("pref1", Arrays.asList(new String[]{"1"}));
        assertNotNull(prefs.get("pref1"));
        
        // Remove should return the deleted pref
        assertNotNull(prefs.remove("pref1"));
        
        // Should be gone
        assertNull(prefs.get("pref1"));        
        
        // Add it back so we can test tole back
        prefs.add("pref1", Arrays.asList(new String[]{"1"}));

        entityAccess.storePortletEntity(entity);
        
        prefs = (PreferenceSetComposite) entity.getPreferenceSet();
        
        assertNotNull(prefs.get("pref1"));
        
        PreferenceComposite pref = (PreferenceComposite) prefs.get("pref1");
        
        assertEquals("1", pref.getValueAt(0));
        
        pref.setValueAt(0, "2");
        
        assertEquals("2", pref.getValueAt(0));
        
        entity.reset();
        
        pref = (PreferenceComposite) prefs.get("pref1");
        
        assertEquals("1", pref.getValueAt(0));
        
        prefs.remove(pref);       
        
        assertNull(prefs.get("pref1"));
        
        entity.reset();
        
        assertNotNull(prefs.get("pref1"));
        
        prefs.add("pref2", Arrays.asList(new String[]{"2", "3"}));
        
        entity.store();
        
        PreferenceComposite pref2 = (PreferenceComposite) prefs.get("pref2");
        
        assertNotNull(pref2);
        
        Iterator prefsValues = pref2.getValues();
        int count = 0;
        while(prefsValues.hasNext())
        {
            prefsValues.next();
            count++;
        }
        
        assertEquals(2, count);
        
        pref2.addValue("4");
        prefsValues = pref2.getValues();
        count = 0;
        while(prefsValues.hasNext())
        {
            assertEquals(String.valueOf(count+2), prefsValues.next());
            count++;
        }
        assertEquals(3, count);
        
        entity.reset();
        
        prefsValues = pref2.getValues();
        count = 0;
        while(prefsValues.hasNext())
        {
            assertEquals(String.valueOf(count+2), prefsValues.next());
            count++;
        }
        assertEquals(2, count);
        
        MutablePortletEntity entity2 = entityAccess.getPortletEntityForFragment(f1);
        assertTrue("entity id ", entity2.getId().toString().equals(TEST_ENTITY));
        assertNotNull("entity's portlet ", entity2.getPortletDefinition());
        mockf1.verify();
        
        
        Mock mockf2 = new Mock(Fragment.class);
        mockf2.expects(new InvokeAtLeastOnceMatcher()).method("getName").will(new ReturnStub(pd.getUniqueName()));
        Fragment f2 =(Fragment) mockf2.proxy();
        
        MutablePortletEntity entity5 = entityAccess.newPortletEntityInstance(pd);
        
        System.out.println("before storing entity: "  + entity5.getId());
        
        entityAccess.storePortletEntity(entity5);
        System.out.println("store done: " + entity5.getId());  
        mockf2.expects(new InvokeAtLeastOnceMatcher()).method("getId").will(new ReturnStub( entity5.getId().toString()));

        MutablePortletEntity entity6 = entityAccess.getPortletEntityForFragment(f2);
        assertNotNull(entity6);
        System.out.println("reget : " + entity6.getId());        
        
        entityAccess.removePortletEntity(entity6);
        
        store.getTransaction().commit();              
    }
        
    private void setupTestData()
    throws Exception
    {
        // TODO: this should strictly use the registry api only         
        PersistenceStore store = registry.getPersistenceStore();
        store.getTransaction().begin();
        
        PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
        app.setName(TEST_APP);
        app.setApplicationIdentifier(TEST_APP);
                
        WebApplicationDefinitionImpl webApp = new WebApplicationDefinitionImpl();
        webApp.setContextRoot("/app1");
        webApp.addDescription(Locale.FRENCH, "Description: Le fromage est dans mon pantalon!");
        webApp.addDisplayName(Locale.FRENCH, "Display Name: Le fromage est dans mon pantalon!");
        
        PortletDefinitionComposite portlet = new PortletDefinitionImpl();
        portlet.setClassName("org.apache.Portlet");
        portlet.setName(TEST_PORTLET);
        portlet.addDescription(Locale.getDefault(),"Portlet description.");
        portlet.addDisplayName(Locale.getDefault(),"Portlet display Name.");
        
        portlet.addInitParameter("testparam", "test value", "This is a test portlet parameter", Locale.getDefault());
                        
        app.addPortletDefinition(portlet);      
  
        app.setWebApplicationDefinition(webApp);
        
        PreferenceSetComposite prefSet = (PreferenceSetComposite) portlet.getPreferenceSet();
        prefSet.add("pref1", Arrays.asList(new String[]{"1"}));
        
        store.makePersistent(app);
        store.getTransaction().commit();              
    }
    
    private void teardownTestData()
    throws Exception
    {
        PersistenceStore store = registry.getPersistenceStore();
        store.getTransaction().begin();
        
        PortletApplicationDefinition pa = registry.getPortletApplication(TEST_APP);
        System.out.println("pa == " + pa);
        if (pa != null)
        {
            registry.removeApplication(pa);
        }
        MutablePortletEntity entity = entityAccess.getPortletEntity(JetspeedObjectID.createFromString(TEST_ENTITY));
        System.out.println("entity == " + entity);
        
        if (entity != null)
        {
            entityAccess.removePortletEntity(entity);
        }
        
        if(Preferences.systemRoot().nodeExists(MutablePortletApplication.PREFS_ROOT))
        {
            Preferences.systemRoot().node(MutablePortletApplication.PREFS_ROOT).removeNode();
        }
        
        if(Preferences.userRoot().nodeExists(PortletDefinitionComposite.PORTLETS_PREFS_ROOT))
        {
            Preferences.userRoot().node(PortletDefinitionComposite.PORTLETS_PREFS_ROOT).removeNode();
        }
        
        
        
        if(Preferences.userRoot().nodeExists(MutablePortletEntity.PORTLET_ENTITY_ROOT))
        {
            Preferences.userRoot().node(MutablePortletEntity.PORTLET_ENTITY_ROOT).removeNode();
        }
        
                
        store.getTransaction().commit();              

    }
    
    
    
}
