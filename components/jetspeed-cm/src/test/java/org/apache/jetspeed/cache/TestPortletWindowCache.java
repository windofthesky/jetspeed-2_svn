/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.cache;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.jetspeed.cache.impl.EhCacheImpl;
import org.apache.jetspeed.cache.impl.EhPortletWindowCache;
import org.apache.jetspeed.container.PortletEntity;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.PortletWindowID;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.core.stub.VoidStub;



/**
 * 
 * Tests for {@link EhPortletWindowCache}.
 * 
 * @author <a href="mailto:scott.t.weaver@gmail.com">Scott T. Weaver</a>
 *
 */
public class TestPortletWindowCache extends MockObjectTestCase
{
    private static final String WINDOW_ID = "window1";
    private static final String ENTITY_ID = "entity1";
    
    
    private Mock cacheMock;
    private Mock windowMock;
    private Mock entityMock;
    private Mock oidMock;
    
    protected void setUp() throws Exception
    {
        super.setUp();
        cacheMock = mock(Ehcache.class);
        windowMock = mock(SerializablePortletWindow.class);
        entityMock = mock(PortletEntity.class);
        oidMock = mock(PortletWindowID.class);
    }    

    public void testSimplePutAndGet()
    {

        PortletWindow window = (PortletWindow) windowMock.proxy();
        Element element = new Element(WINDOW_ID, window);
        PortletWindowID wid = (PortletWindowID) oidMock.proxy();
        oidMock.expects(atLeastOnce()).method("getStringId").will(returnValue(WINDOW_ID));
        windowMock.expects(once()).method("getId").withNoArguments().will(returnValue(wid));
        windowMock.expects(once()).method("getPortletEntity").withNoArguments().will(returnValue(entityMock.proxy()));
        entityMock.expects(once()).method("getId").withNoArguments().will(returnValue(ENTITY_ID));
        cacheMock.expects(once()).method("put").with(eq(element));
        cacheMock.expects(atLeastOnce()).method("get").with(eq(WINDOW_ID)).will(returnValue(element));
        
        
        Ehcache cache = (Ehcache) cacheMock.proxy();        
        PortletWindowCache windowCache = new EhPortletWindowCache(cache);      
        windowCache.putPortletWindow(window);
        
        assertNotNull(windowCache.getPortletWindow(WINDOW_ID));
        assertEquals(windowCache.getPortletWindow(WINDOW_ID), window);
        
        verify();
    }
    
    public void testGetByPortletEntity()
    {

        SerializablePortletWindow window = (SerializablePortletWindow) windowMock.proxy();
        Element element = new Element(WINDOW_ID, window);
        
        PortletWindowID oid = (PortletWindowID) oidMock.proxy();
        oidMock.expects(atLeastOnce()).method("getStringId").will(returnValue(WINDOW_ID));
        cacheMock.expects(once()).method("put").with(eq(element));
        cacheMock.expects(once()).method("get").with(eq(WINDOW_ID)).will(returnValue(element));
        windowMock.expects(once()).method("getId").withNoArguments().will(returnValue(oid));
        windowMock.expects(once()).method("getPortletEntity").withNoArguments().will(returnValue(entityMock.proxy()));
        entityMock.expects(once()).method("getId").withNoArguments().will(returnValue(ENTITY_ID));
        
        Ehcache cache = (Ehcache) cacheMock.proxy();        
        PortletWindowCache windowCache = new EhPortletWindowCache(cache);  
        windowCache.putPortletWindow(window);
        
        PortletWindow fromCache = windowCache.getPortletWindowByEntityId(ENTITY_ID);
        assertNotNull(fromCache);
        
        verify();        
    }
    
