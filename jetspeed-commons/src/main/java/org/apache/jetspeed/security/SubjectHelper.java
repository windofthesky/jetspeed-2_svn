/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.security.auth.Subject;

/**
 * <p>
 * Subject helper.
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SubjectHelper
{
    /**
     * <p>
     * Given a subject, finds the first principal of the given classe for that subject. If a
     * principal of the given classe is not found, null is returned.
     * </p>
     * 
     * @param subject The subject supplying the principals.
     * @param classe A class or interface derived from java.security.InternalPrincipal.
     * @return The first principal matching a principal classe parameter.
     */
    public static Principal getPrincipal(Subject subject, Class<? extends Principal> classe)
    {
        Principal principal = null;
        Set<Principal> principalList = subject.getPrincipals();
        if (principalList != null)
        { 
        	Iterator<Principal> principals = subject.getPrincipals().iterator();
	        while (principals.hasNext())
	        {
	            Principal p = principals.next();
	            if (classe.isInstance(p))
	            {
	                principal = p;
	                break;
	            }
	        }
        }
        return principal;
    }

    /**
     * <p>
     * Given a subject, finds the first principal of the given classe for that subject. If a
     * principal of the given classe is not found, then the first other principal is returned. If
     * the list is empty, null is returned.
     * </p>
     * 
     * @param subject The subject supplying the principals.
     * @param classe A class or interface derived from java.security.InternalPrincipal.
     * @return The first principal matching a principal classe parameter.
     */
    public static Principal getBestPrincipal(Subject subject, Class<? extends Principal> classe)
    {

        Principal principal = null;
        Iterator<Principal> principals = subject.getPrincipals().iterator();
        while (principals.hasNext())
        {
            Principal p = principals.next();
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
    
    /**
     * <p>
     * Returns the first matching principal of a given type.
     * </p>
     * 
     * @param principals The array of pricinpals
     * @param classe The class of Principal
     * @return The principal.
     */
    public static Principal getBestPrincipal(Principal[] principals, Class<? extends Principal> classe)
    {

        Principal principal = null;
        for (int i = 0; i < principals.length; i++)
        {
            Principal p = principals[i];
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

    /**
     * <p>
     * Given a subject, finds all principals of the given classe for that subject. If no principals
     * of the given class is not found, null is returned.
     * </p>
     * 
     * @param subject The subject supplying the principals.
     * @param classe A class or interface derived from java.security.InternalPrincipal.
     * @return A List of all principals of type Principal matching a principal classe parameter.
     */
    public static List<Principal> getPrincipals(Subject subject, Class<? extends Principal> classe)
    {
        List<Principal> result = new LinkedList<Principal>();
        Iterator<Principal> principals = subject.getPrincipals().iterator();
        while (principals.hasNext())
        {
            Principal p = principals.next();
            if (classe.isInstance(p))
            {
                result.add(p);
            }
        }
        return result;
    }
    
    /**
     * <p>
     * Given a subject, finds all principals of the given JetspeedPrincipalType(JPT)  for that subject. If no principals
     * of the given class is not found, null is returned.
     * </p>
     * 
     * @param subject The subject supplying the principals.
     * @param jptName the name of the Jetspeed Principal Type
     * @return A List of all principals of type JetspeedPrincipal matching a JPT name parameter.
     */
    public static List<JetspeedPrincipal> getPrincipals(Subject subject, String jptName)
    {
        List<JetspeedPrincipal> result = new LinkedList<JetspeedPrincipal>();
        Iterator<Principal> principals = subject.getPrincipals().iterator();
        while (principals.hasNext())
        {
            Principal p = principals.next();
            if (p instanceof JetspeedPrincipal)
            {
                JetspeedPrincipal jp = (JetspeedPrincipal)p;
                if (jp.getType().getName().equals(jptName))
                {
                    result.add(jp);
                }
            }
        }
        return result;
    }    
    /**
     * <p>
     * Given a subject, finds a specific principal by name of the given classe for that subject.
     * </p>
     * 
     * @param subject The subject supplying the principals.
     * @param classe A class or interface derived from java.security.InternalPrincipal.
     * @param name the name of the principal to look for
     */
    public static Principal getPrincipal(Subject subject, Class<? extends Principal> classe, String name)
    {
        Iterator<Principal> principals = subject.getPrincipals().iterator();
        while (principals.hasNext())
        {
            Principal p = principals.next();
            if (classe.isInstance(p) && p.getName().equals(name))
            {
                return p;
            }
        }
        return null;
    }

    /**
     * <p>
     * Given a subject, find the (first) UserCredential from the private credentials
     * </p>
     * 
     * @param subject The subject
     * @return the UserCredential or null if not found.
     */
    public static UserCredential getUserCredential(Subject subject)
    {
        Iterator<Object> iter = subject.getPrivateCredentials().iterator();
        while (iter.hasNext())
        {
            Object o = iter.next();
            if (o instanceof UserCredential)
            {
                return (UserCredential) o;
            }
        }
        return null;
    }
}
