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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserCredentialDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserCredentialDaoImpl;

/**
 * @see org.apache.jetspeed.security.spi.CredentialHandler
 *
 * @author <a href="mailto:mike.long@dataline.com">Mike Long</a>
 */
public class LdapCredentialHandler implements CredentialHandler
{
    /** The logger. */
    private static final Log LOG = LogFactory.getLog(LdapCredentialHandler.class);

    /** The {@link LdapUserCredentialDao}. */
    private LdapUserCredentialDao ldap;

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public LdapCredentialHandler()
    {
        this(new LdapUserCredentialDaoImpl());
    }

    public LdapCredentialHandler(LdapUserCredentialDao ldap)
    {
        this.ldap = ldap;
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPublicCredentials(java.lang.String)
     */
    public Set getPublicCredentials(String username)
    {
        return new HashSet();
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPrivateCredentials(java.lang.String)
     */
    public Set getPrivateCredentials(String uid)
    {
        Set privateCredentials = new HashSet();

        try
        {
            privateCredentials.add(new DefaultPasswordCredentialImpl(uid, ldap.getPassword(uid)));
        }
        catch (SecurityException e)
        {
            logSecurityException(e, uid);
        }

        return privateCredentials;
    }

    private void logSecurityException(SecurityException e, String uid)
    {
        if (LOG.isErrorEnabled())
        {
            LOG.error("Failure creating a PasswordCredential for InternalCredential uid:" + uid, e);
        }
    }

    /**
     * <p>
     * Adds or updates a private password credential. <br>
     * If <code>oldPassword</code> is not null, the oldPassword will first be
     * checked (authenticated). <br>
     * </p>
     * 
     * @param uid The LDAP uid attribute.
     * @param oldPassword The old {@link PasswordCredential}.
     * @param newPassword The new {@link PasswordCredential}.
     * @throws SecurityException when the lookup fails because the user does not
     *             exist or the non-null password is not correct. Throws a
     *             {@link SecurityException}.
     */
    public void setPassword(String uid, String oldPassword, String newPassword) throws SecurityException
    {
        validate(uid, newPassword);

        if (!StringUtils.isEmpty(oldPassword))
        {
            ldap.authenticate(uid, oldPassword);
        }

        ldap.changePassword(uid, newPassword);
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPasswordEnabled(java.lang.String,
     *      boolean)
     */
    public void setPasswordEnabled(String userName, boolean enabled) throws SecurityException
    {
        // TODO Implement this.
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPasswordUpdateRequired(java.lang.String,
     *      boolean)
     */
    public void setPasswordUpdateRequired(String userName, boolean updateRequired) throws SecurityException
    {
        // TODO Implement this.
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#authenticate(java.lang.String, java.lang.String)
     */
    public boolean authenticate(String uid, String password) throws SecurityException
    {
        validate(uid, password);

        return ldap.authenticate(uid, password);
    }

    /**
     * <p>
     * Validates the uid.
     * </p>
     * 
     * @param uid The uid.
     * @param password The password.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    private void validate(String uid, String password) throws SecurityException
    {
        if (StringUtils.isEmpty(password))
        {
            throw new SecurityException("The password cannot be null or empty.");
        }

        if (StringUtils.isEmpty(uid))
        {
            throw new SecurityException("The uid cannot be null or empty.");
        }
    }
}