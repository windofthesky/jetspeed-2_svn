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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ComponentAwareTestSuite;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.security.impl.PassiveCallbackHandler;

import org.picocontainer.MutablePicoContainer;

/**
 * <p>Unit testing for {@link UserManager}.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestUserManager extends AbstractComponentAwareTestCase
{

    /** The mutable pico container. */
    private MutablePicoContainer container;

    /** The user manager. */
    private UserManager ums;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestUserManager(String testName)
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
        ums = (UserManager) container.getComponentInstance(UserManager.class);
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
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestUserManager.class);
        suite.setScript("org/apache/jetspeed/security/containers/test.security.groovy");
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
     * <p>Test add user.</p>
     */
    public void testAddUser()
    {
        try
        {
            ums.addUser("anon", "password");
        }
        catch (SecurityException sex)
        {
            assertTrue("user already exists. exception caught: " + sex, false);
        }

        try
        {
            ums.addUser("anon", "password");
            assertTrue("user should already exists. exception not thrown.", false);
        }
        catch (SecurityException sex)
        {
        }

    }

    /**
     * <p>Test remove user.</p>
     */
    public void testRemoveUser()
    {
        try
        {
            ums.removeUser("anon");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user. exception caught: " + sex, false);
        }
        if (ums.userExists("anon"))
        {
            assertTrue("user should have been removed: ", false);
        }
    }

    /**
     * <p>Test get user.</p>
     */
    public void testGetUser()
    {
        // Test when the user does not exist.
        try
        {
            User user = ums.getUser("test");
            assertTrue("user does not exist. should have thrown an exception.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Test when the user exists.
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
        assertNotNull("user is null", user);
        // Test the User Subject.
        Subject subject = user.getSubject();
        assertNotNull("subject is null", subject);
        // Asset user principal.
        Principal userPrincipal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
        assertNotNull("user principal is null", userPrincipal);
        assertEquals(
            "expected user principal full path == /user/test",
            "/user/test",
            SecurityHelper.getPrincipalFullPath(userPrincipal));
        assertEquals("expected user principal name == test", "test", userPrincipal.getName());

        // Test the User Preferences.
        Preferences preferences = user.getPreferences();
        assertEquals("expected user node == /user/test", "/user/test", preferences.absolutePath());
    }

    /**
     * <p>Test set password.</p>
     */
    public void testSetPassword()
    {
        try
        {
            ums.addUser("anon", "password");
            ums.setPassword("anon", "newpassword");

            LoginContext loginContext = null;
            // Test that the user can log in with the new password.
            try
            {
                PassiveCallbackHandler pch = new PassiveCallbackHandler("anon", "newpassword");
                loginContext = new LoginContext("Jetspeed", pch);
                loginContext.login();
                loginContext.logout();
            }
            catch (LoginException le)
            {
                le.printStackTrace();
                assertTrue("failed to login user with new password.", false);
            }
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
        try
        {
            ums.removeUser("anon");
            ums.removeUser("test");
        }
        catch (SecurityException sex)
        {
            System.out.println("could not remove test users. exception caught: " + sex);
        }
    }

}
