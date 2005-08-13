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

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.portlet.PortletRequest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.prefs.PropertyException;
import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;
import org.apache.jetspeed.testhelpers.BuildPropertiesHelper;
import org.apache.jetspeed.util.descriptor.ExtendedPortletMetadata;
import org.apache.jetspeed.util.descriptor.PortletApplicationDescriptor;

/**
 * <p>
 * Unit test for {@link UserInfoManager}
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestUserInfoManager extends AbstractSecurityTestcase
{

    /** The test MutablePortletApplication. */
    private MutablePortletApplication portletApp;

    /** The build properties helper. */
    private BuildPropertiesHelper buildProperties = new BuildPropertiesHelper();

    /** The user info manager. */
    private UserInfoManager single;

    /** The Multi Source user info manager */
    private UserInfoManager multi;

    private PortletRegistry portletRegistry;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();

        single = (UserInfoManager) ctx.getBean("org.apache.jetspeed.userinfo.UserInfoManager");
        portletRegistry = (PortletRegistry) ctx.getBean("portletRegistry");
        multi = (UserInfoManager) ctx.getBean("org.apache.jetspeed.userinfo.MultiUserInfoManager");
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
        cleanUp();
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestUserInfoManager.class);
    }

    /** Test set user info map. * */
    public void testSingleSetUserInfoMap() throws Exception
    {
        innerTestSetUserInfoMap(single);
    }

    // public void testMultiSetUserInfoMap() throws Exception
    // {
    // innerTestSetUserInfoMap(multi);
    // }

    private void innerTestSetUserInfoMap(UserInfoManager uim) throws Exception
    {
        PortletApplicationDescriptor pad = new PortletApplicationDescriptor(new FileReader(buildProperties
                .getUserProperty("org.apache.jetspeed.portal.home")
                + "/components/portal/test/testdata/deploy/portlet.xml"), "unit-test");
        portletApp = pad.createPortletApplication();
        assertNotNull("App is null", portletApp);

        // persist the app
        try
        {
            portletRegistry.registerPortletApplication(portletApp);
        }
        catch (Exception e)
        {
            String msg = "Unable to register portlet application, " + portletApp.getName()
                    + ", through the portlet portletRegistry: " + e.toString();

            throw new Exception(msg, e);
        }

        RequestContext request = initRequestContext("anon");

        // Without linked attributes
        // There are no preferences associated to the user profile.
        Map userInfo = uim.getUserInfoMap(portletApp.getId(), request);
        assertNull(PortletRequest.USER_INFO + " is null", userInfo);

        // The user has preferences associated to the user profile.
        initUser();
        request = initRequestContext("test");
        userInfo = uim.getUserInfoMap(portletApp.getId(), request);
        assertNotNull(PortletRequest.USER_INFO + " should not be null", userInfo);
        assertEquals("should contain user.name.given", "Test Dude", (String) userInfo.get("user.name.given"));
        assertEquals("should contain user.name.family", "Dudley", (String) userInfo.get("user.name.family"));
        assertNull("should not contain user.home-info.online.email", userInfo.get("user.home-info.online.email"));

        // With linked attributes
        ExtendedPortletMetadata extMetaData = new ExtendedPortletMetadata(new FileReader(buildProperties
                .getUserProperty("org.apache.jetspeed.portal.home")
                + "/components/portal/test/testdata/deploy/jetspeed-portlet.xml"), portletApp);
        extMetaData.load();

        // persist the app
        try
        {
            portletRegistry.updatePortletApplication(portletApp);
        }
        catch (Exception e)
        {
            String msg = "Unable to update portlet application, " + portletApp.getName()
                    + ", through the portlet portletRegistry: " + e.toString();

            throw new Exception(msg, e);
        }

        userInfo = uim.getUserInfoMap(portletApp.getId(), request);
        assertNotNull(PortletRequest.USER_INFO + " should not be null", userInfo);
        assertEquals("should contain user-name-given", "Test Dude", (String) userInfo.get("user-name-given"));
        assertEquals("should contain user-name-family", "Dudley", (String) userInfo.get("user-name-family"));
    }

    /**
     * <p>
     * Initialize the mock request context.
     * </p>
     * 
     * @param username
     *            The username.
     * @return The request context.
     */
    private RequestContext initRequestContext(String username)
    {
        RequestContext request = new MockRequestContext("default-other");

        request.setSubject(SecurityHelper.createSubject(username));
        return request;
    }

    /**
     * <p>
     * Init test user.
     * </p>
     */
    private void initUser() throws Exception
    {
        User user = null;
        try
        {
            ums.addUser("test", "password01");
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
            propertyManager.addPropertyKeys(userInfoPrefs, propertyKeys);
        }
        catch (PropertyException pex)
        {
            assertTrue("should have add propertyKeys. should not have thrown an exception.", false);
        }
        userInfoPrefs.put("user.name.given", "Test Dude");
        userInfoPrefs.put("user.name.family", "Dudley");
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
        propertyKeys.put("user.name.given", new Integer(Property.STRING_TYPE));
        propertyKeys.put("user.name.family", new Integer(Property.STRING_TYPE));

        return propertyKeys;
    }

    /**
     * <p>
     * Destroy user test object.
     * </p>
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

    /**
     * <p>
     * Clean up test.
     * </p>
     */
    private void cleanUp() throws Exception
    {
        // remove the app
        if (null != portletApp)
        {
            try
            {
                portletRegistry.removeApplication(portletApp);
            }
            catch (Exception e)
            {
                String msg = "Unable to remove portlet application, " + portletApp.getName()
                        + ", through the portlet portletRegistry: " + e.toString();
                throw new Exception(msg, e);
            }
        }

        destroyUser();
    }

    protected String[] getConfigurations()
    {
        String[] confs = super.getConfigurations();
        List confList = new ArrayList(Arrays.asList(confs));
        confList.add("registry.xml");
        confList.add("JETSPEED-INF/spring/user-info.xml");
        return (String[]) confList.toArray(new String[1]);
    }
}
