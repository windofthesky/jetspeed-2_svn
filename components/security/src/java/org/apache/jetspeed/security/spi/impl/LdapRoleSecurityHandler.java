package org.apache.jetspeed.security.spi.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.impl.ldap.LdapRoleDaoImpl;
import org.apache.jetspeed.security.spi.impl.ldap.LdapPrincipalDao;

public class LdapRoleSecurityHandler implements RoleSecurityHandler {

	   /** The logger. */
    private static final Log logger = LogFactory.getLog(LdapRoleSecurityHandler.class);

    /** The {@link LdapPrincipalDao}. */
    private LdapPrincipalDao ldap;

    /**
     * @param ldap The {@link LdapPrincipalDao}.
     */
    public LdapRoleSecurityHandler(LdapPrincipalDao ldap)
    {
        this.ldap = ldap;
    }

    /**
     * <p>
     * Default constructor.
     * </p>
     * 
     * @throws NamingException A {@link NamingException}.
     * @throws SecurityException A {@link SecurityException}.
     */
    public LdapRoleSecurityHandler() throws NamingException, SecurityException
    {
        this(new LdapRoleDaoImpl());
    }
	
	public RolePrincipal getRolePrincipal(String roleFullPathName) {
        String roleUidWithoutSlashes = ldap.convertUidToLdapAcceptableName(roleFullPathName);
        verifyRoleId(roleUidWithoutSlashes);
        try
        {
            String dn = ldap.lookupByUid(roleUidWithoutSlashes);

            if (!StringUtils.isEmpty(dn))
            {
                return new RolePrincipalImpl(roleFullPathName);
            }
        }
        catch (SecurityException e)
        {
            logSecurityException(e, roleFullPathName);
        }
        return null;
	}

	public void setRolePrincipal(RolePrincipal rolePrincipal) throws SecurityException {
        verifyRolePrincipal(rolePrincipal);

        String fullPath = rolePrincipal.getFullPath();
        String groupUidWithoutSlashes = ldap.convertUidToLdapAcceptableName(fullPath);
        if (getRolePrincipal(groupUidWithoutSlashes) == null)
        {
            ldap.create(groupUidWithoutSlashes);
        }
	}

	public void removeRolePrincipal(RolePrincipal rolePrincipal) throws SecurityException {
        verifyRolePrincipal(rolePrincipal);

        String fullPath = rolePrincipal.getFullPath();
        String roleUidWithoutSlashes = ldap.convertUidToLdapAcceptableName(fullPath);

        ldap.delete(roleUidWithoutSlashes);
	}

	public List getRolePrincipals(String filter) {
        try
        {
            return Arrays.asList(ldap.find(filter, RolePrincipal.PREFS_ROLE_ROOT));
        }
        catch (SecurityException e)
        {
            logSecurityException(e, filter);
        }
        return new ArrayList();
	}
	
    /**
     * <p>
     * Verify that the group uid is valid.
     * </p>
     * 
     * @param groupPrincipalUid The group uid.
     */
    private void verifyRoleId(String rolePrincipalUid)
    {
        if (StringUtils.isEmpty(rolePrincipalUid))
        {
            throw new IllegalArgumentException("The roleId cannot be null or empty.");
        }
    }

    /**
     * <p>
     * Log the security exception.
     * </p>
     * 
     * @param e The {@link SecurityException}.
     * @param groupPrincipalUid The group principal uid.
     */
    private void logSecurityException(SecurityException e, String groupPrincipalUid)
    {
        if (logger.isErrorEnabled())
        {
            logger.error("An LDAP error has occurred for groupId:" + groupPrincipalUid, e);
        }
    }
    
    /**
     * <p>
     * Verify that the group principal is valid.
     * </p>
     * 
     * @param groupPrincipal The group principal.
     */
    private void verifyRolePrincipal(RolePrincipal rolePrincipal)
    {
        if (rolePrincipal == null)
        {
            throw new IllegalArgumentException("The RolePrincipal cannot be null or empty.");
        }
    }    
}
