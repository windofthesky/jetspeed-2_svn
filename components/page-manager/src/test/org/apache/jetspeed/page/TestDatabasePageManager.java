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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToUpdateDocumentException;
import org.apache.jetspeed.page.document.Node;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestPageXmlPersistence
 * 
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 *          
 */
public class TestDatabasePageManager extends DatasourceEnabledSpringTestCase implements PageManagerTestShared, PageManagerEventListener
{
    private String deepFolderPath = "/__subsite-rootx/_user/userx/_role/rolex/_group/groupx/_mediatype/xhtml/_language/en/_country/us/_custom/customx";
    private String deepPagePath = deepFolderPath + "/default-page.psml";

    private static ClassPathXmlApplicationContext context;
    private static boolean lastTestRun;

    private static PageManager pageManager;

    private static int newNodeCount;
    private static int updatedNodeCount;
    private static int removedNodeCount;

    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestDatabasePageManager.class.getName() });
    }
    
    protected void setUp() throws Exception
    {
        // reuse context between test cases below
        // that is normally configured if null in
        // super class setUp() implementation
        if (context == null)
        {
            // new context
            super.setUp();
            context = ctx;
            lastTestRun = false;

            // lookup page manager in context
            pageManager = (PageManager)context.getBean("pageManager");
            pageManager.addListener(this);
        }
        else
        {
            // recycle context
            ctx = context;
            super.setUp();
        }
    }

    protected void tearDown() throws Exception
    {
        // save context for reuse
        if (!lastTestRun)
        {
            ctx = null;
        }
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManagerEventListener#newNode(org.apache.jetspeed.page.document.Node)
     */
    public void newNode(Node node)
    {
        newNodeCount++;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManagerEventListener#newNode(org.apache.jetspeed.page.document.Node)
     */
    public void updatedNode(Node node)
    {
        updatedNodeCount++;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.PageManagerEventListener#newNode(org.apache.jetspeed.page.document.Node)
     */
    public void removedNode(Node node)
    {
        removedNodeCount++;
    }

    public void testCreates() throws Exception
    {
        // reset page manager cache
        pageManager.reset();

        // test document and folder creation
        Folder folder = pageManager.newFolder("/");
        assertEquals("Top", folder.getTitle());
        folder.setTitle("Root Folder");
        folder.setDefaultDecorator("jetspeed", Fragment.LAYOUT);
        folder.setDefaultDecorator("gray-gradient", Fragment.PORTLET);
        folder.setSkin("skin-1");
        folder.setDefaultPage("default-page.psml");
        folder.setShortTitle("Root");
        GenericMetadata metadata = folder.getMetadata();
        metadata.addField(Locale.FRENCH, "title", "[fr] Root Folder");
        SecurityConstraints folderConstraints = pageManager.newSecurityConstraints();
        folderConstraints.setOwner("admin");
        List inlineFolderConstraints = new ArrayList(2);
        SecurityConstraint folderConstraint = pageManager.newFolderSecurityConstraint();
        folderConstraint.setUsers(Shared.makeListFromCSV("user,admin"));
        folderConstraint.setRoles(Shared.makeListFromCSV("manager"));
        folderConstraint.setGroups(Shared.makeListFromCSV("*"));
        folderConstraint.setPermissions(Shared.makeListFromCSV("view,edit"));
        inlineFolderConstraints.add(folderConstraint);
        folderConstraint = folder.newSecurityConstraint();
        folderConstraint.setPermissions(Shared.makeListFromCSV("edit"));
        inlineFolderConstraints.add(folderConstraint);
        folderConstraints.setSecurityConstraints(inlineFolderConstraints);
        List folderConstraintsRefs = new ArrayList(2);
        folderConstraintsRefs.add("public-view");
        folderConstraintsRefs.add("public-edit");
        folderConstraints.setSecurityConstraintsRefs(folderConstraintsRefs);
        folder.setSecurityConstraints(folderConstraints);
        List documentOrder = new ArrayList(2);
        documentOrder.add("some-other-page.psml");
        documentOrder.add("default-page.psml");
        folder.setDocumentOrder(documentOrder);
        MenuDefinition newMenu = folder.newMenuDefinition();
        newMenu.setName("folder-menu");
        newMenu.setTitle("The Test Folder Menu");
        newMenu.setShortTitle("Folder Menu");
        newMenu.setProfile("group-fallback");
        metadata = newMenu.getMetadata();
        metadata.addField(Locale.FRENCH, "short-title", "[fr] Folder Menu");
        metadata.addField(Locale.FRENCH, "title", "[fr] The Test Folder Menu");
        MenuSeparatorDefinition newSeparator = folder.newMenuSeparatorDefinition();
        newSeparator.setText("-- Folder Menu --");
        newSeparator.setTitle("Rollover: Folder Menu");
        newSeparator.setSkin("header");
        metadata = newSeparator.getMetadata();
        metadata.addField(Locale.FRENCH, "text", "-- [fr] Folder Menu --");
        metadata.addField(Locale.FRENCH, "title", "[fr] Rollover: Folder Menu");
        newMenu.getMenuElements().add(newSeparator);
        MenuOptionsDefinition newOptions0 = folder.newMenuOptionsDefinition();
        newOptions0.setOptions("/*.psml");
        newOptions0.setRegexp(true);
        newOptions0.setSkin("flash");
        newMenu.getMenuElements().add(newOptions0);
        MenuOptionsDefinition newOptions1 = folder.newMenuOptionsDefinition();
        newOptions1.setOptions("/folder0");
        newOptions1.setProfile("role-fallback");
        newOptions1.setOrder("/folder*");
        newOptions1.setDepth(1);
        newOptions1.setPaths(true);
        newMenu.getMenuElements().add(newOptions1);
        MenuDefinition newNestedMenu = folder.newMenuDefinition();
        newNestedMenu.setOptions("/*/");
        newNestedMenu.setRegexp(true);
        newNestedMenu.setDepth(2);
        newNestedMenu.setOrder("/x*/,/y*/,/z*/");
        newNestedMenu.setSkin("bold");
        newMenu.getMenuElements().add(newNestedMenu);
        MenuExcludeDefinition newExcludeMenu = folder.newMenuExcludeDefinition();
        newExcludeMenu.setName("exclude-menu");
        newMenu.getMenuElements().add(newExcludeMenu);
        MenuIncludeDefinition newIncludeMenu = folder.newMenuIncludeDefinition();
        newIncludeMenu.setName("include-menu");
        newIncludeMenu.setNest(true);
        newMenu.getMenuElements().add(newIncludeMenu);
        folder.getMenuDefinitions().add(newMenu);
        newMenu = folder.newMenuDefinition();
        newMenu.setName("folder-breadcrumb-menu");
        newMenu.setSkin("bread-crumbs");
        newMenu.setOptions("./");
        newMenu.setPaths(true);
        folder.getMenuDefinitions().add(newMenu);
        pageManager.updateFolder(folder);
        
        assertNull(folder.getParent());

        Page page = pageManager.newPage("/default-page.psml");
        assertEquals("Default Page", page.getTitle());
        page.setTitle("Default Page");
        page.setVersion("6.89");
        page.setDefaultDecorator("tigris", Fragment.LAYOUT);
        page.setDefaultDecorator("blue-gradient", Fragment.PORTLET);
        page.setSkin("skin-1");
        page.setShortTitle("Default");
        metadata = page.getMetadata();
        metadata.addField(Locale.FRENCH, "title", "[fr] Default Page");
        metadata.addField(Locale.JAPANESE, "title", "[ja] Default Page");
        SecurityConstraints pageConstraints = page.newSecurityConstraints();
        pageConstraints.setOwner("user");
        List inlinePageConstraints = new ArrayList(1);
        SecurityConstraint pageConstraint = page.newSecurityConstraint();
        pageConstraint.setUsers(Shared.makeListFromCSV("jetspeed"));
        pageConstraint.setPermissions(Shared.makeListFromCSV("edit"));
        inlinePageConstraints.add(pageConstraint);
        pageConstraints.setSecurityConstraints(inlinePageConstraints);
        List pageConstraintsRefs = new ArrayList(1);
        pageConstraintsRefs.add("manager-edit");
        pageConstraints.setSecurityConstraintsRefs(pageConstraintsRefs);
        page.setSecurityConstraints(pageConstraints);
        List pageMenus = new ArrayList();
        newMenu = page.newMenuDefinition();
        newMenu.setName("page-menu-1");
        newMenu.setTitle("The Test Page Menu");
        metadata = newMenu.getMetadata();
        metadata.addField(Locale.FRENCH, "title", "[fr] The Test Page Menu");
        newSeparator = page.newMenuSeparatorDefinition();
        newSeparator.setText("-- Page Menu --");
        List menuElements = new ArrayList();
        menuElements.add(newSeparator);
        newOptions0 = page.newMenuOptionsDefinition();
        newOptions0.setOptions("/*.psml");
        menuElements.add(newOptions0);
        newNestedMenu = page.newMenuDefinition();
        newNestedMenu.setOptions("/*/");
        menuElements.add(newNestedMenu);
        newExcludeMenu = page.newMenuExcludeDefinition();
        newExcludeMenu.setName("exclude-menu");
        menuElements.add(newExcludeMenu);
        newIncludeMenu = page.newMenuIncludeDefinition();
        newIncludeMenu.setName("include-menu");
        menuElements.add(newIncludeMenu);
        newMenu.setMenuElements(menuElements);
        pageMenus.add(newMenu);
        newMenu = page.newMenuDefinition();
        newMenu.setName("page-menu-2");
        newMenu.setOptions("./");
        pageMenus.add(newMenu);
        page.setMenuDefinitions(pageMenus);

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
        List preferences = new ArrayList(2);
        FragmentPreference preference = pageManager.newFragmentPreference();
        preference.setName("pref0");
        preference.setReadOnly(true);
        List preferenceValues = new ArrayList(2);
        preferenceValues.add("pref0-value0");
        preferenceValues.add("pref0-value1");
        preference.setValueList(preferenceValues);
        preferences.add(preference);
        preference = pageManager.newFragmentPreference();
        preference.setName("pref1");
        preferenceValues = new ArrayList(1);
        preferenceValues.add("pref1-value");
        preference.setValueList(preferenceValues);
        preferences.add(preference);
        portlet.setPreferences(preferences);
        root.getFragments().add(portlet);
        portlet = pageManager.newPortletFragment();
        portlet.setName("some-app::SomePortlet");
        portlet.setShortTitle("Some Portlet");
        portlet.setTitle("Some Portlet Fragment");
        portlet.setState("Normal");
        portlet.setLayoutRow(22);
        portlet.setLayoutColumn(11);
        SecurityConstraints fragmentConstraints = portlet.newSecurityConstraints();
        fragmentConstraints.setOwner("user");
        portlet.setSecurityConstraints(fragmentConstraints);
        root.getFragments().add(portlet);

        pageManager.updatePage(page);

        assertNotNull(page.getParent());
        assertEquals(page.getParent().getId(), folder.getId());
        assertNotNull(folder.getPages());
        assertEquals(1, folder.getPages().size());

        page = pageManager.newPage("/another-page.psml");
        assertEquals("Another Page", page.getTitle());
        page.setTitle("Another Page");
        pageManager.updatePage(page);
        assertNotNull(page.getParent());
        assertEquals(page.getParent().getId(), folder.getId());
        page = pageManager.newPage("/some-other-page.psml");
        assertEquals("Some Other Page", page.getTitle());
        page.setTitle("Some Other Page");
        pageManager.updatePage(page);
        assertNotNull(page.getParent());
        assertEquals(page.getParent().getId(), folder.getId());
        assertEquals(3, folder.getPages().size());

        Link link = pageManager.newLink("/default.link");
        assertEquals("Default", link.getTitle());
        link.setTitle("Default Link");
        link.setVersion("1.23");
        link.setShortTitle("Default");
        link.setTarget("top");
        link.setUrl("http://www.default.org/");
        metadata = link.getMetadata();
        metadata.addField(Locale.FRENCH, "title", "[fr] Default Link");
        metadata.addField(Locale.GERMAN, "title", "[de] Default Link");
        SecurityConstraints linkConstraints = link.newSecurityConstraints();
        linkConstraints.setOwner("user");
        List inlineLinkConstraints = new ArrayList(1);
        SecurityConstraint linkConstraint = link.newSecurityConstraint();
        linkConstraint.setUsers(Shared.makeListFromCSV("jetspeed"));
        linkConstraint.setPermissions(Shared.makeListFromCSV("edit"));
        inlineLinkConstraints.add(linkConstraint);
        linkConstraints.setSecurityConstraints(inlineLinkConstraints);
        List linkConstraintsRefs = new ArrayList(1);
        linkConstraintsRefs.add("manager-edit");
        linkConstraints.setSecurityConstraintsRefs(linkConstraintsRefs);
        link.setSecurityConstraints(linkConstraints);

        pageManager.updateLink(link);

        assertNotNull(link.getParent());
        assertEquals(link.getParent().getId(), folder.getId());
        assertNotNull(folder.getLinks());
        assertEquals(1, folder.getLinks().size());

        PageSecurity pageSecurity = pageManager.newPageSecurity();
        List constraintsDefs = new ArrayList(2);
        SecurityConstraintsDef constraintsDef = pageManager.newSecurityConstraintsDef();
        constraintsDef.setName("public-view");
        List defConstraints = new ArrayList(1);
        SecurityConstraint defConstraint = pageSecurity.newSecurityConstraint();
        defConstraint.setUsers(Shared.makeListFromCSV("*"));
        defConstraint.setPermissions(Shared.makeListFromCSV("view"));
        defConstraints.add(defConstraint);
        constraintsDef.setSecurityConstraints(defConstraints);
        constraintsDefs.add(constraintsDef);
        constraintsDef = pageSecurity.newSecurityConstraintsDef();
        constraintsDef.setName("admin-all");
        defConstraints = new ArrayList(2);
        defConstraint = pageSecurity.newSecurityConstraint();
        defConstraint.setRoles(Shared.makeListFromCSV("admin"));
        defConstraint.setPermissions(Shared.makeListFromCSV("view,edit"));
        defConstraints.add(defConstraint);
        defConstraint = pageSecurity.newSecurityConstraint();
        defConstraint.setRoles(Shared.makeListFromCSV("nobody"));
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
        assertNotNull(folder.getPageSecurity());

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
            Link dupLink = pageManager.newLink("/default.link");
            pageManager.updateLink(dupLink);
            assertTrue("Duplicate Link / CREATED", false);
        }
        catch (FailedToUpdateDocumentException e)
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
        Folder deepFolder = null;
        int pathIndex = deepFolderPath.indexOf('/', 1);
        while ((pathIndex != -1) && (pathIndex <= deepFolderPath.length()))
        {
            deepFolder = pageManager.newFolder(deepFolderPath.substring(0, pathIndex));
            pageManager.updateFolder(deepFolder);
            assertNotNull(deepFolder.getParent());
            assertNotNull(((Folder)deepFolder.getParent()).getFolders());
            assertEquals(1, ((Folder)deepFolder.getParent()).getFolders().size());

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
        Page deepPage = pageManager.newPage(deepPagePath);
        pageManager.updatePage(deepPage);
        assertNotNull(deepPage.getParent());
        assertEquals(deepPage.getParent().getId(), deepFolder.getId());

        // test folder nodesets
        assertNotNull(folder.getFolders());
        assertEquals(1, folder.getFolders().size());
        assertNotNull(folder.getAll());
        assertEquals(6, folder.getAll().size());
        Iterator all = folder.getAll().iterator();
        assertEquals("some-other-page.psml", ((Node)all.next()).getName());
        assertEquals("default-page.psml", ((Node)all.next()).getName());
        assertEquals("__subsite-rootx", ((Node)all.next()).getName());
        assertEquals("another-page.psml", ((Node)all.next()).getName());
        assertEquals("default.link", ((Node)all.next()).getName());
        assertEquals("page.security", ((Node)all.next()).getName());
        assertNotNull(folder.getAll().subset(Page.DOCUMENT_TYPE));
        assertEquals(3, folder.getAll().subset(Page.DOCUMENT_TYPE).size());
        assertNotNull(folder.getAll().inclusiveSubset(".*other.*"));
        assertEquals(2, folder.getAll().inclusiveSubset(".*other.*").size());
        assertNotNull(folder.getAll().inclusiveSubset("nomatch"));
        assertEquals(0, folder.getAll().inclusiveSubset("nomatch").size());
        assertNotNull(folder.getAll().exclusiveSubset(".*-page.psml"));
        assertEquals(3, folder.getAll().exclusiveSubset(".*-page.psml").size());
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
            assertEquals("/page.security", check.getUrl());
            assertNotNull(check.getSecurityConstraintsDefs());
            assertEquals(2, check.getSecurityConstraintsDefs().size());
            assertEquals("admin-all", ((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(0)).getName());
            assertNotNull(((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(0)).getSecurityConstraints());
            assertEquals(2, ((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(0)).getSecurityConstraints().size());
            assertEquals("view,edit", Shared.makeCSVFromList(((SecurityConstraint)((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(0)).getSecurityConstraints().get(0)).getPermissions()));
            assertEquals("public-view", ((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(1)).getName());
            assertNotNull(((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(1)).getSecurityConstraints());
            assertEquals(1, ((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(1)).getSecurityConstraints().size());
            assertEquals("*", Shared.makeCSVFromList(((SecurityConstraint)((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(1)).getSecurityConstraints().get(0)).getUsers()));
            assertEquals("view", Shared.makeCSVFromList(((SecurityConstraint)((SecurityConstraintsDef)check.getSecurityConstraintsDefs().get(1)).getSecurityConstraints().get(0)).getPermissions()));
            assertNotNull(check.getGlobalSecurityConstraintsRefs());
            assertEquals(2, check.getGlobalSecurityConstraintsRefs().size());
            assertEquals("admin-all", (String)check.getGlobalSecurityConstraintsRefs().get(0));
            assertEquals("public-view", (String)check.getGlobalSecurityConstraintsRefs().get(1));
            assertNotNull(check.getParent());
        }
        catch (DocumentNotFoundException e)
        {
            assertTrue("PageSecurity NOT FOUND", false);
        }
        try
        {
            Link check = pageManager.getLink("/default.link");
            assertEquals("/default.link", check.getPath());
            assertEquals("default.link", check.getName());
            assertEquals("Default Link", check.getTitle());
            assertEquals("1.23", check.getVersion());            
            assertEquals("Default", check.getShortTitle());
            assertEquals("top", check.getTarget());
            assertEquals("http://www.default.org/", check.getUrl());
            assertNotNull(check.getMetadata());
            assertEquals("[fr] Default Link", check.getTitle(Locale.FRENCH));
            assertEquals("[de] Default Link", check.getTitle(Locale.GERMAN));
            assertNotNull(check.getSecurityConstraints());
            assertEquals("user", check.getSecurityConstraints().getOwner());
            assertNotNull(check.getSecurityConstraints().getSecurityConstraintsRefs());
            assertEquals(1, check.getSecurityConstraints().getSecurityConstraintsRefs().size());
            assertEquals("manager-edit", (String)check.getSecurityConstraints().getSecurityConstraintsRefs().get(0));
            assertNotNull(check.getSecurityConstraints().getSecurityConstraints());
            assertEquals(1, check.getSecurityConstraints().getSecurityConstraints().size());
            assertEquals("jetspeed", Shared.makeCSVFromList(((SecurityConstraint)check.getSecurityConstraints().getSecurityConstraints().get(0)).getUsers()));
            assertNotNull(check.getParent());
        }
        catch (PageNotFoundException e)
        {
            assertTrue("Link /default.link NOT FOUND", false);
        }
        try
        {
            Page check = pageManager.getPage("/default-page.psml");
            assertEquals("/default-page.psml", check.getPath());
            assertEquals("default-page.psml", check.getName());
            assertEquals("/default-page.psml", check.getUrl());
            assertEquals("Default Page", check.getTitle());
            assertEquals("6.89", check.getVersion());            
            assertEquals("tigris", check.getEffectiveDefaultDecorator(Fragment.LAYOUT));
            assertEquals("tigris", check.getDefaultDecorator(Fragment.LAYOUT));
            assertEquals("blue-gradient", check.getDefaultDecorator(Fragment.PORTLET));
            assertEquals("skin-1", check.getSkin());
            assertEquals("Default", check.getShortTitle());
            assertNotNull(check.getMetadata());
            assertEquals("[fr] Default Page", check.getTitle(Locale.FRENCH));
            assertEquals("[ja] Default Page", check.getTitle(Locale.JAPANESE));
            assertNotNull(check.getSecurityConstraints());
            assertEquals("user", check.getSecurityConstraints().getOwner());
            assertNotNull(check.getSecurityConstraints().getSecurityConstraintsRefs());
            assertEquals(1, check.getSecurityConstraints().getSecurityConstraintsRefs().size());
            assertEquals("manager-edit", (String)check.getSecurityConstraints().getSecurityConstraintsRefs().get(0));
            assertNotNull(check.getSecurityConstraints().getSecurityConstraints());
            assertEquals(1, check.getSecurityConstraints().getSecurityConstraints().size());
            assertEquals("jetspeed", Shared.makeCSVFromList(((SecurityConstraint)check.getSecurityConstraints().getSecurityConstraints().get(0)).getUsers()));
            assertNotNull(check.getMenuDefinitions());
            assertEquals(2, check.getMenuDefinitions().size());
            MenuDefinition checkMenu = (MenuDefinition)check.getMenuDefinitions().get(0);
            assertEquals("page-menu-1", checkMenu.getName());
            assertEquals("The Test Page Menu", checkMenu.getTitle());
            assertEquals("[fr] The Test Page Menu", checkMenu.getTitle(Locale.FRENCH));
            assertNotNull(checkMenu.getMenuElements());
            assertEquals(5,checkMenu.getMenuElements().size());
            assertTrue(checkMenu.getMenuElements().get(0) instanceof MenuSeparatorDefinition);
            assertEquals("-- Page Menu --", ((MenuSeparatorDefinition)checkMenu.getMenuElements().get(0)).getText());
            assertTrue(checkMenu.getMenuElements().get(1) instanceof MenuOptionsDefinition);
            assertEquals("/*.psml", ((MenuOptionsDefinition)checkMenu.getMenuElements().get(1)).getOptions());
            assertTrue(checkMenu.getMenuElements().get(2) instanceof MenuDefinition);
            assertEquals("/*/", ((MenuDefinition)checkMenu.getMenuElements().get(2)).getOptions());
            assertNotNull(((MenuDefinition)checkMenu.getMenuElements().get(2)).getMenuElements());
            assertTrue(((MenuDefinition)checkMenu.getMenuElements().get(2)).getMenuElements().isEmpty());
            assertTrue(checkMenu.getMenuElements().get(3) instanceof MenuExcludeDefinition);
            assertEquals("exclude-menu", ((MenuExcludeDefinition)checkMenu.getMenuElements().get(3)).getName());
            assertTrue(checkMenu.getMenuElements().get(4) instanceof MenuIncludeDefinition);
            assertEquals("include-menu", ((MenuIncludeDefinition)checkMenu.getMenuElements().get(4)).getName());
            checkMenu = (MenuDefinition)check.getMenuDefinitions().get(1);
            assertEquals("page-menu-2", checkMenu.getName());
            assertNotNull(checkMenu.getMenuElements());
            assertTrue(checkMenu.getMenuElements().isEmpty());
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
            assertEquals(2, check.getRootFragment().getFragments().size());
            Fragment check0 = (Fragment)check.getRootFragment().getFragments().get(0);
            assertEquals("security::LoginPortlet", check0.getName());
            assertEquals("Portlet", check0.getShortTitle());
            assertEquals("Portlet Fragment", check0.getTitle());
            assertEquals("Normal", check0.getState());
            assertEquals(88, check0.getLayoutRow());
            assertEquals(88, check0.getIntProperty(Fragment.ROW_PROPERTY_NAME));
            assertEquals(99, check0.getLayoutColumn());
            assertNotNull(check0.getPreferences());
            assertEquals(2, check0.getPreferences().size());
            assertEquals("pref0", ((FragmentPreference)check0.getPreferences().get(0)).getName());
            assertTrue(((FragmentPreference)check0.getPreferences().get(0)).isReadOnly());
            assertNotNull(((FragmentPreference)check0.getPreferences().get(0)).getValueList());
            assertEquals(2, ((FragmentPreference)check0.getPreferences().get(0)).getValueList().size());
            assertEquals("pref0-value0", (String)((FragmentPreference)check0.getPreferences().get(0)).getValueList().get(0));
            assertEquals("pref0-value1", (String)((FragmentPreference)check0.getPreferences().get(0)).getValueList().get(1));
            assertEquals("pref1", ((FragmentPreference)check0.getPreferences().get(1)).getName());
            assertFalse(((FragmentPreference)check0.getPreferences().get(1)).isReadOnly());
            assertNotNull(((FragmentPreference)check0.getPreferences().get(1)).getValueList());
            assertEquals(1, ((FragmentPreference)check0.getPreferences().get(1)).getValueList().size());
            assertEquals("pref1-value", (String)((FragmentPreference)check0.getPreferences().get(1)).getValueList().get(0));
            Fragment check1 = (Fragment)check.getRootFragment().getFragments().get(1);
            assertEquals("some-app::SomePortlet", check1.getName());
            assertEquals("Some Portlet", check1.getShortTitle());
            assertEquals("Some Portlet Fragment", check1.getTitle());
            assertEquals("Normal", check1.getState());
            assertEquals(22, check1.getLayoutRow());
            assertEquals(11, check1.getLayoutColumn());
            assertNotNull(check1.getSecurityConstraints());
            assertEquals("user", check1.getSecurityConstraints().getOwner());
            assertNotNull(check.getFragmentById(check0.getId()));
            assertNotNull(check.getFragmentsByName("some-app::SomePortlet"));
            assertEquals(1, check.getFragmentsByName("some-app::SomePortlet").size());
            assertNotNull(check.getParent());
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
            assertEquals("/", check.getUrl());
            assertEquals("Root Folder", check.getTitle());
            assertEquals("jetspeed", check.getEffectiveDefaultDecorator(Fragment.LAYOUT));
            assertEquals("jetspeed", check.getDefaultDecorator(Fragment.LAYOUT));
            assertEquals("gray-gradient", check.getDefaultDecorator(Fragment.PORTLET));
            assertEquals("skin-1", check.getSkin());
            assertEquals("default-page.psml", check.getDefaultPage());
            assertEquals("Root", check.getShortTitle());
            assertNotNull(check.getMetadata());
            assertEquals("[fr] Root Folder", check.getTitle(Locale.FRENCH));
            assertNotNull(check.getSecurityConstraints());
            assertEquals("admin", check.getSecurityConstraints().getOwner());
            assertNotNull(check.getSecurityConstraints().getSecurityConstraintsRefs());
            assertEquals(2, check.getSecurityConstraints().getSecurityConstraintsRefs().size());
            assertEquals("public-edit", (String)check.getSecurityConstraints().getSecurityConstraintsRefs().get(1));
            assertNotNull(check.getSecurityConstraints().getSecurityConstraints());
            assertEquals(2, check.getSecurityConstraints().getSecurityConstraints().size());
            assertEquals("user,admin", Shared.makeCSVFromList(((SecurityConstraint)check.getSecurityConstraints().getSecurityConstraints().get(0)).getUsers()));
            assertEquals("manager", Shared.makeCSVFromList(((SecurityConstraint)check.getSecurityConstraints().getSecurityConstraints().get(0)).getRoles()));
            assertEquals("*", Shared.makeCSVFromList(((SecurityConstraint)check.getSecurityConstraints().getSecurityConstraints().get(0)).getGroups()));
            assertEquals("edit", Shared.makeCSVFromList(((SecurityConstraint)check.getSecurityConstraints().getSecurityConstraints().get(1)).getPermissions()));
            assertNotNull(check.getDocumentOrder());
            assertEquals(2, check.getDocumentOrder().size());
            assertEquals("some-other-page.psml", (String)check.getDocumentOrder().get(0));
            assertEquals("default-page.psml", (String)check.getDocumentOrder().get(1));
            assertNull(check.getParent());
            assertNotNull(check.getPageSecurity());
            assertNotNull(check.getPages());
            assertEquals(3, check.getPages().size());
            assertNotNull(check.getLinks());
            assertEquals(1, check.getLinks().size());
            assertNotNull(check.getFolders());
            assertEquals(1, check.getFolders().size());
            assertNotNull(check.getAll());
            assertEquals(6, check.getAll().size());
            Iterator all = check.getAll().iterator();
            assertEquals("some-other-page.psml", ((Node)all.next()).getName());
            assertEquals("default-page.psml", ((Node)all.next()).getName());
            assertEquals("__subsite-rootx", ((Node)all.next()).getName());
            assertEquals("another-page.psml", ((Node)all.next()).getName());
            assertEquals("default.link", ((Node)all.next()).getName());
            assertEquals("page.security", ((Node)all.next()).getName());
            assertNotNull(check.getMenuDefinitions());
            assertEquals(2, check.getMenuDefinitions().size());
            MenuDefinition checkMenu = (MenuDefinition)check.getMenuDefinitions().get(0);
            assertEquals("folder-breadcrumb-menu", checkMenu.getName());
            assertEquals("bread-crumbs", checkMenu.getSkin());
            assertEquals("./", checkMenu.getOptions());
            assertTrue(checkMenu.isPaths());
            assertNotNull(checkMenu.getMenuElements());
            assertTrue(checkMenu.getMenuElements().isEmpty());
            checkMenu = (MenuDefinition)check.getMenuDefinitions().get(1);
            assertEquals("folder-menu", checkMenu.getName());
            assertEquals("The Test Folder Menu", checkMenu.getTitle());
            assertEquals("Folder Menu", checkMenu.getShortTitle());
            assertEquals("group-fallback", checkMenu.getProfile());
            assertEquals("[fr] Folder Menu", checkMenu.getShortTitle(Locale.FRENCH));
            assertEquals("[fr] The Test Folder Menu", checkMenu.getTitle(Locale.FRENCH));
            assertNotNull(checkMenu.getMenuElements());
            assertEquals(6,checkMenu.getMenuElements().size());
            assertTrue(checkMenu.getMenuElements().get(0) instanceof MenuSeparatorDefinition);
            assertEquals("-- Folder Menu --", ((MenuSeparatorDefinition)checkMenu.getMenuElements().get(0)).getText());
            assertEquals("Rollover: Folder Menu", ((MenuSeparatorDefinition)checkMenu.getMenuElements().get(0)).getTitle());
            assertEquals("header", ((MenuSeparatorDefinition)checkMenu.getMenuElements().get(0)).getSkin());
            assertEquals("-- [fr] Folder Menu --", ((MenuSeparatorDefinition)checkMenu.getMenuElements().get(0)).getText(Locale.FRENCH));
            assertEquals("[fr] Rollover: Folder Menu", ((MenuSeparatorDefinition)checkMenu.getMenuElements().get(0)).getTitle(Locale.FRENCH));
            assertTrue(checkMenu.getMenuElements().get(1) instanceof MenuOptionsDefinition);
            assertEquals("/*.psml", ((MenuOptionsDefinition)checkMenu.getMenuElements().get(1)).getOptions());
            assertTrue(((MenuOptionsDefinition)checkMenu.getMenuElements().get(1)).isRegexp());
            assertEquals("flash", ((MenuOptionsDefinition)checkMenu.getMenuElements().get(1)).getSkin());
            assertTrue(checkMenu.getMenuElements().get(2) instanceof MenuOptionsDefinition);
            assertEquals("/folder0", ((MenuOptionsDefinition)checkMenu.getMenuElements().get(2)).getOptions());
            assertEquals("role-fallback", ((MenuOptionsDefinition)checkMenu.getMenuElements().get(2)).getProfile());
            assertEquals("/folder*", ((MenuOptionsDefinition)checkMenu.getMenuElements().get(2)).getOrder());
            assertEquals(1, ((MenuOptionsDefinition)checkMenu.getMenuElements().get(2)).getDepth());
            assertTrue(((MenuOptionsDefinition)checkMenu.getMenuElements().get(2)).isPaths());
            assertTrue(checkMenu.getMenuElements().get(3) instanceof MenuDefinition);
            assertEquals("/*/", ((MenuDefinition)checkMenu.getMenuElements().get(3)).getOptions());
            assertTrue(((MenuDefinition)checkMenu.getMenuElements().get(3)).isRegexp());
            assertEquals(2, ((MenuDefinition)checkMenu.getMenuElements().get(3)).getDepth());
            assertEquals("/x*/,/y*/,/z*/", ((MenuDefinition)checkMenu.getMenuElements().get(3)).getOrder());
            assertEquals("bold", ((MenuDefinition)checkMenu.getMenuElements().get(3)).getSkin());
            assertNotNull(((MenuDefinition)checkMenu.getMenuElements().get(3)).getMenuElements());
            assertTrue(((MenuDefinition)checkMenu.getMenuElements().get(3)).getMenuElements().isEmpty());
            assertTrue(checkMenu.getMenuElements().get(4) instanceof MenuExcludeDefinition);
            assertEquals("exclude-menu", ((MenuExcludeDefinition)checkMenu.getMenuElements().get(4)).getName());
            assertTrue(checkMenu.getMenuElements().get(5) instanceof MenuIncludeDefinition);
            assertEquals("include-menu", ((MenuIncludeDefinition)checkMenu.getMenuElements().get(5)).getName());
            assertTrue(((MenuIncludeDefinition)checkMenu.getMenuElements().get(5)).isNest());
        }
        catch (FolderNotFoundException e)
        {
            assertTrue("Folder / NOT FOUND", false);
        }
        try
        {
            Page check = pageManager.getPage("/another-page.psml");
            assertEquals("/another-page.psml", check.getPath());
            assertEquals("another-page.psml", check.getName());
            assertEquals("Another Page", check.getTitle());
            assertEquals("jetspeed", check.getEffectiveDefaultDecorator(Fragment.LAYOUT));
            assertEquals("gray-gradient", check.getEffectiveDefaultDecorator(Fragment.PORTLET));
            assertNull(check.getDefaultDecorator(Fragment.LAYOUT));
            assertNull(check.getDefaultDecorator(Fragment.PORTLET));
        }
        catch (PageNotFoundException e)
        {
            assertTrue("Page /default-page.psml NOT FOUND", false);
        }

        try
        {
            Page check = pageManager.getPage(deepPagePath);
            assertEquals(deepPagePath, check.getPath());
            assertNotNull(check.getParent());
        }
        catch (PageNotFoundException e)
        {
            assertTrue("Page " + deepPagePath + " NOT FOUND", false);
        }
        try
        {
            Folder check = pageManager.getFolder(deepFolderPath);
            assertEquals(deepFolderPath, check.getPath());
            assertNotNull(check.getParent());
        }
        catch (FolderNotFoundException e)
        {
            assertTrue("Folder " + deepFolderPath + " NOT FOUND", false);
        }
    }

    public void testUpdates() throws Exception
    {
        // reset page manager cache
        pageManager.reset();
        
        // update documents and folders in persisted store
        PageSecurity pageSecurity = pageManager.getPageSecurity();
        assertEquals("/page.security", pageSecurity.getPath());
        pageSecurity.getGlobalSecurityConstraintsRefs().add("UPDATED");
        pageManager.updatePageSecurity(pageSecurity);

        Page page = pageManager.getPage("/default-page.psml");
        assertEquals("/default-page.psml", page.getPath());
        page.setTitle("UPDATED");
        page.getRootFragment().getProperties().remove("custom-prop1");
        page.getRootFragment().getProperties().put("UPDATED", "UPDATED");
        assertNotNull(page.getRootFragment().getFragments());
        assertEquals(2, page.getRootFragment().getFragments().size());
        String removeId = ((Fragment)page.getRootFragment().getFragments().get(1)).getId();
        assertNotNull(page.removeFragmentById(removeId));
        SecurityConstraint pageConstraint = page.newSecurityConstraint();
        pageConstraint.setUsers(Shared.makeListFromCSV("UPDATED"));
        page.getSecurityConstraints().getSecurityConstraints().add(0, pageConstraint);
        pageManager.updatePage(page);

        Link link = pageManager.getLink("/default.link");
        assertEquals("/default.link", link.getPath());
        link.setTitle("UPDATED");
        link.getSecurityConstraints().setOwner("UPDATED");
        pageManager.updateLink(link);

        Folder folder = pageManager.getFolder("/");
        assertEquals("/", folder.getPath());
        folder.setTitle("UPDATED");
        folder.getDocumentOrder().remove("some-other-page.psml");
        folder.getDocumentOrder().add("UPDATED");
        folder.getDocumentOrder().add("some-other-page.psml");
        MenuDefinition updateMenu = (MenuDefinition)folder.getMenuDefinitions().get(1);
        updateMenu.setName("UPDATED");
        updateMenu.setTitle("UPDATED");
        updateMenu.getMetadata().addField(Locale.JAPANESE, "short-title", "[ja] UPDATED");
        ((MenuOptionsDefinition)updateMenu.getMenuElements().get(2)).setProfile("UPDATED");
        pageManager.updateFolder(folder);

        assertNotNull(folder.getAll());
        assertEquals(6, folder.getAll().size());
        Iterator all = folder.getAll().iterator();
        assertEquals("default-page.psml", ((Node)all.next()).getName());
        assertEquals("some-other-page.psml", ((Node)all.next()).getName());
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
            Link check = pageManager.getLink("/default.link");
            assertTrue("Link /default.link FOUND", false);
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

    public void testEvents() throws Exception
    {
        // verify listener functionality and operation counts
        assertEquals(22, newNodeCount);
        assertEquals(4, updatedNodeCount);
        assertEquals(1, removedNodeCount);

        // last test has been run
        lastTestRun = true;
    }
}
