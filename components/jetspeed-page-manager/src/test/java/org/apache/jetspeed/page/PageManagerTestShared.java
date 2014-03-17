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

import junit.framework.TestCase;
import net.sf.ehcache.CacheManager;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.impl.EhCacheImpl;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.psml.FolderMetaDataImpl;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.om.page.psml.DynamicPageImpl;
import org.apache.jetspeed.om.page.psml.FragmentDefinitionImpl;
import org.apache.jetspeed.om.page.psml.LinkImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.om.page.psml.PageSecurityImpl;
import org.apache.jetspeed.om.page.psml.PageTemplateImpl;
import org.apache.jetspeed.page.document.DocumentHandler;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.document.psml.CastorFileSystemDocumentHandler;
import org.apache.jetspeed.page.document.psml.DocumentHandlerFactoryImpl;
import org.apache.jetspeed.page.document.psml.FileSystemFolderHandler;
import org.apache.jetspeed.page.psml.CastorXmlPageManager;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PermissionFactory;
import org.apache.jetspeed.security.PrincipalsSet;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.SecurityAttributeType;
import org.apache.jetspeed.security.SecurityAttributeTypes;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.impl.TransientJetspeedPrincipal;
import org.apache.jetspeed.security.spi.impl.FolderPermission;
import org.apache.jetspeed.security.spi.impl.FragmentPermission;
import org.apache.jetspeed.security.spi.impl.JetspeedPermissionFactory;
import org.apache.jetspeed.security.spi.impl.PagePermission;

import javax.security.auth.Subject;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * PageManagerTestShared
 * 
 * @author <a href="rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 *          
 */
public interface PageManagerTestShared
{
    public class Shared
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
        static CastorXmlPageManager makeCastorXMLPageManager(String baseDir, String pagesDirName, boolean permissionsEnabled, boolean constraintsEnabled)
            throws Exception
        {
            Map<String,Object> extensionsToXslt = new HashMap<String,Object>();
            extensionsToXslt.put("psml",baseDir+"src/main/resources/stripIds.xslt");
                
            File pagesDirFile = new File(baseDir+"target/testdata/" + pagesDirName);
            
            
            DirectoryXMLTransform dirHelper = new DirectoryXMLTransform(pagesDirFile,extensionsToXslt);
            FileFilter noCVSorSVNorBackups = new FileFilter()
                {
                    public boolean accept( File pathname )
                    {
                        return !pathname.getName().equals("CVS") && !pathname.getName().equals(".svn") && !pathname.getName().endsWith("~");
                    }
                };
            dirHelper.copyFrom(new File(baseDir+"src/test/testdata/" + pagesDirName), noCVSorSVNorBackups);
            
            // copy documents under webapp/pages folder and strip fragment Ids
            File webappDestDirFile = new File(baseDir+"target/testdata/" + pagesDirName+"/webapp-no-ids");
            dirHelper.setBaseDirectory(webappDestDirFile);
            File webappPagesDirFile = new File(baseDir+"src/test/testdata/pages");
            dirHelper.copyFromAndTransform(webappPagesDirFile, noCVSorSVNorBackups);

            // copy documents under webapp/pages folder without transforming them
            webappDestDirFile = new File(baseDir+"target/testdata/" + pagesDirName+"/webapp-ids");
            dirHelper.setBaseDirectory(webappDestDirFile);
            dirHelper.copyFrom(webappPagesDirFile, noCVSorSVNorBackups);

            IdGenerator idGen = new JetspeedIdGenerator(65536,"P-","");
            FileCache cache = new FileCache(new EhCacheImpl( CacheManager.getInstance().getEhcache("pageFileCache")), 10);
            
            DocumentHandler psmlHandler = new CastorFileSystemDocumentHandler(idGen, "/JETSPEED-INF/castor/page-mapping.xml", Page.DOCUMENT_TYPE, PageImpl.class, baseDir + "target/testdata/" + pagesDirName, cache);
            DocumentHandler tpsmlHandler = new CastorFileSystemDocumentHandler(idGen, "/JETSPEED-INF/castor/page-mapping.xml", PageTemplate.DOCUMENT_TYPE, PageTemplateImpl.class, baseDir + "target/testdata/" + pagesDirName, cache);
            DocumentHandler dpsmlHandler = new CastorFileSystemDocumentHandler(idGen, "/JETSPEED-INF/castor/page-mapping.xml", DynamicPage.DOCUMENT_TYPE, DynamicPageImpl.class, baseDir + "target/testdata/" + pagesDirName, cache);
            DocumentHandler fpsmlHandler = new CastorFileSystemDocumentHandler(idGen, "/JETSPEED-INF/castor/page-mapping.xml", FragmentDefinition.DOCUMENT_TYPE, FragmentDefinitionImpl.class, baseDir + "target/testdata/" + pagesDirName, cache);
            DocumentHandler linkHandler = new CastorFileSystemDocumentHandler(idGen, "/JETSPEED-INF/castor/page-mapping.xml", Link.DOCUMENT_TYPE, LinkImpl.class, baseDir + "target/testdata/" + pagesDirName, cache);
            DocumentHandler folderMetaDataHandler = new CastorFileSystemDocumentHandler(idGen, "/JETSPEED-INF/castor/page-mapping.xml", FolderMetaDataImpl.DOCUMENT_TYPE, FolderMetaDataImpl.class, baseDir + "target/testdata/" + pagesDirName, cache);
            DocumentHandler pageSecurityHandler = new CastorFileSystemDocumentHandler(idGen, "/JETSPEED-INF/castor/page-mapping.xml", PageSecurityImpl.DOCUMENT_TYPE, PageSecurity.class, baseDir + "target/testdata/" + pagesDirName, cache);
            
            DocumentHandlerFactory handlerFactory = new DocumentHandlerFactoryImpl();
            handlerFactory.registerDocumentHandler(psmlHandler);
            handlerFactory.registerDocumentHandler(tpsmlHandler);
            handlerFactory.registerDocumentHandler(dpsmlHandler);
            handlerFactory.registerDocumentHandler(fpsmlHandler);
            handlerFactory.registerDocumentHandler(linkHandler);
            handlerFactory.registerDocumentHandler(folderMetaDataHandler);
            handlerFactory.registerDocumentHandler(pageSecurityHandler);
            FolderHandler folderHandler = new FileSystemFolderHandler(idGen, baseDir+"target/testdata/" + pagesDirName, handlerFactory, cache);

            CastorXmlPageManager pageManager = new CastorXmlPageManager(idGen, handlerFactory, folderHandler, cache, permissionsEnabled, constraintsEnabled);
            pageManager.init();
            return pageManager;
        }
        
