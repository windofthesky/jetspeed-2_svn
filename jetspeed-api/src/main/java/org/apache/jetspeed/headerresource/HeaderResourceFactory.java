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
package org.apache.jetspeed.headerresource;

import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.request.RequestContext;

import javax.portlet.PortletRequest;
import java.util.Map;

/**
 * HeaderResourceFactory provides HeaderResource instance to manage tags, such
 * as &lt;link&gt; and &lt;script&gt;, in &lt;head&gt; tag.
 * 
 * HeaderResourceFactory is defined by jetspeed-spring.xml. If you want to use
 * customized HeaderResource, the parameter needs to be changed in
 * jetspeed-spring.xml.
 * 
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 * @version $Id: PortalReservedParameters.java 188569 2005-05-13 13:35:18Z weaver $
 */
public interface HeaderResourceFactory
{

    /**
     * Provides HeaderResource instance from RequestContext.
     * 
     * @param requestContext
     * @return
     */
    public abstract HeaderResource getHeaderResouce(RequestContext requestContext);
    
    /**
     * Provides HeaderResource instance from RequestContext.
     * 
     * @param requestContext
     * @return
     */
    public abstract HeaderResource getHeaderResource( RequestContext requestContext, BasePortalURL baseUrlAccess,
                                                      boolean isDesktop, Map<String, Object> headerConfiguration );

    /**
     * Provides HeaderResource instance from PortletRequest.
     * 
     * @param request
     * @return
     */
    public abstract HeaderResource getHeaderResouce(PortletRequest request);

}