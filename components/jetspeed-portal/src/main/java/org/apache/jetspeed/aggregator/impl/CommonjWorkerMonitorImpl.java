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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.aggregator.Worker;
import org.apache.jetspeed.aggregator.WorkerMonitor;
import org.apache.jetspeed.container.PortletWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import commonj.work.Work;
import commonj.work.WorkEvent;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkManager;

/**
 * The CommonjWorkerMonitorImpl is responsible for dispatching jobs to workers
 * It wraps CommonJ WorkManager supported by IBM WebSphere and BEA WebLogic sever.
 *
 * @author <a href="mailto:woon_san@apache.org">Woonsan Ko</a>
 * @version $Id: CommonjWorkerMonitorImpl.java 568339 2007-08-22 00:14:51Z ate $
 */
public class CommonjWorkerMonitorImpl implements WorkerMonitor, WorkListener
{

    /**
     * @deprecated Use {@link RenderingJob#ACCESS_CONTROL_CONTEXT_WORKER_ATTR} instead.
     */
    public static final String ACCESS_CONTROL_CONTEXT_WORKER_ATTR = RenderingJob.ACCESS_CONTROL_CONTEXT_WORKER_ATTR;

    public static final String COMMONJ_WORK_ITEM_ATTR = WorkItem.class.getName();
    public static final String WORKER_THREAD_ATTR = Worker.class.getName();

    /** CommonJ Work Manamger provided by JavaEE container */
    protected WorkManager workManager;

    /** If true, invoke interrupt() on the worker thread when the job is timeout. */
    protected boolean interruptOnTimeout = true;

    /** Enable rendering job works monitor thread for timeout checking */
    protected boolean jobWorksMonitorEnabled = true;

    /** Rendering job works to be monitored for timeout checking */
    protected Map<WorkItem,RenderingJobCommonjWork> jobWorksMonitored = Collections.synchronizedMap(new HashMap<WorkItem,RenderingJobCommonjWork>());

    public CommonjWorkerMonitorImpl(WorkManager workManager)
    {
        this(workManager, true);
    }

    public CommonjWorkerMonitorImpl(WorkManager workManager, boolean jobWorksMonitorEnabled)
    {
        this(workManager, jobWorksMonitorEnabled, true);
    }

    public CommonjWorkerMonitorImpl(WorkManager workManager, boolean jobWorksMonitorEnabled, boolean interruptOnTimeout)
    {
        this.workManager = workManager;
        this.jobWorksMonitorEnabled = jobWorksMonitorEnabled;
        this.interruptOnTimeout = interruptOnTimeout;
    }

    /** Commons logging */
    protected final static Logger log = LoggerFactory.getLogger(CommonjWorkerMonitorImpl.class);

    /** Renering Job Timeout monitor */
    protected CommonjWorkerRenderingJobTimeoutMonitor jobMonitor = null;

