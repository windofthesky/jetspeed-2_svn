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