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
package org.apache.jetspeed.security.impl;

import javax.naming.AuthenticationException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.AuthenticatedUser;
import org.apache.jetspeed.security.AuthenticatedUserImpl;
import org.apache.jetspeed.security.InvalidPasswordException;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.spi.JetspeedSecuritySynchronizer;
import org.apache.jetspeed.security.spi.UserPasswordCredentialManager;

import org.springframework.ldap.pool.factory.PoolingContextSource;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.support.LdapUtils;

/**
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class LdapAuthenticationProvider extends BaseAuthenticationProvider
{
    private JetspeedSecuritySynchronizer synchronizer;
    private UserPasswordCredentialManager upcm;
    private UserManager manager;
    private PoolingContextSource poolingContextsource;
    private String userEntryPrefix;
    private DistinguishedName userSearchPath;
    private SearchControls searchControls;
    private Filter userFilter;

    public LdapAuthenticationProvider(String providerName, String providerDescription, String loginConfig, 
                                       UserPasswordCredentialManager upcm, UserManager manager, JetspeedSecuritySynchronizer synchronizer,  PoolingContextSource poolingContextSource, 
                                       String userSearchBase, String userFilter, String userEntryPrefix, String searchScope)
    {
        super(providerName, providerDescription, loginConfig);
        this.upcm = upcm;
        this.manager = manager;
        this.synchronizer = synchronizer;
        this.poolingContextsource = poolingContextSource;
        this.userEntryPrefix = userEntryPrefix;        
        this.userSearchPath = new DistinguishedName(userSearchBase);
        if (!StringUtils.isEmpty(userFilter))
        {
            this.userFilter = new HardcodedFilter(userFilter);
        }        
        this.searchControls = new SearchControls();
        this.searchControls.setReturningAttributes(new String[]{});
        this.searchControls.setReturningObjFlag(true);
        this.searchControls.setSearchScope(Integer.parseInt(searchScope));
    }

    public AuthenticatedUser authenticate(String userName, String password) throws SecurityException
    {
        AuthenticatedUser authUser = null;
        if (StringUtils.isEmpty(userName))
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER, userName));
        }
        if (password == null)
        {
            throw new SecurityException(SecurityException.PASSWORD_REQUIRED);
        }
        authenticateUser(userName, password);
        if (synchronizer != null)
        {
            synchronizer.synchronizeUserPrincipal(userName);
        }
        User user = manager.getUser(userName);
        authUser = new AuthenticatedUserImpl(user, new UserCredentialImpl(upcm.getPasswordCredential(user)));
        return authUser;
    }

    private void authenticateUser(String userName, String password) throws SecurityException
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
            LdapUtils.closeContext(ctx);
            
            // Note: this "authenticating" context is (logically) not pooled
            ctx = poolingContextsource.getContextSource().getContext(dn, password);
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
}
