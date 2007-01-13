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
package org.apache.jetspeed.security;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Policy;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.impl.PassiveCallbackHandler;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;

/**
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestRdbmsPolicy extends AbstractSecurityTestcase
{
    /**
     * <p>
     * The JAAS login context.
     * </p>
     */
    private LoginContext loginContext = null;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        initUser();

        // Let's login in.
        try
        {
            System.out.println("\t\t[TestRdbmsPolicy] Creating login context.");
            PassiveCallbackHandler pch = new PassiveCallbackHandler("anon", "password");
            loginContext = new LoginContext("Jetspeed", pch);
            loginContext.login();
        }
        catch (LoginException le)
        {
            le.printStackTrace();
            assertTrue("\t\t[TestRdbmsPolicy] Failed to setup test.", false);
        }

    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {

        // Logout.
        try
        {
            loginContext.logout();
        }
        catch (LoginException le)
        {
            le.printStackTrace();
            assertTrue("\t\t[TestRdbmsPolicy] Failed to tear down test.", false);
        }
        destroyUser();
        super.tearDown();
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestRdbmsPolicy.class);
    }

    /**
     * <p>
     * Executing this test requires adding an entry to java.policy.
     * </p>
     * <p>
     * A possible entry would be to grant for all principals:
     * </p>
     * 
     * <pre><code>
     *  grant
     *  {
     *      permission org.apache.jetspeed.security.auth.PortletPermission &quot;myportlet&quot;, &quot;view&quot;;
     *  }
     * </code></pre>
     * 
     * <p>
     * Such an entry would also test the Rdbms defaulting behavior if no entry is provided in the
     * database for the tested Subject InternalUserPrincipal.
     * </p>
     */
    /*public void testPermissionWithSubjectInContructor()
    {
        // InternalPermission should be granted.
        PortletPermission perm1 = new PortletPermission("myportlet", "view", loginContext.getSubject());
        try
        {
            AccessController.checkPermission(perm1);
        }
        catch (AccessControlException ace)
        {
            assertTrue("did not authorize view permission on the portlet.", false);
        }

        // InternalPermission should be denied.
        PortletPermission perm2 = new PortletPermission("myportlet", "edit", loginContext.getSubject());
        try
        {
            AccessController.checkPermission(perm2);
            assertTrue("did not deny edit permission on the portlet.", false);
        }
        catch (AccessControlException ace)
        {
        }

        // Subject is omitted. InternalPermission should be denied.
        PortletPermission perm3 = new PortletPermission("myportlet", "view");
        try
        {
            AccessController.checkPermission(perm3);
            // assertTrue("did not deny permission with no subject passed.", false);
        }
        catch (AccessControlException ace)
        {
        }
    }*/
    
    /**
     * <p>
     * Test the policy with the default spring setting where the default underlying policy is not
     * applied.
     * </p>
     */
    public void testPermissionWithSubjectInAccessControlContext()
    {
        
        // InternalPermission should be granted.
        try
        {
            JSSubject.doAsPrivileged(loginContext.getSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    PortletPermission perm1 = new PortletPermission("myportlet", "view");
                    System.out.println("\t\t[TestRdbmsPolicy] Check access control for permission: [myportlet, view]");
                    System.out.println("\t\t                  with policy: " + Policy.getPolicy().getClass().getName());
                    AccessController.checkPermission(perm1);
                    return null;
                }
            }, null);
        }
        catch (AccessControlException ace)
        {
            assertTrue("did not authorize view permission on the portlet.", false);
        }

        // Should be denied.
        try
        {
            JSSubject.doAsPrivileged(loginContext.getSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    PortletPermission perm2 = new PortletPermission("myportlet", "secure");
                    System.out.println("\t\t[TestRdbmsPolicy] Check access control for permission: [myportlet, secure]");
                    System.out.println("\t\t                  with policy: " + Policy.getPolicy().getClass().getName());
                    AccessController.checkPermission(perm2);
                    return null;
                }
            }, null);
            assertTrue("did not deny secure permission on the portlet.", false);
        }
        catch (AccessControlException ace)
        {
        }
    }
    
    /**
     * <p>
     * Test the policy with the default policy being evaluated as well.
     * </p>
     */
    public void testPermissionWithSubjectInAccessControlContextAndDefaultPolicy()
    {
        System.out.println("\n\n\t\t[TestRdbmsPolicy] Test with default Policy enabled.");
        AuthorizationProvider atzProvider = (AuthorizationProvider) ctx.getBean("org.apache.jetspeed.security.AuthorizationProvider");
        atzProvider.useDefaultPolicy(true);
        testPermissionWithSubjectInAccessControlContext();
    }

    /**
     * <p>
     * Initialize user test object.
     * </p>
     */
    protected void initUser()
    {
        try
        {
            ums.addUser("anon", "password");
        }
        catch (SecurityException sex)
        {
        }
        UserPrincipal user = new UserPrincipalImpl("anon");
        PortletPermission perm1 = new PortletPermission("myportlet", "view");
        PortletPermission perm2 = new PortletPermission("myportlet", "view, edit");
        try
        {
            pms.addPermission(perm1);
            pms.addPermission(perm2);

            pms.grantPermission(user, perm1);
            pms.grantPermission(user, perm2);
        }
        catch (SecurityException sex)
        {
            sex.printStackTrace();
        }
    }

    /**
     * <p>
     * Destroy user test object.
     * </p>
     */
    protected void destroyUser() throws Exception
    {
        ums.removeUser("anon");
        // Remove permissions.
        PortletPermission perm1 = new PortletPermission("myportlet", "view");
        PortletPermission perm2 = new PortletPermission("myportlet", "view, edit");
        pms.removePermission(perm1);
        pms.removePermission(perm2);
    }

}
