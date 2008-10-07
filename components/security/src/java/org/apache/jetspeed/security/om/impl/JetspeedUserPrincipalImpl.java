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
package org.apache.jetspeed.security.om.impl;

import java.util.Collection;

import org.apache.jetspeed.security.om.JetspeedUserPrincipal;

/**
 * <p>{@link JetspeedUserPrincipal} interface implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class JetspeedUserPrincipalImpl extends JetspeedPrincipalImpl implements JetspeedUserPrincipal
{
    /** <p>User principal security class.</p> */
    static String USER_PRINCIPAL_CLASSNAME = "org.apache.jetspeed.security.JetspeedUserPrincipalImpl";

    /**
     * <p>JetspeedUserPrincipal implementation default constructor.</p>
     */
    public JetspeedUserPrincipalImpl()
    {
        super();
    }
    
    /**
     * <p>Constructor to create a new user principal and its credential given
     * a username and password.</p>
     * @param username The username.
     */
    public JetspeedUserPrincipalImpl(String username)
    {
        super(USER_PRINCIPAL_CLASSNAME, username);
    }

    private Collection credentials;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedUserPrincipal#getCredentials()
     */
    public Collection getCredentials()
    {
        return this.credentials;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedUserPrincipal#setCredentials(java.util.Collection)
     */
    public void setCredentials(Collection credentials)
    {
        this.credentials = credentials;
    }

    private Collection rolePrincipals;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedUserPrincipal#getRolePrincipals()
     */
    public Collection getRolePrincipals()
    {
        return this.rolePrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedUserPrincipal#setRolePrincipals(java.util.Collection)
     */
    public void setRolePrincipals(Collection rolePrincipals)
    {
        this.rolePrincipals = rolePrincipals;
    }

    private Collection groupPrincipals;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedUserPrincipal#getGroupPrincipals()
     */
    public Collection getGroupPrincipals()
    {
        return this.groupPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedUserPrincipal#setGroupPrincipals(java.util.Collection)
     */
    public void setGroupPrincipals(Collection groupPrincipals)
    {
        this.groupPrincipals = groupPrincipals;
    }

    /**
     * <p>Compares this {@link JetspeedUserPrincipal} to the provided user principal
     * and check if they are equal.</p>
     * return Whether the {@link JetspeedUserPrincipal} are equal.
     */
    public boolean equals(Object object)
    {  
        if (!(object instanceof JetspeedUserPrincipal))
            return false;

        JetspeedUserPrincipal r = (JetspeedUserPrincipal) object;
        boolean isEqual = (r.getFullPath().equals(this.getFullPath()));
        return isEqual;
    }

}
