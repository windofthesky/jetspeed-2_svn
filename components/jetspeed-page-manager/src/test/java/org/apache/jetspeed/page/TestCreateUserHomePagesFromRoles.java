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
package org.apache.jetspeed.page;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.page.psml.CastorXmlPageManager;
import org.apache.jetspeed.test.JetspeedTestCase;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 * TestSecureCastorXmlPageManager
 * 
 * @author <a href="rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class TestCreateUserHomePagesFromRoles extends JetspeedTestCase implements PageManagerTestShared 
{
    protected CastorXmlPageManager pageManager;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        pageManager = Shared.makeCastorXMLPageManager(getBaseDir(), "pages", false, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        Shared.shutdownCastorXMLPageManager(pageManager);
    }

    /**
     * Defines the testcase name for JUnit.
     * 
     * @param name
     *            the testcase's name.
     */
    public TestCreateUserHomePagesFromRoles( String name )
    {
        super(name);
    }

    /**
     * Start the tests.
     * 
     * @param args
     *            the arguments. Not used
     */
    public static void main( String args[] )
    {
        junit.awtui.TestRunner.main(new String[]{TestCreateUserHomePagesFromRoles.class.getName()});
    }

    /**
     * Creates the test suite.
     * 
     * @return a test suite (<code>TestSuite</code>) that includes all
     *         methods starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestCreateUserHomePagesFromRoles.class);
    }

    
    static final String FOLDER1 = Folder.ROLE_FOLDER + "role1";
    static final String FOLDER2 = Folder.ROLE_FOLDER + "role2";
    static final String FOLDER3 = Folder.ROLE_FOLDER + "role3";
        
    static final String DEFAULT_PAGE = Folder.USER_FOLDER + "david" + Folder.PATH_SEPARATOR + "default-page.psml";
    static final String ROLE_PAGE_1 = Folder.USER_FOLDER + "david" + Folder.PATH_SEPARATOR + "role1-default-page.psml";
    static final String ROLE_PAGE_2 = Folder.USER_FOLDER + "david" + Folder.PATH_SEPARATOR + "role2-default-page.psml";
    static final String ROLE_PAGE_3 = Folder.USER_FOLDER + "david" + Folder.PATH_SEPARATOR + "role3-default-page.psml";
    static final String SUB_PAGE = Folder.USER_FOLDER + "david" + Folder.PATH_SEPARATOR + "sub1" + Folder.PATH_SEPARATOR + "default-page.psml";
    static final String SUB_LINK = Folder.USER_FOLDER + "david" + Folder.PATH_SEPARATOR + "sub1" + Folder.PATH_SEPARATOR + "apache_portals.link";    
    
    public void testCreateUserHomePagesFromRoles() throws Exception
    {
        assertTrue("folder1 failed to create", pageManager.folderExists(FOLDER1));
        assertTrue("folder2 failed to create", pageManager.folderExists(FOLDER2));
        assertTrue("folder3 failed to create", pageManager.folderExists(FOLDER3));
        
        Set<Principal> principals = new HashSet<Principal>();
        
        // create the role principals
        principals.add(new TestRole("role1"));
        principals.add(new TestRole("role2"));
        principals.add(new TestRole("role3"));
        
        // create the user principal
        principals.add(new TestUser("david"));
        
        // create the subject
        Subject subject = new Subject(true, principals, new HashSet<Principal>(), new HashSet<Principal>());

        pageManager.createUserHomePagesFromRoles(subject);
        
        assertTrue("failed to create default page", pageManager.pageExists(DEFAULT_PAGE));
        assertTrue("failed to create sub page", pageManager.pageExists(SUB_PAGE));
        assertTrue("failed to create sub link", pageManager.linkExists(SUB_LINK));
    }
    
}
