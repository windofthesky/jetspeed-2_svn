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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;

/**
 * <p>Unit testing for {@link GroupManagerService}.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestGroupManagerService extends JetspeedTest
{

    private GroupManagerService gms = null;
    private UserManagerService ums = null;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestGroupManagerService(String testName)
    {
        super(testName);
    }

    /**
     * <p>Start the tests.</p>
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestGroupManagerService.class.getName()});
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
        destroyGroups();
    }

    /**
     * <p>Creates the test suite.</p>
     * @return A test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        return new JetspeedTestSuite(TestGroupManagerService.class);
    }

    /**
     * <p>Returns the {@link GroupManagerService}.</p>
     * @return The GroupManagerService.
     */
    protected GroupManagerService getGroupManagerService()
    {
        if (gms == null)
        {
            gms = (GroupManagerService) CommonPortletServices.getPortalService(GroupManagerService.SERVICE_NAME);
        }
        return gms;
    }

    /**
     * <p>Returns the {@link UserManagerService}.</p>
     * @return The UserManagerService.
     */
    protected UserManagerService getUserManagerService()
    {
        if (ums == null)
        {
            ums = (UserManagerService) CommonPortletServices.getPortalService(UserManagerService.SERVICE_NAME);
        }
        return ums;
    }

    /**
     * <p>Test that a {@link UserManagerService} was returned.</p>
     */
    public void testService()
    {
        assertNotNull(getGroupManagerService());
    }

    /**
     * <p>Test add group.</p>
     */
    public void testAddGroup()
    {
        GroupManagerService gms = getGroupManagerService();
        // Add group with path beginning with '/'.
        try
        {
            gms.addGroup("/testgroup");
        }
        catch (SecurityException sex)
        {
            assertTrue("group should not already exists. exception caught: " + sex, false);
        }
        // Add group without path beginning with '/'.
        try
        {
            gms.addGroup("testgroup/newgroup0");
        }
        catch (SecurityException sex)
        {
            assertTrue("group should not already exists. exception caught: " + sex, false);
        }
        // Add existing group.
        try
        {
            gms.addGroup("/testgroup/newgroup0");
            assertTrue("group should already exists. exception not thrown.", false);
        }
        catch (SecurityException sex)
        {
        }

        // Cleanup test.
        gms.removeGroup("/testgroup");
    }

    /**
     * <p>Test add user to group.</p>
     */
    public void testAddUserToGroup()
    {
        // Init test.
        GroupManagerService gms = getGroupManagerService();
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anonuser1", "password");
            gms.addGroup("/testusertogroup1");
            gms.addGroup("/testusertogroup1/group1");
            gms.addGroup("/testusertogroup1/group2");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testAddUserToGroup(), " + sex, false);
        }
        // Add group with no prior groups.
        try
        {
            gms.addUserToGroup("anonuser1", "/testusertogroup1/group1");
            Collection principals = ums.getUser("anonuser1").getSubject().getPrincipals();
            Principal found =
                SecurityHelper.getPrincipal(
                    new Subject(false, new HashSet(principals), new HashSet(), new HashSet()),
                    GroupPrincipal.class);
            assertNotNull("found principal is null", found);
            assertTrue(
                "found principal should be /testusertogroup1/group1, " + found.getName(),
                found.getName().equals((new GroupPrincipalImpl("/testusertogroup1/group1")).getName()));
        }
        catch (SecurityException sex)
        {
            assertTrue("should add user to group. exception caught: " + sex, false);
        }
        // Add group with existing groups.
        try
        {
            gms.addUserToGroup("anonuser1", "/testusertogroup1/group2");
            Collection principals = ums.getUser("anonuser1").getSubject().getPrincipals();
            assertTrue(
                "anonuser1 should contain /testusertogroup1/group2",
                principals.contains(new GroupPrincipalImpl("/testusertogroup1/group2")));
        }
        catch (SecurityException sex)
        {
            assertTrue("should add user to group. exception caught: " + sex, false);
        }
        // Add group when user does not exist.
        try
        {
            gms.addUserToGroup("anonuser123", "/testusertogroup1/group2");
            assertTrue("should catch exception: user does not exist.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Add group when group does not exist.
        try
        {
            gms.addUserToGroup("anonuser1", "/testusertogroup1/group123");
            assertTrue("should catch exception: group does not exist.", false);
        }
        catch (SecurityException sex)
        {
        }

        // Cleanup test.
        ums.removeUser("anonuser1");
        gms.removeGroup("/testusertogroup1");
    }

    /**
     * <p>Test remove group.</p>
     */
    public void testRemoveGroup()
    {
        // Init test.
        GroupManagerService gms = getGroupManagerService();
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anonuser2", "password");
            gms.addGroup("/testgroup1");
            gms.addGroup("/testgroup1/group1");
            gms.addGroup("/testgroup1/group2");
            gms.addGroup("/testgroup2");
            gms.addGroup("/testgroup2/group1");
            gms.addUserToGroup("anonuser2", "/testgroup1/group1");
            gms.addUserToGroup("anonuser2", "/testgroup1/group2");
            gms.addUserToGroup("anonuser2", "/testgroup2/group1");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemoveGroup(), " + sex, false);
        }

        gms.removeGroup("/testgroup1/group1");
        try
        {
            Collection principals = ums.getUser("anonuser2").getSubject().getPrincipals();
            assertEquals(
                "principal size should be == 3 after removing /testgroup1/group1, for principals: " + principals.toString(),
                3,
                principals.size());
            assertFalse(
                "anonuser2 should not contain /testgroup1/group1",
                principals.contains(new GroupPrincipalImpl(GroupPrincipalImpl.getFullPathFromPrincipalName("/testgroup1/group1"))));
        }
        catch (SecurityException sex)
        {
            assertTrue("should remove group. exception caught: " + sex, false);
        }

        // Cleanup test.
        ums.removeUser("anonuser2");
        gms.removeGroup("/testgroup1");
        gms.removeGroup("/testgroup2");
    }

    /**
     * <p>Test get group.</p>
     */
    public void testGetGroup()
    {
        GroupManagerService gms = getGroupManagerService();
        // Test when the group does not exist.
        try
        {
            Group group = gms.getGroup("/testgroupdoesnotexist");
            assertTrue("group does not exist. should have thrown an exception.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Test when the group exists.
        Group group = null;
        try
        {
            gms.addGroup("/testgetgroup");
            group = gms.getGroup("/testgetgroup");
        }
        catch (SecurityException sex)
        {
            assertTrue("group exists. should not have thrown an exception.", false);
        }
        assertNotNull("group is null", group);
        // Test the GroupPrincipal.
        GroupPrincipal groupPrincipal = group.getPrincipal();
        assertNotNull("group principal is null", groupPrincipal);
        assertEquals(
            "expected group principal full path == /group/testgetgroup",
            "/group/testgetgroup",
            SecurityHelper.getPrincipalFullPath(groupPrincipal));

        // Test the Group Preferences.
        Preferences preferences = group.getPreferences();
        assertEquals("expected group node == /group/testgetgroup", "/group/testgetgroup", preferences.absolutePath());

        // Cleanup test.
        gms.removeGroup("/testgetgroup");
    }

    /**
     * <p>Test get groups for user.</p>
     */
    public void testGetGroupsForUser()
    {
        // Init test.
        GroupManagerService gms = getGroupManagerService();
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anonuser2", "password");
            gms.addGroup("/testgroup1");
            gms.addGroup("/testgroup1/group1");
            gms.addGroup("/testgroup1/group2");
            gms.addGroup("/testgroup2");
            gms.addGroup("/testgroup2/group1");
            gms.addUserToGroup("anonuser2", "/testgroup1/group1");
            gms.addUserToGroup("anonuser2", "/testgroup1/group2");
            gms.addUserToGroup("anonuser2", "/testgroup2/group1");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testGetGroupsForUser(), " + sex, false);
        }

        try
        {
            Collection groups = gms.getGroupsForUser("anonuser2");
            assertEquals("groups size should be == 3", 3, groups.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("user exists. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        ums.removeUser("anonuser2");
        gms.removeGroup("/testgroup1");
        gms.removeGroup("/testgroup2");
    }

    /**
     * <p>Test get users in group.</p>
     */
    public void testGetUsersInGroup()
    {
        // Init test.
        GroupManagerService gms = getGroupManagerService();
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anonuser2", "password");
            ums.addUser("anonuser3", "password");
            ums.addUser("anonuser4", "password");
            gms.addGroup("/testgroup1");
            gms.addGroup("/testgroup1/group1");
            gms.addUserToGroup("anonuser2", "/testgroup1/group1");
            gms.addUserToGroup("anonuser3", "/testgroup1/group1");
            gms.addUserToGroup("anonuser4", "/testgroup1/group1");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testGetUsersInGroup(), " + sex, false);
        }

        try
        {
            Collection users = gms.getUsersInGroup("/testgroup1/group1");
            assertEquals("users size should be == 3", 3, users.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("group exists. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        ums.removeUser("anonuser2");
        ums.removeUser("anonuser3");
        ums.removeUser("anonuser4");
        gms.removeGroup("/testgroup1");
    }

    /**
     * <p>Test remove user from group.</p>
     */
    public void testRemoveUserFromGroup()
    {
        // Init test.
        GroupManagerService gms = getGroupManagerService();
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anonuser4", "password");
            gms.addGroup("/testgroup1");
            gms.addGroup("/testgroup1/group1");
            gms.addUserToGroup("anonuser4", "/testgroup1/group1");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemoveUserFromGroup(), " + sex, false);
        }

        gms.removeUserFromGroup("anonuser4", "/testgroup1/group1");
        try
        {
            Collection groups = gms.getGroupsForUser("anonuser4");
            assertEquals("groups size should be == 0", 0, groups.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("user exists. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        ums.removeUser("anonuser4");
        gms.removeGroup("/testgroup1");
    }

    /**
     * <p>Test is user in role.</p>
     */
    public void testIsUserInGroup()
    {
        // Init test.
        GroupManagerService gms = getGroupManagerService();
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anonuser3", "password");
            gms.addGroup("/testgroup1");
            gms.addGroup("/testgroup1/group1");
            gms.addUserToGroup("anonuser3", "/testgroup1/group1");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemoveUserFromGroup(), " + sex, false);
        }

        try
        {
            boolean isUserInGroup = gms.isUserInGroup("anonuser3", "/testgroup1/group1");
            assertTrue("anonuser3 should be in group /testgroup1/group1", isUserInGroup);
        }
        catch (SecurityException sex)
        {
            assertTrue("user and group exist. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        ums.removeUser("anonuser4");
        gms.removeGroup("/testgroup1");
    }

    /**
     * <p>Destroy group test objects.</p>
     */
    protected void destroyGroups() throws Exception
    {
        UserManagerService ums = getUserManagerService();
        GroupManagerService gms = getGroupManagerService();
        ums.removeUser("anonuser1");
        ums.removeUser("anonuser2");
        ums.removeUser("anonuser3");
        ums.removeUser("anonuser4");
        gms.removeGroup("/testgroup1");
        gms.removeGroup("/testgroup2");
        gms.removeGroup("/testusertogroup1");
        gms.removeGroup("/testgetgroup");
    }

}
