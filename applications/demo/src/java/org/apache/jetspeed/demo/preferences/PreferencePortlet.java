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
package org.apache.jetspeed.demo.preferences;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

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

/**
 * <p>
 * PreferencePortlet
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PreferencePortlet extends GenericPortlet
{

    /**
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    protected void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        PortletContext context = getPortletContext();
        ResourceBundle resource = getPortletConfig().getResourceBundle(request.getLocale());

        request.setAttribute("viewMessage", resource.getString("preference.label.MyModeIsView"));

        PortletRequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/demo/preference/pref-view.jsp");
        rd.include(request, response);
    }

    /**
     * @see javax.portlet.GenericPortlet#init()
     */
    public void init(PortletConfig config) throws PortletException
    {
        System.out.println("Initializing Preference portlet example. ");
        super.init(config);
    }

    /**
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        ResourceBundle resource = getPortletConfig().getResourceBundle(request.getLocale());
        // Integer iCount = (Integer) request.getAttribute("org.apache.jetspeed.invocationCount");
        Integer iCount = (Integer) request.getPortletSession().getAttribute("org.apache.jetspeed.invocationCount");
        if (iCount == null)
        {
            iCount = new Integer(0);
        }

        int count = iCount.intValue();
        count++;

        response.setRenderParameter("invocationCount", String.valueOf(count));

        MessageFormat format = new MessageFormat(resource.getString("preference.label.processActionIWasInvoked"));
        String[] patterns = { Integer.toString(count)};
        response.setRenderParameter("invokeMessage", format.format(patterns));
        request.getPortletSession().setAttribute("org.apache.jetspeed.invocationCount", new Integer(count), PortletSession.PORTLET_SCOPE);
        System.out.println(resource.getString("preference.label.IWasInvoked"));
    }

}
