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

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.persistence.store.util.PersistenceSupportedTestCase;
import org.apache.jetspeed.prefs.impl.PreferencesProviderImpl;
import org.apache.jetspeed.prefs.impl.PropertyException;
import org.apache.jetspeed.prefs.impl.PropertyManagerImpl;

/**
 * <p>
 * Unit testing for {@link Preferences}.
 * </p>
 * 
 * @author <a href="dlestrat@yahoo.com">David Le Strat </a>
 */
public class TestPreferences extends PersistenceSupportedTestCase
{

    /** The property manager. */
    private static PropertyManager pms;

    /**
     * <p>
     * Defines property set types.
     * </p>
     */
    private final static int USER_PROPERTY_SET_TYPE = 0;
    private final static int SYSTEM_PROPERTY_SET_TYPE = 1;

    private PreferencesProvider provider;

    /**
     * <p>
     * Defines the test case name for junit.
     * </p>
     * 
     * @param testName
     *            The test case name.
     */
    public TestPreferences( String testName )
    {
        super(testName);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        pms = new PropertyManagerImpl(persistenceStore);
        provider = new PreferencesProviderImpl(persistenceStore,
                "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl", true);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        clean();
        // super.tearDown();
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPreferences.class);
    }

    /**
     * <p>
     * Test user root.
     * </p>
     */
    public void testUserRoot()
    {
        pms = new PropertyManagerImpl(persistenceStore);
        provider = new PreferencesProviderImpl(persistenceStore,
                "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl", true);
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

    public void testSansPropertyManager()
    {
        pms = new PropertyManagerImpl(persistenceStore);
        provider = new PreferencesProviderImpl(persistenceStore,
                "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl", false);
        Preferences pref0 = Preferences.userRoot();
        // Test that the property manager is off
        Preferences pref1 = pref0.node("testOpenNode");
        pref1.put("0", "I am 0 key");

        assertNotNull(pref1.get("0", null));

    }

    /**
     * <p>
     * Test system root.
     * </p>
     */
    public void testSystemRoot()
    {
        pms = new PropertyManagerImpl(persistenceStore);
        provider = new PreferencesProviderImpl(persistenceStore,
                "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl", true);
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

    /**
     * <p>
     * Test node and whether children exist under a given node.
     * </p>
     */
    public void testNodeAndChildrenNames()
    {
        pms = new PropertyManagerImpl(persistenceStore);
        provider = new PreferencesProviderImpl(persistenceStore,
                "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl", true);
        Preferences prefs = Preferences.userRoot();
        // Test without children.
        try
        {
            String[] childrenNames = prefs.childrenNames();
            if (childrenNames.length > 0)
            {
                assertTrue("expected no children, " + childrenNames.length + ", " + childrenNames[0],
                        childrenNames.length == 0);
            }
        }
        catch (BackingStoreException bse)
        {
            assertTrue("backing store exception: " + bse, false);
        }

        // Absolute path.
        // 1. The node does not exist. Create it.
        Preferences prefs0 = Preferences.userRoot().node("/an1/san1");
        assertNotNull("should not be null", prefs0);
        assertTrue("expected node == /an1/san1, " + prefs0.absolutePath(), prefs0.absolutePath().equals("/an1/san1"));

        // 2. If node exists. Get it.
        Preferences prefs1 = Preferences.userRoot().node("/an1/san1");
        assertNotNull("should not be null", prefs1);
        assertTrue("expected node == /an1/san1, " + prefs1.absolutePath(), prefs1.absolutePath().equals("/an1/san1"));

        //Relative path.
        Preferences prefs3 = Preferences.userRoot().node("/an1");
        Preferences prefs4 = prefs3.node("rn1/srn1");
        assertNotNull("should not be null", prefs4);
        assertTrue("expected node == /an1/rn1/srn1, " + prefs4.absolutePath(), prefs4.absolutePath().equals(
                "/an1/rn1/srn1"));

        try
        {
            String[] childrenNames = prefs3.childrenNames();
            assertEquals("should have 2 children", 2, childrenNames.length);
        }
        catch (BackingStoreException bse)
        {
            assertTrue("backing store exception: " + bse, false);
        }

        // Remove all nodes.
        try
        {
            prefs3.removeNode();
        }
        catch (BackingStoreException bse)
        {
            assertTrue("backing store exception: " + bse, false);
        }

    }

    /**
     * <p>
     * Test adding properties to a property set node and get property keys for a
     * given node.
     * </p>
     */
    public void testPropertyAndPropertyKeys()
    {
        pms = new PropertyManagerImpl(persistenceStore);
        provider = new PreferencesProviderImpl(persistenceStore,
                "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl", true);

        // 1. Current node does not have any property associated to it.
        // No property has been defined nor added to the node. There should be
        // no property and adding a child should return null.
        Preferences pref0 = Preferences.userRoot();
        try
        {
            String[] propertyKeys = pref0.keys();
            if (propertyKeys.length > 0)
            {
                assertTrue("expected no children, " + propertyKeys.length + ", " + propertyKeys[0],
                        propertyKeys.length == 0);
            }
        }
        catch (BackingStoreException bse)
        {
            assertTrue("backing store exception: " + bse, false);
        }

        pref0.put("propertyName0", "true");
        String prop = pref0.get("propertyName0", null);
        assertNull("should be null.", prop);

        // 2. Current node has properties associated to it.
        initPropertyKeys();
        Preferences pref1 = Preferences.userRoot().node("/user/principal1/propertyset1");
        pref1.put("propertyName0", "true");
        String prop1 = pref1.get("propertyName0", null);
        assertTrue("expected prop1 == true, " + prop1, prop1.equals("true"));

        // There should be 1 property under pref1.
        try
        {
            String[] propertyKeys = pref1.keys();
            assertEquals("expected 1 child, ", 1, propertyKeys.length);
        }
        catch (BackingStoreException bse)
        {
            assertTrue("backing store exception: " + bse, false);
        }

        // Test remove property.
        pref1.remove("propertyName0");
        prop1 = pref1.get("propertyName0", null);
        assertNull("should be null.", prop);

        // Remove all nodes with properties assigned to property sets.
        pref1.put("propertyName0", "true");
        prop1 = pref1.get("propertyName0", null);
        assertTrue("expected prop1 == true, " + prop1, prop1.equals("true"));

        try
        {
            Preferences pref2 = Preferences.userRoot().node("/user");
            pref2.removeNode();
        }
        catch (BackingStoreException bse)
        {
            assertTrue("backing store exception: " + bse, false);
        }

    }

    public void testNodeRemoval() throws Exception
    {
        Preferences prefs = Preferences.userRoot();

        final String test_node = "removeTest";

        assertFalse(prefs.nodeExists(test_node));

        Preferences removeNode = prefs.node(test_node);

        assertNotNull(removeNode);

        // now remove then re-add and see if a IllegalStateException is thrown

        removeNode.removeNode();
        assertFalse(prefs.nodeExists(test_node));
        
        try
        {
           removeNode.childrenNames();
           assertFalse("An IllegalStateException should have been thrown by the AbtractPreferences class", true);
        }
        catch (IllegalStateException e)
        {
            
        }
    }

    /**
     * <p>
     * Init property property keys map.
     * </p>
     */
    protected Map initPropertyKeysMap()
    {
        // Build a few property keys.
        Map propertyKeys = new HashMap();
        propertyKeys.put("propertyName0", new Integer("0"));
        propertyKeys.put("propertyName1", new Integer("1"));
        propertyKeys.put("propertyName2", new Integer("2"));
        propertyKeys.put("propertyName3", new Integer("3"));

        return propertyKeys;
    }

    /**
     * <p>
     * Init property property keys.
     * </p>
     */
    protected void initPropertyKeys()
    {
        Map propertyKeys = initPropertyKeysMap();
        Preferences pref = Preferences.userRoot().node("/user/principal1/propertyset1");

        try
        {
            pms.addPropertyKeys(pref, propertyKeys);
        }
        catch (PropertyException pex)
        {
        }
    }

    /**
     * <p>
     * Clean properties.
     * </p>
     */
    protected void clean()
    {
        Preferences pref = Preferences.userRoot().node("/user/principal1/propertyset1");
        try
        {
            Map propertyKeys = pms.getPropertyKeys(pref);
            pms.removePropertyKeys(pref, propertyKeys.keySet());
            Preferences.userRoot().node("/user").removeNode();
            Preferences.userRoot().node("/an1").removeNode();
            Preferences.userRoot().node("/rn1").removeNode();
            Preferences.userRoot().node("/testOpenNode").removeNode();
            Preferences.userRoot().node("/removeTest").removeNode();
        }
        catch (PropertyException pex)
        {
            System.out.println("PropertyException" + pex);
        }
        catch (BackingStoreException bse)
        {
            System.out.println("BackingStoreException" + bse);
        }
    }

}