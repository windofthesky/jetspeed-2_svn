/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.page.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;

/**
 * SecurityConstraintsImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class SecurityConstraintsImpl implements SecurityConstraints
{
    private String owner;
    private List constraints;
    private List constraintsRefs;

    private List securityConstraints;
    private List securityConstraintsRefs;

    /**
     * beforeUpdate
     *
     * Update persistent constraints using transient constraints.
     */
    public void beforeUpdate()
    {
        // synchronize persistent constraints
        if ((securityConstraints != null) && !securityConstraints.isEmpty())
        {
            // update constraints collection size
            if (constraints == null)
            {
                constraints = new ArrayList(securityConstraints.size());
            }
            while (constraints.size() < securityConstraints.size())
            {
                BaseSecurityConstraint constraint = newSecurityConstraint();
                constraint.setApplyOrder(constraints.size());
                constraints.add(constraint);
            }
            while (constraints.size() > securityConstraints.size())
            {
                constraints.remove(constraints.size()-1);
            }
            // update constraints
            Iterator updateIter0 = securityConstraints.iterator();
            Iterator updateIter1 = constraints.iterator();
            while (updateIter0.hasNext() && updateIter1.hasNext())
            {
                SecurityConstraint securityConstraint = (SecurityConstraint)updateIter0.next();
                BaseSecurityConstraint constraint = (BaseSecurityConstraint)updateIter1.next();
                constraint.setUserPrincipals(securityConstraint.getUsersList());
                constraint.setRolePrincipals(securityConstraint.getRolesList());
                constraint.setGroupPrincipals(securityConstraint.getGroupsList());
                constraint.setPermissions(securityConstraint.getPermissionsList());
            }
        }
        else
        {
            // empty constraints collection
            if (constraints != null)
            {
                constraints.clear();
            }
        }

        // synchronize persistent constraints references
        if ((securityConstraintsRefs != null) && !securityConstraintsRefs.isEmpty())
        {
            // update constraints references collection size
            if (constraintsRefs == null)
            {
                constraintsRefs = new ArrayList(securityConstraintsRefs.size());
            }
            while (constraintsRefs.size() < securityConstraintsRefs.size())
            {
                BaseSecurityConstraintsRef constraintsRef = newSecurityConstraintsRef();
                constraintsRef.setApplyOrder(constraintsRefs.size());
                constraintsRefs.add(constraintsRef);
            }
            while (constraintsRefs.size() > securityConstraintsRefs.size())
            {
                constraintsRefs.remove(constraintsRefs.size()-1);
            }
            // update constraints references
            Iterator updateIter0 = securityConstraintsRefs.iterator();
            Iterator updateIter1 = constraintsRefs.iterator();
            while (updateIter0.hasNext() && updateIter1.hasNext())
            {
                String securityConstraintsRef = (String)updateIter0.next();
                BaseSecurityConstraintsRef constraintsRef = (BaseSecurityConstraintsRef)updateIter1.next();
                constraintsRef.setName(securityConstraintsRef);
            }
        }
        else
        {
            // empty constraints references collection
            if (constraintsRefs != null)
            {
                constraintsRefs.clear();
            }
        }
    }

    /**
     * afterLookup
     *
     * Update transient constraints from persistent constraints.
     */
    public void afterLookup()
    {
        // synchronize constraints
        if ((constraints != null) && !constraints.isEmpty())
        {
            // initialize security constraints collection
            if (securityConstraints == null)
            {
                securityConstraints = new ArrayList(constraints.size());
            }
            else
            {
                securityConstraints.clear();
            }
            // construct security constraints
            Iterator updateIter = constraints.iterator();
            while (updateIter.hasNext())
            {
                BaseSecurityConstraint constraint = (BaseSecurityConstraint)updateIter.next();
                SecurityConstraint securityConstraint = new SecurityConstraintImpl();
                securityConstraint.setUsers(constraint.getUserPrincipals());
                securityConstraint.setRoles(constraint.getRolePrincipals());
                securityConstraint.setGroups(constraint.getGroupPrincipals());
                securityConstraint.setPermissions(constraint.getPermissions());
                securityConstraints.add(securityConstraint);
            }
        }
        else
        {
            // remove security constraints collection
            securityConstraints = null;
        }

        // synchronize constraints references
        if ((constraintsRefs != null) && !constraintsRefs.isEmpty())
        {
            // update security constraints references
            if (securityConstraintsRefs == null)
            {
                securityConstraintsRefs = new ArrayList(constraintsRefs.size());
            }
            else
            {
                securityConstraintsRefs.clear();
            }
            Iterator updateIter = constraintsRefs.iterator();
            while (updateIter.hasNext())
            {
                BaseSecurityConstraintsRef constraintsRef = (BaseSecurityConstraintsRef)updateIter.next();
                securityConstraintsRefs.add(constraintsRef.getName());
            }
        }
        else
        {
            // remove security constraints references collection
            securityConstraintsRefs = null;
        }
    }

    /**
     * newSecurityConstraint
     *
     * Create new persistent constraint instance.
     */
    public BaseSecurityConstraint newSecurityConstraint()
    {
        // transient by default
        return null;
    }

    /**
     * newSecurityConstraintsRef
     *
     * Create new persistent constraints reference instance.
     */
    public BaseSecurityConstraintsRef newSecurityConstraintsRef()
    {
        // transient by default
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#getOwner()
     */
    public String getOwner()
    {
        return owner;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#setOwner(java.lang.String)
     */
    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#getSecurityConstraints()
     */
    public List getSecurityConstraints()
    {
        return securityConstraints;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#setSecurityConstraints(java.util.List)
     */
    public void setSecurityConstraints(List constraints)
    {
        securityConstraints = constraints;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#getSecurityConstraintsRefs()
     */
    public List getSecurityConstraintsRefs()
    {
        return securityConstraintsRefs;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#setSecurityConstraintsRefs(java.util.List)
     */
    public void setSecurityConstraintsRefs(List constraintsRefs)
    {
        securityConstraintsRefs = constraintsRefs;
    }
}
