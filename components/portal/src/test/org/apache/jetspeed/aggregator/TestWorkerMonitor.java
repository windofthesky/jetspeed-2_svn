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
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.aggregator.impl.ContentServerAdapterImpl;
import org.apache.jetspeed.aggregator.impl.PageAggregatorImpl;
import org.apache.jetspeed.aggregator.impl.PortletAggregatorImpl;
import org.apache.jetspeed.aggregator.impl.WorkerMonitorImpl;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.headerresource.impl.HeaderResourceFactoryImpl;
import org.apache.jetspeed.om.window.impl.PortletWindowImpl;
import org.apache.pluto.om.window.PortletWindow;

/**
 * <P>Test the aggregation service</P>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: TestAggregator.java 279463 2005-09-07 23:45:24Z taylor $
 * 
 */
public class TestWorkerMonitor extends TestCase
{    
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestWorkerMonitor.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
        
        
    }
    
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestWorkerMonitor.class);
    }

    private static final int JOB_COUNT = 2;
    
    public void testBasic() throws Exception
    {
        WorkerMonitor monitor = new WorkerMonitorImpl(1, 2, 1, 1);
        
        List jobs = new ArrayList(JOB_COUNT);
        for (int ix = 0; ix < JOB_COUNT; ix++)
        {
            PortletWindow window = new PortletWindowImpl("w" + String.valueOf(ix));       
            jobs.add(new MockRenderJob("Job-" + (ix + 1), 4000, window));
        }
        assertNotNull("monitor is null", monitor);
        monitor.start();
        for (int ix = 0; ix < JOB_COUNT; ix++)
           monitor.process((RenderingJob)jobs.get(ix));
        
        Thread.sleep(2000);
        assertTrue("available jobs expect 0", monitor.getAvailableJobsCount() == 0);
        assertTrue("running jobs expect 2", monitor.getRunningJobsCount() == 2);
        assertTrue("queued jobs expect 0", monitor.getQueuedJobsCount() == 0);
        monitor.stop();
    }

}
