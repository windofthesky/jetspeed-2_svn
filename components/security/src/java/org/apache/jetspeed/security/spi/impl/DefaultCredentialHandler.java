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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalCredentialImpl;
import org.apache.jetspeed.security.spi.CredentialHandler;

/**
 * @see org.apache.jetspeed.security.spi.CredentialHandler
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class DefaultCredentialHandler implements CredentialHandler
{
    /** Private credentials. */
    private static final int PRIVATE = 0;

    /** Public credentials. */
    private static final int PUBLIC = 1;

    /** Common queries. */
    private CommonQueries commonQueries = null;

    /**
     * <p>
     * Constructor providing access to the common queries.
     * </p>
     */
    public DefaultCredentialHandler(CommonQueries commonQueries)
    {
        this.commonQueries = commonQueries;
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPrivateCredentials(java.lang.String)
     */
    public Set getPrivateCredentials(String username)
    {
        return getCredentials(username, PRIVATE);
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPrivatePasswordCredential(org.apache.jetspeed.security.PasswordCredential,
     *      org.apache.jetspeed.security.PasswordCredential)
     */
    public void setPrivatePasswordCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential)
            throws SecurityException
    {
        setCredential(oldPwdCredential, newPwdCredential, PRIVATE);

    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPublicCredentials(java.lang.String)
     */
    public Set getPublicCredentials(String username)
    {
        return getCredentials(username, PUBLIC);
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPublicPasswordCredential(org.apache.jetspeed.security.PasswordCredential,
     *      org.apache.jetspeed.security.PasswordCredential)
     */
    public void setPublicPasswordCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential)
            throws SecurityException
    {
        setCredential(oldPwdCredential, newPwdCredential, PUBLIC);
    }

    /**
     * <p>
     * Gets the credentials given a type.
     * </p>
     * 
     * @param username The username.
     * @param type The type.
     * @return The set.
     */
    private Set getCredentials(String username, int type)
    {
        Set internalCredentials = new HashSet();
        Set credentials = new HashSet();
        InternalUserPrincipal internalUser = commonQueries.getInternalUserPrincipal(username);
        if (null != internalUser)
        {
            internalCredentials.addAll(internalUser.getCredentials());
            Iterator iter = internalCredentials.iterator();
            while (iter.hasNext())
            {
                InternalCredential credential = (InternalCredential) iter.next();
                if (credential.getType() == type)
                {
                    // PasswordCredential support.
                    //Commenting out to fix LoginPortlet
                    //DLS:  please verify this logic...  It's failing currently.
                    //if ((null != credential.getClassname())
                      //      && (credential.getClassname().equals((PasswordCredential.class).getName())))
                    {
                        PasswordCredential pwdCred = new PasswordCredential(username, credential.getValue()
                                .toCharArray());
                        credentials.add(pwdCred);
                    }
                }
            }
        }

        return credentials;
    }

    /**
     * <p>
     * Sets the password credential given a type.
     * </p>
     * 
     * @param oldPwdCredential The old {@link PasswordCredential}.
     * @param newPwdCredential The new {@link PasswordCredential}.
     * @param type The type.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    private void setCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential, int type)
            throws SecurityException
    {
        InternalUserPrincipal internalUser = commonQueries.getInternalUserPrincipal(newPwdCredential.getUserName());
        if (null == internalUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + newPwdCredential.getUserName());
        }
        Collection credentials = internalUser.getCredentials();
        if (null == credentials)
        {
            credentials = new ArrayList();
        }
        if (null != oldPwdCredential)
        {
            InternalCredential oldInternalCredential = new InternalCredentialImpl(internalUser.getPrincipalId(),
                    new String(oldPwdCredential.getPassword()), type, oldPwdCredential.getClass().getName());
            if (credentials.contains(oldInternalCredential))
            {
                credentials.remove(oldInternalCredential);
            }
        }
        InternalCredential newInternalCredential = new InternalCredentialImpl(internalUser.getPrincipalId(),
                new String(newPwdCredential.getPassword()), type, newPwdCredential.getClass().getName());
        credentials.add(newInternalCredential);
        internalUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
        internalUser.setCredentials(credentials);
        // Set the user with the new credentials.
        commonQueries.setInternalUserPrincipal(internalUser);
    }
}