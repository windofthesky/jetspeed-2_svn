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
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.search.SearchResults;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.portals.bridges.beans.TabBean;
import org.apache.portals.bridges.common.GenericServletPortlet;
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
public class PortletApplicationBrowser extends GenericServletPortlet
{
    private String template;
    private PortletContext context;
    private PortletRegistry registry;
    private SearchEngine searchEngine;
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        context = getPortletContext();                
        registry = (PortletRegistry)context.getAttribute(PortletApplicationResources.CPS_REGISTRY_COMPONENT);
        searchEngine = (SearchEngine)context.getAttribute(PortletApplicationResources.CPS_SEARCH_COMPONENT);
        if (null == registry)
        {
            throw new PortletException("Failed to find the Portlet Registry on portlet initialization");
        }
    }
    
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        response.setContentType("text/html");
        
        TreeControl control = (TreeControl) request.getPortletSession().getAttribute("j2_tree");
        if(control == null)
        {
            Collection apps = registry.getPortletApplications();
        	control = buildTree(apps, request.getLocale());
        	request.getPortletSession().setAttribute("j2_tree", control);
        }
        request.setAttribute("j2_tree", control);
        request.setAttribute("search_results", request.getPortletSession().getAttribute("search_results"));
        
        super.doView(request, response);
        
    }

    
	public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
	{
		TreeControl control = (TreeControl) actionRequest.getPortletSession().getAttribute("j2_tree");
		//assert control != null
		if(control != null)
		{
		    String searchString = actionRequest.getParameter("query");
		    if(searchString != null)
		    {
		        SearchResults results = searchEngine.search(searchString);
		        actionRequest.getPortletSession().setAttribute("search_results", results.getResults());
		    }
		    
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
			    TreeControlNode child = control.findNode(selectedNode);
			    if(child != null)
			    {
			        MutablePortletApplication pa = null;
			        
				    String domain = child.getDomain();
				    if(domain.equals("PA_APP_DOMAIN"))
				    {
				        pa = registry.getPortletApplicationByIdentifier(selectedNode);
				        if(pa != null)
				        {
				            actionRequest.getPortletSession().removeAttribute(PortletApplicationResources.REQUEST_SELECT_PORTLET, PortletSession.APPLICATION_SCOPE);
				        }
				    }
				    else if(domain.equals("PD_DOMAIN"))
				    {
				        TreeControlNode parent = child.getParent();
			            pa = registry.getPortletApplicationByIdentifier(parent.getName());
			            
			            //set selected tab to portlets tab
			            if(pa != null)
			            {
			                //TODO:  do we need to look up the pdef?  Could we just pass the child name into setAttribute?
			                String pdefName = child.getName().substring(pa.getName().length() + 2); //remove pa prefix
			                PortletDefinition pdef = pa.getPortletDefinitionByName(pdefName);
			                actionRequest.getPortletSession().setAttribute(PortletApplicationResources.REQUEST_SELECT_PORTLET, pdef.getName(), PortletSession.APPLICATION_SCOPE);
			                actionRequest.getPortletSession().setAttribute(PortletApplicationResources.REQUEST_SELECT_TAB, new TabBean("pa_portlets"), PortletSession.APPLICATION_SCOPE);
			            }
				    }
				    else
				    {
				        //warn about not recognized domain
				    }
				    
				    if (pa != null)
		            {
		                actionRequest.getPortletSession().setAttribute(PortletApplicationResources.PAM_CURRENT_PA, pa.getName(), PortletSession.APPLICATION_SCOPE);
		            }
			    }
			}
		}
	}
	
	private TreeControl buildTree(Collection apps, Locale locale) 
    {	    
		TreeControlNode root =
            new TreeControlNode("ROOT-NODE",
                                null, 
                                "J2_ROOT",
                                PortletApplicationResources.PORTLET_URL,
                                null, 
                                true, 
                                "J2_DOMAIN");
		
		TreeControl control = new TreeControl(root);
		
		
		TreeControlNode portletApps = 
			new TreeControlNode("APP_ROOT", 
                                null, 
                                "APP_ROOT", 
                                PortletApplicationResources.PORTLET_URL, 
                                null, 
                                false, 
                                "J2_DOMAIN");
		root.addChild(portletApps);
		
		Iterator it = apps.iterator();
        while (it.hasNext())
        {
            MutablePortletApplication pa = (MutablePortletApplication)it.next();
            TreeControlNode appNode = new TreeControlNode(pa.getName(), 
                                                          null, 
                                                          pa.getName(), 
                                                          PortletApplicationResources.PORTLET_URL, 
                                                          null, 
                                                          false, 
                                                          "PA_APP_DOMAIN"  );
            portletApps.addChild(appNode);
            
            Iterator pdefIter = pa.getPortletDefinitionList().iterator();
            while (pdefIter.hasNext())
            {
                PortletDefinitionComposite portlet = (PortletDefinitionComposite) pdefIter.next();
                TreeControlNode portletNode = new TreeControlNode(pa.getName() + "::" + portlet.getName(), 
                                                                  null, 
                                                                  portlet.getDisplayNameText(locale), 
                                                                  PortletApplicationResources.PORTLET_URL, 
                                                                  null, 
                                                                  false, 
                                                                  "PD_DOMAIN");
                appNode.addChild(portletNode);
            }
        }
		
		return control;
	}
}