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
import java.util.Iterator;
import java.util.Locale;

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
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.webapp.admin.TreeControl;
import org.apache.webapp.admin.TreeControlNode;

/**
 * This portlet is a browser over all the portlet applications in the system.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class UserBrowserPortlet extends ServletPortlet
{
    private UserManager manager;
    
    /** the id of the tree control */
    private static final String TREE_CONTROL = "j2_tree";
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
    
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);                       
        manager = (UserManager)getPortletContext().getAttribute(PortletApplicationResources.CPS_USER_MANAGER_COMPONENT);
        if (null == manager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
    }
    
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        response.setContentType("text/html");
        
        TreeControl control = (TreeControl) request.getPortletSession().getAttribute(TREE_CONTROL);
        if(control == null)
        {
            Iterator users = manager.getUsers(USER_FILTER);
            control = buildTree(users, request.getLocale());
            request.getPortletSession().setAttribute(TREE_CONTROL, control);
        }
        request.setAttribute(TREE_CONTROL, control);
        
        super.doView(request, response);
        
    }

    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
    {
        TreeControl control = (TreeControl) actionRequest.getPortletSession().getAttribute(TREE_CONTROL);
        //assert control != null
        if(control != null)
        {
            // expand or contact non-leaf nodes
            String node = actionRequest.getParameter(PortletApplicationResources.REQUEST_NODE);
            if(node != null)
            {
                TreeControlNode controlNode = control.findNode(node);
                if(controlNode != null)
                {
                    controlNode.setExpanded(!controlNode.isExpanded());
                }
            }
            
            // select a node
            String selectedNode = actionRequest.getParameter(PortletApplicationResources.REQUEST_SELECT_NODE);
            if(selectedNode != null)
            {
                control.selectNode(selectedNode);
                TreeControlNode child = control.findNode(selectedNode);
                if (child != null)
                {
                    String domain = child.getDomain();
                    if(domain.equals(USER_DETAIL_DOMAIN))
                    {
                        System.out.println("SELECTED NODE = " + selectedNode);
                        if (selectedNode != null)
                        {
                            actionRequest.getPortletSession().setAttribute(
                                    PortletApplicationResources.PAM_CURRENT_USER,
                                    selectedNode,
                                    PortletSession.APPLICATION_SCOPE);
                        }
                    }
                }
            }
        }
    }
            
    private TreeControl buildTree(Iterator users, Locale locale) 
    {       
        
        TreeControlNode root =
            new TreeControlNode(SECURITY_NODE_ID, // node id
                                null,  // icon 
                                getMessage(MSG_SECURITY_ROOT, locale), // title
                                PortletApplicationResources.PORTLET_URL,
                                null, // target window
                                true, // expand initially
                                SECURITY_DOMAIN); // domain
        
        TreeControl control = new TreeControl(root);
        
        
        TreeControlNode userTree = 
            new TreeControlNode(USER_NODE_ID, // node id 
                                null,  // icon
                                getMessage(MSG_USER_ROOT, locale), // title 
                                PortletApplicationResources.PORTLET_URL, 
                                null, // target window
                                false, // expand initially
                                USER_DOMAIN); // domain
        root.addChild(userTree);
        
        while (users.hasNext())
        {
            User user = (User)users.next();
            Principal principal = getPrincipal(user.getSubject(), UserPrincipal.class);
            
            TreeControlNode userNode = new TreeControlNode(principal.getName(), 
                                                           null, 
                                                           principal.getName(), 
                                                           PortletApplicationResources.PORTLET_URL, 
                                                           null, 
                                                           false, 
                                                           USER_DETAIL_DOMAIN);
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
