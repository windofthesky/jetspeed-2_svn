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
import java.util.ListIterator;

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

    private SecurityConstraintList securityConstraints;
    private SecurityConstraintsRefList securityConstraintsRefs;

    private List allConstraints;

    /**
     * accessConstraintsRefs
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List accessConstraintsRefs()
    {
        // create initial collection if necessary
        if (constraintsRefs == null)
        {
            constraintsRefs = new ArrayList(4);
        }
        return constraintsRefs;
    }

    /**
     * accessConstraints
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List accessConstraints()
    {
        // create initial collection if necessary
        if (constraints == null)
        {
            constraints = new ArrayList(4);
        }
        return constraints;
    }

    /**
     * getSecurityConstraintClass
     *
     * Return class of persistent constraint instance.
     *
     * @return constraint class
     */
    public Class getSecurityConstraintClass()
    {
        // none by default
        return null;
    }

    /**
     * getSecurityConstraintsRefClass
     *
     * Return class of persistent constraints reference instance.
     *
     * @return constraints reference class
     */
    public Class getSecurityConstraintsRefClass()
    {
        // none by default
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
        if ((getSecurityConstraintsRefs() != null) && !getSecurityConstraintsRefs().isEmpty())
        {
            List referencedConstraints = dereferenceSecurityConstraintsRefs(getSecurityConstraintsRefs(), pageSecurity);
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
    synchronized void clearAllSecurityConstraints()
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
        // return mutable inline constraint list
        // by using list wrapper to manage apply order
        if (securityConstraints == null)
        {
            securityConstraints = new SecurityConstraintList(this);
        }
        return securityConstraints;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#setSecurityConstraints(java.util.List)
     */
    public void setSecurityConstraints(List constraints)
    {
        // set inline constraints by replacing existing
        // entries with new elements if new collection
        // is specified
        List securityConstraints = getSecurityConstraints();
        if (constraints != securityConstraints)
        {
            // replace all constraints
            securityConstraints.clear();
            if (constraints != null)
            {
                securityConstraints.addAll(constraints);
            }
        }
        // reset cached security constraints
        clearAllSecurityConstraints();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#getSecurityConstraintsRefs()
     */
    public List getSecurityConstraintsRefs()
    {
        // return mutable constraints refs list
        // by using list wrapper to manage apply
        // order and element uniqueness
        if (securityConstraintsRefs == null)
        {
            securityConstraintsRefs = new SecurityConstraintsRefList(this);
        }
        return securityConstraintsRefs;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#setSecurityConstraintsRefs(java.util.List)
     */
    public void setSecurityConstraintsRefs(List constraintsRefs)
    {
        // set constraints refs using ordered ref
        // names by replacing existing entries with
        // new elements if new collection is specified
        List securityConstraintsRefs = getSecurityConstraintsRefs();
        if (constraintsRefs != securityConstraintsRefs)
        {
            // replace all constraints ref names
            securityConstraintsRefs.clear();
            if (constraintsRefs != null)
            {
                securityConstraintsRefs.addAll(constraintsRefs);
            }
        }
        // reset cached security constraints
        clearAllSecurityConstraints();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#isEmpty()
     */
    public boolean isEmpty()
    {
        // test only persistent members for any specified constraints
        return ((owner == null) &&
                ((constraints == null) || constraints.isEmpty()) &&
                ((constraintsRefs == null) || constraintsRefs.isEmpty()));
    }
}
