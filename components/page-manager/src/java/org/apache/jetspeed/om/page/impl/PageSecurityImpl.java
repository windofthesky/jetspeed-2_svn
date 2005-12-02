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
import java.util.ListIterator;
import java.util.Map;

import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.page.document.impl.DocumentImpl;

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

    private PageSecurityConstraintsDefList securityConstraintsDefs;
    private Map securityConstraintsDefsMap;
    private PageSecurityConstraintsRefList globalSecurityConstraintsRefs;

    public PageSecurityImpl()
    {
        super(null);
    }

    /**
     * accessConstraintsDefs
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List accessConstraintsDefs()
    {
        // create initial collection if necessary
        if (constraintsDefs == null)
        {
            constraintsDefs = new ArrayList(4);
        }
        return constraintsDefs;
    }

    /**
     * accessGlobalConstraintsRefs
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List accessGlobalConstraintsRefs()
    {
        // create initial collection if necessary
        if (globalConstraintsRefs == null)
        {
            globalConstraintsRefs = new ArrayList(4);
        }
        return globalConstraintsRefs;
    }

    /**
     * clearSecurityConstraintsDefsMap
     *
     * Clear previously cached security constraints definitions map.
     */
    synchronized void clearSecurityConstraintsDefsMap()
    {
        securityConstraintsDefsMap = null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#newSecurityConstraint()
     */
    public SecurityConstraint newSecurityConstraint()
    {
        // return specific security constraint definition instance
        return new PageSecuritySecurityConstraintImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.PageSecurity#getSecurityConstraintsDefs()
     */
    public List getSecurityConstraintsDefs()
    {
        // return mutable constraints defs list
        // by using list wrapper to manage
        // element uniqueness
        if (securityConstraintsDefs == null)
        {
            securityConstraintsDefs = new PageSecurityConstraintsDefList(this);
        }
        return securityConstraintsDefs;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecuredResource#newSecurityConstraintsDef()
     */
    public SecurityConstraintsDef newSecurityConstraintsDef()
    {
        // return specific security constraints definition instance
        return new SecurityConstraintsDefImpl();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.PageSecurity#setSecurityConstraintsDefs(java.util.List)
     */
    public void setSecurityConstraintsDefs(List definitions)
    {
        // set constraints defs by replacing existing
        // entries with new elements if new collection
        // is specified
        List securityConstraintsDefs = getSecurityConstraintsDefs();
        if (definitions != securityConstraintsDefs)
        {
            // replace all constraints definitions
            securityConstraintsDefs.clear();
            if (definitions != null)
            {
                securityConstraintsDefs.addAll(definitions);
            }
        }
        // clear cached security constraints definition map
        clearSecurityConstraintsDefsMap();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.PageSecurity#getSecurityConstraintsDef(java.lang.String)
     */
    public synchronized SecurityConstraintsDef getSecurityConstraintsDef(String name)
    {
        // build and cache security constraints definitions
        // map if necessary upon realization or after modification
        if ((getSecurityConstraintsDefs() != null) && (securityConstraintsDefsMap == null))
        {
            securityConstraintsDefsMap = new HashMap((getSecurityConstraintsDefs().size() * 2) + 1);
            Iterator definitionsIter = getSecurityConstraintsDefs().iterator();
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
        // lookup constraints definition using cached map 
        if (securityConstraintsDefsMap != null)
        {
            return (SecurityConstraintsDef)securityConstraintsDefsMap.get(name);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.PageSecurity#getGlobalSecurityConstraintsRefs()
     */
    public List getGlobalSecurityConstraintsRefs()
    {
        // return mutable constraints refs list
        // by using list wrapper to manage apply
        // order and element uniqueness
        if (globalSecurityConstraintsRefs == null)
        {
            globalSecurityConstraintsRefs = new PageSecurityConstraintsRefList(this);
        }
        return globalSecurityConstraintsRefs;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.PageSecurity#setGlobalSecurityConstraintsRefs(java.util.List)
     */
    public void setGlobalSecurityConstraintsRefs(List constraintsRefs)
    {
        // set constraints refs using ordered ref
        // names by replacing existing entries with
        // new elements if new collection is specified
        List globalSecurityConstraintsRefs = getGlobalSecurityConstraintsRefs();
        if (constraintsRefs != globalSecurityConstraintsRefs)
        {
            // replace all constraints ref names
            globalSecurityConstraintsRefs.clear();
            if (constraintsRefs != null)
            {
                globalSecurityConstraintsRefs.addAll(constraintsRefs);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getType()
     */
    public String getType()
    {
        return DOCUMENT_TYPE;
    }
}
