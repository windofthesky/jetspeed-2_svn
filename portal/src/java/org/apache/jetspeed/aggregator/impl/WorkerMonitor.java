/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.jetspeed.aggregator.impl;

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
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
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
                worker.setJob((RenderingJob)queue.pop());
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