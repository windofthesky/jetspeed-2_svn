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

import org.apache.commons.logging.Log;
import org.apache.jetspeed.tools.ToolsLogger;

/**
 * Commons Log wrapper of a ToolsLogger allowing plugging in other logging implementations
 * like Maven Log logger to be used as Commons Log logger
 * 
 * @version $Id$
 *
 */
public class CommonsToolsLogger implements Log
{
    ToolsLogger toolsLogger;
    
    public CommonsToolsLogger(ToolsLogger toolsLogger)
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
}
