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
package org.apache.jetspeed.portlets.custom;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.springframework.util.StringUtils;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.page.PageManager;

/**
 * Common Custom Config Mode Portlet
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: $
 */
public class CustomConfigModePortlet extends GenericVelocityPortlet
{
    private static final PortletMode CONFIG_MODE = new PortletMode("config");
    private static final String DELIMITERS = "[],; ";
    
    private PageManager pageManager;
    private String configPage;
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        
        this.configPage = config.getInitParameter("ConfigPage");
        
        PortletContext context = getPortletContext();
        this.pageManager = (PageManager) context.getAttribute(CommonPortletServices.CPS_PAGE_MANAGER_COMPONENT);
        
        if (this.pageManager == null)
        {
            throw new PortletException("Could not get instance of pageManager component");
        }
    }
    
    protected void doDispatch(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        if ( !request.getWindowState().equals(WindowState.MINIMIZED))
        {
            PortletMode curMode = request.getPortletMode();
            
            if (CONFIG_MODE.equals(curMode))
            {
                List securityContraintRefList = null;
                
                try
                {
                    securityContraintRefList = this.pageManager.getPageSecurity().getSecurityConstraintsDefs();
                }
                catch (Exception e)
                {
                    throw new PortletException("Cannot find page security constraint definitions.", e);
                }
                
                if (securityContraintRefList != null)
                {
                    request.setAttribute("securityContraintRefList", securityContraintRefList);
                }
                
                request.setAttribute(PARAM_EDIT_PAGE, this.configPage);
                doEdit(request, response);
            }
            else
            {
                super.doDispatch(request, response);
            }
        }
    }

    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        String action = request.getParameter("action");
        
        if ("addConstraint".equals(action))
        {
            addSecurityConstraint(request, response);
        }
        else if ("removeConstraint".equals(action))
        {
            removeSecurityConstraint(request, response);
        }
        else if ("updateConstraintRefs".equals(action))
        {
            updateSecurityConstraintRefs(request, response);
        }
    }
    
    private void addSecurityConstraint(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        try
        {
            String path = request.getParameter("path");
            String fragmentId = request.getParameter("fragment");
            String type = request.getParameter("type");
            String roles = request.getParameter("roles");
            String groups = request.getParameter("groups");
            String users = request.getParameter("users");
            String permissions = request.getParameter("permissions");
            
            Page page = this.pageManager.getPage(path);
            Fragment fragment = page.getFragmentById(fragmentId);
            
            if (fragment == null)
            {
                throw new IllegalStateException("Cannot find fragment: " + fragmentId);
            }
            
            SecurityConstraints constraints = fragment.getSecurityConstraints();
            
            if (constraints == null)
            {
                constraints = fragment.newSecurityConstraints();
            }
            
            SecurityConstraint constraint = fragment.newSecurityConstraint();
            Set roleSet = convertToSet(roles, DELIMITERS);
            Set groupSet = convertToSet(groups, DELIMITERS);
            Set userSet = convertToSet(users, DELIMITERS);
            
            if (!roleSet.isEmpty())
            {
                constraint.setRoles(new ArrayList(roleSet));
            }
            if (!groupSet.isEmpty())
            {
                constraint.setGroups(new ArrayList(groupSet));
            }
            if (!userSet.isEmpty())
            {
                constraint.setUsers(new ArrayList(userSet));
            }
            
            Set permissionSet = convertToSet(permissions, DELIMITERS);
            
            constraint.setPermissions(new ArrayList(permissionSet));
            List constraintList = constraints.getSecurityConstraints();
            
            if (constraintList == null)
            {
                constraintList = new ArrayList();
            }
            
            constraintList.add(constraint);
            
            constraints.setSecurityConstraints(constraintList);
            fragment.setSecurityConstraints(constraints);
            this.pageManager.updatePage(page);
        }
        catch (Exception e)
        {
            throw new PortletException("Failed to add security constraint.", e);
        }
    }
    
    private void removeSecurityConstraint(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        try
        {
            String path = request.getParameter("path");
            String fragmentId = request.getParameter("fragment");
            String roles = request.getParameter("roles");
            String groups = request.getParameter("groups");
            String users = request.getParameter("users");
            String permissions = request.getParameter("permissions");
            
            Page page = this.pageManager.getPage(path);
            Fragment fragment = page.getFragmentById(fragmentId);
            
            if (fragment == null)
            {
                throw new IllegalStateException("Cannot find fragment: " + fragmentId);
            }
            
            SecurityConstraints constraints = fragment.getSecurityConstraints();
            
            List constraintList = null;
            
            if (constraints != null)
            {
                constraintList = constraints.getSecurityConstraints();
                
                if (constraintList != null)
                {
                    for (Iterator it = constraintList.iterator(); it.hasNext(); )
                    {
                        SecurityConstraint constraint = (SecurityConstraint) it.next();
                        
                        Set removeRoleSet = convertToSet(roles, DELIMITERS);
                        Set removeGroupSet = convertToSet(groups, DELIMITERS);
                        Set removeUserSet = convertToSet(users, DELIMITERS);
                        
                        List roleList = constraint.getRoles();
                        List groupList = constraint.getGroups();
                        List userList = constraint.getUsers();
                        
                        if (equalsSetAndList(removeRoleSet, roleList) &&
                            equalsSetAndList(removeGroupSet, groupList) &&
                            equalsSetAndList(removeUserSet, userList))
                        {
                            it.remove();
                            break;
                        }
                    }
                }
            }
            
            if (constraints != null && constraintList != null)
            {
                constraints.setSecurityConstraints(constraintList);
            }
            
            fragment.setSecurityConstraints(constraints.isEmpty() ? null : constraints);
            this.pageManager.updatePage(page);
        }
        catch (Exception e)
        {
            throw new PortletException("Failed to remove security constraint.", e);
        }
    }

    private void updateSecurityConstraintRefs(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        try
        {
            String path = request.getParameter("path");
            String fragmentId = request.getParameter("fragment");
            String [] securityConstraintRefs = request.getParameterValues("securityConstraintRef");
            
            Page page = this.pageManager.getPage(path);
            Fragment fragment = page.getFragmentById(fragmentId);
            
            if (fragment == null)
            {
                throw new IllegalStateException("Cannot find fragment: " + fragmentId);
            }
            
            SecurityConstraints constraints = fragment.getSecurityConstraints();
            
            if (constraints == null)
            {
                constraints = fragment.newSecurityConstraints();
            }
            
            Set constraintRefSet = new HashSet();
            
            if (securityConstraintRefs != null)
            {
                for (int i = 0; i < securityConstraintRefs.length; i++)
                {
                    if (!"".equals(securityConstraintRefs[i]))
                    {
                        constraintRefSet.add(securityConstraintRefs[i]);
                    }
                }
            }
            
            constraints.setSecurityConstraintsRefs(constraintRefSet.isEmpty() ? null : new ArrayList(constraintRefSet));
            fragment.setSecurityConstraints(constraints.isEmpty() ? null : constraints);
            this.pageManager.updatePage(page);
        }
        catch (Exception e)
        {
            throw new PortletException("Failed to remove security constraint.", e);
        }
    }
    
    private Set convertToSet(String s, String delimiters)
    {
        Set set = new HashSet();
        
        String [] tokens = StringUtils.tokenizeToStringArray(s, delimiters, true, true);
            
        if (tokens != null)
        {
            for (int i = 0; i < tokens.length; i++)
            {
                set.add(tokens[i]);
            }
        }
        
        return set;
    }
    
    private boolean equalsSetAndList(Set set, List list)
    {
        if (set == null)
        {
            return (list == null || list.isEmpty());
        }
        else if (list == null)
        {
            return set.isEmpty();
        }
        else
        {
            return set.equals(new HashSet(list));
        }
    }
    
}