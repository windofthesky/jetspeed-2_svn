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
import org.apache.jetspeed.security.SecurityException;

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
    private String ldapServerName;
    private String ldapServerPort;
    private String rootDn;
    private String rootPassword;
    private String rootContext;
    
    private String defaultDnSuffix;
    
    private PropertiesConfiguration props = null;

	private String roleFilter;
	private String groupFilter;
	private String userFilter;

	private String userAuthenticationFiler;
	
	private String roleMembershipAttributes;
	private String userRoleMembershipAttributes;

	private String groupMembershipAttributes;
	private String userGroupMembershipAttributes;

	private String defaultSearchBase;

	private String roleFilterBase;
	private String groupFilterBase;
	private String userFilterBase;
	
	private String roleIdAttribute;
	private String groupIdAttribute;
	private String userIdAttribute;

	private String[] roleObjectClasses;

	private String[] groupObjectClasses;

	private String[] userObjectClasses;

	private String roleGroupMembershipForRoleAttributes;

	private String groupMembershipForRoleAttributes;	

    /**
     * @param factory The initial context factory.
     * @param name The ldap server name.
     * @param port The ldap server port.
     * @param suffix The default dn suffix.
     * @param context The root context.
     * @param dn The root dn.
     * @param password The root password.
     * @param uou The users organization unit.
     * @param gou The groups organization unit.
     */
    public LdapBindingConfig(String factory, 
    		String name, 
    		String port, 
    		String suffix, 
    		String context, 
    		String dn,
            String password, 
            String roleFilter,
    		String groupFilter,
    		String userFilter,
			String userAuthenticationFiler,
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
			String userIdAttribute)    
    {
        try
        {
            initialContextFactory = factory;
            ldapServerName = name;
            ldapServerPort = port;
            defaultDnSuffix = suffix;
            rootContext = context;
            rootDn = dn;
            rootPassword = password;
    
            this.roleFilter=roleFilter;
    		this.groupFilter=groupFilter;
    		this.userFilter=userFilter;
    		this.userAuthenticationFiler=userAuthenticationFiler;
			
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
    		
            new InitLdapSchema(this);
        }
        catch (SecurityException se)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("The LDAP directory should already be initialized.  If this is not the case, an exception"
                        + "occured during initialization.");
            }
        }
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
            props = new PropertiesConfiguration("JETSPEED-INF/ldap/" + ldapType + "/ldap.properties");
            initialContextFactory = props.getString("org.apache.jetspeed.ldap.initialContextFactory");
            ldapServerName = props.getString("org.apache.jetspeed.ldap.ldapServerName");
            ldapServerPort = props.getString("org.apache.jetspeed.ldap.ldapServerPort");
            defaultDnSuffix = props.getString("org.apache.jetspeed.ldap.defaultDnSuffix");
            rootContext = props.getString("org.apache.jetspeed.ldap.rootContext");
            rootDn = props.getString("org.apache.jetspeed.ldap.rootDn");
            rootPassword = props.getString("org.apache.jetspeed.ldap.rootPassword");
            
            roleFilter=props.getString("org.apache.jetspeed.ldap.RoleFilter");
            groupFilter=props.getString("org.apache.jetspeed.ldap.GroupFilter");
            userFilter=props.getString("org.apache.jetspeed.ldap.UserFilter");

            userAuthenticationFiler=props.getString("org.apache.jetspeed.ldap.UserAuthenticationFiler");

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

            new InitLdapSchema(this);
        }
        catch (ConfigurationException ce)
        {
            logger.error("Could not configure LdapBindingConfig: " + ce);
        }
        catch (SecurityException se)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("The LDAP directory should already be initialized.  If this is not the case, an exception"
                        + "occured during initialization.");
            }
        }
    }

    /**
     * @return Returns the defaultDnSuffix.
     */
    public String getDefaultDnSuffix()
    {
        return defaultDnSuffix;
    }

    /**
     * @param defaultDnSuffix The defaultDnSuffix to set.
     */
    public void setDefaultDnSuffix(String defaultDnSuffix)
    {
        this.defaultDnSuffix = defaultDnSuffix;
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

	public String getUserAuthenticationFiler() {
		return userAuthenticationFiler;
	}

	public void setUserAuthenticationFiler(String userAuthenticationFiler) {
		this.userAuthenticationFiler = userAuthenticationFiler;
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

	public String getRoleIdAttribute() {
		return roleIdAttribute;
	}

	public void setRoleIdAttribute(String roleIdAttribute) {
		this.roleIdAttribute = roleIdAttribute;
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

	public String[] getRoleObjectClasses() {
		return roleObjectClasses;
	}

	public void setRoleObjectClasses(String[] roleObjectClasses) {
		this.roleObjectClasses = roleObjectClasses;
	}

	public String[] getUserObjectClasses() {
		return userObjectClasses;
	}

	public void setUserObjectClasses(String[] userObjectClasses) {
		this.userObjectClasses = userObjectClasses;
	}

	public String getRoleGroupMembershipForRoleAttributes() {
		return this.roleGroupMembershipForRoleAttributes;
	}

	public String getGroupMembershipForRoleAttributes() {
		return this.groupMembershipForRoleAttributes;
	}
	
	public void setRoleGroupMembershipForRoleAttributes(String roleGroupMembershipForRoleAttributes) {
		this.roleGroupMembershipForRoleAttributes=roleGroupMembershipForRoleAttributes;
	}

	public void setGroupMembershipForRoleAttributes(String groupMembershipForRoleAttributes) {
		this.groupMembershipForRoleAttributes=groupMembershipForRoleAttributes;
	}	
	
}
