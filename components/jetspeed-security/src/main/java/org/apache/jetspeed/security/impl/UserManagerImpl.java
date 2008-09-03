/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.AuthenticationProviderProxy;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.HierarchyResolver;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.PrincipalNotFoundException;
import org.apache.jetspeed.security.PrincipalUpdateException;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityProvider;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.attributes.SecurityAttributes;
import org.apache.jetspeed.security.attributes.SecurityAttributesProvider;
import org.apache.jetspeed.security.spi.SecurityMappingHandler;

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
    
    /** The Jetspeed user principal manager */
    private JetspeedPrincipalManager userPrincipalManager;
    
    /** The authentication provider proxy. */
    private AuthenticationProviderProxy atnProviderProxy = null;
    /** The security mapping handler. */
    private SecurityMappingHandler securityMappingHandler = null;    
    /** Security Attributes persistence */
    private SecurityAttributesProvider attributesProvider;    
    private String anonymousUser = "guest";
    private User guest = null;
    
    /** 
     * Flag whether the principals's user group matches the user group to which the role has been mapped. (See SRV.12.4) 
     * If this flag is set to true, roles can be inherited to users via groups.
     */
    private boolean rolesInheritableViaGroups = true;
    
    /**
     * @param securityProvider
     *            The security provider.
     */
    public UserManagerImpl(SecurityProvider securityProvider, SecurityAttributesProvider attributesProvider)
    {
        this.atnProviderProxy = securityProvider.getAuthenticationProviderProxy();
        this.securityMappingHandler = securityProvider.getSecurityMappingHandler();
        this.attributesProvider = attributesProvider;
    }

    /**
     * @param securityProvider
     *            The security provider.
     * @param anonymousUser
     *            The anonymous user name
     */
    public UserManagerImpl(SecurityProvider securityProvider, SecurityAttributesProvider attributesProvider, String anonymousUser)
    {
        this(securityProvider, attributesProvider);
        this.anonymousUser = anonymousUser;
    }

    /**
     * @param securityProvider
     *            The security provider.
     * @param hierarchyResolver
     *            The hierarchy resolver.
     */    
    public UserManagerImpl(SecurityProvider securityProvider, SecurityAttributesProvider attributesProvider, 
            HierarchyResolver hierarchyResolver)
    {
        this(securityProvider, attributesProvider);
        securityProvider.getSecurityMappingHandler().setHierarchyResolver(hierarchyResolver);
    }

    /**
     * @param securityProvider
     *            The security provider.
     * @param hierarchyResolver
     *            The hierarchy resolver.
     * @param anonymousUser
     *            The anonymous user name
     */
    public UserManagerImpl(SecurityProvider securityProvider, SecurityAttributesProvider attributesProvider,
            HierarchyResolver hierarchyResolver, String anonymousUser)
    {
        this(securityProvider, attributesProvider, anonymousUser);
        securityProvider.getSecurityMappingHandler().setHierarchyResolver(hierarchyResolver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.security.UserManager#getAnonymousUser()
     */
    public String getAnonymousUser()
    {
        return this.anonymousUser;
    }
    
    public void setRolesInheritableViaGroups(boolean rolesInheritableViaGroups)
    {
        this.rolesInheritableViaGroups = rolesInheritableViaGroups;
    }
    
    /**
     * @see org.apache.jetspeed.security.UserManager#authenticate(java.lang.String,
     *      java.lang.String)
     */
    public boolean authenticate(String username, String password)
    {
        boolean authenticated = false;
        try
        {
            if (!getAnonymousUser().equals(username))
            {
                authenticated = atnProviderProxy.authenticate(username, password);
                if (authenticated && log.isDebugEnabled())
                {
                    log.debug("Authenticated user: " + username);
                }
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
    public void addUser(String username, String password)
            throws SecurityException
    {
        createUser(username, password, atnProviderProxy
                .getDefaultAuthenticationProvider(),false);
    }

    

    /**
     * @see org.apache.jetspeed.security.UserManager#addUser(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void addUser(String username, String password, String atnProviderName)
            throws SecurityException
    {
        createUser(username, password, atnProviderName, false);
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#importUser(java.lang.String,
     *      java.lang.String, boolean)
     */
    public void importUser(String username, String password, boolean passThrough)
            throws SecurityException
    {
        createUser(username, password, atnProviderProxy
                .getDefaultAuthenticationProvider(),passThrough);
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#importUser(java.lang.String,
     *      java.lang.String, java.lang.String, boolean)
     */
    public void importUser(String username, String password, String atnProviderName, boolean passThrough)
            throws SecurityException
    {
        createUser(username, password, atnProviderName, passThrough);
    }
    /**
     * @see org.apache.jetspeed.security.UserManager#addUser(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    protected void createUser(String userName, String password, String atnProviderName, boolean raw)
            throws SecurityException
    {
        if (userExists(userName)) 
        { 
            throw new SecurityException(SecurityException.USER_ALREADY_EXISTS.create(userName));
        }
        UserPrincipal userPrincipal = new UserPrincipalImpl(userName);        
        atnProviderProxy.addUserPrincipal(userPrincipal);
        if (password != null)
        {
            if (raw)
                atnProviderProxy.importPassword(userName, password, atnProviderName);
            else
                atnProviderProxy.setPassword(userName, null, password, atnProviderName);
        }        
        SecurityAttributes sa = attributesProvider.createSecurityAttributes(userPrincipal);
        attributesProvider.saveAttributes(sa);
        if (log.isDebugEnabled())
            log.debug("Added user: " + userName);
    }    
    
    /**
     * @see org.apache.jetspeed.security.UserManager#removeUser(java.lang.String)
     * 
     * TODO Enforce that only administrators can do this.
     */
    public void removeUser(String username) throws SecurityException
    {
        if (getAnonymousUser().equals(username)) 
        { 
            throw new SecurityException(
                SecurityException.ANONYMOUS_USER_PROTECTED.create(username)); 
        }
        UserPrincipal userPrincipal = new UserPrincipalImpl(username);
        atnProviderProxy.removeUserPrincipal(userPrincipal);
//      TODO: should we use cascading deletes?
        attributesProvider.deleteAttributes(userPrincipal);
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#userExists(java.lang.String)
     */
    public boolean userExists(String username)
    {
        return atnProviderProxy.getUserPrincipal(username) != null;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUser(java.lang.String)
     */
    public User getUser(String username) throws SecurityException
    {       
        // optimize guest lookups as they can be excessive
        if (guest != null && getAnonymousUser().equals(username))
        {
            // TODO: need to handle caching issues            
            return guest;
        }        
        
        return (User) userPrincipalManager.getPrincipal(username);
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUsers(java.lang.String)
     */
    public Collection<User> getUsers(String filter) throws SecurityException
    {
        List<User> users = new LinkedList<User>();
        for (JetspeedPrincipal principal : userPrincipalManager.getPrincipals(filter))
        {
            users.add((User) principal);
        }
        return users;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUserNames(java.lang.String)
     */
    public List<String> getUserNames(String filter) throws SecurityException
    {
        List<String> usernames = new LinkedList<String>();
        for (UserPrincipal userPrincipal : atnProviderProxy.getUserPrincipals(filter))
        {
            usernames.add(userPrincipal.getName());
        }
        return usernames;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUsersInRole(java.lang.String)
     */
    public Collection<User> getUsersInRole(String roleName)
            throws SecurityException
    {
        Collection<User> users = new ArrayList<User>();
        //
        //for (UserPrincipal userPrincipal : securityMappingHandler.getUserPrincipalsInRole(roleName))
        //{
        //    users.add(constructUser(userPrincipal));
        //}
        
        // TODO: need to invoke JPM's getAssociatedFrom() or getAssociatedTo() method here? 
        
        return users;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#getUsersInGroup(java.lang.String)
     */
    public Collection<User> getUsersInGroup(String groupFullPathName)
            throws SecurityException
    {
        Collection<User> users = new ArrayList<User>();
        //for (UserPrincipal userPrincipal : securityMappingHandler.getUserPrincipalsInGroup(groupFullPathName))
        //{
        //    users.add(constructUser(userPrincipal));
        //}
        
        // TODO: need to invoke JPM's getAssociatedFrom() or getAssociatedTo() method here? 
        
        return users;
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#setPassword(java.lang.String,
     *      java.lang.String, java.lang.String)
     * 
     * TODO Enforce that only administrators can do this.
     */
    public void setPassword(String username, String oldPassword,
            String newPassword) throws SecurityException
    {
        if (getAnonymousUser().equals(username)) 
        { 
            throw new SecurityException(
                SecurityException.ANONYMOUS_USER_PROTECTED.create(username)); 
        }
        atnProviderProxy.setPassword(username, oldPassword, newPassword);
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#setPasswordEnabled(java.lang.String,
     *      boolean)
     */
    public void setPasswordEnabled(String userName, boolean enabled)
            throws SecurityException
    {
        if (getAnonymousUser().equals(userName)) 
        { 
            throw new SecurityException(
                SecurityException.ANONYMOUS_USER_PROTECTED.create(userName)); 
        }
        atnProviderProxy.setPasswordEnabled(userName, enabled);
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#setPasswordUpdateRequired(java.lang.String,
     *      boolean)
     */
    public void setPasswordUpdateRequired(String userName,
            boolean updateRequired) throws SecurityException
    {
        if (getAnonymousUser().equals(userName)) 
        { 
            throw new SecurityException(
                SecurityException.ANONYMOUS_USER_PROTECTED.create(userName)); 
        }
        atnProviderProxy.setPasswordUpdateRequired(userName, updateRequired);
    }
    
    
    /**
     * @see org.apache.jetspeed.security.UserManager#setUserEnabled(java.lang.String, boolean)
     */
    public void setUserEnabled(String userName, boolean enabled) throws SecurityException
    {
        if (getAnonymousUser().equals(userName))
        {
            throw new SecurityException(SecurityException.ANONYMOUS_USER_PROTECTED.create(userName));
        }
        UserPrincipalImpl userPrincipal = (UserPrincipalImpl)atnProviderProxy.getUserPrincipal(userName);
        if (null == userPrincipal) 
        { 
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(userName));
        }
        if ( enabled != userPrincipal.isEnabled() )
        {
            userPrincipal.setEnabled(enabled);
            atnProviderProxy.updateUserPrincipal(userPrincipal);
        }
    }

    /**
     * @see org.apache.jetspeed.security.UserManager#setPasswordExpiration(java.lang.String, java.sql.Date)
     */
    public void setPasswordExpiration(String userName, Date expirationDate) throws SecurityException
    {
        if (getAnonymousUser().equals(userName)) 
        { 
            throw new SecurityException(SecurityException.ANONYMOUS_USER_PROTECTED.create(userName)); 
        }
        atnProviderProxy.setPasswordExpiration(userName, expirationDate);
    }
    
    public void updateUser(User user) throws SecurityException
    {
        try
        {
            userPrincipalManager.updatePrincipal(user);
        } 
        catch (PrincipalUpdateException e)
        {
            throw new SecurityException(e);
        } 
        catch (PrincipalNotFoundException e)
        {
            throw new SecurityException(e);
        }
    }

    public Collection<User> lookupUsers(String name, String value) throws SecurityException
    {
        Collection<User> resultSet = new LinkedList<User>();
        Collection<SecurityAttributes> attributes = this.attributesProvider.lookupAttributes(name, value);
        for (SecurityAttributes sa : attributes)
        {
            if (sa.getPrincipal() instanceof UserPrincipal)
            {
                User user = this.getUser(sa.getPrincipal().getName());
                if (user != null)
                {
                    resultSet.add(user);
                }
            }
        }
        return resultSet;
    }
}