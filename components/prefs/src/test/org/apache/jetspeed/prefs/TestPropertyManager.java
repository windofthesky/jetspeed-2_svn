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
import org.apache.jetspeed.prefs.impl.PropertyManagerImpl;
import org.apache.jetspeed.prefs.om.Property;

/**
 * <p>Unit testing for {@link PropertyManager}.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestPropertyManager extends PersistenceSupportedTestCase
{

    /** The property manager. */
    private static PropertyManager pms;

    /**
     * <p>Defines property set types.</p>
     */
    private final static int USER_PROPERTY_SET_TYPE = 0;
    private final static int SYSTEM_PROPERTY_SET_TYPE = 1;

    private PreferencesProvider provider;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestPropertyManager(String testName)
    {
        super(testName);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        pms =new PropertyManagerImpl(persistenceStore);
        provider = new PreferencesProviderImpl(persistenceStore, "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl", true);
        // Class.forName("org.apache.jetspeed.prefs.impl.PreferencesImpl");
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
        return new TestSuite(TestPropertyManager.class);
    }

 
    /**
     * <p>Test add property keys to a {@link Preferences} node.</p>
     */
    public void testAddPropertyKeys() throws PropertyException
    {
        Map propertyKeys = initPropertyKeysMap();
        Preferences pref = Preferences.userRoot().node("/user/principal1/propertyset1"); 
        try
        {
            pms.addPropertyKeys(pref, propertyKeys);
        }
        catch (PropertyException pex)
        {
            assertTrue("could not add property keys to node: " + pex, false);
        }
    }

    /**
     * <p>Test get property key.</p>
     */
    public void testGetPropertyKeys()
    {
        initPropertyKeys();
        Preferences pref = Preferences.userRoot().node("/user/principal1/propertyset1");
        Map propertyKeys = pms.getPropertyKeys(pref);
        assertEquals("should have 4 keys, " + propertyKeys.size(), 4, propertyKeys.size());
    }

    /**
     * <p>Test update property key.</p>
     */
    public void testUpdatePropertyKey() throws PropertyException
    {
        initPropertyKeys();
        Preferences pref = Preferences.userRoot().node("/user/principal1/propertyset1");
        try
        {
            // New key
            HashMap newKey = new HashMap(1);
            newKey.put("newPropertyName0", new Integer("0"));
            pms.updatePropertyKey("propertyName0", pref, newKey);
            Map propKeys = pms.getPropertyKeys(pref);
            assertTrue("should contain newPropertyName0", propKeys.containsKey("newPropertyName0"));
            HashMap oldKey = new HashMap(1);
            oldKey.put("propertyName0", new Integer("0"));
            pms.updatePropertyKey("NewPropertyName0", pref, oldKey);
        }
        catch (PropertyException pex)
        {
            assertTrue("could not update property key. exception caught: " + pex, false);
        }
    }

    /**
     * <p>Test remove property keys.</p>
     */
    public void testRemovePropertyKeys() throws PropertyException
    {
        initPropertyKeys();
        Preferences pref = Preferences.userRoot().node("/user/principal1/propertyset1");
        try
        {
            Map propertyKeys = pms.getPropertyKeys(pref);
            pms.removePropertyKeys(pref, propertyKeys.keySet());
            propertyKeys = pms.getPropertyKeys(pref);
            assertEquals("should have 0 keys, " + propertyKeys.size(), 0, propertyKeys.size());
        }
        catch (PropertyException pex)
        {
            assertTrue("could not delete property keys. exception caught: " + pex, false);
        }
    }

    /**
     * <p>Init property property keys map.</p>
     */
    protected Map initPropertyKeysMap()
    {
        // Build a few property keys.
        Map propertyKeys = new HashMap();
        propertyKeys.put("propertyName0", new Integer(Property.BOOLEAN_TYPE));
        propertyKeys.put("propertyName1", new Integer(Property.LONG_TYPE));
        propertyKeys.put("propertyName2", new Integer(Property.DOUBLE_TYPE));
        propertyKeys.put("propertyName3", new Integer(Property.STRING_TYPE));

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
        }
        catch (PropertyException pex)
        {
        }
        catch (BackingStoreException bse)
        {
            System.out.println("BackingStoreException" + bse);
        }
    }

}
