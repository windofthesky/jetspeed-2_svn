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
