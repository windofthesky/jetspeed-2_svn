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
package org.apache.jetspeed.security.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;
import org.apache.jetspeed.security.SecurityException;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * Unit testing for {@link PasswordCredentialProvider}.
 * </p>
 * 
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 */
public class TestPasswordCredentialProvider extends AbstractSecurityTestcase
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
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
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
        return new TestSuite(TestPasswordCredentialProvider.class);
    }

    /**
     * <p>
     * Test <code>getPrivateCredentials</code>..
     * </p>
     */
    public void testGetPrivateCredentials() throws Exception
    {
        initUser();
        Set privateCredentials = ums.getUser("testcred").getSubject().getPrivateCredentials();
        assertNotNull(privateCredentials);
        assertEquals(1, privateCredentials.size());
        PasswordCredential[] pwdCreds = (PasswordCredential[]) privateCredentials.toArray(new PasswordCredential[0]);
        assertEquals("testcred", pwdCreds[0].getUserName());
        assertNotSame("password01", new String(pwdCreds[0].getPassword()));
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
        Set privateCredentials = ums.getUser("testcred").getSubject().getPrivateCredentials();
        assertNotNull(privateCredentials);
        assertEquals(1, privateCredentials.size());
        PasswordCredential[] pwdCreds = (PasswordCredential[]) privateCredentials.toArray(new PasswordCredential[0]);
        assertEquals("testcred", pwdCreds[0].getUserName());
        String encodedPassword = new String(pwdCreds[0].getPassword());
        assertNotSame("password01", encodedPassword );
        
        // Try setting an invalid password: to short (min: 8)
        try
        {
            ums.setPassword("testcred","password01","1234567");
            fail("Should not be able to set an invalid password");
        }
        catch (SecurityException e){}
        // Try setting an invalid password: no digits
        try
        {
            ums.setPassword("testcred","password01","newpassword");
            fail("Should not be able to set an invalid password");
        }
        catch (SecurityException e){}
        // Setting a valid password
        ums.setPassword("testcred","password01","passwd01");

        // Test that the credential was updated.
        privateCredentials = ums.getUser("testcred").getSubject().getPrivateCredentials();
        assertNotNull(privateCredentials);
        assertEquals(1, privateCredentials.size());
        pwdCreds = (PasswordCredential[]) privateCredentials.toArray(new PasswordCredential[0]);
        assertEquals("testcred", pwdCreds[0].getUserName());
        String newEncodedPassword = new String(pwdCreds[0].getPassword());
        assertNotSame(encodedPassword, newEncodedPassword);
        assertNotSame("passwd01", newEncodedPassword);
        
        // Test authentication with the new password
        assertTrue(ums.authenticate("testcred","passwd01"));
        destroyUser();
    }
    
    /**
     * <p>
     * Initialize user test object.
     * </p>
     */
    protected void initUser() throws Exception
    {
        ums.addUser("testcred", "password01");
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
    
    protected String[] getConfigurations()
    {
        String[] confs = super.getConfigurations();
        List confList = new ArrayList(Arrays.asList(confs));
        confList.add("META-INF/spcpv.xml");
        return (String[]) confList.toArray(new String[1]);
    }    
}
