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
package org.apache.jetspeed.portlets.security.roles;

import java.io.IOException;
import java.security.Principal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
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
 * @version $Id$
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
    throws Exception
    {
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        try
        {
            List list = new ArrayList();
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("Users in Role");
            
            String selectedRole = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_SELECTED);
            if (selectedRole != null)
            {
                Iterator users = userManager.getUsersInRole(selectedRole).iterator();                                    
                while (users.hasNext())
                {
                    User user = (User)users.next();
                    Principal principal = SecurityUtil.getPrincipal(user.getSubject(),
                            UserPrincipal.class);                
                    list.add(principal.getName());
                }
            }
            BrowserIterator iterator = new DatabaseBrowserIterator(
                    list, resultSetTitleList, resultSetTypeList,
                    windowSize);
            setBrowserIterator(request, iterator);
            iterator.sort("Users in Role");
        }
        catch (Exception e)
        {
            //log.error("Exception in CMSBrowserAction.getRows: ", e);
            e.printStackTrace();
            throw e;
        }        
        
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
        
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, SecurityResources.TOPIC_ROLES_USERS, SecurityResources.MESSAGE_STATUS);
        if (msg != null)
        {
            this.getContext(request).put("statusMsg", msg);            
        }
          
        String refresh = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_ROLES_USERS, SecurityResources.MESSAGE_REFRESH); 
        if (refresh != null)
        {        
            this.clearBrowserIterator(request);
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
                PortletMessaging.publish(actionRequest, 
                        SecurityResources.TOPIC_ROLES, 
                        SecurityResources.MESSAGE_REFRESH, "true");
            }            
            catch (Exception se)
            {
                ResourceBundle bundle = ResourceBundle.getBundle("org.apache.jetspeed.portlets.security.resources.UsersResources",actionRequest.getLocale());                
                SecurityUtil.publishErrorMessage(actionRequest, bundle.getString("user.exists"));
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
                PortletMessaging.publish(actionRequest, 
                        SecurityResources.TOPIC_ROLES, 
                        SecurityResources.MESSAGE_REFRESH, "true");
                PortletMessaging.cancel(actionRequest, SecurityResources.TOPIC_ROLES, SecurityResources.MESSAGE_SELECTED);                                                
            }
            catch (Exception se)
            {
                ResourceBundle bundle = ResourceBundle.getBundle("org.apache.jetspeed.portlets.security.resources.UsersResources",actionRequest.getLocale());                
                SecurityUtil.publishErrorMessage(actionRequest, bundle.getString("user.exists"));
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
                catch (Exception e)
                {
                    System.err.println("failed to add user to role: " + user);
                }
            }
            if (count > 0)
            {
                try
                {
                    PortletMessaging.publish(request, 
                            SecurityResources.TOPIC_ROLES_USERS, 
                            SecurityResources.MESSAGE_REFRESH, "true");
                }
                catch (Exception e)
                {}
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
                    catch (Exception e1)
                    {
                        System.err.println("failed to remove user from role: " + user);
                    }
                    
                }
            }
            if (count > 0)
            {
                try
                {
                    PortletMessaging.publish(request, 
                            SecurityResources.TOPIC_ROLES_USERS, 
                            SecurityResources.MESSAGE_REFRESH, "true");
                }
                catch (Exception e2)
                {}
            }
        }
    }
    
}
