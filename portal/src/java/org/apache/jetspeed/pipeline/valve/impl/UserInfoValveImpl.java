/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.pipeline.valve.impl;

import java.security.Principal;
import java.util.prefs.Preferences;

import javax.portlet.PortletRequest;
import javax.security.auth.Subject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.UserInfoValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityHelper;

/**
 * <p>{@link UserInfoValve} interface implementation.</p>
 * 
 * @author <a href="">David Le Strat</a>
 */
public class UserInfoValveImpl extends AbstractValve implements UserInfoValve
{
    /** Logger */
    private static final Log log = LogFactory.getLog(UserInfoValveImpl.class);

    /** The user manager */
    UserManager userMgr;

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        try
        {
            log.info("Invoking UserInfoValveImpl...");
            Subject subject = request.getSubject();

            if (null == subject)
            {
                request.setAttribute(PortletRequest.USER_INFO, null);
                log.info(PortletRequest.USER_INFO + " is set to null");
            }
            else
            {
                log.info("Got subject...");
                Principal userPrincipal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
                if (null == userPrincipal)
                {
                    request.setAttribute(PortletRequest.USER_INFO, null);
                    log.info(PortletRequest.USER_INFO + " is set to null");
                }
                else
                {
                    log.info("Got user principal: " + userPrincipal.getName());
                
                    // Get the user manager
                    // Todo: Remove when valves are componentized.
                    UserManager userMgr = (UserManager) Jetspeed.getComponentManager().getComponent(UserManager.class);

                    try
                    {
                        User user = userMgr.getUser(userPrincipal.getName());
                        Preferences prefs = user.getPreferences();
                        
                        // Which property set do we want to make available to the portlet?
                        //prefs.
                        // Create the map from the preferences.
                    }
                    catch (SecurityException sex)
                    {
                        log.warn("Unexpected SecurityException in UserInfoValveImpl", sex);
                    }
                }
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            context.invokeNext(request);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "UserInfoValveImpl";
    }

}
