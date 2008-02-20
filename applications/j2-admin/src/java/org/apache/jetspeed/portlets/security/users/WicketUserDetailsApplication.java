/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.portlets.security.users;

import javax.portlet.PortletRequest;

import org.apache.wicket.Request;
import org.apache.wicket.RequestContext;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.portlet.PortletRequestContext;

import org.apache.portals.messaging.PortletMessaging;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.portlets.security.SecurityUtil;

import org.apache.jetspeed.portlets.wicket.AbstractAdminWebApplication;

/**
 * User Details Wicket Application
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: $
 */
public class WicketUserDetailsApplication extends AbstractAdminWebApplication
{

	/**
	 * @see org.apache.wicket.Application#getHomePage()
	 */
	public Class getHomePage()
	{
        PortletRequest request = ((PortletRequestContext) RequestContext.get()).getPortletRequest();
        String userName = (String) PortletMessaging.receive(request, SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);

        if (userName != null)
        {
            try
            {
                UserManager userManager = (UserManager) request.getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
                User user = userManager.getUser(userName);
                
                if (user != null)
                {
                    return WicketUserDetails.class;
                }
            }
            catch (Exception e)
            {
            }
        }
        
		return WicketUserAdd.class;
	}
    
}
