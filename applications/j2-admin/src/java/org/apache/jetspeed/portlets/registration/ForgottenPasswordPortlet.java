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
import java.util.Date;
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
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.administration.AdministrationEmailException;
import org.apache.jetspeed.administration.PortalAdministration;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
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
public class ForgottenPasswordPortlet extends AbstractVelocityMessagingPortlet
{

    private PortalAdministration admin;

    private UserManager userManager;

    // Request Params 
    private static final String RP_EMAIL_ADDRESS = "email";

    // Messages 
    private static final String MSG_MESSAGE = "MSG";
    private static final String MSG_CHANGEDPW_MSG = "CH_PWD_MSD";

    // Context Variables
    private static final String CTX_EMAIL_ADDRESS = "email";

    private static final String CTX_RETURN_URL = "returnURL";

    private static final String CTX_NEW_PASSWORD = "password";

    private static final String CTX_USER_NAME = "username";

    private static final String CTX_MESSAGE = "MSG";

    private static final String CTX_CHANGEDPW_MSG = "updatedPWMsg";

    // Init Parameter Constants
    private static final String IP_REDIRECT_PATH = "redirectPath";

    private static final String IP_RETURN_URL = "returnURL";

    private static final String IP_TEMPLATE = "emailTemplate";

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

    //TODO do this in a DB
    Map hackMap;

    class UserPassword
    {

        String user;

        String password;
    }

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

        this.returnUrlPath = config.getInitParameter(IP_RETURN_URL);
        this.redirectPath = config.getInitParameter(IP_REDIRECT_PATH);
        this.template = config.getInitParameter(IP_TEMPLATE);

        hackMap = new HashMap();
    }

    private boolean isValidGUID(String guid)
    {
        // lookup the guid here 
        UserPassword m = (UserPassword) hackMap.get(guid);
        if (m != null) { return true; }
        return false;
    }

    private boolean updatePasswordFromGUID(String guid)
    {
        UserPassword m = (UserPassword) hackMap.get(guid);
        String userName = (String) m.user;
        String newPassword = (String) m.password;

        // Here's where a break should be.   The following code should be put into the RETURN portlet
        try
        {
            userManager.setPassword(userName, null, newPassword);
            userManager.setPasswordUpdateRequired(userName, true);
            // if we got here stuff is changed... removed the key from the map
            hackMap.remove(guid);
        } catch (SecurityException e)
        {
            return false;
        }
        return true;
    }

    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        response.setContentType("text/html");
        Context context = getContext(request);
        String email = request.getParameter(RP_EMAIL_ADDRESS);
        String guid = request.getParameter("guid");

        if (guid != null) 
        {
            if(isValidGUID(guid)) 
            {
                try
                {
                    updatePasswordFromGUID(guid);
                    context
                            .put(CTX_CHANGEDPW_MSG,
                                    "Your password has been updated!  Please login using it!");
                } catch (Exception e)
                {
                    context
                            .put(CTX_CHANGEDPW_MSG,
                                    "<font color=\"red\">unable to update your password, try again please</font>");
                }
            } else {
                // invalid GUID
                context
                .put(CTX_CHANGEDPW_MSG,
                        "<font color=\"red\">I'm sorry that change password link is invalid</font>");
            }
        } else {
            // might be returning from initial request
            context.put(CTX_CHANGEDPW_MSG,consumeRenderMessage(request, MSG_CHANGEDPW_MSG));
        }
        context.put(CTX_EMAIL_ADDRESS, email);
        context.put(CTX_MESSAGE, consumeRenderMessage(request, MSG_MESSAGE));
        super.doView(request, response);
    }

    public static String makeGUID(String user, String newpw)
    {
        // This is a quicky version
        long num = (long) user.hashCode() + (long) newpw.hashCode(); //  Possible collisions here...
        long d = new Date().getTime();
        long val = num * d;
        String retval = Long.toHexString(val);
        return retval;
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
        } catch (Exception e)
        {
            // TODO: get message from localized messages
            publishRenderMessage(
                    request,
                    MSG_MESSAGE,
                    makeMessage("Sorry but we could not find this email address on file. Are you sure you typed it in correctly?"));
            return;
        }

        try
        {
            String userName = getUserName(user);

            String newPassword = admin.generatePassword();

            String urlGUID = makeGUID(userName, newPassword);

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
            userAttributes.put(CTX_RETURN_URL, generateReturnURL(request,
                    response, urlGUID));
            userAttributes.put(CTX_NEW_PASSWORD, newPassword);
            userAttributes.put(CTX_USER_NAME, userName);
            if (this.template == null) { throw new Exception(
                    "email template not available"); }
            admin.sendEmail(this.getPortletConfig(), email,
                    getEmailSubject(request), this.template, userAttributes);

            //TODO this is currently hacked with a hashmap... needs to move to either a DB table
            // or to some sort of credential
            UserPassword up = new UserPassword();
            up.user = userName;
            up.password = newPassword;
            hackMap.put(urlGUID, up);

            publishRenderMessage(
                    request,
                    MSG_CHANGEDPW_MSG,
                    // TODO: localize this!
                    makeMessage("An email has been sent to you.  Please follow the link in the email"));
            
            response.sendRedirect(generateRedirectURL(request, response));
        } 
        catch (AdministrationEmailException e)
        {
            publishRenderMessage(request, CTX_MESSAGE, makeMessage(e
                    .getMessage()));
        } 
        catch (Exception e)
        {
            publishRenderMessage(request, CTX_MESSAGE,
                    makeMessage("Failed to send password: " + e.toString()));
        }

    }

    protected String getEmailSubject(PortletRequest request)
    {
        ResourceBundle resource = getPortletConfig().getResourceBundle(
                request.getLocale());
        try
        {
            this.emailSubject = resource.getString(RB_EMAIL_SUBJECT);
        } catch (Exception e)
        {
            //TODO  report missing resource somehow
        }
        if (this.emailSubject == null)
                this.emailSubject = "Password Notification";
        return this.emailSubject;
    }

    protected String generateReturnURL(PortletRequest request,
                                       PortletResponse response,
                                       String urlGUID)
    {
        String fullPath = this.returnUrlPath + "?guid=" + urlGUID; 
        // NOTE: getPortalURL will encode the fullPath for us
        String url = admin.getPortalURL(request, response, fullPath);
        return url;
    }

    protected String generateRedirectURL(PortletRequest request,
                                         PortletResponse response)
                                         
    {
        return admin.getPortalURL(request, response, this.redirectPath);
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
                principal = (Principal) o;
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
                credential = (PasswordCredential) o;
                char[] charar = credential.getPassword();

                return new String(charar);
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
