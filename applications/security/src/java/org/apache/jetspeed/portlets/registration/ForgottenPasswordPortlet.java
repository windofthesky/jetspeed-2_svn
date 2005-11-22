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
package org.apache.jetspeed.portlets.registration;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.AdministrationEmailException;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;
import org.apache.portals.gems.util.ValidationHelper;

/**
 * This portlet allows a logged on user to change its password.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class ForgottenPasswordPortlet extends AbstractVelocityMessagingPortlet
{
    private PortalAdministration admin;
    private UserManager userManager;    
    
    // Request Params 
    private static final String RP_EMAIL_ADDRESS = "email";

    // Messages 
    private static final String MSG_MESSAGE = "MSG";
    
    // Context Variables
    private static final String CTX_EMAIL_ADDRESS = "email";    
    private static final String CTX_RETURN_URL = "returnURL";
    private static final String CTX_NEW_PASSWORD = "password";
    private static final String CTX_USER_NAME = "username";
    private static final String CTX_MESSAGE = "MSG";

    // Init Parameter Constants
    private static final String IP_REDIRECT_PATH = "redirectPath";    
    private static final String IP_RETURN_URL = "returnURL";
    private static final String IP_TEMPLATE = "template";
    
    // Resource Bundle
    private static final String RB_EMAIL_SUBJECT = "email.subject.forgotten.password";
    
    /** email template to use for merging */
    private String template;
    /** servlet path of the return url to be printed and href'd in email template */
    private String returnUrlPath;
    /** path where to redirect to after pressing submit on the form */
    private String redirectPath;

    /** localized emailSubject */
    private String emailSubject = null;
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        admin = (PortalAdministration) 
                    getPortletContext().getAttribute(CommonPortletServices.CPS_PORTAL_ADMINISTRATION);
        if (null == admin)
        {
            throw new PortletException("Failed to find the Portal Administration on portlet initialization");
        }
        userManager = (UserManager) 
            getPortletContext().getAttribute(CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }        
        
        this.returnUrlPath = config.getInitParameter(IP_RETURN_URL);
        this.redirectPath = config.getInitParameter(IP_REDIRECT_PATH);
        this.template = config.getInitParameter(IP_TEMPLATE);
    }
    
    public void doView(RenderRequest request, RenderResponse response) 
    throws PortletException, IOException
    {
        response.setContentType("text/html");                
        Context context = getContext(request);        
        String email = request.getParameter(RP_EMAIL_ADDRESS);        
        context.put(CTX_EMAIL_ADDRESS, email);
        context.put(CTX_MESSAGE, consumeRenderMessage(request, MSG_MESSAGE));        
        super.doView(request, response);
    }

    public void processAction(ActionRequest request, ActionResponse response) 
        throws PortletException, IOException
    {
        List errors = new LinkedList();
                            
        String email = request.getParameter(RP_EMAIL_ADDRESS);
        
        // validation
        if (!ValidationHelper.isEmailAddress(email, true, 80))
        {
            // TODO: get error message from localized resource
            errors.add("Please enter a valid Email address.");  
        }
        
        
        if (errors.size() > 0)
        {
            publishRenderMessage(request, MSG_MESSAGE, errors);
            return;
        }
        
        User user = null;
        try
        {
            user = admin.lookupUserFromEmail(email);
        } 
        catch (Exception e) 
        {
            // TODO: get message from localized messages
            publishRenderMessage(request, MSG_MESSAGE, 
                    makeMessage("Sorry but we could not find this email address on file. Are you sure you typed it in correctly?"));
            return;
        }

        try 
        {
            String userName = getUserName(user);
            String oldPassword = getPassword(user);
            String newPassword = admin.generatePassword();             
            userManager.setPassword(userName, oldPassword, newPassword);
            userManager.setPasswordUpdateRequired(userName, true);
            Preferences pref = user.getUserAttributes();
            String[] keys = pref.keys();
            Map userAttributes = new HashMap();
            if (keys != null)
            {
                for (int ix = 0; ix < keys.length; ix++)
                {
                    // TODO: how the hell do i tell the pref type
                    // ASSuming they are all strings (sigh)
                    userAttributes.put(keys[ix], pref.get(keys[ix], ""));
                }
            }
            // special attributes
            userAttributes.put(CTX_RETURN_URL, generateReturnURL(request, response));
            userAttributes.put(CTX_NEW_PASSWORD, newPassword);
            userAttributes.put(CTX_USER_NAME, userName);
            
            admin.sendEmail(email, getEmailSubject(request), this.template, userAttributes);
            
            response.sendRedirect(this.redirectPath);
        }
        catch (AdministrationEmailException e)
        {
            publishRenderMessage(request, CTX_MESSAGE, 
                    makeMessage(e.getMessage()));
        }
        catch (Exception e) 
        {
            publishRenderMessage(request, CTX_MESSAGE, makeMessage("Failed to send password: " + e.toString()));
        }
        
    }

    protected String getEmailSubject(PortletRequest request)
    {
        ResourceBundle resource = getPortletConfig().getResourceBundle(request.getLocale());
        this.emailSubject = resource.getString(RB_EMAIL_SUBJECT);
        if (this.emailSubject == null)
            this.emailSubject = "Password Notification";
        return this.emailSubject;
    }
    
    protected String generateReturnURL(ActionRequest request, ActionResponse response)
    {
        // TODO: get the FULL PORTAL URL return address to login from init param        
        return "http://TODO-FIXME" + this.returnUrlPath; 
    }
    
    protected String getUserName(User user)
    {
        Principal principal = null;
        Iterator principals = user.getSubject().getPrincipals().iterator();
        while (principals.hasNext())
        {      
            Object o = principals.next();
            if (o instanceof UserPrincipal)
            {
                principal = (Principal)o;
                return principal.toString();
            }
                
        }
        return null;        
    }

    protected String getPassword(User user)
    {
        PasswordCredential credential = null;
        
        Set credentials = user.getSubject().getPrivateCredentials();
        Iterator iter = credentials.iterator();
        while (iter.hasNext())
        {
            Object o = iter.next();
            if (o instanceof PasswordCredential)
            {
                credential = (PasswordCredential)o;
                return credential.toString();
            }
        }
        return null;                
    }
    
    protected List makeMessage(String msg)
    {
        List errors = new LinkedList();
        errors.add(msg);
        return errors;
    }

}
