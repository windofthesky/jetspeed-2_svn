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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
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
import org.apache.jetspeed.prefs.impl.PropertyManagerImpl;

import org.picocontainer.MutablePicoContainer;

/**
 * <p>Unit testing for {@link PropertyManager}.</p>
 *
 * @author <a href="dlestrat@yahoo.com">David Le Strat</a>
 */
public class TestPropertyManager extends AbstractComponentAwareTestCase
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
    public TestPropertyManager(String testName)
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
        try
        {
            destroyPropertySetDefTestObject(true);
        }
        catch (PropertyException exc)
        {
        }
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
        try
        {
            destroyPropertySetDefTestObject(true);
        }
        catch (PropertyException exc)
        {
        }
    }

    /**
     * <p>Creates the test suite.</p>
     * @return A test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestPropertyManager.class);
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
     * <p>Test add property set definition.</p>
     */
    public void testAddPropertySetDef() throws PropertyException
    {
        try
        {
            pms.addPropertySetDef("propertyset0", USER_PROPERTY_SET_TYPE);
        }
        catch (PropertyException pex)
        {
            // Property set defintion already exists.
            assertTrue("property set definition already exists. exception caught: " + pex, false);
        }

        destroyPropertySetDefTestObject(false);
    }

    /**
     * <p>Test update the property set definition.</p>
     */
    public void testUpdatePropertySetDef() throws PropertyException
    {
        try
        {
            initPropertySetDefTestObject(false);
        }
        catch (PropertyException pex)
        {
            assertTrue("could not initialize property set definition. exception caught: " + pex, false);
        }

        try
        {
            pms.updatePropertySetDef("propertysetupdate0", "propertyset0", USER_PROPERTY_SET_TYPE);
            Collection propertySetsByType = pms.getAllPropertySetsByType(USER_PROPERTY_SET_TYPE);
            //updatedPropertySetDefId = pms.getPropertySetDefIdByType("propertysetupdate0", USER_PROPERTY_SET_TYPE);
            assertTrue(
                "should contain property set definition named propertysetupdate0.",
                propertySetsByType.contains("propertysetupdate0"));
            pms.updatePropertySetDef("propertyset0", "propertysetupdate0", USER_PROPERTY_SET_TYPE);
        }
        catch (PropertyException pex)
        {
            assertTrue("could not get property set definition id. exception caught: " + pex, false);
        }

        destroyPropertySetDefTestObject(false);
    }
    
        /**
         * <p>Test get all property sets by types.</p>
         */
        public void testGetAllPropertySetByType() throws PropertyException
        {
            try
            {
                initPropertySetDefTestObject(true);
            }
            catch (PropertyException pex)
            {
                assertTrue("could not initialize property set definition. exception caught: " + pex, false);
            }
    
            try
            {
                Collection propertySetsByType = pms.getAllPropertySetsByType(USER_PROPERTY_SET_TYPE);
                assertTrue("property set should contain: propertyset0, ", propertySetsByType.contains("propertyset0"));
                assertTrue("property set should contain: propertyset1, ", propertySetsByType.contains("propertyset1"));
                assertTrue("property set should contain: propertyset2, ", propertySetsByType.contains("propertyset2"));
            }
            catch (PropertyException pex)
            {
                assertTrue("could not get property set map: " + pex, false);
            }
    
            destroyPropertySetDefTestObject(true);
        }
    
        /**
         * <p>Test remove property set definition.</p>
         */
        public void testRemovePropertySetDef()
        {
            try
            {
                initPropertySetDefTestObject(false);
            }
            catch (PropertyException pex)
            {
                assertTrue("could not initialize property set definition. exception caught: " + pex, false);
            }
    
            try
            {
                pms.removePropertySetDef("propertyset0", USER_PROPERTY_SET_TYPE);
            }
            catch (PropertyException pex)
            {
                assertTrue("could not remove property set definition. exception caught: " + pex, false);
            }
    
            try
            {
                Collection propertySetsByType = pms.getAllPropertySetsByType(USER_PROPERTY_SET_TYPE);
                assertFalse("property set not should contain: propertyset0, ", propertySetsByType.contains("propertyset0"));
            }
            catch (PropertyException pex)
            {
                assertTrue("could not find property set type. exception caught: " + pex, false);
            }
        }
    
        /**
         * <p>Test add property keys to a set.</p>
         */
        public void testAddUpdatePropertyKeys() throws PropertyException
        {
            try
            {
                initPropertySetDefTestObject(true);
            }
            catch (PropertyException pex)
            {
                assertTrue("could not initialize property set definition. exception caught: " + pex, false);
            }
    
            try
            {
                pms.updatePropertyKey("propertyNameUpdate0", "propertyName0", "propertyset0", USER_PROPERTY_SET_TYPE);
                Collection updatedPropertyKeys = pms.getPropertyKeysBySetDef("propertyset0", USER_PROPERTY_SET_TYPE);
                assertTrue(
                    "updated property set map should contain: propertyNameUpdate0.",
                    updatedPropertyKeys.contains("propertyNameUpdate0"));
            }
            catch (PropertyException pex)
            {
                assertTrue("could not get updated property set map: " + pex, false);
            }
    
            destroyPropertySetDefTestObject(true);
        }
    
