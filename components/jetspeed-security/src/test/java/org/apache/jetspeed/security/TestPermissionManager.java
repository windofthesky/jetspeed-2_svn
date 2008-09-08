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

import java.lang.reflect.Constructor;
import java.security.AccessControlException;
import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.security.auth.Subject;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.om.InternalPermission;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;

/**
 * <p>Unit testing for {@link PermissionManager}.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestPermissionManager extends AbstractSecurityTestcase
{
    private static final Comparator principalComparator = new Comparator()
    {
        public int compare(Object arg0, Object arg1)
        {
            return (((Principal)arg0).getName().compareTo(((Principal)arg1).getName()));
        }
    };

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        destroyPermissions();
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

    public void testWildcardPermissionCheck()
    throws Exception
    {
        //////////////////////////////////////////////////////////////////////////
        // setup
        ////////////
        UserPrincipal adminUser = new UserPrincipalImpl("adminTEST");
        UserPrincipal userUser = new UserPrincipalImpl("userTEST");
        PortletPermission adminPerm = new PortletPermission("adminTEST::*", "view, edit");
        PortletPermission userPerm = new PortletPermission("demoTEST::*", "view, edit");
        RolePrincipal adminRole = new RolePrincipalImpl("adminTEST");
        RolePrincipal userRole = new RolePrincipalImpl("userTEST");
        
        try
        {
            ums.addUser(adminUser.getName(), "password");
            ums.addUser(userUser.getName(), "password");            
            rms.addRole(adminRole.getName());
            rms.addRole(userRole.getName());            
            rms.addRoleToUser(adminUser.getName(), adminRole.getName());
            rms.addRoleToUser(userUser.getName(), userRole.getName());
            rms.addRoleToUser(adminUser.getName(), userRole.getName());            
            pms.addPermission(adminPerm);
            pms.addPermission(userPerm);
            pms.grantPermission(adminRole, adminPerm);
            pms.grantPermission(userRole, userPerm);                        
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemovePrincipalPermissions(), " + sex, false);
        }
        
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
            
            boolean access = pms.checkPermission(adminSubject, adminPerm);
            assertTrue("access to admin Perm should be granted to Admin ", access);
            
            access = pms.checkPermission(adminSubject, userPerm);
            assertTrue("access to user should NOT be granted to Admin ", access);

            access = pms.checkPermission(userSubject, userPerm);
            assertTrue("access to User Perm should be granted to User ", access);
            
            access = pms.checkPermission(userSubject, adminPerm);
            assertFalse("access to Admin Perm should NOT be granted to User ", access);
            
        }
        catch (AccessControlException e)
        {
            fail("failed permission check");
        }
        finally
        {
            //////////////////////////////////////////////////////////////////////////
            // cleanup
            ////////////
            try
            {
                ums.removeUser(adminUser.getName());
                ums.removeUser(userUser.getName());
                rms.removeRole(adminRole.getName());
                rms.removeRole(userRole.getName());
                
                pms.removePermission(adminPerm);
                pms.removePermission(userPerm);
            }
            catch (SecurityException sex)
            {
                assertTrue("could not remove user and permission. exception caught: " + sex, false);
            }            
        }
        
        
    }
    
    public void testPermissionCheck()
    throws Exception
    {
        //////////////////////////////////////////////////////////////////////////
        // setup
        ////////////
        UserPrincipal user = new UserPrincipalImpl("test");
        PortletPermission perm1 = new PortletPermission("PortletOne", "view, edit");
        PortletPermission perm2 = new PortletPermission("PortletTwo", "view");
        PortletPermission perm3 = new PortletPermission("PortletThree", "view");
        PortletPermission perm3a = new PortletPermission("PortletThreeA", "view, edit");
        RolePrincipal role1 = new RolePrincipalImpl("Role1");
        RolePrincipal role2 = new RolePrincipalImpl("Role2");
        
        try
        {
            ums.addUser(user.getName(), "password");
            rms.addRole(role1.getName());
            rms.addRole(role2.getName());            
            rms.addRoleToUser(user.getName(), role1.getName());
            rms.addRoleToUser(user.getName(), role2.getName());
            pms.addPermission(perm1);
            pms.addPermission(perm2);
            pms.addPermission(perm3);
            pms.addPermission(perm3a);
            pms.grantPermission(user, perm1);
            pms.grantPermission(role1, perm2);                        
            pms.grantPermission(role2, perm3);            
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testRemovePrincipalPermissions(), " + sex, false);
        }
        
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
            boolean access = pms.checkPermission(subject, perm1);
            assertTrue("access to perm1 should be granted ", access);
            access = pms.checkPermission(subject, perm2);
            assertTrue("access to perm2 should be granted ", access);
            access = pms.checkPermission(subject, perm3);
            assertTrue("access to perm3 should be granted ", access);
            access = pms.checkPermission(subject, perm3a);
            assertFalse("access to perm3a should be denied ", access);
        }
        catch (AccessControlException e)
        {
            fail("failed permission check");
        }
        finally
        {
            //////////////////////////////////////////////////////////////////////////
            // cleanup
            ////////////
            try
            {
                ums.removeUser(user.getName());
                rms.removeRole(role1.getName());
                rms.removeRole(role2.getName());            
                pms.removePermission(perm1);
                pms.removePermission(perm2);
                pms.removePermission(perm3);
                pms.removePermission(perm3a);                
            }
            catch (SecurityException sex)
            {
                assertTrue("could not remove user and permission. exception caught: " + sex, false);
            }            
        }
        
        
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

        ArrayList<Principal> principals = new ArrayList<Principal>();
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

    /**
     * <p>Destroy permission test objects.</p>
     */
    protected void destroyPermissions() throws Exception
    {
        this.destroyPrincipals();
        for (InternalPermission ip : pms.getInternalPermissions())
        {
            Class permissionClass = Class.forName(ip.getClassname());
            Class[] parameterTypes = { String.class, String.class };
            Constructor permissionConstructor = permissionClass.getConstructor(parameterTypes);
            Object[] initArgs = { ip.getName(), ip.getActions() };
            Permission permission = (Permission) permissionConstructor.newInstance(initArgs);            
            pms.removePermission(permission);
        }                
    }
    
    public void testUpdatePermission()
    {
        // Init test.
        RolePrincipal role1 = new RolePrincipalImpl("role1");
        RolePrincipal role2 = new RolePrincipalImpl("role2");
        RolePrincipal role3 = new RolePrincipalImpl("role3");
        RolePrincipal role4 = new RolePrincipalImpl("role4");
        PortletPermission perm1 = new PortletPermission("testportlet", "view");
        try
        {
            rms.addRole(role1.getName());
            rms.addRole(role2.getName());
            rms.addRole(role3.getName());
            rms.addRole(role4.getName());            
            pms.addPermission(perm1);
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to init testUpdatePermission(), " + sex, false);
        }

        // Grant 1 and 2      
        try
        {
            pms.grantPermission(role1, perm1);
            pms.grantPermission(role2, perm1);
        }
        catch (SecurityException sex)
        {
            assertTrue("failed to grant on testUpdatePermission. caught exception, " + sex, false);
        }

        Collection<Principal> principals = pms.getPrincipals(perm1);        
        assertTrue("principal count should be 2 ", principals.size() == 2);        
        Object [] array = principals.toArray();
        Arrays.sort(array, principalComparator);
        assertTrue("element is Principal ", array[0] instanceof Principal);
        assertTrue("first element not found ", ((Principal)array[0]).getName().equals("role1"));
        assertTrue("second element not found ", ((Principal)array[1]).getName().equals("role2"));
        
        
        // Try to update collection
        try
        {
            Collection<Principal> roles = new Vector<Principal>();
            roles.add(role1);
            roles.add(role3);
            roles.add(role4);
            pms.updatePermission(perm1, roles);
        }
        catch (SecurityException sex)
        {
            assertTrue("principal does not exist. caught exception, " + sex, false);
        }
        principals = pms.getPrincipals(perm1);
        assertTrue("principal count should be 3 ", principals.size() == 3);
        array = principals.toArray();
        Arrays.sort(array, principalComparator);
        assertTrue("first element should be [role1] but found ["+((Principal)array[0]).getName()+"]", ((Principal)array[0]).getName().equals("role1"));
        assertTrue("second element not found ", ((Principal)array[1]).getName().equals("role3"));
        assertTrue("third element not found ", ((Principal)array[2]).getName().equals("role4"));
        
        // Cleanup test.
        try
        {
            rms.removeRole(role1.getName());
            rms.removeRole(role2.getName());
            rms.removeRole(role3.getName());
            rms.removeRole(role4.getName());
            pms.removePermission(perm1);
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user and permission. exception caught: " + sex, false);
        }
    }
    
}
