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
package org.apache.jetspeed.security;

import java.security.Principal;
import java.util.Iterator;

import javax.security.auth.Subject;

import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;

/**
 * <p>Security helper.</p>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SecurityHelper
{
    /**
     * <p>Given a subject, finds the first principal of the given classe for that subject.
     * If a principal of the given classe is not found, null is returned.</p>
     * @param subject The subject supplying the principals.
     * @param classe A class or interface derived from java.security.JetspeedPrincipal.
     * @return The first principal matching a principal classe parameter.
     */
    public static Principal getPrincipal(Subject subject, Class classe)
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

    /**
     * <p>Given a subject, finds the first principal of the given classe for that subject.
     * If a principal of the given classe is not found, then the first
     * other principal is returned. If the list is empty, null is returned.</p>
     * @param subject The subject supplying the principals.
     * @param classe A class or interface derived from java.security.JetspeedPrincipal.
     * @return The first principal matching a principal classe parameter.
     */
    public static Principal getBestPrincipal(Subject subject, Class classe)
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
            else
            {
                if (principal == null)
                {
                    principal = p;
                }
            }
        }
        return principal;
    }

    public static String getPrincipalFullPath(Principal principal)
    {
        
        if ((UserPrincipal.class).isInstance(principal))
        {
            return UserPrincipalImpl.getFullPathFromPrincipalName(principal.getName());
        }
        else if ((RolePrincipal.class).isInstance(principal))
        {
            return RolePrincipalImpl.getFullPathFromPrincipalName(principal.getName());
        }
        else if ((GroupPrincipal.class).isInstance(principal))
        {
            return GroupPrincipalImpl.getFullPathFromPrincipalName(principal.getName());
        }
        else
        {
            return null;
        }
    }
}
