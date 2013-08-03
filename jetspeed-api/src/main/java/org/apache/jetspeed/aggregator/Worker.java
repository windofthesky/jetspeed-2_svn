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
package org.apache.jetspeed.aggregator;

import java.security.AccessControlContext;

/**
 * A Worker represents a single thread in the parallel aggregation engine.
 * These worker threads process {@link RenderingJob} and notify its WorkerMonitor when completed.
 * When no work is available, the worker simply sets itself in a waiting mode
 * pending reactivation by the WorkerMonitor.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface Worker {
    /**
     * Return the count of jobs this worker is processing
     *
     * @return the count of jobs
     */
    int getJobCount();

    /**
     * Reset the processed job counter
     *
     */
    void resetJobCount();

    /**
     * Sets the running status of this Worker. If set to false, the Worker will
     * stop after processing its current job.
     *
     * @param status set to <tt>true</tt> to set this worker to running, false to turn off running
     */
    void setRunning(boolean status);

    /**
     * Sets the WorkMonitor for this worker
     *
     * @param monitor the WorkerMonitor who is monitoring this worker
     */
    void setMonitor(WorkerMonitor monitor);

    /**
     * Sets the job to execute in security context
     *
     * @param job the job to run
     * @context the security context
     * @deprecated Use only {@link #setJob(Runnable)} because AccessControlContext must not be directly accessed by
     *             a worker thread. Instead AccessControlContext must be accessed directly by the job implementation in order
     *             to use the AccessControlContext instance safely regardless of the physical worker thread implementation
     *             (e.g, WorkerImpl or container managed thread by commonj worker monitor).
     */
    void setJob(Runnable job, AccessControlContext context);

    /**
     * Sets the job to execute
     *
     * @param job the job to execute for this worker
     */
    void setJob(Runnable job);

    /**
     * Retrieves the job to execute for this worker
     *
     * @return the job executing
     */
    Runnable getJob();

    /**
     * Start executing the job on this worker thread
     *
     */
    void start();
}
