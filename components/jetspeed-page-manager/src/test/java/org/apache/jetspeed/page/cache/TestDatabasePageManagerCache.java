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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cache.impl.EhCacheConfigResource;

/**
 * TestDatabasePageManagerCache
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class TestDatabasePageManagerCache extends TestCase
{
    protected static Log log = LogFactory.getLog(TestDatabasePageManagerCache.class);
    
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
                    Thread.sleep(250);
                }
            }
            while ((!server0Distributed || !server1Distributed) && (System.currentTimeMillis()-distributedCheckStarted < 5000));
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
            
            // create folder and documents in first page manager
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
            result = server0.execute("pageManager.updatePage(page);");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page = pageManager.newPage(\"/another-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page.setTitle(\"Another Page\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("pageManager.updatePage(page);");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page = pageManager.newPage(\"/some-other-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server0.execute("page.setTitle(\"Some Other Page\");");
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

            // populate folders and documents in second page manager
            result = server1.execute("pageManager.getFolder(\"/\").getTitle();");
            assertTrue(result.endsWith("Root Folder"));
            result = server1.execute("pageManager.getPage(\"/default-page.psml\").getTitle();");
            assertTrue(result.endsWith("Default Page"));
            result = server1.execute("pageManager.getPage(\"/another-page.psml\").getTitle();");
            assertTrue(result.endsWith("Another Page"));
            result = server1.execute("pageManager.getPage(\"/some-other-page.psml\").getTitle();");
            assertTrue(result.endsWith("Some Other Page"));            
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
            
            // update/remove objects in second page manager
            result = server1.execute("page = pageManager.getPage(\"/default-page.psml\");");
            assertTrue(!result.contains("Exception"));
            result = server1.execute("page.setTitle(\"Edited Default Page\");");
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
            
            // test objects in both page managers for cache coherency
            result = server1.execute("pageManager.getPage(\"/default-page.psml\").getTitle();");
            assertTrue(result.endsWith("Edited Default Page"));
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
                // check cache coherency
                if (!defaultPageUpdated)
                {
                    result = server0.execute("pageManager.getPage(\"/default-page.psml\").getTitle();");
                    defaultPageUpdated = result.endsWith("Edited Default Page");
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
                
                // wait for cache coherency
                if (!defaultPageUpdated || !someOtherPageRemoved || !rootFolderPagesCountTwo || !defaultLinkUpdated || !deep0FolderUpdated || !deepPage1Removed || !deep1FolderRemoved || !rootFolderFoldersCountOne)
                {
                    Thread.sleep(250);
                }
            }
            while ((!defaultPageUpdated || !someOtherPageRemoved || !rootFolderPagesCountTwo || !defaultLinkUpdated || !deep0FolderUpdated || !deepPage1Removed || !deep1FolderRemoved || !rootFolderFoldersCountOne) && (System.currentTimeMillis()-coherencyCheckStarted < 5000));
            assertTrue(defaultPageUpdated);
            assertTrue(someOtherPageRemoved);
            assertTrue(rootFolderPagesCountTwo);
            assertTrue(defaultLinkUpdated);
            assertTrue(deep0FolderUpdated);
            assertTrue(deepPage1Removed);
            assertTrue(deep1FolderRemoved);
            assertTrue(rootFolderFoldersCountOne);

            // add new objects in first page manager
            result = server0.execute("page = pageManager.newPage(\"/new-page.psml\");");
            assertTrue(result.indexOf("Exception") == -1);
            result = server0.execute("page.setTitle(\"New Page\");");
            assertTrue(result.indexOf("Exception") == -1);
            result = server0.execute("pageManager.updatePage(page);");
            assertTrue(result.indexOf("Exception") == -1);
            result = server0.execute("folder = pageManager.newFolder(\"/deep-2\");");
            assertTrue(result.indexOf("Exception") == -1);
            result = server0.execute("folder.setTitle(\"Deep 2 Folder\");");
            assertTrue(result.indexOf("Exception") == -1);
            result = server0.execute("pageManager.updateFolder(folder);");
            assertTrue(result.indexOf("Exception") == -1);
            
            // test objects in both page managers for cache coherency
            result = server0.execute("pageManager.getFolder(\"/\").getPages().size();");
            assertTrue(result.endsWith("3"));
            result = server0.execute("pageManager.getFolder(\"/\").getFolders().size();");
            assertTrue(result.endsWith("2"));
            result = server0.execute("pageManager.getPage(\"/new-page.psml\").getTitle();");
            assertTrue(result.endsWith("New Page"));
            result = server0.execute("pageManager.getFolder(\"/deep-2\").getTitle();");
            assertTrue(result.endsWith("Deep 2 Folder"));
            boolean rootFolderPagesCountThree = false;
            boolean rootFolderFoldersCountTwo = false;
            boolean newPageCreated = false;
            boolean deep2FolderCreated = false;
            coherencyCheckStarted = System.currentTimeMillis();
            do
            {
                // check cache coherency
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

                // wait for cache coherency
                if (!rootFolderPagesCountThree || !rootFolderFoldersCountTwo || !newPageCreated || !deep2FolderCreated)
                {
                    Thread.sleep(250);
                }
            }
            while ((!rootFolderPagesCountThree || !rootFolderFoldersCountTwo || !newPageCreated || !deep2FolderCreated) && (System.currentTimeMillis()-coherencyCheckStarted < 5000));
            assertTrue(rootFolderPagesCountThree);
            assertTrue(rootFolderFoldersCountTwo);
            assertTrue(newPageCreated);
            assertTrue(deep2FolderCreated);

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

        public synchronized String execute(final String scriptLine) throws IOException
        {
            assertNotNull(process);

            // read messages from process
            for (String line; (processOutput.ready() && ((line = processOutput.readLine()) != null));)
            {
                logProcessLine(line);
            }

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
                        Thread.sleep(10000);
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
            if (!line.contains("INFO") && (line.contains("ERROR") || line.contains("Exception") || line.startsWith("   at ")))
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
