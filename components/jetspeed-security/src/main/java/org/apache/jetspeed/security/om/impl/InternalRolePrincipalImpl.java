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
package org.apache.jetspeed.security.om.impl;

import java.util.Collection;

import org.apache.jetspeed.security.om.InternalGroupPrincipal;
import org.apache.jetspeed.security.om.InternalRolePrincipal;
import org.apache.jetspeed.security.om.InternalUserPrincipal;

/**
 * <p>{@link InternalRolePrincipal} interface implementation.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class InternalRolePrincipalImpl extends InternalPrincipalImpl implements InternalRolePrincipal
{
    /** The serial version uid. */
    private static final long serialVersionUID = 4422827842052325846L;   
    /** <p>Role principal security class.</p> */
    static String ROLE_PRINCIPAL_CLASSNAME = "org.apache.jetspeed.security.InternalRolePrincipalImpl";

    private Collection<InternalGroupPrincipal> groupPrincipals;
    private Collection<InternalUserPrincipal> userPrincipals;

    /**
     * <p>Role principal implementation default constructor.</p>
     */
    public InternalRolePrincipalImpl()
    {
        super();
    }

    /**
     * <p>Constructor to create a new role principal.</p>
     * @param name The role principal name
     */
    public InternalRolePrincipalImpl(String name)
    {
        super(ROLE_PRINCIPAL_CLASSNAME, ROLE_TYPE, name);
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalRolePrincipal#getUserPrincipals()
     */
    public Collection<InternalUserPrincipal> getUserPrincipals()
    {
        return this.userPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalRolePrincipal#setUserPrincipals(java.util.Collection)
     */
    public void setUserPrincipals(Collection<InternalUserPrincipal> userPrincipals)
    {
        this.userPrincipals = userPrincipals;
    }


    /**
     * @see org.apache.jetspeed.security.om.InternalRolePrincipal#getGroupPrincipals()
     */
    public Collection<InternalGroupPrincipal> getGroupPrincipals()
    {
        return this.groupPrincipals;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalRolePrincipal#setGroupPrincipals(java.util.Collection)
     */
    public void setGroupPrincipals(Collection<InternalGroupPrincipal> groupPrincipals)
    {
        this.groupPrincipals = groupPrincipals;
    }

    /**
     * <p>Compares this {@link InternalRolePrincipal} to the provided role principal
     * and check if they are equal.</p>
     * return Whether the {@link InternalRolePrincipal} are equal.
     */
    public boolean equals(Object object)
    {  
        if (!(object instanceof InternalRolePrincipal))
            return false;

        InternalRolePrincipal r = (InternalRolePrincipal) object;
        boolean isEqual = (r.getName().equals(this.getName()) && r.getType().equals(this.getType()));
        return isEqual;
    }

}
