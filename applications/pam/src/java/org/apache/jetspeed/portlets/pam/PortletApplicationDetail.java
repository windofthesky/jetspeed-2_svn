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
import java.util.HashMap;
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
import org.apache.jetspeed.portlet.ServletPortlet;
import org.apache.jetspeed.portlets.pam.beans.PortletApplicationBean;
import org.apache.jetspeed.portlets.pam.beans.TabBean;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.LocalizedField;
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
    private static final String PORTLET_ACTION = "portlet_action";
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
            
            PortletDefinition pdef = (PortletDefinition) request.getPortletSession().getAttribute(PortletApplicationResources.REQUEST_SELECT_PORTLET, PortletSession.APPLICATION_SCOPE);
            
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
        //System.out.println("PorletApplicationDetail: processAction()");
        MutablePortletApplication pa = (MutablePortletApplication)
    	actionRequest.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_PA, 
                                             PortletSession.APPLICATION_SCOPE);
        
        String selectedPortlet = actionRequest.getParameter(PortletApplicationResources.REQUEST_SELECT_PORTLET);
        if(selectedPortlet != null)
        {
	        
	        
	        PortletDefinition pdef = pa.getPortletDefinitionByName(selectedPortlet);
	        actionRequest.getPortletSession().setAttribute(PortletApplicationResources.REQUEST_SELECT_PORTLET, pdef, PortletSession.APPLICATION_SCOPE);
        }
        
        String selectedTab = actionRequest.getParameter("selected_tab");
        if(selectedTab != null)
        {
            TabBean tab = (TabBean) tabMap.get(selectedTab);
            actionRequest.getPortletSession().setAttribute("selected_tab", tab);
        }
        
        String action = actionRequest.getParameter(PORTLET_ACTION);
        if(action != null)
        {
            if(action.equals("edit_metadata"))
            {
                GenericMetadata md = pa.getMetadata();
                Iterator fieldsIter = md.getFields().iterator();
                
                registry.getPersistenceStore().getTransaction().begin();
                
                while (fieldsIter.hasNext())
                {
                    LocalizedField field = (LocalizedField) fieldsIter.next();
                    String id = field.getId().toString();
                    String value = actionRequest.getParameter(id + ":value");
                    if(value != null)
                    {
                        if(!value.equals(field.getValue()))
                        {
                            field.setValue(value);
                        }
                    }
                }
                
                registry.getPersistenceStore().getTransaction().commit();
            }
            else if(action.equals("remove_metadata"))
            {
                GenericMetadata md = pa.getMetadata();
                Iterator fieldsIter = md.getFields().iterator();
                
                registry.getPersistenceStore().getTransaction().begin();
                while (fieldsIter.hasNext())
                {
                    LocalizedField field = (LocalizedField) fieldsIter.next();
                    String id = field.getId().toString();
                    String[] ids = actionRequest.getParameterValues("metadata_id");
                    if(ids != null)
                    {
                        for(int i=0; i<ids.length; i++)
                        {
                            String mid = ids[i];
                            if(mid.equals(id))
                            {
                                fieldsIter.remove();
                            }
                        }
                    }
                }
                registry.getPersistenceStore().getTransaction().commit();
            }
            else if(action.equals("add_metadata"))
            {
                GenericMetadata md = pa.getMetadata();
                
                PersistenceStore store = registry.getPersistenceStore();
                System.out.println("Transcation is open: " + store.getTransaction().isOpen());
                store.getTransaction().begin();
                System.out.println("Transcation is open: " + store.getTransaction().isOpen());
                String name = actionRequest.getParameter("name");
                String value = actionRequest.getParameter("value");
                String localeParam = actionRequest.getParameter("locale");
                if(localeParam == null)
                {
                    localeParam = "en"; //need to default better
                }
                Locale locale = new Locale(localeParam);
                
                md.addField(locale, name, value);
                
                store.getTransaction().commit();
            }
        }
	}
}