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
package org.apache.jetspeed.container.services.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.services.log.LogService;
import org.apache.pluto.services.log.Logger;


/**
 * Implements the logging service adaptor for the Pluto container 
 * adapting Jetspeed logging service implemented in Commons to Pluto
 * 
 * NOTE: this implementation may have performance issues
 *       since everytime we call isSomethingEnabled, we must get a logger
 *       I recommend deprecated Pluto's logging container service and 
 *       this adaptor once we get the Pluto source in Apache's CVS        
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$  
 */
public class PlutoLogService    
    implements  LogService
{
    private final static Log defaultLog = LogFactory.getLog(PlutoLogService.class);
    


    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.LogService#getLogger(java.lang.String)
     */
    public Logger getLogger(String component)
    {
        return new ContainerLoggerAdaptor(getConfiguredLogger(component));
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.services.log.LogService#getLogger(java.lang.Class)
     */
    public Logger getLogger(Class klass)
    {
        
        return new ContainerLoggerAdaptor(getConfiguredLogger(klass));
    }

    /**
     * Given a string class name returns a logger for that class, or if we can't find a logger for the class
     * the it returns the default logger for this class
     * 
     * @param className
     * @return Log The logger configured for the given class name or the default logger if failed to load class
     */
    private Log getConfiguredLogger(String className)
    {
        Class classe = null;
        Log log = defaultLog;
        
        try
        {        
            classe = Class.forName(className);
            log = LogFactory.getLog(classe);
        }
        catch (ClassNotFoundException e)
        {
            // use the default logger
        }
        catch (LogConfigurationException e)
        {
            // use the default logger            
        }
        return log;        
    }

    /**
     * Given a string class name returns a logger for that class, or if we can't find a logger for the class
     * the it returns the default logger for this class
     * 
     * @param classe the class to get a logger for
     * @return Log The logger configured for the given class name or the default logger if failed to load class
     */
    private Log getConfiguredLogger(Class classe)
    {
        Log log = defaultLog;
        
        try
        {        
            log = LogFactory.getLog(classe);
        }
        catch (LogConfigurationException e)
        {
            // use the default logger            
        }
        return log;        
    }
    
}
