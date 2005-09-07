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

import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.aggregator.impl.ContentServerAdapterImpl;
import org.apache.jetspeed.aggregator.impl.PageAggregatorImpl;
import org.apache.jetspeed.aggregator.impl.PortletAggregatorImpl;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.headerresource.impl.HeaderResourceFactoryImpl;

/**
 * <P>Test the aggregation service</P>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 * 
 */
public class TestAggregator extends TestRenderer
{
    private PortletAggregator portletAggregator;
    private PageAggregator pageAggregator;
    
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
        
        ArrayList paths = new ArrayList(4);
        paths.add("portlet/{mediaType}/jetspeed");
        paths.add("portlet/{mediaType}");
        paths.add("generic/{mediaType}");
        paths.add("/{mediaType}");
        
        HeaderResourceFactory headerFactory = new HeaderResourceFactoryImpl();
        ContentServerAdapter contentServer = new ContentServerAdapterImpl(headerFactory, paths);
        
        pageAggregator = new PageAggregatorImpl(renderer, contentServer);
        portletAggregator = new PortletAggregatorImpl(renderer);
        
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestAggregator.class);
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
