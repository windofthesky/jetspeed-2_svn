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

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.portalsite.PortalSiteRequestContext;
import org.apache.jetspeed.profiler.impl.ProfilerValveImpl;
import org.apache.jetspeed.request.RequestContext;

/**
 * Get menus action retrieves all menu names defined for the addressed page.
 *
 * AJAX Parameters: 
 *    none
 *    
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class GetMenusAction extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Log log = LogFactory.getLog(GetMenusAction.class);
    
    public GetMenusAction(String template,
                          String errorTemplate,
                          PortletActionSecurityBehavior securityBehavior)
    {
        super(template, errorTemplate, securityBehavior);
    }

    public boolean run(RequestContext requestContext, Map resultMap)
    {
        boolean success = true;
        String status = "success";
        try
        {
            // generate action result
            resultMap.put(ACTION, "getmenus");

            // check permission to use ajax api
            if (!checkAccess(requestContext, JetspeedActions.VIEW))
            {
                success = false;
                resultMap.put(REASON, "Insufficient access to get menus");
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

            // get menu names
            Set standardMenuNames = siteRequestContext.getStandardMenuNames();
            Set customMenuNames = null;
            try
            {
                customMenuNames = siteRequestContext.getCustomMenuNames();
            }
            catch (NodeNotFoundException nnfe)
            {
            }

            // return menu names action results
            resultMap.put(STANDARD_MENUS, standardMenuNames);
            resultMap.put(CUSTOM_MENUS, customMenuNames);
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
