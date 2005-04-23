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
import java.util.Iterator;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.portals.gems.browser.BrowserIterator;
import org.apache.portals.gems.browser.DatabaseBrowserIterator;
import org.apache.portals.gems.browser.BrowserPortlet;
import org.apache.portals.gems.util.StatusMessage;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

/**
 * Group Browser - flat non-hierarchical view
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class GroupBrowser extends BrowserPortlet
{
    private GroupManager groupManager;
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        groupManager = (GroupManager) 
            getPortletContext().getAttribute(SecurityResources.CPS_GROUP_MANAGER_COMPONENT);
        if (null == groupManager)
        {
            throw new PortletException("Failed to find the Group Manager on portlet initialization");
        }
    }
           
    public void getRows(RenderRequest request, String sql, int windowSize)
    throws Exception
    {
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        try
        {
            Iterator groups = groupManager.getGroups("");
                        
            
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("Group");

            List list = new ArrayList();
            while (groups.hasNext())
            {
                Group group = (Group)groups.next();
                
                Principal principal = group.getPrincipal();                
                list.add(principal.getName());
            }            
            
            BrowserIterator iterator = new DatabaseBrowserIterator(
                    list, resultSetTitleList, resultSetTypeList,
                    windowSize);
            setBrowserIterator(request, iterator);
            iterator.sort("Group");
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
        String selected = (String)PortletMessaging.receive(request, "group", "selected");
        if (selected != null)
        {        
            Context context = this.getContext(request);
            context.put("selected", selected);
        }
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, "GroupBrowser", "status");
        if (msg != null)
        {
            this.getContext(request).put("statusMsg", msg);            
        }
        
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse response)
    throws PortletException, IOException
    {
        if (request.getPortletMode() == PortletMode.VIEW)
        {
            String selected = request.getParameter("group");
            if (selected != null)
            {
                Group group = lookupGroup(selected);
                if (group != null)
                {
                    PortletMessaging.publish(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_SELECTED, selected);
                    PortletMessaging.publish(request, SecurityResources.TOPIC_GROUPS, SecurityResources.MESSAGE_CHANGED, selected);
                }
            }
            String refresh = request.getParameter("group.refresh");
            String save = request.getParameter("group.save");
            String neue = request.getParameter("group.new");
            String delete = request.getParameter("groupDelete");
            
            if (refresh != null)
            {
                this.clearBrowserIterator(request);
            }
            else if (neue != null)
            {
                PortletMessaging.cancel(request, "group", "selected");
            }
            else if (delete != null && (!(isEmpty(delete))))
            {
                try
                {
                    Group group = lookupGroup(delete);
                    if (group != null)
                    {
                        groupManager.removeGroup(delete);
                        this.clearBrowserIterator(request);
                        PortletMessaging.cancel(request, "group", "selected");
                        PortletMessaging.publish(request, SecurityResources.TOPIC_USERS, "groups", "refresh");
                    }
                }
                catch (Exception e)
                {
                    publishStatusMessage(request, "GroupBrowser", "status", e, "Could not remove group");
                }
            }
            else if (save != null)
            {
                String groupName = request.getParameter("group.name");                
                if (!(isEmpty(groupName)))
                {
                    try
                    {
                        Group group = null;
                        String old = (String)PortletMessaging.receive(request, "group", "selected");
                        if (old != null)
                        {
                            group = lookupGroup(old);
                        }
                        else
                        {
                            group = lookupGroup(groupName);
                        }                        
                        if (group != null)
                        {
                            if (old != null && !old.equals(groupName))
                            {
                                groupManager.removeGroup(old);
                                groupManager.addGroup(groupName);                            
                                this.clearBrowserIterator(request);
                                PortletMessaging.publish(request, "group", "selected", groupName);
                            }
                        }
                        else
                        {
                            groupManager.addGroup(groupName);
                            this.clearBrowserIterator(request);
                        }
                        PortletMessaging.publish(request, SecurityResources.TOPIC_USERS, "groups", "refresh");
                    }
                    catch (Exception e)
                    {
                        publishStatusMessage(request, "GroupBrowser", "status", e, "Could not store group");
                    }
                }
            }            
        }
        super.processAction(request, response);
            
    }

    private Group lookupGroup(String groupName)
    {
        try
        {
            return groupManager.getGroup(groupName);
        }
        catch (SecurityException e)
        {
            return null;
        }
    }
    
    private boolean isEmpty(String s)
    {
        if (s == null) return true;
        
        if (s.trim().equals("")) return true;
        
        return false;
    }
    
}
