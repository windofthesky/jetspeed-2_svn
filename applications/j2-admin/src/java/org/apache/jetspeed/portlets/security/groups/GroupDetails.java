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
package org.apache.jetspeed.portlets.security.groups;

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
import org.apache.jetspeed.security.GroupManager;
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
 * Group Details
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: GroupDetails.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class GroupDetails extends BrowserPortlet
{
    private UserManager userManager;
    private GroupManager groupManager;
        
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        userManager = (UserManager) getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        groupManager = (GroupManager) getPortletContext().getAttribute(CommonPortletServices.CPS_GROUP_MANAGER_COMPONENT);
        if (null == groupManager)
        {
            throw new PortletException("Failed to find the Group Manager on portlet initialization");
        }        
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
        
        try
        {
            List list = new ArrayList();
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("Users in Group");
            
            String selectedGroup = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED);
            if (selectedGroup != null)
            {
                Iterator users = userManager.getUsersInGroup(selectedGroup).iterator();                                    
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
            BrowserIterator iterator = new DatabaseBrowserIterator(
                    list, resultSetTitleList, resultSetTypeList,
                    windowSize);
            setBrowserIterator(request, iterator);
            iterator.sort("Users in Group");
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
        String change = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_CHANGED);
        if (change != null)
        { 
            this.clearBrowserIterator(request);
        }
        Context context = this.getContext(request);
                
        String selectedGroup = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED);
        if (selectedGroup != null)
        {        
            context.put("group", selectedGroup);
        }        
        
        String userChooser = SecurityUtil.getAbsoluteUrl(request, "/Administrative/choosers/multiusers.psml");        
        context.put("userChooser", userChooser);
        
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, SecurityResources.TOPIC_GROUPS_USERS, SecurityResources.MESSAGE_STATUS);
        if (msg != null)
        {
            this.getContext(request).put("statusMsg", msg);            
        }
          
        String filtered = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_FILTERED);
        if (filtered != null)
        {
            this.getContext(request).put(FILTERED, "on");            
        }        

        String refresh = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_GROUPS_USERS, SecurityResources.MESSAGE_REFRESH); 
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
            
            System.out.println("users = " + users);
            if (users != null && users.length() > 0)
            {
                addUsersToGroup(request, users);
            }
            else if (request.getParameter("group.action.Add_New_Group") != null)
            {
                PortletMessaging.cancel(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED);                
            }
            else if (request.getParameter("group.action.Remove_Checked_Users") != null)
            {
                removeUsersFromGroup(request);
            }
            else if (request.getParameter("group.action.Remove_Group") != null)
            {
                removeGroup(request);
            }
            else if (request.getParameter("group.action.Save") != null)
            {
                addGroup(request);
            }
            
        }
        super.processAction(request, response);            
    }

    protected void addGroup(ActionRequest actionRequest)
    {
        String group = actionRequest.getParameter("group");
        if (!SecurityUtil.isEmpty(group)) 
        {
            try
            {            
                groupManager.addGroup(group);
                PortletMessaging.publish(actionRequest, 
                        SecurityResources.TOPIC_GROUPS, 
                        SecurityResources.MESSAGE_REFRESH, "true");
            }            
            catch (Exception se)
            {
                ResourceBundle bundle = ResourceBundle.getBundle("org.apache.jetspeed.portlets.security.resources.UsersResources",actionRequest.getLocale());                
                SecurityUtil.publishErrorMessage(actionRequest, bundle.getString("user.exists"));
            }
        }
    }

    protected void removeGroup(ActionRequest actionRequest)
    {
        String group = actionRequest.getParameter("group");
        if (!SecurityUtil.isEmpty(group)) 
        {
            try
            {            
                groupManager.removeGroup(group);
                PortletMessaging.publish(actionRequest, 
                        SecurityResources.TOPIC_GROUPS, 
                        SecurityResources.MESSAGE_REFRESH, "true");
                PortletMessaging.cancel(actionRequest, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED);                                                
            }
            catch (Exception se)
            {
                ResourceBundle bundle = ResourceBundle.getBundle("org.apache.jetspeed.portlets.security.resources.UsersResources",actionRequest.getLocale());                
                SecurityUtil.publishErrorMessage(actionRequest, bundle.getString("user.exists"));
            }
        }
    }
    
    protected void addUsersToGroup(ActionRequest request, String users)
    {
        String group = request.getParameter("group");
        if (group != null)
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
                        groupManager.addUserToGroup(user, group);
                        count++;
                    }
                }
                catch (Exception e)
                {
                    System.err.println("failed to add user to group: " + user);
                }
            }
            if (count > 0)
            {
                try
                {
                    PortletMessaging.publish(request, 
                            SecurityResources.TOPIC_GROUPS_USERS, 
                            SecurityResources.MESSAGE_REFRESH, "true");
                }
                catch (Exception e)
                {}
            }
        }
    }

    protected void removeUsersFromGroup(ActionRequest request)
    {
        String group = request.getParameter("group");
        if (group != null)
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
                        groupManager.removeUserFromGroup(user, group);                        
                        count++;
                    }
                    catch (Exception e1)
                    {
                        System.err.println("failed to remove user from group: " + user);
                    }
                    
                }
            }
            if (count > 0)
            {
                try
                {
                    PortletMessaging.publish(request, 
                            SecurityResources.TOPIC_GROUPS_USERS, 
                            SecurityResources.MESSAGE_REFRESH, "true");
                }
                catch (Exception e2)
                {}
            }
        }
    }
    
}
