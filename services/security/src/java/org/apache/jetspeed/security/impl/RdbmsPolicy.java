/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.security.impl;

import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.PermissionCollection;
import java.security.Policy;

import javax.security.auth.Subject;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.security.PermissionManagerService;

/**
 * <p>Policy implementation using a relational database as persistent datastore.</p>
 * <p>This code was partially inspired from articles from:<br>
 * <ul>
 *    <li><a href="http://www-106.ibm.com/developerworks/library/j-jaas/">
 *    Extend JAAS for class instance-level authorization.</a></li>
 *    <li><a href="http://www.javageeks.com/Papers/JavaPolicy/index.html">
 *    When "java.policy" Just Isn't Good Enough.</li>
 * </ul></p>
 * @author <a href="mailto:david@sensova.com">David Le Strat</a>
 *
 */
public class RdbmsPolicy extends Policy
{

    /** <p>Default Policy.</p> */
    private static String defaultPolicy = "sun.security.provider.PolicyFile";

    /** <p>JetspeedPermission Manager Service.</p> */
    private PermissionManagerService pms = null;

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
            pms = (PermissionManagerService) CommonPortletServices.getPortalService(PermissionManagerService.SERVICE_NAME);
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
