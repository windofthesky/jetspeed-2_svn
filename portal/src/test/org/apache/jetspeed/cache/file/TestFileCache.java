/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *     "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" or
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
import org.apache.jetspeed.components.ComponentAwareTestSuite;
import org.apache.jetspeed.util.FileCopy;
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
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestFileCache.class);
        suite.setScript("org/apache/jetspeed/cache/file/filecache.container.groovy");
        return suite;
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
        String templateFile = "./test/testdata/psml/user/cachetest/default.psml";
        try
        {
            File file = new File(templateFile);            
            assertTrue(file.exists());

            createTestFiles(templateFile);

            // create the Cache  wake up after 10 seconds, cache size 20
            // FileCache cache = new FileCache(10, 20);

            // load the Cache
            File directory = new File("./test/testdata/psml/user/cachetest/");
            File[] files = directory.listFiles();
            for (int ix=0; ix < files.length; ix++)
            {
                if (files[ix].isDirectory())
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
            String testFile = "./test/testdata/psml/user/cachetest/testFile-" + ix + ".psml";
            FileCopy.copy(templateFile, testFile);
        }
    }

    private void removeTestFiles()
    {
        for (int ix=1; ix < 31; ix++)
        {
            String testFile = "./test/testdata/psml/user/cachetest/testFile-" + ix + ".psml";
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






