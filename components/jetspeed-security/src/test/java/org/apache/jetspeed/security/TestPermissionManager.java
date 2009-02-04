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

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.impl.TransientUser;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * <p>Unit testing for {@link PermissionManager}.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestPermissionManager extends AbstractSecurityTestcase
{
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPermissionManager.class);
    }

    public void testWildcardPermissionCheck()
    throws Exception
    {
        //////////////////////////////////////////////////////////////////////////
        // setup
        ////////////
        User adminUser = ums.addUser("adminTEST");
        User userUser = ums.addUser("userTEST");
        Role adminRole = rms.addRole("adminTEST");
        Role userRole = rms.addRole("userTEST");
        JetspeedPermission adminPerm = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "adminTEST::*", "view, edit");
        JetspeedPermission userPerm = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "demoTEST::*", "view, edit");
        rms.addRoleToUser(adminUser.getName(), adminRole.getName());
        rms.addRoleToUser(userUser.getName(), userRole.getName());
        rms.addRoleToUser(adminUser.getName(), userRole.getName());            
        pms.addPermission(adminPerm);
        pms.addPermission(userPerm);
        pms.grantPermission(adminPerm, adminRole);
        pms.grantPermission(userPerm, userRole);                        
        
        //////////////////////////////////////////////////////////////////////////
        // Run Test
        ////////////        
        Set<Principal> adminPrincipals = new PrincipalsSet();
        Set<Credential> adminPublicCredentials = new HashSet<Credential>();
        Set<Credential> adminPrivateCredentials = new HashSet<Credential>();
        Set<Principal> userPrincipals = new PrincipalsSet();
        Set<Credential> userPublicCredentials = new HashSet<Credential>();
        Set<Credential> userPrivateCredentials = new HashSet<Credential>();
        
        adminPrincipals.add(adminUser);
        adminPrincipals.add(adminRole);
        adminPrincipals.add(userRole);

        userPrincipals.add(userUser);
        userPrincipals.add(userRole);
        
        try
        {
            Subject adminSubject = new Subject(true, adminPrincipals, adminPublicCredentials, adminPrivateCredentials);
            Subject userSubject = new Subject(true, userPrincipals, userPublicCredentials, userPrivateCredentials);                    
            assertTrue("access to admin Perm should be granted to Admin ", checkPermission(adminSubject, adminPerm));
            assertTrue("access to user should NOT be granted to Admin ", checkPermission(adminSubject, userPerm));
            assertTrue("access to User Perm should be granted to User ", checkPermission(userSubject, userPerm));
            assertFalse("access to Admin Perm should NOT be granted to User ", checkPermission(userSubject, adminPerm));
            
        }
        catch (AccessControlException e)
        {
            fail("failed permission check");
        }
    }
    
    public void testPermissionCheck()
    throws Exception
    {
        //////////////////////////////////////////////////////////////////////////
        // setup
        ////////////
        User user = ums.addUser("test");
        Role role1 = rms.addRole("Role1");
        Role role2 = rms.addRole("Role2");            
        JetspeedPermission perm1 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "PortletOne", "view, edit");
        JetspeedPermission perm2 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "PortletTwo", "view");
        JetspeedPermission perm3 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "PortletThree", "view");
        JetspeedPermission perm3a = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "PortletThreeA", "view, edit");
        rms.addRoleToUser(user.getName(), role1.getName());
        rms.addRoleToUser(user.getName(), role2.getName());
        pms.addPermission(perm1);
        pms.addPermission(perm2);
        pms.addPermission(perm3);
        pms.addPermission(perm3a);
        pms.grantPermission(perm1, user);
        pms.grantPermission(perm2, role1);                        
        pms.grantPermission(perm3, role2);            
        
        //////////////////////////////////////////////////////////////////////////
        // Run Test
        ////////////        
        Set<Principal> principals = new PrincipalsSet();
        Set<Credential> publicCredentials = new HashSet<Credential>();
        Set<Credential> privateCredentials = new HashSet<Credential>();
        principals.add(user);
        principals.add(role1);
        principals.add(role2);

        try
        {
            Subject subject = new Subject(true, principals, publicCredentials, privateCredentials);        
            assertTrue("access to perm1 should be granted ", checkPermission(subject, perm1));
            assertTrue("access to perm2 should be granted ", checkPermission(subject, perm2));
            assertTrue("access to perm3 should be granted ", checkPermission(subject, perm3));
            assertFalse("access to perm3a should be denied ", checkPermission(subject, perm3a));
        }
        catch (AccessControlException e)
        {
            fail("failed permission check");
        }
    }
    
    /**
     * <p>Test remove principal and associated permissions.</p>
     */
    public void testRemovePrincipalPermissions() throws Exception
    {
        // Init test.
        User user = ums.addUser("test");
        JetspeedPermission perm = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "anontestportlet", "view, edit");
        pms.addPermission(perm);
        pms.grantPermission(perm, user);
        
        try
        {
            // test revokeAllPrincipal a Transient User representation to ensure
            // that use-case is covered too because it requires additional
            // handling (lookup of the principal Id first)
            pms.revokeAllPermissions(new TransientUser("test"));
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
    }

    /**
     * <p>Test remove permission.</p>
     */
    public void testPermissionExists() throws Exception
    {
        JetspeedPermission perm1 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "removepermission1", "view, edit, secure, minimized, maximized");
        JetspeedPermission perm2 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "removepermission2", "view, edit, minimized, maximized");
        pms.addPermission(perm1);
        assertTrue(pms.permissionExists(perm1));
        assertFalse(pms.permissionExists(perm2));
    }
    
    /**
     * <p>Test remove permission.</p>
     */
    public void testRemovePermission()
    {
        // Init test.
        User user = null;
        Role role = null;
        JetspeedPermission perm1 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "removepermission1", "view, edit, secure, minimized, maximized");
        JetspeedPermission perm2 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "removepermission2", "view, edit, minimized, maximized");
        try
        {
            user = ums.addUser("removepermission");
            role = rms.addRole("removepermissionrole");
            pms.addPermission(perm1);
            pms.addPermission(perm2);
            pms.grantPermission(perm1, user);
            pms.grantPermission(perm2, user);
            pms.grantPermission(perm1, role);
            pms.grantPermission(perm2, role);
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemovePermission(), " + sex, false);
        }
        try
        {
            pms.removePermission(perm1);
            Permissions permCol1 = pms.getPermissions(user);
            assertTrue(
                "should only contain permission == {name = "
                    + perm2.getName()
                    + "}, {action = "
                    + perm2.getActions()
                    + "}, in collection of size == 1, actual size: "
                    + (Collections.list(permCol1.elements())).size(),
                validatePermissions(permCol1, perm2, 1));
            Permissions permCol2 = pms.getPermissions(role);
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
    }

    /**
     * <p>Test grant permission to principal.</p>
     */
    public void testGrantPermission() throws Exception
    {
        // Init test.
        User user1 = ums.newTransientUser("testgrantpermission1");
        User user2 = ums.addUser ("testgrantpermission2");
        JetspeedPermission perm1 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "testportlet", "view, minimized, secure");
        JetspeedPermission perm2 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "testportlet2", "view, minimized, maximized, secure");
        pms.addPermission(perm1);
        pms.addPermission(perm2);

        // Test permission for new permission and new principal (does not exist).      
        try
        {
            pms.grantPermission(perm1, user1);
            assertTrue("principal does not exist. should have caught exception.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Grant  permission to existing principal.
        pms.grantPermission(perm2, user2);
        
        Permissions permCol1 = pms.getPermissions(user2);
        assertTrue(
            "should contain permission == {name = "
                + perm2.getName()
                + "}, {action = "
                + perm2.getActions()
                + "}, in collection of size == 1, actual size: "
                + (Collections.list(permCol1.elements())).size(),
            validatePermissions(permCol1, perm2, 1));
        
        // Test grant duplicate permission for same principal
        pms.grantPermission(perm2, user2);
        Permissions permCol2 = pms.getPermissions(user2);
        assertTrue(
            "should contain permission == {name = "
                + perm2.getName()
                + "}, {action = "
                + perm2.getActions()
                + "}, in collection of size == 1, actual size: "
                + (Collections.list(permCol2.elements())).size(),
            validatePermissions(permCol2, perm2, 1));
    }

    /**
     * <p>Test get permissions from a principal.</p>
     */
    public void testGetPrincipalPermissions() throws Exception
    {
        // Init test.
        User user = ums.addUser("anon");
        JetspeedPermission perm1 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "anontestportlet", "view");
        JetspeedPermission perm2 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "anontestportlet2", "view, edit");
        pms.addPermission(perm1);
        pms.addPermission(perm2);
        pms.grantPermission(perm1, user);
        pms.grantPermission(perm2, user);

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
    }

    /**
     * <p>Test get permissions from a collection of principals.</p>
     */
    public void testGetPermissions() throws Exception
    {
        // Init test.
        User user = ums.addUser("anon");
        Role role1 = rms.addRole("anonrole1");
        Role role2 = rms.addRole("anonrole2");
        Group group1 = gms.addGroup("anongroup1");
        Group group2 = gms.addGroup("anongroup2");
        JetspeedPermission perm1 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "anontestportlet", "view");
        JetspeedPermission perm2 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "anontestportlet2", "view, edit");
        JetspeedPermission perm3 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "anontestportlet3", "view, edit, secure");
        JetspeedPermission perm4 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "anontestportlet4", "view, edit, secure, minimized");
        pms.addPermission(perm1);
        pms.addPermission(perm2);
        pms.addPermission(perm3);
        pms.addPermission(perm4);
        pms.grantPermission(perm1, role1);
        pms.grantPermission(perm1, role2);
        pms.grantPermission(perm2, role2);
        pms.grantPermission(perm3, role2);
        pms.grantPermission(perm4, role2);
        pms.grantPermission(perm1, group1);
        pms.grantPermission(perm1, group2);
        pms.grantPermission(perm2, group2);
        pms.grantPermission(perm3, group2);
        pms.grantPermission(perm4, group2);

        Principal[] principals = new Principal[]{user,role1,role2,group1,group2};
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
    }

    /**
     * <p>Test revoke permission.</p>
     */
    public void testRevokePermission() throws Exception
    {
        // Init test.
        User user = ums.addUser("revokepermission");
        JetspeedPermission perm1 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "revokepermission1", "view, edit, minimized, maximized");
        JetspeedPermission perm2 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "revokepermission2", "view, edit, minimized, maximized");
        pms.addPermission(perm1);
        pms.addPermission(perm2);
        pms.grantPermission(perm1, user);
        pms.grantPermission(perm2, user);
        try
        {
            pms.revokePermission(perm2, user);
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
    }
    
    private boolean checkPermission(Subject subject, final JetspeedPermission permission) 
    {
        try
        {
            JSSubject.doAsPrivileged(subject, new PrivilegedAction<Object>()                
            {
                public Object run()
                {
                    AccessController.checkPermission((Permission)permission);
                    return null;
                }
            }, null);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;         
    }
    
    

    /**
     * <p>Validate whether permission belongs to permissions and whether the permissions
     * size equals the size provided.</p>
     * @param permissions The permissions.
     * @param permission The permission to validate.
     * @param size The permissions expected size.
     * @return
     */
    private boolean validatePermissions(Permissions permissions, JetspeedPermission permission, int size)
    {
        Enumeration<Permission> permissionEnums = permissions.elements();
        boolean hasPermission = false;
        int count = 0;
        while (permissionEnums.hasMoreElements())
        {
            count++;
            Permission enumPerm = (Permission) permissionEnums.nextElement();
            if (enumPerm.equals(permission))
            {
                hasPermission = true;
            }
        }
        boolean validated = ((hasPermission) && (count == size));
        return validated;
    }

    public void testUpdatePermission() throws Exception
    {
        // Init test.
        Role role1 = rms.addRole("role1");
        Role role2 = rms.addRole("role2");
        Role role3 = rms.addRole("role3");
        Role role4 = rms.addRole("role4");
        JetspeedPermission perm1 = pms.newPermission(PermissionFactory.PORTLET_PERMISSION, "testportlet", "view");
        pms.addPermission(perm1);

        // Grant 1 and 2      
        pms.grantPermission(perm1, role1);
        pms.grantPermission(perm1, role2);

        List<JetspeedPrincipal> principals = pms.getPrincipals(perm1);        
        assertTrue("principal count should be 2 ", principals.size() == 2);        
        // PermissionManager returns a list sorted by [type,name]
        assertTrue("first element should be [role1] but found ["+principals.get(0).getName()+"]", principals.get(0).getName().equals("role1"));
        assertTrue("second element should be [role2] but found ["+principals.get(1).getName()+"]", principals.get(1).getName().equals("role2"));
        
        // Try to update collection
        try
        {
            List<JetspeedPrincipal> roles = new ArrayList<JetspeedPrincipal>();
            roles.add(role1);
            roles.add(role3);
            roles.add(role4);
            pms.grantPermissionOnlyTo(perm1, roles);
        }
        catch (SecurityException sex)
        {
            assertTrue("principal does not exist. caught exception, " + sex, false);
        }
        principals = pms.getPrincipals(perm1);
        assertTrue("principal count should be 3 ", principals.size() == 3);
        // PermissionManager returns a list sorted by [type,name]
        assertTrue("first element should be [role1] but found ["+principals.get(0).getName()+"]", principals.get(0).getName().equals("role1"));
        assertTrue("second element should be [role3] but found ["+principals.get(1).getName()+"]", principals.get(1).getName().equals("role3"));
        assertTrue("third element should be [role4] but found ["+principals.get(2).getName()+"]", principals.get(2).getName().equals("role4"));
    }
    
}
