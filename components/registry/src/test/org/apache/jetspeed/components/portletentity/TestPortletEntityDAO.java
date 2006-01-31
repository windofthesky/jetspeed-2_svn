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

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.decoration.Decoration;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
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
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: TestPortletEntityDAO.java,v 1.3 2005/05/24 14:43:19 ate Exp $
 */
public class TestPortletEntityDAO extends DatasourceEnabledSpringTestCase
{
    private static final String TEST_APP = "EntityTestApp";

    private static final String TEST_PORTLET = "EntityTestPortlet";

    private static final String TEST_ENTITY = "user5/entity-9";

    private PersistenceBrokerPortletEntityAccess entityAccess = null;

    private PortletRegistry registry;

    protected void setUp() throws Exception
    {
        super.setUp();
        this.registry = (PortletRegistry) ctx.getBean("portletRegistry");
        this.entityAccess = (PersistenceBrokerPortletEntityAccess) ctx.getBean("portletEntityAccessImpl");

        teardownTestData();
        setupTestData();
    }

    protected void tearDown() throws Exception
    {
        teardownTestData();
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
        PortletDefinitionList portlets = pa.getPortletDefinitionList(); // .get(JetspeedObjectID.createFromString(TEST_PORTLET));
        Iterator pi = portlets.iterator();
        PortletDefinitionComposite pd = null;
        while (pi.hasNext())
        {
            pd = (PortletDefinitionComposite) pi.next();
            assertTrue("Portlet Def not found", pd.getName().equals("EntityTestPortlet"));
        }
        assertNotNull("Portlet Def is null", pd);

        Mock mockf1 = new Mock(Fragment.class);
        mockf1.expects(new InvokeAtLeastOnceMatcher()).method("getName").will(new ReturnStub(pd.getUniqueName()));
        mockf1.expects(new InvokeAtLeastOnceMatcher()).method("getId").will(new ReturnStub(TEST_ENTITY));
        ContentFragment f1 = new ContentFragmentTestImpl((Fragment) mockf1.proxy(), new HashMap());

        MutablePortletEntity entity = entityAccess
                .generateEntityFromFragment(new ContentFragmentTestImpl(f1, new HashMap()));
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

        prefs.add("pref2", Arrays.asList(new String[]
        { "2", "3" }));

        entity.store();

        PreferenceComposite pref2 = (PreferenceComposite) prefs.get("pref2");

        assertNotNull(pref2);

        Iterator prefsValues = pref2.getValues();
        int count = 0;
        while (prefsValues.hasNext())
        {
            prefsValues.next();
            count++;
        }

        assertEquals(2, count);

        pref2.addValue("4");
        prefsValues = pref2.getValues();
        count = 0;
        while (prefsValues.hasNext())
        {
            assertEquals(String.valueOf(count + 2), prefsValues.next());
            count++;
        }
        assertEquals(3, count);

        entity.reset();

        prefsValues = pref2.getValues();
        count = 0;
        while (prefsValues.hasNext())
        {
            assertEquals(String.valueOf(count + 2), prefsValues.next());
            count++;
        }
        assertEquals(2, count);

        MutablePortletEntity entity2 = entityAccess.getPortletEntityForFragment(f1);
        assertTrue("entity id ", entity2.getId().toString().equals(TEST_ENTITY));
        assertNotNull("entity's portlet ", entity2.getPortletDefinition());
        mockf1.verify();

        Mock mockf2 = new Mock(Fragment.class);
        mockf2.expects(new InvokeAtLeastOnceMatcher()).method("getName").will(new ReturnStub(pd.getUniqueName()));
        ContentFragment f2 = new ContentFragmentTestImpl((Fragment) mockf2.proxy(), new HashMap());

        MutablePortletEntity entity5 = entityAccess.newPortletEntityInstance(pd);

        System.out.println("before storing entity: " + entity5.getId());

        entityAccess.storePortletEntity(entity5);
        System.out.println("store done: " + entity5.getId());
        mockf2.expects(new InvokeAtLeastOnceMatcher()).method("getId").will(new ReturnStub(entity5.getId().toString()));

        MutablePortletEntity entity6 = entityAccess.getPortletEntityForFragment(f2);
        assertNotNull(entity6);
        System.out.println("reget : " + entity6.getId());

        entityAccess.removePortletEntity(entity6);

    }

    private void teardownTestData() throws Exception
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

        if (Preferences.systemRoot().nodeExists(MutablePortletApplication.PREFS_ROOT))
        {
            Preferences.systemRoot().node(MutablePortletApplication.PREFS_ROOT).removeNode();
        }

        if (Preferences.userRoot().nodeExists(PortletDefinitionComposite.PORTLETS_PREFS_ROOT))
        {
            Preferences.userRoot().node(PortletDefinitionComposite.PORTLETS_PREFS_ROOT).removeNode();
        }

        if (Preferences.userRoot().nodeExists(MutablePortletEntity.PORTLET_ENTITY_ROOT))
        {
            Preferences.userRoot().node(MutablePortletEntity.PORTLET_ENTITY_ROOT).removeNode();
        }

    }

    private void setupTestData() throws Exception
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
        portlet.addDescription(Locale.getDefault(), "Portlet description.");
        portlet.addDisplayName(Locale.getDefault(), "Portlet display Name.");

        portlet.addInitParameter("testparam", "test value", "This is a test portlet parameter", Locale.getDefault());

        app.addPortletDefinition(portlet);

        app.setWebApplicationDefinition(webApp);

        PreferenceSetComposite prefSet = (PreferenceSetComposite) portlet.getPreferenceSet();
        prefSet.add("pref1", Arrays.asList(new String[]
        { "1" }));

        registry.registerPortletApplication(app);
    }

    protected String[] getConfigurations()
    {
        return new String[]
        { "transaction.xml", "registry.xml", "prefs.xml" };
    }
}
