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
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;

import org.apache.pluto.om.window.PortletWindow;

import org.picocontainer.MutablePicoContainer;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
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
    private NavigationalStateComponent navSession;
    private NavigationalStateComponent navPluto;    
    private RequestContextComponent rcSession;
    private RequestContextComponent rcPluto;
    
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
        navSession = (NavigationalStateComponent) container.getComponentInstance(NavigationalStateComponent.class);
        navPluto = (NavigationalStateComponent) container.getComponentInstance("PathNavs");        
        
        rcSession = (RequestContextComponent) container.getComponentInstance(RequestContextComponent.class);
        rcPluto = (RequestContextComponent) container.getComponentInstance("PlutoRC");        
        
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

    public void testAllComponents()
        throws Exception
    {        
        System.out.println("Starting Navs Mode and State test");
        assertNotNull("nav state component is null", navPluto);
        assertNotNull("pluto nav state component is null", navPluto);        
        assertNotNull("request context component is null", rcSession);
        assertNotNull("pathrequest context component is null", rcPluto);

        // general navigational state test
        navigationTest(navSession, rcSession);
        navigationTest(navPluto, rcPluto);
        
        // URL tests
        String result = urlTest(navPluto, rcPluto);
        assertEquals("Session URL not equal", "http://www.sporteportal.com/jetspeed/portal/_st_33/minimized/_ac_33/AC/_rp_33_test/1_one/_md_33/edit", result);
        result = urlTest(navSession, rcSession);
        assertEquals("Session URL not equal", "http://www.sporteportal.com/jetspeed/portal/_a_33/A/_s_33/minimized/_m_33/edit/_r_33_test/1_one", result);
        
        System.out.println("Ending Navs Mode and State test");
    }

    private void navigationTest(NavigationalStateComponent component, RequestContextComponent rc)
    throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        HttpServletResponse response = new MockHttpServletResponse();        
        ServletConfig config = new MockServletConfig();
        request.setSession(session);
        request.setPathInfo("/stuff/");
        RequestContext context = rc.create(
                                           (HttpServletRequest)request, 
                                            response, 
                                            config);
                
        PortletWindow window = new PortletWindowImpl("111");
        PortletWindow window2 = new PortletWindowImpl("222");
        PortletWindow window3 = new PortletWindowImpl("333");
        
        NavigationalState nav = component.create(context);
        nav.setState(window, WindowState.MAXIMIZED);
        nav.setMode(window, PortletMode.HELP);
        nav.setMode(window2, PortletMode.EDIT);
        
        // TODO: test prev mode, prev state
                
        // Check that they come out correctly
        assertTrue("window mode is not set", nav.getMode(window).equals(PortletMode.HELP));
        assertTrue("window state is not set", nav.getState(window).equals(WindowState.MAXIMIZED));
        assertTrue("window mode is not set", nav.getMode(window2).equals(PortletMode.EDIT));
        assertTrue("window mode is not set", nav.getMode(window3).equals(PortletMode.VIEW));
        
    }
    public String urlTest(NavigationalStateComponent component, RequestContextComponent rc)
    throws Exception
    {        
        String [] values = 
        {
                "one"
        };
        
        MockServletConfig config = new MockServletConfig();
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        request.setServerName("www.sporteportal.com");
        request.setScheme("http");
        request.setContextPath("/jetspeed");
        request.setServletPath("/portal");
        
        RequestContext context = rc.create(
                (HttpServletRequest)request, 
                response, 
                config);
        PortalURL url = component.createURL(context);
        assertNotNull("URL is null", url);
        
        PortletWindow window = new PortletWindowImpl("33");
        url.setAction(window);
        url.setMode(window, PortletMode.EDIT);
        url.setState(window, WindowState.MINIMIZED);
        url.setRenderParam(window, "test", values);
        
        // CANT TEST WITHOUT SETTING UP DATABASE
        // PortletWindow target = url.getPortletWindowOfAction();
        //assertNotNull("target window is null", target);
        //assertEquals("target window should equal window 33", target.getId(), "33");
        System.out.println("URL = " + url.toString());
        return url.toString();        
    }
}
