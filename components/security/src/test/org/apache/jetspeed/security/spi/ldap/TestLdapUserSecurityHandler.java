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
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;

import java.security.Principal;

import java.util.List;

/**
 * <p>
 * LdapServerTest - This class tests the LdapServer. It assumes that the
 * following three inetOrgPerson objects exist:
 * 
 * uid:cbrewton password:maddie uid:dlong, password: uid:mlong, password:maddie
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 */
public class TestLdapUserSecurityHandler extends AbstractLdapTest
{
    /** The logger. */
    private static final Log log = LogFactory.getLog(TestLdapUserSecurityHandler.class);

    public void testUserIsPrincipal()
    {
        assertTrue("User is not principal.", userHandler.isUserPrincipal(uid1));
    }

    public void testUserIsNotPrincipal()
    {
        assertFalse("User is principal and should not be.", userHandler.isUserPrincipal(Integer
                .toString(rand.nextInt()).toString()));
    }

    public void testAddDuplicateUserPrincipal()
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

    public void testAddUserPrincipal() throws SecurityException
    {
        assertTrue("User not found.", userHandler.getUserPrincipal(uid1) != null);
    }

    public void testRemoveExistantUserPrincipal() throws SecurityException
    {
        userHandler.removeUserPrincipal(up1);
        assertTrue("User was found and should have been removed.", userHandler.getUserPrincipal(uid1) == null);
    }

    public void testRemoveNonExistantUserPrincipal() throws SecurityException
    {
        String localUid = Integer.toString(rand.nextInt()).toString();
        UserPrincipal localPrin = new UserPrincipalImpl(localUid);

        userHandler.removeUserPrincipal(localPrin);
    }

    public void testGetUserPrincipals() throws SecurityException
    {
        assertTrue("getUserPrincipals should have returned more than one user.", userHandler.getUserPrincipals("*")
                .size() > 1);

        List users = userHandler.getUserPrincipals(uid1);

        assertTrue("getUserPrincipals should have returned one user.", users.size() == 1);
        assertTrue("List should have consisted of Principal objects.", users.get(0) instanceof Principal);

        String localUid = Integer.toString(rand.nextInt()).toString();

        assertTrue("getUserPrincipals should not have found any users with the specified filter.", userHandler
                .getUserPrincipals(localUid).isEmpty());
    }
}