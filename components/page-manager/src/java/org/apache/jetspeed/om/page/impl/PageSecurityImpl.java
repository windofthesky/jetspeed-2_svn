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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintImpl;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.om.page.SecurityConstraintsDefImpl;
import org.apache.jetspeed.page.document.impl.DocumentImpl;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;

/**
 * PageSecurityImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PageSecurityImpl extends DocumentImpl implements PageSecurity
{
    private List constraintsDefs;
    private List globalConstraintsRefs;

    private List securityConstraintsDefs;
    private Map securityConstraintsDefsMap;
    private List globalSecurityConstraintsRefs;

    public PageSecurityImpl()
    {
        super(null);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.PageSecurity#getSecurityConstraintsDefs()
     */
    public List getSecurityConstraintsDefs()
    {
        return securityConstraintsDefs;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.PageSecurity#setSecurityConstraintsDefs(java.util.List)
     */
    public void setSecurityConstraintsDefs(List definitions)
    {
        securityConstraintsDefs = definitions;
        securityConstraintsDefsMap = null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.PageSecurity#getSecurityConstraintsDef(java.lang.String)
     */
    public SecurityConstraintsDef getSecurityConstraintsDef(String name)
    {
        if ((securityConstraintsDefs != null) && (securityConstraintsDefsMap == null))
        {
            securityConstraintsDefsMap = new HashMap((securityConstraintsDefs.size() * 2) + 1);
            Iterator definitionsIter = securityConstraintsDefs.iterator();
            while (definitionsIter.hasNext())
            {
                SecurityConstraintsDef definition = (SecurityConstraintsDef)definitionsIter.next();
                String definitionName = definition.getName();
                if (!securityConstraintsDefsMap.containsKey(definitionName))
                {
                    securityConstraintsDefsMap.put(definitionName, definition);
                }
            }
        }
        if (securityConstraintsDefsMap != null)
        {
            return (SecurityConstraintsDef) securityConstraintsDefsMap.get(name);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.PageSecurity#getGlobalSecurityConstraintsRefs()
     */
    public List getGlobalSecurityConstraintsRefs()
    {
        return globalSecurityConstraintsRefs;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.PageSecurity#setGlobalSecurityConstraintsRefs(java.util.List)
     */
    public void setGlobalSecurityConstraintsRefs(List constraintsRefs)
    {
        globalSecurityConstraintsRefs = constraintsRefs;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getType()
     */
    public String getType()
    {
        return DOCUMENT_TYPE;
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    public void beforeUpdate(PersistenceBroker broker) throws PersistenceBrokerException
    {
        // propagate to super
        super.beforeUpdate(broker);

        // synchronize persistent constraints definitions
        if ((securityConstraintsDefs != null) && !securityConstraintsDefs.isEmpty())
        {
            // get sorted list of definition names and required
            // size of constraints definitions collection
            List securityConstraintsDefNames = new ArrayList(securityConstraintsDefs.size());
            int securityConstraintsDefsSize = 0;
            Iterator definitionsIter = securityConstraintsDefs.iterator();
            while (definitionsIter.hasNext())
            {
                SecurityConstraintsDef securityConstraintsDef = (SecurityConstraintsDef)definitionsIter.next();
                String securityConstraintsDefName = securityConstraintsDef.getName();
                if ((securityConstraintsDef.getSecurityConstraints() != null) &&
                    (securityConstraintsDefName != null) &&
                    !securityConstraintsDefNames.contains(securityConstraintsDefName))
                {
                    securityConstraintsDefsSize += securityConstraintsDef.getSecurityConstraints().size();
                    securityConstraintsDefNames.add(securityConstraintsDefName);
                }
            }
            Collections.sort(securityConstraintsDefNames);
            // update constraints definitions collection size
            if (constraintsDefs == null)
            {
                constraintsDefs = new ArrayList(securityConstraintsDefsSize);
            }
            while (constraintsDefs.size() < securityConstraintsDefsSize)
            {
                constraintsDefs.add(new PageSecuritySecurityConstraintsDef());
            }
            while (constraintsDefs.size() > securityConstraintsDefsSize)
            {
                constraintsDefs.remove(constraintsDefs.size()-1);
            }
            // update constraints definitions
            Iterator updateIter0 = securityConstraintsDefNames.iterator();
            Iterator updateIter1 = constraintsDefs.iterator();
            while (updateIter0.hasNext() && updateIter1.hasNext())
            {
                // update by definition name
                String securityConstraintsDefName = (String)updateIter0.next();
                // find named definition
                SecurityConstraintsDef securityConstraintsDef = null;
                Iterator findDefinitionIter = securityConstraintsDefs.iterator();
                while ((securityConstraintsDef == null) && findDefinitionIter.hasNext())
                {
                    SecurityConstraintsDef testSecurityConstraintsDef = (SecurityConstraintsDef)findDefinitionIter.next();
                    if (securityConstraintsDefName.equals(testSecurityConstraintsDef.getName()))
                    {
                        securityConstraintsDef = testSecurityConstraintsDef;
                    }
                }
                // update constraints definition
                if ((securityConstraintsDef != null) && (securityConstraintsDef.getSecurityConstraints() != null))
                {
                    Iterator updateIter2 = securityConstraintsDef.getSecurityConstraints().iterator();
                    for (int i = 0; (updateIter1.hasNext() && updateIter2.hasNext()); i++)
                    {
                        SecurityConstraint securityConstraint = (SecurityConstraint)updateIter2.next();
                        PageSecuritySecurityConstraintsDef constraintsDef = (PageSecuritySecurityConstraintsDef)updateIter1.next();
                        constraintsDef.setName(securityConstraintsDefName);
                        constraintsDef.setApplyOrder(i);
                        constraintsDef.setUserPrincipals(securityConstraint.getUsersList());
                        constraintsDef.setRolePrincipals(securityConstraint.getRolesList());
                        constraintsDef.setGroupPrincipals(securityConstraint.getGroupsList());
                        constraintsDef.setPermissions(securityConstraint.getPermissionsList());
                    }
                }
            }
        }
        else
        {
            // empty constraints definitions collection
            if (constraintsDefs != null)
            {
                constraintsDefs.clear();
            }
        }

        // synchronize persistent global constraints references
        if ((globalSecurityConstraintsRefs != null) && !globalSecurityConstraintsRefs.isEmpty())
        {
            // update global constraints references collection size
            if (globalConstraintsRefs == null)
            {
                globalConstraintsRefs = new ArrayList(globalSecurityConstraintsRefs.size());
            }
            while (globalConstraintsRefs.size() < globalSecurityConstraintsRefs.size())
            {
                PageSecurityGlobalSecurityConstraintsRef globalConstraintsRef = new PageSecurityGlobalSecurityConstraintsRef();
                globalConstraintsRef.setApplyOrder(globalConstraintsRefs.size());
                globalConstraintsRefs.add(globalConstraintsRef);
            }
            while (globalConstraintsRefs.size() > globalSecurityConstraintsRefs.size())
            {
                globalConstraintsRefs.remove(globalConstraintsRefs.size()-1);
            }
            // update global constraints references
            Iterator updateIter0 = globalSecurityConstraintsRefs.iterator();
            Iterator updateIter1 = globalConstraintsRefs.iterator();
            while (updateIter0.hasNext() && updateIter1.hasNext())
            {
                String securityConstraintsRef = (String)updateIter0.next();
                PageSecurityGlobalSecurityConstraintsRef globalConstraintsRef = (PageSecurityGlobalSecurityConstraintsRef)updateIter1.next();
                globalConstraintsRef.setName(securityConstraintsRef);
            }
        }
        else
        {
            // empty global constraints references collection
            if (globalConstraintsRefs != null)
            {
                globalConstraintsRefs.clear();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterLookup(org.apache.ojb.broker.PersistenceBroker)
     */
    public void afterLookup(PersistenceBroker broker) throws PersistenceBrokerException
    {
        // propagate to super
        super.afterLookup(broker);

        // synchronize constraints definitions
        if ((constraintsDefs != null) && !constraintsDefs.isEmpty())
        {
            // initialize security constraints definitions collection
            if (securityConstraintsDefs == null)
            {
                securityConstraintsDefs = new ArrayList(4);
            }
            else
            {
                securityConstraintsDefs.clear();
            }
            // construct security constraints definitions
            Iterator updateIter = constraintsDefs.iterator();
            SecurityConstraintsDef securityConstraintsDef = null;
            while (updateIter.hasNext())
            {
                PageSecuritySecurityConstraintsDef constraintsDef = (PageSecuritySecurityConstraintsDef)updateIter.next();
                if ((securityConstraintsDef == null) || !securityConstraintsDef.getName().equals(constraintsDef.getName()))
                {
                    securityConstraintsDef = new SecurityConstraintsDefImpl();
                    securityConstraintsDef.setName(constraintsDef.getName());
                    securityConstraintsDef.setSecurityConstraints(new ArrayList(4));
                    securityConstraintsDefs.add(securityConstraintsDef);
                }
                SecurityConstraint securityConstraint = new SecurityConstraintImpl();
                securityConstraint.setUsers(constraintsDef.getUserPrincipals());
                securityConstraint.setRoles(constraintsDef.getRolePrincipals());
                securityConstraint.setGroups(constraintsDef.getGroupPrincipals());
                securityConstraint.setPermissions(constraintsDef.getPermissions());
                securityConstraintsDef.getSecurityConstraints().add(securityConstraint);
            }
        }
        else
        {
            // remove security constraints collection
            securityConstraintsDefs = null;
        }
        securityConstraintsDefsMap = null;

        // synchronize global constraints references
        if ((globalConstraintsRefs != null) && !globalConstraintsRefs.isEmpty())
        {
            // update global security constraints references
            if (globalSecurityConstraintsRefs == null)
            {
                globalSecurityConstraintsRefs = new ArrayList(globalConstraintsRefs.size());
            }
            else
            {
                globalSecurityConstraintsRefs.clear();
            }
            Iterator updateIter = globalConstraintsRefs.iterator();
            while (updateIter.hasNext())
            {
                PageSecurityGlobalSecurityConstraintsRef globalConstraintsRef = (PageSecurityGlobalSecurityConstraintsRef)updateIter.next();
                globalSecurityConstraintsRefs.add(globalConstraintsRef.getName());
            }
        }
        else
        {
            // remove global security constraints references collection
            globalSecurityConstraintsRefs = null;
        }
    }
}
