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

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * Holds the configuration for ldap binding.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class LdapBindingConfig
{
    /** The logger. */
    private static final Log logger = LogFactory.getLog(LdapBindingConfig.class);

    private String initialContextFactory;
    private String ldapSocketFactory;
    private String ldapScheme = "ldap";
    private String ldapServerName;
    private String ldapServerPort;
    private String ldapSecurityLevel = "simple";
    private String ldapSecurityProtocol;
    private String rootDn;
    private String rootPassword;
    private String rootContext;
    
    private PropertiesConfiguration props = null;

	private String groupFilter;
	private String userFilter;

	private String userRoleMembershipAttributes;

	private String groupMembershipAttributes;
	private String userGroupMembershipAttributes;

	private String defaultSearchBase;

	private String groupFilterBase;
	private String userFilterBase;
	
	private String groupIdAttribute;
	private String userIdAttribute;
	
	private String uidAttribute;
	private String memberShipSearchScope;

	private String[] groupObjectClasses;

	private String[] userObjectClasses;

	private String groupMembershipForRoleAttributes;

	private String groupUidAttribute;
	private String userUidAttribute;	
	
	private String[] groupAttributes;
	private String[] userAttributes;	
		
	private String groupObjectRequiredAttributeClasses;
	
	private String[] roleObjectClasses;
	private String roleGroupMembershipForRoleAttributes;
	private String[] roleAttributes;
	private String roleObjectRequiredAttributeClasses;
	private String roleFilter;
	private String roleFilterBase;
	private String roleIdAttribute;
	private String roleUidAttribute;
	private String roleMembershipAttributes;
	
	private String userPasswordAttribute;

	private String[] knownAttributes;

    public LdapBindingConfig()
    {
        // allow for properties setting configuration instead of through one big ugly constructor call or external properties file
    }
    
    public LdapBindingConfig(String factory, 
    		String name, 
    		String port, 
    		String context, 
    		String dn,
            String password, 
            String roleFilter,
    		String groupFilter,
    		String userFilter,
			String roleMembershipAttributes,
			String userRoleMembershipAttributes,
			String groupMembershipAttributes,
			String userGroupMembershipAttributes,
			String groupMembershipForRoleAttributes,
			String roleGroupMembershipForRoleAttributes,			
			String defaultSearchBase,
			String roleFilterBase,
			String groupFilterBase,
			String userFilterBase,
			String roleObjectClasses,
			String groupObjectClasses,
			String userObjectClasses,			
			String roleIdAttribute,
			String groupIdAttribute,
			String userIdAttribute,
			String uidAttribute,
			String memberShipSearchScope,
			String roleUidAttribute,
			String groupUidAttribute,
			String userUidAttribute,
			String roleObjectRequiredAttributeClasses,
			String groupObjectRequiredAttributeClasses,
			String userAttributes,
			String roleAttributes,
			String groupAttributes,
			String userPasswordAttribute,
			String knownAttributes)    
    {
        initialContextFactory = factory;
        ldapServerName = name;
        ldapServerPort = port;
        rootContext = context;
        rootDn = dn;
        rootPassword = password;

        this.roleFilter=roleFilter;
        this.groupFilter=groupFilter;
        this.userFilter=userFilter;
        
        this.roleMembershipAttributes=roleMembershipAttributes;
        this.userRoleMembershipAttributes=userRoleMembershipAttributes;
        
        this.groupMembershipAttributes=groupMembershipAttributes;
        this.userGroupMembershipAttributes=userGroupMembershipAttributes;
        
        this.groupMembershipForRoleAttributes=groupMembershipForRoleAttributes;
        this.roleGroupMembershipForRoleAttributes=roleGroupMembershipForRoleAttributes;
        this.defaultSearchBase=defaultSearchBase;
        
        this.roleFilterBase=roleFilterBase;
        this.groupFilterBase=groupFilterBase;
        this.userFilterBase=userFilterBase;
        
        
        this.roleObjectClasses=StringUtils.split(roleObjectClasses,",");
        this.groupObjectClasses=StringUtils.split(groupObjectClasses,",");
        this.userObjectClasses=StringUtils.split(userObjectClasses,",");
        
        this.roleIdAttribute=roleIdAttribute;
        this.groupIdAttribute=groupIdAttribute;
        this.userIdAttribute=userIdAttribute;
        
        this.uidAttribute = uidAttribute;
        this.memberShipSearchScope=memberShipSearchScope;
        

        this.roleUidAttribute=roleUidAttribute;
        this.groupUidAttribute=groupUidAttribute;
        this.userUidAttribute=userUidAttribute;             
        
        this.roleObjectRequiredAttributeClasses=roleObjectRequiredAttributeClasses;
        this.groupObjectRequiredAttributeClasses=groupObjectRequiredAttributeClasses;
        
        this.roleAttributes=StringUtils.split(roleAttributes,",");
        this.groupAttributes = StringUtils.split(groupAttributes,",");
        this.userAttributes = StringUtils.split(userAttributes,",");
        
        this.userPasswordAttribute = userPasswordAttribute;
        
        this.knownAttributes =  StringUtils.split(knownAttributes,",");
    }

    /**
     * <p>
     * Default constructor. By default instantiates LdapBindingConfig from
     * JETSPEED-INF/ldap/ldap.properties in the classpath.
     * </p>
     */
    public LdapBindingConfig(String ldapType)
    {
        try
        {
            props = new PropertiesConfiguration("JETSPEED-INF/directory/config/" + ldapType + "/ldap.properties");
            initialContextFactory = props.getString("org.apache.jetspeed.ldap.initialContextFactory");
            ldapServerName = props.getString("org.apache.jetspeed.ldap.ldapServerName");
            ldapServerPort = props.getString("org.apache.jetspeed.ldap.ldapServerPort");
            rootContext = props.getString("org.apache.jetspeed.ldap.rootContext");
            rootDn = props.getString("org.apache.jetspeed.ldap.rootDn");
            rootPassword = props.getString("org.apache.jetspeed.ldap.rootPassword");
            
            roleFilter=props.getString("org.apache.jetspeed.ldap.RoleFilter");
            groupFilter=props.getString("org.apache.jetspeed.ldap.GroupFilter");
            userFilter=props.getString("org.apache.jetspeed.ldap.UserFilter");

            roleMembershipAttributes=props.getString("org.apache.jetspeed.ldap.RoleMembershipAttributes");
            userRoleMembershipAttributes=props.getString("org.apache.jetspeed.ldap.UserRoleMembershipAttributes");

            groupMembershipAttributes=props.getString("org.apache.jetspeed.ldap.GroupMembershipAttributes");
            userGroupMembershipAttributes=props.getString("org.apache.jetspeed.ldap.UserGroupMembershipAttributes");

            groupMembershipForRoleAttributes=props.getString("org.apache.jetspeed.ldap.GroupMembershipForRoleAttributes");
            roleGroupMembershipForRoleAttributes=props.getString("org.apache.jetspeed.ldap.RoleGroupMembershipForRoleAttributes");

            
            defaultSearchBase=props.getString("org.apache.jetspeed.ldap.DefaultSearchBase");
            
            roleFilterBase=props.getString("org.apache.jetspeed.ldap.RoleFilterBase");
            groupFilterBase=props.getString("org.apache.jetspeed.ldap.GroupFilterBase");
            userFilterBase=props.getString("org.apache.jetspeed.ldap.UserFilterBase");
            
            this.roleObjectClasses=StringUtils.split(props.getString("org.apache.jetspeed.ldap.RoleObjectClasses"),",");
    		this.groupObjectClasses=StringUtils.split(props.getString("org.apache.jetspeed.ldap.GroupObjectClasses"),",");
    		this.userObjectClasses=StringUtils.split(props.getString("org.apache.jetspeed.ldap.UserObjectClasses"),",");
    		
    		roleIdAttribute=props.getString("org.apache.jetspeed.ldap.RoleIdAttribute");
            groupIdAttribute=props.getString("org.apache.jetspeed.ldap.GroupIdAttribute");
            userIdAttribute=props.getString("org.apache.jetspeed.ldap.UserIdAttribute");

            uidAttribute =props.getString("org.apache.jetspeed.ldap.UidAttribute");
            memberShipSearchScope = props.getString("org.apache.jetspeed.ldap.MemberShipSearchScope");
            
    		this.roleUidAttribute=props.getString("org.apache.jetspeed.ldap.roleUidAttribute");
    		this.groupUidAttribute=props.getString("org.apache.jetspeed.ldap.groupUidAttribute");
    		this.userUidAttribute=props.getString("org.apache.jetspeed.ldap.userUidAttribute");

    		this.roleObjectRequiredAttributeClasses=props.getString("org.apache.jetspeed.ldap.roleObjectRequiredAttributeClasses");
    		this.groupObjectRequiredAttributeClasses=props.getString("org.apache.jetspeed.ldap.groupObjectRequiredAttributeClasses");

			this.roleAttributes=StringUtils.split(props.getString("org.apache.jetspeed.ldap.roleAttributes"),",");
			this.groupAttributes=StringUtils.split(props.getString("org.apache.jetspeed.ldap.groupAttributes"),",");
			this.userAttributes=StringUtils.split(props.getString("org.apache.jetspeed.ldap.userAttributes"),",");
			this.userPasswordAttribute=props.getString("org.apache.jetspeed.ldap.userPasswordAttribute");
			
			this.knownAttributes=StringUtils.split(props.getString("org.apache.jetspeed.ldap.knownAttributes"),",");
        }
        catch (ConfigurationException ce)
        {
            logger.error("Could not configure LdapBindingConfig: " + ce);
        }
    }

    /**
     * @return Returns the initialContextFactory.
     */
    public String getInitialContextFactory()
    {
        return initialContextFactory;
    }

    /**
     * @param initialContextFactory The initialContextFactory to set.
     */
    public void setInitialContextFactory(String initialContextFactory)
    {
        this.initialContextFactory = initialContextFactory;
    }
    
    /**
     * @return the ldapScheme
     */
    public String getLdapScheme()
    {
        return ldapScheme;
    }

    /**
     * @param ldapScheme the ldapScheme to set
     */
    public void setLdapScheme(String ldapScheme)
    {
        this.ldapScheme = ldapScheme;
    }

    /**
     * @return the ldapSocketFactory
     */
    public String getLdapSocketFactory()
    {
        return ldapSocketFactory;
    }

    /**
     * @param ldapSocketFactory the ldapSocketFactory to set
     */
    public void setLdapSocketFactory(String ldapSocketFactory)
    {
        this.ldapSocketFactory = ldapSocketFactory;
    }

    /**
     * @return Returns the ldapServerName.
     */
    public String getLdapServerName()
    {
        return ldapServerName;
    }

    /**
     * @param ldapServerName The ldapServerName to set.
     */
    public void setLdapServerName(String ldapServerName)
    {
        this.ldapServerName = ldapServerName;
    }

    /**
     * @return Returns the ldapServerPort.
     */
    public String getLdapServerPort()
    {
        return ldapServerPort;
    }

    /**
     * @param ldapServerPort The ldapServerPort to set.
     */
    public void setLdapServerPort(String ldapServerPort)
    {
        this.ldapServerPort = ldapServerPort;
    }

    /**
     * @return the ldapSecurityLevel
     */
    public String getLdapSecurityLevel()
    {
        return ldapSecurityLevel;
    }

    /**
     * @param ldapSecurityLevel the ldapSecurityLevel to set
     */
    public void setLdapSecurityLevel(String ldapSecurityLevel)
    {
        this.ldapSecurityLevel = ldapSecurityLevel;
    }

    /**
     * @return the ldapSecurityProtocol
     */
    public String getLdapSecurityProtocol()
    {
        return ldapSecurityProtocol;
    }

    /**
     * @param ldapSecurityProtocol the ldapSecurityProtocol to set
     */
    public void setLdapSecurityProtocol(String ldapSecurityProtocol)
    {
        this.ldapSecurityProtocol = ldapSecurityProtocol;
    }

    /**
     * @return Returns the rootContext.
     */
    public String getRootContext()
    {
        return rootContext;
    }

    /**
     * @param rootContext The rootContext to set.
     */
    public void setRootContext(String rootContext)
    {
        this.rootContext = rootContext;
    }

    /**
     * @return Returns the rootDn.
     */
    public String getRootDn()
    {
        return rootDn;
    }

    /**
     * @param rootDn The rootDn to set.
     */
    public void setRootDn(String rootDn)
    {
        this.rootDn = rootDn;
    }

    /**
     * @return Returns the rootPassword.
     */
    public String getRootPassword()
    {
        return rootPassword;
    }

    /**
     * @param rootPassword The rootPassword to set.
     */
    public void setRootPassword(String rootPassword)
    {
        this.rootPassword = rootPassword;
    }

	public String getUserFilter() {
		return userFilter;
	}

	public void setUserFilter(String userFilter) {
		this.userFilter = userFilter;
	}

	public String getUserFilterBase() {
		return userFilterBase;
	}

	public void setUserFilterBase(String userFilterBase) {
		this.userFilterBase = userFilterBase;
	}

	public String getUserGroupMembershipAttributes() {
		return userGroupMembershipAttributes;
	}

	public void setUserGroupMembershipAttributes(
			String userGroupMembershipAttributes) {
		this.userGroupMembershipAttributes = userGroupMembershipAttributes;
	}

	public String getUserRoleMembershipAttributes() {
		return userRoleMembershipAttributes;
	}

	public void setUserRoleMembershipAttributes(String userRoleMembershipAttributes) {
		this.userRoleMembershipAttributes = userRoleMembershipAttributes;
	}

	public String getDefaultSearchBase() {
		return defaultSearchBase;
	}

	public void setDefaultSearchBase(String defaultSearchBase) {
		this.defaultSearchBase = defaultSearchBase;
	}

	public String getGroupFilter() {
		return groupFilter;
	}

	public void setGroupFilter(String groupFilter) {
		this.groupFilter = groupFilter;
	}

	public String getGroupFilterBase() {
		return groupFilterBase;
	}

	public void setGroupFilterBase(String groupFilterBase) {
		this.groupFilterBase = groupFilterBase;
	}

	public String getGroupMembershipAttributes() {
		return groupMembershipAttributes;
	}

	public void setGroupMembershipAttributes(String groupMembershipAttributes) {
		this.groupMembershipAttributes = groupMembershipAttributes;
	}

	public String getGroupIdAttribute() {
		return groupIdAttribute;
	}

	public void setGroupIdAttribute(String groupIdAttribute) {
		this.groupIdAttribute = groupIdAttribute;
	}


	public String getUserIdAttribute() {
		return userIdAttribute;
	}

	public void setUserIdAttribute(String userIdAttribute) {
		this.userIdAttribute = userIdAttribute;
	}

	public String[] getGroupObjectClasses() {
		return groupObjectClasses;
	}

	public void setGroupObjectClasses(String[] groupObjectClasses) {
		this.groupObjectClasses = groupObjectClasses;
	}



	public String[] getUserObjectClasses() {
		return userObjectClasses;
	}

	public void setUserObjectClasses(String[] userObjectClasses) {
		this.userObjectClasses = userObjectClasses;
	}


	public String getGroupMembershipForRoleAttributes() {
		return this.groupMembershipForRoleAttributes;
	}
	


	public void setGroupMembershipForRoleAttributes(String groupMembershipForRoleAttributes) {
		this.groupMembershipForRoleAttributes=groupMembershipForRoleAttributes;
	}

	public String getUidAttribute() {
		return uidAttribute;
	}

	public void setUidAttribute(String uidAttribute) {
		this.uidAttribute = uidAttribute;
	}

	public String getMemberShipSearchScope() {
		return memberShipSearchScope;
	}

	public void setMemberShipSearchScope(String memberShipSearchScope) {
		this.memberShipSearchScope = memberShipSearchScope;
	}

	public String getGroupUidAttribute() {
		return this.groupUidAttribute;
	}

	public void setGroupUidAttribute(String groupUidAttribute) {
		this.groupUidAttribute = groupUidAttribute;
	}

	public String getUserUidAttribute() {
		return this.userUidAttribute;
	}		
	
	public void setUserUidAttribute(String userUidAttribute) {
		this.userUidAttribute = userUidAttribute;
	}

	public String getGroupObjectRequiredAttributeClasses() {
		return groupObjectRequiredAttributeClasses;
	}

	public void setGroupObjectRequiredAttributeClasses(
			String groupObjectRequiredAttributeClasses) {
		this.groupObjectRequiredAttributeClasses = groupObjectRequiredAttributeClasses;
	}



	public String[] getGroupAttributes() {
		return groupAttributes;
	}

	public void setGroupAttributes(String[] groupAttributes) {
		this.groupAttributes = groupAttributes;
	}

	public String[] getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(String[] userAttributes) {
		this.userAttributes = userAttributes;
	}	
	
	public String getRoleObjectRequiredAttributeClasses() {
		return roleObjectRequiredAttributeClasses;
	}

	public void setRoleObjectRequiredAttributeClasses(
			String roleObjectRequiredAttributeClasses) {
		this.roleObjectRequiredAttributeClasses = roleObjectRequiredAttributeClasses;
	}
	
	public String[] getRoleAttributes() {
		return roleAttributes;
	}

	public void setRoleAttributes(String[] roleAttributes) {
		this.roleAttributes = roleAttributes;
	}
	
	public String[] getRoleObjectClasses() {
		return roleObjectClasses;
	}

	public void setRoleObjectClasses(String[] roleObjectClasses) {
		this.roleObjectClasses = roleObjectClasses;
	}
	

	public String getRoleGroupMembershipForRoleAttributes() {
		return this.roleGroupMembershipForRoleAttributes;
	}
	
	public void setRoleGroupMembershipForRoleAttributes(String roleGroupMembershipForRoleAttributes) {
		this.roleGroupMembershipForRoleAttributes=roleGroupMembershipForRoleAttributes;
	}

	public String getRoleFilter() {
		return roleFilter;
	}

	public void setRoleFilter(String roleFilter) {
		this.roleFilter = roleFilter;
	}

	public String getRoleFilterBase() {
		return roleFilterBase;
	}

	public void setRoleFilterBase(String roleFilterBase) {
		this.roleFilterBase = roleFilterBase;
	}

	public String getRoleMembershipAttributes() {
		return roleMembershipAttributes;
	}

	public void setRoleMembershipAttributes(String roleMembershipAttributes) {
		this.roleMembershipAttributes = roleMembershipAttributes;
	}

	public String getRoleUidAttribute() {
		return this.roleUidAttribute;
	}

	public void setRoleUidAttribute(String roleUidAttribute) {
		this.roleUidAttribute = roleUidAttribute;
	}
	

	public String getRoleIdAttribute() {
		return roleIdAttribute;
	}

	public void setRoleIdAttribute(String roleIdAttribute) {
		this.roleIdAttribute = roleIdAttribute;
	}

	public String getUserPasswordAttribute() {
		return userPasswordAttribute;
	}

	public void setUserPasswordAttribute(String userPasswordAttribute) {
		this.userPasswordAttribute = userPasswordAttribute;
	}

	public String[] getKnownAttributes() {
		return this.knownAttributes;
	}	
	
	public void setKnownAttributes(String[] knownAttributes) {
		this.knownAttributes = knownAttributes;
	}	
	
}
