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