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
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.UserSecurityHandler;

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

    /** The list of {@link UserSecurityHandler}. */
    private List userSecurityHandlers = new ArrayList();

    /** The list of {@link CredentialHandler}. */
    private List credentialHandlers = new ArrayList();

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

        for (int i = 0; i < authenticationProviders.size(); i++)
        {
            userSecurityHandlers.add(((AuthenticationProvider) authenticationProviders.get(i)).getUserSecurityHandler());
            credentialHandlers.add(((AuthenticationProvider) authenticationProviders.get(i)).getCredentialHandler());
        }
    }
    
    

    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#getDefaultAuthenticationProvider()
     */
    public String getDefaultAuthenticationProvider()
    {
        return this.defaultAuthenticationProvider;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#getUserPrincipal(java.lang.String)
     */
    public Principal getUserPrincipal(String username)
    {
        Principal userPrincipal = null;
        for (int i = 0; i < userSecurityHandlers.size(); i++)
        {
            userPrincipal = ((UserSecurityHandler) userSecurityHandlers.get(i)).getUserPrincipal(username);
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
        for (int i = 0; i < userSecurityHandlers.size(); i++)
        {
            userPrincipals.addAll(((UserSecurityHandler) userSecurityHandlers.get(i)).getUserPrincipals(filter));
        }
        return userPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#setUserPrincipal(org.apache.jetspeed.security.UserPrincipal,
     *      java.lang.String)
     */
    public void setUserPrincipal(UserPrincipal userPrincipal, String authenticationProvider) throws SecurityException
    {
        for (int i = 0; i < authenticationProviders.size(); i++)
        {
            AuthenticationProvider currAuthenticationProvider = (AuthenticationProvider) authenticationProviders.get(i);
            if (authenticationProvider.equals(currAuthenticationProvider.getProviderName()))
            {
                currAuthenticationProvider.getUserSecurityHandler().setUserPrincipal(userPrincipal);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#setUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void setUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        setUserPrincipal(userPrincipal, this.defaultAuthenticationProvider);
    }

    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#removeUserPrincipal(org.apache.jetspeed.security.UserPrincipal,
     *      java.lang.String)
     */
    public void removeUserPrincipal(UserPrincipal userPrincipal, String authenticationProvider) throws SecurityException
    {
        for (int i = 0; i < authenticationProviders.size(); i++)
        {
            AuthenticationProvider currAuthenticationProvider = (AuthenticationProvider) authenticationProviders.get(i);
            if (authenticationProvider.equals(currAuthenticationProvider.getProviderName()))
            {
                currAuthenticationProvider.getUserSecurityHandler().removeUserPrincipal(userPrincipal);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#removeUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void removeUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        removeUserPrincipal(userPrincipal, this.defaultAuthenticationProvider);
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPublicCredentials(java.lang.String)
     */
    public Set getPublicCredentials(String username)
    {
        Set publicCredentials = new HashSet();
        for (int i = 0; i < credentialHandlers.size(); i++)
        {
            publicCredentials.addAll(((CredentialHandler) credentialHandlers.get(i)).getPublicCredentials(username));
        }
        return publicCredentials;
    }

    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#setPublicPasswordCredential(org.apache.jetspeed.security.PasswordCredential,
     *      org.apache.jetspeed.security.PasswordCredential, java.lang.String)
     */
    public void setPublicPasswordCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential,
            String authenticationProvider) throws SecurityException
    {
        for (int i = 0; i < authenticationProviders.size(); i++)
        {
            AuthenticationProvider currAuthenticationProvider = (AuthenticationProvider) authenticationProviders.get(i);
            if (authenticationProvider.equals(currAuthenticationProvider.getProviderName()))
            {
                currAuthenticationProvider.getCredentialHandler().setPublicPasswordCredential(oldPwdCredential,
                        newPwdCredential);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPublicPasswordCredential(org.apache.jetspeed.security.PasswordCredential,
     *      org.apache.jetspeed.security.PasswordCredential)
     */
    public void setPublicPasswordCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential)
            throws SecurityException
    {
        setPublicPasswordCredential(oldPwdCredential, newPwdCredential, this.defaultAuthenticationProvider);
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPrivateCredentials(java.lang.String)
     */
    public Set getPrivateCredentials(String username)
    {
        Set privateCredentials = new HashSet();
        for (int i = 0; i < credentialHandlers.size(); i++)
        {
            privateCredentials.addAll(((CredentialHandler) credentialHandlers.get(i)).getPrivateCredentials(username));
        }
        return privateCredentials;
    }

    /**
     * @see org.apache.jetspeed.security.AuthenticationProviderProxy#setPrivatePasswordCredential(org.apache.jetspeed.security.PasswordCredential,
     *      org.apache.jetspeed.security.PasswordCredential, java.lang.String)
     */
    public void setPrivatePasswordCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential,
            String authenticationProvider) throws SecurityException
    {
        for (int i = 0; i < authenticationProviders.size(); i++)
        {
            AuthenticationProvider currAuthenticationProvider = (AuthenticationProvider) authenticationProviders.get(i);
            if (authenticationProvider.equals(currAuthenticationProvider.getProviderName()))
            {
                currAuthenticationProvider.getCredentialHandler().setPrivatePasswordCredential(oldPwdCredential,
                        newPwdCredential);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPrivatePasswordCredential(org.apache.jetspeed.security.PasswordCredential,
     *      org.apache.jetspeed.security.PasswordCredential)
     */
    public void setPrivatePasswordCredential(PasswordCredential oldPwdCredential, PasswordCredential newPwdCredential)
            throws SecurityException
    {
        setPrivatePasswordCredential(oldPwdCredential, newPwdCredential, this.defaultAuthenticationProvider);
    }

}