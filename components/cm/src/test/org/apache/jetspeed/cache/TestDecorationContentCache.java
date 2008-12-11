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
package org.apache.jetspeed.cache;

import java.io.Serializable;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.jetspeed.cache.impl.EhCacheConfigResource;
import org.apache.jetspeed.cache.impl.EhDecorationContentCacheImpl;
import org.apache.jetspeed.cache.impl.JetspeedCacheKeyGenerator;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;

/**
 * <p>
 * Test Content Cache
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: TestCachingInterceptors.java 516448 2007-03-09 16:25:47Z ate $
 *
 */
public class TestDecorationContentCache extends TestCase
{
       
    public void testContentCacheByUser() throws Exception
    {
        // initialize ehCache
        EhCacheConfigResource.getInstance(EhCacheConfigResource.EHCACHE_CONFIG_RESOURCE_DEFAULT, true);
        CacheManager cacheManager = new CacheManager();
        Cache ehContentCache = new Cache("ehDecorationContentCache", 10000, false, false, 28800, 28800);
        cacheManager.addCache(ehContentCache);
        ehContentCache.setCacheManager(cacheManager);       
        
        // initial Jetspeed caches
        List segments = new LinkedList();
        segments.add("username");
        segments.add("pipeline");
        segments.add("windowid");
        ContentCacheKeyGenerator generator = new JetspeedCacheKeyGenerator(segments);
        JetspeedCache contentCache = new EhDecorationContentCacheImpl(ehContentCache, generator);
        
        // create the mock request context
        MockHttpServletRequest request = new MockHttpServletRequest();       
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setUserPrincipal(new MockPrincipal("david"));
        MockRequestContext context = new MockRequestContext(request, response);
        
        // create a simple key
        String window1 = "/default-page.psml";
        ContentCacheKey cckey1 = contentCache.createCacheKey(context, window1);
        assertEquals(cckey1.getKey(), "david/portal//default-page.psml");

        // create a another key for desktop
        String window2 = "/about.psml";
        context.getParameterMap().put("encoder", "desktop");
        ContentCacheKey cckey2 = contentCache.createCacheKey(context, window2);
        assertEquals(cckey2.getKey(), "david/desktop//about.psml");
        
        // create some PortletContent mock objects
        MockTheme theme1 = new MockTheme("/default-page.psml");
        MockTheme theme2 = new MockTheme("/about.psml");
        
        // put it in the cache
        CacheElement element1 = contentCache.createElement(cckey1, theme1);
        contentCache.put(element1);
        CacheElement element2 = contentCache.createElement(cckey2, theme2);
        contentCache.put(element2);
        
        // assert the gets
        Object result1 = contentCache.get(cckey1);
        assertNotNull(result1);
        System.out.println("result 1 = " + result1);
        Object result2 = contentCache.get(cckey2);
        assertNotNull(result2);
        System.out.println("result 2 = " + result2);
        
        // assert isKey Apis        
        assertTrue(contentCache.isKeyInCache(cckey1));

        
        // test removes
        contentCache.remove(cckey1);
        assertFalse(contentCache.isKeyInCache(cckey1));        
        assertTrue(contentCache.isKeyInCache(cckey2));
        
        // test user stuff
        request.setUserPrincipal(new MockPrincipal("sean"));        
        // create a simple key
        String window3 = "/default-page.psml";
        ContentCacheKey cckey3 = contentCache.createCacheKey(context, window3);
        assertEquals(cckey3.getKey(), "sean/desktop//default-page.psml");

        // create a another key for desktop
        String window4 = "/about.psml";
        ContentCacheKey cckey4 = contentCache.createCacheKey(context, window4);
        assertEquals(cckey4.getKey(), "sean/desktop//about.psml");
        
        // create some MockTheme objects
        MockTheme theme3 = new MockTheme("/default-page.psml");
        MockTheme theme4 = new MockTheme("/about.psml");
        
        // put it in the cache
        CacheElement element3 = contentCache.createElement(cckey3, theme3);
        contentCache.put(element3);
        CacheElement element4 = contentCache.createElement(cckey4, theme4);
        contentCache.put(element4);

        // assert 3 and 4
        assertTrue(contentCache.isKeyInCache(cckey3));
        assertTrue(contentCache.isKeyInCache(cckey4));
        
        // remove for user
        contentCache.evictContentForUser("sean");
        assertFalse(contentCache.isKeyInCache(cckey3));
        assertFalse(contentCache.isKeyInCache(cckey4));
        assertTrue(contentCache.isKeyInCache(cckey2));        
    }
    
