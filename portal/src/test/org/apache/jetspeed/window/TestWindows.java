/*
 * Created on Jul 1, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.window;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.container.window.impl.PortletWindowAccessorImpl;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.page.Fragment;
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
 * @author scott
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
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
        Fragment f1 = (Fragment) fragMock.proxy();
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
        windowAccess.getPortletWindow(f1);
        
        windowListMock.verify();         

    }

    interface CompositeWindowList extends PortletWindowList, PortletWindowListCtrl
    {

    }

    class ListAppendStub extends CustomStub
    {
        StringBuffer buf = new StringBuffer();
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
            return buf.append("");
        }
    }

}