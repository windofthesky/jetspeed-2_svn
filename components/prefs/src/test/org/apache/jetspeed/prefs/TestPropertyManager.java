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
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.prefs.impl.PersistenceBrokerPreferencesProvider;
import org.apache.jetspeed.prefs.impl.PropertyManagerImpl;
import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.testhelpers.OJBHelper;

/**
 * <p>Unit testing for {@link PropertyManager}.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestPropertyManager extends TestCase
{

    /** The property manager. */
    private static PropertyManager pms;
    private OJBHelper ojbHelper;

    /**
     * <p>Defines property set types.</p>
     */
    private final static int USER_PROPERTY_SET_TYPE = 0;
    private final static int SYSTEM_PROPERTY_SET_TYPE = 1;

    private PreferencesProvider provider;


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
    public void testAddPropertyKeys() throws Exception
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
     * @throws Exception
     */
    public void testGetPropertyKeys() throws Exception
    {
        initPropertyKeys();
        Preferences pref = Preferences.userRoot().node("/user/principal1/propertyset1");
        Map propertyKeys = pms.getPropertyKeys(pref);
        assertEquals("should have 4 keys, " + propertyKeys.size(), 4, propertyKeys.size());
    }

    /**
     * <p>Test update property key.</p>
     */
    public void testUpdatePropertyKey() throws Exception
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
    public void testRemovePropertyKeys() throws Exception
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
    protected void initPropertyKeys() throws Exception
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
    protected void clean() throws Exception
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

    protected String[] getConfigurations()
    {
        return new String[]{"META-INF/prefs-dao.xml", "META-INF/transaction.xml"};
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        Map context = new HashMap();
        ojbHelper = new OJBHelper(context);
        ojbHelper.setUp();

        PersistenceBrokerPreferencesProvider targetProvider = new PersistenceBrokerPreferencesProvider("META-INF/prefs_repository.xml", true);
        targetProvider.init();
        this.provider = (PreferencesProvider) 
            ojbHelper.getTxProxiedObject(targetProvider, new String[]{PreferencesProvider.class.getName()});                
        
        PropertyManagerImpl targetPropMan = new PropertyManagerImpl(provider);
        
        pms = (PropertyManager) 
            ojbHelper.getTxProxiedObject(targetPropMan, new String[]{PropertyManager.class.getName()});                
        
    }

}
