/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PermissionCollection;
import java.security.Policy;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.PermissionManager;

/**
 * <p>Policy implementation using a relational database as persistent datastore.</p>
 * <p>This code was partially inspired from articles from:<br>
 * <ul>
 *    <li><a href="http://www-106.ibm.com/developerworks/library/j-jaas/">
 *    Extend JAAS for class instance-level authorization.</a></li>
 *    <li><a href="http://www.javageeks.com/Papers/JavaPolicy/index.html">
 *    When "java.policy" Just Isn't Good Enough.</li>
 * </ul></p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 *
 */
public class RdbmsPolicy extends Policy
{

    /** <p>Default Policy.</p> */
    private static String defaultPolicy = "sun.security.provider.PolicyFile";

    /** <p>JetspeedPermission Manager Service.</p> */
    private PermissionManager pms = null;

    /**
     * <p>Default constructor.</p>
     */
    public RdbmsPolicy()
    {
        System.out.println("\t\t[RdbmsPolicy] Policy constructed.");
    }

    /**
     * <p>Returns the {@link PermissionManagerService}.</p>
     * TODO This should be improved.
     */
    protected void getPermissionManagerService()
    {
        if (pms == null)
        {
            //pms = (PermissionManager) CommonPortletServices.getPortalService(PermissionManager.SERVICE_NAME);
        }
    }

    /**
     * @see java.security.Policy#refresh()
     */
    public void refresh()
    {
        System.out.println("\t\t[RdbmsPolicy] Refresh called.");
    }

    /**
     * <p>Get permissions will check for permissions against the configured
     * RDBMS.  If no permissions is found for the {@link AccessControlContext}
     * {@link Subject} principals or if the {@link Subject} is null, the default
     * policy will be used.</p>
     * <p>The default policy defaults to {@link sun.security.provider.PolicyFile}.
     * If the system uses a different <code>policy.provider</code>, the default policy
     * should be set using <code>RdbmsPolicy.setDefaultPolicy()</code> when the
     * application start/initializes.</p>
     * @param codeSource The codeSource.
     * @see java.security.Policy#getPermissions(java.security.CodeSource)
     */
    public PermissionCollection getPermissions(CodeSource codeSource)
    {
        System.out.println("\t\t[RdbmsPolicy] getPermissions called for '" + codeSource + "'.");

        Permissions perms = null;

        // In the policy, the Subject should come from the context?
        AccessControlContext context = AccessController.getContext();
        Subject user = Subject.getSubject(context);
        if (null != user)
        {
            // Add permission associated with the Subject Principals to Permissions.
            // Get the permissions
            getPermissionManagerService();
            perms = pms.getPermissions(user.getPrincipals());
        }
        if (null != perms)
        {         
            return perms;
        }
        else
        {
            // TODO Is there a better way to do this?
            // If the permission is not found here then delegate it
            // to the standard java Policy class instance.
            Policy.setPolicy(RdbmsPolicy.getDefaultPolicy());
            Policy policy = Policy.getPolicy();
            policy.refresh();
            PermissionCollection defaultPerms = policy.getPermissions(codeSource);
            // Revert back to the current policy.
            Policy.setPolicy(this);
            // Return the default permission collection.
            return defaultPerms;
        }
    }

    /**
     * <p>Utility method to set the default policy when initializing the application
     * using the security service.</p>
     * <p>This can be useful if the default java policy is not {@link sun.security.provider.PolicyFile}.
     * This way the <code>RdbmsPolicy</code> will check credentials against the default policy if no permissions
     * are returned when checking againt the <code>RdbmsPolicy</code>.</p>
     * @param policy The default policy.
     */
    public static void setDefaultPolicy(Policy policy)
    {
        RdbmsPolicy.defaultPolicy = policy.getClass().getName();
    }

    /**
     * <p>Utility method to get the system default policy.</p>
     * <p>This can be useful if the default java policy is not {@link sun.security.provider.PolicyFile}.
     * This way the <code>RdbmsPolicy</code> will check credentials against the default policy if no permissions
     * are returned when checking againt the <code>RdbmsPolicy</code>.</p>
     * @return The default policy.
     */
    public static Policy getDefaultPolicy()
    {
        try
        {
            Class policyClass = Class.forName(RdbmsPolicy.defaultPolicy);
            return (Policy) policyClass.newInstance();
        }
        catch (ClassNotFoundException cnfe)
        {
            cnfe.printStackTrace();
        }
        catch (InstantiationException ie)
        {
            ie.printStackTrace();
        }
        catch (IllegalAccessException iae)
        {
            iae.printStackTrace();
        }

        return null;
    }

}
