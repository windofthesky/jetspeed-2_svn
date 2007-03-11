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
package org.apache.jetspeed.security.spi.impl.ldap;

import javax.naming.NamingException;
import javax.naming.directory.SearchControls;

public interface LdapMembershipDao {

	public abstract String[] searchGroupMemberShipByGroup(
			final String userPrincipalUid, SearchControls cons)
			throws NamingException;

	public abstract String[] searchGroupMemberShipByUser(
			final String userPrincipalUid, SearchControls cons)
			throws NamingException;

	public abstract String[] searchRoleMemberShipByRole(
			final String userPrincipalUid, SearchControls cons)
			throws NamingException;

	public abstract String[] searchRoleMemberShipByUser(
			final String userPrincipalUid, SearchControls cons)
			throws NamingException;

	/**
	 * <p>
	 * Search user by group using the GroupMembershipAttribute.
	 * </p>
	 * 
	 * @param groupPrincipalUid
	 * @param cons
	 * @return
	 * @throws NamingException A {@link NamingException}.
	 */
	public abstract String[] searchUsersFromGroupByGroup(
			final String groupPrincipalUid, SearchControls cons)
			throws NamingException;

	/**
	 * <p>
	 * Search user by group using the UserGroupMembershipAttribute.
	 * </p>
	 * 
	 * @param groupPrincipalUid
	 * @param cons
	 * @return
	 * @throws NamingException A {@link NamingException}.
	 */
	public abstract String[] searchUsersFromGroupByUser(
			final String groupPrincipalUid, SearchControls cons)
			throws NamingException;

	/**
	 * <p>
	 * Search user by role using the RoleMembershipAttribute.
	 * </p>
	 * 
	 * @param groupPrincipalUid
	 * @param cons
	 * @return
	 * @throws NamingException A {@link NamingException}.
	 */
	public abstract String[] searchUsersFromRoleByRole(
			final String rolePrincipalUid, SearchControls cons)
			throws NamingException;

	/**
	 * <p>
	 * Search user by role using the UserRoleMembershipAttribute.
	 * </p>
	 * 
	 * @param groupPrincipalUid
	 * @param cons
	 * @return
	 * @throws NamingException A {@link NamingException}.
	 */
	public abstract String[] searchUsersFromRoleByUser(
			final String groupPrincipalUid, SearchControls cons)
			throws NamingException;
	
	public abstract String[] searchRolesFromGroupByGroup(final String groupPrincipalUid,
			SearchControls cons) throws NamingException;

	public abstract String[] searchRolesFromGroupByRole(final String groupPrincipalUid,
			SearchControls cons) throws NamingException;

}
