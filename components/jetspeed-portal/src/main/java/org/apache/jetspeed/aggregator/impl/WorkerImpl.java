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
import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.aggregator.RenderingJob;
import org.apache.jetspeed.aggregator.Worker;
import org.apache.jetspeed.aggregator.WorkerMonitor;
import org.apache.jetspeed.security.JSSubject;

/**
 * Worker thread processes jobs and notify its WorkerMonitor when completed.
 * When no work is available, the worker simply sets itself in a waiting mode
 * pending reactivation by the WorkerMonitor
 *
 * @author <a href="mailto:raphael@apache.org">Raphael Luta</a>
 * @author <a>Woonsan Ko</a>
 * @version $Id$
 */
public class WorkerImpl extends Thread implements Worker
{
    /** Commons logging */
    protected final static Log log = LogFactory.getLog(WorkerImpl.class);

    /** Running status of this worker */
    private boolean running = true;

    /** Counter of consecutive jobs that can be processed before the
        worker being actually put back on the idle queue */
    private int jobCount = 0;

    /** Job to process */
    private Runnable job = null;

    /** Context to process job within */
    private AccessControlContext context = null;

    /** Monitor for this Worker */
    private WorkerMonitor monitor = null;

    public WorkerImpl(WorkerMonitor monitor)
    {
        super();
        this.setMonitor(monitor);
        this.setDaemon(true);
    }

    public WorkerImpl(WorkerMonitor monitor, ThreadGroup tg, String name)
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
     * Sets the job to execute in security context
     */
    public void setJob(Runnable job, AccessControlContext context)
    {
        this.job = job;
        this.context = context;
    }

    /**
     * Sets the job to execute
     */
    public void setJob(Runnable job)
    {
        this.job = job;
        this.context = null;
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
                log.debug("Processing job for window :" + ((RenderingJob)job).getWindow().getId());
                Subject subject = null;
                if (this.context != null)
                {
                    subject = JSSubject.getSubject(this.context);
                }
                if (subject != null)
                {
                    JSSubject.doAsPrivileged(subject, new PrivilegedAction()
                        {
                            public Object run()
                            {
                                try 
                                {
                                    WorkerImpl.this.job.run();
                                }
                                catch (Throwable t)
                                {                        
                                    log.error("Thread error", t);
                                }
                                return null;                    
                            }
                        }, this.context);
                }
                else
                {
                    try
                    {
                        this.job.run();
                    }
                    catch (Throwable t)
                    {
                        log.error("Thread error", t);
                    }
                }
            }

            this.jobCount++;

            // release the worker
            ((WorkerMonitorImpl) monitor).release(this);
        }
    }
    
}