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
package org.apache.jetspeed.cps.scheduler;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cps.CPSInitializationException;

/**
 * Service for a cron like scheduler that uses the
 * properties file instead of the database.
 * The methods that operate on jobs ( get,add,update,remove )
 * only operate on the queue in memory and changes are not reflected
 * to the properties file which was used to initilize the jobs.
 * An example is given below.  The job names are the class names that
 * extend ScheduledJob.
 *
 * <PRE>
 *
 * services.SchedulerService.scheduler.jobs=scheduledJobName,scheduledJobName2
 *
 * services.SchedulerService.scheduler.job.scheduledJobName.ID=1
 * services.SchedulerService.scheduler.job.scheduledJobName.SECOND=-1
 * services.SchedulerService.scheduler.job.scheduledJobName.MINUTE=-1
 * services.SchedulerService.scheduler.job.scheduledJobName.HOUR=7
 * services.SchedulerService.scheduler.job.scheduledJobName.WEEKDAY=-1
 * services.SchedulerService.scheduler.job.scheduledJobName.DAY_OF_MONTH=-1
 *
 * services.SchedulerService.scheduler.job.scheduledJobName2.ID=1
 * services.SchedulerService.scheduler.job.scheduledJobName2.SECOND=-1
 * services.SchedulerService.scheduler.job.scheduledJobName2.MINUTE=-1
 * services.SchedulerService.scheduler.job.scheduledJobName2.HOUR=7
 * services.SchedulerService.scheduler.job.scheduledJobName2.WEEKDAY=-1
 * services.SchedulerService.scheduler.job.scheduledJobName2.DAY_OF_MONTH=-1
 *
 * </PRE>
 *
 * Based on TamboraSchedulerService written by John Thorhauer.
 *
 * @author <a href="mailto:ekkerbj@netscpae.net">Jeff Brekke</a>
 * @author <a href="mailto:john@zenplex.com">John Thorhauer</a>
 * @version $Id$
 */
public class SchedulerServiceImpl
    extends BaseSchedulerService
{
    private final static Log log = LogFactory.getLog(SchedulerServiceImpl.class);
    
    /**
     * Constructor.
     *
     * @exception Exception, a generic exception.
     */
    public SchedulerServiceImpl()
        throws Exception
    {
        super();
    }

    /**
     * Called the first time the Service is used.<br>
     *
     * Load all the jobs from cold storage.  Add jobs to the queue
     * (sorted in ascending order by runtime) and start the scheduler
     * thread.
     */
    public void init()
        throws CPSInitializationException
    {
        try
        {
            log.info( "Initializing Scheduler CPS service");
            
            scheduleQueue = new JobQueue();
            mainLoop = new MainLoop();

            Vector jobProps = getConfiguration().getVector("jobs");
            Vector jobs = new Vector();
            // If there are scheduler.jobs defined then set up a job vector
            // for the scheduleQueue
            if (!jobProps.isEmpty())
            {
                for (int i=0;i<jobProps.size();i++)
                {
                    String jobName = (String)jobProps.elementAt(i);
                    String jobPrefix = "job." + jobName ;

                    if ( (getConfiguration().getString(jobPrefix + ".ID", null)) == null)
                    {
                        throw new Exception(
                        "There is an error in the properties file. \n" +
                        jobPrefix + ".ID is not found.\n");
                    }

                    int sec =  getConfiguration().getInt(jobPrefix + ".SECOND", -1);
                    int min =  getConfiguration().getInt(jobPrefix + ".MINUTE", -1);
                    int hr  =  getConfiguration().getInt(jobPrefix + ".HOUR", -1);
                    int wkday =  getConfiguration().getInt(jobPrefix + ".WEEKDAY", -1);
                    int dayOfMonth =  getConfiguration().getInt(jobPrefix + ".DAY_OF_MONTH", -1);

                    JobEntry je = new JobEntry(
                        sec,
                        min,
                        hr,
                        wkday,
                        dayOfMonth,
                        jobName);

                    jobs.addElement(je);

                }
            }

            if ( jobs != null && jobs.size() > 0 )
            {
                System.out.println("Starting jobs = " + jobs.size());            
                
                scheduleQueue.batchLoad(jobs);
                restart();
            }

            setInit(true);
        }
        catch (Exception e)
        {
            getCategory().error (
                "Cannot initialize SchedulerService!: ", e);
        }
    }

    /**
     * This method returns the job element from the internal queue.
     *
     * @param oid The int id for the job.
     * @return A JobEntry.
     * @exception Exception, a generic exception.
     */
    public JobEntry getJob(int oid)
        throws Exception
    {
        JobEntry je = new JobEntry(-1,
                                   -1,
                                   -1,
                                   -1,
                                   -1,
                                   null);
        return scheduleQueue.getJob(je);
    }

    /**
     * Add a new job to the queue.
     *
     * @param je A JobEntry with the job to add.
     * @exception Exception, a generic exception.
     */
    public void addJob(JobEntry je)
        throws Exception
    {
        // Add to the queue.
        scheduleQueue.add(je);
        restart();
    }

    /**
     * Remove a job from the queue.
     *
     * @param je A JobEntry with the job to remove.
     * @exception Exception, a generic exception.
     */
    public void removeJob(JobEntry je)
        throws Exception
    {
        // Remove from the queue.
        scheduleQueue.remove(je);
        restart();
    }

    /**
     * Modify a Job.
     *
     * @param je A JobEntry with the job to modify
     * @exception Exception, a generic exception.
     */
    public void updateJob(JobEntry je)
        throws Exception
    {
        try
        {
            je.calcRunTime();
        }
        catch(Exception e)
        {
            // Log problems.
            getCategory().error ( "Problem updating Scheduled Job: " + e);
        }
        // Update the queue.
       scheduleQueue.modify(je);
       restart();
    }
}
