/*
 * Copyright 2000-2005 The Apache Software Foundation.
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
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.proxy.NodeProxy;
import org.apache.jetspeed.portalsite.impl.MenuImpl;
import org.apache.jetspeed.portalsite.view.SiteView;
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

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        this.pageManager = (PageManager) ctx.getBean("pageManager");
        this.portalSite = (PortalSite) ctx.getBean("portalSite");
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
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
        return new String[] {"/META-INF/test-spring.xml"};
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
    }

    /**
     * testSiteView - Test SiteView operation
     *
     * @throws Exception
     */
    public void testSiteView() throws Exception
    {
        // test degenerate SiteView
        SiteView baseView = new SiteView(pageManager);
        assertEquals("/", baseView.getSearchPathsString());
        Folder rootFolderProxy = baseView.getRootFolderProxy();
        assertNotNull(rootFolderProxy);
        assertEquals("/", rootFolderProxy.getName());
        assertEquals("root", rootFolderProxy.getTitle());
        assertEquals("/", extractFileSystemPathFromId(rootFolderProxy.getId()));
        assertEquals(3, rootFolderProxy.getFolders().size());
        Iterator foldersIter = rootFolderProxy.getFolders().iterator();
        assertEquals("folder0", ((Folder)foldersIter.next()).getName());
        assertEquals("folder1", ((Folder)foldersIter.next()).getName());
        assertEquals("folder2", ((Folder)foldersIter.next()).getName());
        assertEquals(3, rootFolderProxy.getPages().size());
        Iterator pagesIter = rootFolderProxy.getPages().iterator();
        assertEquals("page2.psml", ((Page)pagesIter.next()).getName());
        assertEquals("page1.psml", ((Page)pagesIter.next()).getName());
        assertEquals("page0.psml", ((Page)pagesIter.next()).getName());
        assertEquals(2, rootFolderProxy.getLinks().size());
        Iterator linksIter = rootFolderProxy.getLinks().iterator();
        assertEquals("link1.link", ((Link)linksIter.next()).getName());
        assertEquals("link0.link", ((Link)linksIter.next()).getName());
        Page rootPage0Proxy = rootFolderProxy.getPage("page0.psml");
        assertNotNull(rootPage0Proxy);
        assertEquals(rootFolderProxy, rootPage0Proxy.getParent());
        assertEquals("page0.psml", rootPage0Proxy.getName());
        assertEquals("/page0.psml", extractFileSystemPathFromId(rootPage0Proxy.getId()));
        Link rootLink0Proxy = rootFolderProxy.getLink("link0.link");
        assertNotNull(rootLink0Proxy);
        assertEquals(rootFolderProxy, rootLink0Proxy.getParent());
        assertEquals("link0.link", rootLink0Proxy.getName());
        assertEquals("/link0.link", extractFileSystemPathFromId(rootLink0Proxy.getId()));
        Folder rootFolder0Proxy = rootFolderProxy.getFolder("folder0");
        assertNotNull(rootFolder0Proxy);
        assertEquals(rootFolderProxy, rootFolder0Proxy.getParent());
        assertEquals(1, rootFolder0Proxy.getPages().size());
        assertEquals(null, rootFolder0Proxy.getLinks());
        assertEquals("folder0", rootFolder0Proxy.getName());
        assertEquals("/folder0", extractFileSystemPathFromId(rootFolder0Proxy.getId()));
        Page folder0Page0Proxy = rootFolder0Proxy.getPage("page0.psml");
        assertNotNull(folder0Page0Proxy);
        assertEquals(rootFolder0Proxy, folder0Page0Proxy.getParent());
        assertEquals("page0.psml", folder0Page0Proxy.getName());
        assertEquals("/folder0/page0.psml", extractFileSystemPathFromId(folder0Page0Proxy.getId()));

        // test SiteView access by path
        Folder rootFolderProxyByPath = (Folder)baseView.getNodeProxy("/", null, false, false);
        assertNotNull(rootFolderProxyByPath);
        assertEquals(rootFolderProxy, rootFolderProxyByPath);
        Folder rootFolder0ProxyByPath = (Folder)baseView.getNodeProxy("/folder0/", null, false, false);
        assertNotNull(rootFolder0ProxyByPath);
        assertEquals(rootFolder0Proxy, rootFolder0ProxyByPath);
        Page folder0Page0ProxyByPath = (Page)baseView.getNodeProxy("/folder0/page0.psml", null, false, false);
        assertNotNull(folder0Page0ProxyByPath);
        assertEquals(folder0Page0Proxy, folder0Page0ProxyByPath);
        folder0Page0ProxyByPath = (Page)baseView.getNodeProxy("page0.psml", rootFolder0Proxy, false, false);
        assertNotNull(folder0Page0ProxyByPath);
        assertEquals(folder0Page0Proxy, folder0Page0ProxyByPath);
        List rootPageProxiesByPath = baseView.getNodeProxies("/page?.psml", null, false, false);
        assertNotNull(rootPageProxiesByPath);
        assertEquals(3,rootPageProxiesByPath.size());
        assertTrue(rootPageProxiesByPath.contains(rootPage0Proxy));
        List rootFolderProxiesByPath = baseView.getNodeProxies("/*/", null, false, false);
        assertNotNull(rootFolderProxiesByPath);
        assertEquals(3,rootFolderProxiesByPath.size());
        assertTrue(rootFolderProxiesByPath.contains(rootFolder0Proxy));
        List folderPageProxiesByPath = baseView.getNodeProxies("*/p*[0-9].psml", rootFolderProxy, false, false);
        assertNotNull(folderPageProxiesByPath);
        assertEquals(2,folderPageProxiesByPath.size());
        assertTrue(folderPageProxiesByPath.contains(folder0Page0Proxy));

        // test aggregating SiteView
        SiteView aggregateView = new SiteView(pageManager, "/_user/user,/_role/role0,/_group/group,/");
        assertEquals("/_user/user,/_role/role0,/_group/group,/", aggregateView.getSearchPathsString());
        rootFolderProxy = aggregateView.getRootFolderProxy();
        assertNotNull(rootFolderProxy);
        assertEquals("/", rootFolderProxy.getName());
        assertEquals("user root", rootFolderProxy.getTitle());
        assertEquals("/_user/user", extractFileSystemPathFromId(rootFolderProxy.getId()));
        assertEquals(3, rootFolderProxy.getFolders().size());
        assertEquals(3, rootFolderProxy.getPages().size());
        assertEquals(2, rootFolderProxy.getLinks().size());
        rootPage0Proxy = rootFolderProxy.getPage("page0.psml");
        assertNotNull(rootPage0Proxy);
        assertEquals(rootFolderProxy, rootPage0Proxy.getParent());
        assertEquals("page0.psml", rootPage0Proxy.getName());
        assertEquals("/page0.psml", extractFileSystemPathFromId(rootPage0Proxy.getId()));
        List rootPage0ProxyMenus = rootPage0Proxy.getMenuDefinitions();
        assertNotNull(rootPage0ProxyMenus);
        assertEquals(2 + aggregateView.getStandardMenuNames().size(), rootPage0ProxyMenus.size());
        Iterator menusIter = rootPage0ProxyMenus.iterator();
        MenuDefinition rootPage0ProxyTopMenu = (MenuDefinition)menusIter.next();
        assertEquals("top", rootPage0ProxyTopMenu.getName());
        assertEquals("/", rootPage0ProxyTopMenu.getOptions());
        assertEquals(2, rootPage0ProxyTopMenu.getDepth());
        assertEquals("dhtml-pull-down", rootPage0ProxyTopMenu.getSkin());
        MenuDefinition rootPage0ProxyBreadCrumbMenu = (MenuDefinition)menusIter.next();
        assertEquals("bread-crumbs", rootPage0ProxyBreadCrumbMenu.getName());
        assertEquals("./", rootPage0ProxyBreadCrumbMenu.getOptions());
        assertEquals(true, rootPage0ProxyBreadCrumbMenu.isPaths());
        for (int i = 0; (i < aggregateView.getStandardMenuNames().size()); i++)
        {
            assertTrue(aggregateView.getStandardMenuNames().contains(((MenuDefinition)menusIter.next()).getName()));
        }
        Page rootPage2Proxy = rootFolderProxy.getPage("page2.psml");
        assertNotNull(rootPage2Proxy);
        assertEquals(rootFolderProxy, rootPage2Proxy.getParent());
        assertEquals("page2.psml", rootPage2Proxy.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(rootPage2Proxy.getId()));
        List rootPage2ProxyMenus = rootPage2Proxy.getMenuDefinitions();
        assertNotNull(rootPage2ProxyMenus);
        assertEquals(2 + aggregateView.getStandardMenuNames().size(), rootPage2ProxyMenus.size());
        menusIter = rootPage2ProxyMenus.iterator();
        MenuDefinition rootPage2ProxyTopMenu = (MenuDefinition)menusIter.next();
        assertEquals("top", rootPage2ProxyTopMenu.getName());
        assertEquals("/", rootPage2ProxyTopMenu.getOptions());
        assertEquals(1, rootPage2ProxyTopMenu.getDepth());
        MenuDefinition rootPage2ProxyBreadCrumbMenu = (MenuDefinition)menusIter.next();
        assertEquals("bread-crumbs", rootPage2ProxyBreadCrumbMenu.getName());
        for (int i = 0; (i < aggregateView.getStandardMenuNames().size()); i++)
        {
            assertTrue(aggregateView.getStandardMenuNames().contains(((MenuDefinition)menusIter.next()).getName()));
        }
        rootLink0Proxy = rootFolderProxy.getLink("link0.link");
        assertNotNull(rootLink0Proxy);
        assertEquals(rootFolderProxy, rootLink0Proxy.getParent());
        assertEquals("link0.link", rootLink0Proxy.getName());
        assertEquals("/_group/group/link0.link", extractFileSystemPathFromId(rootLink0Proxy.getId()));
        rootFolder0Proxy = rootFolderProxy.getFolder("folder0");
        assertNotNull(rootFolder0Proxy);
        assertEquals(rootFolderProxy, rootFolder0Proxy.getParent());
        assertEquals(1, rootFolder0Proxy.getPages().size());
        assertEquals(null, rootFolder0Proxy.getLinks());
        assertEquals(null, rootFolder0Proxy.getFolders());
        assertEquals("folder0", rootFolder0Proxy.getName());
        assertEquals("folder0", rootFolder0Proxy.getTitle());
        assertEquals("/folder0", extractFileSystemPathFromId(rootFolder0Proxy.getId()));
        folder0Page0Proxy = rootFolder0Proxy.getPage("page0.psml");
        assertNotNull(folder0Page0Proxy);
        assertEquals(rootFolder0Proxy, folder0Page0Proxy.getParent());
        assertEquals("page0.psml", folder0Page0Proxy.getName());
        assertEquals("/folder0/page0.psml", extractFileSystemPathFromId(folder0Page0Proxy.getId()));
        Folder rootFolder1Proxy = rootFolderProxy.getFolder("folder1");
        assertNotNull(rootFolder1Proxy);
        assertEquals(rootFolderProxy, rootFolder1Proxy.getParent());
        assertEquals(2, rootFolder1Proxy.getPages().size());
        assertEquals(null, rootFolder1Proxy.getLinks());
        assertEquals(null, rootFolder1Proxy.getFolders());
        assertEquals("folder1", rootFolder1Proxy.getName());
        assertEquals("group folder1", rootFolder1Proxy.getTitle());
        assertEquals("/_user/user/folder1", extractFileSystemPathFromId(rootFolder1Proxy.getId()));

        // test degenerate aggregating SiteView
        aggregateView = new SiteView(pageManager, "/__subsite-root");
        assertEquals("/__subsite-root", aggregateView.getSearchPathsString());
        rootFolderProxy = aggregateView.getRootFolderProxy();
        assertNotNull(rootFolderProxy);
        assertEquals("/", rootFolderProxy.getName());
        assertEquals("subsite root", rootFolderProxy.getTitle());
        assertEquals("/__subsite-root", extractFileSystemPathFromId(rootFolderProxy.getId()));
        assertEquals(null, rootFolderProxy.getFolders());
        assertEquals(1, rootFolderProxy.getPages().size());
        assertEquals(1, rootFolderProxy.getLinks().size());

        // test SiteView construction using profile locators
        JetspeedProfileLocator locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        SiteView profileView = new SiteView(pageManager, locator);
        assertEquals("/", profileView.getSearchPathsString());
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("user", true, false, "user");
        locator.add("page", false, false, "default-page");
        profileView = new SiteView(pageManager, locator);
        assertEquals("/_user/user,/", profileView.getSearchPathsString());
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "default-page");
        locator.add("user", true, false, "user");
        locator.add("mediatype", true, false, "html");
        locator.add("language", true, false, "en");
        locator.add("country", true, false, "US");
        profileView = new SiteView(pageManager, locator);
        assertEquals("/_user/user/_mediatype/html,/_user/user,/", profileView.getSearchPathsString());
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "default-page");
        locator.add("role", true, false, "role0");
        locator.add("role", true, false, "role1");
        profileView = new SiteView(pageManager, locator);
        assertEquals("/_role/role0,/_role/role1,/", profileView.getSearchPathsString());
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("user", true, false, "user");
        locator.add("navigation", false, true, "/");
        locator.add("role", true, false, "role0");
        locator.add("navigation", false, true, "/");
        locator.add("group", true, false, "group");
        locator.add("page", false, false, "default-page");
        profileView = new SiteView(pageManager, locator);
        assertEquals("/_user/user,/_role/role0,/_group/group,/", profileView.getSearchPathsString());
        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("navigation", false, true, "subsite-root");
        profileView = new SiteView(pageManager, locator);
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
        profileView = new SiteView(pageManager, locators);
        assertEquals("/_role/role0,/_role/role1,/_user/user,/_group/group,/", profileView.getSearchPathsString());
        rootFolderProxy = profileView.getRootFolderProxy();
        assertNotNull(rootFolderProxy);
        assertEquals("/", rootFolderProxy.getName());
        assertEquals("user root", rootFolderProxy.getTitle());
        assertEquals("/_role/role0", extractFileSystemPathFromId(rootFolderProxy.getId()));
        assertEquals(ProfileLocator.PAGE_LOCATOR, extractLocatorNameFromProxy(rootFolderProxy));
        rootPage2Proxy = rootFolderProxy.getPage("page2.psml");
        assertNotNull(rootPage2Proxy);
        assertEquals("page2.psml", rootPage2Proxy.getName());
        assertEquals("/_role/role0/page2.psml", extractFileSystemPathFromId(rootPage2Proxy.getId()));
        assertEquals(ProfileLocator.PAGE_LOCATOR, extractLocatorNameFromProxy(rootPage2Proxy));
        rootFolder1Proxy = rootFolderProxy.getFolder("folder1");
        assertNotNull(rootFolder1Proxy);
        assertEquals("folder1", rootFolder1Proxy.getName());
        assertEquals("group folder1", rootFolder1Proxy.getTitle());
        assertEquals("/_user/user/folder1", extractFileSystemPathFromId(rootFolder1Proxy.getId()));
        assertEquals("alternate-locator-name", extractLocatorNameFromProxy(rootFolder1Proxy));
        Page folder1Page1Proxy = rootFolder1Proxy.getPage("page1.psml");
        assertNotNull(folder1Page1Proxy);
        assertEquals("page1.psml", folder1Page1Proxy.getName());
        assertEquals("/_group/group/folder1/page1.psml", extractFileSystemPathFromId(folder1Page1Proxy.getId()));
        assertEquals("alternate-locator-name", extractLocatorNameFromProxy(folder1Page1Proxy));
    }

    /**
     * testPortalSiteSetup - Test PortalSite test configuration
     *
     * @throws Exception
     */
    public void testPotalSiteSetup() throws Exception
    {
        assertNotNull(portalSite);
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
        Page requestPageProxy = requestContext.getPage();
        assertNotNull(requestPageProxy);
        assertEquals("page2.psml", requestPageProxy.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageProxy.getId()));
        Folder requestFolderProxy = requestContext.getFolder();
        assertNotNull(requestFolderProxy);
        assertEquals("/", requestFolderProxy.getName());
        assertEquals("/_user/user", extractFileSystemPathFromId(requestFolderProxy.getId()));
        NodeSet requestSiblingPageProxies = requestContext.getSiblingPages();
        assertNotNull(requestSiblingPageProxies);
        assertEquals(3, requestSiblingPageProxies.size());
        assertNotNull(requestSiblingPageProxies.get("page0.psml"));
        assertEquals("/page0.psml", extractFileSystemPathFromId(requestSiblingPageProxies.get("page0.psml").getId()));
        assertNotNull(requestSiblingPageProxies.get("page1.psml"));
        assertEquals("/page1.psml", extractFileSystemPathFromId(requestSiblingPageProxies.get("page1.psml").getId()));
        assertNotNull(requestSiblingPageProxies.get("page2.psml"));
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestSiblingPageProxies.get("page2.psml").getId()));
        Folder requestParentFolderProxy = requestContext.getParentFolder();
        assertNull(requestParentFolderProxy);
        NodeSet requestSiblingFolderProxies = requestContext.getSiblingFolders();
        assertNotNull(requestSiblingFolderProxies);
        assertEquals(3, requestSiblingFolderProxies.size());
        assertNotNull(requestSiblingFolderProxies.get("folder0"));
        assertEquals("/folder0", extractFileSystemPathFromId(requestSiblingFolderProxies.get("folder0").getId()));
        assertNotNull(requestSiblingFolderProxies.get("folder1"));
        assertEquals("/_user/user/folder1", extractFileSystemPathFromId(requestSiblingFolderProxies.get("folder1").getId()));
        assertNotNull(requestSiblingFolderProxies.get("folder2"));
        assertEquals("/folder2", extractFileSystemPathFromId(requestSiblingFolderProxies.get("folder2").getId()));
        Folder requestRootFolderProxy = requestContext.getRootFolder();
        assertNotNull(requestRootFolderProxy);
        assertEquals("/", requestRootFolderProxy.getName());
        assertEquals("/_user/user", extractFileSystemPathFromId(requestRootFolderProxy.getId()));
        NodeSet requestRootLinkProxies = requestContext.getRootLinks();
        assertNotNull(requestRootLinkProxies);
        assertEquals(2, requestRootLinkProxies.size());
        assertNotNull(requestRootLinkProxies.get("link0.link"));
        assertEquals("/_group/group/link0.link", extractFileSystemPathFromId(requestRootLinkProxies.get("link0.link").getId()));
        assertNotNull(requestRootLinkProxies.get("link1.link"));
        assertEquals("/link1.link", extractFileSystemPathFromId(requestRootLinkProxies.get("link1.link").getId()));
    }

    /**
     * testPortalSiteRequests - Test PortalSite request path logic
     *
     * @throws Exception
     */
    public void testPotalSiteRequests() throws Exception
    {
        assertNotNull(portalSite);
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
        Page requestPageProxy = requestContext.getPage();
        assertNotNull(requestPageProxy);
        assertEquals("page2.psml", requestPageProxy.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageProxy.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, null);
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageProxy = requestContext.getPage();
        assertNotNull(requestPageProxy);
        assertEquals("page2.psml", requestPageProxy.getName());
        assertEquals("/_user/user/page2.psml", extractFileSystemPathFromId(requestPageProxy.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "page1");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageProxy = requestContext.getPage();
        assertNotNull(requestPageProxy);
        assertEquals("page1.psml", requestPageProxy.getName());
        assertEquals("/page1.psml", extractFileSystemPathFromId(requestPageProxy.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "folder1/");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageProxy = requestContext.getPage();
        assertNotNull(requestPageProxy);
        assertEquals("page0.psml", requestPageProxy.getName());
        assertEquals("/_user/user/folder1/page0.psml", extractFileSystemPathFromId(requestPageProxy.getId()));

        locator = new JetspeedProfileLocator();
        locator.init(null, "/");
        locator.add("page", false, false, "/folder0/");
        locator.add("user", true, false, "user");
        locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext = sessionContext.newRequestContext(locators);
        assertNotNull(requestContext);
        requestPageProxy = requestContext.getPage();
        assertNotNull(requestPageProxy);
        assertEquals("page0.psml", requestPageProxy.getName());
        assertEquals("/folder0/page0.psml", extractFileSystemPathFromId(requestPageProxy.getId()));
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
        assertEquals(2, customMenuNames.size());
        assertTrue(customMenuNames.contains("top"));
        assertTrue(customMenuNames.contains("bread-crumbs"));
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
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link1.link"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("http://link1", ((MenuOption)element).getUrl());
                assertEquals("top", ((MenuOption)element).getTarget());
                assertEquals(MenuOption.LINK_OPTION_TYPE, ((MenuOption)element).getType());
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link0.link"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals("http://link0", ((MenuOption)element).getUrl());
                assertNull(((MenuOption)element).getTarget());
                assertEquals("dhtml-pull-down", element.getSkin());
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
        assertNotNull(breadCrumbsMenu2);
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
        assertTrue(backMenu.isEmpty());
        assertNull(backMenu.getElements());
        assertTrue(((MenuImpl)backMenu).isElementRelative());
        Menu navigationsMenu = requestContext.getMenu("navigations");
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
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("group folder1"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.FOLDER_OPTION_TYPE, ((MenuOption)element).getType());
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
            }
            else if (element.getElementType().equals(MenuElement.OPTION_ELEMENT_TYPE) && element.getTitle().equals("/link0.link"))
            {
                assertTrue(element instanceof MenuOption);
                assertEquals(MenuOption.LINK_OPTION_TYPE, ((MenuOption)element).getType());
                assertEquals("left-navigations", element.getSkin());
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
     * extractLocatorNameFromProxy - utility to access profile locator name from proxy
     *
     * @param proxy site view node proxy
     * @return locator name
     */
    private String extractLocatorNameFromProxy(Object proxy) throws Exception
    {
        return ((NodeProxy)Proxy.getInvocationHandler(proxy)).getLocatorName();
    }
}
