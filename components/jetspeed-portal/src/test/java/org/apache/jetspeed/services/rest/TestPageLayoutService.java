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
package org.apache.jetspeed.services.rest;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletConfig;
import com.mockrunner.mock.web.MockServletContext;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.factorybeans.ServletConfigFactoryBean;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.layout.impl.LayoutValve;
import org.apache.jetspeed.mocks.ResourceLocatingServletContext;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.services.beans.ContentFragmentBean;
import org.apache.jetspeed.test.JetspeedTestCase;
import org.apache.jetspeed.testhelpers.AbstractTestPrincipal;
import org.jmock.Mock;
import org.jmock.core.Constraint;
import org.jmock.core.InvocationMatcher;
import org.jmock.core.constraint.IsEqual;
import org.jmock.core.matcher.InvokeAtLeastOnceMatcher;
import org.jmock.core.stub.ReturnStub;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

/**
 * Test Page Layout REST Service
 *  
 * @author <a>David Sean Taylor </a>
 * @version $Id$
 */
public class TestPageLayoutService extends JetspeedTestCase
{
    private static final String CFIS = PageLayoutComponent.CONTENT_FRAGMENT_ID_SEPARATOR;

    private ComponentManager cm;

    private LayoutValve valve;
    
    private PageManager pageManager;
    private PageLayoutComponent layoutManager;
    private PageLayoutService pageLayoutService;
    private PortletRegistry portletRegistry;

    public static void main(String[] args)
    {
        junit.swingui.TestRunner.run(TestPageLayoutService.class);
    }

