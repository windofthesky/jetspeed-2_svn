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
package org.apache.jetspeed.factory;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.PortletURLGenerationListener;
import javax.portlet.ResourceURL;
import javax.portlet.filter.FilterConfig;
import javax.portlet.filter.PortletFilter;
import javax.servlet.ServletContext;

import junit.framework.TestCase;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.om.portlet.Filter;
import org.apache.jetspeed.om.portlet.Listener;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.util.DelegatingObjectProxy;
import org.apache.pluto.container.impl.RequestDispatcherServiceImpl;
import org.apache.pluto.container.om.portlet.PortletApplicationDefinition;

import com.mockrunner.mock.web.MockServletContext;

public class TestPortletFactory extends TestCase
{
    private PortletFactory portletFactory;
    
    @Override
    public void setUp() throws Exception
    {
        this.portletFactory = new JetspeedPortletFactory(new RequestDispatcherServiceImpl(), true, true);
        PortalConfiguration configuration = (PortalConfiguration) DelegatingObjectProxy.createProxy(new Class [] { PortalConfiguration.class },
                                                                                                    new MockPortalConfiguration());
        PortalContext portalContext = (PortalContext) DelegatingObjectProxy.createProxy(new Class [] { PortalContext.class },
                                                                                        new MockPortalContext(configuration));
        this.portletFactory.setPortalContext(portalContext);        
    }
    
    public void testPortletInstance() throws PortletException
    {
        ServletContext servletContext = new MockServletContext();
        
        PortletApplication demoPA = 
            (PortletApplication) DelegatingObjectProxy.createProxy(new Class [] { PortletApplication.class, PortletApplicationDefinition.class }, 
                                                                   new MockPortletApplication("demo", null, null));
        
        this.portletFactory.registerPortletApplication(demoPA, Thread.currentThread().getContextClassLoader());
        
        PortletDefinition mockPickANumberPD = 
            (PortletDefinition) DelegatingObjectProxy.createProxy(new Class [] { PortletDefinition.class, org.apache.pluto.container.om.portlet.PortletDefinition.class }, 
                                                                  new MockPortletDefinition(demoPA, "MockPickANumber", MockPickANumber.class.getName()));
        
        
        PortletInstance portletInstance = this.portletFactory.getPortletInstance(servletContext, mockPickANumberPD);
        assertNotNull("portletInstance is null!", portletInstance);
        assertTrue("the porlet is not proxied version.", portletInstance.isProxyInstance());
        
        PortletInstance portletInstance2 = this.portletFactory.getPortletInstance(servletContext, mockPickANumberPD);
        assertTrue("the porlet is not cached.", portletInstance == portletInstance2);
        
        PortletInstance portletInstanceNoProxy = this.portletFactory.getPortletInstance(servletContext, mockPickANumberPD, false);
        assertNotNull("portletInstance is null!", portletInstanceNoProxy);
        assertFalse("the porlet is proxied version.", portletInstanceNoProxy.isProxyInstance());
        
        this.portletFactory.unregisterPortletApplication(demoPA);
        
        PortletInstance porletInstanceAfterUnregistration = null;
        
        try
        {
            porletInstanceAfterUnregistration = this.portletFactory.getPortletInstance(servletContext, mockPickANumberPD);
        }
        catch (PortletException e)
        {
        }
        
        assertNull("portletInstance cannot be retrieved after unregistration.", porletInstanceAfterUnregistration);
    }
    
    public void testPortletFilterInstance() throws Exception
    {
        Map<String, Filter> filtersMap = new HashMap<String, Filter>();
        Filter mockFilter = 
            (Filter) DelegatingObjectProxy.createProxy(new Class [] { Filter.class,org.apache.pluto.container.om.portlet.Filter.class },
                                                       new MockFilter(MockTestFilter1.class.getName()));
        filtersMap.put("testfilter1", mockFilter);
        
        PortletApplication demoPA = 
            (PortletApplication) DelegatingObjectProxy.createProxy(new Class [] { PortletApplication.class, PortletApplicationDefinition.class }, 
                                                                   new MockPortletApplication("demo", filtersMap, null));
        
        this.portletFactory.registerPortletApplication(demoPA, Thread.currentThread().getContextClassLoader());
        
        PortletFilterInstance filterInstance = this.portletFactory.getPortletFilterInstance(demoPA, "testfilter1");
        assertNotNull("filterInstance is null!", filterInstance);
        
        assertTrue("The filter is not an instance of MockTestFilter1", filterInstance.getRealPortletFilter() instanceof MockTestFilter1);
        
        PortletFilterInstance filterInstance2 = this.portletFactory.getPortletFilterInstance(demoPA, "testfilter1");
        assertNotNull("filterInstances are not cached!", filterInstance == filterInstance2);

        this.portletFactory.unregisterPortletApplication(demoPA);
        
        PortletFilterInstance filterInstanceAfterUnregistration = null;
        
        try
        {
            filterInstanceAfterUnregistration = this.portletFactory.getPortletFilterInstance(demoPA, "testfilter1");
        }
        catch (PortletException e) 
        {
        }
        
        assertNull("filterInstance cannot be retrieved after unregistration.", filterInstanceAfterUnregistration);
    }
    
