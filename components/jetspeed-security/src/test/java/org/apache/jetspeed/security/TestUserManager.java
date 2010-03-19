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
package org.apache.jetspeed.security;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Test;

import org.apache.jetspeed.security.impl.PassiveCallbackHandler;

/**
 * <p>
 * Unit testing for {@link UserManager}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class TestUserManager extends AbstractLDAPSecurityTestCase
{
    public static Test suite()
    {
        return createFixturedTestSuite(TestUserManager.class, "ldapTestSetup", "ldapTestTeardown");
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
            ums.addUser("anon");
        }
        catch (SecurityException sex)
        {
            assertTrue("user already exists. exception caught: " + sex, false);
        }

        try
        {
            ums.addUser("anon");
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
            ums.getUser("test");
            assertTrue("user does not exist. should have thrown an exception.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Test when the user exists.
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
        assertNotNull("user is null", user);
        
        try
        {
            // Test the User JSSubject
            Subject subject = ums.getSubject(user);
            assertNotNull("subject is null", subject);
            // Asset user principal.
            Principal userPrincipal = (Principal) user;
            assertEquals("expected user principal name == test", "test", userPrincipal.getName());

            // Test if roles are inheritable to a user via groups
            
            // If user 'inheritedUser' belongs to group 'inheritingGroup' and group 'group' has role 'assignedRole', then
            // the role 'assignedRole' can be inherited to the user 'inheritedUser' via group 'inheritingGroup'.
            
            ums.addUser("inheritedUser");
            gms.addGroup("inheritingGroup");
            gms.addUserToGroup("inheritedUser", "inheritingGroup");
            rms.addRole("assignedRole");
            rms.addRoleToGroup("assignedRole", "inheritingGroup");
            User testUser = ums.getUser("inheritedUser");

            List<String> principalNames = new ArrayList<String>();
            for (Principal p : ums.getSubject(testUser).getPrincipals())
            {
                principalNames.add(p.getName());
            }
            
            assertTrue("user is expected to have a user principal named inheritedUser.", principalNames.contains("inheritedUser"));
            assertTrue("user is expected to have a group principal named inheritingGroup.", principalNames.contains("inheritingGroup"));
            assertTrue("user is expected to have a role principal named assignedRole which is inherited via the group.", principalNames.contains("assignedRole"));
            
            // However, roles from role manager should not contain the role 'assignedRole'
            // because the role 'assignedRole' is not directly assigned to user 'inheritedUser'.
            // For example, the Users Admin portlet uses RoleManager to retrieve roles directly assigned to a user.
            
            List<String> userRoleNames = new ArrayList<String>();
            Collection<Role> roles = rms.getRolesForUser("inheritedUser");
            for (Role role : roles)
            {
                userRoleNames.add(role.getName());
            }
            
            assertFalse("role 'assignedRole' is not expected to be retrieved because the role 'assignedRole' is not directly assigned to user 'inheritedUser'.", userRoleNames.contains("assignedRole"));
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to test 'rolesInheritableViaGroups' mode in testGetUser(), " + sex, false);
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
            User user = ums.addUser("anon");
            PasswordCredential pwc = ums.getPasswordCredential(user);
            pwc.setPassword("password", false);
            ums.storePasswordCredential(pwc);

            LoginContext loginContext = null;
            
            // Test that the user can log in.
            try
            {
                PassiveCallbackHandler pch = new PassiveCallbackHandler("anon", "password");
                loginContext = new LoginContext("Jetspeed", pch);
                loginContext.login();
                loginContext.logout();
            }
            catch (LoginException le)
            {
                le.printStackTrace();
                assertTrue("failed to login user with new password.", false);
            }
            
            pwc = ums.getPasswordCredential(user);
            pwc.setPassword("password", "newpassword");
            ums.storePasswordCredential(pwc);

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
        ums.addUser("one");
        ums.addUser("two");
        ums.addUser("three");
        int count = 0;
        for (User user : ums.getUsers(null))
        {
            if (user.getName().equals("one"))
            {
                count++;
            }
            else if (user.getName().equals("two"))
            {
                count++;
            }
            else if (user.getName().equals("three"))
            {
                count++;
            }
        }
        assertTrue("user count should be 3", count == 3);
    }
}