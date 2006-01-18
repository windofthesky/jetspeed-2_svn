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

    /** The initial context factory for the LDAP provider. */
    private String initialContextFactory;

    /** The server name. */
    private String ldapServerName;

    /** The server port. */
    private String ldapServerPort;

    /** The root distinguished name. */
    private String rootDn;

    /** The root password. */
    private String rootPassword;

    /** The root context. */
    private String rootContext;

    /** The default suffix. */
    private String defaultDnSuffix;

    /** The users ou. */
    private String usersOu;

    /** The groups ou. */
    private String groupsOu;
    
    /** The roles ou. */
    private String rolesOu;    

    /** The ldap properties. */
    private PropertiesConfiguration props = null;

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
    public LdapBindingConfig(String factory, String name, String port, String suffix, String context, String dn,
            String password, String uou, String goups,String roles)
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
            usersOu = uou;
            groupsOu = goups;
            rolesOu = roles;
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
    public LdapBindingConfig()
    {
        try
        {
            props = new PropertiesConfiguration("JETSPEED-INF/ldap/ldap.properties");
            initialContextFactory = props.getString("org.apache.jetspeed.ldap.initialContextFactory");
            ldapServerName = props.getString("org.apache.jetspeed.ldap.ldapServerName");
            ldapServerPort = props.getString("org.apache.jetspeed.ldap.ldapServerPort");
            defaultDnSuffix = props.getString("org.apache.jetspeed.ldap.defaultDnSuffix");
            rootContext = props.getString("org.apache.jetspeed.ldap.rootContext");
            rootDn = props.getString("org.apache.jetspeed.ldap.rootDn");
            rootPassword = props.getString("org.apache.jetspeed.ldap.rootPassword");
            usersOu = props.getString("org.apache.jetspeed.ldap.ou.users");
            groupsOu = props.getString("org.apache.jetspeed.ldap.ou.groups");
            rolesOu = props.getString("org.apache.jetspeed.ldap.ou.roles");
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
     * @return Returns the groupsOu.
     */
    public String getGroupsOu()
    {
        return groupsOu;
    }

    /**
     * @param groupsOu The groupsOu to set.
     */
    public void setGroupsOu(String groupsOu)
    {
        this.groupsOu = groupsOu;
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

    /**
     * @return Returns the usersOu.
     */
    public String getUsersOu()
    {
        return usersOu;
    }

    /**
     * @param usersOu The usersOu to set.
     */
    public void setUsersOu(String usersOu)
    {
        this.usersOu = usersOu;
    }

	public String getRolesOu() {
		return rolesOu;
	}

	public void setRolesOu(String rolesOu) {
		this.rolesOu = rolesOu;
	}
}
