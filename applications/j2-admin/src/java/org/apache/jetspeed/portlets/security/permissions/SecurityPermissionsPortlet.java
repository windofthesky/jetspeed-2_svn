/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.security.permissions;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.om.InternalPermission;
import org.apache.jetspeed.security.om.InternalPrincipal;
import org.apache.portals.gems.dojo.AbstractDojoVelocityPortlet;
import org.apache.velocity.context.Context;

/**
 * Security Permissions Portlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class SecurityPermissionsPortlet extends AbstractDojoVelocityPortlet
{
    protected final Log logger = LogFactory.getLog(this.getClass());
    protected PermissionManager pm = null;
    protected RoleManager rm = null;
    
    // TODO: move to prefs
    static final String CLASSNAMES[] = 
    {
        "org.apache.jetspeed.security.FolderPermission",
        "org.apache.jetspeed.security.PagePermission",
        "org.apache.jetspeed.security.PortletPermission"
    };
    static final String TITLES[] = 
    {
        "Folders",
        "Pages",
        "Portlets"
    };
    
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        PortletContext context = getPortletContext();
        pm = (PermissionManager) context
                .getAttribute(CommonPortletServices.CPS_PERMISSION_MANAGER);
        if (pm == null)
                throw new PortletException(
                        "Could not get instance of portal permission manager component");
        rm = (RoleManager) context
                .getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (rm == null)
            throw new PortletException(
                "Could not get instance of portal role manager component");        
    }

    protected void includeDojoRequires(StringBuffer headerInfoText)
    {
        appendHeaderText(headerInfoText, "dojo.lang.*");
        appendHeaderText(headerInfoText, "dojo.event.*");
        appendHeaderText(headerInfoText, "dojo.io");
        appendHeaderText(headerInfoText, "dojo.widget.LayoutContainer");
        appendHeaderText(headerInfoText, "dojo.widget.ContentPane");
        appendHeaderText(headerInfoText, "dojo.widget.LinkPane");
        appendHeaderText(headerInfoText, "dojo.widget.SplitContainer");
        appendHeaderText(headerInfoText, "dojo.widget.TabContainer");
        appendHeaderText(headerInfoText, "dojo.widget.Tree");
        appendHeaderText(headerInfoText, "dojo.widget.SortableTable");
        appendHeaderText(headerInfoText, "dojo.widget.Checkbox");
    }
    
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        retrievePermissions(request.getPortletSession(), getContext(request));
        super.doView(request, response);
    }

    public void retrievePermissions(PortletSession session, Context context)
    {
        // TODO: don't use session, since this is a client-side portlet
        Iterator folderPermissions = (Iterator)session.getAttribute("folderPermissions", PortletSession.PORTLET_SCOPE);
        Iterator pagePermissions = (Iterator)session.getAttribute("pagePermissions", PortletSession.PORTLET_SCOPE);
        Iterator portletPermissions = (Iterator)session.getAttribute("portletPermissions", PortletSession.PORTLET_SCOPE);
        Iterator roles = (Iterator)session.getAttribute("roles", PortletSession.PORTLET_SCOPE);
        if (portletPermissions == null)
        {
            List folders = new LinkedList();
            List pages = new LinkedList();
            List portlets = new LinkedList();
            Iterator all = pm.getPermissions();
            while (all.hasNext())
            {
                InternalPermission permission = (InternalPermission)all.next();                
                if (permission.getClassname().equals(CLASSNAMES[0]))
                {
                    folders.add(new PermissionData(permission));                    
                }
                else if (permission.getClassname().equals(CLASSNAMES[1]))
                {
                    pages.add(new PermissionData(permission));
                }
                else if (permission.getClassname().equals(CLASSNAMES[2]))
                {
                    portlets.add(new PermissionData(permission));
                }                
            }
            folderPermissions = folders.iterator();
            pagePermissions = pages.iterator();
            portletPermissions = portlets.iterator();
            try
            {
                roles = rm.getRoles("");
            }
            catch(Exception e)
            {
                logger.error(e);
            }
        }        
        context.put("folderPermissions", folderPermissions);
        context.put("pagePermissions", pagePermissions);
        context.put("portletPermissions", portletPermissions);
        ArrayList rolesList = new ArrayList();
        if ( roles != null )
        {
            while( roles.hasNext() )
            {
                rolesList.add( roles.next() );
            }
        }
        context.put("roles", rolesList);
    }
    
    public void processAction(ActionRequest request,
            ActionResponse actionResponse) throws PortletException, IOException
    {
        PortletSession session = request.getPortletSession();
        //session.setAttribute(SESSION_RESULTS, stats);
    }

    public class PermissionData
    {
        public PermissionData(InternalPermission permission)
        {
            this.permission = permission;
            this.roles = ""; 
            int size = permission.getPrincipals().size(); 
            if (size == 0)
            {
                return;
            }
            Iterator principals = permission.getPrincipals().iterator();
            int count = 0;
            StringBuffer result = new StringBuffer();
            while (principals.hasNext())
            {
                InternalPrincipal principal = (InternalPrincipal)principals.next();
                int last = principal.getFullPath().lastIndexOf("/") + 1;
                result.append(principal.getFullPath().substring(last));            
                count++;
                if (count < size)
                {
                    result.append(",");
                }
            }
            this.roles = result.toString();
        }
        
        InternalPermission permission;
        String roles;
        
        public InternalPermission getPermission()
        {
            return permission;
        }
        
        public void setPermission(InternalPermission permission)
        {
            this.permission = permission;
        }
        
        public String getRoles()
        {
            return roles;
        }
        
        public void setRoles(String roles)
        {
            this.roles = roles;
        }
    }
}
