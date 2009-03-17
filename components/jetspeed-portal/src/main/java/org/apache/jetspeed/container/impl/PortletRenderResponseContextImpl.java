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

package org.apache.jetspeed.container.impl;

import java.util.Collection;

import javax.portlet.PortletMode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletRenderResponseContext;
import org.apache.jetspeed.container.PortletWindow;

/**
 * @version $Id$
 *
 */
public class PortletRenderResponseContextImpl extends PortletMimeResponseContextImpl implements
                PortletRenderResponseContext
{
    public PortletRenderResponseContextImpl(PortletContainer container, HttpServletRequest containerRequest,
                                            HttpServletResponse containerResponse, PortletWindow window)
    {
        super(container, containerRequest, containerResponse, window);
    }

    public void setNextPossiblePortletModes(Collection<PortletMode> portletModes)
    {
        //TODO
    }

    public void setTitle(String title)
    {
        if (!isClosed())
        {
            //TODO

            // TODO: 2.2 jetspeed uses a title service        
//            String title = null;
//            if (titleArg == null || titleArg.length() == 0)
//            {
//                title = getTitleFromPortletDefinition(portletWindow, request);
//            }
//            else
//            {
//                title = titleArg;
//            }
//            request.setAttribute(
//                    PortalReservedParameters.OVERRIDE_PORTLET_TITLE_ATTR
//                            + "::window.id::" + portletWindow.getId(), title);        

        }
    }
    
//    protected final String getTitleFromPortletDefinition(org.apache.pluto.PortletWindow window, HttpServletRequest request)
//    {
//        String title = null;
//        RequestContext requestContext = (RequestContext) request
//                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
//        
//        org.apache.jetspeed.container.PortletWindow  jsWindow = (org.apache.jetspeed.container.PortletWindow)window;
//        PortletEntity entity = jsWindow.getPortletEntity();
//        if (entity != null && entity.getPortletDefinition() != null)
//        {
//            title = requestContext.getPreferedLanguage(
//                    entity.getPortletDefinition()).getTitle();
//        }
//
//        if (title == null && entity.getPortletDefinition() != null)
//        {
//            title = entity.getPortletDefinition().getPortletName();
//        }
//        else if (title == null)
//        {
//            title = "Invalid portlet entity " + entity.getId();
//        }
//        
//        return title;
//    }
    
}
