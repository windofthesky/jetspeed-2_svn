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

import java.security.Principal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.UserSecurityHandler;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>
 * Implementation for managing users and provides access to the {@link User}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @version $Id$
 */
public class UserManagerImpl implements UserManager
{
    private static final Log log = LogFactory.getLog(UserManagerImpl.class);

    /** The user security handler. */
    private UserSecurityHandler userSecurityHandler = null;
    
    /** The role security handler. */
    private RoleSecurityHandler roleSecurityHandler = null;
    
    /** The group security handler. */
    private GroupSecurityHandler groupSecurityHandler = null;

    /** The credential handler. */
    private CredentialHandler credentialHandler = null;

    /**
     * @param securityProvider The security provider.
     */
    public UserManagerImpl(SecurityProvider securityProvider)
    {
        this.userSecurityHandler = securityProvider.getUserSecurityHandler();
        this.roleSecurityHandler = securityProvider.getRoleSecurityHandler();
        this.groupSecurityHandler = securityProvider.getGroupSecurityHandler();
        this.credentialHandler = securityProvider.getCredentialHandler();
    }

    /**
     * @param securityProvider The security provider.
     * @param roleHierarchyResolver The role hierachy resolver.
     * @param groupHierarchyResolver The group hierarchy resolver.
     */
    public UserManagerImpl(SecurityProvider securityProvider,
            HierarchyResolver roleHierarchyResolver, HierarchyResolver groupHierarchyResolver)
    {
        securityProvider.getRoleSecurityHandler().setRoleHierarchyResolver(roleHierarchyResolver);
        securityProvider.getGroupSecurityHandler().setGroupHierarchyResolver(groupHierarchyResolver);
        this.userSecurityHandler = securityProvider.getUserSecurityHandler();
        this.roleSecurityHandler = securityProvider.getRoleSecurityHandler();
        this.groupSecurityHandler = securityProvider.getGroupSecurityHandler();
        this.credentialHandler = securityProvider.getCredentialHandler();
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#authenticate(java.lang.String,
     *      java.lang.String)
     */
    public boolean authenticate(String username, String password)
    {
        ArgUtil.notNull(new Object[] { username, password }, new String[] { "username", "password" },
                "authenticate(java.lang.String, java.lang.String)");

        boolean authenticated = false;
        Set privateCredentials = this.credentialHandler.getPrivateCredentials(username);

        Iterator privateCredIter = privateCredentials.iterator();
        PasswordCredential authPwdCred = new PasswordCredential(username, password.toCharArray());
        while (privateCredIter.hasNext())
        {
            Object currPrivateCred = privateCredIter.next();
            if (currPrivateCred instanceof PasswordCredential)
            {
                PasswordCredential currPwdCred = (PasswordCredential) currPrivateCred;
                if (currPrivateCred.equals(authPwdCred))
                {
                    if (log.isDebugEnabled())
                    {
                        log.debug("Authenticated user: " + username);
                    }
                    authenticated = true;
                    break;
                }
            }
        }
        return authenticated;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#addUser(java.lang.String,
     *      java.lang.String)
     */
    public void addUser(String username, String password) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username, password }, new String[] { "username", "password" },
                "addUser(java.lang.String, java.lang.String)");

        // Check if user already exists.
        if (userExists(username))
        {
            throw new SecurityException(SecurityException.USER_ALREADY_EXISTS + " " + username);
        }

        UserPrincipal userPrincipal = new UserPrincipalImpl(username);
        String fullPath = userPrincipal.getFullPath();
        // Add the preferences.
        Preferences preferences = Preferences.userRoot().node(fullPath);
        if (log.isDebugEnabled())
        {
            log.debug("Added user preferences node: " + fullPath);
        }
        try
        {
            if ((null != preferences) && preferences.absolutePath().equals(fullPath))
            {
                // Add user principal.
                userSecurityHandler.setUserPrincipal(userPrincipal);
                // Set security credentials
                PasswordCredential pwdCredential = new PasswordCredential(username, password.toCharArray());
                credentialHandler.setPrivatePasswordCredential(null, pwdCredential);
                if (log.isDebugEnabled())
                {
                    log.debug("Added user: " + fullPath);
                }
            }
        }
        catch (SecurityException se)
        {
            String msg = "Unable to create the user.";
            log.error(msg, se);
            // Make sure the user principal is removed.

            // Remove the preferences node.
            try
            {
                preferences.removeNode();
            }
            catch (BackingStoreException bse)
            {
                bse.printStackTrace();
            }
            throw new SecurityException(msg, se);
        }
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#removeUser(java.lang.String)
     * 
     * TODO Enforce that only administrators can do this.
     */
    public void removeUser(String username) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "removeUser(java.lang.String)");

        UserPrincipal userPrincipal = new UserPrincipalImpl(username);
        String fullPath = userPrincipal.getFullPath();
        userSecurityHandler.removeUserPrincipal(userPrincipal);
        if (!userExists(username))
        {
            // Remove preferences
            Preferences preferences = Preferences.userRoot().node(fullPath);
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

    /**
     * @see org.apache.jetspeed.security.UserManager#userExists(java.lang.String)
     */
    public boolean userExists(String username)
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "userExists(java.lang.String)");

        Principal principal = userSecurityHandler.getUserPrincipal(username);
        boolean userExists = (null != principal);
        if (log.isDebugEnabled())
        {
            log.debug("User exists: " + userExists);
            log.debug("User: " + username);
        }
        return userExists;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUser(java.lang.String)
     */
    public User getUser(String username) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "getUser(java.lang.String)");

        Set principals = new HashSet();
        String fullPath = (new UserPrincipalImpl(username)).getFullPath();
        
        Principal userPrincipal = userSecurityHandler.getUserPrincipal(username);
        if (null == userPrincipal)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }
        
        principals.add(userPrincipal);
        principals.addAll(roleSecurityHandler.getRolePrincipals(username));
        principals.addAll(groupSecurityHandler.getGroupPrincipals(username));
        
        Subject subject = new Subject(true, principals, credentialHandler.getPublicCredentials(username), credentialHandler.getPrivateCredentials(username));
        Preferences preferences = Preferences.userRoot().node(fullPath);
        User user = new UserImpl(subject, preferences);
        
        return user;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUsers(java.lang.String)
     */
    public Iterator getUsers(String filter) throws SecurityException
    {
        List users = new LinkedList();
        Iterator userPrincipals = userSecurityHandler.getUserPrincipals(filter);
        while (userPrincipals.hasNext())
        {
            String username = ((Principal) userPrincipals.next()).getName();
            User user = getUser(username);
            users.add(user);
        }
        return users.iterator();
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#setPassword(java.lang.String,
     *      java.lang.String, java.lang.String)
     * 
     * TODO Enforce that only administrators can do this.
     */
    public void setPassword(String username, String oldPassword, String newPassword) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username, oldPassword, newPassword }, new String[] { "username", "oldPassword",
                "newPassword" }, "setPassword(java.lang.String, java.lang.String, java.lang.String)");

        PasswordCredential oldPwdCredential = new PasswordCredential(username, oldPassword.toCharArray());
        PasswordCredential newPwdCredential = new PasswordCredential(username, newPassword.toCharArray());

        credentialHandler.setPrivatePasswordCredential(oldPwdCredential, newPwdCredential);
    }

}