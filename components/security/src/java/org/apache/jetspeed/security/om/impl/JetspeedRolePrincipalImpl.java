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

import org.apache.jetspeed.security.om.JetspeedRolePrincipal;

/**
 * <p>{@link JetspeedRolePrincipal} interface implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class JetspeedRolePrincipalImpl extends JetspeedPrincipalImpl implements JetspeedRolePrincipal
{
    /** <p>Role principal security class.</p> */
    static String ROLE_PRINCIPAL_CLASSNAME = "org.apache.jetspeed.security.JetspeedRolePrincipalImpl";

    /**
     * <p>Role principal implementation default constructor.</p>
     */
    public JetspeedRolePrincipalImpl()
    {
        super();
    }

    /**
     * <p>Constructor to create a new role principal.</p>
     * @param fullPath The role full path.
     */
    public JetspeedRolePrincipalImpl(String fullPath)
    {
        super(ROLE_PRINCIPAL_CLASSNAME, fullPath);
    }

    private Collection userPrincipals;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedRolePrincipal#getUserPrincipals()
     */
    public Collection getUserPrincipals()
    {
        return this.userPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedRolePrincipal#setUserPrincipals(java.util.Collection)
     */
    public void setUserPrincipals(Collection userPrincipals)
    {
        this.userPrincipals = userPrincipals;
    }

    private Collection groupPrincipals;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedRolePrincipal#getGroupPrincipals()
     */
    public Collection getGroupPrincipals()
    {
        return this.groupPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedRolePrincipal#setGroupPrincipals(java.util.Collection)
     */
    public void setGroupPrincipals(Collection groupPrincipals)
    {
        this.groupPrincipals = groupPrincipals;
    }

    /**
     * <p>Compares this {@link JetspeedRolePrincipal} to the provided role principal
     * and check if they are equal.</p>
     * return Whether the {@link JetspeedRolePrincipal} are equal.
     */
    public boolean equals(Object object)
    {  
        if (!(object instanceof JetspeedRolePrincipal))
            return false;

        JetspeedRolePrincipal r = (JetspeedRolePrincipal) object;
        boolean isEqual = (r.getFullPath().equals(this.getFullPath()));
        return isEqual;
    }

}
