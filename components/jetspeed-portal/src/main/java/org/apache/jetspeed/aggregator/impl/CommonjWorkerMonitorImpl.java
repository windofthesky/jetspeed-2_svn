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

package org.apache.jetspeed.aggregator.impl;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.aggregator.WorkerMonitor;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.CurrentWorkerContext;

import commonj.work.WorkManager;
import commonj.work.Work;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkEvent;

/**
 * The CommonjWorkerMonitorImpl is responsible for dispatching jobs to workers
 * It wraps CommonJ WorkManager supported by IBM WebSphere and BEA WebLogic sever.
 *
 * @author <a href="mailto:woon_san@apache.org">Woonsan Ko</a>
 * @version $Id: CommonjWorkerMonitorImpl.java 568339 2007-08-22 00:14:51Z ate $
 */
public class CommonjWorkerMonitorImpl implements WorkerMonitor, WorkListener
{

    public static final String ACCESS_CONTROL_CONTEXT_WORKER_ATTR = AccessControlContext.class.getName();
    
    /** CommonJ Work Manamger provided by JavaEE container */
    protected WorkManager workManager;
    
    /** Work items to be monitored for timeout checking */
    protected List workItemsMonitored = Collections.synchronizedList(new LinkedList());

    public CommonjWorkerMonitorImpl(WorkManager workManager)
    {
        this.workManager = workManager;
    }
    
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(CommonjWorkerMonitorImpl.class);
    
    /** Renering Job Timeout monitor */
    protected CommonjWorkerRenderingJobTimeoutMonitor jobMonitor = null;
    
    public void start()
    {
        jobMonitor = new CommonjWorkerRenderingJobTimeoutMonitor(1000);
        jobMonitor.start();
    }

    public void stop()
    {    
    	if (jobMonitor != null)
        {
    		jobMonitor.endThread();
        }
        
    	jobMonitor = null;
    }
    
    /**
     * Assign a job to a worker and execute it or queue the job if no
     * worker is available.
     *
     * @param job the Job to process
     */
    public void process(RenderingJob job)
    {
        AccessControlContext context = AccessController.getContext();
        job.setWorkerAttribute(ACCESS_CONTROL_CONTEXT_WORKER_ATTR, context);
        
        try
        {
            WorkItem workItem = this.workManager.schedule(new RenderingJobCommonjWork(job), this);
            this.workItemsMonitored.add(workItem);
        }
        catch (Throwable t)
        {
            log.error("Worker exception", t);
        }
    }

    public int getQueuedJobsCount()
    {
        return 0;
    }
    
    /**
     * Returns a snapshot of the available jobs
     * @return available jobs
     */
    public int getAvailableJobsCount()
    {
        return 0;
    }
    
    public int getRunningJobsCount()
    {
        return 0;
    }
    
    public void workAccepted(WorkEvent we)
    {
        WorkItem workItem = we.getWorkItem();
        if (log.isDebugEnabled()) log.debug("[CommonjWorkMonitorImpl] workAccepted: " + workItem);
    }

    public void workRejected(WorkEvent we)
    {
        WorkItem workItem = we.getWorkItem();
        if (log.isDebugEnabled()) log.debug("[CommonjWorkMonitorImpl] workRejected: " + workItem);
        removeMonitoredWorkItem(workItem);
    }

    public void workStarted(WorkEvent we)
    {
        WorkItem workItem = we.getWorkItem();
        if (log.isDebugEnabled()) log.debug("[CommonjWorkMonitorImpl] workStarted: " + workItem);
    }

    public void workCompleted(WorkEvent we)
    {
        WorkItem workItem = we.getWorkItem();
        if (log.isDebugEnabled()) log.debug("[CommonjWorkMonitorImpl] workCompleted: " + workItem);
        removeMonitoredWorkItem(workItem);
    }
    
    protected boolean removeMonitoredWorkItem(WorkItem workItem)
    {
        return this.workItemsMonitored.remove(workItem);
    }
    
    class RenderingJobCommonjWork implements Work
    {

        protected RenderingJob job;

        public RenderingJobCommonjWork()
        {
        }
        
        public RenderingJobCommonjWork(RenderingJob job)
        {
            this.job = job;
        }

        public boolean isDaemon()
        {
            return false;
        }
        
        public void run()
        {
            this.job.run();
        }
        
        public void release()
        {
        }
        
        public void setRenderingJob(RenderingJob job)
        {
            this.job = job;
        }
        
        public RenderingJob getRenderingJob()
        {
            return this.job;
        }

    }

    class CommonjWorkerRenderingJobTimeoutMonitor extends Thread {

        long interval = 1000;
        boolean shouldRun = true;
        
        CommonjWorkerRenderingJobTimeoutMonitor(long interval) 
        {
            super("CommonjWorkerRenderingJobTimeoutMonitor");

            if (interval > 0) 
            {
                this.interval = interval;
            }
        }
        /**
         * Thread.stop() is deprecated.
         * This method achieves the same by setting the run varaible "shouldRun" to false and interrupting the Thread, 
         * effectively causing the thread to shutdown correctly.
         *
         */
        public void endThread()
        {
        	shouldRun = false;
        	this.interrupt();
        }
        
        public void run() {
            while (shouldRun) {
                try 
                {
                    synchronized (workItemsMonitored) 
                    {
                        for (Iterator it = workItemsMonitored.iterator(); it.hasNext(); )
                        {
                            WorkItem workItem = (WorkItem) it.next();
                            int status = workItem.getStatus();
                            
                            if (status == WorkEvent.WORK_COMPLETED || status == WorkEvent.WORK_REJECTED)
                            {
                                it.remove();
                            }
                            else
                            {
                            }
                        }
                    }
                } 
                catch (Exception e) 
                {
                    log.error("Exception during job monitoring.", e);
                }
               
                try 
                {
                    synchronized (this) 
                    {
                        wait(this.interval);
                    }
                } 
                catch (InterruptedException e) 
                {
                    ;
                }
            }
        }
    }
}
