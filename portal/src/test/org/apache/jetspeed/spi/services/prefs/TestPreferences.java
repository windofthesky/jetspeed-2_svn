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
 *
 * File Created: org.apache.jetspeed.services.usermgt.TestProfileManagerService.java
 * Creation Date: Oct 6, 2003
 *
 */
package org.apache.jetspeed.spi.services.prefs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.spi.services.prefs.impl.PropertyException;
import org.apache.jetspeed.test.JetspeedTest;

/**
 * <p>Unit testing for {@link Preferences}.</p>
 * 
 * @author <a href="dlestrat@yahoo.com">David Le Strat</a>
 */
public class TestPreferences extends JetspeedTest
{

    private PropertyManagerService propertyMgrService = null;

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
     * <p>Start the tests.</p>
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestPreferences.class.getName()});
    }

    public void setup()
    {
        System.out.println("Setup: Testing the java.util.prefs.Preferences implementation");
    }

    /**
     * <p>Creates the test suite.</p>
     * @return A test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        return new TestSuite(TestPreferences.class);
    }

    /**
     * <p>Returns the {@link PropertyManagerService}.</p>
     * @return The PropertyManagerService.
     */
    protected PropertyManagerService getPropertyManagerService()
    {
        if (propertyMgrService == null)
        {
            propertyMgrService =
                (PropertyManagerService) CommonPortletServices.getPortalService(PropertyManagerService.SERVICE_NAME);
        }
        return propertyMgrService;
    }

    /**
     * <p>Test user root.</p>
     */
    public void testUserRoot()
    {
        Preferences prefs = Preferences.userRoot();
        if ((null == prefs) || (!(prefs.absolutePath().equals("/"))))
        {
            assertTrue(false);
        }
    }

    /**
     * <p>Test system root.</p>
     */
    public void testSystemRoot()
    {
        Preferences prefs = Preferences.systemRoot();
        if ((null == prefs) || (!(prefs.absolutePath().equals("/"))))
        {
            assertTrue(false);
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
                assertTrue(false);
            }
        }
        catch (BackingStoreException bse)
        {
            assertTrue(false);
        }

        // TODO Test with children.
    }

    /**
     * <p>Test node.</p>
     */
    public void testNode()
    {
        String testRelNodeName = "rn1/srn1";
        String testAbsNodeName0 = "/an1";
        String testAbsNodeName1 = "/san1";
        String testAbsNodeName = testAbsNodeName0 + testAbsNodeName1;

        // Absolute path.
        // 1. The node does not exist. Create it.
        Preferences prefs0 = Preferences.userRoot().node(testAbsNodeName);
        if ((null == prefs0) || (!(prefs0.absolutePath().equals(testAbsNodeName))))
        {
            assertTrue(false);
        }

        // 2. If node exists. Get it.
        Preferences prefs1 = Preferences.userRoot().node(testAbsNodeName);
        if ((null == prefs1) || (!(prefs1.absolutePath().equals(testAbsNodeName))))
        {
            assertTrue(false);
        }

        //Relative path.
        Preferences prefs3 = Preferences.userRoot().node(testAbsNodeName0);
        Preferences prefs4 = prefs3.node(testRelNodeName);
        if ((null == prefs4) || (!(prefs4.absolutePath().equals(testAbsNodeName0 + "/" + testRelNodeName))))
        {
            assertTrue(false);
        }

        // Remove all nodes.
        try
        {
            prefs3.removeNode();
        }
        catch (BackingStoreException bse)
        {
            assertTrue(false);
        }
    }

    /**
     * <p>Test adding properties to a property set node.</p>
     */
    public void testProperty()
    {
        String userNodeName = "user";
        String principalNodeName = "principal";
        String propertySetNodeName = "propertyset1";
        String fullPropertySetPath = "/" + userNodeName + "/" + principalNodeName + "/" + propertySetNodeName;
        int[] propertySetDefIds = new int[2];
        
        try
        {
            initPropertySetDefTestObject();
        }
        catch (PropertyException pe)
        {
            assertTrue(false);
        }

        // 1. Current node is not defined as property set.
        Preferences prefs0 = Preferences.userRoot();
        prefs0.put("propertyName00", "true");
        String prop0 = prefs0.get("propertyName00", null);
        if (null != prop0)
        {
            assertTrue(false);
        }
        
        // 2. Current node is defined as property set.
        Preferences prefs1 = Preferences.userRoot().node(fullPropertySetPath);
        prefs1.put("propertyName00", "true");
        String prop1 = prefs1.get("propertyName00", null);
        assertTrue("expected prop1 == true, " + prop1, prop1.equals("true"));
        
        // Test remove property.
        prefs1.remove("propertyName00");
        prop1 = prefs1.get("propertyName00", null);
        if (null != prop1)
        {
            assertTrue(false);
        }

        // Remove all nodes with properties assigned to property sets.
        prefs1.put("propertyName00", "true");
        prop1 = prefs1.get("propertyName00", null);
        if (!prop1.equals("true"))
        {
            assertTrue(false);
        }

        try
        {
            Preferences prefs2 = Preferences.userRoot().node("/" + userNodeName);
            prefs2.removeNode();
        }
        catch (BackingStoreException bse)
        {
            assertTrue(false);
        }
        destroyPropertySetDefTestObject();
    }



    /**
     * <p>Init property set definition object.</p>
     */
    protected int[] initPropertySetDefTestObject() throws PropertyException
    {

        PropertyManagerService pms = getPropertyManagerService();
        int[] propertySetDefIds = new int[2];
        // Create the set definition.
        propertySetDefIds[0] = pms.addPropertySetDef("propertyset1", USER_PROPERTY_SET_TYPE);
        // Build a few property keys.
        Collection propertyKeys = new ArrayList();
        Map propertyKey00 = new HashMap();
        propertyKey00.put(PropertyManagerService.PROPERTYKEY_NAME, "propertyName00");
        propertyKey00.put(PropertyManagerService.PROPERTYKEY_TYPE, new Short("0"));
        propertyKeys.add(propertyKey00);
        Map propertyKey01 = new HashMap();
        propertyKey01.put(PropertyManagerService.PROPERTYKEY_NAME, "propertyName01");
        propertyKey01.put(PropertyManagerService.PROPERTYKEY_TYPE, new Short("1"));
        propertyKeys.add(propertyKey01);
        Map propertyKey02 = new HashMap();
        propertyKey02.put(PropertyManagerService.PROPERTYKEY_NAME, "propertyName02");
        propertyKey02.put(PropertyManagerService.PROPERTYKEY_TYPE, new Short("2"));
        propertyKeys.add(propertyKey02);
        Map propertyKey03 = new HashMap();
        propertyKey03.put(PropertyManagerService.PROPERTYKEY_NAME, "propertyName03");
        propertyKey03.put(PropertyManagerService.PROPERTYKEY_TYPE, new Short("3"));
        propertyKeys.add(propertyKey03);
        pms.addPropertyKeys(propertySetDefIds[0], propertyKeys);
        // Add another set.
        // Create the set definition.
        propertySetDefIds[1] = pms.addPropertySetDef("propertyset2", USER_PROPERTY_SET_TYPE);
        // Build a few property keys.
        Collection propertyKeys1 = new ArrayList();
        Map propertyKey10 = new HashMap();
        propertyKey10.put(PropertyManagerService.PROPERTYKEY_NAME, "propertyName10");
        propertyKey10.put(PropertyManagerService.PROPERTYKEY_TYPE, new Short("0"));
        propertyKeys1.add(propertyKey10);
        Map propertyKey11 = new HashMap();
        propertyKey11.put(PropertyManagerService.PROPERTYKEY_NAME, "propertyName11");
        propertyKey11.put(PropertyManagerService.PROPERTYKEY_TYPE, new Short("1"));
        propertyKeys1.add(propertyKey11);
        pms.addPropertyKeys(propertySetDefIds[1], propertyKeys1);
        return propertySetDefIds;
    }

    /**
     * <p>Destroy profile test object.</p>
     */
    protected void destroyPropertySetDefTestObject()
    {
        PropertyManagerService pms = getPropertyManagerService();
        try
        {
            int propertySetDefId1 = pms.getPropertySetDefIdByType("propertyset1", USER_PROPERTY_SET_TYPE);
            pms.removePropertySetDef(propertySetDefId1);
            int propertySetDefId2 = pms.getPropertySetDefIdByType("propertyset2", USER_PROPERTY_SET_TYPE);
            pms.removePropertySetDef(propertySetDefId2);
        }
        catch (PropertyException pex)
        {
        }
    }

    /**
     * <p>Init property values collection object.</p>
     * @param propertySetDefId The property set definition id.
     * @param propertyKeyIds The property key ids.
     * @return A collection of property values.
     */
    /*
    protected Collection initPropertyValuesCollectionTestObject(Map propertyKeys)
    {
        PropertyManagerService propms = getPropertyManagerService();
        Collection propertyValues = new ArrayList(4);
        // This is a boolean property.
        Integer ppkId00 = (Integer) TestUtils.getMapKeyByValue(propertyKeys, "propertyName00");
        if (null != ppkId00)
        {
            Map propertyValue00 = new HashMap();
            propertyValue00.put(ProfileManagerService.PROPERTY_KEY_ID, ppkId00);
            propertyValue00.put(ProfileManagerService.PROPERTY_VALUE, new Boolean("true"));
            propertyValues.add(propertyValue00);
        } // This is a long property
        Integer ppkId01 = (Integer) TestUtils.getMapKeyByValue(propertyKeys, "propertyName01");
        if (null != ppkId01)
        {
            Map propertyValue01 = new HashMap();
            propertyValue01.put(ProfileManagerService.PROPERTY_KEY_ID, ppkId01);
            propertyValue01.put(ProfileManagerService.PROPERTY_VALUE, new Long("123"));
            propertyValues.add(propertyValue01);
        } // This is a double property.
        Integer ppkId02 = (Integer) TestUtils.getMapKeyByValue(propertyKeys, "propertyName02");
        if (null != ppkId02)
        {
            Map propertyValue02 = new HashMap();
            propertyValue02.put(ProfileManagerService.PROPERTY_KEY_ID, ppkId02);
            propertyValue02.put(ProfileManagerService.PROPERTY_VALUE, new Double("123.23"));
            propertyValues.add(propertyValue02);
        } // This is a text property.
        Integer ppkId03 = (Integer) TestUtils.getMapKeyByValue(propertyKeys, "propertyName03");
        if (null != ppkId03)
        {
            Map propertyValue03 = new HashMap();
            propertyValue03.put(ProfileManagerService.PROPERTY_KEY_ID, ppkId03);
            propertyValue03.put(ProfileManagerService.PROPERTY_VALUE, "value3");
            propertyValues.add(propertyValue03);
        } // This is a boolean property.
        Integer ppkId10 = (Integer) TestUtils.getMapKeyByValue(propertyKeys, "propertyName10");
        if (null != ppkId10)
        {
            Map propertyValue10 = new HashMap();
            propertyValue10.put(ProfileManagerService.PROPERTY_KEY_ID, ppkId10);
            propertyValue10.put(ProfileManagerService.PROPERTY_VALUE, new Boolean("true"));
            propertyValues.add(propertyValue10);
        } // This is a long property
        Integer ppkId11 = (Integer) TestUtils.getMapKeyByValue(propertyKeys, "propertyName11");
        if (null != ppkId11)
        {
            Map propertyValue11 = new HashMap();
            propertyValue11.put(ProfileManagerService.PROPERTY_KEY_ID, ppkId11);
            propertyValue11.put(ProfileManagerService.PROPERTY_VALUE, new Long("123"));
            propertyValues.add(propertyValue11);
        }
    
        return propertyValues;
    }
    */

}
