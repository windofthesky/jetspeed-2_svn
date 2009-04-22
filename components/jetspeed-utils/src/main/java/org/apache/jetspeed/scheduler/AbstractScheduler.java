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
package org.apache.jetspeed.scheduler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for a cron like scheduler.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public abstract class AbstractScheduler implements Scheduler
{
    private final static Logger log = LoggerFactory.getLogger(MemoryBasedScheduler.class);
    
    /**
     * The queue.
     */
    protected JobQueue scheduleQueue = null;

    /**
     * The main loop for starting jobs.
     */
    protected MainLoop mainLoop;

    /**
     * The thread used to process commands.
     */
    protected Thread thread;

    /**
     * Creates a new instance.
     */
    public AbstractScheduler()
    {
        mainLoop = null;
        thread = null;
    }

    public void start()
    {
    }
    
    public void stop()
    {
        if(getThread() != null)
        {
            getThread().interrupt();
        }
    }

    /**
     * Get a specific Job from Storage.
     *
     * @param oid The int id for the job.
     * @return A JobEntry.
     * @exception Exception, a generic exception.
     */
    public abstract JobEntry getJob(int oid)
        throws Exception;

    /**
     * Add a new job to the queue.  Before adding a job, calculate the runtime 
     * to make sure the entry will be placed at the right order in the queue.
     *
     * @param je A JobEntry with the job to add.
     * @exception Exception, a generic exception.
     */
    public abstract void addJob(JobEntry je)
        throws Exception; 
        
    /**
     * Remove a job from the queue.
     *
     * @param je A JobEntry with the job to remove.
     * @exception Exception, a generic exception.
     */
    public abstract void removeJob(JobEntry je)
        throws Exception;

    /**
     * Modify a Job.
     *
     * @param je A JobEntry with the job to modify
     * @exception Exception, a generic exception.
     */
    public abstract void updateJob(JobEntry je)
        throws Exception;
        
    /**
     * List jobs in the queue.  This is used by the scheduler UI.
     *
     * @return A List of jobs.
     */
    public List listJobs()
    {
        return scheduleQueue.list();
    }

    /**
     * Return the thread being used to process commands, or null if
     * there is no such thread.  You can use this to invoke any
     * special methods on the thread, for example, to interrupt it.
     *
     * @return A Thread.
     */
    public synchronized Thread getThread()
    {
        return thread;
    }

    /**
     * Set thread to null to indicate termination.
     */
    private synchronized void clearThread()
    {
        thread = null;
    }

    /**
     * Start (or restart) a thread to process commands, or wake up an
     * existing thread if one is already running.  This method can be
     * invoked if the background thread crashed due to an
     * unrecoverable exception in an executed command.
     */
    public synchronized void restart()
    {
        if (thread == null)
        {
            // Create the the housekeeping thread of the scheduler. It will wait
            // for the time when the next task needs to be started, and then
            // launch a worker thread to execute the task.
            thread = new Thread(mainLoop, Scheduler.SERVICE_NAME);
            // Indicate that this is a system thread. JVM will quit only when there
            // are no more active user threads. Settings threads spawned internally
            // by CPS as daemons allows commandline applications 
            // to terminate in an orderly manner.
            thread.setDaemon(true);
            thread.start();
        }
        else
        {
            notify();
        }
    }

    /**
     *  Return the next Job to execute, or null if thread is
     *  interrupted.
     *
     * @return A JobEntry.
     * @exception Exception, a generic exception.
     */
    private synchronized JobEntry nextJob()
        throws Exception
    {
        try
        {
            while ( !Thread.interrupted() )
            {
                // Grab the next job off the queue.
                JobEntry je = scheduleQueue.getNext();

                if (je == null)
                {
                    // Queue must be empty. Wait on it.
                    wait();
                }
                else
                {
                    long now = System.currentTimeMillis();
                    long when = je.getNextRuntime();

                    if ( when > now )
                    {
                        // Wait till next runtime.
                        wait(when - now);
                    }
                    else
                    {
                        // Update the next runtime for the job.
                        scheduleQueue.updateQueue(je);
                        // Return the job to run it.
                        return je;
                    }
                }
            }
        }
        catch (InterruptedException ex)
        {
        }

        // On interrupt.
        return null;
    }

    /**
     * Inner class.  This is isolated in its own Runnable class just
     * so that the main class need not implement Runnable, which would
     * allow others to directly invoke run, which is not supported.
     */
    protected class MainLoop
        implements Runnable
    {
        /**
         * Method to run the class.
         */
        public void run()
        {
            try
            {
                for(;;)
                {
                    JobEntry je = nextJob();
                    if ( je != null )
                    {
                        // Start the thread to run the job.
                        Runnable wt = new WorkerThread(je);
                        Thread helper = new Thread(wt);
                        helper.start();
                    }
                    else
                    {
                        break;
                    }
                }
            }
            catch(Exception e)
            {
                // Log error.
                log.error("Error running a Scheduled Job: " + e);
            }
            finally
            {
                clearThread();
            }
        }
    }
}
