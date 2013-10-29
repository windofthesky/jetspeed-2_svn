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
package org.apache.jetspeed.aggregator;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import com.mockrunner.mock.web.MockServletConfig;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.capabilities.Capabilities;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.container.state.NavigationalStateComponent;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.mocks.ResourceLocatingServletContext;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.JetspeedSubjectFactory;
import org.apache.jetspeed.security.impl.UserImpl;
import org.apache.jetspeed.test.JetspeedTestCase;
import org.apache.jetspeed.testhelpers.SpringEngineHelper;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <P>Test the aggregation service</P>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 * 
 */
public class TestAggregator extends JetspeedTestCase
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
    private String testPage = "/default-page.psml";

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
        engineHelper.setUp(getBaseDir());
        engine = (Engine) context.get(SpringEngineHelper.ENGINE_ATTR);

        pageAggregator = engine.getComponentManager().lookupComponent(PageAggregator.class);
        asyncPageAggregator =
                engine.getComponentManager().lookupComponent("org.apache.jetspeed.aggregator.AsyncPageAggregator");
        portletAggregator = engine.getComponentManager().lookupComponent(PortletAggregator.class);
        
        profiler = engine.getComponentManager().lookupComponent(Profiler.class);
        capabilities = engine.getComponentManager().lookupComponent(Capabilities.class);
        navComponent = engine.getComponentManager().lookupComponent(NavigationalStateComponent.class);

        servletConfig = engine.getServletConfig();
        servletContext = servletConfig.getServletContext();

        portletRegistry = engine.getComponentManager().lookupComponent("portletRegistry");
        portletFactory = engine.getComponentManager().lookupComponent("portletFactory");
        rcc = engine.getComponentManager().lookupComponent("org.apache.jetspeed.request.RequestContextComponent");

        File paRootDir = null;
        paRootDir = new File("../../layout-portlets/target/jetspeed-layout-portlets");
        initPA("jetspeed-layouts", "/jetspeed-layouts", paRootDir);

        paRootDir = new File("../../applications/demo/target/demo");
        initPA("demo", "/demo", paRootDir);

        paRootDir = new File("../../applications/j2-admin/target/j2-admin");
        initPA("j2-admin", "/j2-admin", paRootDir);
 
        // j2-admin portlet needs user manager component, but the followings does not effect..
//        userManager = (UserManager) engine.getComponentManager().lookupComponent(UserManager.class);
//        paContext.setAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT, userManager);
//        assertEquals(userManager, paContext.getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT));
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
        assertNotNull("portal servlet config is null", servletConfig);
        assertNotNull("portal servlet context is null", servletContext);
        assertNotNull("portlet registry is null", portletRegistry);
        assertNotNull("request context component is null", rcc);

        final RequestContext requestContext = initRequestContext();
        final Subject subject = JetspeedSubjectFactory.createSubject(new UserImpl("user"), null, null, null);
        requestContext.getRequest().getSession().setAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);
        requestContext.setSubject(subject);
        
        ProfileLocator locator = profiler.createLocator(requestContext);
        HashMap locators = new HashMap();
        locators.put(ProfileLocator.PAGE_LOCATOR, locator);
        requestContext.setProfileLocators(locators);

        requestContext.setCapabilityMap(capabilities.getCapabilityMap("Mozilla/5"));
        requestContext.setPortalURL(navComponent.createURL(requestContext.getRequest(), requestContext.getCharacterEncoding()));

        Exception ex = (Exception) JSSubject.doAsPrivileged(subject, new PrivilegedAction()
            {
                public Object run()
                {
                    try {
                        PageManager pageManager = 
                            engine.getComponentManager().lookupComponent(PageManager.class);
                        Page page = pageManager.getPage(testPage);
                        PageLayoutComponent pageLayoutComponent = 
                            engine.getComponentManager().lookupComponent(PageLayoutComponent.class);
                        assertNotNull(page);
                        requestContext.setPage(pageLayoutComponent.newContentPage(page, null, null));

                        if (!isParallelMode) {
                            pageAggregator.build(requestContext);
                        } else {
                            asyncPageAggregator.build(requestContext);
                        }

                        MockHttpServletResponse rsp = (MockHttpServletResponse) requestContext.getResponse();
                        System.out.println(">>>> " + rsp.getOutputStreamContent());
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

        //Principal p = new UserPrincipalImpl("user");
        //request.setUserPrincipal(p);

        request.setScheme("http");
        request.setContextPath("/jetspeed");
        request.setServletPath("/portal" + testPage);
        request.setMethod("GET");

//        RequestContext rc = 
//            new JetspeedRequestContext(request, response, servletConfig, null);
        RequestContext rc = rcc.create(request, response, servletConfig);
        return rc;
    }

    private ResourceLocatingServletContext initPA(String paName, String paContextPath, File paRootDir) 
    {
        ResourceLocatingServletContext paContext = new ResourceLocatingServletContext(paRootDir, true);   
        MockServletConfig paConfig = new MockServletConfig();
        paConfig.setServletContext(paContext);
        ((ResourceLocatingServletContext) servletContext).setContext(paContextPath, paContext);

        try
        {
            ClassLoader paCl = createPAClassLoader(new File(paRootDir, "WEB-INF"));
            PortletApplication pa = portletRegistry.getPortletApplication(paName);        
            portletFactory.registerPortletApplication(pa, paCl);
        }
        catch (Exception e)
        {
            System.out.println("Failed to register portlet application, " + paName + ": " + e);
        }        

        return paContext;
    }

    protected ClassLoader createPAClassLoader(File webInfDir)
    {
        ClassLoader localPAClassLoader = null;

        ArrayList urls = new ArrayList();
        File webInfClassesDir = null;

        try 
        {
            webInfClassesDir = new File(webInfDir, "classes");            
            if (webInfClassesDir.isDirectory())
            {
                urls.add(webInfClassesDir.toURL());
            }
            
            File webInfLibDir = new File(webInfDir, "lib");
            if (webInfLibDir.isDirectory())
            {
                File [] jars = webInfLibDir.listFiles();           
                for (int i = 0; i < jars.length; i++)
                {
                    File jar = jars[i];
                    urls.add(jar.toURL());
                }
            }
            
            localPAClassLoader = 
                new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
        } catch (Exception e) 
        {
            throw new RuntimeException("Failed to create classloader for PA.", e);
        }

        return localPAClassLoader;
    }

}
