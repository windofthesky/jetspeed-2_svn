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
package org.apache.jetspeed.portalsite;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.test.AbstractSpringTestCase;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.BaseConcretePageElement;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.page.document.proxy.NodeProxy;
import org.apache.jetspeed.portalsite.impl.MenuImpl;
import org.apache.jetspeed.portalsite.view.PhysicalSiteView;
import org.apache.jetspeed.portalsite.view.SearchPathsSiteView;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.impl.JetspeedProfileLocator;

/**
 * TestPortalSite
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class TestPortalSite extends AbstractSpringTestCase
{
    /**
     * pageManager - PageManager component
     */
    private PageManager pageManager;

    /**
     * portalSite - PortalSite component
     */
    private PortalSite portalSite;
    
    /**
     * default locale
     */
    private Locale defaultLocale;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        this.pageManager = (PageManager) scm.getComponent("pageManager");
        this.portalSite = (PortalSite) scm.getComponent("portalSite");
        this.defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        Locale.setDefault(this.defaultLocale);
        super.tearDown();
    }

    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] {TestPortalSite.class.getName()});
    }

    /**
     * Define test suite.
     *
     * @return the test suite
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPortalSite.class);
    }

    /**
     * Define configuration paths.
     *
     * @return array of paths.
     */
    protected String[] getConfigurations()
    {
        return new String[] {"/JETSPEED-INF/spring/test-spring.xml", "cache-test.xml"};
    }
    
    protected String getBeanDefinitionFilterCategories()
    {
        return "default";
    }

    /**
     * testPageManagerSetup - Test PageManager test configuration
     *
     * @throws Exception
     */
    public void testPageManagerSetup() throws Exception
    {
        assertNotNull(pageManager);
        Folder rootFolder = pageManager.getFolder("/");
        assertNotNull(rootFolder);
        Page rootPage0 = pageManager.getPage("/page0.psml");
        assertNotNull(rootPage0);
        assertEquals(rootFolder.getPage("page0.psml"), rootPage0);        
        Link rootLink0 = pageManager.getLink("/link0.link");        
        assertNotNull(rootLink0);
        assertEquals(rootFolder.getLink("link0.link"), rootLink0);        
        DynamicPage docPage = pageManager.getDynamicPage("/docpage.dpsml");        
        assertNotNull(docPage);
        assertEquals(rootFolder.getDynamicPage("docpage.dpsml"), docPage);        
    }

    /**
     * testSiteView - Test SiteView operation
     *
     * @throws Exception
     */
    public void testSiteView() throws Exception
    {
        // test degenerate SiteView
        SearchPathsSiteView baseView = new SearchPathsSiteView(pageManager);
        assertEquals("/", baseView.getSearchPathsString());
        Folder rootFolderView = baseView.getRootFolderView();
        assertNotNull(rootFolderView);
        assertEquals("/", rootFolderView.getName());
        assertEquals("root", rootFolderView.getTitle());
        assertEquals("/", extractFileSystemPathFromId(rootFolderView.getId()));
        assertEquals(5, rootFolderView.getFolders().size());
        Iterator foldersIter = rootFolderView.getFolders().iterator();
        assertEquals("contentfolder", ((Folder)foldersIter.next()).getName());
        assertEquals("folder0", ((Folder)foldersIter.next()).getName());
        assertEquals("folder1", ((Folder)foldersIter.next()).getName());
        assertEquals("folder2", ((Folder)foldersIter.next()).getName());
        assertEquals("folder3", ((Folder)foldersIter.next()).getName());
        assertEquals(4, rootFolderView.getPages().size());
        Iterator pagesIter = rootFolderView.getPages().iterator();
        assertEquals("page2.psml", ((Page)pagesIter.next()).getName());
        assertEquals("page1.psml", ((Page)pagesIter.next()).getName());
        assertEquals("page0.psml", ((Page)pagesIter.next()).getName());
        assertEquals("hidden.psml", ((Page)pagesIter.next()).getName());
        assertEquals(2, rootFolderView.getLinks().size());
        Iterator linksIter = rootFolderView.getLinks().iterator();
        assertEquals("link1.link", ((Link)linksIter.next()).getName());
        assertEquals("link0.link", ((Link)linksIter.next()).getName());
        assertEquals(2, rootFolderView.getDynamicPages().size());
        Iterator dynamicPagesIter = rootFolderView.getDynamicPages().iterator();
        assertEquals("contentpage.dpsml", ((DynamicPage)dynamicPagesIter.next()).getName());
        assertEquals("docpage.dpsml", ((DynamicPage)dynamicPagesIter.next()).getName());
        Page rootPage0View = rootFolderView.getPage("page0.psml");
        assertNotNull(rootPage0View);
        assertEquals(rootFolderView, rootPage0View.getParent());
        assertEquals("page0.psml", rootPage0View.getName());
        assertEquals("/page0.psml", extractFileSystemPathFromId(rootPage0View.getId()));
        Page rootHiddenView = rootFolderView.getPage("hidden.psml");
        assertNotNull(rootHiddenView);
        assertEquals("hidden.psml", rootHiddenView.getName());
        assertTrue(rootHiddenView.isHidden());
        Link rootLink0View = rootFolderView.getLink("link0.link");
        assertNotNull(rootLink0View);
        assertEquals(rootFolderView, rootLink0View.getParent());
        assertEquals("link0.link", rootLink0View.getName());
        assertEquals("/link0.link", extractFileSystemPathFromId(rootLink0View.getId()));
        DynamicPage docPageView = rootFolderView.getDynamicPage("docpage.dpsml");        
        assertNotNull(docPageView);
        assertEquals(rootFolderView, docPageView.getParent());
        assertEquals("docpage.dpsml", docPageView.getName());
        assertEquals("/docpage.dpsml", extractFileSystemPathFromId(docPageView.getId()));
        Folder rootFolder0View = rootFolderView.getFolder("folder0");
        assertNotNull(rootFolder0View);
        assertEquals(rootFolderView, rootFolder0View.getParent());
        assertEquals(1, rootFolder0View.getPages().size());
        assertNull(rootFolder0View.getLinks());
        assertEquals("folder0", rootFolder0View.getName());
        assertEquals("/folder0", extractFileSystemPathFromId(rootFolder0View.getId()));
        Page folder0Page0View = rootFolder0View.getPage("page0.psml");
        assertNotNull(folder0Page0View);
        assertEquals(rootFolder0View, folder0Page0View.getParent());
        assertEquals("page0.psml", folder0Page0View.getName());
        assertEquals("/folder0/page0.psml", extractFileSystemPathFromId(folder0Page0View.getId()));

        // test SiteView access by path
        Folder rootFolderViewByPath = (Folder)baseView.getNodeView("/", null, false, false);
        assertNotNull(rootFolderViewByPath);
        assertEquals(rootFolderView, rootFolderViewByPath);
        Folder rootFolder0ViewByPath = (Folder)baseView.getNodeView("/folder0/", null, false, false);
        assertNotNull(rootFolder0ViewByPath);
        assertEquals(rootFolder0View, rootFolder0ViewByPath);
        Page folder0Page0ViewByPath = (Page)baseView.getNodeView("/folder0/page0.psml", null, false, false);
        assertNotNull(folder0Page0ViewByPath);
        assertEquals(folder0Page0View, folder0Page0ViewByPath);
        folder0Page0ViewByPath = (Page)baseView.getNodeView("page0.psml", rootFolder0View, false, false);
        assertNotNull(folder0Page0ViewByPath);
        assertEquals(folder0Page0View, folder0Page0ViewByPath);
        try
        {
            baseView.getNodeView("/folderX/page0.psml", null, false, false);
            fail("/folderX/page0.psml should not be found");
        }
        catch (NodeNotFoundException nnfe)
        {
        }
        try
        {
            baseView.getNodeView("/folder0/pageX.psml", null, false, false);
            fail("/folder0/pageX.psml should not be found");
        }
        catch (NodeNotFoundException nnfe)
        {
        }
        List rootPageViewsByPath = baseView.getNodeViews("/page?.psml", null, false, false);
        assertNotNull(rootPageViewsByPath);
        assertEquals(3,rootPageViewsByPath.size());
        assertTrue(rootPageViewsByPath.contains(rootPage0View));
        List rootFolderViewsByPath = baseView.getNodeViews("/*/", null, false, false);
        assertNotNull(rootFolderViewsByPath);
        assertEquals(5,rootFolderViewsByPath.size());
        assertTrue(rootFolderViewsByPath.contains(rootFolder0View));
        List folderPageViewsByPath = baseView.getNodeViews("*/p*[0-9].psml", rootFolderView, false, false);
        assertNotNull(folderPageViewsByPath);
        assertEquals(2,folderPageViewsByPath.size());
        assertTrue(folderPageViewsByPath.contains(folder0Page0View));

        // test aggregating SiteView
        SearchPathsSiteView aggregateView = new SearchPathsSiteView(pageManager, "/_user/user,/_role/role0,/_group/group,/", false);
        assertEquals("/_user/user,/_role/role0,/_group/group,/", aggregateView.getSearchPathsString());
        rootFolderView = aggregateView.getRootFolderView();
        assertNotNull(rootFolderView);
        assertEquals("/", rootFolderView.getName());
        assertEquals("user root", rootFolderView.getTitle());
        assertEquals("/_user/user", extractFileSystemPathFromId(rootFolderView.getId()));
        assertEquals(5, rootFolderView.getFolders().size());
        assertEquals(4, rootFolderView.getPages().size());
        assertEquals(2, rootFolderView.getLinks().size());
        assertEquals(2, rootFolderView.getDynamicPages().size());
        rootPage0View = rootFolderView.getPage("page0.psml");
        assertNotNull(rootPage0View);
        assertEquals(rootFolderView, rootPage0View.getParent());
        assertEquals("page0.psml", rootPage0View.getName());
        assertEquals("/page0.psml", extractFileSystemPathFromId(rootPage0View.getId()));
        List rootPage0ViewMenus = rootPage0View.getMenuDefinitions();
        assertNotNull(rootPage0ViewMenus);
        assertEquals(5 + aggregateView.getStandardMenuNames().size(), rootPage0ViewMenus.size());
        Iterator menusIter = rootPage0ViewMenus.iterator();
        MenuDefinition rootPage0ViewTemplateTestMenu = (MenuDefinition)menusIter.next();
        assertEquals("template-test", rootPage0ViewTemplateTestMenu.getName());
        assertEquals("/page2.psml", rootPage0ViewTemplateTestMenu.getOptions());
        MenuDefinition rootPage0ViewTopMenu = (MenuDefinition)menusIter.next();
        assertEquals("top", rootPage0ViewTopMenu.getName());
        assertEquals("/", rootPage0ViewTopMenu.getOptions());
        assertEquals(2, rootPage0ViewTopMenu.getDepth());
        assertEquals("dhtml-pull-down", rootPage0ViewTopMenu.getSkin());
        MenuDefinition rootPage0ViewBreadCrumbMenu = (MenuDefinition)menusIter.next();
        assertEquals("bread-crumbs", rootPage0ViewBreadCrumbMenu.getName());
        assertEquals("./", rootPage0ViewBreadCrumbMenu.getOptions());
        assertEquals(true, rootPage0ViewBreadCrumbMenu.isPaths());
        MenuDefinition rootPage0ViewCurrentPageTestMenu = (MenuDefinition)menusIter.next();
        assertEquals("current-page-test", rootPage0ViewCurrentPageTestMenu.getName());
        MenuDefinition rootPage0ViewCurrentPathTestMenu = (MenuDefinition)menusIter.next();
        assertEquals("current-path-test", rootPage0ViewCurrentPathTestMenu.getName());        
        for (int i = 0; (i < aggregateView.getStandardMenuNames().size()); i++)
        {
            assertTrue(aggregateView.getStandardMenuNames().contains(((MenuDefinition)menusIter.next()).getName()));
        }
        Page rootPage2View = rootFolderView.getPage("page2.psml");
        assertNotNull(rootPage2View);
        assertEquals(rootFolderView, rootPage2View.getParent());
        assertEquals("page2.psml", rootPage2View.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(rootPage2View.getId()));
        List rootPage2ViewMenus = rootPage2View.getMenuDefinitions();
        assertNotNull(rootPage2ViewMenus);
        assertEquals(5 + aggregateView.getStandardMenuNames().size(), rootPage2ViewMenus.size());
        menusIter = rootPage2ViewMenus.iterator();
        MenuDefinition rootPage2ViewTopMenu = (MenuDefinition)menusIter.next();
        assertEquals("top", rootPage2ViewTopMenu.getName());
        assertEquals("/", rootPage2ViewTopMenu.getOptions());
        assertEquals(1, rootPage2ViewTopMenu.getDepth());
        MenuDefinition rootPage2ViewBreadCrumbMenu = (MenuDefinition)menusIter.next();
        assertEquals("bread-crumbs", rootPage2ViewBreadCrumbMenu.getName());
        MenuDefinition rootPage2ViewTemplateTestMenu = (MenuDefinition)menusIter.next();
        assertEquals("template-test", rootPage2ViewTemplateTestMenu.getName());
        assertEquals("/page0.psml", rootPage2ViewTemplateTestMenu.getOptions());
        MenuDefinition rootPage2ViewCurrentPageTestMenu = (MenuDefinition)menusIter.next();
        assertEquals("current-page-test", rootPage0ViewCurrentPageTestMenu.getName());
        MenuDefinition rootPage2ViewCurrentPathTestMenu = (MenuDefinition)menusIter.next();
        assertEquals("current-path-test", rootPage0ViewCurrentPathTestMenu.getName());        
        for (int i = 0; (i < aggregateView.getStandardMenuNames().size()); i++)
        {
            assertTrue(aggregateView.getStandardMenuNames().contains(((MenuDefinition)menusIter.next()).getName()));
        }
        rootLink0View = rootFolderView.getLink("link0.link");
        assertNotNull(rootLink0View);
        assertEquals(rootFolderView, rootLink0View.getParent());
        assertEquals("link0.link", rootLink0View.getName());
        assertEquals("/_group/group/link0.link", extractFileSystemPathFromId(rootLink0View.getId()));
        rootFolder0View = rootFolderView.getFolder("folder0");
        assertNotNull(rootFolder0View);
        assertEquals(rootFolderView, rootFolder0View.getParent());
        assertEquals(1, rootFolder0View.getPages().size());
        assertNull(rootFolder0View.getLinks());
        assertNull(rootFolder0View.getFolders());
        assertEquals("folder0", rootFolder0View.getName());
        assertEquals("folder0", rootFolder0View.getTitle());
        assertEquals("/folder0", extractFileSystemPathFromId(rootFolder0View.getId()));
        folder0Page0View = rootFolder0View.getPage("page0.psml");
        assertNotNull(folder0Page0View);
        assertEquals(rootFolder0View, folder0Page0View.getParent());
        assertEquals("page0.psml", folder0Page0View.getName());
        assertEquals("/folder0/page0.psml", extractFileSystemPathFromId(folder0Page0View.getId()));
        Folder rootFolder1View = rootFolderView.getFolder("folder1");
        assertNotNull(rootFolder1View);
        assertEquals(rootFolderView, rootFolder1View.getParent());
        assertEquals(2, rootFolder1View.getPages().size());
        assertNull(rootFolder1View.getLinks());
        assertNull(rootFolder1View.getFolders());
        assertEquals("folder1", rootFolder1View.getName());
        assertEquals("group folder1", rootFolder1View.getTitle());
        assertEquals("/_user/user/folder1", extractFileSystemPathFromId(rootFolder1View.getId()));

        // test degenerate aggregating SiteView
        aggregateView = new SearchPathsSiteView(pageManager, "/__subsite-root", false);
        assertEquals("/__subsite-root", aggregateView.getSearchPathsString());
        rootFolderView = aggregateView.getRootFolderView();
        assertNotNull(rootFolderView);
        assertEquals("/", rootFolderView.getName());
        assertEquals("subsite root", rootFolderView.getTitle());
        assertEquals("/__subsite-root", extractFileSystemPathFromId(rootFolderView.getId()));
        assertNull(rootFolderView.getFolders());
        assertEquals(1, rootFolderView.getPages().size());
        assertEquals(1, rootFolderView.getLinks().size());

        // test SiteView construction using profile locators
        JetspeedProfileLocator locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        SearchPathsSiteView profileView = new SearchPathsSiteView(pageManager, locator, false);
        assertEquals("/", profileView.getSearchPathsString());
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("user", true, false, "user");
        locator.add("page", false, false, "default-page");
        profileView = new SearchPathsSiteView(pageManager, locator, false);
        assertEquals("/_user/user,/", profileView.getSearchPathsString());
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "default-page");
        locator.add("user", true, false, "user");
        locator.add("mediatype", true, false, "html");
        locator.add("language", true, false, "en");
        locator.add("country", true, false, "US");
        profileView = new SearchPathsSiteView(pageManager, locator, false);
        assertEquals("/_user/user/_mediatype/html,/_user/user,/", profileView.getSearchPathsString());
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "default-page");
        locator.add("role", true, false, "role0");
        locator.add("role", true, false, "role1");
        profileView = new SearchPathsSiteView(pageManager, locator, false);
        assertEquals("/_role/role0,/_role/role1,/", profileView.getSearchPathsString());
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("user", true, false, "user");
        locator.add("navigation", false, true, "/");
        locator.add("role", true, false, "role0");
        locator.add("navigation", false, true, "/");
        locator.add("group", true, false, "group");
        locator.add("page", false, false, "default-page");
        profileView = new SearchPathsSiteView(pageManager, locator, false);
        assertEquals("/_user/user,/_role/role0,/_group/group,/", profileView.getSearchPathsString());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("hostname", true, false, "dash");
        locator.add("user", true, false, "joe");
        locator.add("page", false, false, "home");
        profileView = new SearchPathsSiteView(pageManager, locator, false);
        assertEquals("/_hostname/dash/_user/joe,/_hostname/dash,/", profileView.getSearchPathsString());
        
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("navigation", false, true, "subsite-root");
        profileView = new SearchPathsSiteView(pageManager, locator, false);
        assertEquals("/__subsite-root", profileView.getSearchPathsString());
        Map locators = new HashMap();
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("role", true, false, "role0");
        locator.add("role", true, false, "role1");
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("user", true, false, "user");
        locator.add("navigation", false, true, "/");
        locator.add("group", true, false, "group");
        locators.put("alternate-locator-name", locator);
        profileView = new SearchPathsSiteView(pageManager, locators, false);
        assertEquals("/_role/role0,/_role/role1,/_user/user,/_group/group,/", profileView.getSearchPathsString());
        rootFolderView = profileView.getRootFolderView();
        assertNotNull(rootFolderView);
        assertEquals("/", rootFolderView.getName());
        assertEquals("user root", rootFolderView.getTitle());
        assertEquals("/_role/role0", extractFileSystemPathFromId(rootFolderView.getId()));
        assertEquals(ProfileLocator.PAGE_LOCATOR, extractLocatorNameFromView(rootFolderView));
        rootPage2View = rootFolderView.getPage("page2.psml");
        assertNotNull(rootPage2View);
        assertEquals("page2.psml", rootPage2View.getName());
        assertEquals("/_role/role0/page2.psml", extractFileSystemPathFromId(rootPage2View.getId()));
        assertEquals(ProfileLocator.PAGE_LOCATOR, extractLocatorNameFromView(rootPage2View));
        rootFolder1View = rootFolderView.getFolder("folder1");
        assertNotNull(rootFolder1View);
        assertEquals("folder1", rootFolder1View.getName());
        assertEquals("group folder1", rootFolder1View.getTitle());
        assertEquals("/_user/user/folder1", extractFileSystemPathFromId(rootFolder1View.getId()));
        assertEquals("alternate-locator-name", extractLocatorNameFromView(rootFolder1View));
        Page folder1Page1View = rootFolder1View.getPage("page1.psml");
        assertNotNull(folder1Page1View);
        assertEquals("page1.psml", folder1Page1View.getName());
        assertEquals("/_group/group/folder1/page1.psml", extractFileSystemPathFromId(folder1Page1View.getId()));
        assertEquals("alternate-locator-name", extractLocatorNameFromView(folder1Page1View));
        
        // test physical SiteView
        PhysicalSiteView basePhysicalView = new PhysicalSiteView(pageManager);
        rootFolderView = basePhysicalView.getRootFolderView();
        assertNotNull(rootFolderView);
        assertEquals("/", rootFolderView.getName());
        assertEquals("root", rootFolderView.getTitle());
        assertEquals("/", extractFileSystemPathFromId(rootFolderView.getId()));
        assertEquals(10, rootFolderView.getFolders().size());
        foldersIter = rootFolderView.getFolders().iterator();
        assertEquals("__subsite-root", ((Folder)foldersIter.next()).getName());
        assertEquals("_group", ((Folder)foldersIter.next()).getName());
        assertEquals("_hostname", ((Folder)foldersIter.next()).getName());
        assertEquals("_role", ((Folder)foldersIter.next()).getName());
        assertEquals("_user", ((Folder)foldersIter.next()).getName());
        assertEquals("contentfolder", ((Folder)foldersIter.next()).getName());
        assertEquals("folder0", ((Folder)foldersIter.next()).getName());
        assertEquals("folder1", ((Folder)foldersIter.next()).getName());
        assertEquals("folder2", ((Folder)foldersIter.next()).getName());
        assertEquals("folder3", ((Folder)foldersIter.next()).getName());
        assertEquals(4, rootFolderView.getPages().size());
        pagesIter = rootFolderView.getPages().iterator();
        assertEquals("page2.psml", ((Page)pagesIter.next()).getName());
        assertEquals("page1.psml", ((Page)pagesIter.next()).getName());
        assertEquals("page0.psml", ((Page)pagesIter.next()).getName());
        assertEquals("hidden.psml", ((Page)pagesIter.next()).getName());
        assertEquals(2, rootFolderView.getLinks().size());
        linksIter = rootFolderView.getLinks().iterator();
        assertEquals("link1.link", ((Link)linksIter.next()).getName());
        assertEquals("link0.link", ((Link)linksIter.next()).getName());
        assertEquals(2, rootFolderView.getDynamicPages().size());
        dynamicPagesIter = rootFolderView.getDynamicPages().iterator();
        assertEquals("contentpage.dpsml", ((DynamicPage)dynamicPagesIter.next()).getName());
        assertEquals("docpage.dpsml", ((DynamicPage)dynamicPagesIter.next()).getName());
        rootPage0View = rootFolderView.getPage("page0.psml");
        assertNotNull(rootPage0View);
        assertEquals(rootFolderView, rootPage0View.getParent());
        assertEquals("page0.psml", rootPage0View.getName());
        assertEquals("/page0.psml", extractFileSystemPathFromId(rootPage0View.getId()));
        rootHiddenView = rootFolderView.getPage("hidden.psml");
        assertNotNull(rootHiddenView);
        assertEquals("hidden.psml", rootHiddenView.getName());
        assertTrue(rootHiddenView.isHidden());
        rootLink0View = rootFolderView.getLink("link0.link");
        assertNotNull(rootLink0View);
        assertEquals(rootFolderView, rootLink0View.getParent());
        assertEquals("link0.link", rootLink0View.getName());
        assertEquals("/link0.link", extractFileSystemPathFromId(rootLink0View.getId()));
        docPageView = rootFolderView.getDynamicPage("docpage.dpsml");        
        assertNotNull(docPageView);
        assertEquals(rootFolderView, docPageView.getParent());
        assertEquals("docpage.dpsml", docPageView.getName());
        assertEquals("/docpage.dpsml", extractFileSystemPathFromId(docPageView.getId()));
        rootFolder0View = rootFolderView.getFolder("folder0");
        assertNotNull(rootFolder0View);
        assertEquals(rootFolderView, rootFolder0View.getParent());
        assertEquals(1, rootFolder0View.getPages().size());
        assertEquals(0, rootFolder0View.getLinks().size());
        assertEquals("folder0", rootFolder0View.getName());
        assertEquals("/folder0", extractFileSystemPathFromId(rootFolder0View.getId()));
        folder0Page0View = rootFolder0View.getPage("page0.psml");
        assertNotNull(folder0Page0View);
        assertEquals(rootFolder0View, folder0Page0View.getParent());
        assertEquals("page0.psml", folder0Page0View.getName());
        assertEquals("/folder0/page0.psml", extractFileSystemPathFromId(folder0Page0View.getId()));
        Folder rootUserView = rootFolderView.getFolder("_user");
        assertNotNull(rootUserView);
        assertEquals(rootFolderView, rootUserView.getParent());
        Folder rootUserUserView = rootUserView.getFolder("user");
        assertNotNull(rootUserUserView);
        assertEquals(rootUserView, rootUserUserView.getParent());
        assertEquals(2, rootUserUserView.getFolders().size());
        assertNotNull(rootUserUserView.getFolder("folder1"));
        assertNotNull(rootUserUserView.getFolder("_mediatype"));
        assertEquals(1, rootUserUserView.getPages().size());
        assertNotNull(rootUserUserView.getPage("page2.psml"));
        assertEquals(0, rootUserUserView.getLinks().size());
        assertEquals(1, rootUserUserView.getFragmentDefinitions().size());
        assertNotNull(rootUserUserView.getFragmentDefinition("definition1.fpsml"));
    }

    /**
     * testRelativeNavigations - Test SiteView search patch for navigation paths
     *
     * @throws Exception
     */
    public void testRelativeNavigations() throws Exception
    {
        JetspeedProfileLocator locator = new JetspeedProfileLocator();

        locator.init(null, "/");
        locator.add("hostname", true, false, "dash");
        locator.add("user", true, false, "joe");
        locator.add("page", false, false, "home");
        SearchPathsSiteView profileView = new SearchPathsSiteView(pageManager, locator, false);
        assertEquals("/_hostname/dash/_user/joe,/_hostname/dash,/", profileView.getSearchPathsString());
        
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("hostname", true, false, "dash");
        locator.add("user", true, false, "joe");
        locator.add("navigation", false, true, "/");
        locator.add("hostname", true, false, "dash");
        locator.add("role", true, false, "user");
        locator.add("page", false, false, "home");
        profileView = new SearchPathsSiteView(pageManager, locator, false);
        assertEquals("/_hostname/dash/_user/joe,/_hostname/dash/_role/user,/_hostname/dash,/", profileView.getSearchPathsString());
  
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");        
        locator.add("navigation", false, true, "subsite-root");
        locator.add("hostname", true, false, "localhost");
        locator.add("user", true, false, "sublocal");
        locator.add("navigation", false, true, "subsite-root");
        locator.add("hostname", true, false, "localhost");
        locator.add("role", true, false, "somerole");
        locator.add("path", false, false, "home");
        profileView = new SearchPathsSiteView(pageManager, locator, false);
        assertEquals("/__subsite-root/_hostname/localhost/_user/sublocal,/__subsite-root/_hostname/localhost/_role/somerole,/__subsite-root/_hostname/localhost,/__subsite-root", profileView.getSearchPathsString());                       
    }
    
    /**
     * testPortalSiteSetup - Test PortalSite test configuration
     *
     * @throws Exception
     */
    public void testPortalSiteSetup() throws Exception
    {
        assertNotNull(portalSite);
        
        // search path site setup
        PortalSiteSessionContext sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        JetspeedProfileLocator locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("user", true, false, "user");
        locator.add("navigation", false, true, "/");
        locator.add("role", true, false, "role0");
        locator.add("role", true, false, "role1");
        locator.add("navigation", false, true, "/");
        locator.add("group", true, false, "group");
        Map locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        BaseConcretePageElement requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page2.psml", requestPageView.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageView.getId()));
        PageTemplate requestPageTemplateView = requestContext.getPageTemplate();
        assertNotNull(requestPageTemplateView);
        assertEquals("template.tpsml", requestPageTemplateView.getName());
        assertEquals("/template.tpsml", extractFileSystemPathFromId(requestPageTemplateView.getId()));
        Map requestFragmentDefinitionViews = requestContext.getFragmentDefinitions();
        assertNotNull(requestFragmentDefinitionViews);
        assertEquals(2, requestFragmentDefinitionViews.size());
        FragmentDefinition requestFragmentDefinitionView0 = (FragmentDefinition)requestFragmentDefinitionViews.get("fake-fragment-definition-0");
        assertNotNull(requestFragmentDefinitionView0);
        assertEquals("definition0.fpsml", requestFragmentDefinitionView0.getName());
        assertEquals("/definition0.fpsml", extractFileSystemPathFromId(requestFragmentDefinitionView0.getId()));
        FragmentDefinition requestFragmentDefinitionView1 = (FragmentDefinition)requestFragmentDefinitionViews.get("fake-fragment-definition-1");
        assertNotNull(requestFragmentDefinitionView1);
        assertEquals("definition1.fpsml", requestFragmentDefinitionView1.getName());
        assertEquals("/_user/user/definition1.fpsml", extractFileSystemPathFromId(requestFragmentDefinitionView1.getId()));        
        Folder requestFolderView = requestContext.getFolder();
        assertNotNull(requestFolderView);
        assertEquals("/", requestFolderView.getName());
        assertEquals("/_user/user", extractFileSystemPathFromId(requestFolderView.getId()));
        NodeSet requestSiblingPageViews = requestContext.getSiblingPages();
        assertNotNull(requestSiblingPageViews);
        assertEquals(3, requestSiblingPageViews.size());
        assertNotNull(requestSiblingPageViews.get("page0.psml"));
        assertEquals("/page0.psml", extractFileSystemPathFromId(requestSiblingPageViews.get("page0.psml").getId()));
        assertNotNull(requestSiblingPageViews.get("page1.psml"));
        assertEquals("/page1.psml", extractFileSystemPathFromId(requestSiblingPageViews.get("page1.psml").getId()));
        assertNotNull(requestSiblingPageViews.get("page2.psml"));
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestSiblingPageViews.get("page2.psml").getId()));
        Folder requestParentFolderView = requestContext.getParentFolder();
        assertNull(requestParentFolderView);
        NodeSet requestSiblingFolderViews = requestContext.getSiblingFolders();
        assertNotNull(requestSiblingFolderViews);
        assertEquals(3, requestSiblingFolderViews.size());
        assertNotNull(requestSiblingFolderViews.get("folder0"));
        assertEquals("/folder0", extractFileSystemPathFromId(requestSiblingFolderViews.get("folder0").getId()));
        assertNotNull(requestSiblingFolderViews.get("folder1"));
        assertEquals("/_user/user/folder1", extractFileSystemPathFromId(requestSiblingFolderViews.get("folder1").getId()));
        assertNotNull(requestSiblingFolderViews.get("folder2"));
        assertEquals("/folder2", extractFileSystemPathFromId(requestSiblingFolderViews.get("folder2").getId()));
        Folder requestRootFolderView = requestContext.getRootFolder();
        assertNotNull(requestRootFolderView);
        assertEquals("/", requestRootFolderView.getName());
        assertEquals("/_user/user", extractFileSystemPathFromId(requestRootFolderView.getId()));
        NodeSet requestRootLinkViews = requestContext.getRootLinks();
        assertNotNull(requestRootLinkViews);
        assertEquals(2, requestRootLinkViews.size());
        assertNotNull(requestRootLinkViews.get("link0.link"));
        assertEquals("/_group/group/link0.link", extractFileSystemPathFromId(requestRootLinkViews.get("link0.link").getId()));
        assertNotNull(requestRootLinkViews.get("link1.link"));
        assertEquals("/link1.link", extractFileSystemPathFromId(requestRootLinkViews.get("link1.link").getId()));

        // physical site setup
        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        requestContext = sessionContext.newRequestContext("/", null);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page0.psml", requestPageView.getName());
        assertEquals("/page0.psml", extractFileSystemPathFromId(requestPageView.getId()));
        requestPageTemplateView = requestContext.getPageTemplate();
        assertNotNull(requestPageTemplateView);
        assertEquals("template.tpsml", requestPageTemplateView.getName());
        assertEquals("/template.tpsml", extractFileSystemPathFromId(requestPageTemplateView.getId()));
        requestFragmentDefinitionViews = requestContext.getFragmentDefinitions();
        assertNotNull(requestFragmentDefinitionViews);
        assertEquals(1, requestFragmentDefinitionViews.size());
        requestFragmentDefinitionView0 = (FragmentDefinition)requestFragmentDefinitionViews.get("fake-fragment-definition-0");
        assertNotNull(requestFragmentDefinitionView0);
        assertEquals("definition0.fpsml", requestFragmentDefinitionView0.getName());
        assertEquals("/definition0.fpsml", extractFileSystemPathFromId(requestFragmentDefinitionView0.getId()));
        requestFolderView = requestContext.getFolder();
        assertNotNull(requestFolderView);
        assertEquals("/", requestFolderView.getName());
        assertEquals("/", extractFileSystemPathFromId(requestFolderView.getId()));
        requestSiblingPageViews = requestContext.getSiblingPages();
        assertNotNull(requestSiblingPageViews);
        assertEquals(3, requestSiblingPageViews.size());
        assertNotNull(requestSiblingPageViews.get("page0.psml"));
        assertEquals("/page0.psml", extractFileSystemPathFromId(requestSiblingPageViews.get("page0.psml").getId()));
        assertNotNull(requestSiblingPageViews.get("page1.psml"));
        assertEquals("/page1.psml", extractFileSystemPathFromId(requestSiblingPageViews.get("page1.psml").getId()));
        assertNotNull(requestSiblingPageViews.get("page2.psml"));
        assertEquals("/page2.psml", extractFileSystemPathFromId(requestSiblingPageViews.get("page2.psml").getId()));
        requestParentFolderView = requestContext.getParentFolder();
        assertNull(requestParentFolderView);
        requestSiblingFolderViews = requestContext.getSiblingFolders();
        assertNotNull(requestSiblingFolderViews);
        assertEquals(8, requestSiblingFolderViews.size());
        assertNotNull(requestSiblingFolderViews.get("__subsite-root"));
        assertEquals("/__subsite-root", extractFileSystemPathFromId(requestSiblingFolderViews.get("__subsite-root").getId()));
        assertNotNull(requestSiblingFolderViews.get("_group"));
        assertEquals("/_group", extractFileSystemPathFromId(requestSiblingFolderViews.get("_group").getId()));
        assertNotNull(requestSiblingFolderViews.get("_hostname"));
        assertEquals("/_hostname", extractFileSystemPathFromId(requestSiblingFolderViews.get("_hostname").getId()));
        assertNotNull(requestSiblingFolderViews.get("_role"));
        assertEquals("/_role", extractFileSystemPathFromId(requestSiblingFolderViews.get("_role").getId()));
        assertNotNull(requestSiblingFolderViews.get("_user"));
        assertEquals("/_user", extractFileSystemPathFromId(requestSiblingFolderViews.get("_user").getId()));
        assertNotNull(requestSiblingFolderViews.get("folder0"));
        assertEquals("/folder0", extractFileSystemPathFromId(requestSiblingFolderViews.get("folder0").getId()));
        assertNotNull(requestSiblingFolderViews.get("folder1"));
        assertEquals("/folder1", extractFileSystemPathFromId(requestSiblingFolderViews.get("folder1").getId()));
        assertNotNull(requestSiblingFolderViews.get("folder2"));
        assertEquals("/folder2", extractFileSystemPathFromId(requestSiblingFolderViews.get("folder2").getId()));
        requestRootFolderView = requestContext.getRootFolder();
        assertNotNull(requestRootFolderView);
        assertEquals("/", requestRootFolderView.getName());
        assertEquals("/", extractFileSystemPathFromId(requestRootFolderView.getId()));
        requestRootLinkViews = requestContext.getRootLinks();
        assertNotNull(requestRootLinkViews);
        assertEquals(2, requestRootLinkViews.size());
        assertNotNull(requestRootLinkViews.get("link0.link"));
        assertEquals("/link0.link", extractFileSystemPathFromId(requestRootLinkViews.get("link0.link").getId()));
        assertNotNull(requestRootLinkViews.get("link1.link"));
        assertEquals("/link1.link", extractFileSystemPathFromId(requestRootLinkViews.get("link1.link").getId()));
    }

    /**
     * testPortalSiteRequests - Test PortalSite request path logic
     *
     * @throws Exception
     */
    public void testPotalSiteRequests() throws Exception
    {
        assertNotNull(portalSite);

        // search path site requests
        PortalSiteSessionContext sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        JetspeedProfileLocator locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "default-page");
        locator.add("user", true, false, "user");
        Map locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        BaseConcretePageElement requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page2.psml", requestPageView.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageView.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, null);
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page2.psml", requestPageView.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageView.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "page1");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page1.psml", requestPageView.getName());
        assertEquals("/page1.psml", extractFileSystemPathFromId(requestPageView.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "folder1/");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page0.psml", requestPageView.getName());
        assertEquals("/_user/user/folder1/page0.psml", extractFileSystemPathFromId(requestPageView.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "/folder0/");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page0.psml", requestPageView.getName());
        assertEquals("/folder0/page0.psml", extractFileSystemPathFromId(requestPageView.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "/folder3/default-folder0/");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page1.psml", requestPageView.getName());
        assertEquals("/folder3/default-folder1/page1.psml", extractFileSystemPathFromId(requestPageView.getId()));

        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/_user/user/page2.psml");
        locator.add("user", true, false, "admin");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, true, true, true);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page2.psml", requestPageView.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageView.getId()));

        // physical site requests
        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        requestContext = sessionContext.newRequestContext("/", null);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page0.psml", requestPageView.getName());
        assertEquals("/page0.psml", extractFileSystemPathFromId(requestPageView.getId()));

        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        requestContext = sessionContext.newRequestContext("/_user/user/page2.psml", null);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page2.psml", requestPageView.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageView.getId()));
    }

    /**
     * testPortalSiteMenus - Test PortalSite menu generation
     *
     * @throws Exception
     */
    public void testPotalSiteMenus() throws Exception
    {
        assertNotNull(portalSite);
        PortalSiteSessionContext sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);

        // first request at /
        JetspeedProfileLocator locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("user", true, false, "user");
        locator.add("mediatype", true, false, "html");
        Map locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("role", true, false, "role0");
        locators.put("role", locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("group", true, false, "group");
        locators.put("group", locator);
        PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        Set customMenuNames = requestContext.getCustomMenuNames();
        assertNotNull(customMenuNames);
        assertEquals(5, customMenuNames.size());
        assertTrue(customMenuNames.contains("top"));
        assertTrue(customMenuNames.contains("bread-crumbs"));
        assertTrue(customMenuNames.contains("template-test"));
        assertTrue(customMenuNames.contains("current-page-test"));
        assertTrue(customMenuNames.contains("current-path-test"));
        Menu topMenu = requestContext.getMenu("top");
        assertNotNull(topMenu);
        assertEquals(MenuElement.MENU_ELEMENT_TYPE, topMenu.getElementType());
        assertNull(topMenu.getParentMenu());
        assertEquals("user root", topMenu.getTitle());
        assertEquals("user", topMenu.getShortTitle());
        assertEquals("dhtml-pull-down", topMenu.getSkin());
        assertEquals("top", topMenu.getName());
        assertEquals("/", topMenu.getUrl());
        assertFalse(topMenu.isEmpty());
        List topMenuElements = topMenu.getElements();
        assertNotNull(topMenuElements);
        assertEquals(7, topMenuElements.size());
        Iterator menuElementsIter = topMenuElements.iterator();
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("folder0"))
            {
                assertTrue(element instanceof Menu);
                assertEquals("/folder0", ((Menu)element).getUrl());
                assertTrue(((Menu)element).getParentMenu() == topMenu);
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder0", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/folder0", element.getManagedNode().getPath());
                assertFalse(((Menu)element).isEmpty());
                List elements = ((Menu)element).getElements();
                assertNotNull(elements);
                assertEquals(1, elements.size());
                assertTrue(elements.get(0) instanceof MenuOption);
                assertEquals("/folder0/page0.psml", ((MenuOption)elements.get(0)).getUrl());
                assertEquals("dhtml-pull-down", element.getSkin());
            }
            else if (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("group folder1"))
            {
                assertTrue(element instanceof Menu);
                assertEquals("/folder1", ((Menu)element).getUrl());
                assertTrue(((Menu)element).getParentMenu() == topMenu);
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder1", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/_user/user/folder1", element.getManagedNode().getPath());
                assertFalse(((Menu)element).isEmpty());
                List elements = ((Menu)element).getElements();
                assertNotNull(elements);
                assertEquals(2, elements.size());
                assertTrue(elements.get(0) instanceof MenuOption);
                assertEquals("/folder1/page0.psml", ((MenuOption)elements.get(0)).getTitle());
                assertTrue(elements.get(1) instanceof MenuOption);
                assertEquals("/folder1/page1.psml", ((MenuOption)elements.get(1)).getTitle());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page2.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/page2.psml", ((MenuOption)element).getUrl());
                assertEquals(MenuOption.PAGE_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Page);
                assertEquals("/page2.psml", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Page);
                assertEquals("/_user/user/_mediatype/html/page2.psml", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page1.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/page1.psml", ((MenuOption)element).getUrl());
                assertTrue(element.getNode() instanceof Page);
                assertEquals("/page1.psml", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Page);
                assertEquals("/page1.psml", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page0.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/page0.psml", ((MenuOption)element).getUrl());
                assertTrue(element.getNode() instanceof Page);
                assertEquals("/page0.psml", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Page);
                assertEquals("/page0.psml", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link1.link"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("http://link1", ((MenuOption)element).getUrl());
                assertEquals("top", ((MenuOption)element).getTarget());
                assertEquals(MenuOption.LINK_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Link);
                assertEquals("/link1.link", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Link);
                assertEquals("/link1.link", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link0.link"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("http://link0", ((MenuOption)element).getUrl());
                assertNull(((MenuOption)element).getTarget());
                assertEquals("dhtml-pull-down", element.getSkin());
                assertTrue(element.getNode() instanceof Link);
                assertEquals("/link0.link", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Link);
                assertEquals("/_group/group/link0.link", element.getManagedNode().getPath());
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }
        }
        assertTrue(topMenu.isSelected(requestContext));
        MenuElement selected = topMenu.getSelectedElement(requestContext);
        assertNotNull(selected);
        assertEquals(MenuElement.OPTION_ELEMENT_TYPE, selected.getElementType());
        assertTrue(selected instanceof MenuOption);
        assertEquals("/page2.psml", ((MenuOption)selected).getUrl());
        assertEquals("/page2.psml", selected.getTitle());
        assertEquals("dhtml-pull-down", selected.getSkin());
        assertFalse(((MenuImpl)topMenu).isElementRelative());
        Menu breadCrumbsMenu = requestContext.getMenu("bread-crumbs");
        assertNotNull(breadCrumbsMenu);
        assertEquals("bread-crumbs", breadCrumbsMenu.getName());
        assertEquals("/", breadCrumbsMenu.getUrl());
        assertFalse(breadCrumbsMenu.isEmpty());
        List breadCrumbsElements = breadCrumbsMenu.getElements();
        assertNotNull(breadCrumbsElements);
        assertEquals(1, breadCrumbsElements.size());
        assertTrue(breadCrumbsElements.get(0) instanceof MenuOption);
        assertEquals("/", ((MenuOption)breadCrumbsElements.get(0)).getUrl());
        assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)breadCrumbsElements.get(0)).getType());
        assertTrue(((MenuImpl)breadCrumbsMenu).isElementRelative());
        Menu templateTestMenu = requestContext.getMenu("template-test");
        assertNotNull(templateTestMenu);
        assertEquals("template-test", templateTestMenu.getName());
        assertFalse(templateTestMenu.isEmpty());
        List templateTestElements = templateTestMenu.getElements();
        assertNotNull(templateTestElements);
        assertEquals(1, templateTestElements.size());
        assertTrue(templateTestElements.get(0) instanceof MenuOption);
        assertEquals("/page1.psml", ((MenuOption)templateTestElements.get(0)).getUrl());
        Menu currentPageTestMenu = requestContext.getMenu("current-page-test");
        assertEquals("current-page-test", currentPageTestMenu.getName());
        assertFalse(currentPageTestMenu.isEmpty());
        List currentPageTestElements = currentPageTestMenu.getElements();
        assertNotNull(currentPageTestElements);
        assertEquals(1, currentPageTestElements.size());
        assertTrue(currentPageTestElements.get(0) instanceof MenuOption);
        assertEquals("/page2.psml", ((MenuOption)currentPageTestElements.get(0)).getUrl());
        assertTrue(currentPageTestMenu.isSelected(requestContext));
        assertTrue(((MenuOption)currentPageTestElements.get(0)).isSelected(requestContext));
        Menu currentPathTestMenu = requestContext.getMenu("current-path-test");
        assertEquals("current-path-test", currentPathTestMenu.getName());
        assertTrue(currentPathTestMenu.isEmpty());
        List currentPathTestElements = currentPathTestMenu.getElements();
        assertNull(currentPathTestElements);

        // second request at /folder0
        locator = new JetspeedProfileLocator();
        locator.init(null, "/folder0");
        locator.add("user", true, false, "user");
        locator.add("mediatype", true, false, "html");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/folder0");
        locator.add("role", true, false, "role0");
        locators.put("role", locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/folder0");
        locator.add("group", true, false, "group");
        locators.put("group", locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        Menu topMenu2 = requestContext.getMenu("top");
        assertNotNull(topMenu2);
        assertTrue(topMenu == topMenu2);
        Menu breadCrumbsMenu2 = requestContext.getMenu("bread-crumbs");
        assertNotNull(breadCrumbsMenu2);
        assertTrue(breadCrumbsMenu != breadCrumbsMenu2);
        assertEquals("bread-crumbs", breadCrumbsMenu2.getName());
        assertEquals("/folder0", breadCrumbsMenu2.getUrl());
        assertFalse(breadCrumbsMenu2.isEmpty());
        breadCrumbsElements = breadCrumbsMenu2.getElements();
        assertNotNull(breadCrumbsElements);
        assertEquals(2, breadCrumbsElements.size());
        assertTrue(breadCrumbsElements.get(0) instanceof MenuOption);
        assertEquals("/", ((MenuOption)breadCrumbsElements.get(0)).getUrl());
        assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)breadCrumbsElements.get(0)).getType());
        assertEquals("/folder0", ((MenuOption)breadCrumbsElements.get(1)).getUrl());
        assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)breadCrumbsElements.get(1)).getType());
        assertTrue(((MenuImpl)breadCrumbsMenu2).isElementRelative());
        Menu hiddenMenu = requestContext.getMenu("override-hidden");
        assertNotNull(hiddenMenu);
        assertTrue(hiddenMenu.isEmpty());
        Menu navigationsMenu = requestContext.getMenu("navigations");
        assertNotNull(navigationsMenu);
        assertTrue(navigationsMenu.isEmpty());
        currentPageTestMenu = requestContext.getMenu("current-page-test");
        assertEquals("current-page-test", currentPageTestMenu.getName());
        assertFalse(currentPageTestMenu.isEmpty());
        currentPageTestElements = currentPageTestMenu.getElements();
        assertNotNull(currentPageTestElements);
        assertEquals(1, currentPageTestElements.size());
        assertTrue(currentPageTestElements.get(0) instanceof MenuOption);
        assertEquals("/folder0/page0.psml", ((MenuOption)currentPageTestElements.get(0)).getUrl());
        assertTrue(currentPageTestMenu.isSelected(requestContext));
        assertTrue(((MenuOption)currentPageTestElements.get(0)).isSelected(requestContext));
        currentPathTestMenu = requestContext.getMenu("current-path-test");
        assertEquals("current-path-test", currentPathTestMenu.getName());
        assertFalse(currentPathTestMenu.isEmpty());
        currentPathTestElements = currentPathTestMenu.getElements();
        assertNotNull(currentPathTestElements);
        assertEquals(1, currentPathTestElements.size());
        assertTrue(currentPathTestElements.get(0) instanceof MenuOption);
        assertEquals("/folder0/page0.psml", ((MenuOption)currentPathTestElements.get(0)).getUrl());
        assertTrue(currentPathTestMenu.isSelected(requestContext));
        assertTrue(((MenuOption)currentPathTestElements.get(0)).isSelected(requestContext));
        
        // third request at /page1.psml
        locator = new JetspeedProfileLocator();
        locator.init(null, "/page1.psml");
        locator.add("user", true, false, "user");
        locator.add("mediatype", true, false, "html");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/page1.psml");
        locator.add("role", true, false, "role0");
        locators.put("role", locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/page1.psml");
        locator.add("group", true, false, "group");
        locators.put("group", locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        assertNull(requestContext.getMenu("no-such-menu"));
        Menu topMenu3 = requestContext.getMenu("top");
        assertNotNull(topMenu3);
        assertTrue(topMenu == topMenu3);
        Menu backMenu = requestContext.getMenu("back");
        assertNotNull(backMenu);
        assertTrue(backMenu.isEmpty());
        assertNull(backMenu.getElements());
        assertTrue(((MenuImpl)backMenu).isElementRelative());
        assertEquals("back", backMenu.getName());
        assertEquals("Back to", backMenu.getTitle());
        assertEquals("Back to", backMenu.getShortTitle());
        assertEquals("\u00bb", backMenu.getTitle(Locale.JAPANESE));
        assertEquals("\u00bb", backMenu.getShortTitle(Locale.JAPANESE));
        assertEquals("Back to", backMenu.getTitle(new Locale("xx")));
        assertEquals("Back to", backMenu.getShortTitle(new Locale("xx")));
        Menu breadcrumbsMenu = requestContext.getMenu("breadcrumbs");
        assertNotNull(breadcrumbsMenu);
        assertFalse(breadcrumbsMenu.isEmpty());
        assertEquals("You are here:", breadcrumbsMenu.getTitle());
        assertEquals("\u73fe\u5728\u30d1\u30b9\uff1a", breadcrumbsMenu.getTitle(Locale.JAPANESE));
        navigationsMenu = requestContext.getMenu("navigations");
        assertNotNull(navigationsMenu);
        assertFalse(navigationsMenu.isEmpty());
        List navigationsElements = navigationsMenu.getElements();
        assertNotNull(navigationsElements);
        assertEquals(6, navigationsElements.size());
        menuElementsIter = navigationsElements.iterator();
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.SEPARATOR_ELEMENT_TYPE) &&
                (element instanceof MenuSeparator) &&
                ((MenuSeparator)element).getText().equals("Folders"))
            {
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("folder0"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder0", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/folder0", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("group folder1"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder1", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/_user/user/folder1", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.SEPARATOR_ELEMENT_TYPE) &&
                     (element instanceof MenuSeparator) &&
                     ((MenuSeparator)element).getText().equals("Additional Links"))
            {
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link1.link"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.LINK_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Link);
                assertEquals("/link1.link", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Link);
                assertEquals("/link1.link", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link0.link"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.LINK_OPTION_TYPE, ((MenuOption)element).getType());
                assertEquals("left-navigations", element.getSkin());
                assertTrue(element.getNode() instanceof Link);
                assertEquals("/link0.link", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Link);
                assertEquals("/_group/group/link0.link", element.getManagedNode().getPath());
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }
        }
        assertEquals("left-navigations", navigationsMenu.getSkin());
        assertTrue(((MenuImpl)navigationsMenu).isElementRelative());
        Menu pagesMenu = requestContext.getMenu("pages");
        assertNotNull(pagesMenu);
        assertFalse(pagesMenu.isEmpty());
        List pagesElements = pagesMenu.getElements();
        assertNotNull(pagesElements);
        assertEquals(3, pagesElements.size());
        menuElementsIter = pagesElements.iterator();
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page2.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/page2.psml", ((MenuOption)element).getUrl());
                assertEquals(MenuOption.PAGE_OPTION_TYPE, ((MenuOption)element).getType());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page1.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/page1.psml", ((MenuOption)element).getUrl());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page0.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/page0.psml", ((MenuOption)element).getUrl());
                assertEquals("tabs", element.getSkin());
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }
        }
        assertEquals("tabs", pagesMenu.getSkin());
        assertTrue(((MenuImpl)pagesMenu).isElementRelative());

        // fourth request at /page0.psml
        locator = new JetspeedProfileLocator();
        locator.init(null, "/page0.psml");
        locator.add("user", true, false, "user");
        locator.add("mediatype", true, false, "html");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/page0.psml");
        locator.add("role", true, false, "role0");
        locators.put("role", locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/page0.psml");
        locator.add("group", true, false, "group");
        locators.put("group", locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        Menu templateTestMenu2 = requestContext.getMenu("template-test");
        assertNotNull(templateTestMenu2);
        assertEquals("template-test", templateTestMenu2.getName());
        assertFalse(templateTestMenu2.isEmpty());
        List templateTestElements2 = templateTestMenu2.getElements();
        assertNotNull(templateTestElements2);
        assertEquals(1, templateTestElements2.size());
        assertTrue(templateTestElements2.get(0) instanceof MenuOption);
        assertEquals("/page2.psml", ((MenuOption)templateTestElements2.get(0)).getUrl());

        // new request at /folder1
        locator = new JetspeedProfileLocator();
        locator.init(null, "/folder1");
        locator.add("user", true, false, "user");
        locator.add("mediatype", true, false, "html");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/folder1");
        locator.add("group", true, false, "group");
        locators.put("group", locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        Menu backMenu2 = requestContext.getMenu("back");
        assertNotNull(backMenu2);
        assertFalse(backMenu2.isEmpty());
        Menu topMenu4 = requestContext.getMenu("top");
        assertNotNull(topMenu4);
        assertTrue(topMenu != topMenu4);
        Menu customMenu = requestContext.getMenu("custom");
        assertNotNull(customMenu);
        assertFalse(customMenu.isEmpty());
        List customElements = customMenu.getElements();
        assertNotNull(customElements);
        assertEquals(12, customElements.size());
        assertEquals("custom", customMenu.getName());
        assertEquals("Top Menu", customMenu.getTitle());
        assertEquals("Haut", customMenu.getTitle(Locale.FRENCH));
        menuElementsIter = customElements.iterator();        
        for (int i = 0; ((i < 2) && menuElementsIter.hasNext()); i++)
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link0.link"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("http://link0", ((MenuOption)element).getUrl());
                assertEquals(MenuOption.LINK_OPTION_TYPE, ((MenuOption)element).getType());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/folder1/page1.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/folder1/page1.psml", ((MenuOption)element).getUrl());
                assertEquals(MenuOption.PAGE_OPTION_TYPE, ((MenuOption)element).getType());
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }            
        }
        if (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("user root"))
            {
                assertFalse(((Menu)element).isEmpty());
                List nestedElements = ((Menu)element).getElements();
                assertEquals(6, nestedElements.size());
                Iterator nestedElementsIter = nestedElements.iterator();
                if (nestedElementsIter.hasNext())
                {
                    MenuElement nestedElement = (MenuElement)nestedElementsIter.next();
                    if (nestedElement.getElementType().equals(MenuElement.SEPARATOR_ELEMENT_TYPE) &&
                        (nestedElement instanceof MenuSeparator) &&
                        ((MenuSeparator)nestedElement).getText().equals("=== Current Page ==="))
                    {
                    }
                    else
                    {
                        fail("Unexpected nested menu element type/title: "+nestedElement.getElementType()+"/"+nestedElement.getTitle());
                    }            
                }
                if (nestedElementsIter.hasNext())
                {
                    MenuElement nestedElement = (MenuElement)nestedElementsIter.next();
                    if (nestedElement.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) &&
                        nestedElement.getTitle().equals("/folder1/page1.psml"))
                    {
                        assertTrue(nestedElement instanceof MenuOption);
                        assertEquals("/folder1/page1.psml", ((MenuOption)nestedElement).getUrl());
                        assertEquals(MenuOption.PAGE_OPTION_TYPE, ((MenuOption)nestedElement).getType());
                    }
                    else
                    {
                        fail("Unexpected nested menu element type/title: "+nestedElement.getElementType()+"/"+nestedElement.getTitle());
                    }            
                }
                if (nestedElementsIter.hasNext())
                {
                    MenuElement nestedElement = (MenuElement)nestedElementsIter.next();
                    if (nestedElement.getElementType().equals(MenuElement.SEPARATOR_ELEMENT_TYPE) &&
                        (nestedElement instanceof MenuSeparator) &&
                        ((MenuSeparator)nestedElement).getText().equals("=== Top Pages ==="))
                    {
                        assertEquals("Top Pages", nestedElement.getTitle());
                    }
                    else
                    {
                        fail("Unexpected nested menu element type/title: "+nestedElement.getElementType()+"/"+nestedElement.getTitle());
                    }            
                }
                for (int i = 0; ((i < 3) && nestedElementsIter.hasNext()); i++)
                {
                    MenuElement nestedElement = (MenuElement)nestedElementsIter.next();
                    if (nestedElement.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) &&
                        nestedElement.getTitle().equals("/page2.psml"))
                    {
                        assertTrue(nestedElement instanceof MenuOption);
                        assertEquals("/page2.psml", ((MenuOption)nestedElement).getUrl());
                        assertEquals(MenuOption.PAGE_OPTION_TYPE, ((MenuOption)nestedElement).getType());
                    }
                    else if (nestedElement.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) &&
                             nestedElement.getTitle().equals("/page1.psml"))
                    {
                        assertTrue(nestedElement instanceof MenuOption);
                        assertEquals("/page1.psml", ((MenuOption)nestedElement).getUrl());
                        assertEquals(MenuOption.PAGE_OPTION_TYPE, ((MenuOption)nestedElement).getType());
                    }
                    else if (nestedElement.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) &&
                             nestedElement.getTitle().equals("/page0.psml"))
                    {
                        assertTrue(nestedElement instanceof MenuOption);
                        assertEquals("/page0.psml", ((MenuOption)nestedElement).getUrl());
                        assertEquals(MenuOption.PAGE_OPTION_TYPE, ((MenuOption)nestedElement).getType());
                    }
                    else
                    {
                        fail("Unexpected nested menu element type/title: "+nestedElement.getElementType()+"/"+nestedElement.getTitle());
                    }            
                }
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }            
        }
        if (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.SEPARATOR_ELEMENT_TYPE) &&
                (element instanceof MenuSeparator) &&
                ((MenuSeparator)element).getText().equals("=== More Options ==="))
            {
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }            
        }
        for (int i = 0; ((i < 4) && menuElementsIter.hasNext()); i++)
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link1.link"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("http://link1", ((MenuOption)element).getUrl());
                assertEquals(MenuOption.LINK_OPTION_TYPE, ((MenuOption)element).getType());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page2.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/page2.psml", ((MenuOption)element).getUrl());
                assertEquals(MenuOption.PAGE_OPTION_TYPE, ((MenuOption)element).getType());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page1.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/page1.psml", ((MenuOption)element).getUrl());
                assertEquals(MenuOption.PAGE_OPTION_TYPE, ((MenuOption)element).getType());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/folder1/page1.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/folder1/page1.psml", ((MenuOption)element).getUrl());
                assertEquals(MenuOption.PAGE_OPTION_TYPE, ((MenuOption)element).getType());
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }            
        }
        if (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.SEPARATOR_ELEMENT_TYPE) &&
                (element instanceof MenuSeparator) &&
                ((MenuSeparator)element).getText().equals("=== Standard Menus ==="))
            {
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }            
        }
        if (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("user root"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/", ((MenuOption)element).getUrl());
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }            
        }
        if (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("navigations"))
            {
                assertTrue(element instanceof Menu);
                assertEquals("navigations", ((Menu)element).getName());
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }            
        }
        if (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("pages"))
            {
                assertTrue(element instanceof Menu);
                assertEquals("pages", ((Menu)element).getName());
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }            
        }

        // physical site menus
        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        requestContext = sessionContext.newRequestContext("/", null);
        assertNotNull(requestContext);
        customMenuNames = requestContext.getCustomMenuNames();
        assertNotNull(customMenuNames);
        assertEquals(4, customMenuNames.size());
        assertTrue(customMenuNames.contains("bread-crumbs"));
        assertTrue(customMenuNames.contains("template-test"));
        assertTrue(customMenuNames.contains("current-page-test"));
        assertTrue(customMenuNames.contains("current-path-test"));
        breadCrumbsMenu = requestContext.getMenu("bread-crumbs");
        assertNotNull(breadCrumbsMenu);
        assertEquals("bread-crumbs", breadCrumbsMenu.getName());
        assertEquals("/", breadCrumbsMenu.getUrl());
        assertFalse(breadCrumbsMenu.isEmpty());
        breadCrumbsElements = breadCrumbsMenu.getElements();
        assertNotNull(breadCrumbsElements);
        assertEquals(1, breadCrumbsElements.size());
        assertTrue(breadCrumbsElements.get(0) instanceof MenuOption);
        assertEquals("/", ((MenuOption)breadCrumbsElements.get(0)).getUrl());
        assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)breadCrumbsElements.get(0)).getType());
        assertTrue(((MenuImpl)breadCrumbsMenu).isElementRelative());
        templateTestMenu = requestContext.getMenu("template-test");
        assertNotNull(templateTestMenu);
        assertEquals("template-test", templateTestMenu.getName());
        assertFalse(templateTestMenu.isEmpty());
        templateTestElements = templateTestMenu.getElements();
        assertNotNull(templateTestElements);
        assertEquals(1, templateTestElements.size());
        assertTrue(templateTestElements.get(0) instanceof MenuOption);
        assertEquals("/page2.psml", ((MenuOption)templateTestElements.get(0)).getUrl());
        currentPageTestMenu = requestContext.getMenu("current-page-test");
        assertEquals("current-page-test", currentPageTestMenu.getName());
        assertFalse(currentPageTestMenu.isEmpty());
        currentPageTestElements = currentPageTestMenu.getElements();
        assertNotNull(currentPageTestElements);
        assertEquals(1, currentPageTestElements.size());
        assertTrue(currentPageTestElements.get(0) instanceof MenuOption);
        assertEquals("/page0.psml", ((MenuOption)currentPageTestElements.get(0)).getUrl());
        assertTrue(currentPageTestMenu.isSelected(requestContext));
        assertTrue(((MenuOption)currentPageTestElements.get(0)).isSelected(requestContext));
        currentPathTestMenu = requestContext.getMenu("current-path-test");
        assertEquals("current-path-test", currentPathTestMenu.getName());
        assertTrue(currentPathTestMenu.isEmpty());
        currentPathTestElements = currentPathTestMenu.getElements();
        assertNull(currentPathTestElements);
        navigationsMenu = requestContext.getMenu("navigations");
        assertNotNull(navigationsMenu);
        assertFalse(navigationsMenu.isEmpty());
        navigationsElements = navigationsMenu.getElements();
        assertNotNull(navigationsElements);
        assertEquals(11, navigationsElements.size());
        menuElementsIter = navigationsElements.iterator();
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.SEPARATOR_ELEMENT_TYPE) &&
                (element instanceof MenuSeparator) &&
                ((MenuSeparator)element).getText().equals("Folders"))
            {
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("subsite root"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/__subsite-root", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/__subsite-root", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("Group"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/_group", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/_group", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("Hostname"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/_hostname", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/_hostname", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("Role"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/_role", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/_role", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("User"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/_user", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/_user", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("folder0"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder0", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/folder0", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("folder1"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder1", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/folder1", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.SEPARATOR_ELEMENT_TYPE) &&
                     (element instanceof MenuSeparator) &&
                     ((MenuSeparator)element).getText().equals("Additional Links"))
            {
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link1.link"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.LINK_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Link);
                assertEquals("/link1.link", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Link);
                assertEquals("/link1.link", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link0.link"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.LINK_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Link);
                assertEquals("/link0.link", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Link);
                assertEquals("/link0.link", element.getManagedNode().getPath());
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }
        }
        assertEquals("left-navigations", navigationsMenu.getSkin());
        assertTrue(((MenuImpl)navigationsMenu).isElementRelative());
        pagesMenu = requestContext.getMenu("pages");
        assertNotNull(pagesMenu);
        assertFalse(pagesMenu.isEmpty());
        pagesElements = pagesMenu.getElements();
        assertNotNull(pagesElements);
        assertEquals(3, pagesElements.size());
        menuElementsIter = pagesElements.iterator();
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page2.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/page2.psml", ((MenuOption)element).getUrl());
                assertEquals(MenuOption.PAGE_OPTION_TYPE, ((MenuOption)element).getType());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page1.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/page1.psml", ((MenuOption)element).getUrl());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page0.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/page0.psml", ((MenuOption)element).getUrl());
                assertEquals("tabs", element.getSkin());
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }
        }
        assertEquals("tabs", pagesMenu.getSkin());
        assertTrue(((MenuImpl)pagesMenu).isElementRelative());
    }

    /**
     * testPortalSiteHiddenPageMenus - Test PortalSite menu generation for hidden pages
     *
     * @throws Exception
     */
    public void testPotalSiteHiddenPageMenus() throws Exception
    {
        assertNotNull(portalSite);
        PortalSiteSessionContext sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);

        // first request at /: hidden page suppressed
        JetspeedProfileLocator locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("user", true, false, "user");
        Map locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        Menu topMenu = requestContext.getMenu("top");
        assertNotNull(topMenu);
        assertFalse(topMenu.isEmpty());
        List topMenuElements = topMenu.getElements();
        assertNotNull(topMenuElements);
        Iterator menuElementsIter = topMenuElements.iterator();
        boolean hiddenElement = false;
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/hidden.psml"))
            {
                hiddenElement = true;
            }
        }
        assertFalse(hiddenElement);
        Menu pagesMenu = requestContext.getMenu("pages");
        assertNotNull(pagesMenu);
        assertFalse(pagesMenu.isEmpty());
        List pagesElements = pagesMenu.getElements();
        assertNotNull(pagesElements);
        menuElementsIter = pagesElements.iterator();
        hiddenElement = false;
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/hidden.psml"))
            {
                hiddenElement = true;
            }
        }
        assertFalse(hiddenElement);

        // second request at /hidden.psml: hidden page visible
        locator = new JetspeedProfileLocator();
        locator.init(null, "/hidden.psml");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        topMenu = requestContext.getMenu("top");
        assertNotNull(topMenu);
        assertFalse(topMenu.isEmpty());
        topMenuElements = topMenu.getElements();
        assertNotNull(topMenuElements);
        menuElementsIter = topMenuElements.iterator();
        hiddenElement = false;
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/hidden.psml"))
            {
                hiddenElement = true;
            }
        }
        assertTrue(hiddenElement);
        pagesMenu = requestContext.getMenu("pages");
        assertNotNull(pagesMenu);
        assertFalse(pagesMenu.isEmpty());
        pagesElements = pagesMenu.getElements();
        assertNotNull(pagesElements);
        menuElementsIter = pagesElements.iterator();
        hiddenElement = false;
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/hidden.psml"))
            {
                hiddenElement = true;
            }
        }
        assertTrue(hiddenElement);

        // third request at /: hidden page suppressed
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        topMenu = requestContext.getMenu("top");
        assertNotNull(topMenu);
        assertFalse(topMenu.isEmpty());
        topMenuElements = topMenu.getElements();
        assertNotNull(topMenuElements);
        menuElementsIter = topMenuElements.iterator();
        hiddenElement = false;
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/hidden.psml"))
            {
                hiddenElement = true;
            }
        }
        assertFalse(hiddenElement);
        pagesMenu = requestContext.getMenu("pages");
        assertNotNull(pagesMenu);
        assertFalse(pagesMenu.isEmpty());
        pagesElements = pagesMenu.getElements();
        assertNotNull(pagesElements);
        menuElementsIter = pagesElements.iterator();
        hiddenElement = false;
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/hidden.psml"))
            {
                hiddenElement = true;
            }
        }
        assertFalse(hiddenElement);

        // physical site menus
        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);

        // first request at /: hidden page suppressed
        requestContext = sessionContext.newRequestContext("/", null);
        assertNotNull(requestContext);
        pagesMenu = requestContext.getMenu("pages");
        assertNotNull(pagesMenu);
        assertFalse(pagesMenu.isEmpty());
        pagesElements = pagesMenu.getElements();
        assertNotNull(pagesElements);
        menuElementsIter = pagesElements.iterator();
        hiddenElement = false;
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/hidden.psml"))
            {
                hiddenElement = true;
            }
        }
        assertFalse(hiddenElement);
    
        // second request at /hidden.psml: hidden page visible
        requestContext = sessionContext.newRequestContext("/hidden.psml", null);
        assertNotNull(requestContext);
        pagesMenu = requestContext.getMenu("pages");
        assertNotNull(pagesMenu);
        assertFalse(pagesMenu.isEmpty());
        pagesElements = pagesMenu.getElements();
        assertNotNull(pagesElements);
        menuElementsIter = pagesElements.iterator();
        hiddenElement = false;
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/hidden.psml"))
            {
                hiddenElement = true;
            }
        }
        assertTrue(hiddenElement);
    }

    /**
     * testPortalSiteContentRequests - Test PortalSite content request mapping and
     *                                 dynamic page resolution
     *
     * @throws Exception
     */
    public void testPotalSiteContentRequests() throws Exception
    {
        assertNotNull(portalSite);

        // search path site view
        PortalSiteSessionContext sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        JetspeedProfileLocator locator = new JetspeedProfileLocator();
        locator.init(null, "/document.doc");
        locator.add("user", true, false, "user");
        Map locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        BaseConcretePageElement requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("docpage.dpsml", requestPageView.getName());
        assertEquals("/docpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/contentfolder/document.doc");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("matchdocpage.dpsml", requestPageView.getName());
        assertEquals("/contentfolder/matchdocpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/contentfolder/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/contentfolder/folder/document.doc");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("docpage.dpsml", requestPageView.getName());
        assertEquals("/contentfolder/docpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/contentfolder/folder/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/contentfolder/document.txt");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("contentpage.dpsml", requestPageView.getName());
        assertEquals("/contentpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/contentfolder/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/preview/document.doc");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("docpage.dpsml", requestPageView.getName());
        assertEquals("/docpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/preview/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/preview/contentfolder/draft/document.doc", "test.domain.com");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("docpage.dpsml", requestPageView.getName());
        assertEquals("/contentfolder/pub/docpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/preview/contentfolder/draft/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/document.psml");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("contentpage.dpsml", requestPageView.getName());
        assertEquals("/contentpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/preview/folder0/page0.doc");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page0.psml", requestPageView.getName());
        assertEquals("/folder0/page0.psml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/preview/folder0/page0", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/page2.doc");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page2.psml", requestPageView.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/page2", requestContext.getPageContentPath());

        // physical site view
        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        requestContext = sessionContext.newRequestContext("/document.doc", null);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("docpage.dpsml", requestPageView.getName());
        assertEquals("/docpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/document", requestContext.getPageContentPath());

        requestContext = sessionContext.newRequestContext("/preview/contentfolder/draft/document.doc", "test.domain.com");
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("docpage.dpsml", requestPageView.getName());
        assertEquals("/contentfolder/pub/docpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/preview/contentfolder/draft/document", requestContext.getPageContentPath());

        requestContext = sessionContext.newRequestContext("/document.psml", null);
        assertNotNull(requestContext);
        requestPageView = requestContext.getPage();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("contentpage.dpsml", requestPageView.getName());
        assertEquals("/contentpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/document", requestContext.getPageContentPath());
    }

    /**
     * extractFileSystemPathFromId - utility to convert proxy ids to file system paths
     *
     * @param id proxy node id
     * @return files system path
     */
    private String extractFileSystemPathFromId(String id)
    {
        if ((id != null) && !id.equals(Folder.PATH_SEPARATOR) && id.endsWith(Folder.PATH_SEPARATOR))
        {
            return id.substring(0, id.length() - 1);
        }
        return id;
    }

    /**
     * extractLocatorNameFromView - utility to access profile locator name from view
     *
     * @param proxy site view node view
     * @return locator name
     */
    private String extractLocatorNameFromView(Object view) throws Exception
    {
        try
        {
            return ((NodeProxy)Proxy.getInvocationHandler(view)).getLocatorName();
        }
        catch (IllegalArgumentException iae)
        {
            return null;
        }
    }
}
