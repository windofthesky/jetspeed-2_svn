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
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.Role;
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
 * Group Details
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: GroupDetails.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class GroupDetails extends BrowserPortlet
{
    private UserManager userManager;
    private RoleManager roleManager;
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
        roleManager = (RoleManager) getPortletContext().getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager)
        {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }
        groupManager = (GroupManager) getPortletContext().getAttribute(CommonPortletServices.CPS_GROUP_MANAGER_COMPONENT);
        if (null == groupManager)
        {
            throw new PortletException("Failed to find the Group Manager on portlet initialization");
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
        
        String groupTab = request.getParameter("groupTab");
        if ( groupTab == null )
        {
            groupTab = "users";
        }            
        if ("users".equals(groupTab))
        {
            resultSetTitleList.add("usersingroup"); // resource bundle key
            
            String selectedGroup = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED);
            if (selectedGroup != null)
            {
                try
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
                catch (SecurityException sex)
                {
                    SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_GROUP, sex.getMessage());
                }                                    
                
            }
            BrowserIterator iterator = new DatabaseBrowserIterator(list, resultSetTitleList, resultSetTypeList, windowSize);
            setBrowserIterator(request, iterator);
            iterator.sort("usersingroup"); // resource bundle key
        }
        else
        {
            resultSetTitleList.add("rolesingroup"); // resource bundle key
            
            String selectedGroup = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED);
            if (selectedGroup != null)
            {
                try
                {
                    Iterator roles = roleManager.getRolesInGroup(selectedGroup).iterator();                                    
                    while (roles.hasNext())
                    {
                        String roleName = ((Role)roles.next()).getPrincipal().getName();
                        if ( filter == null || roleName.toLowerCase().startsWith(filter))
                        {
                            list.add(roleName);
                        }
                    }
                } 
                catch (SecurityException sex)
                {
                    SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_GROUP, sex.getMessage());
                }                                    
            }
            BrowserIterator iterator = new DatabaseBrowserIterator(list, resultSetTitleList, resultSetTypeList, windowSize);
            setBrowserIterator(request, iterator);
            iterator.sort("usersingroup"); // resource bundle key
        }
    }
           
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        String change = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_CHANGED);
        if (change != null)
        { 
            this.clearBrowserIterator(request);
            PortletMessaging.cancel(request, SecurityResources.TOPIC_GROUP, SecurityResources.MESSAGE_FILTERED);
        }

        Context context = this.getContext(request);
                
        String selectedGroup = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED);
        if (selectedGroup != null)
        {        
            context.put("group", selectedGroup);
        }        
        String groupTab = request.getParameter("groupTab");
        if ( groupTab == null )
        {
            groupTab = "users";
        }
        context.put("groupTab",groupTab);
        
        if ("users".equals(groupTab))
        {
            String popupChooser = SecurityUtil.getAbsoluteUrl(request, "/Administrative/choosers/multiusers.psml");        
            context.put("popupChooser", popupChooser);            
        }
        else
        {
            String popupChooser = SecurityUtil.getAbsoluteUrl(request, "/Administrative/choosers/multiroles.psml");        
            context.put("popupChooser", popupChooser);
        }
        
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, SecurityResources.TOPIC_GROUP, SecurityResources.MESSAGE_STATUS);
        if (msg != null)
        {
            this.getContext(request).put("statusMsg", msg);            
        }
          
        String filtered = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_GROUP, SecurityResources.MESSAGE_FILTERED);
        if (filtered != null)
        {
            this.getContext(request).put(FILTERED, "on");            
        }        
        
        String refresh = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_GROUP, SecurityResources.MESSAGE_REFRESH); 
        if (refresh != null)
        {        
            this.clearBrowserIterator(request);
        }                

        ArrayList errorMessages = (ArrayList)PortletMessaging.consume(request, SecurityResources.TOPIC_GROUP, SecurityResources.ERROR_MESSAGES);
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
            String groupTab = request.getParameter("groupTab");
            if ( groupTab == null )
            {
                PortletMessaging.publish(request, SecurityResources.TOPIC_GROUP, SecurityResources.MESSAGE_REFRESH, "true");                        
                PortletMessaging.cancel(request, SecurityResources.TOPIC_GROUP, SecurityResources.MESSAGE_FILTERED);
                groupTab = request.getParameter("switchTab");
                if ( groupTab == null )
                {
                    // should never happen
                    groupTab = "users";
                }
            }
            response.setRenderParameter("groupTab",groupTab);

            
            if (request.getParameter("group.action.Save") != null)
            {
                addGroup(request);
            }
            else if (request.getParameter("group.action.Add_New_Group") != null)
            {
                PortletMessaging.cancel(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED);                
            }
            else if (request.getParameter("group.action.Remove_Group") != null)
            {
                removeGroup(request);
            }
            else if ("users".equals(groupTab))
            {
                String users = request.getParameter("users");
                
                if (users != null && users.length() > 0)
                {
                    addUsersToGroup(request, users);
                }
                else if (request.getParameter("group.action.Remove_Checked_Users") != null)
                {
                    removeUsersFromGroup(request);
                }
            }
            else
            {
                String roles = request.getParameter("roles");
                
                if (roles != null && roles.length() > 0)
                {
                    addRolesToGroup(request, roles);
                }
                else if (request.getParameter("group.action.Remove_Checked_Roles") != null)
                {
                    removeRolesFromGroup(request);
                }
            }
            
            if (request.getParameter(FILTERED) != null )
            {
                PortletMessaging.publish(request, SecurityResources.TOPIC_GROUP, SecurityResources.MESSAGE_FILTERED, "on");            
            }
            else
            {
                PortletMessaging.cancel(request, SecurityResources.TOPIC_GROUP, SecurityResources.MESSAGE_FILTERED);                    
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
                PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_REFRESH, "true");
                PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED, group);
                PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_CHANGED, group);
            } 
            catch (NotSerializableException e)
            {
                e.printStackTrace();
            }
            catch (SecurityException sex)
            {
                SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_GROUP, sex.getMessage());
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
                try
                {
                    PortletMessaging.publish(actionRequest, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_REFRESH, "true");
                } 
                catch (NotSerializableException e)
                {
                    e.printStackTrace();
                }
                PortletMessaging.cancel(actionRequest, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED);                                                
            }
            catch (SecurityException sex)
            {
                SecurityUtil.publishErrorMessage(actionRequest, SecurityResources.TOPIC_GROUP, sex.getMessage());
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
                catch (SecurityException sex)
                {
                    SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_GROUP, sex.getMessage());
                }
            }
            if (count > 0)
            {
                try
                {
                    PortletMessaging.publish(request, SecurityResources.TOPIC_GROUP, SecurityResources.MESSAGE_REFRESH, "true");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
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
                    catch (SecurityException sex)
                    {
                        SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_GROUP, sex.getMessage());
                    }
                    
                }
            }
            if (count > 0)
            {
                try
                {
                    PortletMessaging.publish(request, SecurityResources.TOPIC_GROUP, SecurityResources.MESSAGE_REFRESH, "true");
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    protected void addRolesToGroup(ActionRequest request, String roles)
    {
        String group = request.getParameter("group");
        if (group != null)
        {
            int count = 0;
            StringTokenizer tokenizer = new StringTokenizer(roles, ",");
            while (tokenizer.hasMoreTokens())
            {
                String role = tokenizer.nextToken();
                try
                {
                    if (role.startsWith("box_"))
                    {
                        role = role.substring("box_".length());
                        roleManager.addRoleToGroup(role, group);
                        count++;
                    }
                }
                catch (SecurityException sex)
                {
                    SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_GROUP, sex.getMessage());
                }
            }
            if (count > 0)
            {
                try
                {
                    PortletMessaging.publish(request, SecurityResources.TOPIC_GROUP, SecurityResources.MESSAGE_REFRESH, "true");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void removeRolesFromGroup(ActionRequest request)
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
                    String role = name.substring("box_".length());
                    try
                    {
                        roleManager.removeRoleFromGroup(role, group);                        
                        count++;
                    }
                    catch (SecurityException sex)
                    {
                        SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_GROUP, sex.getMessage());
                    }
                    
                }
            }
            if (count > 0)
            {
                try
                {
                    PortletMessaging.publish(request, SecurityResources.TOPIC_GROUP, SecurityResources.MESSAGE_REFRESH, "true");
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }
}
