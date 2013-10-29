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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.jetspeed.AbstractRequestContextTestCase;
import org.apache.jetspeed.descriptor.JetspeedDescriptorService;
import org.apache.jetspeed.descriptor.JetspeedDescriptorServiceImpl;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.JetspeedSubjectFactory;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.impl.UserImpl;
import org.apache.jetspeed.util.MultiFileChecksumHelper;
import org.apache.pluto.container.impl.PortletAppDescriptorServiceImpl;

import javax.portlet.PortletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Unit test for {@link UserInfoManager}
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestUserInfoManager extends AbstractRequestContextTestCase
{
    private PortletApplication portletApp;
    private UserInfoManager single;
    /** The user manager. */
    protected UserManager ums;
    
    public void setUp() throws Exception
    {
        super.setUp();

        ums = scm.lookupComponent("org.apache.jetspeed.security.UserManager");
        single = scm.lookupComponent("org.apache.jetspeed.userinfo.MultiUserInfoManager");
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
        File webXmlFile = new File(getBaseDir()+"src/test/testdata/deploy/web.xml");
        File portletXmlFile = new File(getBaseDir()+"src/test/testdata/deploy/portlet.xml");
        File jetspeedPortletXmlFile = new File(getBaseDir()+"src/test/testdata/deploy/jetspeed-portlet.xml");
        
        JetspeedDescriptorService descriptorService = new JetspeedDescriptorServiceImpl(new PortletAppDescriptorServiceImpl());
        InputStream webDescriptor = new FileInputStream(webXmlFile);
        InputStream portletDescriptor = new FileInputStream(portletXmlFile);
        InputStream jetspeedPortletDescriptor = new FileInputStream(jetspeedPortletXmlFile);
        ClassLoader paClassLoader = Thread.currentThread().getContextClassLoader();
        
        String appName = "TestRegistry";
        String contextPath = "/TestRegistry";
        long paChecksum = MultiFileChecksumHelper.getChecksum(new File[] {webXmlFile, portletXmlFile, jetspeedPortletXmlFile});
        
        portletApp = descriptorService.read(appName, contextPath, webDescriptor, portletDescriptor, jetspeedPortletDescriptor, paClassLoader);
        portletApp.setName(appName);
        portletApp.setContextPath(contextPath);
        portletApp.setChecksum(paChecksum);
        
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
        Map<String, String> userInfo = uim.getUserInfoMap(portletApp.getName(), request);
        // disabled test: UserInfoManager doesn't return null anymore for a more efficient solution
        // PortletRequest.getAttribute(USER_INFO) == null for unauthorised users but that requires a different test setup 
        // assertNull(PortletRequest.USER_INFO + " is null", userInfo);

        // The user has preferences associated to the user profile.
        initUser();
        request = initRequestContext("test");
        userInfo = uim.getUserInfoMap(portletApp.getName(), request);
        assertNotNull(PortletRequest.USER_INFO + " should not be null", userInfo);
        assertEquals("should contain user-name-given", "Test Dude", (String) userInfo.get("user-name-given"));
        assertEquals("should contain user-name-family", "Dudley", (String) userInfo.get("user-name-family"));
        assertNull("should not contain user.home-info.online.email", userInfo.get("user.home-info.online.email"));

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

        userInfo = uim.getUserInfoMap(portletApp.getName(), request);
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
        User user = null;
        // need to retrieve user from usermanager to retrieve previously configured user attributes
        // if user doesn't exist (e.g. anon user), a transient UserImpl will be used instead
        try
        {
            user = ums.getUser(username);
        }
        catch (SecurityException e)
        {
            user = new UserImpl(username);
        }
        request.setSubject(JetspeedSubjectFactory.createSubject(user, null, null, null));
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
        
        user.getSecurityAttributes().getAttribute("user.name.given", true).setStringValue("Test Dude");
        user.getSecurityAttributes().getAttribute("user.name.family", true).setStringValue("Dudley");
        
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
        List<String> confList = new ArrayList<String>(Arrays.asList(confs));
        confList.add("rc3.xml");
        confList.add("JETSPEED-INF/spring/user-info.xml");
        return (String[]) confList.toArray(new String[1]);
    }
}
