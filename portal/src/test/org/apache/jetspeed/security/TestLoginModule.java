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
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.impl.PassiveCallbackHandler;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;

/**
 * @author <a href="mailto:david@sensova.com">David Le Strat</a>
 */
public class TestLoginModule extends JetspeedTest
{
    /** <p>The JAAS login context.</p> */
    private LoginContext loginContext = null;

    /** <p>The user manager service.</p> */
    private UserManagerService ums = null;

    /**
     * <p>Defines the test case name for junit.</p>
     * @param testName The test case name.
     */
    public TestLoginModule(String testName)
    {
        super(testName);
    }

    /**
     * <p>Returns the {@link UserManagerService}.</p>
     * @return The UserManagerService.
     */
    protected UserManagerService getService()
    {
        if (ums == null)
        {
            ums = (UserManagerService) CommonPortletServices.getPortalService(UserManagerService.SERVICE_NAME);
        }
        return ums;
    }

    /**
     * <p>Start the tests.</p>
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestLoginModule.class.getName()});
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();
        initUserObject();

        // Set up login context.
        try {
            PassiveCallbackHandler pch = new PassiveCallbackHandler("anonlogin", "password");
            loginContext = new LoginContext("Jetspeed", pch);
        }
        catch (LoginException le)
        {
            le.printStackTrace();
            assertTrue("\t\t[TestLoginModule] Failed to setup test.", false);
        }
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
        return new JetspeedTestSuite(TestLoginModule.class);
    }

    public void testLogin() throws LoginException
    {
        loginContext.login();
        Principal found = SecurityHelper.getPrincipal(loginContext.getSubject(), UserPrincipal.class);
        assertNotNull("found principal is null", found);
        assertTrue("found principal should be anonlogin, " + found.getName(), found.getName().equals((new UserPrincipalImpl("anonlogin")).getName()));      
    }
    
    public void testLogout() throws LoginException
    {
        loginContext.login();
        loginContext.logout();
        Principal found = SecurityHelper.getBestPrincipal(loginContext.getSubject(), UserPrincipal.class);
        assertNull("found principal is not null", found);
    }

    /**
     * <p>Initialize user test object.</p>
     */
    protected void initUserObject()
    {
        UserManagerService ums = getService();
        try
        {
            ums.addUser("anonlogin", "password");
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
        ums.removeUser("anonlogin");
    }

}
