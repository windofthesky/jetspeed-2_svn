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
package org.apache.jetspeed.layout.impl;

import java.security.Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    
    public PortletActionSecurityConstraintsBehavior(PageManager pageManager)
    {
        super(pageManager);
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
            String userName = "guest";
            if (principal != null)
                userName = principal.getName();
            log.warn("Insufficient access to page " + page.getPath() + " by user " + userName);
            return false;
        }     
        return true;
    }
}
