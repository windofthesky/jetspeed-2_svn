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
package org.apache.jetspeed.headerresource.impl;

import javax.portlet.PortletRequest;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.request.RequestContext;

/**
 * Default implementation for HeaderResourceFactory
 * 
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 * @version $Id: PortalReservedParameters.java 188569 2005-05-13 13:35:18Z weaver $
 */
public class HeaderResourceFactoryImpl implements HeaderResourceFactory
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.headerresource.impl.HeaderResourceFactory#getHeaderResouce(org.apache.jetspeed.request.RequestContext)
     */
    public HeaderResource getHeaderResouce(RequestContext requestContext)
    {
        return new HeaderResourceImpl(requestContext);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.headerresource.impl.HeaderResourceFactory#getHeaderResouce(javax.portlet.PortletRequest)
     */
    public HeaderResource getHeaderResouce(PortletRequest request)
    {
        RequestContext requestContext=(RequestContext)request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        return new HeaderResourceImpl(requestContext);
    }
}
