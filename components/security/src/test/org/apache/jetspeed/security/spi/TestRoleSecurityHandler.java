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

import org.apache.jetspeed.security.AbstractSecurityTestcase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * Unit testing for {@link RoleSecurityHandler}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class TestRoleSecurityHandler extends AbstractSecurityTestcase
{

    /**
     * <p>
     * Defines the test case name for junit.
     * </p>
     * 
     * @param testName The test case name.
     */
    public TestRoleSecurityHandler(String testName)
    {
        super(testName);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
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
        return new TestSuite(TestRoleSecurityHandler.class);
    }

    /**
     * <p>
     * Test <code>getRolePrincipal</code>.
     * </p>
     */
    public void testGetRolePrincipal() throws Exception
    {
        initRole();
        Principal principal = rsh.getRolePrincipal("testusertorole1");
        assertNotNull(principal);
        assertEquals("testusertorole1", principal.getName());
        destroyRole();
    }
    
    /**
     * <p>
     * Initialize role test object.
     * </p>
     */
    protected void initRole() throws Exception
    {
        rms.addRole("testusertorole1");
    }

    /**
     * <p>
     * Destroy role test object.
     * </p>
     */
    protected void destroyRole() throws Exception
    {
        rms.removeRole("testusertorole1");
    }

}