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
package org.apache.jetspeed.aggregator.impl;

import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.aggregator.ContentDispatcherCtrl;
import org.apache.jetspeed.aggregator.PortletContent;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.container.PortletWindow;

/**
 * <p>
 * The ContentDispatcher allows customer classes to retrieved rendered content
 * for a specific fragment
 * </p>
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @version $Id$
 */
public class ContentDispatcherImpl implements ContentDispatcherCtrl
{
    private PortletContent content = null;

    private ContentDispatcherImpl() 
    {}
    
    public ContentDispatcherImpl(PortletContent content)
    {
        this.content = content;
    }

    public HttpServletResponse getResponseForWindow( PortletWindow window, RequestContext request )
    {
        return new HttpBufferedResponse(request.getResponse(), this.content.getWriter());
    }
    
    public HttpServletResponse getResponseForFragment( Fragment fragment, RequestContext request )
    {
        return new HttpBufferedResponse(request.getResponse(), this.content.getWriter());
    }

    /**
     * <p>
     * getPortletContent
     * </p>
     *
     * @see org.apache.jetspeed.aggregator.ContentDispatcher#getPortletContent(org.apache.jetspeed.om.page.Fragment)
     * @param fragment
     * @return
     */
    public PortletContent getPortletContent( Fragment fragment )
    {       
        return this.content;
    }
}
