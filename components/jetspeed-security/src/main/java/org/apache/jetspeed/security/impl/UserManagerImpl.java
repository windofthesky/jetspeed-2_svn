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
package org.apache.jetspeed.security.impl;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.AuthenticatedUser;
import org.apache.jetspeed.security.DependentPrincipalException;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.JetspeedSubjectFactory;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PrincipalAlreadyExistsException;
import org.apache.jetspeed.security.PrincipalAssociationNotAllowedException;
import org.apache.jetspeed.security.PrincipalAssociationRequiredException;
import org.apache.jetspeed.security.PrincipalNotFoundException;
import org.apache.jetspeed.security.PrincipalNotRemovableException;
import org.apache.jetspeed.security.PrincipalReadOnlyException;
import org.apache.jetspeed.security.PrincipalUpdateException;
import org.apache.jetspeed.security.PrincipalsSet;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalPermissionStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialManager;

/**
 * <p>
 * Implementation for managing users and provides access to the {@link User}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @author <a href="mailto:vkumar@apache.org">Vivek Kumar </a>
 * @version $Id$
 */
public class UserManagerImpl extends BaseJetspeedPrincipalManager implements UserManager
{
	private static final Log log = LogFactory.getLog(UserManagerImpl.class);

	private String anonymousUser = "guest";
	private JetspeedPrincipalType roleType;
	private JetspeedPrincipalType groupType;
	
	private UserPasswordCredentialManager credentialManager;
	private RoleManager roleManager;
	private GroupManager groupManager;

	public UserManagerImpl(JetspeedPrincipalType principalType, JetspeedPrincipalType roleType, JetspeedPrincipalType groupType,
			JetspeedPrincipalAccessManager jpam, JetspeedPrincipalStorageManager jpsm, JetspeedPrincipalPermissionStorageManager jppsm,
			UserPasswordCredentialManager credentialManager, RoleManager roleManager, GroupManager groupManager) 
	{
		super(principalType, jpam, jpsm, jppsm);
		this.credentialManager = credentialManager;
		this.roleType = ((JetspeedPrincipalManager)roleManager).getPrincipalType();
		this.groupType = ((JetspeedPrincipalManager)roleManager).getPrincipalType();
	}

	public User addUser(String username) throws SecurityException
	{
	    return addUser(username, true);
	}

	public User addUser(String username, boolean mapped) throws SecurityException
	{
        User user = newUser(username, mapped);
		try
		{
            super.addPrincipal(user, null);
		}
		catch (PrincipalAlreadyExistsException e)
		{
			throw new SecurityException(SecurityException.USER_ALREADY_EXISTS.create(username));
		}
		catch (PrincipalAssociationRequiredException e)
		{
			// TODO: add SecurityException type for this?
			throw new SecurityException(SecurityException.UNEXPECTED.create("UserManager.addUser", "add", e.getMessage()));
		}
		catch (PrincipalAssociationNotAllowedException e)
		{
			throw new SecurityException(SecurityException.UNEXPECTED.create("UserManager.addUser", "add", e.getMessage()));
		}		
        catch (PrincipalNotFoundException e)
        {
            // cannot occurr as no associations are provided with addPrincipal
        }
		if (log.isDebugEnabled())
			log.debug("Added user: " + username);

        return user;
	}

	public String getAnonymousUser()
	{
		return anonymousUser;
	}

	public PasswordCredential getPasswordCredential(User user)
	{
	    if (credentialManager != null)
	    {
	        return credentialManager.getPasswordCredential(user);
	    }
        return null;
	}

	public Subject getSubject(AuthenticatedUser user) throws SecurityException
	{
        Set<Principal> principals = new PrincipalsSet();
        addSubjectPrincipals(user, principals);
        return JetspeedSubjectFactory.createSubject(user.getUser(), getPublicCredentialsForSubject(user), getPrivateCredentialsForSubject(user), principals);
	}
	
	protected Set<Object> getPublicCredentialsForSubject(AuthenticatedUser user)
	{
        HashSet<Object> credentials = new HashSet<Object>();
        if (user.getPublicCredentials() != null)
        {
            credentials.addAll(user.getPublicCredentials());
        }
        return credentials;
	}
	
