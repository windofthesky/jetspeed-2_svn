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

import java.security.Principal;
import java.util.Iterator;

import org.apache.jetspeed.security.AbstractSecurityTestcase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * Unit testing for {@link UserSecurityHandler}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class TestUserSecurityHandler extends AbstractSecurityTestcase
{

    /**
     * <p>
     * Defines the test case name for junit.
     * </p>
     * 
     * @param testName The test case name.
     */
    public TestUserSecurityHandler(String testName)
    {
        super(testName);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        destroyUsers();
        initUsers();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        super.tearDown();
        destroyUsers();
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
        return new TestSuite(TestUserSecurityHandler.class);
    }

    /**
     * <p>
     * Test <code>getUserPrincipal</code>.
     * </p>
     */
    public void testGetUserPrincipal() throws Exception
    {
        Principal principal = ush.getUserPrincipal("testuser1");
        assertNotNull(principal);
        assertEquals("testuser1", principal.getName());
    }
    
    /**
     * <p>
     * Test <code>getUserPrincipals</code>.
     * </p>
     */
    public void testGetUserPrincipals() throws Exception
    {
        Iterator principals = ush.getUserPrincipals("").iterator();
        int count = 0;
        while (principals.hasNext())
        {
            Principal principal = (Principal) principals.next();
            if (0 == count)
            {
                assertNotNull(principal);
                assertEquals("testuser1", principal.getName());
            }
            else if (1 == count)
            {
                assertNotNull(principal);
                assertEquals("testuser2", principal.getName());
            }
            count ++;
        }
    }

    /**
     * <p>
     * Initialize user test object.
     * </p>
     */
    protected void initUsers() throws Exception
    {
        ums.addUser("testuser1", "password");
        ums.addUser("testuser2", "password");
    }

    /**
     * <p>
     * Destroy user test object.
     * </p>
     */
    protected void destroyUsers() throws Exception
    {
        ums.removeUser("testuser1");
        ums.removeUser("testuser2");
    }

}