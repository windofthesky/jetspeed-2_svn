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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.components.portletregistry.PersistenceBrokerPortletRegistry;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.testhelpers.OJBHelper;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.jmock.Mock;
import org.jmock.core.matcher.InvokeAtLeastOnceMatcher;
import org.jmock.core.stub.ReturnStub;

/**
 * <p>
 * TestPortletEntityDAO
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: TestPortletEntityDAO.java,v 1.3 2005/05/24 14:43:19 ate Exp $
 *
 */
public class TestPortletEntityDAO extends TestCase
{

    // Dummy ContentFragment wrapper around Fragment as using the real ContentFragmentImpl
    // would introduce a circular dependency between the registry and page-manager components.
    // Probably should be replaced by a Mock but I don't know how to setup that quickly
    // and the whole ContentFragment construction is bound to be replaced soon anyway...
    
    private static class ContentFragmentImpl implements Fragment, ContentFragment
    {
        private Fragment f;
        
        /**
         * @param f
         * @param list
         */
        public ContentFragmentImpl(Fragment f, HashMap list)
        {
            super();
            this.f = f;
        }
        /**
         * @param p
         */
        public void addProperty(Property p)
        {
            f.addProperty(p);
        }
        /**
         * @param actions
         * @throws SecurityException
         */
        public void checkAccess(String actions) throws SecurityException
        {
            f.checkAccess(actions);
        }
        /**
         * @param actions
         * @throws SecurityException
         */
        public void checkConstraints(String actions) throws SecurityException
        {
            f.checkConstraints(actions);
        }
        /**
         * @param actions
         * @throws SecurityException
         */
        public void checkPermissions(String actions) throws SecurityException
        {
            f.checkPermissions(actions);
        }
        /**
         * @param layoutName
         */
        public void clearProperties(String layoutName)
        {
            f.clearProperties(layoutName);
        }
        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj)
        {
            return f.equals(obj);
        }
        /**
         * @return
         */
        public boolean getConstraintsEnabled()
        {
            return f.getConstraintsEnabled();
        }
        /**
         * @return
         */
        public String getDecorator()
        {
            return f.getDecorator();
        }
        /**
         * @return
         */
        public List getFragments()
        {
            return f.getFragments();
        }
        /**
         * @return
         */
        public String getId()
        {
            return f.getId();
        }
        /**
         * @return
         */
        public List getLayoutProperties()
        {
            return f.getLayoutProperties();
        }
        /**
         * @return
         */
        public String getName()
        {
            return f.getName();
        }
        /**
         * @return
         */
        public boolean getPermissionsEnabled()
        {
            return f.getPermissionsEnabled();
        }
        /**
         * @param layoutName
         * @return
         */
        public List getProperties(String layoutName)
        {
            return f.getProperties(layoutName);
        }
        /**
         * @param layout
         * @param propName
         * @return
         */
        public String getPropertyValue(String layout, String propName)
        {
            return f.getPropertyValue(layout, propName);
        }
        /**
         * @return
         */
        public SecurityConstraints getSecurityConstraints()
        {
            return f.getSecurityConstraints();
        }
        /**
         * @return
         */
        public String getShortTitle()
        {
            return f.getShortTitle();
        }
        /**
         * @return
         */
        public String getSkin()
        {
            return f.getSkin();
        }
        /**
         * @return
         */
        public String getState()
        {
            return f.getState();
        }
        /**
         * @return
         */
        public String getTitle()
        {
            return f.getTitle();
        }
        /**
         * @return
         */
        public String getType()
        {
            return f.getType();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode()
        {
            return f.hashCode();
        }
        /**
         * @return
         */
        public boolean isReference()
        {
            return f.isReference();
        }
        /**
         * @param p
         */
        public void removeProperty(Property p)
        {
            f.removeProperty(p);
        }
        /**
         * @param decoratorName
         */
        public void setDecorator(String decoratorName)
        {
            f.setDecorator(decoratorName);
        }
        /**
         * @param id
         */
        public void setId(String id)
        {
            f.setId(id);
        }
        /**
         * @param name
         */
        public void setName(String name)
        {
            f.setName(name);
        }
        /**
         * @param layout
         * @param propName
         * @param value
         */
        public void setPropertyValue(String layout, String propName, String value)
        {
            f.setPropertyValue(layout, propName, value);
        }
        /**
         * @param constraints
         */
        public void setSecurityConstraints(SecurityConstraints constraints)
        {
            f.setSecurityConstraints(constraints);
        }
        /**
         * @param title
         */
        public void setShortTitle(String title)
        {
            f.setShortTitle(title);
        }
        /**
         * @param skinName
         */
        public void setSkin(String skinName)
        {
            f.setSkin(skinName);
        }
        /**
         * @param state
         */
        public void setState(String state)
        {
            f.setState(state);
        }
        /**
         * @param title
         */
        public void setTitle(String title)
        {
            f.setTitle(title);
        }
        /**
         * @param type
         */
        public void setType(String type)
        {
            f.setType(type);
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString()
        {
            return f.toString();
        }
        
        public Object clone() throws CloneNotSupportedException
        {
            return f.clone();
        }
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.ContentFragment#getContentFragments()
         */
        public List getContentFragments()
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.ContentFragment#getRenderedContent()
         */
        public String getRenderedContent() throws IllegalStateException
        {
            // TODO Auto-generated method stub
            return null;
        }
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.ContentFragment#overrideRenderedContent(java.lang.String)
         */
        public void overrideRenderedContent(String contnent)
        {
            // TODO Auto-generated method stub
            
        }
        /* (non-Javadoc)
         * @see org.apache.jetspeed.om.page.ContentFragment#setPortletContent(org.apache.jetspeed.aggregator.PortletContent)
         */
        public void setPortletContent(PortletContent portletContent)
        {
            // TODO Auto-generated method stub
            
        }
    }
    
    private static final String TEST_APP = "EntityTestApp";
    private static final String TEST_PORTLET = "EntityTestPortlet";
    private static final String TEST_ENTITY = "user5/entity-9";
    private PersistenceBrokerPortletEntityAccess entityAccess = null;
    private PortletRegistry registry;
    private OJBHelper ojbHelper;   
 
    protected void setUp() throws Exception
    {      
        Map context = new HashMap();
        ojbHelper = new OJBHelper(context);
        ojbHelper.setUp();
        
        PersistenceBrokerPortletRegistry targetRegistry = new PersistenceBrokerPortletRegistry("META-INF/registry_repository.xml");
        targetRegistry.init();
        this.registry = (PortletRegistry) ojbHelper.getTxProxiedObject(targetRegistry, new String[]{PortletRegistry.class.getName()});
        
        
        this.entityAccess = new PersistenceBrokerPortletEntityAccess(this.registry);
        teardownTestData();
        setupTestData();
    }
    
    protected void tearDown() throws Exception
    {
        teardownTestData();
        ojbHelper.tearDown();
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
        ContentFragment f1 = new ContentFragmentImpl((Fragment) mockf1.proxy(), new HashMap());
            
        MutablePortletEntity entity = entityAccess.generateEntityFromFragment(new ContentFragmentImpl(f1, new HashMap()));
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
        ContentFragment f2 = new ContentFragmentImpl((Fragment) mockf2.proxy(), new HashMap());
        
        MutablePortletEntity entity5 = entityAccess.newPortletEntityInstance(pd);
        
        System.out.println("before storing entity: "  + entity5.getId());
        
        entityAccess.storePortletEntity(entity5);
        System.out.println("store done: " + entity5.getId());  
        mockf2.expects(new InvokeAtLeastOnceMatcher()).method("getId").will(new ReturnStub( entity5.getId().toString()));
    
        MutablePortletEntity entity6 = entityAccess.getPortletEntityForFragment(f2);
        assertNotNull(entity6);
        System.out.println("reget : " + entity6.getId());        
        
        entityAccess.removePortletEntity(entity6);
        
                
    }

    private void teardownTestData()
    throws Exception
    {   
        
        
        JetspeedObjectID objId = JetspeedObjectID.createFromString(TEST_ENTITY);
        MutablePortletEntity entity = entityAccess.getPortletEntity(objId);
        System.out.println("entity == " + entity);
        
        if (entity != null)
        {
            entityAccess.removePortletEntity(entity);
        }
        
        PortletApplicationDefinition pa = registry.getPortletApplication(TEST_APP);
        System.out.println("pa == " + pa);
        if (pa != null)
        {
            registry.removeApplication(pa);
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

    }

    private void setupTestData()
    throws Exception
    {
                
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
        
        registry.registerPortletApplication(app);           
    }
    
    protected String[] getConfigurations()
    {
        return new String[] {"/META-INF/transaction.xml", "/META-INF/registry-dao.xml", "/META-INF/entity-dao.xml"};
    }
}
