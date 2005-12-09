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
package org.apache.jetspeed.security.spi;

import java.security.Permission;
import java.security.Permissions;
import java.security.Principal;

import org.apache.jetspeed.security.PortletPermission;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * Unit testing for {@link RoleSecurityHandler}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class TestRoleSecurityHandler extends AbstractSecurityTestcase
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
        super.tearDown();
    }

    /**
     * <p>
     * Constructs the suite.
     * </p>
     * 
     * @return The {@Test}.
     */
    public static Test suite()
    {
        return new TestSuite(TestRoleSecurityHandler.class);
    }

    /**
     * <p>
     * Test <code>getRolePrincipal</code>.
     * </p>
     */
    public void testGetRolePrincipal() throws Exception
    {
        initRole();
        Principal principal = rsh.getRolePrincipal("testusertorole1");
        assertNotNull(principal);
        assertEquals("testusertorole1", principal.getName());
        destroyRole();
    }
    
    /**
     * <p>
     * Test <code>removeRolePrincipal</code>.
     * </p>
     */
    public void testRemoveRolePrincipal() throws Exception
    {
        initMappedRole();
        rsh.removeRolePrincipal(new RolePrincipalImpl("mappedrole"));
        // The user should still exist.
        assertTrue(ums.userExists("mappedroleuser"));
        // The group should still exist.
        assertTrue(gms.groupExists("mappedgroup"));
        // The permission should still exist.
        assertTrue(pms.permissionExists(new PortletPermission("myportlet", "view")));
        // The user-role mapping should be gone.
        assertFalse(rms.isUserInRole("mappedroleuser", "mappedrole"));
        // The group-role mapping should be gone.
        assertFalse(rms.isGroupInRole("mappedgroup", "mappedroleuser"));
        // The permission-role mapping should be gone.
        Permissions perms = pms.getPermissions(new RolePrincipalImpl("mappedrole"));
        assertFalse(perms.implies(new PortletPermission("myportlet", "view")));
        
        destroyMappedRole();
    }
    
    /**
     * <p>
     * Initialize role test object.
     * </p>
     */
    protected void initRole() throws Exception
    {
        rms.addRole("testusertorole1");
    }

    /**
     * <p>
     * Destroy role test object.
     * </p>
     */
    protected void destroyRole() throws Exception
    {
        rms.removeRole("testusertorole1");
    }
    
    protected void initMappedRole() throws Exception
    {
        destroyMappedRole();
        ums.addUser("mappedroleuser", "password");
        rms.addRole("mappedrole");
        rms.addRole("mappedrole.role1");
        gms.addGroup("mappedgroup");
        
        Permission perm = new PortletPermission("myportlet", "view");
        pms.addPermission(perm);
        pms.grantPermission(new RolePrincipalImpl("mappedrole"), perm);
        
        rms.addRoleToUser("mappedroleuser", "mappedrole");
        rms.addRoleToGroup("mappedrole", "mappedgroup");    
    }
    
    protected void destroyMappedRole() throws Exception
    {
        if (ums.userExists("mappedroleuser"))
            ums.removeUser("mappedroleuser");
        if (rms.roleExists("mappedrole"))
            rms.removeRole("mappedrole.role1");
        if (rms.roleExists("mappedrole.role1"))
            rms.removeRole("mappedrole");
        if (gms.groupExists("mappedgroup"))
            gms.removeGroup("mappedgroup");
        PortletPermission pp = new PortletPermission("myportlet", "view");
        if (pms.permissionExists(pp))
            pms.removePermission(pp);   
    }
}