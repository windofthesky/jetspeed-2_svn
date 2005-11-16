/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
                                                                                                     
import javax.security.auth.Subject;

import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.PrincipalsSet;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestPageXmlPersistence
 * 
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 *          
 */
public class TestSecureDatabasePageManager extends DatasourceEnabledSpringTestCase
{
    private PageManager pageManager;

    private String somePortletId;
    
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestDatabasePageManager.class.getName() });
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();
        pageManager = (PageManager)ctx.getBean("securePageManager");
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestSecureDatabasePageManager.class);
    }
    
    protected String[] getConfigurations()
    {
        return new String[]
        { "secure-database-page-manager.xml", "transaction.xml" };
    }

    public void testSecurePageManager() throws Exception
    {
        // reset page manager cache
        pageManager.reset();

        // setup test subjects
        Principal userPrincipal = new UserPrincipalImpl("admin");
        Principal rolePrincipal = new RolePrincipalImpl("admin");
        Set principals = new PrincipalsSet();
        principals.add(userPrincipal);
        principals.add(rolePrincipal);
        Subject adminSubject = new Subject(true, principals, new HashSet(), new HashSet());

        userPrincipal = new UserPrincipalImpl("user");
        principals = new PrincipalsSet();
        principals.add(userPrincipal);
        Subject userSubject = new Subject(true, principals, new HashSet(), new HashSet());

        userPrincipal = new UserPrincipalImpl("manager");
        rolePrincipal = new RolePrincipalImpl("manager");
        principals = new PrincipalsSet();
        principals.add(userPrincipal);
        principals.add(rolePrincipal);
        Subject managerSubject = new Subject(true, principals, new HashSet(), new HashSet());

        userPrincipal = new UserPrincipalImpl("guest");
        principals = new PrincipalsSet();
        principals.add(userPrincipal);
        Subject guestSubject = new Subject(true, principals, new HashSet(), new HashSet());

        // setup test as admin user
        Exception setup = (Exception)Subject.doAs(adminSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        // create test documents and folders
                        Folder folder = pageManager.newFolder("/");
                        SecurityConstraints constraints = pageManager.newSecurityConstraints();
                        constraints.setOwner("admin");
                        List constraintsRefs = new ArrayList(1);
                        constraintsRefs.add("public-view");
                        constraints.setSecurityConstraintsRefs(constraintsRefs);
                        folder.setSecurityConstraints(constraints);
                        pageManager.updateFolder(folder);
                        
                        PageSecurity pageSecurity = pageManager.newPageSecurity();
                        List constraintsDefs = new ArrayList(2);
                        SecurityConstraintsDef constraintsDef = pageManager.newSecurityConstraintsDef();
                        constraintsDef.setName("public-view");
                        List defConstraints = new ArrayList(1);
                        SecurityConstraint defConstraint = pageManager.newSecurityConstraint();
                        defConstraint.setUsers("*");
                        defConstraint.setPermissions("view");
                        defConstraints.add(defConstraint);
                        constraintsDef.setSecurityConstraints(defConstraints);
                        constraintsDefs.add(constraintsDef);
                        constraintsDef = pageManager.newSecurityConstraintsDef();
                        constraintsDef.setName("admin-all");
                        defConstraints = new ArrayList(1);
                        defConstraint = pageManager.newSecurityConstraint();
                        defConstraint.setRoles("admin");
                        defConstraint.setPermissions("view,edit");
                        defConstraints.add(defConstraint);
                        constraintsDef.setSecurityConstraints(defConstraints);
                        constraintsDefs.add(constraintsDef);
                        pageSecurity.setSecurityConstraintsDefs(constraintsDefs);
                        List globalConstraintsRefs = new ArrayList(1);
                        globalConstraintsRefs.add("admin-all");
                        pageSecurity.setGlobalSecurityConstraintsRefs(globalConstraintsRefs);
                        pageManager.updatePageSecurity(pageSecurity);
                        
                        Page page = pageManager.newPage("/default-page.psml");
                        constraints = pageManager.newSecurityConstraints();
                        constraints.setOwner("admin");
                        List inlineConstraints = new ArrayList(1);
                        SecurityConstraint constraint = pageManager.newSecurityConstraint();
                        constraint.setRoles("manager");
                        constraint.setPermissions("edit");
                        inlineConstraints.add(constraint);
                        constraints.setSecurityConstraints(inlineConstraints);
                        constraintsRefs = new ArrayList(1);
                        constraintsRefs.add("public-view");
                        constraints.setSecurityConstraintsRefs(constraintsRefs);
                        page.setSecurityConstraints(constraints);                        
                        Fragment root = page.getRootFragment();
                        root.setName("jetspeed-layouts::VelocityTwoColumns");
                        Fragment portlet = pageManager.newPortletFragment();
                        portlet.setName("security::LoginPortlet");
                        root.getFragments().add(portlet);
                        portlet = pageManager.newPortletFragment();
                        portlet.setName("some-app::SomePortlet");
                        SecurityConstraints fragmentConstraints = pageManager.newSecurityConstraints();
                        fragmentConstraints.setOwner("user");
                        portlet.setSecurityConstraints(fragmentConstraints);
                        root.getFragments().add(portlet);
                        pageManager.updatePage(page);
                        assertNotNull(page.getRootFragment());
                        assertNotNull(page.getRootFragment().getFragments());
                        assertEquals(2, page.getRootFragment().getFragments().size());
                        assertEquals("some-app::SomePortlet", ((Fragment)page.getRootFragment().getFragments().get(1)).getName());
                        assertFalse("0".equals(((Fragment)page.getRootFragment().getFragments().get(1)).getId()));
                        TestSecureDatabasePageManager.this.somePortletId = ((Fragment)page.getRootFragment().getFragments().get(1)).getId();
                        
                        page = pageManager.newPage("/user-page.psml");
                        constraints = pageManager.newSecurityConstraints();
                        inlineConstraints = new ArrayList(1);
                        constraint = pageManager.newSecurityConstraint();
                        constraint.setUsers("user");
                        constraint.setPermissions("view,edit");
                        inlineConstraints.add(constraint);
                        constraints.setSecurityConstraints(inlineConstraints);
                        page.setSecurityConstraints(constraints);
                        pageManager.updatePage(page);

                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                }
            });
        if (setup != null)
        {
            throw setup;
        }

        // access test as admin user
        Exception adminAccess = (Exception)Subject.doAs(adminSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        // test view access
                        Folder folder = pageManager.getFolder("/");
                        assertNotNull(folder.getPageSecurity());
                        assertNotNull(folder.getPages());
                        assertEquals(2, folder.getPages().size());
                        PageSecurity pageSecurity = pageManager.getPageSecurity();
                        Page page0 = pageManager.getPage("/default-page.psml");
                        assertNotNull(page0.getRootFragment());
                        assertNotNull(page0.getRootFragment().getFragments());
                        assertEquals(2, page0.getRootFragment().getFragments().size());
                        assertNotNull(page0.getFragmentById(TestSecureDatabasePageManager.this.somePortletId));
                        assertNotNull(page0.getFragmentsByName("some-app::SomePortlet"));
                        assertEquals(1, page0.getFragmentsByName("some-app::SomePortlet").size());
                        Page page1 = pageManager.getPage("/user-page.psml");
                        // test edit access
                        pageManager.updateFolder(folder);
                        pageManager.updatePageSecurity(pageSecurity);
                        pageManager.updatePage(page0);
                        pageManager.updatePage(page1);
                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                }
            });
        if (adminAccess != null)
        {
            throw adminAccess;
        }

        // access test as user user
        Exception userAccess = (Exception)Subject.doAs(userSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        // test view access
                        Folder folder = pageManager.getFolder("/");
                        assertNotNull(folder.getPageSecurity());
                        assertNotNull(folder.getPages());
                        assertEquals(2, folder.getPages().size());
                        PageSecurity pageSecurity = pageManager.getPageSecurity();
                        Page page0 = pageManager.getPage("/default-page.psml");
                        assertNotNull(page0.getRootFragment());
                        assertNotNull(page0.getRootFragment().getFragments());
                        assertEquals(2, page0.getRootFragment().getFragments().size());
                        assertNotNull(page0.getFragmentById(TestSecureDatabasePageManager.this.somePortletId));
                        assertNotNull(page0.getFragmentsByName("some-app::SomePortlet"));
                        assertEquals(1, page0.getFragmentsByName("some-app::SomePortlet").size());
                        Page page1 = pageManager.getPage("/user-page.psml");
                        // test edit access
                        try
                        {
                            pageManager.updateFolder(folder);
                            assertTrue("Folder / not editable for user", false);
                        }
                        catch (SecurityException se)
                        {
                        }
                        try
                        {
                            pageManager.updatePageSecurity(pageSecurity);
                            assertTrue("PageSecurity not editable for user", false);
                        }
                        catch (SecurityException se)
                        {
                        }
                        try
                        {
                            pageManager.updatePage(page0);
                            assertTrue("Page /default-page.psml not editable for user", false);
                        }
                        catch (SecurityException se)
                        {
                        }
                        pageManager.updatePage(page1);
                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                }
            });
        if (userAccess != null)
        {
            throw userAccess;
        }

        // access test as manager user
        Exception managerAccess = (Exception)Subject.doAs(managerSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        // test view access
                        Folder folder = pageManager.getFolder("/");
                        assertNotNull(folder.getPageSecurity());
                        assertNotNull(folder.getPages());
                        assertEquals(1, folder.getPages().size());
                        PageSecurity pageSecurity = pageManager.getPageSecurity();
                        Page page0 = pageManager.getPage("/default-page.psml");
                        assertNotNull(page0.getRootFragment());
                        assertNotNull(page0.getRootFragment().getFragments());
                        assertEquals(1, page0.getRootFragment().getFragments().size());
                        assertNull(page0.getFragmentById(TestSecureDatabasePageManager.this.somePortletId));
                        assertNull(page0.getFragmentsByName("some-app::SomePortlet"));
                        try
                        {
                            Page page1 = pageManager.getPage("/user-page.psml");
                            assertTrue("Page /user-page.psml not viewable for manager", false);
                        }
                        catch (SecurityException se)
                        {
                        }                        
                        // test edit access
                        try
                        {
                            pageManager.updateFolder(folder);
                            assertTrue("Folder / not editable for manager", false);
                        }
                        catch (SecurityException se)
                        {
                        }
                        try
                        {
                            pageManager.updatePageSecurity(pageSecurity);
                            assertTrue("PageSecurity not editable for manager", false);
                        }
                        catch (SecurityException se)
                        {
                        }
                        pageManager.updatePage(page0);
                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                }
            });
        if (managerAccess != null)
        {
            throw managerAccess;
        }

        // access test as guest user
        Exception guestAccess = (Exception)Subject.doAs(guestSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        // test view access
                        Folder folder = pageManager.getFolder("/");
                        assertNotNull(folder.getPageSecurity());
                        assertNotNull(folder.getPages());
                        assertEquals(1, folder.getPages().size());
                        PageSecurity pageSecurity = pageManager.getPageSecurity();
                        Page page0 = pageManager.getPage("/default-page.psml");
                        assertNotNull(page0.getRootFragment());
                        assertNotNull(page0.getRootFragment().getFragments());
                        assertEquals(1, page0.getRootFragment().getFragments().size());
                        assertNull(page0.getFragmentById(TestSecureDatabasePageManager.this.somePortletId));
                        assertNull(page0.getFragmentsByName("some-app::SomePortlet"));
                        try
                        {
                            Page page1 = pageManager.getPage("/user-page.psml");
                            assertTrue("Page /user-page.psml not viewable for guest", false);
                        }
                        catch (SecurityException se)
                        {
                        }                        
                        // test edit access
                        try
                        {
                            pageManager.updateFolder(folder);
                            assertTrue("Folder / not editable for guest", false);
                        }
                        catch (SecurityException se)
                        {
                        }
                        try
                        {
                            pageManager.updatePageSecurity(pageSecurity);
                            assertTrue("PageSecurity not editable for guest", false);
                        }
                        catch (SecurityException se)
                        {
                        }
                        try
                        {
                            pageManager.updatePage(page0);
                            assertTrue("Page /default-page.psml not editable for guest", false);
                        }
                        catch (SecurityException se)
                        {
                        }
                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                }
            });
        if (guestAccess != null)
        {
            throw guestAccess;
        }

        // cleanup test as admin user
        Exception cleanup = (Exception)Subject.doAs(adminSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        // cleanup by removing root folder
                        try
                        {
                            Folder remove = pageManager.getFolder("/");
                            assertEquals("/", remove.getPath());
                            pageManager.removeFolder(remove);
                        }
                        catch (FolderNotFoundException e)
                        {
                            assertTrue("Folder / NOT FOUND", false);
                        }

                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                }
            });
        if (cleanup != null)
        {
            throw cleanup;
        }
    }
}
