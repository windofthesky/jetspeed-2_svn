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
import java.util.Iterator;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.impl.PassiveCallbackHandler;

/**
 * <p>
 * Unit testing for {@link UserManager}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class TestUserManager extends AbstractSecurityTestcase
{

    /**
     * <p>
     * Defines the test case name for junit.
     * </p>
     * 
     * @param testName The test case name.
     */
    public TestUserManager(String testName)
    {
        super(testName);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        destroyUserObject();
        super.tearDown();
    }

    public static Test suite()
    {
        return new TestSuite(TestUserManager.class);
    }

    /**
     * <p>
     * Test add/remove user.
     * </p>
     */
    public void testAddRemoveUser()
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
     * <p>
     * Test get user.
     * </p>
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
        assertEquals("expected user principal full path == /user/test", "/user/test", SecurityHelper
                .getPreferencesFullPath(userPrincipal));
        assertEquals("expected user principal name == test", "test", userPrincipal.getName());

        // Test the User Preferences.
        Preferences preferences = user.getPreferences();
        assertEquals("expected user node == /user/test", "/user/test", preferences.absolutePath());
    }

    /**
     * <p>
     * Test get users in role.
     * </p>
     */
    public void testGetUsersInRole()
    {
        // Init test.
        try
        {
            ums.addUser("anonuser3", "password");
            ums.addUser("anonuser4", "password");
            rms.addRole("testuserrolemapping");
            rms.addRole("testuserrolemapping.role1");
            rms.addRole("testuserrolemapping.role2");
            rms.addRoleToUser("anonuser3", "testuserrolemapping");
            rms.addRoleToUser("anonuser3", "testuserrolemapping.role1");
            rms.addRoleToUser("anonuser3", "testuserrolemapping.role2");
            rms.addRoleToUser("anonuser4", "testuserrolemapping");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testGetUsersInRole(), " + sex, false);
        }

        try
        {
            Collection users = ums.getUsersInRole("testuserrolemapping");
            assertEquals("users size should be == 2", 2, users.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("role exists. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        try
        {
            ums.removeUser("anonuser3");
            ums.removeUser("anonuser4");
            rms.removeRole("testuserrolemapping");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user and role. exception caught: " + sex, false);
        }
    }

    /**
     * <p>
     * Test get users in group.
     * </p>
     */
    public void testGetUsersInGroup()
    {
        // Init test.
        try
        {
            ums.addUser("anonuser2", "password");
            ums.addUser("anonuser3", "password");
            ums.addUser("anonuser4", "password");
            gms.addGroup("testgroup1");
            gms.addGroup("testgroup1.group1");
            gms.addUserToGroup("anonuser2", "testgroup1.group1");
            gms.addUserToGroup("anonuser3", "testgroup1.group1");
            gms.addUserToGroup("anonuser4", "testgroup1.group1");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testGetUsersInGroup(), " + sex, false);
        }

        try
        {
            Collection users = ums.getUsersInGroup("testgroup1.group1");
            assertEquals("users size should be == 3", 3, users.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("group exists. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        try
        {
            ums.removeUser("anonuser2");
            ums.removeUser("anonuser3");
            ums.removeUser("anonuser4");
            gms.removeGroup("testgroup1");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user and group. exception caught: " + sex, false);
        }
    }

    /**
     * <p>
     * Test set password.
     * </p>
     */
    public void testSetPassword()
    {
        try
        {
            ums.addUser("anon", "password");
            ums.setPassword("anon", "password", "newpassword");

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
     * <p>
     * Test get users.
     * </p>
     * 
     * @throws Exception Throws an exception.
     */
    public void testGetUsers() throws Exception
    {
        ums.addUser("one", "one-pw");
        ums.addUser("two", "two-pw");
        ums.addUser("three", "three-pw");
        int count = 0;
        Iterator it = ums.getUsers("");
        while (it.hasNext())
        {
            User user = (User) it.next();
            Iterator principals = user.getSubject().getPrincipals().iterator();
            while (principals.hasNext())
            {
                Principal principal = (Principal) principals.next();
                System.out.println("principal = " + principal.getName());
                if (principal.getName().equals("one"))
                {
                    count++;
                }
                else if (principal.getName().equals("two"))
                {
                    count++;
                }
                else if (principal.getName().equals("three"))
                {
                    count++;
                }
            }
        }
        assertTrue("user count should be 3", count == 3);
        ums.removeUser("one");
        ums.removeUser("two");
        ums.removeUser("three");
    }

    /**
     * <p>
     * Destroy user test object.
     * </p>
     */
    protected void destroyUserObject()
    {
        try
        {
            if (ums.userExists("anon"))
                ums.removeUser("anon");
            if (ums.userExists("test"))
                ums.removeUser("test");
        }
        catch (SecurityException sex)
        {
            System.out.println("could not remove test users. exception caught: " + sex);
        }
    }

}