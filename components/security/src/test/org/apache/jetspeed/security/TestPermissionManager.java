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

import java.security.Permission;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;

/**
 * <p>Unit testing for {@link PermissionManager}.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestPermissionManager extends AbstractSecurityTestcase
{

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestPermissionManager(String testName)
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
        destroyPermissions();
        super.tearDown();        
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPermissionManager.class);
    }
    
    /**
     * <p>Test remove principal and associated permissions.</p>
     */
    public void testRemovePrincipalPermissions()
    {
        // Init test.
        UserPrincipal user = new UserPrincipalImpl("test");
        PortletPermission perm = new PortletPermission("anontestportlet", "view, edit");
        try
        {
            ums.addUser(user.getName(), "password");
            pms.addPermission(perm);
            pms.grantPermission(user, perm);
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemovePrincipalPermissions(), " + sex, false);
        }
        try
        {
            pms.removePermissions(user);
            Permissions permissions = pms.getPermissions(user);
            assertEquals(
                "permissions should be empty for user " + user.getName(),
                0,
                (Collections.list(permissions.elements())).size());
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove permission. exception caught: " + sex, false);
        }
        // Cleanup test.
        try
        {
            ums.removeUser(user.getName());
            pms.removePermission(perm);
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user and permission. exception caught: " + sex, false);
        }
    }

    /**
     * <p>Test remove permission.</p>
     */
    public void testPermissionExists()
    {
        PortletPermission perm1 = new PortletPermission("removepermission1", "view, edit, secure, minimized, maximized");
        PortletPermission perm2 = new PortletPermission("removepermission2", "view, edit, minimized, maximized");
        try
        {
            pms.addPermission(perm1);
            assertTrue(pms.permissionExists(perm1));
        }
        catch (SecurityException sex)
        {
            assertTrue("could not add permission, " + sex, false);
        }
        assertFalse(pms.permissionExists(perm2));
        
        //  Cleanup test.
        try
        {
            pms.removePermission(perm1);
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove permission. exception caught: " + sex, false);
        }
    }
    
    /**
     * <p>Test remove permission.</p>
     */
    public void testRemovePermission()
    {
        // Init test.
        UserPrincipal user = new UserPrincipalImpl("removepermission");
        RolePrincipal role = new RolePrincipalImpl("removepermissionrole");
        PortletPermission perm1 = new PortletPermission("removepermission1", "view, edit, secure, minimized, maximized");
        PortletPermission perm2 = new PortletPermission("removepermission2", "view, edit, minimized, maximized");
        try
        {
            ums.addUser(user.getName(), "password");
            rms.addRole(role.getName());
            pms.addPermission(perm1);
            pms.addPermission(perm2);
            pms.grantPermission(user, perm1);
            pms.grantPermission(user, perm2);
            pms.grantPermission(role, perm1);
            pms.grantPermission(role, perm2);
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemovePermission(), " + sex, false);
        }
        try
        {
            pms.removePermission(perm1);
            Permissions permCol1 = pms.getPermissions(new UserPrincipalImpl("removepermission"));
            assertTrue(
                "should only contain permission == {name = "
                    + perm2.getName()
                    + "}, {action = "
                    + perm2.getActions()
                    + "}, in collection of size == 1, actual size: "
                    + (Collections.list(permCol1.elements())).size(),
                validatePermissions(permCol1, perm2, 1));
            Permissions permCol2 = pms.getPermissions(new RolePrincipalImpl("removepermissionrole"));
            assertTrue(
                "should only contain permission == {name = "
                    + perm2.getName()
                    + "}, {action = "
                    + perm2.getActions()
                    + "}, in collection of size == 1, actual size: "
                    + (Collections.list(permCol2.elements())).size(),
                validatePermissions(permCol2, perm2, 1));
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove permission. exception caught: " + sex, false);
        }
        // Cleanup test.
        try
        {
            ums.removeUser(user.getName());
            pms.removePermission(perm1);
            pms.removePermission(perm2);
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user and permission. exception caught: " + sex, false);
        }
    }

    /**
     * <p>Test grant permission to principal.</p>
     */
    public void testGrantPermission()
    {
        // Init test.
        UserPrincipal user1 = new UserPrincipalImpl("testgrantpermission1");
        UserPrincipal user2 = new UserPrincipalImpl("testgrantpermission2");
        PortletPermission perm1 = new PortletPermission("testportlet", "view, minimized, secure");
        PortletPermission perm2 = new PortletPermission("testportlet", "view, minimized, maximized, secure");
        try
        {
            ums.addUser(user2.getName(), "password");
            pms.addPermission(perm1);
            pms.addPermission(perm2);
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testGrantPermission(), " + sex, false);
        }

        // Test permission for new permission and new principal (does not exist).      
        try
        {
            pms.grantPermission(user1, perm1);
            assertTrue("principal does not exist. should have caught exception.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Test insert new permission and existing principal.
        try
        {
            pms.grantPermission(user2, perm2);
        }
        catch (SecurityException sex)
        {
            assertTrue("principal does not exist. caught exception, " + sex, false);
        }
        Permissions permCol1 = pms.getPermissions(user2);
        assertTrue(
            "should contain permission == {name = "
                + perm2.getName()
                + "}, {action = "
                + perm2.getActions()
                + "}, in collection of size == 1, actual size: "
                + (Collections.list(permCol1.elements())).size(),
            validatePermissions(permCol1, perm2, 1));
        // Test insert duplicate permission for same principal
        try
        {
            pms.grantPermission(user2, perm2);
        }
        catch (SecurityException sex)
        {
            assertTrue("principal does not exist. caught exception, " + sex, false);
        }
        Permissions permCol2 = pms.getPermissions(user2);
        assertTrue(
            "should contain permission == {name = "
                + perm2.getName()
                + "}, {action = "
                + perm2.getActions()
                + "}, in collection of size == 1, actual size: "
                + (Collections.list(permCol2.elements())).size(),
            validatePermissions(permCol2, perm2, 1));

        // Cleanup test.
        try
        {
            ums.removeUser(user2.getName());
            pms.removePermission(perm1);
            pms.removePermission(perm2);
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user and permission. exception caught: " + sex, false);
        }
    }

    /**
     * <p>Test get permissions from a principal.</p>
     */
    public void testGetPrincipalPermissions()
    {
        // Init test.
        UserPrincipal user = new UserPrincipalImpl("anon");
        PortletPermission perm1 = new PortletPermission("anontestportlet", "view");
        PortletPermission perm2 = new PortletPermission("anontestportlet", "view, edit");
        try
        {
            ums.addUser(user.getName(), "password");
            pms.addPermission(perm1);
            pms.addPermission(perm2);
            pms.grantPermission(user, perm1);
            pms.grantPermission(user, perm2);
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testGetPrincipalPermissions(), " + sex, false);
        }

        Permissions permissions = pms.getPermissions(user);
        assertTrue(
            "should contain permission == {name = "
                + perm1.getName()
                + "}, {action = "
                + perm1.getActions()
                + "}, in collection of size == 2, actual size: "
                + (Collections.list(permissions.elements())).size(),
            validatePermissions(permissions, perm1, 2));
        assertTrue(
            "should contain permission == {name = "
                + perm2.getName()
                + "}, {action = "
                + perm2.getActions()
                + "}, in collection of size == 2, actual size: "
                + (Collections.list(permissions.elements())).size(),
            validatePermissions(permissions, perm2, 2));

        // Cleanup test.
        try
        {
            ums.removeUser(user.getName());
            pms.removePermission(perm1);
            pms.removePermission(perm2);
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user and permission. exception caught: " + sex, false);
        }
    }

    /**
     * <p>Test get permissions from a collection of principals.</p>
     */
    public void testGetPermissions()
    {
        // Init test.
        UserPrincipal user = new UserPrincipalImpl("anon");
        RolePrincipal role1 = new RolePrincipalImpl("anonrole1");
        RolePrincipal role2 = new RolePrincipalImpl("anonrole2");
        GroupPrincipal group1 = new GroupPrincipalImpl("anongroup1");
        GroupPrincipal group2 = new GroupPrincipalImpl("anongroup2");
        PortletPermission perm1 = new PortletPermission("anontestportlet", "view");
        PortletPermission perm2 = new PortletPermission("anontestportlet", "view, edit");
        PortletPermission perm3 = new PortletPermission("anontestportlet", "view, edit, secure");
        PortletPermission perm4 = new PortletPermission("anontestportlet", "view, edit, secure, minimized");
        try
        {
            ums.addUser(user.getName(), "password");
            rms.addRole(role1.getName());
            rms.addRole(role2.getName());
            gms.addGroup(group1.getName());
            gms.addGroup(group2.getName());
            pms.addPermission(perm1);
            pms.addPermission(perm2);
            pms.addPermission(perm3);
            pms.addPermission(perm4);
            pms.grantPermission(role1, perm1);
            pms.grantPermission(role2, perm1);
            pms.grantPermission(role2, perm2);
            pms.grantPermission(role2, perm3);
            pms.grantPermission(role2, perm4);
            pms.grantPermission(group1, perm1);
            pms.grantPermission(group2, perm1);
            pms.grantPermission(group2, perm2);
            pms.grantPermission(group2, perm3);
            pms.grantPermission(group2, perm4);
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testGetPrincipalPermissions(), " + sex, false);
        }

        ArrayList principals = new ArrayList();
        principals.add(user);
        principals.add(role1);
        principals.add(role2);
        principals.add(group1);
        principals.add(group2);
        Permissions permissions = pms.getPermissions(principals);
        assertTrue(
            "should contain permission == {name = "
                + perm1.getName()
                + "}, {action = "
                + perm1.getActions()
                + "}, in collection of size == 4, actual size: "
                + (Collections.list(permissions.elements())).size(),
            validatePermissions(permissions, perm1, 4));
        assertTrue(
            "should contain permission == {name = "
                + perm2.getName()
                + "}, {action = "
                + perm2.getActions()
                + "}, in collection of size == 4, actual size: "
                + (Collections.list(permissions.elements())).size(),
            validatePermissions(permissions, perm2, 4));
        assertTrue(
            "should contain permission == {name = "
                + perm3.getName()
                + "}, {action = "
                + perm3.getActions()
                + "}, in collection of size == 4, actual size: "
                + (Collections.list(permissions.elements())).size(),
            validatePermissions(permissions, perm3, 4));
        assertTrue(
            "should contain permission == {name = "
                + perm4.getName()
                + "}, {action = "
                + perm4.getActions()
                + "}, in collection of size == 4, actual size: "
                + (Collections.list(permissions.elements())).size(),
            validatePermissions(permissions, perm4, 4));

        // Cleanup test.
        try
        {
            ums.removeUser(user.getName());
            pms.removePermission(perm1);
            pms.removePermission(perm2);
            pms.removePermission(perm3);
            pms.removePermission(perm4);
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user. exception caught: " + sex, false);
        }
    }

    /**
     * <p>Test revoke permission.</p>
     */
    public void testRevokePermission()
    {
        // Init test.
        UserPrincipal user = new UserPrincipalImpl("revokepermission");
        PortletPermission perm1 = new PortletPermission("revokepermission1", "view, edit, minimized, maximized");
        PortletPermission perm2 = new PortletPermission("revokepermission2", "view, edit, minimized, maximized");
        try
        {
            ums.addUser(user.getName(), "password");
            pms.addPermission(perm1);
            pms.addPermission(perm2);
            pms.grantPermission(user, perm1);
            pms.grantPermission(user, perm2);
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRevokePermission(), " + sex, false);
        }
        try
        {
            pms.revokePermission(user, perm2);
            Permissions permCol = pms.getPermissions(user);
            assertTrue(
                "should only contain permission == {name = "
                    + perm1.getName()
                    + "}, {action = "
                    + perm1.getActions()
                    + "}, in collection of size == 1, actual size: "
                    + (Collections.list(permCol.elements())).size(),
                validatePermissions(permCol, perm1, 1));
        }
        catch (SecurityException sex)
        {
            assertTrue("could not revoke permission. esception caught: " + sex, false);
        }
        // Cleanup test.
        try
        {
            ums.removeUser(user.getName());
            pms.removePermission(perm1);
            pms.removePermission(perm2);
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user. exception caught: " + sex, false);
        }
    }

    /**
     * <p>Validate whether permission belongs to permissions and whether the permissions
     * size equals the size provided.</p>
     * @param permissions The permissions.
     * @param permission The permission to validate.
     * @param size The permissions expected size.
     * @return
     */
    private boolean validatePermissions(Permissions permissions, Permission permission, int size)
    {
        Enumeration enum = permissions.elements();
        boolean hasPermission = false;
        int count = 0;
        while (enum.hasMoreElements())
        {
            count++;
            Permission enumPerm = (Permission) enum.nextElement();
            if (enumPerm.equals(permission))
            {
                hasPermission = true;
            }
        }
        boolean validated = ((hasPermission) && (count == size));
        return validated;
    }

    /**
     * <p>Destroy permission test objects.</p>
     */
    protected void destroyPermissions()
    {
        try
        {
            // Remove users.
            ums.removeUser("anon");
            ums.removeUser("test");
            ums.removeUser("removepermission");
            ums.removeUser("revokepermission");
            ums.removeUser("testgrantpermission2");
            // Remove roles.
            rms.removeRole("anonrole1");
            rms.removeRole("anonrole2");
            rms.removeRole("removepermissionrole");
            // Remove groups.
            gms.removeGroup("anongroup1");
            gms.removeGroup("anongroup2");
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user, role and group. exception caught: " + sex, false);
        }
        // Remove permissions.
        PortletPermission perm1 = new PortletPermission("anontestportlet", "view");
        PortletPermission perm2 = new PortletPermission("anontestportlet", "view, edit");
        PortletPermission perm3 = new PortletPermission("anontestportlet", "view, edit, secure");
        PortletPermission perm4 = new PortletPermission("anontestportlet", "view, edit, secure, minimized");
        PortletPermission perm5 = new PortletPermission("removepermission1", "view, edit, secure, minimized, maximized");
        PortletPermission perm6 = new PortletPermission("removepermission2", "view, edit, minimized, maximized");
        PortletPermission perm7 = new PortletPermission("revokepermission1", "view, edit, minimized, maximized");
        PortletPermission perm8 = new PortletPermission("revokepermission2", "view, edit, minimized, maximized");
        PortletPermission perm9 = new PortletPermission("testportlet", "view, minimized, secure");
        try
        {
            pms.removePermission(perm1);
            pms.removePermission(perm2);
            pms.removePermission(perm3);
            pms.removePermission(perm4);
            pms.removePermission(perm5);
            pms.removePermission(perm6);
            pms.removePermission(perm7);
            pms.removePermission(perm8);
            pms.removePermission(perm9);
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove permissions. exception caught: " + sex, false);
        }
    }
}
