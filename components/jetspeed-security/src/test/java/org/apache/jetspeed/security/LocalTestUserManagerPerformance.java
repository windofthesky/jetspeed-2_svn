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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * <p>
 * Unit testing for {@link UserManager}.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class LocalTestUserManagerPerformance extends AbstractSecurityTestcase
{
    public static Test suite()
    {
        return new TestSuite(LocalTestUserManagerPerformance.class);
    }
    
    /**
     * <p>
     * performance test user retrieval with low number of users.
     * </p>
     */
    public void testUserRetrievalWithLowNumberOfUsers()
    {
        int nUsers = 20;
    	try
        {
    		
    		System.out.println("adding " + nUsers + " users...");
    		for (int i=0; i<nUsers; i++) {
            	ums.addUser("anon"+i);
            }
    		System.out.println("done.");
    		System.gc();
            long startTime = System.currentTimeMillis();
            long freeMemory = Runtime.getRuntime().freeMemory();
            UserResultList ul = ums.getUsersExtended(new JetspeedPrincipalQueryContext(null, 0, 10));
            System.out.println(nUsers + " user: get 10 users via getUsers(null, null, 1, 10) ...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
            System.out.println("first retrieved principals name:" + ul.getResults().get(0).getName());
            assertEquals(ul.getResults().size(), 10);
        	System.gc();
            startTime = System.currentTimeMillis();
            freeMemory = Runtime.getRuntime().freeMemory();
            ul = ums.getUsersExtended(new JetspeedPrincipalQueryContext(null, 10, 10));
            System.out.println(nUsers + " user: get 10 users via getUsers(null, null, 10, 10) ...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
            System.out.println("first retrieved principals name:" + ul.getResults().get(0).getName());
            assertEquals(ul.getResults().size(), 10);
        	System.gc();
            startTime = System.currentTimeMillis();
            freeMemory = Runtime.getRuntime().freeMemory();
            List<User> l = ums.getUsers("");
            System.out.println(nUsers + " user: get all users via ums.getUsers(null)...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
        	System.gc();
            startTime = System.currentTimeMillis();
            freeMemory = Runtime.getRuntime().freeMemory();
            l = ums.getUsers("");
            System.out.println(nUsers + " user: get all users via ums.getUsers(null)...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
        }
        catch (SecurityException sex)
        {
            assertTrue("user already exists. exception caught: " + sex, false);
        }

        try
        {
    		System.out.println("removing " + nUsers + " users...");
            for (int i=0; i<nUsers; i++) {
            	ums.removeUser("anon"+i);
            }
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user. exception caught: " + sex, false);
        }
    }


    /**
     * <p>
     * performance test user retrieval with medium number of users.
     * </p>
     */
    public void testUserRetrievalWithMediumNumberOfUsers()
    {
        
        int nUsers = 300;
    	try
        {
    		
    		System.out.println("adding " + nUsers + " users...");
    		for (int i=0; i<nUsers; i++) {
            	ums.addUser("anon"+i);
            }
    		System.out.println("done.");
    		System.gc();
            long startTime = System.currentTimeMillis();
            long freeMemory = Runtime.getRuntime().freeMemory();
            UserResultList ul = ums.getUsersExtended(new JetspeedPrincipalQueryContext(null, 0, 10));
            System.out.println(nUsers + " user: get 10 users via getUsers(null, null, 1, 10) ...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
            System.out.println("first retrieved principals name:" + ul.getResults().get(0).getName());
            assertEquals(ul.getResults().size(), 10);
        	System.gc();
            startTime = System.currentTimeMillis();
            freeMemory = Runtime.getRuntime().freeMemory();
            ul = ums.getUsersExtended(new JetspeedPrincipalQueryContext(null, 10, 10));
            System.out.println(nUsers + " user: get 10 users via getUsers(null, null, 10, 10) ...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
            System.out.println("first retrieved principals name:" + ul.getResults().get(0).getName());
            assertEquals(ul.getResults().size(), 10);
        	System.gc();
            startTime = System.currentTimeMillis();
            freeMemory = Runtime.getRuntime().freeMemory();
            List<User> l = ums.getUsers(null);
            System.out.println(nUsers + " user: get all users via ums.getUsers(null)...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
        	System.gc();
            startTime = System.currentTimeMillis();
            freeMemory = Runtime.getRuntime().freeMemory();
            l = ums.getUsers(null);
            System.out.println(nUsers + " user: get all users via ums.getUsers(null)...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");

        }
        catch (SecurityException sex)
        {
            assertTrue("user already exists. exception caught: " + sex, false);
        }

        try
        {
    		System.out.println("removing " + nUsers + " users...");
            for (int i=0; i<nUsers; i++) {
            	ums.removeUser("anon"+i);
            }
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user. exception caught: " + sex, false);
        }
    }
    
    
    /**
     * <p>
     * performance test user retrieval with hi number of users.
     * </p>
     */
    public void testUserRetrievalWithHighNumberOfUsers()
    {
        
        int nUsers = 10000;
    	try
        {
    		
    		System.out.println("adding " + nUsers + " users...");
    		for (int i=0; i<nUsers; i++) {
    			User user = ums.addUser("anon"+i);
            	if (i%10 == 0) {
	            	user.getSecurityAttributes().getAttribute("name", true).setStringValue("dude");
	            	ums.updateUser(user);
            	}
            }
    		System.out.println("done.");
    		System.gc();
            long startTime = System.currentTimeMillis();
            long freeMemory = Runtime.getRuntime().freeMemory();
            UserResultList ul = ums.getUsersExtended(new JetspeedPrincipalQueryContext(null, 0, 10));
            System.out.println(nUsers + " user: get 10 users via getUsers(null, null, 1, 10) ...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
            System.out.println("first retrieved principals name:" + ul.getResults().get(0).getName());
            assertEquals(ul.getResults().size(), 10);
        	System.gc();
            startTime = System.currentTimeMillis();
            freeMemory = Runtime.getRuntime().freeMemory();
            ul = ums.getUsersExtended(new JetspeedPrincipalQueryContext(null, 10, 10));
            System.out.println(nUsers + " user: get 10 users via getUsers(null, null, 10, 10) ...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
            System.out.println("first retrieved principals name:" + ul.getResults().get(0).getName());
            assertEquals(ul.getResults().size(), 10);
        	System.gc();
            startTime = System.currentTimeMillis();
            freeMemory = Runtime.getRuntime().freeMemory();
            ul = ums.getUsersExtended(new JetspeedPrincipalQueryContext(null, (int)Math.round(nUsers * 0.9), 10));
            System.out.println(nUsers + " user: get 10 users via getUsers(null, null, " + Math.round(nUsers * 0.9) + ", 10) ...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
            System.out.println("first retrieved principals name:" + ul.getResults().get(0).getName());
            assertEquals(ul.getResults().size(), 10);
        	System.gc();
            startTime = System.currentTimeMillis();
            freeMemory = Runtime.getRuntime().freeMemory();
            Map<String, String> attributeMap = new HashMap<String, String>() {
                {
                    put("name", "dude");
                }
            };
            
            JetspeedPrincipalQueryContext c = new JetspeedPrincipalQueryContext(null, 50, 10);
            c.put(JetspeedPrincipalQueryContext.SECURITY_ATTRIBUTES, attributeMap);
            ul = ums.getUsersExtended(c);
            System.out.println(nUsers + " user: get 10 users with the attribute name=dude via getUsers(null, attributeMap, 50, 10) ...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
            System.out.println("first retrieved principals name:" + ul.getResults().get(0).getName());
            assertEquals(ul.getResults().size(), 10);
        	System.gc();
            startTime = System.currentTimeMillis();
            freeMemory = Runtime.getRuntime().freeMemory();
            List<User> l = ums.getUsers(null);
            System.out.println(nUsers + " user: get all users via ums.getUsers(null)...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
        	System.gc();
            startTime = System.currentTimeMillis();
            freeMemory = Runtime.getRuntime().freeMemory();
            l = ums.getUsers(null);
            System.out.println(nUsers + " user: get all users via ums.getUsers(null)...");
            System.out.println("... took " + (System.currentTimeMillis() - startTime) + " ms, needed " + (freeMemory - Runtime.getRuntime().freeMemory()) / 1024 + " kbytes.");
        	System.gc();

        }
        catch (SecurityException sex)
        {
            assertTrue("user already exists. exception caught: " + sex, false);
        }

        try
        {
    		System.out.println("removing " + nUsers + " users...");
            for (int i=0; i<nUsers; i++) {
            	ums.removeUser("anon"+i);
            }
        }
        catch (SecurityException sex)
        {
            assertTrue("could not remove user. exception caught: " + sex, false);
        }
    }

}