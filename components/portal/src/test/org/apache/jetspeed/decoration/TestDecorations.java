/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.decoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.Path;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.jmock.core.Constraint;
import org.jmock.core.InvocationMatcher;

public class TestDecorations extends MockObjectTestCase
{
    private Path testPathHtmlEn;
    private Mock prcMock;
    private Mock rvMock;
    private PathResolverCache prc;
    private ResourceValidator rv;
    private Properties config;
    private Page page;
    private RequestContext requestContext;
    private Mock pageMock;
    private Mock factoryMock;
    private Mock fragmentMock;
    private Mock requestContextMock;
    private Fragment fragment;
    private Mock childFragmentMock;
    private Fragment childFragment;
    private Mock layoutMock;
    private LayoutDecoration layout;
    private Mock portletDecorMock;
    private PortletDecoration portletDecor;
    private DecorationFactory factory;

    protected void themeInitExpectations()
    {
        pageMock = new Mock(Page.class);
        page = (Page) pageMock.proxy();
        factoryMock = new Mock(DecorationFactory.class);
        factory = (DecorationFactory) factoryMock.proxy();
        requestContextMock = new Mock(RequestContext.class);
        requestContext = (RequestContext) requestContextMock.proxy();
        fragmentMock = new Mock(Fragment.class);
        fragment = (Fragment) fragmentMock.proxy();
        childFragmentMock = new Mock(Fragment.class);
        childFragment = (Fragment) childFragmentMock.proxy();
        layoutMock = new Mock(LayoutDecoration.class);
        layout = (LayoutDecoration) layoutMock.proxy();
        portletDecorMock = new Mock(PortletDecoration.class);
        portletDecor = (PortletDecoration) portletDecorMock.proxy();
        
        // Define expected behavior

                               
        ArrayList list = new ArrayList(1);
        list.add(childFragment);
        
        expectAndReturn(fragmentMock, "getFragments", list);
          
        expectAndReturn(atLeastOnce(), fragmentMock, "getId", "001");
      
        expectAndReturn(atLeastOnce(), childFragmentMock, "getId", "002");   

        expectAndReturn(childFragmentMock, "getFragments", null);
    }

    protected void setUp1() throws Exception
    {
        super.setUp();
        
        prcMock = new Mock(PathResolverCache.class);
        prcMock.expects(atLeastOnce()).method("getPath").withAnyArguments().will(returnValue(null));
        prcMock.expects(atLeastOnce()).method("addPath").withAnyArguments().isVoid();
        
        rvMock = new Mock(ResourceValidator.class);
        
        prc = (PathResolverCache)prcMock.proxy();
        rv = (ResourceValidator)rvMock.proxy();
        
        config = new Properties();
        config.setProperty("name", "test");
        
        testPathHtmlEn = new Path("/decorations/test/html/en");
    }

    public void testSimpleLocation() throws Exception
    {
        setUp1();
      
        String expectedResult = testPathHtmlEn.getChild("/images/myimage.gif").toString();
        rvMock.expects(once()).method("resourceExists").with(eq(expectedResult)).will(returnValue(true));

        BaseDecoration decoration = new BaseDecoration(config, rv, testPathHtmlEn, prc);
        
        String result = decoration.getResource("/images/myimage.gif");
        
        assertEquals(expectedResult, result);
        
        verify();
    }
    
    public void testResolution() throws Exception
    {
        setUp1();
        
        Path testPath = (Path) testPathHtmlEn.clone();
        String failure1 = testPath.getChild("/images/myimage.gif").toString();
        testPath.removeLastPathSegment();
        String failure2 = testPath.getChild("/images/myimage.gif").toString();
        testPath.removeLastPathSegment();
        String success = testPath.getChild("/images/myimage.gif").toString();
        
        Constraint[] constraints = new Constraint[]{eq(failure1), eq(failure2), eq(success)};
        
        rvMock.expects(atLeastOnce()).method("resourceExists").with(new OnConsecutiveInvokes(constraints))
              .will(onConsecutiveCalls(returnValue(false), returnValue(false), returnValue(true)));
        
        BaseDecoration decoration = new BaseDecoration(config, rv, testPathHtmlEn, prc);
        
        String result = decoration.getResource("/images/myimage.gif");
        
        assertEquals(success, result);
        
        verify();

    }
    
