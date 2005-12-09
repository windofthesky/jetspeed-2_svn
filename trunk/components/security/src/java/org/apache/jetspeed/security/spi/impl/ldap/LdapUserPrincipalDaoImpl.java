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

import java.util.List;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;

/**
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a
 *         href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class LdapUserPrincipalDaoImpl extends LdapPrincipalDaoImpl implements LdapUserPrincipalDao
{
    /** The logger. */
    private static final Log logger = LogFactory.getLog(LdapUserPrincipalDaoImpl.class);

    /** The group attribute name. */
    private static final String GROUP_ATTR_NAME = "j2-group";

    /**
     * <p>
     * Default constructor.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapUserPrincipalDaoImpl() throws SecurityException
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
    public LdapUserPrincipalDaoImpl(LdapBindingConfig ldapConfig) throws SecurityException
    {
        super(ldapConfig);
    }

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapUserPrincipalDao#addGroup(java.lang.String,
     *      java.lang.String)
     */
    public void addGroup(String userPrincipalUid, String groupPrincipalUid) throws SecurityException
    {
        modifyUserGroup(userPrincipalUid, groupPrincipalUid, DirContext.ADD_ATTRIBUTE);
    }

    /**
     * <p>
     * Replace or delete the user group attribute.
     * </p>
     * 
     * @param userPrincipalUid
     * @param groupPrincipalUid
     * @param operationType whether to replace or remove the specified user group from the user
     * @throws SecurityException A {@link SecurityException}.
     */
    private void modifyUserGroup(String userPrincipalUid, String groupPrincipalUid, int operationType)
            throws SecurityException
    {
        validateUid(userPrincipalUid);
        validateUid(groupPrincipalUid);
        String userDn = lookupByUid(userPrincipalUid);
        try
        {
            String rdn = getSubcontextName(userDn);
            Attributes attrs = new BasicAttributes(false);

            attrs.put("j2-group", groupPrincipalUid);
            ctx.modifyAttributes(rdn, operationType, attrs);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapUserPrincipalDao#removeGroup(java.lang.String,
     *      java.lang.String)
     */
    public void removeGroup(String userPrincipalUid, String groupPrincipalUid) throws SecurityException
    {
        modifyUserGroup(userPrincipalUid, groupPrincipalUid, DirContext.REMOVE_ATTRIBUTE);
    }

    /**
     * <p>
     * A template method for defining the attributes for a particular LDAP class.
     * </p>
     * 
     * @param principalUid The principal uid.
     * @return the LDAP attributes object for the particular class.
     */
    protected Attributes defineLdapAttributes(final String principalUid)
    {
        Attributes attrs = new BasicAttributes(true);
        BasicAttribute classes = new BasicAttribute("objectclass");

        classes.add("top");
        classes.add("person");
        classes.add("uidObject");
        classes.add("organizationalPerson");
        classes.add("inetorgperson");
        classes.add("jetspeed-2-user");
        attrs.put(classes);
        attrs.put("cn", principalUid);
        attrs.put("uid", principalUid);
        attrs.put("sn", principalUid);
        attrs.put("ou", getUsersOu());

        return attrs;
    }

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDaoImpl#getDnSuffix()
     */
    protected String getDnSuffix()
    {
        String suffix = "";
        if (!StringUtils.isEmpty(getUsersOu()))
        {
            suffix += ",ou=" + getUsersOu();
        }
        if (!StringUtils.isEmpty(getDefaultDnSuffix()))
        {
            suffix += getDefaultDnSuffix();
        }
        return suffix;
    }

    /**
     * <p>
     * Creates a GroupPrincipal object.
     * </p>
     * 
     * @param principalUid The principal uid.
     * @return A group principal object.
     */
    protected Principal makePrincipal(String principalUid)
    {
        return new UserPrincipalImpl(principalUid);
    }

    /**
     * <p>
     * A template method that returns the LDAP object class of the concrete DAO.
     * </p>
     * 
     * @return a String containing the LDAP object class name.
     */
    protected String getObjectClass()
    {
        return "jetspeed-2-user";
    }

    /**
     * <p>
     * Return an array of the user principal UIDS that belong to a group.
     * </p>
     * 
     * @param groupPrincipalUid The group principal uid.
     * @return The array of user uids asociated with this group
     * @throws SecurityException A {@link SecurityException}.
     */
    public String[] getUserUidsForGroup(String groupPrincipalUid) throws SecurityException
    {
        validateUid(groupPrincipalUid);
        SearchControls cons = setSearchControls();
        NamingEnumeration results;
        try
        {
            List userPrincipalUids = new ArrayList();
            results = searchUserByGroup(groupPrincipalUid, cons);
            while (results.hasMore())
            {
                SearchResult result = (SearchResult) results.next();
                Attributes answer = result.getAttributes();

                userPrincipalUids.addAll(getAttributes(getAttribute(UID_ATTR_NAME, answer)));
            }
            return (String[]) userPrincipalUids.toArray(new String[userPrincipalUids.size()]);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    /**
     * <p>
     * Search user by group.
     * </p>
     * 
     * @param groupPrincipalUid
     * @param cons
     * @return
     * @throws NamingException A {@link NamingException}.
     */
    private NamingEnumeration searchUserByGroup(final String groupPrincipalUid, SearchControls cons)
            throws NamingException
    {
        String query = "(&(" + GROUP_ATTR_NAME + "=" + (groupPrincipalUid) + ") (objectclass=" + getObjectClass()
                + "))";
        if (logger.isDebugEnabled())
        {
            logger.debug("query[" + query + "]");
        }
        NamingEnumeration searchResults = ((DirContext) ctx).search("", "(&(" + GROUP_ATTR_NAME + "="
                + (groupPrincipalUid) + ") (objectclass=" + getObjectClass() + "))", cons);

        return searchResults;
    }

    /**
     * @param userPrincipalUid
     * @return the array of group uids asociated with this user
     * @throws SecurityException
     */
    public String[] getGroupUidsForUser(String userPrincipalUid) throws SecurityException
    {
        validateUid(userPrincipalUid);
        SearchControls cons = setSearchControls();
        NamingEnumeration results;
        try
        {
            results = searchByWildcardedUid(userPrincipalUid, cons);
            return getGroups(results, userPrincipalUid);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    /**
     * <p>
     * Get the groups.
     * </p>
     * 
     * @param results
     * @param uid
     * @return
     * @throws NamingException
     */
    private String[] getGroups(final NamingEnumeration results, final String uid) throws NamingException
    {
        if (!results.hasMore())
        {
            throw new NamingException("Could not find any user with uid[" + uid + "]");
        }

        Attributes userAttributes = getFirstUser(results);

        List uids = getAttributes(getAttribute(GROUP_ATTR_NAME, userAttributes));
        return (String[]) uids.toArray(new String[uids.size()]);
    }

    /**
     * @param results
     * @return
     * @throws NamingException
     */
    private Attributes getFirstUser(NamingEnumeration results) throws NamingException
    {
        SearchResult result = (SearchResult) results.next();
        Attributes answer = result.getAttributes();

        return answer;
    }

    /**
     * @param attr
     * @return
     * @throws NamingException
     */
    private List getAttributes(Attribute attr) throws NamingException
    {
        List uids = new ArrayList();
        if (attr != null)
        {
            Enumeration groupUidEnum = attr.getAll();
            while (groupUidEnum.hasMoreElements())
            {
                uids.add(groupUidEnum.nextElement());
            }
        }
        return uids;
    }
}