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
package org.apache.jetspeed.portlets.security.users;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.security.auth.Subject;

import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.portlets.security.users.JetspeedUserBean;
import org.apache.jetspeed.portlets.security.users.JetspeedUserBean.StringAttribute;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.portals.bridges.beans.TabBean;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.messaging.PortletMessaging;

/**
 * This portlet is a tabbed editor user interface for editing user attributes
 * and security definitions.
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class UserDetailsPortlet extends GenericServletPortlet
{
    private final String VIEW_USER = "user"; 
    private final String VIEW_ROLES = "roles";
    private final String VIEW_GROUPS = "groups";
    private final String VIEW_RULES = "rules";
    private final String VIEW_CREDENTIAL = "credential"; 
    private final String VIEW_ALL_RULES = "prules";
    private final String VIEW_SELECTED_RULE = "selectedRule";
    
    private final String USER_ACTION_PREFIX = "security_user.";
    private final String ACTION_UPDATE_ATTRIBUTE = "update_user_attribute";
    private final String ACTION_REMOVE_ATTRIBUTE = "remove_user_attribute";
    private final String ACTION_ADD_ATTRIBUTE = "add_user_attribute";
    private final String ACTION_REMOVE_ROLE = "remove_user_role";
    private final String ACTION_ADD_ROLE = "add_user_role";
    private final String ACTION_REMOVE_GROUP = "remove_user_group";
    private final String ACTION_ADD_GROUP = "add_user_group";
    private final String ACTION_REMOVE_RULE = "remove_user_rule";
    private final String ACTION_ADD_RULE = "add_rule";
    private final String ACTION_UPDATE_CREDENTIAL = "update_user_credential";
    
    private final String TAB_ATTRIBUTES = "user_attributes";
    private final String TAB_ROLE = "user_role";
    private final String TAB_GROUP = "user_group";
    private final String TAB_PROFILE = "user_profile";
    private final String TAB_CREDENTIAL = "user_credential";
    
    /** the id of the roles control */
    private static final String ROLES_CONTROL = "jetspeedRoles";

    /** the id of the groups control */
    private static final String GROUPS_CONTROL = "jetspeedGroups";
    
    
    private UserManager  userManager;
    private RoleManager  roleManager;
    private GroupManager groupManager;
    private Profiler     profiler;

    private LinkedHashMap userTabMap = new LinkedHashMap();
    private LinkedHashMap anonymousUserTabMap = new LinkedHashMap();
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        userManager = (UserManager)getPortletContext().getAttribute(SecurityResources.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        roleManager = (RoleManager)getPortletContext().getAttribute(SecurityResources.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager)
        {
            throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }
        groupManager = (GroupManager)getPortletContext().getAttribute(SecurityResources.CPS_GROUP_MANAGER_COMPONENT);
        if (null == groupManager)
        {
            throw new PortletException("Failed to find the Group Manager on portlet initialization");
        }
        profiler = (Profiler)getPortletContext().getAttribute(SecurityResources.CPS_PROFILER_COMPONENT);
        if (null == profiler)
        {
            throw new PortletException("Failed to find the Profiler on portlet initialization");
        }
        
        TabBean tb1 = new TabBean(TAB_ATTRIBUTES);
        TabBean tb2 = new TabBean(TAB_ROLE);
        TabBean tb3 = new TabBean(TAB_GROUP);
        TabBean tb4 = new TabBean(TAB_PROFILE);
        TabBean tb5 = new TabBean(TAB_CREDENTIAL);
        
        userTabMap.put(tb1.getId(), tb1);
        userTabMap.put(tb5.getId(), tb5);
        userTabMap.put(tb2.getId(), tb2);
        userTabMap.put(tb3.getId(), tb3); 
        userTabMap.put(tb4.getId(), tb4);
        
        anonymousUserTabMap.put(tb1.getId(), tb1);
        anonymousUserTabMap.put(tb2.getId(), tb2);
        anonymousUserTabMap.put(tb3.getId(), tb3);
        anonymousUserTabMap.put(tb4.getId(), tb4);
    }
    
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        response.setContentType("text/html");
        
        String userName = (String)
            request.getPortletSession().getAttribute(SecurityResources.PAM_CURRENT_USER, 
                                         PortletSession.APPLICATION_SCOPE);

        User user = null;
        if (userName != null)
        {
            // TODO: don't lookup with every view call
            user = lookupUser(userName);
        }
        
        if (user != null)
        {       
            LinkedHashMap tabMap = null;
            if ( userManager.getAnonymousUser().equals(userName) )
            {
                tabMap = anonymousUserTabMap;
            }
            else
            {
                tabMap = userTabMap;
            }
            
            // Tabs
            request.setAttribute("tabs", tabMap.values());        
            TabBean selectedTab = 
                (TabBean) request.getPortletSession().getAttribute(SecurityResources.REQUEST_SELECT_TAB);

            if(selectedTab != null && !tabMap.containsKey(selectedTab.getId()))
            {
                selectedTab = null;
            }
            
            if(selectedTab == null)
            {
                selectedTab = (TabBean) tabMap.values().iterator().next();
            }
            JetspeedUserBean bean = new JetspeedUserBean(user);
            request.setAttribute(VIEW_USER, bean);
            if (selectedTab.getId().equals(TAB_ROLE))
            {                
                request.setAttribute(VIEW_ROLES, getRoles(userName));
                
                // check for refresh on roles list
                String refreshRoles = (String)PortletMessaging.consume(request, "roles", "refresh");
                List roles = null;
                if (refreshRoles == null)
                {        
                    roles = (List) request.getPortletSession().getAttribute(ROLES_CONTROL);
                }
                
                // build the roles control and provide it to the view
                try
                {
                    if (roles == null)
                    {
                        roles = new LinkedList();
                        Iterator fullRoles = roleManager.getRoles("");
                        while (fullRoles.hasNext())
                        {
                            Role role = (Role)fullRoles.next();
                            roles.add(role.getPrincipal().getName());
                        }
                        request.getPortletSession().setAttribute(ROLES_CONTROL, roles);
                    }
                }
                catch (SecurityException se)
                {
                    throw new PortletException(se);
                }        
                request.setAttribute(ROLES_CONTROL, roles);
                
            }
            else if (selectedTab.getId().equals(TAB_GROUP))
            {
                request.setAttribute(VIEW_GROUPS, getGroups(userName));
                
                // check for refresh on groups list
                String refreshGroups = (String)PortletMessaging.consume(request, "groups", "refresh");
                List groups = null;
                if (refreshGroups == null)
                {        
                    groups = (List) request.getPortletSession().getAttribute(GROUPS_CONTROL);
                }
                
                // build the groups control and provide it to the view
                try
                {
                    if (groups == null)
                    {
                        groups = new LinkedList();
                        Iterator fullGroups = groupManager.getGroups("");
                        while (fullGroups.hasNext())
                        {
                            Group group = (Group)fullGroups.next();
                            groups.add(group.getPrincipal().getName());
                        }
                        request.getPortletSession().setAttribute(GROUPS_CONTROL, groups);
                    }
                }
                catch (SecurityException se)
                {
                    throw new PortletException(se);
                }        
                request.setAttribute(GROUPS_CONTROL, groups);
                
            }
            else if (selectedTab.getId().equals(TAB_PROFILE))
            {
                request.setAttribute(VIEW_RULES, getRules(user));
                request.setAttribute(VIEW_ALL_RULES, getProfilerRules());
            }
            else if (selectedTab.getId().equals(TAB_CREDENTIAL))
            {
                request.setAttribute(VIEW_CREDENTIAL, getCredential(userName));
            }
           
            request.setAttribute(SecurityResources.REQUEST_SELECT_TAB, selectedTab);
        }
        
        super.doView(request, response);
    }

    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) 
        throws PortletException, IOException
    {                        
        String selectedTab = actionRequest.getParameter(SecurityResources.REQUEST_SELECT_TAB);
        if (selectedTab != null)
        {
            TabBean tab = (TabBean) userTabMap.get(selectedTab);
            if (tab != null)
            {
                actionRequest.getPortletSession().setAttribute(
                        SecurityResources.REQUEST_SELECT_TAB, tab);
            }            
        }             
        String action = actionRequest.getParameter(SecurityResources.PORTLET_ACTION);
        if (action != null && action.equals("remove.user"))
        {
            removeUser(actionRequest, actionResponse);
        }
        else if (action != null && isUserPortletAction(action))
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
            else if (action.endsWith(this.ACTION_ADD_RULE))
            {
                addUserProfile(actionRequest, actionResponse);
            }
            else if (action.endsWith(this.ACTION_REMOVE_RULE))
            {
                removeUserProfile(actionRequest, actionResponse);
            }
            else if (action.endsWith(this.ACTION_UPDATE_CREDENTIAL))
            {
                updateUserCredential(actionRequest, actionResponse);
            }
        }
    }    
    
    public void removeUser(ActionRequest actionRequest, ActionResponse actionResponse) 
    throws PortletException
    {
        String userName = (String)
            actionRequest.getPortletSession().getAttribute(SecurityResources.PAM_CURRENT_USER, 
                             PortletSession.APPLICATION_SCOPE);
        User user = lookupUser(userName);
        if (user != null)
        {
            try
            {
                userManager.removeUser(userName);
                PortletMessaging.publish(actionRequest, "users", "refresh", "all");
            }
            catch (Exception e)
            {
                
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

    private void updateUserCredential(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)
        actionRequest.getPortletSession().getAttribute(SecurityResources.PAM_CURRENT_USER, 
                             PortletSession.APPLICATION_SCOPE);
        User user = lookupUser(userName);
        if (user != null)
        {
            try
            {
                String password = actionRequest.getParameter("user_cred_value");
                boolean updated = false;
                if ( password != null && password.trim().length() > 0 )
                {
                    userManager.setPassword(userName, null, password);
                    updated = true;
                }
                PasswordCredential credential = getCredential(userName);
                if ( credential != null )
                {
                    String updateRequiredStr = actionRequest.getParameter("user_cred_updreq");
                    if (updateRequiredStr != null)
                    {
                        boolean updateRequired = Boolean.valueOf(updateRequiredStr).booleanValue();
                        if (updateRequired != credential.isUpdateRequired())
                        {
                            userManager.setPasswordUpdateRequired(userName,updateRequired);
                            updated = true;
                        }
                    }
                    String enabledStr = actionRequest.getParameter("user_cred_enabled");
                    if (enabledStr != null)
                    {
                        boolean enabled = Boolean.valueOf(enabledStr).booleanValue();
                        if (enabled != credential.isEnabled())
                        {
                            userManager.setPasswordEnabled(userName,enabled);
                            updated = true;
                        }
                    }
                }
            }
            catch (SecurityException e)
            {
                // TODO: logging
                System.err.println("failed to update user credential " + userName + ": " + e);                                      
            }
        }
    }
    
    private void updateUserAttribute(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)
            actionRequest.getPortletSession().getAttribute(SecurityResources.PAM_CURRENT_USER, 
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
            actionRequest.getPortletSession().getAttribute(SecurityResources.PAM_CURRENT_USER, 
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
            actionRequest.getPortletSession().getAttribute(SecurityResources.PAM_CURRENT_USER, 
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
            actionRequest.getPortletSession().getAttribute(SecurityResources.PAM_CURRENT_USER, 
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
            actionRequest.getPortletSession().getAttribute(SecurityResources.PAM_CURRENT_USER, 
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
            actionRequest.getPortletSession().getAttribute(SecurityResources.PAM_CURRENT_USER, 
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
            actionRequest.getPortletSession().getAttribute(SecurityResources.PAM_CURRENT_USER, 
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
    
    private PasswordCredential getCredential(String userName)
    {
        PasswordCredential credential = null;
        Set credentials = lookupUser(userName).getSubject().getPrivateCredentials();
        Iterator iter = credentials.iterator();
        while (iter.hasNext())
        {
            Object o = iter.next();
            if (o instanceof PasswordCredential)
            {
                credential = (PasswordCredential)o;
                break;
            }
        }
        return credential;
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
    
    private Collection getRules(User user)
    {
        Principal userPrincipal = createPrincipal(user.getSubject(), UserPrincipal.class);
        return profiler.getRulesForPrincipal(userPrincipal);
    }

    private void addUserProfile(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)
            actionRequest.getPortletSession().getAttribute(SecurityResources.PAM_CURRENT_USER, 
                                     PortletSession.APPLICATION_SCOPE);
        User user = lookupUser(userName);
        if (user != null)
        {
            String locatorName = actionRequest.getParameter("locator_name");
            if (locatorName != null && locatorName.trim().length() > 0)
            {
                try
                {
                    Principal userPrincipal = createPrincipal(user.getSubject(), UserPrincipal.class);                          
                    String ruleName = actionRequest.getParameter("select_rule");
                    profiler.setRuleForPrincipal(userPrincipal, 
                            profiler.getRule(ruleName),
                            locatorName);                                                         
                }
                catch (Exception e)
                {
                    // TODO: logging
                    System.err.println("failed to set rule for principal: " + userName + ", "  + locatorName + e);                       
                }
            }
            
        }
    }
    
    private void removeUserProfile(ActionRequest actionRequest, ActionResponse actionResponse)
    {
        String userName = (String)
            actionRequest.getPortletSession().getAttribute(SecurityResources.PAM_CURRENT_USER, 
                                     PortletSession.APPLICATION_SCOPE);
        User user = lookupUser(userName);
        if (user != null)
        {
            String[] locatorNames = actionRequest.getParameterValues("user_profile_id");

            if(locatorNames != null)
            {
                Principal userPrincipal = createPrincipal(user.getSubject(), UserPrincipal.class);                                              
                Collection rules = profiler.getRulesForPrincipal(userPrincipal);
                for (int ix = 0; ix < locatorNames.length; ix++)
                {
                    try
                    {
                        Iterator it = rules.iterator();
                        while (it.hasNext())
                        {
                            PrincipalRule rule = (PrincipalRule)it.next();
                            if (rule.getLocatorName().equals(locatorNames[ix]))
                            {
                                profiler.deletePrincipalRule(rule);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        // TODO: logging
                        System.err.println("failed to remove rule for principal: " + userName + ", "  + locatorNames[ix] + e);                       
                    }                
                }
            }                                    
        }
    }        
}
