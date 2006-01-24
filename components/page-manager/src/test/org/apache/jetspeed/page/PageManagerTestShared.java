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

import java.io.File;
import java.io.FileFilter;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.security.auth.Subject;

import junit.framework.TestCase;

import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.psml.FolderMetaDataImpl;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.om.page.psml.LinkImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.om.page.psml.PageSecurityImpl;
import org.apache.jetspeed.page.document.DocumentHandler;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.document.psml.CastorFileSystemDocumentHandler;
import org.apache.jetspeed.page.document.psml.DocumentHandlerFactoryImpl;
import org.apache.jetspeed.page.document.psml.FileSystemFolderHandler;
import org.apache.jetspeed.page.psml.CastorXmlPageManager;
import org.apache.jetspeed.security.FolderPermission;
import org.apache.jetspeed.security.FragmentPermission;
import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.PagePermission;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.PrincipalsSet;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.util.DirectoryHelper;

/**
 * PageManagerTestShared
 * 
 * @author <a href="rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 *          
 */
interface PageManagerTestShared
{
    class Shared
    {
        /**
         * makeCastorXMLPageManager
         *
         * Create and configure a Castor XML PageManager.
         *
         * @param pagesDirName
         * @param permissionsEnabled
         * @param constraintsEnabled
         * @return page manager instance
         */
        static CastorXmlPageManager makeCastorXMLPageManager(String pagesDirName, boolean permissionsEnabled, boolean constraintsEnabled)
            throws Exception
        {
            File pagesDirFile = new File("target/testdata/" + pagesDirName);
            DirectoryHelper dirHelper = new DirectoryHelper(pagesDirFile);
            FileFilter noCVSorSVNorBackups = new FileFilter()
                {
                    public boolean accept( File pathname )
                    {
                        return !pathname.getName().equals("CVS") && !pathname.getName().equals(".svn") && !pathname.getName().endsWith("~");
                    }
                };
            dirHelper.copyFrom(new File("testdata/" + pagesDirName), noCVSorSVNorBackups);
            IdGenerator idGen = new JetspeedIdGenerator(65536,"P-","");
            FileCache cache = new FileCache(10, 12);
            
            DocumentHandler psmlHandler = new CastorFileSystemDocumentHandler("/JETSPEED-INF/castor/page-mapping.xml", Page.DOCUMENT_TYPE, PageImpl.class, "target/testdata/" + pagesDirName, cache);
            DocumentHandler linkHandler = new CastorFileSystemDocumentHandler("/JETSPEED-INF/castor/page-mapping.xml", Link.DOCUMENT_TYPE, LinkImpl.class, "target/testdata/" + pagesDirName, cache);
            DocumentHandler folderMetaDataHandler = new CastorFileSystemDocumentHandler("/JETSPEED-INF/castor/page-mapping.xml", FolderMetaDataImpl.DOCUMENT_TYPE, FolderMetaDataImpl.class, "target/testdata/" + pagesDirName, cache);
            DocumentHandler pageSecurityHandler = new CastorFileSystemDocumentHandler("/JETSPEED-INF/castor/page-mapping.xml", PageSecurityImpl.DOCUMENT_TYPE, PageSecurity.class, "target/testdata/" + pagesDirName, cache);
            
            DocumentHandlerFactory handlerFactory = new DocumentHandlerFactoryImpl();
            handlerFactory.registerDocumentHandler(psmlHandler);
            handlerFactory.registerDocumentHandler(linkHandler);
            handlerFactory.registerDocumentHandler(folderMetaDataHandler);
            handlerFactory.registerDocumentHandler(pageSecurityHandler);
            FolderHandler folderHandler = new FileSystemFolderHandler("target/testdata/" + pagesDirName, handlerFactory, cache);

            return new CastorXmlPageManager(idGen, handlerFactory, folderHandler, cache, permissionsEnabled, constraintsEnabled);
        }

