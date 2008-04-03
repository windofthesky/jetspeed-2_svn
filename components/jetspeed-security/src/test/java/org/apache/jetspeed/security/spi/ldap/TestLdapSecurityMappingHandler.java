/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi.ldap;


import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;

/**
 * <p>
 * Test the LDAP implementation for the {@link SecurityMappingHandler}.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a href="dlestrat@apache.org">David Le Strat</a>  
 */
public class TestLdapSecurityMappingHandler extends AbstractLdapTest
{
    /** The logger. */
    private static final Log logger = LogFactory.getLog(TestLdapSecurityMappingHandler.class);

    /** The group principal for gpUid1. */
    private GroupPrincipal gp1;
    
    /** The group principal for gpUid2. */
    private GroupPrincipal gp2;
    
    /** The role principal for gpUid1. */
    private RolePrincipal ro1;
    
    /** The role principal for gpUid2. */
    private RolePrincipal ro2;    

    /**
     * @see org.apache.jetspeed.security.spi.ldap.AbstractLdapTest#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        gp1 = new GroupPrincipalImpl(gpUid1);
        gp2 = new GroupPrincipalImpl(gpUid2);
        LdapDataHelper.seedGroupData(gpUid1);
        LdapDataHelper.seedGroupData(gpUid2);
        
        ro1 = new RolePrincipalImpl(roleUid1);
        ro2 = new RolePrincipalImpl(roleUid2);        
        LdapDataHelper.seedRoleData(roleUid1);
        LdapDataHelper.seedRoleData(roleUid2);
                
        LdapDataHelper.seedUserData(uid1, password);
        LdapDataHelper.seedUserData(uid2, password);
    }

    /**
     * @see org.apache.jetspeed.security.spi.ldap.AbstractLdapTest#tearDown()
     */
    protected void tearDown() throws Exception
    {
        LdapDataHelper.removeGroupData(gpUid1);
        LdapDataHelper.removeGroupData(gpUid2);
        LdapDataHelper.removeUserData(uid1);
        LdapDataHelper.removeUserData(uid2);
        LdapDataHelper.removeRoleData(roleUid1);
        LdapDataHelper.removeRoleData(roleUid2);
        super.tearDown();
    }

    /**
     * Adds 2 users to a group and checks their presence in the group
     * 
     * @throws Exception
     */
    public void testGetUserPrincipalsInGroup() throws Exception
    {
        secHandler.setUserPrincipalInGroup(uid1, gp1.getName());
        secHandler.setUserPrincipalInGroup(uid2, gp1.getName());
        String fullPathName = new GroupPrincipalImpl(gpUid1).getName();
        logger.debug("Group full path name from testGetUserPrincipalsInGroup()[" + fullPathName + "]");
        Set userPrincipals = secHandler.getUserPrincipalsInGroup(fullPathName);
        assertTrue(userPrincipals.contains(new UserPrincipalImpl(uid1)));
        assertTrue(userPrincipals.contains(new UserPrincipalImpl(uid2)));
        
        assertEquals("The user should have been in two groups.", 2, userPrincipals.size());
    }
    


    /**
     * Adds 1 user to 2 groups, and checks its presence in both groups
     * @throws Exception
     */
    public void testSetUserPrincipalInGroup() throws Exception
    {
        secHandler.setUserPrincipalInGroup(uid1, gp1.getName());
        secHandler.setUserPrincipalInGroup(uid1, gp2.getName());

        assertEquals("The user should have been in two groups.", 2, secHandler.getGroupPrincipals(uid1).size());
        
    }
    

    /**
     * Adds 1 user to 2 groups, and checks its presence in both groups
     * @throws Exception
     */
    public void testGetUserPrincipalInGroup() throws Exception
    {
        secHandler.setUserPrincipalInGroup(uid1, gp1.getName());
        secHandler.setUserPrincipalInGroup(uid1, gp2.getName());
        secHandler.setUserPrincipalInRole(uid1, ro1.getName());
        assertEquals(2, secHandler.getGroupPrincipals(uid1).size());
    }    

    /**
     * @throws Exception
     */
    public void testRemoveUserPrincipalInGroup() throws Exception
    {
        secHandler.setUserPrincipalInGroup(uid1, gp1.getName());
        secHandler.setUserPrincipalInGroup(uid1, gp2.getName());

        assertEquals("The user should have been in two groups.", 2, secHandler.getGroupPrincipals(uid1).size());

        secHandler.removeUserPrincipalInGroup(uid1, gp2.getName());
        assertEquals("The user should have been in one groups.", 1, secHandler.getGroupPrincipals(uid1).size());

        secHandler.removeUserPrincipalInGroup(uid1, gp1.getName());
        assertEquals("The user should have been in two groups.", 0, secHandler.getGroupPrincipals(uid1).size());
    }

