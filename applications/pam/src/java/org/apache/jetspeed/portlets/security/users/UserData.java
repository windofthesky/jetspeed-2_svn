package org.apache.jetspeed.portlets.security.users;

import java.security.Principal;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.security.auth.Subject;

import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;


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
