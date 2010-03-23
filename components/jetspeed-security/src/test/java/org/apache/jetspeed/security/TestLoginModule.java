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

import java.security.Principal;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import junit.framework.Test;

import org.apache.jetspeed.security.impl.PassiveCallbackHandler;

/**
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class TestLoginModule extends AbstractLDAPSecurityTestCase
{
    /** <p>The JAAS login context.</p> */
    private LoginContext loginContext = null;

    public static Test suite()
    {
        return createFixturedTestSuite(TestLoginModule.class, "ldapTestSetup", "ldapTestTeardown");
    }

    private void setupTest() throws Exception
    {
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

    public void testLogin() throws Exception
    { 
        setupTest();
        loginContext.login();        
        Subject subject = loginContext.getSubject();
        Principal found = SubjectHelper.getPrincipal(loginContext.getSubject(), User.class);
        assertNotNull("found principal is null, subject: "+subject, found);
        assertTrue("found principal should be anonlogin, " + found.getName(), found.getName().equals("anonlogin"));      
    }
    
    public void testLogout() throws Exception
    {
        setupTest();
        loginContext.login();
        loginContext.logout();
        Principal found = SubjectHelper.getBestPrincipal(loginContext.getSubject(), User.class);
        assertNull("found principal is not null", found);
    }

    /**
     * <p>Initialize user test object.</p>
     */
    protected void initUserObject() throws SecurityException
    {
        addUser("anonlogin", "password");
    }
}
