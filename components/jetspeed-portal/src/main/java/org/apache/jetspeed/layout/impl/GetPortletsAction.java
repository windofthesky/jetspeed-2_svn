/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.security.SecurityAccessController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    protected static final Logger log = LoggerFactory.getLogger(GetPortletsAction.class);
    private PortletRegistry registry = null;
    private SearchEngine searchEngine = null;
    private SecurityAccessController securityAccessController;
    
    public final static String PORTLET_ICON = "portlet-icon";
    
    public GetPortletsAction(String template, String errorTemplate)
    {
        this(template, errorTemplate, null, null, null, null, null);
    }
    
    public GetPortletsAction(String template, 
                             String errorTemplate,
                             PageManager pageManager,
                             PortletRegistry registry,
                             SearchEngine searchEngine,
                             SecurityAccessController securityAccessController,
                             PortletActionSecurityBehavior securityBehavior)
    {
        super(template, errorTemplate, pageManager, securityBehavior);
        this.registry = registry;
        this.searchEngine = searchEngine;
        this.securityAccessController = securityAccessController;
    }

    public boolean run(RequestContext requestContext, Map<String,Object> resultMap)
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
            String type = getActionParameter(requestContext, TYPE );
            String format = getActionParameter(requestContext, FORMAT );
            
            String filter = getActionParameter(requestContext, FILTER);                                    
            List<PortletInfo> portlets = retrievePortlets(requestContext, filter); 
            
            resultMap.put(TYPE, type );
            resultMap.put(FORMAT, format );
            resultMap.put(STATUS, status);
            resultMap.put(PORTLETS, portlets);
        } 
        catch (Exception e)
        {
            // Logger the exception
            log.error("exception while getting portlet info", e);

            // Return a failure indicator
            success = false;
        }

        return success;
	}
    
    public List<PortletInfo> retrievePortlets(RequestContext requestContext, String filter)
    {
        Iterator portlets = null;
        List<PortletInfo> list = new ArrayList<PortletInfo>();
        Locale locale = requestContext.getLocale();
        
        if (filter == null)
            portlets = registry.getAllDefinitions().iterator();
        else
            portlets = searchEngine.search(filter).getResults().iterator();
        
        while (portlets.hasNext())
        {
            PortletDefinition portlet = null;
            if (filter == null)
                portlet = (PortletDefinition)portlets.next();
            else
                portlet = this.getPortletFromParsedObject((ParsedObject)portlets.next());
            
            if (portlet == null)
                continue;
            
            // Do not display Jetspeed Layout Applications
            PortletApplication pa = (PortletApplication)portlet.getApplication();
            if (pa.isLayoutApplication())
                continue;
                 
            // SECURITY filtering
            String uniqueName = pa.getName() + "::" + portlet.getPortletName();
            if (securityAccessController.checkPortletAccess(portlet, JetspeedActions.MASK_VIEW))
            {
                InitParam param = portlet.getInitParam(PORTLET_ICON);
                String image;
                if (param != null)
                {
                    //String relativeImagePath = param.getValue();
                    //String context = muta.getWebApplicationDefinition().getContextRoot();
                    // Have to use a supported icon in jetspeed, otherwise image can be out of skew
                    image = "images/portlets/" + param.getParamValue();
                }
                else
                {                                        
                    image = "images/portlets/applications-internet.png";
                }
                

                PortletInfo portInfo=new PortletInfo(uniqueName, 
                		StringEscapeUtils.escapeXml(portlet.getDisplayNameText(locale)), 
                        StringEscapeUtils.escapeXml(portlet.getDescriptionText(locale)), image);
                
                list.add(portInfo);
            }
        }            
        Collections.sort(list, this);
        return list;
    }
    
    protected PortletDefinition getPortletFromParsedObject(ParsedObject po)
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
        
        return registry.getPortletDefinitionByUniqueName(name, true);
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
