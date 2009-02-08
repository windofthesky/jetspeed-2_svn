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
package org.apache.jetspeed.security;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.PassiveCallbackHandler;

/**
 * @author <a href="mailto:christophe.lombart@sword-technologies.com">Christophe Lombart</a>
 * @version $Id$
 */
public class TestRdbmsPolicyFolder extends AbstractSecurityTestcase
{
    /** <p>The JAAS login context.</p> */
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
            System.out.println("\t\t[TestRdbmsPolicy - Folder] Creating login context.");
            PassiveCallbackHandler pch = new PassiveCallbackHandler("anon", "password");
            loginContext = new LoginContext("Jetspeed", pch);
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
        super.tearDown();
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestRdbmsPolicyFolder.class);
    }

    /**
     * Test simple permission on one document
     *
     */
    public void testSimplePermission()
    {

        try
        {
            JSSubject.doAs(loginContext.getSubject(), new PrivilegedAction<Object>()
            {
                public Object run()
                {
                    JetspeedPermission perm1 = pms.newPermission(PermissionFactory.FOLDER_PERMISSION, "/files/test.xml", "edit");                    
                    AccessController.checkPermission((Permission)perm1);
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
            JSSubject.doAs(loginContext.getSubject(), new PrivilegedAction<Object>()
            {
                public Object run()
                {
                    JetspeedPermission perm2 = pms.newPermission(PermissionFactory.FOLDER_PERMISSION, "/files/test.xml", "secure");
                    AccessController.checkPermission((Permission)perm2);
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
            JSSubject.doAs(loginContext.getSubject(), new PrivilegedAction<Object>()
            {
                public Object run()
                {
                    JetspeedPermission perm1 = pms.newPermission(PermissionFactory.FOLDER_PERMISSION, "/files/subfolder1/test.xml", "view");
                    AccessController.checkPermission((Permission)perm1);
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
            JSSubject.doAs(loginContext.getSubject(), new PrivilegedAction<Object>()
            {
                public Object run()
                {
                    JetspeedPermission perm1 = pms.newPermission(PermissionFactory.FOLDER_PERMISSION, "/files/subfolder1/foo", "view");
                    AccessController.checkPermission((Permission)perm1);
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
            JSSubject.doAs(loginContext.getSubject(), new PrivilegedAction<Object>()
            {
                public Object run()
                {
                    JetspeedPermission perm1 = pms.newPermission(PermissionFactory.FOLDER_PERMISSION, "/files/subfolder1/foo/anotherdoc.xml", "view");
                    AccessController.checkPermission((Permission)perm1);
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
            JSSubject.doAs(loginContext.getSubject(), new PrivilegedAction<Object>()
            {
                public Object run()
                {
                    JetspeedPermission perm1 = pms.newPermission(PermissionFactory.FOLDER_PERMISSION, "/files/subfolder2/test.xml", "view");
                    AccessController.checkPermission((Permission)perm1);
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
            JSSubject.doAs(loginContext.getSubject(), new PrivilegedAction<Object>()
            {
                public Object run()
                {
                    JetspeedPermission perm1 = pms.newPermission(PermissionFactory.FOLDER_PERMISSION, "/files/subfolder2/foo", "view");
                    AccessController.checkPermission((Permission)perm1);
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
            JSSubject.doAs(loginContext.getSubject(), new PrivilegedAction<Object>()
            {
                public Object run()
                {
                    JetspeedPermission perm1 = pms.newPermission(PermissionFactory.FOLDER_PERMISSION, "/files/subfolder2/foo/anotherdoc.xml", "view");
                    AccessController.checkPermission((Permission)perm1);
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
    protected void initUser() throws SecurityException
    {
        User user = addUser("anon","password");
        
        JetspeedPermission perm1 = pms.newPermission(PermissionFactory.FOLDER_PERMISSION, "/files/test.xml", "edit");
        JetspeedPermission perm2 = pms.newPermission(PermissionFactory.FOLDER_PERMISSION, "/files/subfolder1/*", "view");
        JetspeedPermission perm3 = pms.newPermission(PermissionFactory.FOLDER_PERMISSION, "/files/subfolder2/-", "view");
        pms.addPermission(perm1);
        pms.addPermission(perm2);
        pms.addPermission(perm3);
        
        pms.grantPermission(perm1, user);
        pms.grantPermission(perm2, user);
        pms.grantPermission(perm3, user);
    }
}