    public void start()
    {
        if (this.jobWorksMonitorEnabled)
        {
            jobMonitor = new CommonjWorkerRenderingJobTimeoutMonitor(1000);
            jobMonitor.start();
        }
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
        job.setWorkerAttribute(RenderingJob.ACCESS_CONTROL_CONTEXT_WORKER_ATTR, context);

        try
        {
            RenderingJobCommonjWork jobWork = new RenderingJobCommonjWork(job);
            WorkItem workItem = this.workManager.schedule(jobWork, this);
            job.setWorkerAttribute(COMMONJ_WORK_ITEM_ATTR, workItem);

            if (this.jobWorksMonitorEnabled)
            {
                this.jobWorksMonitored.put(workItem, jobWork);
            }
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
     * Wait for all rendering jobs in the collection to finish successfully or otherwise.
     * @param renderingJobs the Collection of rendering job objects to wait for.
     */
    public void waitForRenderingJobs(List<RenderingJob> renderingJobs)
    {
        if (this.jobWorksMonitorEnabled)
        {
            try
            {
                for (RenderingJob job : renderingJobs)
                {
                    PortletContent portletContent = job.getPortletContent();

                    synchronized (portletContent)
                    {
                        if (!portletContent.isComplete())
                        {
                            portletContent.wait();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                log.error("Exception during synchronizing all portlet rendering jobs.", e);
            }
        }
        else
        {
            // We cannot use WorkingManager#waitForAll(workitems, timeout_ms) for timeout.
            // The second argument could be either WorkManager.IMMEDIATE or WorkManager.INDEFINITE.

            try
            {
                if (!renderingJobs.isEmpty())
                {
                    Object lock = new Object();
                    MonitoringJobCommonjWork monitoringWork = new MonitoringJobCommonjWork(lock, renderingJobs);

                    synchronized (lock)
                    {
                        this.workManager.schedule(monitoringWork, this);
                        lock.wait();
                    }
                }
            }
            catch (Exception e)
            {
                log.error("Exception during synchronizing all portlet rendering jobs.", e);
            }
        }
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

    // commonj.work.WorkListener implementations

    public void workAccepted(WorkEvent we)
    {
        WorkItem workItem = we.getWorkItem();
        if (log.isDebugEnabled())
        {
            log.debug("[CommonjWorkMonitorImpl] workAccepted: " + workItem);
        }
    }

    public void workRejected(WorkEvent we)
    {
        WorkItem workItem = we.getWorkItem();
        if (log.isDebugEnabled())
        {
            log.debug("[CommonjWorkMonitorImpl] workRejected: " + workItem);
        }

        if (this.jobWorksMonitorEnabled)
        {
            removeMonitoredJobWork(workItem);
        }
    }

    public void workStarted(WorkEvent we)
    {
        WorkItem workItem = we.getWorkItem();
        if (log.isDebugEnabled())
        {
            log.debug("[CommonjWorkMonitorImpl] workStarted: " + workItem);
        }
    }

    public void workCompleted(WorkEvent we)
    {
        WorkItem workItem = we.getWorkItem();
        if (log.isDebugEnabled())
        {
            log.debug("[CommonjWorkMonitorImpl] workCompleted: " + workItem);
        }

        if (this.jobWorksMonitorEnabled)
        {
            removeMonitoredJobWork(workItem);
        }
    }

    protected Object removeMonitoredJobWork(WorkItem workItem)
    {
        return this.jobWorksMonitored.remove(workItem);
    }

    class RenderingJobCommonjWork implements Work
    {

        protected RenderingJob job;

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
            if (jobWorksMonitorEnabled || interruptOnTimeout)
            {
                this.job.setWorkerAttribute(WORKER_THREAD_ATTR, Thread.currentThread());
            }

            this.job.run();
        }

        public void release()
        {
        }

        public RenderingJob getRenderingJob()
        {
            return this.job;
        }
    }

    class MonitoringJobCommonjWork implements Work
    {

        protected Object lock;
        protected List<RenderingJob> renderingJobs;

        public MonitoringJobCommonjWork(Object lock, List<RenderingJob> jobs)
        {
            this.lock = lock;
            this.renderingJobs = new ArrayList<RenderingJob>(jobs);
        }

        public boolean isDaemon()
        {
            return false;
        }

        public void run()
        {
            try
            {
                while (!this.renderingJobs.isEmpty())
                {
                    for (Iterator<RenderingJob> it = this.renderingJobs.iterator(); it.hasNext(); )
                    {
                        RenderingJob job = it.next();
                        WorkItem workItem = (WorkItem) job.getWorkerAttribute(COMMONJ_WORK_ITEM_ATTR);
                        int status = WorkEvent.WORK_ACCEPTED;

                        if (workItem != null)
                        {
                            status = workItem.getStatus();
                        }

                        boolean isTimeout = job.isTimeout();

                        if (isTimeout)
                        {
                            PortletContent content = job.getPortletContent();

                            if (interruptOnTimeout)
                            {
                                Thread worker = (Thread) job.getWorkerAttribute(WORKER_THREAD_ATTR);

                                if (worker != null)
                                {
                                    synchronized (content)
                                    {
                                        if (!content.isComplete())
                                        {
                                            worker.interrupt();
                                            content.wait();
                                        }
                                    }
                                }
                            }
                            else
                            {
                                synchronized (content)
                                {
                                    content.complete();
                                }
                            }
                        }

                        if (status == WorkEvent.WORK_COMPLETED || status == WorkEvent.WORK_REJECTED || isTimeout)
                        {
                            it.remove();
                        }
                    }

                    if (!this.renderingJobs.isEmpty())
                    {
                        synchronized (this)
                        {
                            wait(100);
                        }
                    }
                }

                synchronized (this.lock)
                {
                    this.lock.notify();
                }
            }
            catch (Exception e)
            {
                log.error("Exceptiong during job timeout monitoring.", e);
            }
        }

        public void release()
        {
        }

    }

    class CommonjWorkerRenderingJobTimeoutMonitor extends Thread {

        long interval = 1000;
        boolean shouldRun = true;

        CommonjWorkerRenderingJobTimeoutMonitor(long interval)
        {
            super("CommonjWorkerRenderingJobTimeoutMonitor");
            setDaemon(true);

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

        public void run()
        {
            while (shouldRun)
            {
                try
                {
                    List<RenderingJobCommonjWork> timeoutJobWorks = new ArrayList<RenderingJobCommonjWork>();

                    for (RenderingJobCommonjWork jobWork : jobWorksMonitored.values() )
                    {
                        RenderingJob job = jobWork.getRenderingJob();

                        if (job.isTimeout())
                        {
                            timeoutJobWorks.add(jobWork);
                        }
                    }

                    // Now, we can kill the timeout worker(s).
                    for (RenderingJobCommonjWork jobWork : timeoutJobWorks )
                    {
                        RenderingJob job = jobWork.getRenderingJob();

                        // If the job is just completed, then do not kill the worker.
                        if (job.isTimeout())
                        {
                            killJobWork(jobWork);
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

        public void killJobWork(RenderingJobCommonjWork jobWork)
        {
            RenderingJob job = jobWork.getRenderingJob();

            try
            {
                if (log.isWarnEnabled())
                {
                    PortletWindow window = job.getWindow();
                    log.warn("Portlet Rendering job to be interrupted by timeout (" + job.getTimeout() + "ms)" + (window != null ? ": "+window.getId().getStringId() : ""));
                }

                PortletContent content = job.getPortletContent();
                Thread worker = (Thread) job.getWorkerAttribute(WORKER_THREAD_ATTR);

                if (worker != null)
                {
                    synchronized (content)
                    {
                        if (!content.isComplete())
                        {
                            worker.interrupt();
                            content.wait();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                log.error("Exceptiong during job killing.", e);
            }
            finally
            {
                WorkItem workItem = (WorkItem) job.getWorkerAttribute(COMMONJ_WORK_ITEM_ATTR);

                if (workItem != null)
                {
                    removeMonitoredJobWork(workItem);
                }
            }
        }
    }
}
