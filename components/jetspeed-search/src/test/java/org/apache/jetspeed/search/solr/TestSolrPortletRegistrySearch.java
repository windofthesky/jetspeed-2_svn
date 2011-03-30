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
package org.apache.jetspeed.search.solr;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.collections.MultiHashMap;
import org.apache.jetspeed.search.AbstractObjectHandler;
import org.apache.jetspeed.search.BaseParsedObject;
import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.search.SearchResults;
import org.apache.jetspeed.search.handlers.HandlerFactoryImpl;
import org.apache.jetspeed.test.JetspeedTestCase;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;

/**
 * TestSolrPortletRegistrySearch
 * @version $Id: TestSolrPortletRegistrySearch.java 1086464 2011-03-29 02:08:39Z woonsan $
 */
public class TestSolrPortletRegistrySearch extends JetspeedTestCase
{
    private CoreContainer coreContainer;
    private SolrServer server;
    private SearchEngine searchEngine;
    
    public TestSolrPortletRegistrySearch(String name)
    {
        super(name);
    }
    
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[]) 
    {
        junit.awtui.TestRunner.main( new String[] { TestSolrPortletRegistrySearch.class.getName() } );
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
        return new TestSuite( TestSolrPortletRegistrySearch.class );
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();
        HashMap mapping = new HashMap();
        mapping.put("java.util.HashMap", MapObjectHandler.class.getName());
        HandlerFactoryImpl hfi = new HandlerFactoryImpl(mapping);
        
        File solrFile = new File(getClass().getResource("/solr-test-home/solr.xml").toURI());
        File homeDir = solrFile.getParentFile();
        File dataDir = new File(homeDir, "data");
        String homeDirPath = homeDir.getCanonicalPath();
        String dataDirPath = dataDir.getCanonicalPath();
        System.setProperty("solr.solr.home", homeDirPath);
        System.setProperty("solr.data.dir", dataDirPath);
        coreContainer = new CoreContainer();
        coreContainer.load(homeDirPath, solrFile);
        
        server = new EmbeddedSolrServer(coreContainer, "js");
        server.deleteByQuery("*:*");
        server.commit();

        searchEngine = new SolrSearchEngineImpl(server, true, hfi);
    }
    
    protected void tearDown() throws Exception
    {
        super.tearDown();
        coreContainer.shutdown();
    }
    
    public void testSimpleSearch()
    {
        Map<String, String> paDemo = new HashMap<String, String>();
        paDemo.put("keyPrefix", "PortletApplication::");
        paDemo.put("description", "demo portlet application");
        paDemo.put("title", "Demo");
        paDemo.put("name", "demo");
        paDemo.put("type", ParsedObject.OBJECT_TYPE_PORTLET_APPLICATION);

        Map<String, String> paRss = new HashMap<String, String>();
        paRss.put("keyPrefix", "PortletApplication::");
        paRss.put("description", "rss portlet application");
        paRss.put("title", "RSS");
        paRss.put("name", "rss");
        paRss.put("type", ParsedObject.OBJECT_TYPE_PORTLET_APPLICATION);

        searchEngine.add(paDemo);
        searchEngine.add(paRss);
        
        SearchResults searchResults = searchEngine.search("demo");
        assertEquals(1, searchResults.size());
        
        searchResults = searchEngine.search("rss");
        assertEquals(1, searchResults.size());
        
        searchResults = searchEngine.search("application");
        assertEquals(2, searchResults.size());
        
        // adding one more; the search engine is expected to have duplicate index.
        searchEngine.add(paDemo);
        
        searchResults = searchEngine.search("demo");
        assertEquals(1, searchResults.size());
        
        searchResults = searchEngine.search("application");
        assertEquals(2, searchResults.size());
    }
    
    public void testPortletSearch()
    {
        Map<String, String> paDemo = new HashMap<String, String>();
        paDemo.put("keyPrefix", "PortletApplication::");
        paDemo.put("description", "demo portlet application");
        paDemo.put("title", "Demo");
        paDemo.put("name", "demo");
        paDemo.put("type", ParsedObject.OBJECT_TYPE_PORTLET_APPLICATION);

        Map<String, String> helloPortlet = new HashMap<String, String>();
        helloPortlet.put("keyPrefix", "PortletDefinition::");
        helloPortlet.put("description", "hello portlet definition");
        helloPortlet.put("title", "Hello World");
        helloPortlet.put("name", "hello");
        helloPortlet.put("type", ParsedObject.OBJECT_TYPE_PORTLET);

        Map<String, String> guessPortlet = new HashMap<String, String>();
        guessPortlet.put("keyPrefix", "PortletDefinition::");
        guessPortlet.put("description", "guess portlet definition");
        guessPortlet.put("title", "Guess - Pick A Number");
        guessPortlet.put("name", "guess");
        guessPortlet.put("type", ParsedObject.OBJECT_TYPE_PORTLET);

        searchEngine.add(paDemo);
        searchEngine.add(Arrays.asList(helloPortlet, guessPortlet));
        
        SearchResults searchResults = searchEngine.search("demo");
        assertEquals(1, searchResults.size());
        
        searchResults = searchEngine.search("hello");
        assertEquals(1, searchResults.size());
        
        searchResults = searchEngine.search("guess");
        assertEquals(1, searchResults.size());
        
        searchResults = searchEngine.search("definition");
        assertEquals(2, searchResults.size());
    }
    
    public void testPortletSearchByRichQuery()
    {
        Map<String, String> paDemo = new HashMap<String, String>();
        paDemo.put("keyPrefix", "PortletApplication::");
        paDemo.put("description", "demo portlet application");
        paDemo.put("title", "Demo");
        paDemo.put("name", "demo");
        paDemo.put("type", ParsedObject.OBJECT_TYPE_PORTLET_APPLICATION);

        Map<String, String> helloPortlet = new HashMap<String, String>();
        helloPortlet.put("keyPrefix", "PortletDefinition::");
        helloPortlet.put("description", "demo hello portlet definition");
        helloPortlet.put("title", "Hello World");
        helloPortlet.put("name", "hello");
        helloPortlet.put("type", ParsedObject.OBJECT_TYPE_PORTLET);

        Map<String, String> guessPortlet = new HashMap<String, String>();
        guessPortlet.put("keyPrefix", "PortletDefinition::");
        guessPortlet.put("description", "demo guess portlet definition");
        guessPortlet.put("title", "Guess - Pick A Number");
        guessPortlet.put("name", "guess");
        guessPortlet.put("type", ParsedObject.OBJECT_TYPE_PORTLET);

        searchEngine.add(paDemo);
        searchEngine.add(Arrays.asList(helloPortlet, guessPortlet));
        
        SearchResults searchResults = searchEngine.search("demo");
        assertEquals(3, searchResults.size());

        String query = ParsedObject.FIELDNAME_TYPE + ":\"" + ParsedObject.OBJECT_TYPE_PORTLET_APPLICATION + "\" AND ( demo )";
        searchResults = searchEngine.search(query);
        assertEquals(1, searchResults.size());
        
        query = ParsedObject.FIELDNAME_TYPE + ":\"" + ParsedObject.OBJECT_TYPE_PORTLET + "\" " +
            "AND NOT " + ParsedObject.FIELDNAME_TYPE + ":\"" + ParsedObject.OBJECT_TYPE_PORTLET_APPLICATION + "\" " + 
            "AND ( demo )";
        
        searchResults = searchEngine.search(query);
        assertEquals(2, searchResults.size());

        query = ParsedObject.FIELDNAME_TYPE + ":\"" + ParsedObject.OBJECT_TYPE_PORTLET + "\" " +
        "AND NOT " + ParsedObject.FIELDNAME_TYPE + ":\"" + ParsedObject.OBJECT_TYPE_PORTLET_APPLICATION + "\" " + 
        "AND ( hello )";
    
        searchResults = searchEngine.search(query);
        assertEquals(1, searchResults.size());
        
        query = ParsedObject.FIELDNAME_TYPE + ":\"" + ParsedObject.OBJECT_TYPE_PORTLET + "\" " +
        "AND NOT " + ParsedObject.FIELDNAME_TYPE + ":\"" + ParsedObject.OBJECT_TYPE_PORTLET_APPLICATION + "\" " + 
        "AND ( guess )";
    
        searchResults = searchEngine.search(query);
        assertEquals(1, searchResults.size());
    }
    
    public static class MapObjectHandler extends AbstractObjectHandler
    {
        private String keyPrefix;
        
        public ParsedObject parseObject(Object o)
        {
            BaseParsedObject result = null;
            
            if(o instanceof Map)
            {
                result = new BaseParsedObject();
                Map<String, String> map = (Map<String, String>) o;
                
                keyPrefix = map.get("keyPrefix");
                
                result.setDescription(map.get("description"));
                
                result.setTitle(map.get("title"));
                result.setKey(keyPrefix + map.get("name"));
                result.setType(map.get("type"));
                result.setClassName(map.get("class"));
                
                MultiHashMap fieldMap = new MultiHashMap();
                fieldMap.put(ParsedObject.ID, map.get("name"));
            }
            
            return result;
        }
    }
}
