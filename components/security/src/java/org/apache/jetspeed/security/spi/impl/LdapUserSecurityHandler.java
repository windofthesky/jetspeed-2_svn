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
package org.apache.jetspeed.security.spi.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.spi.UserSecurityHandler;
import org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserPrincipalDaoImpl;

/**
 * @see org.apache.jetspeed.security.spi.UserSecurityHandler
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 */
public class LdapUserSecurityHandler implements UserSecurityHandler
{
    /** The logger. */
    private static final Log LOG = LogFactory.getLog(LdapUserSecurityHandler.class);

    /** The {@link LdapPrincipalDao}. */
    private LdapPrincipalDao ldap;

    /**
     * @param ldap The {@link LdapUserSecurityDao}.
     */
    public LdapUserSecurityHandler(LdapPrincipalDao ldap)
    {
        this.ldap = ldap;
    }

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public LdapUserSecurityHandler() throws NamingException, SecurityException
    {
        this(new LdapUserPrincipalDaoImpl());
    }

    /**
     * <p>
     * Lookup the user by his UID attribute on the Ldap Server.
     * </p>
     * 
     * @return true if the Ldap Server finds a user with that UID; false if he
     *         is not found or some sort of NamingException occurred.
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#isUserPrincipal(java.lang.String)
     */
    public boolean isUserPrincipal(String uid)
    {
        verifyUid(uid);
        return getUserPrincipal(uid) != null;
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#getUserPrincipal(java.lang.String)
     */
    public Principal getUserPrincipal(String uid)
    {
        verifyUid(uid);
        try
        {
            String dn = ldap.lookupByUid(uid);

            if (!StringUtils.isEmpty(dn))
            {
                return new UserPrincipalImpl(uid);
            }
        }
        catch (SecurityException e)
        {
            logSecurityException(e, uid);
        }

        return null;
    }

    /**
     * <p>
     * Verify the uid.
     * </p>
     * 
     * @param uid The uid.
     */
    private void verifyUid(String uid)
    {
        if (StringUtils.isEmpty(uid))
        {
            throw new IllegalArgumentException("The uid cannot be null or empty.");
        }
    }

    /**
     * @param se SecurityException Throws a {@link SecurityException}.
     * @param uid The uid.
     */
    private void logSecurityException(SecurityException se, String uid)
    {
        if (LOG.isErrorEnabled())
        {
            LOG.error("An LDAP error has occurred for user uid:" + uid, se);
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#getUserPrincipals(java.lang.String)
     */
    public List getUserPrincipals(String filter)
    {
        try
        {
            return Arrays.asList(ldap.find(filter));
        }
        catch (SecurityException e)
        {
            logSecurityException(e, filter);
        }

        return new ArrayList();
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#addUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void addUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        verifyUserPrincipal(userPrincipal);

        String uid = userPrincipal.getName();
        if (isUserPrincipal(uid))
        {
            throw new SecurityException("The user:" + uid + " already exists.");
        }
        ldap.create(uid);
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#updateUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void updateUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        verifyUserPrincipal(userPrincipal);
        String uid = userPrincipal.getName();
        if (!isUserPrincipal(uid))
        {
            ldap.create(uid);
        }
    }

    /**
     * @param userPrincipal
     */
    private void verifyUserPrincipal(UserPrincipal userPrincipal)
    {
        if (userPrincipal == null)
        {
            throw new IllegalArgumentException("The UserPrincipal cannot be null or empty.");
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#removeUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void removeUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        verifyUserPrincipal(userPrincipal);

        String uid = userPrincipal.getName();

        ldap.delete(uid);
    }
}