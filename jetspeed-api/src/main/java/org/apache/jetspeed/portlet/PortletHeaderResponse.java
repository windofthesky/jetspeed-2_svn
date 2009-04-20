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
package org.apache.jetspeed.portlet;

import java.util.Map;

import javax.portlet.PortletException;

import org.apache.jetspeed.headerresource.HeaderResource;

/**
 * This interface is deprecated because head contribution from portlet is possible
 * by the native portlet API in a JSR-286 compliant portal.
 * 
 * @version $Id$
 * @deprecated
 */
public interface PortletHeaderResponse
{    
    /**
     * Retrieves the header resource for this request
     * 
     * @return a per request HeaderResource
     */
    HeaderResource getHeaderResource();
    
    /**
     * Is request for /desktop rather than /portal
     * 
     * @return true if request is for /desktop, false if request is for /portal
     */
    boolean isDesktop();
    
    
    /**
     * Configuration data for use by HeaderResource
     * 
     * @return an immutable Map
     */
    Map getHeaderConfiguration();
    
    /**
     * Map containing overrides of content for header statements
     * 
     * @return an immutable Map
     */
    Map getHeaderResourceRegistry();
    
    /**
     * Temporary solution: get the content after calling include
     * this will be deprecated
     * 
     */
    String getContent();

    
    /**
     * Dispatch to a servlet or resource to generate and include the header content
     * 
     * @param request
     * @param response
     * @param headerResource
     * @return
     * @throws PortletException
     */
    void include(PortletHeaderRequest request, PortletHeaderResponse response, String headerResource) 
        throws PortletException;
}
