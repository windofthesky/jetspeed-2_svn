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
package org.apache.jetspeed.security;

import java.util.Collection;

import javax.security.auth.Subject;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.impl.AggregationHierarchyResolver;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserManagerImpl;

/**
 * <p>
 * Unit testing for {@link AggregationHierarchyResolver}.
 * </p>
 * 
 * @author <a href="mailto:Artem.Grinshtein@t-systems.com">Artem Grinshtein </a>
 * @version $Id$
 */
public class TestAggregationHierarchy extends AbstractSecurityTestcase
{

    /**
     * <p>
     * Defines the test case name for junit.
     * </p>
     * 
     * @param testName The test case name.
     */
    public TestAggregationHierarchy(String testName)
    {
        super(testName);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        ums = new UserManagerImpl(securityProvider, new AggregationHierarchyResolver(),
                new AggregationHierarchyResolver());
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        destroyUserObject();
        super.tearDown();
    }

    public static Test suite()
    {
        return new TestSuite(TestAggregationHierarchy.class);
    }

    /**
     * <p>
     * Test RoleManager.
     * </p>
     */
    public void testRoleManager()
    {

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

        try
        {
            rms.addRole("rootrole");
            rms.addRole("rootrole.childrole1");
            rms.addRole("rootrole.childrole2");

        }
        catch (SecurityException sex)
        {
            assertTrue("add roles. should not have thrown an exception.", false);
        }

        try
        {
            rms.addRoleToUser("test", "rootrole");

            user = ums.getUser("test");
            Subject subject = user.getSubject();
            assertNotNull("subject is null", subject);
            Collection principals = getPrincipals(subject, RolePrincipal.class);
            assertEquals("should have 3 principals;", 3, principals.size());
            assertTrue("should contain rootrole", principals.contains(new RolePrincipalImpl("rootrole")));
            assertTrue("should contain rootrole.childrole1", principals.contains(new RolePrincipalImpl(
                    "rootrole.childrole1")));
            assertTrue("should contain rootrole.childrole2", principals.contains(new RolePrincipalImpl(
                    "rootrole.childrole2")));

            rms.removeRoleFromUser("test", "rootrole");

            user = ums.getUser("test");
            principals = getPrincipals(user.getSubject(), RolePrincipal.class);
            assertEquals("should not have any principals;", 0, principals.size());

        }
        catch (SecurityException sex)
        {
            assertTrue("test with parent role " + sex.getMessage(), false);
        }

        try
        {
            rms.addRoleToUser("test", "rootrole.childrole1");

            user = ums.getUser("test");
            Subject subject = user.getSubject();
            assertNotNull("subject is null", subject);
            Collection principals = getPrincipals(subject, RolePrincipal.class);
            assertEquals("shoud have 1 principal;", 1, principals.size());

            assertTrue("should contain rootrole.childrole1", principals.contains(new RolePrincipalImpl(
                    "rootrole.childrole1")));

            rms.removeRoleFromUser("test", "rootrole.childrole1");

            user = ums.getUser("test");
            principals = getPrincipals(user.getSubject(), RolePrincipal.class);
            assertEquals("should not have any principals;", 0, principals.size());

        }
        catch (SecurityException sex)
        {
            assertTrue("test with child role " + sex.getMessage(), false);
        }

    }

    /**
     * <p>
     * Destroy user test object.
     * </p>
     */
    protected void destroyUserObject()
    {
        try
        {
            if (ums.userExists("test"))
                ums.removeUser("test");
            if (rms.roleExists("rootrole"))
                rms.removeRole("rootrole");
        }
        catch (SecurityException sex)
        {
            System.out.println("could not remove test users. exception caught: " + sex);
        }
    }

}