/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;

/**
 * <p>Unit testing for {@link RoleManagerService}.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestRoleManagerService extends JetspeedTest
{

    private GroupManagerService gms = null;
    private RoleManagerService rms = null;
    private UserManagerService ums = null;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestRoleManagerService(String testName)
    {
        super(testName);
    }

    /**
     * <p>Start the tests.</p>
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestRoleManagerService.class.getName()});
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
        destroyRoles();
    }

    /**
     * <p>Creates the test suite.</p>
     * @return A test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        return new JetspeedTestSuite(TestRoleManagerService.class);
    }

    /**
     * <p>Returns the {@link RoleManagerService}.</p>
     * @return The RoleManagerService.
     */
    protected RoleManagerService getRoleManagerService()
    {
        if (rms == null)
        {
            rms = (RoleManagerService) CommonPortletServices.getPortalService(RoleManagerService.SERVICE_NAME);
        }
        return rms;
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
        assertNotNull(getRoleManagerService());
    }

    /**
     * <p>Test add role.</p>
     */
    public void testAddRole()
    {
        RoleManagerService rms = getRoleManagerService();
        // Add role with path beginning with '/'.
        try
        {
            rms.addRole("/testrole");
        }
        catch (SecurityException sex)
        {
            assertTrue("role should not already exists. exception caught: " + sex, false);
        }
        // Add role without path beginning with '/'.
        try
        {
            rms.addRole("testrole/newrole0");
        }
        catch (SecurityException sex)
        {
            assertTrue("role should not already exists. exception caught: " + sex, false);
        }
        // Add existing role.
        try
        {
            rms.addRole("/testrole/newrole0");
            assertTrue("role should already exists. exception not thrown.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Remove role.
        rms.removeRole("/testrole");
    }

    /**
     * <p>Test add user to role.</p>
     */
    public void testAddRoleToUser()
    {
        // Init test.
        RoleManagerService rms = getRoleManagerService();
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anonuser1", "password");
            rms.addRole("/testusertorole1");
            rms.addRole("/testusertorole1/role1");
            rms.addRole("/testusertorole1/role2");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testAddRoleToUser(), " + sex, false);
        }
        // Add role with no prior roles.
        try
        {
            rms.addRoleToUser("anonuser1", "/testusertorole1/role1");
            Collection principals = ums.getUser("anonuser1").getSubject().getPrincipals();
            Principal found =
                SecurityHelper.getPrincipal(
                    new Subject(false, new HashSet(principals), new HashSet(), new HashSet()),
                    RolePrincipal.class);
            assertNotNull("found principal is null", found);
            assertTrue(
                "found principal should be /testusertorole1/role1, " + found.getName(),
                found.getName().equals((new RolePrincipalImpl("/testusertorole1/role1")).getName()));
        }
        catch (SecurityException sex)
        {
            assertTrue("should add user to role. exception caught: " + sex, false);
        }
        // Add role with existing roles.
        try
        {
            rms.addRoleToUser("anonuser1", "/testusertorole1/role2");
            Collection principals = ums.getUser("anonuser1").getSubject().getPrincipals();
            assertTrue(
                "anonuser1 should contain /testusertorole1/role2",
                principals.contains(new RolePrincipalImpl("/testusertorole1/role2")));
        }
        catch (SecurityException sex)
        {
            assertTrue("should add user to role. exception caught: " + sex, false);
        }
        // Add role when user does not exist.
        try
        {
            rms.addRoleToUser("anonuser123", "/testusertorole1/role2");
            assertTrue("should catch exception: user does not exist.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Add role when role does not exist.
        try
        {
            rms.addRoleToUser("anonuser1", "/testusertorole1/role123");
            assertTrue("should catch exception: role does not exist.", false);
        }
        catch (SecurityException sex)
        {
        }

        // Cleanup.
        ums.removeUser("anonuser1");
        rms.removeRole("/testusertorole1");
    }

    /**
     * <p>Test remove role.</p>
     */
    public void testRemoveRole()
    {
        // Init test.
        RoleManagerService rms = getRoleManagerService();
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anonuser2", "password");
            rms.addRole("/testrole1");
            rms.addRole("/testrole1/role1");
            rms.addRole("/testrole1/role2");
            rms.addRole("/testrole2");
            rms.addRole("/testrole2/role1");
            rms.addRole("/testrole2/role2");
            rms.addRoleToUser("anonuser2", "/testrole1/role1");
            rms.addRoleToUser("anonuser2", "/testrole1/role2");
            rms.addRoleToUser("anonuser2", "/testrole2/role1");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemoveRole(), " + sex, false);
        }

        rms.removeRole("/testrole1/role1");
        try
        {
            Collection principals = ums.getUser("anonuser2").getSubject().getPrincipals();
            assertEquals(
                "principal size should be == 3 after removing /testrole1/role1, for principals: " + principals.toString(),
                3,
                principals.size());
            assertFalse(
                "anonuser2 should not contain /testrole1/role1",
                principals.contains(new RolePrincipalImpl(RolePrincipalImpl.getFullPathFromPrincipalName("/testrole1/role1"))));
        }
        catch (SecurityException sex)
        {
            assertTrue("should remove role. exception caught: " + sex, false);
        }

        // Cleanup test.
        ums.removeUser("anonuser2");
        rms.removeRole("/testrole1");
        rms.removeRole("/testrole2");
    }

    /**
     * <p>Test get role.</p>
     */
    public void testGetRole()
    {
        RoleManagerService rms = getRoleManagerService();
        // Test when the role does not exist.
        try
        {
            Role role = rms.getRole("/testroledoesnotexist");
            assertTrue("role does not exist. should have thrown an exception.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Test when the role exists.
        Role role = null;
        try
        {
            rms.addRole("/testgetrole");
            role = rms.getRole("/testgetrole");
        }
        catch (SecurityException sex)
        {
            assertTrue("role exists. should not have thrown an exception.", false);
        }
        assertNotNull("role is null", role);
        // Test the RolePrincipal.
        RolePrincipal rolePrincipal = role.getPrincipal();
        assertNotNull("role principal is null", rolePrincipal);
        assertEquals(
            "expected role principal full path == /role/testgetrole",
            "/role/testgetrole",
            SecurityHelper.getPrincipalFullPath(rolePrincipal));

        // Test the Role Preferences.
        Preferences preferences = role.getPreferences();
        assertEquals("expected role node == /role/testgetrole", "/role/testgetrole", preferences.absolutePath());

        // Cleanup test.
        rms.removeRole("/testgetrole");
    }

    /**
     * <p>Test get roles for user.</p>
     */
    public void testGetRolesForUser()
    {
        // Init test.
        RoleManagerService rms = getRoleManagerService();
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anonuser3", "password");
            rms.addRole("/testuserrolemapping");
            rms.addRole("/testuserrolemapping/role1");
            rms.addRole("/testuserrolemapping/role2");
            rms.addRoleToUser("anonuser3", "/testuserrolemapping");
            rms.addRoleToUser("anonuser3", "/testuserrolemapping/role1");
            rms.addRoleToUser("anonuser3", "/testuserrolemapping/role2");
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
        ums.removeUser("anonuser3");
        rms.removeRole("/testuserrolemapping");
    }

    /**
     * <p>Test get users in role.</p>
     */
    public void testGetUsersInRole()
    {
        // Init test.
        RoleManagerService rms = getRoleManagerService();
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anonuser3", "password");
            ums.addUser("anonuser4", "password");
            rms.addRole("/testuserrolemapping");
            rms.addRole("/testuserrolemapping/role1");
            rms.addRole("/testuserrolemapping/role2");
            rms.addRoleToUser("anonuser3", "/testuserrolemapping");
            rms.addRoleToUser("anonuser3", "/testuserrolemapping/role1");
            rms.addRoleToUser("anonuser3", "/testuserrolemapping/role2");
            rms.addRoleToUser("anonuser4", "/testuserrolemapping");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testGetUsersInRole(), " + sex, false);
        }

        try
        {
            Collection users = rms.getUsersInRole("/testuserrolemapping");
            assertEquals("users size should be == 2", 2, users.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("role exists. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        ums.removeUser("anonuser3");
        ums.removeUser("anonuser4");
        rms.removeRole("/testuserrolemapping");
    }

    /**
     * <p>Test get roles for group.</p>
     */
    public void testGetRolesForGroup()
    {
        // Init test.
        RoleManagerService rms = getRoleManagerService();
        GroupManagerService gms = getGroupManagerService();
        try
        {
            rms.addRole("/testuserrolemapping");
            rms.addRole("/testuserrolemapping/role1");
            rms.addRole("/testuserrolemapping/role3");
            gms.addGroup("/testrolegroupmapping");
            rms.addRoleToGroup("/testuserrolemapping", "/testrolegroupmapping");
            rms.addRoleToGroup("/testuserrolemapping/role1", "/testrolegroupmapping");
            rms.addRoleToGroup("/testuserrolemapping/role3", "/testrolegroupmapping");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testGetRolesForGroup(), " + sex, false);
        }

        try
        {
            Collection roles = rms.getRolesForGroup("/testrolegroupmapping");
            assertEquals("roles size should be == 3", 3, roles.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("group exists. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        rms.removeRole("/testuserrolemapping");
        gms.removeGroup("/testrolegroupmapping");
    }

    /**
     * <p>Test get groups in role.</p>
     */
    public void testGetGroupsInRole()
    {
        // Init test.
        RoleManagerService rms = getRoleManagerService();
        GroupManagerService gms = getGroupManagerService();
        try
        {
            rms.addRole("/testuserrolemapping");
            gms.addGroup("/testrolegroupmapping");
            gms.addGroup("/testrolegroupmapping/group1");
            gms.addGroup("/testrolegroupmapping/group2");
            rms.addRoleToGroup("/testuserrolemapping", "/testrolegroupmapping");
            rms.addRoleToGroup("/testuserrolemapping", "/testrolegroupmapping/group1");
            rms.addRoleToGroup("/testuserrolemapping", "/testrolegroupmapping/group2");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testGetRolesForGroup(), " + sex, false);
        }

        try
        {
            Collection groups = rms.getGroupsInRole("/testuserrolemapping");
            assertEquals("groups size should be == 3", 3, groups.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("role exists. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        rms.removeRole("/testuserrolemapping");
        gms.removeGroup("/testrolegroupmapping");
    }

    /**
     * <p>Test remove role from user.</p>
     */
    public void testRemoveRoleFromUser()
    {
        // Init test.
        RoleManagerService rms = getRoleManagerService();
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anonuser5", "password");
            rms.addRole("/testrole3");
            rms.addRoleToUser("anonuser5", "/testrole3");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemoveRoleFromUser(), " + sex, false);
        }

        rms.removeRoleFromUser("anonuser5", "/testrole3");
        try
        {
            Collection roles = rms.getRolesForUser("anonuser5");
            assertEquals("roles size should be == 0", 0, roles.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("user exists. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        ums.removeUser("anonuser5");
        rms.removeRole("/testrole3");
    }

    /**
     * <p>Test is user in role.</p>
     */
    public void testIsUserInRole()
    {
        // Init test.
        RoleManagerService rms = getRoleManagerService();
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anonuser4", "password");
            rms.addRole("/testuserrolemapping");
            rms.addRoleToUser("anonuser4", "/testuserrolemapping");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testIsUserInRole(), " + sex, false);
        }

        try
        {
            boolean isUserInRole = rms.isUserInRole("anonuser4", "/testuserrolemapping");
            assertTrue("anonuser4 should be in role /testuserrolemapping", isUserInRole);
        }
        catch (SecurityException sex)
        {
            assertTrue("user and role exist. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        ums.removeUser("anonuser4");
        rms.removeRole("/testuserrolemapping");
    }

    /**
     * <p>Test remove role from group.</p>
     */
    public void testRemoveRoleFromGroup()
    {
        // Init test.
        RoleManagerService rms = getRoleManagerService();
        GroupManagerService gms = getGroupManagerService();
        try
        {
            rms.addRole("/testuserrolemapping");
            rms.addRole("/testuserrolemapping/role1");
            rms.addRole("/testuserrolemapping/role3");
            gms.addGroup("/testrolegroupmapping");
            rms.addRoleToGroup("/testuserrolemapping", "/testrolegroupmapping");
            rms.addRoleToGroup("/testuserrolemapping/role1", "/testrolegroupmapping");
            rms.addRoleToGroup("/testuserrolemapping/role3", "/testrolegroupmapping");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemoveRoleFromGroup(), " + sex, false);
        }

        rms.removeRoleFromGroup("/testuserrolemapping/role3", "/testrolegroupmapping");
        try
        {
            Collection roles = rms.getRolesForGroup("/testrolegroupmapping");
            assertEquals("roles size should be == 2", 2, roles.size());
        }
        catch (SecurityException sex)
        {
            assertTrue("group exists. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        rms.removeRole("/testuserrolemapping");
        gms.removeGroup("/testrolegroupmapping");
    }

    /**
     * <p>Test is user in role.</p>
     */
    public void testIsGroupInRole()
    {
        // Init test.
        RoleManagerService rms = getRoleManagerService();
        GroupManagerService gms = getGroupManagerService();
        try
        {
            rms.addRole("/testuserrolemapping");
            gms.addGroup("/testrolegroupmapping");
            rms.addRoleToGroup("/testuserrolemapping", "/testrolegroupmapping");
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testIsGroupInRole(), " + sex, false);
        }
        try
        {
            boolean isGroupInRole = rms.isGroupInRole("/testrolegroupmapping", "/testuserrolemapping");
            assertTrue("/testrolegroupmapping should be in role /testuserrolemapping", isGroupInRole);
        }
        catch (SecurityException sex)
        {
            assertTrue("group and role exist. should not have thrown an exception: " + sex, false);
        }

        // Cleanup test.
        rms.removeRole("/testuserrolemapping");
        gms.removeGroup("/testrolegroupmapping");
    }

    /**
     * <p>Destroy role test objects.</p>
     */
    protected void destroyRoles()
    {
        UserManagerService ums = getUserManagerService();
        RoleManagerService rms = getRoleManagerService();
        GroupManagerService gms = getGroupManagerService();
        ums.removeUser("anonuser1");
        ums.removeUser("anonuser2");
        ums.removeUser("anonuser3");
        ums.removeUser("anonuser4");
        ums.removeUser("anonuser5");
        rms.removeRole("/testrole1");
        rms.removeRole("/testrole2");
        rms.removeRole("/testrole3");
        rms.removeRole("/testgetrole");
        rms.removeRole("/testuserrolemapping");
        gms.removeGroup("/testrolegroupmapping");
    }

}
