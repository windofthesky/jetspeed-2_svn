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
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * @version $Id$
 */
public class WorkerMonitor
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(WorkerMonitor.class);

    /** Static counters for identifying workers */
    private static long sCount = 0;

    /** Minimum number of wokers to create */
    private int minWorkers = 5;

    /** Maximum number of workers */
    private int maxWorkers = 50;

    /** Minimum amount of spare workers */
    private int spareWorkers = 3;

    /** Maximum of job processed by a worker before being released */
    private int maxJobsPerWorker = 10;

    /** Stack containing currently idle workers */
    private Stack workers = new Stack();

    /** The thread group used to group all worker threads */
    private ThreadGroup tg = new ThreadGroup("Workers");

    /** Job queue */
    private Queue queue;

    public void init()
    {
        addWorkers(this.minWorkers);
        setQueue(new FIFOQueue());
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
                Worker worker = new Worker(this, this.tg, "WORKER_" + (++sCount));
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
                return;
            }
            else
            {
                worker.setJob(null);
                worker.resetJobCount();
            }
        }

        synchronized (this.workers)
        {
            this.workers.push(worker);
        }
    }
}
