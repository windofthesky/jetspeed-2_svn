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

import java.sql.Timestamp;
import java.util.Collection;

import org.apache.jetspeed.security.om.JetspeedGroupPrincipal;

/**
 * <p>{@link JetspeedGroupPrincipal} interface implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class JetspeedGroupPrincipalImpl extends JetspeedPrincipalImpl implements JetspeedGroupPrincipal
{
    /** <p>Group principal security class.</p> */
    static String GROUP_PRINCIPAL_CLASSNAME = "org.apache.jetspeed.security.JetspeedGroupPrincipalImpl";

    /**
     * <p>Group principal implementation default constructor.</p>
     */
    public JetspeedGroupPrincipalImpl()
    {
        super();
    }

    /**
     * <p>Constructor to create a new group principal.</p>
     * @param fullPath The group full path.
     */
    public JetspeedGroupPrincipalImpl(String fullPath)
    {
        super(GROUP_PRINCIPAL_CLASSNAME, fullPath);  
    }

    private Collection userPrincipals;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedGroupPrincipal#getUserPrincipals()
     */
    public Collection getUserPrincipals()
    {
        return this.userPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedGroupPrincipal#setUserPrincipals(java.util.Collection)
     */
    public void setUserPrincipals(Collection userPrincipals)
    {
        this.userPrincipals = userPrincipals;
    }

    private Collection rolePrincipals;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedGroupPrincipal#getRolePrincipals()
     */
    public Collection getRolePrincipals()
    {
        return this.rolePrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedGroupPrincipal#setRolePrincipals(java.util.Collection)
     */
    public void setRolePrincipals(Collection rolePrincipals)
    {
        this.rolePrincipals = rolePrincipals;
    }

    /**
     * <p>Compares this {@link JetspeedGroupPrincipal} to the provided group principal
     * and check if they are equal.</p>
     * return Whether the {@link JetspeedGroupPrincipal} are equal.
     */
    public boolean equals(Object object)
    {  
        if (!(object instanceof JetspeedGroupPrincipal))
            return false;

        JetspeedGroupPrincipal r = (JetspeedGroupPrincipal) object;
        boolean isEqual = (r.getFullPath().equals(this.getFullPath()));
        return isEqual;
    }
}
