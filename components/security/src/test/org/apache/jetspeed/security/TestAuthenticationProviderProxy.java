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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.impl.AuthenticationProviderImpl;
import org.apache.jetspeed.security.impl.AuthenticationProviderProxyImpl;
import org.apache.jetspeed.security.impl.GroupManagerImpl;
import org.apache.jetspeed.security.impl.LoginModuleProxyImpl;
import org.apache.jetspeed.security.impl.RoleManagerImpl;
import org.apache.jetspeed.security.impl.SecurityProviderImpl;
import org.apache.jetspeed.security.impl.UserManagerImpl;
import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.UserSecurityHandler;
import org.apache.jetspeed.security.spi.impl.LdapCredentialHandler;
import org.apache.jetspeed.security.spi.impl.LdapUserSecurityHandler;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;

/**
 * <p>Unit testing for {@link TestAuthenticationProviderProxy}.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestAuthenticationProviderProxy extends AbstractSecurityTestcase
{

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        destroyTestData();
        
        // The LDAP user security handler.
        UserSecurityHandler ldapUsh = new LdapUserSecurityHandler();
        // The LDAP credential handler.
        CredentialHandler ldapCh = new LdapCredentialHandler();
        
        // Security Providers.
        AuthenticationProvider defaultAtnProvider = new AuthenticationProviderImpl("DefaultAuthenticator", "The default authenticator", "login.conf", ch, ush);
        AuthenticationProvider ldapAtnProvider = new AuthenticationProviderImpl("LdapAuthenticator", "The ldap authenticator", ldapCh, ldapUsh);

        List atnProviders = new ArrayList();
        atnProviders.add(defaultAtnProvider);
        atnProviders.add(ldapAtnProvider);
        AuthenticationProviderProxy atnProviderProxy = new AuthenticationProviderProxyImpl(atnProviders, "DefaultAuthenticator");
        
        // Need to override the AbstractSecurityTestcase behavior.
        securityProvider = new SecurityProviderImpl(atnProviderProxy, rsh, gsh, smh);
        ums = new UserManagerImpl(securityProvider);
        gms = new GroupManagerImpl(securityProvider);
        rms = new RoleManagerImpl(securityProvider);
        
        // Login module.
        new LoginModuleProxyImpl(ums);
    }
   
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {       
        destroyTestData();
        super.tearDown();
    }

  
    
    public static Test suite()
    {
           return new TestSuite(TestAuthenticationProviderProxy.class);
    }


    /**
     * <p>
     * Test user manager.
     * </p>
     */
    public void testUserManager()
    {
        initTestData();
        
        try
        {
            // Get user.
            // From LDAP.
            User user = ums.getUser("ldap1");
            assertNotNull(user);
            assertEquals("ldap1", SecurityHelper.getPrincipal(user.getSubject(), UserPrincipal.class).getName());
            // From RDBMS.
            user = ums.getUser("anonuser1");
            assertNotNull(user);
            assertEquals("anonuser1", SecurityHelper.getPrincipal(user.getSubject(), UserPrincipal.class).getName());   
            
            // Authenticate.
            // From Ldap.
            assertTrue(ums.authenticate("ldap2", "password"));
            assertFalse(ums.authenticate("ldap3", "pword"));
            // From RDBMS.
            assertTrue(ums.authenticate("anonuser2", "password"));
            assertFalse(ums.authenticate("anonuser3", "pword"));
            
            // Get all users. 5 rdbms users + 3 ldap users.
            Iterator users = ums.getUsers("");
            int count = 0;
            while (users.hasNext())
            {
                users.next();
                count ++;
            }
            assertEquals(8, count);
        }
        catch (SecurityException sex)
        {
            assertTrue("security exception caught: " + sex, false);
        }

        
        destroyTestData();
    }
    
    /**
     * <p>
     * Test role manager.
     * </p>
     */
    public void testRoleManager()
    {
        initTestData();
        
        try
        {
            // Add user to role.
            // Mapping only.
            rms.addRoleToUser("ldap1", "testrole1.subrole1");
            // Get role mapping.
            Collection roles = rms.getRolesForUser("ldap1");
            assertNotNull(roles);
            // Given the hierarchy resolution. Should contain 2 roles.
            assertEquals("should contain 2 roles", 2, roles.size());
            
            // Is user in roles?
            assertTrue(rms.isUserInRole("ldap1", "testrole1"));
            assertTrue(rms.isUserInRole("ldap1", "testrole1.subrole1"));
            
            // Remove role mapping.
            rms.removeRoleFromUser("ldap1", "testrole1.subrole1");
            // Get role mapping.
            roles = rms.getRolesForUser("ldap1");
            assertNotNull(roles);
            assertEquals("should not contain any role", 0, roles.size());
            
            // The mapping entry should be gone.
            assertNull(securityAccess.getInternalUserPrincipal("ldap1", true));
            
            // Is user in roles?
            assertFalse(rms.isUserInRole("ldap1", "testrole1"));
            assertFalse(rms.isUserInRole("ldap1", "testrole1.subrole1"));
        }
        catch (SecurityException sex)
        {
            assertTrue("security exception caught: " + sex, false);
        }
        
        destroyTestData();
    }
    
    /**
     * <p>
     * Test group manager.
     * </p>
     */
    public void testGroupManager()
    {
        initTestData();
        
        try
        {
            // Add user to group.
            // Mapping only.
            gms.addUserToGroup("ldap1", "testgroup1.subgroup1");
            // Get group mapping.
            Collection groups = gms.getGroupsForUser("ldap1");
            assertNotNull(groups);
            // Given the hierarchy resolution. Should contain 2 groups.
            assertEquals("should contain 2 groups", 2, groups.size());
            
            // Is user in groups?
            assertTrue(gms.isUserInGroup("ldap1", "testgroup1"));
            assertTrue(gms.isUserInGroup("ldap1", "testgroup1.subgroup1"));
            
            // Remove group mapping.
            gms.removeUserFromGroup("ldap1", "testgroup1.subgroup1");
            // Get group mapping.
            groups = gms.getGroupsForUser("ldap1");
            assertNotNull(groups);
            assertEquals("should not contain any group", 0, groups.size());
            
            // The mapping entry should be gone.
            assertNull(securityAccess.getInternalUserPrincipal("ldap1", true));
            
            // Is user in groups?
            assertFalse(gms.isUserInGroup("ldap1", "testgroup1"));
            assertFalse(gms.isUserInGroup("ldap1", "testgroup1.subgroup1"));
        }
        catch (SecurityException sex)
        {
            assertTrue("security exception caught: " + sex, false);
        }
        
        destroyTestData();
    }
    
    /**
     * <p>
     * Init test data.
     * </p>
     */
    private void initTestData()
    {
        final String[] users = new String[] { "anonuser1", "anonuser2", "anonuser3", "anonuser4", "anonuser5", };
        final String[] roles = new String[] { "testrole1", "testrole1.subrole1", "testrole1.subrole1.subrole2", "testrole2",
                "testrole2.subrole1" };
        final String[] groups = new String[] { "testgroup1", "testgroup1.subgroup1", "testgroup1.subgroup1.subgroup2", "testgroup2",
        "testgroup2.subgroup1" };
        
        for (int i = 0; i < users.length; i++)
        {
            try
            {
                ums.addUser(users[i], "password");
            }
            catch (SecurityException e)
            {
                System.err.println(e.toString());
            }
        }
        
        for (int i = 0; i < roles.length; i++)
        {
            try
            {
                rms.addRole(roles[i]);
            }
            catch (SecurityException e)
            {
                System.err.println(e.toString());
            }
        }
        
        for (int i = 0; i < groups.length; i++)
        {
            try
            {
                gms.addGroup(groups[i]);
            }
            catch (SecurityException e)
            {
                System.err.println(e.toString());
            }
        }
    }

    /**
     * <p>
     * Destroy test data.
     * </p>
     */
    private void destroyTestData()
    {
        final String[] users = new String[] { "anonuser1", "anonuser2", "anonuser3", "anonuser4", "anonuser5", };
        final String[] roles = new String[] { "testrole1", "testrole1.subrole1", "testrole1.subrole1.subrole2", "testrole2",
        "testrole2.subrole1" };
        final String[] groups = new String[] { "testgroup1", "testgroup1.subgroup1", "testgroup1.subgroup1.subgroup2", "testgroup2",
        "testgroup2.subgroup1" };
        
        for (int i = 0; i < users.length; i++)
        {
            try
            {
                ums.removeUser(users[i]);
            }
            catch (SecurityException e)
            {
                System.err.println(e.toString());
            }
        }
        
        for (int i = 0; i < roles.length; i++)
        {
            try
            {
                rms.removeRole(roles[i]);
            }
            catch (SecurityException e)
            {
                System.err.println(e.toString());
            }
        }
        
        for (int i = 0; i < groups.length; i++)
        {
            try
            {
                gms.removeGroup(groups[i]);
            }
            catch (SecurityException e)
            {
                System.err.println(e.toString());
            }
        }
    }

}
