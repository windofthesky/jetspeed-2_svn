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

import java.io.Serializable;
import java.lang.reflect.Method;

import org.apache.jetspeed.logger.JetspeedLogger;
import org.apache.jetspeed.logger.JetspeedLoggerFactory;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;

/**
 * JetspeedLoggerUtil to get access to portlet web application's logger or shared portal logger.
 * 
 * @version $Id$
 */
public class JetspeedLoggerUtil
{
    
    /**
     * Default logger factory class name
     */
    public static final String DEFAULT_LOGGER_FACTORY = "org.slf4j.LoggerFactory";
    
    /**
     * Default logger factory method to create a logger object.
     */
    public static final String DEFAULT_LOGGER_FACTORY_METHOD = "getLogger";
    
    private static JetspeedLogger noopLogger = new NOOPJetspeedLogger();
    
    private JetspeedLoggerUtil()
    {
    }
    
    /**
     * Returns a JetspeedLogger from the portlet application.
     * <P>
     * <EM>Note: This method tries to retrieve slf4j Logger by using current context classloader
     *           to get a portlet application specific logger.</EM>
     * </P>
     * <P>
     * If no portlet application specific slf4j Logger is available, then it returns null.
     * </P>
     * @param clazz
     * @return
     */
    public static JetspeedLogger getLocalLogger(Class<?> clazz)
    {
        try
        {
            Class<?> factoryClazz = Thread.currentThread().getContextClassLoader().loadClass(DEFAULT_LOGGER_FACTORY);
            Object logger = invokeDeclaredMethod(factoryClazz, DEFAULT_LOGGER_FACTORY_METHOD, new Class [] { Class.class }, new Object [] { clazz });
            
            if (logger != null)
            {
                return new DelegatingByReflectionJetspeedLogger(logger);
            }
        }
        catch (Exception e)
        {
            getSharedLogger(clazz).warn("Failed to create PA logger: " + e);
        }
        
        return null;
    }
    
    /**
     * Returns a JetspeedLogger from the portlet application.
     * <P>
     * <EM>Note: This method tries to retrieve slf4j Logger by using current context classloader
     *           to get a portlet application specific logger.</EM>
     * </P>
     * <P>
     * If no portlet application specific slf4j Logger is available, then it returns null.
     * </P>
     * @param name
     * @return
     */
    public static JetspeedLogger getLocalLogger(String name)
    {
        try
        {
            Class<?> factoryClazz = Thread.currentThread().getContextClassLoader().loadClass(DEFAULT_LOGGER_FACTORY);
            Object logger = invokeDeclaredMethod(factoryClazz, DEFAULT_LOGGER_FACTORY_METHOD, new Class [] { String.class }, new Object [] { name });
            
            if (logger != null)
            {
                return new DelegatingByReflectionJetspeedLogger(logger);
            }
        }
        catch (Exception e)
        {
            getSharedLogger(name).warn("Failed to create PA logger: " + e);
        }
        
        return null;
    }
    
    /**
     * Returns a JetspeedLogger from the portal services component.
     * <P>
     * <EM>Note: A component which wants to use the shared <CODE>JetspeedLogger</CODE> should invoke this method
     * whenever it tries to leave logs. The retrieved logger instance should not be kept for later use.
     * Jetspeed container can be reloaded any time and it can make the old logger instances invalid.</EM>
     * </P>
     * <P>
     * If Jetspeed container is not available, then it returns a NOOP logger instead.
     * which does not do anything.
     * </P>
     * @param clazz
     * @return
     */
    public static JetspeedLogger getSharedLogger(Class<?> clazz)
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
     * <EM>Note: A component which wants to use the shared <CODE>JetspeedLogger</CODE> should invoke this method
     * whenever it tries to leave logs. The retrieved logger instance should not be kept for later use.
     * Jetspeed container can be reloaded any time and it can make the old logger instances invalid.</EM>
     * </P>
     * <P>
     * If Jetspeed container is not available, then it returns a NOOP logger instead.
     * which does not do anything.
     * </P>
     * @param name
     * @return
     */
    public static JetspeedLogger getSharedLogger(String name)
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
    
    private static Object invokeDeclaredMethod(Class<?> targetClazz, String methodName, Class<?> [] argTypes, Object ... args)
    {
        try
        {
            Method method = targetClazz.getDeclaredMethod(methodName, argTypes);
            return method.invoke(targetClazz, args);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to invoke logging method. " + e);
        }
    }
    
    private static class DelegatingByReflectionJetspeedLogger implements JetspeedLogger, Serializable
    {
        private static final long serialVersionUID = 1L;
        
        private Class<?> targetClazz;
        private Object targetLogger;
        
