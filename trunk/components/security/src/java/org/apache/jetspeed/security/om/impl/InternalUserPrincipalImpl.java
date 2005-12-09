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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.jetspeed.security.om.InternalUserPrincipal;

/**
 * <p>{@link InternalUserPrincipal} interface implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class InternalUserPrincipalImpl extends InternalPrincipalImpl implements InternalUserPrincipal
{
    /** The serial version uid. */
    private static final long serialVersionUID = 6713096308414915156L;

    /** <p>User principal security class.</p> */
    static String USER_PRINCIPAL_CLASSNAME = "org.apache.jetspeed.security.InternalUserPrincipalImpl";
    
    /** The credentials. */
    private Collection credentials;
    
    /** The role principals. */
    private Collection rolePrincipals;
    
    /** The group principals. */
    private Collection groupPrincipals;

    /**
     * <p>InternalUserPrincipal implementation default constructor.</p>
     */
    public InternalUserPrincipalImpl()
    {
        super();
    }
    
    /**
     * <p>Constructor to create a new user principal and its credential given
     * a username and password.</p>
     * @param username The username.
     */
    public InternalUserPrincipalImpl(String username)
    {
        super(USER_PRINCIPAL_CLASSNAME, username);
        this.rolePrincipals = new ArrayList();
        this.groupPrincipals = new ArrayList();
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalUserPrincipal#getCredentials()
     */
    public Collection getCredentials()
    {
        return this.credentials;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalUserPrincipal#setCredentials(java.util.Collection)
     */
    public void setCredentials(Collection credentials)
    {
        this.credentials = credentials;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalUserPrincipal#getRolePrincipals()
     */
    public Collection getRolePrincipals()
    {
        return this.rolePrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalUserPrincipal#setRolePrincipals(java.util.Collection)
     */
    public void setRolePrincipals(Collection rolePrincipals)
    {
        this.rolePrincipals = rolePrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalUserPrincipal#getGroupPrincipals()
     */
    public Collection getGroupPrincipals()
    {
        return this.groupPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalUserPrincipal#setGroupPrincipals(java.util.Collection)
     */
    public void setGroupPrincipals(Collection groupPrincipals)
    {
        this.groupPrincipals = groupPrincipals;
    }

    /**
     * <p>Compares this {@link InternalUserPrincipal} to the provided user principal
     * and check if they are equal.</p>
     * return Whether the {@link InternalUserPrincipal} are equal.
     */
    public boolean equals(Object object)
    {  
        if (!(object instanceof InternalUserPrincipal))
            return false;

        InternalUserPrincipal r = (InternalUserPrincipal) object;
        boolean isEqual = (r.getFullPath().equals(this.getFullPath()));
        return isEqual;
    }

}