    public void testRemove()
    {
        SerializablePortletWindow window = (SerializablePortletWindow) windowMock.proxy();
        Element element = new Element(WINDOW_ID, window);
        
        PortletWindowID oid = (PortletWindowID) oidMock.proxy();
        oidMock.expects(atLeastOnce()).method("getStringId").will(returnValue(WINDOW_ID));
        
        cacheMock.expects(once()).method("put").with(eq(element));
        cacheMock.expects(exactly(2)).method("get").with(eq(WINDOW_ID)).will(returnValue(element));
        windowMock.expects(once()).method("getId").withNoArguments().will(returnValue(oid));
        windowMock.expects(exactly(2)).method("getPortletEntity").withNoArguments().will(returnValue(entityMock.proxy()));
        entityMock.expects(exactly(2)).method("getId").withNoArguments().will(returnValue(ENTITY_ID));
        
        
        cacheMock.expects(once()).method("removeQuiet").with(eq(WINDOW_ID)).will(returnValue(true));
        
        
        Ehcache cache = (Ehcache) cacheMock.proxy();        
        PortletWindowCache windowCache = new EhPortletWindowCache(cache);  
        windowCache.putPortletWindow(window);
        
        windowCache.removePortletWindow(WINDOW_ID);
        assertNull(windowCache.getPortletWindowByEntityId(ENTITY_ID));
        
        verify();        
    }
    
    public void testRemoveByEntityId()
    {
        SerializablePortletWindow window = (SerializablePortletWindow) windowMock.proxy();
        Element element = new Element(WINDOW_ID, window);
        
        PortletWindowID oid = (PortletWindowID) oidMock.proxy();
        oidMock.expects(atLeastOnce()).method("getStringId").will(returnValue(WINDOW_ID));
        
        cacheMock.expects(once()).method("put").with(eq(element));
        cacheMock.expects(exactly(3)).method("get").with(eq(WINDOW_ID)).will(onConsecutiveCalls(returnValue(element), returnValue(element), new VoidStub()));
        windowMock.expects(exactly(2)).method("getId").withNoArguments().will(returnValue(oid));
        windowMock.expects(once()).method("getPortletEntity").withNoArguments().will(returnValue(entityMock.proxy()));
        entityMock.expects(once()).method("getId").withNoArguments().will(returnValue(ENTITY_ID));
        
        
        cacheMock.expects(atLeastOnce()).method("removeQuiet").with(eq(WINDOW_ID)).will(returnValue(true));
        
        
        Ehcache cache = (Ehcache) cacheMock.proxy();        
        PortletWindowCache windowCache = new EhPortletWindowCache(cache);  
        windowCache.putPortletWindow(window);
        
        windowCache.removePortletWindowByPortletEntityId(ENTITY_ID);
        assertNull(windowCache.getPortletWindow(WINDOW_ID));
        
        verify();        
    }
    
    public void testGetAllPortletWindows()
    {        
        PortletWindow window = (PortletWindow) windowMock.proxy();
        PortletWindow window2 = (PortletWindow) mock(SerializablePortletWindow.class).proxy();
        PortletWindow window3 = (PortletWindow) mock(SerializablePortletWindow.class).proxy();
        
        List keys = Arrays.asList(new String[] {WINDOW_ID, "window2", "window3"});
        
        cacheMock.expects(once()).method("getKeys").withNoArguments().will(returnValue(keys));        
        cacheMock.expects(once()).method("get").with(eq(WINDOW_ID)).will(returnValue(new Element(WINDOW_ID, window)));
        cacheMock.expects(once()).method("get").with(eq("window2")).will(returnValue(new Element("window2", window2)));
        cacheMock.expects(once()).method("get").with(eq("window3")).will(returnValue(new Element("window3", window3)));
        
        PortletWindowCache windowCache = new EhPortletWindowCache((Ehcache) cacheMock.proxy());
        
        Set allPortletWindows = windowCache.getAllPortletWindows();
        assertNotNull(allPortletWindows);
        assertEquals(3, allPortletWindows.size());
    }
    
    public void testUnexpected()
    {
//        PortletWindowCache windowCache = new EhPortletWindowCache((Ehcache) cacheMock.proxy());
//        cacheMock.proxy();
//        windowCache.getPortletWindow(null);
//        verify();
    }
    
    /**
     * We need this class to test the cache as the {@link EhCacheImpl} object only
     * allows {@link Serializable} objects to be cached.
     * 
     * @author <a href="mailto:scott.t.weaver@gmail.com">Scott T. Weaver</a>
     *
     */
    private interface SerializablePortletWindow extends PortletWindow, Serializable
    {
        
    }
    

}
