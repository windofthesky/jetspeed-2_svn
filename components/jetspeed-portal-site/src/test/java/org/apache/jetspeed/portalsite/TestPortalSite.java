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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.jetspeed.components.test.AbstractSpringTestCase;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.page.BaseConcretePageElement;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.proxy.NodeProxy;
import org.apache.jetspeed.portalsite.impl.MenuImpl;
import org.apache.jetspeed.portalsite.view.PhysicalSiteView;
import org.apache.jetspeed.portalsite.view.SearchPathsSiteView;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.impl.JetspeedProfileLocator;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
     * defaultLocale - default locale
     */
    private Locale defaultLocale;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        this.pageManager = scm.lookupComponent("pageManager");
        this.portalSite = scm.lookupComponent("portalSite");
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
        assertEquals(6, rootFolderView.getFolders().size());
        Iterator<Node> foldersIter = rootFolderView.getFolders().iterator();
        assertEquals("contentfolder", ((Folder)foldersIter.next()).getName());
        assertEquals("folder0", ((Folder)foldersIter.next()).getName());
        assertEquals("folder1", ((Folder)foldersIter.next()).getName());
        assertEquals("folder2", ((Folder)foldersIter.next()).getName());
        assertEquals("folder3", ((Folder)foldersIter.next()).getName());
        assertEquals("folder4", ((Folder)foldersIter.next()).getName());
        assertEquals(4, rootFolderView.getPages().size());
        Iterator<Node> pagesIter = rootFolderView.getPages().iterator();
        assertEquals("page2.psml", ((Page)pagesIter.next()).getName());
        assertEquals("page1.psml", ((Page)pagesIter.next()).getName());
        assertEquals("page0.psml", ((Page)pagesIter.next()).getName());
        assertEquals("hidden.psml", ((Page)pagesIter.next()).getName());
        assertEquals(2, rootFolderView.getLinks().size());
        Iterator<Node> linksIter = rootFolderView.getLinks().iterator();
        assertEquals("link1.link", ((Link)linksIter.next()).getName());
        assertEquals("link0.link", ((Link)linksIter.next()).getName());
        assertEquals(2, rootFolderView.getDynamicPages().size());
        Iterator<Node> dynamicPagesIter = rootFolderView.getDynamicPages().iterator();
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
        Folder rootFolderViewByPath = (Folder)baseView.getNodeView("/", null, null, true, false, false);
        assertNotNull(rootFolderViewByPath);
        assertEquals(rootFolderView, rootFolderViewByPath);
        Folder rootFolder0ViewByPath = (Folder)baseView.getNodeView("/folder0/", null, null, true, false, false);
        assertNotNull(rootFolder0ViewByPath);
        assertEquals(rootFolder0View, rootFolder0ViewByPath);
        Page folder0Page0ViewByPath = (Page)baseView.getNodeView("/folder0/page0.psml", null, null, true, false, false);
        assertNotNull(folder0Page0ViewByPath);
        assertEquals(folder0Page0View, folder0Page0ViewByPath);
        folder0Page0ViewByPath = (Page)baseView.getNodeView("page0.psml", rootFolder0View, null, true, false, false);
        assertNotNull(folder0Page0ViewByPath);
        assertEquals(folder0Page0View, folder0Page0ViewByPath);
        try
        {
            baseView.getNodeView("/folderX/page0.psml", null, null, true, false, false);
            fail("/folderX/page0.psml should not be found");
        }
        catch (NodeNotFoundException nnfe)
        {
        }
        try
        {
            baseView.getNodeView("/folder0/pageX.psml", null, null, true, false, false);
            fail("/folder0/pageX.psml should not be found");
        }
        catch (NodeNotFoundException nnfe)
        {
        }
        List<Node> rootPageViewsByPath = baseView.getNodeViews("/page?.psml", null, null, true, false, false);
        assertNotNull(rootPageViewsByPath);
        assertEquals(3,rootPageViewsByPath.size());
        assertTrue(rootPageViewsByPath.contains(rootPage0View));
        List<Node> rootFolderViewsByPath = baseView.getNodeViews("/*/", null, null, true, false, false);
        assertNotNull(rootFolderViewsByPath);
        assertEquals(6,rootFolderViewsByPath.size());
        assertTrue(rootFolderViewsByPath.contains(rootFolder0View));
        List<Node> folderPageViewsByPath = baseView.getNodeViews("*/p*[0-9].psml", rootFolderView, null, true, false, false);
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
        assertEquals(6, rootFolderView.getFolders().size());
        assertEquals(4, rootFolderView.getPages().size());
        assertEquals(2, rootFolderView.getLinks().size());
        assertEquals(2, rootFolderView.getDynamicPages().size());
        rootPage0View = rootFolderView.getPage("page0.psml");
        assertNotNull(rootPage0View);
        assertEquals(rootFolderView, rootPage0View.getParent());
        assertEquals("page0.psml", rootPage0View.getName());
        assertEquals("/page0.psml", extractFileSystemPathFromId(rootPage0View.getId()));
        List<MenuDefinition> rootPage0ViewMenus = rootPage0View.getMenuDefinitions();
        assertNotNull(rootPage0ViewMenus);
        assertEquals(7 + aggregateView.getStandardMenuNames().size(), rootPage0ViewMenus.size());
        Iterator<MenuDefinition> menusIter = rootPage0ViewMenus.iterator();
        MenuDefinition rootPage0ViewTemplateTestMenu = menusIter.next();
        assertEquals("template-test", rootPage0ViewTemplateTestMenu.getName());
        assertEquals("/page2.psml", rootPage0ViewTemplateTestMenu.getOptions());
        MenuDefinition rootPage0ViewTopMenu = menusIter.next();
        assertEquals("top", rootPage0ViewTopMenu.getName());
        assertEquals("/", rootPage0ViewTopMenu.getOptions());
        assertEquals(2, rootPage0ViewTopMenu.getDepth());
        assertEquals("dhtml-pull-down", rootPage0ViewTopMenu.getSkin());
        MenuDefinition rootPage0ViewBreadCrumbMenu = menusIter.next();
        assertEquals("bread-crumbs", rootPage0ViewBreadCrumbMenu.getName());
        assertEquals("./", rootPage0ViewBreadCrumbMenu.getOptions());
        assertEquals(true, rootPage0ViewBreadCrumbMenu.isPaths());
        MenuDefinition rootPage0ViewCurrentPageTestMenu = menusIter.next();
        assertEquals("current-page-test", rootPage0ViewCurrentPageTestMenu.getName());
        MenuDefinition rootPage0ViewCurrentPathTestMenu = menusIter.next();
        assertEquals("current-path-test", rootPage0ViewCurrentPathTestMenu.getName());        
        MenuDefinition rootPage0SiteNavigationsMenu = menusIter.next();
        assertEquals("site-navigations", rootPage0SiteNavigationsMenu.getName());        
        assertTrue(rootPage0SiteNavigationsMenu.isRegexp());        
        assertEquals("/*/,/*.psml", rootPage0SiteNavigationsMenu.getOptions());        
        assertEquals(-1, rootPage0SiteNavigationsMenu.getDepth());        
        MenuDefinition rootPage0RootedNavigationsMenu = menusIter.next();
        assertEquals("rooted-navigations", rootPage0RootedNavigationsMenu.getName());        
        for (int i = 0; (i < aggregateView.getStandardMenuNames().size()); i++)
        {
            assertTrue(aggregateView.getStandardMenuNames().contains((menusIter.next()).getName()));
        }
        Page rootPage2View = rootFolderView.getPage("page2.psml");
        assertNotNull(rootPage2View);
        assertEquals(rootFolderView, rootPage2View.getParent());
        assertEquals("page2.psml", rootPage2View.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(rootPage2View.getId()));
        List<MenuDefinition> rootPage2ViewMenus = rootPage2View.getMenuDefinitions();
        assertNotNull(rootPage2ViewMenus);
        assertEquals(7 + aggregateView.getStandardMenuNames().size(), rootPage2ViewMenus.size());
        menusIter = rootPage2ViewMenus.iterator();
        MenuDefinition rootPage2ViewTopMenu = menusIter.next();
        assertEquals("top", rootPage2ViewTopMenu.getName());
        assertEquals("/", rootPage2ViewTopMenu.getOptions());
        assertEquals(1, rootPage2ViewTopMenu.getDepth());
        MenuDefinition rootPage2ViewBreadCrumbMenu = menusIter.next();
        assertEquals("bread-crumbs", rootPage2ViewBreadCrumbMenu.getName());
        MenuDefinition rootPage2ViewTemplateTestMenu = menusIter.next();
        assertEquals("template-test", rootPage2ViewTemplateTestMenu.getName());
        assertEquals("/page0.psml", rootPage2ViewTemplateTestMenu.getOptions());
        MenuDefinition rootPage2ViewCurrentPageTestMenu = menusIter.next();
        assertEquals("current-page-test", rootPage2ViewCurrentPageTestMenu.getName());
        MenuDefinition rootPage2ViewCurrentPathTestMenu = menusIter.next();
        assertEquals("current-path-test", rootPage2ViewCurrentPathTestMenu.getName());
        MenuDefinition rootPage2SiteNavigationsMenu = menusIter.next();
        assertEquals("site-navigations", rootPage2SiteNavigationsMenu.getName());        
        MenuDefinition rootPage2RootedNavigationsMenu = menusIter.next();
        assertEquals("rooted-navigations", rootPage2RootedNavigationsMenu.getName());        
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
        assertNotNull(rootFolder1View.getFolders());
        assertEquals(1, rootFolder1View.getFolders().size());
        assertEquals("folder1", rootFolder1View.getName());
        assertEquals("group folder1", rootFolder1View.getTitle());
        assertEquals("/_user/user/folder1", extractFileSystemPathFromId(rootFolder1View.getId()));
        assertEquals("/_user/user", aggregateView.getUserFolderPath());
        assertEquals("/", aggregateView.getBaseFolderPath());

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
        Map<String,ProfileLocator> locators = new HashMap<String,ProfileLocator>();
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
        assertEquals("/_user/user", profileView.getUserFolderPath());
        assertEquals("/", profileView.getBaseFolderPath());
        
        // test physical SiteView
        PhysicalSiteView basePhysicalView = new PhysicalSiteView(pageManager, "user");
        rootFolderView = basePhysicalView.getRootFolderView();
        assertNotNull(rootFolderView);
        assertEquals("/", rootFolderView.getName());
        assertEquals("root", rootFolderView.getTitle());
        assertEquals("/", extractFileSystemPathFromId(rootFolderView.getId()));
        assertEquals(11, rootFolderView.getFolders().size());
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
        assertEquals("folder4", ((Folder)foldersIter.next()).getName());
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
        assertEquals("/_user/user", basePhysicalView.getUserFolderPath());
        assertEquals("/", basePhysicalView.getBaseFolderPath());
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
        assertEquals("/_hostname/dash/_user/joe", profileView.getUserFolderPath());
        assertEquals("/_hostname/dash", profileView.getBaseFolderPath());
        
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("hostname", true, false, "new-host");
        locator.add("user", true, false, "new-user");
        locator.add("page", false, false, "home");
        profileView = new SearchPathsSiteView(pageManager, locator, false);
        assertEquals("/", profileView.getSearchPathsString());
        assertEquals("/_hostname/new-host/_user/new-user", profileView.getUserFolderPath());
        assertEquals("/_hostname/new-host", profileView.getBaseFolderPath());
        
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
        assertEquals("/_hostname/dash/_user/joe", profileView.getUserFolderPath());
        assertEquals("/_hostname/dash", profileView.getBaseFolderPath());
  
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
        assertEquals("/__subsite-root/_hostname/localhost/_user/sublocal", profileView.getUserFolderPath());
        assertEquals("/__subsite-root/_hostname/localhost", profileView.getBaseFolderPath());
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
        Map<String,ProfileLocator> locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertFalse(requestContext.isContentPage());
        BaseConcretePageElement requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page2.psml", requestPageView.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageView.getId()));
        PageTemplate requestPageTemplateView = requestContext.getPageTemplate();
        assertNotNull(requestPageTemplateView);
        assertEquals("template.tpsml", requestPageTemplateView.getName());
        assertEquals("/template.tpsml", extractFileSystemPathFromId(requestPageTemplateView.getId()));
        Map<String,FragmentDefinition> requestFragmentDefinitionViews = requestContext.getFragmentDefinitions();
        assertNotNull(requestFragmentDefinitionViews);
        assertEquals(2, requestFragmentDefinitionViews.size());
        FragmentDefinition requestFragmentDefinitionView0 = requestFragmentDefinitionViews.get("fake-fragment-definition-0");
        assertNotNull(requestFragmentDefinitionView0);
        assertEquals("definition0.fpsml", requestFragmentDefinitionView0.getName());
        assertEquals("/definition0.fpsml", extractFileSystemPathFromId(requestFragmentDefinitionView0.getId()));
        FragmentDefinition requestFragmentDefinitionView1 = requestFragmentDefinitionViews.get("fake-fragment-definition-1");
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
        assertEquals(4, requestSiblingFolderViews.size());
        assertNotNull(requestSiblingFolderViews.get("folder0"));
        assertEquals("/folder0", extractFileSystemPathFromId(requestSiblingFolderViews.get("folder0").getId()));
        assertNotNull(requestSiblingFolderViews.get("folder1"));
        assertEquals("/_user/user/folder1", extractFileSystemPathFromId(requestSiblingFolderViews.get("folder1").getId()));
        assertNotNull(requestSiblingFolderViews.get("folder2"));
        assertEquals("/folder2", extractFileSystemPathFromId(requestSiblingFolderViews.get("folder2").getId()));
        assertNotNull(requestSiblingFolderViews.get("folder4"));
        assertEquals("/folder4", extractFileSystemPathFromId(requestSiblingFolderViews.get("folder4").getId()));
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
        assertEquals("/_user/user", requestContext.getUserFolderPath());
        assertEquals("/", requestContext.getBaseFolderPath());

        // physical site setup
        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        requestContext = sessionContext.newRequestContext("/", null, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertFalse(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
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
        assertEquals(9, requestSiblingFolderViews.size());
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
        assertNotNull(requestSiblingFolderViews.get("folder4"));
        assertEquals("/folder4", extractFileSystemPathFromId(requestSiblingFolderViews.get("folder4").getId()));
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
        assertEquals("/_user/user", requestContext.getUserFolderPath());
        assertEquals("/", requestContext.getBaseFolderPath());        
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
        Map<String,ProfileLocator> locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertFalse(requestContext.isContentPage());
        BaseConcretePageElement requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page2.psml", requestPageView.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageView.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, null);
        locator.add("user", true, false, "user");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertFalse(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page2.psml", requestPageView.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageView.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "page1");
        locator.add("user", true, false, "user");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertFalse(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page1.psml", requestPageView.getName());
        assertEquals("/page1.psml", extractFileSystemPathFromId(requestPageView.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "folder1/");
        locator.add("user", true, false, "user");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertFalse(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page0.psml", requestPageView.getName());
        assertEquals("/_user/user/folder1/page0.psml", extractFileSystemPathFromId(requestPageView.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "/folder0/");
        locator.add("user", true, false, "user");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertFalse(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page0.psml", requestPageView.getName());
        assertEquals("/folder0/page0.psml", extractFileSystemPathFromId(requestPageView.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "/folder3/default-folder0/");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, null);
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertFalse(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page1.psml", requestPageView.getName());
        assertEquals("/folder3/default-folder1/page1.psml", extractFileSystemPathFromId(requestPageView.getId()));

        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/_user/user/page2.psml");
        locator.add("user", true, false, "admin");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "admin", true, true, true, false);
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertFalse(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page2.psml", requestPageView.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageView.getId()));

        // physical site requests
        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        requestContext = sessionContext.newRequestContext("/", null, null);
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertFalse(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page0.psml", requestPageView.getName());
        assertEquals("/page0.psml", extractFileSystemPathFromId(requestPageView.getId()));

        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        requestContext = sessionContext.newRequestContext("/_user/user/page2.psml", null, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertFalse(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
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
        Map<String,ProfileLocator> locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("role", true, false, "role0");
        locators.put("role", locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("group", true, false, "group");
        locators.put("group", locator);
        PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        Set<String> customMenuNames = requestContext.getCustomMenuNames();
        assertNotNull(customMenuNames);
        assertEquals(7, customMenuNames.size());
        assertTrue(customMenuNames.contains("top"));
        assertTrue(customMenuNames.contains("bread-crumbs"));
        assertTrue(customMenuNames.contains("template-test"));
        assertTrue(customMenuNames.contains("current-page-test"));
        assertTrue(customMenuNames.contains("current-path-test"));
        assertTrue(customMenuNames.contains("site-navigations"));
        assertTrue(customMenuNames.contains("rooted-navigations"));
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
        List<MenuElement> topMenuElements = topMenu.getElements();
        assertNotNull(topMenuElements);
        assertEquals(8, topMenuElements.size());
        for (MenuElement element : topMenuElements)
        {
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
                List<MenuElement> elements = ((Menu)element).getElements();
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
                List<MenuElement> elements = ((Menu)element).getElements();
                assertNotNull(elements);
                assertEquals(3, elements.size());
                assertTrue(elements.get(0) instanceof MenuOption);
                assertEquals("folder1/folder", ((MenuOption)elements.get(0)).getTitle());
                assertTrue(elements.get(1) instanceof MenuOption);
                assertEquals("/folder1/page0.psml", ((MenuOption)elements.get(1)).getTitle());
                assertTrue(elements.get(2) instanceof MenuOption);
                assertEquals("/folder1/page1.psml", ((MenuOption)elements.get(2)).getTitle());
            }
            else if (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("Folder4"))
            {
                assertTrue(element instanceof Menu);
                assertEquals("/folder4", ((Menu)element).getUrl());
                assertTrue(((Menu)element).getParentMenu() == topMenu);
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder4", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/folder4", element.getManagedNode().getPath());
                assertFalse(((Menu)element).isEmpty());
                List<MenuElement> elements = ((Menu)element).getElements();
                assertNotNull(elements);
                assertEquals(1, elements.size());
                assertTrue(elements.get(0) instanceof MenuOption);
                assertEquals("Folder", ((MenuOption)elements.get(0)).getTitle());
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
        List<MenuElement> breadCrumbsElements = breadCrumbsMenu.getElements();
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
        List<MenuElement> templateTestElements = templateTestMenu.getElements();
        assertNotNull(templateTestElements);
        assertEquals(1, templateTestElements.size());
        assertTrue(templateTestElements.get(0) instanceof MenuOption);
        assertEquals("/page1.psml", ((MenuOption)templateTestElements.get(0)).getUrl());
        Menu currentPageTestMenu = requestContext.getMenu("current-page-test");
        assertEquals("current-page-test", currentPageTestMenu.getName());
        assertFalse(currentPageTestMenu.isEmpty());
        List<MenuElement> currentPageTestElements = currentPageTestMenu.getElements();
        assertNotNull(currentPageTestElements);
        assertEquals(1, currentPageTestElements.size());
        assertTrue(currentPageTestElements.get(0) instanceof MenuOption);
        assertEquals("/page2.psml", ((MenuOption)currentPageTestElements.get(0)).getUrl());
        assertTrue(currentPageTestMenu.isSelected(requestContext));
        assertTrue(((MenuOption)currentPageTestElements.get(0)).isSelected(requestContext));
        Menu currentPathTestMenu = requestContext.getMenu("current-path-test");
        assertEquals("current-path-test", currentPathTestMenu.getName());
        assertTrue(currentPathTestMenu.isEmpty());
        List<MenuElement> currentPathTestElements = currentPathTestMenu.getElements();
        assertNull(currentPathTestElements);
        Menu siteNavigationsMenu = requestContext.getMenu("site-navigations");
        assertEquals("site-navigations", siteNavigationsMenu.getName());
        assertFalse(siteNavigationsMenu.isEmpty());
        List<MenuElement> siteNavigationsElements = siteNavigationsMenu.getElements();
        assertNotNull(siteNavigationsElements);
        assertEquals(6, siteNavigationsElements.size());
        for (MenuElement element : siteNavigationsElements)
        {
            if (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("folder0"))
            {
                assertTrue(element instanceof Menu);
                assertEquals("/folder0", ((Menu)element).getUrl());
                assertTrue(((Menu)element).getParentMenu() == siteNavigationsMenu);
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder0", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/folder0", element.getManagedNode().getPath());
                assertFalse(((Menu)element).isEmpty());
                List<MenuElement> elements = ((Menu)element).getElements();
                assertNotNull(elements);
                assertEquals(1, elements.size());
                assertTrue(elements.get(0) instanceof MenuOption);
                assertEquals("/folder0/page0.psml", ((MenuOption)elements.get(0)).getUrl());
            }
            else if (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("group folder1"))
            {
                assertTrue(element instanceof Menu);
                assertEquals("/folder1", ((Menu)element).getUrl());
                assertTrue(((Menu)element).getParentMenu() == siteNavigationsMenu);
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder1", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/_user/user/folder1", element.getManagedNode().getPath());
                assertFalse(((Menu)element).isEmpty());
                List<MenuElement> elements = ((Menu)element).getElements();
                assertNotNull(elements);
                assertEquals(3, elements.size());
                assertTrue(elements.get(0) instanceof Menu);
                assertEquals("folder1/folder", ((Menu)elements.get(0)).getTitle());
                assertTrue(elements.get(1) instanceof MenuOption);
                assertEquals("/folder1/page0.psml", ((MenuOption)elements.get(1)).getTitle());
                assertTrue(elements.get(2) instanceof MenuOption);
                assertEquals("/folder1/page1.psml", ((MenuOption)elements.get(2)).getTitle());
            }
            else if (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("Folder4"))
            {
                assertTrue(element instanceof Menu);
                assertEquals("/folder4", ((Menu)element).getUrl());
                assertTrue(((Menu)element).getParentMenu() == siteNavigationsMenu);
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder4", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/folder4", element.getManagedNode().getPath());
                Menu menuElement = (Menu)element;
                assertFalse(menuElement.isEmpty());
                List<MenuElement> elements = menuElement.getElements();
                assertNotNull(elements);
                assertEquals(1, elements.size());
                assertTrue(elements.get(0) instanceof Menu);
                Menu subFolderMenuElement = (Menu)elements.get(0);
                assertEquals("Folder", subFolderMenuElement.getTitle());
                elements = subFolderMenuElement.getElements();
                assertNotNull(elements);
                assertEquals(1, elements.size());
                assertTrue(elements.get(0) instanceof MenuOption);
                MenuOption subFolderMenuOptionElement = (MenuOption)elements.get(0);
                assertEquals("/folder4/folder/page0.psml", subFolderMenuOptionElement.getUrl());
                assertEquals(MenuOption.PAGE_OPTION_TYPE, subFolderMenuOptionElement.getType());
                assertTrue(subFolderMenuOptionElement.getNode() instanceof Page);
                assertEquals("/folder4/folder/page0.psml", subFolderMenuOptionElement.getNode().getPath());
                assertTrue(subFolderMenuOptionElement.getManagedNode() instanceof Page);
                assertEquals("/folder4/folder/page0.psml", subFolderMenuOptionElement.getManagedNode().getPath());
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
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }
        }
        Menu rootedMenu = requestContext.getMenu("rooted-navigations");
        assertNotNull(rootedMenu);
        assertFalse(rootedMenu.isEmpty());
        List<MenuElement> rootedElements = rootedMenu.getElements();
        assertNotNull(rootedElements);
        assertEquals(8, rootedElements.size());
        for (MenuElement element : rootedElements)
        {
            if ((element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("folder0")) ||
                (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("group folder1")) ||
                (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("Folder4")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page2.psml")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page1.psml")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page0.psml")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link0.link")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link1.link")))
            {
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }
        }
        
        // second request at /folder0
        locator = new JetspeedProfileLocator();
        locator.init(null, "/folder0");
        locator.add("user", true, false, "user");
        locator.add("mediatype", true, false, "html");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/folder0");
        locator.add("role", true, false, "role0");
        locators.put("role", locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/folder0");
        locator.add("group", true, false, "group");
        locators.put("group", locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
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
        siteNavigationsMenu = requestContext.getMenu("site-navigations");
        assertEquals("site-navigations", siteNavigationsMenu.getName());
        
        // third request at /page1.psml
        locator = new JetspeedProfileLocator();
        locator.init(null, "/page1.psml");
        locator.add("user", true, false, "user");
        locator.add("mediatype", true, false, "html");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/page1.psml");
        locator.add("role", true, false, "role0");
        locators.put("role", locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/page1.psml");
        locator.add("group", true, false, "group");
        locators.put("group", locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
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
        List<MenuElement> navigationsElements = navigationsMenu.getElements();
        assertNotNull(navigationsElements);
        assertEquals(7, navigationsElements.size());
        for (MenuElement element : navigationsElements)
        {
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
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("Folder4"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder4", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/folder4", element.getManagedNode().getPath());
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
        List<MenuElement> pagesElements = pagesMenu.getElements();
        assertNotNull(pagesElements);
        assertEquals(3, pagesElements.size());
        for (MenuElement element : pagesElements)
        {
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
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/page0.psml");
        locator.add("role", true, false, "role0");
        locators.put("role", locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/page0.psml");
        locator.add("group", true, false, "group");
        locators.put("group", locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        Menu templateTestMenu2 = requestContext.getMenu("template-test");
        assertNotNull(templateTestMenu2);
        assertEquals("template-test", templateTestMenu2.getName());
        assertFalse(templateTestMenu2.isEmpty());
        List<MenuElement> templateTestElements2 = templateTestMenu2.getElements();
        assertNotNull(templateTestElements2);
        assertEquals(1, templateTestElements2.size());
        assertTrue(templateTestElements2.get(0) instanceof MenuOption);
        assertEquals("/page2.psml", ((MenuOption)templateTestElements2.get(0)).getUrl());

        // new request at /folder1
        locator = new JetspeedProfileLocator();
        locator.init(null, "/folder1");
        locator.add("user", true, false, "user");
        locator.add("mediatype", true, false, "html");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/folder1");
        locator.add("group", true, false, "group");
        locators.put("group", locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
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
        List<MenuElement> customElements = customMenu.getElements();
        assertNotNull(customElements);
        assertEquals(12, customElements.size());
        assertEquals("custom", customMenu.getName());
        assertEquals("Top Menu", customMenu.getTitle());
        assertEquals("Haut", customMenu.getTitle(Locale.FRENCH));
        Iterator<MenuElement> menuElementsIter = customElements.iterator();
        for (int i = 0; ((i < 2) && menuElementsIter.hasNext()); i++)
        {
            MenuElement element = menuElementsIter.next();
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
            MenuElement element = menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("user root"))
            {
                assertFalse(((Menu)element).isEmpty());
                List<MenuElement> nestedElements = ((Menu)element).getElements();
                assertEquals(6, nestedElements.size());
                Iterator<MenuElement> nestedElementsIter = nestedElements.iterator();
                if (nestedElementsIter.hasNext())
                {
                    MenuElement nestedElement = nestedElementsIter.next();
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
                    MenuElement nestedElement = nestedElementsIter.next();
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
                    MenuElement nestedElement = nestedElementsIter.next();
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
                    MenuElement nestedElement = nestedElementsIter.next();
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
            MenuElement element = menuElementsIter.next();
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
            MenuElement element = menuElementsIter.next();
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
            MenuElement element = menuElementsIter.next();
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
            MenuElement element = menuElementsIter.next();
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
            MenuElement element = menuElementsIter.next();
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
            MenuElement element = menuElementsIter.next();
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
        rootedMenu = requestContext.getMenu("rooted-navigations");
        assertNotNull(rootedMenu);
        assertFalse(rootedMenu.isEmpty());
        rootedElements = rootedMenu.getElements();
        assertNotNull(rootedElements);
        assertEquals(3, rootedElements.size());
        for (MenuElement element : rootedElements)
        {
            if (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("folder1/folder"))
            {
                assertTrue(element instanceof Menu);
                assertEquals("/folder1/folder", ((Menu)element).getUrl());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/folder1/page0.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/folder1/page0.psml", ((MenuOption)element).getUrl());
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

        // new request at /folder1/folder/page0.psml
        locator = new JetspeedProfileLocator();
        locator.init(null, "/folder1/folder/page0.psml");
        locator.add("user", true, false, "user");
        locator.add("mediatype", true, false, "html");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        locator = new JetspeedProfileLocator();
        locator.init(null, "/folder1");
        locator.add("group", true, false, "group");
        locators.put("group", locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        rootedMenu = requestContext.getMenu("rooted-navigations");
        assertNotNull(rootedMenu);
        assertFalse(rootedMenu.isEmpty());
        rootedElements = rootedMenu.getElements();
        assertNotNull(rootedElements);
        assertEquals(3, rootedElements.size());
        menuElementsIter = rootedElements.iterator();        
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if ((element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("folder1/folder")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/folder1/page0.psml")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/folder1/page1.psml")))
            {
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }            
        }

        // physical site menus
        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        requestContext = sessionContext.newRequestContext("/", null, "user");
        assertNotNull(requestContext);
        customMenuNames = requestContext.getCustomMenuNames();
        assertNotNull(customMenuNames);
        assertEquals(6, customMenuNames.size());
        assertTrue(customMenuNames.contains("bread-crumbs"));
        assertTrue(customMenuNames.contains("template-test"));
        assertTrue(customMenuNames.contains("current-page-test"));
        assertTrue(customMenuNames.contains("current-path-test"));
        assertTrue(customMenuNames.contains("site-navigations"));
        assertTrue(customMenuNames.contains("rooted-navigations"));
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
        assertEquals(7, navigationsElements.size());
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
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("folder1"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder1", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/folder1", element.getManagedNode().getPath());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("Folder4"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
                assertTrue(element.getNode() instanceof Folder);
                assertEquals("/folder4", element.getNode().getPath());
                assertTrue(element.getManagedNode() instanceof Folder);
                assertEquals("/folder4", element.getManagedNode().getPath());
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
        rootedMenu = requestContext.getMenu("rooted-navigations");
        assertNotNull(rootedMenu);
        assertFalse(rootedMenu.isEmpty());
        rootedElements = rootedMenu.getElements();
        assertNotNull(rootedElements);
        assertEquals(8, rootedElements.size());
        menuElementsIter = rootedElements.iterator();
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if ((element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("folder0")) ||
                (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("folder1")) ||
                (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("Folder4")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page2.psml")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page1.psml")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/page0.psml")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link0.link")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link1.link")))
            {
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }
        }

        requestContext = sessionContext.newRequestContext("/folder1", null, "user");
        assertNotNull(requestContext);
        rootedMenu = requestContext.getMenu("rooted-navigations");
        assertNotNull(rootedMenu);
        assertFalse(rootedMenu.isEmpty());
        rootedElements = rootedMenu.getElements();
        assertNotNull(rootedElements);
        assertEquals(2, rootedElements.size());
        menuElementsIter = rootedElements.iterator();        
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if (element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("folder1/folder"))
            {
                assertTrue(element instanceof Menu);
                assertEquals("/folder1/folder", ((Menu)element).getUrl());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/folder1/page0.psml"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("/folder1/page0.psml", ((MenuOption)element).getUrl());
                assertEquals(MenuOption.PAGE_OPTION_TYPE, ((MenuOption)element).getType());
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }            
        }

        requestContext = sessionContext.newRequestContext("/folder1/folder/page0.psml", null, "user");
        assertNotNull(requestContext);
        rootedMenu = requestContext.getMenu("rooted-navigations");
        assertNotNull(rootedMenu);
        assertFalse(rootedMenu.isEmpty());
        rootedElements = rootedMenu.getElements();
        assertNotNull(rootedElements);
        assertEquals(2, rootedElements.size());
        menuElementsIter = rootedElements.iterator();        
        while (menuElementsIter.hasNext())
        {
            MenuElement element = (MenuElement)menuElementsIter.next();
            if ((element.getElementType().equals(MenuElement.MENU_ELEMENT_TYPE) && element.getTitle().equals("folder1/folder")) ||
                (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/folder1/page0.psml")))
            {
            }
            else
            {
                fail("Unexpected menu element type/title: "+element.getElementType()+"/"+element.getTitle());
            }            
        }
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
        Map<String,ProfileLocator> locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        Menu topMenu = requestContext.getMenu("top");
        assertNotNull(topMenu);
        assertFalse(topMenu.isEmpty());
        List<MenuElement> topMenuElements = topMenu.getElements();
        assertNotNull(topMenuElements);
        boolean hiddenElement = false;
        for (MenuElement element : topMenuElements)
        {
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/hidden.psml"))
            {
                hiddenElement = true;
            }
        }
        assertFalse(hiddenElement);
        Menu pagesMenu = requestContext.getMenu("pages");
        assertNotNull(pagesMenu);
        assertFalse(pagesMenu.isEmpty());
        List<MenuElement> pagesElements = pagesMenu.getElements();
        assertNotNull(pagesElements);
        hiddenElement = false;
        for (MenuElement element : pagesElements)
        {
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
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        topMenu = requestContext.getMenu("top");
        assertNotNull(topMenu);
        assertFalse(topMenu.isEmpty());
        topMenuElements = topMenu.getElements();
        assertNotNull(topMenuElements);
        hiddenElement = false;
        for (MenuElement element : topMenuElements)
        {
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
        hiddenElement = false;
        for (MenuElement element : pagesElements)
        {
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
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        topMenu = requestContext.getMenu("top");
        assertNotNull(topMenu);
        assertFalse(topMenu.isEmpty());
        topMenuElements = topMenu.getElements();
        assertNotNull(topMenuElements);
        hiddenElement = false;
        for (MenuElement element : topMenuElements)
        {
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
        hiddenElement = false;
        for (MenuElement element : pagesElements)
        {
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
        requestContext = sessionContext.newRequestContext("/", null, null);
        assertNotNull(requestContext);
        pagesMenu = requestContext.getMenu("pages");
        assertNotNull(pagesMenu);
        assertFalse(pagesMenu.isEmpty());
        pagesElements = pagesMenu.getElements();
        assertNotNull(pagesElements);
        hiddenElement = false;
        for (MenuElement element : pagesElements)
        {
            if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/hidden.psml"))
            {
                hiddenElement = true;
            }
        }
        assertFalse(hiddenElement);
    
        // second request at /hidden.psml: hidden page visible
        requestContext = sessionContext.newRequestContext("/hidden.psml", null, null);
        assertNotNull(requestContext);
        pagesMenu = requestContext.getMenu("pages");
        assertNotNull(pagesMenu);
        assertFalse(pagesMenu.isEmpty());
        pagesElements = pagesMenu.getElements();
        assertNotNull(pagesElements);
        hiddenElement = false;
        for (MenuElement element : pagesElements)
        {
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
        Map<String,ProfileLocator> locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertTrue(requestContext.isContentPage());
        BaseConcretePageElement requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("docpage.dpsml", requestPageView.getName());
        assertEquals("/docpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/contentfolder/document.doc");
        locator.add("user", true, false, "user");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertTrue(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("matchdocpage.dpsml", requestPageView.getName());
        assertEquals("/contentfolder/matchdocpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/contentfolder/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/contentfolder/folder/document.doc");
        locator.add("user", true, false, "user");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertTrue(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("docpage.dpsml", requestPageView.getName());
        assertEquals("/contentfolder/docpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/contentfolder/folder/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/contentfolder/document.txt");
        locator.add("user", true, false, "user");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertTrue(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("contentpage.dpsml", requestPageView.getName());
        assertEquals("/contentpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/contentfolder/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/preview/document.doc");
        locator.add("user", true, false, "user");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertTrue(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("docpage.dpsml", requestPageView.getName());
        assertEquals("/docpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/preview/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/preview/contentfolder/draft/document.doc", "test.domain.com");
        locator.add("user", true, false, "user");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertTrue(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("docpage.dpsml", requestPageView.getName());
        assertEquals("/contentfolder/pub/docpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/preview/contentfolder/draft/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/document.psml");
        locator.add("user", true, false, "user");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertTrue(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("contentpage.dpsml", requestPageView.getName());
        assertEquals("/contentpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/document", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/preview/folder0/page0.doc");
        locator.add("user", true, false, "user");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertTrue(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page0.psml", requestPageView.getName());
        assertEquals("/folder0/page0.psml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/preview/folder0/page0", requestContext.getPageContentPath());

        locator = new JetspeedProfileLocator();
        locator.init(null, "/page2.doc");
        locator.add("user", true, false, "user");
        locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators, "user");
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertTrue(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof Page);
        assertEquals("page2.psml", requestPageView.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/page2", requestContext.getPageContentPath());

        // physical site view
        sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        requestContext = sessionContext.newRequestContext("/document.doc", null, null);
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertTrue(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("docpage.dpsml", requestPageView.getName());
        assertEquals("/docpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/document", requestContext.getPageContentPath());

        requestContext = sessionContext.newRequestContext("/preview/contentfolder/draft/document.doc", "test.domain.com", null);
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertTrue(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("docpage.dpsml", requestPageView.getName());
        assertEquals("/contentfolder/pub/docpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/preview/contentfolder/draft/document", requestContext.getPageContentPath());

        requestContext = sessionContext.newRequestContext("/document.psml", null, null);
        assertNotNull(requestContext);
        assertTrue(requestContext.isConcretePage());
        assertTrue(requestContext.isContentPage());
        requestPageView = (BaseConcretePageElement)requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("contentpage.dpsml", requestPageView.getName());
        assertEquals("/contentpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNotNull(requestContext.getPageContentPath());
        assertEquals("/document", requestContext.getPageContentPath());
    }

    /**
     * testPortalSiteTemplateRequests - Test PortalSite template request mappings
     *
     * @throws Exception
     */
    public void testPotalSiteTemplateRequests() throws Exception
    {
        assertNotNull(portalSite);

        // search path site view
        PortalSiteSessionContext sessionContext = portalSite.newSessionContext();
        assertNotNull(sessionContext);
        JetspeedProfileLocator locator = new JetspeedProfileLocator();
        locator.init(null, "/contentpage.dpsml");
        locator.add("admin", true, false, "admin");
        Map<String,ProfileLocator> locators = new HashMap<String,ProfileLocator>();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        PortalSiteRequestContext requestContext = sessionContext.newRequestContext(locators, "admin", true, true, true, true);
        assertNotNull(requestContext);
        assertFalse(requestContext.isConcretePage());
        assertFalse(requestContext.isContentPage());
        BaseFragmentsElement requestPageView = requestContext.getPageOrTemplate();
        assertNotNull(requestPageView);
        assertTrue(requestPageView instanceof DynamicPage);
        assertEquals("contentpage.dpsml", requestPageView.getName());
        assertEquals("/contentpage.dpsml", extractFileSystemPathFromId(requestPageView.getId()));
        assertNull(requestContext.getPageContentPath());
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
     * @param view site view node view
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
