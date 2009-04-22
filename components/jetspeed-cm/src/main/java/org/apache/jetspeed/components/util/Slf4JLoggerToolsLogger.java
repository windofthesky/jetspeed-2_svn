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
package org.apache.jetspeed.components.util;

import java.text.MessageFormat;

import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.apache.jetspeed.tools.ToolsLogger;

/**
 * SLF4J Logger wrapper of a ToolsLogger allowing plugging in other logging implementations
 * like Maven Logger logger to be used as a SLF4J logger
 * 
 * @version $Id$
 *
 */
public class Slf4JLoggerToolsLogger extends MarkerIgnoringBase
{
    private static final long serialVersionUID = 3184831685009180480L;
    
    ToolsLogger toolsLogger;
    
    public Slf4JLoggerToolsLogger(ToolsLogger toolsLogger)
    {
        this.toolsLogger = toolsLogger;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#debug(java.lang.Object)
     */
    public void debug(Object message)
    {
        toolsLogger.debug(message.toString());
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#debug(java.lang.Object, java.lang.Throwable)
     */
    public void debug(Object message, Throwable t)
    {
        toolsLogger.debug(message.toString(), t);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#error(java.lang.Object)
     */
    public void error(Object message)
    {
        toolsLogger.error(message.toString());
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#error(java.lang.Object, java.lang.Throwable)
     */
    public void error(Object message, Throwable t)
    {
        toolsLogger.error(message.toString(), t);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object)
     */
    public void fatal(Object message)
    {
        toolsLogger.error(message.toString());
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#fatal(java.lang.Object, java.lang.Throwable)
     */
    public void fatal(Object message, Throwable t)
    {
        toolsLogger.error(message.toString(),t);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#info(java.lang.Object)
     */
    public void info(Object message)
    {
        toolsLogger.info(message.toString());
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#info(java.lang.Object, java.lang.Throwable)
     */
    public void info(Object message, Throwable t)
    {
        toolsLogger.info(message.toString(),t);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#isDebugEnabled()
     */
    public boolean isDebugEnabled()
    {
        return toolsLogger.isDebugEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#isErrorEnabled()
     */
    public boolean isErrorEnabled()
    {
        return toolsLogger.isErrorEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#isFatalEnabled()
     */
    public boolean isFatalEnabled()
    {
        return toolsLogger.isErrorEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#isInfoEnabled()
     */
    public boolean isInfoEnabled()
    {
        return toolsLogger.isInfoEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#isTraceEnabled()
     */
    public boolean isTraceEnabled()
    {
        return toolsLogger.isDebugEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#isWarnEnabled()
     */
    public boolean isWarnEnabled()
    {
        return toolsLogger.isWarnEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#trace(java.lang.Object)
     */
    public void trace(Object message)
    {
        toolsLogger.debug(message.toString());
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#trace(java.lang.Object, java.lang.Throwable)
     */
    public void trace(Object message, Throwable t)
    {
        toolsLogger.debug(message.toString(),t);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#warn(java.lang.Object)
     */
    public void warn(Object message)
    {
        toolsLogger.warn(message.toString());
    }

    /* (non-Javadoc)
     * @see org.apache.commons.logging.Log#warn(java.lang.Object, java.lang.Throwable)
     */
    public void warn(Object message, Throwable t)
    {
        toolsLogger.warn(message.toString(),t);
    }

    public void debug(String arg0)
    {
        toolsLogger.debug(arg0);
    }

    public void debug(String arg0, Object arg1)
    {
        toolsLogger.debug(MessageFormatter.format(arg0, arg1));
    }

    public void debug(String arg0, Object[] arg1)
    {
        toolsLogger.debug(MessageFormatter.format(arg0, arg1));
    }

    public void debug(String arg0, Throwable arg1)
    {
        toolsLogger.debug(arg0, arg1);
    }

    public void debug(String arg0, Object arg1, Object arg2)
    {
        toolsLogger.debug(MessageFormatter.format(arg0, arg1, arg2));
    }
    
    public void error(String arg0)
    {
        toolsLogger.error(arg0);
    }

    public void error(String arg0, Object arg1)
    {
        toolsLogger.error(MessageFormatter.format(arg0, arg1));
    }

    public void error(String arg0, Object[] arg1)
    {
        toolsLogger.error(MessageFormatter.format(arg0, arg1));
    }

    public void error(String arg0, Throwable arg1)
    {
        toolsLogger.error(arg0, arg1);
    }

    public void error(String arg0, Object arg1, Object arg2)
    {
        toolsLogger.error(MessageFormatter.format(arg0, arg1, arg2));
    }

    public String getName()
    {
        return toolsLogger.toString();
    }

    public void info(String arg0)
    {
        toolsLogger.info(arg0);
    }

    public void info(String arg0, Object arg1)
    {
        toolsLogger.info(MessageFormatter.format(arg0, arg1));
    }

    public void info(String arg0, Object[] arg1)
    {
        toolsLogger.info(MessageFormatter.format(arg0, arg1));
    }

    public void info(String arg0, Throwable arg1)
    {
        toolsLogger.info(arg0, arg1);
    }
    
    public void info(String arg0, Object arg1, Object arg2)
    {
        toolsLogger.info(MessageFormatter.format(arg0, arg1, arg2));
    }

    public void trace(String arg0)
    {
        toolsLogger.debug(arg0);
    }

    public void trace(String arg0, Object arg1)
    {
        toolsLogger.debug(MessageFormat.format(arg0, arg1));
    }

    public void trace(String arg0, Object[] arg1)
    {
        toolsLogger.debug(MessageFormat.format(arg0, arg1));
    }

    public void trace(String arg0, Throwable arg1)
    {
        toolsLogger.debug(arg0, arg1);
    }

    public void trace(String arg0, Object arg1, Object arg2)
    {
        toolsLogger.debug(MessageFormat.format(arg0, arg1, arg2));
    }

    public void warn(String arg0)
    {
        toolsLogger.warn(arg0);
    }

    public void warn(String arg0, Object arg1)
    {
        toolsLogger.warn(MessageFormatter.format(arg0, arg1));
    }

    public void warn(String arg0, Object[] arg1)
    {
        toolsLogger.warn(MessageFormatter.format(arg0, arg1));
    }

    public void warn(String arg0, Throwable arg1)
    {
        toolsLogger.warn(arg0, arg1);
    }

    public void warn(String arg0, Object arg1, Object arg2)
    {
        toolsLogger.warn(MessageFormatter.format(arg0, arg1));
    }
}
