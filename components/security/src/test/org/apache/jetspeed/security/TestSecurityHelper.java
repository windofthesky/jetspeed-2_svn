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

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.security.impl.UserPrincipalImpl;

/**
 * TestSecurityHelper
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestSecurityHelper extends AbstractSecurityTestcase
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestSecurityHelper(String name)
    {
        super(name);
    }

 
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSecurityHelper.class);
    }
    
    public void testHelpers() throws Exception
    {
        Principal principal = new UserPrincipalImpl("anon");
        Set principals = new HashSet();
        principals.add(principal);
        Subject subject = new Subject(true, principals, new HashSet(), new HashSet());
        System.out.println("subject = " + subject);
        
        Principal found = SecurityHelper.getBestPrincipal(subject, UserPrincipal.class);
        assertNotNull("found principal is null", found);
        assertTrue("found principal should be anon", found.getName().equals("anon"));
        System.out.println("found = " + found.getName());
    }
    
}