        public DelegatingByReflectionJetspeedLogger(Object targetLogger)
        {
            this.targetLogger = targetLogger;
            this.targetClazz = targetLogger.getClass();
        }
        
        public boolean isDebugEnabled()
        {
            return ((Boolean) invokeLoggerMethod("isDebugEnabled", null)).booleanValue();
        }
        
        public boolean isInfoEnabled()
        {
            return ((Boolean) invokeLoggerMethod("isInfoEnabled", null)).booleanValue();
        }
        
        public boolean isWarnEnabled()
        {
            return ((Boolean) invokeLoggerMethod("isWarnEnabled", null)).booleanValue();
        }
        
        public boolean isErrorEnabled()
        {
            return ((Boolean) invokeLoggerMethod("isErrorEnabled", null)).booleanValue();
        }
        
        public void debug(String msg)
        {
            invokeLoggerMethod("debug", new Class [] { String.class }, msg);
        }

        public void debug(String format, Object arg)
        {
            invokeLoggerMethod("debug", new Class [] { String.class, Object.class }, format, arg);
        }

        public void debug(String format, Object arg1, Object arg2)
        {
            invokeLoggerMethod("debug", new Class [] { String.class, Object.class, Object.class }, format, arg1, arg2);
        }

        public void debug(String format, Object[] argArray)
        {
            invokeLoggerMethod("debug", new Class [] { String.class, Object [].class }, format, argArray);
        }

        public void debug(String msg, Throwable t)
        {
            invokeLoggerMethod("debug", new Class [] { String.class, Throwable.class }, msg, t);
        }

        public void info(String msg)
        {
            invokeLoggerMethod("info", new Class [] { String.class }, msg);
        }

        public void info(String format, Object arg)
        {
            invokeLoggerMethod("info", new Class [] { String.class, Object.class }, format, arg);
        }

        public void info(String format, Object arg1, Object arg2)
        {
            invokeLoggerMethod("info", new Class [] { String.class, Object.class, Object.class }, format, arg1, arg2);
        }

        public void info(String format, Object[] argArray)
        {
            invokeLoggerMethod("info", new Class [] { String.class, Object [].class }, format, argArray);
        }

        public void info(String msg, Throwable t)
        {
            invokeLoggerMethod("info", new Class [] { String.class, Throwable.class }, msg, t);
        }
        
        public void warn(String msg)
        {
            invokeLoggerMethod("warn", new Class [] { String.class }, msg);
        }

        public void warn(String format, Object arg)
        {
            invokeLoggerMethod("warn", new Class [] { String.class, Object.class }, format, arg);
        }

        public void warn(String format, Object arg1, Object arg2)
        {
            invokeLoggerMethod("warn", new Class [] { String.class, Object.class, Object.class }, format, arg1, arg2);
        }

        public void warn(String format, Object[] argArray)
        {
            invokeLoggerMethod("warn", new Class [] { String.class, Object [].class }, format, argArray);
        }

        public void warn(String msg, Throwable t)
        {
            invokeLoggerMethod("warn", new Class [] { String.class, Throwable.class }, msg, t);
        }
        
        public void error(String msg)
        {
            invokeLoggerMethod("error", new Class [] { String.class }, msg);
        }

        public void error(String format, Object arg)
        {
            invokeLoggerMethod("error", new Class [] { String.class, Object.class }, format, arg);
        }

        public void error(String format, Object arg1, Object arg2)
        {
            invokeLoggerMethod("error", new Class [] { String.class, Object.class, Object.class }, format, arg1, arg2);
        }

        public void error(String format, Object[] argArray)
        {
            invokeLoggerMethod("error", new Class [] { String.class, Object [].class }, format, argArray);
        }

        public void error(String msg, Throwable t)
        {
            invokeLoggerMethod("error", new Class [] { String.class, Throwable.class }, msg, t);
        }
        
        private Object invokeLoggerMethod(String methodName, Class<?> [] argTypes, Object ... args)
        {
            try
            {
                Method method = targetClazz.getMethod(methodName, argTypes);
                return method.invoke(targetLogger, args);
            }
            catch (Exception e)
            {
                throw new RuntimeException("Failed to invoke logger method, " + methodName + ", on " + targetLogger + ". " + e);
            }
        }
        
    };
    
    private static class NOOPJetspeedLogger implements JetspeedLogger, Serializable
    {
        private static final long serialVersionUID = 1L;
        
        public boolean isDebugEnabled()
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
        
        public boolean isErrorEnabled()
        {
            return false;
        }
        
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

        public void info(String msg)
        {
        }

        public void info(String format, Object arg)
        {
        }

        public void info(String format, Object arg1, Object arg2)
        {
        }

        public void info(String format, Object[] argArray)
        {
        }

        public void info(String msg, Throwable t)
        {
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

    };

}
