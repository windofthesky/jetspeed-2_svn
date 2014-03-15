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
package org.apache.jetspeed.page.document.psml;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sf.ehcache.CacheManager;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.impl.EhCacheImpl;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator;
import org.apache.jetspeed.om.folder.psml.FolderMetaDataImpl;
import org.apache.jetspeed.om.page.Document;
import org.apache.jetspeed.page.document.DocumentHandler;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.test.JetspeedTestCase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * TestCastorFileSystemDocumentHandler
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id$
 *  
 */
public class TestCastorFileSystemDocumentHandler extends JetspeedTestCase
{
    protected FileCache cache;
    protected CastorFileSystemDocumentHandler folderMetaDataDocumentHandler;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        IdGenerator idGen = new JetspeedIdGenerator(65536,"P-","");
        cache = new FileCache(new EhCacheImpl(CacheManager.getInstance().getEhcache("pageFileCache")), 10);
        
        folderMetaDataDocumentHandler = new CastorFileSystemDocumentHandler(
            idGen,
            "/JETSPEED-INF/castor/page-mapping.xml",
            "folder.metadata",
            FolderMetaDataImpl.class,
            getBaseDir()+"src/test/testdata/pages",
            cache);
            
        Map<String,DocumentHandler> handlerMap = new HashMap<String,DocumentHandler>();
        handlerMap.put("folder.metadata", folderMetaDataDocumentHandler);
        DocumentHandlerFactory handlerFactory = new DocumentHandlerFactoryImpl(handlerMap);
        folderMetaDataDocumentHandler.setHandlerFactory(handlerFactory);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();

        cache.evictAll();
        folderMetaDataDocumentHandler.shutdown();
    }

    /**
     * Defines the testcase name for JUnit.
     * 
     * @param name
     *            the testcase's name.
     */
    public TestCastorFileSystemDocumentHandler( String name )
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
        junit.awtui.TestRunner.main(new String[]{TestCastorFileSystemDocumentHandler.class.getName()});
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
        return new TestSuite(TestCastorFileSystemDocumentHandler.class);
    }
    
    public void testFolderMetaData() throws Exception
    {
        Document doc = folderMetaDataDocumentHandler.getDocument("/folder1/folder.metadata", false);
        assertNotNull(doc);
        String title = doc.getTitle();
        assertEquals("Default Title for Folder 1", title);
    }

    public void testFolderMetaDataInParallel() throws Exception
    {
        Thread [] threads = new Thread[10];
        int i;
        final List<Exception> exceptions = new ArrayList<Exception>(10);
        
        for (i = 0; i < threads.length; i++)
        {
            threads[i] = new Thread(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            Document doc = folderMetaDataDocumentHandler.getDocument("/folder1/folder.metadata", false);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace(System.out);
                            exceptions.add(e);
                        }
                    }
                });
        }
        
        for (i = 0; i < threads.length; i++)
        {
            threads[i].start();
        }
        
        for (i = 0; i < threads.length; i++)
        {
            threads[i].join();
        }
        
        assertTrue("folderMetaDataDocumentHandler.getDocument() is not thread-safe!", exceptions.size() == 0);
    }

}
