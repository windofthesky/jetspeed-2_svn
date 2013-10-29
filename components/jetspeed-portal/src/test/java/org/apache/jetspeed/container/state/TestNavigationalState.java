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
package org.apache.jetspeed.container.state;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpSession;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.state.impl.NavigationalStateCodec;
import org.apache.jetspeed.container.state.impl.PathNavigationalState;
import org.apache.jetspeed.container.state.impl.SessionFullNavigationalState;
import org.apache.jetspeed.container.state.impl.SessionNavigationalState;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.container.url.impl.AbstractPortalURL;
import org.apache.jetspeed.container.url.impl.PathInfoEncodingPortalURL;
import org.apache.jetspeed.container.url.impl.QueryStringEncodingPortalURL;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.test.JetspeedTestCase;
import org.apache.jetspeed.testhelpers.SpringEngineHelper;
import org.apache.jetspeed.window.MockPortletWindow;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TestPortletContainer
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */

public class TestNavigationalState extends JetspeedTestCase
{
    private SpringEngineHelper engineHelper;
    private Engine engine;
    private NavigationalStateCodec codec;
    private PortalContext portalContext;
    private JetspeedContentCache cache;

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
        engineHelper.setUp(getBaseDir());
        engine = (Engine) context.get(SpringEngineHelper.ENGINE_ATTR);
        codec = engine.getComponentManager().lookupComponent("NavigationalStateCodec");
        portalContext = engine.getComponentManager().lookupComponent("PortalContext");
        cache = engine.getComponentManager().lookupComponent("portletContentCache");
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestNavigationalState.class);
    }

    public void testSessionFullStateAndQuery()
    {        
        SessionFullNavigationalState navState = new SessionFullNavigationalState(codec, cache);
        QueryStringEncodingPortalURL portalUrl = new QueryStringEncodingPortalURL(navState, portalContext);
        HttpServletRequest request = buildRequest(portalUrl, true);
        navState = new SessionFullNavigationalState(codec, cache);
        portalUrl = new QueryStringEncodingPortalURL(navState, portalContext);
        doTestUrl(portalUrl, request);
        
    }
    
    public void testSessionStateAndPathInfo()
    {        
        SessionNavigationalState navState = new SessionNavigationalState(codec, cache);
        PathInfoEncodingPortalURL portalUrl = new PathInfoEncodingPortalURL(navState, portalContext);
        HttpServletRequest request = buildRequest(portalUrl, false);
        navState = new SessionNavigationalState(codec, cache);
        portalUrl = new PathInfoEncodingPortalURL(navState, portalContext);
        doTestUrl(portalUrl, request);
    }
    
    public void testPathStateAndPathInfo()
    {        
        PathNavigationalState navState = new PathNavigationalState(codec, cache);
        PathInfoEncodingPortalURL portalUrl = new PathInfoEncodingPortalURL(navState, portalContext);
        HttpServletRequest request = buildRequest(portalUrl, false);
        navState = new PathNavigationalState(codec, cache);
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
        MockRequestContext requestContext = new MockRequestContext();
        requestContext.setRequest(request);
        portalURL.getNavigationalState().sync(requestContext);

        PortletWindow window = new MockPortletWindow("111");

        HashMap<String,String[]> parameters = new HashMap<String, String[]>();
        parameters.put("test",new String[]{"one","two","three"});

        Map<String, String[]> privateRenderParameters = Collections.emptyMap();
        Map<String, String[]> publicRenderParameters = Collections.emptyMap();
        
        String portletURL = portalURL.createPortletURL( window, parameters, null, false, 
                                                        "PAGE", null, privateRenderParameters, publicRenderParameters, 
                                                        PortletMode.EDIT, WindowState.MAXIMIZED, 
                                                        PortalURL.URLType.ACTION, false );
        
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
        
        PortletWindow window = new MockPortletWindow("111");
        NavigationalState nav = portalURL.getNavigationalState();
        MockRequestContext requestContext = new MockRequestContext();
        requestContext.addPortletWindow(window);
        requestContext.setRequest(request);
        nav.sync(requestContext);
        
        // Check that they come out correctly
        assertTrue("window mode is not set", nav.getMode(window).equals(PortletMode.EDIT));
        assertTrue("window state is not set", nav.getState(window).equals(WindowState.MAXIMIZED));
        PortletWindow target = nav.getPortletWindowOfAction();
        assertNotNull("target window is null", target);
        assertEquals("target window should equal window 111", target.getId().getStringId(), "111");
        
        PortletWindow maximizedWindow = nav.getMaximizedWindow();
        assertNotNull("maximized window is null", maximizedWindow);
        assertEquals("maximized window should equal window 111", maximizedWindow.getId().getStringId(), "111");
        
        Map<String,String[]> parameters = nav.getParameterMap(target);
        assertTrue("There should be one parameter",parameters.size()==1);
        String[] values = parameters.get("test");
        assertNotNull("parameter name has no values", values);
        assertEquals("parameter test should have 3 values", values.length, 3);
        assertEquals("parameter test[0] should be \"one\"", values[0], "one");
        assertEquals("parameter test[1] should be \"two\"", values[1], "two");
        assertEquals("parameter test[2] should be \"three\"", values[2], "three");
    }


    protected void tearDown() throws Exception
    {
        engineHelper.tearDown();
        super.tearDown();
    }
}
