/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi.impl.ldap;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.SecurityException;

/**
 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 */
public abstract class LdapPrincipalDaoImpl extends AbstractLdapDao implements LdapPrincipalDao
{
    /** The logger. */
    private static final Log LOG = LogFactory.getLog(LdapPrincipalDaoImpl.class);

    /** The uid attribute name. */
    protected static final String UID_ATTR_NAME = "uid";
    
    /**
     * <p>
     * Default constructor.
     * </p>
     * 
     * @throws NamingException A {@link NamingException}.
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapPrincipalDaoImpl() throws NamingException, SecurityException
    {
        super();
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
     * 
     * @throws NamingException A {@link NamingException}.
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapPrincipalDaoImpl(String ldapServerName, String rootDn, String rootPassword, String rootContext,
            String defaultDnSuffix) throws NamingException, SecurityException
    {
        super(ldapServerName, rootDn, rootPassword, rootContext, defaultDnSuffix);
    }

    /**
     * <p>
     * A template method for creating a concrete principal object.
     * </p>
     * 
     * @param principalUid The principal uid.
     * @return A concrete principal object.
     */
    protected abstract Principal makePrincipal(String principalUid);

    /**
     * <p>
     * A template method for defining the attributes for a particular LDAP
     * class.
     * </p>
     * 
     * @param principalUid The principal uid.
     * @return The LDAP attributes object for the particular class.
     */
    protected abstract Attributes defineLdapAttributes(final String principalUid);

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao#create(java.lang.String)
     */
    public void create(final String principalUid) throws SecurityException
    {
        Attributes attrs = defineLdapAttributes(principalUid);

        try
        {
            ctx.createSubcontext("uid=" + principalUid + super.defaultDnSuffix, attrs);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao#delete(java.lang.String)
     */
    public void delete(final String principalUid) throws SecurityException
    {
        String dn = lookupByUid(principalUid);

        if (dn == null)
        {
            return;
        }

        String rdn;
        try
        {
            rdn = getSubcontextName(dn);
            ctx.destroySubcontext(rdn);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    /**
     * <p>
     * Converts the uid to an ldap acceptable name.
     * </p>
     * 
     * @param uid The uid.
     * @return The converted name.
     */
    protected String convertUidToLdapAcceptableName(String uid)
    {
        return uid.replaceAll("/", "&");
    }

    /**
     * <p>
     * Convert the uid back from the ldap acceptable name.
     * </p>
     * 
     * @param uid The uid.
     * @return The converted back name.
     */
    protected String convertUidFromLdapAcceptableName(String uid)
    {
        return uid.replaceAll("&", "/");
    }

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao#find(java.lang.String)
     */
    public Principal[] find(final String principalUid) throws SecurityException
    {
        try
        {
            SearchControls cons = setSearchControls();
            NamingEnumeration searchResults = searchByWildcardedUid(convertUidToLdapAcceptableName(principalUid), cons);
            Collection principals = new ArrayList();

            enumerateOverSearchResults(searchResults, principals);

            return convertPrincipalListToArray(principals);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    /**
     * <p>
     * Converts a list of principals to an array of principals.
     * </p>
     * 
     * @param userPrincipals The list of principals.
     * @return The array of principals.
     */
    private Principal[] convertPrincipalListToArray(Collection principals)
    {
        return (Principal[]) principals.toArray(new Principal[principals.size()]);
    }

    /**
     * <p>
     * Build the user principal by enumerating through the search results.
     * </p>
     * 
     * @param searchResults The {@link NamingEnumeration} of results.
     * @param userPrincipals The collection of user principals.
     * @throws NamingException Throws a {@link NamingException}.
     */
    private void enumerateOverSearchResults(NamingEnumeration searchResults, Collection principals)
            throws NamingException
    {
        while (searchResults.hasMore())
        {
            SearchResult searchResult = (SearchResult) searchResults.next();

            buildPrincipal(principals, searchResult);
        }
    }

    /**
     * @param principals The collection of principals.
     * @param searchResult The {@link SearchResult}
     * @throws NamingException Throws a {@link NamingException}.
     */
    private void buildPrincipal(Collection principals, SearchResult searchResult) throws NamingException
    {
        if (searchResult.getObject() instanceof DirContext)
        {
            Attributes atts = searchResult.getAttributes();

            String uid = (String) getAttribute(UID_ATTR_NAME, atts).getAll().next();
            Principal principal = makePrincipal(uid);

            principals.add(principal);
        }
    }

    /**
     * @param attributeName The attribute name.
     * @param userAttributes The user {@link Attributes}.
     * @return The {@link Attribute}.
     * @throws NamingException Throws a {@link NamingException}.
     */
    protected Attribute getAttribute(String attributeName, Attributes userAttributes) throws NamingException
    {
        for (NamingEnumeration ae = userAttributes.getAll(); ae.hasMore();)
        {
            Attribute attr = (Attribute) ae.next();

            if (attr.getID().equals(attributeName))
            {
                return attr;
            }
        }
        return null;
    }

}