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
import java.util.ArrayList;
import java.util.HashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.apache.jetspeed.portlet.ServletPortlet;
import org.apache.jetspeed.portlets.pam.beans.PortletApplicationBean;
import org.apache.jetspeed.portlets.pam.beans.TabBean;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.pluto.om.portlet.PortletDefinition;
/**
 * This portlet is a browser over all the portlet applications in the system.
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletApplicationDetail extends ServletPortlet
{
    private final String VIEW_PA = "portletApplication"; 
    private final String VIEW_PD = "portletDefinition";

    private PortletContext context;
    private PortletRegistryComponent registry;
    private HashMap tabMap = new HashMap();
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        context = getPortletContext();
        registry = (PortletRegistryComponent)context.getAttribute(PortletApplicationResources.CPS_REGISTRY_COMPONENT);
        if (null == registry)
        {
            throw new PortletException("Failed to find the Portlet Registry on portlet initialization");
        }
        
        TabBean tb1 = new TabBean("Details", "Details");
        TabBean tb2 = new TabBean("Metadata", "Metadata");
        TabBean tb3 = new TabBean("Portlets", "Portlets");
        
        tabMap.put(tb1.getId(), tb1);
        tabMap.put(tb2.getId(), tb2);
        tabMap.put(tb3.getId(), tb3);
    }
    
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        response.setContentType("text/html");
        
        MutablePortletApplication pa = (MutablePortletApplication)
                request.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_PA, 
                                                         PortletSession.APPLICATION_SCOPE);
        
        if (null != pa)
        {
            request.setAttribute(VIEW_PA, new PortletApplicationBean(pa));
            
            PortletDefinition pdef = (PortletDefinition) request.getPortletSession().getAttribute("select_portlet");
            
            request.setAttribute(VIEW_PD, pdef);
            
            request.setAttribute("tabs", tabMap.values());
            
            TabBean selectedTab = (TabBean) request.getPortletSession().getAttribute("selected_tab");
            if(selectedTab == null) {
                selectedTab = (TabBean) tabMap.values().iterator().next();
            }
            request.setAttribute("selected_tab", selectedTab);
            
        }
        super.doView(request, response);
    }
    
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
	{
        System.out.println("PorletApplicationDetail: processAction()");
        
        String selectedPortlet = actionRequest.getParameter("select_portlet");
        if(selectedPortlet != null)
        {
	        MutablePortletApplication pa = (MutablePortletApplication)
	        	actionRequest.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_PA, 
	                                                 PortletSession.APPLICATION_SCOPE);
	        
	        PortletDefinition pdef = pa.getPortletDefinitionByName(selectedPortlet);
	        actionRequest.getPortletSession().setAttribute("select_portlet", pdef);
        }
        
        String selectedTab = actionRequest.getParameter("selected_tab");
        if(selectedTab != null)
        {
            TabBean tab = (TabBean) tabMap.get(selectedTab);
            actionRequest.getPortletSession().setAttribute("selected_tab", tab);
        }
	}
}