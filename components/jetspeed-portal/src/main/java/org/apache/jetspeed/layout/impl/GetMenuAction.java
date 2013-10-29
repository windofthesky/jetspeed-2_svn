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

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.portalsite.Menu;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.profiler.impl.ProfilerValveImpl;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;

/**
 * Get menu action retrieves a menu defined for the addressed page.
 *
 * AJAX Parameters: 
 *    menu = the name of the menu definition to retrieve 
 *    
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class GetMenuAction extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected static final Logger log = LoggerFactory.getLogger(GetMenusAction.class);
    
    public GetMenuAction(String template,
                         String errorTemplate,
                         PortletActionSecurityBehavior securityBehavior)
    {
        super(template, errorTemplate, securityBehavior);
    }

    public boolean run(RequestContext requestContext, Map<String,Object> resultMap)
    {
        boolean success = true;
        String status = "success";
        try
        {
            // generate action result
            resultMap.put(ACTION, "getmenu");

            // check permission to use ajax api
            if (!checkAccess(requestContext, JetspeedActions.VIEW))
            {
                success = false;
                resultMap.put(REASON, "Insufficient access to get menu");
                return success;
            }

            // get action parameter
            String menuName = getActionParameter(requestContext, MENU_NAME);
            if (menuName == null)
            {
                success = false;
                resultMap.put(REASON, "Missing required '" + MENU_NAME + "' parameter");
                return success;
            }

            // get request context
            PortalSiteRequestContext siteRequestContext = (PortalSiteRequestContext)requestContext.getAttribute(ProfilerValveImpl.PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY);
            if (siteRequestContext == null)
            {
                success = false;
                resultMap.put(REASON, "Missing portal site request context from ProfilerValve");
                return success;
            }

            // get request locale
            Locale locale = requestContext.getLocale();

            // get menu definition
            Menu menuDefinition = null;
            try
            {
                menuDefinition = siteRequestContext.getMenu(menuName);
            }
            catch (NodeNotFoundException nnfe)
            {
            }
            if (menuDefinition == null)
            {
                success = false;
                resultMap.put(REASON, "Unable to lookup specified menu for page");
                return success;
            }

            // return menu definition action results
            resultMap.put(MENU, menuDefinition);
            resultMap.put(MENU_CONTEXT, siteRequestContext);
            resultMap.put(MENU_LOCALE, locale);
            resultMap.put(STATUS, status);
        }
        catch (Exception e)
        {
            log.error("Exception while getting page menus info", e);
            success = false;
        }

        return success;
	}
}
