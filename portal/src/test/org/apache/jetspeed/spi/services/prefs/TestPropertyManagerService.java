/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.spi.services.prefs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.spi.services.prefs.impl.PropertyException;
import org.apache.jetspeed.test.JetspeedTest;

/**
 * <p>Unit testing for {@link PropertyManagerService}.</p>
 *
 * @author <a href="dlestrat@yahoo.com">David Le Strat</a>
 */
public class TestPropertyManagerService extends JetspeedTest
{

    private PropertyManagerService service = null;

    /**
     * <p>Defines property set types.</p>
     */
    private final static short USER_PROPERTY_SET_TYPE = 0;
    private final static short SYSTEM_PROPERTY_SET_TYPE = 1;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestPropertyManagerService(String testName)
    {
        super(testName);
    }

    /**
     * <p>Start the tests.</p>
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestPropertyManagerService.class.getName()});
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp()
    {
        super.setUp();
        destroyPropertySetDefTestObject(true);
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown()
    {
        super.tearDown();
        destroyPropertySetDefTestObject(true);
    }

    /**
     * <p>Creates the test suite.</p>
     * @return A test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        return new TestSuite(TestPropertyManagerService.class);
    }

    /**
     * <p>Returns the {@link PropertyManagerService}.</p>
     * @return The PropertyManagerService.
     */
    protected PropertyManagerService getService()
    {
        if (service == null)
        {
            service = (PropertyManagerService) CommonPortletServices.getPortalService(PropertyManagerService.SERVICE_NAME);
        }
        return service;
    }

    /**
     * <p>Test that a {@link PropertyManagerService} was returned.</p>
     */
    public void testService()
    {
        assertNotNull(getService());
    }

