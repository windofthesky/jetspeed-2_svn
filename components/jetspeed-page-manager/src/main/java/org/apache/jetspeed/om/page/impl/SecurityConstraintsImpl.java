/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.om.page.SecurityConstraintsRefExpression;
import org.apache.jetspeed.om.page.SecurityConstraintsRefParser;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * SecurityConstraintsImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class SecurityConstraintsImpl implements SecurityConstraints
{
    private final static Logger log = LoggerFactory.getLogger(SecurityConstraintsImpl.class);

    private String owner;
    private List<SecurityConstraintImpl> constraints;
    private List<BaseSecurityConstraintsRef> constraintsRefs;

    private SecurityConstraintList securityConstraints;
    private SecurityConstraintsRefList securityConstraintsRefs;

    private List<Object> allConstraints;

    /**
     * accessConstraintsRefs
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List<BaseSecurityConstraintsRef> accessConstraintsRefs()
    {
        // create initial collection if necessary
        if (constraintsRefs == null)
        {
            constraintsRefs = DatabasePageManagerUtils.createList();
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
    List<SecurityConstraintImpl> accessConstraints()
    {
        // create initial collection if necessary
        if (constraints == null)
        {
            constraints = DatabasePageManagerUtils.createList();
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
    public void checkConstraints(List<String> actions, List<String> userPrincipals, List<String> rolePrincipals, List<String> groupPrincipals, PageSecurity pageSecurity) throws SecurityException
    {
        // if owner defined, override all constraints and allow all access
        if ((owner != null) && (userPrincipals != null) && userPrincipals.contains(owner))
        {
            return;
        }

        try
        {
            // skip missing or empty constraints: permit all access
            List<Object> checkConstraints = getAllSecurityConstraints(pageSecurity);
            if ((checkConstraints != null) && !checkConstraints.isEmpty())
            {
                // test each action, constraints check passes only
                // if all actions are permitted for principals
                for (String action : actions)
                {
                    // check each action:
                    // - if any actions explicitly permitted, (including owner),
                    //   assume no permissions are permitted by default
                    // - if all constraints do not specify a permission or an
                    //   expression, assume access is permitted by default
                    boolean actionPermitted = false;
                    boolean actionNotPermitted = false;
                    boolean anyActionsPermitted = (getOwner() != null);

                    // check against constraints and constraint ref expressions
                    for (Object constraintOrExpression : checkConstraints)
                    {
                        if (constraintOrExpression instanceof SecurityConstraintImpl)
                        {
                            // check constraint
                            SecurityConstraintImpl constraint = (SecurityConstraintImpl)constraintOrExpression;

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
                        else if (constraintOrExpression instanceof SecurityConstraintsRefExpression)
                        {
                            // check expression
                            SecurityConstraintsRefExpression expression = (SecurityConstraintsRefExpression)constraintOrExpression;

                            // assume actions are permitted in expression
                            anyActionsPermitted = true;

                            // check expression with action permission and user/role/group principals
                            if (expression.checkExpression(action, userPrincipals, rolePrincipals, groupPrincipals))
                            {
                                actionPermitted = true;
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
            else
            {
                // fail for any action if owner specified
                // since no other constraints were found
                if ((getOwner() != null) && !actions.isEmpty())
                {
                    String action = actions.get(0);
                    throw new SecurityException("SecurityConstraintsImpl.checkConstraints(): Access for " + action + " not permitted, (not owner).");
                }
            }
        }
        catch (SecurityException se)
        {
            // rethrow expected SecurityExceptions
            throw se;
        }
        catch (Exception e)
        {
            // log and wrap other unexpected exceptions
            if (log.isDebugEnabled())
            {
                log.error("Security constraints check exception: "+e, e);
            }
            else
            {
                log.error("Security constraints check exception: "+e);
            }
            throw new SecurityException("SecurityConstraintsImpl.checkConstraints(): Exception detected: "+e);
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
     * @return all security constraints and constraints ref expressions
     * @throws RuntimeException if expression parsing error occurs
     */
    private synchronized List<Object> getAllSecurityConstraints(PageSecurity pageSecurity)
    {
        // return previously cached security constraints
        if (allConstraints != null)
        {
            return allConstraints;
        }

        // construct new ordered security constraints list
        List<Object> newAllConstraints = new ArrayList<Object>();

        // add any defined security constraints
        if ((getSecurityConstraints() != null) && !getSecurityConstraints().isEmpty())
        {
            newAllConstraints.addAll(securityConstraints);
        }

        // add any security constraints references
        if ((getSecurityConstraintsRefs() != null) && !getSecurityConstraintsRefs().isEmpty())
        {
            List<Object> referencedConstraints = dereferenceSecurityConstraintsRefs(getSecurityConstraintsRefs(), pageSecurity);
            if (referencedConstraints != null)
            {
                newAllConstraints.addAll(referencedConstraints);
            }
        }
        
        // add any global security constraints references
        if (pageSecurity != null)
        {
            List<String> globalConstraintsRefs = pageSecurity.getGlobalSecurityConstraintsRefs();
            if ((globalConstraintsRefs != null) && !globalConstraintsRefs.isEmpty())
            {
                List<Object> referencedConstraints = dereferenceSecurityConstraintsRefs(globalConstraintsRefs, pageSecurity);
                if (referencedConstraints != null)
                {
                    newAllConstraints.addAll(referencedConstraints);
                }
            }
        }

        return allConstraints = newAllConstraints;
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
     * @param constraintsRefs constraints references to be dereferenced
     * @param pageSecurity page security definitions
     * @return security constraints and constraints ref expressions
     * @throws RuntimeException if expression parsing error occurs
     */
    private List<Object> dereferenceSecurityConstraintsRefs(List<String> constraintsRefs, PageSecurity pageSecurity)
    {
        List<Object> constraints = null;
        if (pageSecurity != null)
        {   
            // dereference each security constraints definition
            for (String constraintsRef : constraintsRefs)
            {
                // parse constraints ref and return constraints/constraints ref expressions
                Object parsedConstraintsOrExpression = SecurityConstraintsRefParser.parse(constraintsRef, pageSecurity);
                if (parsedConstraintsOrExpression instanceof List)
                {
                    @SuppressWarnings("unchecked")
                    List<Object> parsedConstraints = (List)parsedConstraintsOrExpression;
                    if (constraints == null)
                    {
                        constraints = new ArrayList<Object>();
                    }
                    constraints.addAll(parsedConstraints);
                }
                else if (parsedConstraintsOrExpression instanceof SecurityConstraintsRefExpression)
                {
                    if (constraints == null)
                    {
                        constraints = new ArrayList<Object>();
                    }
                    constraints.add(parsedConstraintsOrExpression);
                }
                else if (parsedConstraintsOrExpression != null)
                {
                    throw new RuntimeException("Unexpected security constraints ref parser result");
                }
            }
        }
        else
        {
            throw new RuntimeException("Page security definitions not available");
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
    public List<SecurityConstraint> getSecurityConstraints()
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
    public void setSecurityConstraints(List<SecurityConstraint> constraints)
    {
        // set inline constraints by replacing existing
        // entries with new elements if new collection
        // is specified
        List<SecurityConstraint> securityConstraints = getSecurityConstraints();
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
    public List<String> getSecurityConstraintsRefs()
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
    public void setSecurityConstraintsRefs(List<String> constraintsRefs)
    {
        // set constraints refs using ordered ref
        // names by replacing existing entries with
        // new elements if new collection is specified
        List<String> securityConstraintsRefs = getSecurityConstraintsRefs();
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
