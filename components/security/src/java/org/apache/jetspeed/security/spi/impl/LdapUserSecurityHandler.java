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
import java.util.LinkedList;
import java.util.List;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.spi.UserSecurityHandler;

/**
 * @see org.apache.jetspeed.security.spi.UserSecurityHandler
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class LdapUserSecurityHandler implements UserSecurityHandler
{
    /**
     * <p>
     * Default Constructor.
     * </p>
     */
    public LdapUserSecurityHandler()
    {
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#getUserPrincipal(java.lang.String)
     */
    public Principal getUserPrincipal(String username)
    {
        UserPrincipal userPrincipal = null;
        if (username.equals("ldap1"))
        {
            userPrincipal = new UserPrincipalImpl(UserPrincipalImpl.getPrincipalNameFromFullPath("/user/ldap1"));
        }
        else if (username.equals("ldap2"))
        {
            userPrincipal = new UserPrincipalImpl(UserPrincipalImpl.getPrincipalNameFromFullPath("/user/ldap2"));
        }
        else if (username.equals("ldap3"))
        {
            userPrincipal = new UserPrincipalImpl(UserPrincipalImpl.getPrincipalNameFromFullPath("/user/ldap3"));
        }
        return userPrincipal;
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#getUserPrincipals(java.lang.String)
     */
    public List getUserPrincipals(String filter)
    {
        List userPrincipals = new LinkedList();
        userPrincipals.add(new UserPrincipalImpl(UserPrincipalImpl.getPrincipalNameFromFullPath("/user/ldap1")));
        userPrincipals.add(new UserPrincipalImpl(UserPrincipalImpl.getPrincipalNameFromFullPath("/user/ldap2")));
        userPrincipals.add(new UserPrincipalImpl(UserPrincipalImpl.getPrincipalNameFromFullPath("/user/ldap3")));

        return userPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#setUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void setUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        // To implement.
    }
    
    /**
     * @see org.apache.jetspeed.security.spi.UserSecurityHandler#removeUserPrincipal(org.apache.jetspeed.security.UserPrincipal)
     */
    public void removeUserPrincipal(UserPrincipal userPrincipal) throws SecurityException
    {
        // To implement        
    }

}
