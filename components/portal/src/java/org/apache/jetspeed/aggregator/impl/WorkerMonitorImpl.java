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

package org.apache.jetspeed.aggregator.impl;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.aggregator.Worker;
import org.apache.jetspeed.aggregator.WorkerMonitor;
import org.apache.jetspeed.util.Queue;
import org.apache.jetspeed.util.FIFOQueue;

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
    public WorkerMonitorImpl(int minWorkers, int maxWorkers, int spareWorkers, int maxJobsPerWorker)
    {
        this.minWorkers = minWorkers;
        this.maxWorkers = maxWorkers;
        this.spareWorkers = spareWorkers;
        this.maxJobsPerWorker = maxJobsPerWorker;
    }
    
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(WorkerMonitorImpl.class);

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
    protected Stack workers = new Stack();

    /** The thread group used to group all worker threads */
    protected ThreadGroup tg = new ThreadGroup("Workers");

    /** Job queue */
    protected Queue queue;

    public void start()
    {
        addWorkers(this.minWorkers);
        setQueue(new FIFOQueue());
    }

    public void stop()
    {        
    }
    
    public void setQueue(Queue queue)
    {
        this.queue = queue;
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
                Worker worker = new WorkerImpl(this, this.tg, "WORKER_" + (++sCount));
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
    public Worker getWorker()
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

            return (Worker)workers.pop();
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
        Worker worker = this.getWorker();

        AccessControlContext context = AccessController.getContext();
        if (worker==null)
        {
            queue.push(job);
            queue.push(context);
        }
        else
        {
            try
            {
                synchronized (worker)
                {
                    worker.setJob(job, context);
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
     * Put back the worker in the idle queue unless there are pending jobs and
     * worker can still be committed to a new job before being released.
     */
    public void release(Worker worker)
    {
        // if worker can still proces some jobs assign the first
        // backlog job to this worker, else reset job count and put
        // it on the idle queue.

        synchronized (worker)
        {
            if ((worker.getJobCount()<this.maxJobsPerWorker)&&(queue.size()>0))
            {
                RenderingJob job = (RenderingJob)queue.pop();
                AccessControlContext context = (AccessControlContext)queue.pop();
                worker.setJob(job, context);
                runningJobs--;
                return;
            }
            else
            {
                worker.setJob(null);
                worker.resetJobCount();
                runningJobs--;
            }
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
    
}
