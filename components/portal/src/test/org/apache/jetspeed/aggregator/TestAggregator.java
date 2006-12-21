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
package org.apache.jetspeed.aggregator;

import java.io.File;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.capabilities.Capabilities;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.state.NavigationalStateComponent;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.mocks.ResourceLocatingServletContext;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.om.page.ContentPageImpl;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.testhelpers.SpringEngineHelper;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletConfig;

/**
 * <P>Test the aggregation service</P>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 * 
 */
public class TestAggregator extends TestCase
{
    private SpringEngineHelper engineHelper;
    private Engine engine;
    private PortletAggregator portletAggregator;
    private PageAggregator pageAggregator;
    private PageAggregator asyncPageAggregator;
    private Profiler profiler;
    private Capabilities capabilities;
    private NavigationalStateComponent navComponent;
    private PortletFactory portletFactory;
    private ServletConfig servletConfig;
    private ServletContext servletContext;
    private PortletRegistry portletRegistry;
    private RequestContextComponent rcc;
    

    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestAggregator.class.getName()});
    }

    protected void setUp() throws Exception
    {
        try
        {
            super.setUp();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        
        HashMap context = new HashMap();
        engineHelper = new SpringEngineHelper(context);
        engineHelper.setUp();
        engine = (Engine) context.get(SpringEngineHelper.ENGINE_ATTR);

        pageAggregator = (PageAggregator) engine.getComponentManager().getComponent(PageAggregator.class);
        asyncPageAggregator = 
            (PageAggregator) engine.getComponentManager().getComponent("org.apache.jetspeed.aggregator.AsyncPageAggregator");
        portletAggregator = (PortletAggregator) engine.getComponentManager().getComponent(PortletAggregator.class);
        
        profiler = (Profiler) engine.getComponentManager().getComponent(Profiler.class);
        capabilities = (Capabilities) engine.getComponentManager().getComponent(Capabilities.class);
        navComponent = 
            (NavigationalStateComponent) engine.getComponentManager().getComponent(NavigationalStateComponent.class);

        servletConfig = engine.getServletConfig();
        servletContext = servletConfig.getServletContext();

        portletRegistry = (PortletRegistry) engine.getComponentManager().getComponent("portletRegistry");
        portletFactory = (PortletFactory) engine.getComponentManager().getComponent("portletFactory");
        rcc = (RequestContextComponent) engine.getComponentManager().getComponent("org.apache.jetspeed.request.RequestContextComponent");

        File paRootDir = new File("../../applications/j2-admin/src/webapp");
        //File paRootDir = new File("../../applications/j2-admin/target/j2-admin");
        ResourceLocatingServletContext j2adminContext = new ResourceLocatingServletContext(paRootDir);
        MockServletConfig j2adminConfig = new MockServletConfig();
        j2adminConfig.setServletContext(j2adminContext);
        ((ResourceLocatingServletContext) servletContext).setContext("/j2-admin", j2adminContext);

        paRootDir = new File("../../applications/demo/src/webapp");
        //paRootDir = new File("../../applications/demo/target/demo");
        ResourceLocatingServletContext demoContext = new ResourceLocatingServletContext(paRootDir);
        MockServletConfig demoConfig = new MockServletConfig();
        demoConfig.setServletContext(demoContext);
        ((ResourceLocatingServletContext) servletContext).setContext("/demo", demoContext);

        Iterator it = portletRegistry.getPortletApplications().iterator();
        while (it.hasNext()) {
            PortletApplication pa = (PortletApplication) it.next();
            if (!portletFactory.isPortletApplicationRegistered(pa)) {
                portletFactory.registerPortletApplication(pa, pa.getClass().getClassLoader());
            }
        }
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestAggregator.class);
    }

//    public void testBasic() throws Exception
//    {
//        doAggregation(false);
//    }

    public void testParallelMode() throws Exception
    {
        doAggregation(true);
    }

    protected void tearDown() throws Exception
    {
        engineHelper.tearDown();
        super.tearDown();
    }

    private void doAggregation(final boolean isParallelMode) throws Exception
    {
        assertNotNull("portlet aggregator is null", portletAggregator);
        assertNotNull("page aggregator is null", pageAggregator);
        assertNotNull("async page aggregator is null", asyncPageAggregator);
        assertNotNull("profiler is null", profiler);
        assertNotNull("capabilities is null", capabilities);
        assertNotNull("navigational state component is null", navComponent);

        final RequestContext requestContext = initRequestContext();
        final Subject subject = SecurityHelper.createSubject("user");
        requestContext.getRequest().getSession().setAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);
        requestContext.setSubject(subject);
        
        ProfileLocator locator = profiler.createLocator(requestContext);
        HashMap locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext.setProfileLocators(locators);

        requestContext.setCapabilityMap(capabilities.getCapabilityMap("Mozilla/5"));
        requestContext.setPortalURL(navComponent.createURL(requestContext.getRequest(), requestContext.getCharacterEncoding()));

        Exception ex = (Exception) Subject.doAsPrivileged(subject, new PrivilegedAction()
            {
                public Object run()
                {
                    try {
                        PageManager pageManager = 
                            (PageManager) engine.getComponentManager().getComponent(PageManager.class);
                        Page page = pageManager.getPage("/default-page.psml");
                        assertNotNull(page);
                        requestContext.setPage(new ContentPageImpl(page));

                        if (!isParallelMode) {
                            pageAggregator.build(requestContext);
                        } else {
                            asyncPageAggregator.build(requestContext);
                        }
                    } catch (Exception e) {
                        return e;
                    }
                    return null;
                }
            }, null);

        if (ex != null)
            throw ex;
    }

    private RequestContext initRequestContext()
    {
        MockHttpSession session = new MockHttpSession();
        session.setupServletContext(servletContext);
        assertEquals(servletContext, session.getServletContext());

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setSession(session);

        Principal p = new UserPrincipalImpl("user");
        request.setUserPrincipal(p);

        request.setScheme("http");
        request.setContextPath("/jetspeed");
        request.setServletPath("/portal/default-page.psml");
        request.setMethod("GET");

//        RequestContext rc = 
//            new JetspeedRequestContext(request, response, servletConfig, null);
        RequestContext rc = rcc.create(request, response, servletConfig);
        return rc;
    }

    protected String[] getBootConfigurations()
    {
        return new String[]
        { "boot/datasource.xml"};
    }
    
    protected String[] getConfigurations()
    {
        //String[] confs = super.getConfigurations();
        //List confList = new ArrayList(Arrays.asList(confs));
        List confList = new ArrayList();
        confList.add("registry.xml");
        confList.add("transaction.xml");
        confList.add("prefs.xml");
        return (String[]) confList.toArray(new String[1]);
    }
    
}
