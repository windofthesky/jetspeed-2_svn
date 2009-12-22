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
package org.apache.jetspeed.logger;

/**
 * JetspeedLoggerFactory 
 * <P>
 * Wrapper interface to a logger factory instance. By using this interface, 
 * common components can leave logs in the Jetspeed's logging context.
 * </P>
 * 
 * @version $Id$
 */
public interface JetspeedLoggerFactory
{
    
    /**
     * Returns a JetspeedLogger.
     * <P>
     * <EM>Note: A component which wants to use <CODE>JetspeedLogger</CODE> must invoke this method
     * whenever it tries to leave logs. The retrieved logger instance must not be kept for later use.
     * Jetspeed container can be reloaded any time and it can make the old logger instances invalid.</EM>
     * </P>
     * @param clazz
     * @return
     */
    public JetspeedLogger getLogger(Class<?> clazz);
    
    /**
     * Returns a JetspeedLogger.
     * <P>
     * <EM>Note: A component which wants to use <CODE>JetspeedLogger</CODE> must invoke this method
     * whenever it tries to leave logs. The retrieved logger instance must not be kept for later use.
     * Jetspeed container can be reloaded any time and it can make the old logger instances invalid.</EM>
     * </P>
     * @param name
     * @return
     */
    public JetspeedLogger getLogger(String name);
    
}
