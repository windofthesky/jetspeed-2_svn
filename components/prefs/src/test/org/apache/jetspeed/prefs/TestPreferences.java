/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.prefs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.naming.NameAlreadyBoundException;

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ComponentAwareTestSuite;
import org.apache.jetspeed.components.datasource.DatasourceComponent;
import org.apache.jetspeed.components.jndi.JNDIComponent;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.persistence.store.impl.LockFailedException;
import org.apache.jetspeed.prefs.impl.PropertyException;

import org.picocontainer.MutablePicoContainer;

/**
 * <p>Unit testing for {@link Preferences}.</p>
 * 
 * @author <a href="dlestrat@yahoo.com">David Le Strat</a>
 */
public class TestPreferences extends AbstractComponentAwareTestCase
{

    /** The mutable pico container. */
    private MutablePicoContainer container;

    /** The persistence store. */
    private static PersistenceStore store;

    /** The property manager. */
    private static PropertyManager pms;

    /**
     * <p>Defines property set types.</p>
     */
    private final static short USER_PROPERTY_SET_TYPE = 0;
    private final static short SYSTEM_PROPERTY_SET_TYPE = 1;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestPreferences(String testName)
    {
        super(testName);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        container = (MutablePicoContainer) getContainer();
        pms = (PropertyManager) container.getComponentInstance(PropertyManager.class);
        //destroyRootNodes();
        //destroyPropertySetDefTestObject();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
        //destroyPropertySetDefTestObject();
    }

    /**
     * <p>Creates the test suite.</p>
     * @return A test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestPreferences.class);
        suite.setScript("org/apache/jetspeed/containers/prefs.container.groovy");
        return suite;
    }

    /**
     * <p>Test the container.</p>
     */
    public void testContainer()
    {
        assertNotNull(container);
    }

    /**
     * <p>Test user root.</p>
     */
    public void testUserRoot()
    {
        Preferences prefs = Preferences.userRoot();
        if (null != prefs)
        {
            assertTrue("expected user root == '/', " + prefs.absolutePath(), prefs.absolutePath().equals("/"));
        }
        else
        {
            assertTrue("expected user root == '/', " + prefs, false);
        }
    }

