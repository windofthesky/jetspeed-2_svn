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
package org.apache.jetspeed.page.cache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.cache.impl.EhCacheConfigResource;
import org.apache.jetspeed.om.page.FragmentProperty;
import org.apache.jetspeed.page.PageManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TestDatabasePageManagerCache
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class TestDatabasePageManagerCache extends TestCase
{
    protected static Logger log = LoggerFactory.getLogger(TestDatabasePageManagerCache.class);
    
    private static final long CACHE_NOTIFICATION_STARTUP_WAIT = 10000;
    private static final long CACHE_NOTIFICATION_WAIT = 2000;
    private static final long CACHE_NOTIFICATION_POLL = 250;
    private static final long CACHE_LOGGING_PUMP_WAIT = 50;
    
    // Members
    
    private String osExecutableExtension;
    private String fileSeparator;
    private File javaExecutablePath;
    private String classPathSeparator;
    private File projectDirectoryPath;
    private Map<String,String> systemProperties;
    private String classPath;
 
    // Test methods
    
    /**
     * Tests distributed cache operation for DatabasePageManager
     */
    public void testDatabasePageManagerCache()
    {
        String result;

        // check for distributed database support
        String databaseName = System.getProperty("org.apache.jetspeed.database.default.name");
        if ((databaseName != null) && databaseName.equals("derby"))
        {
            System.out.println("Database support not distributed: system limitation... test skipped");
            log.warn("Database support not distributed: system limitation... test skipped");
            return;
        }
        
        // create and start servers
        final TestProgram server0 = new TestProgram("server-0", DatabasePageManagerServer.class, 0);
        final TestProgram server1 = new TestProgram("server-1", DatabasePageManagerServer.class, 1);
        try
        {
            // start servers
            server0.start();
            server1.start();

            // wait until servers have started
            server0.execute("");
            server1.execute("");

            // check to ensure servers have distributed page manager caches
            boolean server0Distributed = false;
            boolean server1Distributed = false;
            final long distributedCheckStarted = System.currentTimeMillis();
            do
            {
                // check servers
                if (!server0Distributed)
                {
                    result = server0.execute("pageManager.isDistributed();");
                    assertTrue(!result.contains("Exception"));
                    server0Distributed = result.endsWith("true");
                }
                if (!server1Distributed)
                {
                    result = server1.execute("pageManager.isDistributed();");
                    assertTrue(!result.contains("Exception"));
                    server1Distributed = result.endsWith("true");
                }
                
                // wait if servers not distributed
                if (!server0Distributed || !server1Distributed)
                {
                    sleep(server0, server1, CACHE_NOTIFICATION_POLL);
                }
            }
            while ((!server0Distributed || !server1Distributed) && (System.currentTimeMillis()-distributedCheckStarted < CACHE_NOTIFICATION_STARTUP_WAIT));
            if (!server0Distributed && !server1Distributed)
            {                
                System.out.println("Server page managers not distributed: possible system limitation... test skipped");
                log.warn("Server page managers not distributed: possible system limitation... test skipped");
                return;
            }
            assertTrue(server0Distributed);
            assertTrue(server1Distributed);

            // clean and setup database page managers
            result = server0.execute("removeRootFolder = pageManager.getFolder(\"/\");");
            if (!result.contains("FolderNotFoundException"))
            {
                result = server0.execute("pageManager.removeFolder(removeRootFolder);");
                assertTrue(!result.contains("Exception"));
            }
            result = server0.execute("pageManager.reset();");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.reset();");
            assertTrue(!result.contains("Exception"));

            // login servers setting test user, group, and role principal names
            result = server0.execute("pageManagerServer.setUser(\"user\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManagerServer.setGroups(\"group\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManagerServer.setRoles(\"role\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManagerServer.setUser(\"user\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManagerServer.setGroups(\"group\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManagerServer.setRoles(\"role\");");
            assertTrue(!result.contains("Exception"));
            
            // reset request cache
            result = server0.execute("pageManager.cleanupRequestCache();");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.cleanupRequestCache();");
            assertTrue(!result.contains("Exception"));
            
            // create folders, documents, and properties in first page manager
            result = server0.execute("folder = pageManager.newFolder(\"/\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("folder.setTitle(\"Root Folder\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updateFolder(folder);");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page = pageManager.newPage(\"/default-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page.setTitle(\"Default Page\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment = page.getRootFragment();");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment.setTitle(\"Default Page Root Fragment\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment.setState(\"DEFAULT\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment.setProperty(\"CUSTOM\", null, null, \"CUSTOM\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updatePage(page);");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment.setState(\""+FragmentProperty.USER_PROPERTY_SCOPE+"\", null, \"USER\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment.setProperty(\"CUSTOM\", null, null, \"CUSTOM2\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updateFragmentProperties(fragment, \""+PageManager.ALL_PROPERTY_SCOPE+"\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page = pageManager.newPage(\"/another-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page.setTitle(\"Another Page\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment = page.getRootFragment();");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment.setTitle(\"Another Page Root Fragment\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment.setState(\"DEFAULT\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment.setProperty(\"CUSTOM\", null, null, \"CUSTOM\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updatePage(page);");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page = pageManager.newPage(\"/some-other-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page.setTitle(\"Some Other Page\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment = page.getRootFragment();");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment.setProperty(\"CUSTOM\", null, null, \"SOME-OTHER-CUSTOM\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updatePage(page);");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("link = pageManager.newLink(\"/default.link\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("link.setTitle(\"Default Link\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("link.setUrl(\"http://www.default.org/\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updateLink(link);");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("folder = pageManager.newFolder(\"/deep-0\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("folder.setTitle(\"Deep 0 Folder\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updateFolder(folder);");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page = pageManager.newPage(\"/deep-0/deep-page-0.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page.setTitle(\"Deep Page 0\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updatePage(page);");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("folder = pageManager.newFolder(\"/deep-1\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("folder.setTitle(\"Deep 1 Folder\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updateFolder(folder);");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page = pageManager.newPage(\"/deep-1/deep-page-1.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page.setTitle(\"Deep Page 1\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updatePage(page);");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageSecurity = pageManager.newPageSecurity();");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updatePageSecurity(pageSecurity);");
            assertTrue(!result.contains("Exception"));
            
            // wait for cache notifications to propagate between servers
            sleep(server0, server1, CACHE_NOTIFICATION_WAIT);

            // populate folders, documents, and properties in second page manager
            result = server1.execute("pageManager.getFolder(\"/\").getTitle();");
            assertTrue(result.endsWith("Root Folder"));
            result = server1.execute("pageManager.getPage(\"/default-page.psml\").getTitle();");
            assertTrue(result.endsWith("Default Page"));
            result = server1.execute("pageManager.getPage(\"/default-page.psml\").getRootFragment().getTitle();");
            assertTrue(result.endsWith("Default Page Root Fragment"));
            result = server1.execute("pageManager.getPage(\"/default-page.psml\").getRootFragment().getState();");
            assertTrue(result.endsWith("USER"));
            result = server1.execute("pageManager.getPage(\"/default-page.psml\").getRootFragment().getProperty(\"CUSTOM\");");
            assertTrue(result.endsWith("CUSTOM2"));
            result = server1.execute("pageManager.getPage(\"/another-page.psml\").getTitle();");
            assertTrue(result.endsWith("Another Page"));
            result = server1.execute("pageManager.getPage(\"/another-page.psml\").getRootFragment().getTitle();");
            assertTrue(result.endsWith("Another Page Root Fragment"));
            result = server1.execute("pageManager.getPage(\"/another-page.psml\").getRootFragment().getState();");
            assertTrue(result.endsWith("DEFAULT"));
            result = server1.execute("pageManager.getPage(\"/another-page.psml\").getRootFragment().getProperty(\"CUSTOM\");");
            assertTrue(result.endsWith("CUSTOM"));
            result = server1.execute("pageManager.getPage(\"/some-other-page.psml\").getTitle();");
            assertTrue(result.endsWith("Some Other Page"));            
            result = server1.execute("pageManager.getPage(\"/some-other-page.psml\").getRootFragment().getProperty(\"CUSTOM\");");
            assertTrue(result.endsWith("SOME-OTHER-CUSTOM"));
            result = server1.execute("pageManager.getLink(\"/default.link\").getTitle();");
            assertTrue(result.endsWith("Default Link"));
            result = server1.execute("pageManager.getFolder(\"/deep-0\").getTitle();");
            assertTrue(result.endsWith("Deep 0 Folder"));
            result = server1.execute("pageManager.getPage(\"/deep-0/deep-page-0.psml\").getTitle();");
            assertTrue(result.endsWith("Deep Page 0"));
            result = server1.execute("pageManager.getFolder(\"/deep-1\").getTitle();");
            assertTrue(result.endsWith("Deep 1 Folder"));
            result = server1.execute("pageManager.getPage(\"/deep-1/deep-page-1.psml\").getTitle();");
            assertTrue(result.endsWith("Deep Page 1"));
            result = server1.execute("pageManager.getPageSecurity().getPath();");
            assertTrue(result.endsWith("/page.security"));

            // wait for cache notifications to propagate between servers
            sleep(server0, server1, CACHE_NOTIFICATION_WAIT);
            
            // update/remove objects and properties in second page manager
            result = server1.execute("page = pageManager.getPage(\"/default-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("page.setTitle(\"Edited Default Page\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.updatePage(page);");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("page = pageManager.getPage(\"/another-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("fragment = page.getRootFragment();");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("fragment.setState(\"DEFAULT2\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("fragment.setProperty(\"CUSTOM\", null, null, \"CUSTOM2\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.updatePage(page);");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("page = pageManager.getPage(\"/some-other-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.removePage(page);");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("link = pageManager.getLink(\"/default.link\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("link.setTitle(\"Edited Default Link\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.updateLink(link);");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("folder = pageManager.getFolder(\"/deep-0\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("folder.setTitle(\"Edited Deep 0 Folder\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.updateFolder(folder);");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("folder = pageManager.getFolder(\"/deep-1\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.removeFolder(folder);");
            assertTrue(!result.contains("Exception"));
            
            // reset request cache
            result = server1.execute("pageManager.cleanupRequestCache();");
            assertTrue(!result.contains("Exception"));

            // wait for cache notifications to propagate between servers
            sleep(server0, server1, CACHE_NOTIFICATION_WAIT);
            
            // test objects and properties in both page managers for cache coherence
            result = server1.execute("pageManager.getPage(\"/default-page.psml\").getTitle();");
            assertTrue(result.endsWith("Edited Default Page"));
            result = server1.execute("pageManager.getPage(\"/another-page.psml\").getRootFragment().getState();");
            assertTrue(result.endsWith("DEFAULT2"));
            result = server1.execute("pageManager.getPage(\"/another-page.psml\").getRootFragment().getProperty(\"CUSTOM\");");
            assertTrue(result.endsWith("CUSTOM2"));
            result = server1.execute("pageManager.getPage(\"/some-other-page.psml\");");
            assertTrue(result.contains("PageNotFoundException"));
            result = server1.execute("pageManager.getFolder(\"/\").getPages().size();");
            assertTrue(result.endsWith("2"));
            result = server1.execute("pageManager.getLink(\"/default.link\").getTitle();");
            assertTrue(result.endsWith("Edited Default Link"));
            result = server1.execute("pageManager.getFolder(\"/deep-0\").getTitle();");
            assertTrue(result.endsWith("Edited Deep 0 Folder"));
            result = server1.execute("pageManager.getPage(\"/deep-1/deep-page-1.psml\");");
            assertTrue(result.contains("PageNotFoundException"));
            result = server1.execute("pageManager.getFolder(\"/deep-1\");");
            assertTrue(result.contains("FolderNotFoundException"));
            result = server1.execute("pageManager.getFolder(\"/\").getFolders().size();");
            assertTrue(result.endsWith("1"));
            boolean defaultPageUpdated = false;
            boolean anotherPageStateUpdated = false;
            boolean anotherPagePropertyUpdated = false;
            boolean someOtherPageRemoved = false;
            boolean rootFolderPagesCountTwo = false;
            boolean defaultLinkUpdated = false;
            boolean deep0FolderUpdated = false;
            boolean deepPage1Removed = false;
            boolean deep1FolderRemoved = false;
            boolean rootFolderFoldersCountOne = false;
            long coherencyCheckStarted = System.currentTimeMillis();
            do
            {
                // reset request cache
                result = server0.execute("pageManager.cleanupRequestCache();");
                assertTrue(!result.contains("Exception"));

                // check cache coherence
                if (!defaultPageUpdated)
                {
                    result = server0.execute("pageManager.getPage(\"/default-page.psml\").getTitle();");
                    defaultPageUpdated = result.endsWith("Edited Default Page");
                }
                if (!anotherPageStateUpdated)
                {
                    result = server0.execute("pageManager.getPage(\"/another-page.psml\").getRootFragment().getState();");
                    anotherPageStateUpdated = result.endsWith("DEFAULT2");
                }
                if (!anotherPagePropertyUpdated)
                {
                    result = server0.execute("pageManager.getPage(\"/another-page.psml\").getRootFragment().getProperty(\"CUSTOM\");");
                    anotherPagePropertyUpdated = result.endsWith("CUSTOM2");
                }
                if (!someOtherPageRemoved)
                {
                    result = server0.execute("pageManager.getPage(\"/some-other-page.psml\");");
                    someOtherPageRemoved = result.contains("PageNotFoundException");
                }
                if (!rootFolderPagesCountTwo)
                {
                    result = server0.execute("pageManager.getFolder(\"/\").getPages().size();");
                    rootFolderPagesCountTwo = result.endsWith("2");
                }
                if (!defaultLinkUpdated)
                {
                    result = server0.execute("pageManager.getLink(\"/default.link\").getTitle();");
                    defaultLinkUpdated = result.endsWith("Edited Default Link");
                }
                if (!deep0FolderUpdated)
                {
                    result = server0.execute("pageManager.getFolder(\"/deep-0\").getTitle();");
                    deep0FolderUpdated = result.endsWith("Edited Deep 0 Folder");
                }
                if (!deepPage1Removed)
                {
                    result = server0.execute("pageManager.getPage(\"/deep-1/deep-page-1.psml\");");
                    deepPage1Removed = result.contains("PageNotFoundException");
                }
                if (!deep1FolderRemoved)
                {
                    result = server0.execute("pageManager.getFolder(\"/deep-1\");");
                    deep1FolderRemoved = result.contains("FolderNotFoundException");
                }
                if (!rootFolderFoldersCountOne)
                {
                    result = server0.execute("pageManager.getFolder(\"/\").getFolders().size();");
                    rootFolderFoldersCountOne = result.endsWith("1");
                }
                
                // wait for cache coherence
                if (!defaultPageUpdated || !anotherPageStateUpdated || !anotherPagePropertyUpdated || !someOtherPageRemoved || !rootFolderPagesCountTwo || !defaultLinkUpdated || !deep0FolderUpdated || !deepPage1Removed || !deep1FolderRemoved || !rootFolderFoldersCountOne)
                {
                    sleep(server0, server1, CACHE_NOTIFICATION_POLL);
                }
            }
            while ((!defaultPageUpdated || !anotherPageStateUpdated || !anotherPagePropertyUpdated || !someOtherPageRemoved || !rootFolderPagesCountTwo || !defaultLinkUpdated || !deep0FolderUpdated || !deepPage1Removed || !deep1FolderRemoved || !rootFolderFoldersCountOne) && (System.currentTimeMillis()-coherencyCheckStarted < CACHE_NOTIFICATION_WAIT));
            assertTrue(defaultPageUpdated);
            assertTrue(anotherPageStateUpdated);
            assertTrue(anotherPagePropertyUpdated);
            assertTrue(someOtherPageRemoved);
            assertTrue(rootFolderPagesCountTwo);
            assertTrue(defaultLinkUpdated);
            assertTrue(deep0FolderUpdated);
            assertTrue(deepPage1Removed);
            assertTrue(deep1FolderRemoved);
            assertTrue(rootFolderFoldersCountOne);

            // reset request cache
            result = server0.execute("pageManager.cleanupRequestCache();");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.cleanupRequestCache();");
            assertTrue(!result.contains("Exception"));
            
            // update properties/add new objects in first page manager
            result = server0.execute("page = pageManager.getPage(\"/default-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment = page.getRootFragment();");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("fragment.setState(\""+FragmentProperty.USER_PROPERTY_SCOPE+"\", null, \"USER2\");");
            assertTrue(!result.contains("Exception"));            
            result = server0.execute("pageManager.updateFragmentProperties(fragment, \""+PageManager.USER_PROPERTY_SCOPE+"\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page = pageManager.newPage(\"/new-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page.setTitle(\"New Page\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updatePage(page);");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("folder = pageManager.newFolder(\"/deep-2\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("folder.setTitle(\"Deep 2 Folder\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updateFolder(folder);");
            assertTrue(!result.contains("Exception"));

            // reset request cache
            result = server0.execute("pageManager.cleanupRequestCache();");
            assertTrue(!result.contains("Exception"));

            // wait for cache notifications to propagate between servers
            sleep(server0, server1, CACHE_NOTIFICATION_WAIT);
            
            // test objects in both page managers for cache coherence
            result = server0.execute("pageManager.getPage(\"/default-page.psml\").getRootFragment().getState();");
            assertTrue(result.endsWith("USER2"));
            result = server0.execute("pageManager.getFolder(\"/\").getPages().size();");
            assertTrue(result.endsWith("3"));
            result = server0.execute("pageManager.getFolder(\"/\").getFolders().size();");
            assertTrue(result.endsWith("2"));
            result = server0.execute("pageManager.getPage(\"/new-page.psml\").getTitle();");
            assertTrue(result.endsWith("New Page"));
            result = server0.execute("pageManager.getFolder(\"/deep-2\").getTitle();");
            assertTrue(result.endsWith("Deep 2 Folder"));
            boolean defaultPageUserStateUpdated = false;
            boolean rootFolderPagesCountThree = false;
            boolean rootFolderFoldersCountTwo = false;
            boolean newPageCreated = false;
            boolean deep2FolderCreated = false;
            coherencyCheckStarted = System.currentTimeMillis();
            do
            {
                // reset request cache
                result = server1.execute("pageManager.cleanupRequestCache();");
                assertTrue(!result.contains("Exception"));
                
                // check cache coherence
                if (!defaultPageUserStateUpdated)
                {
                    result = server1.execute("pageManager.getPage(\"/default-page.psml\").getRootFragment().getState();");
                    defaultPageUserStateUpdated = result.endsWith("USER2");                    
                }
                if (!rootFolderPagesCountThree)
                {
                    result = server1.execute("pageManager.getFolder(\"/\").getPages().size();");
                    rootFolderPagesCountThree = result.endsWith("3");
                }
                if (!rootFolderFoldersCountTwo)
                {
                    result = server1.execute("pageManager.getFolder(\"/\").getFolders().size();");
                    rootFolderFoldersCountTwo = result.endsWith("2");
                }
                if (!newPageCreated)
                {
                    result = server1.execute("pageManager.getPage(\"/new-page.psml\").getTitle();");
                    newPageCreated = result.endsWith("New Page");
                }
                if (!deep2FolderCreated)
                {
                    result = server1.execute("pageManager.getFolder(\"/deep-2\").getTitle();");
                    deep2FolderCreated = result.endsWith("Deep 2 Folder");
                }

                // wait for cache coherence
                if (!defaultPageUserStateUpdated || !rootFolderPagesCountThree || !rootFolderFoldersCountTwo || !newPageCreated || !deep2FolderCreated)
                {
                    sleep(server0, server1, CACHE_NOTIFICATION_POLL);
                }
            }
            while ((!defaultPageUserStateUpdated || !rootFolderPagesCountThree || !rootFolderFoldersCountTwo || !newPageCreated || !deep2FolderCreated) && (System.currentTimeMillis()-coherencyCheckStarted < CACHE_NOTIFICATION_WAIT));
            assertTrue(defaultPageUserStateUpdated);
            assertTrue(rootFolderPagesCountThree);
            assertTrue(rootFolderFoldersCountTwo);
            assertTrue(newPageCreated);
            assertTrue(deep2FolderCreated);
                        
            // reset request cache
            result = server0.execute("pageManager.cleanupRequestCache();");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.cleanupRequestCache();");
            assertTrue(!result.contains("Exception"));
            
            // update only properties in second page manager
            result = server1.execute("page = pageManager.getPage(\"/default-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("fragment = page.getRootFragment();");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("fragment.setState(\""+FragmentProperty.USER_PROPERTY_SCOPE+"\", null, \"USER3\");");
            assertTrue(!result.contains("Exception"));            
            result = server1.execute("pageManager.updateFragmentProperties(fragment, \""+PageManager.USER_PROPERTY_SCOPE+"\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("page = pageManager.getPage(\"/another-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("fragment = page.getRootFragment();");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("fragment.setProperty(\"CUSTOM\", null, null, \"CUSTOM3\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.updateFragmentProperties(fragment, null);");
            assertTrue(!result.contains("Exception"));
            if (FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
            {
                result = server1.execute("page = pageManager.getPage(\"/new-page.psml\");");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("fragment = page.getRootFragment();");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("fragment.setProperty(\"GROUP-CUSTOM\", \""+FragmentProperty.GROUP_PROPERTY_SCOPE+"\", \"group\", \"GROUP-CUSTOM\");");
                assertTrue(!result.contains("Exception"));
                result = server1.execute("pageManager.updateFragmentProperties(fragment, \""+PageManager.GROUP_PROPERTY_SCOPE+"\");");
                assertTrue(!result.contains("Exception"));
            }

            // reset request cache
            result = server1.execute("pageManager.cleanupRequestCache();");
            assertTrue(!result.contains("Exception"));

            // wait for cache notifications to propagate between servers
            sleep(server0, server1, CACHE_NOTIFICATION_WAIT);
            
            // test objects in both page managers for cache coherence
            result = server1.execute("pageManager.getPage(\"/default-page.psml\").getRootFragment().getState();");
            assertTrue(result.endsWith("USER3"));
            result = server1.execute("pageManager.getPage(\"/another-page.psml\").getRootFragment().getProperty(\"CUSTOM\");");
            assertTrue(result.endsWith("CUSTOM3"));
            if (FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
            {
                result = server1.execute("pageManager.getPage(\"/new-page.psml\").getRootFragment().getProperty(\"GROUP-CUSTOM\");");
                assertTrue(result.endsWith("GROUP-CUSTOM"));                
            }
            defaultPageUserStateUpdated = false;
            anotherPagePropertyUpdated = false;
            boolean newPagePropertyUpdated = false;
            coherencyCheckStarted = System.currentTimeMillis();
            do
            {
                // reset request cache
                result = server0.execute("pageManager.cleanupRequestCache();");
                assertTrue(!result.contains("Exception"));
                
                // check cache coherence
                if (!defaultPageUserStateUpdated)
                {
                    result = server0.execute("pageManager.getPage(\"/default-page.psml\").getRootFragment().getState();");
                    defaultPageUserStateUpdated = result.endsWith("USER3");                    
                }
                if (!anotherPagePropertyUpdated)
                {
                    result = server0.execute("pageManager.getPage(\"/another-page.psml\").getRootFragment().getProperty(\"CUSTOM\");");
                    anotherPagePropertyUpdated = result.endsWith("CUSTOM3");
                }
                if (FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
                {
                    if (!newPagePropertyUpdated)
                    {
                        result = server0.execute("pageManager.getPage(\"/new-page.psml\").getRootFragment().getProperty(\"GROUP-CUSTOM\");");
                        newPagePropertyUpdated = result.endsWith("GROUP-CUSTOM");
                    }                    
                }

                // wait for cache coherence
                if (!defaultPageUserStateUpdated || !anotherPagePropertyUpdated || (!newPagePropertyUpdated && FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED))
                {
                    sleep(server0, server1, CACHE_NOTIFICATION_POLL);
                }
            }
            while ((!defaultPageUserStateUpdated || !anotherPagePropertyUpdated || (!newPagePropertyUpdated && FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)) && (System.currentTimeMillis()-coherencyCheckStarted < CACHE_NOTIFICATION_WAIT));
            assertTrue(defaultPageUserStateUpdated);
            assertTrue(anotherPagePropertyUpdated);
            assertTrue(newPagePropertyUpdated || !FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED);
            
            // reset request cache
            result = server0.execute("pageManager.cleanupRequestCache();");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.cleanupRequestCache();");
            assertTrue(!result.contains("Exception"));
            
            // return servers to anonymous mode
            result = server0.execute("pageManagerServer.setUser(null);");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManagerServer.setUser(null);");
            assertTrue(!result.contains("Exception"));            

            // reset database page managers
            result = server0.execute("pageManager.reset();");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("pageManager.reset();");
            assertTrue(!result.contains("Exception"));
        }
        catch (final Exception e)
        {
            log.error("Server test exception: "+e, e);
            fail( "Server test exception: "+e);            
        }        
        finally
        {
            // silently shutdown servers
            try
            {
                server0.shutdown();
            }
            catch (final Exception e)
            {
                log.error( "Server shutdown exception: "+e, e);
            }
            try
            {
                server1.shutdown();
            }
            catch (final Exception e)
            {
                log.error( "Server shutdown exception: "+e, e);
            }
        }
    }
    
    private void sleep(TestProgram server0, TestProgram server1, long millis) throws IOException, InterruptedException
    {
        long slept = 0;
        while (slept < millis)
        {
            // poll servers for logging
            server0.poll();
            server1.poll();
            // sleep for interval
            long sleep = Math.min(millis-slept, CACHE_LOGGING_PUMP_WAIT);
            Thread.sleep(sleep);
            slept += sleep;
        }
    }
    
    // Implementation classes
    
    protected class TestProgram
    {
        private String name;
        private Class<?> mainClass;
        private int index;

        private Process process;
        private BufferedWriter processInput;
        private BufferedReader processOutput;
        
        public TestProgram(final String name, final Class<?> mainClass, final int index)
        {
            this.name = name;
            this.mainClass = mainClass;
            this.index = index;
        }
        
        public synchronized void start() throws IOException
        {
            assertNull(process);

            // configure launcher with paths, properties, and indexed properties
            final ProcessBuilder launcher = new ProcessBuilder();
            final List<String> commandAndArgs = new ArrayList<String>();
            commandAndArgs.add(javaExecutablePath.getCanonicalPath());
            for (Map.Entry<String,String> systemProperty : systemProperties.entrySet())
            {
                final String propertyName = systemProperty.getKey();
                String propertyValue = systemProperty.getValue();
                if (propertyName.equals(EhCacheConfigResource.EHCACHE_PORT_PROP_NAME))
                {
                    propertyValue = Integer.toString(Integer.parseInt(propertyValue)+index);
                }
                commandAndArgs.add( "-D"+propertyName+"="+propertyValue);
            }
            commandAndArgs.add("-Dlog4j.configuration=log4j-stdout.properties");
            commandAndArgs.add("-classpath");
            commandAndArgs.add(classPath);
            commandAndArgs.add(mainClass.getName());
            log.info("Launcher command for "+name+": "+commandAndArgs);
            launcher.command(commandAndArgs);
            launcher.directory(projectDirectoryPath);
            launcher.redirectErrorStream(true);

            // launch test programs
            process = launcher.start();

            // setup I/O for process
            processInput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // read messages from process
            for (String line; (processOutput.ready() && ((line = processOutput.readLine()) != null));)
            {
                logProcessLine(line);
            }
        }

        public synchronized void poll() throws IOException
        {
            assertNotNull(process);

            // read messages from process
            for (String line; (processOutput.ready() && ((line = processOutput.readLine()) != null));)
            {
                logProcessLine(line);
            }
        }

        public synchronized String execute(final String scriptLine) throws IOException
        {
            // poll to read messages from process
            poll();

            // write script line to process
            processInput.write(scriptLine);
            processInput.newLine();
            processInput.flush();

            // read result or messages from process
            String resultLine = null;
            for (String line; ((line = processOutput.readLine()) != null);)
            {
                if (! line.startsWith(DatabasePageManagerServer.SCRIPT_RESULT_LINE_PREFIX))
                {
                    logProcessLine(line);
                }
                else
                {
                    resultLine = line;
                    break;
                }
            }
            if ( resultLine == null)
            {
                throw new IOException("Unexpected EOF from process output");
            }
            return resultLine;
        }
        
        public synchronized void shutdown() throws IOException, InterruptedException
        {
            assertNotNull( process);

            // start thread to destroy process on timeout
            final Thread destroyThread = new Thread(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        Thread.sleep(CACHE_NOTIFICATION_STARTUP_WAIT);
                        if ( process != null)
                        {
                            log.warn( "Forcibly stopping "+name);
                            process.destroy();
                        }
                    }
                    catch ( final Exception e)
                    {
                    }
                }
            }, "DestroyThread");
            destroyThread.setDaemon( true);
            destroyThread.start();

            // close process input to shutdown server and read messages
            processInput.close();
            for (String line; ((line = processOutput.readLine()) != null);)
            {
                logProcessLine(line);
            }

            // join on process completion
            process.waitFor();
            processOutput.close();
            process = null;

            // join on destroy thread
            destroyThread.interrupt();
            destroyThread.join();
        }
        
        private void logProcessLine(final String line)
        {
            if (line.contains("ERROR") || line.contains("Exception") || line.matches("\\s+at\\s.*"))
            {
                log.error("{"+name+"} "+line);
            }
            else
            {
                log.info("{"+name+"} "+line);                        
            }
        }
    }
    
    // TestCase implementation
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        // setup cache properties
        EhCacheConfigResource.getInstance(EhCacheConfigResource.EHCACHE_CONFIG_RESOURCE_DISTRIBUTED_CACHE, true);
        
        // environment setup
        osExecutableExtension = (System.getProperty("os.name").startsWith("Windows") ? ".exe" : "");
        fileSeparator = System.getProperty("file.separator");
        javaExecutablePath = new File(System.getProperty("java.home")+fileSeparator+"bin"+fileSeparator+"java"+osExecutableExtension);
        classPathSeparator = System.getProperty("path.separator");
        projectDirectoryPath = new File(System.getProperty("basedir"));
        systemProperties = new HashMap<String,String>();
        for (final Map.Entry<Object,Object> systemProperty : System.getProperties().entrySet())
        {
            final String propertyName = systemProperty.getKey().toString();
            final String propertyValue = systemProperty.getValue().toString();
            if (propertyName.startsWith("org.apache.jetspeed.") || propertyName.startsWith("java.net.") || propertyName.equals("basedir"))
            {
                systemProperties.put(propertyName, propertyValue);
            }
        }

        // construct launcher classpath from current class loader
        final StringBuilder classPathBuilder = new StringBuilder();
        final ClassLoader loader = this.getClass().getClassLoader();
        assertTrue(loader instanceof URLClassLoader);
        final URLClassLoader urlLoader = (URLClassLoader)loader;
        assertNotNull(urlLoader.getURLs());
        for (final URL pathURL : urlLoader.getURLs())
        {
            // convert path URL to file path
            final String path = new File(pathURL.toURI()).getCanonicalPath();

            // build class path
            if (classPathBuilder.length() > 0)
            {
                classPathBuilder.append(classPathSeparator);
            }
            classPathBuilder.append(path);
        }
        classPath = classPathBuilder.toString();
        assertTrue(classPath.length() > 0);

        // continue setup
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    // Application entry point

    /**
     * Start the tests.
     * 
     * @param args not used
     */
    public static void main(final String [] args)
    {
        junit.awtui.TestRunner.main(new String[]{TestDatabasePageManagerCache.class.getName()});
    }

    /**
     * Creates the test suite.
     * 
     * @return a test suite that includes all methods starting with "test"
     */
    public static Test suite()
    {
        return new TestSuite(TestDatabasePageManagerCache.class);
    }
}
