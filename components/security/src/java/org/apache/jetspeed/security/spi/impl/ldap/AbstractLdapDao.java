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
package org.apache.jetspeed.security.spi.impl.ldap;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * Abstract ldap dao.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 */
public abstract class AbstractLdapDao
{
    /** The ldap server name. */
    private String ldapServerName = null;

    /** The root domain. */
    protected String rootDn = null;

    /** The root password. */
    protected String rootPassword = null;

    /** The root context. */
    protected String rootContext = null;

    /** The default suffix. */
    protected String defaultDnSuffix = null;

    /** Reference to remote server context */
    protected LdapContext ctx;

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public AbstractLdapDao()
    {
    }
    
    /**
     * <p>
     * Initializes the dao.
     * </p>
     * 
     * @param ldapServerName The server name.
     * @param rootDn The root domain.
     * @param rootPassword The root password.
     * @param rootContext The root context.
     * @param defaultDnSuffix The default suffix.
     */
    public AbstractLdapDao(String ldapServerName, String rootDn, String rootPassword, String rootContext,
            String defaultDnSuffix)
    {
        this.ldapServerName = ldapServerName;
        this.rootDn = rootDn;
        this.rootPassword = rootPassword;
        this.rootContext = rootContext;
        this.defaultDnSuffix = defaultDnSuffix;
    }
    
    /**
     * <p>
     * Binds to the ldap server.
     * </p>
     * 
     * @param userDn
     * @param password
     * @throws NamingException
     */
    protected void bindToServer(final String userDn, final String password) throws NamingException
    {
        validateDn(userDn);
        validatePassword(password);

        Properties env = new Properties();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + this.ldapServerName + "/" + this.rootContext);
        env.put(Context.SECURITY_PRINCIPAL, userDn);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        ctx = new InitialLdapContext(env, null);
    }

    /**
     * <p>
     * Validate the domain name.
     * </p>
     * 
     * @param dn The domain name.
     */
    protected void validateDn(final String dn)
    {
        if (StringUtils.isEmpty(dn))
        {
            throw new IllegalArgumentException("The dn cannot be null or empty");
        }
    }

    /**
     * <p>
     * Valiate the users password.
     * </p>
     * 
     * @param userDn The user.
     */
    protected void validatePassword(final String password)
    {
        if (StringUtils.isEmpty(password))
        {
            throw new IllegalArgumentException("The password cannot be null or empty");
        }
    }

    /**
     * @return The factors that determine the scope of the search and what gets
     *         returned as a result of the search.
     */
    protected SearchControls setSearchControls()
    {
        SearchControls controls = new SearchControls();

        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningObjFlag(true);

        return controls;
    }

    /**
     * <p>
     * Searches the LDAP server for the user with the specified userid (uid
     * attribute).
     * </p>
     * 
     * @return the user's DN
     */
    public String lookupByUid(final String uid) throws SecurityException
    {
        validateUid(uid);

        try
        {
            bindToServer(this.rootDn, this.rootPassword);

            SearchControls cons = setSearchControls();
            NamingEnumeration searchResults = searchByWildcardedUid(uid, cons);

            return getFirstDnForUid(searchResults);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    /**
     * <p>
     * Gets the first matching user for the given uid.
     * </p>
     * 
     * @param searchResults The {@link NamingEnumeration}.
     * @return the user's DN of the first use in the list. Null if no users were
     *         found.
     * @throws NamingException Throws a {@link NamingException}.
     */
    private String getFirstDnForUid(NamingEnumeration searchResults) throws NamingException
    {
        String userDn = null;
        while (searchResults.hasMore())
        {
            SearchResult searchResult = (SearchResult) searchResults.next();

            if (searchResult.getObject() instanceof DirContext)
            {
                DirContext userEntry = (DirContext) searchResult.getObject();
                userDn = userEntry.getNameInNamespace();
            }
        }
        return userDn;
    }

    /**
     * <p>
     * Validate the uid.
     * </p>
     * 
     * @param uid The uid.
     */
    protected void validateUid(String uid)
    {
        if (StringUtils.isEmpty(uid) || uid.matches("\\(\\[\\{\\^\\$\\|\\)\\?\\*\\+\\.\\\\"))
        {
            throw new IllegalArgumentException(
                    "The uid cannot contain any regular expression meta-characters or be null or be empty ");
        }
    }

    /**
     * <p>
     * Search uid by wild card.
     * </p>
     * 
     * @param filter The filter.
     * @param cons The {@link SearchControls}
     * @return The {@link NamingEnumeration}
     * @throws NamingException Throws a {@link NamingEnumeration}.
     */
    protected NamingEnumeration searchByWildcardedUid(final String filter, SearchControls cons) throws NamingException
    {
        NamingEnumeration searchResults = ((DirContext) ctx).search("", "uid="
                + (StringUtils.isEmpty(filter) ? "*" : filter), cons);

        return searchResults;
    }
}