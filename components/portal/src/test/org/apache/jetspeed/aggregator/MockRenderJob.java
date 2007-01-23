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

import org.apache.pluto.om.window.PortletWindow;


/**
 * MockRenderJob
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */

public class MockRenderJob  implements RenderingJob
{
    private long mockTime;
    private String jobName;
    private PortletWindow window;

    protected long startTimeMillis = 0;
    protected long timeout;
    
    public MockRenderJob(String jobName, long mockTime, PortletWindow window)
    {
        this.mockTime = mockTime;
        this.jobName = jobName;
        this.window = window;
    }

    /**
     * Sets portlet timout in milliseconds.
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets portlet timout in milliseconds.
     */
    public long getTimeout() {
        return this.timeout;
    }

    /**
     * Checks if the portlet rendering is timeout
     */
    public boolean isTimeout() {
        if ((this.timeout > 0) && (this.startTimeMillis > 0)) {
            return (System.currentTimeMillis() - this.startTimeMillis > this.timeout);
        }

        return false;
    }
    
    public void run()
    {       
        if (this.timeout > 0) {
            this.startTimeMillis = System.currentTimeMillis();
        }

        execute();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.aggregator.RenderingJob#execute()
     */
    public void execute()
    {
        System.out.println("Running mock rendering job ..." + jobName);
        try
        {
            Thread.sleep(mockTime);
        }
        catch (InterruptedException e)
        {
            System.out.println("Interrupted job..." + jobName);
        }
        System.out.println("mock job completed " + jobName);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.aggregator.RenderingJob#getWindow()
     */
    public PortletWindow getWindow()
    {
        // TODO Auto-generated method stub
        return window;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.aggregator.RenderingJob#getPortletContent()
     */
    public PortletContent getPortletContent()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
