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
package org.apache.jetspeed.security.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.UserSecurityProvider;
import org.apache.jetspeed.security.spi.UserSecurityHandler;

/**
 * @see org.apache.jetspeed.security.UserSecurityProvider
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class UserSecurityProviderImpl implements UserSecurityProvider
{
    /** The list of {@link UserSecurityHandler}. */
    private List userSecurityHandlers = new ArrayList();
    
    /**
     * <p>Constructor providing the configured {@link UserSecurityHandler}.</p>
     */
    public UserSecurityProviderImpl(List userSecurityHandlers)
    {
        this.userSecurityHandlers = userSecurityHandlers;
    }
    
    /**
     * @see org.apache.jetspeed.security.UserSecurityProvider#getUserSecurityHandlers()
     */
    public List getUserSecurityHandlers()
    {
        return this.userSecurityHandlers;
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#getUserPrincipal(java.lang.String)
     */
    public Principal getUserPrincipal(String username)
    {
        Principal userPrincipal = null;
        
        for (int i = 0; i < userSecurityHandlers.size(); i++)
        {
            UserSecurityHandler userSecurityHandler = (UserSecurityHandler) userSecurityHandlers.get(i);
            userPrincipal = userSecurityHandler.getUserPrincipal(username);
        }
            
        return userPrincipal;
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#getUserPrincipals(java.lang.String)
     */
    public Iterator getUserPrincipals(String filter)
    {
        Iterator userPrincipals = (new ArrayList()).iterator();
        
        for (int i = 0; i < userSecurityHandlers.size(); i++)
        {
            UserSecurityHandler userSecurityHandler = (UserSecurityHandler) userSecurityHandlers.get(i);
            userPrincipals = userSecurityHandler.getUserPrincipals(filter);
        }

        return userPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#setUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void setUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        for (int i = 0; i < userSecurityHandlers.size(); i++)
        {
            UserSecurityHandler userSecurityHandler = (UserSecurityHandler) userSecurityHandlers.get(i);
            userSecurityHandler.setUserPrincipal(userPrincipal);
        }
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#removeUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void removeUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        for (int i = 0; i < userSecurityHandlers.size(); i++)
        {
            UserSecurityHandler userSecurityHandler = (UserSecurityHandler) userSecurityHandlers.get(i);
            userSecurityHandler.removeUserPrincipal(userPrincipal);
        }
    }

}
