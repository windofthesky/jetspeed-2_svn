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
package org.apache.jetspeed.portlets.localeselector;

import java.io.IOException;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.request.RequestContext;

/**
 * 
 * 
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 */
public class LocaleSelectorPortlet extends GenericPortlet
{
    public static final String PREFERED_LOCALE_SESSION_KEY = "org.apache.jetspeed.prefered.locale";

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
    }

    /* (non-Javadoc)
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        PortletContext context = getPortletContext();

        Locale locale = request.getLocale();
        if (locale == null)
        {
            locale = Locale.getDefault();
        }
        request.setAttribute("currentLocale", locale.toString());

        PortletRequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/view/locale-list.jsp");
        rd.include(request, response);
    }

    /* (non-Javadoc)
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {

        PortletSession session = request.getPortletSession();
        String language = request.getParameter(PREFERED_LOCALE_SESSION_KEY);

        if (language != null)
        {
            String country = "";
            String variant = "";
            int countryIndex = language.indexOf('_');
            if (countryIndex > -1)
            {
                country = language.substring(countryIndex + 1).trim();
                language = language.substring(0, countryIndex).trim();
                int vDash = country.indexOf("_");
                if (vDash > 0)
                {
                    String cTemp = country.substring(0, vDash);
                    variant = country.substring(vDash + 1);
                    country = cTemp;
                }
            }

            // TODO Set the prefered locale to user's persistent storage if not anon user
            Locale preferedLocale = new Locale(language, country, variant);
            session.setAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE, preferedLocale,PortletSession.APPLICATION_SCOPE);
            RequestContext requestContext = (RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
            requestContext.setLocale(preferedLocale);
            requestContext.setSessionAttribute(PortalReservedParameters.PREFERED_LOCALE_ATTRIBUTE, preferedLocale);
        }

        return;
    }

}
