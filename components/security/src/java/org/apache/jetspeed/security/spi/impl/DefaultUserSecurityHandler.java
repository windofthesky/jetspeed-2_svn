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
package org.apache.jetspeed.security.spi.impl;

import java.security.Principal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalUserPrincipalImpl;
import org.apache.jetspeed.security.spi.SecurityAccess;
import org.apache.jetspeed.security.spi.UserSecurityHandler;

/**
 * @see org.apache.jetspeed.security.spi.UserSecurityHandler
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class DefaultUserSecurityHandler implements UserSecurityHandler
{
    /** Common queries. */
    private SecurityAccess commonQueries = null;
    
    /**
     * <p>Constructor providing access to the common queries.</p>
     */
    public DefaultUserSecurityHandler(SecurityAccess commonQueries)
    {
        this.commonQueries = commonQueries;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#isUserPrincipal(java.lang.String)
     */
    public boolean isUserPrincipal(String userName)
    {
        return commonQueries.getInternalUserPrincipal(userName, false) != null;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#getUserPrincipal(java.lang.String)
     */
    public Principal getUserPrincipal(String username)
    {
        UserPrincipal userPrincipal = null;
        InternalUserPrincipal internalUser = commonQueries.getInternalUserPrincipal(username, false);
        if (null != internalUser)
        {
            userPrincipal = new UserPrincipalImpl(UserPrincipalImpl.getPrincipalNameFromFullPath(internalUser.getFullPath()));
        }
        return userPrincipal;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#getUserPrincipals(java.lang.String)
     */
    public List getUserPrincipals(String filter)
    {
        List userPrincipals = new LinkedList();
        Iterator result = commonQueries.getInternalUserPrincipals(filter);
        while (result.hasNext())
        {
            InternalUserPrincipal internalUser = (InternalUserPrincipal) result.next();
            String path = internalUser.getFullPath();
            if (path == null || !path.startsWith(UserPrincipal.PREFS_USER_ROOT)) // TODO: FIXME: the extend shouldn't return roles!
            {
                continue;
            }
            userPrincipals.add(new UserPrincipalImpl(UserPrincipalImpl.getPrincipalNameFromFullPath(internalUser.getFullPath())));
        }
        return userPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#addUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void addUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        String fullPath = userPrincipal.getFullPath();
        InternalUserPrincipal internalUser = new InternalUserPrincipalImpl(fullPath);
        commonQueries.setInternalUserPrincipal(internalUser, false);        
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#updateUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void updateUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        String fullPath = userPrincipal.getFullPath();
        InternalUserPrincipal internalUser = new InternalUserPrincipalImpl(fullPath);
        commonQueries.setInternalUserPrincipal(internalUser, false);        
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#removeUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void removeUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        InternalUserPrincipal internalUser = commonQueries.getInternalUserPrincipal(userPrincipal.getName(), false);
        if (null != internalUser)
        {
            commonQueries.removeInternalUserPrincipal(internalUser);
        }
        else
        {
            internalUser = commonQueries.getInternalUserPrincipal(userPrincipal.getName(), true);
            if (null != internalUser)
            {
                commonQueries.removeInternalUserPrincipal(internalUser);
            }
        }
    }

}
