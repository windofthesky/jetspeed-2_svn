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
import javax.portlet.PortletMode;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.components.portletregistry.FailedToStorePortletDefinitionException;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.common.MutableDescription;
import org.apache.jetspeed.om.common.MutableDisplayName;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.om.common.SecurityRoleRefComposite;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.portlets.pam.beans.PortletApplicationBean;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.portals.bridges.beans.TabBean;
import org.apache.portals.bridges.common.GenericServletPortlet;

/**
 * This portlet is a tabbed editor user interface for editing both portlet defintions
 * and portlet applications.
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletApplicationDetail extends GenericServletPortlet
{
    private static final String PORTLET_ACTION = "portlet_action";
    private final String VIEW_PA = "portletApplication"; 
    private final String VIEW_PD = "portletDefinition";
    
    private static final String PORTLET_APP_ACTION_PREFIX = "portlet_app.";
    private static final String PORTLET_ACTION_PREFIX = "portlet.";

    private PortletContext context;
    private PortletRegistry registry;
    private LinkedHashMap paTabMap = new LinkedHashMap();
    private LinkedHashMap pdTabMap = new LinkedHashMap();
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        context = getPortletContext();
        registry = (PortletRegistry)context.getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
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
        
        String paName = (String)
            request.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_PA, 
                                             PortletSession.APPLICATION_SCOPE);
        
        MutablePortletApplication pa = registry.getPortletApplication(paName);
        
        if (null != pa)
        {
            request.setAttribute(VIEW_PA, new PortletApplicationBean(pa));
            
            String pdefName = (String) request.getPortletSession().getAttribute(PortletApplicationResources.REQUEST_SELECT_PORTLET, PortletSession.APPLICATION_SCOPE);
            PortletDefinitionComposite pdef = (PortletDefinitionComposite) pa.getPortletDefinitionByName(pdefName);
            
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
        String paName = (String)
        actionRequest.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_PA, 
                                             PortletSession.APPLICATION_SCOPE);
                
        String selectedPortlet = actionRequest.getParameter(PortletApplicationResources.REQUEST_SELECT_PORTLET);
        if(selectedPortlet != null)
        {
            actionRequest.getPortletSession().setAttribute(PortletApplicationResources.REQUEST_SELECT_PORTLET, selectedPortlet, PortletSession.APPLICATION_SCOPE);
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
                    processMetadataAction(actionRequest, actionResponse, paName, null, action);
                }
                else if(action.endsWith("user_attribute"))
                {
                    processUserAttributeAction(actionRequest, actionResponse, paName, action);
                }
            }
            else if(isPortletAction(action))
            {
                
                action = getAction(PORTLET_ACTION_PREFIX, action);
                String pdefName = (String) actionRequest.getPortletSession().getAttribute(PortletApplicationResources.REQUEST_SELECT_PORTLET, PortletSession.APPLICATION_SCOPE);
                
                try
                {
                    if(action.endsWith("metadata"))
                    {
                        processMetadataAction(actionRequest, actionResponse, paName, pdefName, action);
                    }
                    else if(action.endsWith("portlet"))
                    {

                        MutablePortletApplication pa = registry.getPortletApplication(paName);                                        

                        PortletDefinitionComposite pdef = (PortletDefinitionComposite) pa.getPortletDefinitionByName(pdefName);
                        
                        processPortletAction(actionRequest, actionResponse, pa, pdef, action);
                    }
                    else if(action.endsWith("preference"))
                    {

                        MutablePortletApplication pa = registry.getPortletApplication(paName);                                        

                        PortletDefinitionComposite pdef = (PortletDefinitionComposite) pa.getPortletDefinitionByName(pdefName);
                        
                        processPreferenceAction(actionRequest, actionResponse, pa, pdef, action);
                    }
                    else if(action.endsWith("language"))
                    {

                        MutablePortletApplication pa = registry.getPortletApplication(paName);                                        

                        PortletDefinitionComposite pdef = (PortletDefinitionComposite) pa.getPortletDefinitionByName(pdefName);
                        
                        processLanguage(actionRequest, actionResponse, pa, pdef, action);
                    }
                    else if(action.endsWith("parameter"))
                    {

                        MutablePortletApplication pa = registry.getPortletApplication(paName);                                        

                        PortletDefinitionComposite pdef = (PortletDefinitionComposite) pa.getPortletDefinitionByName(pdefName);
                        
                        processParameter(actionRequest, actionResponse, pa, pdef, action);
                    }
                    else if(action.endsWith("security"))
                    {

                        MutablePortletApplication pa = registry.getPortletApplication(paName);                                        

                        PortletDefinitionComposite pdef = (PortletDefinitionComposite) pa.getPortletDefinitionByName(pdefName);
                        
                        processSecurity(actionRequest, actionResponse, pa, pdef, action);
                    }
                    else if(action.endsWith("content_type"))
                    {

                        MutablePortletApplication pa = registry.getPortletApplication(paName);                                        

                        PortletDefinitionComposite pdef = (PortletDefinitionComposite) pa.getPortletDefinitionByName(pdefName);
                        
                        processContentType(actionRequest, actionResponse, pa, pdef, action);
                    }
                }
                catch (RegistryException e)
                {                    
                    throw new PortletException("A Registry action has failed.  "+e.getMessage());                    
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
    private void processUserAttributeAction(ActionRequest actionRequest, ActionResponse actionResponse, String paName, String action) 
    throws PortletException, IOException
    {
        
        if(action.equals("edit_user_attribute"))
        {
            String userAttrName = "";
            try
            {
                MutablePortletApplication mpa = registry.getPortletApplication(paName);
                boolean modified = false;
                Iterator userAttrIter = mpa.getUserAttributes().iterator();
                while (userAttrIter.hasNext())
                {
                    UserAttribute userAttr = (UserAttribute) userAttrIter.next();
                    
                    userAttrName = userAttr.getName();
                    String description = actionRequest.getParameter(userAttrName + ":description");
                    if(!userAttr.getDescription().equals(description))
                    {
                        userAttr.setDescription(description);
                        modified = true;
                    }
                }
                if (modified)
                {
                    registry.updatePortletApplication(mpa);
                }
            }
            catch (RegistryException e)
            {
                throw new PortletException("Failed update user attribute: " + userAttrName, e);
                
            }
        }
        else if(action.equals("add_user_attribute"))
        {
            String userAttrName = actionRequest.getParameter("user_attr_name");
            String userAttrDesc = actionRequest.getParameter("user_attr_desc");
            if (userAttrName != null && userAttrName.trim().length() > 0)                
            {
                try
                {
                    MutablePortletApplication mpa = registry.getPortletApplication(paName);
                    
                    mpa.addUserAttribute(userAttrName.trim(), userAttrDesc);                
                    
                    registry.updatePortletApplication(mpa);
                }
                catch (RegistryException e)
                {
                    throw new PortletException("Failed add user attribute: " + userAttrName, e);                    
                }
            }
        }
        else if(action.equals("remove_user_attribute"))
        {
            String[] userAttrNames = actionRequest.getParameterValues("user_attr_id");
            
            if(userAttrNames != null)
            {
                String userAttrName = "";
                try
                {
                    int count = 0;
                    MutablePortletApplication mpa = registry.getPortletApplication(paName);
                    Iterator userAttrIter = mpa.getUserAttributes().iterator();
                    while (userAttrIter.hasNext())
                    {
                        UserAttribute userAttr = (UserAttribute) userAttrIter.next();
                        for(int ix = 0; ix < userAttrNames.length; ix++)
                        {
                            userAttrName = userAttrNames[ix];
                            if(userAttr.getName().equals(userAttrName))
                            {
                                userAttrIter.remove();
                                count++;                                
                                break;
                            }
                        }
                    }                    
                    if (count > 0)
                    {

                        registry.updatePortletApplication(mpa);
                    }
                }
                catch (RegistryException e)
                {
                    throw new PortletException("Failed remove user attribute: " + userAttrName, e);
                    
                }                
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
    private void processMetadataAction(ActionRequest actionRequest, 
                                       ActionResponse actionResponse, 
                                       String  paName, 
                                       String  pdName,
                                       String action)
    throws PortletException, IOException
    {
        MutablePortletApplication pa = registry.getPortletApplication(paName);
        if (pa == null)
        {
            return;
        }        
        PortletDefinitionComposite pd = null;
        GenericMetadata meta = null;                
        if (pdName != null)
        {
            pd = (PortletDefinitionComposite) pa.getPortletDefinitionByName(pdName);
            if (pd != null)
            {
                meta = pd.getMetadata();
            }
            else
            {
                return;
            }
        }
        else
        {
            meta = pa.getMetadata();
        }
        if (meta == null)
        {
            return;
        }
        
        if(action.equals("edit_metadata"))
        {               
            try
            {                
                boolean modified = false;
                Iterator fieldsIter = meta.getFields().iterator();            
                while (fieldsIter.hasNext())
                {
                    LocalizedField field = (LocalizedField) fieldsIter.next();
                    String id = field.getId().toString();
                    String value = actionRequest.getParameter(id + ":value");
                    if (value != null)
                    {
                        if (!value.equals(field.getValue()))
                        {
                            field.setValue(value);
                            modified = true;
                        }
                    }
                }
                if (modified)
                {
                    if (pd == null)
                    {                        
                        registry.updatePortletApplication(pa);
                    }
                    else
                    {                        
                        registry.savePortletDefinition(pd);
                    }                                         
                }
            }
            catch (RegistryException e)
            {
                throw new PortletException("Failed update meta data attributes: " 
                        + paName + ", " + ((pdName == null) ? "" : pdName), e);                                    
            }            
        }
        else if (action.equals("remove_metadata"))
        {
            String[] ids = actionRequest.getParameterValues("metadata_id");            
            if (ids != null)
            {
                try
                {
                    Iterator fieldsIter = meta.getFields().iterator();
                    int count = 0;                        
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
                                count++;
                                break;
                            }
                        }
                    }
                    if (count > 0)
                    {
                        if (pd == null)
                        {                            
                            registry.updatePortletApplication(pa);
                        }
                        else
                        {                                
                            registry.savePortletDefinition(pd);
                        }   
                    
                    }                        
                }
                catch (RegistryException e)
                {
                    throw new PortletException("Failed remove meta data attributes: " 
                            + paName + ", " + ((pdName == null) ? "" : pdName), e);                                    
                }                                            
            }
        }
        else if(action.equals("add_metadata"))
        {
            String name = actionRequest.getParameter("name");
            String value = actionRequest.getParameter("value");
            String localeParam = actionRequest.getParameter("locale");
            
            if(localeParam == null || name.trim().length() == 0)
            {
                localeParam = "en"; //need to default better
            }
            Locale locale = new Locale(localeParam);
            
            if (name != null && name.trim().length() > 0)                
            {
                try
                {
                    meta.addField(locale, name, value);   
                    if (pd == null)
                    {
                        registry.updatePortletApplication(pa);
                    }
                    else
                    {
                        registry.savePortletDefinition(pd);                      
                    }                                      
                  
                }
                catch (RegistryException e)
                {
                    throw new PortletException("Failed add meta data attribute: " + paName + ", " + name, e);                    
                }                                                                
            }
        }
    }
        
    private void processPortletAction(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, PortletDefinitionComposite portlet, String action) throws RegistryException
    {
        if(action.equals("edit_portlet"))
        {
            String displayNameParam = actionRequest.getParameter("display_name");
            if(displayNameParam == null)
            {            
                int index = 0;
                Iterator displayNameIter = portlet.getDisplayNameSet().iterator();
                while (displayNameIter.hasNext())
                {
                    MutableDisplayName displayName = (MutableDisplayName) displayNameIter.next();
                    displayNameParam = actionRequest.getParameter("display_name:" + index);
                    
                    //this should never happen
                    if(displayNameParam != null)
                    {
                        if(displayNameParam.length() == 0)
                        {
                            displayNameIter.remove();
                        }
                        else if(!displayNameParam.equals(displayName.getDisplayName()))
                        {
                            displayName.setDisplayName(displayNameParam);
                        }
                    }
                    index++;
                }
            }
            else
            {
                String locale = actionRequest.getParameter("locale");
                portlet.addDisplayName(new Locale(locale), displayNameParam);
            }            
            
            registry.savePortletDefinition(portlet);
        
        }
        else if(action.equals("remove_portlet"))
        {
            //TODO should this be allowed??
        }
        else if(action.equals("add_portlet"))
        {
            
        }
    }
    
    /**
     * @param actionRequest
     * @param actionResponse
     * @param pa
     * @param pdef
     * @param action
     * @throws RegistryException
     */
    private void processPreferenceAction(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, PortletDefinitionComposite portlet, String action) throws RegistryException
    {
        if(action.equals("add_preference"))
        {
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
            
            registry.savePortletDefinition(portlet);
        }
        else if(action.equals("edit_preference"))
        {
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
            
            registry.savePortletDefinition(portlet);
        }
        else if(action.equals("remove_preference"))
        {
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
            
         // registry.getPersistenceStore().getTransaction().commit();
        }
    }
    
    /**
     * @param actionRequest
     * @param actionResponse
     * @param pa
     * @param pdef
     * @param action
     * @throws RegistryException
     */
    private void processLanguage(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, PortletDefinitionComposite portlet, String action) throws RegistryException
    {
         if(action.equals("add_language"))
         {
             String title = actionRequest.getParameter("title");
             String shortTitle = actionRequest.getParameter("short_title");
             String keywords = actionRequest.getParameter("keyword");
             String locale = actionRequest.getParameter("locale");

             portlet.addLanguage(title, shortTitle, keywords, new Locale(locale));

             registry.savePortletDefinition(portlet);
         }
         else if(action.equals("remove_language"))
         {
             String[] removeIds = actionRequest.getParameterValues("language_remove_id");

             if(removeIds != null)
             {
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

                 registry.savePortletDefinition(portlet);
             }
         }
         else if(action.equals("edit_language"))
         {
             String[] editIds = actionRequest.getParameterValues("language_edit_id");

             if(editIds != null)
             {
                 //technically, the size and set of edit ids should be 
                 //equal to the size and set of the language set

                 String id;
                 int index=0;
                 Iterator langIter = portlet.getLanguageSet().iterator();
                 while (langIter.hasNext())
                 {
                     id = editIds[index];
                     
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

                     registry.savePortletDefinition(portlet);
                     
                     index++;
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
     * @throws FailedToStorePortletDefinitionException
     */
    private void processParameter(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, PortletDefinitionComposite portlet, String action) throws FailedToStorePortletDefinitionException
    {
        if(action.equals("add_parameter"))
        {
            String name = actionRequest.getParameter("name");
            if(name != null)
            {
                String description = actionRequest.getParameter("description");
                String locale = actionRequest.getParameter("locale");
                
                ParameterComposite parameter = (ParameterComposite)portlet.getInitParameterSet().get(name);
                if(parameter == null)
                {
                    String value = actionRequest.getParameter("value");
                    parameter = portlet.addInitParameter(name, value, description, new Locale(locale));
                }
                else
                {
                    parameter.addDescription(new Locale(locale), description);
                }
                
                registry.savePortletDefinition(portlet);
            }
        }
        else if(action.equals("edit_parameter"))
        {
            String[] paramIds = actionRequest.getParameterValues("parameter_edit_id");
            
            if(paramIds != null)
            {
                for(int i=0; i<paramIds.length; i++)
                {
                    String paramId = paramIds[i];
                    ParameterComposite param = (ParameterComposite) portlet.getInitParameterSet().get(paramId);
                    
                    String value = actionRequest.getParameter(paramId + ":value");
                    param.setValue(value);
                    
                    int index = 0;
                    Iterator descIter = param.getDescriptionSet().iterator();
                    while (descIter.hasNext())
                    {
                        MutableDescription description = (MutableDescription) descIter.next();
                        String descParam = actionRequest.getParameter(paramId + ":description:" + index);
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
            }
            
            registry.savePortletDefinition(portlet);
        }
        else if(action.equals("remove_parameter"))
        {
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
            
            registry.savePortletDefinition(portlet);
        }
    }
    
    /**
     * @param actionRequest
     * @param actionResponse
     * @param pa
     * @param pdef
     * @param action
     * @throws FailedToStorePortletDefinitionException
     */
    private void processSecurity(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, PortletDefinitionComposite portlet, String action) throws FailedToStorePortletDefinitionException
    {
        if(action.equals("add_security"))
        {
            String name = actionRequest.getParameter("name");
            
            if(name != null)
            {
                String link = actionRequest.getParameter("link");
                
                SecurityRoleRefComposite securityRoleRef = (SecurityRoleRefComposite) portlet.getInitSecurityRoleRefSet().get(name);
                if(securityRoleRef == null && link != null)
                {
                    securityRoleRef = (SecurityRoleRefComposite) portlet.addSecurityRoleRef(name, link);
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
                registry.savePortletDefinition(portlet);
            }
        }
        else if(action.equals("edit_security"))
        {
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
            
            registry.savePortletDefinition(portlet);
        }
        else if(action.equals("remove_security"))
        {
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
            
            registry.savePortletDefinition(portlet);
        }
    }
    
    /**
     * @param actionRequest
     * @param actionResponse
     * @param pa
     * @param pdef
     * @param action
     * @throws FailedToStorePortletDefinitionException
     */
    private void processContentType(ActionRequest actionRequest, ActionResponse actionResponse, MutablePortletApplication pa, PortletDefinitionComposite portlet, String action) throws FailedToStorePortletDefinitionException
    {
        if(action.equals("add_content_type"))
        {
            String contentType = actionRequest.getParameter("content_type");
            if(contentType != null)
            {
                ArrayList allModes = new ArrayList();
                
                
                String[] modes = actionRequest.getParameterValues("mode");
                if(modes != null)
                {
                    for(int i=0; i<modes.length; i++)
                    {
                        String mode = modes[i];
                        //contentTypeImpl.addPortletMode(mode);
                        allModes.add(new PortletMode(mode));
                    }
                }

                String customModes = actionRequest.getParameter("custom_modes");
                StringTokenizer tok = new StringTokenizer(customModes, ",");
                while (tok.hasMoreTokens())
                {
                    //contentTypeImpl.addPortletMode(tok.nextToken());
                    allModes.add(tok.nextToken());
                }
                
                portlet.addContentType(contentType, allModes);
                
                //registry.getPersistenceStore().getTransaction().commit();
                registry.savePortletDefinition(portlet);
            }
        }
        else if(action.equals("edit_content_type"))
        {
            registry.savePortletDefinition(portlet);
        }
        else if(action.equals("remove_content_type"))
        {
            String[] contentIds = actionRequest.getParameterValues("content_type_remove_id");
            if(contentIds != null)
            {
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
                
                registry.savePortletDefinition(portlet);
            }
        }
    }
    
    private String createXml(MutablePortletApplication pa)
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        
        //TODO:  add namespace
        buffer.append("<portlet-app id=\"");
        buffer.append(pa.getApplicationIdentifier());
        buffer.append("\" version=\"");
        buffer.append(pa.getVersion());
        buffer.append("\">\n");
        
        Iterator portletDefsIter = pa.getPortletDefinitions().iterator();
        while (portletDefsIter.hasNext())
        {
            PortletDefinitionComposite pDef = (PortletDefinitionComposite) portletDefsIter.next();
            buffer.append(createPortletDefinitionXml(pDef));
        }
        
        buffer.append("</portlet-app>\n");
        
        return buffer.toString();
    }
    
    private String createPortletDefinitionXml(PortletDefinitionComposite pDef)
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("<portlet id=\"");
        buffer.append(pDef.getPortletIdentifier());
        buffer.append("\">\n");
        
        Iterator paramIter = pDef.getInitParameterSet().iterator();
        while (paramIter.hasNext())
        {
            ParameterComposite param = (ParameterComposite) paramIter.next();
            buffer.append("<init-param>\n");
            
            addDescriptions(buffer, param.getDescriptionSet());
            
            buffer.append("\t<name>");
            buffer.append(param.getName());
            buffer.append("</name>\n");
            buffer.append("\t<value>");
            buffer.append(param.getValue());
            buffer.append("</value>\n");
            buffer.append("</init-param>\n");
        }
        
        buffer.append("\t<portlet-name>");
        buffer.append(pDef.getName());
        buffer.append("</portlet-name>\n");
        
        Iterator displayNameIter = pDef.getDisplayNameSet().iterator();
        while (displayNameIter.hasNext())
        {
            MutableDisplayName displayName = (MutableDisplayName) displayNameIter.next();
            buffer.append("\t<display-name");
            if(displayName.getLocale() != null)
            {
                buffer.append(" xml:lang=\"");
                buffer.append(displayName.getLocale().getCountry());
                buffer.append("\"");
            }
            buffer.append(">");
            buffer.append(displayName.getDisplayName());
            buffer.append("</display-name>\n");
        }
        
        addDescriptions(buffer, pDef.getDescriptionSet());
        
        buffer.append("\t<portlet-class>");
        buffer.append(pDef.getClassName());
        buffer.append("</portlet-class>\n");
        
        buffer.append("\t<expiration-cache>");
        buffer.append(pDef.getExpirationCache());
        buffer.append("</expiration-cache>\n");
        
        
        Iterator contentTypeIter = pDef.getContentTypeSet().iterator();
        while (contentTypeIter.hasNext())
        {
            buffer.append("\t<supports>\n");
            ContentType contentType = (ContentType) contentTypeIter.next();
            buffer.append("\t\t<mime-type>\n");
            buffer.append(contentType.getContentType());
            buffer.append("</mime-type>\n");
            
            Iterator modeIter = contentType.getPortletModes();
            while (modeIter.hasNext())
            {
                PortletMode mode = (PortletMode) modeIter.next();
                buffer.append("\t\t<portlet-mode>");
                buffer.append(mode.toString());
                buffer.append("</portlet-mode>\n");
            }
            
            buffer.append("</supports>");
        }
        
        
        
        String resourceBundle = pDef.getResourceBundle();
        if(resourceBundle == null)
        {
	        //<portlet-info>
	        //StringBuffer supportedLocaleBuffer = new StringBuffer();
	        StringBuffer portletInfoBuffer = new StringBuffer();
	        
	        Iterator langIter = pDef.getLanguageSet().iterator();
	        while (langIter.hasNext())
	        {
	            MutableLanguage lang = (MutableLanguage) langIter.next();
	            /*
	            supportedLocaleBuffer.append("\t<supported-locale>");
	            supportedLocaleBuffer.append(lang.getLocale().getCountry());
	            supportedLocaleBuffer.append("</supported-locale>\n");
	            */
	            
	            //lang.
	            portletInfoBuffer.append("\t<portlet-info>\n");
	            portletInfoBuffer.append("\t\t<title>");
	            portletInfoBuffer.append(lang.getTitle());
	            portletInfoBuffer.append("</title>\n");
	            if(lang.getShortTitle() != null)
	            {
	                portletInfoBuffer.append("\t\t<short-title>");
	                portletInfoBuffer.append(lang.getShortTitle());
	                portletInfoBuffer.append("</short-title>\n");
	            }
	            Iterator keywordIter = lang.getKeywords();
	            if(keywordIter.hasNext())
	            {
	                portletInfoBuffer.append("\t\t<keywords>");
		            while (keywordIter.hasNext())
		            {
		                String keyword = (String) keywordIter.next();
		                portletInfoBuffer.append(keyword);
		                if(keywordIter.hasNext())
		                {
		                    portletInfoBuffer.append(",");
		                }
		            }
		            portletInfoBuffer.append("</keywords>\n");
	            }
	            portletInfoBuffer.append("\t</portlet-info>\n");
	        }
	        
//	        buffer.append(supportedLocaleBuffer);
	        buffer.append(portletInfoBuffer);
        }
        else
        {
            Iterator supportIter = pDef.getSupportedLocales().iterator();
            while (supportIter.hasNext())
            {
                Locale locale = (Locale) supportIter.next();
                buffer.append("\t<supported-locale>");
                buffer.append(locale.getCountry());
                buffer.append("<supported-locale>\n");
            }
        }
        
        buffer.append("\t<portlet-preferences>\n");
        Iterator prefIter = pDef.getPreferenceSet().iterator();
        while (prefIter.hasNext())
        {
            PreferenceComposite pref = (PreferenceComposite) prefIter.next();
            buffer.append("\t\t<preference>\n");
            buffer.append("\t\t\t<name>);");
            buffer.append(pref.getName());
            buffer.append("</name>\n");
            String[] values = pref.getValueArray();
            for (int i = 0; i < values.length; i++)
            {
                String value = values[i];
                buffer.append("\t\t\t<value>");
                buffer.append(value);
                buffer.append("</value>\n");
            }
            
            buffer.append("\t\t</preference>\n");
        }
        buffer.append("</portlet-preferences>");
        
        buffer.append("</portlet>\n");
        
        return buffer.toString();
    }
    
    private void addDescriptions(StringBuffer buffer, DescriptionSet descriptions)
    {
        Iterator descIter = descriptions.iterator();
        MutableDescription desc = (MutableDescription) descIter.next();
        buffer.append("\t<description");
        if(desc.getLocale() != null)
        {
            buffer.append(" xml:lang=\"");
            buffer.append(desc.getLocale().getCountry());
            buffer.append("\"");
        }
        buffer.append(">");
        buffer.append(desc.getDescription());
        buffer.append("</description>\n");
    }

}
