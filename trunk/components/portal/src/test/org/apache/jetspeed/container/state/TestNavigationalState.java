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

import java.util.HashMap;
import java.util.Iterator;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.JetspeedPortalContext;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.container.state.impl.JetspeedNavigationalStateCodec;
import org.apache.jetspeed.container.state.impl.JetspeedNavigationalStateComponent;
import org.apache.jetspeed.container.state.impl.NavigationalStateCodec;
import org.apache.jetspeed.container.state.impl.PathNavigationalState;
import org.apache.jetspeed.container.state.impl.SessionFullNavigationalState;
import org.apache.jetspeed.container.state.impl.SessionNavigationalState;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.container.url.impl.AbstractPortalURL;
import org.apache.jetspeed.container.url.impl.PathInfoEncodingPortalURL;
import org.apache.jetspeed.container.url.impl.QueryStringEncodingPortalURL;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.testhelpers.SpringEngineHelper;
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

/**
 * TestPortletContainer
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */

public class TestNavigationalState extends TestCase
{
    // needed to be able to Mock PortletWindowListCtrl
    private interface CompositeWindowList extends PortletWindowList, PortletWindowListCtrl{}

    private NavigationalStateComponent navFullSession;
    private NavigationalStateComponent navSession;
    private NavigationalStateComponent navPluto;
    private SpringEngineHelper engineHelper;
    private Engine engine;
    private NavigationalStateCodec codec;
    private PortalContext portalContext;

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

        HashMap context = new HashMap();
        engineHelper = new SpringEngineHelper(context);
        engineHelper.setUp();
        engine = (Engine) context.get(SpringEngineHelper.ENGINE_ATTR);
        // mock test PortletWindow
        Mock entityMock = new Mock(MutablePortletEntity.class);        
        Mock windowListMock = new Mock(CompositeWindowList.class);
        PortletWindowListCtrl windowList = (PortletWindowListCtrl)windowListMock.proxy();
        entityMock.expects(new AnyArgumentsMatcher()).method("getPortletWindowList").withNoArguments().will(
                new ReturnStub(windowList));
        windowListMock.expects(new AnyArgumentsMatcher()).method("add").withAnyArguments().will(
                new VoidStub());

        PortletWindowAccessor accessor = (PortletWindowAccessor) engine.getComponentManager().getComponent(PortletWindowAccessor.class);        
        accessor.createPortletWindow((PortletEntity)entityMock.proxy(), "111");
        accessor.createPortletWindow((PortletEntity)entityMock.proxy(), "222");
        accessor.createPortletWindow((PortletEntity)entityMock.proxy(), "333");
        
        codec = (NavigationalStateCodec) engine.getComponentManager().getComponent("NavigationalStateCodec");
        portalContext = (PortalContext) engine.getComponentManager().getComponent("PortalContext");        
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestNavigationalState.class);
    }

    
    public void testSessionFullStateAndQuery()
    {        
        SessionFullNavigationalState navState = new SessionFullNavigationalState(codec);
        QueryStringEncodingPortalURL portalUrl = new QueryStringEncodingPortalURL(navState, portalContext);
        HttpServletRequest request = buildRequest(portalUrl, true);
        navState = new SessionFullNavigationalState(codec);
        portalUrl = new QueryStringEncodingPortalURL(navState, portalContext);
        doTestUrl(portalUrl, request);
        
    }
    
    public void testSessionStateAndPathInfo()
    {        
        SessionNavigationalState navState = new SessionNavigationalState(codec);
        PathInfoEncodingPortalURL portalUrl = new PathInfoEncodingPortalURL(navState, portalContext);
        HttpServletRequest request = buildRequest(portalUrl, false);
        navState = new SessionNavigationalState(codec);
        portalUrl = new PathInfoEncodingPortalURL(navState, portalContext);
        doTestUrl(portalUrl, request);
    }
    
    public void testPathStateAndPathInfo()
    {        
        PathNavigationalState navState = new PathNavigationalState(codec);
        PathInfoEncodingPortalURL portalUrl = new PathInfoEncodingPortalURL(navState, portalContext);
        HttpServletRequest request = buildRequest(portalUrl, false);
        navState = new PathNavigationalState(codec);
        portalUrl = new PathInfoEncodingPortalURL(navState, portalContext);
        doTestUrl(portalUrl, request);
    }
    
    
    protected HttpServletRequest buildRequest(PortalURL portalURL, boolean useQueryStringPortalURL)
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpSession session = new MockHttpSession();

        request.setSession(session);
        request.setServerName("www.sporteportal.com");
        request.setScheme("http");
        request.setContextPath("/jetspeed");
        request.setServletPath("/portal");
        request.setPathInfo("stuff");
        request.setRequestURI("/jetspeed/portal/stuff");

        portalURL.setRequest(request);
        portalURL.setCharacterEncoding("UTF-8");

        PortletWindow window = new PortletWindowImpl("111");

        HashMap parameters = new HashMap();
        parameters.put("test",new String[]{"one","two","three"});

        String portletURL = portalURL.createPortletURL(window,parameters,PortletMode.EDIT,WindowState.MAXIMIZED,true,false);
        
        String navStateParameterName = engine.getContext().getConfigurationProperty("portalurl.navigationalstate.parameter.name", AbstractPortalURL.DEFAULT_NAV_STATE_PARAMETER); 

        if ( useQueryStringPortalURL )
        {
            request.setupAddParameter(navStateParameterName,portletURL.substring(portletURL.indexOf('=')+1));            
        }
        else
        {
            request.setPathInfo(portletURL.substring(portletURL.indexOf("/portal")+7));
        }
        
        return request;        
    }
    
    protected void doTestUrl(PortalURL portalURL, HttpServletRequest request)
    {             
      portalURL.setRequest(request);
      portalURL.setCharacterEncoding("UTF-8");
      
      PortletWindow window = new PortletWindowImpl("111");
      NavigationalState nav = portalURL.getNavigationalState();

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


    protected void tearDown() throws Exception
    {
        engineHelper.tearDown();
        super.tearDown();
    }
}
