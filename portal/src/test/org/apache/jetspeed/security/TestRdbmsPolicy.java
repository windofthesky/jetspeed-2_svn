/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security;

import java.security.AccessController;
import java.security.AccessControlException;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.Policy;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.PassiveCallbackHandler;
import org.apache.jetspeed.security.impl.RdbmsPolicy;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;

/**
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestRdbmsPolicy extends JetspeedTest
{
    /** <p>The JAAS login context.</p> */
    private LoginContext loginContext = null;

    /** <p>The user manager service.</p> */
    private UserManagerService ums = null;

    /** <p>The permission manager service.</p> */
    private PermissionManagerService pms = null;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestRdbmsPolicy(String testName)
    {
        super(testName);
    }

    /**
     * <p>Returns the {@link UserManagerService}.</p>
     * @return The UserManagerService.
     */
    protected UserManagerService getUserManagerService()
    {
        if (ums == null)
        {
            ums = (UserManagerService) CommonPortletServices.getPortalService(UserManagerService.SERVICE_NAME);
        }
        return ums;
    }

    /**
     * <p>Returns the {@link PermissionManagerService}.</p>
     * @return The PermissionManagerService.
     */
    protected PermissionManagerService getPermissionManagerService()
    {
        if (pms == null)
        {
            pms = (PermissionManagerService) CommonPortletServices.getPortalService(PermissionManagerService.SERVICE_NAME);
        }
        return pms;
    }

    /**
     * <p>Start the tests.</p>
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestRdbmsPolicy.class.getName()});
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();

        // Insert custom provider here.
        Policy.setPolicy(new RdbmsPolicy());
        Policy.getPolicy().refresh();

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
        super.tearDown();

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
    }

    /**
     * <p>Creates the test suite.</p>
     * @return A test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        return new JetspeedTestSuite(TestRdbmsPolicy.class);
    }

    /**
     * <p>Executing this test requires adding an entry to java.policy.</p>
     * <p>A possible entry would be to grant for all principals:</p>
     * <pre><code>
     * grant
     * {
     *     permission org.apache.jetspeed.security.auth.PortletPermission "myportlet", "view";
     * }
     * </code></pre>
     * <p>Such an entry would also test the Rdbms defaulting behavior if no
     * entry is provided in the database for the tested Subject JetspeedUserPrincipal.</p>
     */
    /*
    public void testPermissionWithSubjectInContructor()
    {
        // JetspeedPermission should be granted.
        PortletPermission perm1 = new PortletPermission("myportlet", "view", loginContext.getSubject());
        try
        {
            AccessController.checkPermission(perm1);
        }
        catch (AccessControlException ace)
        {
            assertTrue("did not authorize view permission on the portlet.", false);
        }
    
        // JetspeedPermission should be denied.
        PortletPermission perm2 = new PortletPermission("myportlet", "edit", loginContext.getSubject());
        try
        {
            AccessController.checkPermission(perm2);
            assertTrue("did not deny edit permission on the portlet.", false);
        }
        catch (AccessControlException ace)
        {
        }
    
        // Subject is omitted. JetspeedPermission should be denied.
        PortletPermission perm3 = new PortletPermission("myportlet", "view");
        try
        {
            AccessController.checkPermission(perm3);
            //assertTrue("did not deny permission with no subject passed.", false);
        }
        catch (AccessControlException ace)
        {
        }
    }
    */

    public void testPermissionWithSubjectInAccessControlContext()
    {
        // JetspeedPermission should be granted.
        try
        {
            Subject.doAs(loginContext.getSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    PortletPermission perm1 = new PortletPermission("myportlet", "view");
                    AccessController.checkPermission(perm1);
                    return null;
                }
            });
        }
        catch (AccessControlException ace)
        {
            assertTrue("did not authorize view permission on the portlet.", false);
        }

        // Should be denied.
        try
        {
            Subject.doAs(loginContext.getSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    PortletPermission perm2 = new PortletPermission("myportlet", "delete");
                    AccessController.checkPermission(perm2);
                    return null;
                }
            });
            assertTrue("did not deny delete permission on the portlet.", false);
        }
        catch (AccessControlException ace)
        {
        }
    }

    /**
     * <p>Initialize user test object.</p>
     */
    protected void initUser()
    {
        UserManagerService ums = getUserManagerService();
        try
        {
            ums.addUser("anon", "password");
        }
        catch (SecurityException sex)
        {
        }
        PermissionManagerService pms = getPermissionManagerService();
        UserPrincipal user = new UserPrincipalImpl("anon");
        PortletPermission perm1 = new PortletPermission("myportlet", "view");
        PortletPermission perm2 = new PortletPermission("myportlet", "view, edit");
        try
        {
            pms.grantPermission(user, perm1);
            pms.grantPermission(user, perm2);
        }
        catch (SecurityException sex)
        {
            sex.printStackTrace();
        }      
    }

    /**
     * <p>Destroy user test object.</p>
     */
    protected void destroyUser()
    {
        UserManagerService ums = getUserManagerService();
        PermissionManagerService pms = getPermissionManagerService();

        ums.removeUser("anon");
        // Remove permissions.
        PortletPermission perm1 = new PortletPermission("myportlet", "view");
        PortletPermission perm2 = new PortletPermission("myportlet", "view, edit");
        pms.removePermission(perm1);
        pms.removePermission(perm2);
    }

}
