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
package org.apache.jetspeed.security;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.security.impl.PassiveCallbackHandler;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;

/**
 * <p>Unit testing for {@link UserManagerService}.</p>
 *
 * @author <a href="mailto:david@sensova.com">David Le Strat</a>
 */
public class TestUserManagerService extends JetspeedTest
{

    private UserManagerService service = null;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestUserManagerService(String testName)
    {
        super(testName);
    }

    /**
     * <p>Start the tests.</p>
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestUserManagerService.class.getName()});
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        destroyUserObject();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
        destroyUserObject();
    }

    /**
     * <p>Creates the test suite.</p>
     * @return A test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        return new JetspeedTestSuite(TestUserManagerService.class);
    }

    /**
     * <p>Returns the {@link UserManagerService}.</p>
     * @return The UserManagerService.
     */
    protected UserManagerService getService()
    {
        if (service == null)
        {
            service = (UserManagerService) CommonPortletServices.getPortalService(UserManagerService.SERVICE_NAME);
        }
        return service;
    }

    /**
     * <p>Test that a {@link UserManagerService} was returned.</p>
     */
    public void testService()
    {
        assertNotNull(getService());
    }

    /**
     * <p>Test add user.</p>
     */
    public void testAddUser()
    {
        UserManagerService ums = getService();
        try
        {
            ums.addUser("anon", "password");
        }
        catch (SecurityException sex)
        {
            assertTrue("user already exists. exception caught: " + sex, false);
        }

        try
        {
            ums.addUser("anon", "password");
            assertTrue("user should already exists. exception not thrown.", false);
        }
        catch (SecurityException sex)
        {
        }

    }

    /**
     * <p>Test remove user.</p>
     */
    public void testRemoveUser()
    {
        UserManagerService ums = getService();
        ums.removeUser("anon");
        if (ums.userExists("anon"))
        {
            assertTrue("user should have been removed: ", false);
        }
    }

    /**
     * <p>Test get user.</p>
     */
    public void testGetUser()
    {
        UserManagerService ums = getService();
        // Test when the user does not exist.
        try
        {
            User user = ums.getUser("test");
            assertTrue("user does not exist. should have thrown an exception.", false);
        }
        catch (SecurityException sex)
        {
        }
        // Test when the user exists.
        User user = null;
        try
        {
            ums.addUser("test", "password");
            user = ums.getUser("test");
        }
        catch (SecurityException sex)
        {
            assertTrue("user exists. should not have thrown an exception.", false);
        }
        assertNotNull("user is null", user);
        // Test the User Subject.
        Subject subject = user.getSubject();
        assertNotNull("subject is null", subject);
        // Asset user principal.
        Principal userPrincipal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
        assertNotNull("user principal is null", userPrincipal);
        assertEquals(
            "expected user principal full path == /user/test",
            "/user/test",
            SecurityHelper.getPrincipalFullPath(userPrincipal));
        assertEquals(
            "expected user principal name == test",
             "test",
             userPrincipal.getName());

        // Test the User Preferences.
        Preferences preferences = user.getPreferences();
        assertEquals("expected user node == /user/test", "/user/test", preferences.absolutePath());
    }

    /**
     * <p>Test set password.</p>
     */
    public void testSetPassword()
    {
        UserManagerService ums = getService();
        try
        {
            ums.addUser("anon", "password");
            ums.setPassword("anon", "newpassword");

            LoginContext loginContext = null;
            // Test that the user can log in with the new password.
            try
            {
                PassiveCallbackHandler pch = new PassiveCallbackHandler("anon", "newpassword");
                loginContext = new LoginContext("Jetspeed", pch);
                loginContext.login();
                loginContext.logout();
            }
            catch (LoginException le)
            {
                le.printStackTrace();
                assertTrue("failed to login user with new password.", false);
            }
        }
        catch (SecurityException sex)
        {
        }
    }

    /**
     * <p>Destroy user test object.</p>
     */
    protected void destroyUserObject()
    {
        UserManagerService ums = getService();
        ums.removeUser("anon");
        ums.removeUser("test");
    }

}
