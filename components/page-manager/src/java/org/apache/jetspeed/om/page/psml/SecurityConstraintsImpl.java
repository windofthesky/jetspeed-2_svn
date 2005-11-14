/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.om.page.psml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.PageNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private final static Log log = LogFactory.getLog(SecurityConstraintsImpl.class);

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
     * @param handlerFactory
     * @throws SecurityException
     */
    public void checkConstraints(List actions, List userPrincipals, List rolePrincipals,
                                 List groupPrincipals, DocumentHandlerFactory handlerFactory) throws SecurityException
    {
        // if owner defined, override all constraints and allow all access
        if ((owner != null) && (userPrincipals != null) && userPrincipals.contains(owner))
        {
            return;
        }

        // skip missing or empty constraints: permit all access
        List checkConstraints = getAllSecurityConstraints(handlerFactory);
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
     * @param handlerFactory
     * @return all security constraints
     */
    private List getAllSecurityConstraints(DocumentHandlerFactory handlerFactory)
    {
        // return previously cached security constraints; note that
        // cache is assumed valid until owning document is evicted
        if (allConstraints != null)
        {
            return allConstraints;
        }

        // construct new ordered security constraints list
        allConstraints = new ArrayList(8);

        // add any defined security constraints
        if (constraints != null)
        {
            allConstraints.addAll(constraints);
        }

        // add any security constraints references
        if ((constraintsRefs != null) && !constraintsRefs.isEmpty())
        {
            List referencedConstraints = dereferenceSecurityConstraintsRefs(constraintsRefs, handlerFactory);
            if (referencedConstraints != null)
            {
                allConstraints.addAll(referencedConstraints);
            }
        }

        // add any global decurity constraints references
        PageSecurity pageSecurity = getSecurity(handlerFactory);
        if (pageSecurity != null)
        {
            List globalConstraintsRefs = pageSecurity.getGlobalSecurityConstraintsRefs();
            if ((globalConstraintsRefs != null) && !globalConstraintsRefs.isEmpty())
            {
                List referencedConstraints = dereferenceSecurityConstraintsRefs(globalConstraintsRefs, handlerFactory);
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
     * @param handlerFactory
     * @return security constraints
     */
    private List dereferenceSecurityConstraintsRefs(List constraintsRefs, DocumentHandlerFactory handlerFactory)
    {
        // access security document to dereference security
        // constriants definitions
        List constraints = null;
        if (handlerFactory != null)
        {
            // security document
            PageSecurity pageSecurity = getSecurity(handlerFactory);
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
                    else
                    {
                        log.error("dereferenceSecurityConstraintsRefs(): Unable to dereference \"" + constraintsRef + "\" security constraints definition.");
                    }
                }
            }
            else
            {
                log.error("dereferenceSecurityConstraintsRefs(): Failed to load page security configuration while dereferencing security constraints definitions.");
            }
        }
        else
        {
            log.error("dereferenceSecurityConstraintsRefs(): Missing document handler, unable to dereference security constraints definitions.");
        }
        
        return constraints;
    }

    /**
     * <p>
     * getSecurity
     * </p>
     *
     * @param handlerFactory
     * @return security document
     */
    private PageSecurity getSecurity(DocumentHandlerFactory handlerFactory)
    {
        // access security document using document handler
        // at fixed location in root
        if (handlerFactory != null)
        {
            String pageSecurityPath = Folder.PATH_SEPARATOR + PageSecurity.DOCUMENT_TYPE;
            try
            {
                return (PageSecurity)handlerFactory.getDocumentHandler(PageSecurity.DOCUMENT_TYPE).getDocument(pageSecurityPath);                
            }
            catch (PageNotFoundException pnfe)
            {
                log.debug("getSecurity(): Failed to load page security configuration at " + pageSecurityPath + ".");
            }
            catch (Exception e)
            {
                log.error("getSecurity(): Failed to load page security configuration at " + pageSecurityPath + ".", e);
            }
        }
        return null;
    }
}
