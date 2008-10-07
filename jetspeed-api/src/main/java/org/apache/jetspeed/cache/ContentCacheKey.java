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
package org.apache.jetspeed.cache;

import java.io.Serializable;


/**
 * <p>
 *  Provides interface to all Content Caches (Portlet API cache)
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface ContentCacheKey extends Serializable
{
    /**
     * Get the username or null if not used
     * @return
     */
    String getUsername();
    
    /**
     * Get the pipeline name or null if not used
     * @return
     */
    String getPipeline();
    
    /**
     * Get the window (portlet fragment) id
     * @return
     */
    String getWindowId();
    
    /**
     * Get the session id or null if not used
     * 
     * @return
     */
    String getSessionId();
    
    /**
     * 
     * @return
     */
    String getRequestParameter();
    
    /**
     * 
     * @return
     */
    String getSessionAttribute();
    
    /**
     * Return the full key as a string
     * @return
     */
    String getKey();
    
    void createFromUser(String username, String pipeline, String windowId);
    
    void createFromSession(String sessionid, String pipeline, String windowId);
}