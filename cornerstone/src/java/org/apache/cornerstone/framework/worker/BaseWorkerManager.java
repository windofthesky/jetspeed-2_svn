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

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Vector;
import org.apache.cornerstone.framework.api.worker.IWorker;
import org.apache.cornerstone.framework.api.worker.IWorkerManager;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.log4j.Logger;

/**
 * The BaseWorkerManager is responsible for creating teh threaded workers
 * based on the number of threads have been specifid for itself to create.
 * It allos for the adding and getting of a job from the manager,
 * which in turn are queued or dequeued respectively.
 *
 */

public abstract class BaseWorkerManager extends BaseObject implements IWorkerManager
{
    public static final String REVISION = "$Revision$";

    public static final String WORKER = "worker";
    public static final String WORKER_DOT = WORKER + Constant.DOT;

    public static final String CONFIG_WORKER_INSTANCE_CLASS_NAME = WORKER_DOT + Constant.INSTANCE_CLASS_NAME;    
    public static final String CONFIG_WORKER_COUNT = WORKER_DOT + "count";

    protected BaseWorkerManager()
    {
        _jobQueue = new Queue();
        String strWorkerCount = getConfigProperty(CONFIG_WORKER_COUNT);
        if ( strWorkerCount != null && strWorkerCount.trim().length() > 0)
        {
            _workerCount = Integer.parseInt(strWorkerCount);
            
            // get the worker classname
            String workerClassName = getConfigProperty(CONFIG_WORKER_INSTANCE_CLASS_NAME);
                         
            for ( int i = 0 ; i < _workerCount; i++ )
            {
                try
                {
                    Class[] types = {IWorkerManager.class};
                    Class workerClass = Class.forName(workerClassName);
                    Constructor cons = workerClass.getConstructor(types);
                    Object[] params = {this};
                    IWorker workerObject = (IWorker) cons.newInstance(params);
                    Thread thread = new Thread(workerObject);
                    thread.setName(workerClassName + i);
                    thread.start();
                    
                    _listOfWorkers.add(workerObject);
                    _listOfWorkerThreads.add(thread);
                }
                catch (Exception e)
                {
                    _Logger.error(e);
                }
            }
        }
        else
        {
            // it defaults to the intialised value of 1;
        }
    }

    /**
     * Adds a job to the WorkerManager internal queue
     * @param Object jobObject
     */
    public void addJob(Object jobObject)
    {
        _jobQueue.enqueue(jobObject);        
    }

    /**
     * Gets a job from the internal work amanger queue.
     * @return Object job
     */
    public Object getJob()
    {
        return _jobQueue.dequeue();
    }

    private static Logger _Logger = Logger.getLogger(BaseWorkerManager.class);
    protected int _workerCount = 1;
    protected List _listOfWorkers = new Vector();
    protected List _listOfWorkerThreads = new Vector();
    protected Queue _jobQueue;
}