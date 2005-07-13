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
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderMetaData;
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
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.page.document.CastorFileSystemDocumentHandler;
import org.apache.jetspeed.page.document.DocumentHandler;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.DocumentHandlerFactoryImpl;
import org.apache.jetspeed.page.document.FileSystemFolderHandler;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.impl.CastorXmlPageManager;
import org.apache.jetspeed.util.DirectoryHelper;

/**
 * TestPageXmlPersistence
 * 
 * @author <a href="raphael@apache.org">Rapha\u00ebl Luta </a>
 * @version $Id: TestCastorXmlPageManager.java,v 1.9 2004/08/24 21:33:05 weaver
 *          Exp $
 */
public class TestCastorXmlPageManager extends TestCase
{
    private String testId = "/test002.psml";
    protected CastorXmlPageManager pageManager;
    protected DirectoryHelper dirHelper;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        dirHelper = new DirectoryHelper(new File("target/testdata/pages"));
        FileFilter noCVSorSVNorBackups = new FileFilter() {

            public boolean accept( File pathname )
            {
                return !pathname.getName().equals("CVS") && !pathname.getName().equals(".svn") && !pathname.getName().endsWith("~");
            }
            
        };
        dirHelper.copyFrom(new File("testdata/pages"), noCVSorSVNorBackups);
        IdGenerator idGen = new JetspeedIdGenerator(65536,"P-","");
        FileCache cache = new FileCache(10, 12);
        
        DocumentHandler psmlHandler = new CastorFileSystemDocumentHandler("/META-INF/page-mapping.xml", Page.DOCUMENT_TYPE, Page.class, "target/testdata/pages", cache);
        DocumentHandler linkHandler = new CastorFileSystemDocumentHandler("/META-INF/page-mapping.xml", Link.DOCUMENT_TYPE, Link.class, "target/testdata/pages", cache);
        DocumentHandler folderMetaDataHandler = new CastorFileSystemDocumentHandler("/META-INF/page-mapping.xml", FolderMetaData.DOCUMENT_TYPE, FolderMetaData.class, "target/testdata/pages", cache);
        DocumentHandler pageSecurityHandler = new CastorFileSystemDocumentHandler("/META-INF/page-mapping.xml", PageSecurity.DOCUMENT_TYPE, PageSecurity.class, "target/testdata/pages", cache);
        
        DocumentHandlerFactory handlerFactory = new DocumentHandlerFactoryImpl();
        handlerFactory.registerDocumentHandler(psmlHandler);
        handlerFactory.registerDocumentHandler(linkHandler);
        handlerFactory.registerDocumentHandler(folderMetaDataHandler);        
        handlerFactory.registerDocumentHandler(pageSecurityHandler);        
        
        FolderHandler folderHandler = new FileSystemFolderHandler("target/testdata/pages", handlerFactory, cache);
        
        pageManager = new CastorXmlPageManager(idGen, handlerFactory, folderHandler, cache, false, false);
        
        
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
        Page testpage = pageManager.newPage();
        assertNotNull(testpage);
        assertNotNull(testpage.getId());
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

    public void testNewProperty()
    {
        // TODO: Fix Property manipulation API, too clumsy right now
    }

