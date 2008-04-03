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
package org.apache.jetspeed.security.spi;

import java.util.Set;

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * Unit testing for {@link UserManager}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class TestCredentialHandler extends AbstractSecurityTestcase
{

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp(); 
        // cleanup for previously failed test
        destroyUser();
    }

    /**
     * <p>
     * Constructs the suite.
     * </p>
     * 
     * @return The {@Test}.
     */
    public static Test suite()
    {
        return new TestSuite(TestCredentialHandler.class);
    }

    /**
     * <p>
     * Test <code>getPrivateCredentials</code>..
     * </p>
     */
    public void testGetPrivateCredentials() throws Exception
    {
        initUser();
        Set privateCredentials = ch.getPrivateCredentials("testcred");
        assertNotNull(privateCredentials);
        assertEquals(1, privateCredentials.size());
        PasswordCredential[] pwdCreds = (PasswordCredential[]) privateCredentials.toArray(new PasswordCredential[1]);
        assertEquals("testcred", pwdCreds[0].getUserName());
        assertNotNull(new String(pwdCreds[0].getPassword()));
        destroyUser();
    }
    
    /**
     * <p>
     * Test <code>getPublicCredentials</code>..
     * </p>
     */
    public void testGetPublicCredentials() throws Exception
    {
        initUser();
        Set publicCredentials = ch.getPublicCredentials("testcred");
        assertNotNull(publicCredentials);
        assertEquals(0, publicCredentials.size());
        destroyUser();
    }

    /**
     * <p>
     * Test <code>setPassword</code>..
     * </p>
     */
    public void testSetPassword() throws Exception
    {
        initUser();
        // Replace existing password credential.
        ch.setPassword("testcred","password","newpassword");
        // Test that the credential was properly set.
        Set privateCredentials = ch.getPrivateCredentials("testcred");
        assertNotNull(privateCredentials);
        assertEquals(1, privateCredentials.size());
        PasswordCredential[] pwdCreds = (PasswordCredential[]) privateCredentials.toArray(new PasswordCredential[0]);
        assertEquals("testcred", pwdCreds[0].getUserName());
        assertNotNull(new String(pwdCreds[0].getPassword()));
        // Add password credential.
        ch.setPassword("testcred","newpassword","anotherpassword");
        // Test that the credential was properly set.
        privateCredentials = ch.getPrivateCredentials("testcred");
        assertNotNull(privateCredentials);
        assertEquals(1, privateCredentials.size());
        destroyUser();
    }
    
    /**
     * <p>
     * Initialize user test object.
     * </p>
     */
    protected void initUser() throws Exception
    {
        ums.addUser("testcred", "password");
    }

    /**
     * <p>
     * Destroy user test object.
     * </p>
     */
    protected void destroyUser() throws Exception
    {
        ums.removeUser("testcred");
    }
}