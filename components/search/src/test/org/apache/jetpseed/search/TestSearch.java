/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.jetpseed.search;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.search.SearchResults;
import org.apache.jetspeed.search.handlers.HandlerFactoryImpl;
import org.apache.jetspeed.search.lucene.SearchEngineImpl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author jford
 *
 */
public class TestSearch extends TestCase
{
    
    private final static String INDEX_DIRECTORY = "./search_index";

    private File indexRoot;
    SearchEngine searchEngine;
    
    private URL jetspeedHomePage = null;
    
    public TestSearch(String name)
    {
        super(name);
        
        try {
            jetspeedHomePage = new URL("http://portals.apache.org/jetspeed-1/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        indexRoot = new File(INDEX_DIRECTORY);
    }
    
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[]) 
    {
        junit.awtui.TestRunner.main( new String[] { TestSearch.class.getName() } );
    }
 
    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite() 
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite( TestSearch.class );
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();
        HashMap mapping = new HashMap();
        mapping.put("java.net.URL", "org.apache.jetspeed.search.handlers.URLToDocHandler");
        
        HandlerFactoryImpl hfi = new HandlerFactoryImpl(mapping);
        
        searchEngine = new SearchEngineImpl(indexRoot.getPath(), null, true, hfi);
    }
    
    protected void tearDown() throws Exception
    {
        File[] indexFiles = indexRoot.listFiles();
        if(indexFiles != null)
        {
	        for(int i=0; i<indexFiles.length; i++)
	        {
	            File file = indexFiles[i];
	            file.delete();
	        }
        }
        
        indexRoot.delete();
    }
    
    public void testRemoveWebPage() throws Exception
    {
        //System.out.println("search home = " + JetspeedResources.getString("services.SearchService.directory"));
        
        assertNotNull("Created URL to Jetspeed Home Page",  jetspeedHomePage);
        assertTrue("Removing non-existent index entry", searchEngine.remove(jetspeedHomePage) == false);
        assertTrue("Adding to index", searchEngine.add(jetspeedHomePage));
        assertTrue("Removing from index", searchEngine.remove(jetspeedHomePage));
    }
    
    public void testPutWebPage() throws Exception
    {
        //System.out.println("search home = " + JetspeedResources.getString("services.SearchService.directory"));
        
        assertNotNull("Created URL to Jetspeed Home Page",  jetspeedHomePage);
        assertTrue("Adding to index", searchEngine.add(jetspeedHomePage));
        assertTrue("Adding to index", searchEngine.add(new URL("http://www.java.net")));
        assertTrue("Adding to index", searchEngine.add(new URL("http://portals.apache.org")));
    }
    
    /**
     *
     * @throws Exception
     */
    public void testVerifyJetspeedSearch() throws Exception
    {
        //because tear down deletes files, need to do add again
        testPutWebPage();
        
        SearchResults results  = searchEngine.search("YourResultsBelongToUs");
        //System.out.println("Query 'YourResultsBelongToUs' hits = " + results.size());
        assertTrue(" Hit count == 0", results.size() == 0);
        Iterator resultIter = results.iterator();
        while (resultIter.hasNext())
        {
            ParsedObject result = (ParsedObject) resultIter.next();
            
            System.out.println("Score = " + result.getScore());
            System.out.println("title = " + result.getTitle());
            System.out.println("summary = " + result.getDescription());
            System.out.println("url = " + result.getURL());
        }
    }
    
    public void testVerifyJetspeedSearch1() throws Exception
    {
//      because tear down deletes files, need to do add again
        testPutWebPage();
        
        SearchResults results  = searchEngine.search("Jetspeed");
        assertTrue(" Hit count == 0", results.size() > 0);
        
        Iterator resultIter = results.iterator();
        while (resultIter.hasNext())
        {
            ParsedObject result = (ParsedObject) resultIter.next();
            System.out.println("Score = " + result.getScore());
            System.out.println("title = " + result.getTitle());
            System.out.println("summary = " + result.getDescription());
            System.out.println("url = " + result.getURL());
        }
    }
    
    public void testVerifyJetspeedSearch2() throws Exception
    {
//      because tear down deletes files, need to do add again
        testPutWebPage();
        
        SearchResults results  = searchEngine.search("community");
        assertTrue(" Hit count == 0", results.size() > 0);
        
        Iterator resultIter = results.iterator();
        while (resultIter.hasNext())
        {
            ParsedObject result = (ParsedObject) resultIter.next();
            System.out.println("Score = " + result.getScore());
            System.out.println("title = " + result.getTitle());
            System.out.println("summary = " + result.getDescription());
            System.out.println("url = " + result.getURL());
        }
    }
}
