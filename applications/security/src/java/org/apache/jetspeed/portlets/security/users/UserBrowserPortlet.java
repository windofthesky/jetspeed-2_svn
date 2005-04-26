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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.security.auth.Subject;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.portals.bridges.common.GenericServletPortlet;
import org.apache.portals.messaging.PortletMessaging;
import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlNode;

/**
 * This portlet is a browser over all the portlet applications in the system.
 *
 * @deprecated 
 * @see UserBrowser.java (new implementation)
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class UserBrowserPortlet extends GenericServletPortlet
{
    private UserManager userManager;
    private RoleManager roleManager;
    private Profiler    profiler;
    
    /** the id of the tree control */
    private static final String TREE_CONTROL = "j2_tree";

    /** the id of the roles control */
    private static final String ROLES_CONTROL = "jetspeedRoles";

    /** the id of the rules control */
    private static final String RULES_CONTROL = "jetspeedRules";
    
    /** query filter for selecting users */
    private static final String USER_FILTER = "";

    /** the id of the root node of the tree control */
    private static final String SECURITY_NODE_ID = "SECURITY-NODE";

    /** the domain of the security sub-tree */
    private static final String SECURITY_DOMAIN = "SECURITY_DOMAIN";

    /** the id of the user node of the tree control */
    private static final String USER_NODE_ID = "USER-NODE";

    /** the domain of the user sub-tree */
    private static final String USER_DOMAIN = "USER_DOMAIN";

    /** the domain of the users leaf nodes */
    private static final String USER_DETAIL_DOMAIN = "USER_DETAIL_DOMAIN";

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        userManager = (UserManager) getPortletContext()
                .getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        roleManager = (RoleManager) getPortletContext()
        			.getAttribute(CommonPortletServices.CPS_ROLE_MANAGER_COMPONENT);
        if (null == roleManager)
        {
        		throw new PortletException("Failed to find the Role Manager on portlet initialization");
        }
        profiler = (Profiler)getPortletContext().getAttribute(CommonPortletServices.CPS_PROFILER_COMPONENT);
        if (null == profiler)
        {
            throw new PortletException("Failed to find the Profiler on portlet initialization");
        }        
    }

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");

        String errorMessage = (String)PortletMessaging.consume(request, "user.error");
        if (errorMessage != null)
        {
            request.setAttribute("errorMessage", errorMessage);            
        }
        
        // check for refresh on users list
        TreeControl control = null;
        String refresh = (String)PortletMessaging.consume(request, "users", "refresh");
        if (refresh == null)
        {        
            control = (TreeControl) request.getPortletSession().getAttribute(TREE_CONTROL);
        }
        
        // build the tree control and provide it to the view
        try
        {
            if (control == null)
            {
                Iterator users = userManager.getUsers(USER_FILTER);
                control = buildTree(users, request.getLocale());
                request.getPortletSession().setAttribute(TREE_CONTROL, control);
            }
        }
        catch (SecurityException se)
        {
            throw new PortletException(se);
        }        
        request.setAttribute(TREE_CONTROL, control);

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

        // check for refresh on profiles list
        String refreshProfiles = (String)PortletMessaging.consume(request, "profiles", "refresh");
        Collection rules = null;
        if (refreshProfiles == null)
        {        
            rules = (Collection) request.getPortletSession().getAttribute(RULES_CONTROL);
        }
        
        // build the profiles control and provide it to the view
        if (rules == null)
        {
            rules = profiler.getRules();
            request.getPortletSession().setAttribute(RULES_CONTROL, rules);
        }
        request.setAttribute(RULES_CONTROL, rules);
        
        super.doView(request, response);
    }

    private boolean isEmpty(String s)
    {
        if (s == null) return true;
        
        if (s.trim().equals("")) return true;
        
        return false;
    }
    
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) 
    throws PortletException,
          IOException
    {
        String browserAction = actionRequest.getParameter("browser.action");
        if (browserAction != null)
        {
            String userName = actionRequest.getParameter("jetspeed.user");
            String password = actionRequest.getParameter("jetspeed.password");            
            if (!isEmpty(userName) && !isEmpty(password)) 
            {
                try
                {            
                    userManager.addUser(userName, password);
                    TreeControl control = (TreeControl) actionRequest.getPortletSession().getAttribute(TREE_CONTROL);
                    Iterator users = userManager.getUsers(USER_FILTER);
                    control = buildTree(users, actionRequest.getLocale());
                    actionRequest.getPortletSession().setAttribute(TREE_CONTROL, control);
                    selectNode(actionRequest, control, userName);
                    
                    User user = userManager.getUser(userName);
                    String role = actionRequest.getParameter(ROLES_CONTROL);
                    if (!isEmpty(role) && user != null) 
                    {
                        roleManager.addRoleToUser(userName, role);
                    }

                    String rule = actionRequest.getParameter(RULES_CONTROL);
                    if (!isEmpty(rule) && user != null) 
                    {
                        Principal principal = getPrincipal(user.getSubject(), UserPrincipal.class);                         
                        profiler.setRuleForPrincipal(principal, profiler.getRule(rule), "page");
                    }
                    
                }
                catch (SecurityException se)
                {
                    PortletMessaging.publish(actionRequest, "user.error", se.getMessage());
                }
                
            }
                        
            
            return;
        }
        TreeControl control = (TreeControl) actionRequest.getPortletSession().getAttribute(TREE_CONTROL);
        //assert control != null
        if (control != null)
        {
            // expand or contact non-leaf nodes
            String node = actionRequest.getParameter(SecurityResources.REQUEST_NODE);
            if (node != null)
            {
                TreeControlNode controlNode = control.findNode(node);
                if (controlNode != null)
                {
                    controlNode.setExpanded(!controlNode.isExpanded());
                }
            }

            // select a node
            String selectedNode = actionRequest.getParameter(SecurityResources.REQUEST_SELECT_NODE);
            if (selectedNode != null)
            {
                selectNode(actionRequest, control, selectedNode);
            }
        }
    }

    private void selectNode(ActionRequest actionRequest, TreeControl control, String selectedNode)
    {
        control.selectNode(selectedNode);
        TreeControlNode child = control.findNode(selectedNode);
        if (child != null)
        {
            String domain = child.getDomain();
            if (domain.equals(USER_DETAIL_DOMAIN))
            {
                if (selectedNode != null)
                {
                    actionRequest.getPortletSession().setAttribute(
                            SecurityResources.PAM_CURRENT_USER, selectedNode,
                            PortletSession.APPLICATION_SCOPE);
                }
            }
        }
    }
    
    private TreeControl buildTree(Iterator users, Locale locale)
    {

        TreeControlNode root = new TreeControlNode(SECURITY_NODE_ID, // node id
                null, // icon
                getMessage(MSG_SECURITY_ROOT, locale), // title
                SecurityResources.PORTLET_URL, null, // target window
                true, // expand initially
                SECURITY_DOMAIN); // domain

        TreeControl control = new TreeControl(root);

        TreeControlNode userTree = new TreeControlNode(USER_NODE_ID, // node id
                null, // icon
                getMessage(MSG_USER_ROOT, locale), // title
                SecurityResources.PORTLET_URL, null, // target window
                false, // expand initially
                USER_DOMAIN); // domain
        root.addChild(userTree);

        while (users.hasNext())
        {
            User user = (User) users.next();
            Principal principal = getPrincipal(user.getSubject(), UserPrincipal.class);

            TreeControlNode userNode = new TreeControlNode(principal.getName(), null, principal.getName(),
                    SecurityResources.PORTLET_URL, null, false, USER_DETAIL_DOMAIN);
            userTree.addChild(userNode);
        }

        return control;
    }

    private Principal getPrincipal(Subject subject, Class classe)
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

    /** Messages */
    private static final String MSG_SECURITY_ROOT = "tree.security.root";

    private static final String MSG_USER_ROOT = "tree.user.root";

    private String getMessage(String key, Locale locale)
    {
        return getResourceBundle(locale).getString(key);
    }
    
    

}