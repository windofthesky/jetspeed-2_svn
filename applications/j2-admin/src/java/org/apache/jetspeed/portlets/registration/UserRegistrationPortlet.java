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
import java.io.Serializable;
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
import org.apache.jetspeed.administration.AdministrationEmailException;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.portals.bridges.frameworks.model.ModelBean;
import org.apache.portals.bridges.velocity.AbstractVelocityMessagingPortlet;
import org.apache.portals.gems.util.ValidationHelper;
import org.apache.velocity.context.Context;

/**
 * This portlet allows a logged on user to change its password.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer</a>
 * @version $Id: $
 */
public class UserRegistrationPortlet extends AbstractVelocityMessagingPortlet
{

    private PortalAdministration admin;

    private UserManager userManager;

    // commonly USED attributes

    private static final String USER_ATTRIBUTE_EMAIL = "user.business-info.online.email";

    // Messages 
    private static final String MSG_MESSAGE = "MSG";

    private static final String MSG_USERINFO = "user";

    private static final String MSG_REGED_USER_MSG = "registeredUserMsg";

    // Init Parameters
    private static final String IP_ROLES = "roles"; // comma separated    

    private static final String IP_GROUPS = "groups"; // comma separated

    private static final String IP_EMAIL_TEMPLATE = "emailTemplate";

    private static final String IP_RULES_NAMES = "rulesNames";

    private static final String IP_RULES_VALUES = "rulesValues";

    private static final String IP_REDIRECT_PATH = "redirectPath";

    private static final String IP_RETURN_URL = "returnURL";

    private static final String IP_OPTION_EMAILS_SYSTEM_UNIQUE = "Option_Emails_System_Unique";

    private static final String IP_OPTION_GENERATE_PASSWORDS = "Option_Generate_Passwords";

    private static final String IP_OPTION_USE_EMAIL_AS_USERNAME = "Option_Use_Email_As_Username";

    // Context Variables
    private static final String CTX_RETURN_URL = "returnURL";

    private static final String CTX_MESSAGE = "MSG";

    private static final String CTX_USERINFO = "user";

    private static final String CTX_REGED_USER_MSG = "registeredUserMsg";

    private static final String CTX_OPTION_GENERATE_PASSWORDS = "CTX_Option_Generate_Passwords";

    private static final String CTX_OPTION_USE_EMAIL_AS_USERNAME = "CTX_Option_Use_Email_As_Username";

    // Resource Bundle
    private static final String RB_EMAIL_SUBJECT = "email.subject.registration";

    /** email template to use for merging */
    private String emailTemplate;

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

    /** will force the passwords to be generated instead of picked by the user */
    private boolean optionForceGeneratedPasswords = false;

    /** will use cause the portlet to use a user request username instead otherwise forces emailaddress */
    private boolean optionForceEmailAsUsername = true;

    /** will check to make sure the email address is unique to the system */
    private boolean optionForceEmailsToBeSystemUnique = true;

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        admin = (PortalAdministration) getPortletContext().getAttribute(
                CommonPortletServices.CPS_PORTAL_ADMINISTRATION);
        if (null == admin) { throw new PortletException(
                "Failed to find the Portal Administration on portlet initialization"); }
        userManager = (UserManager) getPortletContext().getAttribute(
                CommonPortletServices.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager) { throw new PortletException(
                "Failed to find the User Manager on portlet initialization"); }

        // roles
        this.roles = getInitParameterList(config, IP_ROLES);

        // groups
        this.groups = getInitParameterList(config, IP_GROUPS);

        // rules (name,value pairs)
        List names = getInitParameterList(config, IP_RULES_NAMES);
        List values = getInitParameterList(config, IP_RULES_VALUES);
        rules = new HashMap();
        for (int ix = 0; ix < ((names.size() < values.size()) ? names.size()
                : values.size()); ix++)
        {
            rules.put(names.get(ix), values.get(ix));
        }

        this.emailTemplate = config.getInitParameter(IP_EMAIL_TEMPLATE);

        // user attributes ? 

