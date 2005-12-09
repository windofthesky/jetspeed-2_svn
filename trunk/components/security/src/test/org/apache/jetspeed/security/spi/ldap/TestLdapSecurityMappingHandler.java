/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;

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
        LdapDataHelper.seedUserData(uid1, password);
        LdapDataHelper.seedUserData(uid2, password);
    }

    /**
     * @see org.apache.jetspeed.security.spi.ldap.AbstractLdapTest#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        LdapDataHelper.removeGroupData(gpUid1);
        LdapDataHelper.removeGroupData(gpUid2);
        LdapDataHelper.removeUserData(uid1);
        LdapDataHelper.removeUserData(uid2);
    }

    /**
     * @throws Exception
     */
    public void testGetUserPrincipalsInGroup() throws Exception
    {
        secHandler.setUserPrincipalInGroup(uid1, gp1.getFullPath());
        secHandler.setUserPrincipalInGroup(uid2, gp1.getFullPath());

        String fullPathName = new GroupPrincipalImpl(gpUid1).getFullPath();
        logger.debug("Group full path name from testGetUserPrincipalsInGroup()[" + fullPathName + "]");
        assertEquals("The user should have been in two groups.", 2, secHandler.getUserPrincipalsInGroup(fullPathName)
                .size());
    }

    /**
     * @throws Exception
     */
    public void testSetUserPrincipalInGroup() throws Exception
    {
        secHandler.setUserPrincipalInGroup(uid1, gp1.getFullPath());
        secHandler.setUserPrincipalInGroup(uid1, gp2.getFullPath());

        assertEquals("The user should have been in two groups.", 2, secHandler.getGroupPrincipals(uid1).size());
    }

    /**
     * @throws Exception
     */
    public void testRemoveUserPrincipalInGroup() throws Exception
    {
        secHandler.setUserPrincipalInGroup(uid1, gp1.getFullPath());
        secHandler.setUserPrincipalInGroup(uid1, gp2.getFullPath());

        assertEquals("The user should have been in two groups.", 2, secHandler.getGroupPrincipals(uid1).size());

        secHandler.removeUserPrincipalInGroup(uid1, gp1.getFullPath());
        assertEquals("The user should have been in one groups.", 1, secHandler.getGroupPrincipals(uid1).size());

        secHandler.removeUserPrincipalInGroup(uid1, gp2.getFullPath());
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
}