        /**
         * makeListFromCSV
         *
         * Create List of String values from CSV String for principals/permissions.
         * 
         * @param csv CSV string
         * @return values list
         */
        static List makeListFromCSV(String csv)
        {
            if (csv != null)
            {
                List csvList = new ArrayList();
                if (csv.indexOf(',') != -1)
                {
                    StringTokenizer csvTokens = new StringTokenizer(csv, ",");
                    while (csvTokens.hasMoreTokens())
                    {
                        csvList.add(csvTokens.nextToken().trim());
                    }
                }
                else
                {
                    csvList.add(csv);
                }
                return csvList;
            }
            return null;        
        }

        /**
         * makeCSVFromList
         *
         * Create CSV String for principals/permissions from List of String values
         * 
         * @param list values list
         * @return CSV string
         */
        static String makeCSVFromList(List list)
        {
            if ((list != null) && !list.isEmpty())
            {
                StringBuffer csv = new StringBuffer();
                Iterator listIter = list.iterator();
                while (listIter.hasNext())
                {
                    if (csv.length() > 0)
                    {
                        csv.append(",");
                    }
                    csv.append((String)listIter.next());
                }
                return csv.toString();
            }
            return null;
        }

        /**
         * testSecurePageManager
         *
         * @param test case
         * @param page manager
         */
        static void testSecurePageManager(final TestCase test, final PageManager pageManager) throws Exception
        {
            // tracking
            final String [] somePortletId = new String[1];

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
            Exception setup = (Exception)Subject.doAsPrivileged(adminSubject, new PrivilegedAction()
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
                            SecurityConstraint defConstraint = pageManager.newPageSecuritySecurityConstraint();
                            defConstraint.setUsers(Shared.makeListFromCSV("*"));
                            defConstraint.setPermissions(Shared.makeListFromCSV("view"));
                            defConstraints.add(defConstraint);
                            constraintsDef.setSecurityConstraints(defConstraints);
                            constraintsDefs.add(constraintsDef);
                            constraintsDef = pageManager.newSecurityConstraintsDef();
                            constraintsDef.setName("admin-all");
                            defConstraints = new ArrayList(1);
                            defConstraint = pageManager.newPageSecuritySecurityConstraint();
                            defConstraint.setRoles(Shared.makeListFromCSV("admin"));
                            defConstraint.setPermissions(Shared.makeListFromCSV("view,edit"));
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
                            SecurityConstraint constraint = pageManager.newPageSecurityConstraint();
                            constraint.setRoles(Shared.makeListFromCSV("manager"));
                            constraint.setPermissions(Shared.makeListFromCSV("edit"));
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
                            test.assertNotNull(page.getRootFragment());
                            test.assertNotNull(page.getRootFragment().getFragments());
                            test.assertEquals(2, page.getRootFragment().getFragments().size());
                            test.assertEquals("some-app::SomePortlet", ((Fragment)page.getRootFragment().getFragments().get(1)).getName());
                            test.assertFalse("0".equals(((Fragment)page.getRootFragment().getFragments().get(1)).getId()));
                            somePortletId[0] = ((Fragment)page.getRootFragment().getFragments().get(1)).getId();
                        
                            page = pageManager.newPage("/user-page.psml");
                            constraints = pageManager.newSecurityConstraints();
                            inlineConstraints = new ArrayList(1);
                            constraint = pageManager.newPageSecurityConstraint();
                            constraint.setUsers(Shared.makeListFromCSV("user"));
                            constraint.setPermissions(Shared.makeListFromCSV("view,edit"));
                            inlineConstraints.add(constraint);
                            constraints.setSecurityConstraints(inlineConstraints);
                            page.setSecurityConstraints(constraints);
                            pageManager.updatePage(page);

                            Link link = pageManager.newLink("/default.link");
                            link.setUrl("http://www.default.org/");
                            constraints = pageManager.newSecurityConstraints();
                            constraints.setOwner("admin");
                            inlineConstraints = new ArrayList(1);
                            constraint = pageManager.newLinkSecurityConstraint();
                            constraint.setRoles(Shared.makeListFromCSV("manager"));
                            constraint.setPermissions(Shared.makeListFromCSV("edit"));
                            inlineConstraints.add(constraint);
                            constraints.setSecurityConstraints(inlineConstraints);
                            link.setSecurityConstraints(constraints);                        
                            pageManager.updateLink(link);

                            return null;
                        }
                        catch (Exception e)
                        {
                            return e;
                        }
                    }
                }, null);
            if (setup != null)
            {
                throw setup;
            }

            // reset page manager cache
            pageManager.reset();

            // access test as admin user
            Exception adminAccess = (Exception)Subject.doAsPrivileged(adminSubject, new PrivilegedAction()
                {
                    public Object run()
                    {
                        try
                        {
                            // test view access
                            Folder folder = pageManager.getFolder("/");
                            test.assertNotNull(folder.getPageSecurity());
                            test.assertNotNull(folder.getPages());
                            test.assertEquals(2, folder.getPages().size());
                            PageSecurity pageSecurity = pageManager.getPageSecurity();
                            Page page0 = pageManager.getPage("/default-page.psml");
                            test.assertNotNull(page0.getRootFragment());
                            test.assertNotNull(page0.getRootFragment().getFragments());
                            test.assertEquals(2, page0.getRootFragment().getFragments().size());
                            test.assertNotNull(page0.getFragmentById(somePortletId[0]));
                            test.assertNotNull(page0.getFragmentsByName("some-app::SomePortlet"));
                            test.assertEquals(1, page0.getFragmentsByName("some-app::SomePortlet").size());
                            Page page1 = pageManager.getPage("/user-page.psml");
                            Link link = pageManager.getLink("/default.link");
                            // test edit access
                            pageManager.updateFolder(folder);
                            pageManager.updatePageSecurity(pageSecurity);
                            pageManager.updatePage(page0);
                            pageManager.updatePage(page1);
                            pageManager.updateLink(link);
                            return null;
                        }
                        catch (Exception e)
                        {
                            return e;
                        }
                    }
                }, null);
            if (adminAccess != null)
            {
                throw adminAccess;
            }

            // access test as user user
            Exception userAccess = (Exception)Subject.doAsPrivileged(userSubject, new PrivilegedAction()
                {
                    public Object run()
                    {
                        try
                        {
                            // test view access
                            Folder folder = pageManager.getFolder("/");
                            test.assertNotNull(folder.getPageSecurity());
                            test.assertNotNull(folder.getPages());
                            test.assertEquals(2, folder.getPages().size());
                            PageSecurity pageSecurity = pageManager.getPageSecurity();
                            Page page0 = pageManager.getPage("/default-page.psml");
                            test.assertNotNull(page0.getRootFragment());
                            test.assertNotNull(page0.getRootFragment().getFragments());
                            test.assertEquals(2, page0.getRootFragment().getFragments().size());
                            test.assertNotNull(page0.getFragmentById(somePortletId[0]));
                            test.assertNotNull(page0.getFragmentsByName("some-app::SomePortlet"));
                            test.assertEquals(1, page0.getFragmentsByName("some-app::SomePortlet").size());
                            Page page1 = pageManager.getPage("/user-page.psml");
                            Link link = pageManager.getLink("/default.link");
                            // test edit access
                            try
                            {
                                pageManager.updateFolder(folder);
                                test.assertTrue("Folder / not editable for user", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            try
                            {
                                pageManager.updatePageSecurity(pageSecurity);
                                test.assertTrue("PageSecurity not editable for user", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            try
                            {
                                pageManager.updatePage(page0);
                                test.assertTrue("Page /default-page.psml not editable for user", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            pageManager.updatePage(page1);
                            try
                            {
                                pageManager.updateLink(link);
                                test.assertTrue("Page /default.link not editable for user", false);
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
                }, null);
            if (userAccess != null)
            {
                throw userAccess;
            }

            // access test as manager user
            Exception managerAccess = (Exception)Subject.doAsPrivileged(managerSubject, new PrivilegedAction()
                {
                    public Object run()
                    {
                        try
                        {
                            // test view access
                            Folder folder = pageManager.getFolder("/");
                            test.assertNotNull(folder.getPageSecurity());
                            test.assertNotNull(folder.getPages());
                            test.assertEquals(1, folder.getPages().size());
                            PageSecurity pageSecurity = pageManager.getPageSecurity();
                            Page page0 = pageManager.getPage("/default-page.psml");
                            test.assertNotNull(page0.getRootFragment());
                            test.assertNotNull(page0.getRootFragment().getFragments());
                            test.assertEquals(1, page0.getRootFragment().getFragments().size());
                            test.assertNull(page0.getFragmentById(somePortletId[0]));
                            test.assertNull(page0.getFragmentsByName("some-app::SomePortlet"));
                            Link link = pageManager.getLink("/default.link");
                            try
                            {
                                Page page1 = pageManager.getPage("/user-page.psml");
                                test.assertTrue("Page /user-page.psml not viewable for manager", false);
                            }
                            catch (SecurityException se)
                            {
                            }                        
                            // test edit access
                            try
                            {
                                pageManager.updateFolder(folder);
                                test.assertTrue("Folder / not editable for manager", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            try
                            {
                                pageManager.updatePageSecurity(pageSecurity);
                                test.assertTrue("PageSecurity not editable for manager", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            pageManager.updatePage(page0);
                            pageManager.updateLink(link);
                            return null;
                        }
                        catch (Exception e)
                        {
                            return e;
                        }
                    }
                }, null);
            if (managerAccess != null)
            {
                throw managerAccess;
            }

            // access test as guest user
            Exception guestAccess = (Exception)Subject.doAsPrivileged(guestSubject, new PrivilegedAction()
                {
                    public Object run()
                    {
                        try
                        {
                            // test view access
                            Folder folder = pageManager.getFolder("/");
                            test.assertNotNull(folder.getPageSecurity());
                            test.assertNotNull(folder.getPages());
                            test.assertEquals(1, folder.getPages().size());
                            PageSecurity pageSecurity = pageManager.getPageSecurity();
                            Page page0 = pageManager.getPage("/default-page.psml");
                            test.assertNotNull(page0.getRootFragment());
                            test.assertNotNull(page0.getRootFragment().getFragments());
                            test.assertEquals(1, page0.getRootFragment().getFragments().size());
                            test.assertNull(page0.getFragmentById(somePortletId[0]));
                            test.assertNull(page0.getFragmentsByName("some-app::SomePortlet"));
                            Link link = pageManager.getLink("/default.link");
                            try
                            {
                                Page page1 = pageManager.getPage("/user-page.psml");
                                test.assertTrue("Page /user-page.psml not viewable for guest", false);
                            }
                            catch (SecurityException se)
                            {
                            }                        
                            // test edit access
                            try
                            {
                                pageManager.updateFolder(folder);
                                test.assertTrue("Folder / not editable for guest", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            try
                            {
                                pageManager.updatePageSecurity(pageSecurity);
                                test.assertTrue("PageSecurity not editable for guest", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            try
                            {
                                pageManager.updatePage(page0);
                                test.assertTrue("Page /default-page.psml not editable for guest", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            try
                            {
                                pageManager.updateLink(link);
                                test.assertTrue("Page /default.link not editable for guest", false);
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
                }, null);
            if (guestAccess != null)
            {
                throw guestAccess;
            }

            // reset page manager cache
            pageManager.reset();

            // cleanup test as admin user
            Exception cleanup = (Exception)Subject.doAsPrivileged(adminSubject, new PrivilegedAction()
                {
                    public Object run()
                    {
                        try
                        {
                            // cleanup by removing root folder
                            try
                            {
                                Folder remove = pageManager.getFolder("/");
                                test.assertEquals("/", remove.getPath());
                                pageManager.removeFolder(remove);
                            }
                            catch (FolderNotFoundException e)
                            {
                                test.assertTrue("Folder / NOT FOUND", false);
                            }

                            return null;
                        }
                        catch (Exception e)
                        {
                            return e;
                        }
                    }
                }, null);
            if (cleanup != null)
            {
                throw cleanup;
            }
        }
    }

    /**
     * PageManagerPermissionsPolicy
     *
     * Policy implementation for permissions based security
     * tests against testSecurePageManager test case above. 
     */
    static class PageManagerPermissionsPolicy extends Policy
    {
        private Policy defaultPolicy;

        public PageManagerPermissionsPolicy(Policy defaultPolicy)
        {
            this.defaultPolicy = defaultPolicy;
        }

        public boolean implies(ProtectionDomain domain, Permission permission)
        {
            // classify policy query for local test case; this implementation
            // is not optimized: multiple protection domains exist on the
            // call stack, so this method will be invoked 2-3 times for each
            // access check with the identical principals and permission
            Principal[] principals = domain.getPrincipals();
            if ((principals != null) && (principals.length > 0) &&
                ((permission instanceof FolderPermission) ||
                 (permission instanceof PagePermission) ||
                 (permission instanceof FragmentPermission)))
            {
                // check permission using principals if available
                Permissions permissions = new Permissions();
                for (int i = 0; (i < principals.length); i++)
                {
                    if (principals[i] instanceof UserPrincipal)
                    {
                        // get permissions for users
                        String user = principals[i].getName();
                        if (user.equals("admin"))
                        {
                            // owner permissions
                            permissions.add(new FolderPermission("/", "view, edit"));
                            permissions.add(new PagePermission("/default-page.psml", "view, edit"));
                        }
                        else if (user.equals("user"))
                        {
                            // owner permissions
                            permissions.add(new FragmentPermission("/default-page.psml/some-app::SomePortlet", "view, edit"));
                            
                            // granted permissions
                            permissions.add(new PagePermission("/user-page.psml", "view, edit"));
                            permissions.add(new FragmentPermission("/user-page.psml/*", "view"));
                        }
                        
                        // public view permissions
                        permissions.add(new FolderPermission("/", "view"));
                        permissions.add(new PagePermission("/default-page.psml", "view"));
                        permissions.add(new PagePermission("/page.security", "view"));
                        permissions.add(new FragmentPermission("security::*", "view"));
                    }
                    else if (principals[i] instanceof RolePrincipal)
                    {
                        // get permissions for roles
                        String role = principals[i].getName();
                        if (role.equals("admin"))
                        {
                            // global permissions
                            permissions.add(new FolderPermission("<<ALL FILES>>", "view, edit"));
                            permissions.add(new FragmentPermission("<<ALL FRAGMENTS>>", "view, edit"));
                        }
                        else if (role.equals("manager"))
                        {
                            // granted permissions
                            permissions.add(new PagePermission("/default-page.psml", "edit"));
                            permissions.add(new PagePermission("/default.link", "edit"));
                        }
                    }
                }
                
                // check permission
                if (permissions.implies(permission))
                {
                    return true;
                }
            }

            // check default permissions
            if (defaultPolicy != null)
            {
                return defaultPolicy.implies(domain, permission);
            }
            return false;
        }

        public PermissionCollection getPermissions(ProtectionDomain domain)
        {
            // return default permissions only since
            // domain and permsission not available
            if (defaultPolicy != null)
            {
                return defaultPolicy.getPermissions(domain);
            }
            return new Permissions();
        }

        public PermissionCollection getPermissions(CodeSource codesource)
        {
            // return default permissions only since
            // domain and permsission not available
            if (defaultPolicy != null)
            {
                return defaultPolicy.getPermissions(codesource);
            }
            return new Permissions();
        }

        public void refresh()
        {
            // propagate refresh
            if (defaultPolicy != null)
            {
                defaultPolicy.refresh();
            }
        }
    }
}
