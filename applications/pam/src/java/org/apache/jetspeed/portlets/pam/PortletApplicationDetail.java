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
import java.util.LinkedHashMap;
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
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.impl.UserAttributeImpl;
import org.apache.pluto.om.common.Preference;
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
    
    private static final String PORTLET_APP_ACTION_PREFIX = "portlet_app.";
    private static final String PORTLET_ACTION_PREFIX = "portlet.";

    private PortletContext context;
    private PortletRegistryComponent registry;
    private LinkedHashMap paTabMap = new LinkedHashMap();
    private LinkedHashMap pdTabMap = new LinkedHashMap();
    
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
        
        TabBean tb1 = new TabBean("pa_details");
        TabBean tb2 = new TabBean("pa_metadata");
        TabBean tb3 = new TabBean("pa_portlets");
        TabBean tb4 = new TabBean("pa_user_attribtues");
        
        paTabMap.put(tb1.getId(), tb1);
        paTabMap.put(tb2.getId(), tb2);
        paTabMap.put(tb3.getId(), tb3);
        paTabMap.put(tb4.getId(), tb4);
        
        TabBean tb_1 = new TabBean("pd_details");
        TabBean tb_2 = new TabBean("pd_metadata");
        TabBean tb_3 = new TabBean("pd_preferences");
        TabBean tb_4 = new TabBean("pd_languages");
        TabBean tb_5 = new TabBean("pd_parameters");
        TabBean tb_6 = new TabBean("pd_security");
        TabBean tb_7 = new TabBean("pd_content_type");
        
        pdTabMap.put(tb_1.getId(), tb_1);
        pdTabMap.put(tb_2.getId(), tb_2);
        pdTabMap.put(tb_3.getId(), tb_3);
        pdTabMap.put(tb_4.getId(), tb_4);
        pdTabMap.put(tb_5.getId(), tb_5);
        pdTabMap.put(tb_6.getId(), tb_6);
        pdTabMap.put(tb_7.getId(), tb_7);
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
            
            request.setAttribute("tabs", paTabMap.values());
            request.setAttribute("portlet_tabs", pdTabMap.values());
            
            TabBean selectedTab = (TabBean) request.getPortletSession().getAttribute("selected_tab");
            if(selectedTab == null)
            {
                selectedTab = (TabBean) paTabMap.values().iterator().next();
            }
            
            //this supports tabs for portlets
            if(selectedTab.getId().equals("pa_portlets"))
            {
                TabBean selectedPortletTab = (TabBean) request.getPortletSession().getAttribute("selected_portlet_tab");
                if(selectedPortletTab == null)
                {
                    selectedPortletTab = (TabBean) pdTabMap.values().iterator().next();
                }
                request.setAttribute("selected_portlet_tab", selectedPortletTab);
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
            TabBean tab = (TabBean) paTabMap.get(selectedTab);
            actionRequest.getPortletSession().setAttribute("selected_tab", tab);
        }
        
        String selectedPortletTab = actionRequest.getParameter("selected_portlet_tab");
        if(selectedPortletTab != null)
        {
            TabBean tab = (TabBean) pdTabMap.get(selectedPortletTab);
            actionRequest.getPortletSession().setAttribute("selected_portlet_tab", tab);
        }
        
        String action = actionRequest.getParameter(PORTLET_ACTION);
        if(action != null)
        {
            
            if(isAppAction(action))
            {
                action = getAction(PORTLET_APP_ACTION_PREFIX, action);
                
                if(action.endsWith("metadata"))
                {
                    processMetadataAction(actionRequest, actionResponse, pa.getMetadata(), action);
                }
                else if(action.endsWith("user_attribute"))
                {
                    processUserAttributeAction(actionRequest, actionResponse, pa, action);
                }
            }
            else if(isPortletAction(action))
            {
                action = getAction(PORTLET_ACTION_PREFIX, action);
                PortletDefinitionComposite pdef = (PortletDefinitionComposite) actionRequest.getPortletSession().getAttribute(PortletApplicationResources.REQUEST_SELECT_PORTLET, PortletSession.APPLICATION_SCOPE);
                
                if(action.endsWith("metadata"))
                {
                    processMetadataAction(actionRequest, actionResponse, pdef.getMetadata(), action);
                }
                else if(action.endsWith("portlet"))
                {
                    processPortletAction(actionRequest, actionResponse, pa, pdef, action);
                }
                else if(action.endsWith("preference"))
                {
                    processPreferenceAction(actionRequest, actionResponse, pa, pdef, action);
                }
            }
        }
	}

    private boolean isAppAction(String action)
    {
        return action.startsWith(PORTLET_APP_ACTION_PREFIX);
    }
    
    private boolean isPortletAction(String action)
    {
        return action.startsWith(PORTLET_ACTION_PREFIX);
    }
    
    private String getAction(String prefix, String action)
    {
        return action.substring(prefix.length());
    }
    
    /**
     * @param actionRequest
     * @param actionResponse
     * @param pa
     * @param action
     */
    private void processUserAttributeAction(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, String action) throws PortletException, IOException
    {
        if(action.equals("edit_user_attribute"))
        {
            registry.getPersistenceStore().getTransaction().begin();
            
            Iterator userAttrIter = pa.getUserAttributes().iterator();
            while (userAttrIter.hasNext())
            {
                UserAttribute userAttr = (UserAttribute) userAttrIter.next();
                
                String userAttrName = userAttr.getName();
                String description = actionRequest.getParameter(userAttrName + ":description");
                if(!userAttr.getDescription().equals(description))
                {
                    userAttr.setDescription(description);
                }
            }
            
            registry.getPersistenceStore().getTransaction().commit();
        }
        else if(action.equals("add_user_attribute"))
        {
            String userAttrName = actionRequest.getParameter("user_attr_name");
            String userAttrDesc = actionRequest.getParameter("user_attr_desc");
            if(userAttrName != null)
            {
                registry.getPersistenceStore().getTransaction().begin();
            
                //TODO: should this come from a factory??
                UserAttribute userAttribute = new UserAttributeImpl();
                userAttribute.setName(userAttrName);
                userAttribute.setDescription(userAttrDesc);
                pa.addUserAttribute(userAttribute);
	            
	            registry.getPersistenceStore().getTransaction().commit();
            }
        }
        else if(action.equals("remove_user_attribute"))
        {
            String[] userAttrNames = actionRequest.getParameterValues("user_attr_id");

            if(userAttrNames != null)
            {
                registry.getPersistenceStore().getTransaction().begin();
                                
	            Iterator userAttrIter = pa.getUserAttributes().iterator();
	            while (userAttrIter.hasNext())
	            {
	                UserAttribute userAttr = (UserAttribute) userAttrIter.next();
	                for(int i=0; i<userAttrNames.length; i++)
	                {
	                    String userAttrName = userAttrNames[i];
	                    if(userAttr.getName().equals(userAttrName))
	                    {
	                        userAttrIter.remove();
	                        break;
	                    }
	                }
	            }
	            
	            registry.getPersistenceStore().getTransaction().commit();
                
            }
        }
    }

    /**
     * @param actionRequest
     * @param actionResponse
     * @param pa
     * @param action
     * @throws PortletException
     * @throws IOException
     */
    private void processMetadataAction(ActionRequest actionRequest, ActionResponse actionResponse, GenericMetadata md, String action) throws PortletException, IOException
    {
        if(action.equals("edit_metadata"))
        {
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
            Iterator fieldsIter = md.getFields().iterator();
            String[] ids = actionRequest.getParameterValues("metadata_id");
            
            if(ids != null)
            {
	            registry.getPersistenceStore().getTransaction().begin();
	            while (fieldsIter.hasNext())
	            {
	                LocalizedField field = (LocalizedField) fieldsIter.next();
	                String id = field.getId().toString();

                    for(int i=0; i<ids.length; i++)
                    {
                        String mid = ids[i];
                        if(mid.equals(id))
                        {
                            fieldsIter.remove();
                            break;
                        }
                    }
                }
            }
            registry.getPersistenceStore().getTransaction().commit();
        }
        else if(action.equals("add_metadata"))
        {
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
    
    private void processPortletAction(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, PortletDefinitionComposite portlet, String action) throws PortletException, IOException
    {
        if(action.equals("edit_portlet"))
        {

        }
        else if(action.equals("remove_portlet"))
        {
            //TODO should this be allowed??
        }
        else if(action.equals("add_portlet"))
        {
            //TODO should this be allowed??
        }
    }
    
    /**
     * @param actionRequest
     * @param actionResponse
     * @param pa
     * @param pdef
     * @param action
     */
    private void processPreferenceAction(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, PortletDefinitionComposite portlet, String action)
    {
        if(action.equals("add_preference"))
        {
            registry.getPersistenceStore().getTransaction().begin();
            
            String name = actionRequest.getParameter("name");
            String value = actionRequest.getParameter("value");
            
            Preference pref = portlet.getPreferenceSet().get(name);
            //if(pref == null)
            {
                portlet.addPreference(name, new String[] { value });
            }
            //else
            {
                //pref.
            }
            
            registry.getPersistenceStore().getTransaction().commit();
        }        
    }
}