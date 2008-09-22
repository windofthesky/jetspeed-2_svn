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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.AuthenticatedUser;
import org.apache.jetspeed.security.AuthenticatedUserImpl;
import org.apache.jetspeed.security.DependentPrincipalException;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.JetspeedSubjectFactory;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PrincipalAlreadyExistsException;
import org.apache.jetspeed.security.PrincipalAssociationNotAllowedException;
import org.apache.jetspeed.security.PrincipalAssociationRequiredException;
import org.apache.jetspeed.security.PrincipalAssociationUnsupportedException;
import org.apache.jetspeed.security.PrincipalNotFoundException;
import org.apache.jetspeed.security.PrincipalNotRemovableException;
import org.apache.jetspeed.security.PrincipalReadOnlyException;
import org.apache.jetspeed.security.PrincipalUpdateException;
import org.apache.jetspeed.security.PrincipalsSet;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserCredential;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;
import org.apache.jetspeed.security.spi.UserPasswordCredentialManager;
import org.apache.jetspeed.security.spi.UserSubjectPrincipalsProvider;
import org.apache.jetspeed.security.spi.UserSubjectPrincipalsResolver;

/**
 * <p>
 * Implementation for managing users and provides access to the {@link User}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @author <a href="mailto:vkumar@apache.org">Vivek Kumar </a>
 * @version $Id$
 */
public class UserManagerImpl extends BaseJetspeedPrincipalManager implements UserManager, UserSubjectPrincipalsProvider
{
	private static final Log log = LogFactory.getLog(UserManagerImpl.class);

	private String anonymousUser = "guest";
	private JetspeedPrincipalType roleType;
	private JetspeedPrincipalType groupType;

	private UserPasswordCredentialManager credentialManager;
	private RoleManager roleManager;
	private GroupManager groupManager;
	private Map<String, UserSubjectPrincipalsResolver> usprMap = new HashMap<String, UserSubjectPrincipalsResolver>();

	public UserManagerImpl(JetspeedPrincipalType principalType, JetspeedPrincipalType roleType, JetspeedPrincipalType groupType,
			JetspeedPrincipalAccessManager jpam, JetspeedPrincipalStorageManager jpsm, UserPasswordCredentialManager credentialManager)
	{
		super(principalType, jpam, jpsm);
		this.credentialManager = credentialManager;
		this.roleType = roleType;
		this.groupType = groupType;
	}

	public void checkInitialized()
	{
		if (groupManager == null)
		{
			groupManager = (GroupManager) getPrincipalManagerProvider().getManager(groupType);
		}
		if (roleManager == null)
		{
			roleManager = (RoleManager) getPrincipalManagerProvider().getManager(roleType);
		}
	}

	public User addUser(String username) throws SecurityException
	{
		return addUser(username, true);
	}

	public User addUser(String username, boolean mapped) throws SecurityException
	{
		User user = newUser(username, mapped);
		
		super.addPrincipal(user, null);

		if (log.isDebugEnabled())
			log.debug("Added user: " + username);

		return user;
	}

	public String getAnonymousUser()
	{
		return anonymousUser;
	}

	public PasswordCredential getPasswordCredential(User user) throws SecurityException
	{
		if (credentialManager != null)
		{
			return credentialManager.getPasswordCredential(user);
		}
		return null;
	}

	public Subject getSubject(User user) throws SecurityException
	{
		if (credentialManager != null)
		{
			PasswordCredential pwc = getPasswordCredential(user);
			if (pwc != null)
			{
				UserCredential credential = new UserCredentialImpl(pwc);
				HashSet<Object> privateCred = new HashSet<Object>();
				privateCred.add(credential);
				return getSubject(new AuthenticatedUserImpl(user, null, privateCred));
			}
		}
		return getSubject(new AuthenticatedUserImpl(user, null, null));
	}

	public Subject getSubject(AuthenticatedUser user) throws SecurityException
	{
		Set<Principal> principals = new PrincipalsSet();
		resolveSubjectPrincipals(user, principals);
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

	protected void resolveSubjectPrincipals(AuthenticatedUser user, Set<Principal> principals) throws SecurityException
	{
		checkInitialized();
		HashSet<Long> resolvedIds = new HashSet<Long>();
		for (UserSubjectPrincipalsResolver resolver : usprMap.values())
		{
		    resolver.resolve(user.getUser(), resolvedIds, principals, usprMap);
		}
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
		return (List<User>) getPrincipals(nameFilter);
	}

	public List<User> getUsersInGroup(String groupFullPathName) throws SecurityException
	{
		return (List<User>) super.getAssociatedTo(groupFullPathName, groupType, JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME);
	}

	public List<User> getUsersInRole(String roleFullPathName) throws SecurityException
	{
		return (List<User>) super.getAssociatedTo(roleFullPathName, roleType, JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME);
	}

	public List<User> lookupUsers(String attributeName, String attributeValue) throws SecurityException
	{
		return (List<User>) super.getPrincipalsByAttribute(attributeName, attributeValue);
	}

	public User newTransientUser(String name)
	{
		TransientUser user = new TransientUser(name);
		return user;
	}

	public User newUser(String name)
	{
		UserImpl user = new UserImpl(name);
		return user;
	}

	public User newUser(String name, boolean mapped)
	{
		UserImpl user = new UserImpl(name);
		user.setMapped(mapped);
		return user;
	}

	public void removeUser(String username) throws SecurityException
	{
		JetspeedPrincipal user;
		
		user = getUser(username);
		super.removePrincipal(user);
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
		super.updatePrincipal(user);
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

    public void addSubjectPrincipalsResolver(UserSubjectPrincipalsResolver resolver)
    {
        this.usprMap.put(resolver.getPrincipalType().getName(), resolver);
    }
}