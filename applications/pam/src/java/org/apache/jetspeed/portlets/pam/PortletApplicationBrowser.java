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
import javax.portlet.PortletURL;
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
        
        PortletRegistryComponent registry = (PortletRegistryComponent)
                context.getAttribute("cps:PortletRegistryComponent");
        if (registry != null)
        {
            List apps = registry.getPortletApplications();
            /*
            Iterator it = apps.iterator();
            while (it.hasNext())
            {
                MutablePortletApplication pa = (MutablePortletApplication)it.next();
                System.out.println("PA = " + pa.getName());                
            }
            */
            
            TreeControl control = (TreeControl) request.getPortletSession().getAttribute("j2_tree");
            if(control == null)
            {
                PortletURL actionURL = response.createActionURL();
            	control = buildTree(apps, actionURL);
            	request.getPortletSession().setAttribute("j2_tree", control);
            }
            request.setAttribute("j2_tree", control);
            request.setAttribute("apps", apps);            
        }        
        super.doView(request, response);
        
    }

    
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
	{
		System.out.println("PorletApplicationBrowser: processAction()");
		
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
		
		String selectedNode = actionRequest.getParameter("select_node");
		if(selectedNode != null)
		{
		    control.selectNode(selectedNode);
		    //TODO:  signal details portlet that node was selected
		    
		    System.out.println("Node Selected: " + selectedNode);
		}	
	}
	
	private TreeControl buildTree(List apps, PortletURL actionURL) {
	    
	    
	    actionURL.setParameter("select_node", "ROOT-NODE");
		TreeControlNode root =
            new TreeControlNode("ROOT-NODE",
                                null, "J2_ROOT",
                                actionURL.toString(),
                                null, true, "J2_DOMAIN");
		
		TreeControl control = new TreeControl(root);
		
		
		actionURL.setParameter("select_node", "APP_ROOT");
		TreeControlNode portletApps = 
			new TreeControlNode("APP-NODE", null, "APP_ROOT", actionURL.toString(), null, false, "J2_DOMAIN");
		root.addChild(portletApps);
		
		Iterator it = apps.iterator();
        while (it.hasNext())
        {
            MutablePortletApplication pa = (MutablePortletApplication)it.next();
            actionURL.setParameter("select_node", pa.getName());
            TreeControlNode appNode = new TreeControlNode(pa.getName(), null, pa.getName(), actionURL.toString(), null, false, "PA_APP_DOMAIN"  );
            portletApps.addChild(appNode);
        }
		
		return control;
	}
}