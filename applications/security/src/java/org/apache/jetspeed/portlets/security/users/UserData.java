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
import javax.security.auth.Subject;

import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;

/**
 * User data table bean (JSF).
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */

public class UserData
{
    private static final UserBean[] xusers = new UserBean[]     
    { 
            new UserBean("Taylor", "David"),
            new UserBean("Weaver", "Scott"),
            new UserBean("Ford", "Jeremy")
    };
    
    public UserBean[] getUsers()
    {
        try
        {
            //Context ctx = new InitialContext();
            //UserManager userManager = (UserManager)ctx.lookup("java:comp/UserManager");
            PortletServices services = JetspeedPortletServices.getSingleton();
            UserManager userManager = 
                (UserManager)services.getService("UserManager");
            
            Map appMap = (Map)FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
            UserManager u2 = (UserManager)appMap.get(SecurityResources.CPS_USER_MANAGER_COMPONENT);                        
            System.out.println("u2 == " + u2);
            System.out.println("um == " + userManager);
            if (userManager == null)
            {
                return xusers;
            }
            UserBean[] xu = new UserBean[8];
            Iterator it = userManager.getUsers("");
            int ix = 0;
            while (it.hasNext())
            {
                User user = (User)it.next();
                Principal princ = getPrincipal(user.getSubject(), UserPrincipal.class);
                UserBean bean  = new UserBean(princ.getName(), princ.getName());
                xu[ix] = bean;
                ix++;
                //preferences.node(userinfo)
            }
            return xu;
        }
        catch (Exception e)
        { 
            e.printStackTrace();
        }
        return xusers;
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
