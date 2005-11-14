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
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;

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

    private List allConstraints;

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

    /**
     * checkConstraints
     *
     * @param actions actions to check
     * @param userPrincipals principal users list
     * @param rolePrincipals principal roles list
     * @param groupPrincipals principal group list
     * @param pageSecurity page security definitions
     * @throws SecurityException
     */
    public void checkConstraints(List actions, List userPrincipals, List rolePrincipals, List groupPrincipals, PageSecurity pageSecurity) throws SecurityException
    {
        // if owner defined, override all constraints and allow all access
        if ((owner != null) && (userPrincipals != null) && userPrincipals.contains(owner))
        {
            return;
        }

        // skip missing or empty constraints: permit all access
        List checkConstraints = getAllSecurityConstraints(pageSecurity);
        if ((checkConstraints != null) && !checkConstraints.isEmpty())
        {
            // test each action, constraints check passes only
            // if all actions are permitted for principals
            Iterator actionsIter = actions.iterator();
            while (actionsIter.hasNext())
            {
                // check each action:
                // - if any actions explicity permitted, assume no permissions
                //   are permitted by default
                // - if all constraints do not specify a permission, assume
                //   access is permitted by default
                String action = (String)actionsIter.next();
                boolean actionPermitted = false;
                boolean actionNotPermitted = false;
                boolean anyActionsPermitted = false;
                
                // check against constraints
                Iterator checkConstraintsIter = checkConstraints.iterator();
                while (checkConstraintsIter.hasNext())
                {
                    SecurityConstraintImpl constraint = (SecurityConstraintImpl)checkConstraintsIter.next();
                    
                    // if permissions specified, attempt to match constraint
                    if (constraint.getPermissions() != null)
                    {
                        // explicit actions permitted
                        anyActionsPermitted = true;

                        // test action permission match and user/role/group principal match
                        if (constraint.actionMatch(action) &&
                            constraint.principalsMatch(userPrincipals, rolePrincipals, groupPrincipals, true))
                        {
                            actionPermitted = true;
                            break;
                        }
                    }
                    else
                    {
                        // permissions not specified: not permitted if any principal matched
                        if (constraint.principalsMatch(userPrincipals, rolePrincipals, groupPrincipals, false))
                        {
                            actionNotPermitted = true;
                            break;
                        }
                    }
                }
                
                // fail if any action not permitted
                if ((!actionPermitted && anyActionsPermitted) || actionNotPermitted)
                {
                    throw new SecurityException("SecurityConstraintsImpl.checkConstraints(): Access for " + action + " not permitted.");
                }
            }
        }
    }

    /**
     * resetCachedSecurityConstraints
     */
    public void resetCachedSecurityConstraints()
    {
        // clear previously cached security constraints
        clearAllSecurityConstraints();
    }

    /**
     * getAllSecurityConstraints
     *
     * @param pageSecurity page security definitions
     * @return all security constraints
     */
    private synchronized List getAllSecurityConstraints(PageSecurity pageSecurity)
    {
        // return previously cached security constraints
        if (allConstraints != null)
        {
            return allConstraints;
        }

        // construct new ordered security constraints list
        allConstraints = new ArrayList(8);

        // add any defined security constraints
        if ((securityConstraints != null) && !securityConstraints.isEmpty())
        {
            allConstraints.addAll(securityConstraints);
        }

        // add any security constraints references
        if ((securityConstraintsRefs != null) && !securityConstraintsRefs.isEmpty())
        {
            List referencedConstraints = dereferenceSecurityConstraintsRefs(securityConstraintsRefs, pageSecurity);
            if (referencedConstraints != null)
            {
                allConstraints.addAll(referencedConstraints);
            }
        }
        
        // add any global decurity constraints references
        if (pageSecurity != null)
        {
            List globalConstraintsRefs = pageSecurity.getGlobalSecurityConstraintsRefs();
            if ((globalConstraintsRefs != null) && !globalConstraintsRefs.isEmpty())
            {
                List referencedConstraints = dereferenceSecurityConstraintsRefs(globalConstraintsRefs, pageSecurity);
                if (referencedConstraints != null)
                {
                    allConstraints.addAll(referencedConstraints);
                }
            }
        }
        
        return allConstraints;
    }

    /**
     * clearAllSecurityConstraints
     */
    private synchronized void clearAllSecurityConstraints()
    {
        // clear previously cached security constraints
        allConstraints = null;
    }

    /**
     * dereferenceSecurityConstraintsRefs
     *
     * @param constraintsRefs contstraints references to be dereferenced
     * @param pageSecurity page security definitions
     * @return security constraints
     */
    private List dereferenceSecurityConstraintsRefs(List constraintsRefs, PageSecurity pageSecurity)
    {
        List constraints = null;
        if (pageSecurity != null)
        {   
            // dereference each security constraints definition
            Iterator constraintsRefsIter = constraintsRefs.iterator();
            while (constraintsRefsIter.hasNext())
            {
                String constraintsRef = (String)constraintsRefsIter.next();
                SecurityConstraintsDef securityConstraintsDef = pageSecurity.getSecurityConstraintsDef(constraintsRef);
                if ((securityConstraintsDef != null) && (securityConstraintsDef.getSecurityConstraints() != null))
                {
                    if (constraints == null)
                    {
                        constraints = new ArrayList(constraintsRefs.size());
                    }
                    constraints.addAll(securityConstraintsDef.getSecurityConstraints());
                }
            }
        }
        return constraints;
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
        // save new setting and reset cached security constraints
        this.owner = owner;
        clearAllSecurityConstraints();
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
        // save new setting and reset cached security constraints
        securityConstraints = constraints;
        clearAllSecurityConstraints();
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
        // save new setting and reset cached security constraints
        securityConstraintsRefs = constraintsRefs;
        clearAllSecurityConstraints();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#isEmpty()
     */
    public boolean isEmpty()
    {
        return ((owner == null) && (securityConstraints == null) && (securityConstraintsRefs == null));
    }
}
