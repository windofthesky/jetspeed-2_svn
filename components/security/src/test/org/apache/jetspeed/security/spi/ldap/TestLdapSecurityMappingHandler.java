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

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;
import org.apache.jetspeed.security.spi.impl.LdapSecurityMappingHandler;

/**
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 *  
 */
public class TestLdapSecurityMappingHandler extends AbstractLdapTest
{
    /** The logger. */
    private static final Log LOG = LogFactory.getLog(TestLdapSecurityMappingHandler.class);

    /** The {@link SecurityMappingHandler}. */
    private SecurityMappingHandler secHandler;

    /**
     * @throws NamingException A {@link NamingException}.
     * @throws SecurityException A {@link SecurityException}.
     */
    public TestLdapSecurityMappingHandler() throws SecurityException, NamingException
    {
        this.secHandler = new LdapSecurityMappingHandler();
    }

    public void testGetUserPrincipalsInGroup() throws SecurityException
    {
        secHandler.setUserPrincipalInGroup(uid1, gp1.getFullPath());
        secHandler.setUserPrincipalInGroup(uid2, gp1.getFullPath());

        String fullPathName = new GroupPrincipalImpl(gpUid1).getFullPath();
        LOG.debug("Group full path name from testGetUserPrincipalsInGroup()[" + fullPathName + "]");
        assertEquals("The user should have been in two groups.", 2, secHandler.getUserPrincipalsInGroup(fullPathName)
                .size());
    }

    public void testSetUserPrincipalInGroup() throws SecurityException
    {
        secHandler.setUserPrincipalInGroup(uid1, gp1.getFullPath());
        secHandler.setUserPrincipalInGroup(uid1, gp2.getFullPath());

        assertEquals("The user should have been in two groups.", 2, secHandler.getGroupPrincipals(uid1).size());
    }

    public void testRemoveUserPrincipalInGroup() throws SecurityException
    {
        secHandler.setUserPrincipalInGroup(uid1, gp1.getFullPath());
        secHandler.setUserPrincipalInGroup(uid1, gp2.getFullPath());

        assertEquals("The user should have been in two groups.", 2, secHandler.getGroupPrincipals(uid1).size());

        secHandler.removeUserPrincipalInGroup(uid1, gp1.getFullPath());
        assertEquals("The user should have been in one groups.", 1, secHandler.getGroupPrincipals(uid1).size());

        secHandler.removeUserPrincipalInGroup(uid1, gp2.getFullPath());
        assertEquals("The user should have been in two groups.", 0, secHandler.getGroupPrincipals(uid1).size());
    }

    public void testSetUserPrincipalInGroupForNonExistantUser() throws SecurityException
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

    public void testSetUserPrincipalInGroupForNonExistantGroup() throws SecurityException
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