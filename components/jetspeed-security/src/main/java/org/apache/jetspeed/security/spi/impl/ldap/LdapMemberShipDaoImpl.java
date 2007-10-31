/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
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

import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

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


public class LdapMemberShipDaoImpl extends LdapPrincipalDaoImpl implements LdapMembershipDao {

	/** The logger. */
	private static final Log logger = LogFactory.getLog(LdapMemberShipDaoImpl.class);

	public LdapMemberShipDaoImpl() throws SecurityException {
		super();
	}
	
	public LdapMemberShipDaoImpl(LdapBindingConfig config) throws SecurityException {
		super(config);
	}	

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchGroupMemberShipByGroup(java.lang.String, javax.naming.directory.SearchControls)
	 */
	public String[] searchGroupMemberShipByGroup(final String userPrincipalUid, SearchControls cons) throws NamingException {
		
		String query = "(&(" + getGroupMembershipAttribute() + "=" + getUserDN(userPrincipalUid) + ")" + getGroupFilter()  + ")";
		
	    if (logger.isDebugEnabled())
	    {
	        logger.debug("query[" + query + "]");
	    }
	    
	    cons.setSearchScope(getSearchScope());
        String groupFilterBase = getGroupFilterBase();
	    NamingEnumeration searchResults = ((DirContext) ctx).search(groupFilterBase,query , cons);	    

	   List groupPrincipalUids = new ArrayList();
	    while (searchResults.hasMore())
	    {
	        SearchResult result = (SearchResult) searchResults.next();
	        Attributes answer = result.getAttributes();
	        groupPrincipalUids.addAll(getAttributes(getAttribute(getGroupIdAttribute(), answer)));
	    }
	    return (String[]) groupPrincipalUids.toArray(new String[groupPrincipalUids.size()]);
	
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchGroupMemberShipByUser(java.lang.String, javax.naming.directory.SearchControls)
	 */
	public String[] searchGroupMemberShipByUser(final String userPrincipalUid, SearchControls cons) throws NamingException {
		NamingEnumeration searchResults = searchByWildcardedUid(userPrincipalUid, cons);
	    
	    if (!searchResults.hasMore())
	    {
	        throw new NamingException("Could not find any user with uid[" + userPrincipalUid + "]");
	    }
	    
		Attributes userAttributes = getFirstUser(searchResults);
		List groupUids = new ArrayList();
		Attribute attr = getAttribute(getUserGroupMembershipAttribute(), userAttributes);
		 List attrs = getAttributes(attr);
		        Iterator it = attrs.iterator();
		        while(it.hasNext()) {
		        	String cnfull = (String)it.next();
		        	if(cnfull.toLowerCase().indexOf(getGroupFilterBase().toLowerCase())!=-1) {
			        	String cn = extractLdapAttr(cnfull,getRoleUidAttribute());
			        	groupUids.add(cn);
		        	}
		        }
	    //List uids = getAttributes(getAttribute(getUserGroupMembershipAttribute(), userAttributes),getGroupFilterBase());
	    return (String[]) groupUids.toArray(new String[groupUids.size()]);
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchRoleMemberShipByRole(java.lang.String, javax.naming.directory.SearchControls)
	 */
	public String[] searchRoleMemberShipByRole(final String userPrincipalUid, SearchControls cons) throws NamingException {

		String query = "(&(" + getRoleMembershipAttribute() + "=" + getUserDN(userPrincipalUid) + ")" + getRoleFilter()  + ")";
		
	    if (logger.isDebugEnabled())
	    {
	        logger.debug("query[" + query + "]");
	    }

	    cons.setSearchScope(getSearchScope());
	    NamingEnumeration searchResults = ((DirContext) ctx).search(getRoleFilterBase(),query , cons);
	    List rolePrincipalUids = new ArrayList();
	     while (searchResults.hasMore())
	     {
	    	 
	         SearchResult result = (SearchResult) searchResults.next();
	         Attributes answer = result.getAttributes();
	         rolePrincipalUids.addAll(getAttributes(getAttribute(getRoleIdAttribute(), answer)));
	     }
	     return (String[]) rolePrincipalUids.toArray(new String[rolePrincipalUids.size()]);
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchRoleMemberShipByUser(java.lang.String, javax.naming.directory.SearchControls)
	 */
	public String[] searchRoleMemberShipByUser(final String userPrincipalUid, SearchControls cons) throws NamingException {
	
		NamingEnumeration results = searchByWildcardedUid(userPrincipalUid, cons);
	
		if (!results.hasMore())
		{
		    throw new NamingException("Could not find any user with uid[" + userPrincipalUid + "]");
		}
		
		Attributes userAttributes = getFirstUser(results);
		List newAttrs = new ArrayList();
		Attribute attr = getAttribute(getUserRoleMembershipAttribute(), userAttributes);
		 List attrs = getAttributes(attr);
		        Iterator it = attrs.iterator();
		        while(it.hasNext()) {
		        	String cnfull = (String)it.next();
		        	if(cnfull.toLowerCase().indexOf(getRoleFilterBase().toLowerCase())!=-1) {
			        	String cn = extractLdapAttr(cnfull,getRoleUidAttribute());
			        	newAttrs.add(cn);
		        	}else{
		        		// No conversion required (I think!)
		        		String cn = cnfull;
		        		newAttrs.add(cn);
		        	}
		        }
		return (String[]) newAttrs.toArray(new String[newAttrs.size()]);
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchUsersFromGroupByGroup(java.lang.String, javax.naming.directory.SearchControls)
	 */
	public String[] searchUsersFromGroupByGroup(final String groupPrincipalUid, SearchControls cons)
	        throws NamingException
	{
	
		String query = "(&(" + getGroupIdAttribute() + "=" + (groupPrincipalUid) + ")" + getGroupFilter() + ")";
	    
		if (logger.isDebugEnabled())
	    {
	        logger.debug("query[" + query + "]");
	    }
	    
	    ArrayList userPrincipalUids=new ArrayList();
	    
	    cons.setSearchScope(getSearchScope());
	    NamingEnumeration results = ((DirContext) ctx).search(getGroupFilterBase(),query , cons);	    
		
	    while (results.hasMore())
	    {
	        SearchResult result = (SearchResult) results.next();
	        Attributes answer = result.getAttributes();
	        
	        List newAttrs = new ArrayList();
	        
	        Attribute userPrincipalUid = getAttribute(getGroupMembershipAttribute(), answer);
	        List attrs = getAttributes(userPrincipalUid);
	        Iterator it = attrs.iterator();
	        while(it.hasNext()) {
	        	String uidfull = (String)it.next();
	        	if (!StringUtils.isEmpty(uidfull)) {
		        	if (uidfull.toLowerCase().indexOf(getUserFilterBase().toLowerCase())!=-1) {
			        	String uid = extractLdapAttr(uidfull,getUserIdAttribute());
		        		newAttrs.add(uid);
		        	}
	        	}
	        }
	        userPrincipalUids.addAll(newAttrs);
	    }
	    return (String[]) userPrincipalUids.toArray(new String[userPrincipalUids.size()]);
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchUsersFromGroupByUser(java.lang.String, javax.naming.directory.SearchControls)
	 */
	public String[] searchUsersFromGroupByUser(final String groupPrincipalUid, SearchControls cons)
	        throws NamingException
	{
		
		String query = "(&(" + getUserGroupMembershipAttribute() + "=" + getGroupDN(groupPrincipalUid) + ")" + getUserFilter() + ")";
	    if (logger.isDebugEnabled())
	    {
	        logger.debug("query[" + query + "]");
	    }

	    cons.setSearchScope(getSearchScope());
	    NamingEnumeration results = ((DirContext) ctx).search(getUserFilterBase(),query , cons);	    

	    ArrayList userPrincipalUids = new ArrayList();
	    
	    while (results.hasMore())
	    {
	        SearchResult result = (SearchResult) results.next();
	        Attributes answer = result.getAttributes();
	        userPrincipalUids.addAll(getAttributes(getAttribute(getUserIdAttribute(), answer)));
	    }
	    return (String[]) userPrincipalUids.toArray(new String[userPrincipalUids.size()]);
	}
	
	public String[] searchRolesFromGroupByGroup(final String groupPrincipalUid,
			SearchControls cons) throws NamingException {

		String query = "(&(" + getGroupIdAttribute() + "=" + (groupPrincipalUid) + ")" + getGroupFilter() + ")";

		if (logger.isDebugEnabled()) {
			logger.debug("query[" + query + "]");
		}

		ArrayList rolePrincipalUids = new ArrayList();

	    cons.setSearchScope(getSearchScope());
	    NamingEnumeration groups = ((DirContext) ctx).search(getGroupFilterBase(),query , cons);	    

		while (groups.hasMore()) {
			SearchResult group = (SearchResult) groups.next();
			Attributes groupAttributes = group.getAttributes();

			Attribute rolesFromGroup = getAttribute(getGroupMembershipForRoleAttribute(), groupAttributes);
			List roleDNs = getAttributes(rolesFromGroup,getRoleFilterBase());
			Iterator it = roleDNs.iterator();
			while (it.hasNext()) {
				String roleDN = (String) it.next();
				if (!StringUtils.isEmpty(roleDN)) {
					String roleId = extractLdapAttr(roleDN,getRoleUidAttribute());
					if (roleId!=null) {
						NamingEnumeration rolesResults = searchRoleByWildcardedUid(roleId, cons);
						if (rolesResults.hasMore())
							if(rolesResults.nextElement()!=null)
								rolePrincipalUids.add(roleId);
					}
				}
			}
		}
		return (String[]) rolePrincipalUids.toArray(new String[rolePrincipalUids.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchUsersFromGroupByUser(java.lang.String,
	 *      javax.naming.directory.SearchControls)
	 */
	public String[] searchRolesFromGroupByRole(final String groupPrincipalUid,
			SearchControls cons) throws NamingException {

		String query = "(&(" + getRoleGroupMembershipForRoleAttribute() + "=" + getGroupDN(groupPrincipalUid) + ")" + getRoleFilter() + ")";
		
		if (logger.isDebugEnabled()) {
			logger.debug("query[" + query + "]");
		}
		
	    cons.setSearchScope(getSearchScope());
	    NamingEnumeration results = ((DirContext) ctx).search(getRoleFilterBase(),query , cons);	    

		ArrayList rolePrincipalUids = new ArrayList();

		while (results.hasMore()) {
			SearchResult result = (SearchResult) results.next();
			Attributes answer = result.getAttributes();
			rolePrincipalUids.addAll(getAttributes(getAttribute(getRoleIdAttribute(), answer)));
		}
		return (String[]) rolePrincipalUids
				.toArray(new String[rolePrincipalUids.size()]);
	}


	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchUsersFromRoleByRole(java.lang.String, javax.naming.directory.SearchControls)
	 */
	public String[] searchUsersFromRoleByRole(final String rolePrincipalUid, SearchControls cons)
	        throws NamingException
	{
	
		String query = "(&(" + getRoleIdAttribute() + "=" + (rolePrincipalUid) + ")" + getRoleFilter() + ")";
	    
		if (logger.isDebugEnabled())
	    {
	        logger.debug("query[" + query + "]");
	    }
	    
	    ArrayList userPrincipalUids=new ArrayList();

	    cons.setSearchScope(getSearchScope());
	    NamingEnumeration results = ((DirContext) ctx).search(getRoleFilterBase(),query , cons);	    
		
	    while (results.hasMore())
	    {
	        SearchResult result = (SearchResult) results.next();
	        Attributes answer = result.getAttributes();
	        
	        Attribute userPrincipalUid = getAttribute(getRoleMembershipAttribute(), answer);
	        List attrs = getAttributes(userPrincipalUid);
	        Iterator it = attrs.iterator();
	        while(it.hasNext()) {
	        	String uidfull = (String)it.next();
	        	if (!StringUtils.isEmpty(uidfull)) {	        	
		        	String uid = extractLdapAttr(uidfull,getUserIdAttribute());
		        	userPrincipalUids.add(uid);
	        	}
	        }
	    }
	    return (String[]) userPrincipalUids.toArray(new String[userPrincipalUids.size()]);
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchUsersFromRoleByUser(java.lang.String, javax.naming.directory.SearchControls)
	 */
	public String[] searchUsersFromRoleByUser(final String rolePrincipalUid, SearchControls cons)
	throws NamingException
	{
	
		String query = "(&(" + getUserRoleMembershipAttribute() + "=" + rolePrincipalUid + ")" + getUserFilter() + ")";
		if (logger.isDebugEnabled())
		{
		    logger.debug("query[" + query + "]");
		}
	    
		cons.setSearchScope(getSearchScope());
	    NamingEnumeration results = ((DirContext) ctx).search(getUserFilterBase(),query , cons);	    

		ArrayList userPrincipalUids = new ArrayList();
		
		while (results.hasMore())
		{
		    SearchResult result = (SearchResult) results.next();
		    Attributes answer = result.getAttributes();
		    userPrincipalUids.addAll(getAttributes(getAttribute(getUserIdAttribute(), answer)));
		}
		return (String[]) userPrincipalUids.toArray(new String[userPrincipalUids.size()]);
	}

    /**
     * @param attr
     * @return
     * @throws NamingException
     */
    protected List getAttributes(Attribute attr) throws NamingException
    {
    	return getAttributes(attr, null);
    }
    /**
     * @param attr
     * @return
     * @throws NamingException
     */
    protected List getAttributes(Attribute attr,String filter) throws NamingException
    {
        List uids = new ArrayList();
        if (attr != null)
        {
            Enumeration groupUidEnum = attr.getAll();
            while (groupUidEnum.hasMoreElements())
            {
            	String groupDN = (String)groupUidEnum.nextElement();
            	if (filter==null) {
            		uids.add(groupDN);
            	} else if (filter!=null && groupDN.toLowerCase().indexOf(filter.toLowerCase())!=-1) {
            		uids.add(groupDN);
            	}
            }
        }
        return uids;
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
	    classes.add("organizationalPerson");
	    classes.add("inetorgperson");
	    attrs.put(classes);
	    attrs.put("cn", principalUid);
	    attrs.put("sn", principalUid);
	
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
	
	private String extractLdapAttr(String dn,String ldapAttrName) {

		String dnLowerCase = dn.toLowerCase();
		String ldapAttrNameLowerCase = ldapAttrName.toLowerCase();
		
		if (dnLowerCase.indexOf(ldapAttrNameLowerCase + "=")==-1)
			return null;
		
		if (dn.indexOf(",")!=-1 && dnLowerCase.indexOf(ldapAttrNameLowerCase + "=")!=-1)
			return dn.substring(dnLowerCase.indexOf(ldapAttrNameLowerCase)+ldapAttrName.length()+1,dn.indexOf(","));
		return dn.substring(dnLowerCase.indexOf(ldapAttrNameLowerCase)+ldapAttrName.length()+1,dn.length());
	}

	protected String[] getObjectClasses() {
		return this.getUserObjectClasses();
	}
	
	protected String getUidAttributeForPrincipal() {
		return this.getUserUidAttribute();
	}

	protected String[] getAttributes() {
		return getUserAttributes();
	}

	protected String getEntryPrefix() 
    {
        return this.getUidAttribute();
	}

	protected String getSearchSuffix() {
		return this.getUserFilter();
	}	
}
