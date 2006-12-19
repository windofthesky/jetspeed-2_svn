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

import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.impl.ContentServerAdapterImpl;
import org.apache.jetspeed.capabilities.Capabilities;
import org.apache.jetspeed.container.state.NavigationalStateComponent;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.headerresource.impl.HeaderResourceFactoryImpl;
import org.apache.jetspeed.om.page.ContentPageImpl;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.request.JetspeedRequestContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.testhelpers.SpringEngineHelper;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;

/**
 * <P>Test the aggregation service</P>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 * 
 */
public class TestAggregator extends TestRenderer
{
    private SpringEngineHelper engineHelper;
    private Engine engine;
    private PortletAggregator portletAggregator;
    private PageAggregator pageAggregator;
    private PageAggregator asyncPageAggregator;
    private Profiler profiler;
    private Capabilities capabilities;
    private NavigationalStateComponent navComponent;

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
        super.setUp();
        
        HashMap context = new HashMap();
        engineHelper = new SpringEngineHelper(context);
        engineHelper.setUp();
        engine = (Engine) context.get(SpringEngineHelper.ENGINE_ATTR);

        ArrayList paths = new ArrayList(4);
        paths.add("portlet/{mediaType}/jetspeed");
        paths.add("portlet/{mediaType}");
        paths.add("generic/{mediaType}");
        paths.add("/{mediaType}");
        
        HeaderResourceFactory headerFactory = new HeaderResourceFactoryImpl();
        ContentServerAdapter contentServer = new ContentServerAdapterImpl(headerFactory, paths);
        
        //pageAggregator = new PageAggregatorImpl(renderer, contentServer);
        //asyncPageAggregator = new AsyncPageAggregatorImpl(renderer, contentServer);
        //portletAggregator = new PortletAggregatorImpl(renderer);
        pageAggregator = (PageAggregator) engine.getComponentManager().getComponent(PageAggregator.class);
        asyncPageAggregator = 
            (PageAggregator) engine.getComponentManager().getComponent("org.apache.jetspeed.aggregator.AsyncPageAggregator");
        portletAggregator = (PortletAggregator) engine.getComponentManager().getComponent(PortletAggregator.class);
        
        profiler = (Profiler) engine.getComponentManager().getComponent(Profiler.class);
        capabilities = (Capabilities) engine.getComponentManager().getComponent(Capabilities.class);
        navComponent = 
            (NavigationalStateComponent) engine.getComponentManager().getComponent(NavigationalStateComponent.class);
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestAggregator.class);
    }

    public void testBasic() throws Exception
    {
        doAggregation(false);
    }

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
        ServletConfig config = engine.getServletConfig();
        ServletContext context = config.getServletContext();
        MockHttpSession session = new MockHttpSession();
        session.setupServletContext(context);
        assertEquals(context, session.getServletContext());

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setSession(session);

        Principal p = new UserPrincipalImpl("user");
        request.setUserPrincipal(p);

        request.setScheme("http");
        request.setContextPath("/jetspeed");
        request.setServletPath("/portal/default-page.psml");
        request.setMethod("GET");

        RequestContext rc = 
            new JetspeedRequestContext(request, response, config, null);

        return rc;
    }

}
