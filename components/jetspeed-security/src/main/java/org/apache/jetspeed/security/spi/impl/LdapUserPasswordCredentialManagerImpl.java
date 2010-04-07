/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi.impl;

import javax.naming.AuthenticationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.CredentialPasswordEncoder;
import org.apache.jetspeed.security.InvalidPasswordException;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.spi.AlgorithmUpgradeCredentialPasswordEncoder;
import org.apache.jetspeed.security.spi.JetspeedSecuritySynchronizer;
import org.apache.jetspeed.security.spi.UserPasswordCredentialAccessManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialPolicyManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialStorageManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.pool.factory.PoolingContextSource;
import org.springframework.ldap.support.LdapUtils;

/**
 * @version $Id$
 */
public class LdapUserPasswordCredentialManagerImpl implements UserPasswordCredentialManager
{
    private static final long serialVersionUID = 1131764631931510796L;
    
    static final Logger log = LoggerFactory.getLogger(UserPasswordCredentialManager.class);
    
    private UserPasswordCredentialStorageManager upcsm;
    private UserPasswordCredentialAccessManager upcam;
    private UserPasswordCredentialPolicyManager upcpm;
    private UserManager um;
    private JetspeedSecuritySynchronizer synchronizer;
    private PoolingContextSource poolingContextsource;
    private String userEntryPrefix;
    private DistinguishedName userSearchPath;
    private SearchControls searchControls;
    private Filter userFilter;
    private CredentialPasswordEncoder cpe;
    private boolean persistCredentials;
    private boolean changePasswordByUser;
    
    public LdapUserPasswordCredentialManagerImpl(UserPasswordCredentialStorageManager upcsm, UserPasswordCredentialAccessManager upcam,
                                                 UserPasswordCredentialPolicyManager upcpm, CredentialPasswordEncoder cpe,
                                                 PoolingContextSource poolingContextSource, 
                                                 String userSearchBase, String userFilter, String userEntryPrefix, String searchScope)
    {
        this.upcsm = upcsm;
        this.upcam = upcam;
        this.upcpm = upcpm;
        this.cpe =  cpe != null && (upcpm == null || upcpm.getCredentialPasswordEncoder() != cpe) ? cpe : null;
        this.poolingContextsource = poolingContextSource;
        this.userEntryPrefix = userEntryPrefix;        
        this.userSearchPath = new DistinguishedName(userSearchBase);
        if (!StringUtils.isEmpty(userFilter))
        {
            this.userFilter = new HardcodedFilter(userFilter);
        }        
        this.searchControls = new SearchControls();
        this.searchControls.setReturningAttributes(new String[]{});
        this.searchControls.setReturningObjFlag(false);
        this.searchControls.setSearchScope(Integer.parseInt(searchScope));
    }
    
