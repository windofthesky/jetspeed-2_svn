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
package org.apache.jetspeed.security.impl;


import java.security.AccessControlException;
import java.security.AccessController;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.security.PortletPermission;
import org.apache.jetspeed.security.SecurityAccessController;

/**
 * SecurityAccessorImpl implements SecurityAccessor component abstracting
 * access to either Security Permission or Security Constraint implementations
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class SecurityAccessControllerImpl implements SecurityAccessController
{
    protected PageManager pageManager;
    protected int securityMode = SecurityAccessController.PERMISSIONS;
    
    public SecurityAccessControllerImpl(PageManager pageManager, int securityMode)
    {
        this.pageManager = pageManager;
        this.securityMode = securityMode;
    }
    
    public int getSecurityMode()
    {
        return securityMode;
    }
    
    public boolean checkPortletAccess(PortletDefinitionComposite portlet, int mask)
    {
        if (portlet == null)
            return false;
        if (securityMode == SecurityAccessController.CONSTRAINTS)
        {
            String constraintRef = portlet.getJetspeedSecurityConstraint();
            if (constraintRef == null)
            {
                constraintRef = ((MutablePortletApplication)portlet.getPortletApplicationDefinition()).getJetspeedSecurityConstraint();                
                if (constraintRef == null)
                {
                    return true; // allow access
                }
            }
            String actions = JetspeedActions.getContainerActions(mask);
            return pageManager.checkConstraint(constraintRef, actions);                
        }
        else
        {
            try
            {
                AccessController.checkPermission(new PortletPermission(portlet.getUniqueName(), mask));
            }
            catch (AccessControlException ace)
            {
                return false;
            }
            return true;
        }
    
    }
}
