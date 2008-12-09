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
package org.apache.jetspeed.window;

import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.HashMapWindowCache;
import org.apache.jetspeed.PortletFactoryMock;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.container.window.impl.PortletWindowAccessorImpl;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.ContentFragmentImpl;
import org.apache.jetspeed.container.PortletEntity;
import org.apache.jetspeed.container.PortletWindow;
import org.jmock.Mock;
import org.jmock.core.Invocation;
import org.jmock.core.InvocationMatcher;
import org.jmock.core.matcher.InvokeAtLeastOnceMatcher;
import org.jmock.core.stub.CustomStub;
import org.jmock.core.stub.ReturnStub;
import org.jmock.core.stub.VoidStub;

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
        entityMock = new Mock(PortletEntity.class);
        windowAccess = new PortletWindowAccessorImpl((PortletEntityAccessComponent) entityAccessMock.proxy(), PortletFactoryMock.instance, new HashMapWindowCache(),true);
    }

    public void testWindowAccess() throws Exception
    {
        ContentFragment f1 = new ContentFragmentImpl((Fragment) fragMock.proxy(), new HashMap());
        PortletEntity entity = (PortletEntity) entityMock.proxy();
        entityAccessMock.expects(new InvokeAtLeastOnceMatcher()).method("getPortletEntityForFragment")
                .withAnyArguments().will(new ReturnStub(entity));
        fragMock.expects(new InvokeAtLeastOnceMatcher()).method("getId").withNoArguments()
                .will(new ReturnStub("frag1"));
        entityMock.expects(new InvokeAtLeastOnceMatcher()).method("getId").withNoArguments().will(
            new ReturnStub("entity1"));
        entityMock.expects(new InvokeAtLeastOnceMatcher()).method("setPortletWindow").withAnyArguments().will(new VoidStub());

        PortletWindow window = windowAccess.getPortletWindow(f1);
        assertNotNull(window);
        assertEquals("frag1", window.getId().toString());

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
        
/*        
        windowAccess.removeWindows(entity);  
        
        windowAccess.getPortletWindow(f1);
        // Double that second call bypasses creating a new window
        //windowAccess.getPortletWindow(f1);
        
        windowListMock.verify();         
*/
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

    /**
     * Inline copy of InvokeCountMatcher from latest jMock Development Snapshot: 20050628-175146
     * so we don't need to depend on their SNAPSHOT release anymore but can fallback on their 1.0.1 version.
     * (doesn't seem they are going to release a new real version soon as it has been ages since 1.0.1 came out)
     * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
     *
     */
    private static class InvokeCountMatcher implements InvocationMatcher
    {
        private int invocationCount = 0;

        private int expectedCount;

        public InvokeCountMatcher(int expectedCount)
        {
            this.expectedCount = expectedCount;
        }

        public boolean matches(Invocation invocation)
        {
            return getInvocationCount() < expectedCount;
        }

        public void verify()
        {
            verifyHasBeenInvokedExactly(expectedCount);
        }

        public boolean hasDescription()
        {
            return true;
        }

        public StringBuffer describeTo(StringBuffer buffer)
        {
            return buffer.append("expected ").append(expectedCount).append(" times, invoked ").append(
                            getInvocationCount()).append(" times");
        }

        public int getInvocationCount()
        {
            return invocationCount;
        }

        public boolean hasBeenInvoked()
        {
            return invocationCount > 0;
        }

        public void invoked(Invocation invocation)
        {
            invocationCount++;
        }

        public void verifyHasBeenInvoked()
        {
            Assert.assertTrue("expected method was not invoked", hasBeenInvoked());
        }

        public void verifyHasBeenInvokedExactly(int expectedCount)
        {
            Assert.assertTrue("expected method was not invoked the expected number of times: expected " + expectedCount
                            + " times, was invoked " + invocationCount + " times", invocationCount == expectedCount);
        }

    }
}