    protected String getUserDn(String userName) throws SecurityException
    {
        DirContext ctx = null;
        try
        {
            Filter filter = new EqualsFilter(userEntryPrefix, userName);
            if (userFilter != null)
            {
                filter = new AndFilter().and(userFilter).and(filter);
            }
            ctx = poolingContextsource.getReadOnlyContext();
            NamingEnumeration<SearchResult> results = ctx.search(userSearchPath, filter.encode(), searchControls);
            
            String dn = null;         
            if (null != results && results.hasMore())
            {
                SearchResult result = results.next();
                dn = result.getNameInNamespace();
            }
            if (dn == null)
            {
                throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER, userName));
            }
            return dn;
        }
        catch (NamingException nex)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "authenticateUser", nex.getMessage()), nex);
        }
        finally
        {
            LdapUtils.closeContext(ctx);
        }
    }

    protected void authenticateUser(String userName, String dn, String password) throws SecurityException
    {
        DirContext ctx = null;
        try
        {
            // Note: this "authenticating" context is (logically) not pooled
            ctx = poolingContextsource.getContextSource().getContext(dn, password);
            ctx.close();
            ctx = null;
        }
        catch (AuthenticationException aex)
        {
            if (aex.getMessage() != null && aex.getMessage().equalsIgnoreCase("[LDAP: error code 49 - Invalid Credentials]"))
            {
                throw new InvalidPasswordException();
            }
            else
            {
                throw new SecurityException(aex);
            }
        }
        catch (NamingException nex)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "authenticateUser", nex.getMessage()), nex);
        }
        finally
        {
            LdapUtils.closeContext(ctx);
        }
    }
    
    protected void setPassword(String userName, String dn, String oldPassword, String newPassword, boolean changePasswordByUserOnly) throws SecurityException
    {
        DirContext ctx = null;
        try
        {
            if (changePasswordByUserOnly)
            {
                // Note: this "authenticating" context is (logically) not pooled
                ctx = poolingContextsource.getContextSource().getContext(dn, oldPassword);
            }
            else
            {
                ctx = poolingContextsource.getReadWriteContext();
            }
            DistinguishedName name = new DistinguishedName(dn);
            name.removeFirst(new DistinguishedName(ctx.getNameInNamespace()));
            Attribute namingAttr = new BasicAttribute("userPassword", newPassword);
            ModificationItem[] items = new ModificationItem[1];
            items[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, namingAttr);
            ctx.modifyAttributes(name, items);
        }
        catch (NamingException nex)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "setPassword", nex.getMessage()), nex);
        }
        finally
        {
            LdapUtils.closeContext(ctx);
        }
    }
    
    public void setUserManager(UserManager um)
    {
        this.um = um;
    }
    
    public void setJetspeedSecuritySynchronizer(JetspeedSecuritySynchronizer synchronizer)
    {
        this.synchronizer = synchronizer;
    }
    
    public void setPersistCredentials(boolean persistCredentials)
    {
        this.persistCredentials = persistCredentials;
    }
    
    public boolean isPersistCredentials()
    {
        return persistCredentials;
    }
    
    public void setChangePasswordByUser(boolean changePasswordByUser)
    {
        this.changePasswordByUser = changePasswordByUser;
    }
    
    public boolean isChangePasswordByUser()
    {
        return changePasswordByUser;
    }
    
    public PasswordCredential getPasswordCredential(User user) throws SecurityException
    {
        if (isPersistCredentials())
        {
            PasswordCredential credential = upcsm.getPasswordCredential(user);
            if (!credential.isNew() && upcpm != null)
            {
                if (upcpm.onLoad(credential, user.getName()))
                {
                    upcsm.storePasswordCredential(credential);                
                }
            }
            return credential;
        }
        else
        {
            // create new transient credential
            PasswordCredentialImpl credential = new PasswordCredentialImpl();
            credential.setUser(user);
            return credential;
        }
    }

    public void storePasswordCredential(PasswordCredential credential) throws SecurityException
    {
        String userDn = null;
        boolean authenticated = false;
        boolean isNewPasswordSet = credential.isNewPasswordSet();
        String newPassword = credential.getNewPassword();
        String oldPassword = credential.getOldPassword();
        String password = credential.getPassword();
        boolean encoded = credential.isEncoded();
        
        if (SynchronizationStateAccess.isSynchronizing())
        {
            authenticated = true;
        }
        else if (isNewPasswordSet)
        {
            userDn = getUserDn(credential.getUserName());
            if (oldPassword != null)
            {
                authenticateUser(credential.getUserName(), userDn, oldPassword);
                authenticated = true;
            }
        }
        
        if (upcpm != null)
        {
            upcpm.onStore(credential, authenticated);
        }
        if (isPersistCredentials())
        {
            upcsm.storePasswordCredential(credential);
        }
        
        if (isNewPasswordSet && !SynchronizationStateAccess.isSynchronizing())
        {
            String ldapPassword = credential.getPassword();
            if (cpe != null && newPassword != null || !encoded)
            {
                // encode password for LDAP ourselves
                ldapPassword = cpe.encode(credential.getUserName(), newPassword != null ? newPassword : password);
            }
            setPassword(credential.getUserName(), userDn, oldPassword, ldapPassword, oldPassword != null ? changePasswordByUser : false);
        }
    }

    public PasswordCredential getAuthenticatedPasswordCredential(String userName, String password) throws SecurityException
    {
        if (!SynchronizationStateAccess.isSynchronizing())
        {
            authenticateUser(userName, getUserDn(userName), password);
            if (synchronizer != null)
            {
                synchronizer.synchronizeUserPrincipal(userName);
            }
        }
        PasswordCredential credential = isPersistCredentials() ? upcam.getPasswordCredential(userName) : new PasswordCredentialImpl();
        if (credential == null)
        {
            credential = new PasswordCredentialImpl();
            // persistCredentials but user credentials not yet synchronized/stored
            if (um == null)
            {
                log.error("New User PasswordCredential cannot be persisted: requires UserManager to be set!!!");
            }
            else
            {
                // to be able to store the new password credential it needs the User to be set
                ((PasswordCredentialImpl)credential).setUser(um.getUser(userName));
            }
        }
        boolean setPassword = false;
        if (isPersistCredentials() && (!credential.isNew() || credential.getUser() != null))
        {
            if (credential.isNew())
            {
                setPassword = true;
            }
            else
            {
                String encodedPassword = password;
                if (upcpm != null && upcpm.getCredentialPasswordEncoder() != null && credential.isEncoded())
                {
                    CredentialPasswordEncoder encoder = upcpm.getCredentialPasswordEncoder();
                    if (upcpm.getCredentialPasswordEncoder() instanceof AlgorithmUpgradeCredentialPasswordEncoder)
                    {
                        encodedPassword = ((AlgorithmUpgradeCredentialPasswordEncoder)encoder).encode(credential, password);
                    }
                    else
                    {
                        encodedPassword = encoder.encode(userName, password);
                    }
                }
                if (!credential.getPassword().equals(encodedPassword))
                {
                    setPassword = true;
                }
            }
            if (setPassword)
            {
                credential.setPassword(null, password);
                boolean synchronizing = SynchronizationStateAccess.isSynchronizing();
                try
                {
                    SynchronizationStateAccess.setSynchronizing(Boolean.TRUE);
                    storePasswordCredential(credential);
                }
                finally
                {
                    SynchronizationStateAccess.setSynchronizing(synchronizing ? Boolean.TRUE : Boolean.FALSE);
                }                
            }
            
            if (upcpm != null)
            {
                if (upcpm.onLoad(credential, userName))
                {
                    upcsm.storePasswordCredential(credential);
                }
                if (credential.isEnabled() && !credential.isExpired())
                {
                    if (upcpm.authenticate(credential, userName, password, true))
                    {
                        upcsm.storePasswordCredential(credential);
                    }
                    if (!credential.isEnabled() || credential.isExpired())
                    {
                        throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER, userName));
                    }
                    else if (credential.getAuthenticationFailures() != 0)
                    {
                        throw new SecurityException(SecurityException.INVALID_PASSWORD);
                    }
                }
            }
        }
        if (credential.getUser() == null)
        {
            if (!credential.isNew())
            {            
                try
                {
                    upcam.loadPasswordCredentialUser(credential);
                }
                catch (Exception e)
                {
                    throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER, userName), e);
                }            
            }
            else
            {
                ((PasswordCredentialImpl)credential).setUserName(userName);
            }
        }
        return credential;
    }
}
