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
package org.apache.jetspeed.security.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.om.JetspeedCredential;
import org.apache.jetspeed.security.om.JetspeedGroupPrincipal;
import org.apache.jetspeed.security.om.JetspeedRolePrincipal;
import org.apache.jetspeed.security.om.JetspeedUserPrincipal;
import org.apache.jetspeed.security.om.impl.JetspeedUserPrincipalImpl;
import org.apache.jetspeed.security.om.impl.JetspeedCredentialImpl;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>Implementation for managing users and provides access
 * to the {@link User}.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class UserManagerImpl extends BaseSecurityImpl implements UserManager
{
    private static final Log log = LogFactory.getLog(UserManagerImpl.class);

    /**
     * <p>Constructor providing access to the persistence component.</p>
     */
    public UserManagerImpl(PersistenceStoreContainer storeContainer, String keyStoreName)
    {
        super(storeContainer, keyStoreName);
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#authenticate(java.lang.String, java.lang.String)
     */
    public boolean authenticate(String username, String password)
    {
        ArgUtil.notNull(
            new Object[] { username, password },
            new String[] { "username", "password" },
            "authenticate(java.lang.String, java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        Collection credentials = omUser.getCredentials();
        // Create a new credential with the given password.
        short credentialType = 0;
        JetspeedCredential omCredential = new JetspeedCredentialImpl(omUser.getPrincipalId(), password, credentialType, null);
        if (log.isDebugEnabled())
            log.debug("Credential: " + omCredential.toString());
        boolean userMatch = ((null != omUser) && (credentials.contains(omCredential)));

        return userMatch;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#addUser(java.lang.String, java.lang.String)
     */
    public void addUser(String username, String password) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { username, password },
            new String[] { "username", "password" },
            "addUser(java.lang.String, java.lang.String)");

        UserPrincipal userPrincipal = new UserPrincipalImpl(username);
        String fullPath = userPrincipal.getFullPath();
        // Check if user already exists.
        if (userExists(username))
        {
            throw new SecurityException(SecurityException.USER_ALREADY_EXISTS + " " + userPrincipal.getName());
        }

        // If does not exist, create.
        JetspeedUserPrincipal omUser = new JetspeedUserPrincipalImpl(fullPath);
        Preferences preferences = Preferences.userRoot().node(fullPath);
        if (log.isDebugEnabled())
            log.debug("Added user preferences node: " + fullPath);
        PersistenceStore store = getPersistenceStore();
        try
        {
            if ((null != preferences) && preferences.absolutePath().equals(fullPath))
            {
                store.lockForWrite(omUser);
                store.getTransaction().checkpoint();
                // Add the password as a credential.
                short credentialType = 0;
                JetspeedCredential omCredential =
                    new JetspeedCredentialImpl(omUser.getPrincipalId(), password, credentialType, null);
                Collection credentials = new ArrayList();
                credentials.add(omCredential);
                omUser.setCredentials(credentials);
                store.getTransaction().checkpoint();
                if (log.isDebugEnabled())
                    log.debug("Added user: " + omUser.getFullPath());
            }
        }
        catch (Exception e)
        {
            String msg = "Unable to lock User for update.";
            log.error(msg, e);
            store.getTransaction().rollback();
            try
            {
                preferences.removeNode();
            }
            catch (BackingStoreException bse)
            {
                bse.printStackTrace();
            }
            throw new SecurityException(msg, e);
        }
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#removeUser(java.lang.String)
     * TODO Enforce that only administrators can do this.
     */
    public void removeUser(String username) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "removeUser(java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        // TODO This should be managed in a transaction.
        if (null != omUser)
        {
            PersistenceStore store = getPersistenceStore();
            try
            {
                // Remove user.
                store.deletePersistent(omUser);
                store.getTransaction().checkpoint();
                if (log.isDebugEnabled())
                    log.debug("Deleted user: " + omUser.getFullPath());

            }
            catch (Exception e)
            {
                String msg = "Unable to lock User for update.";
                log.error(msg, e);
                store.getTransaction().rollback();
                throw new SecurityException(msg, e);
            }
            if (!userExists(username))
            {
                // Remove preferences
                Preferences preferences = Preferences.userRoot().node(omUser.getFullPath());
                try
                {
                    preferences.removeNode();
                }
                catch (BackingStoreException bse)
                {
                    bse.printStackTrace();
                }
            }
            else
            {
                String msg = "Could not remove user.";
                log.error(msg);
                throw new SecurityException(msg);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#userExists(java.lang.String)
     */
    public boolean userExists(String username)
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "userExists(java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        boolean userExists = (null != omUser);
        if (log.isDebugEnabled())
            log.debug("User exists: " + userExists);
        if (log.isDebugEnabled() && (null != omUser))
            log.debug("User: [[id, " + omUser.getPrincipalId() + "], [fullPath, " + omUser.getFullPath() + "]]");
        return userExists;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUser(java.lang.String)
     */
    public User getUser(String username) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "getUserProfile(java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        if (null == omUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }
        return super.getUser(omUser);
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUsers(java.lang.String)
     */
    public Iterator getUsers(String filter)
    {
        // TODO Not Implemented
        return null;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#setPassword(java.lang.String, java.lang.String)
     * TODO Enforce that only administrators can do this.
     * TODO Should we define any constraint on password and throw a security exception if invalid.
     */
    public void setPassword(String username, String password) throws SecurityException
    {
        ArgUtil.notNull(
            new Object[] { username, password },
            new String[] { "username", "password" },
            "setPassword(java.lang.String, java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username);
        if (null == omUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }
        // TODO Needs to be changed for multiple credentials support.
        Collection credentials = new ArrayList();
        // TODO For now, we do not have custom credentials classes.  All credentials are passwords.
        // TODO We may want to change this in the future.
        // Create a new credential with the given password.
        short credentialType = 0;
        JetspeedCredential omCredential = new JetspeedCredentialImpl(omUser.getPrincipalId(), password, credentialType, null);
        credentials.add(omCredential);
        PersistenceStore store = getPersistenceStore();
        try
        {
            store.lockForWrite(omUser);
            omUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
            omUser.setCredentials(credentials);
            store.getTransaction().checkpoint();
        }
        catch (Exception e)
        {
            String msg = "Unable to lock User for update.";
            log.error(msg, e);
            store.getTransaction().rollback();
            throw new SecurityException(msg, e);
        }
    }

}
