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
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.prefs.PreferencesProvider;
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
        super(testName, "./src/test/Log4j.properties");
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        container = (MutablePicoContainer) getContainer();
        pms = (PropertyManager) container.getComponentInstance(PropertyManager.class);
        container.getComponentInstance(PreferencesProvider.class);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
        clean();
    }

    /**
     * <p>Creates the test suite.</p>
     * @return A test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestPreferences.class);
        suite.setScript("org/apache/jetspeed/containers/test.prefs.groovy");
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

    /**
     * <p>Test children names.</p>
     */
    public void testChildrenNames()
    {
        Preferences prefs = Preferences.userRoot();
        // Test without children.
        try
        {
            String[] childrenNames = prefs.childrenNames();
            if (childrenNames.length > 0)
            {
                assertTrue("expected no children, " + childrenNames.length + ", " + childrenNames[0], childrenNames.length == 0);
            }
        }
        catch (BackingStoreException bse)
        {
            assertTrue("backing store exception: " + bse, false);
        }

        // TODO Test with children.
    }

    /**
     * <p>Test node.</p>
     */
    public void testNode()
    {
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
        assertTrue("expected node == /an1/rn1/srn1, " + prefs4.absolutePath(), prefs4.absolutePath().equals("/an1/rn1/srn1"));

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
     * <p>Test adding properties to a property set node.</p>
     */
    public void testProperty()
    {

        // 1. Current node does not have any property associated to it.
        Preferences pref0 = Preferences.userRoot();
        pref0.put("propertyName0", "true");
        String prop = pref0.get("propertyName0", null);
        assertNull("should be null.", prop);

        // 2. Current node has properties associated to it.
        initPropertyKeys();
        Preferences pref1 = Preferences.userRoot().node("/user/principal1/propertyset1");
        pref1.put("propertyName0", "true");
        String prop1 = pref1.get("propertyName0", null);
        assertTrue("expected prop1 == true, " + prop1, prop1.equals("true"));

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

    /**
     * <p>Init property property keys map.</p>
     */
    protected Map initPropertyKeysMap()
    {
        // Build a few property keys.
        Map propertyKeys = new HashMap();
        propertyKeys.put("propertyName0", new Short("0"));
        propertyKeys.put("propertyName1", new Short("1"));
        propertyKeys.put("propertyName2", new Short("2"));
        propertyKeys.put("propertyName3", new Short("3"));

        return propertyKeys;
    }

    /**
     * <p>Init property property keys.</p>
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
     * <p>Clean properties.</p>
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
