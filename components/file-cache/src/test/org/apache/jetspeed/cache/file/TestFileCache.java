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

package org.apache.jetspeed.cache.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Iterator;

import junit.framework.Test;

import org.apache.commons.io.StreamUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.NanoDeployerBasedTestSuite;
import org.picocontainer.MutablePicoContainer;

/**
 * Unit test for FileCache 
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */

public class TestFileCache extends AbstractComponentAwareTestCase implements FileCacheEventListener
{    
    String refreshedEntry = null;

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestFileCache( String name ) 
    {
        super( name );
    }
    
 
    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() 
    {
      // ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestFileCache.class);
      //  suite.setScript("org/apache/jetspeed/cache/file/filecache.container.groovy");
        return new NanoDeployerBasedTestSuite(TestFileCache.class);
    }
    
    private MutablePicoContainer container = null;
    
    private FileCache cache = null;
    
    protected void setUp() throws Exception
    {
        super.setUp();
        container = (MutablePicoContainer) getContainer();
        cache = (FileCache) container.getComponentInstance(FileCache.class);
    }    
    
    public void testComponent()
    throws Exception
    {
        assertNotNull("container failed to load", container);
        assertNotNull("component failed to load", cache);        
    }

    /**
     * Tests loading the cache
     * @throws Exception
     */

    public void testLoadCache() throws Exception 
    {        
        String templateFile = getApplicationRoot()+"/test/testdata/psml/user/cachetest/default.psml";
        try
        {
            File file = new File(templateFile);            
            assertTrue(file.exists());

            createTestFiles(templateFile);

            // create the Cache  wake up after 10 seconds, cache size 20
            // FileCache cache = new FileCache(10, 20);

            // load the Cache
            File directory = new File(getApplicationRoot()+"/test/testdata/psml/user/cachetest/");
            File[] files = directory.listFiles();
            for (int ix=0; ix < files.length; ix++)
            {
                if (files[ix].isDirectory() || files[ix].getName().equals(".cvsignore"))
                {
                    continue;
                }
                String testData = readFile(files[ix]);                
                cache.put(files[ix], testData);
            }

            assertTrue(cache.getSize() == 31);

            dumpCache(cache.getIterator());

            cache.addListener(this);
            // start the cache's scanner
            cache.startFileScanner();

            Thread.sleep(2000);

            assertTrue(cache.getSize() == 20);

            dumpCache(cache.getIterator());

            String stuff = (String) cache.getDocument(files[18].getCanonicalPath());
            assertNotNull(stuff);

            files[18].setLastModified(new Date().getTime());


            Thread.sleep(9000);

            assertNotNull(refreshedEntry);
            System.out.println("refreshed entry = " + refreshedEntry);

            cache.stopFileScanner();

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
            String testFile = getApplicationRoot()+"/test/testdata/psml/user/cachetest/testFile-" + ix + ".psml";
            FileCopy.copy(templateFile, testFile);
        }
    }

    private void removeTestFiles()
    {
        for (int ix=1; ix < 31; ix++)
        {
            String testFile = getApplicationRoot()+"/test/testdata/psml/user/cachetest/testFile-" + ix + ".psml";
            File file = new File(testFile);
            file.delete();
        }
    }

    private String readFile(File file)
        throws java.io.IOException, java.io.FileNotFoundException
    {
        BufferedInputStream input;

        input = new BufferedInputStream(new FileInputStream(file));
        String result = StreamUtils.streamAsString(input);
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

    private void dumpCache(Iterator it)
    {
        for ( ; it.hasNext(); )
        {
            FileCacheEntry entry = (FileCacheEntry) it.next();
            System.out.println(entry.getFile().getName());
        }
    }
            
}






