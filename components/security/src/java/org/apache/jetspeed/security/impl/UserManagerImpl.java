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
import java.util.ArrayList;
import java.util.Collection;
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
import org.apache.jetspeed.security.AuthenticationProviderProxy;
import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;
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

    /** The authenticatino provider proxy. */
    private AuthenticationProviderProxy atnProviderProxy = null;

    /** The security mapping handler. */
    private SecurityMappingHandler securityMappingHandler = null;

    /**
     * @param securityProvider The security provider.
     */
    public UserManagerImpl(SecurityProvider securityProvider)
    {
        this.atnProviderProxy = securityProvider.getAuthenticationProviderProxy();
        this.securityMappingHandler = securityProvider.getSecurityMappingHandler();
    }

    /**
     * @param securityProvider The security provider.
     * @param roleHierarchyResolver The role hierachy resolver.
     * @param groupHierarchyResolver The group hierarchy resolver.
     */
    public UserManagerImpl(SecurityProvider securityProvider, HierarchyResolver roleHierarchyResolver,
            HierarchyResolver groupHierarchyResolver)
    {
        securityProvider.getSecurityMappingHandler().setRoleHierarchyResolver(roleHierarchyResolver);
        securityProvider.getSecurityMappingHandler().setGroupHierarchyResolver(groupHierarchyResolver);
        this.atnProviderProxy = securityProvider.getAuthenticationProviderProxy();
        this.securityMappingHandler = securityProvider.getSecurityMappingHandler();
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
        try
        {
            authenticated = atnProviderProxy.authenticate(username, password);
            if (authenticated && log.isDebugEnabled())
            {
                log.debug("Authenticated user: " + username);
            }
        }
        catch (SecurityException e)
        {
            // ignore: not authenticated
        }
        return authenticated;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#addUser(java.lang.String,
     *      java.lang.String)
     */
    public void addUser(String username, String password) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" },
                "addUser(java.lang.String, java.lang.String)");

        addUser(username, password, atnProviderProxy.getDefaultAuthenticationProvider());
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#addUser(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void addUser(String username, String password, String atnProviderName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username, atnProviderName }, new String[] { "username", "atnProviderName"},
                "addUser(java.lang.String, java.lang.String, java.lang.String)");

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
                atnProviderProxy.addUserPrincipal(userPrincipal);
                if ( password != null )
                {
                    // Set private password credential
                    atnProviderProxy.setPassword(username, null, password, atnProviderName);
                }
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
        atnProviderProxy.removeUserPrincipal(userPrincipal);
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

    /**
     * @see org.apache.jetspeed.security.UserManager#userExists(java.lang.String)
     */
    public boolean userExists(String username)
    {
        ArgUtil.notNull(new Object[] { username }, new String[] { "username" }, "userExists(java.lang.String)");

        Principal principal = atnProviderProxy.getUserPrincipal(username);
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

        Principal userPrincipal = atnProviderProxy.getUserPrincipal(username);
        if (null == userPrincipal)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + username);
        }

        principals.add(userPrincipal);
        principals.addAll(securityMappingHandler.getRolePrincipals(username));
        principals.addAll(securityMappingHandler.getGroupPrincipals(username));

        Subject subject = new Subject(true, principals, atnProviderProxy.getPublicCredentials(username),
                atnProviderProxy.getPrivateCredentials(username));
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
        Iterator userPrincipals = atnProviderProxy.getUserPrincipals(filter).iterator();
        while (userPrincipals.hasNext())
        {
            String username = ((Principal) userPrincipals.next()).getName();
            User user = getUser(username);
            users.add(user);
        }
        return users.iterator();
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUsersInRole(java.lang.String)
     */
    public Collection getUsersInRole(String roleFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { roleFullPathName }, new String[] { "roleFullPathName" },
                "getUsersInRole(java.lang.String)");

        Collection users = new ArrayList();

        Set userPrincipals = securityMappingHandler.getUserPrincipalsInRole(roleFullPathName);
        Iterator userPrincipalsIter = userPrincipals.iterator();
        while (userPrincipalsIter.hasNext())
        {
            Principal userPrincipal = (Principal) userPrincipalsIter.next();
            users.add(getUser(userPrincipal.getName()));
        }
        return users;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUsersInGroup(java.lang.String)
     */
    public Collection getUsersInGroup(String groupFullPathName) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { groupFullPathName }, new String[] { "groupFullPathName" },
                "getUsersInGroup(java.lang.String)");

        Collection users = new ArrayList();

        Set userPrincipals = securityMappingHandler.getUserPrincipalsInGroup(groupFullPathName);
        Iterator userPrincipalsIter = userPrincipals.iterator();
        while (userPrincipalsIter.hasNext())
        {
            Principal userPrincipal = (Principal) userPrincipalsIter.next();
            users.add(getUser(userPrincipal.getName()));
        }
        return users;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#setPassword(java.lang.String,
     *      java.lang.String, java.lang.String)
     * 
     * TODO Enforce that only administrators can do this.
     */
    public void setPassword(String username, String oldPassword, String newPassword) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { username, newPassword }, new String[] { "username", 
                "newPassword" }, "setPassword(java.lang.String, java.lang.String, java.lang.String)");

        atnProviderProxy.setPassword(username, oldPassword, newPassword);
    }

    
    /**
     * @see org.apache.jetspeed.security.UserManager#setPasswordEnabled(java.lang.String, boolean)
     */
    public void setPasswordEnabled(String userName, boolean enabled) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { userName,  }, new String[] { "userName" }, 
                "setPasswordEnabled(java.lang.String, boolean)");

        atnProviderProxy.setPasswordEnabled(userName, enabled);
    }
    /**
     * @see org.apache.jetspeed.security.UserManager#setPasswordUpdateRequired(java.lang.String, boolean)
     */
    public void setPasswordUpdateRequired(String userName, boolean updateRequired) throws SecurityException
    {
        ArgUtil.notNull(new Object[] { userName,  }, new String[] { "userName" }, 
            "setPasswordUpdateRequired(java.lang.String, boolean)");

        atnProviderProxy.setPasswordUpdateRequired(userName, updateRequired);
    }
}