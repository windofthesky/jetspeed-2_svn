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
import java.util.List;
//import java.io.InputStream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.portlet.ServletPortlet;

//import org.apache.jetspeed.cps.util.Streams;

/**
 * This portlet is a browser over all the portlet applications in the system.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:ccardona@gluecode.com">Chris Cardona</a>
 * 
 * @version $Id$
 */
public class PortletApplicationBrowser extends ServletPortlet
{
    private String template;
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
    }
    
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        PortletContext context = getPortletContext();
        response.setContentType("text/html");
        PortletRegistryComponent registry = (PortletRegistryComponent)Jetspeed.getComponentManager().getComponent(PortletRegistryComponent.class);  
        List apps = registry.getPortletApplications();
        request.setAttribute("apps", apps);
        super.doView(request, response);
        
    }

    
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
	{
		System.out.println("PorletApplicationBrowser: processAction()");
	}
}