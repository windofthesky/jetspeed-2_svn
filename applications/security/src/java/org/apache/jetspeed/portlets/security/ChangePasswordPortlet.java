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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.PortalReservedParameters;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserManager;
import org.apache.pluto.core.impl.PortletRequestImpl;
import org.apache.portals.
bridges.common.GenericServletPortlet;

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
    public static final String WHY = "why";
    public static final String REQUIRED = "required";
    public static final String CANCELLED = "cancelled";
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        manager = (UserManager) getPortletContext().getAttribute(SecurityApplicationResources.CPS_USER_MANAGER_COMPONENT);
        if (null == manager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
    }
    
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        response.setContentType("text/html");

        if ( request.getUserPrincipal() != null )
        {
            // TODO 2004-11-18: Hack around PLUTO-83 bug which fix should be availabe in 1.0.1-rc2 
            //                  After the fix, the RequestContext should be retrieved from the RenderRequest
            //                  and the pluto depedency removed from this project project.xml
            HttpServletRequest req = (HttpServletRequest)((PortletRequestImpl)request).getRequest();
            RequestContext requestContext = (RequestContext)req.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
            Integer passwordDaysValid = (Integer)requestContext.getAttribute(PasswordCredential.PASSWORD_CREDENTIAL_DAYS_VALID_REQUEST_ATTR_KEY);
            
            if ( passwordDaysValid != null )
            {
                ResourceBundle bundle = ResourceBundle.getBundle("org.apache.jetspeed.portlets.security.resources.ChgPwdResources",request.getLocale());
                if ( passwordDaysValid.intValue() < 1 )
                {
                    request.setAttribute(WHY,bundle.getString("chgpwd.message.change.required"));
                    request.setAttribute(REQUIRED,Boolean.TRUE);
                }
                else if ( passwordDaysValid.intValue() == 1 )
                {
                    request.setAttribute(WHY,bundle.getString("chgpwd.message.expires.today"));
                    request.setAttribute(REQUIRED,Boolean.TRUE);
                }
                else
                {
                    MessageFormat mf = new MessageFormat(bundle.getString("chgpwd.message.expires.in.days"));
                    request.setAttribute(WHY,mf.format(new Integer[]{passwordDaysValid}));
                }
            }
            
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
        }
        super.doView(request, response);
    }
    
    public void processAction(ActionRequest actionRequest, ActionResponse actionResponse) throws PortletException,
    IOException
    {
        if ( actionRequest.getUserPrincipal() != null )
        {
            ResourceBundle bundle = ResourceBundle.getBundle("org.apache.jetspeed.portlets.security.resources.ChgPwdResources",actionRequest.getLocale());

            ArrayList errorMessages = new ArrayList();
            
            String cancelled = actionRequest.getParameter(CANCELLED);
            if ( cancelled == null )
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
                        String userName = actionRequest.getUserPrincipal().getName();
                        manager.setPassword(userName, currPassword, newPassword);

                        // refresh/update Subject in session to reflect the changed PasswordCredential
                        Subject subject = manager.getUser(userName).getSubject();
                        RequestContext requestContext = (RequestContext)actionRequest.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);                  
                        requestContext.setSessionAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT, subject);
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
}
