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
package org.apache.jetspeed.portlets.pam;

import java.io.IOException;
//import java.io.InputStream;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import org.apache.jetspeed.portlet.ServletPortlet;

/**
 * This portlet is a browser over all the portlet applications in the system.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:ccardona@gluecode.com">Chris Cardona</a>
 * @version $Id$
 */
public class PortletApplicationDetail extends ServletPortlet
{
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        PortletContext context = getPortletContext();
        response.setContentType("text/html");        
        PortletURL url = response.createRenderURL();
        PortletURL actionUrl = response.createActionURL();
        url.setWindowState(WindowState.MAXIMIZED);
        actionUrl.setPortletMode(PortletMode.EDIT);
        // url.addParameter("test", "value");
        response.getWriter().println("<br/><b>Init Param 'Template' = " + this.getInitParameter("template") +  "</b>");
        response.getWriter().println("<br/><b>Render URL = <a href='" + url +  "'>" + url + "</a></b>");
        response.getWriter().println("<br/><b>Action URL = <a href='" + actionUrl +  "'>" + actionUrl + "</a></b>");
        response.getWriter().println("<br/><b>Request dispatching now</b>");        
        super.doView(request, response);
     }
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
	{
		System.out.println("PorletApplicationDetail: processAction()");
	}
}