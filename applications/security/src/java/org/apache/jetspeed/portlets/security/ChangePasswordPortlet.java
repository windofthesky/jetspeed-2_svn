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
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.common.GenericServletPortlet;

/**
 * This portlet allows a logged on user to change its password.
 *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class ChangePasswordPortlet extends GenericServletPortlet
{
    private UserManager manager;
    
    public static final String CURRENT_PASSWORD = "currentPassword";
    public static final String NEW_PASSWORD = "newPassword";
    public static final String NEW_PASSWORD_AGAIN = "newPasswordAgain";
    public static final String ERROR_MESSAGES = "errorMessages";
    public static final String PASSWORD_CHANGED = "passwordChanged";
    public static final String CPS_USER_MANAGER_COMPONENT = "cps:UserManager";
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        manager = (UserManager) getPortletContext().getAttribute(CPS_USER_MANAGER_COMPONENT);
        if (null == manager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
    }
    
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");
        
        ArrayList errorMessages = (ArrayList)request.getPortletSession().getAttribute(ERROR_MESSAGES);
        if (errorMessages != null )
        {
            request.getPortletSession().removeAttribute(ERROR_MESSAGES);
            request.setAttribute(ERROR_MESSAGES,errorMessages);
        }
        else
        {
            Boolean password_changed = (Boolean)request.getPortletSession().getAttribute(PASSWORD_CHANGED);
            if ( password_changed != null )
            {
                request.getPortletSession().removeAttribute(PASSWORD_CHANGED);
                request.setAttribute(PASSWORD_CHANGED,password_changed);
            }
        }        
        super.doView(request, response);
    }
    
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException,
    IOException
    {
        ResourceBundle bundle = ResourceBundle.getBundle("org.apache.jetspeed.portlets.security.resources.ChgPwdResources",actionRequest.getLocale());

        ArrayList errorMessages = new ArrayList();
        
        if ( actionRequest.getUserPrincipal() != null )
        {
            String currPassword = actionRequest.getParameter(CURRENT_PASSWORD);
            String newPassword = actionRequest.getParameter(NEW_PASSWORD);
            String newPasswordAgain = actionRequest.getParameter(NEW_PASSWORD_AGAIN);
        
            if (currPassword == null || currPassword.length() == 0)
            {
                errorMessages.add(bundle.getString("chgpwd.error.currentPasswordNull"));
                currPassword = null;
            }
            if (newPassword == null || newPassword.length() == 0)
            {
                errorMessages.add(bundle.getString("chgpwd.error.newPasswordNull"));
                newPassword = null;
            }
            if (newPassword != null && newPassword.length() == 0)
            {
                newPassword = null;
            }
            
            if (newPassword != null && 
                    (newPasswordAgain == null || (newPasswordAgain != null && !newPassword.equals(newPasswordAgain))))
            {
                errorMessages.add(bundle.getString("chgpwd.error.newPasswordsDoNotMatch"));
            }
            if ( errorMessages.size() == 0 )
            {
                try
                {
                    System.out.println("manager="+manager);
                    manager.setPassword(actionRequest.getUserPrincipal().getName(), currPassword, newPassword);
                }
                catch ( SecurityException e)
                {
                    errorMessages.add(e.getMessage());
                }
            }
            if ( errorMessages.size() > 0 )
            {
                actionRequest.getPortletSession().setAttribute(ERROR_MESSAGES,errorMessages);
            }
            else
            {
                actionRequest.getPortletSession().setAttribute(PASSWORD_CHANGED,Boolean.TRUE);
            }
        }
    }
}
