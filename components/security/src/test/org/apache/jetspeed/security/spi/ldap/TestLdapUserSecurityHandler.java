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

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;

import java.security.Principal;

import java.util.List;

/**
 * <p>
 * LdapServerTest - This class tests the LdapServer. It assumes that the following three
 * inetOrgPerson objects exist: uid:cbrewton password:maddie uid:dlong, password: uid:mlong,
 * password:maddie
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 */
public class TestLdapUserSecurityHandler extends AbstractLdapTest
{
    /**
     * @see org.apache.jetspeed.security.spi.ldap.AbstractLdapTest#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        LdapDataHelper.seedUserData(uid1, password);
    }

    /**
     * @see org.apache.jetspeed.security.spi.ldap.AbstractLdapTest#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        LdapDataHelper.removeUserData(uid1);
    }

    /**
     * @throws Exception
     */
    public void testUserIsPrincipal() throws Exception
    {
        assertTrue("User is not principal.", userHandler.isUserPrincipal(uid1));
    }

    /**
     * @throws Exception
     */
    public void testUserIsNotPrincipal() throws Exception
    {
        assertFalse("User is principal and should not be.", userHandler.isUserPrincipal(Integer
                .toString(rand.nextInt()).toString()));
    }

    /**
     * @throws Exception
     */
    public void testAddDuplicateUserPrincipal() throws Exception
    {
        try
        {
            userHandler.addUserPrincipal(new UserPrincipalImpl(uid1));
            fail("Adding an already existant user should have thrown a SecurityException.");
        }
        catch (Exception e)
        {
            assertTrue("Adding an already existant user should have thrown a SecurityException.",
                    e instanceof SecurityException);
        }
    }

    /**
     * @throws Exception
     */
    public void testAddUserPrincipal() throws Exception
    {
        assertTrue("User not found.", userHandler.getUserPrincipal(uid1) != null);
    }

    /**
     * @throws Exception
     */
    public void testRemoveExistantUserPrincipal() throws Exception
    {
        UserPrincipal up = new UserPrincipalImpl(uid1);
        userHandler.removeUserPrincipal(up);
        assertTrue("User was found and should have been removed.", userHandler.getUserPrincipal(uid1) == null);
    }

    /**
     * @throws Exception
     */
    public void testRemoveNonExistantUserPrincipal() throws Exception
    {
        String localUid = Integer.toString(rand.nextInt()).toString();
        UserPrincipal localPrin = new UserPrincipalImpl(localUid);

        userHandler.removeUserPrincipal(localPrin);
    }

    /**
     * @throws Exception
     */
    public void testGetUserPrincipals() throws Exception
    {
        try
        {
            LdapDataHelper.seedUserData(uid2, password);
            // With wild card search
            assertTrue("getUserPrincipals should have returned more than one user.", userHandler.getUserPrincipals("*")
                    .size() > 1);
            
            // With empty string search
            assertTrue("getUserPrincipals should have returned more than one user.", userHandler.getUserPrincipals("")
                    .size() > 1);

            // With specific uid.
            List users = userHandler.getUserPrincipals(uid1);

            assertTrue("getUserPrincipals should have returned one user.", users.size() == 1);
            assertTrue("List should have consisted of Principal objects.", users.get(0) instanceof Principal);

            String localUid = Integer.toString(rand.nextInt()).toString();

            assertTrue("getUserPrincipals should not have found any users with the specified filter.", userHandler
                    .getUserPrincipals(localUid).isEmpty());
        }
        finally
        {
            LdapDataHelper.removeUserData(uid2);
        }
    }
}