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
package org.apache.jetspeed.services.title;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.PortletEntity;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.request.RequestContext;

public class DynamicTitleServiceImpl implements DynamicTitleService
{
    public void setDynamicTitle(PortletWindow window,
            HttpServletRequest request, String titleArg)
    {
        //String title = getTitleFromPreference(window, request);
        String title = null;
//        if (title == null || title.length() < 0)
//        {
            if (titleArg == null || titleArg.length() == 0)
            {
                title = getTitleFromPortletDefinition(window, request);
            }
            else
            {
                title = titleArg;
            }

//        }
        request.setAttribute(
                PortalReservedParameters.OVERRIDE_PORTLET_TITLE_ATTR
                        + "::window.id::" + window.getId(), title);
    }
    
    public String getDynamicTitle(PortletWindow window,
            HttpServletRequest request)
    {
        return (String)request.getAttribute(PortalReservedParameters.OVERRIDE_PORTLET_TITLE_ATTR
                        + "::window.id::" + window.getId());
    }

    protected final String getTitleFromPortletDefinition(PortletWindow window,
            HttpServletRequest request)
    {
        String title = null;
        RequestContext requestContext = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        
        PortletEntity entity = window.getPortletEntity();
        if (entity != null && entity.getPortletDefinition() != null)
        {
            title = requestContext.getPreferedLanguage(
                    entity.getPortletDefinition()).getTitle();
        }

        if (title == null && entity.getPortletDefinition() != null)
        {
            title = entity.getPortletDefinition().getPortletName();
        }
        else if (title == null)
        {
            title = "Invalid portlet entity " + entity.getId();
        }
        
        return title;
    }

    public static String createTitleKey(Locale locale, boolean languageOnly)
    {
        if(languageOnly)
        {
            return "jetspeed.title."+locale.getLanguage();
        }
        else
        {
            return "jetspeed.title."+locale.toString();
        }
    }

}
