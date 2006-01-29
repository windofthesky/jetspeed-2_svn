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
package org.apache.jetspeed.layout.impl;

import java.security.AccessControlException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.PortletPermission;

/**
 * Get Portlets retrieves the portlet list available to the current subject
 *
 * AJAX Parameters: 
 *    filter = (optional)filter to lookup portlets using fulltext search
 *    
 * @author <a>David Gurney</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class GetPortletsAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants, Comparator
{
    protected Log log = LogFactory.getLog(GetPortletsAction.class);
    private PortletRegistry registry = null;
    private SearchEngine searchEngine = null;
    
    public GetPortletsAction(String template, 
                             String errorTemplate,
                             PageManager pageManager,
                             PortletRegistry registry,
                             SearchEngine searchEngine,
                             PermissionManager permissionManager,
                             PortletActionSecurityBehavior securityBehavior)
    {
        super(template, errorTemplate, pageManager, securityBehavior);
        this.registry = registry;
        this.searchEngine = searchEngine;
    }

    public boolean run(RequestContext requestContext, Map resultMap)
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, "getportlets");
            if (false == checkAccess(requestContext, JetspeedActions.VIEW))
            {
//                if (!createNewPageOnEdit(requestContext))
//                {
                    success = false;
                    resultMap.put(REASON, "Insufficient access to edit page");
                    return success;
//                }
//                status = "refresh";
            }            
            String filter = requestContext.getRequestParameter(FILTER);                                    
            List portlets = retrievePortlets(requestContext, filter);            
            resultMap.put(STATUS, status);
            resultMap.put(PORTLETS, portlets);
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while getting portlet info", e);

            // Return a failure indicator
            success = false;
        }

        return success;
	}
    
    protected List retrievePortlets(RequestContext requestContext, String filter)
    {
        Iterator portlets = null;
        List list = new ArrayList();
        Locale locale = requestContext.getLocale();
        
        if (filter == null)
            portlets = registry.getAllPortletDefinitions().iterator();
        else
            portlets = searchEngine.search(filter).getResults().iterator();
        
        while (portlets.hasNext())
        {
            PortletDefinitionComposite portlet = null;
            if (filter == null)
                portlet = (PortletDefinitionComposite)portlets.next();
            else
                portlet = this.getPortletFromParsedObject((ParsedObject)portlets.next());
            
            if (portlet == null)
                continue;
            
            MutablePortletApplication muta = 
                (MutablePortletApplication)portlet.getPortletApplicationDefinition();
            String appName = muta.getName();
            if (appName != null && appName.equals("jetspeed-layouts"))
                continue;                
            
            // SECURITY filtering
            String uniqueName = appName + "::" + portlet.getName();
            try
            {
                AccessController.checkPermission(new PortletPermission(portlet.getUniqueName(), JetspeedActions.MASK_VIEW));
                list.add(new PortletInfo(uniqueName, portlet.getDisplayNameText(locale), portlet.getDescriptionText(locale)));
            }
            catch (AccessControlException ace)
            {
                //continue
            }
        }            
        Collections.sort(list, this);
        return list;
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
    
    public int compare(Object obj1, Object obj2)
    {
        PortletInfo portlet1 = (PortletInfo)obj1;
        PortletInfo portlet2 = (PortletInfo)obj2;
        String name1 = portlet1.getName();
        String name2 = portlet2.getName();
        name1 = (name1 == null) ? "unknown" : name1;
        name2 = (name2 == null) ? "unknown" : name2;
        return name1.compareTo(name2);
    }
}
