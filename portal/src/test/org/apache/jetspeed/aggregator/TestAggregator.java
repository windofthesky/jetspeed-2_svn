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

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;

import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextFactory;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.profiler.ProfileLocator;

/**
 * <P>Test the aggregation service</P>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 * 
 */
public class TestAggregator extends JetspeedTest
{
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestAggregator(String name)
    {
        super(name);
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new JetspeedTestSuite(TestAggregator.class);
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

    /**
     * Simple test that verifies ...
     *
     * @throws Exception
     */
    public void testAggregator() throws Exception
    {
        Aggregator aggregator = (Aggregator) CommonPortletServices.getPortalService(Aggregator.SERVICE_NAME);
        Profiler profiler = (Profiler)Jetspeed.getComponentManager().getComponent(Profiler.class);

        RequestContext request = RequestContextFactory.getInstance(null, null, null);

        ProfileLocator locator = profiler.getProfile(request);
        request.setProfileLocator(locator);

        aggregator.build(request);

    }
}
