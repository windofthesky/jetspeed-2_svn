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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserCredentialDao;
import org.apache.jetspeed.security.spi.impl.ldap.LdapUserCredentialDaoImpl;

/**
 * <p>
 * Test the {@link LdapUserCredentialDao}.
 * </p>
 * 
 * @author <a href="mailto:mike.long@dataline.com">Mike Long </a>
 *  
 */
public class TestLdapUserCredentialDao extends AbstractLdapTest
{
    /** Configuration for the number of threads performing login. */
    private static int NUMBER_OF_LOGIN_THREADS = 5;

    /** Configuration for the number of login per thread. */
    private static int NUMBER_OF_LOGINS_PER_THREAD = 10;

    /** Map of login threads. */
    private static Map loginThreads = new HashMap();

    /** The logger. */
    private static final Log log = LogFactory.getLog(TestLdapUserCredentialDao.class);

    /** The {@link LdapUserCredentialDao}. */
    private LdapUserCredentialDao ldap;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        ldap = new LdapUserCredentialDaoImpl();
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
        assertTrue("The login failed for user.", ldap.authenticate(uid1, password));
    }

    /**
     * <p>
     * Test that the uid does not contain any of the following character:
     * <code>([{\^$|)?*+.</code>
     * </p>
     */
    public void testRegularExpessionInUid()
    {
        // ([{\^$|)?*+.
        verifyRegularExpressionFails("(");
        verifyRegularExpressionFails("[");
        verifyRegularExpressionFails("{");
        verifyRegularExpressionFails("\\");
        verifyRegularExpressionFails("^");
        verifyRegularExpressionFails("$");
        verifyRegularExpressionFails("|");
        verifyRegularExpressionFails(")");
        verifyRegularExpressionFails("?");
        verifyRegularExpressionFails("*");
        verifyRegularExpressionFails("+");
        verifyRegularExpressionFails(".");
    }

    /**
     * <p>
     * Test <code>authenticate</code> with incorrect character in uid.
     * </p>
     */
    private void verifyRegularExpressionFails(String metaCharacter)
    {
        try
        {
            ldap.authenticate(uid1 + metaCharacter, password);
            fail("Should have thrown an IllegalArgumentException because the uid contained a regular expression meta-character.");
        }
        catch (Exception e)
        {
            assertTrue(
                    "Should have thrown an IllegalArgumentException  because the uid contained a regular expression meta-character.",
                    e instanceof IllegalArgumentException);
        }
    }

    /**
     * <p>
     * Test <code>authenticate</code> with no password.
     * </p>
     */
    public void testCannotAuthenticateWithNoPassword()
    {
        try
        {
            ldap.authenticate(uid1, "");
            fail("Should have thrown an SecurityException.");
        }
        catch (Exception e)
        {
            log.debug(e);
            assertTrue("Should have thrown an SecurityException. Instead it threw:" + e.getClass().getName(),
                    e instanceof SecurityException);
        }

        try
        {
            ldap.authenticate(uid1, null);
            fail("Should have thrown an SecurityException.");
        }
        catch (Exception e)
        {
            assertTrue("Should have thrown an SecurityException." + e, e instanceof SecurityException);
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

        try
        {
            ldap.authenticate(uid1 + "123", password);
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
     * @throws NamingException A {@link NamingException}.
     */
    public void testBadPassword() throws SecurityException
    {
        assertFalse("Should not have authenticated with bad password.", ldap.authenticate(uid1, password + "123"));
    }

    /**
     * <p>
     * Test <code>authenticate</code> with concurrent logins.
     * </p>
     * 
     * @throws InterruptedException A {@link InterruptedException}.
     */
    public void testConcurrentLogins() throws InterruptedException, SecurityException, NamingException
    {
        for (int i = 0; i < NUMBER_OF_LOGIN_THREADS; i++)
        {
            LoginThread thread = new LoginThread();

            thread.start();
        }

        Thread.sleep(6000);
        assertTrue("Not all login threads completed.", loginThreads.size() == NUMBER_OF_LOGIN_THREADS);
        assertTrue("Not all login threads successfully ran all their logins().", allLoginThreadsCompletedTheirLogins());
        assertFalse("An exception was thrown by a login thread. This means there is a concurrency problem.",
                exceptionThrownByLogin());
    }

    /**
     * <p>
     * Gets the exception thrown by the login operation.
     * </p>
     */
    private boolean exceptionThrownByLogin()
    {
        boolean exceptionThrown = false;
        Iterator loginThreadStatuses = loginThreads.values().iterator();

        while (loginThreadStatuses.hasNext())
        {
            LoginThreadStatus status = (LoginThreadStatus) loginThreadStatuses.next();

            if (status.isSomeExceptionThrown())
            {
                exceptionThrown = true;
            }
        }

        return exceptionThrown;
    }

    /**
     * <p>
     * Whether all login thread completed their login.
     * </p>
     */
    private boolean allLoginThreadsCompletedTheirLogins()
    {
        boolean allThreadsCompletedTheirLogins = true;
        Iterator loginThreadStatuses = loginThreads.values().iterator();

        while (loginThreadStatuses.hasNext())
        {
            LoginThreadStatus status = (LoginThreadStatus) loginThreadStatuses.next();

            if (status.getNumberOfSuccessfulLogins() < NUMBER_OF_LOGINS_PER_THREAD)
            {
                allThreadsCompletedTheirLogins = false;
            }
        }

        return allThreadsCompletedTheirLogins;
    }

    /**
     * <p>
     * Login threads.
     * </p>
     */
    private class LoginThread extends Thread
    {
        /** The login thread status. */
        private LoginThreadStatus status = new LoginThreadStatus();

        /** The {@link LdapUserCredentialDao}. */
        private LdapUserCredentialDao threadLdap;

        public LoginThread() throws NamingException, SecurityException
        {
            threadLdap = new LdapUserCredentialDaoImpl();
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run()
        {
            for (int i = 0; i < NUMBER_OF_LOGINS_PER_THREAD; i++)
            {
                try
                {
                    assertTrue("The login failed for user.", threadLdap.authenticate(uid1, password));
                    status.incrementNumberOfSuccessfulLogins();
                }
                catch (Exception e)
                {
                    status.setSomeExceptionThrown(true);
                }
            }

            TestLdapUserCredentialDao.loginThreads.put(this, status);
        }
    }
}

/**
 * <p>
 * The Login thread status.
 * </p>
 */

class LoginThreadStatus
{
    private int numberOfSuccessfulLogins;

    private boolean someExceptionThrown;

    void incrementNumberOfSuccessfulLogins()
    {
        this.numberOfSuccessfulLogins++;
    }

    int getNumberOfSuccessfulLogins()
    {
        return numberOfSuccessfulLogins;
    }

    void setSomeExceptionThrown(boolean someExceptionThrown)
    {
        this.someExceptionThrown = someExceptionThrown;
    }

    boolean isSomeExceptionThrown()
    {
        return someExceptionThrown;
    }
}