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

package org.apache.cornerstone.framework.logging;

import java.util.List;
import java.util.Vector;

import org.apache.cornerstone.framework.api.worker.IWorkerManager;
import org.apache.cornerstone.framework.core.BaseObject;

/**
 * This class abstracts an in memory logger.  Log entries sent 
 * to this class get added ont an isntanc eof this class in memeory.
 * Once the configurable threshold limit is reached the inmemeory log gets
 * handed over to the WorkerManager.
 * 
 */

public class InMemoryLog extends BaseObject
{
    public static final String REVISION = "$Revision$";

    public static final String BUFFER_FLUSH_THRESHOLD = "buffer.flushThreshold";    
    public static final String DEFAULT_BUFFER_FLUSH_THRESHOLD = "1000";

    /**
     * Constructor
     *
     */
    public InMemoryLog()
    {
        _workerManager = LogFlusherWorkerManager.getSingleton();
                
        String inMemoryLogThresholdString = getConfigPropertyWithDefault(BUFFER_FLUSH_THRESHOLD, DEFAULT_BUFFER_FLUSH_THRESHOLD);
        _bufferFlushThreshold = Integer.parseInt(inMemoryLogThresholdString);
        
        _buffer = createBuffer();
    }
    
    /**
     * and InMemeory log with a size equals to th threshold +1.
     * @return List the in memory log.
     */
    protected List createBuffer()
    {
        return new Vector(_bufferFlushThreshold + 1);
    }

    /**
     * Adds an entry to the InMemoryLog
     * @param inMemoryLogEntry
     */
    public void addEntry(Object logEntry)
    {
        _buffer.add(logEntry);
        
        if (_buffer.size() == _bufferFlushThreshold )
        {
            List newJob = null;
            
            // launch a new thread and pass the vector to it
            synchronized (_buffer) {
                 newJob = _buffer;
                 _buffer = createBuffer();
            }     
            
            _workerManager.addJob(newJob);
        }
    }
    
    private List _buffer;
    private int _bufferFlushThreshold;
    protected IWorkerManager _workerManager;
}