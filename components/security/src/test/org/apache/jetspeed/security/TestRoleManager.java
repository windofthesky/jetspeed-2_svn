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

import java.util.Collection;
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.impl.RolePrincipalImpl;

/**
 * <p>Unit testing for {@link RoleManager}.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 * @version $Id$
 */
public class TestRoleManager extends AbstractSecurityTestcase
{

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestRoleManager(String testName)
    {
        super(testName);
    }


    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {        
        destroyRoles();
        super.tearDown();
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestRoleManager.class);
    }
    
    /**
     * <p>Test add role.</p>
     */
    public void testAddRole()
    {
        try
        {
            rms.addRole("testrole");
        }
        catch (SecurityException sex)
        {
            assertTrue("role should not already exists. exception caught: " + sex, false);
        }
        try
        {
            rms.addRole("testrole.newrole0");
        }
        catch (SecurityException sex)
        {
            assertTrue("role should not already exists. exception caught: " + sex, false);
        }
        // Add existing role.
        try
        {
            rms.addRole("testrole.newrole0");
            assertTrue("role should already exists. exception not thrown.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Remove role.
        try
        {
            rms.removeRole("testrole");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove role. exception caught: " + sex, false);
        }
    }

    /**
     * <p>Test add user to role.</p>
     */
    public void testAddRoleToUser()
    {
        // Init test.
        try
        {
            ums.addUser("anonuser1", "password");
            rms.addRole("testusertorole1");
            rms.addRole("testusertorole1.role1");
            rms.addRole("testusertorole1.role2");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testAddRoleToUser(), " + sex, false);
        }
        // Add role with no prior roles.
        try
        {
            rms.addRoleToUser("anonuser1", "testusertorole1.role1");
          
            Collection principals = ums.getUser("anonuser1").getSubject().getPrincipals();
            assertTrue(
                "anonuser1 should contain testusertorole1.role1",
                principals.contains(new RolePrincipalImpl("testusertorole1.role1")));
        }
        catch (SecurityException sex)
        {
            assertTrue("should add user to role. exception caught: " + sex, false);
        }
        // Add role with existing roles.
        try
        {
            rms.addRoleToUser("anonuser1", "testusertorole1.role2");
            Collection principals = ums.getUser("anonuser1").getSubject().getPrincipals();
            assertTrue(
                "anonuser1 should contain testusertorole1.role2",
                principals.contains(new RolePrincipalImpl("testusertorole1.role2")));
        }
        catch (SecurityException sex)
        {
            assertTrue("should add user to role. exception caught: " + sex, false);
        }
        // Add role when user does not exist.
        try
        {
            rms.addRoleToUser("anonuser123", "testusertorole1.role2");
            assertTrue("should catch exception: user does not exist.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Add role when role does not exist.
        try
        {
            rms.addRoleToUser("anonuser1", "testusertorole1.role123");
            assertTrue("should catch exception: role does not exist.", false);
        }
        catch (SecurityException sex)
        {
        }

        // Cleanup.
        try
        {
            ums.removeUser("anonuser1");
            rms.removeRole("testusertorole1");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user and role. exception caught: " + sex, false);
        }
    }

    /**
     * <p>Test remove role.</p>
     */
    public void testRemoveRole()
    {
        // Init test.
        try
        {
            ums.addUser("anonuser2", "password");
            rms.addRole("testrole1");
            rms.addRole("testrole1.role1");
            rms.addRole("testrole1.role2");
            rms.addRole("testrole2");
            rms.addRole("testrole2.role1");
            rms.addRole("testrole2.role2");
            rms.addRoleToUser("anonuser2", "testrole1.role1");
            rms.addRoleToUser("anonuser2", "testrole1.role2");
            rms.addRoleToUser("anonuser2", "testrole2.role1");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemoveRole(), " + sex, false);
        }

        try
        {
            rms.removeRole("testrole1.role1");
            Collection principals = ums.getUser("anonuser2").getSubject().getPrincipals();
            // because of hierarchical roles
            //
            // assertEquals(
            //     "principal size should be == 3 after removing testrole1.role1, for principals: " + principals.toString(),
            //     3,
            //     principals.size());
            assertFalse(
                "anonuser2 should not contain testrole1.role1",
                principals.contains(new RolePrincipalImpl("testrole1.role1")));
        }
        catch (SecurityException sex)
        {
            assertTrue("should remove role. exception caught: " + sex, false);
        }

        // Cleanup test.
        try
        {
            ums.removeUser("anonuser2");
            rms.removeRole("testrole1");
            rms.removeRole("testrole2");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user and role. exception caught: " + sex, false);
        }
    }

    /**
     * <p>Test get role.</p>
     */
    public void testGetRole()
    {
        // Test when the role does not exist.
        try
        {
            Role role = rms.getRole("testroledoesnotexist");
            assertTrue("role does not exist. should have thrown an exception.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Test when the role exists.
        Role role = null;
        try
        {
            rms.addRole("testgetrole");
            role = rms.getRole("testgetrole");
        }
        catch (SecurityException sex)
        {
            assertTrue("role exists. should not have thrown an exception.", false);
        }
        assertNotNull("role is null", role);
        // Test the RolePrincipal.
        RolePrincipal rolePrincipal = role.getPrincipal();
        assertNotNull("role principal is null", rolePrincipal);
        assertEquals("expected role principal full path name == testgetrole", "testgetrole", rolePrincipal.getName());

        // Test the Role Preferences.
        Preferences preferences = role.getPreferences();
        assertEquals(
            "expected role node == /role/testgetrole",
            SecurityHelper.getPreferencesFullPath(rolePrincipal),
            preferences.absolutePath());

        // Cleanup test.
        try
        {
            rms.removeRole("testgetrole");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove role. exception caught: " + sex, false);
        }
    }

        /**
         * <p>Test get roles for user.</p>
         */
        public void testGetRolesForUser()
        {
            // Init test.
            try
            {
                ums.addUser("anonuser3", "password");
                rms.addRole("testuserrolemapping");
                rms.addRole("testuserrolemapping.role1");
                rms.addRole("testuserrolemapping.role2");
                rms.addRoleToUser("anonuser3", "testuserrolemapping");
                rms.addRoleToUser("anonuser3", "testuserrolemapping.role1");
                rms.addRoleToUser("anonuser3", "testuserrolemapping.role2");
            }
            catch (SecurityException sex)
            {
                assertTrue("failed to init testGetRolesForUser(), " + sex, false);
            }
    
            try
            {
                Collection roles = rms.getRolesForUser("anonuser3");
                assertEquals("roles size should be == 3", 3, roles.size());
            }
            catch (SecurityException sex)
            {
                assertTrue("user exists. should not have thrown an exception: " + sex, false);
            }
    
            // Cleanup test.
            try
            {
                ums.removeUser("anonuser3");
                rms.removeRole("testuserrolemapping");
            }
            catch (SecurityException sex)
            {
                assertTrue("could not remove user and role. exception caught: " + sex, false);
            }
        }
    
        /**
         * <p>Test get users in role.</p>
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
                Collection users = rms.getUsersInRole("testuserrolemapping");
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
         * <p>Test get roles for group.</p>
         */
        public void testGetRolesForGroup()
        {
            // Init test.
            try
            {
                rms.addRole("testuserrolemapping");
                rms.addRole("testuserrolemapping.role1");
                rms.addRole("testuserrolemapping.role3");
                gms.addGroup("testrolegroupmapping");
                rms.addRoleToGroup("testuserrolemapping", "testrolegroupmapping");
                rms.addRoleToGroup("testuserrolemapping.role1", "testrolegroupmapping");
                rms.addRoleToGroup("testuserrolemapping.role3", "testrolegroupmapping");
            }
            catch (SecurityException sex)
            {
                assertTrue("failed to init testGetRolesForGroup(), " + sex, false);
            }
    
            try
            {
                Collection roles = rms.getRolesForGroup("testrolegroupmapping");
                assertEquals("roles size should be == 3", 3, roles.size());
            }
            catch (SecurityException sex)
            {
                assertTrue("group exists. should not have thrown an exception: " + sex, false);
            }
    
            // Cleanup test.
            try
            {
                rms.removeRole("testuserrolemapping");
                gms.removeGroup("testrolegroupmapping");
            }
            catch (SecurityException sex)
            {
                assertTrue("could not remove role and group. exception caught: " + sex, false);
            }
        }
    
        /**
         * <p>Test get groups in role.</p>
         */
        public void testGetGroupsInRole()
        {
            // Init test.
            try
            {
                rms.addRole("testuserrolemapping");
                gms.addGroup("testrolegroupmapping");
                gms.addGroup("testrolegroupmapping.group1");
                gms.addGroup("testrolegroupmapping.group2");
                rms.addRoleToGroup("testuserrolemapping", "testrolegroupmapping");
                rms.addRoleToGroup("testuserrolemapping", "testrolegroupmapping.group1");
                rms.addRoleToGroup("testuserrolemapping", "testrolegroupmapping.group2");
            }
            catch (SecurityException sex)
            {
                assertTrue("failed to init testGetRolesForGroup(), " + sex, false);
            }
    
            try
            {
                Collection groups = rms.getGroupsInRole("testuserrolemapping");
                assertEquals("groups size should be == 3", 3, groups.size());
            }
            catch (SecurityException sex)
            {
                assertTrue("role exists. should not have thrown an exception: " + sex, false);
            }
    
            // Cleanup test.
            try
            {
                rms.removeRole("testuserrolemapping");
                gms.removeGroup("testrolegroupmapping");
            }
            catch (SecurityException sex)
            {
                assertTrue("could not remove role and group. exception caught: " + sex, false);
            }
        }
    
        /**
         * <p>Test remove role from user.</p>
         */
        public void testRemoveRoleFromUser()
        {
            // Init test.
            try
            {
                ums.addUser("anonuser5", "password");
                rms.addRole("testrole3");
                rms.addRoleToUser("anonuser5", "testrole3");
            }
            catch (SecurityException sex)
            {
                assertTrue("failed to init testRemoveRoleFromUser(), " + sex, false);
            }
    
            try
            {
                rms.removeRoleFromUser("anonuser5", "testrole3");
                Collection roles = rms.getRolesForUser("anonuser5");
                assertEquals("roles size should be == 0", 0, roles.size());
            }
            catch (SecurityException sex)
            {
                assertTrue("user exists. should not have thrown an exception: " + sex, false);
            }
    
            // Cleanup test.
            try
            {
                ums.removeUser("anonuser5");
                rms.removeRole("testrole3");
            }
            catch (SecurityException sex)
            {
                assertTrue("could not remove user and role. exception caught: " + sex, false);
            }
        }
    
        /**
         * <p>Test is user in role.</p>
         */
        public void testIsUserInRole()
        {
            // Init test.
            try
            {
                ums.addUser("anonuser4", "password");
                rms.addRole("testuserrolemapping");
                rms.addRoleToUser("anonuser4", "testuserrolemapping");
            }
            catch (SecurityException sex)
            {
                assertTrue("failed to init testIsUserInRole(), " + sex, false);
            }
    
            try
            {
                boolean isUserInRole = rms.isUserInRole("anonuser4", "testuserrolemapping");
                assertTrue("anonuser4 should be in role testuserrolemapping", isUserInRole);
            }
            catch (SecurityException sex)
            {
                assertTrue("user and role exist. should not have thrown an exception: " + sex, false);
            }
    
            // Cleanup test.
            try
            {
                ums.removeUser("anonuser4");
                rms.removeRole("testuserrolemapping");
            }
            catch (SecurityException sex)
            {
                assertTrue("could not remove user and role. exception caught: " + sex, false);
            }
        }
    
        /**
         * <p>Test remove role from group.</p>
         */
        public void testRemoveRoleFromGroup()
        {
            // Init test.
            try
            {
                rms.addRole("testuserrolemapping");
                rms.addRole("testuserrolemapping.role1");
                rms.addRole("testuserrolemapping.role3");
                gms.addGroup("testrolegroupmapping");
                rms.addRoleToGroup("testuserrolemapping", "testrolegroupmapping");
                rms.addRoleToGroup("testuserrolemapping.role1", "testrolegroupmapping");
                rms.addRoleToGroup("testuserrolemapping.role3", "testrolegroupmapping");
            }
            catch (SecurityException sex)
            {
                assertTrue("failed to init testRemoveRoleFromGroup(), " + sex, false);
            }
    
            try
            {
                rms.removeRoleFromGroup("testuserrolemapping.role3", "testrolegroupmapping");
                Collection roles = rms.getRolesForGroup("testrolegroupmapping");
                assertEquals("roles size should be == 2", 2, roles.size());
            }
            catch (SecurityException sex)
            {
                assertTrue("group exists. should not have thrown an exception: " + sex, false);
            }
    
            // Cleanup test.
            try
            {
                rms.removeRole("testuserrolemapping");
                gms.removeGroup("testrolegroupmapping");
            }
            catch (SecurityException sex)
            {
                assertTrue("could not remove group and role. exception caught: " + sex, false);
            }
        }
    
        /**
         * <p>Test is user in role.</p>
         */
        public void testIsGroupInRole()
        {
            // Init test.
            try
            {
                rms.addRole("testuserrolemapping");
                gms.addGroup("testrolegroupmapping");
                rms.addRoleToGroup("testuserrolemapping", "testrolegroupmapping");
            }
            catch (SecurityException sex)
            {
                assertTrue("failed to init testIsGroupInRole(), " + sex, false);
            }
            try
            {
                boolean isGroupInRole = rms.isGroupInRole("testrolegroupmapping", "testuserrolemapping");
                assertTrue("testrolegroupmapping should be in role testuserrolemapping", isGroupInRole);
            }
            catch (SecurityException sex)
            {
                assertTrue("group and role exist. should not have thrown an exception: " + sex, false);
            }
    
            // Cleanup test.
            try
            {
                rms.removeRole("testuserrolemapping");
                gms.removeGroup("testrolegroupmapping");
            }
            catch (SecurityException sex)
            {
                assertTrue("could not remove role and group. exception caught: " + sex, false);
            }
        }

    /**
     * <p>Destroy role test objects.</p>
     */
    protected void destroyRoles()
    {
        try
        {
            ums.removeUser("anonuser1");
            ums.removeUser("anonuser2");
            ums.removeUser("anonuser3");
            ums.removeUser("anonuser4");
            ums.removeUser("anonuser5");
            rms.removeRole("testrole1");
            rms.removeRole("testrole2");
            rms.removeRole("testrole3");
            rms.removeRole("testgetrole");
            rms.removeRole("testuserrolemapping");
            gms.removeGroup("testrolegroupmapping");
            rms.removeRole("testusertorole1");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user, group and role. exception caught: " + sex, false);
        }
    }

}