    /**
     * @throws Exception
     */
    public void testSetUserPrincipalInGroupForNonExistantUser() throws Exception
    {
        try
        {
            secHandler.setUserPrincipalInGroup(Integer.toString(rand.nextInt()), gpUid1);
            fail("Trying to associate a group with a non-existant user should have thrown a SecurityException.");

        }
        catch (Exception e)
        {
            assertTrue("Trying to associate a group with a non-existant user should have thrown a SecurityException.",
                    e instanceof SecurityException);
        }
    }

    /**
     * @throws Exception
     */
    public void testSetUserPrincipalInGroupForNonExistantGroup() throws Exception
    {
        try
        {
            secHandler.setUserPrincipalInGroup(uid1, Integer.toString(rand.nextInt()));
            fail("Trying to associate a user with a non-existant group should have thrown a SecurityException.");

        }
        catch (Exception e)
        {
            assertTrue("Trying to associate a user with a non-existant group should have thrown a SecurityException.",
                    e instanceof SecurityException);
        }
    }
    
    /**
     * Adds 2 users to a group and checks their presence in the group
     * 
     * @throws Exception
     */
    public void testGetUserPrincipalsInRole() throws Exception
    {
        secHandler.setUserPrincipalInRole(uid1, ro1.getName());
        secHandler.setUserPrincipalInRole(uid2, ro1.getName());

        String fullPathName = new RolePrincipalImpl(roleUid1).getName();
        logger.debug("Role full path name from testGetUserPrincipalsInRole()[" + fullPathName + "]");
        Set userPrincipals = secHandler.getUserPrincipalsInRole(fullPathName);
        assertTrue(userPrincipals.contains(new UserPrincipalImpl(uid1)));
        assertTrue(userPrincipals.contains(new UserPrincipalImpl(uid2)));
        assertEquals("The user should have been in two roles.", 2, userPrincipals.size());
    }
    
    /**
     * Adds 2 users to a group and checks their presence in the group
     * 
     * @throws Exception
     */
    public void testGetRolePrincipalInGroup() throws Exception
    {
        secHandler.setRolePrincipalInGroup(gpUid1, ro1.getName());
        secHandler.setRolePrincipalInGroup(gpUid1, ro2.getName());
        secHandler.setRolePrincipalInGroup(gpUid2, ro1.getName());
        

        String fullPathName = new RolePrincipalImpl(roleUid1).getName();
        logger.debug("Role full path name from testGetUserPrincipalsInRole()[" + fullPathName + "]");
        assertEquals("The group should have 2 roles.", 2, secHandler.getRolePrincipalsInGroup(gpUid1).size());
        assertEquals("The group should have 1 role.", 1, secHandler.getRolePrincipalsInGroup(gpUid2).size());
    }
    
    /**
     * Adds 2 roles + 1 user to a group and checks their presence in the group.
     * 
     * @throws Exception
     */
    public void testGetRolePrincipalInGroupWithUsersInIt() throws Exception
    {
        secHandler.setRolePrincipalInGroup(gpUid1, ro1.getName());
        secHandler.setRolePrincipalInGroup(gpUid1, ro2.getName());
        secHandler.setRolePrincipalInGroup(gpUid2, ro1.getName());
        secHandler.setUserPrincipalInGroup(uid1,gpUid1);
        

        String fullPathName = new RolePrincipalImpl(roleUid1).getName();
        logger.debug("Role full path name from testGetUserPrincipalsInRole()[" + fullPathName + "]");
        assertEquals("The group should have 2 roles.", 2, secHandler.getRolePrincipalsInGroup(gpUid1).size());
        assertEquals("The group should have 1 role.", 1, secHandler.getRolePrincipalsInGroup(gpUid2).size());
    }     
    
    
    /**
     * Adds 2 users to a group and checks their presence in the group
     * 
     * @throws Exception
     */
    public void testGetRolePrincipalInGroup2() throws Exception
    {
        secHandler.setRolePrincipalInGroup(gpUid1, ro1.getName());
        secHandler.setRolePrincipalInGroup(gpUid2, ro1.getName());
        secHandler.setUserPrincipalInRole(uid1, ro1.getName());
        secHandler.setUserPrincipalInRole(uid1, ro2.getName());
        String fullPathName = new RolePrincipalImpl(gpUid1).getName();
        logger.debug("Role full path name from testGetUserPrincipalsInRole()[" + fullPathName + "]");
        assertEquals("The group should have contained 1 role.", 1, secHandler.getRolePrincipalsInGroup(gpUid1)
                .size());
        assertEquals("The group should have contained 1 role.", 1, secHandler.getRolePrincipalsInGroup(gpUid1)
                .size());
        
    }     

