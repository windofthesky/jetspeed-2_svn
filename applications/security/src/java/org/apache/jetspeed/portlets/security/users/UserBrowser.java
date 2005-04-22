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
package org.apache.jetspeed.portlets.security.users;

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

import org.apache.jetspeed.portlets.security.SecurityUtil;
import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.portals.gems.browser.BrowserIterator;
import org.apache.portals.gems.browser.BrowserPortlet;
import org.apache.portals.gems.browser.DatabaseBrowserIterator;
import org.apache.portals.gems.util.StatusMessage;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.velocity.context.Context;

/**
 * Role Browser - flat non-hierarchical view
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class UserBrowser extends BrowserPortlet
{
    protected UserManager userManager;

    // view context
    public static final String STATUS = "statusMsg";
    public static final String SELECTED = "selected";

    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        userManager = (UserManager) 
            getPortletContext().getAttribute(SecurityResources.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
    }

    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        String selected = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
        if (selected != null)
        {        
            Context context = this.getContext(request);
            context.put(SELECTED, selected);
        }
        StatusMessage msg = (StatusMessage)PortletMessaging.consume(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_STATUS);
        if (msg != null)
        {
            this.getContext(request).put(STATUS, msg);            
        }
        String refresh = (String)PortletMessaging.consume(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_REFRESH); 
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
            String selected = request.getParameter("user");
            if (selected != null)
            {
                PortletMessaging.publish(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED, selected);
            }
        }
        super.processAction(request, response);
            
    }

    
    
    public void getRows(RenderRequest request, String sql, int windowSize)
    throws Exception
    {
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        try
        {
            Iterator users = userManager.getUsers("");
                        
            
            resultSetTypeList.add(String.valueOf(Types.VARCHAR));
            resultSetTitleList.add("User");

            List list = new ArrayList();
            while (users.hasNext())
            {
                User user = (User)users.next();
                Principal principal = SecurityUtil.getPrincipal(user.getSubject(),
                        UserPrincipal.class);                
                list.add(principal.getName());
            }            
            BrowserIterator iterator = new DatabaseBrowserIterator(
                    list, resultSetTitleList, resultSetTypeList,
                    windowSize);
            setBrowserIterator(request, iterator);
            iterator.sort("User");
        }
        catch (Exception e)
        {
            //log.error("Exception in CMSBrowserAction.getRows: ", e);
            e.printStackTrace();
            throw e;
        }        
    }
    
}
