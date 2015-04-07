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


/**
 * <p>
 * Unit testing for {@link RoleManager}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id$
 */
public class TestRoleManager extends AbstractLDAPSecurityTestCase
{
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return createFixturedTestSuite(TestRoleManager.class, "ldapTestSetup", "ldapTestTeardown");
    }

    public TestRoleManager() {
        super();
    }

    /**
     * <p>
     * Test add role.
     * </p>
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
     * <p>
     * Test add user to role.
     * </p>
     */
    public void testAddRoleToUser()
    {
        // Init test.
        try
        {
            ums.addUser("anonuser1");
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

            Subject subject = ums.getSubject(ums.getUser("anonuser1"));
            assertTrue("anonuser1 should contain testusertorole1.role1", SubjectHelper.getPrincipal(subject, Role.class, "testusertorole1.role1") != null);
        }
        catch (SecurityException sex)
        {
            assertTrue("should add user to role. exception caught: " + sex, false);
        }
        // Add role with existing roles.
        try
        {
            rms.addRoleToUser("anonuser1", "testusertorole1.role2");
            Subject subject = ums.getSubject(ums.getUser("anonuser1"));
            assertTrue("anonuser1 should contain testusertorole1.role2", SubjectHelper.getPrincipal(subject, Role.class, "testusertorole1.role2") != null);
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
    }

    /**
     * <p>
     * Test remove role.
     * </p>
     */
    public void testRemoveRole() throws Exception
    {
        // Init test.
        User user = ums.addUser("anonuser2");
        Role role1 = rms.addRole("testrole1");
        Role role11 = rms.addRole("testrole1.role1");
        Role role12 = rms.addRole("testrole1.role2");
        Role role2 = rms.addRole("testrole2");
        Role role21 = rms.addRole("testrole2.role1");
        Role role22 = rms.addRole("testrole2.role2");
        rms.addRoleToRole(role11,role1, JetspeedPrincipalAssociationType.IS_A);
        rms.addRoleToRole(role21,role2, JetspeedPrincipalAssociationType.IS_A);
        rms.addRoleToRole(role12,role1, JetspeedPrincipalAssociationType.IS_A);
        rms.addRoleToRole(role22,role2, JetspeedPrincipalAssociationType.IS_A);
        rms.addRoleToUser("anonuser2", "testrole1.role1");
        rms.addRoleToUser("anonuser2", "testrole1.role2");
        rms.addRoleToUser("anonuser2", "testrole2.role1");

        try
        {
            Subject subject = ums.getSubject(user);
            assertEquals("Number of role principals should be 5", 5, SubjectHelper.getPrincipals(subject, Role.class).size());
            rms.removeRole("testrole1.role1");
            subject = ums.getSubject(user);
            assertEquals("Number of role principals should be 4", 4, SubjectHelper.getPrincipals(subject, Role.class).size());
            assertTrue("anonuser2 should not contain testrole1.role1", SubjectHelper.getPrincipal(subject, Role.class, "testrole1.role1") == null);
            // Make sure that the children are removed as well.
            rms.removeRole("testrole2");
            assertFalse(rms.roleExists("testrole2"));
            assertFalse(rms.roleExists("testrole2.role1"));
            subject = ums.getSubject(user);
            assertEquals("Number of role principals should be 2", 2, SubjectHelper.getPrincipals(subject, Role.class).size());
        }
        catch (SecurityException sex)
        {
            assertTrue("should remove role. exception caught: " + sex, false);
        }
    }

    /**
     * <p>
     * Test get role.
     * </p>
     */
    public void testGetRole()
    {
        // Test when the role does not exist.
        try
        {
            rms.getRole("testroledoesnotexist");
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
        assertEquals("expected role principal full path name == testgetrole", "testgetrole", role.getName());
    }

    /**
     * <p>
     * Test remove role from user.
     * </p>
     */
    public void testRemoveRoleFromUser()
    {
        // Init test.
        try
        {
            ums.addUser("anonuser5");
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
            assertEquals("roles size should be == 0", 0, rms.getRolesForUser("anonuser5").size());
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
    public void testIsUserInRole()
    {
        // Init test.
        try
        {
            ums.addUser("anonuser4");
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
    }

    /**
     * <p>
     * Test is user in role.
     * </p>
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
    }

    /**
     * <p>
     * Test get roles.
     * </p>
     * 
     * @throws Exception Throws an exception.
     */
    public void testGetRoles() throws Exception
    {
        rms.addRole("r1");
        rms.addRole("r2");
        rms.addRole("r3");
        assertTrue("role count should be 3", 3 == rms.getRoles(null).size());
               
    }
}