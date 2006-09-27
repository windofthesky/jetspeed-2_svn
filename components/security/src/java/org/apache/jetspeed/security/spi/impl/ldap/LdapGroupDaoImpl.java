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

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;

/**
 * <p>
 * DAO for handling group objects.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a
 *         href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class LdapGroupDaoImpl extends LdapPrincipalDaoImpl
{

	 /**
     * <p>
     * Default constructor.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapGroupDaoImpl() throws SecurityException
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
    public LdapGroupDaoImpl(LdapBindingConfig ldapConfig) throws SecurityException
    {
        super(ldapConfig);
    }

    /**
     * <p>
     * A template method for defining the attributes for a particular LDAP class.
     * </p>
     * 
     * @param principalUid The principal uid.
     * @return The LDAP attributes object for the particular class.
     */
    protected Attributes defineLdapAttributes(final String principalUid)
    {
        Attributes attrs = new BasicAttributes(true);
        BasicAttribute classes = new BasicAttribute("objectclass");

        for (int i=0 ; i<getObjectClasses().length ; i++) 
        	classes.add(getObjectClasses()[i]);
        attrs.put(classes);
        attrs.put(getEntryPrefix(), principalUid);
        return attrs;
    }

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDaoImpl#getDnSuffix()
     */
    protected String getDnSuffix()
    {
       return getGroupFilterBase();
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
        return new GroupPrincipalImpl(principalUid);
    }


	protected String getEntryPrefix() {
		return this.getGroupIdAttribute();
	}
	
	protected String getSearchSuffix() {
		return this.getGroupFilter();
	}

	protected String getSearchDomain() {
		return this.getGroupFilterBase();
	}

	protected String[] getObjectClasses() {
		return this.getGroupObjectClasses();
	}
	
 	
}