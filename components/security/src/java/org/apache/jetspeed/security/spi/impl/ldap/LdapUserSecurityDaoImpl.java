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

import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;

/**
 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapUserSecurityDao
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 */
public class LdapUserSecurityDaoImpl extends AbstractLdapDao implements LdapUserSecurityDao
{
    /** The logger. */
    private static final Log LOG = LogFactory.getLog(LdapUserSecurityDaoImpl.class);

    /** The uid attribute name. */
    private static final String UID_ATTR_NAME = "uid";

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public LdapUserSecurityDaoImpl()
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
     */
    public LdapUserSecurityDaoImpl(String ldapServerName, String rootDn, String rootPassword, String rootContext,
            String defaultDnSuffix)
    {
        super(ldapServerName, rootDn, rootPassword, rootContext, defaultDnSuffix);
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapUserSecurityDao#create(java.lang.String)
     */
    public void create(final String uid) throws SecurityException
    {
        Attributes attrs = new BasicAttributes(true);
        BasicAttribute classes = new BasicAttribute("objectclass");

        classes.add("top");
        classes.add("person");
        classes.add("uidObject");
        classes.add("organizationalPerson");
        classes.add("inetorgperson");
        attrs.put(classes);
        attrs.put("cn", uid);
        attrs.put("uid", uid);
        attrs.put("sn", uid);

        try
        {
            ctx.createSubcontext("uid=" + uid + this.defaultDnSuffix, attrs);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapUserSecurityDao#delete(java.lang.String)
     */
    public void delete(final String uid) throws SecurityException
    {
        String dn = lookupByUid(uid);

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
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapUserSecurityDao#find(java.lang.String)
     */
    public Principal[] find(final String uid) throws SecurityException
    {
        try
        {
            bindToServer(this.rootDn, this.rootPassword);

            SearchControls cons = setSearchControls();
            NamingEnumeration searchResults = searchByWildcardedUid(uid, cons);
            Collection userPrincipals = new ArrayList();

            enumerateOverSearchResults(searchResults, userPrincipals);

            return convertPrincipalListToArray(userPrincipals);
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
    private Principal[] convertPrincipalListToArray(Collection userPrincipals)
    {
        return (Principal[]) userPrincipals.toArray(new Principal[userPrincipals.size()]);
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
    private void enumerateOverSearchResults(NamingEnumeration searchResults, Collection userPrincipals)
            throws NamingException
    {
        while (searchResults.hasMore())
        {
            SearchResult searchResult = (SearchResult) searchResults.next();

            buildUserPrincipal(userPrincipals, searchResult);
        }
    }

    /**
     * @param userPrincipals The collection of user principals.
     * @param searchResult The {@link SearchResult}
     * @throws NamingException Throws a {@link NamingException}.
     */
    private void buildUserPrincipal(Collection userPrincipals, SearchResult searchResult) throws NamingException
    {
        if (searchResult.getObject() instanceof DirContext)
        {
            Attributes atts = searchResult.getAttributes();

            String uid = (String) getAttribute(UID_ATTR_NAME, atts).getAll().next();
            Principal userPrincipal = new UserPrincipalImpl(uid);

            userPrincipals.add(userPrincipal);
        }
    }

    /**
     * @param attributeName The attribute name.
     * @param userAttributes The user {@link Attributes}.
     * @return The {@link Attribute}.
     * @throws NamingException Throws a {@link NamingException}.
     */
    private Attribute getAttribute(String attributeName, Attributes userAttributes) throws NamingException
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

    /**
     * @param dn
     * @return
     * @throws NamingException
     */
    private String getSubcontextName(final String dn) throws NamingException
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
}