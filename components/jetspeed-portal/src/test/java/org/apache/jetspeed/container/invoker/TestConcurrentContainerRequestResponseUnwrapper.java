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
package org.apache.jetspeed.container.invoker;

import junit.framework.TestCase;

import com.mockrunner.mock.web.MockHttpServletRequest;

/**
 * TestConcurrentContainerRequestResponseUnwrapper
 * @version $Id$
 */
public class TestConcurrentContainerRequestResponseUnwrapper extends TestCase 
{
    private ContainerRequestResponseUnwrapper unwrapper;

    public void setUp() throws Exception
    {
        super.setUp();
        
        ConcurrentContainerRequestResponseUnwrapper unwrapperImpl = new ConcurrentContainerRequestResponseUnwrapper();
        unwrapperImpl.setAttributableProperties(new String [] { "dispatcherContext" });
        
        unwrapper = unwrapperImpl;
    }

    public void testThreadSafety() throws Exception
    {
        MockConcurrentRequest containerConcurrentRequest = new MockConcurrentRequest(null);
        MockConcurrentRequest adjustedConcurrentRequest = (MockConcurrentRequest) unwrapper.unwrapContainerRequest(containerConcurrentRequest);
        
        int workerCount = 40;
        Worker[] workers = new Worker[workerCount];
        
        for (int i = 0; i < workerCount; i++)
        {
            workers[i] = new Worker("Worker-" + i, adjustedConcurrentRequest, "Portlet-" + i, new Object());
        }
        
        for (int i = 0; i < workerCount; i++)
        {
            workers[i].start();
        }
        
        for (int i = 0; i < workerCount; i++)
        {
            workers[i].join();
        }
        
        for (int i = 0; i < workerCount; i++)
        {
            assertEquals("Difference-1: " + workers[i].getOriginalPortletName() + ", " + workers[i].getCurrentPortletName(), 
                         workers[i].getOriginalPortletName(),
                         workers[i].getCurrentPortletName());
            assertEquals("Difference-2: " + workers[i].getOriginalWebAppDispatcherContext() + ", " + workers[i].getCurrentWebAppDispatcherContext(), 
                         workers[i].getOriginalWebAppDispatcherContext(),
                         workers[i].getCurrentWebAppDispatcherContext());
        }
    }

    private static class Worker extends Thread
    {
        private final MockConcurrentRequest concurrentRequest;
        private final String orginalPortletName;
        private String currentPortletName;
        private final Object originalWebAppDispatcherContext;
        private Object currentWebAppDispatcherContext;

        private Worker(String name, final MockConcurrentRequest concurrentRequest, final String orginalPortletName, final Object originalWebAppDispatcherContext)
        {
            super(name);
            this.concurrentRequest = concurrentRequest;
            this.orginalPortletName = orginalPortletName;
            this.originalWebAppDispatcherContext = originalWebAppDispatcherContext;
        }

        public void run()
        {
            concurrentRequest.setAttribute("portletName", orginalPortletName);
            concurrentRequest.setDispatcherContext(originalWebAppDispatcherContext);
            
            try
            {
                Thread.sleep(10);
            }
            catch (Exception e)
            {
            }
            
            currentPortletName = (String) concurrentRequest.getAttribute("portletName");
            currentWebAppDispatcherContext = concurrentRequest.getDispatcherContext();
        }

        public String getOriginalPortletName()
        {
            return orginalPortletName;
        }

        public String getCurrentPortletName()
        {
            return currentPortletName;
        }

        public Object getOriginalWebAppDispatcherContext()
        {
            return originalWebAppDispatcherContext;
        }

        public Object getCurrentWebAppDispatcherContext()
        {
            return currentWebAppDispatcherContext;
        }
    }
    
    public static class MockConcurrentRequest extends MockHttpServletRequest
    {
        private Object dispatcherContext;
        
        public MockConcurrentRequest(MockConcurrentRequest parent)
        {
            super();
        }
        
        public void setDispatcherContext(Object dispatcherContext)
        {
            this.dispatcherContext = dispatcherContext;
        }
        
        public Object getDispatcherContext()
        {
            return dispatcherContext;
        }
    }

}
