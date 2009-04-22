/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.tools;

/**
 * ToolsLogger is a lightweight "typical" logger interface allowing wrapping a Maven Log to be used by SLF4J Logger
 * while not depending on not requiring either of those logging api's to be required from a shared classpath.
 * 
 * @version $Id$
 *
 */
public interface ToolsLogger
{
    /**
     * @return true if the <b>debug</b> error level is enabled
     */
    boolean isDebugEnabled();

    /**
     * Send a message to the user in the <b>debug</b> error level.
     *
     * @param content
     */
    void debug( CharSequence content );

    /**
     * Send a message (and accompanying exception) to the user in the <b>debug</b> error level.
     * <br/>
     * The error's stacktrace will be output when this error level is enabled.
     *
     * @param content
     * @param error
     */
    void debug( CharSequence content, Throwable error );

    /**
     * Send an exception to the user in the <b>debug</b> error level.
     * <br/>
     * The stack trace for this exception will be output when this error level is enabled.
     *
     * @param error
     */
    void debug( Throwable error );

    /**
     * @return true if the <b>info</b> error level is enabled
     */
    boolean isInfoEnabled();

    /**
     * Send a message to the user in the <b>info</b> error level.
     *
     * @param content
     */
    void info( CharSequence content );

    /**
     * Send a message (and accompanying exception) to the user in the <b>info</b> error level.
     * <br/>
     * The error's stacktrace will be output when this error level is enabled.
     *
     * @param content
     * @param error
     */
    void info( CharSequence content, Throwable error );

    /**
     * Send an exception to the user in the <b>info</b> error level.
     * <br/>
     * The stack trace for this exception will be output when this error level is enabled.
     *
     * @param error
     */
    void info( Throwable error );

    /**
     * @return true if the <b>warn</b> error level is enabled
     */
    boolean isWarnEnabled();

    /**
     * Send a message to the user in the <b>warn</b> error level.
     *
     * @param content
     */
    void warn( CharSequence content );

    /**
     * Send a message (and accompanying exception) to the user in the <b>warn</b> error level.
     * <br/>
     * The error's stacktrace will be output when this error level is enabled.
     *
     * @param content
     * @param error
     */
    void warn( CharSequence content, Throwable error );

    /**
     * Send an exception to the user in the <b>warn</b> error level.
     * <br/>
     * The stack trace for this exception will be output when this error level is enabled.
     *
     * @param error
     */
    void warn( Throwable error );

    /**
     * @return true if the <b>error</b> error level is enabled
     */
    boolean isErrorEnabled();

    /**
     * Send a message to the user in the <b>error</b> error level.
     *
     * @param content
     */
    void error( CharSequence content );

    /**
     * Send a message (and accompanying exception) to the user in the <b>error</b> error level.
     * <br/>
     * The error's stacktrace will be output when this error level is enabled.
     *
     * @param content
     * @param error
     */
    void error( CharSequence content, Throwable error );

    /**
     * Send an exception to the user in the <b>error</b> error level.
     * <br/>
     * The stack trace for this exception will be output when this error level is enabled.
     *
     * @param error
     */
    void error( Throwable error );
}
