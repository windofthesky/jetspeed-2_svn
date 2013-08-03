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

import java.util.List;

/**
 * The Worker Monitor is a thread manager and monitor for asynchronous (parallel) portlet aggregation and rendering.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface WorkerMonitor 
{
    /**
     * Start processing the worker monitor
     *
     */
    void start();
    
    /**
     * Stop processing the worker monitor
     * Finish all jobs
     *
     */
    void stop();
    
    /**
     * Retrieves a snapshot of job count in the waiting (backlogged) queue
     * 
     * @return snapshot count of waiting jobs
     */
    int getQueuedJobsCount();
    
    /**
     * Returns a snapshot count of the available jobs
     * @return available jobs count
     */
    int getAvailableJobsCount();
    
    /**
     * Returns a snapshot count of the jobs currently running
     * 
     * @return snapshot count of running jobs
     */
    int getRunningJobsCount();
    
    /** 
     * Start processing a job, assign it to a worker thread.
     * 
     * @param job
     */
    void process(RenderingJob job);
    
    /**
     * Wait for all rendering jobs in the collection to finish successfully or otherwise. 
     * @param renderingJobs the Collection of rendering job objects to wait for.
     */
    public void waitForRenderingJobs(List<RenderingJob> renderingJobs);
}
