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
package org.apache.jetspeed.security.impl;

import org.apache.jetspeed.security.BasePrincipal;

/**
 * <p>{@link BasePrincipal} interface implementation.</p>
 * @author <a href="mailto:david@sensova.com">David Le Strat</a>
 */
public class BasePrincipalImpl implements BasePrincipal
{
    private final String name;
    private final String fullPath;

    /**
     * <p>Principal constructor given a name and preferences root.</p>
     * @param name The principal name.
     * @param prefsRoot The preferences root node.
     */
    public BasePrincipalImpl(String name, String prefsRoot)
    {
        this.name = name;
        this.fullPath = getFullPathFromPrincipalName(name, prefsRoot);
    }

    /**
     * @see org.apache.jetspeed.security.BasePrincipal#getFullPath()
     */
    public String getFullPath()
    {
        return this.fullPath;
    }

    /**
     * @see java.security.Principal#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * <p>Returns a string representation of this principal.</p>
     * @return A string representation of this principal.
     */
    public String toString()
    {
        return this.name;
    }

    /**
     * <p>Gets the principal implementation full path from the principal name.</p>
     * <p>Prepends PREFS_{PRINCIPAL}_ROOT if not prepended.</p>        
     * @param name The principal name.
     * @param prefsRoot The preferences root node.
     * @return The preferences full path / principal name.
     */
    public static String getFullPathFromPrincipalName(String name, String prefsRoot)
    {
        String fullPath = name;
        if (!fullPath.startsWith(prefsRoot))
        {
            if (fullPath.startsWith("/"))
            {
                fullPath = fullPath.substring(1, fullPath.length());
            }
            fullPath = prefsRoot + fullPath;
        }
        return fullPath;
    }

    /**
     * <p>Gets the principal name from the principal implementation full path.</p>
     * <p>Remove prepended PREFS_{PRINCIPAL}_ROOT if present.</p>        
     * @param fullPath The principal full path.
     * @param prefsRoot The preferences root node.
     * @return The principal name.
     */
    public static String getPrincipalNameFromFullPath(String fullPath, String prefsRoot)
    {
        String name = fullPath;
        if (name.startsWith(prefsRoot))
        {
            if (prefsRoot.equals(UserPrincipalImpl.PREFS_USER_ROOT))
            {
                name = name.substring(prefsRoot.length(), name.length());
            }
            else
            {
                name = name.substring(prefsRoot.length() - 1, name.length());
            }
        }
        return name;
    }

}
