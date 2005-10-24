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
package org.apache.jetspeed.statistics;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.test.AbstractSpringTestCase;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.request.RequestContext;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;

/**
 * TestProfiler
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class TestStatistics extends AbstractSpringTestCase
{
    private PortalStatistics statistics = null;
    
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Start the tests.
     * 
     * @param args
     *            the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestStatistics.class.getName() });
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        this.statistics = (PortalStatistics) ctx.getBean("PortalStatistics");
        assertNotNull("statistics not found ", statistics);
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestStatistics.class);
    }


    public void testPortletStatistics() 
    throws Exception
    {
        assertNotNull("statistics service is null", statistics);
        RequestContext request = initRequestContext();
        
        PortletDefinitionImpl portlet = new PortletDefinitionImpl();
        portlet.setName("TestPortlet");
        statistics.logAccess(request, portlet, "401");                
    }

    private RequestContext initRequestContext()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpSession session = new MockHttpSession();

        request.setRemoteAddr("192.168.2.3");

        request.setSession(session);
        request.setServerName("www.sporteportal.com");
        request.setScheme("http");
        request.setContextPath("/jetspeed");
        request.setServletPath("/portal");
        request.setPathInfo("stuff");
        request.setRequestURI("/jetspeed/portal/stuff");
        request.setMethod("GET");
        RequestContext rc = new MockRequestContext(request, response);       
        return rc;
    }
    
    protected String[] getConfigurations()
    {
        return new String[]{"statistics.xml"};
    }

}
