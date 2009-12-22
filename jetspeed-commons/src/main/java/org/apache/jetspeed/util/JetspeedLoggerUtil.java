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
package org.apache.jetspeed.util;

import org.apache.jetspeed.logger.JetspeedLogger;
import org.apache.jetspeed.logger.JetspeedLoggerFactory;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;

/**
 * JetspeedLoggerUtil
 * 
 * @version $Id$
 */
public class JetspeedLoggerUtil
{
    private JetspeedLoggerUtil()
    {
    }

    /**
     * Returns a JetspeedLogger from the portal services component.
     * <P>
     * <EM>Note: A component which wants to use <CODE>JetspeedLogger</CODE> must invoke this method
     * whenever it tries to leave logs. The retrieved logger instance must not be kept for later use.
     * Jetspeed container can be reloaded any time and it can make the old logger instances invalid.</EM>
     * </P>
     * <P>
     * If Jetspeed container is not available, then it returns a NOOP logger instead.
     * which does not do anything.
     * </P>
     * @param clazz
     * @return
     */
    public static JetspeedLogger getLogger(Class<?> clazz)
    {
        PortletServices ps = JetspeedPortletServices.getSingleton();
        
        if (ps != null)
        {
            JetspeedLoggerFactory jsLoggerFactory = (JetspeedLoggerFactory) ps.getService(JetspeedLoggerFactory.class.getName());
            
            if (jsLoggerFactory != null)
            {
                return jsLoggerFactory.getLogger(clazz);
            }
        }
        
        return noopLogger;
    }

    /**
     * Returns a JetspeedLogger from the portal services component.
     * <P>
     * <EM>Note: A component which wants to use <CODE>JetspeedLogger</CODE> must invoke this method
     * whenever it tries to leave logs. The retrieved logger instance must not be kept for later use.
     * Jetspeed container can be reloaded any time and it can make the old logger instances invalid.</EM>
     * </P>
     * <P>
     * If Jetspeed container is not available, then it returns a NOOP logger instead.
     * which does not do anything.
     * </P>
     * @param name
     * @return
     */
    public static JetspeedLogger getLogger(String name)
    {
        PortletServices ps = JetspeedPortletServices.getSingleton();
        
        if (ps != null)
        {
            JetspeedLoggerFactory jsLoggerFactory = (JetspeedLoggerFactory) ps.getService(JetspeedLoggerFactory.class.getName());

            if (jsLoggerFactory != null)
            {
                return jsLoggerFactory.getLogger(name);
            }
        }
        
        return noopLogger;
    }
    
    private static JetspeedLogger noopLogger = new JetspeedLogger()
    {
        public void debug(String msg)
        {
        }

        public void debug(String format, Object arg)
        {
        }

        public void debug(String format, Object arg1, Object arg2)
        {
        }

        public void debug(String format, Object[] argArray)
        {
        }

        public void debug(String msg, Throwable t)
        {
        }

        public void error(String msg)
        {
        }

        public void error(String format, Object arg)
        {
        }

        public void error(String format, Object arg1, Object arg2)
        {
        }

        public void error(String format, Object[] argArray)
        {
        }

        public void error(String msg, Throwable t)
        {
        }

        public void info(String msg)
        {
        }

        public void info(String format, Object arg)
        {
        }

        public void info(String format, Object arg1, Object arg2)
        {
        }

        public void info(String format, Object[] arg1)
        {
        }

        public void info(String msg, Throwable t)
        {
        }

        public boolean isDebugEnabled()
        {
            return false;
        }

        public boolean isErrorEnabled()
        {
            return false;
        }

        public boolean isInfoEnabled()
        {
            return false;
        }

        public boolean isWarnEnabled()
        {
            return false;
        }

        public void warn(String msg)
        {
        }

        public void warn(String format, Object arg)
        {
        }

        public void warn(String format, Object[] argArray)
        {
        }

        public void warn(String format, Object arg1, Object arg2)
        {
        }

        public void warn(String msg, Throwable t)
        {
        }
    };
}
