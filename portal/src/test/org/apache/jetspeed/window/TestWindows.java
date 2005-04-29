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
package org.apache.jetspeed.window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.container.window.impl.PortletWindowAccessorImpl;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.psml.ContentFragmentImpl;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.om.window.PortletWindowList;
import org.apache.pluto.om.window.PortletWindowListCtrl;
import org.jmock.Mock;
import org.jmock.core.Invocation;
import org.jmock.core.matcher.InvokeAtLeastOnceMatcher;
import org.jmock.core.matcher.InvokeCountMatcher;
import org.jmock.core.matcher.InvokeOnceMatcher;
import org.jmock.core.stub.CustomStub;
import org.jmock.core.stub.ReturnStub;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * 
*/
public class TestWindows extends TestCase
{
    protected PortletWindowAccessor windowAccess;
    protected Mock fragMock;
    protected Mock entityAccessMock;
    protected Mock entityMock;
    protected Mock windowListMock;

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestWindows.class);
    }

    /**
     * <p>
     * setUp
     * </p>
     * 
     * @see junit.framework.TestCase#setUp()
     * @throws java.lang.Exception
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        entityAccessMock = new Mock(PortletEntityAccessComponent.class);
        fragMock = new Mock(Fragment.class);
        entityMock = new Mock(MutablePortletEntity.class);
        windowListMock = new Mock(CompositeWindowList.class);
        windowAccess = new PortletWindowAccessorImpl((PortletEntityAccessComponent) entityAccessMock.proxy());
    }

    public void testWindowAccess() throws Exception
    {
        List windows = new ArrayList();
        ContentFragment f1 = new ContentFragmentImpl((Fragment) fragMock.proxy(), new HashMap());
        MutablePortletEntity entity = (MutablePortletEntity) entityMock.proxy();
        CompositeWindowList windowList = (CompositeWindowList) windowListMock.proxy();
        entityAccessMock.expects(new InvokeAtLeastOnceMatcher()).method("getPortletEntityForFragment")
                .withAnyArguments().will(new ReturnStub(entity));
        fragMock.expects(new InvokeAtLeastOnceMatcher()).method("getId").withNoArguments()
                .will(new ReturnStub("frag1"));
        entityMock.expects(new InvokeAtLeastOnceMatcher()).method("getPortletWindowList").withNoArguments().will(
                new ReturnStub(windowList));

        windowListMock.expects(new InvokeCountMatcher(4)).method("add").withAnyArguments().will(
                new ListAppendStub(windows));
        

        PortletWindow window = windowAccess.getPortletWindow(f1);
        assertNotNull(window);
        assertEquals("frag1", window.getId().toString());

        // Make sure the portlet entity's window list got updated
        assertEquals(1, windows.size());

        PortletWindow windowInList = (PortletWindow) windows.get(0);

        // The window in the entities list should be the same as the one
        // returned by getPortletWindow(f1)
        assertEquals(windowInList, window);

        // remove the window
        windowAccess.removeWindow(window);

        // Calling this after a remove go through th procedure of adding a newly
        // created window
        // back the portlet entity's list. We check this through vefirying calls
        // to our mocks
        windowAccess.getPortletWindow(f1);
        
        // Test same remove but via entity
        windowAccess.removeWindow(window);              
              
        assertNotNull(windowAccess.getPortletWindow(f1));                
        
        windowListMock.expects(new InvokeOnceMatcher()).method("iterator").withNoArguments().will(new ReturnStub(windows.iterator()));
        
        windowAccess.removeWindows(entity);  
        
        windowAccess.getPortletWindow(f1);
        // Double that second call bypasses creating a new window
        //windowAccess.getPortletWindow(f1);
        
        windowListMock.verify();         

    }

    interface CompositeWindowList extends PortletWindowList, PortletWindowListCtrl
    {

    }

    class ListAppendStub extends CustomStub
    {
       
        List list;

        /**
         * @param arg0
         */
        public ListAppendStub( List list )
        {
            super("Appends object to a list");
            this.list = list;
        }

        /**
         * <p>
         * invoke
         * </p>
         * 
         * @see org.jmock.core.Stub#invoke(org.jmock.core.Invocation)
         * @param arg0
         * @return @throws
         *         java.lang.Throwable
         */
        public Object invoke( Invocation invocation ) throws Throwable
        {
            list.add(invocation.parameterValues.get(0));
            return null;
        }
    }

}