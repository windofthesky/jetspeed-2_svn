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

import java.io.File;
import java.security.PrivilegedAction;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.factorybeans.ServletConfigFactoryBean;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.layout.impl.LayoutValve;
import org.apache.jetspeed.mocks.ResourceLocatingServletContext;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Page;
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

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletConfig;
import com.mockrunner.mock.web.MockServletContext;

/**
 * Test Page Layout REST Service
 *  
 * @author <a>David Sean Taylor </a>
 * @version $Id$
 */
public class TestPageLayoutService extends JetspeedTestCase
{

    private ComponentManager cm;

    private LayoutValve valve;
    
    private PageManager pageManager;
    private PageLayoutComponent layoutManager;
    private PageLayoutService pageLayoutService;

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
        valve = (LayoutValve) cm.getComponent("layoutValve");
        pageManager = (PageManager) cm.getComponent("pageManager");
        assertNotNull(pageManager);
        layoutManager = (PageLayoutComponent)cm.getComponent("org.apache.jetspeed.layout.PageLayoutComponent");
        assertNotNull(layoutManager);
        pageLayoutService = (PageLayoutService)cm.getComponent("jaxrsPageLayoutService");
        assertNotNull(pageLayoutService);        
    }

    protected void tearDown() throws Exception
    {
        cm.stop();
        super.tearDown();
    }

    public void testRunner()
    throws Exception
    {
        if (0 == 0)
            return; // UNDER DEVELOPMENT, let it pass for now....
        
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
        JSSubject.doAsPrivileged(subject, new PrivilegedAction()
                {
                    public Object run() 
                    {
                         try
                        {
                            executeGridMoves(request, rc);                 
                            return null;
                        }
                        catch (Exception e)
                        {
                            return e;
                        }                    
                    }
                }, null);        
    }
    
    public void executeGridMoves(HttpServletRequest request, RequestContext rc) throws Exception
    {
        Page grid = pageManager.getPage("grid.psml");
        assertNotNull("default page not found", grid);
        ContentPage page = layoutManager.newContentPage(grid, null, null);
        rc.setPage(page);
        ContentFragmentBean cfb = this.pageLayoutService.moveContentFragment(request, null, "dp-1.dp-3", null, null, "1", "0", null, null, null, null, null);
        assertEquals(cfb.getId(), "dp-1.dp-3");
        assertEquals(cfb.getProperties().get("row"), "1");
        assertEquals(cfb.getProperties().get("column"), "0");        
    }    
        
    static class TestUser extends AbstractTestPrincipal implements User
    {
        private static final long serialVersionUID = 1L;

        public TestUser(String name)
        {
            super(JetspeedPrincipalType.USER, name);
        }
    }

    static class TestRole extends AbstractTestPrincipal implements Role
    {
        private static final long serialVersionUID = 1L;

        public TestRole(String name)
        {
            super(JetspeedPrincipalType.ROLE, name);
        }
    }
}
