/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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

import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.persistence.TransactionStateException;
import org.apache.jetspeed.security.PermissionManagerService;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManagerService;
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
 * @author <a href="mailto:david@sensova.com">David Le Strat</a>
 */
public class UserManagerServiceImpl extends BaseSecurityServiceImpl implements UserManagerService
{
    /** <p>The persistence plugin.</p> */
    private PersistencePlugin plugin;

    /** <p>The persistence plugin.</p> */
    private PermissionManagerService pms;

    /**
     * <p>Default constructor.</p>
     */
    public UserManagerServiceImpl()
    {
    }

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws CPSInitializationException
    {
        if (!isInitialized())
        {
            // Get persistence plugin
            PersistenceService ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
            String pluginName = getConfiguration().getString("persistence.plugin.name", "jetspeed");
            plugin = ps.getPersistencePlugin(pluginName);
            // Initialize the parent plugin.
            super.plugin = plugin;            
            // Get persistence manager service
            pms = (PermissionManagerService) CommonPortletServices.getPortalService(PermissionManagerService.SERVICE_NAME);
            setInit(true);
        }
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#authenticate(java.lang.String, java.lang.String)
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
        boolean userMatch = ((null != omUser) && (credentials.contains(omCredential)));

        return userMatch;
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#addUser(java.lang.String, java.lang.String)
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
        try
        {
            if ((null != preferences) && preferences.absolutePath().equals(fullPath))
            {
                plugin.beginTransaction();
                plugin.prepareForUpdate(omUser);
                // Add the password as a credential.
                short credentialType = 0;
                JetspeedCredential omCredential =
                    new JetspeedCredentialImpl(omUser.getPrincipalId(), password, credentialType, null);
                Collection credentials = new ArrayList();
                credentials.add(omCredential);
                omUser.setCredentials(credentials);
                plugin.commitTransaction();
            }
        }
        catch (TransactionStateException e)
        {
            try
            {
                plugin.rollbackTransaction();
                try
                {
                    preferences.removeNode();
                }
                catch (BackingStoreException bse)
                {
                    bse.printStackTrace();
                }
            }
            catch (TransactionStateException e1)
            {
                log.error("Failed to rollback transaction.", e);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#removeUser(java.lang.String)
     * TODO Enforce that only administrators can do this.
     */
    public void removeUser(String username)
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "removeUser(java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username); 
        // TODO This should be managed in a transaction.
        if (null != omUser)
        {
            Collection omRoles = omUser.getRolePrincipals();
            if (null != omRoles)
            {
                omRoles.clear();
            }
            Collection omGroups = omUser.getGroupPrincipals();
            if (null != omGroups)
            {
                omGroups.clear();
            }
            Collection omCredentials = omUser.getCredentials();
            if (null != omCredentials)
            {
                omCredentials.clear();
            }
            Collection omPermissions = omUser.getPermissions();
            if (null != omPermissions)
            {
                omPermissions.clear();
            }
            try
            {
                // Remove dependencies.
                plugin.beginTransaction();
                plugin.prepareForUpdate(omUser);
                omUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                omUser.setRolePrincipals(omRoles);
                omUser.setGroupPrincipals(omGroups);
                omUser.setCredentials(omCredentials);
                omUser.setPermissions(omPermissions);
                plugin.commitTransaction();

                // Remove user.
                plugin.beginTransaction();
                plugin.prepareForDelete(omUser);
                plugin.commitTransaction();
            }
            catch (TransactionStateException e)
            {
                try
                {
                    plugin.rollbackTransaction();
                }
                catch (TransactionStateException e1)
                {
                    log.error("Failed to rollback transaction.", e);
                }
            }
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
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#userExists(java.lang.String)
     */
    public boolean userExists(String username)
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "userExists(java.lang.String)");

        JetspeedUserPrincipal omUser = super.getJetspeedUserPrincipal(username); 
        boolean userExists = (null != omUser);
        return userExists;
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#getUser(java.lang.String)
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
     * @see org.apache.jetspeed.security.UserManagerService#getUsers(java.lang.String)
     */
    public Iterator getUsers(String filter)
    {
        // TODO Not Implemented
        return null;
    }

    /**
     * @see org.apache.jetspeed.security.UserManagerService#setPassword(java.lang.String, java.lang.String)
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
        try
        {
            plugin.beginTransaction();
            plugin.prepareForUpdate(omUser);
            omUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
            omUser.setCredentials(credentials);
            plugin.commitTransaction();
        }
        catch (TransactionStateException e)
        {
            try
            {
                plugin.rollbackTransaction();
            }
            catch (TransactionStateException e1)
            {
                log.error("Failed to rollback transaction.", e);
            }
        }
    }

}
