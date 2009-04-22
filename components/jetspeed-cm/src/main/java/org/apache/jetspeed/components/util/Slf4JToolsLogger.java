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

import org.slf4j.Logger;
import org.apache.jetspeed.tools.ToolsLogger;

/**
 * ToolsLogger wrapper of a SLF4J Logger
 * 
 * @version $Id$
 *
 */
public class Slf4JToolsLogger implements ToolsLogger
{
    private Logger log;
    
    public Slf4JToolsLogger(Logger log)
    {
        this.log = log;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#debug(java.lang.CharSequence)
     */
    public void debug(CharSequence content)
    {
        log.debug(content.toString());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#debug(java.lang.CharSequence, java.lang.Throwable)
     */
    public void debug(CharSequence content, Throwable error)
    {
        log.debug(content.toString(), error);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#debug(java.lang.Throwable)
     */
    public void debug(Throwable error)
    {
        log.debug(error.getMessage(),error);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#error(java.lang.CharSequence)
     */
    public void error(CharSequence content)
    {
        log.error(content.toString());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#error(java.lang.CharSequence, java.lang.Throwable)
     */
    public void error(CharSequence content, Throwable error)
    {
        log.error(content.toString(),error);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#error(java.lang.Throwable)
     */
    public void error(Throwable error)
    {
        log.error(error.getMessage(),error);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#info(java.lang.CharSequence)
     */
    public void info(CharSequence content)
    {
        log.info(content.toString());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#info(java.lang.CharSequence, java.lang.Throwable)
     */
    public void info(CharSequence content, Throwable error)
    {
        log.info(content.toString(),error);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#info(java.lang.Throwable)
     */
    public void info(Throwable error)
    {
        log.info(error.getMessage(),error);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#isDebugEnabled()
     */
    public boolean isDebugEnabled()
    {
        return log.isDebugEnabled() || log.isTraceEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#isErrorEnabled()
     */
    public boolean isErrorEnabled()
    {
        return log.isErrorEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#isInfoEnabled()
     */
    public boolean isInfoEnabled()
    {
        return log.isInfoEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#isWarnEnabled()
     */
    public boolean isWarnEnabled()
    {
        return log.isWarnEnabled();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#warn(java.lang.CharSequence)
     */
    public void warn(CharSequence content)
    {
        log.warn(content.toString());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#warn(java.lang.CharSequence, java.lang.Throwable)
     */
    public void warn(CharSequence content, Throwable error)
    {
        log.warn(content.toString(),error);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.tools.ToolsLogger#warn(java.lang.Throwable)
     */
    public void warn(Throwable error)
    {
        log.warn(error.getMessage(),error);
    }
}
