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
import javax.servlet.http.HttpServletResponse;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.container.session.impl.JetspeedNavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.pluto.om.window.PortletWindow;

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

public class TestNavigationalState extends TestCase 
{
    private NavigationalStateComponent navSession;
    private NavigationalStateComponent navPluto;    
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
        
     //   navSession = (NavigationalStateComponent) container.getComponentInstance(NavigationalStateComponent.class);
       navSession = new JetspeedNavigationalStateComponent("org.apache.jetspeed.container.session.impl.SessionNavigationalState", 
                                                                                                 "org.apache.jetspeed.container.url.impl.SessionPortalURL", 
                                                                                                  "_,a,m,s,r,i,pm,ps,:");
      //   navPluto = (NavigationalStateComponent) container.getComponentInstance("PathNavs");        
       navPluto = new JetspeedNavigationalStateComponent("org.apache.jetspeed.container.session.impl.PathNavigationalState", 
                                                                                           "org.apache.jetspeed.container.url.impl.PathPortalURL",
                                                                                           "_,ac,md,st,rp,pid,pm,ps,:");
                   
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestNavigationalState.class);
    }

    public void testAllComponents()
        throws Exception
    {        
        System.out.println("Starting Navs Mode and State test");
      
        // general navigational state test
        navigationTest(navSession);
        navigationTest(navPluto);
        
        // URL tests
        String result = urlTest(navPluto);
        assertEquals("Session URL not equal", "http://www.sporteportal.com/jetspeed/portal/_st_33/minimized/_ac_33/AC/_rp_33_test/1_one/_md_33/edit", result);
        result = urlTest(navSession);
        assertEquals("Session URL not equal", "http://www.sporteportal.com/jetspeed/portal/_a_33/A/_s_33/minimized/_m_33/edit/_r_33_test/1_one", result);
        
        System.out.println("Ending Navs Mode and State test");
    }

    private void navigationTest(NavigationalStateComponent component)
    throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        HttpServletResponse response = new MockHttpServletResponse();        
        ServletConfig config = new MockServletConfig();
        request.setSession(session);
        request.setPathInfo("/stuff/");
        

//      RequestContext context = rc.create(
//              (HttpServletRequest)request, 
//              response, 
//              config);
      JetspeedRequestContext context = new JetspeedRequestContext(request, response, config, component, null );
    PortalURL url = component.createURL(context);
                
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
    public String urlTest(NavigationalStateComponent component)
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
        
//        RequestContext context = rc.create(
//                (HttpServletRequest)request, 
//                response, 
//                config);
        PortalURL url = component.createURL(new JetspeedRequestContext(request, response, config, component, null ));
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
