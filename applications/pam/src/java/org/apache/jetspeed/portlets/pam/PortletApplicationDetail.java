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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.StringTokenizer;

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
import org.apache.jetspeed.om.common.MutableDescription;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.om.common.SecurityRoleRefComposite;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.impl.LanguageImpl;
import org.apache.jetspeed.om.impl.SecurityRoleRefImpl;
import org.apache.jetspeed.om.impl.UserAttributeImpl;
import org.apache.jetspeed.om.portlet.impl.ContentTypeImpl;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.portlet.ContentType;
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
            
            TabBean selectedTab = (TabBean) request.getPortletSession().getAttribute(PortletApplicationResources.REQUEST_SELECT_TAB, PortletSession.APPLICATION_SCOPE);
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
            
            request.setAttribute(PortletApplicationResources.REQUEST_SELECT_TAB, selectedTab);
            
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
        
        String selectedTab = actionRequest.getParameter(PortletApplicationResources.REQUEST_SELECT_TAB);
        if(selectedTab != null)
        {
            TabBean tab = (TabBean) paTabMap.get(selectedTab);
            actionRequest.getPortletSession().setAttribute(PortletApplicationResources.REQUEST_SELECT_TAB, tab, PortletSession.APPLICATION_SCOPE);
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
                else if(action.endsWith("language"))
                {
                    processLanguage(actionRequest, actionResponse, pa, pdef, action);
                }
                else if(action.endsWith("parameter"))
                {
                    processParameter(actionRequest, actionResponse, pa, pdef, action);
                }
                else if(action.endsWith("security"))
                {
                    processSecurity(actionRequest, actionResponse, pa, pdef, action);
                }
                else if(action.endsWith("content_type"))
                {
                    processContentType(actionRequest, actionResponse, pa, pdef, action);
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
            
            PreferenceComposite pref = (PreferenceComposite) portlet.getPreferenceSet().get(name);
            if(pref == null)
            {
                portlet.addPreference(name, new String[] { value });
            }
            else
            {
                pref.addValue(value);
            }
            
            registry.getPersistenceStore().getTransaction().commit();
        }
        else if(action.equals("edit_preference"))
        {
            registry.getPersistenceStore().getTransaction().begin();
            
            String[] prefNames = actionRequest.getParameterValues("pref_edit_id");
            for (int i = 0; i < prefNames.length; i++)
            {
                String prefName = prefNames[i];
                PreferenceComposite prefComp = (PreferenceComposite) portlet.getPreferenceSet().get(prefName);
                String[] values = prefComp.getValueArray();
                for (int j = 0; j < values.length; j++)
                {
                    String value = values[j];
                    String newValue = actionRequest.getParameter(prefName + ":" + j);
                    if(!value.equals(newValue))
                    {
                        prefComp.setValueAt(j, newValue);
                    }
                }
            }
            
            registry.getPersistenceStore().getTransaction().commit();
        }
        else if(action.equals("remove_preference"))
        {
            registry.getPersistenceStore().getTransaction().begin();
            
            String[] prefNames = actionRequest.getParameterValues("pref_remove_id");
            
            Iterator prefIter = portlet.getPreferenceSet().iterator();
            while (prefIter.hasNext())
            {
                PreferenceComposite pref = (PreferenceComposite) prefIter.next();
                String name = pref.getName();
                
                for(int i=0; i<prefNames.length; i++)
                {
                    String prefName = prefNames[i];
                    if(name.equals(prefName))
                    {
                        prefIter.remove();
                        break;
                    }
                }
            }
            
            registry.getPersistenceStore().getTransaction().commit();
        }
    }
    
    /**
     * @param actionRequest
     * @param actionResponse
     * @param pa
     * @param pdef
     * @param action
     */
    private void processLanguage(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, PortletDefinitionComposite portlet, String action)
    {
         if(action.equals("add_language"))
         {
             registry.getPersistenceStore().getTransaction().begin();

             String title = actionRequest.getParameter("title");
             String shortTitle = actionRequest.getParameter("short_title");
             String keywords = actionRequest.getParameter("keyword");
             String locale = actionRequest.getParameter("locale");

             LanguageImpl language = new LanguageImpl();
             language.setTitle(title);
             language.setShortTitle(shortTitle);
             language.setKeywords(keywords);
             language.setLocale(new Locale(locale));
             portlet.addLanguage(language);

             registry.getPersistenceStore().getTransaction().commit();
         }
         else if(action.equals("remove_language"))
         {
             String[] removeIds = actionRequest.getParameterValues("language_remove_id");

             if(removeIds != null)
             {
                 registry.getPersistenceStore().getTransaction().begin();

                 int id = 0;
                 Iterator langIter = portlet.getLanguageSet().iterator();
                 while (langIter.hasNext())
                 {
                     langIter.next();

                     int currentId = id++;
                     for(int i=0; i<removeIds.length; i++)
                     {
                         String removeId = removeIds[i];
                         String tempId = "" + currentId;
                         if(removeId.equals(tempId))
                         {
                             langIter.remove();
                             break;
                         }
                     }
                 }

                 registry.getPersistenceStore().getTransaction().commit();
	         }
         }
         else if(action.equals("edit_language"))
         {
             String[] editIds = actionRequest.getParameterValues("language_edit_id");

             if(editIds != null)
             {
                 registry.getPersistenceStore().getTransaction().begin();

                 //technically, the size and set of edit ids should be 
                 //equal to the size and set of the language set

                 int id = 0;
                 Iterator langIter = portlet.getLanguageSet().iterator();
                 while (langIter.hasNext())
                 {
                     String title = actionRequest.getParameter("title:" + id);
                     String shortTitle = actionRequest.getParameter("short_title:" + id);

                     //must cast to interface to avoid class loader issues
                     MutableLanguage lang = (MutableLanguage) langIter.next();

                     if(!lang.getTitle().equals(title))
                     {
                         lang.setTitle(title);
                     }

                     Iterator keywordIter = lang.getKeywords();
                     int keywordIndex = 0;
                     ArrayList keywordList = new ArrayList();
                     
                     while (keywordIter.hasNext())
                     {
                         String keyword = (String) keywordIter.next();
                         String keywordParam = actionRequest.getParameter("keyword:" + id + ":" + keywordIndex);

                         if(keywordParam != null && keywordParam.length() > 0)
                         {
                             keywordList.add(keywordParam);
                         }

                         keywordIndex++;
                     }

                     lang.setKeywords(keywordList);
                     
                     if(!lang.getShortTitle().equals(shortTitle))
                     {
                         lang.setShortTitle(shortTitle);
                     }

                     registry.getPersistenceStore().getTransaction().commit();
                 }
             }
         }
    }
    
    /**
     * @param actionRequest
     * @param actionResponse
     * @param pa
     * @param pdef
     * @param action
     */
    private void processParameter(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, PortletDefinitionComposite portlet, String action)
    {
        if(action.equals("add_parameter"))
        {
            registry.getPersistenceStore().getTransaction().begin();
            
            String name = actionRequest.getParameter("name");
            String value = actionRequest.getParameter("value");
            String description = actionRequest.getParameter("description");
            String locale = actionRequest.getParameter("locale");
            
            portlet.addInitParameter(name, value, description, new Locale(locale));
            
            registry.getPersistenceStore().getTransaction().commit();
        }
        else if(action.equals("edit_parameter"))
        {
            registry.getPersistenceStore().getTransaction().begin();
            
            String[] paramIds = actionRequest.getParameterValues("parameter_edit_id");
            
            if(paramIds != null)
            {
                for(int i=0; i<paramIds.length; i++)
                {
                    String paramId = paramIds[i];
                    ParameterComposite param = (ParameterComposite) portlet.getInitParameterSet().get(paramId);
                    
                    String value = actionRequest.getParameter(paramId + ":value");
                    //String description[] = actionRequest.getParameterValues(paramId + ":description");
                    
                    
                    param.setValue(value);
                }
            }
            
            registry.getPersistenceStore().getTransaction().commit();
        }
        else if(action.equals("remove_parameter"))
        {
            registry.getPersistenceStore().getTransaction().begin();
            
            String[] paramIds = actionRequest.getParameterValues("parameter_remove_id");
            
            if(paramIds != null)
            {
	            Iterator paramIter = portlet.getInitParameterSet().iterator();
	            while (paramIter.hasNext())
	            {
	                ParameterComposite param = (ParameterComposite) paramIter.next();
	                
	                for(int i=0; i<paramIds.length; i++)
	                {
	                    String paramId = paramIds[i];
	                    if(param.getName().equals(paramId))
	                    {
	                        paramIter.remove();
	                        break;
	                    }
	                }
	            }
            }
            
            registry.getPersistenceStore().getTransaction().commit();
        }
    }
    
    /**
     * @param actionRequest
     * @param actionResponse
     * @param pa
     * @param pdef
     * @param action
     */
    private void processSecurity(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, PortletDefinitionComposite portlet, String action)
    {
        if(action.equals("add_security"))
        {
            String name = actionRequest.getParameter("name");
            
            if(name != null)
            {
                registry.getPersistenceStore().getTransaction().begin();
                
                String link = actionRequest.getParameter("link");
	            
	            SecurityRoleRefComposite securityRoleRef = (SecurityRoleRefComposite) portlet.getInitSecurityRoleRefSet().get(name);
	            if(securityRoleRef == null && link != null)
	            {
		            securityRoleRef = new SecurityRoleRefImpl();
		            securityRoleRef.setRoleName(name);
		            securityRoleRef.setRoleLink(link);
		            portlet.addSecurityRoleRef(securityRoleRef);
	            }
	            
	            if(securityRoleRef != null)
	            {
		            String description = actionRequest.getParameter("description");
		            if(description != null && description.length() > 0)
		            {
			            String locale = actionRequest.getParameter("locale");
			            if(locale == null)
			            {
			                locale = "en";
			            }
			            securityRoleRef.addDescription(new Locale(locale), description);
		            }
	            }
	            registry.getPersistenceStore().getTransaction().commit();
            }
        }
        else if(action.equals("edit_security"))
        {
            registry.getPersistenceStore().getTransaction().begin();
            
            Iterator securityIter = portlet.getInitSecurityRoleRefSet().iterator();
            while (securityIter.hasNext())
            {
                SecurityRoleRefComposite secRef = (SecurityRoleRefComposite) securityIter.next();
                String name = secRef.getRoleName();
                
                //TODO:  should this be editable
                String newName = actionRequest.getParameter(name + ":name");
                String link = actionRequest.getParameter(name + ":link");
                
                if(!secRef.getRoleLink().equals(link))
                {
                    secRef.setRoleLink(link);
                }
                
                int index = 0;
                Iterator descIter = secRef.getDescriptionSet().iterator();
                while (descIter.hasNext())
                {
                    MutableDescription description = (MutableDescription) descIter.next();
                    String descParam = actionRequest.getParameter(name + ":description:" + index);
                    //changing locale not allowed.
                    
                    if(descParam != null)
                    {
                        if(descParam.length() == 0)
                        {
                            descIter.remove();
                        }
                        else if(!descParam.equals(description.getDescription()))
                        {
                            description.setDescription(descParam);
                        }
                    }
                    
                    index++;
                }
            }
            
            registry.getPersistenceStore().getTransaction().commit();
        }
        else if(action.equals("remove_security"))
        {
            registry.getPersistenceStore().getTransaction().begin();
            
            String[] securityIds = actionRequest.getParameterValues("security_remove_id");
            if(securityIds != null)
            {
                for(int i=0; i<securityIds.length; i++)
                {
                    String id = securityIds[i];
                    SecurityRoleRef secRef = portlet.getInitSecurityRoleRefSet().get(id);
                    portlet.getInitSecurityRoleRefSet().remove(secRef);
                }
                /*
                Iterator securityIter = portlet.getInitSecurityRoleRefSet()..iterator();
                while (securityIter.hasNext())
                {
                    SecurityRoleRefComposite secRef = (SecurityRoleRefComposite) securityIter.next();
                    for(int i=0; i<securityIds.length; i++)
                    {
                        String id = securityIds[i];
                        if(secRef.getRoleName().equals(id))
                        {
                            securityIter.remove();
                            break;
                        }
                    }
                }
                */
            }
            
            registry.getPersistenceStore().getTransaction().commit();
        }
    }
    
    /**
     * @param actionRequest
     * @param actionResponse
     * @param pa
     * @param pdef
     * @param action
     */
    private void processContentType(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, PortletDefinitionComposite portlet, String action)
    {
        if(action.equals("add_content_type"))
        {
            String contentType = actionRequest.getParameter("content_type");
            if(contentType != null)
            {
	            registry.getPersistenceStore().getTransaction().begin();
	            
	            ContentTypeImpl contentTypeImpl = new ContentTypeImpl();
	            contentTypeImpl.setContentType(contentType);
	            
	            String[] modes = actionRequest.getParameterValues("mode");
	            if(modes != null)
	            {
	                for(int i=0; i<modes.length; i++)
	                {
	                    String mode = modes[i];
	                    contentTypeImpl.addPortletMode(mode);
	                }
	            }
	            
	            
	            String customModes = actionRequest.getParameter("custom_modes");
	            StringTokenizer tok = new StringTokenizer(customModes, ",");
	            while (tok.hasMoreTokens())
	            {
	                contentTypeImpl.addPortletMode(tok.nextToken());
	            }
	            
	            portlet.addContentType(contentTypeImpl);
	            
	            registry.getPersistenceStore().getTransaction().commit();
            }
        }
        else if(action.equals("edit_content_type"))
        {
            registry.getPersistenceStore().getTransaction().begin();
            registry.getPersistenceStore().getTransaction().commit();
        }
        else if(action.equals("remove_content_type"))
        {
            String[] contentIds = actionRequest.getParameterValues("content_type_remove_id");
            if(contentIds != null)
            {
                registry.getPersistenceStore().getTransaction().begin();
                
                Iterator contentIter = portlet.getContentTypeSet().iterator();
                while (contentIter.hasNext())
                {
                    ContentType contentType = (ContentType) contentIter.next();
                    for(int i=0; i<contentIds.length; i++)
                    {
                        String id = contentIds[i];
	                    if(contentType.getContentType().equals(id))
	                    {
	                        contentIter.remove();
	                        break;
	                    }
                    }
                }
                
                registry.getPersistenceStore().getTransaction().commit();
            }
        }
    }
}