/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.JetspeedSubjectFactory;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.impl.UserImpl;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;
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
    private MutablePortletApplication portletApp;
    private UserInfoManager single;
    private PortletRegistry portletRegistry;
    
    public void setUp() throws Exception
    {
        super.setUp();

        single = (UserInfoManager) scm.getComponent("org.apache.jetspeed.userinfo.UserInfoManager");
        portletRegistry = (PortletRegistry) scm.getComponent("portletRegistry");
    }

    public void tearDown() throws Exception
    {
        cleanUp();
        super.tearDown();
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
        PortletApplicationDescriptor pad = new PortletApplicationDescriptor(new FileReader(getBaseDir()+"src/test/testdata/deploy/portlet.xml"), "unit-test");
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
        Map<String, String> userInfo = uim.getUserInfoMap(portletApp.getId(), request);
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
        ExtendedPortletMetadata extMetaData = new ExtendedPortletMetadata(new FileReader(getBaseDir()+"src/test/testdata/deploy/jetspeed-portlet.xml"), portletApp);
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
        request.setSubject(JetspeedSubjectFactory.createSubject(new UserImpl(username), null, null, null));
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
            ums.addUser("test");
            user = ums.getUser("test");
        }
        catch (SecurityException sex)
        {
            assertTrue("user exists. should not have thrown an exception.", false);
        }
        
        SecurityAttributes attributes = user.getSecurityAttributes();
        attributes.getAttribute("user.name.given", true).setStringValue("Test Dude");
        attributes.getAttribute("user.name.family", true).setStringValue("Dudley");
        
        ums.updateUser(user);
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
        confList.add("jetspeed-base.xml");
        confList.add("jetspeed-properties.xml");
        confList.add("page-manager.xml");
        confList.add("registry.xml");
        confList.add("rc3.xml");
        confList.add("JETSPEED-INF/spring/user-info.xml");
        confList.add("prefs.xml");
        confList.add("cache.xml");
        return (String[]) confList.toArray(new String[1]);
    }

    protected String getBeanDefinitionFilterCategories()
    {
        return super.getBeanDefinitionFilterCategories()+",xmlPageManager";
    }
}