        /**
         * shutdownCastorXmlPageManager
         * 
         * shutdown page manager and free cache between test invocations
         */
        static void shutdownCastorXMLPageManager(CastorXmlPageManager pageManager)
        {
            // reset to clear cache
            pageManager.reset();
            // shutdown page manager and handlers
            pageManager.shutdown();
            // destroy page manager
            pageManager.destroy();
        }

        /**
         * makeListFromCSV
         *
         * Create List of String values from CSV String for principals/permissions.
         * 
         * @param csv CSV string
         * @return values list
         */
        static List<String> makeListFromCSV(String csv)
        {
            if (csv != null)
            {
                List<String> csvList = new ArrayList<String>();
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
        static String makeCSVFromList(List<String> list)
        {
            if ((list != null) && !list.isEmpty())
            {
                StringBuffer csv = new StringBuffer();
                for (String item : list)
                {
                    if (csv.length() > 0)
                    {
                        csv.append(",");
                    }
                    csv.append(item);
                }
                return csv.toString();
            }
            return null;
        }

        /**
         * testSecurePageManager
         *
         * @param test test case
         * @param pageManager page manager
         */
        static void testSecurePageManager(final TestCase test, final PageManager pageManager) throws Exception
        {
            // tracking
            final String [] somePortletId = new String[1];

            // reset page manager cache
            pageManager.reset();
            
            // setup test subjects
            Set<Principal> principals = new PrincipalsSet();
            principals.add(new TestUser("admin"));
            principals.add(new TestRole("admin"));
            Subject adminSubject = new Subject(true, principals, new HashSet<Principal>(), new HashSet<Principal>());
            
            principals = new PrincipalsSet();
            principals.add(new TestUser("user"));
            Subject userSubject = new Subject(true, principals, new HashSet<Principal>(), new HashSet<Principal>());
            
            principals = new PrincipalsSet();
            principals.add(new TestUser("manager"));
            principals.add(new TestRole("manager"));
            Subject managerSubject = new Subject(true, principals, new HashSet<Principal>(), new HashSet<Principal>());

            principals = new PrincipalsSet();
            principals.add(new TestUser("guest"));
            Subject guestSubject = new Subject(true, principals, new HashSet<Principal>(), new HashSet<Principal>());

            // setup test as admin user
            Exception setup = (Exception)JSSubject.doAsPrivileged(adminSubject, new PrivilegedAction()
                {
                    public Object run()
                    {
                        try
                        {
                            // reset page manager to initial state
                            try
                            {
                                Folder removeRootFolder = pageManager.getFolder("/");
                                pageManager.removeFolder(removeRootFolder);
                                pageManager.reset();
                            }
                            catch (FolderNotFoundException e)
                            {
                            }

                            // create test documents and folders
                            Folder folder = pageManager.newFolder("/");
                            SecurityConstraints constraints = pageManager.newSecurityConstraints();
                            constraints.setOwner("admin");
                            List<String> constraintsRefs = new ArrayList<String>(1);
                            constraintsRefs.add("public-view");
                            constraints.setSecurityConstraintsRefs(constraintsRefs);
                            folder.setSecurityConstraints(constraints);
                            pageManager.updateFolder(folder);
                        
                            PageSecurity pageSecurity = pageManager.newPageSecurity();
                            List<SecurityConstraintsDef> constraintsDefs = new ArrayList<SecurityConstraintsDef>(2);
                            SecurityConstraintsDef constraintsDef = pageManager.newSecurityConstraintsDef();
                            constraintsDef.setName("public-view");
                            List<SecurityConstraint> defConstraints = new ArrayList<SecurityConstraint>(1);
                            SecurityConstraint defConstraint = pageManager.newPageSecuritySecurityConstraint();
                            defConstraint.setUsers(Shared.makeListFromCSV("*"));
                            defConstraint.setPermissions(Shared.makeListFromCSV("view"));
                            defConstraints.add(defConstraint);
                            constraintsDef.setSecurityConstraints(defConstraints);
                            constraintsDefs.add(constraintsDef);
                            constraintsDef = pageManager.newSecurityConstraintsDef();
                            constraintsDef.setName("admin-all");
                            defConstraints = new ArrayList<SecurityConstraint>(1);
                            defConstraint = pageManager.newPageSecuritySecurityConstraint();
                            defConstraint.setRoles(Shared.makeListFromCSV("admin"));
                            defConstraint.setPermissions(Shared.makeListFromCSV("view,edit"));
                            defConstraints.add(defConstraint);
                            constraintsDef.setSecurityConstraints(defConstraints);
                            constraintsDefs.add(constraintsDef);
                            pageSecurity.setSecurityConstraintsDefs(constraintsDefs);
                            List<String> globalConstraintsRefs = new ArrayList<String>(1);
                            globalConstraintsRefs.add("admin-all");
                            pageSecurity.setGlobalSecurityConstraintsRefs(globalConstraintsRefs);
                            pageManager.updatePageSecurity(pageSecurity);
                        
                            Page page = pageManager.newPage("/default-page.psml");
                            constraints = pageManager.newSecurityConstraints();
                            constraints.setOwner("admin");
                            List<SecurityConstraint> inlineConstraints = new ArrayList<SecurityConstraint>(1);
                            SecurityConstraint constraint = pageManager.newPageSecurityConstraint();
                            constraint.setRoles(Shared.makeListFromCSV("manager"));
                            constraint.setPermissions(Shared.makeListFromCSV("edit"));
                            inlineConstraints.add(constraint);
                            constraints.setSecurityConstraints(inlineConstraints);
                            constraintsRefs = new ArrayList<String>(1);
                            constraintsRefs.add("public-view");
                            constraints.setSecurityConstraintsRefs(constraintsRefs);
                            page.setSecurityConstraints(constraints);
                            BaseFragmentElement rootFragmentElement = page.getRootFragment();
                            TestCase.assertTrue(rootFragmentElement instanceof Fragment);
                            Fragment root = (Fragment)rootFragmentElement;
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
                            BaseFragmentElement validateRootFragmentElement = page.getRootFragment();
                            TestCase.assertTrue(validateRootFragmentElement instanceof Fragment);
                            Fragment validateRoot = (Fragment)validateRootFragmentElement;                            
                            TestCase.assertNotNull(validateRoot.getFragments());
                            TestCase.assertEquals(2, validateRoot.getFragments().size());
                            BaseFragmentElement validateFragmentElement = (BaseFragmentElement)validateRoot.getFragments().get(1);
                            TestCase.assertTrue(validateFragmentElement instanceof Fragment);
                            Fragment validateFragment = (Fragment)validateFragmentElement;
                            TestCase.assertEquals("some-app::SomePortlet", validateFragment.getName());
                            TestCase.assertFalse("0".equals(validateFragment.getId()));
                            somePortletId[0] = validateFragment.getId();
                        
                            page = pageManager.newPage("/user-page.psml");
                            constraints = pageManager.newSecurityConstraints();
                            inlineConstraints = new ArrayList<SecurityConstraint>(1);
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
                            inlineConstraints = new ArrayList<SecurityConstraint>(1);
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
                        finally
                        {
                            JSSubject.clearSubject();
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
            Exception adminAccess = (Exception)JSSubject.doAsPrivileged(adminSubject, new PrivilegedAction()
                {
                    public Object run()
                    {
                        try
                        {
                            // test view access
                            Folder folder = pageManager.getFolder("/");
                            TestCase.assertNotNull(folder.getPageSecurity());
                            TestCase.assertNotNull(folder.getPages());
                            TestCase.assertEquals(2, folder.getPages().size());
                            TestCase.assertNotNull(pageManager.getPages(folder));
                            TestCase.assertEquals(2, pageManager.getPages(folder).size());
                            PageSecurity pageSecurity = pageManager.getPageSecurity();
                            Page page0 = pageManager.getPage("/default-page.psml");
                            BaseFragmentElement validateRootFragmentElement = page0.getRootFragment();
                            TestCase.assertTrue(validateRootFragmentElement instanceof Fragment);
                            Fragment validateRoot = (Fragment)validateRootFragmentElement;                            
                            TestCase.assertNotNull(validateRoot.getFragments());
                            TestCase.assertEquals(2, validateRoot.getFragments().size());
                            TestCase.assertNotNull(page0.getFragmentById(somePortletId[0]));
                            TestCase.assertNotNull(page0.getFragmentsByName("some-app::SomePortlet"));
                            TestCase.assertEquals(1, page0.getFragmentsByName("some-app::SomePortlet").size());
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
                        finally
                        {
                            JSSubject.clearSubject();
                        }
                    }
                }, null);
            if (adminAccess != null)
            {
                throw adminAccess;
            }

            // access test as user user
            Exception userAccess = (Exception)JSSubject.doAsPrivileged(userSubject, new PrivilegedAction()
                {
                    public Object run()
                    {
                        try
                        {
                            // test view access
                            Folder folder = pageManager.getFolder("/");
                            TestCase.assertNotNull(folder.getPageSecurity());
                            TestCase.assertNotNull(folder.getPages());
                            TestCase.assertEquals(2, folder.getPages().size());
                            PageSecurity pageSecurity = pageManager.getPageSecurity();
                            Page page0 = pageManager.getPage("/default-page.psml");                            
                            BaseFragmentElement validateRootFragmentElement = page0.getRootFragment();
                            TestCase.assertTrue(validateRootFragmentElement instanceof Fragment);
                            Fragment validateRoot = (Fragment)validateRootFragmentElement;                            
                            TestCase.assertNotNull(validateRoot.getFragments());
                            TestCase.assertEquals(2, validateRoot.getFragments().size());
                            TestCase.assertNotNull(page0.getFragmentById(somePortletId[0]));
                            TestCase.assertNotNull(page0.getFragmentsByName("some-app::SomePortlet"));
                            TestCase.assertEquals(1, page0.getFragmentsByName("some-app::SomePortlet").size());
                            Page page1 = pageManager.getPage("/user-page.psml");
                            Link link = pageManager.getLink("/default.link");
                            // test edit access
                            try
                            {
                                pageManager.updateFolder(folder);
                                TestCase.assertTrue("Folder / not editable for user", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            try
                            {
                                pageManager.updatePageSecurity(pageSecurity);
                                TestCase.assertTrue("PageSecurity not editable for user", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            try
                            {
                                pageManager.updatePage(page0);
                                TestCase.assertTrue("Page /default-page.psml not editable for user", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            pageManager.updatePage(page1);
                            try
                            {
                                pageManager.updateLink(link);
                                TestCase.assertTrue("Page /default.link not editable for user", false);
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
                        finally
                        {
                            JSSubject.clearSubject();
                        }
                    }
                }, null);
            if (userAccess != null)
            {
                throw userAccess;
            }

            // access test as manager user
            Exception managerAccess = (Exception)JSSubject.doAsPrivileged(managerSubject, new PrivilegedAction()
                {
                    public Object run()
                    {
                        try
                        {
                            // test view access
                            Folder folder = pageManager.getFolder("/");
                            TestCase.assertNotNull(folder.getPageSecurity());
                            TestCase.assertNotNull(folder.getPages());
                            TestCase.assertEquals(1, folder.getPages().size());
                            PageSecurity pageSecurity = pageManager.getPageSecurity();
                            Page page0 = pageManager.getPage("/default-page.psml");
                            BaseFragmentElement validateRootFragmentElement = page0.getRootFragment();
                            TestCase.assertTrue(validateRootFragmentElement instanceof Fragment);
                            Fragment validateRoot = (Fragment)validateRootFragmentElement;
                            TestCase.assertNotNull(validateRoot.getFragments());
                            TestCase.assertEquals(1, validateRoot.getFragments().size());
                            TestCase.assertNull(page0.getFragmentById(somePortletId[0]));
                            TestCase.assertTrue(page0.getFragmentsByName("some-app::SomePortlet").isEmpty());
                            Link link = pageManager.getLink("/default.link");
                            try
                            {
                                pageManager.getPage("/user-page.psml");
                                TestCase.assertTrue("Page /user-page.psml not viewable for manager", false);
                            }
                            catch (SecurityException se)
                            {
                            }                        
                            // test edit access
                            try
                            {
                                pageManager.updateFolder(folder);
                                TestCase.assertTrue("Folder / not editable for manager", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            try
                            {
                                pageManager.updatePageSecurity(pageSecurity);
                                TestCase.assertTrue("PageSecurity not editable for manager", false);
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
                        finally
                        {
                            JSSubject.clearSubject();
                        }
                    }
                }, null);
            if (managerAccess != null)
            {
                throw managerAccess;
            }

            // access test as guest user
            Exception guestAccess = (Exception)JSSubject.doAsPrivileged(guestSubject, new PrivilegedAction()
                {
                    public Object run()
                    {
                        try
                        {
                            // test view access
                            Folder folder = pageManager.getFolder("/");
                            TestCase.assertNotNull(folder.getPageSecurity());
                            TestCase.assertNotNull(folder.getPages());
                            TestCase.assertEquals(1, folder.getPages().size());
                            PageSecurity pageSecurity = pageManager.getPageSecurity();
                            Page page0 = pageManager.getPage("/default-page.psml");
                            BaseFragmentElement validateRootFragmentElement = page0.getRootFragment();
                            TestCase.assertTrue(validateRootFragmentElement instanceof Fragment);
                            Fragment validateRoot = (Fragment)validateRootFragmentElement;                            
                            TestCase.assertNotNull(validateRoot.getFragments());
                            TestCase.assertEquals(1, validateRoot.getFragments().size());
                            TestCase.assertNull(page0.getFragmentById(somePortletId[0]));
                            TestCase.assertTrue(page0.getFragmentsByName("some-app::SomePortlet").isEmpty());
                            Link link = pageManager.getLink("/default.link");
                            try
                            {
                                pageManager.getPage("/user-page.psml");
                                TestCase.assertTrue("Page /user-page.psml not viewable for guest", false);
                            }
                            catch (SecurityException se)
                            {
                            }                        
                            // test edit access
                            try
                            {
                                pageManager.updateFolder(folder);
                                TestCase.assertTrue("Folder / not editable for guest", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            try
                            {
                                pageManager.updatePageSecurity(pageSecurity);
                                TestCase.assertTrue("PageSecurity not editable for guest", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            try
                            {
                                pageManager.updatePage(page0);
                                TestCase.assertTrue("Page /default-page.psml not editable for guest", false);
                            }
                            catch (SecurityException se)
                            {
                            }
                            try
                            {
                                pageManager.updateLink(link);
                                TestCase.assertTrue("Page /default.link not editable for guest", false);
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
                        finally
                        {
                            JSSubject.clearSubject();
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
            Exception cleanup = (Exception)JSSubject.doAsPrivileged(adminSubject, new PrivilegedAction()
                {
                    public Object run()
                    {
                        try
                        {
                            // cleanup by removing root folder
                            try
                            {
                                Folder remove = pageManager.getFolder("/");
                                TestCase.assertEquals("/", remove.getPath());
                                pageManager.removeFolder(remove);
                            }
                            catch (FolderNotFoundException e)
                            {
                                TestCase.assertTrue("Folder / NOT FOUND", false);
                            }

                            return null;
                        }
                        catch (Exception e)
                        {
                            return e;
                        }
                        finally
                        {
                            JSSubject.clearSubject();
                        }
                    }
                }, null);
            if (cleanup != null)
            {
                throw cleanup;
            }
        }

        /**
         * testSecurityConstraintsRefExpressions
         *
         * @param test test case
         * @param pageManager page manager
         */
        static void testSecurityConstraintsRefExpressions(final TestCase test, final PageManager pageManager) throws Exception
        {
            // reset page manager cache
            pageManager.reset();

            // setup test subjects
            Set<Principal> principals = new PrincipalsSet();
            principals.add(new TestUser("admin"));
            Subject adminSubject = new Subject(true, principals, new HashSet<Principal>(), new HashSet<Principal>());

            principals = new PrincipalsSet();
            principals.add(new TestUser("user-with-admin"));
            principals.add(new TestRole("admin"));
            Subject userWithAdminSubject = new Subject(true, principals, new HashSet<Principal>(), new HashSet<Principal>());

            principals = new PrincipalsSet();
            principals.add(new TestUser("user"));
            Subject userSubject = new Subject(true, principals, new HashSet<Principal>(), new HashSet<Principal>());

            principals = new PrincipalsSet();
            principals.add(new TestUser("test-group-user"));
            principals.add(new TestGroup("test-group"));
            Subject testGroupUserSubject = new Subject(true, principals, new HashSet<Principal>(), new HashSet<Principal>());

            principals = new PrincipalsSet();
            principals.add(new TestUser("test-role-user"));
            principals.add(new TestRole("test-role"));
            Subject testRoleUserSubject = new Subject(true, principals, new HashSet<Principal>(), new HashSet<Principal>());

            principals = new PrincipalsSet();
            principals.add(new TestUser("test-group-role-user"));
            principals.add(new TestGroup("test-group"));
            principals.add(new TestRole("test-role"));
            Subject testGroupRoleUserSubject = new Subject(true, principals, new HashSet<Principal>(), new HashSet<Principal>());

            // setup test as admin
            Exception setup = (Exception) JSSubject.doAsPrivileged(adminSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        // reset page manager to initial state
                        try
                        {
                            Folder removeRootFolder = pageManager.getFolder("/");
                            pageManager.removeFolder(removeRootFolder);
                            pageManager.reset();
                        }
                        catch (FolderNotFoundException e)
                        {
                        }

                        // create test documents and folders
                        Folder folder = pageManager.newFolder("/");
                        SecurityConstraints constraints = pageManager.newSecurityConstraints();
                        constraints.setOwner("admin");
                        List<String> constraintsRefs = new ArrayList<String>(1);
                        constraintsRefs.add("public-view");
                        constraints.setSecurityConstraintsRefs(constraintsRefs);
                        folder.setSecurityConstraints(constraints);
                        pageManager.updateFolder(folder);

                        PageSecurity pageSecurity = pageManager.newPageSecurity();
                        List<SecurityConstraintsDef> constraintsDefs = new ArrayList<SecurityConstraintsDef>(5);
                        SecurityConstraintsDef constraintsDef = pageManager.newSecurityConstraintsDef();
                        constraintsDef.setName("public-view");
                        List<SecurityConstraint> defConstraints = new ArrayList<SecurityConstraint>(1);
                        SecurityConstraint defConstraint = pageManager.newPageSecuritySecurityConstraint();
                        defConstraint.setUsers(Shared.makeListFromCSV("*"));
                        defConstraint.setPermissions(Shared.makeListFromCSV("view"));
                        defConstraints.add(defConstraint);
                        constraintsDef.setSecurityConstraints(defConstraints);
                        constraintsDefs.add(constraintsDef);
                        constraintsDef = pageManager.newSecurityConstraintsDef();
                        constraintsDef.setName("test-group");
                        defConstraints = new ArrayList<SecurityConstraint>(1);
                        defConstraint = pageManager.newPageSecuritySecurityConstraint();
                        defConstraint.setGroups(Shared.makeListFromCSV("test-group"));
                        defConstraint.setPermissions(Shared.makeListFromCSV("view"));
                        defConstraints.add(defConstraint);
                        constraintsDef.setSecurityConstraints(defConstraints);
                        constraintsDefs.add(constraintsDef);
                        constraintsDef = pageManager.newSecurityConstraintsDef();
                        constraintsDef.setName("test-role");
                        defConstraints = new ArrayList<SecurityConstraint>(1);
                        defConstraint = pageManager.newPageSecuritySecurityConstraint();
                        defConstraint.setRoles(Shared.makeListFromCSV("test-role"));
                        defConstraint.setPermissions(Shared.makeListFromCSV("view"));
                        defConstraints.add(defConstraint);
                        constraintsDef.setSecurityConstraints(defConstraints);
                        constraintsDefs.add(constraintsDef);
                        constraintsDef = pageManager.newSecurityConstraintsDef();
                        constraintsDef.setName("admin-role");
                        defConstraints = new ArrayList<SecurityConstraint>(1);
                        defConstraint = pageManager.newPageSecuritySecurityConstraint();
                        defConstraint.setRoles(Shared.makeListFromCSV("admin"));
                        defConstraint.setPermissions(Shared.makeListFromCSV("view,edit"));
                        defConstraints.add(defConstraint);
                        constraintsDef.setSecurityConstraints(defConstraints);
                        constraintsDefs.add(constraintsDef);
                        constraintsDef = pageManager.newSecurityConstraintsDef();
                        constraintsDef.setName("admin-user");
                        defConstraints = new ArrayList<SecurityConstraint>(1);
                        defConstraint = pageManager.newPageSecuritySecurityConstraint();
                        defConstraint.setUsers(Shared.makeListFromCSV("admin"));
                        defConstraint.setPermissions(Shared.makeListFromCSV("view,edit"));
                        defConstraints.add(defConstraint);
                        constraintsDef.setSecurityConstraints(defConstraints);
                        constraintsDefs.add(constraintsDef);
                        pageSecurity.setSecurityConstraintsDefs(constraintsDefs);
                        List<String> globalConstraintsRefs = new ArrayList<String>(1);
                        globalConstraintsRefs.add("admin-role or admin-user");
                        pageSecurity.setGlobalSecurityConstraintsRefs(globalConstraintsRefs);
                        pageManager.updatePageSecurity(pageSecurity);

                        Page page = pageManager.newPage("/default-page.psml");
                        constraints = pageManager.newSecurityConstraints();
                        constraints.setOwner("admin");
                        constraintsRefs = new ArrayList<String>(1);
                        constraintsRefs.add("public-view");
                        constraints.setSecurityConstraintsRefs(constraintsRefs);
                        page.setSecurityConstraints(constraints);
                        pageManager.updatePage(page);

                        page = pageManager.newPage("/or-page.psml");
                        constraints = pageManager.newSecurityConstraints();
                        constraints.setOwner("admin");
                        List<SecurityConstraint> inlineConstraints = new ArrayList<SecurityConstraint>(1);
                        SecurityConstraint constraint = pageManager.newPageSecurityConstraint();
                        constraint.setUsers(Shared.makeListFromCSV("user"));
                        constraint.setPermissions(Shared.makeListFromCSV("view"));
                        inlineConstraints.add(constraint);
                        constraints.setSecurityConstraints(inlineConstraints);
                        constraintsRefs = new ArrayList<String>(1);
                        constraintsRefs.add("test-group || test-role");
                        constraints.setSecurityConstraintsRefs(constraintsRefs);
                        page.setSecurityConstraints(constraints);
                        pageManager.updatePage(page);

                        page = pageManager.newPage("/and-page.psml");
                        constraints = pageManager.newSecurityConstraints();
                        constraints.setOwner("admin");
                        constraintsRefs = new ArrayList<String>(1);
                        constraintsRefs.add("test-group and test-role");
                        constraints.setSecurityConstraintsRefs(constraintsRefs);
                        page.setSecurityConstraints(constraints);
                        pageManager.updatePage(page);

                        page = pageManager.newPage("/not-page.psml");
                        constraints = pageManager.newSecurityConstraints();
                        constraints.setOwner("admin");
                        constraintsRefs = new ArrayList<String>(1);
                        constraintsRefs.add("not test-role");
                        constraints.setSecurityConstraintsRefs(constraintsRefs);
                        page.setSecurityConstraints(constraints);
                        pageManager.updatePage(page);

                        page = pageManager.newPage("/and-not-page.psml");
                        constraints = pageManager.newSecurityConstraints();
                        constraints.setOwner("admin");
                        constraintsRefs = new ArrayList<String>(1);
                        constraintsRefs.add("public-view and not test-group");
                        constraints.setSecurityConstraintsRefs(constraintsRefs);
                        page.setSecurityConstraints(constraints);
                        pageManager.updatePage(page);

                        page = pageManager.newPage("/paren-page.psml");
                        constraints = pageManager.newSecurityConstraints();
                        constraints.setOwner("admin");
                        constraintsRefs = new ArrayList<String>(1);
                        constraintsRefs.add("((test-group||test-role)&&!admin-role)");
                        constraints.setSecurityConstraintsRefs(constraintsRefs);
                        page.setSecurityConstraints(constraints);
                        pageManager.updatePage(page);

                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                    finally
                    {
                        JSSubject.clearSubject();
                    }
                }
            }, null);
            if (setup != null)
            {
                throw setup;
            }

            // reset page manager
            pageManager.reset();

            // test as admin
            Exception adminAccess = (Exception) JSSubject.doAsPrivileged(adminSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        assertPageAccessGranted(pageManager, "/default-page.psml");
                        assertPageAccessGranted(pageManager, "/or-page.psml");
                        assertPageAccessGranted(pageManager, "/and-page.psml");
                        assertPageAccessGranted(pageManager, "/not-page.psml");
                        assertPageAccessGranted(pageManager, "/and-not-page.psml");
                        assertPageAccessGranted(pageManager, "/paren-page.psml");
                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                    finally
                    {
                        JSSubject.clearSubject();
                    }
                }
            }, null);
            if (adminAccess != null)
            {
                throw adminAccess;
            }

            // test as user with admin
            Exception userWithAdminAccess = (Exception) JSSubject.doAsPrivileged(userWithAdminSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        assertPageAccessGranted(pageManager, "/default-page.psml");
                        assertPageAccessGranted(pageManager, "/or-page.psml");
                        assertPageAccessGranted(pageManager, "/and-page.psml");
                        assertPageAccessGranted(pageManager, "/not-page.psml");
                        assertPageAccessGranted(pageManager, "/and-not-page.psml");
                        assertPageAccessGranted(pageManager, "/paren-page.psml");
                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                    finally
                    {
                        JSSubject.clearSubject();
                    }
                }
            }, null);
            if (userWithAdminAccess != null)
            {
                throw userWithAdminAccess;
            }

            // test as user
            Exception userAccess = (Exception) JSSubject.doAsPrivileged(userSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        assertPageAccessGranted(pageManager, "/default-page.psml");
                        assertPageAccessGranted(pageManager, "/or-page.psml");
                        assertPageAccessDenied(pageManager, "/and-page.psml");
                        assertPageAccessGranted(pageManager, "/not-page.psml");
                        assertPageAccessGranted(pageManager, "/and-not-page.psml");
                        assertPageAccessDenied(pageManager, "/paren-page.psml");
                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                    finally
                    {
                        JSSubject.clearSubject();
                    }
                }
            }, null);
            if (userAccess != null)
            {
                throw userAccess;
            }

            // test as test group user
            Exception testGroupUserAccess = (Exception) JSSubject.doAsPrivileged(testGroupUserSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        assertPageAccessGranted(pageManager, "/default-page.psml");
                        assertPageAccessGranted(pageManager, "/or-page.psml");
                        assertPageAccessDenied(pageManager, "/and-page.psml");
                        assertPageAccessGranted(pageManager, "/not-page.psml");
                        assertPageAccessDenied(pageManager, "/and-not-page.psml");
                        assertPageAccessGranted(pageManager, "/paren-page.psml");
                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                    finally
                    {
                        JSSubject.clearSubject();
                    }
                }
            }, null);
            if (testGroupUserAccess != null)
            {
                throw testGroupUserAccess;
            }

            // test as test role user
            Exception testRoleUserAccess = (Exception) JSSubject.doAsPrivileged(testRoleUserSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        assertPageAccessGranted(pageManager, "/default-page.psml");
                        assertPageAccessGranted(pageManager, "/or-page.psml");
                        assertPageAccessDenied(pageManager, "/and-page.psml");
                        assertPageAccessDenied(pageManager, "/not-page.psml");
                        assertPageAccessGranted(pageManager, "/and-not-page.psml");
                        assertPageAccessGranted(pageManager, "/paren-page.psml");
                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                    finally
                    {
                        JSSubject.clearSubject();
                    }
                }
            }, null);
            if (testRoleUserAccess != null)
            {
                throw testRoleUserAccess;
            }

            // test as test group role user
            Exception testGroupRoleUserAccess = (Exception) JSSubject.doAsPrivileged(testGroupRoleUserSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        assertPageAccessGranted(pageManager, "/default-page.psml");
                        assertPageAccessGranted(pageManager, "/or-page.psml");
                        assertPageAccessGranted(pageManager, "/and-page.psml");
                        assertPageAccessDenied(pageManager, "/not-page.psml");
                        assertPageAccessDenied(pageManager, "/and-not-page.psml");
                        assertPageAccessGranted(pageManager, "/paren-page.psml");
                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                    finally
                    {
                        JSSubject.clearSubject();
                    }
                }
            }, null);
            if (testGroupRoleUserAccess != null)
            {
                throw testGroupRoleUserAccess;
            }

            // cleanup test as admin user
            Exception cleanup = (Exception)JSSubject.doAsPrivileged(adminSubject, new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        // cleanup by removing root folder
                        try
                        {
                            Folder remove = pageManager.getFolder("/");
                            TestCase.assertEquals("/", remove.getPath());
                            pageManager.removeFolder(remove);
                        }
                        catch (FolderNotFoundException e)
                        {
                            TestCase.assertTrue("Folder / NOT FOUND", false);
                        }

                        return null;
                    }
                    catch (Exception e)
                    {
                        return e;
                    }
                    finally
                    {
                        JSSubject.clearSubject();
                    }
                }
            }, null);
            if (cleanup != null)
            {
                throw cleanup;
            }
        }

        static void assertPageAccessGranted(PageManager pageManager, String path) throws Exception
        {
            try
            {
                pageManager.getPage(path);
            }
            catch (SecurityException se)
            {
                TestCase.fail("Page "+path+" access denied");
            }
        }

        static void assertPageAccessDenied(PageManager pageManager, String path) throws Exception
        {
            try
            {
                pageManager.getPage(path);
                TestCase.fail("Page "+path+" access granted");
            }
            catch (SecurityException se)
            {
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
        private static PermissionFactory pf = new TestPermissionFactory();
        static 
        {
            org.apache.jetspeed.om.page.psml.AbstractBaseElement.setPermissionsFactory(pf);
            org.apache.jetspeed.om.page.impl.BaseElementImpl.setPermissionsFactory(pf);
            org.apache.jetspeed.om.folder.impl.FolderImpl.setPermissionsFactory(pf);
            org.apache.jetspeed.om.folder.psml.FolderImpl.setPermissionsFactory(pf);
        }

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
            JetspeedPermission j2p = permission instanceof JetspeedPermission ? (JetspeedPermission)permission : null;
            boolean testPermission = j2p != null && (j2p.getType().equals("folder")||j2p.getType().equals("page")||j2p.getType().equals("fragment"));
            Principal[] principals = domain.getPrincipals();
            if ((principals != null) && (principals.length > 0) && (testPermission))
            {
                // check permission using principals if available
                Permissions permissions = new Permissions();
                for (int i = 0; (i < principals.length); i++)
                {
                    if (principals[i] instanceof User)
                    {
                        // get permissions for users
                        String user = principals[i].getName();
                        if (user.equals("admin"))
                        {
                            // owner permissions                            
                            permissions.add((Permission)pf.newPermission("folder", "/", "view, edit"));
                            permissions.add((Permission)pf.newPermission("page", "/default-page.psml", "view, edit"));
                        }
                        else if (user.equals("user"))
                        {
                            // owner permissions
                            permissions.add((Permission)pf.newPermission("fragment", "/default-page.psml/some-app::SomePortlet", "view, edit"));
                            
                            // granted permissions
                            permissions.add((Permission)pf.newPermission("page", "/user-page.psml", "view, edit"));
                            permissions.add((Permission)pf.newPermission("fragment", "/user-page.psml/*", "view"));
                        }
                        
                        // public view permissions
                        permissions.add((Permission)pf.newPermission("folder", "/", "view"));
                        permissions.add((Permission)pf.newPermission("page", "/default-page.psml", "view"));
                        permissions.add((Permission)pf.newPermission("page", "/page.security", "view"));
                        permissions.add((Permission)pf.newPermission("fragment", "security::*", "view"));
                    }
                    else if (principals[i] instanceof Role)
                    {
                        // get permissions for roles
                        String role = principals[i].getName();
                        if (role.equals("admin"))
                        {
                            // global permissions
                            permissions.add((Permission)pf.newPermission("folder", "<<ALL FILES>>", "view, edit"));
                            permissions.add((Permission)pf.newPermission("fragment", "<<ALL FRAGMENTS>>", "view, edit"));
                        }
                        else if (role.equals("manager"))
                        {
                            // granted permissions
                            permissions.add((Permission)pf.newPermission("page", "/default-page.psml", "edit"));
                            permissions.add((Permission)pf.newPermission("page", "/default.link", "edit"));
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
    
    public static abstract class AbstractTestPrincipal extends TransientJetspeedPrincipal
    {
        private static final SecurityAttributeTypes attributeTypes = new SecurityAttributeTypes()
        {

            public Map<String, SecurityAttributeType> getAttributeTypeMap()
            {
                return Collections.emptyMap();
            }

            public Map<String, SecurityAttributeType> getAttributeTypeMap(String category)
            {
                return Collections.emptyMap();
            }

            public boolean isExtendable()
            {
                return false;
            }

            public boolean isReadOnly()
            {
                return true;
            }
        };
        
        private JetspeedPrincipalType type;
        
        private static final long serialVersionUID = 1L;
        

        public AbstractTestPrincipal(final String type, String name)
        {
            super(type, name);
            this.type = new JetspeedPrincipalType()
            {               
                public SecurityAttributeTypes getAttributeTypes()
                {
                    return attributeTypes;
                }

                public String getClassName()
                {
                    return null;
                }

                public String getName()
                {
                    return type;
                }

                public Class<JetspeedPrincipal> getPrincipalClass()
                {
                    return null;
                }
            };
        }

        public synchronized JetspeedPrincipalType getType()
        {
            return type;
        }
    }
    
    public static class TestUser extends AbstractTestPrincipal implements User
    {
        private static final long serialVersionUID = 1L;

        public TestUser(String name)
        {
            super(JetspeedPrincipalType.USER, name);
        }
    }

    public static class TestGroup extends AbstractTestPrincipal implements Group
    {
        private static final long serialVersionUID = 1L;

        public TestGroup(String name)
        {
            super(JetspeedPrincipalType.GROUP, name);
        }
    }
    
    public static class TestRole extends AbstractTestPrincipal implements Role
    {
        private static final long serialVersionUID = 1L;

        public TestRole(String name)
        {
            super(JetspeedPrincipalType.ROLE, name);
        }
    }
    
    public static class TestPermissionFactory implements PermissionFactory
    {
        private static Map<String, JetspeedPermissionFactory> factories = new HashMap<String, JetspeedPermissionFactory>();
        static
        {
            factories.put("folder", new FolderPermission.Factory());
            factories.put("page", new PagePermission.Factory());
            factories.put("fragment", new FragmentPermission.Factory());
        }

        public JetspeedPermission newPermission(String type, String name, String actions)
        {
            return factories.get(type).newPermission(name, actions);
        }

        public JetspeedPermission newPermission(String type, String name, int mask)
        {
            return factories.get(type).newPermission(name, mask);
        }

        public int parseActions(String actions)
        {
            return JetspeedActions.getContainerActionsMask(actions);
        }
    }
}
