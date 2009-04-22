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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class MemoryBasedScheduler
    extends AbstractScheduler implements Scheduler
{
    private final static Logger log = LoggerFactory.getLogger(MemoryBasedScheduler.class);
    private Configuration config;
    
    /**
     * Constructor.
     *
     * @exception Exception, a generic exception.
     */
    public MemoryBasedScheduler(Configuration config)
        throws Exception
    {
        super();
        this.config = config;
    }

    private Configuration getConfiguration()
    {
        return config;
    }
    
    public void start()
    {
        try
        {            
            super.start();
            scheduleQueue = new JobQueue();
            mainLoop = new MainLoop();

            List jobProps = getConfiguration().getList("jobs");            
            List jobs = new ArrayList();
            // If there are scheduler.jobs defined then set up a job vector
            // for the scheduleQueue
            if (!jobProps.isEmpty())
            {
                for (int i=0;i<jobProps.size();i++)
                {
                    String jobName = (String)jobProps.get(i);
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

                    jobs.add(je);

                }
            }

            if ( jobs != null && jobs.size() > 0 )
            {
//                System.out.println("Starting jobs = " + jobs.size());            
                
                scheduleQueue.batchLoad(jobs);
                restart();
            }

        }
        catch (Exception e)
        {
            log.error ("Cannot initialize SchedulerService!: ", e);
        }
         
    }
    
    public void stop()
    {
        super.stop();
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
            log.error("Problem updating Scheduled Job: " + e);
        }
        // Update the queue.
       scheduleQueue.modify(je);
       restart();
    }
}
