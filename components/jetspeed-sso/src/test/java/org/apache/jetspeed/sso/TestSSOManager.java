/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PrincipalTypeManager;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.impl.RoleImpl;
import org.apache.jetspeed.sso.impl.SSOUserImpl;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class TestSSOManager extends AbstractSecurityTestCase
{

    public static final String TESTSITE = "testsite";
    public static final String ANOTHER_TESTSITE = "anotherTestSite";

    public static final String TESTUSER = "testuser";
    public static final String ANOTHER_TESTUSER = "anotherTestUser";

    public static final String TESTGROUP = "testgroup";
    public static final String ANOTHER_TESTGROUP = "anotherTestGroup";

    /** The property manager. */
    private static SSOManager ssoManager = null;
    
    private User testuser;
    private User anotherTestuser;
    
    private Group testgroup;
    private Group anotherTestgroup;
    
    private SSOSite testSite;
    private SSOSite anotherTestSite;
    
    /*
     * 
     * 

X    SSOUser getRemoteUser(SSOSite site, JetspeedPrincipal principal);

X    SSOUser getRemoteUser(SSOSite site, String remoteUserName);

X    Collection<SSOUser> getRemoteUsers(SSOSite site, Subject subject) throws SSOException;

X    PasswordCredential getCredentials(SSOUser user) throws SSOException;

X    Collection<SSOSite> getSitesForPrincipal(JetspeedPrincipal principal);

     Collection<SSOSite> getSitesForSubject(Subject subject) throws SSOException;
     
X    Collection<SSOUser> getUsersForSite(SSOSite site);
     
X    Collection<SSOSite> getSites(String filter);
     
X    SSOSite getSiteByUrl(String siteUrl);

X    SSOSite getSiteByName(String siteName);

X    void setCredentials(SSOUser user, String pwd) throws SSOException;
     
X    void removeUser(SSOUser remoteUser) throws SSOException;
     
X    void updateSite(SSOSite site) throws SSOException;
     
X    void addSite(String siteName, String siteUrl) throws SSOException; 
     
X    void addSite(Long ownerDomainId, String siteName, String siteUrl) throws SSOException;
     
X    void removeSite(SSOSite site) throws SSOException;
     
X    void addUser(SSOSite site, JetspeedPrincipal localPrincipal, String ssoUsername, String ssoUserPassword) throws SSOException;

    Collection<JetspeedPrincipal> getPortalPrincipals(SSOUser user);

     
     * 
     * 
 
     
     */
    public void testAddAndFetchSites() throws Exception
    {
        addTestUser();
        addAnotherTestUser();
        addTestSite();
        addAnotherTestSite();
        
        // test fetching a single site
        SSOSite site = ssoManager.getSiteByName(TESTSITE);
        assertEquals(TESTSITE,site.getName());
        assertEquals("http://www.blah.com",site.getURL());
        assertNotNull(site.getSecurityDomainId());

        ssoManager.addSite(ssoManager.newSite("foo", "http://www.foo.com"));
        site = ssoManager.getSiteByName("foo");
        assertEquals("foo",site.getName());
        assertEquals("http://www.foo.com",site.getURL());
        assertNotNull(site.getSecurityDomainId());
        
        site = ssoManager.getSiteById(site.getId());
        assertNotNull(site);
        
        site = ssoManager.getSiteByUrl("http://www.foo.com");
        assertEquals("foo",site.getName());
        
        // test fetching multiple sites
        ssoManager.addUser(testSite, testuser, "someRemoteUser", "someRemotePwd");
        
        Collection<SSOSite> sites = ssoManager.getSitesForPrincipal(testuser);
        
        assertEquals(1,sites.size());
        
        assertEquals(TESTSITE,sites.iterator().next().getName());
        
        sites = ssoManager.getSitesForPrincipal(anotherTestuser);
        
        assertEquals(0,sites.size());

        ssoManager.addUser(testSite, anotherTestuser, "remoteUserForAnotherLocalUser", "anotherRemotePwd");
        ssoManager.addUser(anotherTestSite, anotherTestuser, "someRemoteUser", "someRemotePwd");

        sites = ssoManager.getSitesForPrincipal(anotherTestuser);
        
        assertEquals(2,sites.size());
        
        ssoManager.removeSite(testSite);
        
        site = ssoManager.getSiteByName(TESTSITE);
        assertNull(site);
        
        sites = ssoManager.getSitesForPrincipal(anotherTestuser);
        
        assertEquals(1,sites.size());
        
        assertEquals(ANOTHER_TESTSITE,sites.iterator().next().getName());
        
    }
    
    public void testWildCardSiteSearch() throws Exception 
    {

        // test fetching sites with wildcard searches
        ssoManager.addSite(ssoManager.newSite("Our Website", "http://portals.apache.org/jetspeed-2"));
        ssoManager.addSite(ssoManager.newSite("Their Website", "http://some.other.portal/"));
        ssoManager.addSite(ssoManager.newSite("Foo", "http://www.site.com"));
        ssoManager.addSite(ssoManager.newSite("F%oB%r", "http://www.site-name-with-weird-chars.com"));
        
        // match name
        
        Collection<SSOSite> sites = ssoManager.getSites("Website");
        assertEquals(2, sites.size());

        // matches both url and name
        sites = ssoManager.getSites("site");
        assertEquals(4, sites.size());
        
        // matches only url
        sites = ssoManager.getSites("portal");
        assertEquals(2, sites.size());

        // matches nothing
        sites = ssoManager.getSites("nothing");
        assertEquals(0, sites.size());

        // matches sites with % in name or url
        sites = ssoManager.getSites("%oB");
        assertEquals(1, sites.size());

    }
    
    public void testUpdateSite() throws Exception 
    {
        addTestSite();
        
        SSOSite s = ssoManager.getSiteByName(TESTSITE);

        s.setURL("SomeOtherUrl");
        ssoManager.updateSite(s);
        s = ssoManager.getSiteByName(TESTSITE);
        assertEquals("SomeOtherUrl", s.getURL());
        
        s.setName("otherName");
        ssoManager.updateSite(s);
        s = ssoManager.getSiteByName("otherName");
        assertEquals("SomeOtherUrl", s.getURL());
        
        s.setAllowUserSet(true);
        s.setCertificateRequired(true);
        s.setChallengeResponseAuthentication(true);
        s.setFormAuthentication(true);
        s.setFormPwdField("formPwdField");
        s.setFormUserField("formUserField");
        s.setRealm("realm");
        s.setURL("siteURL");
        
        ssoManager.updateSite(s);
        s = ssoManager.getSiteByUrl("siteURL");
        assertTrue(s.isAllowUserSet());
        assertTrue(s.isCertificateRequired());
        assertTrue(s.isChallengeResponseAuthentication());
        assertTrue(s.isFormAuthentication());
        assertEquals("formPwdField",s.getFormPwdField());
        assertEquals("formUserField",s.getFormUserField());
        assertEquals("realm",s.getRealm());
        assertEquals("siteURL",s.getURL());
    }
    
    public void testFecthSitesForSubject() throws Exception 
    {
        addTestUser();
        addAnotherTestUser();
        addTestSite();
        addAnotherTestSite();
        addTestGroup();        
        
        ssoManager.addUser(testSite, testuser, "userForTestuserPrincipal", "x");
        ssoManager.addUser(testSite, testgroup, "foo", "x");
        ssoManager.addUser(anotherTestSite, testgroup, "userForTestgroupPrincipal", "x");
        
        Subject s = new Subject();
        s.getPrincipals().add(testuser);
        s.getPrincipals().add(testuser); // add twice 
        s.getPrincipals().add(testgroup);
        // the following principal should just be ignored
        s.getPrincipals().add(new Principal(){

            public String getName()
            {
                return "someNonJetspeedPrincipal";
            }
            
        });
        
        Collection<SSOSite> sites = ssoManager.getSitesForSubject(s);
        assertEquals(2, sites.size());
    }
    
    public void testAddAndFetchUsers() throws Exception 
    {
        addTestUser();
        addAnotherTestUser();
        addTestSite();
        addAnotherTestSite();
        addTestGroup();        
        
        Collection<SSOUser> users = ssoManager.getRemoteUsers(testSite, testuser);
        assertEquals(0, users.size());

        
        User subjectUser = ums.addUser("userForSubjectTest");
        Group subjectGroup = gms.addGroup("groupForSubjectTest");
        
        ssoManager.addUser(testSite, subjectUser, "userForTestuserPrincipal", "x");
        ssoManager.addUser(testSite, subjectGroup, "userForTestgroupPrincipal", "x");
        
        Subject s = new Subject();
        s.getPrincipals().add(subjectUser);
        s.getPrincipals().add(subjectUser); // add twice 
        s.getPrincipals().add(subjectGroup);
        // the following principal should just be ignored
        s.getPrincipals().add(new Principal()
        {
            public String getName()
            {
                return "someNonJetspeedPrincipal";
            }
            
        });
        // add a Jetspeed role principal ==> should be ignored as well
        s.getPrincipals().add(new RoleImpl("someRole"));
        
        users = ssoManager.getRemoteUsers(testSite,s);
        assertEquals(2,users.size());

        ssoManager.addUser(testSite, testuser, "someRemoteUser", "someRemotePwd");
        
        // fetch user by local principal
        users = ssoManager.getRemoteUsers(testSite, testuser);
        assertEquals(1,users.size());

        // try the other method for fetching a user, by remote user name
        SSOUser someRemoteUser = ssoManager.getRemoteUser(testSite, "someRemoteUser");
        assertNotNull(someRemoteUser);
    }

    public void testAddDuplicateSite() throws Exception 
    {        
        ssoManager.addSite(ssoManager.newSite(TESTSITE, "http://www.blah.com"));

        try{
            ssoManager.addSite(ssoManager.newSite(TESTSITE, "http://www.blah.com"));
            assertTrue(false);
        } catch (Exception e){
            // caught exception, test succeeded
        }

        // test with a different name, same url
        try{
            ssoManager.addSite(ssoManager.newSite("foo", "http://www.blah.com"));
            assertTrue(false);
        } catch (Exception e){
            // caught exception, test succeeded
        }
    }
    
    /**
     * Tests SSOManager.testAddUserForLocalGroup()
     */
    public void testAddUserForLocalGroup() throws Exception 
    {
        addTestGroup();
        addTestSite(); 
        
        ssoManager.addUser(testSite, testgroup, "someRemoteUser", "someRemotePwd");
        
        // fetch user by local principal
        Collection<SSOUser> remoteUsers = ssoManager.getRemoteUsers(testSite, testgroup);
        assertEquals(1, remoteUsers.size());

        // try the other method for fetching a user, by remote user name
        SSOUser someRemoteUser = ssoManager.getRemoteUser(testSite, "someRemoteUser");
        assertNotNull(someRemoteUser);
    }
    
    public void testRemoveUser() throws Exception 
    {
        addTestUser();
        addTestSite();
        
        ssoManager.addUser(testSite, testuser, "someRemoteUser", "someRemotePwd");
        SSOUser fetchedUser = ssoManager.getRemoteUser(testSite, "someRemoteUser");
        
        // verify the user is really added
        assertNotNull(fetchedUser);
        ssoManager.removeUser(fetchedUser);
        
        SSOUser fetchedRemovedUser = ssoManager.getRemoteUser(testSite, "someRemoteUser");
        assertNull(fetchedRemovedUser);
        
        try{
            ssoManager.removeUser(fetchedUser);
            assertTrue(false); // generate a test error since remove should throw exception
        } catch (SSOException secex){
            
        }
    }

    /**
     * Tests SSOManager.addUser()
     */
    public void testAddDuplicateUser() throws Exception 
    {
        addTestUser();
        addTestSite();
        
        ssoManager.addUser(testSite, testuser, "someRemoteUser", "someRemotePwd");

        try
        {
            ssoManager.addUser(testSite, testuser, "someRemoteUser", "whatever");
            throw new Exception("Test failed: duplicate user was added");
        } 
        catch (SSOException ssoe)
        {
            
        }
        Collection<SSOUser> remoteUsers = ssoManager.getRemoteUsers(testSite, testuser);
        assertEquals(1,remoteUsers.size());
        
        // test adding remote user with other local user
        addAnotherTestUser();
        try
        {
            ssoManager.addUser(testSite, anotherTestuser, "someRemoteUser", "myOwnPwd");
            throw new Exception("Test failed: duplicate user was added");
        } 
        catch (SSOException ssoe)
        {            
        }        
    }
    
    public void testCredentials() throws Exception 
    {
        addTestUser();
        addTestSite();
        
        SSOUser someRemoteUser = ssoManager.addUser(testSite, testuser, "someRemoteUser", "someRemotePwd");
        PasswordCredential pwd = ssoManager.getCredentials(someRemoteUser);
        assertEquals("someRemotePwd",pwd.getPassword());
        
        ssoManager.setPassword(someRemoteUser,"anotherPassword");
        pwd = ssoManager.getCredentials(someRemoteUser);
        assertEquals("anotherPassword",pwd.getPassword());

    }
    
    public void testFetchPortalPrincipals() throws Exception 
    {
        addTestUser();
        addAnotherTestUser();
        addTestGroup();
        addAnotherTestGroup();
        addTestSite();
        
        SSOUser addedUser = ssoManager.addUser(testSite, testuser, "someRemoteUser", "someRemotePwd");
        ssoManager.addAssociation(addedUser, anotherTestuser);
        ssoManager.addAssociation(addedUser, anotherTestuser);
        ssoManager.addAssociation(addedUser, testgroup);
        ssoManager.addAssociation(addedUser, anotherTestgroup);
        
        Collection<JetspeedPrincipal> fetchedPrincipals = ssoManager.getPortalPrincipals(addedUser);
        assertEquals(4,fetchedPrincipals.size());

        SSOUserImpl nonExistingUser = new SSOUserImpl();
        nonExistingUser.setName("fake");
        nonExistingUser.setDomainId(addedUser.getDomainId());
        fetchedPrincipals = ssoManager.getPortalPrincipals(nonExistingUser);
        assertEquals(0,fetchedPrincipals.size());
        

    }
    
    public void addTestSite() throws Exception
    {
        testSite = ssoManager.addSite(ssoManager.newSite(TESTSITE, "http://www.blah.com"));
    }

    public void addAnotherTestSite() throws Exception
    {
        anotherTestSite = ssoManager.addSite(ssoManager.newSite(ANOTHER_TESTSITE, "http://www.alternative.com"));
    }

    public void addTestUser() throws Exception
    {
        testuser = ums.addUser(TESTUSER);
    }

    public void addAnotherTestUser() throws Exception
    {
        anotherTestuser = ums.addUser(ANOTHER_TESTUSER);
    }

    public void addTestGroup() throws Exception
    {
        testgroup = gms.addGroup(TESTGROUP);
    }

    public void addAnotherTestGroup() throws Exception
    {
        anotherTestgroup = gms.addGroup(ANOTHER_TESTGROUP);
    }
    
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


        clean();

    }

   
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        clean();
        super.tearDown();
        
        testuser=null;
        anotherTestuser=null;
        
        testgroup=null;
        anotherTestgroup=null;
        
        testSite=null;
        anotherTestSite=null;
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSSOManager.class);
    }

    protected void tryRemovePrincipal(PrincipalTypeManager pman, String pname)
    {
        try
        {
            pman.getPrincipalManager().removePrincipal(pname);
        } 
        catch (Exception e)
        {            
        }
    }
    
    /**
     * <p>
     * Clean properties.
     * </p>
     */
    protected void clean() throws Exception
    {
        // remove SSO Sites and SSO Users attached to each site.
        
        Collection<SSOSite> sites = ssoManager.getSites("");
        if (sites != null){
            for (SSOSite site : sites)
            {
                Collection<SSOUser> users = ssoManager.getUsersForSite(site);
                for (SSOUser user : users)
                {
                    ssoManager.removeUser(user);
                }
                ssoManager.removeSite(site);
            }
        }
        
        tryRemovePrincipal(ums,"testuser");
        tryRemovePrincipal(ums,"anotherTestuser");
        
        tryRemovePrincipal(gms,"testgroup");
        tryRemovePrincipal(gms,"anotherTestgroup");
        
    }

    protected String[] getConfigurations()
    {
        String[] confs = super.getConfigurations();
        List confList = new ArrayList(Arrays.asList(confs));
        confList.add("sso.xml");
        return (String[]) confList.toArray(new String[1]);
    }
}
