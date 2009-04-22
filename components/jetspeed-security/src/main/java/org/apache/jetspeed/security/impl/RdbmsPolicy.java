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

import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.SecurityPolicies;

/**
 * <p>
 * Policy implementation using a relational database as persistent datastore.
 * </p>
 * <p>
 * This code was partially inspired from articles from:<br>
 * <ul>
 * <li><a href="http://www.ibm.com/developerworks/library/j-jaas/"> Extend JAAS for class
 * instance-level authorization.</a></li>
 * <li><a href="http://www.javageeks.com/Papers/JavaPolicy/index.html"> When "java.policy" Just
 * Isn't Good Enough.</li>
 * </ul>
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class RdbmsPolicy extends Policy
{
    private static final Logger log = LoggerFactory.getLogger(RdbmsPolicy.class);

    /**
     * <p>
     * JetspeedPermission Manager Service.
     * </p>
     */
    private PermissionManager pms = null;

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public RdbmsPolicy(PermissionManager pms)
    {
        if (log.isDebugEnabled())
        {
            log.debug("RdbmsPolicy constructed.");
        }
        this.pms = pms;
    }

    /**
     * @see java.security.Policy#refresh()
     */
    public void refresh()
    {
//        if (log.isDebugEnabled())
//        {
//            log.debug("RdbmsPolicy refresh called.");
//        }
    }

    /**
     * <p>
     * Check that the permission is implied for the protection domain. This will check for
     * permissions against the configured RDBMS and all {@link SecurityPolicies} configured through
     * the AuthorizationProvider.
     * </p>
     * <p>
     * The default policy is by default part of the {@link SecurityPolicies} and will only if
     * configured through assembly.
     * </p>
     * 
     * @see java.security.Policy#implies(java.security.ProtectionDomain, java.security.Permission)
     */
    public boolean implies(ProtectionDomain protectionDomain, Permission permission)
    {
        Principal[] principals = protectionDomain.getPrincipals();
        PermissionCollection perms = new Permissions();
        boolean permImplied = false;
        if ((null != principals) && (principals.length > 0))
        {
            // We need to authorize java permissions.
            // Without this check, we get a ClassCircularityError in Tomcat.
            if (permission.getClass().getName().startsWith("java"))
            {
                perms.add(new AllPermission());
            }
            else
            {
//                if (log.isDebugEnabled())
//                {
//                    log.debug("Implying permission [class, " + permission.getClass().getName() + "], " + "[name, "
//                            + permission.getName() + "], " + "[actions, " + permission.getActions() + "] for: ");
//                    log.debug("\tCodeSource:" + protectionDomain.getCodeSource().getLocation().getPath());
//                    for (int i = 0; i < principals.length; i++)
//                    {
//                        log.debug("\tPrincipal[" + i + "]: [name, " + principals[i].getName() + "], [class, "
//                                + principals[i].getClass() + "]");
//                    }
//                }
                perms = pms.getPermissions(principals);
            }
        }
        else
        {
            // No principal is returned from the subject.
            // For security check, be sure to use doAsPrivileged(theSubject, anAction, null)...
            // We grant access when no principal is associated to the subject.
            perms.add(new AllPermission());
        }
        if (null != perms)
        {
            permImplied = perms.implies(permission);
        }
        return permImplied;
    }

    /**
     * @see java.security.Policy#getPermissions(java.security.ProtectionDomain)
     */
    public PermissionCollection getPermissions(ProtectionDomain domain)
    {
        PermissionCollection otherPerms = new Permissions();
        if (null != domain)
        {
            otherPerms = getPermissions(domain.getCodeSource());
        }
        return otherPerms;
    }

    /**
     * <p>
     * The RdbmsPolicy does not protect code source per say, but will return the protected code
     * source from the other configured policies.
     * </p>
     * 
     * @see java.security.Policy#getPermissions(java.security.CodeSource)
     */
    public PermissionCollection getPermissions(CodeSource codeSource)
    {
//        if (log.isDebugEnabled())
//        {
//            log.debug("getPermissions called for '" + codeSource + "'.");
//        }
        PermissionCollection otherPerms = getOtherPoliciesPermissions(codeSource);

        return otherPerms;
    }

    /**
     * <p>
     * Gets all the permissions that should be enforced through the other policies configured.
     * </p>
     * 
     * @param codeSource The CodeSource.
     * @return A collection of permissions as a {@link PermissionCollection}
     */
    private PermissionCollection getOtherPoliciesPermissions(CodeSource codeSource)
    {
//        if (log.isDebugEnabled())
//        {
//            log.debug("Checking other policies permissions.");
//        }
        log.debug("CodeSource: " + codeSource.getLocation().getPath());

        List securityPolicies = SecurityPolicies.getInstance().getUsedPolicies();
        PermissionCollection otherPerms = new Permissions();
        for (int i = 0; i < securityPolicies.size(); i++)
        {
            Policy currPolicy = (Policy) securityPolicies.get(i);
            if (!currPolicy.getClass().equals(getClass()))
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Checking policy: " + currPolicy.getClass().getName());
                }
                PermissionCollection currPerms = currPolicy.getPermissions(codeSource);
                addPermissions(otherPerms, currPerms);
            }
        }

        // Return the default permission collection.
        return otherPerms;
    }

    /**
     * <p>
     * Adds a collection of permsToAdd to a collection of existing permissions.
     * </p>
     * 
     * @param perms The existing permissions.
     * @param permsToAdd The permissions to add.
     */
    private static void addPermissions(PermissionCollection perms, PermissionCollection permsToAdd)
    {
        int permsAdded = 0;
        if (null != permsToAdd)
        {
            Enumeration<Permission> permsToAddEnum = permsToAdd.elements();
            while (permsToAddEnum.hasMoreElements())
            {
                permsAdded++;
                Permission currPerm = permsToAddEnum.nextElement();
                perms.add(currPerm);
                if (log.isDebugEnabled())
                {
                    log.debug("Adding the permission: [class, " + currPerm.getClass().getName() + "], " + "[name, "
                            + currPerm.getName() + "], " + "[actions, " + currPerm.getActions() + "]");
                }
            }
        }
        if ((permsAdded == 0) && log.isDebugEnabled())
        {
            log.debug("No permissions to add...");
        }
    }
    
}
