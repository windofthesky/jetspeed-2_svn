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
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;

/**
 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a
 *         href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public abstract class LdapPrincipalDaoImpl extends AbstractLdapDao implements LdapPrincipalDao
{
    /** The logger. */
    private static final Log logger = LogFactory.getLog(LdapPrincipalDaoImpl.class);

    /** The uid attribute name. */
    protected String UID_ATTR_NAME = "uid";

    /**
     * <p>
     * Default constructor.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapPrincipalDaoImpl() throws SecurityException
    {
        super();
    }

    /**
     * <p>
     * Initializes the dao.
     * </p>
     * 
     * @param ldapConfig Holds the ldap binding configuration.
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapPrincipalDaoImpl(LdapBindingConfig ldapConfig) throws SecurityException
    {
        super(ldapConfig);
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
     * A template method for defining the attributes for a particular LDAP class.
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
            String userDn = getEntryPrefix() + "=" + principalUid + getDnSuffix();
            ctx.createSubcontext(userDn, attrs);
            if (logger.isDebugEnabled())
            {
                logger.debug("Creating user dn: " + userDn);
            }
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    /**
     * <p>
     * Builds the dn suffix.
     * </p>
     * 
     * @return The dn suffix.
     */
    protected abstract String getDnSuffix();

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
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao#convertUidToLdapAcceptableName(java.lang.String)
     */
    public String convertUidToLdapAcceptableName(String fullPath)
    {
        if (logger.isErrorEnabled())
        {
            logger.debug("Principal fullPath:" + fullPath);
        }
        String ldapAcceptableName = fullPath;
        if (null == fullPath)
        {
            return ldapAcceptableName;
        }
        else if (fullPath.indexOf(UserPrincipal.PREFS_USER_ROOT) >= 0)
        {
            ldapAcceptableName = convertUidWithoutSlashes(UserPrincipalImpl.getPrincipalNameFromFullPath(fullPath));
        }
        else if (fullPath.indexOf(GroupPrincipal.PREFS_GROUP_ROOT) >= 0)
        {
            ldapAcceptableName = convertUidWithoutSlashes(GroupPrincipalImpl.getPrincipalNameFromFullPath(fullPath));
        }
        else if (fullPath.indexOf(GroupPrincipal.PREFS_ROLE_ROOT) >= 0)
        {
            ldapAcceptableName = convertUidWithoutSlashes(RolePrincipalImpl.getPrincipalNameFromFullPath(fullPath));
        }        
        if (logger.isErrorEnabled())
        {
            logger.debug("Ldap acceptable name:" + ldapAcceptableName);
        }

        return ldapAcceptableName;
    }

    /**
     * <p>
     * Returns a well formed uid for LDAP.
     * </p>
     * 
     * @param uid The uid.
     * @return The well formed uid.
     */
    private String convertUidWithoutSlashes(String uid)
    {
        String uidWithSlashed = uid.replaceAll("/", "&");
        return uidWithSlashed;
    }

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao#find(java.lang.String,
     *      java.lang.String)
     */
    public Principal[] find(final String principalUid, String principalType) throws SecurityException
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
     * @param principals The list of principals.
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
     * @param principals The collection of user principals.
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