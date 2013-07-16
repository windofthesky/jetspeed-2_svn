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
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.aggregator.WorkerMonitor;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.PortletWindowID;
import org.apache.jetspeed.util.FIFOQueue;
import org.apache.jetspeed.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The WorkerMonitor is responsible for dispatching jobs to workers
 * It uses an Apache HTTPd configuration style of min/max/spare workers
 * threads to throttle the rendering work.
 * If jobs come in faster that processing, they are stored in a queue
 * which is flushed periodically by a QueueMonitor.
 *
 * @author <a href="mailto:raphael@apache.org">Rapha\u00ebl Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class WorkerMonitorImpl implements WorkerMonitor
{
    /**
     * @deprecated Use {@link RenderingJob#ACCESS_CONTROL_CONTEXT_WORKER_ATTR} instead.
     */
    public static final String ACCESS_CONTROL_CONTEXT_WORKER_ATTR = RenderingJob.ACCESS_CONTROL_CONTEXT_WORKER_ATTR;

    public WorkerMonitorImpl(int minWorkers, int maxWorkers, int spareWorkers, int maxJobsPerWorker)
    {
        this.minWorkers = minWorkers;
        this.maxWorkers = maxWorkers;
        this.spareWorkers = spareWorkers;
        this.maxJobsPerWorker = maxJobsPerWorker;
    }

    /** Commons logging */
    protected final static Logger log = LoggerFactory.getLogger(WorkerMonitorImpl.class);

    /** Static counters for identifying workers */
    protected static long sCount = 0;

    /** Count of running jobs **/
    protected int runningJobs = 0;

    /** Minimum number of wokers to create */
    protected int minWorkers = 5;

    /** Maximum number of workers */
    protected int maxWorkers = 50;

    /** Minimum amount of spare workers */
    protected int spareWorkers = 3;

    /** Maximum of job processed by a worker before being released */
    protected int maxJobsPerWorker = 10;

    /** Stack containing currently idle workers */
    protected Stack<WorkerImpl> workers = new Stack<WorkerImpl>();

    /** The thread group used to group all worker threads */
    protected ThreadGroup tg = new ThreadGroup("Workers");

    /** Job queue */
    protected Queue queue;

    /** Workers to be monitored for timeout checking */
    protected List<WorkerImpl> workersMonitored = Collections.synchronizedList(new LinkedList<WorkerImpl>());

    /** Renering Job Timeout monitor */
    protected RenderingJobTimeoutMonitor jobMonitor = null;

    public void start()
    {
        addWorkers(this.minWorkers);
        this.queue = new FIFOQueue();

        jobMonitor = new RenderingJobTimeoutMonitor(1000);
        jobMonitor.start();
    }

    public void stop()
    {
        synchronized (workers)
        {
            for (WorkerImpl worker : new ArrayList<WorkerImpl>(workers))
            {
                worker.interrupt();
            }
        }
        synchronized (workersMonitored)
        {
            for (WorkerImpl worker : new ArrayList<WorkerImpl>(workersMonitored))
            {
                worker.interrupt();
            }
        }
    	if (jobMonitor != null)
    	{
    		jobMonitor.endThread();
    	}
    	jobMonitor = null;
    }

    /**
     * Create the request number of workers and add them to
     * list of available workers.
     *
     * @param wCount the number of workers to create
     */
    protected synchronized void addWorkers(int wCount)
    {
        int wCurrent = this.tg.activeCount();

        if (wCurrent < maxWorkers)
        {
            if (wCurrent + wCount > maxWorkers)
            {
                wCount = maxWorkers - wCurrent;
            }

            log.info("Creating "+ wCount +" workers -> "+ (wCurrent + wCount));

            for (int i = 0; i < wCount; ++i)
            {
                WorkerImpl worker = new WorkerImpl(this, this.tg, "WORKER_" + (++sCount));
                worker.start();
                workers.push(worker);
            }
        }
    }

    /**
     * Retrieves an idle worker
     *
     * @return a Worker from the idle pool or null if non available
     */
    protected WorkerImpl getWorker()
    {
        synchronized(this.workers)
        {
            if (this.workers.size() < spareWorkers)
            {
                addWorkers(spareWorkers);
            }

            if (this.workers.size() == 0)
            {
                return null;
            }

            return workers.pop();
        }
    }

    /**
     * Assign a job to a worker and execute it or queue the job if no
     * worker is available.
     *
     * @param job the Job to process
     */
    public void process(RenderingJob job)
    {
        WorkerImpl worker = this.getWorker();

        AccessControlContext context = AccessController.getContext();
        job.setWorkerAttribute(RenderingJob.ACCESS_CONTROL_CONTEXT_WORKER_ATTR, context);

        if (worker==null)
        {
            queue.push(job);
        }
        else
        {
            try
            {
                synchronized (worker)
                {
                    worker.setJob(job);

                    if (job.getTimeout() > 0)
                    {
                        workersMonitored.add(worker);
                    }

                    worker.notify();
                    runningJobs++;
                }
            }
            catch (Throwable t)
            {
                log.error("Worker exception", t);
            }
        }
    }

    /**
     * Wait for all rendering jobs in the collection to finish successfully or otherwise.
     * @param renderingJobs the Collection of rendering job objects to wait for.
     */
    public void waitForRenderingJobs(List<RenderingJob> renderingJobs)
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

    /**
     * Put back the worker in the idle queue unless there are pending jobs and
     * worker can still be committed to a new job before being released.
     */
    protected void release(WorkerImpl worker)
    {
        // if worker can still proces some jobs assign the first
        // backlog job to this worker, else reset job count and put
        // it on the idle queue.

        long jobTimeout = 0;

        RenderingJob oldJob = (RenderingJob) worker.getJob();
        if (oldJob != null)
        {
            jobTimeout = oldJob.getTimeout();
        }

        synchronized (worker)
        {
            RenderingJob job = null;

            if (worker.getJobCount() < this.maxJobsPerWorker)
            {
                job = (RenderingJob) queue.pop();

                if (job != null)
                {
                    worker.setJob(job);
                    runningJobs--;
                    return;
                }
            }

            worker.setJob(null);
            worker.resetJobCount();
            runningJobs--;
        }

        if (jobTimeout > 0)
        {
            workersMonitored.remove(worker);
        }

        synchronized (this.workers)
        {
            this.workers.push(worker);
        }
    }

    public int getQueuedJobsCount()
    {
        return queue.size();
    }

    /**
     * Returns a snapshot of the available jobs
     * @return available jobs
     */
    public int getAvailableJobsCount()
    {
        return workers.size();
    }

    public int getRunningJobsCount()
    {
        return this.tg.activeCount();
    }

    class RenderingJobTimeoutMonitor extends Thread
    {
        long interval = 1000;
        boolean shouldRun = true;

        RenderingJobTimeoutMonitor(long interval)
        {
            super("RenderingJobTimeoutMonitor");
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
                    // Because a timeout worker can be removed
                    // in the workersMonitored collection during iterating,
                    // copy timeout workers in the following collection to kill later.

                    List<WorkerImpl> timeoutWorkers = new ArrayList<WorkerImpl>();

                    synchronized (workersMonitored)
                    {
                        for (WorkerImpl worker : workersMonitored)
                        {
                            RenderingJob job = (RenderingJob) worker.getJob();

                            if ((null != job) && (job.isTimeout()))
                            {
                                timeoutWorkers.add(worker);
                            }
                        }
                    }

                    // Now, we can kill the timeout worker(s).
                    for (WorkerImpl worker : timeoutWorkers)
                    {
                        RenderingJob job = (RenderingJob) worker.getJob();

                        // If the job is just completed, then do not kill the worker.
                        if ((null != job) && (job.isTimeout()))
                        {
                            killJob(worker, job);
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
                }
            }
        }

        public void killJob(WorkerImpl worker, RenderingJob job)
        {
            try
            {
                if (log.isWarnEnabled())
                {
                    PortletWindow window = job.getWindow();
                    PortletWindowID windowId = (null != window ? window.getId() : null);
                    log.warn("Portlet Rendering job to be interrupted by timeout (" + job.getTimeout() + "ms): " + windowId.getStringId());
                }

                PortletContent content = job.getPortletContent();

                synchronized (content)
                {
                    if (!content.isComplete())
                    {
                        worker.interrupt();
                        content.wait();
                    }
                }

            } catch (Exception e)
            {
                log.error("Exceptiong during job killing.", e);
            }
        }
    }
}
