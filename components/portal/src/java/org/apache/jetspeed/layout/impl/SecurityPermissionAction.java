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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.Coordinate;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.layout.PortletPlacementContext;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.PermissionManager;

/**
 * Security Permission action
 * 
 * AJAX Parameters: 
 *    action = permission
 *    sub = add | remove | grant | revoke 
 *    page = (implied in the URL)
 *    resource = name of the resource to modify
 *    actions = comma-separated actions
 *    type = portlet | page | folder
 * Parameters for grant | revoke:  
 *    role = name of  role to grant or revoke
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class SecurityPermissionAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Log log = LogFactory.getLog(SecurityPermissionAction.class);
    protected PermissionManager pm = null;

    public SecurityPermissionAction(String template, 
                            String errorTemplate, 
                            PermissionManager pm,
                            PortletActionSecurityBehavior securityBehavior)
    {
        super(template, errorTemplate, securityBehavior); 
        this.pm = pm;
    }
    
    public boolean run(RequestContext requestContext, Map resultMap)
            throws AJAXException
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, "permissions");
            // Get the necessary parameters off of the request
            String sub = requestContext.getRequestParameter("sub");
            if (sub == null) 
            { 
                throw new RuntimeException("Sub Action not provided"); 
            }            
            resultMap.put("sub", sub);
            if (false == checkAccess(requestContext, JetspeedActions.EDIT))
            {
                if (!createNewPageOnEdit(requestContext))
                {
                    success = false;
                    resultMap.put(REASON, "Insufficient access to edit page");                
                    return success;
                }
                status = "refresh";
            }           
            resultMap.put(STATUS, status);
        } 
        catch (Exception e)
        {
            // Log the exception
            log.error("exception while adding a portlet", e);
            resultMap.put(REASON, e.toString());

            // Return a failure indicator
            success = false;
        }

        return success;
    }
    
}