    public void testTheme()
    {
        themeInitExpectations();
        
        expectAndReturn(pageMock, "getRootFragment", fragment);

        expectAndReturn(factoryMock, 
                        "getDecoration", 
                        new Constraint[] {eq(page), eq(fragment), eq(requestContext)}, 
                        layout);
        
        expectAndReturn(factoryMock, 
                "getDecoration", 
                new Constraint[] {eq(page), eq(childFragment), eq(requestContext)}, 
                portletDecor);
            
        expectAndReturn(layoutMock, "getStyleSheet", "/decorations/layout/test/html/css/styles.css");
        
        expectAndReturn(portletDecorMock, "getStyleSheet", "/decorations/portlet/test/html/css/styles.css");
        
        
        fragmentMock.expects(once()).method("getId")
                                    .withNoArguments()
                                    .will(returnValue("001"));    

        childFragmentMock.expects(once()).method("getId")
                                         .withNoArguments()
                                         .will(returnValue("002")); 
                                         
        
        Theme theme = new PageTheme(page, factory, requestContext);
        
        assertEquals(layout, theme.getPageLayoutDecoration());
        
        assertEquals(2, theme.getStyleSheets().size());
        
        Iterator itr = theme.getStyleSheets().iterator();
        assertEquals("/decorations/layout/test/html/css/styles.css", itr.next());
        assertEquals("/decorations/portlet/test/html/css/styles.css", itr.next());
        
        assertEquals(layout, theme.getDecoration(fragment));
        assertEquals(portletDecor, theme.getDecoration(childFragment));
        
        verify();
    }

    public void testDecortaionFactory()
    {      
        
        rvMock = new Mock(ResourceValidator.class);
        rv = (ResourceValidator)rvMock.proxy();
        rvMock.expects(atLeastOnce()).method("resourceExists")
                                     .withAnyArguments()
                                     .will(returnValue(false));
        
        // Define expected behavior
        Mock servletContextMock = new Mock(ServletContext.class);
        
        DecorationFactoryImpl testFactory = new DecorationFactoryImpl("/decorations", rv, "tigris", "tigris");
        testFactory.setServletContext((ServletContext)servletContextMock.proxy());
        
        themeInitExpectations();
        
        expectAndReturn(fragmentMock, "getDecorator", "myLayoutDecorator");
         
        expectAndReturn(fragmentMock, "getType", Fragment.LAYOUT);

        expectAndReturn(childFragmentMock, "getType", Fragment.PORTLET);
        
//        expectAndReturn(pageMock, "getRootFragment", fragment);
        
        expectAndReturn(atLeastOnce(), requestContextMock, "getMediaType", "html");
        
        expectAndReturn(atLeastOnce(), requestContextMock, "getLocale", Locale.ENGLISH);   
        
        StringReaderInputStream is1 = new StringReaderInputStream("id=myLayoutDecoration");
        StringReaderInputStream is2 = new StringReaderInputStream("id=myPortletDecoration");
        
        expectAndReturn(atLeastOnce(), servletContextMock, "getResourceAsStream",new Constraint[] {eq("/decorations/layout/myLayoutDecorator/decorator.properties")}, is1);
        expectAndReturn(atLeastOnce(), servletContextMock, "getResourceAsStream",new Constraint[] {eq("/decorations/portlet/myPortletDecoration/decorator.properties")}, is2);
        
        Mock servletRequestMock = new Mock(HttpServletRequest.class);
        Mock sessionMock = new Mock(HttpSession.class);
        
        expectAndReturn(atLeastOnce(), servletRequestMock, "getSession", (HttpSession) sessionMock.proxy());
        expectAndReturn(atLeastOnce(), requestContextMock, "getRequest", (HttpServletRequest)servletRequestMock.proxy());
        
        expectAndReturn(atLeastOnce(), sessionMock, "getAttribute", new Constraint[]{eq(PortalReservedParameters.RESOVLER_CACHE_ATTR)}, new HashMap());
        //expectAndReturn(sessionMock, "getAttribute", PortalReservedParameters.RESOVLER_CACHE_ATTR);

        expectAndReturn(childFragmentMock, "getDecorator", "myPortletDecoration");

        expectAndReturn(pageMock, "getRootFragment", fragment);

        Theme theme = testFactory.getTheme(page, requestContext);
        
        Decoration result1 = theme.getDecoration(fragment);
        
        assertNotNull(result1);
        assertEquals("myLayoutDecorator", result1.getName());
        
        Decoration result2 = theme.getDecoration(childFragment);
        assertNotNull(result2);
        assertEquals("myPortletDecoration", result2.getName());
        
        verify();
        
    }
    
    protected void expectAndReturn(Mock mock, String methodName, Object returnValue)
    {
        mock.expects(once()).method(methodName)
                            .withNoArguments()
                            .will(returnValue(returnValue));
    }
    
    protected void expectAndReturn(Mock mock, String methodName, Constraint[] constraints, Object returnValue)
    {
        mock.expects(once()).method(methodName)
                            .with(constraints)
                            .will(returnValue(returnValue));
    }
    
    protected void expectAndReturn(InvocationMatcher matcher, Mock mock, String methodName, Object returnValue)
    {
        mock.expects(matcher).method(methodName)
                            .withNoArguments()
                            .will(returnValue(returnValue));
    }
    
    protected void expectAndReturn(InvocationMatcher matcher, Mock mock, String methodName, Constraint[] constraints, Object returnValue)
    {
        mock.expects(matcher).method(methodName)
                            .with(constraints)
                            .will(returnValue(returnValue));
    }
}
