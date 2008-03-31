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

import java.util.List;

import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;

/**
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a
 *         href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestLdapRoleSecurityHandler extends AbstractLdapTest
{

    /**
     * @see org.apache.jetspeed.security.spi.ldap.AbstractLdapTest#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        LdapDataHelper.seedRoleData(roleUid1);
    }

    /**
     * @see org.apache.jetspeed.security.spi.ldap.AbstractLdapTest#tearDown()
     */
    protected void tearDown() throws Exception
    {
        LdapDataHelper.removeRoleData(roleUid1);
        super.tearDown();
    }

    /**
     * @throws Exception
     */
    public void testGetRolePrincipal() throws Exception
    {
        String fullPath = (new RolePrincipalImpl(roleUid1)).getFullPath();
        RolePrincipal rolePrincipal = roleHandler.getRolePrincipal(roleUid1);
        assertNotNull("Role was not found.", rolePrincipal);
        assertEquals(roleUid1,rolePrincipal.getName());
        assertEquals(fullPath,rolePrincipal.getFullPath());
    }

    /**
     * @throws Exception
     */
    public void testAddDuplicateRolePrincipal() throws Exception
    {
    	roleHandler.setRolePrincipal(new RolePrincipalImpl(roleUid1));
        List roles = roleHandler.getRolePrincipals("");
        assertEquals(1,roles.size());
    }
    
    /**
     * @throws Exception
     */
    public void testGetNonExistingRolePrincipal() throws Exception
    {
        RolePrincipal role = roleHandler.getRolePrincipal(roleUid1 + "FAKE");
        assertNull(role);
    }

    /**
     * @throws Exception
     */
    public void testRemoveExistantUserPrincipal() throws Exception
    {
        RolePrincipal gp = new RolePrincipalImpl(roleUid1);
        roleHandler.removeRolePrincipal(gp);
        RolePrincipal rolePrincipal = roleHandler.getRolePrincipal(gp.getFullPath());
        assertNull("Role was found and should have been removed.", rolePrincipal);
        List roles = roleHandler.getRolePrincipals("");
        assertEquals(0,roles.size());        
    }

    /**
     * @throws Exception
     */
    public void testRemoveNonExistantUserPrincipal() throws Exception
    {
        String localUid = Integer.toString(rand.nextInt()).toString();
        RolePrincipal localPrin = new RolePrincipalImpl(localUid);
        roleHandler.removeRolePrincipal(localPrin);
        List roles = roleHandler.getRolePrincipals("");
        assertEquals(1,roles.size());
    }

    /**
     * @throws Exception
     */
    public void testGetRolePrincipals() throws Exception
    {
        try
        {
            LdapDataHelper.seedRoleData(gpUid2);
            assertTrue("getUserPrincipals should have returned more than one user.", roleHandler.getRolePrincipals("*")
                    .size() > 1);

            String fullPath = (new RolePrincipalImpl(roleUid1)).getFullPath();
            List roles = roleHandler.getRolePrincipals(fullPath);
            assertTrue("getRolePrincipals should have returned one role.", roles.size() == 1);
            assertTrue("List should have consisted of RolePrincipal objects.", roles.get(0) instanceof RolePrincipal);

            String localUid = Integer.toString(rand.nextInt()).toString();
            assertTrue("getRolePrincipals should not have found any roles with the specified filter.", roleHandler
                    .getRolePrincipals(new RolePrincipalImpl(localUid).getFullPath()).isEmpty());
        }
        finally
        {
            LdapDataHelper.removeRoleData(gpUid2);
        }
    }

}