    /**
     * Setup the request context
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        
        MockServletConfig servletConfig = new MockServletConfig();        
        ResourceLocatingServletContext servletContent = new ResourceLocatingServletContext(new File(getBaseDir()));        
        servletConfig.setServletContext(servletContent);
        ServletConfigFactoryBean.setServletConfig(servletConfig);
        
        // Load the Spring configs
        String[] bootConfigs = null;
        String[] appConfigs =
        { //"src/webapp/WEB-INF/assembly/layout-api.xml",
                "src/test/assembly/test-layout-constraints-api.xml",
                "src/test/assembly/page-manager.xml",
                "src/test/assembly/jetspeed-restful-services.xml",
                "src/test/assembly/cache-test.xml"};
        
                
        cm = new SpringComponentManager(null, bootConfigs, appConfigs, servletContent, getBaseDir());
        cm.addComponent("javax.servlet.ServletConfig", servletConfig);
        cm.start();
        valve = cm.lookupComponent("layoutValve");
        pageManager = cm.lookupComponent("pageManager");
        assertNotNull(pageManager);
        layoutManager = cm.lookupComponent("org.apache.jetspeed.layout.PageLayoutComponent");
        assertNotNull(layoutManager);

        portletRegistry = createMockPortletRegistry();
        assertNotNull(portletRegistry);

        pageLayoutService = new PageLayoutService(layoutManager, portletRegistry);
    }

    protected void tearDown() throws Exception
    {
        cm.stop();
        super.tearDown();
    }

    public void testRunner()
    throws Exception
    {
        RequestContextComponent rcc = (RequestContextComponent) new Mock(RequestContextComponent.class).proxy();        
        MockServletConfig config = new MockServletConfig();
        MockServletContext context = new MockServletContext();
        MockHttpSession session = new MockHttpSession();
        session.setupServletContext(context);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setSession(session);
        MockHttpServletResponse response = new MockHttpServletResponse();
        final RequestContext rc = new JetspeedRequestContext(rcc, request, response, config, null);        
        Set principals = new HashSet();
        principals.add(new TestUser("admin"));
        principals.add(new TestRole("user"));
        principals.add(new TestRole("admin"));
        Subject subject = new Subject(true, principals, new HashSet(), new HashSet());
        
        Object ret = JSSubject.doAsPrivileged(subject, new PrivilegedAction()
                {
                    public Object run() 
                    {
                        try
                        {
                            executeGridMoves(request, rc);     
                            executeDetachedMoves(request, rc);
                            return null;
                        }
                        catch (Exception e)
                        {
                            return e;
                        }                    
                    }
                }, null);
        
        assertFalse("Exception occurrred: " + ret, ret instanceof Throwable);
    }
    
    private void executeGridMoves(HttpServletRequest request, RequestContext rc) throws Exception
    {
        Page src = pageManager.getPage("grid.psml");
        assertNotNull("default page not found", src);
        Page grid = pageManager.copyPage(src, "grid-1.psml", true);
        pageManager.updatePage(grid);
       
        ContentPage page = layoutManager.newContentPage(grid, null, null);
        rc.setPage(page);
        
        ContentFragmentBean cfb = pageLayoutService.moveContentFragment(request, null, "dp-0"+CFIS+"dp-00", null, null, "1", "0", null, null, null, null, null);
        assertEquals(cfb.getId(), "dp-0"+CFIS+"dp-00");
        assertEquals(cfb.getProperties().get("column"), "0");
        assertEquals(cfb.getProperties().get("row"), "1");
        
        cfb = pageLayoutService.moveContentFragment(request, null, "dp-0"+CFIS+"dp-02", null, "right", null, null, null, null, null, null, null);
        assertEquals(cfb.getId(), "dp-0"+CFIS+"dp-02");
        assertEquals(cfb.getProperties().get("column"), "1");
        assertEquals(cfb.getProperties().get("row"), "2");

        cfb = pageLayoutService.moveContentFragment(request, null, "dp-0"+CFIS+"dp-02", null, "down", null, null, null, null, null, null, null);
        assertEquals(cfb.getId(), "dp-0"+CFIS+"dp-02");
        assertEquals(cfb.getProperties().get("column"), "1");
        assertEquals(cfb.getProperties().get("row"), "3");
        
        cfb = pageLayoutService.moveContentFragment(request, null, "dp-0"+CFIS+"dp-02", null, "up", null, null, null, null, null, null, null);
        assertEquals(cfb.getId(), "dp-0"+CFIS+"dp-02");
        assertEquals(cfb.getProperties().get("column"), "1");
        assertEquals(cfb.getProperties().get("row"), "2");
        
        cfb = pageLayoutService.moveContentFragment(request, null, "dp-0"+CFIS+"dp-02", null, "left", null, null, null, null, null, null, null);
        assertEquals(cfb.getId(), "dp-0"+CFIS+"dp-02");
        assertEquals(cfb.getProperties().get("column"), "0");
        assertEquals(cfb.getProperties().get("row"), "2");
        
        pageManager.removePage(grid);
    }

    private void executeDetachedMoves(HttpServletRequest request, RequestContext rc) throws Exception
    {
        Page src = pageManager.getPage("grid.psml");
        assertNotNull("default page not found", src);
        Page grid = pageManager.copyPage(src, "grid-2.psml", true);
        pageManager.updatePage(grid);
       
        ContentPage page = layoutManager.newContentPage(grid, null, null);
        rc.setPage(page);

        ContentFragmentBean cfb = pageLayoutService.moveContentFragment(request, null, "dp-0"+CFIS+"dp-10", "detach", null, null, null, "491.0", "14.0", null, null, null);
        assertEquals(cfb.getId(), "dp-0"+CFIS+"dp-10");
        assertEquals(cfb.getProperties().get("column"), "1");
        assertEquals(cfb.getProperties().get("row"), "3");
        assertEquals(cfb.getProperties().get("x"), "491.0");
        assertEquals(cfb.getProperties().get("y"), "14.0");
        assertEquals(cfb.getState(), JetspeedActions.DETACH);
        ContentFragment dp10 = page.getFragmentByFragmentId("dp-10");
        assertNotNull(dp10);
        assertEquals(dp10.getLayoutRow(), 3);
        assertEquals(dp10.getLayoutX(), (float)491.0);
        assertEquals(dp10.getLayoutY(), (float)14.0);
        assertEquals(dp10.getState(), JetspeedActions.DETACH);
        // test shift up of all rows not detached
        ContentFragment dp11 = page.getFragmentByFragmentId("dp-11");
        assertNotNull(dp11);
        assertEquals(dp11.getLayoutRow(), 0);
        ContentFragment dp12 = page.getFragmentByFragmentId("dp-12");
        assertNotNull(dp12);
        assertEquals(dp12.getLayoutRow(), 1);
        ContentFragment dp13 = page.getFragmentByFragmentId("dp-13");
        assertNotNull(dp13);
        assertEquals(dp13.getLayoutRow(), 2);
        
        pageManager.removePage(grid);        
    }
    
    private PortletRegistry createMockPortletRegistry()
    {
        Mock portletRegistryMock;
        PortletRegistry portletRegistry;
        Mock portletDefMock;
        PortletDefinition portletDef;

        Mock portletSizesParamMock;
        InitParam portletSizesParam;
        
        portletRegistryMock = new Mock(PortletRegistry.class);
        portletRegistry = (PortletRegistry) portletRegistryMock.proxy();
        
        portletDefMock = new Mock(PortletDefinition.class);
        portletDef = (PortletDefinition) portletDefMock.proxy();

        portletSizesParamMock = new Mock(InitParam.class);
        portletSizesParam = (InitParam) portletSizesParamMock.proxy();

        expectAndReturn(new InvokeAtLeastOnceMatcher(), portletSizesParamMock, "getParamValue", "33%,66%");
        expectAndReturn(new InvokeAtLeastOnceMatcher(), portletRegistryMock, "getPortletDefinitionByUniqueName", portletDef);
        expectAndReturn(new InvokeAtLeastOnceMatcher(), portletDefMock, "getInitParam", new Constraint[] {new IsEqual("sizes")}, portletSizesParam);
        
        return portletRegistry;
    }

    private void expectAndReturn(InvocationMatcher matcher, Mock mock, String methodName, Constraint[] constraints, Object returnValue)
    {
        mock.expects(matcher).method(methodName)
                            .with(constraints)
                            .will(new ReturnStub(returnValue));
    }
    
    private void expectAndReturn(InvocationMatcher matcher, Mock mock, String methodName, Object returnValue)
    {
        mock.expects(matcher).method(methodName)
                            .will(new ReturnStub(returnValue));
    }
    
    private static class TestUser extends AbstractTestPrincipal implements User
    {
        private static final long serialVersionUID = 1L;

        public TestUser(String name)
        {
            super(JetspeedPrincipalType.USER, name);
        }
    }

    private static class TestRole extends AbstractTestPrincipal implements Role
    {
        private static final long serialVersionUID = 1L;

        public TestRole(String name)
        {
            super(JetspeedPrincipalType.ROLE, name);
        }
    }
}
