/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.portlets.security.roles;

import java.io.IOException;
import java.security.Principal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.portlets.security.SecurityUtil;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.portals.gems.browser.BrowserIterator;
import org.apache.portals.gems.browser.DatabaseBrowserIterator;
import org.apache.portals.gems.browser.BrowserPortlet;
import org.apache.portals.gems.util.StatusMessage;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

/**
 * Role Browser - flat non-hierarchical view
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: RoleBrowser.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class RoleBrowser extends BrowserPortlet
{
    private RoleManager roleManager;
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        roleManager = (RoleManager) 
            getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager)
        {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }
    }
           
    public void getRows(RenderRequest request, String sql, int windowSize)
    {
        getRows(request, sql, windowSize, "");
    }
    
    public void getRows(RenderRequest request, String sql, int windowSize, String filter)
    {
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        resultSetTypeList.add(String.valueOf(Types.VARCHAR));
        resultSetTitleList.add("role"); // resource bundle key

        List list = new ArrayList();
        try
        {
            Iterator roles = roleManager.getRoles(filter);
            
            while (roles.hasNext())
            {
                Role role = (Role)roles.next();
                
                Principal principal = role.getPrincipal();                
                list.add(principal.getName());
            }                        
        }
        catch (SecurityException sex)
        {
            SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_ROLES, sex.getMessage());
        }                                    
        BrowserIterator iterator = new DatabaseBrowserIterator(list, resultSetTitleList, resultSetTypeList,windowSize);
        setBrowserIterator(request, iterator);
        iterator.sort("role"); // resource bundle key
    }
       
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        String selected = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_SELECTED);
        if (selected != null)
        {        
            Context context = this.getContext(request);
            context.put("selected", selected);
        }
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_STATUS);
        if (msg != null)
        {
            this.getContext(request).put("statusMsg", msg);            
        }
        
        String filtered = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_FILTERED);
        if (filtered != null)
        {
            this.getContext(request).put(FILTERED, "on");            
        }

        String refresh = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_REFRESH); 
        if (refresh != null)
        {        
            this.clearBrowserIterator(request);
        }                
                
        ArrayList errorMessages = (ArrayList)PortletMessaging.consume(request, SecurityResources.TOPIC_ROLES, SecurityResources.ERROR_MESSAGES);
        if (errorMessages != null )
        {
            this.getContext(request).put(SecurityResources.ERROR_MESSAGES, errorMessages);
        }
        
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse response)
    throws PortletException, IOException
    {
        if (request.getPortletMode() == PortletMode.VIEW)
        {
            String selected = request.getParameter("role");
            if (selected != null)
            {
                Role role = lookupRole(request, selected);
                if (role != null)
                {
                    PortletMessaging.publish(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_SELECTED, selected);
                    PortletMessaging.publish(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_CHANGED, selected);
                }
            }
        }
        
        String filtered = request.getParameter(FILTERED);
        if (filtered != null)
        {
            PortletMessaging.publish(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_FILTERED, "on");            
        }
        else
        {
            PortletMessaging.cancel(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_FILTERED);
        }
        
        super.processAction(request, response);
            
    }

    private Role lookupRole(ActionRequest actionRequest, String roleName)
    {
        try
        {
            return roleManager.getRole(roleName);
        }
        catch (SecurityException sex)
        {
            SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_ROLES, sex.getMessage());
            return null;
        }
    }
}