    /**
     * <p>Test system root.</p>
     */
    public void testSystemRoot()
    {
        Preferences prefs = Preferences.systemRoot();
        if (null != prefs)
        {
            assertTrue("expected system root == '/', " + prefs.absolutePath(), prefs.absolutePath().equals("/"));
        }
        else
        {
            assertTrue("expected system root == '/', " + prefs, false);
        }
    }

//    /**
//     * <p>Test children names.</p>
//     */
//    public void testChildrenNames()
//    {
//        Preferences prefs = Preferences.userRoot();
//        // Test without children.
//        try
//        {
//            String[] childrenNames = prefs.childrenNames();
//            if (childrenNames.length > 0)
//            {
//                assertTrue("expected no children, " + childrenNames.length + ", " + childrenNames[0], childrenNames.length == 0);
//            }
//        }
//        catch (BackingStoreException bse)
//        {
//            assertTrue("backing store exception: " + bse, false);
//        }
//
//        // TODO Test with children.
//    }
//
//    /**
//     * <p>Test node.</p>
//     */
//    public void testNode()
//    {
//        String testRelNodeName = "rn1/srn1";
//        String testAbsNodeName0 = "/an1";
//        String testAbsNodeName1 = "/san1";
//        String testAbsNodeName = testAbsNodeName0 + testAbsNodeName1;
//
//        // Absolute path.
//        // 1. The node does not exist. Create it.
//        Preferences prefs0 = Preferences.userRoot().node(testAbsNodeName);
//        if (null != prefs0)
//        {
//            assertTrue(
//                "expected node == '" + testAbsNodeName + "', " + prefs0.absolutePath(),
//                prefs0.absolutePath().equals(testAbsNodeName));
//        }
//        else
//        {
//            assertTrue("expected node == '" + testAbsNodeName + "', " + prefs0, false);
//        }
//
//        // 2. If node exists. Get it.
//        Preferences prefs1 = Preferences.userRoot().node(testAbsNodeName);
//        if (null != prefs1)
//        {
//            assertTrue(
//                "expected node == '" + testAbsNodeName + "', " + prefs1.absolutePath(),
//                prefs1.absolutePath().equals(testAbsNodeName));
//        }
//        else
//        {
//            assertTrue("expected node == '" + testAbsNodeName + "', " + prefs1, false);
//        }
//
//        //Relative path.
//        Preferences prefs3 = Preferences.userRoot().node(testAbsNodeName0);
//        Preferences prefs4 = prefs3.node(testRelNodeName);
//        if (null != prefs4)
//        {
//            assertTrue(
//                "expected node == '" + testAbsNodeName0 + "/" + testRelNodeName + "', " + prefs4.absolutePath(),
//                prefs4.absolutePath().equals(testAbsNodeName0 + "/" + testRelNodeName));
//        }
//        else
//        {
//            assertTrue("expected node == '" + testAbsNodeName0 + "/" + testRelNodeName + "', " + prefs4, false);
//        }
//
//        // Remove all nodes.
//        try
//        {
//            prefs3.removeNode();
//        }
//        catch (BackingStoreException bse)
//        {
//            assertTrue("backing store exception: " + bse, false);
//        }
//    }
//
//    /**
//     * <p>Test adding properties to a property set node.</p>
//     */
//    public void testProperty()
//    {
//        String userNodeName = "user";
//        String principalNodeName = "principal";
//        String propertySetNodeName = "propertyset1";
//        String fullPropertySetPath = "/" + userNodeName + "/" + principalNodeName + "/" + propertySetNodeName;
//        int[] propertySetDefIds = new int[2];
//
//        try
//        {
//            initPropertySetDefTestObject();
//        }
//        catch (PropertyException pe)
//        {
//            assertTrue(false);
//        }
//
//        // 1. Current node is not defined as property set.
//        Preferences prefs0 = Preferences.userRoot();
//        prefs0.put("propertyName00", "true");
//        String prop0 = prefs0.get("propertyName00", null);
//        if (null != prop0)
//        {
//            assertTrue(false);
//        }
//
//        // 2. Current node is defined as property set.
//        Preferences prefs1 = Preferences.userRoot().node(fullPropertySetPath);
//        prefs1.put("propertyName00", "true");
//        String prop1 = prefs1.get("propertyName00", null);
//        assertTrue("expected prop1 == true, " + prop1, prop1.equals("true"));
//
//        // Test remove property.
//        prefs1.remove("propertyName00");
//        prop1 = prefs1.get("propertyName00", null);
//        if (null != prop1)
//        {
//            assertTrue(false);
//        }
//
//        // Remove all nodes with properties assigned to property sets.
//        prefs1.put("propertyName00", "true");
//        prop1 = prefs1.get("propertyName00", null);
//        if (!prop1.equals("true"))
//        {
//            assertTrue(false);
//        }
//
//        try
//        {
//            Preferences prefs2 = Preferences.userRoot().node("/" + userNodeName);
//            prefs2.removeNode();
//        }
//        catch (BackingStoreException bse)
//        {
//            assertTrue("backing store exception: " + bse, false);
//        }
//    }
//
//    /**
//     * <p>Init property set definition object.</p>
//     */
//    protected void initPropertySetDefTestObject() throws PropertyException
//    {
//        // Create the set definition.
//        pms.addPropertySetDef("propertyset1", USER_PROPERTY_SET_TYPE);
//        // Build a few property keys.
//        Collection propertyKeys = new ArrayList();
//        Map propertyKey00 = new HashMap();
//        propertyKey00.put(PropertyManager.PROPERTYKEY_NAME, "propertyName00");
//        propertyKey00.put(PropertyManager.PROPERTYKEY_TYPE, new Short("0"));
//        propertyKeys.add(propertyKey00);
//        Map propertyKey01 = new HashMap();
//        propertyKey01.put(PropertyManager.PROPERTYKEY_NAME, "propertyName01");
//        propertyKey01.put(PropertyManager.PROPERTYKEY_TYPE, new Short("1"));
//        propertyKeys.add(propertyKey01);
//        Map propertyKey02 = new HashMap();
//        propertyKey02.put(PropertyManager.PROPERTYKEY_NAME, "propertyName02");
//        propertyKey02.put(PropertyManager.PROPERTYKEY_TYPE, new Short("2"));
//        propertyKeys.add(propertyKey02);
//        Map propertyKey03 = new HashMap();
//        propertyKey03.put(PropertyManager.PROPERTYKEY_NAME, "propertyName03");
//        propertyKey03.put(PropertyManager.PROPERTYKEY_TYPE, new Short("3"));
//        propertyKeys.add(propertyKey03);
//        pms.addPropertyKeys("propertyset1", USER_PROPERTY_SET_TYPE, propertyKeys);
//        // Add another set.
//        // Create the set definition.
//        pms.addPropertySetDef("propertyset2", USER_PROPERTY_SET_TYPE);
//        // Build a few property keys.
//        Collection propertyKeys1 = new ArrayList();
//        Map propertyKey10 = new HashMap();
//        propertyKey10.put(PropertyManager.PROPERTYKEY_NAME, "propertyName10");
//        propertyKey10.put(PropertyManager.PROPERTYKEY_TYPE, new Short("0"));
//        propertyKeys1.add(propertyKey10);
//        Map propertyKey11 = new HashMap();
//        propertyKey11.put(PropertyManager.PROPERTYKEY_NAME, "propertyName11");
//        propertyKey11.put(PropertyManager.PROPERTYKEY_TYPE, new Short("1"));
//        propertyKeys1.add(propertyKey11);
//        pms.addPropertyKeys("propertyset2", USER_PROPERTY_SET_TYPE, propertyKeys1);
//    }
//
//    /**
//     * <p>Destroy profile test object.</p>
//     */
//    protected void destroyPropertySetDefTestObject()
//    {
//        try
//        {
//            pms.removePropertySetDef("propertyset1", USER_PROPERTY_SET_TYPE);
//            pms.removePropertySetDef("propertyset2", USER_PROPERTY_SET_TYPE);
//        }
//        catch (PropertyException pex)
//        {
//        }
//    }
//
//    /**
//     * <p>Destroy possible existing root nodes.</p>
//     */
//    protected void destroyRootNodes() throws Exception
//    {
//        // Remove user, group and role root nodes.
//        Preferences groupRootPrefs = Preferences.userRoot().node("/group");
//        groupRootPrefs.removeNode();
//        Preferences roleRootPrefs = Preferences.userRoot().node("/role");
//        roleRootPrefs.removeNode();
//        Preferences userRootPrefs = Preferences.userRoot().node("/user");
//        userRootPrefs.removeNode();
//    }

}
