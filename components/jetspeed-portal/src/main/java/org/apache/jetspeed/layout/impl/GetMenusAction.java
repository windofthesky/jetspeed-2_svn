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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Get menus action retrieves all menu names defined for the addressed page.
 * <p/>
 * AJAX Parameters:
 * none
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class GetMenusAction extends BasePortletAction
        implements AjaxAction, AjaxBuilder, Constants {
    protected static final Logger log = LoggerFactory.getLogger(GetMenusAction.class);

    public GetMenusAction(String template,
                          String errorTemplate,
                          PortletActionSecurityBehavior securityBehavior) {
        super(template, errorTemplate, securityBehavior);
    }

    public boolean run(RequestContext requestContext, Map<String, Object> resultMap) {
        boolean success = true;
        String status = "success";
        try {
            // generate action result
            resultMap.put(ACTION, "getmenus");

            // check permission to use ajax api
            if (!checkAccess(requestContext, JetspeedActions.VIEW)) {
                success = false;
                resultMap.put(REASON, "Insufficient access to get menus");
                return success;
            }

            // get request context
            PortalSiteRequestContext siteRequestContext = (PortalSiteRequestContext) requestContext.getAttribute(ProfilerValveImpl.PORTAL_SITE_REQUEST_CONTEXT_ATTR_KEY);
            if (siteRequestContext == null) {
                success = false;
                resultMap.put(REASON, "Missing portal site request context from ProfilerValve");
                return success;
            }

            // get menu names
            Set standardMenuNames = siteRequestContext.getStandardMenuNames();
            Set customMenuNames = null;
            try {
                customMenuNames = siteRequestContext.getCustomMenuNames();
            } catch (NodeNotFoundException nnfe) {
            }

            // return menu names action results
            resultMap.put(STANDARD_MENUS, standardMenuNames);
            resultMap.put(CUSTOM_MENUS, customMenuNames);

            // get action parameter
            String includeMenuDefinitions = getActionParameter(requestContext, INCLUDE_MENU_DEFS);
            if (includeMenuDefinitions != null && includeMenuDefinitions.toLowerCase().equals("true")) {
                // get request locale
                Locale locale = requestContext.getLocale();

                HashMap menuDefinitionsMap = new HashMap();

                StringBuffer failReason = new StringBuffer();
                Iterator menuNamesIter = standardMenuNames.iterator();
                while (menuNamesIter.hasNext()) {
                    String menuName = (String) menuNamesIter.next();
                    Menu menuDefinition = getMenuDefinition(menuName, siteRequestContext, failReason);
                    if (menuDefinition != null)
                        menuDefinitionsMap.put(menuName, menuDefinition);
                }
                menuNamesIter = customMenuNames.iterator();
                while (menuNamesIter.hasNext()) {
                    String menuName = (String) menuNamesIter.next();
                    Menu menuDefinition = getMenuDefinition(menuName, siteRequestContext, failReason);
                    if (menuDefinition != null)
                        menuDefinitionsMap.put(menuName, menuDefinition);
                }

                if (failReason.length() > 0) {
                    success = false;
                    resultMap.put(REASON, failReason.toString());
                    return success;
                }
                resultMap.put(INCLUDE_MENU_DEFS, new Boolean(true));
                resultMap.put(MENU_DEFINITIONS, menuDefinitionsMap);
                resultMap.put(MENU_CONTEXT, siteRequestContext);
                resultMap.put(MENU_LOCALE, locale);
            } else {
                resultMap.put(INCLUDE_MENU_DEFS, new Boolean(false));
            }
            resultMap.put(STATUS, status);
        } catch (Exception e) {
            log.error("Exception while getting page menus info", e);
            success = false;
        }

        return success;
    }

    private Menu getMenuDefinition(String menuName, PortalSiteRequestContext siteRequestContext, StringBuffer failReason) {
        // get menu definition
        Menu menuDefinition = null;
        try {
            menuDefinition = siteRequestContext.getMenu(menuName);
        } catch (NodeNotFoundException nnfe) {
        }
        if (menuDefinition == null && failReason != null) {
            if (failReason.length() == 0)
                failReason.append("Unable to lookup specified menus: ").append(menuName);
            else
                failReason.append(", ").append(menuName);
        }
        return menuDefinition;
    }
}
