/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.container.session;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ComponentAwareTestSuite;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.pluto.om.window.PortletWindow;
import org.picocontainer.MutablePicoContainer;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockServletConfig;

/**
 * TestPortletContainer
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */

public class TestNavigationalState extends AbstractComponentAwareTestCase 
{
    private MutablePicoContainer container;
    private NavigationalStateComponent navState;
    private RequestContextComponent rcc;
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestNavigationalState(String name)
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
        junit.awtui.TestRunner.main(new String[] { TestNavigationalState.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        container = (MutablePicoContainer) getContainer();
        assertNotNull("container is null", container);
        navState = (NavigationalStateComponent) container.getComponentInstance(NavigationalStateComponent.class);        
        rcc = (RequestContextComponent) container.getComponentInstance(RequestContextComponent.class);        
    }

    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestNavigationalState.class);
        suite.setScript("org/apache/jetspeed/containers/test-navstate-container.groovy");
        return suite;
    }

    public void testBasic()
    {
        assertNotNull("nav state component is null", navState);
        assertNotNull("request context component is null", rcc);

        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        ServletConfig config = new MockServletConfig();
        request.setPathInfo("/stuff/");
        RequestContext context = rcc.create(
                                                (HttpServletRequest)request, 
                                                response, 
                                                config);
        
System.out.println("$$$ context = " + context);
        
        PortletWindow window = new PortletWindowImpl("33");
        PortletWindow window2 = new PortletWindowImpl("222");
        
        NavigationalState nav = navState.create(context);
        nav.setState(window, WindowState.MAXIMIZED);
        nav.setMode(window, PortletMode.HELP);
        
        // Check that they come out correctly
        assertTrue("window mode is not set", nav.getMode(window).equals(PortletMode.HELP));
        assertTrue("window state is not set", nav.getState(window).equals(WindowState.MAXIMIZED));
        assertTrue("window mode is not set", nav.getMode(window2).equals(PortletMode.VIEW));
        
    }
    
}
