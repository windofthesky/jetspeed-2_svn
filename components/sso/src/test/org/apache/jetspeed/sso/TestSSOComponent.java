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

package org.apache.jetspeed.sso;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.sso.SSOProvider;

import junit.framework.Test;
import junit.framework.TestSuite;

import javax.security.auth.Subject;

import java.security.Principal;
import java.util.HashSet;
import java.util.Set;


import org.apache.jetspeed.sso.SSOException;
import java.lang.Exception;

import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;


/**
 * <p>
 * Unit testing for {@link Preferences}.
 * </p>
 * 
 * @author <a href="rogerrut@apache.org">Roger Ruttimann </a>
 */
public class TestSSOComponent extends DatasourceEnabledSpringTestCase
{
	/**
	 * test url for this UnitTest
	 */
	static private String TEST_URL= "http://localhost/jetspeed";
	static private String TEST_USER= "joe";
	
		
    /** The property manager. */
    private static SSOProvider ssoBroker = null;
    /** The user manager. */
    protected UserManager ums;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();

        try
        {
            ssoBroker = (SSOProvider) ctx.getBean("ssoProvider");
            ums = (UserManager) ctx.getBean("org.apache.jetspeed.security.UserManager");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new Exception("Exception while setup SSO TEST");
        }
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        clean();
        // super.tearDown();
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSSOComponent.class);
    }

    /**
     * <p>
     * Test user root.
     * </p>
     */
    public void testSSO() throws Exception
    {
		// Create a user
		 try
		    {
		        ums.addUser(TEST_USER, "password");
		    }
		    catch (SecurityException sex)
		    {
		        //assertTrue("user already exists. exception caught: " + sex, false);
		    }
	        
    	// Initialization
    	Principal principal = new UserPrincipalImpl(TEST_USER);
        Set principals = new HashSet();
        principals.add(principal);
        Subject subject = new Subject(true, principals, new HashSet(), new HashSet());	
    	
    	if ( ssoBroker.hasSSOCredentials(subject, TEST_URL) == false)
    	{
    		System.out.println("No SSO Credential for user:" + TEST_USER+ " site: " + TEST_URL);
    		
    		// Add credential
    		try
			{
    			ssoBroker.addCredentialsForSite(subject, "TODO", TEST_URL,"test");
    			System.out.println("SSO Credential added for user:" + TEST_USER+ " site: " + TEST_URL);
			}
			catch(SSOException ssoex)
			{
	    		System.out.println("SSO Credential add FAILED for user:" + TEST_USER+ " site: " + TEST_URL);
	    		ssoex.printStackTrace();
	    		throw new Exception(ssoex.getMessage());
			}
    	}
    	else
    	{
    		System.out.println("SSO Credential found for user:" + TEST_USER+ " site: " + TEST_URL);
    	}
    	
     	try
		{
	    	// Remove credential for Site
	    	ssoBroker.removeCredentialsForSite(subject, TEST_URL);
	    	System.out.println("SSO Credential removed for user:" + TEST_USER+ " site: " + TEST_URL);
		}
    	catch(SSOException ssoex)
		{
    		System.out.println("SSO Credential remove FAILED for user:" + TEST_USER+ " site: " + TEST_URL);
    		throw new Exception(ssoex.getMessage());
		}
    }

    /**
     * <p>
     * Clean properties.
     * </p>
     */
    protected void clean() throws Exception
    {
        // Cleanup any credentails added during the test
        /*
         * try { } catch (SSOException ex) { System.out.println("SSOException" +
         * ex); }
         */
    }

    protected String[] getConfigurations()
    {
        return new String[]
        { "META-INF/sso-dao.xml", "META-INF/transaction.xml"};
    }
}