/*
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
package org.apache.jetspeed.aggregator;

import java.io.PrintWriter;

import org.apache.jetspeed.cache.ContentCacheKey;

/**
 * <p>
 * PortletContent
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <a href="mailto:taylor@apache.org">David S. Taylor</a>  
 * @version $Id$
 *
 */
public interface PortletContent
{
    /**
     * Retrieve the actual content of a portlet as a string
     * 
     * @return
     */
    String getContent();
    
    /** 
     * Has the renderer completed rendering the content?
     * 
     * @return
     */
    boolean isComplete();
    
    /**
     * Notify that this content is completed.
     *
     */
    void complete();
    
    /**
     * Notify that this content is complete with error
     *
     */
    void completeWithError();
    
    /**
     * Get a writer to the content to stream content into this object
     * @return
     */
    PrintWriter getWriter();
    
    /**
     * Get the expiration setting for this content if it is cached.
     * @return
     */
    int getExpiration();
    void setExpiration(int expiration);
    
    /**
     * Get the cache key used to cache this content
     * @since 2.1.2 
     * @return
     */
    ContentCacheKey getCacheKey();
    
    /**
     * Get the title of the portlet, used during caching
     * 
     * @return
     */
    String getTitle();
    
    /**
     * Set the title of this portlet, used during caching
     * @param title
     */
    void setTitle(String title);
    
    String getContentType();
    void setContentType(String contentType);
    
    void reset();
    void resetBuffer();
    
    /**
     * Release the buffers used by the portlet content cache. Note the actual release may not occur until garbage collection. 
     *
     */
    void release();                    
}