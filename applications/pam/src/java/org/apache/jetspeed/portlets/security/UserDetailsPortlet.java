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
import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.security.auth.Subject;

import org.apache.jetspeed.portlet.ServletPortlet;
import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.portlets.pam.beans.TabBean;
import org.apache.jetspeed.portlets.security.users.JetspeedUserBean;
import org.apache.jetspeed.portlets.security.users.JetspeedUserBean.StringAttribute;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;

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
    private final String VIEW_USER = "user"; 
    private final String VIEW_ROLES = "roles";
    private final String VIEW_GROUPS = "groups";
    private final String VIEW_RULES = "rules";
    private final String VIEW_SELECTED_RULE = "selectedRule";
    
    private final String USER_ACTION_PREFIX = "security_user.";
    private final String ACTION_UPDATE_ATTRIBUTE = "update_user_attribute";
    private final String ACTION_REMOVE_ATTRIBUTE = "remove_user_attribute";
    private final String ACTION_ADD_ATTRIBUTE = "add_user_attribute";
    private final String ACTION_REMOVE_ROLE = "remove_user_role";
    private final String ACTION_ADD_ROLE = "add_user_role";
    private final String ACTION_REMOVE_GROUP = "remove_user_group";
    private final String ACTION_ADD_GROUP = "add_user_group";
    private final String ACTION_UPDATE_RULE = "update_user_rule";
    
    private final String TAB_ATTRIBUTES = "user_attributes";
    private final String TAB_ROLE = "user_role";
    private final String TAB_GROUP = "user_group";
    private final String TAB_PROFILE = "user_profile";
    
    private UserManager  userManager;
    private RoleManager  roleManager;
    private GroupManager groupManager;
    private Profiler     profiler;

    private LinkedHashMap userTabMap = new LinkedHashMap();
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        userManager = (UserManager)getPortletContext().getAttribute(PortletApplicationResources.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        roleManager = (RoleManager)getPortletContext().getAttribute(PortletApplicationResources.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager)
        {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }
        groupManager = (GroupManager)getPortletContext().getAttribute(PortletApplicationResources.CPS_GROUP_MANAGER_COMPONENT);
        if (null == groupManager)
        {
            throw new PortletException("Failed to find the Group Manager on portlet initialization");
        }
        profiler = (Profiler)getPortletContext().getAttribute(PortletApplicationResources.CPS_PROFILER_COMPONENT);
        if (null == profiler)
        {
            throw new PortletException("Failed to find the Profiler on portlet initialization");
        }
        
        TabBean tb1 = new TabBean(TAB_ATTRIBUTES);
        TabBean tb2 = new TabBean(TAB_ROLE);
        TabBean tb3 = new TabBean(TAB_GROUP);
        TabBean tb4 = new TabBean(TAB_PROFILE);
        
        userTabMap.put(tb1.getId(), tb1);
        userTabMap.put(tb2.getId(), tb2);
        userTabMap.put(tb3.getId(), tb3); 
        userTabMap.put(tb4.getId(), tb4);
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
            
            // Tabs
            request.setAttribute("tabs", userTabMap.values());        
            TabBean selectedTab = 
                (TabBean) request.getPortletSession().getAttribute(PortletApplicationResources.REQUEST_SELECT_TAB);
            if(selectedTab == null)
            {
                selectedTab = (TabBean) userTabMap.values().iterator().next();
            }
            JetspeedUserBean bean = new JetspeedUserBean(user);
            request.setAttribute(VIEW_USER, bean);
            if (selectedTab.getId().equals(TAB_ROLE))
            {
                request.setAttribute(VIEW_ROLES, getRoles(userName));                
            }
            else if (selectedTab.getId().equals(TAB_GROUP))
            {
                request.setAttribute(VIEW_GROUPS, getGroups(userName));  
            }
            else if (selectedTab.getId().equals(TAB_PROFILE))
            {
                Principal userPrincipal = createPrincipal(user.getSubject(), UserPrincipal.class);      
                ProfilingRule rule = profiler.getRuleForPrincipal(userPrincipal);
                if (rule != null)
                {
                    request.setAttribute(VIEW_SELECTED_RULE, rule.getId());
                }
                request.setAttribute(VIEW_RULES, getProfilerRules());                  
            }
           
            request.setAttribute(PortletApplicationResources.REQUEST_SELECT_TAB, selectedTab);
        }
        
        super.doView(request, response);
    }

    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) 
        throws PortletException, IOException
    {                        
        String selectedTab = actionRequest.getParameter(PortletApplicationResources.REQUEST_SELECT_TAB);
        if (selectedTab != null)
        {
            TabBean tab = (TabBean) userTabMap.get(selectedTab);
            if (tab != null)
            {
                actionRequest.getPortletSession().setAttribute(
                        PortletApplicationResources.REQUEST_SELECT_TAB, tab);
            }            
        }             
        String action = actionRequest.getParameter(PortletApplicationResources.PORTLET_ACTION);
        if (action != null && isUserPortletAction(action))
        {
            action = getAction(USER_ACTION_PREFIX, action);                
            if (action.endsWith(ACTION_UPDATE_ATTRIBUTE))
            {
                updateUserAttribute(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_REMOVE_ATTRIBUTE))
            {
                removeUserAttributes(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_ADD_ATTRIBUTE))
            {
                addUserAttribute(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_REMOVE_ROLE))
            {
                removeUserRoles(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_ADD_ROLE))
            {
                addUserRole(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_REMOVE_GROUP))
            {
                removeUserGroups(actionRequest, actionResponse);
            }
            else if (action.endsWith(ACTION_ADD_GROUP))
            {
                addUserGroup(actionRequest, actionResponse);
            }
            else if (action.endsWith(this.ACTION_UPDATE_RULE))
            {
                updateUserProfile(actionRequest, actionResponse);
            }
        }
    }    
    
    public Principal createPrincipal(Subject subject, Class classe)
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
        }
        return principal;
    }    

    private void updateUserAttribute(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)
            actionRequest.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_USER, 
                                 PortletSession.APPLICATION_SCOPE);
        User user = lookupUser(userName);
        if (user != null)
        {
            String[] userAttrNames = actionRequest.getParameterValues("user_attr_id");
            if(userAttrNames != null)
            {                
                for (int i=0; i<userAttrNames.length; i++)
                {
                    String userAttrName = userAttrNames[i];
                    String value = actionRequest.getParameter(userAttrName + ":value");
                    user.getUserAttributes().put(userAttrName, value);
                }                
            }        
        }
    }
    
    private void addUserAttribute(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)
            actionRequest.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_USER, 
                                     PortletSession.APPLICATION_SCOPE);
        
        User user = lookupUser(userName);
        if (user != null)
        {
            String userAttrName = actionRequest.getParameter("user_attr_name");
            String userAttrValue = actionRequest.getParameter("user_attr_value");
            if (userAttrName != null && userAttrName.trim().length() > 0)
            {
                Preferences attributes = user.getUserAttributes();
                attributes.put(userAttrName, userAttrValue);
            }
        }
    }

    private void removeUserAttributes(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)
            actionRequest.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_USER, 
                                     PortletSession.APPLICATION_SCOPE);
        List deletes = new LinkedList();
        
        User user = lookupUser(userName);
        if (user != null)
        {
            String[] userAttrNames = actionRequest.getParameterValues("user_attr_id");

            if(userAttrNames != null)
            {
                JetspeedUserBean bean = new JetspeedUserBean(user);
                Preferences attributes = user.getUserAttributes();
                Iterator userAttrIter = bean.getAttributes().iterator();
                while (userAttrIter.hasNext())
                {
                    StringAttribute userAttr = (StringAttribute) userAttrIter.next();
                    for(int ix = 0; ix < userAttrNames.length; ix++)
                    {
                        String userAttrName = userAttrNames[ix];
                        if(userAttr.getName().equals(userAttrName))
                        {
                            deletes.add(userAttrName);
                            break;
                        }
                    }
                }
                Iterator it = deletes.iterator();            
                while (it.hasNext())
                {
                    String attribute = (String)it.next();
                    attributes.remove(attribute);
                }
                
            }            
        }
    }
    
    private void removeUserRoles(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)
            actionRequest.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_USER, 
                                     PortletSession.APPLICATION_SCOPE);
        User user = lookupUser(userName);
        if (user != null)
        {
            String[] roleNames = actionRequest.getParameterValues("user_role_id");

            if(roleNames != null)
            {
                for (int ix = 0; ix < roleNames.length; ix++)
                {
                    try
                    {
                        if (roleManager.roleExists(roleNames[ix]))
                        {
                            roleManager.removeRoleFromUser(userName, roleNames[ix]);
                        }
                    }
                    catch (SecurityException e)
                    {
                        // TODO: logging
                        System.err.println("failed to remove user from role: " + userName + ", "  + roleNames[ix] + e);                       
                    }                
                }
            }            
        }
    }    
    
    private void addUserRole(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)
            actionRequest.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_USER, 
                                     PortletSession.APPLICATION_SCOPE);
        
        User user = lookupUser(userName);
        if (user != null)
        {
            String roleName = actionRequest.getParameter("role_name");
            if (roleName != null && roleName.trim().length() > 0)
            {
                try
                {
                    roleManager.addRoleToUser(userName, roleName);
                }
                catch (SecurityException e)
                {
                    // TODO: logging
                    System.err.println("failed to add user to role: " + userName + ", "  + roleName + e);                       
                }
            }
        }
    }
    
    private void removeUserGroups(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)
            actionRequest.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_USER, 
                                     PortletSession.APPLICATION_SCOPE);
        User user = lookupUser(userName);
        if (user != null)
        {
            String[] groupNames = actionRequest.getParameterValues("user_group_id");

            if(groupNames != null)
            {
                for (int ix = 0; ix < groupNames.length; ix++)
                {
                    try
                    {
                        if (groupManager.groupExists(groupNames[ix]))
                        {
                            groupManager.removeUserFromGroup(userName, groupNames[ix]);
                        }
                    }
                    catch (SecurityException e)
                    {
                        // TODO: logging
                        System.err.println("failed to remove user from group: " + userName + ", "  + groupNames[ix] + e);                       
                    }                
                }
            }            
        }
    }    
    
    private void addUserGroup(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)
            actionRequest.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_USER, 
                                     PortletSession.APPLICATION_SCOPE);
        
        User user = lookupUser(userName);
        if (user != null)
        {
            String groupName = actionRequest.getParameter("group_name");
            if (groupName != null && groupName.trim().length() > 0)
            {
                try
                {
                    groupManager.addUserToGroup(userName, groupName);
                }
                catch (SecurityException e)
                {
                    // TODO: logging
                    System.err.println("failed to add user to group: " + userName + ", "  + groupName + e);                       
                }
            }
        }
    }
        
    private String getAction(String prefix, String action)
    {
        return action.substring(prefix.length());
    }

    private boolean isUserPortletAction(String action)
    {
        return action.startsWith(USER_ACTION_PREFIX);
    }
    
    private Collection getRoles(String userName)
    {
        try
        {
            return roleManager.getRolesForUser(userName); 
        }
        catch (SecurityException e)
        {
            // TODO: logging
            System.err.println("roles not found: " + userName + ", " + e);       
        }
        return new LinkedList();
    }
    
    private Collection getGroups(String userName)
    {
        try
        {
            return groupManager.getGroupsForUser(userName); 
        }
        catch (SecurityException e)
        {
            // TODO: logging
            System.err.println("groups not found: " + userName + ", " + e);       
        }
        return new LinkedList();
    }    
    
    private User lookupUser(String userName)
    {
        User user = null;
        try
        {
            user = userManager.getUser(userName);
        }
        catch (Exception e)
        {
            // TODO: logging
            System.err.println("user not found: " + userName + ", " + e);
        }    
        return user;
    }
    
    private Collection getProfilerRules()
    {        
        return profiler.getRules();
    }
    
    private void updateUserProfile(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)
            actionRequest.getPortletSession().getAttribute(PortletApplicationResources.PAM_CURRENT_USER, 
                                     PortletSession.APPLICATION_SCOPE);
        User user = lookupUser(userName);
        if (user != null)
        {
            String profileId = actionRequest.getParameter("user_profile_id");

            if(profileId != null)
            {
                try
                {
                    Principal userPrincipal = createPrincipal(user.getSubject(), UserPrincipal.class);      
                    ProfilingRule rule = profiler.getRule(profileId);
                    if (userPrincipal != null)
                    {
                        if (rule == null)
                        {
                            profiler.setRuleForPrincipal(userPrincipal, profiler.getDefaultRule());
                        }
                        else
                        {
                            profiler.setRuleForPrincipal(userPrincipal, rule);
                        }
                    }
                }
                catch (Exception e)
                {
                    // TODO: logging
                    System.err.println("failed to update user + profile: " + userName + ", "  + profileId + e);                       
                }                
            }            
        }
    }        
}