    /**
     * Adds 1 user to 2 roles, and checks its presence in both roles
     * @throws Exception
     */
    public void testSetUserPrincipalInRole() throws Exception
    {
        secHandler.setUserPrincipalInRole(uid1, ro1.getName());
        secHandler.setUserPrincipalInRole(uid1, ro2.getName());
        Set rolePrinciples = secHandler.getRolePrincipals(uid1);
        assertEquals("The user should have been in two roles.", 2, rolePrinciples.size());
        assertTrue(rolePrinciples.contains(ro1));
        assertTrue(rolePrinciples.contains(ro2));
        
    }
    
    /**
     * Adds 1 user to 2 roles & 1 group, and checks its presence in both roles
     * @throws Exception
     */
    public void testSetUserPrincipalInRole2() throws Exception
    {
        secHandler.setUserPrincipalInRole(uid1, ro1.getName());
        secHandler.setUserPrincipalInRole(uid1, ro2.getName());
        secHandler.setUserPrincipalInGroup(uid1, gp1.getName());
        Set rolePrinciples = secHandler.getRolePrincipals(uid1);
        assertEquals("The user should have been in two roles.", 2, rolePrinciples.size());
        assertTrue(rolePrinciples.contains(ro1));
        assertTrue(rolePrinciples.contains(ro2));
        
    }    

    /**
     * @throws Exception
     */
    public void testRemoveUserPrincipalInRole() throws Exception
    {
        secHandler.setUserPrincipalInRole(uid1, ro1.getName());
        secHandler.setUserPrincipalInRole(uid1, ro2.getName());
        assertEquals("The user should have been in two roles.", 2, secHandler.getRolePrincipals(uid1).size());

        secHandler.removeUserPrincipalInRole(uid1, ro1.getName());
        assertEquals("The user should have been in one roles.", 1, secHandler.getRolePrincipals(uid1).size());

        secHandler.removeUserPrincipalInRole(uid1, ro2.getName());
        assertEquals("The user should have been in zero roles.", 0, secHandler.getRolePrincipals(uid1).size());
    }
    
   
    /**
     * @throws Exception
     */
    public void testRemoveRolePrincipalInGroup() throws Exception
    {
        secHandler.setRolePrincipalInGroup(gpUid1, ro1.getName());
        secHandler.setRolePrincipalInGroup(gpUid1, ro2.getName());
        assertEquals("The role should have been in two groups.", 2, secHandler.getRolePrincipalsInGroup(gpUid1).size());

        secHandler.removeRolePrincipalInGroup(gpUid1,ro1.getName());
        assertEquals("The role should have been in one group.", 1, secHandler.getRolePrincipalsInGroup(gpUid1).size());

        secHandler.removeRolePrincipalInGroup(gpUid1, ro2.getName());
        assertEquals("The role should have been in 0 roles.", 0, secHandler.getRolePrincipalsInGroup(gpUid1).size());
    }
    

    /**
     * @throws Exception
     */
    public void testSetUserPrincipalInRoleForNonExistantUser() throws Exception
    {
        try
        {
            secHandler.setUserPrincipalInRole(Integer.toString(rand.nextInt()), roleUid1);
            fail("Trying to associate a role with a non-existant user should have thrown a SecurityException.");

        }
        catch (Exception e)
        {
            assertTrue("Trying to associate a role with a non-existant user should have thrown a SecurityException.",
                    e instanceof SecurityException);
        }
    }

    /**
     * @throws Exception
     */
    public void testSetUserPrincipalInRoleForNonExistantRole() throws Exception
    {
        try
        {
            secHandler.setUserPrincipalInRole(uid1, Integer.toString(rand.nextInt()));
            fail("Trying to associate a user with a non-existant role should have thrown a SecurityException.");

        }
        catch (Exception e)
        {
            assertTrue("Trying to associate a user with a non-existant role should have thrown a SecurityException.",
                    e instanceof SecurityException);
        }
    }    
}