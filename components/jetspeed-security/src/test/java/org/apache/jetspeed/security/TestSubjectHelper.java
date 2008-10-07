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
import java.util.Collections;
import java.util.Set;

import javax.security.auth.Subject;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * TestSubjectHelper
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestSubjectHelper extends AbstractSecurityTestcase
{
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSubjectHelper.class);
    }
    
    public void testHelpers() throws Exception
    {
        Principal principal = ums.newTransientUser("anon");
        Set<Principal> principals = new PrincipalsSet();
        principals.add(principal);
        Subject subject = new Subject(true, principals, Collections.emptySet(), Collections.emptySet());
        
        Principal found = SubjectHelper.getBestPrincipal(subject, User.class);
        assertNotNull("found principal is null", found);
        assertTrue("found principal should be anon", found.getName().equals("anon"));
    }
}
