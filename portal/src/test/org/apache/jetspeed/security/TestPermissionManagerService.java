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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.security.Permission;
import java.security.Permissions;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;

/**
 * <p>Unit testing for {@link PermissionManagerService}.</p>
 *
 * @author <a href="mailto:david@sensova.com">David Le Strat</a>
 */
public class TestPermissionManagerService extends JetspeedTest
{

    private PermissionManagerService pms = null;
    private RoleManagerService rms = null;
    private GroupManagerService gms = null;
    private UserManagerService ums = null;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestPermissionManagerService(String testName)
    {
        super(testName);
    }

    /**
     * <p>Start the tests.</p>
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestPermissionManagerService.class.getName()});
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
        destroyPermissions();
    }

    /**
     * <p>Creates the test suite.</p>
     * @return A test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        return new JetspeedTestSuite(TestPermissionManagerService.class);
    }

    /**
     * <p>Returns the {@link PermissionManagerService}.</p>
     * @return The PermissionManagerService.
     */
    protected PermissionManagerService getPermissionManagerService()
    {
        if (pms == null)
        {
            pms = (PermissionManagerService) CommonPortletServices.getPortalService(PermissionManagerService.SERVICE_NAME);
        }
        return pms;
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
     * <p>Test that a {@link PermissionManagerService} was returned.</p>
     */
    public void testService()
    {
        assertNotNull(getPermissionManagerService());
    }

    /**
     * <p>Test remove principal and associated permissions.</p>
     */
    public void testRemovePrincipalPermissions()
    {
        // Init test.
        PermissionManagerService pms = getPermissionManagerService();
        UserManagerService ums = getUserManagerService();
        UserPrincipal user = new UserPrincipalImpl("test");
        PortletPermission perm = new PortletPermission("anontestportlet", "view, edit");
        try
        {
            ums.addUser(user.getName(), "password");
            pms.grantPermission(user, perm);
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemovePrincipalPermissions(), " + sex, false);
        }

        pms.removePermissions(user);
        Permissions permissions = pms.getPermissions(user);
        assertEquals(
            "permissions should be empty for user " + user.getName(),
            0,
            (Collections.list(permissions.elements())).size());

        // Cleanup test.
        ums.removeUser(user.getName());
        pms.removePermission(perm);
    }

    /**
     * <p>Test remove permission.</p>
     */
    public void testRemovePermission()
    {
        // Init test.
        PermissionManagerService pms = getPermissionManagerService();
        UserManagerService ums = getUserManagerService();
        RoleManagerService rms = getRoleManagerService();
        UserPrincipal user = new UserPrincipalImpl("removepermission");
        RolePrincipal role = new RolePrincipalImpl("removepermissionrole");
        PortletPermission perm1 = new PortletPermission("removepermission1", "view, edit, delete, minimize, maximize");
        PortletPermission perm2 = new PortletPermission("removepermission2", "view, edit, minimize, maximize");
        try
        {
            ums.addUser(user.getName(), "password");
            rms.addRole(role.getName());
            pms.grantPermission(user, perm1);
            pms.grantPermission(user, perm2);
            pms.grantPermission(role, perm1);
            pms.grantPermission(role, perm2);
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemovePermission(), " + sex, false);
        }

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

        // Cleanup test.
        ums.removeUser(user.getName());
        pms.removePermission(perm1);
        pms.removePermission(perm2);
    }

    /**
     * <p>Test grant permission to principal.</p>
     */
    public void testGrantPermission()
    {
        // Init test.
        PermissionManagerService pms = getPermissionManagerService();
        UserManagerService ums = getUserManagerService();
        UserPrincipal user1 = new UserPrincipalImpl("testgrantpermission1");
        UserPrincipal user2 = new UserPrincipalImpl("testgrantpermission2");
        PortletPermission perm1 = new PortletPermission("testportlet", "view, minimize, delete");
        PortletPermission perm2 = new PortletPermission("testportlet", "view, minimize, maximize, delete");
        try
        {
            ums.addUser(user2.getName(), "password");
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
        ums.removeUser(user2.getName());
        pms.removePermission(perm1);
        pms.removePermission(perm2);
    }

    /**
     * <p>Test get permissions from a principal.</p>
     */
    public void testGetPrincipalPermissions()
    {
        // Init test.
        PermissionManagerService pms = getPermissionManagerService();
        UserManagerService ums = getUserManagerService();
        UserPrincipal user = new UserPrincipalImpl("anon");
        PortletPermission perm1 = new PortletPermission("anontestportlet", "view");
        PortletPermission perm2 = new PortletPermission("anontestportlet", "view, edit");
        try
        {
            ums.addUser(user.getName(), "password");
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
        ums.removeUser(user.getName());
        pms.removePermission(perm1);
        pms.removePermission(perm2);
    }

    /**
     * <p>Test get permissions from a collection of principals.</p>
     */
    public void testGetPermissions()
    {
        // Init test.
        PermissionManagerService pms = getPermissionManagerService();
        UserManagerService ums = getUserManagerService();
        RoleManagerService rms = getRoleManagerService();
        GroupManagerService gms = getGroupManagerService();
        UserPrincipal user = new UserPrincipalImpl("anon");
        RolePrincipal role1 = new RolePrincipalImpl("anonrole1");
        RolePrincipal role2 = new RolePrincipalImpl("anonrole2");
        GroupPrincipal group1 = new GroupPrincipalImpl("anongroup1");
        GroupPrincipal group2 = new GroupPrincipalImpl("anongroup2");
        PortletPermission perm1 = new PortletPermission("anontestportlet", "view");
        PortletPermission perm2 = new PortletPermission("anontestportlet", "view, edit");
        PortletPermission perm3 = new PortletPermission("anontestportlet", "view, edit, delete");
        PortletPermission perm4 = new PortletPermission("anontestportlet", "view, edit, delete, minimize");
        try
        {
            ums.addUser(user.getName(), "password");
            rms.addRole(role1.getName());
            rms.addRole(role2.getName());
            gms.addGroup(group1.getName());
            gms.addGroup(group2.getName());
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
        ums.removeUser(user.getName());
        pms.removePermission(perm1);
        pms.removePermission(perm2);
        pms.removePermission(perm3);
        pms.removePermission(perm4);
    }

    /**
     * <p>Test revoke permission.</p>
     */
    public void testRevokePermission()
    {
        // Init test.
        PermissionManagerService pms = getPermissionManagerService();
        UserManagerService ums = getUserManagerService();
        UserPrincipal user = new UserPrincipalImpl("revokepermission");
        PortletPermission perm1 = new PortletPermission("revokepermission1", "view, edit, minimize, maximize");
        PortletPermission perm2 = new PortletPermission("revokepermission2", "view, edit, minimize, maximize");
        try
        {
            ums.addUser(user.getName(), "password");
            pms.grantPermission(user, perm1);
            pms.grantPermission(user, perm2);
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRevokePermission(), " + sex, false);
        }
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

        // Cleanup test.
        ums.removeUser(user.getName());
        pms.removePermission(perm1);
        pms.removePermission(perm2);
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
        PermissionManagerService pms = getPermissionManagerService();
        UserManagerService ums = getUserManagerService();
        RoleManagerService rms = getRoleManagerService();
        GroupManagerService gms = getGroupManagerService();

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
        // Remove permissions.
        PortletPermission perm1 = new PortletPermission("anontestportlet", "view");
        PortletPermission perm2 = new PortletPermission("anontestportlet", "view, edit");
        PortletPermission perm3 = new PortletPermission("anontestportlet", "view, edit, delete");
        PortletPermission perm4 = new PortletPermission("anontestportlet", "view, edit, delete, minimize");
        PortletPermission perm5 = new PortletPermission("removepermission1", "view, edit, delete, minimize, maximize");
        PortletPermission perm6 = new PortletPermission("removepermission2", "view, edit, minimize, maximize");
        PortletPermission perm7 = new PortletPermission("revokepermission1", "view, edit, minimize, maximize");
        PortletPermission perm8 = new PortletPermission("revokepermission2", "view, edit, minimize, maximize");
        PortletPermission perm9 = new PortletPermission("testportlet", "view, minimize, delete");
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
}
