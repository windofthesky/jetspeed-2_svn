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

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;

/**
 * <p>
 * DAO for handling group objects.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 */
public class LdapGroupDaoImpl extends LdapPrincipalDaoImpl
{
    /** The logger. */
    private static final Log LOG = LogFactory.getLog(LdapGroupDaoImpl.class);

    /** The group class name. */
    private static final String GROUP_CLASSNAME = "com.jetspeed.dung.GroupClassImpl";

    /**
     * <p>
     * Default constructor.
     * </p>
     * 
     * @throws NamingException A {@link NamingException}.
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapGroupDaoImpl() throws SecurityException, NamingException
    {
    }

    /**
     * <p>
     * A template method for defining the attributes for a particular LDAP
     * class.
     * </p>
     * 
     * @param principalUid The principal uid.
     * @return The LDAP attributes object for the particular class.
     */
    protected Attributes defineLdapAttributes(final String principalUid)
    {
        Attributes attrs = new BasicAttributes(true);
        BasicAttribute classes = new BasicAttribute("objectclass");

        classes.add("top");
        classes.add("uidObject");
        classes.add("jetspeed-2-group");
        attrs.put(classes);
        attrs.put("j2-classname", GROUP_CLASSNAME);
        attrs.put("uid", principalUid);
        return attrs;
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
        return new GroupPrincipalImpl(convertUidFromLdapAcceptableName(principalUid));
    }

    /**
     * <p>
     * A template method that returns the LDAP object class of the concrete DAO.
     * </p>
     * 
     * @return A String containing the LDAP object class name.
     */
    protected String getObjectClass()
    {
        return "jetspeed-2-group";
    }
}