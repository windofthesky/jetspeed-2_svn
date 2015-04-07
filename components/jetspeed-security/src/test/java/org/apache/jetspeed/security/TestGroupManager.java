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

import junit.framework.Test;

import javax.security.auth.Subject;
import java.util.List;


/**
 * <p>
 * Unit testing for {@link GroupManager}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id$
 */
public class TestGroupManager extends AbstractLDAPSecurityTestCase
{
    public static Test suite()
    {
        return createFixturedTestSuite(TestGroupManager.class, "ldapTestSetup", "ldapTestTeardown");
    }

    public TestGroupManager() {
        super();
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
            ums.addUser("anonuser1");
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
            Subject subject = ums.getSubject(ums.getUser("anonuser1"));
            assertTrue("anonuser1 should contain testusertogroup1.group1", SubjectHelper.getPrincipal(subject, Group.class, "testusertogroup1.group1") != null);
        }
        catch (SecurityException sex)
        {
            assertTrue("should add user to group. exception caught: " + sex, false);
        }
        // Add group with existing groups.
        try
        {
            gms.addUserToGroup("anonuser1", "testusertogroup1.group2");
            Subject subject = ums.getSubject(ums.getUser("anonuser1"));
            assertTrue("anonuser1 should contain testusertogroup1.group2", SubjectHelper.getPrincipal(subject, Group.class, "testusertogroup1.group2") != null);
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
    }

    /**
     * <p>
     * Test remove group.
     * </p>
     */
    public void testRemoveGroup() throws Exception
    {
        // Init test.
        User user = ums.addUser("anonuser2");
        Group group1 = gms.addGroup("testgroup1");
        Group group11 = gms.addGroup("testgroup1.group1");
        Group group12 = gms.addGroup("testgroup1.group2");
        Group group2 = gms.addGroup("testgroup2");
        Group group21 = gms.addGroup("testgroup2.group1");
        gms.addGroupToGroup(group11, group1, JetspeedPrincipalAssociationType.IS_A);
        gms.addGroupToGroup(group12, group1, JetspeedPrincipalAssociationType.IS_A);
        gms.addGroupToGroup(group21, group2, JetspeedPrincipalAssociationType.IS_A);
        gms.addUserToGroup("anonuser2", "testgroup1.group1");
        gms.addUserToGroup("anonuser2", "testgroup1.group2");
        gms.addUserToGroup("anonuser2", "testgroup2.group1");            

        try
        {
            gms.removeGroup("testgroup1.group1");            
            Subject subject = ums.getSubject(user);
            // because of hierarchical groups with generalization strategy as default. Was 5 groups + 2 users (including UserSubjectPrincipal), should now be 6
            // (4 groups + 2 users).
            assertEquals(
                "principal size should be == 6 after removing testgroup1.group1, for principals: " + subject.getPrincipals(),
                6,
                subject.getPrincipals().size());
            assertTrue("anonuser2 should not contain testgroup1.group1", SubjectHelper.getPrincipal(subject, Group.class, "testgroup1.group1") == null);
        }
        catch (SecurityException sex)
        {
            assertTrue("should remove group. exception caught: " + sex, false);
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
            gms.getGroup("testgroupdoesnotexist");
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
        assertEquals("expected group principal full path == testgetgroup", "testgetgroup", group.getName());
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
            ums.addUser("anonuser2");
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
            List<Group> groups = gms.getGroupsForUser("anonuser2");
            // Default hierarchy used in by generalization.
            assertEquals("groups size should be == 3", 3, groups.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("user exists. should not have thrown an exception: " + sex, false);
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
            List<Group> groups = gms.getGroupsInRole("testuserrolemapping");
            assertEquals("groups size should be == 3", 3, groups.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("role exists. should not have thrown an exception: " + sex, false);
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
            ums.addUser("anonuser4");
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
            List<Group> groups = gms.getGroupsForUser("anonuser4");
            assertEquals("groups size should be == 0", 0, groups.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("user exists. should not have thrown an exception: " + sex, false);
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
            ums.addUser("anonuser3");
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
    }

    /**
     * <p>
     * Test get groups.
     * </p>
     * 
     * @throws Exception Throws an exception.
     */
    public void testGetGroups() throws Exception
    {
        gms.addGroup("g1");
        gms.addGroup("g2");
        gms.addGroup("g3");
        assertTrue("group count should be 3", 3 == gms.getGroups(null).size());
    }
}