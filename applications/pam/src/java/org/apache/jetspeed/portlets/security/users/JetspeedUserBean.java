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

import javax.security.auth.Subject;

import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserPrincipal;

/**
 * User state.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedUserBean
{
    private String principal;
    
    public JetspeedUserBean(User user)
    {
        Principal userPrincipal = createPrincipal(user.getSubject(), UserPrincipal.class);             
        
        this.principal = userPrincipal.getName();
        System.out.println("principal = " + principal);
    }
    
    /**
     * @return Returns the principal.
     */
    public String getPrincipal()
    {
        return principal;
    }
    /**
     * @param principal The principal to set.
     */
    public void setPrincipal(String principal)
    {
        this.principal = principal;
    }
    
    public Principal createPrincipal(Subject subject, Class classe)
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