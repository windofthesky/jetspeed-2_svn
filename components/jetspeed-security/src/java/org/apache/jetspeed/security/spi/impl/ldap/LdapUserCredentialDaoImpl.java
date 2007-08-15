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

import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.i18n.KeyedMessage;
import org.apache.jetspeed.security.SecurityException;

/**
 * @see org.apache.jetspeed.security.spi.impl.ldap.LdapUserCredentialDao
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>, <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class LdapUserCredentialDaoImpl extends AbstractLdapDao implements LdapUserCredentialDao
{
    /** The logger. */
    private static final Log logger = LogFactory.getLog(LdapUserCredentialDaoImpl.class);

    /** The password attribute. */ 
    
    /**
     * <p>
     * Default constructor.
     * </p>
     *
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapUserCredentialDaoImpl() throws SecurityException
    {
        super();
    }
    
    /**
     * <p>
     * Initializes the dao.
     * </p>
     * 
     * @param ldapConfig Holds the ldap binding configuration.
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapUserCredentialDaoImpl(LdapBindingConfig ldapConfig) throws SecurityException
    {
        super(ldapConfig);
    }    
    
    /**
     * <p>
     * Updates the password for the specified user.
     * </p>
     */
    public void changePassword(final String uid, final String password) throws SecurityException
    {
        validateUid(uid);
        validatePassword(password);
        logger.debug("changePassword for " + uid + " with " + password);
        String userDn = lookupByUid(uid);
        logger.debug("userDn = " + userDn);
        try
        {
            setPassword(userDn, password);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    /**
     * <p>
     * Looks up the user by the UID attribute. If this lookup succeeds, this
     * method then attempts to authenticate the user using the password,
     * throwing an AuthenticationException if the password is incorrect or an
     * OperationNotSupportedException if the password is empty.
     * </p>
     * 
     * @param uid The uid.
     * @param password The password.
     * @throws SecurityException Throws a {@link SecurityException}.
     */	
    public boolean authenticate(final String uid, final String password) throws SecurityException
    {
        validateUid(uid);
        validatePassword(password);
        try
        {
			Hashtable env = this.ctx.getEnvironment();
			//String savedPassword = String.valueOf(getPassword(uid));
			String oldCredential = (String)env.get(Context.SECURITY_CREDENTIALS);
			String oldUsername = (String)env.get(Context.SECURITY_PRINCIPAL);
						
			String dn = lookupByUid(uid);
            if ( dn == null )
				throw new SecurityException(new KeyedMessage("User " + uid + " not found"));
            
            // Build user dn using lookup value, just appending the user filter after the uid won't work when users
            // are/can be stored in a subtree (searchScope sub-tree)
            // The looked up dn though is/should always be correct, just need to append the root context.
            if (!StringUtils.isEmpty(getRootContext()))
                dn +="," + getRootContext();
			
			env.put(Context.SECURITY_PRINCIPAL,dn);
			env.put(Context.SECURITY_CREDENTIALS,password);
			new InitialContext(env);
			env.put(Context.SECURITY_PRINCIPAL,oldUsername);
			env.put(Context.SECURITY_CREDENTIALS,oldCredential);
			return true;
		}
		catch (AuthenticationException e)
		{
			return false;
		}
		catch (NamingException e)
		{
			throw new SecurityException(e);
		}
    }

    /**
     * @see org.apache.jetspeed.security.spi.impl.ldap.LdapUserCredentialDao#getPassword(java.lang.String)
     */
    public char[] getPassword(final String uid) throws SecurityException
    {
        validateUid(uid);
        try
        {
            SearchControls cons = setSearchControls();
            NamingEnumeration results = searchByWildcardedUid(uid, cons);

            return getPassword(results, uid);
        }
        catch (NamingException e)
        {
            throw new SecurityException(e);
        }
    }

    /**
     * <p>
     * Set the user's password.
     * </p>
     * 
     * @param userDn The user.
     * @param password The password.
     * @throws NamingException Throws a {@link NamingException}.
     */
    private void setPassword(final String userDn, final String password) throws NamingException
    {
    	logger.debug("setPassword userDn = " + userDn);
        String rdn = getSubcontextName(userDn);
        if (!StringUtils.isEmpty(getUserFilterBase()))
        	rdn+="," + getUserFilterBase();
        logger.debug("setPassword rdn = " + rdn);
        Attributes attrs = new BasicAttributes(false);

        attrs.put(getUserPasswordAttribute(), password);
        ctx.modifyAttributes(rdn, DirContext.REPLACE_ATTRIBUTE, attrs);
    }

    /**
     * <p>
     * Get the password.
     * </p>
     * 
     * @param results The {@link NamingEnumeration}.
     * @param uid The uid.
     * @return The password as an array of char.
     * @throws NamingException Throws a {@link NamingException}.
     */
    private char[] getPassword(final NamingEnumeration results, final String uid) throws NamingException
    {
    	if (!results.hasMore())
        {
            throw new NamingException("Could not find any user with uid[" + uid + "]");
        }

        Attributes userAttributes = getFirstUser(results);

        char[] rawPassword = convertRawPassword(getAttribute(getUserPasswordAttribute(), userAttributes));
        return rawPassword;
    }

    /**
     * <p>
     * Get the attribute.
     * </p>
     * 
     * @param attributeName The attribute name.
     * @param userAttributes The user {@link Attributes}.
     * @return The {@link Attribute}
     * @throws NamingException Throws a {@link NamingException}.
     */
    private Attribute getAttribute(String attributeName, Attributes userAttributes) throws NamingException
    {
        for (NamingEnumeration ae = userAttributes.getAll(); ae.hasMore();)
        {
            Attribute attr = (Attribute) ae.next();

            if (attr.getID().equalsIgnoreCase(attributeName))
            {
                return attr;
            }
        }
        return null;
    }

    /**
     * <p>
     * This method converts an ascii password to a char array. It needs to be
     * improved to do proper unicode conversion.
     * </p>
     * 
     * @param attr The {@link Attribute}.  
     */
    private char[] convertRawPassword(Attribute attr) throws NamingException
    {
        char[] charPass = null;
        
        if ( attr != null )
        {
            byte[] rawPass = (byte[]) attr.getAll().next();
            charPass = new char[rawPass.length];

            for (int i = 0; i < rawPass.length; i++)
            {
                // I know I lose the sign and this is only good for ascii text.
                charPass[i] = (char) rawPass[i];           
            }
        }
        else
        {
            charPass = new char[0];
        }
        return charPass;
    }

    /**
     * <p>
     * Gets the first matching user.
     * </p>
     * 
     * @param results The results to find the user in.
     * @return The Attributes.
     * @throws NamingException Throws a {@link NamingException}.
     */
    private Attributes getFirstUser(NamingEnumeration results) throws NamingException
    {
        SearchResult result = (SearchResult) results.next();
        Attributes answer = result.getAttributes();

        return answer;
    }

	protected String getEntryPrefix() {
		return this.getUserIdAttribute();
	}
	
	protected String getSearchSuffix() {
		return this.getUserFilter();
	}

	protected String getSearchDomain() {
		return this.getUserFilterBase();
	}	
	
	protected String[] getObjectClasses() {
		return this.getUserObjectClasses();
	}

	protected String[] getAttributes() {
		return this.getUserAttributes();
	}
	
}