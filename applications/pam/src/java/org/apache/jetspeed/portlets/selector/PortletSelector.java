/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.portlets.selector;

import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.security.auth.Subject;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.PortletPermission;
import org.apache.portals.gems.browser.BrowserIterator;
import org.apache.portals.gems.browser.BrowserPortlet;
import org.apache.portals.gems.util.StatusMessage;
import org.apache.portals.messaging.PortletMessaging;

/**
 * Selects one or more portlets
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletSelector extends BrowserPortlet
{
    protected PortletRegistry registry;
    protected SearchEngine searchEngine;
    protected PermissionManager permissionManager;
    
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
        searchEngine = (SearchEngine)context.getAttribute(CommonPortletServices.CPS_SEARCH_COMPONENT);
        if (null == searchEngine)
        {
            throw new PortletException("Failed to find the Search Engine on portlet initialization");
        }
        permissionManager = (PermissionManager)context.getAttribute(CommonPortletServices.CPS_PERMISSION_MANAGER);
        if (null == permissionManager)
        {
            throw new PortletException("Failed to find the Permission Manager on portlet initialization");
        }        
        
    }
          
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_STATUS);
        if (msg != null)
        {
            this.getContext(request).put("statusMsg", msg);            
        }
        
        String filtered = (String)PortletMessaging.receive(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_FILTERED);
        if (filtered != null)
        {
            this.getContext(request).put(FILTERED, "on");            
        }
                
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse response)
    throws PortletException, IOException
    {
        String filtered = (String)request.getParameter(FILTERED);
        if (filtered != null)
        {
            PortletMessaging.publish(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_FILTERED, "on");            
        }
        else
        {
            PortletMessaging.cancel(request, PortletApplicationResources.TOPIC_PORTLET_SELECTOR, PortletApplicationResources.MESSAGE_FILTERED);
        }
        
        super.processAction(request, response);
            
    }
    
    public void getRows(RenderRequest request, String sql, int windowSize)
    throws Exception
    {
        getRows(request, sql, windowSize, null);
    }

    public void getRows(RenderRequest request, String sql, int windowSize, String filter)
    throws Exception    
    {
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        try
        {
            Iterator portlets = null;
            
            if (filter == null)
                portlets = registry.getAllPortletDefinitions().iterator();
            else
                portlets = searchEngine.search(filter).getResults().iterator();
                                    
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("Portlet");
            resultSetTitleList.add("Description");            
            Locale locale = request.getLocale();
            List list = new ArrayList();
            
            // get subject
            RequestContext requestContext = (RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);            
            Subject subject = null;
            if (requestContext != null)
                subject = requestContext.getSubject();
            
            while (portlets.hasNext())
            {
                PortletDefinitionComposite portlet = null;
                if (filter == null)
                    portlet = (PortletDefinitionComposite)portlets.next();
                else
                    portlet = this.getPortletFromParsedObject((ParsedObject)portlets.next());
                
                if (portlet == null)
                    continue;
                
                MutablePortletApplication muta = (MutablePortletApplication)portlet.getPortletApplicationDefinition();
                String appName = muta.getName();
                if (appName != null && appName.equals("jetspeed-layouts"))
                    continue;                
                
                // SECURITY filtering
                String uniqueName = appName + "::" + portlet.getName();
                if (subject != null)
                {
                    if (permissionManager.checkPermission(subject, 
                        new PortletPermission(portlet.getUniqueName(), 
                        SecuredResource.VIEW_ACTION, subject )))
                    {
                        list.add(new PortletInfo(uniqueName, portlet.getDisplayNameText(locale), portlet.getDescriptionText(locale)));
                    }
                }
            }            
            BrowserIterator iterator = new PortletIterator(
                    list, resultSetTitleList, resultSetTypeList,
                    windowSize);
            setBrowserIterator(request, iterator);
            iterator.sort("Portlet");
        }
        catch (Exception e)
        {
            //log.error("Exception in CMSBrowserAction.getRows: ", e);
            e.printStackTrace();
            throw e;
        }        
    }
      
    public class PortletInfo 
    {
        private String name;
        private String displayName;
        private String description;
        
        public PortletInfo(String name, String displayName, String description)
        {
            this.name = name;
            this.displayName = displayName;
            this.description = description;
        }
        /**
         * @return Returns the description.
         */
        public String getDescription()
        {
            return description;
        }
        /**
         * @return Returns the displayName.
         */
        public String getDisplayName()
        {
            return displayName;
        }
        /**
         * @return Returns the name.
         */
        public String getName()
        {
            return name;
        }
    }

    public int find(BrowserIterator iterator, String searchString, String searchColumn)
    {
        int index = 0;
        int column = 1; 
        
        if (searchColumn != null)
            column = Integer.parseInt(searchColumn);
        
        Iterator it = iterator.getResultSet().iterator();
        while (it.hasNext())
        {
            PortletInfo info = (PortletInfo)it.next();
            String name = info.getDisplayName();
            if (name != null && name.startsWith(searchString))
            {
                return index;
            }
            index++;
        }
        
        return -1;
    }
    
    protected PortletDefinitionComposite getPortletFromParsedObject(ParsedObject po)
    {
        boolean found = false;
        String name = "";
        Map fields = po.getFields();
        if(fields != null)
        {
            Object id = fields.get("ID");
    
            if(id != null)
            {
                if(id instanceof Collection)
                {
                    Collection coll = (Collection)id;
                    name = (String) coll.iterator().next();
                }
                else
                {
                    name = (String)id;
                }
            }
            
            if(po.getType().equals("portlet"))
            {
                Object pa = fields.get("portlet_application");
                String paName = "";
                if(pa != null)
                {
                    if(id instanceof Collection)
                    {
                        Collection coll = (Collection)pa;
                        paName = (String) coll.iterator().next();
                    }
                    else
                    {
                        paName = (String)pa;
                    }
                }
                name = paName + "::" + name;
                found = true;
            }
        }
        if (found == false)
            return null;
        
        return registry.getPortletDefinitionByUniqueName(name);
    }
}
