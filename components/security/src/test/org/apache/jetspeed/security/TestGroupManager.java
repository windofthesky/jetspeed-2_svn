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
import java.util.prefs.Preferences;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;

/**
 * <p>
 * Unit testing for {@link GroupManager}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id$
 */
public class TestGroupManager extends AbstractSecurityTestcase
{

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
        destroyGroups();
        super.tearDown();

    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestGroupManager.class);
    }

    /**
     * <p>
     * Test add group.
     * </p>
     */
    public void testAddGroup()
    {
        try
        {
            gms.addGroup("testgroup");
        }
        catch (SecurityException sex)
        {
            assertTrue("group should not already exists. exception caught: " + sex, false);
        }
        try
        {
            gms.addGroup("testgroup.newgroup0");
        }
        catch (SecurityException sex)
        {
            assertTrue("group should not already exists. exception caught: " + sex, false);
        }
        // Add existing group.
        try
        {
            gms.addGroup("testgroup.newgroup0");
            assertTrue("group should already exists. exception not thrown.", false);
        }
        catch (SecurityException sex)
        {
        }

        // Cleanup test.
        try
        {
            gms.removeGroup("testgroup");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove group. exception caught: " + sex, false);
        }
    }

    /**
     * <p>
     * Test add user to group.
     * </p>
     */
    public void testAddUserToGroup()
    {
        // Init test.
        try
        {
            ums.addUser("anonuser1", "password");
            gms.addGroup("testusertogroup1");
            gms.addGroup("testusertogroup1.group1");
            gms.addGroup("testusertogroup1.group2");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testAddUserToGroup(), " + sex, false);
        }
        // Add group with no prior groups.
        try
        {
            gms.addUserToGroup("anonuser1", "testusertogroup1.group1");
            Collection principals = ums.getUser("anonuser1").getSubject().getPrincipals();
            assertTrue("anonuser1 should contain testusertogroup1.group1", principals.contains(new GroupPrincipalImpl(
                    "testusertogroup1.group1")));
        }
        catch (SecurityException sex)
        {
            assertTrue("should add user to group. exception caught: " + sex, false);
        }
        // Add group with existing groups.
        try
        {
            gms.addUserToGroup("anonuser1", "testusertogroup1.group2");
            Collection principals = ums.getUser("anonuser1").getSubject().getPrincipals();
            assertTrue("anonuser1 should contain testusertogroup1.group2", principals.contains(new GroupPrincipalImpl(
                    "testusertogroup1.group2")));
        }
        catch (SecurityException sex)
        {
            assertTrue("should add user to group. exception caught: " + sex, false);
        }
        // Add group when user does not exist.
        try
        {
            gms.addUserToGroup("anonuser123", "testusertogroup1.group2");
            assertTrue("should catch exception: user does not exist.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Add group when group does not exist.
        try
        {
            gms.addUserToGroup("anonuser1", "testusertogroup1.group123");
            assertTrue("should catch exception: group does not exist.", false);
        }
        catch (SecurityException sex)
        {
        }

        // Cleanup test.
        try
        {
            ums.removeUser("anonuser1");
            gms.removeGroup("testusertogroup1");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user and group. exception caught: " + sex, false);
        }
    }

    /**
     * <p>
     * Test remove group.
     * </p>
     */
    public void testRemoveGroup()
    {
        // Init test.
        try
        {
            ums.addUser("anonuser2", "password");
            gms.addGroup("testgroup1");
            gms.addGroup("testgroup1.group1");
            gms.addGroup("testgroup1.group2");
            gms.addGroup("testgroup2");
            gms.addGroup("testgroup2.group1");
            gms.addUserToGroup("anonuser2", "testgroup1.group1");
            gms.addUserToGroup("anonuser2", "testgroup1.group2");
            gms.addUserToGroup("anonuser2", "testgroup2.group1");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemoveGroup(), " + sex, false);
        }

        try
        {
            gms.removeGroup("testgroup1.group1");
            Collection principals = ums.getUser("anonuser2").getSubject().getPrincipals();
            // because of hierarchical groups with generalization strategy as default. Was 5 groups + 1 user, should now be 5
            // (4 groups + 1 user).
            assertEquals(
                "principal size should be == 5 after removing testgroup1.group1, for principals: " + principals.toString(),
                5,
                principals.size());
            assertFalse("anonuser2 should not contain testgroup1.group1", principals.contains(new GroupPrincipalImpl(
                    "testgroup1.group1")));
        }
        catch (SecurityException sex)
        {
            assertTrue("should remove group. exception caught: " + sex, false);
        }

        // Cleanup test.
        try
        {
            ums.removeUser("anonuser2");
            gms.removeGroup("testgroup1");
            gms.removeGroup("testgroup2");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user and group. exception caught: " + sex, false);
        }
    }

    /**
     * <p>
     * Test get group.
     * </p>
     */
    public void testGetGroup()
    {
        // Test when the group does not exist.
        try
        {
            Group group = gms.getGroup("testgroupdoesnotexist");
            assertTrue("group does not exist. should have thrown an exception.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Test when the group exists.
        Group group = null;
        try
        {
            gms.addGroup("testgetgroup");
            group = gms.getGroup("testgetgroup");
        }
        catch (SecurityException sex)
        {
            assertTrue("group exists. should not have thrown an exception.", false);
        }
        assertNotNull("group is null", group);
        // Test the Principal.
        Principal groupPrincipal = group.getPrincipal();
        assertNotNull("group principal is null", groupPrincipal);
        assertEquals("expected group principal full path == testgetgroup", "testgetgroup", groupPrincipal.getName());

        // Test the Group Preferences.
        Preferences preferences = group.getPreferences();
        assertEquals("expected group node == /group/testgetgroup", SecurityHelper
                .getPreferencesFullPath(groupPrincipal), preferences.absolutePath());

        // Cleanup test.
        try
        {
            gms.removeGroup("testgetgroup");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove group. exception caught: " + sex, false);
        }
    }

    /**
     * <p>
     * Test get groups for user.
     * </p>
     */
    public void testGetGroupsForUser()
    {
        // Init test.
        try
        {
            ums.addUser("anonuser2", "password");
            gms.addGroup("testgroup1");
            gms.addGroup("testgroup1.group1");
            gms.addGroup("testgroup1.group2");
            gms.addGroup("testgroup2");
            gms.addGroup("testgroup2.group1");
            gms.addUserToGroup("anonuser2", "testgroup1.group1");
            gms.addUserToGroup("anonuser2", "testgroup1.group2");
            gms.addUserToGroup("anonuser2", "testgroup2.group1");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testGetGroupsForUser(), " + sex, false);
        }

        try
        {
            Collection groups = gms.getGroupsForUser("anonuser2");
            // Default hierarchy used in by generalization.
            assertEquals("groups size should be == 5", 5, groups.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("user exists. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        try
        {
            ums.removeUser("anonuser2");
            gms.removeGroup("testgroup1");
            gms.removeGroup("testgroup2");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user and group. exception caught: " + sex, false);
        }
    }

    /**
     * <p>
     * Test get groups in role.
     * </p>
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
            Collection groups = gms.getGroupsInRole("testuserrolemapping");
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
     * <p>
     * Test remove user from group.
     * </p>
     */
    public void testRemoveUserFromGroup()
    {
        // Init test.
        try
        {
            ums.addUser("anonuser4", "password");
            gms.addGroup("testgroup1");
            gms.addGroup("testgroup1.group1");
            gms.addUserToGroup("anonuser4", "testgroup1.group1");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemoveUserFromGroup(), " + sex, false);
        }

        try
        {
            gms.removeUserFromGroup("anonuser4", "testgroup1.group1");
            Collection groups = gms.getGroupsForUser("anonuser4");
            assertEquals("groups size should be == 0", 0, groups.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("user exists. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        try
        {
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
     * Test is user in role.
     * </p>
     */
    public void testIsUserInGroup()
    {
        // Init test.
        try
        {
            ums.addUser("anonuser3", "password");
            gms.addGroup("testgroup1");
            gms.addGroup("testgroup1.group1");
            gms.addUserToGroup("anonuser3", "testgroup1.group1");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemoveUserFromGroup(), " + sex, false);
        }

        try
        {
            boolean isUserInGroup = gms.isUserInGroup("anonuser3", "testgroup1.group1");
            assertTrue("anonuser3 should be in group testgroup1.group1", isUserInGroup);
        }
        catch (SecurityException sex)
        {
            assertTrue("user and group exist. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        try
        {
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
     * Destroy group test objects.
     * </p>
     */
    protected void destroyGroups() throws Exception
    {
        ums.removeUser("anonuser1");
        ums.removeUser("anonuser2");
        ums.removeUser("anonuser3");
        ums.removeUser("anonuser4");
        gms.removeGroup("testgroup1");
        gms.removeGroup("testgroup2");
        gms.removeGroup("testusertogroup1");
        gms.removeGroup("testgetgroup");
    }

}