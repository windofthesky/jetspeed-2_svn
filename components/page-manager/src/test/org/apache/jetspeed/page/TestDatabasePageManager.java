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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.components.test.AbstractSpringTestCase;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToUpdateDocumentException;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestPageXmlPersistence
 * 
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 *          
 */
public class TestDatabasePageManager extends AbstractSpringTestCase
{
    private String deepFolderPath = "/__subsite-rootx/_user/userx/_role/rolex/_group/groupx/_mediatype/xhtml/_language/en/_country/us/_custom/customx";
    private String deepPagePath = deepFolderPath + "/default-page.psml";

    private PageManager pageManager;
    
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestDatabasePageManager.class.getName() });
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();        
        pageManager = (PageManager)ctx.getBean("pageManager");
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestDatabasePageManager.class);
    }
    
    protected String[] getConfigurations()
    {
        return new String[]
        { "database-page-manager.xml", "transaction.xml" };
    }

    protected String[] getBootConfigurations()
    {
        return new String[]
        { "test-repository-datasource-spring.xml" };
    }

    public void testCreates() throws Exception
    {
        // reset page manager cache
        pageManager.reset();

        // test document and folder creation
        Folder folder = pageManager.newFolder("/");
        folder.setTitle("Root Folder");
        folder.setDefaultPage("default-page.psml");
        folder.setShortTitle("Root");
        GenericMetadata metadata = folder.getMetadata();
        metadata.addField(Locale.FRENCH, "title", "[fr] Root Folder");
        SecurityConstraints folderConstraints = pageManager.newSecurityConstraints();
        folderConstraints.setOwner("admin");
        List inlineFolderConstraints = new ArrayList(2);
        SecurityConstraint folderConstraint = pageManager.newSecurityConstraint();
        folderConstraint.setUsers("user,admin");
        folderConstraint.setRoles("manager");
        folderConstraint.setGroups("*");
        folderConstraint.setPermissions("view,edit");
        inlineFolderConstraints.add(folderConstraint);
        folderConstraint = pageManager.newSecurityConstraint();
        folderConstraint.setPermissions("edit");
        inlineFolderConstraints.add(folderConstraint);
        folderConstraints.setSecurityConstraints(inlineFolderConstraints);
        List folderConstraintsRefs = new ArrayList(2);
        folderConstraintsRefs.add("public-view");
        folderConstraintsRefs.add("public-edit");
        folderConstraints.setSecurityConstraintsRefs(folderConstraintsRefs);
        folder.setSecurityConstraints(folderConstraints);
        pageManager.updateFolder(folder);
        
        assertNull(folder.getParent());

        Page page = pageManager.newPage("/default-page.psml");
        page.setTitle("Default Page");
        page.setDefaultDecorator("tigris", Fragment.LAYOUT);
        page.setDefaultDecorator("blue-gradient", Fragment.PORTLET);
        page.setDefaultSkin("skin-1");
        page.setShortTitle("Default");
        metadata = page.getMetadata();
        metadata.addField(Locale.FRENCH, "title", "[fr] Default Page");
        metadata.addField(Locale.JAPANESE, "title", "[ja] Default Page");
        SecurityConstraints pageConstraints = pageManager.newSecurityConstraints();
        pageConstraints.setOwner("user");
        List inlinePageConstraints = new ArrayList(1);
        SecurityConstraint pageConstraint = pageManager.newSecurityConstraint();
        pageConstraint.setUsers("jetspeed");
        pageConstraint.setPermissions("edit");
        inlinePageConstraints.add(pageConstraint);
        pageConstraints.setSecurityConstraints(inlinePageConstraints);
        List pageConstraintsRefs = new ArrayList(1);
        pageConstraintsRefs.add("manager-edit");
        pageConstraints.setSecurityConstraintsRefs(pageConstraintsRefs);
        page.setSecurityConstraints(pageConstraints);

        Fragment root = page.getRootFragment();
        root.setDecorator("blue-gradient");
        root.setName("jetspeed-layouts::VelocityTwoColumns");
        root.setShortTitle("Root");
        root.setTitle("Root Fragment");
        root.setState("Normal");
        root.setLayoutSizes("50%,50%");
        root.getProperties().put("custom-prop1", "custom-prop-value1");
        root.getProperties().put("custom-prop2", "custom-prop-value2");
        
        Fragment portlet = pageManager.newPortletFragment();
        portlet.setName("security::LoginPortlet");
        portlet.setShortTitle("Portlet");
        portlet.setTitle("Portlet Fragment");
        portlet.setState("Normal");
        portlet.setLayoutRow(88);
        portlet.setLayoutColumn(99);
        root.getFragments().add(portlet);

        pageManager.updatePage(page);

        assertNotNull(page.getParent());
        assertEquals(page.getParent().getId(), folder.getId());

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
        defConstraints = new ArrayList(2);
        defConstraint = pageManager.newSecurityConstraint();
        defConstraint.setRoles("admin");
        defConstraint.setPermissions("view,edit");
        defConstraints.add(defConstraint);
        defConstraint = pageManager.newSecurityConstraint();
        defConstraint.setRoles("nobody");
        defConstraints.add(defConstraint);
        constraintsDef.setSecurityConstraints(defConstraints);
        constraintsDefs.add(constraintsDef);
        pageSecurity.setSecurityConstraintsDefs(constraintsDefs);
        List globalConstraintsRefs = new ArrayList(2);
        globalConstraintsRefs.add("admin-all");
        globalConstraintsRefs.add("public-view");
        pageSecurity.setGlobalSecurityConstraintsRefs(globalConstraintsRefs);

        pageManager.updatePageSecurity(pageSecurity);

        assertNotNull(pageSecurity.getParent());
        assertEquals(pageSecurity.getParent().getId(), folder.getId());

        // test duplicate creates
        try
        {
            Folder dupFolder = pageManager.newFolder("/");
            pageManager.updateFolder(dupFolder);
            assertTrue("Duplicate Folder / CREATED", false);
        }
        catch (FolderNotUpdatedException e)
        {
        }
        try
        {
            Page dupPage = pageManager.newPage("/default-page.psml");
            pageManager.updatePage(dupPage);
            assertTrue("Duplicate Page / CREATED", false);
        }
        catch (PageNotUpdatedException e)
        {
        }
        try
        {
            PageSecurity dupPageSecurity = pageManager.newPageSecurity();
            pageManager.updatePageSecurity(dupPageSecurity);
            assertTrue("Duplicate PageSecurity / CREATED", false);
        }
        catch (FailedToUpdateDocumentException e)
        {
        }

        // test folder/page creation with attributes on deep path
        int pathIndex = deepFolderPath.indexOf('/', 1);
        while ((pathIndex != -1) && (pathIndex <= deepFolderPath.length()))
        {
            folder = pageManager.newFolder(deepFolderPath.substring(0, pathIndex));
            pageManager.updateFolder(folder);
            assertNotNull(folder.getParent());

            if (pathIndex < deepFolderPath.length())
            {
                pathIndex = deepFolderPath.indexOf('/', pathIndex+1);
                if (pathIndex == -1)
                {
                    pathIndex = deepFolderPath.length();
                }
            }
            else
            {
                pathIndex = -1;
            }
        }
        page = pageManager.newPage(deepPagePath);
        pageManager.updatePage(page);
        assertNotNull(page.getParent());
        assertEquals(page.getParent().getId(), folder.getId());
    }
    
    public void testGets() throws Exception
    {
        // reset page manager cache
        pageManager.reset();
        
        // read documents and folders from persisted store
        try
        {
            PageSecurity check = pageManager.getPageSecurity();
            assertEquals("/page.security", check.getPath());
            assertEquals("page.security", check.getName());
            assertNotNull(check.getSecurityConstraintsDefs());
            assertEquals(2, check.getSecurityConstraintsDefs().size());
            assertEquals("admin-all", ((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(0)).getName());
            assertNotNull(((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(0)).getSecurityConstraints());
            assertEquals(2, ((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(0)).getSecurityConstraints().size());
            assertEquals("view,edit", ((SecurityConstraint)((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(0)).getSecurityConstraints().get(0)).getPermissions());
            assertEquals("public-view", ((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(1)).getName());
            assertNotNull(((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(1)).getSecurityConstraints());
            assertEquals(1, ((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(1)).getSecurityConstraints().size());
            assertEquals("view", ((SecurityConstraint)((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(1)).getSecurityConstraints().get(0)).getPermissions());
            assertNotNull(check.getGlobalSecurityConstraintsRefs());
            assertEquals(2, check.getGlobalSecurityConstraintsRefs().size());
            assertEquals("admin-all", (String)check.getGlobalSecurityConstraintsRefs().get(0));
            assertEquals("public-view", (String)check.getGlobalSecurityConstraintsRefs().get(1));
        }
        catch (DocumentNotFoundException e)
        {
            assertTrue("PageSecurity NOT FOUND", false);
        }
        try
        {
            Page check = pageManager.getPage("/default-page.psml");
            assertEquals("/default-page.psml", check.getPath());
            assertEquals("default-page.psml", check.getName());
            assertEquals("Default Page", check.getTitle());
            assertEquals("tigris", check.getDefaultDecorator(Fragment.LAYOUT));
            assertEquals("blue-gradient", check.getDefaultDecorator(Fragment.PORTLET));
            assertEquals("skin-1", check.getDefaultSkin());
            assertEquals("Default", check.getShortTitle());
            assertNotNull(check.getMetadata());
            assertEquals("[fr] Default Page", check.getTitle(Locale.FRENCH));
            assertEquals("[ja] Default Page", check.getTitle(Locale.JAPANESE));
            assertNotNull(check.getRootFragment());
            assertEquals("blue-gradient", check.getRootFragment().getDecorator());
            assertEquals("jetspeed-layouts::VelocityTwoColumns", check.getRootFragment().getName());
            assertEquals("Root", check.getRootFragment().getShortTitle());
            assertEquals("Root Fragment", check.getRootFragment().getTitle());
            assertEquals("Normal", check.getRootFragment().getState());
            assertEquals("50%,50%", check.getRootFragment().getLayoutSizes());
            assertNotNull(check.getRootFragment().getProperties());
            assertEquals("custom-prop-value1", check.getRootFragment().getProperty("custom-prop1"));
            assertNotNull(check.getRootFragment().getFragments());
            assertEquals(1, check.getRootFragment().getFragments().size());
            assertEquals("security::LoginPortlet", ((Fragment)check.getRootFragment().getFragments().get(0)).getName());
            assertEquals("Portlet", ((Fragment)check.getRootFragment().getFragments().get(0)).getShortTitle());
            assertEquals("Portlet Fragment", ((Fragment)check.getRootFragment().getFragments().get(0)).getTitle());
            assertEquals("Normal", ((Fragment)check.getRootFragment().getFragments().get(0)).getState());
            assertEquals(88, ((Fragment)check.getRootFragment().getFragments().get(0)).getLayoutRow());
            assertEquals(88, ((Fragment)check.getRootFragment().getFragments().get(0)).getIntProperty(Fragment.ROW_PROPERTY_NAME));
            assertEquals(99, ((Fragment)check.getRootFragment().getFragments().get(0)).getLayoutColumn());
            assertNotNull(check.getSecurityConstraints());
            assertNotNull(check.getSecurityConstraints().getSecurityConstraintsRefs());
            assertEquals(1, check.getSecurityConstraints().getSecurityConstraintsRefs().size());
            assertEquals("manager-edit", (String)check.getSecurityConstraints().getSecurityConstraintsRefs().get(0));
            assertNotNull(check.getSecurityConstraints().getSecurityConstraints());
            assertEquals(1, check.getSecurityConstraints().getSecurityConstraints().size());
            assertEquals("jetspeed", ((SecurityConstraint)check.getSecurityConstraints().getSecurityConstraints().get(0)).getUsers());
        }
        catch (PageNotFoundException e)
        {
            assertTrue("Page /default-page.psml NOT FOUND", false);
        }
        try
        {
            Folder check = pageManager.getFolder("/");
            assertEquals("/", check.getPath());
            assertEquals("/", check.getName());
            assertEquals("Root Folder", check.getTitle());
            assertEquals("default-page.psml", check.getDefaultPage());
            assertEquals("Root", check.getShortTitle());
            assertNotNull(check.getMetadata());
            assertEquals("[fr] Root Folder", check.getTitle(Locale.FRENCH));
            assertNotNull(check.getSecurityConstraints());
            assertNotNull(check.getSecurityConstraints().getSecurityConstraintsRefs());
            assertEquals(2, check.getSecurityConstraints().getSecurityConstraintsRefs().size());
            assertEquals("public-edit", (String)check.getSecurityConstraints().getSecurityConstraintsRefs().get(1));
            assertNotNull(check.getSecurityConstraints().getSecurityConstraints());
            assertEquals(2, check.getSecurityConstraints().getSecurityConstraints().size());
            assertEquals("user,admin", ((SecurityConstraint)check.getSecurityConstraints().getSecurityConstraints().get(0)).getUsers());
            assertEquals("edit", ((SecurityConstraint)check.getSecurityConstraints().getSecurityConstraints().get(1)).getPermissions());
        }
        catch (FolderNotFoundException e)
        {
            assertTrue("Folder / NOT FOUND", false);
        }
        
        try
        {
            Page check = pageManager.getPage(deepPagePath);
            assertEquals(deepPagePath, check.getPath());
        }
        catch (PageNotFoundException e)
        {
            assertTrue("Page " + deepPagePath + " NOT FOUND", false);
        }
        try
        {
            Folder check = pageManager.getFolder(deepFolderPath);
            assertEquals(deepFolderPath, check.getPath());
        }
        catch (FolderNotFoundException e)
        {
            assertTrue("Folder " + deepFolderPath + " NOT FOUND", false);
        }
    }

    public void testRemoves() throws Exception
    {
        // reset page manager cache
        pageManager.reset();
        
        // remove root folder
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
        
        // reset page manager cache
        pageManager.reset();
        
        // verify root folder deep removal
        try
        {
            Folder check = pageManager.getFolder("/");
            assertTrue("Folder / FOUND", false);
        }
        catch (FolderNotFoundException e)
        {
        }
        try
        {
            PageSecurity check = pageManager.getPageSecurity();
            assertTrue("PageSecurity FOUND", false);
        }
        catch (DocumentNotFoundException e)
        {
        }
        try
        {
            Page check = pageManager.getPage("/default-page.psml");
            assertTrue("Page /default-page.psml FOUND", false);
        }
        catch (PageNotFoundException e)
        {
        }
        try
        {
            Folder check = pageManager.getFolder("/");
            assertTrue("Folder / FOUND", false);
        }
        catch (FolderNotFoundException e)
        {
        }
        try
        {
            Folder check = pageManager.getFolder(deepFolderPath);
            assertTrue("Folder " + deepFolderPath + " FOUND", false);
        }
        catch (FolderNotFoundException e)
        {
        }
        try
        {
            Page check = pageManager.getPage(deepPagePath);
            assertTrue("Page " + deepPagePath + " FOUND", false);
        }
        catch (PageNotFoundException e)
        {
        }
    }
}
