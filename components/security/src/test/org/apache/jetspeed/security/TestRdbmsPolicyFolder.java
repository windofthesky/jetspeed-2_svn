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
import java.security.PrivilegedAction;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.FolderPermission;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.PassiveCallbackHandler;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;

/**
 * @author <a href="mailto:christophe.lombart@sword-technologies.com">Christophe Lombart</a>
 * @version $Id$
 */
public class TestRdbmsPolicyFolder extends AbstractSecurityTestcase
{
    /** <p>The JAAS login context.</p> */
    private LoginContext loginContext = null;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestRdbmsPolicyFolder(String testName)
    {
        super(testName);
    }

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
            System.out.println("\t\t[TestRdbmsPolicy - Folder] Creating login context.");
            PassiveCallbackHandler pch = new PassiveCallbackHandler("anon", "password");
            loginContext = new LoginContext("jetspeed", pch);
            loginContext.login();
        }
        catch (LoginException le)
        {
            le.printStackTrace();
            assertTrue("\t\t[TestRdbmsPolicy - Folder] Failed to setup test.", false);
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
            assertTrue("\t\t[TestRdbmsPolicy - Folder] Failed to tear down test.", false);
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
     * Test simple permission on one document
     *
     */
    public void testSimplePermission()
    {

        try
        {
            Subject.doAs(loginContext.getSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    FolderPermission perm1 = new FolderPermission("/files/test.xml", "edit");
                    AccessController.checkPermission(perm1);
                    return null;
                }
            });
        }
        catch (AccessControlException ace)
        {
            assertTrue("did not authorize view permission on the Folder.", false);
        }

        // Should be denied.
        try
        {
            Subject.doAs(loginContext.getSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    FolderPermission perm2 = new FolderPermission("/files/test.xml", "secure");
                    AccessController.checkPermission(perm2);
                    return null;
                }
            });
            assertTrue("did not deny update permission on the folder.", false);
        }
        catch (AccessControlException ace)
        {
        }
    }

    /**
     * Test permissions with wild card (eg. /file/*) & with recursive setting (eg. /files/- ) 
     *
     */
    public void testAdvancedPermission()
    {

        try
        {
            Subject.doAs(loginContext.getSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    FolderPermission perm1 = new FolderPermission("/files/subfolder1/test.xml", "view");
                    AccessController.checkPermission(perm1);
                    return null;
                }
            });
        }
        catch (AccessControlException ace)
        {
            fail("did not authorize view permission on the Folder.");
        }

        
        try
        {
            Subject.doAs(loginContext.getSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    FolderPermission perm1 = new FolderPermission("/files/subfolder1/foo", "view");
                    AccessController.checkPermission(perm1);
                    return null;
                }
            });
        }
        catch (AccessControlException ace)
        {
            fail("did not authorize view permission on the Folder.");
        }  
        
        try
        {
            Subject.doAs(loginContext.getSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    FolderPermission perm1 = new FolderPermission("/files/subfolder1/foo/anotherdoc.xml", "view");
                    AccessController.checkPermission(perm1);
                    return null;
                }
            });
            fail("Permission error - should not view the document ");
        }
        catch (AccessControlException ace)
        {
            // Correct behavior - not authorise to view the document
        }         
        
        try
        {
            Subject.doAs(loginContext.getSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    FolderPermission perm1 = new FolderPermission("/files/subfolder2/test.xml", "view");
                    AccessController.checkPermission(perm1);
                    return null;
                }
            });
        }
        catch (AccessControlException ace)
        {
            fail("did not authorize view permission on the Folder.");
        }

        
        try
        {
            Subject.doAs(loginContext.getSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    FolderPermission perm1 = new FolderPermission("/files/subfolder2/foo", "view");
                    AccessController.checkPermission(perm1);
                    return null;
                }
            });
        }
        catch (AccessControlException ace)
        {
            fail("did not authorize view permission on the Folder.");
        }
        
        try
        {
            Subject.doAs(loginContext.getSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    FolderPermission perm1 = new FolderPermission("/files/subfolder2/foo/anotherdoc.xml", "view");
                    AccessController.checkPermission(perm1);
                    return null;
                }
            });
        }
        catch (AccessControlException ace)
        {
            fail("did not authorize view permission on the Folder.");
        }                
    }


    /**
     * <p>Initialize user test object.</p>
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

        FolderPermission perm1 = new FolderPermission("/files/test.xml", "edit");
        FolderPermission perm2 = new FolderPermission("/files/subfolder1/*", "view");
        FolderPermission perm3 = new FolderPermission("/files/subfolder2/-", "view");
        try
        {
            pms.addPermission(perm1);
            pms.addPermission(perm2);
            pms.addPermission(perm3);
            
            pms.grantPermission(user, perm1);
            pms.grantPermission(user, perm2);
            pms.grantPermission(user, perm3);
        }
        catch (SecurityException sex)
        {
            sex.printStackTrace();
        }
    }

    /**
     * <p>Destroy user test object.</p>
     */
    protected void destroyUser() throws Exception
    {
        ums.removeUser("anon");

        FolderPermission perm1 = new FolderPermission("/files/test.xml", "edit");
        FolderPermission perm2 = new FolderPermission("/files/subfolder1/*", "view");
        FolderPermission perm3 = new FolderPermission("/files/subfolder2/-", "view");
        pms.removePermission(perm1);
        pms.removePermission(perm2);
        pms.removePermission(perm3);
    }

}
