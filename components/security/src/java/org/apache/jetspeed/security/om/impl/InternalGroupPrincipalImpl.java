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

import org.apache.jetspeed.security.om.InternalGroupPrincipal;

/**
 * <p>{@link InternalGroupPrincipal} interface implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class InternalGroupPrincipalImpl extends InternalPrincipalImpl implements InternalGroupPrincipal
{
    /** <p>Group principal security class.</p> */
    static String GROUP_PRINCIPAL_CLASSNAME = "org.apache.jetspeed.security.InternalGroupPrincipalImpl";

    /**
     * <p>Group principal implementation default constructor.</p>
     */
    public InternalGroupPrincipalImpl()
    {
        super();
    }

    /**
     * <p>Constructor to create a new group principal.</p>
     * @param fullPath The group full path.
     */
    public InternalGroupPrincipalImpl(String fullPath)
    {
        super(GROUP_PRINCIPAL_CLASSNAME, fullPath);  
    }

    private Collection userPrincipals;

    /**
     * @see org.apache.jetspeed.security.om.InternalGroupPrincipal#getUserPrincipals()
     */
    public Collection getUserPrincipals()
    {
        return this.userPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalGroupPrincipal#setUserPrincipals(java.util.Collection)
     */
    public void setUserPrincipals(Collection userPrincipals)
    {
        this.userPrincipals = userPrincipals;
    }

    private Collection rolePrincipals;

    /**
     * @see org.apache.jetspeed.security.om.InternalGroupPrincipal#getRolePrincipals()
     */
    public Collection getRolePrincipals()
    {
        return this.rolePrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalGroupPrincipal#setRolePrincipals(java.util.Collection)
     */
    public void setRolePrincipals(Collection rolePrincipals)
    {
        this.rolePrincipals = rolePrincipals;
    }

    /**
     * <p>Compares this {@link InternalGroupPrincipal} to the provided group principal
     * and check if they are equal.</p>
     * return Whether the {@link InternalGroupPrincipal} are equal.
     */
    public boolean equals(Object object)
    {  
        if (!(object instanceof InternalGroupPrincipal))
            return false;

        InternalGroupPrincipal r = (InternalGroupPrincipal) object;
        boolean isEqual = (r.getFullPath().equals(this.getFullPath()));
        return isEqual;
    }
}
