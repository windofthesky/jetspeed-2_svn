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

import java.io.PrintWriter;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import junit.framework.TestCase;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.cache.impl.EhCacheConfigResource;
import org.apache.jetspeed.cache.impl.EhPortletContentCacheImpl;
import org.apache.jetspeed.cache.impl.JetspeedCacheKeyGenerator;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.util.KeyValue;

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
public class TestContentCache extends TestCase
{
       
    public void testContentCacheByUser() throws Exception
    {
        // initialize ehCache
        EhCacheConfigResource.getInstance(EhCacheConfigResource.EHCACHE_CONFIG_RESOURCE_DEFAULT, true);
        CacheManager cacheManager = new CacheManager();
        Cache ehContentCache = new Cache("ehPortletContentCache", 10000, false, false, 28800, 28800);
        cacheManager.addCache(ehContentCache);
        ehContentCache.setCacheManager(cacheManager);       
        
        // initial Jetspeed caches
        List segments = new LinkedList();
        segments.add("username");
        segments.add("pipeline");
        segments.add("windowid");
        ContentCacheKeyGenerator generator = new JetspeedCacheKeyGenerator(segments);
        JetspeedCache contentCache = new EhPortletContentCacheImpl(ehContentCache, generator);
        
        // create the mock request context
        MockHttpServletRequest request = new MockHttpServletRequest();       
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setUserPrincipal(new MockPrincipal("david"));
        MockRequestContext context = new MockRequestContext(request, response);
        
        // create a simple key
        String window1 = "555-01";
        ContentCacheKey cckey1 = contentCache.createCacheKey(context, window1);
        assertEquals(cckey1.getKey(), "david/portal/555-01");

        // create a another key for desktop
        String window2 = "555-02";
        context.getParameterMap().put("encoder", "desktop");
        ContentCacheKey cckey2 = contentCache.createCacheKey(context, window2);
        assertEquals(cckey2.getKey(), "david/desktop/555-02");
        
        // create some PortletContent mock objects
        PortletContent content1 = new MockPortletContent(cckey1, 100, "ContentOne", "content1content1content1content1");
        PortletContent content2 = new MockPortletContent(cckey2, 200, "ContentTwo", "content2content2content2content2");
        
        // put it in the cache
        CacheElement element1 = contentCache.createElement(cckey1, content1);
        contentCache.put(element1);
        CacheElement element2 = contentCache.createElement(cckey2, content2);
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
        String window3 = "555-03";
        ContentCacheKey cckey3 = contentCache.createCacheKey(context, window3);
        assertEquals(cckey3.getKey(), "sean/desktop/555-03");

        // create a another key for desktop
        String window4 = "555-04";
        ContentCacheKey cckey4 = contentCache.createCacheKey(context, window4);
        assertEquals(cckey4.getKey(), "sean/desktop/555-04");
        
        // create some PortletContent mock objects
        PortletContent content3 = new MockPortletContent(cckey3, 300, "ContentThree", "content3content3content3content3");
        PortletContent content4 = new MockPortletContent(cckey4, 400, "ContentTwo", "content4content4content4content4");
        
        // put it in the cache
        CacheElement element3 = contentCache.createElement(cckey3, content3);
        contentCache.put(element3);
        CacheElement element4 = contentCache.createElement(cckey4, content4);
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
        Cache ehContentCache = new Cache("ehPortletContentCache", 10000, false, false, 28800, 28800);
        cacheManager.addCache(ehContentCache);
        ehContentCache.setCacheManager(cacheManager);       
        
        // initial Jetspeed caches
        List segments = new LinkedList();
        segments.add("sessionid");
        segments.add("pipeline");
        segments.add("windowid");
        ContentCacheKeyGenerator generator = new JetspeedCacheKeyGenerator(segments);
        JetspeedCache contentCache = new EhPortletContentCacheImpl(ehContentCache, generator);
        
        // create the mock request context
        MockHttpServletRequest request = new MockHttpServletRequest();       
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.setUserPrincipal(new MockPrincipal("david"));
        MockHttpSession session = new MockHttpSession();
        request.setSession(session);
        String sessionId = session.getId();

        MockRequestContext context = new MockRequestContext(request, response);
        
        // create a simple key
        String window1 = "555-01";
        ContentCacheKey cckey1 = contentCache.createCacheKey(context, window1);
        assertEquals(cckey1.getKey(), sessionId + "/portal/555-01");

        // create a another key for desktop
        String window2 = "555-02";
        context.getParameterMap().put("encoder", "desktop");
        ContentCacheKey cckey2 = contentCache.createCacheKey(context, window2);
        assertEquals(cckey2.getKey(), sessionId + "/desktop/555-02");
        
        // create some PortletContent mock objects
        PortletContent content1 = new MockPortletContent(cckey1, 100, "ContentOne", "content1content1content1content1");
        PortletContent content2 = new MockPortletContent(cckey2, 200, "ContentTwo", "content2content2content2content2");
        
        // put it in the cache
        CacheElement element1 = contentCache.createElement(cckey1, content1);
        contentCache.put(element1);
        CacheElement element2 = contentCache.createElement(cckey2, content2);
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
        String window3 = "555-03";
        ContentCacheKey cckey3 = contentCache.createCacheKey(context, window3);
        assertEquals(cckey3.getKey(), sessionId + "/desktop/555-03");

        // create a another key for desktop
        String window4 = "555-04";
        ContentCacheKey cckey4 = contentCache.createCacheKey(context, window4);
        assertEquals(cckey4.getKey(), sessionId + "/desktop/555-04");
        
        // create some PortletContent mock objects
        PortletContent content3 = new MockPortletContent(cckey3, 300, "ContentThree", "content3content3content3content3");
        PortletContent content4 = new MockPortletContent(cckey4, 400, "ContentTwo", "content4content4content4content4");
        
        // put it in the cache
        CacheElement element3 = contentCache.createElement(cckey3, content3);
        contentCache.put(element3);
        CacheElement element4 = contentCache.createElement(cckey4, content4);
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
        
    class MockPortletContent implements PortletContent
    {
        private boolean complete = false;
        private ContentCacheKey cacheKey;
        private int expiration = 0;
        private String title;
        private String content;
        
        
        MockPortletContent(ContentCacheKey cacheKey, int expiration, String title, String content)
        {
            this.cacheKey = cacheKey;
            this.expiration = expiration;
            this.title = title;
            this.content = content;
        }

       
        public PrintWriter getWriter()
        {
            return null;
        }

        public void init()
        {
        }

        public void release()
        {
        }

        public String toString()
        {
            return content;
        }

        public void writeTo( java.io.Writer out ) throws java.io.IOException
        {
        }

        public char[] toCharArray()
        {
            return content.toCharArray();
        }

        public boolean isComplete()
        {
            return complete;
        }

        void setComplete(boolean state, boolean notify)
        {
            this.complete = state;
        }
        
        public String getContent()
        {
            return toString();
        }
        /**
         * <p>
         * complete
         * </p>
         *
         * @see org.apache.jetspeed.aggregator.PortletContent#complete()
         * 
         */
        public void complete()
        {
           setComplete(true, true);
        }
        
        // error case, don't notify 
        public void completeWithError()
        {
            setComplete(true, false);
        }
        
        public ContentCacheKey getCacheKey()
        {
            return cacheKey;
        }
       
        public int getExpiration()
        {
            return expiration;
        }
        
        public void setExpiration(int expiration)
        {
            this.expiration = expiration;
        }
        
        public String getTitle()
        {
            return title;
        }
        
        public void setTitle(String title)
        {
            this.title = title;
        }


        public String getContentType()
        {
            // TODO Auto-generated method stub
            return null;
        }


        public void reset()
        {
            // TODO Auto-generated method stub
            
        }


        public void resetBuffer()
        {
            // TODO Auto-generated method stub
            
        }


        public void setContentType(String contentType)
        {
            // TODO Auto-generated method stub
            
        }


        public void addHeadElement(HeadElement element, String keyHint)
        {
            // TODO Auto-generated method stub
            
        }


        public List<KeyValue<String, HeadElement>> getHeadElements()
        {
            // TODO Auto-generated method stub
            return null;
        }


        public PortletMode getPortletMode()
        {
            return PortletMode.VIEW;
        }
        
        public WindowState getWindowState()
        {
            return WindowState.NORMAL;
        }
    }
        
}
