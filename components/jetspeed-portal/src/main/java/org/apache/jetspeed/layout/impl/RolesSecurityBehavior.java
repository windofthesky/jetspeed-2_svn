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

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.request.RequestContext;

/**
 * Abstracted behavior of security checks for portlet actions
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class RolesSecurityBehavior implements PortletActionSecurityBehavior
{
    protected Logger log = LoggerFactory.getLogger(PortletActionSecurityPathBehavior.class);    
    protected List roles;
    
    public RolesSecurityBehavior(List roles)
    {
        this.roles = roles;
    }

    public boolean checkAccess(RequestContext context, String action)
    {
        Iterator iterator = roles.iterator();
        while (iterator.hasNext())
        {
            String role = (String)iterator.next();
            if (context.getRequest().isUserInRole(role))
                return true;
        }        
        return false;
    }
    
    public boolean isCreateNewPageOnEditEnabled()
    {
    	return false;
    }
    public boolean isPageQualifiedForCreateNewPageOnEdit(RequestContext context)
    {
    	return false ;
    }

    public boolean createNewPageOnEdit(RequestContext context)
    {
        return false;
    }
}
