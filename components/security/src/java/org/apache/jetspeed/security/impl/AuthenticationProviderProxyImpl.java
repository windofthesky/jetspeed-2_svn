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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jetspeed.security.AuthenticationProvider;
import org.apache.jetspeed.security.AuthenticationProviderProxy;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;

/**
 * @see org.apache.jetspeed.security.AuthenticationProviderProxy
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class AuthenticationProviderProxyImpl implements AuthenticationProviderProxy
{

    /** The list of {@link AuthenticationProvider}. */
    private List authenticationProviders = new ArrayList();

    /** The default authentication provider name. */
    private String defaultAuthenticationProvider = null;

    /**
     * <p>
     * Constructor given a list of {@link AuthenticationProvider}.
     * </p>
     * 
     * @param authenticationProviders The list of {@link AuthenticationProvider}.
     * @param defaultAuthenticationProvider The default authentication provider name.
     */
    public AuthenticationProviderProxyImpl(List authenticationProviders, String defaultAuthenticationProvider)
    {
        this.authenticationProviders = authenticationProviders;
        this.defaultAuthenticationProvider = defaultAuthenticationProvider;
    }
    
    protected AuthenticationProvider getAuthenticationProviderByName(String providerName)
    {
        AuthenticationProvider provider = null;
        
        for (int i = 0; i < authenticationProviders.size(); i++)
        {
            provider = (AuthenticationProvider) authenticationProviders.get(i);
            if (providerName.equals(provider.getProviderName()))
            {
                break;
            }
            else
            {
                provider = null;
            }
        }
        return provider;
    }
    
    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#getDefaultAuthenticationProvider()
     */
    public String getDefaultAuthenticationProvider()
    {
        return this.defaultAuthenticationProvider;
    }
    
    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#getAuthenticationProvider(java.lang.String)
     */
    public String getAuthenticationProvider(String userName)
    {
        AuthenticationProvider authenticationProvider;
        String providerName = null;
        
        for (int i = 0; i < authenticationProviders.size(); i++)
        {
            authenticationProvider = (AuthenticationProvider)authenticationProviders.get(i);
            if (authenticationProvider.getUserSecurityHandler().isUserPrincipal(userName))
            {
                providerName = authenticationProvider.getProviderName();
                break;
            }
        }
        return providerName;
    }    
    
    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#isUserPrincipal(java.lang.String)
     */
    public boolean isUserPrincipal(String userName)
    {
        boolean exists = false;
        
        for (int i = 0; i < authenticationProviders.size(); i++)
        {
            exists = ((AuthenticationProvider)authenticationProviders.get(i)).getUserSecurityHandler().isUserPrincipal(userName);
            if (exists)
            {
                break;
            }
        }
        return exists;
    }
    
    
    
    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#getUserPrincipal(java.lang.String)
     */
    public Principal getUserPrincipal(String username)
    {
        Principal userPrincipal = null;
        for (int i = 0; i < authenticationProviders.size(); i++)
        {
            userPrincipal = ((AuthenticationProvider)authenticationProviders.get(i)).getUserSecurityHandler().getUserPrincipal(username);
            if (null != userPrincipal)
            {
                break;
            }
        }
        return userPrincipal;
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#getUserPrincipals(java.lang.String)
     */
    public List getUserPrincipals(String filter)
    {
        List userPrincipals = new LinkedList();
        for (int i = 0; i < authenticationProviders.size(); i++)
        {
            userPrincipals.addAll(((AuthenticationProvider)authenticationProviders.get(i)).getUserSecurityHandler().getUserPrincipals(filter));
        }
        return userPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#addUserPrincipal(org.apache.jetspeed.security.UserPrincipal,
     *      java.lang.String)
     */
    public void addUserPrincipal(UserPrincipal userPrincipal, String authenticationProvider) throws SecurityException
    {
        AuthenticationProvider provider = getAuthenticationProviderByName(authenticationProvider);
        if ( provider != null )
        {
            provider.getUserSecurityHandler().updateUserPrincipal(userPrincipal);
        }
        else
        {
            throw new SecurityException(SecurityException.INVALID_AUTHENTICATION_PROVIDER.create(authenticationProvider));
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#addUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void addUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        String providerName = getAuthenticationProvider(userPrincipal.getName());
        if ( providerName == null )
        {
            updateUserPrincipal(userPrincipal, defaultAuthenticationProvider);
        }
        else
        {
            throw new SecurityException(SecurityException.USER_ALREADY_EXISTS.create(userPrincipal.getName()));
        }
    }

    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#updateUserPrincipal(org.apache.jetspeed.security.UserPrincipal,
     *      java.lang.String)
     */
    public void updateUserPrincipal(UserPrincipal userPrincipal, String authenticationProvider) throws SecurityException
    {
        AuthenticationProvider provider = getAuthenticationProviderByName(authenticationProvider);
        if ( provider != null )
        {
            provider.getUserSecurityHandler().updateUserPrincipal(userPrincipal);
        }
        else
        {
            throw new SecurityException(SecurityException.INVALID_AUTHENTICATION_PROVIDER.create(authenticationProvider));
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#updateUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void updateUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        String providerName = getAuthenticationProvider(userPrincipal.getName());
        if ( providerName != null )
        {
            updateUserPrincipal(userPrincipal, providerName);
        }
        else
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(userPrincipal.getName()));
        }
    }

    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#removeUserPrincipal(org.apache.jetspeed.security.UserPrincipal,
     *      java.lang.String)
     */
    public void removeUserPrincipal(UserPrincipal userPrincipal, String authenticationProvider) throws SecurityException
    {
        AuthenticationProvider provider = getAuthenticationProviderByName(authenticationProvider);
        if ( provider != null )
        {
            provider.getUserSecurityHandler().removeUserPrincipal(userPrincipal);
        }
        else
        {
            throw new SecurityException(SecurityException.INVALID_AUTHENTICATION_PROVIDER.create(authenticationProvider));
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#removeUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void removeUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        String providerName = getAuthenticationProvider(userPrincipal.getName());
        if ( providerName != null )
        {
            removeUserPrincipal(userPrincipal, providerName);
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPublicCredentials(java.lang.String)
     */
    public Set getPublicCredentials(String username)
    {
        Set publicCredentials = new HashSet();
        String providerName = getAuthenticationProvider(username);
        if ( providerName != null )
        {
            AuthenticationProvider provider = getAuthenticationProviderByName(providerName);
            publicCredentials.addAll(provider.getCredentialHandler().getPublicCredentials(username));
        }
        return publicCredentials;
    }

    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#setPublicPasswordCredential(org.apache.jetspeed.security.PasswordCredential,
     *      org.apache.jetspeed.security.PasswordCredential, java.lang.String)
     */
    public void setPassword(String userName, String oldPassword, String newPassword, String authenticationProvider) throws SecurityException
    {
        AuthenticationProvider provider = getAuthenticationProviderByName(authenticationProvider);
        if ( provider != null )
        {
            provider.getCredentialHandler().setPassword(userName,oldPassword,newPassword);
        }
        else
        {
            throw new SecurityException(SecurityException.INVALID_AUTHENTICATION_PROVIDER.create(authenticationProvider));
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPassword(java.lang.String,java.lang.String,java.lang.String)
     */
    public void setPassword(String userName, String oldPassword, String newPassword) throws SecurityException
    {
        String providerName = getAuthenticationProvider(userName);
        if ( providerName != null )
        {
            setPassword(userName, oldPassword, newPassword, providerName);
        }
        else
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(userName));
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPrivateCredentials(java.lang.String)
     */
    public Set getPrivateCredentials(String username)
    {
        Set privateCredentials = new HashSet();
        String providerName = getAuthenticationProvider(username);
        if ( providerName != null )
        {
            AuthenticationProvider provider = getAuthenticationProviderByName(providerName);
            privateCredentials.addAll(provider.getCredentialHandler().getPrivateCredentials(username));
        }
        return privateCredentials;
    }    
    
    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#setPasswordEnabled(java.lang.String, boolean, java.lang.String)
     */
    public void setPasswordEnabled(String userName, boolean enabled, String authenticationProvider)
            throws SecurityException
    {
        AuthenticationProvider provider = getAuthenticationProviderByName(authenticationProvider);
        if ( provider != null )
        {
            provider.getCredentialHandler().setPasswordEnabled(userName,enabled);
        }
        else
        {
            throw new SecurityException(SecurityException.INVALID_AUTHENTICATION_PROVIDER.create(authenticationProvider));
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPasswordEnabled(java.lang.String, boolean)
     */
    public void setPasswordEnabled(String userName, boolean enabled) throws SecurityException
    {
        String providerName = getAuthenticationProvider(userName);
        if ( providerName != null )
        {
            setPasswordEnabled(userName, enabled, providerName);
        }
        else
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(userName));
        }
    }

    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#setPasswordUpdateRequired(java.lang.String, boolean, java.lang.String)
     */
    public void setPasswordUpdateRequired(String userName, boolean updateRequired, String authenticationProvider)
            throws SecurityException
    {
        AuthenticationProvider provider = getAuthenticationProviderByName(authenticationProvider);
        if ( provider != null )
        {
            provider.getCredentialHandler().setPasswordUpdateRequired(userName,updateRequired);
        }
        else
        {
            throw new SecurityException(SecurityException.INVALID_AUTHENTICATION_PROVIDER.create(authenticationProvider));
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPasswordUpdateRequired(java.lang.String, boolean)
     */
    public void setPasswordUpdateRequired(String userName, boolean updateRequired) throws SecurityException
    {
        String providerName = getAuthenticationProvider(userName);
        if ( providerName != null )
        {
            setPasswordUpdateRequired(userName, updateRequired, providerName);
        }
        else
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(userName));
        }
    }

    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#authenticate(java.lang.String, char[], java.lang.String)
     */
    public boolean authenticate(String userName, String password, String authenticationProvider) throws SecurityException
    {
        AuthenticationProvider provider = getAuthenticationProviderByName(authenticationProvider);
        if ( provider != null )
        {
            return provider.getCredentialHandler().authenticate(userName, password);
        }
        else
        {
            throw new SecurityException(SecurityException.INVALID_AUTHENTICATION_PROVIDER.create(authenticationProvider));
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#authenticate(java.lang.String, java.lang.String)
     */
    public boolean authenticate(String userName, String password) throws SecurityException
    {
        String providerName = getAuthenticationProvider(userName);
        if ( providerName != null )
        {
            return authenticate(userName, password, providerName);
        }
        else
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(userName));
        }
    }
}