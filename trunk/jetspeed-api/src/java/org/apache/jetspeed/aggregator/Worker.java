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
package org.apache.jetspeed.aggregator;

import java.security.AccessControlContext;

/**
 * Worker thread processes jobs and notify its WorkerMonitor when completed.
 * When no work is available, the worker simply sets itself in a waiting mode
 * pending reactivation by the WorkerMonitor
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface Worker 
{
     int getJobCount();

    /**
     * Reset the processed job counter
     */
     void resetJobCount();

    /**
     * Sets the running status of this Worker. If set to false, the Worker will
     * stop after processing its current job.
     */
     void setRunning(boolean status);
     
    /**
     * Sets the moitor of this worker
     */
     void setMonitor(WorkerMonitor monitor);
     
    /**
     * Sets the job to execute in security context
     */
     void setJob(Runnable job, AccessControlContext context);

    /**
     * Sets the job to execute
     */
     void setJob(Runnable job);

    /**
     * Retrieves the job to execute
     */
     Runnable getJob();
     
     void start();
}
