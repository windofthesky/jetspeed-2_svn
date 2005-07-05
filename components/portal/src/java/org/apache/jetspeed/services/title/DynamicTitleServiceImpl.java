/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.services.title;

import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.title.DynamicTitleService;


public class DynamicTitleServiceImpl implements DynamicTitleService
{

    public void setDynamicTitle(PortletWindow window,
            HttpServletRequest request, String titleArg)
    {
        ObjectID id = window.getPortletEntity().getId();        
        request.setAttribute(
                PortalReservedParameters.OVERRIDE_PORTLET_TITLE_ATTR
                        + "::entity.id::" + id.toString(), titleArg);
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
            title = entity.getPortletDefinition().getName();
        }
        else if (title == null)
        {
            title = "Invalid portlet entity " + entity.getId();
        }
        
        return title;
    }

    protected final String getTitleFromPreference(PortletWindow window,
            HttpServletRequest request)
    {
        Locale locale = request.getLocale();
        String titleKey = createTitleKey(locale, false);

        Preference titlePref = window.getPortletEntity().getPreferenceSet()
                .get(titleKey);
        if (titlePref == null)
        {
            titleKey = createTitleKey(locale, true);
            titlePref = window.getPortletEntity().getPreferenceSet().get(
                    titleKey);
        }

        if (titlePref != null)
        {
            Iterator values = titlePref.getValues();
            if (values.hasNext())
            {
                return (String) titlePref.getValues().next();
            }
        }

        return null;
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
