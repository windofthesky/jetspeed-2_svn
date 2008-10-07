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

import java.util.Stack;

import org.apache.log4j.Logger;

/**
 * An implemetnation of a Queue structure 
 * 
 */

public class Queue
{
    public static final String REVISION = "$Revision$";

    protected Stack _queue;

    public Queue()
    {
        _queue = new Stack();    
    }

    /**
     * Queues an object
     * @param object the object to be queued
     */
    public synchronized void enqueue(Object object)
    {
        _queue.push(object);
        notify();
    }

    /**
     * Gets an object from the queue
     * @return Object an object from the queue
     */
    public synchronized Object dequeue()
    {
        if (_queue.isEmpty())
        {
            try {
                _Logger.info("thread '" + Thread.currentThread().getName() + "' waiting");
                wait();
            } catch (InterruptedException e) {
                _Logger.info("thread '" + Thread.currentThread().getName() + "' Caught InterruptedException");
            }
            _Logger.info("thread '" + Thread.currentThread().getName() + "' woken up");
        }

        return _queue.pop();
    }

    private static Logger _Logger = Logger.getLogger(Queue.class);
}