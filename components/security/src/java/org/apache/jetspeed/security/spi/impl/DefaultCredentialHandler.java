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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalCredential;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalCredentialImpl;
import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.InternalPasswordCredentialInterceptor;
import org.apache.jetspeed.security.spi.PasswordCredentialProvider;
import org.apache.jetspeed.security.spi.SecurityAccess;

/**
 * @see org.apache.jetspeed.security.spi.CredentialHandler
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class DefaultCredentialHandler implements CredentialHandler
{
    private static final Log log = LogFactory.getLog(DefaultCredentialHandler.class);

    private SecurityAccess securityAccess;

    private PasswordCredentialProvider pcProvider;
    
    private InternalPasswordCredentialInterceptor ipcInterceptor;
    
    public DefaultCredentialHandler(SecurityAccess securityAccess, PasswordCredentialProvider pcProvider, 
            InternalPasswordCredentialInterceptor ipcInterceptor)
    {
        this.securityAccess = securityAccess;
        this.pcProvider = pcProvider;
        this.ipcInterceptor = ipcInterceptor;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPrivateCredentials(java.lang.String)
     */
    public Set getPrivateCredentials(String username)
    {
        Set credentials = new HashSet();
        InternalUserPrincipal internalUser = securityAccess.getInternalUserPrincipal(username, false);
        if (null != internalUser)
        {
            InternalCredential credential = getPasswordCredential(internalUser, username );
            if ( credential != null )
            {
                try
                {
                    credentials.add(pcProvider.create(username,credential));
                }
                catch (SecurityException e)
                {
                    if ( log.isErrorEnabled() )
                        log.error("Failure creating a PasswordCredential for InternalCredential "+credential, e);
                }
            }
        }
        return credentials;
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#getPublicCredentials(java.lang.String)
     */
    public Set getPublicCredentials(String username)
    {
        return new HashSet();
    }
    
    private InternalCredential getPasswordCredential(InternalUserPrincipal internalUser, String username)
    {
        InternalCredential credential = null;
        
        Collection internalCredentials = internalUser.getCredentials();
        if ( internalCredentials != null )
        {
            Iterator iter = internalCredentials.iterator();
            
            while (iter.hasNext())
            {
                credential = (InternalCredential) iter.next();
                if (credential.getType() == InternalCredential.PRIVATE )
                {
                    if ((null != credential.getClassname())
                            && (credential.getClassname().equals(pcProvider.getPasswordCredentialClass().getName())))
                    {
                        try
                        {
                            if ( ipcInterceptor != null && ipcInterceptor.afterLoad(pcProvider, username, credential) )
                            {
                                // update InternalUserPrincipal to save post processed data 
                                securityAccess.setInternalUserPrincipal(internalUser,internalUser.isMappingOnly());
                            }
                            break;
                        }
                        catch (SecurityException e)
                        {
                            if ( log.isErrorEnabled() )
                                log.error("Failure loading InternalCredential "+credential, e);
                        }
                    }
                }
                credential = null;
            }
        }
        return credential;
    }

    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPassword(java.lang.String,java.lang.String,java.lang.String)
     */
    public void setPassword(String userName, String oldPassword, String newPassword) throws SecurityException
    {
        InternalUserPrincipal internalUser = securityAccess.getInternalUserPrincipal(userName, false);
        if (null == internalUser)
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST + " " + userName);
        }
        
        Collection credentials = internalUser.getCredentials();
        if (null == credentials)
        {
            credentials = new ArrayList();
        }

        if (null != oldPassword)
        {
            if ( pcProvider.getValidator() != null )
            {
                pcProvider.getValidator().validate(oldPassword);
            }
            if ( pcProvider.getEncoder() != null )
            {
                oldPassword = pcProvider.getEncoder().encode(userName, oldPassword);
            }
        }
        
        InternalCredential credential = getPasswordCredential(internalUser, userName );
        
        if (oldPassword != null && (credential == null || credential.getValue() == null || !credential.getValue().equals(oldPassword)))
        {
            // supplied PasswordCredential not defined for this user
            throw new SecurityException(SecurityException.INVALID_PASSWORD);
        }
        
        if ( pcProvider.getValidator() != null )
        {
            pcProvider.getValidator().validate(newPassword);
        }
        
        boolean encoded = false;
        if ( pcProvider.getEncoder() != null )
        {
            newPassword = pcProvider.getEncoder().encode(userName, newPassword);
            encoded = true;
        }

        boolean create = credential == null;

        if ( create )
        {
            credential = new InternalCredentialImpl(internalUser.getPrincipalId(), newPassword, InternalCredential.PRIVATE,
                            pcProvider.getPasswordCredentialClass().getName());
            credential.setEncoded(encoded);
            credentials.add(credential);
        }
        else if ( oldPassword == null )
        {
/* TODO: should only be allowed for admin                     
            // User *has* an PasswordCredential: setting a new Credential without supplying
            // its current one is not allowed
            throw new SecurityException(SecurityException.PASSWORD_REQUIRED);
*/            
        }
        else if ( oldPassword.equals(newPassword) )
        {
            throw new SecurityException(SecurityException.INVALID_PASSWORD);
        }

        if ( ipcInterceptor != null )
        {
            if ( create )
            {
                ipcInterceptor.beforeCreate(internalUser, credentials, userName, credential, newPassword );
            }
            else
            {
                ipcInterceptor.beforeSetPassword(internalUser, credentials, userName, credential, newPassword, oldPassword != null );
            }
        }
        if (!create)
        {
            credential.setValue(newPassword);
            credential.setEncoded(encoded);
        }
                
        internalUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
        internalUser.setCredentials(credentials);
        // Set the user with the new credentials.
        securityAccess.setInternalUserPrincipal(internalUser, false);
    }
    
    
    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPasswordEnabled(java.lang.String, boolean)
     */
    public void setPasswordEnabled(String userName, boolean enabled) throws SecurityException
    {
        InternalUserPrincipal internalUser = securityAccess.getInternalUserPrincipal(userName, false);
        if (null != internalUser)
        {
            InternalCredential credential = getPasswordCredential(internalUser, userName );
            if ( credential != null && !credential.isExpired() && credential.isEnabled() != enabled )
            {
                credential.setEnabled(enabled);
                internalUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                securityAccess.setInternalUserPrincipal(internalUser, false);
            }
        }
        else
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST);
        }
    }
  
    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#setPasswordUpdateRequired(java.lang.String, boolean)
     */
    public void setPasswordUpdateRequired(String userName, boolean updateRequired) throws SecurityException
    {
        InternalUserPrincipal internalUser = securityAccess.getInternalUserPrincipal(userName, false);
        if (null != internalUser)
        {
            InternalCredential credential = getPasswordCredential(internalUser, userName );
            if ( credential != null && !credential.isExpired() && credential.isUpdateRequired() != updateRequired )
            {
                credential.setUpdateRequired(updateRequired);
                internalUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                securityAccess.setInternalUserPrincipal(internalUser, false);
            }
        }
        else
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST);
        }
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.CredentialHandler#authenticate(java.lang.String, java.lang.String)
     */
    public boolean authenticate(String userName, String password) throws SecurityException
    {
        boolean authenticated = false;
        InternalUserPrincipal internalUser = securityAccess.getInternalUserPrincipal(userName, false);
        if (null != internalUser)
        {
            InternalCredential credential = getPasswordCredential(internalUser, userName );
            if ( credential != null && credential.isEnabled() && !credential.isExpired())
            {
                if ( pcProvider.getEncoder() != null && credential.isEncoded())
                {
                    password = pcProvider.getEncoder().encode(userName,password);
                }

                authenticated = credential.getValue().equals(password);
                boolean update = false;

                if ( ipcInterceptor != null )
                {
                    update = ipcInterceptor.afterAuthenticated(internalUser, userName, credential, authenticated);
                    if ( update && (!credential.isEnabled() || credential.isExpired()))
                    {
                        authenticated = false;
                    }
                }
                if ( authenticated )
                {
                    credential.setAuthenticationFailures(0);
                    credential.setLastLogonDate(new Timestamp(System.currentTimeMillis()));
                    update = true;
                }
                
                if ( update )
                {
                    internalUser.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                    securityAccess.setInternalUserPrincipal(internalUser, false);
                }
            }
        }
        else
        {
            throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST);
        }
        return authenticated;
    }
}