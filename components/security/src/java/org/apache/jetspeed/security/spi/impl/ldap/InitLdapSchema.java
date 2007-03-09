/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.security.SecurityException;

public class InitLdapSchema extends AbstractLdapDao
{

    /**
     * <p>
     * Default constructor.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public InitLdapSchema() throws SecurityException
    {
        super();
    }

    /**
     * <p>
     * Initializes the LDAP schema.
     * </p>
     * 
     * @param ldapConfig Holds the ldap binding configuration.
     * @throws SecurityException A {@link SecurityException}.
     */
    public InitLdapSchema(LdapBindingConfig ldapConfig) throws SecurityException
    {
        super(ldapConfig);
    }

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.AbstractLdapDao#getObjectClass()
     */
    protected String getObjectClass()
    {
        // Implementation not required for initializing the ldap schema.
        return null;
    }

    /**
     * <p>
     * Inits a given ou.
     * </p>
     * 
     * @param ou The org unit.
     * @throws SecurityException
     */
    public void initOu(String ou) throws NamingException
    {
        if (!StringUtils.isEmpty(ou))
        {
            Attributes attrs = defineLdapAttributes(ou);
            String dn = "ou=" + ou; // + "," + getDefaultSearchBase();
            ctx.createSubcontext(dn, attrs);
        }
    }
    
    public void initOu(String ou,String folder) throws NamingException
    {
        if (!StringUtils.isEmpty(ou))
        {
            Attributes attrs = defineLdapAttributes(ou);
            ctx.createSubcontext("ou=" + ou + "," + folder, attrs);
        }
    }    

    /**
     * <p>
     * A template method for defining the attributes for a particular LDAP class.
     * </p>
     * 
     * @param principalUid The principal uid.
     * @return the LDAP attributes object for the particular class.
     */
    protected Attributes defineLdapAttributes(String ou)
    {
        Attributes attrs = new BasicAttributes(true);
        BasicAttribute classes = new BasicAttribute("objectclass");

        classes.add("top");
        classes.add("organizationalUnit");
        attrs.put(classes);
        attrs.put("ou", ou);

        return attrs;
    }

	protected String getEntryPrefix()
	{
		return null;
	}
	
	protected String getSearchSuffix() {
		return null;
	}

	protected String getSearchDomain() {
		return null;
	}

	protected String[] getObjectClasses() {
		return null;
	}

    protected String[] getAttributes() {
        return null;
    }

}
