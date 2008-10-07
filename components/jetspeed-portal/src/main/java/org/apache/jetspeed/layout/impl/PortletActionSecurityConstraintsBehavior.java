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
package org.apache.jetspeed.layout.impl;

import java.security.Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;

/**
 * Abstracted behavior of security checks for portlet actions
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PortletActionSecurityConstraintsBehavior 
       extends PortletActionSecurityPathBehavior
       implements PortletActionSecurityBehavior
{
    protected Log log = LogFactory.getLog(PortletActionSecurityConstraintsBehavior.class);    
    protected String guest = "guest";
    
    public PortletActionSecurityConstraintsBehavior(PageManager pageManager)
    {
    	this( pageManager, Boolean.FALSE );
    }
    public PortletActionSecurityConstraintsBehavior(PageManager pageManager, Boolean enableCreateUserPagesFromRolesOnEdit )
    {
        super( pageManager, enableCreateUserPagesFromRolesOnEdit );
        PortalConfiguration config = Jetspeed.getConfiguration();
        if (config != null)
        {
            guest = config.getString("default.user.principal");
        }
    }

    public boolean checkAccess(RequestContext context, String action)
    {
        Page page = context.getPage();
        try
        {
            page.checkAccess(action);            
        }
        catch (Exception e)
        {
            Principal principal = context.getRequest().getUserPrincipal();
            String userName = this.guest;
            if (principal != null)
                userName = principal.getName();
            log.warn("Insufficient access to page " + page.getPath() + " by user " + userName);
            return false;
        }     
        return true;
    }
}