    /**
     * <p>Test add property set definition.</p>
     */
    public void testAddPropertySetDef()
    {
        PropertyManagerService pms = getService();
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
    public void testUpdatePropertySetDef()
    {
        PropertyManagerService pms = getService();

        int[] propertySetDefs = null;
        try
        {
            propertySetDefs = initPropertySetDefTestObject(false);
        }
        catch (PropertyException pex)
        {
            assertTrue("could not initialize property set definition. exception caught: " + pex, false);
        }

        pms.updatePropertySetDef(propertySetDefs[0], "propertysetupdate0", USER_PROPERTY_SET_TYPE);

        int updatedPropertySetDefId = -1;
        try
        {
            updatedPropertySetDefId = pms.getPropertySetDefIdByType("propertysetupdate0", USER_PROPERTY_SET_TYPE);
            assertEquals(
                "expected property set def id == " + propertySetDefs[0] + ", ",
                propertySetDefs[0],
                updatedPropertySetDefId);
        }
        catch (PropertyException pex)
        {
            assertTrue("could not get property set definition id. exception caught: " + pex, false);
        }

        pms.updatePropertySetDef(updatedPropertySetDefId, "propertyset0", USER_PROPERTY_SET_TYPE);
        destroyPropertySetDefTestObject(false);
    }

    /**
     * <p>Test get all property sets by types.</p>
     */
    public void testGetAllPropertySetByType()
    {
        PropertyManagerService pms = getService();

        int[] propertySetDefs = null;
        try
        {
            propertySetDefs = initPropertySetDefTestObject(true);
        }
        catch (PropertyException pex)
        {
            assertTrue("could not initialize property set definition. exception caught: " + pex, false);
        }

        try
        {
            Map propSetsMap = pms.getAllPropertySetsByType(USER_PROPERTY_SET_TYPE);
            assertTrue("property set map should contain: propertyset0, ", propSetsMap.containsValue("propertyset0"));
            assertTrue("property set map should contain: propertyset1, ", propSetsMap.containsValue("propertyset1"));
            assertTrue("property set map should contain: propertyset2, ", propSetsMap.containsValue("propertyset2"));
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
        PropertyManagerService pms = getService();

        int[] propertySetDefs = null;
        try
        {
            propertySetDefs = initPropertySetDefTestObject(false);
        }
        catch (PropertyException pex)
        {
            assertTrue("could not initialize property set definition. exception caught: " + pex, false);
        }

        try
        {
            pms.removePropertySetDef(propertySetDefs[0]);
        }
        catch (PropertyException pex)
        {
            assertTrue("could not remove property set definition. exception caught: " + pex, false);
        }

        int propertySetDefId = -1;
        try
        {
            propertySetDefId = pms.getPropertySetDefIdByType("propertyset0", USER_PROPERTY_SET_TYPE);
        }
        catch (PropertyException pex)
        {
            assertTrue(
                "caught expected exception while getting property set definition id for propertyset0. exception caught: " + pex,
                true);
        }
    }

    /**
     * <p>Test add property keys to a set.</p>
     */
    public void testAddUpdatePropertyKeys()
    {
        PropertyManagerService pms = getService();

        int[] propertySetDefs = null;
        try
        {
            propertySetDefs = initPropertySetDefTestObject(true);
        }
        catch (PropertyException pex)
        {
            assertTrue("could not initialize property set definition. exception caught: " + pex, false);
        }

        try
        {
            Integer ppkId =
                (Integer) TestUtils.getMapKeyByValue(
                    pms.getPropertyKeysBySetDef(propertySetDefs[0]),
                    "propertyName0".toLowerCase());
            pms.updatePropertyKey(ppkId.intValue(), "PropertyNameUpdate0");
            Map updatedPropertyMap = pms.getPropertyKeysBySetDef(propertySetDefs[0]);
            assertTrue(
                "updated property set map should contain: PropertyNameUpdate0 in lower case, ",
                updatedPropertyMap.containsValue("PropertyNameUpdate0"));
        }
        catch (PropertyException pex)
        {
            assertTrue("could not get updated property set map: " + pex, false);
        }

        destroyPropertySetDefTestObject(true);
    }

    /**
     * <p>Test remove property keys.</p>
     */
    public void testRemovePropertyKeys()
    {
        PropertyManagerService propms = getService();
        String userNodeName = "user";
        String principalNodeName = "principal";
        String propertySetNodeName = "propertyset1";
        String fullPropertySetPath = "/" + userNodeName + "/" + principalNodeName + "/" + propertySetNodeName;
        int[] propertySetDefs = new int[1];

        try
        {
            // Create a node.
            Preferences prefs1 = Preferences.userRoot().node(fullPropertySetPath);
            // Create the set definition.
            propertySetDefs[0] = propms.addPropertySetDef("propertyset0", USER_PROPERTY_SET_TYPE);

            // Build a few property keys.
            Collection propertyKeys = new ArrayList(1);

            Map propertyKey0 = new HashMap();
            propertyKey0.put(PropertyManagerService.PROPERTYKEY_NAME, "propertyName0");
            propertyKey0.put(PropertyManagerService.PROPERTYKEY_TYPE, new Short("0"));

            propertyKeys.add(propertyKey0);
            propms.addPropertyKeys(propertySetDefs[0], propertyKeys);

            prefs1.put("propertyName0", "true");
        }
        catch (PropertyException pex)
        {
            assertTrue("could not add property keys: " + pex, false);
        }

        // Now let's remove the keys.
        try
        {
            for (Iterator i = (propms.getPropertyKeysBySetDef(propertySetDefs[0]).keySet()).iterator(); i.hasNext();)
            {
                propms.removePropertyKey(((Integer) i.next()).intValue());
            }
        }
        catch (PropertyException pex)
        {
            assertTrue("could not remove property keys: " + pex, false);
        }

        // Let's verify it was removed.
        try
        {
            Map propKeysCol = propms.getPropertyKeysBySetDef(propertySetDefs[0]);
            assertEquals("expected property key map size == 0, ", 0, propKeysCol.size());
        }
        catch (PropertyException pex)
        {
            assertTrue("could not get property keys: " + pex, false);
        }

        // Clean up everything else.
        try
        {
            Preferences prefs2 = Preferences.userRoot().node("/" + userNodeName);
            prefs2.removeNode();
        }
        catch (BackingStoreException bse)
        {
            assertTrue("backing store exception: " + bse, false);
        }
        try
        {
            propms.removePropertySetDef(propertySetDefs[0]);
        }
        catch (PropertyException pex)
        {
            assertTrue("could not remove property set definition: " + pex, false);
        }

    }

    /**
     * <p>Init property set definition object.</p>
     */
    protected int[] initPropertySetDefTestObject(boolean isAll) throws PropertyException
    {
        PropertyManagerService pms = getService();

        int[] propertySetDefs = new int[4];
        // Create the set definition.
        propertySetDefs[0] = pms.addPropertySetDef("propertyset0", USER_PROPERTY_SET_TYPE);

        if (isAll)
        {
            propertySetDefs[1] = pms.addPropertySetDef("propertyset1", USER_PROPERTY_SET_TYPE);
            propertySetDefs[2] = pms.addPropertySetDef("propertyset2", USER_PROPERTY_SET_TYPE);
            propertySetDefs[3] = pms.addPropertySetDef("propertyset3", SYSTEM_PROPERTY_SET_TYPE);
        }

        // Build a few property keys.
        Collection propertyKeys = new ArrayList();

        Map propertyKey0 = new HashMap();
        propertyKey0.put(PropertyManagerService.PROPERTYKEY_NAME, "propertyName0");
        propertyKey0.put(PropertyManagerService.PROPERTYKEY_TYPE, new Short("0"));

        propertyKeys.add(propertyKey0);

        Map propertyKey1 = new HashMap();
        propertyKey1.put(PropertyManagerService.PROPERTYKEY_NAME, "propertyName1");
        propertyKey1.put(PropertyManagerService.PROPERTYKEY_TYPE, new Short("1"));

        propertyKeys.add(propertyKey1);

        Map propertyKey2 = new HashMap();
        propertyKey2.put(PropertyManagerService.PROPERTYKEY_NAME, "propertyName2");
        propertyKey2.put(PropertyManagerService.PROPERTYKEY_TYPE, new Short("2"));

        propertyKeys.add(propertyKey2);

        Map propertyKey3 = new HashMap();
        propertyKey3.put(PropertyManagerService.PROPERTYKEY_NAME, "propertyName3");
        propertyKey3.put(PropertyManagerService.PROPERTYKEY_TYPE, new Short("3"));

        propertyKeys.add(propertyKey3);

        pms.addPropertyKeys(propertySetDefs[0], propertyKeys);

        return propertySetDefs;
    }

    /**
     * <p>Destroy profile test object.</p>
     */
    protected void destroyPropertySetDefTestObject(boolean isAll)
    {
        PropertyManagerService pms = getService();
        try
        {
            int propertySetDefId0 = pms.getPropertySetDefIdByType("propertyset0", USER_PROPERTY_SET_TYPE);
            pms.removePropertySetDef(propertySetDefId0);

            if (isAll)
            {
                int propertySetDefId1 = pms.getPropertySetDefIdByType("propertyset1", USER_PROPERTY_SET_TYPE);
                pms.removePropertySetDef(propertySetDefId1);

                int propertySetDefId2 = pms.getPropertySetDefIdByType("propertyset2", USER_PROPERTY_SET_TYPE);
                pms.removePropertySetDef(propertySetDefId2);

                int propertySetDefId3 = pms.getPropertySetDefIdByType("propertyset3", SYSTEM_PROPERTY_SET_TYPE);
                pms.removePropertySetDef(propertySetDefId3);
            }
        }
        catch (PropertyException pex)
        {
        }
    }

}
