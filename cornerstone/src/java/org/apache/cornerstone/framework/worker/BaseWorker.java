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

package org.apache.cornerstone.framework.worker;

import org.apache.cornerstone.framework.api.worker.IWorker;
import org.apache.cornerstone.framework.api.worker.IWorkerManager;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.log4j.Logger;

/**
 * This is a thread like implementaiton object.  It implements
 * the IWorker interface, and IWorker interface in turn extnds
 * the Runnable interface.
 * 
 */

public abstract class BaseWorker extends BaseObject implements IWorker
{
    public static final String REVISION = "$Revision$";

    /**
     * Abstract method for processing the job.
     * @param job
     */
    public abstract void processJob(Object job);
    
    /**
     * Constructor
     * @param workerManager
     */
    public BaseWorker(IWorkerManager workerManager)
    {
        _workerManager = workerManager;        
    }
    
    /**
     * runs to get a a job from workerManager, if it doesnt find a job it waits
     * otherwise it proceeds to processing the job.
     *  
     */
    public void run()
    {
        for (;;)
        {
            Object job = _workerManager.getJob();
            processJob(job);
        }
    }
    
    protected IWorkerManager _workerManager;
    private static Logger _Logger = Logger.getLogger(BaseWorker.class);
}