/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.io.Writer;

import javax.portlet.PortletException;

import org.apache.jetspeed.headerresource.HeaderResource;


public interface PortletHeaderResponse
{    
    /**
     * Retrieves the header resource for this request
     * @return a per request HeaderResource
     */
    HeaderResource getHeaderResource();
    
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
