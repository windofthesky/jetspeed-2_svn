/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.impl.PassiveCallbackHandler;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;

/**
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestLoginModule extends JetspeedTest
{
    /** <p>The JAAS login context.</p> */
    private LoginContext loginContext = null;

    /** <p>The user manager service.</p> */
    private UserManagerService ums = null;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestLoginModule(String testName)
    {
        super(testName);
    }

    /**
     * <p>Returns the {@link UserManagerService}.</p>
     * @return The UserManagerService.
     */
    protected UserManagerService getService()
    {
        if (ums == null)
        {
            ums = (UserManagerService) CommonPortletServices.getPortalService(UserManagerService.SERVICE_NAME);
        }
        return ums;
    }

    /**
     * <p>Start the tests.</p>
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestLoginModule.class.getName()});
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        initUserObject();

        // Set up login context.
        try {
            PassiveCallbackHandler pch = new PassiveCallbackHandler("anonlogin", "password");
            loginContext = new LoginContext("Jetspeed", pch);
        }
        catch (LoginException le)
        {
            le.printStackTrace();
            assertTrue("\t\t[TestLoginModule] Failed to setup test.", false);
        }
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
        destroyUserObject();
    }

    /**
     * <p>Creates the test suite.</p>
     * @return A test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        return new JetspeedTestSuite(TestLoginModule.class);
    }

    public void testLogin() throws LoginException
    {
        loginContext.login();
        Principal found = SecurityHelper.getPrincipal(loginContext.getSubject(), UserPrincipal.class);
        assertNotNull("found principal is null", found);
        assertTrue("found principal should be anonlogin, " + found.getName(), found.getName().equals((new UserPrincipalImpl("anonlogin")).getName()));      
    }
    
    public void testLogout() throws LoginException
    {
        loginContext.login();
        loginContext.logout();
        Principal found = SecurityHelper.getBestPrincipal(loginContext.getSubject(), UserPrincipal.class);
        assertNull("found principal is not null", found);
    }

    /**
     * <p>Initialize user test object.</p>
     */
    protected void initUserObject()
    {
        UserManagerService ums = getService();
        try
        {
            ums.addUser("anonlogin", "password");
        }
        catch (SecurityException sex)
        {
        }
    }

    /**
     * <p>Destroy user test object.</p>
     */
    protected void destroyUserObject()
    {
        UserManagerService ums = getService();
        ums.removeUser("anonlogin");
    }

}