    protected Set<Object> getPrivateCredentialsForSubject(AuthenticatedUser user)
    {
        HashSet<Object> credentials = new HashSet<Object>();
        if (user.getPrivateCredentials() != null)
        {
            credentials.addAll(user.getPrivateCredentials());
        }
        return credentials;
    }
    
	protected void addSubjectPrincipals(AuthenticatedUser user, Set<Principal> principals) throws SecurityException
	{
	    addSubjectRolePrincipals(user, principals, roleManager);
        addSubjectGroupPrincipals(user, principals, groupManager);
	}
	
	protected void addSubjectRolePrincipals(AuthenticatedUser user, Set<Principal> principals, RoleManager roleManager) throws SecurityException
	{
        // TODO role hierarchies ...
        principals.addAll(roleManager.getRolesForUser(user.getUserName()));
	}

    protected void addSubjectGroupPrincipals(AuthenticatedUser user, Set<Principal> principals, GroupManager groupManager) throws SecurityException
    {
        // TODO group hierarchies ...
        principals.addAll(groupManager.getGroupsForUser(user.getUserName()));
    }
	
	public User getUser(String username) throws SecurityException
	{
		return (User) getPrincipal(username);
	}

	public List<String> getUserNames(String nameFilter) throws SecurityException
	{
		return getPrincipalNames(nameFilter);
	}

	public List<User> getUsers(String nameFilter) throws SecurityException
	{
		return (List<User>)getPrincipals(nameFilter);
	}

	public List<User> getUsersInGroup(String groupFullPathName) throws SecurityException
	{
		return (List<User>) super.getAssociatedFrom(groupFullPathName, groupType, JetspeedPrincipalAssociationType.IS_PART_OF);
	}

	public List<User> getUsersInRole(String roleFullPathName) throws SecurityException
	{
		return (List<User>) super.getAssociatedFrom(roleFullPathName, roleType, JetspeedPrincipalAssociationType.IS_PART_OF);
	}

	public List<User> lookupUsers(String attributeName, String attributeValue) throws SecurityException
	{
		return (List<User>) super.getPrincipalsByAttribute(attributeName, attributeValue);
	}

	public User newTransientUser(String name)
	{
		TransientUser user = new TransientUser();
		user.setName(name);
		return user;
	}

	public User newUser(String name)
	{
		UserImpl user = new UserImpl();
		user.setName(name);
		return user;
	}

	public User newUser(String name, boolean mapped)
	{
		UserImpl user = new UserImpl();
		user.setName(name);
		user.setMapped(mapped);
		return user;
	}

	public void removeUser(String username) throws SecurityException
	{
		JetspeedPrincipal user;
		try
		{
			user = getUser(username);
			super.removePrincipal(user);
		}
		catch (PrincipalNotFoundException pnfe)
		{
			throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(username));
		}
		catch (PrincipalNotRemovableException pnre)
		{
			throw new SecurityException(SecurityException.UNEXPECTED.create(username));
		}
		catch (DependentPrincipalException dpe)
		{
			throw new SecurityException(SecurityException.UNEXPECTED.create(username));
		}
	}

	public void storePasswordCredential(PasswordCredential credential) throws SecurityException
	{
	    if (credentialManager == null)
	    {
	        throw new UnsupportedOperationException();
	    }
	    credentialManager.storePasswordCredential(credential);
	}

	public void updateUser(User user) throws SecurityException
	{	    
		try
		{
			super.updatePrincipal(user);
		}
		catch (PrincipalNotFoundException pnfe)
		{
			throw new SecurityException(SecurityException.USER_DOES_NOT_EXIST.create(user.getName()));
		}
		catch (PrincipalUpdateException pue)
		{
			throw new SecurityException(SecurityException.UNEXPECTED.create(user.getName()));
		}
        catch (PrincipalReadOnlyException e)
        {
            throw new SecurityException(e);
        }
	}

	public boolean userExists(String username)
	{
		return super.principalExists(username);
	}

	public JetspeedPrincipal newPrincipal(String name, boolean mapped)
	{
		return newUser(name, mapped);
	}

	public JetspeedPrincipal newTransientPrincipal(String name)
	{
		return newTransientPrincipal(name);
	}
}