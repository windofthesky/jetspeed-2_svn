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

import java.util.Iterator;
import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.util.PersistenceSupportedTestCase;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponentImpl;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;

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
    
            entityAccess = new PortletEntityAccessComponentImpl(persistenceStore);
            registry = new PortletRegistryComponentImpl(persistenceStore);
        
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
        super.tearDown();
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
        PortletDefinition pd = null;
        while (pi.hasNext())
        {
            pd = (PortletDefinition)pi.next();
            assertTrue("Portlet Def not found", pd.getName().equals("EntityTestPortlet"));
        }
        assertNotNull("Portlet Def is null", pd);
        StoreablePortletEntityDelegate entity = entityAccess.newPortletEntityInstance(pd);
        entity.setId(TEST_ENTITY);
        entityAccess.storePortletEntity(entity);
        
        StoreablePortletEntityDelegate entity2 = entityAccess.getPortletEntity(JetspeedObjectID.createFromString(TEST_ENTITY));
        assertTrue("entity id ", entity2.getId().toString().equals(TEST_ENTITY));
        assertNotNull("entity's portlet ", entity2.getPortletDefinition());
        
        StoreablePortletEntityDelegate entity5 = entityAccess.newPortletEntityInstance(pd);
        System.out.println("before storing entity: "  + entity5.getId());
        
        //entity.setId(TEST_ENTITY);
        entityAccess.storePortletEntity(entity5);
        System.out.println("store done: " + entity5.getId());        

        StoreablePortletEntityDelegate entity6 = entityAccess.getPortletEntity(entity5.getId());
        assertNotNull(entity6);
        System.out.println("reget : " + entity6.getId());        
        
        entityAccess.removePortletEntity(entity6);
        
        store.getTransaction().commit();              
        
        // TODO: test preferences
        System.out.println("PortletEntity Test completed.");
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
        StoreablePortletEntityDelegate entity = entityAccess.getPortletEntity(JetspeedObjectID.createFromString(TEST_ENTITY));
        System.out.println("entity == " + entity);
        
        if (entity != null)
        {
            entityAccess.removePortletEntity(entity);
        }
                
        store.getTransaction().commit();              

    }
    
}
