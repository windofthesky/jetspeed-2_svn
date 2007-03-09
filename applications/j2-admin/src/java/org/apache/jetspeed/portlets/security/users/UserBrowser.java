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
import javax.security.auth.Subject;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.portlets.security.SecurityUtil;
import org.apache.jetspeed.security.SecurityException;
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
 * @version $Id: UserBrowser.java 348264 2005-11-22 22:06:45Z taylor $
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
            getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
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
        
        String filtered = (String)PortletMessaging.receive(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_FILTERED);
        if (filtered != null)
        {
            this.getContext(request).put(FILTERED, "on");            
        }
        
        ArrayList errorMessages = (ArrayList)PortletMessaging.consume(request, SecurityResources.TOPIC_USERS, SecurityResources.ERROR_MESSAGES);
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
            String selected = request.getParameter("user");
            if (selected != null)
            {
                PortletMessaging.publish(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED, selected);
            }
        }
        
        // TODO: if request parameters were working correctly we could replace this with render parameters
        String filtered = (String)request.getParameter(FILTERED);
        if (filtered != null)
        {
            PortletMessaging.publish(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_FILTERED, "on");            
        }
        else
        {
            PortletMessaging.cancel(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_FILTERED);
        }
        
        super.processAction(request, response);            
    }
      
    public void getRows(RenderRequest request, String sql, int windowSize)
    {
        getRows(request, sql, windowSize, "");
    }

    public void getRows(RenderRequest request, String sql, int windowSize, String filter)
    {
        String roleFilter = request.getPreferences().getValue("FilterByRole", "");
        boolean filterByRole = !roleFilter.equals("") || roleFilter.equalsIgnoreCase("false");
        List resultSetTitleList = new ArrayList();
        List resultSetTypeList = new ArrayList();
        resultSetTypeList.add(String.valueOf(Types.VARCHAR));
        resultSetTitleList.add("user"); // resource bundle key

        List list = new ArrayList();
        try
        {
            if (filterByRole)
            {
                Iterator users = userManager.getUsersInRole(roleFilter).iterator();
                while (users.hasNext())
                {
                    // NOTE: this can be a bit costly if you have a lot of users in a role
                    User user = (User)users.next();
                    Principal pr = getBestPrincipal(user.getSubject(), UserPrincipal.class);
                    list.add(pr.getName());
                }                            
            }
            else
            {
                Iterator users = userManager.getUserNames(filter);
                while (users.hasNext())
                {
                    list.add(users.next());
                }            
                
            }                            
        }
        catch (SecurityException sex)
        {
            SecurityUtil.publishErrorMessage(request, SecurityResources.TOPIC_USERS, sex.getMessage());
        }                                    
        BrowserIterator iterator = new DatabaseBrowserIterator(list, resultSetTitleList, resultSetTypeList, windowSize);
        setBrowserIterator(request, iterator);
        iterator.sort("user"); // resource bundle key        
    }    
    
    public static Principal getBestPrincipal(Subject subject, Class classe)
    {

        Principal principal = null;
        Iterator principals = subject.getPrincipals().iterator();
        while (principals.hasNext())
        {
            Principal p = (Principal) principals.next();
            if (classe.isInstance(p))
            {
                principal = p;
                break;
            }
            else
            {
                if (principal == null)
                {
                    principal = p;
                }
            }
        }
        return principal;
    }    
}
