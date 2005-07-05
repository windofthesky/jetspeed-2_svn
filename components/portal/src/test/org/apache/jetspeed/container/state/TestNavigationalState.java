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
package org.apache.jetspeed.container.state;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalTestConstants;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.state.NavigationalStateComponent;
import org.apache.jetspeed.container.state.impl.JetspeedNavigationalStateComponent;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.container.url.impl.AbstractPortalURL;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.engine.AbstractEngine;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.engine.SpringEngine;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.om.window.PortletWindowList;
import org.apache.pluto.om.window.PortletWindowListCtrl;
import org.jmock.Mock;
import org.jmock.core.matcher.AnyArgumentsMatcher;
import org.jmock.core.stub.ReturnStub;
import org.jmock.core.stub.VoidStub;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletConfig;
import com.mockrunner.mock.web.MockServletContext;

/**
 * TestPortletContainer
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: TestNavigationalState.java 188438 2005-03-23 22:57:11Z ate $
 */

public class TestNavigationalState extends TestCase
{
    // needed to be able to Mock PortletWindowListCtrl
    private interface CompositeWindowList extends PortletWindowList, PortletWindowListCtrl{}

    private NavigationalStateComponent navFullSession;
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

        // need to flag internal JNDI on...
        System.setProperty(AbstractEngine.JNDI_SUPPORT_FLAG_KEY, "true");
        
        // create Engine
        PropertiesConfiguration config = new  PropertiesConfiguration();
        config.load(new FileInputStream(PortalTestConstants.JETSPEED_PROPERTIES_PATH));
        Mock servletConfigMock = new Mock(ServletConfig.class);
        MockServletConfig msc = new MockServletConfig();
        msc.setServletContext(new MockServletContext());
        Engine engine = Jetspeed.createEngine(config, PortalTestConstants.PORTAL_WEBAPP_PATH, msc, SpringEngine.class);

        // mock test PortletWindow
        Mock entityMock = new Mock(MutablePortletEntity.class);        
        Mock windowListMock = new Mock(CompositeWindowList.class);
        PortletWindowListCtrl windowList = (PortletWindowListCtrl)windowListMock.proxy();
        entityMock.expects(new AnyArgumentsMatcher()).method("getPortletWindowList").withNoArguments().will(
                new ReturnStub(windowList));
        windowListMock.expects(new AnyArgumentsMatcher()).method("add").withAnyArguments().will(
                new VoidStub());

        PortletWindowAccessor accessor = (PortletWindowAccessor) Jetspeed.getComponentManager().getComponent(PortletWindowAccessor.class);        
        accessor.createPortletWindow((PortletEntity)entityMock.proxy(), "111");
        accessor.createPortletWindow((PortletEntity)entityMock.proxy(), "222");
        accessor.createPortletWindow((PortletEntity)entityMock.proxy(), "333");
        
        navFullSession = new JetspeedNavigationalStateComponent("org.apache.jetspeed.container.state.impl.SessionFullNavigationalState",
                "org.apache.jetspeed.container.url.impl.QueryStringEncodingPortalURL",
                 "org.apache.jetspeed.container.state.impl.JetspeedNavigationalStateCodec");
        navSession = new JetspeedNavigationalStateComponent("org.apache.jetspeed.container.state.impl.SessionNavigationalState",
                "org.apache.jetspeed.container.url.impl.PathInfoEncodingPortalURL",
                 "org.apache.jetspeed.container.state.impl.JetspeedNavigationalStateCodec");
        navPluto = new JetspeedNavigationalStateComponent("org.apache.jetspeed.container.state.impl.PathNavigationalState",
               "org.apache.jetspeed.container.url.impl.PathInfoEncodingPortalURL",
                "org.apache.jetspeed.container.state.impl.JetspeedNavigationalStateCodec");
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestNavigationalState.class);
    }

    public void testAllComponents()
        throws Exception
    {

        // general navigational state test
        navigationTest(navFullSession, true);
        navigationTest(navSession, false);
        navigationTest(navPluto, false);
    }

    private void navigationTest(NavigationalStateComponent component, boolean useQueryStringPortalURL)
    throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();
        HttpServletResponse response = new MockHttpServletResponse();
        ServletConfig config = new MockServletConfig();
        request.setSession(session);
        request.setServerName("www.sporteportal.com");
        request.setScheme("http");
        request.setContextPath("/jetspeed");
        request.setServletPath("/portal");
        request.setPathInfo("stuff");
        request.setRequestURI("/jetspeed/portal/stuff");

        JetspeedRequestContext context = new JetspeedRequestContext(request, response, config, null );
        
        // create base PortletURL
        PortalURL url = component.createURL(context.getRequest(), context.getCharacterEncoding());
        context.setPortalURL(url);

        PortletWindow window = new PortletWindowImpl("111");
        PortletWindow window2 = new PortletWindowImpl("222");
        PortletWindow window3 = new PortletWindowImpl("333");
 
        HashMap parameters = new HashMap();
        parameters.put("test",new String[]{"one","two","three"});

        String portletURL = url.createPortletURL(window,parameters,PortletMode.EDIT,WindowState.MAXIMIZED,true,false);
        
        String navStateParameterName = Jetspeed.getContext().getConfigurationProperty("portalurl.navigationalstate.parameter.name", AbstractPortalURL.DEFAULT_NAV_STATE_PARAMETER); 

        if ( useQueryStringPortalURL )
        {
            request.setupAddParameter(navStateParameterName,portletURL.substring(portletURL.indexOf('=')+1));            
        }
        else
        {
            request.setPathInfo(portletURL.substring(portletURL.indexOf("/portal")+7));
        }
        
        context = new JetspeedRequestContext(request, response, config, null );
                
        url = component.createURL(context.getRequest(), context.getCharacterEncoding());
        context.setPortalURL(url);
        NavigationalState nav = url.getNavigationalState();

        // Check that they come out correctly
        assertTrue("window mode is not set", nav.getMode(window).equals(PortletMode.EDIT));
        assertTrue("window state is not set", nav.getState(window).equals(WindowState.MAXIMIZED));
        PortletWindow target = nav.getPortletWindowOfAction();
        assertNotNull("target window is null", target);
        assertEquals("target window should equal window 111", target.getId(), "111");

        PortletWindow maximizedWindow = nav.getMaximizedWindow();
        assertNotNull("maximized window is null", maximizedWindow);
        assertEquals("maximized window should equal window 111", maximizedWindow.getId(), "111");

        Iterator iter = nav.getParameterNames(target);
        int parameterCount = 0;
        assertTrue("There should be one parameter",iter.hasNext());
        while ( iter.hasNext() ) {
            assertEquals("parameter name should equals \"test\"", (String)iter.next(), "test");
            String[] values = nav.getParameterValues(target,"test");
            assertNotNull("parameter name has no values", values);
            assertEquals("parameter test should have 3 values", values.length, 3);
            assertEquals("parameter test[0] should be \"one\"", values[0], "one");
            assertEquals("parameter test[1] should be \"two\"", values[1], "two");
            assertEquals("parameter test[2] should be \"three\"", values[2], "three");
        }
    }
}
