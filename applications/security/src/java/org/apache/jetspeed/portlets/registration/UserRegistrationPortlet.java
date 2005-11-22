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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.velocity.context.Context;

/**
 * This portlet allows a logged on user to change its password.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class UserRegistrationPortlet extends AbstractVelocityMessagingPortlet
{
    private PortalAdministration admin;
    private UserManager userManager;
 

    // Messages 
    private static final String MSG_MESSAGE = "MSG";    
    private static final String MSG_USERINFO = "user";
    
    // Init Parameters
    private static final String IP_ROLES = "roles"; // comma separated    
    private static final String IP_GROUPS = "groups"; // comma separated
    private static final String IP_TEMPLATE = "template";
    private static final String IP_RULES_NAMES = "rulesNames";
    private static final String IP_RULES_VALUES = "rulesValues";
    private static final String IP_REDIRECT_PATH = "redirectPath";    
    private static final String IP_RETURN_URL = "returnURL";
    
    // Context Variables
    private static final String CTX_RETURN_URL = "returnURL";
    private static final String CTX_MESSAGE = "MSG";
    private static final String CTX_USERINFO = "user";    
    
    // Resource Bundle
    private static final String RB_EMAIL_SUBJECT = "email.subject.registration";
    
    /** email template to use for merging */
    private String template;

    /** localized emailSubject */
    private String emailSubject = null;
    
    /** path where to redirect to after pressing submit on the form */
    private String redirectPath;

    /** servlet path of the return url to be printed and href'd in email template */
    private String returnUrlPath;
    
    /** roles */
    private List roles;
    
    /** groups */
    private List groups;
    
    /** profile rules */
    private Map rules;
    
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
        
        // roles
        this.roles = getInitParameterList(config, IP_ROLES);
        
        // groups
        this.groups = getInitParameterList(config, IP_GROUPS);
        
        // rules (name,value pairs)
        List names = getInitParameterList(config, IP_RULES_NAMES);
        List values = getInitParameterList(config, IP_RULES_VALUES);
        rules = new HashMap();
        for (int ix = 0; ix < ((names.size() < values.size()) ? names.size() : values.size()); ix++)
        {
            rules.put(names.get(ix), values.get(ix));
        }
   
        // user attributes ? 
        
        this.template = config.getInitParameter(IP_TEMPLATE);
        this.redirectPath = config.getInitParameter(IP_REDIRECT_PATH);
        this.returnUrlPath = config.getInitParameter(IP_RETURN_URL);        
    }
    
    public void doView(RenderRequest request,
                       RenderResponse response) 
        throws PortletException, IOException 
    {
        response.setContentType("text/html");                
        Context context = getContext(request);        
        context.put(CTX_USERINFO, this.receiveRenderMessage(request, MSG_USERINFO));
        context.put(CTX_MESSAGE, consumeRenderMessage(request, MSG_MESSAGE));        
        super.doView(request, response);                
    }
    
    public void processAction(ActionRequest actionRequest,
            ActionResponse actionResponse) throws PortletException, IOException 
    {
        List errors = new LinkedList();
        
        // TODO: determine which PLT.D Information to register with
        String familyName = actionRequest.getParameter("user.name.family");
        String givenName = actionRequest.getParameter("user.name.given");     
        String email = actionRequest.getParameter("user.email");
        
        String userName = actionRequest.getParameter("user.name");
        String password = actionRequest.getParameter("password");
        String verifyPassword = actionRequest.getParameter("verifyPassword");

        // TODO: Validation
        
        // Could have two modes (init param or pref) of user name creation:
        //  1. use email address
        //  2. request user id
        // Could have two modes (init param or pref) of password creation
        //  1. generate new password automatically
        //  2. enter a new password and verify
        
        // TODO: add select user attributes to map
        Map userInfo = new HashMap();
        
        boolean requestUserId = true;
        if (requestUserId)
        {
            boolean userIdExistsFlag = true;
            try
            {
                User user = userManager.getUser(userName);
            } 
            catch (SecurityException e) 
            {
                userIdExistsFlag = false;
            }
    //
            if (userIdExistsFlag) 
            {
                // TODO: localize messages
                errors.add("Requested User ID already exists.  Please select another User Id.");
                publishRenderMessage(actionRequest, MSG_MESSAGE, errors);
                return;
            }
        }
        
        try 
        {
            boolean generatePasswordOption = true;
            if (generatePasswordOption)
            {
                password = admin.generatePassword();
            }
            
            admin.registerUser(userName, 
                               password, 
                               this.roles, 
                               this.groups, 
                               userInfo, 
                               rules, 
                               template);
          
            // special attributes
            userInfo.put(CTX_RETURN_URL, generateReturnURL(actionRequest, actionResponse));
            
            admin.sendEmail(email, getEmailSubject(actionRequest), this.template, userInfo);
            
            actionResponse.sendRedirect(this.redirectPath);

        } 
        catch (Exception e) 
        {
            // TODO: localize messages
            errors.add("Failed to add user. " + e.toString());
            publishRenderMessage(actionRequest, MSG_MESSAGE, errors);            
        }
    }
    
    protected String getEmailSubject(PortletRequest request)
    {
        ResourceBundle resource = getPortletConfig().getResourceBundle(request.getLocale());
        this.emailSubject = resource.getString(RB_EMAIL_SUBJECT);
        if (this.emailSubject == null)
            this.emailSubject = "Registration Confirmation";
        return this.emailSubject;
    }

    protected List getInitParameterList(PortletConfig config, String ipName)
    {
        String temp = config.getInitParameter(ipName);
        if (temp == null)
            return new ArrayList();
        
        String[] temps = temp.split("\\,");
        for (int ix = 0; ix < temps.length; ix++)
            temps[ix] = temps[ix].trim();        
        
        return Arrays.asList(temps);        
    }
    
    protected String generateReturnURL(ActionRequest request, ActionResponse response)
    {
        // TODO: get the FULL PORTAL URL return address to login from init param        
        return "http://TODO-FIXME" + this.returnUrlPath; 
    }
    
}
