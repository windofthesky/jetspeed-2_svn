package org.apache.jetspeed.security.spi.impl.ldap;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
/* TODO: Java 5 dependency, needs to be resolved for Java 1.4 first before this can be enabled again
import javax.naming.ldap.LdapName;
*/

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;


public class LdapMemberShipDaoImpl extends LdapPrincipalDaoImpl implements LdapMembershipDao {

	public LdapMemberShipDaoImpl() throws SecurityException {
		super();
	}
	
	public LdapMemberShipDaoImpl(LdapBindingConfig config) throws SecurityException {
		super(config);
	}	

	/** The logger. */
    private static final Log logger = LogFactory.getLog(LdapMemberShipDaoImpl.class);

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchGroupMemberShipByGroup(java.lang.String, javax.naming.directory.SearchControls)
	 */
	public String[] searchGroupMemberShipByGroup(final String userPrincipalUid, SearchControls cons) throws NamingException {
		String subfilter = "uid=" + userPrincipalUid + "," + getUserFilterBase() + "," + getRootContext(); 
		String query = "(&(" + getGroupMembershipAttribute() + "=" + subfilter + ")" + getGroupFilter()  + ")";
		
	    if (logger.isDebugEnabled())
	    {
	        logger.debug("query[" + query + "]");
	    }

/* TODO: Java 5 dependency, needs to be resolved for Java 1.4 first before this can be enabled again
        Name name = new LdapName(getGroupFilterBase());
	    NamingEnumeration searchResults = ((DirContext) ctx).search(name,query , cons);
*/
	   List groupPrincipalUids = new ArrayList();
/*       
	    while (searchResults.hasMore())
	    {
	        SearchResult result = (SearchResult) searchResults.next();
	        Attributes answer = result.getAttributes();
	
	        groupPrincipalUids.addAll(getAttributes(getAttribute(getGroupIdAttribute(), answer)));
	    }
*/        
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
	    List uids = getAttributes(getAttribute(getUserGroupMembershipAttribute(), userAttributes));
	    return (String[]) uids.toArray(new String[uids.size()]);
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchRoleMemberShipByRole(java.lang.String, javax.naming.directory.SearchControls)
	 */
	public String[] searchRoleMemberShipByRole(final String userPrincipalUid, SearchControls cons) throws NamingException {
		String subfilter = "uid=" + userPrincipalUid + "," + getUserFilterBase() + "," + getRootContext(); 
		String query = "(&(" + getRoleMembershipAttribute() + "=" + subfilter + ")" + getRoleFilter()  + ")";
		
	    if (logger.isDebugEnabled())
	    {
	        logger.debug("query[" + query + "]");
	    }

/* TODO: Java 5 dependency, needs to be resolved for Java 1.4 first before this can be enabled again
	    Name name = new LdapName(getRoleFilterBase()) ;
	    NamingEnumeration searchResults = ((DirContext) ctx).search(name,query , cons);
*/	
	    List rolePrincipalUids = new ArrayList();
/*        
	     while (searchResults.hasMore())
	     {
	    	 
	         SearchResult result = (SearchResult) searchResults.next();
	         Attributes answer = result.getAttributes();
	
	         rolePrincipalUids.addAll(getAttributes(getAttribute(getRoleIdAttribute(), answer)));
	     }
*/         
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
		        	String cn = extractCn(cnfull);
		        	newAttrs.add(cn);
		        }
		//List uids = getAttributes(attr);
		return (String[]) newAttrs.toArray(new String[newAttrs.size()]);
	}

//	/**
//	 * <p>
//	 * Search user by group.
//	 * </p>
//	 * 
//	 * @param groupPrincipalUid
//	 * @param cons
//	 * @return
//	 * @throws NamingException A {@link NamingException}.
//	 */
//	private NamingEnumeration searchRolesByGroup(final String rolePrincipalUid, SearchControls cons)
//	        throws NamingException
//	{
//	    String query = "(&(cn=" + (rolePrincipalUid) + ")" + getRoleFilter() + ")";
//	
//	    if (logger.isDebugEnabled())
//	    {
//	        logger.debug("query[" + query + "]");
//	    }
//	    NamingEnumeration searchResults = ((DirContext) ctx).search("",query , cons);
//	
//	    return searchResults;
//	}

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
	    
	    NamingEnumeration results = ((DirContext) ctx).search("",query , cons);
		
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
	        	String uid = extractUid(uidfull);
	        	if (uidfull.indexOf(getUserFilterBase())!=-1)
	        		newAttrs.add(uid);
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
		
		String subfilter = getGroupIdAttribute() + "=" 	+  getGroupFilterBase(); 
	    if (getGroupFilterBase()!=null && !getGroupFilterBase().equals("")) subfilter+="," + getGroupFilterBase();
	    subfilter+="," + getRootContext();
		String query = "(&(" + getUserGroupMembershipAttribute() + "=" + subfilter + ")" + getUserFilter() + ")";
	    if (logger.isDebugEnabled())
	    {
	        logger.debug("query[" + query + "]");
	    }
	    NamingEnumeration results = ((DirContext) ctx).search("", query, cons);
	
	    ArrayList userPrincipalUids = new ArrayList();
	    
