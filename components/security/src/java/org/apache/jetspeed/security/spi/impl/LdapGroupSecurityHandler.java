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
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapGroupDaoImpl;

/**
 * @see org.apache.jetspeed.security.spi.GroupSecurityHandler
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a><br/>
 *         <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class LdapGroupSecurityHandler implements GroupSecurityHandler
{
    /** The logger. */
    private static final Log LOG = LogFactory.getLog(LdapGroupSecurityHandler.class);

    /** The {@link LdapPrincipalDao}. */
    private LdapPrincipalDao ldap;

    /**
     * @param ldap The {@link LdapPrincipalDao}.
     */
    public LdapGroupSecurityHandler(LdapPrincipalDao ldap)
    {
        this.ldap = ldap;
    }

    /**
     * <p>
     * Default constructor.
     * </p>
     * 
     * @throws NamingException A {@link NamingException}.
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapGroupSecurityHandler() throws NamingException, SecurityException
    {
        this(new LdapGroupDaoImpl());
    }

    /**
     * @see org.apache.jetspeed.security.spi.GroupSecurityHandler#getGroupPrincipal(java.lang.String)
     */
    public Principal getGroupPrincipal(String groupPrincipalUid)
    {
        String groupUidWithoutSlashes = convertUidToAcceptableName(groupPrincipalUid);
        verifyGroupId(groupUidWithoutSlashes);
        try
        {
            String dn = ldap.lookupByUid(groupUidWithoutSlashes);

            if (!StringUtils.isEmpty(dn))
            {
                return new GroupPrincipalImpl(groupPrincipalUid);
            }
        }
        catch (SecurityException e)
        {
            logSecurityException(e, groupPrincipalUid);
        }
        return null;
    }

    /**
     * <p>
     * Verify that the group uid is valid.
     * </p>
     * 
     * @param groupPrincipalUid The group uid.
     */
    private void verifyGroupId(String groupPrincipalUid)
    {
        if (StringUtils.isEmpty(groupPrincipalUid))
        {
            throw new IllegalArgumentException("The groupId cannot be null or empty.");
        }
    }

    /**
     * <p>
     * Log the security exception.
     * </p>
     * 
     * @param e The {@link SecurityException}.
     * @param groupPrincipalUid The group principal uid.
     */
    private void logSecurityException(SecurityException e, String groupPrincipalUid)
    {
        if (LOG.isErrorEnabled())
        {
            LOG.error("An LDAP error has occurred for groupId:" + groupPrincipalUid, e);
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.GroupSecurityHandler#setGroupPrincipal(org.apache.jetspeed.security.GroupPrincipal)
     */
    public void setGroupPrincipal(GroupPrincipal groupPrincipal) throws SecurityException
    {
        System.out.println("Group Principal UID:" + groupPrincipal.getFullPath());
        verifyGroupPrincipal(groupPrincipal);

        String uid = groupPrincipal.getFullPath();
        String groupUidWithoutSlashes = convertUidToAcceptableName(uid);
        LOG.debug("Group Principal UID:" + groupUidWithoutSlashes);

        if (getGroupPrincipal(groupUidWithoutSlashes) == null)
        {
            LOG.debug("Group Principal UID:" + groupUidWithoutSlashes);
            ldap.create(groupUidWithoutSlashes);
        }

    }

    /**
     * <p>
     * Converts the uid to an acceptable ldap name.
     * </p>
     * 
     * @param uid The uid.
     * @return The acceptable name.
     */
    private String convertUidToAcceptableName(String uid)
    {
        return uid.replaceAll("/", "&");
    }

    /**
     * <p>
     * Verify that the group principal is valid.
     * </p>
     * 
     * @param groupPrincipal The group principal.
     */
    private void verifyGroupPrincipal(GroupPrincipal groupPrincipal)
    {
        if (groupPrincipal == null)
        {
            throw new IllegalArgumentException("The GroupPrincipal cannot be null or empty.");
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.GroupSecurityHandler#removeGroupPrincipal(org.apache.jetspeed.security.GroupPrincipal)
     */
    public void removeGroupPrincipal(GroupPrincipal groupPrincipal) throws SecurityException
    {
        verifyGroupPrincipal(groupPrincipal);

        String uid = groupPrincipal.getFullPath();
        String groupUidWithoutSlashes = convertUidToAcceptableName(uid);

        ldap.delete(groupUidWithoutSlashes);
    }

    /**
     * @see org.apache.jetspeed.security.spi.GroupSecurityHandler#getGroupPrincipals(java.lang.String)
     */
    public List getGroupPrincipals(String filter)
    {
        try
        {
            String filterWithoutSlashes = convertUidToAcceptableName(filter);
            return Arrays.asList(ldap.find(filterWithoutSlashes));
        }
        catch (SecurityException e)
        {
            logSecurityException(e, filter);
        }
        return new ArrayList();
    }
}