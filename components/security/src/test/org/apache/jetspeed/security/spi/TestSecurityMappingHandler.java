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

import java.util.Set;

import org.apache.jetspeed.security.util.test.AbstractSecurityTestcase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * Unit testing for {@link GroupSecurityHandler}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class TestSecurityMappingHandler extends AbstractSecurityTestcase
{

   

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        destroyGroupUser();
        destroyRoleUser();
        initGroupUser();
        initRoleUser();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        destroyGroupUser();
        destroyRoleUser();
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
        return new TestSuite(TestSecurityMappingHandler.class);
    }

    /**
     * <p>
     * Test <code>getRolePrincipals</code>.
     * </p>
     */
    public void testGetRolePrincipals() throws Exception
    {

        Set principals = smh.getRolePrincipals("testuser");
        assertNotNull(principals);
        // Hierarchy by generalization should return 3 roles.
        assertEquals(3, principals.size());

    }
    
    /**
     * <p>
     * Test <code>getUserPrincipal</code>.
     * </p>
     */
    public void testGetGroupPrincipals() throws Exception
    {
  
        Set principals = smh.getGroupPrincipals("testuser");
        assertNotNull(principals);
        // Hierarchy by generalization should return 3 roles.
        assertEquals(3, principals.size());

    }

    /**
     * <p>
     * Initialize user test object.
     * </p>
     */
    protected void initGroupUser() throws Exception
    {
        ums.addUser("testuser", "password");
        gms.addGroup("testusertogroup1");
        gms.addGroup("testusertogroup2.group1");
        gms.addUserToGroup("testuser", "testusertogroup1");
        gms.addUserToGroup("testuser", "testusertogroup2.group1");
    }

    /**
     * <p>
     * Destroy user test object.
     * </p>
     */
    protected void destroyGroupUser() throws Exception
    {
        ums.removeUser("testuser");
        gms.removeGroup("testusertogroup1");
        gms.removeGroup("testusertogroup2");
        gms.removeGroup("testusertogroup2.group1");
    }
    
    /**
     * <p>
     * Initialize user test object.
     * </p>
     */
    protected void initRoleUser() throws Exception
    {
        
        rms.addRole("testusertorole1");
        rms.addRole("testusertorole2.role1");
        rms.addRoleToUser("testuser", "testusertorole1");
        rms.addRoleToUser("testuser", "testusertorole2.role1");
    }

    /**
     * <p>
     * Destroy user test object.
     * </p>
     */
    protected void destroyRoleUser() throws Exception
    {

        rms.removeRole("testusertorole1");
        rms.removeRole("testusertorole2");
        rms.removeRole("testusertorole2.role1");
    }

}