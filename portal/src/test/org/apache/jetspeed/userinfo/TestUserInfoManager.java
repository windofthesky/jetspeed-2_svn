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
package org.apache.jetspeed.userinfo;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.portlet.PortletRequest;

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ComponentAwareTestSuite;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.prefs.PropertyManager;
import org.apache.jetspeed.prefs.impl.PropertyException;
import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.tools.pamanager.PortletDescriptorUtilities;

import org.picocontainer.MutablePicoContainer;

/**
 * <p>Unit test for {@link UserInfoManager}</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestUserInfoManager extends AbstractComponentAwareTestCase
{
    /** The mutable pico container. */
    private MutablePicoContainer container;

    /** The property manager. */
    private static PropertyManager pms;

    /** The user info manager. */
    private UserInfoManager uim;

    /** The user manager. */
    private UserManager ums;
    
    /** The portlet registry. */
    private static PortletRegistryComponent registry;
    
    /** The persistence store. */
    private PersistenceStore store;

    /**
     * <p>Defines the testcase name for JUnit.</p>
     *
     * @param name the testcase's name.
     */
    public TestUserInfoManager(String name)
    {
        super(name);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        container = (MutablePicoContainer) getContainer();
        uim = (UserInfoManager) container.getComponentInstance(UserInfoManager.class);
        pms = (PropertyManager) container.getComponentInstance(PropertyManager.class);
        ums = (UserManager) container.getComponentInstance(UserManager.class);
        registry = (PortletRegistryComponent) container.getComponentInstance(PortletRegistryComponent.class);
        store = registry.getPersistenceStore();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * <p>Creates the test suite.</p>
     *
     * @return A test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestUserInfoManager.class);
        suite.setScript("org/apache/jetspeed/containers/test-userinfo-container.groovy");
        return suite;
    }

    /**
     * <p>Test the container.</p>
     */
    public void testContainer()
    {
        assertNotNull(container);
    }

    /** Test set user info map. */
    public void testSetUserInfoMap() throws Exception
    {
        MutablePortletApplication app =
            PortletDescriptorUtilities.loadPortletDescriptor("./test/testdata/deploy/portlet.xml", "unit-test");
        assertNotNull("App is null", app);

        // persist the app
        try
        {
            store.getTransaction().begin();
            registry.registerPortletApplication(app);
            store.getTransaction().commit();
        }
        catch (Exception e)
        {
            String msg =
                "Unable to register portlet application, " + app.getName() + ", through the portlet registry: " + e.toString();
            store.getTransaction().rollback();
            throw new Exception(msg, e);
        }

        RequestContext request = initRequestContext("anon");

        // There are no preferences associated to the user profile.
        Map userInfo = uim.getUserInfoMap(app.getId(), request);
        assertNull(PortletRequest.USER_INFO + " is null", userInfo);

        // The user has preferences associated to the user profile.
        initUser();
        request = initRequestContext("test");
        userInfo = uim.getUserInfoMap(app.getId(), request);
        assertNotNull(PortletRequest.USER_INFO + " should not be null", userInfo);
        assertEquals("should contain user.name.given", "Test Dude", (String) userInfo.get("user.name.given"));
        assertEquals("should contain user.name.family", "Dudley", (String) userInfo.get("user.name.family"));
        assertNull("should not contain user.home-info.online.email", userInfo.get("user.home-info.online.email"));

        // remove the app
        try
        {
            store.getTransaction().begin();
            registry.removeApplication(app);
            store.getTransaction().commit();
        }
        catch (Exception e)
        {
            String msg =
                "Unable to remove portlet application, " + app.getName() + ", through the portlet registry: " + e.toString();
            throw new Exception(msg, e);
        }
                
        destroyUser();
    }

    /**
     * <p>Initialize the mock request context.</p>
     * @param username The username.
     * @return The request context.
     */
    private RequestContext initRequestContext(String username)
    {
        RequestContext request = new MockRequestContext("default-other");

        request.setSubject(SecurityHelper.createSubject(username));
        return request;
    }

    /**
     * <p>Init test user.</p>
     */
    private void initUser()
    {
        User user = null;
        try
        {
            ums.addUser("test", "password");
            user = ums.getUser("test");
        }
        catch (SecurityException sex)
        {
            assertTrue("user exists. should not have thrown an exception.", false);
        }
        Preferences userInfoPrefs = user.getPreferences().node("userinfo");
        Map propertyKeys = initPropertyKeysMap();
        try
        {
            pms.addPropertyKeys(userInfoPrefs, propertyKeys);
        }
        catch (PropertyException pex)
        {
            assertTrue("should have add propertyKeys. should not have thrown an exception.", false);
        }
        userInfoPrefs.put("user.name.given", "Test Dude");
        userInfoPrefs.put("user.name.family", "Dudley");
    }

    /**
     * <p>Init property property keys map.</p>
     */
    protected Map initPropertyKeysMap()
    {
        // Build a few property keys.
        Map propertyKeys = new HashMap();
        propertyKeys.put("user.name.given", new Integer(Property.STRING_TYPE));
        propertyKeys.put("user.name.family", new Integer(Property.STRING_TYPE));

        return propertyKeys;
    }

    /**
     * <p>Destroy user test object.</p>
     */
    protected void destroyUser()
    {
        try
        {
            if (ums.userExists("test"))
            {
                ums.removeUser("test");
            }
        }
        catch (SecurityException sex)
        {
            System.out.println("could not remove test users. exception caught: " + sex);
        }
    }

}