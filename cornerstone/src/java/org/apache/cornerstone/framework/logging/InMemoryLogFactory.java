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

import org.apache.cornerstone.framework.factory.BaseFactory;

/**
 * Factory returns an instance of the inmemeory log
 *
 */

public class InMemoryLogFactory extends BaseFactory
{
    public static final String REVISION = "$Revision$";

    public static final String IN_MEMORY_LOG = "inMemoryLog";

    /**
     * Returns the singleton isntance of this factory class.
     * @return InMemoryLogFactory
     */
    public static InMemoryLogFactory getSingleton()
    {
        return _Singleton;
    }
    
    /**
     * Creates an isntance with a particular name.
     * @param name
     * @return an InMemoryLog
     */
    public Object createInstance(Object name)
    {
        String logClassVariableName = IN_MEMORY_LOG + "_" + name;
        InMemoryLog log = (InMemoryLog) getClassVariable(logClassVariableName);
        
        if ( log == null )
        {
            log = new InMemoryLog();
            setClassVariable(logClassVariableName, log);
        }
        
        return log;
    }

    /**
     * Override from parent to allow createInstance(name)
     */
    public Object createInstance()
    {
        throw new RuntimeException("use createInstance(Object name) instead");
    }

    private static InMemoryLogFactory _Singleton = new InMemoryLogFactory();
}