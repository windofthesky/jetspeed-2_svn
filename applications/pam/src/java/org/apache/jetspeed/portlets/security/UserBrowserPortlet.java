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
        
        TreeControl control = (TreeControl) request.getPortletSession().getAttribute("j2_tree");
        if(control == null)
        {
            Iterator users = manager.getUsers("");
            control = buildTree(users, request.getLocale());
            request.getPortletSession().setAttribute("j2_tree", control);
        }
        request.setAttribute("j2_tree", control);
        
        super.doView(request, response);
        
    }

    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException, IOException
    {
        TreeControl control = (TreeControl) actionRequest.getPortletSession().getAttribute("j2_tree");
        //assert control != null
        if(control != null)
        {
            String node = actionRequest.getParameter("node");
            if(node != null)
            {
                TreeControlNode controlNode = control.findNode(node);
                if(controlNode != null)
                {
                    controlNode.setExpanded(!controlNode.isExpanded());
                }
            }
            
            String selectedNode = actionRequest.getParameter(PortletApplicationResources.REQUEST_SELECT_NODE);
            if(selectedNode != null)
            {
                control.selectNode(selectedNode);
                TreeControlNode child = control.findNode(selectedNode);
                if(child != null)
                {
                    User user = null;
                    
                    String domain = child.getDomain();
                    if(domain.equals("USER_DOMAIN"))
                    {
                        System.out.println("SELECTED NODE = " + selectedNode);
                        /*pa = registry.getPortletApplicationByIdentifier(selectedNode);
                        if(pa != null)
                        {
                            actionRequest.getPortletSession().removeAttribute(PortletApplicationResources.REQUEST_SELECT_PORTLET, PortletSession.APPLICATION_SCOPE);
                        }*/
                    }
                    else
                    {
                        //warn about not recognized domain
                    }
                    
                    if (user != null)
                    {
                        // actionRequest.getPortletSession().setAttribute(PortletApplicationResources.PAM_CURRENT_PA, pa.getName(), PortletSession.APPLICATION_SCOPE);
                    }
                }
            }
        }
    }
    
    
    private TreeControl buildTree(Iterator users, Locale locale) 
    {       
        TreeControlNode root =
            new TreeControlNode("SECURITY-NODE",
                                null, "SECURITY_ROOT",
                                PortletApplicationResources.PORTLET_URL,
                                null, true, "SECURITY_DOMAIN");
        
        TreeControl control = new TreeControl(root);
        
        
        TreeControlNode userTree = 
            new TreeControlNode("USER_ROOT", 
                                null, 
                                "USER_ROOT", 
                                PortletApplicationResources.PORTLET_URL, 
                                null, 
                                false, 
                                "SECURITY_DOMAIN");
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
                                                           "USER_APP_DOMAIN");
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
    
}