    public void testGetPage() throws Exception
    {
        Page testpage = pageManager.getPage("/test001.psml");
        assertNotNull(testpage);
        assertTrue(testpage.getId().equals("/test001.psml"));
        assertTrue(testpage.getTitle().equals("Test Page"));
        assertTrue(testpage.getDefaultSkin().equals("test-skin"));
        assertTrue(testpage.getDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(testpage.getDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));

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

        List properties = f.getProperties(root.getName());
        assertNotNull(properties);
        assertTrue(properties.size() == 2);
        assertTrue(((Property) properties.get(0)).getName().equals("row"));
        assertTrue(((Property) properties.get(0)).getValue().equals("0"));
        assertTrue(((Property) properties.get(1)).getName().equals("column"));
        assertTrue(((Property) properties.get(1)).getValue().equals("0"));

        f = (Fragment) children.get(1);
        assertTrue(f.getId().equals("pe002"));
        assertTrue(f.getName().equals("JMXPortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));

        properties = f.getProperties(root.getName());
        assertNotNull(properties);
        assertTrue(properties.size() == 2);
        assertTrue(((Property) properties.get(0)).getName().equals("row"));
        assertTrue(((Property) properties.get(0)).getValue().equals("0"));
        assertTrue(((Property) properties.get(1)).getName().equals("column"));
        assertTrue(((Property) properties.get(1)).getValue().equals("1"));

        f = testpage.getFragmentById("f002");
        assertNotNull(f);
        assertTrue(f.getId().equals("f002"));
        assertTrue(f.getName().equals("Card"));
        assertTrue(f.getType().equals(Fragment.LAYOUT));
        assertTrue(f.getDecorator().equals("Tab"));
        assertNotNull(f.getFragments());
        assertTrue(f.getFragments().size() == 2);
    }

    public void testRegisterPage() throws Exception
    {
        Page page = pageManager.newPage();
        System.out.println("Retrieved test_id in register " + this.testId);
        page.setId(this.testId);
        page.setDefaultSkin("myskin");
        page.setTitle("Registered Page");

        Fragment root = page.getRootFragment();
        root.setName("TestLayout");
        Fragment f = pageManager.newFragment();
        f.setType(Fragment.PORTLET);
        f.setName("TestPortlet");
        Property p = pageManager.newProperty();
        p.setLayout("TestLayout");
        p.setName("row");
        p.setValue("0");
        f.addProperty(p);
        p = pageManager.newProperty();
        p.setLayout("TestLayout");
        p.setName("column");
        p.setValue("0");
        f.addProperty(p);
        root.getFragments().add(f);

        try
        {
            pageManager.registerPage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page registration: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        page = pageManager.getPage(this.testId);
        assertNotNull(page);
        assertTrue(page.getId().equals(this.testId));
        assertTrue(page.getTitle().equals("Registered Page"));
        assertNotNull(page.getRootFragment());
        assertTrue(page.getRootFragment().getName().equals("TestLayout"));
        assertTrue(page.getRootFragment().getFragments().size() == 1);

        f = (Fragment) page.getRootFragment().getFragments().get(0);
        assertNotNull(f.getProperties("TestLayout"));
        assertTrue(((Property) f.getProperties("TestLayout").get(0)).getValue().equals("0"));
    }

    public void testUpdatePage() throws Exception
    {
        Page page = pageManager.getPage(this.testId);
        page.setTitle("Updated Title");

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

        page = pageManager.getPage(this.testId);
        assertTrue(page.getTitle().equals("Updated Title"));
    }

    public void testFolders() throws Exception
    {

        Folder folder1 = pageManager.getFolder("/folder1");
        assertNotNull(folder1);
                
        assertEquals(2, folder1.getFolders().size());
        Iterator childItr = folder1.getFolders().iterator();
        // Test that the folders are naturally orderd
        Folder folder2 = (Folder) childItr.next();
        assertEquals("default-page.psml", folder2.getDefaultPage(true));
        assertEquals("/folder1/folder2", folder2.getPath());
        assertEquals("folder2", folder2.getName());
        Folder folder3 = (Folder) childItr.next();
        assertEquals("/folder1/folder3", folder3.getPath());
        assertEquals("test001.psml", folder3.getDefaultPage(true));

        assertEquals(1, folder2.getPages().size());
        assertEquals(2, folder3.getPages().size());
        
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

        Folder folder = pageManager.getFolder("/");
        assertNotNull(folder);
        assertNotNull(folder.getLinks());
        assertEquals(folder.getLinks().size(), 1);
        assertEquals("http://portals.apache.org", ((Document) folder.getLinks().iterator().next()).getUrl());
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
        page = pageManager.getPage(this.testId);
        page.setMenuDefinitions(new ArrayList());
        MenuDefinition newMenu = pageManager.newMenuDefinition();
        newMenu.setName("updated-menu");
        newMenu.setSkin("tabs");
        newMenu.setMenuElements(new ArrayList());
        MenuSeparatorDefinition newSeparator = pageManager.newMenuSeparatorDefinition();
        newSeparator.setText("-- Updated Menu --");
        newMenu.getMenuElements().add(newSeparator);
        MenuOptionsDefinition newOptions0 = pageManager.newMenuOptionsDefinition();
        newOptions0.setOptions("/*.psml");
        newOptions0.setRegexp(true);
        newMenu.getMenuElements().add(newOptions0);
        MenuOptionsDefinition newOptions1 = pageManager.newMenuOptionsDefinition();
        newOptions1.setOptions("/folder0");
        newMenu.getMenuElements().add(newOptions1);
        MenuDefinition newNestedMenu = pageManager.newMenuDefinition();
        newNestedMenu.setOptions("/*/");
        newNestedMenu.setRegexp(true);
        newMenu.getMenuElements().add(newNestedMenu);
        MenuExcludeDefinition newExcludeMenu = pageManager.newMenuExcludeDefinition();
        newExcludeMenu.setName("exclude-menu");
        newMenu.getMenuElements().add(newExcludeMenu);
        MenuIncludeDefinition newIncludeMenu = pageManager.newMenuIncludeDefinition();
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
        page = pageManager.getPage(this.testId);
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
    }

    public void testRemovePage() throws Exception
    {
        Page page = pageManager.getPage(this.testId);

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
            page = pageManager.getPage(this.testId);
        }
        catch (PageNotFoundException pnfe)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
    }
}