//        /**
//         * <p>Test remove property keys.</p>
//         */
//        public void testRemovePropertyKeys()
//        {
//            String userNodeName = "user";
//            String principalNodeName = "principal";
//            String propertySetNodeName = "propertyset1";
//            String fullPropertySetPath = "/" + userNodeName + "/" + principalNodeName + "/" + propertySetNodeName;
//            int[] propertySetDefs = new int[1];
//    
//            try
//            {
//                // Create a node.
//                Preferences prefs1 = Preferences.userRoot().node(fullPropertySetPath);
//                // Create the set definition.
//                pms.addPropertySetDef("propertyset0", USER_PROPERTY_SET_TYPE);
//    
//                // Build a few property keys.
//                Collection propertyKeys = new ArrayList(1);
//    
//                Map propertyKey0 = new HashMap();
//                propertyKey0.put(PropertyManager.PROPERTYKEY_NAME, "propertyName0");
//                propertyKey0.put(PropertyManager.PROPERTYKEY_TYPE, new Short("0"));
//    
//                propertyKeys.add(propertyKey0);
//                pms.addPropertyKeys("propertyset0", USER_PROPERTY_SET_TYPE, propertyKeys);
//    
//                prefs1.put("propertyName0", "true");
//            }
//            catch (PropertyException pex)
//            {
//                assertTrue("could not add property keys: " + pex, false);
//            }
//    
//            // Now let's remove the keys.
//            try
//            {
//                for (Iterator i = (pms.getPropertyKeysBySetDef(propertySetDefs[0]).keySet()).iterator(); i.hasNext();)
//                {
//                    pms.removePropertyKey(((Integer) i.next()).intValue());
//                }
//            }
//            catch (PropertyException pex)
//            {
//                assertTrue("could not remove property keys: " + pex, false);
//            }
//    
//            // Let's verify it was removed.
//            try
//            {
//                Map propKeysCol = pms.getPropertyKeysBySetDef(propertySetDefs[0]);
//                assertEquals("expected property key map size == 0, ", 0, propKeysCol.size());
//            }
//            catch (PropertyException pex)
//            {
//                assertTrue("could not get property keys: " + pex, false);
//            }
//    
//            // Clean up everything else.
//            try
//            {
//                Preferences prefs2 = Preferences.userRoot().node("/" + userNodeName);
//                prefs2.removeNode();
//            }
//            catch (BackingStoreException bse)
//            {
//                assertTrue("backing store exception: " + bse, false);
//            }
//            try
//            {
//                pms.removePropertySetDef(propertySetDefs[0]);
//            }
//            catch (PropertyException pex)
//            {
//                assertTrue("could not remove property set definition: " + pex, false);
//            }
//    
//        }

    /**
     * <p>Init property set definition object.</p>
     */
    protected void initPropertySetDefTestObject(boolean isAll) throws PropertyException
    {
        // Create the set definition.
        pms.addPropertySetDef("propertyset0", USER_PROPERTY_SET_TYPE);

        if (isAll)
        {
            pms.addPropertySetDef("propertyset1", USER_PROPERTY_SET_TYPE);
            pms.addPropertySetDef("propertyset2", USER_PROPERTY_SET_TYPE);
            pms.addPropertySetDef("propertyset3", SYSTEM_PROPERTY_SET_TYPE);
        }

        // Build a few property keys.
        Collection propertyKeys = new ArrayList();

        Map propertyKey0 = new HashMap();
        propertyKey0.put(PropertyManager.PROPERTYKEY_NAME, "propertyName0");
        propertyKey0.put(PropertyManager.PROPERTYKEY_TYPE, new Short("0"));

        propertyKeys.add(propertyKey0);

        Map propertyKey1 = new HashMap();
        propertyKey1.put(PropertyManager.PROPERTYKEY_NAME, "propertyName1");
        propertyKey1.put(PropertyManager.PROPERTYKEY_TYPE, new Short("1"));

        propertyKeys.add(propertyKey1);

        Map propertyKey2 = new HashMap();
        propertyKey2.put(PropertyManager.PROPERTYKEY_NAME, "propertyName2");
        propertyKey2.put(PropertyManager.PROPERTYKEY_TYPE, new Short("2"));

        propertyKeys.add(propertyKey2);

        Map propertyKey3 = new HashMap();
        propertyKey3.put(PropertyManager.PROPERTYKEY_NAME, "propertyName3");
        propertyKey3.put(PropertyManager.PROPERTYKEY_TYPE, new Short("3"));

        propertyKeys.add(propertyKey3);

        pms.addPropertyKeys("propertyset0", USER_PROPERTY_SET_TYPE, propertyKeys);
    }

    /**
     * <p>Destroy profile test object.</p>
     */
    protected void destroyPropertySetDefTestObject(boolean isAll) throws PropertyException
    {
        pms.removePropertySetDef("propertyset0", USER_PROPERTY_SET_TYPE);

        if (isAll)
        {
            pms.removePropertySetDef("propertyset1", USER_PROPERTY_SET_TYPE);
            pms.removePropertySetDef("propertyset2", USER_PROPERTY_SET_TYPE);
            pms.removePropertySetDef("propertyset3", SYSTEM_PROPERTY_SET_TYPE);
        }
    }

}
