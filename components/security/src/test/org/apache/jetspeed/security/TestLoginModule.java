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
package org.apache.jetspeed.security;

import java.security.Principal;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ContainerDeployerTestSuite;
import org.apache.jetspeed.security.impl.PassiveCallbackHandler;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.picocontainer.MutablePicoContainer;

/**
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestLoginModule extends AbstractComponentAwareTestCase
{
    /** <p>The JAAS login context.</p> */
    private LoginContext loginContext = null;

    /** The mutable pico container. */
    private MutablePicoContainer container;

    /** The user manager. */
    private UserManager ums;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestLoginModule(String testName)
    {
        super(testName);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        container = (MutablePicoContainer) getContainer();
        ums = (UserManager) container.getComponentInstance(UserManager.class);
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
//        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestLoginModule.class);
//        suite.setScript("org/apache/jetspeed/security/containers/test.security.groovy");
    	return new ContainerDeployerTestSuite(TestLoginModule.class);
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
    protected void destroyUserObject() throws Exception
    {
        ums.removeUser("anonlogin");
    }

}
