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

package org.apache.jetspeed.sso;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author   David Le Strat
 */
public abstract class TestSSOComponent extends AbstractSecurityTestCase
{
    /**
     * test url for this UnitTest
     */
    static private String TEST_URL = "http://localhost/jetspeed";

    static private String TEST_URL2 = "http://192.168.2.63/nagios/cgi-bin/status.cgi?hostgroup=all&style=hostdetail";

    static private String TEST_USER = "joe";

    static private String REMOTE_USER = "remoteJS";

    static private String REMOTE_USER2 = "nagiosadmin";

    static private String REMOTE_PWD_1 = "remote_1";

    static private String REMOTE_PWD_2 = "nagiosadmin";

    static private String TEST_GROUP = "engineers";

    static private String TEST_GROUP_USER = "jack";

    /** The property manager. */
    private static SSOManager ssoManager = null;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() throws Exception
    {
        super.setUp();

        try
        {
            ssoManager = scm.lookupComponent("org.apache.jetspeed.sso.SSOManager");
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
        super.tearDown();
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSSOComponent.class);
    }

//    /**
//     * <p>
//     * Test user root.
//     * </p>
//     */
//    public void testSSOGroup() throws Exception
//    {
//        System.out.println("*************************************\n" + "Start Unit Test for SSO Group Support"
//                + "\n*************************************");
//
//        // Create a user
//        try
//        {
//            ums.addUser(TEST_GROUP_USER);
//        }
//        catch (SecurityException sex)
//        {
//            // assertTrue("user already exists. exception caught: " + sex, false);
//        }
//
//        // Create a group
//        try
//        {
//            gms.addGroup(TEST_GROUP);
//            System.out.println("Creating Group " + TEST_GROUP + " and adding User " + TEST_GROUP_USER + " succeeded!.");
//        }
//        catch (SecurityException secex)
//        {
//            System.out.println("Creating Group " + TEST_GROUP + " and adding User " + TEST_GROUP_USER
//                    + " failed. Group might already exist. Continue test...");
//            // secex.printStackTrace();
//            // throw new Exception(secex.getMessage());
//        }
//
//        if (gms.groupExists(TEST_GROUP))
//        {
//            // Add user to Group
//            gms.addUserToGroup(TEST_GROUP_USER, TEST_GROUP);
//        }
//        else
//        {
//            assertTrue("Could not create group. Abort test.", false);
//        }
//
//        // Initialization of Group
//        
//        Principal principal = gms.newTransientGroup(TEST_GROUP);
//        Set principals = new HashSet();
//        principals.add(principal);
//        Subject subject = new Subject(true, principals, new HashSet(), new HashSet());
//
//        // Add SSO Credential for Group
//        if (ssoBroker.hasSSOCredentials(subject, TEST_URL) == false)
//        {
//            try
//            {
//                ssoBroker.addCredentialsForSite(subject, REMOTE_USER, TEST_URL, REMOTE_PWD_1);
//                System.out.println("SSO Credential added for Group:" + TEST_GROUP + " site: " + TEST_URL);
//            }
//            catch (SSOException ssoex)
//            {
//                System.out.println("SSO Credential add FAILED for Group:" + TEST_GROUP + " site: " + TEST_URL);
//                ssoex.printStackTrace();
//                throw new Exception(ssoex.getMessage());
//            }
//        }
//        else
//        {
//            System.out.println("Group:" + TEST_GROUP + " site: " + TEST_URL + " has already a remote credential");
//        }
//
//        // Create Principal for User
//        principal = ums.newTransientUser(TEST_GROUP_USER);
//        principals = new HashSet();
//        principals.add(principal);
//        subject = new Subject(true, principals, new HashSet(), new HashSet());
//
//        // User should have credential for site
//        if (ssoBroker.hasSSOCredentials(subject, TEST_URL) == false)
//        {
//            // Group expansion failed. User not recognized
//            System.out.println("No SSO Credential for user:" + TEST_GROUP_USER + " site: " + TEST_URL);
//
//            // Test failure
//            try
//            {
//                ums.removeUser(TEST_GROUP_USER);
//                gms.removeGroup(TEST_GROUP);
//            }
//            catch (SecurityException sex)
//            {
//                assertTrue("could not remove user and group. exception caught: " + sex, false);
//            }
//
//            throw new Exception("SSO Unit test for Group support failed");
//        }
//        else
//        {
//            // Group lookup succesful
//            System.out.println("SSO Test for Group support successful" + "\nSSO Credential for user:" + TEST_GROUP_USER
//                    + " site: " + TEST_URL + " found. User is member of Group " + TEST_GROUP);
//        }
//
//        // Cleanup test.
//
//        /*
//         * For hypersonic the cascading deletes are not generated by Torque and the remove credentials fails with a
//         * constrGroupPrincipalImplaint error. Comment test out for M1 release but the problem needs to be addressed for the upcoming
//         * releases
//         */
//        try
//        {
//            // Remove credential for Site
//            ssoBroker.removeCredentialsForSite("/group/" + TEST_GROUP, TEST_URL);
//            System.out.println("SSO Credential removed for Group:" + TEST_GROUP + " site: " + TEST_URL);
//        }
//        catch (SSOException ssoex)
//        {
//            System.out.println("SSO Credential remove FAILED for Group:" + TEST_GROUP + " site: " + TEST_URL);
//            throw new Exception(ssoex.getMessage());
//        }
//
//        try
//        {
//            ums.removeUser(TEST_GROUP_USER);
//            gms.removeGroup(TEST_GROUP);
//        }
//        catch (SecurityException sex)
//        {
//            assertTrue("could not remove user and group. exception caught: " + sex, false);
//        }
//
//    }
//
//    public void testSSO() throws Exception
//    {
//        System.out.println("***************************\nStart Unit Test for SSO API\n***************************");
//
//        // Create a user
//        try
//        {
//            ums.addUser(TEST_USER);
//        }
//        catch (SecurityException sex)
//        {
//            // assertTrue("user already exists. exception caught: " + sex, false);
//        }
//
//        // Initialization
//        Principal principal = ums.newTransientUser(TEST_USER);
//        Set principals = new HashSet();
//        principals.add(principal);
//        Subject subject = new Subject(true, principals, new HashSet(), new HashSet());
//
//        if (ssoBroker.hasSSOCredentials(subject, TEST_URL) == false)
//        {
//            System.out.println("No SSO Credential for user:" + TEST_USER + " site: " + TEST_URL);
//
//            // Add credential
//            try
//            {
//                ssoBroker.addCredentialsForSite(subject, REMOTE_USER, TEST_URL, REMOTE_PWD_1);
//                System.out.println("SSO Credential added for user:" + TEST_USER + " site: " + TEST_URL);
//            }
//            catch (SSOException ssoex)
//            {
//                System.out.println("SSO Credential add FAILED for user:" + TEST_USER + " site: " + TEST_URL);
//                ssoex.printStackTrace();
//                throw new Exception(ssoex.getMessage());
//            }
//        }
//        else
//        {
//            System.out.println("SSO Credential found for user:" + TEST_USER + " site: " + TEST_URL);
//        }
//
//        // Add another remote principal for the same user
//        if (ssoBroker.hasSSOCredentials(subject, TEST_URL2) == false)
//        {
//            System.out.println("No SSO Credential for user:" + TEST_USER + " site: " + TEST_URL2);
//
//            // Add credential
//            try
//            {
//                ssoBroker.addCredentialsForSite(subject, REMOTE_USER2, TEST_URL2, REMOTE_PWD_2);
//                ssoBroker.setRealmForSite(TEST_URL2, "Nagios Access");
//                
//                System.out.println("SSO Credential added for user:" + TEST_USER + " site: " + TEST_URL2);
//            }
//            catch (SSOException ssoex)
//            {
//                System.out.println("SSO Credential add FAILED for user:" + TEST_USER + " site: " + TEST_URL2);
//                ssoex.printStackTrace();
//                throw new Exception(ssoex.getMessage());
//            }
//        }
//        else
//        {
//            System.out.println("SSO Credential found for user:" + TEST_USER + " site: " + TEST_URL2);
//        }
//
//        // Add the credentail again -- should get an error
//        try
//        {
//            ssoBroker.addCredentialsForSite(subject, REMOTE_USER2, TEST_URL2, REMOTE_PWD_2);
//            throw new Exception("Added same credentail twice -- API should prevent users from doing that.");
//
//        }
//        catch (SSOException ssoex)
//        {
//            System.out.println("Adding same SSO Credential twice failed (as expected) Message :" + ssoex.getMessage());
//        }
//        catch (Exception e)
//        {
//            throw new Exception("Adding SSO Credential twice throw an unandled exception. Error: " + e.getMessage());
//        }
//
//        // Test if the credential where persisted
//
//        // Test credential update
//        SSOContext ssocontext = ssoBroker.getCredentials(subject, TEST_URL);
//        System.out.println("SSO Credential: User:" + ssocontext.getRemotePrincipalName() + " Password: "
//                + ssocontext.getRemoteCredential() + " for site: " + TEST_URL);
//
//        System.out.println("SSO Credential: User:" + ssocontext.getRemotePrincipalName() + " Password: "
//                + ssocontext.getRemoteCredential() + " for site: " + TEST_URL2);
//
//        try
//        {
//            // Update Remote credential
//            System.out.println("SSO Credential Update");
//            ssoBroker.updateCredentialsForSite(subject, REMOTE_USER, TEST_URL, REMOTE_PWD_2);
//
//            ssocontext = ssoBroker.getCredentials(subject, TEST_URL);
//            System.out.println("SSO Credential updated: User:" + ssocontext.getRemotePrincipalName() + " Password: "
//                    + ssocontext.getRemoteCredential());
//
//        }
//        catch (SSOException ssoex)
//        {
//            System.out.println("SSO Credential update FAILED for user:" + TEST_USER + " site: " + TEST_URL);
//            throw new Exception(ssoex.getMessage());
//        }
//          
//        /*
//         * For hypersonic the cascading deletes are not generated by Torque and the remove credentials fails with a
//         * constraint error. Comment test out for M1 release but the problem needs to be addressed for the upcoming
//         * releases try { // Remove credential for Site ssoBroker.removeCredentialsForSite(subject, TEST_URL);
//         * System.out.println("SSO Credential removed for user:" + TEST_USER+ " site: " + TEST_URL); }
//         * catch(SSOException ssoex) { System.out.println("SSO Credential remove FAILED for user:" + TEST_USER+ " site: " +
//         * TEST_URL); throw new Exception(ssoex.getMessage()); }
//         */
//
//        Iterator sites = ssoBroker.getSites("");
//        while (sites.hasNext())
//        {
//            SSOSite site = (SSOSite) sites.next();
//            System.out.println("Site = " + site.getName());
//        }
//        // Cleanup
//        try
//        {
//        	ssoBroker.removeCredentialsForSite(subject, TEST_URL);
//        	ssoBroker.removeCredentialsForSite(subject, TEST_URL2);
//        	System.out.println("SSO Credential removed for user:" + TEST_USER+ " sites: " + TEST_URL + " " + TEST_URL2); 
//        }
//        catch(SSOException ssoex) 
//        { 
//        	System.out.println("SSO Credential remove FAILED for user:" + TEST_USER+ " site: " + TEST_URL + " and " + TEST_URL2); 
//        	throw new Exception(ssoex.getMessage());
//        }
//
//    }

    /**
     * <p>
     * Clean properties.
     * </p>
     */
    protected void clean() throws Exception
    {
        // Cleanup any credentails added during the test
        /*
         * try { } catch (SSOException ex) { System.out.println("SSOException" + ex); }
         */
    }

    protected String[] getConfigurations()
    {
        String[] confs = super.getConfigurations();
        List confList = new ArrayList(Arrays.asList(confs));
        confList.add("sso.xml");
        return (String[]) confList.toArray(new String[1]);
    }
}