    public void testPortletListener() throws Exception
    {
        List<Listener> listeners = new ArrayList<Listener>();
        Listener mockListener =
            (Listener) DelegatingObjectProxy.createProxy(new Class [] { Listener.class, org.apache.pluto.container.om.portlet.Listener.class }, 
                                                         new MockListener(MockTestListener1.class.getName()));
        listeners.add(mockListener);
        
        PortletApplication demoPA = 
            (PortletApplication) DelegatingObjectProxy.createProxy(new Class [] { PortletApplication.class, PortletApplicationDefinition.class }, 
                                                                   new MockPortletApplication("demo", null, listeners));
        
        this.portletFactory.registerPortletApplication(demoPA, Thread.currentThread().getContextClassLoader());
        
        List<PortletURLGenerationListener> urlListeners = this.portletFactory.getPortletApplicationListeners(demoPA);
        assertNotNull("urlListeners is null!", urlListeners);
        
        assertFalse("urlListeners is empty!", urlListeners.isEmpty());
        
        assertTrue("The urlListeners[0] is not an instance of MockTestListener1.", urlListeners.get(0) instanceof MockTestListener1);
        
        List<PortletURLGenerationListener> urlListeners2 = this.portletFactory.getPortletApplicationListeners(demoPA);
        assertNotNull("urlListeners are not cached!", urlListeners == urlListeners2);

        this.portletFactory.unregisterPortletApplication(demoPA);
        
        List<PortletURLGenerationListener> urlListenersAfterUnregistration = null;
        
        try
        {
            urlListenersAfterUnregistration = this.portletFactory.getPortletApplicationListeners(demoPA);
        }
        catch (PortletException e)
        {
        }

        assertNull("filterInstance cannot be retrieved after unregistration.", urlListenersAfterUnregistration);
    }
    
    public static class MockPortalConfiguration
    {
        public String[] getStringArray(String key)
        {
            return new String[0];
        }
    }
    
    public static class MockPortalContext
    {
        private PortalConfiguration configuration;
        
        public MockPortalContext(PortalConfiguration configuration)
        {
            this.configuration = configuration;
        }
        
        public PortalConfiguration getConfiguration()
        {
            return this.configuration;
        }
    }
    
    public static class MockPickANumber extends GenericPortlet
    {
    }
    
    public static class MockTestFilter1 implements PortletFilter
    {
        public void destroy()
        {
        }

        public void init(FilterConfig filterConfig) throws PortletException
        {
        }
    }
    
    public static class MockTestListener1 implements PortletURLGenerationListener
    {
        public void filterActionURL(PortletURL actionURL)
        {
        }

        public void filterRenderURL(PortletURL renderURL)
        {
        }

        public void filterResourceURL(ResourceURL resourceURL)
        {
        }
    }
    
    public static class MockPortletDefinition
    {
        private PortletApplication pa;
        private String portletName;
        private String portletClass;
        
        public MockPortletDefinition(PortletApplication pa, String portletName, String portletClass)
        {
            this.pa = pa;
            this.portletName = portletName;
            this.portletClass = portletClass;
        }
        
        public PortletApplication getApplication()
        {
            return this.pa;
        }
        
        public String getPortletName() 
        {
            return this.portletName;
        }
        
        public String getPortletClass()
        {
            return this.portletClass;
        }
    }
    
    public static class MockFilter
    {
        private String filterClass;
        
        public MockFilter(String filterClass)
        {
            this.filterClass = filterClass;
        }
        
        public String getFilterClass()
        {
            return this.filterClass;
        }
    }
    
    public static class MockListener
    {
        private String listenerClass;
        
        public MockListener(String listenerClass)
        {
            this.listenerClass = listenerClass;
        }
        
        public String getListenerClass()
        {
            return this.listenerClass;
        }
    }
    
    public static class MockPortletApplication
    {
        private String name;
        private Map<String, Filter> filters;
        private List<Listener> listeners;
        
        public MockPortletApplication(String name, Map<String, Filter> filters, List<Listener> listeners)
        {
            this.name = name;
            this.filters = filters;
            this.listeners = listeners;
        }
        
        public String getName()
        {
            return this.name;
        }
        
        public Filter getFilter(String filterName)
        {
            return this.filters.get(filterName);
        }
        
        public List<Listener> getListeners()
        {
            return this.listeners;
        }
    }
}
