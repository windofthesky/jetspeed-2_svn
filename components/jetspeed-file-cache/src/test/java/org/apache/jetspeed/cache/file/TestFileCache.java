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

package org.apache.jetspeed.cache.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.impl.EhCacheConfigResource;
import org.apache.jetspeed.cache.impl.EhCacheImpl;
import org.apache.jetspeed.test.JetspeedTestCase;


/**
 * Unit test for FileCache 
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */

public class TestFileCache extends JetspeedTestCase implements FileCacheEventListener
{    
    protected static final String TEST_DIRECTORY = "target/test-classes";
    protected static final int CACHE_SIZE = 20;
    protected static final int SCAN_RATE = 10;
    String refreshedEntry = null;


 
    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
     public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestFileCache.class);
    }
    
       
    private FileCache cache = null;
    
    protected void setUp() throws Exception
    {
        super.setUp();
        
        // initialize ehCache
        EhCacheConfigResource.getInstance(EhCacheConfigResource.EHCACHE_CONFIG_RESOURCE_DEFAULT, true);
        CacheManager cacheManager = new CacheManager();
        Cache ehPageFileCache = new Cache("ehPageFileCache", CACHE_SIZE, false, false, 0, 0);
        cacheManager.addCache(ehPageFileCache);
        ehPageFileCache.setCacheManager(cacheManager);       
        
        cache = new FileCache(new EhCacheImpl(ehPageFileCache), SCAN_RATE);
    }    
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    public void tearDown() throws Exception
    {
        removeTestFiles();
        super.tearDown();
    }

     /**
     * Tests loading the cache
     * @throws Exception
     */

    public void testLoadCache() throws Exception 
    {        
        String templateFile = getBaseDir() + TEST_DIRECTORY+"/default.psml";
        try
        {
            File file = new File(templateFile);            
            assertTrue(file.exists());

            createTestFiles(templateFile);

            // create the Cache  wake up after 10 seconds, cache size 20
            // FileCache cache = new FileCache(10, 20);

            // load the Cache
            File directory = new File(getBaseDir() + TEST_DIRECTORY);
            File[] files = directory.listFiles();
            int fileCount = 0;
            for (int ix=0; ix < files.length; ix++)
            {
                if (files[ix].isDirectory() || files[ix].getName().equals(".cvsignore"))
                {
                    continue;
                }
                String testData = readFile(files[ix]);                
                cache.put(files[ix], testData);
                ++fileCount;
            }

            assertTrue(cache.getSize() == Math.min(CACHE_SIZE, fileCount));

            dumpCache(cache.getKeys());

            cache.addListener(this);

            // start the cache's scanner
            cache.startFileScanner();

            Thread.sleep(2000);

            assertTrue(cache.getSize() == Math.min(CACHE_SIZE, fileCount));

            dumpCache(cache.getKeys());

            // Reload files array to get the files back in the correct order
            // because the cache CAN have reordered them while evicting.
            // This can happen if test files where left over from a previous 
            // test which then will have an older timestamp.
            // In that case it is NOT garanteed that files[18] below will still
            // be in the cache!
            // Note: this is only an issue for the test itself and not for the
            // cache as such. 

            int ix = 0;
            for (Object key : cache.getKeys())
            {
                FileCacheEntry entry = (FileCacheEntry) cache.get((String) key);
                files[ix++] = entry.getFile();            
            }

            String stuff = (String) cache.getDocument(files[18].getCanonicalPath());
            assertNotNull(stuff);

            files[18].setLastModified(new Date().getTime());

            Thread.sleep(9000);

            assertNotNull(refreshedEntry);
            System.out.println("refreshed entry = " + refreshedEntry);

            cache.stopFileScanner();

            // evict all from cache
            cache.evictAll();
            assertTrue(cache.getSize() == 0);

            removeTestFiles();
        }
        catch (Exception e)
        {
            fail(ExceptionUtils.getStackTrace(e));
        }

        System.out.println("Completed loadCache Test OK ");

    }

    private void createTestFiles(String templateFile)
        throws java.io.IOException
    {
        for (int ix=1; ix < 31; ix++)
        {
            String testFile = getBaseDir()+TEST_DIRECTORY+"/testFile-" + ix + ".psml";
            FileCopy.copy(templateFile, testFile);
        }
    }

    private void removeTestFiles()
    {
        for (int ix=1; ix < 31; ix++)
        {
            String testFile = getBaseDir()+TEST_DIRECTORY+"/testFile-" + ix + ".psml";
            File file = new File(testFile);
            if ( file.exists() )
                file.delete();
        }
    }

    private String readFile(File file)
        throws java.io.IOException, java.io.FileNotFoundException
    {
        BufferedInputStream input;

        input = new BufferedInputStream(new FileInputStream(file));
        String result = IOUtils.toString(input);
        input.close();
        return result;
    }

    /**
     * Refresh event, called when the entry is being refreshed from file system.
     *
     * @param entry the entry being refreshed.
     */
    public void refresh(FileCacheEntry entry)
    {
        System.out.println("entry is refreshing: " + entry.getFile().getName());
        this.refreshedEntry = entry.getFile().getName();
    }

    /**
     * Evict event, called when the entry is being evicted out of the cache
     *
     * @param entry the entry being refreshed.
     */
    public void evict(FileCacheEntry entry)
    {
        System.out.println("entry is evicting: " + entry.getFile().getName());
    }

    private void dumpCache(List keys)
    {
        for (Object key : keys)
        {
            FileCacheEntry entry = (FileCacheEntry) cache.get((String) key);
            System.out.println(entry.getFile().getName());
        }
    }
}