	    while (results.hasMore())
	    {
	        SearchResult result = (SearchResult) results.next();
	        Attributes answer = result.getAttributes();
	
	        userPrincipalUids.addAll(getAttributes(getAttribute("uid", answer)));
	    }
	    return (String[]) userPrincipalUids.toArray(new String[userPrincipalUids.size()]);
	}
	
	public String[] searchRolesFromGroupByGroup(final String groupPrincipalUid,
			SearchControls cons) throws NamingException {

		String query = "(&(" + getGroupIdAttribute() + "=" + (groupPrincipalUid) + ")" + getGroupFilter()
				+ ")";

		if (logger.isDebugEnabled()) {
			logger.debug("query[" + query + "]");
		}

		ArrayList rolePrincipalUids = new ArrayList();

		NamingEnumeration results = ((DirContext) ctx).search("", query, cons);

		while (results.hasMore()) {
			SearchResult result = (SearchResult) results.next();
			Attributes answer = result.getAttributes();

			List newAttrs = new ArrayList();

			Attribute userPrincipalUid = getAttribute(
					getGroupMembershipForRoleAttribute(), answer);
			List attrs = getAttributes(userPrincipalUid);
			Iterator it = attrs.iterator();
			while (it.hasNext()) {
				String uidfull = (String) it.next();
				String uid = extractUid(uidfull);
				if (uidfull.indexOf(getRoleFilterBase())!=-1)
					newAttrs.add(uid);
			}
			rolePrincipalUids.addAll(newAttrs);
		}
		return (String[]) rolePrincipalUids
				.toArray(new String[rolePrincipalUids.size()]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchUsersFromGroupByUser(java.lang.String,
	 *      javax.naming.directory.SearchControls)
	 */
	public String[] searchRolesFromGroupByRole(final String groupPrincipalUid,
			SearchControls cons) throws NamingException {

		String subfilter = getGroupIdAttribute() + "=" + groupPrincipalUid;
		if (getGroupFilterBase() != null && !getGroupFilterBase().equals(""))
			subfilter += "," + getGroupFilterBase() + "," + getRootContext();
		String query = "(&(" + getRoleGroupMembershipForRoleAttribute() + "="
				+ subfilter + ")" + getUserFilter() + ")";
		if (logger.isDebugEnabled()) {
			logger.debug("query[" + query + "]");
		}
		NamingEnumeration results = ((DirContext) ctx).search("", query, cons);

		ArrayList userPrincipalUids = new ArrayList();

		while (results.hasMore()) {
			SearchResult result = (SearchResult) results.next();
			Attributes answer = result.getAttributes();

			userPrincipalUids
					.addAll(getAttributes(getAttribute("uid", answer)));
		}
		return (String[]) userPrincipalUids
				.toArray(new String[userPrincipalUids.size()]);
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
	    
	    NamingEnumeration results = ((DirContext) ctx).search("",query , cons);
		
	    while (results.hasMore())
	    {
	        SearchResult result = (SearchResult) results.next();
	        Attributes answer = result.getAttributes();
	        
	        //List cUserPrincipalUid = getAttributes(getAttribute(getRoleMembershipAttribute(), answer));
	        //TODO: better implementtion
	        List newAttrs = new ArrayList();
	        
	        Attribute userPrincipalUid = getAttribute(getRoleMembershipAttribute(), answer);
	        List attrs = getAttributes(userPrincipalUid);
	        Iterator it = attrs.iterator();
	        while(it.hasNext()) {
	        	String uidfull = (String)it.next();
	        	String uid = extractUid(uidfull);
	        	newAttrs.add(uid);
	        }
	        userPrincipalUids.addAll(newAttrs);

	        
	        //userPrincipalUids.addAll(cUserPrincipalUid);
	    }
	    return (String[]) userPrincipalUids.toArray(new String[userPrincipalUids.size()]);
	}

	/* (non-Javadoc)
	 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapMembershipDao#searchUsersFromRoleByUser(java.lang.String, javax.naming.directory.SearchControls)
	 */
	public String[] searchUsersFromRoleByUser(final String rolePrincipalUid, SearchControls cons)
	throws NamingException
	{
	
		//TODO: rename params / vars !!!
		String subfilter = getRoleIdAttribute() + "=" + rolePrincipalUid; 
		if (getRoleFilterBase()!=null && !getRoleFilterBase().equals("")) subfilter+="," + getRoleFilterBase();
		subfilter+="," + getRootContext();
		String query = "(&(" + getUserRoleMembershipAttribute() + "=" + subfilter + ")" + getUserFilter() + ")";
		if (logger.isDebugEnabled())
		{
		    logger.debug("query[" + query + "]");
		}
		NamingEnumeration results = ((DirContext) ctx).search("", query, cons);
		
		ArrayList userPrincipalUids = new ArrayList();
		
		while (results.hasMore())
		{
		    SearchResult result = (SearchResult) results.next();
		    Attributes answer = result.getAttributes();
		
		    userPrincipalUids.addAll(getAttributes(getAttribute("uid", answer)));
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
    
	protected String getEntryPrefix() {
		return "uid";
	}

	protected String getSearchSuffix() {
		return this.getUserFilter();
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
	
	private String extractUid(String ldapName) {
		if (ldapName.indexOf(",")!=-1)
			return ldapName.substring(ldapName.indexOf("uid=")+4,ldapName.indexOf(","));
		return ldapName.substring(ldapName.indexOf("uid=")+4,ldapName.length());
	}
	
	private String extractCn(String ldapName) {
		if (ldapName.indexOf(",")!=-1)
			return ldapName.substring(ldapName.indexOf("cn=")+3,ldapName.indexOf(","));
		return ldapName.substring(ldapName.indexOf("cn=")+3,ldapName.length());
	}
	
	protected String[] getObjectClasses() {
		return this.getUserObjectClasses();
	}
	
	
}
