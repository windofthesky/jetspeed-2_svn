/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.security.spi.ldap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.impl.LdapCredentialHandler;

import java.util.Set;

/**
 * <p>
 * Test {@link LdapCredentialHandler}implementation of the SPI
 * <code>CredentialHandler</code>.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 */
public class TestLdapCredentialHandler extends AbstractLdapTest
{
    /** The logger. */
    private static final Log log = LogFactory.getLog(TestLdapCredentialHandler.class);

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        crHandler = new LdapCredentialHandler();
    }

    /**
     * <p>
     * Test <code>getPrivateCredentials</code>
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public void testGetPrivateCredentials() throws SecurityException
    {
        Set credentials = crHandler.getPrivateCredentials(uid1);

        assertTrue("getPrivateCredentials found no credentials for user:" + uid1, credentials.size() > 0);

        PasswordCredential cred = (PasswordCredential) credentials.iterator().next();

        assertEquals(password, String.valueOf(cred.getPassword()));
    }

    /**
     * <p>
     * Test <code>getPrivateCredentials</code> for a user that does not exist.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public void testGetPrivateCredentialsForNonExistantUser() throws SecurityException
    {
        String nonExistantUser = Integer.toString(rand.nextInt());
        Set credentials = crHandler.getPrivateCredentials(nonExistantUser);

        assertTrue("getPrivateCredentials should not have found credentials for user:" + nonExistantUser, credentials
                .isEmpty());
    }

    /**
     * <p>
     * Test <code>setPassword</code>.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public void testSetPassword() throws SecurityException
    {
        crHandler.setPassword(uid1, password, "freddie");
        assertTrue("Failed to change the password.", crHandler.authenticate(uid1, "freddie"));
        crHandler.setPassword(uid1, "freddie", password);
    }

    /**
     * <p>
     * Test <code>setPassword</code> with null password.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public void testVerifyNullSetPassword() throws SecurityException
    {
        crHandler.setPassword(uid1, null, password);
    }

    /**
     * <p>
     * Test <code>authenticate</code> with correct login.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public void testGoodLogin() throws SecurityException
    {
        assertTrue("The login failed for user.", crHandler.authenticate(uid1, password));
    }

    /**
     * <p>
     * Test <code>authenticate</code> with no password.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public void testCannotAuthenticateWithNoPassword()
    {
        try
        {
            crHandler.authenticate(uid1, "");
            fail("Should have thrown a SecurityException.");
        }
        catch (Exception e)
        {
            assertTrue("Should have thrown an SecurityException but threw:" + e, e instanceof SecurityException);
        }
    }

    /**
     * <p>
     * Test <code>authenticate</code> with bad uid.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public void testBadUID() throws SecurityException
    {
        String nonExistantUser = Integer.toString(rand.nextInt());

        try
        {
            crHandler.authenticate(nonExistantUser, password);
            fail("Should have thrown an exception for a non-existant user.");
        }
        catch (Exception e)
        {
            assertTrue("Should have thrown a SecurityException for a non-existant user.",
                    e instanceof SecurityException);
        }
    }

    /**
     * <p>
     * Test <code>authenticate</code> with bad password.
     * </p>
     * 
     * @throws SecurityException A {@link SecurityException}.
     */
    public void testBadPassword() throws SecurityException
    {
        assertFalse("Should not have authenticated with a bad password.", crHandler
                .authenticate(uid1, password + "123"));
    }
}