/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
