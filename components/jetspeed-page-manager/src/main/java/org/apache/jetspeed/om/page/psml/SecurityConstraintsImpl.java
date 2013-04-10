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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;

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
     * <p>
     * getAllSecurityConstraints
     * </p>
     *
     * @param pageSecurity
     * @return all security constraints
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
        allConstraints = Collections.synchronizedList(new ArrayList(8));

        // add any defined security constraints
        if (constraints != null)
        {
            allConstraints.addAll(constraints);
        }

        // add any security constraints references
        if ((constraintsRefs != null) && !constraintsRefs.isEmpty())
        {
            List referencedConstraints = dereferenceSecurityConstraintsRefs(constraintsRefs, pageSecurity);
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
     * <p>
     * dereferenceSecurityConstraintsRefs
     * </p>
     *
     * @param constraintsRefs
     * @param pageSecurity
     * @return security constraints
     */
    private List dereferenceSecurityConstraintsRefs(List constraintsRefs, PageSecurity pageSecurity)
    {
        // access security document to dereference security
        // constriants definitions
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
                        constraints = Collections.synchronizedList(new ArrayList(constraintsRefs.size()));
                    }
                    constraints.addAll(securityConstraintsDef.getSecurityConstraints());
                }
                else
                {
                    log.error("dereferenceSecurityConstraintsRefs(): Unable to dereference \"" + constraintsRef + "\" security constraints definition.");
                }
            }
        }
        else
        {
            log.error("dereferenceSecurityConstraintsRefs(): Missing page security, unable to dereference security constraints definitions.");
        }
        
        return constraints;
    }
}
