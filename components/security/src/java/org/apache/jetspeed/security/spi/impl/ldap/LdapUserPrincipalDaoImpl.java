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
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
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
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a
 *         href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class LdapUserPrincipalDaoImpl extends LdapPrincipalDaoImpl implements LdapUserPrincipalDao
{
    /** The logger. */
    private static final Log logger = LogFactory.getLog(LdapUserPrincipalDaoImpl.class);

    private LdapMembershipDao membership;

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
    	membership=new LdapMemberShipDaoImpl();
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
    	membership=new LdapMemberShipDaoImpl(ldapConfig);
    }

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapUserPrincipalDao#addGroup(java.lang.String,
     *      java.lang.String)
     */
    public void addGroup(String userPrincipalUid, String groupPrincipalUid) throws SecurityException
    {
    	if (getUserGroupMembershipAttribute()!=null && !getUserGroupMembershipAttribute().equals(""))	
    		modifyUserGroupByUser(userPrincipalUid, groupPrincipalUid, DirContext.ADD_ATTRIBUTE);
    	else
    		modifyUserGroupByGroup(userPrincipalUid, groupPrincipalUid, DirContext.ADD_ATTRIBUTE);
    	
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
    private void modifyUserGroupByGroup(String userPrincipalUid, String groupPrincipalUid, int operationType)
            throws SecurityException
    {
        validateUid(userPrincipalUid);
        validateUid(groupPrincipalUid);
        
        String userDn = "uid=" + userPrincipalUid + "," + getUserFilterBase();
        userDn+="," + getRootContext();
        try
        {
        	groupPrincipalUid = getGroupIdAttribute() + "=" + groupPrincipalUid; 
        	
        	if (getGroupFilterBase()!=null && !getGroupFilterBase().equals(""))
        		groupPrincipalUid+="," + getGroupFilterBase();
        	groupPrincipalUid+="," + getRootContext();
        	
            String rdn = getSubcontextName(groupPrincipalUid);
            Attributes attrs = new BasicAttributes(false);
            attrs.put(getGroupMembershipAttribute(), userDn);
            ctx.modifyAttributes(rdn, operationType, attrs);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
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
    private void modifyUserGroupByUser(String userPrincipalUid, String groupPrincipalUid, int operationType)
            throws SecurityException
    {
        validateUid(userPrincipalUid);
        validateUid(groupPrincipalUid);
        
        String userDn = "uid=" + userPrincipalUid + "," + getUserFilterBase();
        try
        {
        	groupPrincipalUid = getGroupIdAttribute() + "=" + groupPrincipalUid; 
        	
        	if (getGroupFilterBase()!=null && !getGroupFilterBase().equals(""))
        		groupPrincipalUid+="," + getGroupFilterBase();
        	groupPrincipalUid+="," + getRootContext();
            String rdn = getSubcontextName(userDn);
            Attributes attrs = new BasicAttributes(false);

            attrs.put(getUserGroupMembershipAttribute(), groupPrincipalUid);
            logger.debug("modifying attrs on " + rdn + " with : " + attrs);
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
    	if (getUserGroupMembershipAttribute()!=null && !getUserGroupMembershipAttribute().equals(""))
    		modifyUserGroupByUser(userPrincipalUid, groupPrincipalUid, DirContext.REMOVE_ATTRIBUTE);
    	else
    		modifyUserGroupByGroup(userPrincipalUid, groupPrincipalUid, DirContext.REMOVE_ATTRIBUTE);
    	
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapUserPrincipalDao#addGroup(java.lang.String,
     *      java.lang.String)
     */
    public void addRole(String userPrincipalUid, String rolePrincipalUid) throws SecurityException
    {
    	if (getUserRoleMembershipAttribute()!=null && !getUserRoleMembershipAttribute().equals(""))
    		modifyUserRoleByUser(userPrincipalUid, rolePrincipalUid, DirContext.ADD_ATTRIBUTE);
    	else
    		modifyUserRoleByRole(userPrincipalUid, rolePrincipalUid, DirContext.ADD_ATTRIBUTE);
    }

    /**
     * <p>
     * Replace or delete the role attribute.
     * 
     * </p>
     * 
     * @param userPrincipalUid
     * @param rolePrincipalUid
     * @param operationType whether to replace or remove the specified user group from the user
     * @throws SecurityException A {@link SecurityException}.
     */
    private void modifyUserRoleByUser(String userPrincipalUid, String rolePrincipalUid, int operationType)
            throws SecurityException
    {
        validateUid(userPrincipalUid);
        validateUid(rolePrincipalUid);
        String userDn = lookupByUid(userPrincipalUid);
        
        try
        {
        	rolePrincipalUid = getRoleIdAttribute() + "=" + rolePrincipalUid; 
        	
        	if (getRoleFilterBase()!=null && !getRoleFilterBase().equals(""))
        		rolePrincipalUid+="," + getRoleFilterBase();
        	rolePrincipalUid+="," + getRootContext();
            String rdn = getSubcontextName(userDn);
            Attributes attrs = new BasicAttributes(false);

            attrs.put(getUserRoleMembershipAttribute(), rolePrincipalUid);
            ctx.modifyAttributes(rdn, operationType, attrs);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    /**
     * <p>
     * Replace or delete the role attribute.
     * 
     * </p>
     * 
     * @param userPrincipalUid
     * @param rolePrincipalUid
     * @param operationType whether to replace or remove the specified user group from the user
     * @throws SecurityException A {@link SecurityException}.
     */
    private void modifyUserRoleByRole(String userPrincipalUid, String rolePrincipalUid, int operationType)
            throws SecurityException
    {
        validateUid(userPrincipalUid);
        validateUid(rolePrincipalUid);
        String userDn = "uid=" + userPrincipalUid + "," + getUserFilterBase() + "," + getRootContext();
        
        try
        {
        	rolePrincipalUid = getRoleIdAttribute() + "=" + rolePrincipalUid; 
        	
        	if (getRoleFilterBase()!=null && !getRoleFilterBase().equals(""))
        		rolePrincipalUid+="," + getRoleFilterBase();
        	
            String rdn = getSubcontextName(rolePrincipalUid);
            Attributes attrs = new BasicAttributes(false);

            attrs.put(getRoleMembershipAttribute(), userDn);
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
    public void removeRole(String userPrincipalUid, String rolePrincipalUid) throws SecurityException
    {
    	if (getUserRoleMembershipAttribute()!=null && !getUserRoleMembershipAttribute().equals(""))
    		modifyUserRoleByUser(userPrincipalUid, rolePrincipalUid, DirContext.REMOVE_ATTRIBUTE);
    	else
    		modifyUserRoleByRole(userPrincipalUid, rolePrincipalUid, DirContext.REMOVE_ATTRIBUTE);
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

        for (int i=0;i<getObjectClasses().length;i++)
        	classes.add(getObjectClasses()[i]);
        attrs.put(classes);
        attrs.put("cn", principalUid);
        attrs.put("sn", principalUid);
        attrs.put(getEntryPrefix(), principalUid);
        return attrs;
    }

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDaoImpl#getDnSuffix()
     */
    protected String getDnSuffix()
    {

        return this.getUserFilterBase();
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
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapUserPrincipalDao#addGroup(java.lang.String,
     *      java.lang.String)
     */
    public void addRoleToGroup(String groupPrincipalUid, String rolePrincipalUid) throws SecurityException
    {
    	if (getRoleGroupMembershipForRoleAttribute()!=null && !getRoleGroupMembershipForRoleAttribute().equals(""))
    		modifyRoleGroupByRole(groupPrincipalUid, rolePrincipalUid, DirContext.ADD_ATTRIBUTE);
    	else
    		modifyRoleGroupByGroup(groupPrincipalUid, rolePrincipalUid, DirContext.ADD_ATTRIBUTE);
        
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
    private void modifyRoleGroupByRole(String groupPrincipalUid, String rolePrincipalUid, int operationType)
            throws SecurityException
    {
        validateUid(groupPrincipalUid);
        validateUid(rolePrincipalUid);
        String userDn = lookupGroupByUid(groupPrincipalUid);
        try
        {
        	groupPrincipalUid = getGroupIdAttribute() + "=" + groupPrincipalUid; 
        	
        	if (getRoleFilterBase()!=null && !getRoleFilterBase().equals(""))
        		rolePrincipalUid+="," + getRoleFilterBase();
        	
            String rdn = getSubcontextName(userDn);
            Attributes attrs = new BasicAttributes(false);

            attrs.put(getRoleGroupMembershipForRoleAttribute(), rolePrincipalUid);
            ctx.modifyAttributes(rdn, operationType, attrs);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
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
    private void modifyRoleGroupByGroup(String groupPrincipalUid, String rolePrincipalUid, int operationType)
            throws SecurityException
    {
        validateUid(groupPrincipalUid);
        validateUid(rolePrincipalUid);
        String userDn = lookupGroupByUid(groupPrincipalUid);
        try
        {
        	rolePrincipalUid = getRoleIdAttribute() + "=" + rolePrincipalUid; 
        	
        	if (getRoleFilterBase()!=null && !getRoleFilterBase().equals(""))
        		rolePrincipalUid+="," + getRoleFilterBase();
        	
            String rdn = getSubcontextName(userDn);
            Attributes attrs = new BasicAttributes(false);

            attrs.put(getGroupMembershipForRoleAttribute(), rolePrincipalUid);
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
    public void removeRoleFromGroup(String groupPrincipalUid, String rolePrincipalUid) throws SecurityException
    {
        
    	if (getRoleGroupMembershipForRoleAttribute()!=null && !getRoleGroupMembershipForRoleAttribute().equals(""))
    		modifyRoleGroupByRole(groupPrincipalUid, rolePrincipalUid, DirContext.REMOVE_ATTRIBUTE);
    	else
    		modifyRoleGroupByGroup(groupPrincipalUid, rolePrincipalUid, DirContext.REMOVE_ATTRIBUTE);
        
    }        
    

	protected String getEntryPrefix() {
		return this.getUserIdAttribute();
	}

	protected String getSearchSuffix() {
		return this.getUserFilter();
	}

	    /**
	     * 
	     * Return the list of group IDs for a particular user
	     * 
	     * @param userPrincipalUid
	     * @return the array of group uids asociated with this user
	     * @throws SecurityException
	     */
	    public String[] getGroupUidsForUser(String userPrincipalUid) throws SecurityException
	    {
	        validateUid(userPrincipalUid);
	        SearchControls cons = setSearchControls();
	        try
	        {
	        	if (getUserGroupMembershipAttribute()!=null && !getUserGroupMembershipAttribute().equals("")) { 
	        		return membership.searchGroupMemberShipByUser(userPrincipalUid,cons);
	        	}
	        	return membership.searchGroupMemberShipByGroup(userPrincipalUid,cons);
	        	
	        	
	        }
	        catch (NamingException e)
	        {
	            throw new SecurityException(e);
	        }
	    }

	/**
	 * <p>
	 * Return an array of the roles that belong to a group.
	 * </p>
	 * 
	 * @param groupPrincipalUid The group principal uid.
	 * @return The array of user uids asociated with this group
	 * @throws SecurityException A {@link SecurityException}.
	 */
	public String[] getRolesForGroup(String groupPrincipalUid) throws SecurityException
	{
	    {
	        validateUid(groupPrincipalUid);
	        SearchControls cons = setSearchControls();
	        try
	        {
	        	if (getRoleGroupMembershipForRoleAttribute()!=null && !getRoleGroupMembershipForRoleAttribute().equals("")) { 
	            	return membership.searchRolesFromGroupByRole(groupPrincipalUid,cons);
	        	}
	        	return membership.searchRolesFromGroupByGroup(groupPrincipalUid,cons);
	        }
	        catch (NamingException e)
	        {
	            throw new SecurityException(e);
	        }
	    }	    
	}

	    
	    /**
	     * 
	     * Returns the role IDs for a particular user
	     * 
	     * Looks up the user, and extracts the rolemembership attr (ex : uniquemember)
	     * 
	     * @param userPrincipalUid
	     * @return the array of group uids asociated with this user
	     * @throws SecurityException
	     */
	    public String[] getRoleUidsForUser(String userPrincipalUid) throws SecurityException
	    {
	        validateUid(userPrincipalUid);
	        SearchControls cons = setSearchControls();
	        try
	        {
	        	if (getUserRoleMembershipAttribute()!=null && !getUserRoleMembershipAttribute().equals("")) { 
	            	return membership.searchRoleMemberShipByUser(userPrincipalUid,cons);
	        	}
	        	return membership.searchRoleMemberShipByRole(userPrincipalUid,cons);
	        }
	        catch (NamingException e)
	        {
	            throw new SecurityException(e);
	        }
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
	    try
	    {
	    	if (getUserGroupMembershipAttribute()!=null && !getUserGroupMembershipAttribute().equals("")) { 
	        	return membership.searchUsersFromGroupByUser(groupPrincipalUid,cons);
	    	}
	    	return membership.searchUsersFromGroupByGroup(groupPrincipalUid,cons);
	    }
	    catch (NamingException e)
	    {
	        throw new SecurityException(e);
	    }
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
	public String[] getUserUidsForRole(String rolePrincipalUid) throws SecurityException
	{
	    validateUid(rolePrincipalUid);
	    SearchControls cons = setSearchControls();
	    try
	    {
	    	if (getUserRoleMembershipAttribute()!=null && !getUserRoleMembershipAttribute().equals("")) { 
	            return membership.searchUsersFromRoleByUser(rolePrincipalUid,cons);
	    	}
	    	return membership.searchUsersFromRoleByRole(rolePrincipalUid,cons);
	    }
	    catch (NamingException e)
	    {
	        throw new SecurityException(e);
	    }
	}
	
	protected String[] getObjectClasses() {
		return this.getUserObjectClasses();
	}	
}