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
import java.io.NotSerializableException;
import java.security.Principal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

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
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.portals.gems.browser.BrowserIterator;
import org.apache.portals.gems.browser.DatabaseBrowserIterator;
import org.apache.portals.gems.browser.BrowserPortlet;
import org.apache.portals.gems.util.StatusMessage;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

/**
 * Role Details
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: RoleDetails.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class RoleDetails extends BrowserPortlet
{
    private UserManager userManager;
    private RoleManager roleManager;
        
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        roleManager = (RoleManager) getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager)
        {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }        
    }
    
    public void getRows(RenderRequest request, String sql, int windowSize)
    {
        getRows(request, sql, windowSize, null);        
    }

    public void getRows(RenderRequest request, String sql, int windowSize, String filter)
    {
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        if ( filter != null )
        {
            if ( filter.length() == 0 )
            {
                filter = null;
            }
            else
            {
                filter = filter.toLowerCase();
            }
        }
        
        List list = new ArrayList();
        resultSetTypeList.add(String.valueOf(Types.VARCHAR));
        resultSetTitleList.add("usersinrole"); // resource bundle key
        
        String selectedRole = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_SELECTED);
        if (selectedRole != null)
        {
            try
            {
                Iterator users = userManager.getUsersInRole(selectedRole).iterator();                                    
                while (users.hasNext())
                {
                    User user = (User)users.next();
                    Principal principal = SecurityUtil.getPrincipal(user.getSubject(),
                            UserPrincipal.class);
                    if ( filter == null || principal.getName().toLowerCase().startsWith(filter))
                    {
                        list.add(principal.getName());
                    }
                }
            } 
            catch (SecurityException sex)
            {
                SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_ROLE, sex.getMessage());
            }                                    
        }
        BrowserIterator iterator = new DatabaseBrowserIterator(list, resultSetTitleList, resultSetTypeList, windowSize);
        setBrowserIterator(request, iterator);
        iterator.sort("usersinrole"); // resource bundle key
    }
           
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        String change = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_CHANGED);
        if (change != null)
        { 
            this.clearBrowserIterator(request);
        }
        Context context = this.getContext(request);
                
        String selectedRole = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_SELECTED);
        if (selectedRole != null)
        {        
            context.put("role", selectedRole);
        }        
        
        String userChooser = SecurityUtil.getAbsoluteUrl(request, "/Administrative/choosers/multiusers.psml");        
        context.put("userChooser", userChooser);
        
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, SecurityResources.TOPIC_ROLE, SecurityResources.MESSAGE_STATUS);
        if (msg != null)
        {
            this.getContext(request).put("statusMsg", msg);            
        }
          
        String filtered = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_ROLE, SecurityResources.MESSAGE_FILTERED);
        if (filtered != null)
        {
            this.getContext(request).put(FILTERED, "on");            
        }

        String refresh = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_ROLE, SecurityResources.MESSAGE_REFRESH); 
        if (refresh != null)
        {        
            this.clearBrowserIterator(request);
        }                
        
        ArrayList errorMessages = (ArrayList)PortletMessaging.consume(request, SecurityResources.TOPIC_ROLE, SecurityResources.ERROR_MESSAGES);
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
            String users = request.getParameter("users");
            
            if (users != null && users.length() > 0)
            {
                addUsersToRole(request, users);
            }
            else if (request.getParameter("role.action.Add_New_Role") != null)
            {
                PortletMessaging.cancel(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_SELECTED);                
            }
            else if (request.getParameter("role.action.Remove_Checked_Users") != null)
            {
                removeUsersFromRole(request);
            }
            else if (request.getParameter("role.action.Remove_Role") != null)
            {
                removeRole(request);
            }
            else if (request.getParameter("role.action.Save") != null)
            {
                addRole(request);
            }

            if (request.getParameter(FILTERED) != null )
            {
                PortletMessaging.publish(request, SecurityResources.TOPIC_ROLE, SecurityResources.MESSAGE_FILTERED, "on");            
            }
            else
            {
                PortletMessaging.cancel(request, SecurityResources.TOPIC_ROLE, SecurityResources.MESSAGE_FILTERED);                    
            }            
        }
        super.processAction(request, response);            
    }

    protected void addRole(ActionRequest actionRequest)
    {
        String role = actionRequest.getParameter("role");
        if (!SecurityUtil.isEmpty(role)) 
        {
            try
            {            
                roleManager.addRole(role);
                PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_REFRESH, "true");
                PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_SELECTED, role);
                PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_CHANGED, role);
            }            
            catch (SecurityException sex)
            {
                SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_ROLE, sex.getMessage());
            }
            catch (NotSerializableException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void removeRole(ActionRequest actionRequest)
    {
        String role = actionRequest.getParameter("role");
        if (!SecurityUtil.isEmpty(role)) 
        {
            try
            {            
                roleManager.removeRole(role);
                try
                {
                PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_REFRESH, "true");
                }
                catch (NotSerializableException e)
                {
                    e.printStackTrace();
                }
                PortletMessaging.cancel(actionRequest, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_SELECTED);                                                
            }
            catch (SecurityException sex)
            {
                SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_ROLE, sex.getMessage());
            } 
        }
    }
    
    protected void addUsersToRole(ActionRequest request, String users)
    {
        String role = request.getParameter("role");
        if (role != null)
        {
            int count = 0;
            StringTokenizer tokenizer = new StringTokenizer(users, ",");
            while (tokenizer.hasMoreTokens())
            {
                String user = tokenizer.nextToken();
                try
                {
                    if (user.startsWith("box_"))
                    {
                        user = user.substring("box_".length());
                        roleManager.addRoleToUser(user, role);
                        count++;
                    }
                }
                catch (SecurityException sex)
                {
                    SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_ROLE, sex.getMessage());
                }
            }
            if (count > 0)
            {
                try
                {
                    PortletMessaging.publish(request, SecurityResources.TOPIC_ROLE, SecurityResources.MESSAGE_REFRESH, "true");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void removeUsersFromRole(ActionRequest request)
    {
        String role = request.getParameter("role");
        if (role != null)
        {
            int count = 0;
            Enumeration e = request.getParameterNames();
            while (e.hasMoreElements())
            {
                String name = (String)e.nextElement();
                if (name.startsWith("box_"))
                {
                    String user = name.substring("box_".length());
                    try
                    {
                        roleManager.removeRoleFromUser(user, role);
                        count++;
                    }
                    catch (SecurityException sex)
                    {
                        SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_ROLE, sex.getMessage());
                    }
                }
            }
            if (count > 0)
            {
                try
                {
                    PortletMessaging.publish(request, SecurityResources.TOPIC_ROLE, SecurityResources.MESSAGE_REFRESH, "true");
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
    
}
