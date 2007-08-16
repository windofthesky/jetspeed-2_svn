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


/**
 * Wrapper for a <code>JobEntry</code> to actually perform the job's action.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public class WorkerThread
    implements Runnable
{
    /**
     * The <code>JobEntry</code> to run.
     */
    private JobEntry je = null;

    /**
     * The {@link org.apache.fulcrum.logging.Logger} facility to use.
     */

    /**
     * Creates a new worker to run the specified <code>JobEntry</code>.
     *
     * @param je The <code>JobEntry</code> to create a worker for.
     */
    public WorkerThread(JobEntry je)
    {
        this.je = je;
    }

    /**
     * Run the job.
     */
    public void run()
    {
        if (je == null || je.isActive())
        {
            return;
        }

        try
        {
            if (! je.isActive())
            {
                je.setActive(true);
                logStateChange("started");

                // We should have a set of job packages and
                // search through them like the module
                // loader does. This right here requires the
                // getTask() method to return a class name.
                String className = je.getTask();

                //If a FactoryService is registered, use it. Otherwise,
                //instantiate the ScheduledJob directly.
                ScheduledJob sc = (ScheduledJob)Class.forName(className).newInstance();
                sc.execute(je);
            }
        }
        catch (Exception e)
        {
            //!! use the service for logging
            //Log.error("Error in WorkerThread for sheduled job #" +
            //             je.getPrimaryKey() + ", task: " + je.getTask(), e);
        }
        finally
        {
            if (je.isActive())
            {
                je.setActive(false);
                logStateChange("completed");
            }
        }
    }

    /**
     * Macro to log <code>JobEntry</code> status information.
     *
     * @param state The new state of the <code>JobEntry</code>.
     */
    private final void logStateChange(String state)
    {
        //!! use the service to log.
        //Log.debug("Scheduled job #" + je.getPrimaryKey() + ' ' + state +
        //    ", task: " + je.getTask());
    }
}
