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
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.InvalidDnException;
import org.apache.jetspeed.security.InvalidPasswordException;
import org.apache.jetspeed.security.InvalidUidException;
import org.apache.jetspeed.security.SecurityException;

/**
 * <p>
 * Abstract ldap dao.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a
 *         href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public abstract class AbstractLdapDao
{
    /** The ldap binding configuration. */
    private LdapBindingConfig ldapBindingConfig = null;

    /** Reference to remote server context */
    protected LdapContext ctx;

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public AbstractLdapDao()
    {
        throw new UnsupportedOperationException("Must be instantiated with LDAP binding configuration.");
    }

    /**
     * <p>
     * Initializes the dao.
     * </p>
     * 
     * @param ldapConfig Holds the ldap configuration.
     * @throws SecurityException
     */
    public AbstractLdapDao(LdapBindingConfig ldapConfig) throws SecurityException
    {
        this.ldapBindingConfig = ldapConfig;
        bindToServer(ldapConfig.getRootDn(), ldapConfig.getRootPassword());
    }

    /**
     * <p>
     * Binds to the ldap server.
     * </p>
     * 
     * @param rootDn
     * @param rootPassword
     * @throws SecurityException
     */
    protected void bindToServer(String rootDn, String rootPassword) throws SecurityException
    {
        validateDn(rootDn);
        validatePassword(rootPassword);

        try
        {
            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY, this.ldapBindingConfig.getInitialContextFactory());
            env.put(Context.PROVIDER_URL, "ldap://" + this.ldapBindingConfig.getLdapServerName() + ":"
                    + this.ldapBindingConfig.getLdapServerPort() + "/" + this.ldapBindingConfig.getRootContext());
            env.put(Context.SECURITY_PRINCIPAL, rootDn);
            env.put(Context.SECURITY_CREDENTIALS, rootPassword);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            ctx = new InitialLdapContext(env, null);
        }
        catch (NamingException ne)
        {
            throw new SecurityException(ne);
        }
    }

    /**
     * <p>
     * Gets the sub context name.
     * </p>
     * 
     * @param dn The domain name.
     * @return The sub context name.
     * @throws NamingException
     */
    protected String getSubcontextName(final String dn) throws NamingException
    {
        NameParser parser = ctx.getNameParser("");
        Name name = parser.parse(dn);
        String rootStr = ctx.getNameInNamespace();
        Name root = parser.parse(rootStr);

        if (name.startsWith(root))
        {
            Name rname = name.getSuffix(root.size());

            return rname.toString();
        }

        return dn;
    }

    /**
     * <p>
     * Validate the domain name.
     * </p>
     * 
     * @param dn The domain name.
     */
    protected void validateDn(final String dn) throws SecurityException
    {
        if (StringUtils.isEmpty(dn))
        {
            throw new InvalidDnException();
        }
    }

    /**
     * <p>
     * Valiate the users password.
     * </p>
     * 
     * @param password The user.
     */
    protected void validatePassword(final String password) throws SecurityException
    {
        if (StringUtils.isEmpty(password))
        {
            throw new InvalidPasswordException();
        }
    }

    /**
     * @return The factors that determine the scope of the search and what gets returned as a result
     *         of the search.
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
     * Searches the LDAP server for the user with the specified userid (uid attribute).
     * </p>
     * 
     * @return the user's DN
     */
    public String lookupByUid(final String uid) throws SecurityException
    {
        validateUid(uid);

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

    /**
     * <p>
     * Gets the first matching user for the given uid.
     * </p>
     * 
     * @param searchResults The {@link NamingEnumeration}.
     * @return the user's DN of the first use in the list. Null if no users were found.
     * @throws NamingException Throws a {@link NamingException}.
     */
    private String getFirstDnForUid(NamingEnumeration searchResults) throws NamingException
    {
        String userDn = null;
        while ((null != searchResults) && searchResults.hasMore())
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
    protected void validateUid(String uid) throws SecurityException
    {
        String pattern = ".*\\(.*|.*\\[.*|.*\\{.*|.*\\\\.*|.*\\^.*|.*\\$.*|.*\\|.*|.*\\).*|.*\\?.*|.*\\*.*|.*\\+.*|.*\\..*";
        if (StringUtils.isEmpty(uid) || uid.matches(pattern))
        {
            throw new InvalidUidException();
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
        String searchFilter = "(&(uid=" + (StringUtils.isEmpty(filter) ? "*" : filter) + ") (objectclass="
                + getObjectClass() + "))";
        NamingEnumeration searchResults = ((DirContext) ctx).search("", searchFilter, cons);

        return searchResults;
    }

    /**
     * <p>
     * Returns the default suffix dn.
     * </p>
     * 
     * @return The defaultDnSuffix.
     */
    protected String getDefaultDnSuffix()
    {
        return this.ldapBindingConfig.getDefaultDnSuffix();
    }

    /**
     * <p>
     * Returns the groups organization unit.
     * </p>
     * 
     * @return The groupsOu.
     */
    protected String getGroupsOu()
    {
        return this.ldapBindingConfig.getGroupsOu();
    }

    /**
     * <p>
     * Returns the users organization unit.
     * </p>
     * 
     * @return The usersOu.
     */
    protected String getUsersOu()
    {
        return this.ldapBindingConfig.getUsersOu();
    }

    /**
     * <p>
     * Returns the root context.
     * </p>
     * 
     * @return The root context.
     */
    protected String getRootContext()
    {
        return this.ldapBindingConfig.getRootContext();
    }

    /**
     * <p>
     * A template method that returns the LDAP object class of the concrete DAO.
     * </p>
     * 
     * @return a String containing the LDAP object class name.
     */
    protected abstract String getObjectClass();
}