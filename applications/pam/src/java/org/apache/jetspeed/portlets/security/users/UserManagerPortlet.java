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

import java.security.Principal;
import java.util.Iterator;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.security.auth.Subject;

import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.portals.bridges.myfaces.FacesPortlet;

/**
 * Provides maintenance capabilities for User Administration.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class UserManagerPortlet extends FacesPortlet
{
    private UserManager userManager;

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        userManager = (UserManager) getPortletContext().getAttribute(
                PortletApplicationResources.CPS_USER_MANAGER_COMPONENT);
        if (null == userManager)
        {
            throw new PortletException("Failed to find the User Manager on portlet initialization");
        }
        System.out.println("user manager = " + userManager);
        try
        {
            Iterator users = userManager.getUsers("");
            while (users.hasNext())
            {
                User user = (User) users.next();
                System.out.println("++++ User = " + user);
                Principal principal = getPrincipal(user.getSubject(), UserPrincipal.class);
                System.out.println("principal = " + principal.getName());
            }
        }
        catch (SecurityException se)
        {
            throw new PortletException(se);
        }
    }

    protected void preProcessFaces(FacesContext context)    
    {
        System.out.println("*** pre processing faces for user manager: " + context);
    }
    
    public Principal getPrincipal(Subject subject, Class classe)
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