        this.optionForceEmailsToBeSystemUnique = Boolean.getBoolean(config
                .getInitParameter(IP_OPTION_EMAILS_SYSTEM_UNIQUE));
        this.optionForceGeneratedPasswords = Boolean.getBoolean(config
                .getInitParameter(IP_OPTION_GENERATE_PASSWORDS));
        this.optionForceEmailAsUsername = Boolean.getBoolean(config
                .getInitParameter(IP_OPTION_USE_EMAIL_AS_USERNAME));
        if (this.optionForceEmailAsUsername)
        {
            // just to be sure
            this.optionForceEmailsToBeSystemUnique = true;
        }
    }

    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        response.setContentType("text/html");
        Context context = getContext(request);
        context.put(CTX_USERINFO, this.receiveRenderMessage(request,
                MSG_USERINFO));
        context.put(CTX_MESSAGE, consumeRenderMessage(request, MSG_MESSAGE));
        
        // if this is non-null, then we know that we registered
        context.put(CTX_REGED_USER_MSG, consumeRenderMessage(request,
                MSG_REGED_USER_MSG));

        // next two control the existence of some of the fields in the form
        if (this.optionForceEmailAsUsername)
        {
            context.put(CTX_OPTION_USE_EMAIL_AS_USERNAME, "TRUE");
        }
        if (this.optionForceGeneratedPasswords)
        {
            context.put(CTX_OPTION_GENERATE_PASSWORDS, "TRUE");
        }

        super.doView(request, response);
    }

    private Object getBeanFromSession(PortletRequest request, ModelBean mb)
    {
        return request.getPortletSession().getAttribute(makeModelBeanKey(mb));
    }

    private void clearBeanFromSession(PortletRequest request, ModelBean mb)
    {
        System.out.println("Clearing bean " + makeModelBeanKey(mb));
        request.getPortletSession().removeAttribute(makeModelBeanKey(mb));
    }

    private void putBeanInSession(PortletRequest request, ModelBean mb,
            Object bean)
    {
        if (bean instanceof Serializable)
        {
            request.getPortletSession()
                    .setAttribute(makeModelBeanKey(mb), bean);
        }
    }

    private String makeModelBeanKey(ModelBean mb)
    {
        return "ModelBean:" + mb.getBeanName();
    }

    /*   
     protected Object formToBean(ActionRequest request, String view, ModelBean mb) throws PortletException
     {

     // try to get the bean from the session first
     Object bean = getBeanFromSession(request, mb);
     if (bean == null)
     {
     bean = model.createBean(mb);
     if (bean == null) { throw new PortletException("Portlet Action error in creating bean for view: " + view); }
     putBeanInSession(request, mb, bean);
     }

     Map params = request.getParameterMap();
     try
     {
     BeanUtils.populate(bean, params);
     }
     catch (Exception e)
     {
     throw new PortletException("Portlet Action error in  populating bean: " + mb.getBeanName(), e);
     }
     return bean;
     }
     */

    protected static String[] formValues =
    { "user.name.family", "user.name.given", "user.business-info.online.email",
            "user.name", "password", "verifyPassword", "user.department",
            "user.employer"};

    public void processAction(ActionRequest actionRequest,
            ActionResponse actionResponse) throws PortletException, IOException
    {
        List errors = new LinkedList();

        Map userAttributes = new HashMap();

        Map userInfo = new HashMap();

        for (int i = 0; i < formValues.length; i++)
        {
            String key = formValues[i];
            String value = actionRequest.getParameter(key);
            if (value != null)
            {
                userInfo.put(key, value);
                if (key.startsWith("user."))
                {
                    // we'll assume that these map back to PLT.D  values
                    userAttributes.put(key, value);
                }
            }
        }

        if (!ValidationHelper.isAny((String) userInfo.get("user.name.given"),
                true, 25))
        {
            // TODO: get error message from localized resource
            errors.add("You must enter a first name");
        }
        if (!ValidationHelper.isAny((String) userInfo.get("user.name.family"),
                true, 25))
        {
            // TODO: get error message from localized resource
            errors.add("You must enter a last name");
        }
        if (!ValidationHelper.isAny((String) userInfo.get("user.name"), true,
                25))
        {
            // TODO: get error message from localized resource
            errors.add("You must enter a username");
        }
        if (!ValidationHelper.isEmailAddress((String) userInfo
                .get(USER_ATTRIBUTE_EMAIL), true, 80))
        {
            // TODO: get error message from localized resource
            errors.add("Please enter a valid Email address.");
        }
        if (!this.optionForceGeneratedPasswords)
        {
            if (!ValidationHelper.isAny((String) userInfo.get("password"),
                    true, 25))
            {
                // TODO: get error message from localized resource
                errors.add("You must enter a password");
            }
        }

        if (optionForceEmailAsUsername)
        {
            // force user.name to be same as email
            userInfo.put("user.name", userInfo.get(USER_ATTRIBUTE_EMAIL));
        }

        boolean userIdExistsFlag = true;
        try
        {
            User user = userManager.getUser((String) userInfo.get("user.name"));
        } catch (SecurityException e)
        {
            userIdExistsFlag = false;
        }
        //
        if (userIdExistsFlag)
        {
            // TODO: localize messages
            errors
                    .add("Requested User ID already exists.  Please select another User Id.");
            publishRenderMessage(actionRequest, MSG_MESSAGE, errors);
            return;
        }
        if (optionForceEmailsToBeSystemUnique)
        {
            boolean emailExistsFlag = true;
            User user = null;
            try
            {
                user = admin.lookupUserFromEmail((String) userInfo
                        .get(USER_ATTRIBUTE_EMAIL));
            } catch (AdministrationEmailException e1)
            {
                emailExistsFlag = false;
            }
            if ((emailExistsFlag) || (user != null))
            {
                // TODO: localize messages
                errors
                        .add("The requested email address is already being used in the system.  If you already have an account, consider using the forgotten password portlet to retreive your password");
                publishRenderMessage(actionRequest, MSG_MESSAGE, errors);
                return;
            }

        }

        try
        {
            if (optionForceGeneratedPasswords)
            {
                String password = admin.generatePassword();
                userInfo.put("user.password", password);
            } else
            {
                if (userInfo.get("password").equals(
                        userInfo.get("verifyPassword")))
                {

                } else
                {
                    //                  TODO: localize messages
                    errors
                            .add("The two passwords do not match, please re-type them");
                    publishRenderMessage(actionRequest, MSG_MESSAGE, errors);
                    return;
                }
            }
        } catch (Exception e)
        {
            // TODO: localize messages
            errors.add("Failed to add user. " + e.toString());
            publishRenderMessage(actionRequest, MSG_MESSAGE, errors);
        }
        // make sure no errors have occurred
        if (errors.size() > 0)
        {
            publishRenderMessage(actionRequest, MSG_MESSAGE, errors);
        }

        // Ok, we think we're good to go, let's create the user!
        try
        {
            admin.registerUser((String) userInfo.get("user.name"),
                    (String) userInfo.get("password"), this.roles, this.groups,
                    userAttributes, // note use of only PLT.D  values here.
                    rules, null); // passing in null causes use of default template

            String urlGUID = ForgottenPasswordPortlet.makeGUID(
                    (String) userInfo.get("user.name"), (String) userInfo
                            .get("password"));

            userInfo.put(CTX_RETURN_URL, generateReturnURL(actionRequest,
                    actionResponse, urlGUID));

            if (this.emailTemplate == null) { throw new Exception(
                    "email template not available"); }

            admin.sendEmail(getPortletConfig(), (String) userInfo
                    .get(USER_ATTRIBUTE_EMAIL), getEmailSubject(actionRequest),
                    this.emailTemplate, userInfo);

            publishRenderMessage(actionRequest, MSG_REGED_USER_MSG,
                    "You have completed the user registration process.  Please login above");

            actionResponse.sendRedirect(this.redirectPath);

        } catch (Exception e)
        {
            // TODO: localize messages
            errors.add("Failed to add user. " + e.toString());
            publishRenderMessage(actionRequest, MSG_MESSAGE, errors);
        }
    }

    protected String getEmailSubject(PortletRequest request)
    {
        ResourceBundle resource = getPortletConfig().getResourceBundle(
                request.getLocale());
        try
        {
            this.emailSubject = resource.getString(RB_EMAIL_SUBJECT);
        } catch (java.util.MissingResourceException mre)
        {
            this.emailSubject = null;
        }
        if (this.emailSubject == null)
                this.emailSubject = "Registration Confirmation";
        return this.emailSubject;
    }

    protected List getInitParameterList(PortletConfig config, String ipName)
    {
        String temp = config.getInitParameter(ipName);
        if (temp == null) return new ArrayList();

        String[] temps = temp.split("\\,");
        for (int ix = 0; ix < temps.length; ix++)
            temps[ix] = temps[ix].trim();

        return Arrays.asList(temps);
    }

    protected String generateReturnURL(ActionRequest request,
            ActionResponse response, String urlGUID)
    {
        return this.returnUrlPath + "?newUserGUID=" + urlGUID;
    }

}
