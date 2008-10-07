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
package org.apache.jetspeed.maven.utils;

import org.apache.jetspeed.tools.ToolsLogger;
import org.apache.maven.plugin.logging.Log;

/**
 * @version $Id$
 *
 */
public class MavenToolsLogger implements ToolsLogger
{
    Log mavenLogger;
    
    public MavenToolsLogger(Log mavenLogger)
    {
        this.mavenLogger = mavenLogger;
    }

    public void debug(CharSequence content, Throwable error)
    {
        mavenLogger.debug(content, error);
    }

    public void debug(CharSequence content)
    {
        mavenLogger.debug(content);
    }

    public void debug(Throwable error)
    {
        mavenLogger.debug(error);
    }

    public void error(CharSequence content, Throwable error)
    {
        mavenLogger.error(content, error);
    }

    public void error(CharSequence content)
    {
        mavenLogger.error(content);
    }

    public void error(Throwable error)
    {
        mavenLogger.error(error);
    }

    public void info(CharSequence content, Throwable error)
    {
        mavenLogger.info(content, error);
    }

    public void info(CharSequence content)
    {
        mavenLogger.info(content);
    }

    public void info(Throwable error)
    {
        mavenLogger.info(error);
    }

    public boolean isDebugEnabled()
    {
        return mavenLogger.isDebugEnabled();
    }

    public boolean isErrorEnabled()
    {
        return mavenLogger.isErrorEnabled();
    }

    public boolean isInfoEnabled()
    {
        return mavenLogger.isInfoEnabled();
    }

    public boolean isWarnEnabled()
    {
        return mavenLogger.isWarnEnabled();
    }

    public void warn(CharSequence content, Throwable error)
    {
        mavenLogger.warn(content, error);
    }

    public void warn(CharSequence content)
    {
        mavenLogger.warn(content);
    }

    public void warn(Throwable error)
    {
        mavenLogger.warn(error);
    }
}
