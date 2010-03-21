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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * Unit testing for {@link UserManager} using the extended pageable API methods.
 * </p>
 * 
 * @author <a href="mailto:joachim@wemove.com">Joachim Mueller</a>
 */
public class TestUserManagerExtLookupManager extends AbstractSecurityTestcase
{
    public static Test suite()
    {
        return new TestSuite(TestUserManagerExtLookupManager.class);
    }
    
    public void testGetUsersExt() throws Exception 
    {
		int nUsers = 100;
    	for (int i=0; i<nUsers; i++) {
			User user = ums.addUser("anon"+i);
        	if (i%10 == 0) {
            	user.getSecurityAttributes().getAttribute("name", true).setStringValue("dude");
            	user.getSecurityAttributes().getAttribute("fame", true).setStringValue("none");
            	ums.updateUser(user);
        	}
        }
        UserResultList ul = ums.getUsersExtended(new JetspeedPrincipalQueryContext(null, 0, 10));
        assertEquals(10, ul.getResults().size());
        assertEquals(true, ul.getTotalSize() >= nUsers);
        // check empty string
        ul = ums.getUsersExtended(new JetspeedPrincipalQueryContext("", 0, 10));
        assertEquals(10, ul.getResults().size());
        assertEquals(true, ul.getTotalSize() >= nUsers);
        // test wildcard
        ul = ums.getUsersExtended(new JetspeedPrincipalQueryContext("anon%", 0, 10));
        assertEquals(10, ul.getResults().size());
        assertEquals(nUsers, ul.getTotalSize());
        // test wildcard, paging
        ul = ums.getUsersExtended(new JetspeedPrincipalQueryContext("anon%", 50, 20));
        assertEquals(20, ul.getResults().size());
        assertEquals(nUsers, ul.getTotalSize());
        // test requesting more results than available
        ul = ums.getUsersExtended(new JetspeedPrincipalQueryContext("anon%", nUsers-5, 10));
        assertEquals(5, ul.getResults().size());
        assertEquals(nUsers, ul.getTotalSize());
        // testing attribute selection
        Map<String, String> attributeMap = new HashMap<String, String>() {
            {
                put("name", "dude");
            }
        };
        JetspeedPrincipalQueryContext c = new JetspeedPrincipalQueryContext(null, 0, 10);
        c.put(JetspeedPrincipalQueryContext.SECURITY_ATTRIBUTES, attributeMap);
        ul = ums.getUsersExtended(c);
        assertEquals(10, ul.getResults().size());
        assertEquals(10, ul.getTotalSize());
        // testing attribute selection, user name restriction
        attributeMap = new HashMap<String, String>() {
            {
                put("name", "dude");
            }
        };
        c = new JetspeedPrincipalQueryContext("anon0", 0, 10);
        c.put(JetspeedPrincipalQueryContext.SECURITY_ATTRIBUTES, attributeMap);
        ul = ums.getUsersExtended(c);
        assertEquals(1, ul.getResults().size());
        assertEquals(1, ul.getTotalSize());
        // testing attribute selection with wildcard
        attributeMap = new HashMap<String, String>() {
            {
                put("name", "du%");
            }
        };
        c = new JetspeedPrincipalQueryContext(null, 0, 10);
        c.put(JetspeedPrincipalQueryContext.SECURITY_ATTRIBUTES, attributeMap);
        ul = ums.getUsersExtended(c);
        assertEquals(10, ul.getResults().size());
        assertEquals(10, ul.getTotalSize());        
        // testing multiple attribute selection
        attributeMap = new HashMap<String, String>() {
            {
                put("name", "du%");
                put("fame", "none");
            }
        };
        c = new JetspeedPrincipalQueryContext(null, 0, 10);
        c.put(JetspeedPrincipalQueryContext.SECURITY_ATTRIBUTES, attributeMap);
        ul = ums.getUsersExtended(c);
        assertEquals(10, ul.getResults().size());
        assertEquals(10, ul.getTotalSize());        
        // testing non existing multiple attribute selection
        attributeMap = new HashMap<String, String>() {
            {
                put("name", "du%");
                put("fame", "wow");
            }
        };
        c = new JetspeedPrincipalQueryContext(null, 0, 10);
        c.put(JetspeedPrincipalQueryContext.SECURITY_ATTRIBUTES, attributeMap);
        ul = ums.getUsersExtended(c);
        assertEquals(0, ul.getResults().size());
        assertEquals(0, ul.getTotalSize());        
        
        for (int i=0; i<nUsers; i++) {
        	ums.removeUser("anon"+i);
        }
    }
    
    public void testGetUserByRoleExt() throws Exception {
		int nUsers = 100;
		rms.addRole("role");
    	for (int i=0; i<nUsers; i++) {
			User user = ums.addUser("anon"+i);
			if (i%10 == 0) {
	        	rms.addRoleToUser(user.getName(), "role");
        	}
        }
        JetspeedPrincipalQueryContext c = new JetspeedPrincipalQueryContext("anon*", 0, 10);
        List<String> roles = new ArrayList<String>();
        roles.add("role");
        c.put(JetspeedPrincipalQueryContext.ASSOCIATED_ROLES, roles);
        UserResultList ul = ums.getUsersExtended(c);
        assertEquals(10, ul.getResults().size());
    	
    	for (int i=0; i<nUsers; i++) {
        	ums.removeUser("anon"+i);
        }
    	
    	rms.removeRole("role");
    }
    
    public void testGetUserByGroupExt() throws Exception {
		int nUsers = 100;
		gms.addGroup("group");
    	for (int i=0; i<nUsers; i++) {
			User user = ums.addUser("anon"+i);
			if (i%10 == 0) {
	        	gms.addUserToGroup(user.getName(), "group");
        	}
        }
        JetspeedPrincipalQueryContext c = new JetspeedPrincipalQueryContext("anon*", 0, 10);
        List<String> groups = new ArrayList<String>();
        groups.add("group");
        c.put(JetspeedPrincipalQueryContext.ASSOCIATED_GROUPS, groups);
        UserResultList ul = ums.getUsersExtended(c);
        assertEquals(10, ul.getResults().size());
    	
    	for (int i=0; i<nUsers; i++) {
        	ums.removeUser("anon"+i);
        }
    	
    	gms.removeGroup("group");
    }    
    
}