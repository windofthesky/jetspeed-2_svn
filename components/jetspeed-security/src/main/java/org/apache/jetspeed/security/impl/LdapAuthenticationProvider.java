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

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.AuthenticatedUser;
import org.apache.jetspeed.security.AuthenticatedUserImpl;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.mapping.ldap.util.DnUtils;
import org.apache.jetspeed.security.spi.JetspeedSecuritySynchronizer;
import org.apache.jetspeed.security.spi.UserPasswordCredentialManager;
import org.apache.jetspeed.security.spi.impl.ldap.LdapContextProxy;

/**
 * @author <a href="mailto:vkumar@apache.org">Vivek Kumar</a>
 * @version $Id:
 */
public class LdapAuthenticationProvider extends BaseAuthenticationProvider
{
    private JetspeedSecuritySynchronizer synchronizer;
    private UserPasswordCredentialManager upcm;
    private UserManager manager;
    private LdapContextProxy context;

    public LdapAuthenticationProvider(String providerName, String providerDescription, String loginConfig, UserPasswordCredentialManager upcm,
                                      UserManager manager)
    {
        super(providerName, providerDescription, loginConfig);
        this.upcm = upcm;
        this.manager = manager;
    }

    public void setContext(LdapContextProxy context)
    {
        this.context = context;
    }

    public void setSynchronizer(JetspeedSecuritySynchronizer synchronizer)
    {
        this.synchronizer = synchronizer;
    }

    public AuthenticatedUser authenticate(String userName, String password) throws SecurityException
    {
        AuthenticatedUser authUser = null;
        boolean authenticated = false;
        try
        {
            if (userName == null)
            {
                throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER, userName));
            }
            if (password == null)
            {
                throw new SecurityException(SecurityException.PASSWORD_REQUIRED);
            }
            authenticated = authenticateUser(userName, password);
            if (authenticated)
            {
                User user = getUser(userName);
                authUser = new AuthenticatedUserImpl(user, new UserCredentialImpl(upcm.getPasswordCredential(user)));
            }
        }
        catch (SecurityException authEx)
        {
            if (authEx.getCause().getMessage().equalsIgnoreCase("[LDAP: error code 49 - Invalid Credentials]"))
            {
                throw new SecurityException(SecurityException.INCORRECT_PASSWORD);
            }
            else
            {
                throw authEx;
            }
        }
        return authUser;
    }

    private User getUser(String userName) throws SecurityException
    {
        if (synchronizer != null)
        {
            synchronizer.synchronizeUserPrincipal(userName,false);
        }
        return manager.getUser(userName);
    }

    private boolean authenticateUser(String userName, String password) throws SecurityException
    {
        try
        {
            Hashtable env = (Hashtable) context.getCtx().getEnvironment().clone();
            // String savedPassword = String.valueOf(getPassword(uid));
            String dn = lookupByUid(userName);
            if (dn == null)
            {
                throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER, userName));
            }
            // Build user dn using lookup value, just appending the user filter after the uid won't work when users
            // are/can be stored in a subtree (searchScope sub-tree)
            // The looked up dn though is/should always be correct, just need to append the root context.
            if (!StringUtils.isEmpty(context.getRootContext()))
            {
                if (DnUtils.encodeDn(dn).indexOf(DnUtils.encodeDn(context.getRootContext())) < 0)
                {
                    dn += "," + DnUtils.encodeDn(context.getRootContext());
                }
            }
            env.put(Context.SECURITY_PRINCIPAL, dn);
            env.put(Context.SECURITY_CREDENTIALS, password);
            new InitialContext(env);
            return true;
        }
        catch (AuthenticationException aex)
        {
            throw new SecurityException(aex);
        }
        catch (NamingException nex)
        {
            throw new SecurityException(SecurityException.UNEXPECTED.create(getClass().getName(), "authenticateUser", nex.getMessage()));
        }
    }

    public String lookupByUid(final String uid) throws SecurityException
    {
        try
        {
            SearchControls cons = setSearchControls();
            NamingEnumeration searchResults = searchByWildcardedUid(uid, cons);
            return getFirstDnForUid(searchResults);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    protected SearchControls setSearchControls()
    {
        SearchControls controls = new SearchControls();
        controls.setReturningAttributes(new String[] {});
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningObjFlag(true);
        return controls;
    }

    protected NamingEnumeration searchByWildcardedUid(final String filter, SearchControls cons) throws NamingException
    {
        // usa a template method to use users/groups/roles
        String query = "";
        if (StringUtils.isEmpty(getSearchSuffix()))
        {
            query = "(" + context.getEntryPrefix() + "=" + (StringUtils.isEmpty(filter) ? "*" : filter) + ")";
        }
        else
        {
            query = "(&(" + context.getEntryPrefix() + "=" + (StringUtils.isEmpty(filter) ? "*" : filter) + ")" + getSearchSuffix() + ")";
        }
        // logger.debug("searchByWildCardedUid = " + query);
        cons.setSearchScope(Integer.parseInt(context.getMemberShipSearchScope()));
        // TODO: added this here for OpenLDAP (when users are stored in ou=People,o=evenSeas)
        // String searchBase = StringUtils.replace(getSearchDomain(), "," + context.getRootContext(), "");
        NamingEnumeration results = ((DirContext) context.getCtx()).search(getSearchDomain(), query, cons);
        return results;
    }

    private String getFirstDnForUid(NamingEnumeration searchResults) throws NamingException
    {
        String userDn = null;
        while ((null != searchResults) && searchResults.hasMore())
        {
            SearchResult searchResult = (SearchResult) searchResults.next();
            userDn = searchResult.getName();
            String searchDomain = getSearchDomain();
            if (searchDomain.length() > 0)
            {
                userDn += "," + StringUtils.replace(searchDomain, "," + context.getRootContext(), "");
            }
        }
        return userDn;
    }

    private String getSearchSuffix()
    {
        return context.getUserFilter();
    }

    private String getSearchDomain()
    {
        StringBuffer searchDomain = new StringBuffer();
        if (!StringUtils.isEmpty(context.getUserSearchBase()))
        {
            searchDomain.append(context.getUserSearchBase());
        }
        if (searchDomain.length() == 0)
        {
            if (!StringUtils.isEmpty(context.getRootContext()))
            {
                searchDomain.append(context.getRootContext());
            }
        }
        return searchDomain.toString();
    }
}