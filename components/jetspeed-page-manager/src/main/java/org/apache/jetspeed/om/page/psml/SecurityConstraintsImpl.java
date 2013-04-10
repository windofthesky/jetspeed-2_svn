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
package org.apache.jetspeed.om.page.psml;

import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.om.page.SecurityConstraintsRefExpression;
import org.apache.jetspeed.om.page.SecurityConstraintsRefParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * SecurityConstraintsImpl
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:rwatler@finali.com">Randy Watler</a>
 * @version $Id$
 *
 */
public class SecurityConstraintsImpl implements SecurityConstraints
{
    private final static Logger log = LoggerFactory.getLogger(SecurityConstraintsImpl.class);

    private String owner;

    private List constraints;

    private List constraintsRefs;

    private List allConstraints;

    /**
     * <p>
     * getOwner
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraints#getOwner()
     * @return
     */
    public String getOwner()
    {
        return owner;
    }
    
    /**
     * <p>
     * setOwner
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraints#setOwner(java.lang.String)
     * @param owner
     */
    public void setOwner(String owner)
    {
        this.owner = owner;
    }

    /**
     * <p>
     * getSecurityConstraints
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraints#getSecurityConstraints()
     * @return
     */
    public List getSecurityConstraints()
    {
        if (this.constraints == null)
        {
            this.constraints = Collections.synchronizedList(new ArrayList());
        }                
        return constraints;
    }
    
    /**
     * <p>
     * setSecurityConstraint
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraints#setSecurityConstraints(java.util.List)
     * @param constraints
     */
    public void setSecurityConstraints(List constraints)
    {        
        this.constraints = constraints;
    }

    /**
     * <p>
     * getSecurityConstraintsRefs
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraints#getSecurityConstraintsRefs()
     * @return
     */
    public List getSecurityConstraintsRefs()
    {
        if (this.constraintsRefs == null)
        {
            this.constraintsRefs = Collections.synchronizedList(new ArrayList());
        }        
        return constraintsRefs;
    }
    
    /**
     * <p>
     * setSecurityConstraintsRefs
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraints#setSecurityConstraintsRefs(java.util.List)
     * @param constraintsRefs
     */
    public void setSecurityConstraintsRefs(List constraintsRefs)
    {
        this.constraintsRefs = constraintsRefs;
    }

    /**
     * <p>
     * isEmpty
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraints#isEmpty()
     * @return flag indicating whether there are constraints or owner set
     */
    public boolean isEmpty()
    {
        return ((owner == null) && (constraints == null) && (constraintsRefs == null));
    }

    /**
     * <p>
     * checkConstraints
     * </p>
     *
     * @param actions
     * @param userPrincipals
     * @param rolePrincipals
     * @param groupPrincipals
     * @param pageSecurity page security definitions
     * @throws SecurityException
     */
    public void checkConstraints(List actions, List userPrincipals, List rolePrincipals,
                                 List groupPrincipals, PageSecurity pageSecurity) throws SecurityException
    {
        // if owner defined, override all constraints and allow all access
        if ((owner != null) && (userPrincipals != null) && userPrincipals.contains(owner))
        {
            return;
        }

        try
        {
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
                    // - if any actions explicitly permitted, (including owner),
                    //   assume no permissions are permitted by default
                    // - if all constraints do not specify a permission or an
                    //   expression, assume access is permitted by default
                    String action = (String)actionsIter.next();
                    boolean actionPermitted = false;
                    boolean actionNotPermitted = false;
                    boolean anyActionsPermitted = (getOwner() != null);

                    // check against constraints and constraint ref expressions
                    Iterator checkConstraintsIter = checkConstraints.iterator();
                    while (checkConstraintsIter.hasNext())
                    {
                        Object constraintOrExpression = checkConstraintsIter.next();
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
                    String action = (String)actions.get(0);
                    throw new SecurityException("SecurityConstraintsImpl.checkConstraints(): Access for " + action + " not permitted, (not owner).");
                }
            }
        }
        catch (Exception e)
        {
            log.error("Security constraints check exception: "+e);
            throw new SecurityException("SecurityConstraintsImpl.checkConstraints(): Exception detected: "+e);
        }
    }

    /**
     * <p>
     * getAllSecurityConstraints
     * </p>
     *
     * @param pageSecurity
     * @return all security constraints and constraints ref expressions
     * @throws RuntimeException if expression parsing error occurs
     */
    private synchronized List getAllSecurityConstraints(PageSecurity pageSecurity)
    {
        // return previously cached security constraints; note that
        // cache is assumed valid until owning document is evicted
        if (allConstraints != null)
        {
            return allConstraints;
        }

        // construct new ordered security constraints list
        List newAllConstraints = new ArrayList();

        // add any defined security constraints
        if (constraints != null)
        {
            newAllConstraints.addAll(constraints);
        }

        // add any security constraints references
        if ((constraintsRefs != null) && !constraintsRefs.isEmpty())
        {
            List referencedConstraints = dereferenceSecurityConstraintsRefs(constraintsRefs, pageSecurity);
            if (referencedConstraints != null)
            {
                newAllConstraints.addAll(referencedConstraints);
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
                    newAllConstraints.addAll(referencedConstraints);
                }
            }
        }   

        return allConstraints = newAllConstraints;
    }

    /**
     * <p>
     * dereferenceSecurityConstraintsRefs
     * </p>
     *
     * @param constraintsRefs constraints references to be dereferenced
     * @param pageSecurity page security definitions
     * @return security constraints and constraints ref expressions
     * @throws RuntimeException if expression parsing error occurs
     */
    private List dereferenceSecurityConstraintsRefs(List constraintsRefs, PageSecurity pageSecurity)
    {
        // access security document to dereference security
        // constraints definitions
        List constraints = null;
        if (pageSecurity != null)
        {   
            // dereference each security constraints definition
            Iterator constraintsRefsIter = constraintsRefs.iterator();
            while (constraintsRefsIter.hasNext())
            {
                String constraintsRef = (String)constraintsRefsIter.next();
                // parse constraints ref and return constraints/constraints ref expressions
                Object constraintsOrExpression = SecurityConstraintsRefParser.parse(constraintsRef, pageSecurity);
                if (constraintsOrExpression instanceof List)
                {
                    if (constraints == null)
                    {
                        constraints = new ArrayList();
                    }
                    constraints.addAll((List)constraintsOrExpression);
                }
                else if (constraintsOrExpression instanceof SecurityConstraintsRefExpression)
                {
                    if (constraints == null)
                    {
                        constraints = new ArrayList();
                    }
                    constraints.add(constraintsOrExpression);
                }
                else if (constraintsOrExpression != null)
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
}
