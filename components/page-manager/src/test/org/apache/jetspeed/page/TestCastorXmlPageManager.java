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

// Java imports
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.JetspeedActions;
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
import org.apache.jetspeed.om.page.Document;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToDeleteFolderException;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.psml.CastorXmlPageManager;

/**
 * TestCastorXmlPageManager
 * 
 * @author <a href="raphael@apache.org">Rapha\u00ebl Luta</a>
 * @author <a href="rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class TestCastorXmlPageManager extends TestCase implements PageManagerTestShared 
{
    private String testPage002 = "/test002.psml";
    private String testPage003 = "/test003.psml";
    private String testPage004 = "/folder2/test004.psml";
    private String testFolder2 = "/folder2";
    private String testFolder3 = "/folder3";
    private String testLink002 = "/test002.link";
    private String testLink003 = "/test003.link";

    protected CastorXmlPageManager pageManager;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        pageManager = Shared.makeCastorXMLPageManager("pages", false, false);
    }

    /**
     * <p>
     * tearDown
     * </p>
     * 
     * @see junit.framework.TestCase#tearDown()
     * @throws java.lang.Exception
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Defines the testcase name for JUnit.
     * 
     * @param name
     *            the testcase's name.
     */
    public TestCastorXmlPageManager( String name )
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
        junit.awtui.TestRunner.main(new String[]{TestCastorXmlPageManager.class.getName()});
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
        return new TestSuite(TestCastorXmlPageManager.class);
    }

    public void testNewPage()
    {
        Page testpage = pageManager.newPage(this.testPage003);
        assertNotNull(testpage);
        assertNotNull(testpage.getId());
        assertNotNull(testpage.getPath());
        assertEquals(testpage.getId(), testpage.getPath());
        assertNotNull(testpage.getRootFragment());
        assertNotNull(testpage.getRootFragment().getId());
    }

    public void testNewFragment()
    {
        Fragment f = pageManager.newFragment();
        assertNotNull(f);
        assertNotNull(f.getId());
        assertTrue(f.getType().equals(Fragment.LAYOUT));
    }

    public void testNewFolder()
    {
        Folder testfolder = pageManager.newFolder(this.testFolder3);
        assertNotNull(testfolder);
        assertNotNull(testfolder.getId());
        assertNotNull(testfolder.getPath());
        assertEquals(testfolder.getId(), testfolder.getPath());
    }

    public void testNewLink()
    {
        Link testlink = pageManager.newLink(this.testLink003);
        assertNotNull(testlink);
        assertNotNull(testlink.getId());
        assertNotNull(testlink.getPath());
        assertEquals(testlink.getId(), testlink.getPath());
    }

    public void testGetPage() throws Exception
    {
        Page testpage = pageManager.getPage("/test001.psml");
        assertNotNull(testpage);
        assertTrue(testpage.getId().equals("/test001.psml"));
        assertTrue(testpage.getTitle().equals("Test Page"));
        assertTrue(testpage.getSkin().equals("test-skin"));
        assertTrue(testpage.getEffectiveDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(testpage.getDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(testpage.getDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));
        assertTrue(testpage.getVersion().equals("2.77"));

        GenericMetadata md = testpage.getMetadata();
        Collection descriptions = md.getFields("description");
        Collection subjects = md.getFields("subject");
        assertEquals(2, descriptions.size());
        assertEquals(1, subjects.size());

        Fragment root = testpage.getRootFragment();
        assertNotNull(root);
        assertTrue(root.getId().equals("f001"));
        assertTrue(root.getName().equals("TwoColumns"));
        assertTrue(root.getType().equals(Fragment.LAYOUT));
        assertNull(root.getDecorator());

        List children = root.getFragments();
        assertNotNull(children);
        assertTrue(children.size() == 3);

        Fragment f = (Fragment) children.get(0);
        assertTrue(f.getId().equals("pe001"));
        assertTrue(f.getName().equals("HelloPortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));

        Map properties = f.getProperties();
        assertNotNull(properties);
        assertTrue(properties.size() == 7);
        assertEquals("0", f.getProperty(Fragment.ROW_PROPERTY_NAME));
        assertEquals(0, f.getIntProperty(Fragment.COLUMN_PROPERTY_NAME));
        assertEquals(0, f.getLayoutRow());
        assertEquals(0, f.getLayoutColumn());
        assertNotNull(f.getProperty(Fragment.X_PROPERTY_NAME));
        assertTrue(f.getProperty(Fragment.X_PROPERTY_NAME).startsWith("11.1"));
        assertTrue((f.getLayoutX() > 11.0F) && (f.getLayoutX() < 12.0F));
        assertTrue((f.getFloatProperty(Fragment.X_PROPERTY_NAME) > 11.0F) &&
                   (f.getFloatProperty(Fragment.X_PROPERTY_NAME) < 12.0F));
        assertTrue((f.getLayoutY() > 22.0F) && (f.getLayoutY() < 23.0F));
        assertTrue((f.getLayoutZ() > 33.0F) && (f.getLayoutZ() < 34.0F));
        assertTrue((f.getLayoutWidth() > 44.0F) && (f.getLayoutWidth() < 45.0F));
        assertTrue((f.getLayoutHeight() > 55.0F) && (f.getLayoutWidth() < 56.0F));

        List preferences = f.getPreferences();
        assertNotNull(preferences);
        assertTrue(preferences.size() == 2);
        assertEquals("pref0", ((FragmentPreference)preferences.get(0)).getName());
        assertTrue(((FragmentPreference)preferences.get(0)).isReadOnly());
        assertNotNull(((FragmentPreference)preferences.get(0)).getValueList());
        assertEquals(2, ((FragmentPreference)preferences.get(0)).getValueList().size());
        assertEquals("pref0-value0", (String)((FragmentPreference)preferences.get(0)).getValueList().get(0));
        assertEquals("pref0-value1", (String)((FragmentPreference)preferences.get(0)).getValueList().get(1));
        assertEquals("pref1", ((FragmentPreference)preferences.get(1)).getName());
        assertFalse(((FragmentPreference)preferences.get(1)).isReadOnly());
        assertNotNull(((FragmentPreference)preferences.get(1)).getValueList());
        assertEquals(1, ((FragmentPreference)preferences.get(1)).getValueList().size());
        assertEquals("pref1-value", (String)((FragmentPreference)preferences.get(1)).getValueList().get(0));

        f = (Fragment) children.get(1);
        assertTrue(f.getId().equals("pe002"));
        assertTrue(f.getName().equals("JMXPortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));

        properties = f.getProperties();
        assertNotNull(properties);
        assertTrue(properties.size() == 2);
        assertEquals("0", f.getProperty(Fragment.ROW_PROPERTY_NAME));
        assertEquals(1, f.getIntProperty(Fragment.COLUMN_PROPERTY_NAME));

        f = testpage.getFragmentById("f002");
        assertNotNull(f);
        assertTrue(f.getId().equals("f002"));
        assertTrue(f.getName().equals("Card"));
        assertTrue(f.getType().equals(Fragment.LAYOUT));
        assertTrue(f.getDecorator().equals("Tab"));
        assertNotNull(f.getFragments());
        assertTrue(f.getFragments().size() == 2);

        List fragments = testpage.getFragmentsByName("JMXPortlet");
        assertNotNull(fragments);
        assertEquals(1, fragments.size());
        assertTrue(((Fragment)fragments.get(0)).getId().equals("pe002"));
        assertTrue(((Fragment)fragments.get(0)).getName().equals("JMXPortlet"));
        assertTrue(((Fragment)fragments.get(0)).getType().equals(Fragment.PORTLET));
    }

    public void testCreatePage() throws Exception
    {
        Page page = pageManager.newPage(this.testPage002);
        System.out.println("Retrieved test_id in create " + this.testPage002);
        page.setSkin("myskin");
        page.setTitle("Created Page");
        GenericMetadata metadata = page.getMetadata();
        metadata.addField(Locale.FRENCH, "title", "Created Page de PSML");
        metadata.addField(Locale.JAPANESE, "title", "Created \u3078\u3088\u3046\u3053\u305d");

        Fragment root = page.getRootFragment();
        root.setName("TestLayout");
        Fragment f = pageManager.newFragment();
        f.setType(Fragment.PORTLET);
        f.setName("TestPortlet");
        Map properties = f.getProperties();
        properties.put(Fragment.ROW_PROPERTY_NAME, "0");
        properties.put(Fragment.COLUMN_PROPERTY_NAME, "0");
        root.getFragments().add(f);

        SecurityConstraints constraints = page.newSecurityConstraints();
        constraints.setOwner("new-user");
        List constraintsList = new ArrayList(1);
        SecurityConstraint constraint = page.newSecurityConstraint();
        constraint.setUsers(Shared.makeListFromCSV("user10,user11"));
        constraint.setRoles(Shared.makeListFromCSV("*"));
        constraint.setPermissions(Shared.makeListFromCSV(JetspeedActions.EDIT + "," + JetspeedActions.VIEW));
        constraintsList.add(constraint);
        constraints.setSecurityConstraints(constraintsList);
        List constraintsRefsList = new ArrayList(1);
        constraintsRefsList.add("public-view");
        constraints.setSecurityConstraintsRefs(constraintsRefsList);
        page.setSecurityConstraints(constraints);

        try
        {
            pageManager.updatePage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        page = pageManager.getPage(this.testPage002);
        assertNotNull(page);
        assertTrue(page.getId().equals(this.testPage002));
        assertEquals("Created Page", page.getTitle());
        assertEquals("Created Page de PSML", page.getTitle(Locale.FRENCH));
        assertEquals("Created \u3078\u3088\u3046\u3053\u305d", page.getTitle(Locale.JAPANESE));
        assertNotNull(page.getRootFragment());
        assertTrue(page.getRootFragment().getName().equals("TestLayout"));
        assertTrue(page.getRootFragment().getFragments().size() == 1);

        f = (Fragment) page.getRootFragment().getFragments().get(0);
        assertNotNull(f.getProperties());
        assertEquals(0, f.getIntProperty(Fragment.ROW_PROPERTY_NAME));
    }

    public void testCreateFolder() throws Exception
    {
        Folder folder = pageManager.newFolder(this.testFolder2);
        System.out.println("Retrieved test_id in create " + this.testFolder2);
        folder.setTitle("Created Folder");
        folder.setSkin("test-skin");
        folder.setDefaultDecorator("test-layout", Fragment.LAYOUT);
        folder.setDefaultDecorator("test-portlet", Fragment.PORTLET);

        try
        {
            pageManager.updateFolder(folder);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in folder update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        folder = pageManager.getFolder(this.testFolder2);
        assertNotNull(folder);
        assertTrue(folder.getId().equals(this.testFolder2));
        assertTrue(folder.getTitle().equals("Created Folder"));
        assertTrue(folder.getSkin().equals("test-skin"));
        assertTrue(folder.getEffectiveDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(folder.getDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(folder.getDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));
    }

    public void testCreateLink() throws Exception
    {
        Link link = pageManager.newLink(this.testLink002);
        System.out.println("Retrieved test_id in create " + this.testLink002);
        link.setTitle("Created Link");
        link.setSkin("test-skin");
        link.setUrl("http://www.created.link.com/");

        try
        {
            pageManager.updateLink(link);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in link update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        link = pageManager.getLink(this.testLink002);
        assertNotNull(link);
        assertTrue(link.getId().equals(this.testLink002));
        assertTrue(link.getTitle().equals("Created Link"));
        assertTrue(link.getSkin().equals("test-skin"));
    }

    public void testUpdatePage() throws Exception
    {
        Page page = pageManager.getPage(this.testPage002);
        page.setTitle("Updated Title");
        Fragment root = page.getRootFragment();
        assertNotNull(root);
        assertNotNull(root.getFragments());
        assertEquals(1, root.getFragments().size());
        String testId = ((Fragment)root.getFragments().get(0)).getId();
        assertNotNull(page.removeFragmentById(testId));

        try
        {
            pageManager.updatePage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        page = pageManager.getPage(this.testPage002);
        assertTrue(page.getTitle().equals("Updated Title"));
        root = page.getRootFragment();
        assertNotNull(root);
        assertNotNull(root.getFragments());
        assertTrue(root.getFragments().isEmpty());
    }

    public void testUpdateFolder() throws Exception
    {
        Folder folder = pageManager.getFolder(this.testFolder2);
        folder.setTitle("Updated Title");

        try
        {
            pageManager.updateFolder(folder);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in folder update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        folder = pageManager.getFolder(this.testFolder2);
        assertTrue(folder.getTitle().equals("Updated Title"));

        Page page = pageManager.newPage(this.testPage004);
        page.setTitle("Folder Page");

        try
        {
            pageManager.updatePage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        assertEquals(1, folder.getPages().size());
        assertNotNull(folder.getPages().get(this.testPage004));
    }

    public void testUpdateLink() throws Exception
    {
        Link link = pageManager.getLink(this.testLink002);
        link.setTitle("Updated Title");

        try
        {
            pageManager.updateLink(link);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in link update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        link = pageManager.getLink(this.testLink002);
        assertTrue(link.getTitle().equals("Updated Title"));
    }

    public void testFolders() throws Exception
    {

        Folder folder1 = pageManager.getFolder("/folder1");
        assertNotNull(folder1);
        assertTrue(folder1.getSkin().equals("test-skin"));
        assertTrue(folder1.getEffectiveDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(folder1.getDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(folder1.getDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));
                
        assertEquals(2, folder1.getFolders().size());
        Iterator childItr = folder1.getFolders().iterator();
        // Test that the folders are naturally orderd
        Folder folder2 = (Folder) childItr.next();
        assertEquals("/folder1/folder2", folder2.getPath());
        assertEquals("folder2", folder2.getName());
        Folder folder3 = (Folder) childItr.next();
        assertEquals("/folder1/folder3", folder3.getPath());
        assertEquals("test001.psml", folder3.getDefaultPage());
        assertEquals(1, folder2.getPages().size());
        assertEquals(2, folder3.getPages().size());

        // test folder decoration inheritance
        Page page = (Page)folder3.getPages().get("test001.psml");
        assertTrue(page.getEffectiveDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(page.getEffectiveDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));
        
        // Check link order
        Iterator linkItr = folder3.getAll().iterator();
        assertEquals("Jetspeed2Wiki.link", ((Link)linkItr.next()).getName());
        assertEquals("Jetspeed2.link", ((Link)linkItr.next()).getName());        
        assertEquals("apache_portals.link", ((Link)linkItr.next()).getName());
        assertEquals("apache.link", ((Link)linkItr.next()).getName());
        assertEquals("test001.psml", ((Page)linkItr.next()).getName());
        assertEquals("default-page.psml", ((Page)linkItr.next()).getName());
        

        //Test FolderSet with both absolute and relative names
        assertNotNull(folder1.getFolders().get("/folder1/folder2"));
        assertNotNull(folder1.getFolders().get("folder2"));
        assertEquals(folder1.getFolders().get("/folder1/folder2"), folder1.getFolders().get("folder2"));

        //Test PageSet with both absolute and relative names
        assertNotNull(folder3.getPages().get("/folder1/folder3/test001.psml"));
        assertNotNull(folder3.getPages().get("test001.psml"));
        assertEquals("test001.psml", folder3.getPages().get("/folder1/folder3/test001.psml").getName());
        
        assertTrue(folder3.isHidden());
        assertTrue(folder3.getPage("default-page.psml").isHidden());
        assertTrue(folder3.getLinks().get("Jetspeed2.link").isHidden());
        assertFalse(folder3.getLinks().get("apache.link").isHidden());
        
        assertNotNull(folder3.getAll().get("Jetspeed2.link"));
        assertNull(folder3.getAll().exclusiveSubset("Jetspeed2\\.link").get("Jetspeed2.link"));
        assertNull(folder3.getAll().inclusiveSubset("apache\\.link").get("Jetspeed2.link"));
        assertNotNull(folder3.getAll().inclusiveSubset("apache\\.link").get("apache.link"));
    }

    public void testFolderMetaData() throws Exception
    {
        Folder folder1French = pageManager.getFolder("/folder1");        

        assertEquals("Titre francais pour la chemise 1", folder1French.getTitle(Locale.FRENCH));
        assertEquals("Titre francais pour la chemise 1", folder1French.getTitle(Locale.FRANCE));

        Folder folder1English = pageManager.getFolder("/folder1");

        assertEquals("English Title for Folder 1", folder1English.getTitle(Locale.ENGLISH));

        // check that default works
        Folder folder1German = pageManager.getFolder("/folder1");
       
        assertEquals("Default Title for Folder 1", folder1German.getTitle(Locale.GERMAN));
        
        // Test folder with no metadata assigned
        Folder rootFolder = pageManager.getFolder("/");
        assertEquals(rootFolder.getTitle(), rootFolder.getTitle(Locale.FRENCH));
    }

    public void testDefaultTitles() throws Exception
    {
        Page defaultPage = pageManager.getPage("/folder1/folder2/default-page.psml");
        assertNotNull(defaultPage);
        assertEquals("Default Page", defaultPage.getTitle());

        Folder rootFolder = pageManager.getFolder("/");
        assertEquals("Top", rootFolder.getTitle());
    }

    public void testPageMetaData() throws Exception
    {
        Page page = pageManager.getPage("/default-page.psml");
        assertNotNull(page);
        String frenchTitle = page.getTitle(Locale.FRENCH);
        assertNotNull(frenchTitle);
        assertEquals("Ma Premiere Page de PSML", frenchTitle);
        String japaneseTitle = page.getTitle(Locale.JAPANESE);
        assertNotNull(japaneseTitle);
        assertEquals("Jetspeed 2 \u3078\u3088\u3046\u3053\u305d", japaneseTitle);
        String defaultTitle = page.getTitle(Locale.GERMAN);
        assertNotNull(defaultTitle);
        assertEquals("My First PSML Page", defaultTitle);
    }

    public void testLinks() throws Exception
    {
        Link link = pageManager.getLink("/apache_portals.link");
        assertNotNull(link);
        assertEquals("http://portals.apache.org", link.getUrl());
        assertEquals("Apache Portals Website", link.getTitle());
        assertEquals("Apache Software Foundation [french]", link.getTitle(Locale.FRENCH));
        assertEquals("test-skin", link.getSkin());

        Folder folder = pageManager.getFolder("/");
        assertNotNull(folder);
        assertNotNull(folder.getLinks());
        assertEquals(2,folder.getLinks().size());
        assertEquals("http://portals.apache.org", ((Document) folder.getLinks().get("/apache_portals.link")).getUrl());
    }

    public void testMenuDefinitions() throws Exception
    {
        // test folder resident menu definitions
        Folder folder = pageManager.getFolder("/");
        assertNotNull(folder);
        List menus = folder.getMenuDefinitions();
        assertNotNull(menus);
        assertEquals(5, menus.size());

        MenuDefinition simpleMenu = (MenuDefinition)menus.get(0);
        assertNotNull(simpleMenu);
        assertEquals("simple", simpleMenu.getName());
        assertNotNull(simpleMenu.getMenuElements());
        assertEquals(1, simpleMenu.getMenuElements().size());
        assertTrue(simpleMenu.getMenuElements().get(0) instanceof MenuOptionsDefinition);
        assertEquals("/test001.psml,/folder1/folder2", ((MenuOptionsDefinition)simpleMenu.getMenuElements().get(0)).getOptions());

        MenuDefinition top2LevelsMenu = (MenuDefinition)menus.get(1);
        assertNotNull(top2LevelsMenu);
        assertEquals("top-2-levels", top2LevelsMenu.getName());
        assertNull(top2LevelsMenu.getMenuElements());
        assertEquals("/", top2LevelsMenu.getOptions());
        assertEquals(2, top2LevelsMenu.getDepth());
        assertEquals("dhtml-pull-down", top2LevelsMenu.getSkin());

        MenuDefinition topRolePagesMenu = (MenuDefinition)menus.get(2);
        assertNotNull(topRolePagesMenu);
        assertEquals("top-role-pages", topRolePagesMenu.getName());
        assertTrue(topRolePagesMenu.isRegexp());
        assertEquals("roles", topRolePagesMenu.getProfile());
        assertEquals("*.psml,*.link", topRolePagesMenu.getOrder());

        MenuDefinition breadCrumbsMenu = (MenuDefinition)menus.get(3);
        assertNotNull(breadCrumbsMenu);
        assertEquals("bread-crumbs", breadCrumbsMenu.getName());
        assertTrue(breadCrumbsMenu.isPaths());

        MenuDefinition topCustomMenu = (MenuDefinition)menus.get(4);
        assertNotNull(topCustomMenu);
        assertEquals("top-custom", topCustomMenu.getName());
        assertEquals("Top Menu", topCustomMenu.getTitle());
        assertEquals("Top", topCustomMenu.getShortTitle());
        assertEquals("Haut", topCustomMenu.getTitle(Locale.FRENCH));
        assertEquals("H", topCustomMenu.getShortTitle(Locale.FRENCH));
        assertNotNull(topCustomMenu.getMenuElements());
        assertEquals(5, topCustomMenu.getMenuElements().size());
        assertTrue(topCustomMenu.getMenuElements().get(0) instanceof MenuOptionsDefinition);
        assertTrue(((MenuOptionsDefinition)topCustomMenu.getMenuElements().get(0)).isRegexp());
        assertEquals("groups", ((MenuOptionsDefinition)topCustomMenu.getMenuElements().get(0)).getProfile());
        assertTrue(topCustomMenu.getMenuElements().get(1) instanceof MenuDefinition);
        assertTrue(topCustomMenu.getMenuElements().get(2) instanceof MenuExcludeDefinition);
        assertEquals("top-role-pages", ((MenuExcludeDefinition)topCustomMenu.getMenuElements().get(2)).getName());
        assertTrue(topCustomMenu.getMenuElements().get(3) instanceof MenuSeparatorDefinition);
        assertEquals("More Top Pages", ((MenuSeparatorDefinition)topCustomMenu.getMenuElements().get(3)).getText());
        assertTrue(topCustomMenu.getMenuElements().get(4) instanceof MenuIncludeDefinition);
        assertEquals("simple", ((MenuIncludeDefinition)topCustomMenu.getMenuElements().get(4)).getName());
        assertTrue(((MenuIncludeDefinition)topCustomMenu.getMenuElements().get(4)).isNest());

        MenuDefinition topCustomNestedMenu = (MenuDefinition)topCustomMenu.getMenuElements().get(1);
        assertEquals("/", topCustomNestedMenu.getOptions());
        assertEquals("page", topCustomNestedMenu.getProfile());
        assertEquals(5, topCustomNestedMenu.getMenuElements().size());
        assertTrue(topCustomNestedMenu.getMenuElements().get(0) instanceof MenuSeparatorDefinition);
        assertEquals("Top Pages", ((MenuSeparatorDefinition)topCustomNestedMenu.getMenuElements().get(0)).getText());
        assertEquals("Ye Olde Top Pages", ((MenuSeparatorDefinition)topCustomNestedMenu.getMenuElements().get(0)).getText(Locale.ENGLISH));
        assertEquals("Select from Top Pages menu...", ((MenuSeparatorDefinition)topCustomNestedMenu.getMenuElements().get(0)).getTitle());
        assertEquals("Haut", ((MenuSeparatorDefinition)topCustomNestedMenu.getMenuElements().get(0)).getTitle(Locale.FRENCH));
        assertTrue(topCustomNestedMenu.getMenuElements().get(1) instanceof MenuOptionsDefinition);
        assertTrue(topCustomNestedMenu.getMenuElements().get(2) instanceof MenuSeparatorDefinition);
        assertEquals("bold", ((MenuSeparatorDefinition)topCustomNestedMenu.getMenuElements().get(2)).getSkin());
        assertEquals("Custom Pages", ((MenuSeparatorDefinition)topCustomNestedMenu.getMenuElements().get(2)).getTitle());
        assertTrue(topCustomNestedMenu.getMenuElements().get(3) instanceof MenuOptionsDefinition);
        assertEquals(1, ((MenuOptionsDefinition)topCustomNestedMenu.getMenuElements().get(3)).getDepth());
        assertEquals("*.psml", ((MenuOptionsDefinition)topCustomNestedMenu.getMenuElements().get(3)).getOrder());
        assertTrue(topCustomNestedMenu.getMenuElements().get(4) instanceof MenuOptionsDefinition);
        assertTrue(((MenuOptionsDefinition)topCustomNestedMenu.getMenuElements().get(4)).isPaths());
        assertEquals("*", ((MenuOptionsDefinition)topCustomNestedMenu.getMenuElements().get(4)).getProfile());
        assertEquals("links", ((MenuOptionsDefinition)topCustomNestedMenu.getMenuElements().get(4)).getSkin());
        assertEquals("@", ((MenuOptionsDefinition)topCustomNestedMenu.getMenuElements().get(4)).getOptions());

        // test page resident menu definitions
        Page page = pageManager.getPage("/test001.psml");
        assertNotNull(page);
        menus = page.getMenuDefinitions();
        assertNotNull(menus);
        assertEquals(1, menus.size());

        simpleMenu = (MenuDefinition)menus.get(0);
        assertNotNull(simpleMenu);
        assertEquals("simple", simpleMenu.getName());
        assertNotNull(simpleMenu.getMenuElements());
        assertEquals(2, simpleMenu.getMenuElements().size());

        // test writing page menu definitions
        page = pageManager.getPage(this.testPage002);
        page.setMenuDefinitions(new ArrayList());
        MenuDefinition newMenu = page.newMenuDefinition();
        newMenu.setName("updated-menu");
        newMenu.setSkin("tabs");
        newMenu.setMenuElements(new ArrayList());
        MenuSeparatorDefinition newSeparator = page.newMenuSeparatorDefinition();
        newSeparator.setText("-- Updated Menu --");
        newMenu.getMenuElements().add(newSeparator);
        MenuOptionsDefinition newOptions0 = page.newMenuOptionsDefinition();
        newOptions0.setOptions("/*.psml");
        newOptions0.setRegexp(true);
        newMenu.getMenuElements().add(newOptions0);
        MenuOptionsDefinition newOptions1 = page.newMenuOptionsDefinition();
        newOptions1.setOptions("/folder0");
        newMenu.getMenuElements().add(newOptions1);
        MenuDefinition newNestedMenu = page.newMenuDefinition();
        newNestedMenu.setOptions("/*/");
        newNestedMenu.setRegexp(true);
        newMenu.getMenuElements().add(newNestedMenu);
        MenuExcludeDefinition newExcludeMenu = page.newMenuExcludeDefinition();
        newExcludeMenu.setName("exclude-menu");
        newMenu.getMenuElements().add(newExcludeMenu);
        MenuIncludeDefinition newIncludeMenu = page.newMenuIncludeDefinition();
        newIncludeMenu.setName("include-menu");
        newIncludeMenu.setNest(true);
        newMenu.getMenuElements().add(newIncludeMenu);
        page.getMenuDefinitions().add(newMenu);
        try
        {
            pageManager.updatePage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
        page = pageManager.getPage(this.testPage002);
        assertNotNull(page.getMenuDefinitions());
        assertEquals(1, page.getMenuDefinitions().size());
        assertNotNull(((MenuDefinition)page.getMenuDefinitions().get(0)).getMenuElements());
        assertEquals(6,((MenuDefinition)page.getMenuDefinitions().get(0)).getMenuElements().size());
        assertTrue(((MenuDefinition)page.getMenuDefinitions().get(0)).getMenuElements().get(0) instanceof MenuSeparatorDefinition);
        assertTrue(((MenuDefinition)page.getMenuDefinitions().get(0)).getMenuElements().get(1) instanceof MenuOptionsDefinition);
        assertTrue(((MenuDefinition)page.getMenuDefinitions().get(0)).getMenuElements().get(2) instanceof MenuOptionsDefinition);
        assertTrue(((MenuDefinition)page.getMenuDefinitions().get(0)).getMenuElements().get(3) instanceof MenuDefinition);
        assertTrue(((MenuDefinition)page.getMenuDefinitions().get(0)).getMenuElements().get(4) instanceof MenuExcludeDefinition);
        assertTrue(((MenuDefinition)page.getMenuDefinitions().get(0)).getMenuElements().get(5) instanceof MenuIncludeDefinition);

        // test writing folder menu definitions
        folder = pageManager.getFolder(this.testFolder2);
        folder.setMenuDefinitions(new ArrayList());
        newMenu = folder.newMenuDefinition();
        newMenu.setName("updated-menu");
        newMenu.setSkin("bread-crumbs");
        newMenu.setOptions("./");
        newMenu.setPaths(true);
        folder.getMenuDefinitions().add(newMenu);
        try
        {
            pageManager.updateFolder(folder);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in folder update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
        folder = pageManager.getFolder(this.testFolder2);
        assertNotNull(folder.getMenuDefinitions());
        assertEquals(1, folder.getMenuDefinitions().size());
        assertEquals("updated-menu", ((MenuDefinition)folder.getMenuDefinitions().get(0)).getName());
        assertEquals("bread-crumbs", ((MenuDefinition)folder.getMenuDefinitions().get(0)).getSkin());
        assertEquals("./", ((MenuDefinition)folder.getMenuDefinitions().get(0)).getOptions());
    }

    public void testRemovePage() throws Exception
    {
        Page page = pageManager.getPage(this.testPage002);

        try
        {
            pageManager.removePage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page remove: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        boolean exceptionFound = false;
        try
        {
            page = pageManager.getPage(this.testPage002);
        }
        catch (DocumentNotFoundException dnfe)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
    }

    public void testRemoveFolder() throws Exception
    {
        Folder folder = pageManager.getFolder(this.testFolder2);

        try
        {
            pageManager.removeFolder(folder);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in folder remove: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        boolean exceptionFound = false;
        try
        {
            folder = pageManager.getFolder(this.testFolder2);
        }
        catch (FolderNotFoundException fnfe)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
    }

    public void testRemoveLink() throws Exception
    {
        Link link = pageManager.getLink(this.testLink002);

        try
        {
            pageManager.removeLink(link);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in link remove: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        boolean exceptionFound = false;
        try
        {
            link = pageManager.getLink(this.testLink002);
        }
        catch (DocumentNotFoundException dnfe)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
    }
    
    public void testClonePage() throws Exception
    {
        Page testpage = pageManager.getPage("/clonetest.psml");
        assertNotNull(testpage);
        Page clone = pageManager.copyPage(testpage, "/cloned.psml");
        assertNotNull(clone);
        
        assertTrue(clone.getId().equals("/cloned.psml"));
        assertTrue(clone.getName().equals("cloned.psml"));
        assertTrue(clone.getTitle().equals("Test Page"));
        assertTrue(clone.getSkin().equals("test-skin"));
        assertTrue(clone.getEffectiveDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(clone.getDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(clone.getDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));

        // TODO: Test Meta data
        Fragment root = testpage.getRootFragment();
        Fragment cloneRoot = clone.getRootFragment();
        
        assertNotNull(cloneRoot);
        assertNotNull(cloneRoot.getId());        
        assertFalse(cloneRoot.getId().equals(root.getId()));
        assertTrue(cloneRoot.getName().equals("TwoColumns"));
        assertTrue(cloneRoot.getType().equals(Fragment.LAYOUT));
        assertNull(cloneRoot.getDecorator());

        List children = root.getFragments();
        List cloneChildren = cloneRoot.getFragments();
        assertNotNull(cloneChildren);
        assertTrue(cloneChildren.size() == 3);

        Fragment f = (Fragment) children.get(0);
        Fragment cf = (Fragment) cloneChildren.get(0);
        assertNotNull(cf.getId());
        assertFalse(cf.getId().equals(f.getId()));
        assertTrue(cf.getName().equals("HelloPortlet"));
        assertTrue(cf.getType().equals(Fragment.PORTLET));

        Map properties = f.getProperties();
        Map cloneProperties = cf.getProperties();

        assertNotNull(cloneProperties);
        assertTrue(cloneProperties.size() == 2);
        assertEquals("0", cf.getProperty(Fragment.ROW_PROPERTY_NAME));
        assertEquals(0, cf.getIntProperty(Fragment.COLUMN_PROPERTY_NAME));

        cf = (Fragment) cloneChildren.get(1);
        f = (Fragment) children.get(1);
        assertNotNull(cf.getId());
        assertFalse(cf.getId().equals(f.getId()));
        assertTrue(cf.getName().equals("JMXPortlet"));
        assertTrue(cf.getType().equals(Fragment.PORTLET));

        properties = cf.getProperties();
        assertNotNull(properties);
        assertTrue(properties.size() == 2);
        assertEquals("0", cf.getProperty(Fragment.ROW_PROPERTY_NAME));
        assertEquals(1, cf.getIntProperty(Fragment.COLUMN_PROPERTY_NAME));

        f = testpage.getFragmentById("f002");
        cf = (Fragment) cloneChildren.get(2);
        String id = cf.getId();
        cf = clone.getFragmentById(id);
        
        assertNotNull(cf);        
        assertNotNull(cf.getId());        
        assertFalse(cf.getId().equals(f.getId()));
        assertTrue(cf.getName().equals("Card"));
        assertTrue(cf.getType().equals(Fragment.LAYOUT));
        assertTrue(cf.getDecorator().equals("Tab"));
        assertNotNull(cf.getFragments());
        assertTrue(cf.getFragments().size() == 2);
        
        // security testing
        SecurityConstraints constraints = clone.getSecurityConstraints();
        assertNotNull(constraints); 
        assertTrue(constraints.getOwner().equals("new-user"));
        List secs = constraints.getSecurityConstraints();
        assertNotNull(secs);
        assertTrue(secs.size() == 1);
        SecurityConstraint constraint = (SecurityConstraint)secs.get(0);
        assertNotNull(constraint);
        assertTrue(constraint.getUsers() != null);
        assertTrue(constraint.getUsers().size() == 2);
        assertTrue(Shared.makeCSVFromList(constraint.getUsers()).equals("user10,user11"));
        assertTrue(constraint.getRoles() != null);
        assertTrue(constraint.getRoles().size() == 1);
        assertTrue(Shared.makeCSVFromList(constraint.getRoles()).equals("*"));
        assertTrue(constraint.getPermissions() != null);
        assertTrue(constraint.getPermissions().size() == 2);
        assertTrue(Shared.makeCSVFromList(constraint.getPermissions()).equals("edit,view"));
        List refs = constraints.getSecurityConstraintsRefs();
        assertNotNull(refs);
        assertTrue(refs.size() == 1);
        String ref = (String)refs.get(0);
        assertNotNull(ref);
        assertTrue(ref.equals("public-view"));
        
        // TODO: menu testing
    }
}
