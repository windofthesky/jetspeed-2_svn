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

import org.apache.cornerstone.framework.api.worker.IWorkerManager;
import org.apache.cornerstone.framework.worker.BaseWorkerManager;

/**
 * 
 * Specialising the BaseWorkermanager to manage the 
 * LogFlusherWorker. 
 *
 */

public class LogFlusherWorkerManager extends BaseWorkerManager
{
    public static final String REVISION = "$Revision$";

    /**
     * Returns a singleton instance of the BaseWorkerManager
     * @return IWorkerManager
     */    
    public static IWorkerManager getSingleton()
    {
        return _Singleton;
    }
            
    private static LogFlusherWorkerManager _Singleton = new LogFlusherWorkerManager();
}