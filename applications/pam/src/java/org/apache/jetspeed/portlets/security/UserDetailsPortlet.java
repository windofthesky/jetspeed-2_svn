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
package org.apache.jetspeed.portlets.security;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.portlet.ServletPortlet;
import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.portlets.pam.beans.PortletApplicationBean;
import org.apache.jetspeed.portlets.pam.beans.TabBean;
import org.apache.jetspeed.portlets.security.users.JetspeedUserBean;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;

/**
 * This portlet is a tabbed editor user interface for editing user attributes
 * and security definitions.
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class UserDetailsPortlet extends ServletPortlet
{
    private static final String PORTLET_ACTION = "portlet_action";
    private final String VIEW_USER = "user"; 
    
    private static final String PORTLET_APP_ACTION_PREFIX = "portlet_app.";
    private static final String PORTLET_ACTION_PREFIX = "portlet.";

    private UserManager manager;

    private LinkedHashMap userTabMap = new LinkedHashMap();
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        manager = (UserManager)getPortletContext().getAttribute(PortletApplicationResources.CPS_USER_MANAGER_COMPONENT);
        if (null == manager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        
        TabBean tb1 = new TabBean("user_attributes");
        TabBean tb2 = new TabBean("user_security");
        
        userTabMap.put(tb1.getId(), tb1);
        userTabMap.put(tb2.getId(), tb2);
        
    }
    
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        response.setContentType("text/html");
        
        String userName = (String)
            request.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_USER, 
                                         PortletSession.APPLICATION_SCOPE);

        User user = null;
        if (userName != null)
        {
            // TODO: don't lookup with every view call
            user = lookupUser(userName);
        }
        
        if (user != null)
        {        
            request.setAttribute(VIEW_USER, new JetspeedUserBean(user));
            
            // Tabs
            request.setAttribute("tabs", userTabMap.values());        
            TabBean selectedTab = (TabBean) request.getPortletSession().getAttribute(PortletApplicationResources.REQUEST_SELECT_TAB, PortletSession.APPLICATION_SCOPE);
            if(selectedTab == null)
            {
                selectedTab = (TabBean) userTabMap.values().iterator().next();
            }
                        
            request.setAttribute(PortletApplicationResources.REQUEST_SELECT_TAB, selectedTab);
        }
        
        super.doView(request, response);
    }

    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) 
        throws PortletException, IOException
    {                        
        String selectedTab = actionRequest.getParameter(PortletApplicationResources.REQUEST_SELECT_TAB);
        System.out.println("SELECTED TAB = " + selectedTab);
        if (selectedTab != null)
        {
            TabBean tab = (TabBean) userTabMap.get(selectedTab);
            if (tab != null)
            {
                actionRequest.getPortletSession().setAttribute(
                        PortletApplicationResources.REQUEST_SELECT_TAB, tab);
            }
        }
                
    }    

    private User lookupUser(String userName)
    {
        User user = null;
        try
        {
            user = manager.getUser(userName);
        }
        catch (Exception e)
        {
            // TODO: logging
            System.err.println("user not found: " + userName + ", " + e);
        }    
        return user;
    }
    
}
