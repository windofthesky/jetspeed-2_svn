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
import java.util.Iterator;
import java.util.List;
//import java.io.InputStream;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.portlet.ServletPortlet;
import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlNode;

//import org.apache.jetspeed.cps.util.Streams;

/**
 * This portlet is a browser over all the portlet applications in the system.
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletApplicationBrowser extends ServletPortlet
{
    private static final String PORTLET_URL = "portlet_url";
    private String template;
    private PortletContext context;
    private PortletRegistryComponent registry;
    
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
    }
    
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        response.setContentType("text/html");
        
        List apps = registry.getPortletApplications();
        TreeControl control = (TreeControl) request.getPortletSession().getAttribute("j2_tree");
        if(control == null)
        {
        	control = buildTree(apps);
        	request.getPortletSession().setAttribute("j2_tree", control);
        }
        request.setAttribute("j2_tree", control);
        
        super.doView(request, response);
        
    }

    
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
	{
		TreeControl control = (TreeControl) actionRequest.getPortletSession().getAttribute("j2_tree");
		//assert control != null
		
		String node = actionRequest.getParameter("node");
		if(node != null)
		{
		    TreeControlNode controlNode = control.findNode(node);
		    if(controlNode != null)
		    {
		        controlNode.setExpanded(!controlNode.isExpanded());
		    }
		}
		
		String selectedNode = actionRequest.getParameter(PortletApplicationResources.REQUEST_SELECT_NODE);
		if(selectedNode != null)
		{
		    control.selectNode(selectedNode);
            MutablePortletApplication pa = registry.getPortletApplicationByIdentifier(selectedNode);
            if (null != pa)
            {
                actionRequest.getPortletSession().setAttribute(PortletApplicationResources.PAM_CURRENT_PA, pa, PortletSession.APPLICATION_SCOPE);
            }
		}	
	}
	
	private TreeControl buildTree(List apps) {
	    
		TreeControlNode root =
            new TreeControlNode("ROOT-NODE",
                                null, "J2_ROOT",
                                PORTLET_URL,
                                null, true, "J2_DOMAIN");
		
		TreeControl control = new TreeControl(root);
		
		
		TreeControlNode portletApps = 
			new TreeControlNode("APP_ROOT", null, "APP_ROOT", PORTLET_URL, null, false, "J2_DOMAIN");
		root.addChild(portletApps);
		
		Iterator it = apps.iterator();
        while (it.hasNext())
        {
            MutablePortletApplication pa = (MutablePortletApplication)it.next();
            TreeControlNode appNode = new TreeControlNode(pa.getName(), null, pa.getName(), PORTLET_URL, null, false, "PA_APP_DOMAIN"  );
            portletApps.addChild(appNode);
        }
		
		return control;
	}
}