    public void testContentCacheBySession() throws Exception
    {
        // initialize ehCache
        EhCacheConfigResource.getInstance(EhCacheConfigResource.EHCACHE_CONFIG_RESOURCE_DEFAULT, true);
        CacheManager cacheManager = new CacheManager();
        Cache ehContentCache = new Cache("ehDecorationContentCache", 10000, false, false, 28800, 28800);
        cacheManager.addCache(ehContentCache);
        ehContentCache.setCacheManager(cacheManager);       
        
        // initial Jetspeed caches
        List segments = new LinkedList();
        segments.add("sessionid");
        segments.add("pipeline");
        segments.add("windowid");
        ContentCacheKeyGenerator generator = new JetspeedCacheKeyGenerator(segments);
        JetspeedCache contentCache = new EhDecorationContentCacheImpl(ehContentCache, generator);
        
        // create the mock request context
        MockHttpServletRequest request = new MockHttpServletRequest();       
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setUserPrincipal(new MockPrincipal("david"));
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        String sessionId = session.getId();

        MockRequestContext context = new MockRequestContext(request, response);
        
        // create a simple key
        String window1 = "/default-page.psml";
        ContentCacheKey cckey1 = contentCache.createCacheKey(context, window1);
        assertEquals(cckey1.getKey(), sessionId + "/portal//default-page.psml");

        // create a another key for desktop
        String window2 = "/about.psml";
        context.getParameterMap().put("encoder", "desktop");
        ContentCacheKey cckey2 = contentCache.createCacheKey(context, window2);
        assertEquals(cckey2.getKey(), sessionId + "/desktop//about.psml");
        
        // create some MockTheme objects
        MockTheme theme1 = new MockTheme("/default-page.psml");
        MockTheme theme2 = new MockTheme("/about.psml");
        
        // put it in the cache
        CacheElement element1 = contentCache.createElement(cckey1, theme1);
        contentCache.put(element1);
        CacheElement element2 = contentCache.createElement(cckey2, theme2);
        contentCache.put(element2);
        
        // assert the gets
        Object result1 = contentCache.get(cckey1);
        assertNotNull(result1);
        System.out.println("result 1 = " + result1);
        Object result2 = contentCache.get(cckey2);
        assertNotNull(result2);
        System.out.println("result 2 = " + result2);
        
        // assert isKey Apis        
        assertTrue(contentCache.isKeyInCache(cckey1));
                
        
        // test removes
        contentCache.remove(cckey1);
        assertFalse(contentCache.isKeyInCache(cckey1));        
        assertTrue(contentCache.isKeyInCache(cckey2));
        
        // test user stuff
        session = new MockHttpSession();
        request.setSession(session);        
        sessionId = session.getId();        
        request.setUserPrincipal(new MockPrincipal("sean"));        
        // create a simple key
        String window3 = "/default-page.psml";
        ContentCacheKey cckey3 = contentCache.createCacheKey(context, window3);
        assertEquals(cckey3.getKey(), sessionId + "/desktop//default-page.psml");

        // create a another key for desktop
        String window4 = "about.psml";
        ContentCacheKey cckey4 = contentCache.createCacheKey(context, window4);
        assertEquals(cckey4.getKey(), sessionId + "/desktop/about.psml");
        
        // create some PortletContent mock objects
        MockTheme theme3 = new MockTheme("/default-page.psml");
        MockTheme theme4 = new MockTheme("/about.psml");
        
        // put it in the cache
        CacheElement element3 = contentCache.createElement(cckey3, theme3);
        contentCache.put(element3);
        CacheElement element4 = contentCache.createElement(cckey4, theme4);
        contentCache.put(element4);

        // assert 3 and 4
        assertTrue(contentCache.isKeyInCache(cckey3));
        assertTrue(contentCache.isKeyInCache(cckey4));
        
        // remove for user
        contentCache.evictContentForSession(sessionId);
        assertFalse(contentCache.isKeyInCache(cckey3));
        assertFalse(contentCache.isKeyInCache(cckey4));
        assertTrue(contentCache.isKeyInCache(cckey2));                      
    }
    
    class MockPrincipal implements Principal
    {
        private String name;
        public MockPrincipal(String name)
        {
            this.name = name;
        }
        
        public String getName()
        {
            return name;
        }
    }
    
    class MockTheme implements Serializable
    {
        private String pageId;
        
        public MockTheme(String pageId)
        {
            this.pageId = pageId;
        }
        
        public String toString()
        {
            return this.pageId;
        }
    } 
    
}
