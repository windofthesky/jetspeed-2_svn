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

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ComponentAwareTestSuite;
import org.picocontainer.MutablePicoContainer;

/**
 * <P>Test the aggregation service</P>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 * 
 */
public class TestAggregator extends AbstractComponentAwareTestCase
{
    private MutablePicoContainer container;
    private PortletAggregator portletAggregator;
    private PageAggregator pageAggregator;
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestAggregator(String name)
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
        junit.awtui.TestRunner.main(new String[] { TestAggregator.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        container = (MutablePicoContainer) getContainer();
        pageAggregator = (PageAggregator) container.getComponentInstance(PageAggregator.class);
        portletAggregator = (PortletAggregator) container.getComponentInstance(PortletAggregator.class);                
    }

    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestAggregator.class);
        suite.setScript("org/apache/jetspeed/containers/test-aggregator-container.groovy");
        return suite;                                   
    }

    public void testBasic() throws Exception
    {
        assertNotNull("portlet aggregator is null", portletAggregator);
        assertNotNull("page aggregator is null", pageAggregator);
        /*
        Profiler profiler = (Profiler)Jetspeed.getComponentManager().getComponent(Profiler.class);

        RequestContext request = RequestContextFactory.getInstance(null, null, null);

        ProfileLocator locator = profiler.getProfile(request);
        request.setProfileLocator(locator);

        pageAggregator.build(request);
        */
    }

}
