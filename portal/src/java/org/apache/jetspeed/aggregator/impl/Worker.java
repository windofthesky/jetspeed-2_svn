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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Worker thread processes jobs and notify its WorkerMonitor when completed.
 * When no work is available, the worker simply sets itself in a waiting mode
 * pending reactivation by the WorkerMonitor
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class Worker extends Thread
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(Worker.class);

    /** Running status of this worker */
    private boolean running = true;

    /** Counter of consecutive jobs that can be processed before the
        worker being actually put back on the idle queue */
    private int jobCount = 0;

    /** Job to process */
    private Runnable job = null;

    /** Monitor for this Worker */
    private WorkerMonitor monitor = null;

    public Worker(WorkerMonitor monitor)
    {
        super();
        this.setMonitor(monitor);
        this.setDaemon(true);
    }

    public Worker(WorkerMonitor monitor, ThreadGroup tg, String name)
    {
        super(tg, name);
        this.setMonitor(monitor);
        this.setDaemon(true);
    }

    /**
     * Return the number of jobs processed by this worker since the last time it
     * has been on the idle queue
     */
    public int getJobCount()
    {
        return this.jobCount;
    }

    /**
     * Reset the processed job counter
     */
    public void resetJobCount()
    {
        this.jobCount=0;
    }

    /**
     * Sets the running status of this Worker. If set to false, the Worker will
     * stop after processing its current job.
     */
    public void setRunning(boolean status)
    {
        this.running = status;
    }

    /**
     * Sets the moitor of this worker
     */
    public void setMonitor(WorkerMonitor monitor)
    {
        this.monitor = monitor;
    }

    /**
     * Sets the job to execute
     */
    public void setJob(Runnable job)
    {
        this.job = job;
    }

    /**
     * Retrieves the job to execute
     */
    public Runnable getJob()
    {
        return this.job;
    }

    /**
     * Process the job assigned, then notify Monitor. If no job available,
     * go into sleep mode
     */
    public void run()
    {
        while (running)
        {
            // wait for a job to come
            synchronized (this)
            {
                if (this.job == null)
                {
                    try
                    {
                        this.wait();
                    }
                    catch (InterruptedException e)
                    {
                        // nothing done
                    }
                }
            }

            // process it
            if (this.job != null)
            {
                try
                {
                    log.debug("Processing job for window :" + ((RenderingJob)job).getWindow().getId());
                    this.job.run();
                }
                catch (Throwable t)
                {
                    log.error("Thread error", t);
                }
            }

            this.jobCount++;

            // release the worker
            monitor.release(this);
        }
    }
}
