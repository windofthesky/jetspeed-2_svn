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
package org.apache.jetspeed.portlets.security.rolemgt;

import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.portlets.security.SecurityApplicationResources;
import org.apache.jetspeed.portlets.security.SecurityApplicationUtils;
import org.apache.jetspeed.security.RoleManager;
import org.apache.portals.bridges.myfaces.FacesPortlet;

/**
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class RoleMgtPortlet extends FacesPortlet
{
    /** The logger. */
    private static final Log log = LogFactory.getLog(RoleMgtPortlet.class);
    
    /**
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);

        RoleManager roleMgr = (RoleManager) getPortletContext().getAttribute(SecurityApplicationResources.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleMgr)
        {
            throw new PortletException("Failed to find the role manager on portlet initialization.");
        }
    }
   
    /**
     * @see org.apache.portals.bridges.myfaces.FacesPortlet#preProcessFaces(javax.faces.context.FacesContext)
     */
    protected void preProcessFaces(FacesContext context)
    {
        if (null == context.getExternalContext().getSessionMap().get(SecurityApplicationResources.ROLE_TREE_TABLE))
        {
            context.getExternalContext().getSessionMap().put(SecurityApplicationResources.ROLE_TREE_TABLE, 
                    new RoleTreeTable(SecurityApplicationUtils.buildRoleTreeModel()));
        }
    }

}