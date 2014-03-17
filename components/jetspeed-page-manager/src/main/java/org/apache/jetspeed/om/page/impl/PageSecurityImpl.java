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
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.page.document.impl.DocumentImpl;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PageSecurityImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class PageSecurityImpl extends DocumentImpl implements PageSecurity
{
    private List<SecurityConstraintsDefImpl> constraintsDefs;
    private List<PageSecurityGlobalSecurityConstraintsRef> globalConstraintsRefs;

    private PageSecurityConstraintsDefList securityConstraintsDefs;
    private Map<String,SecurityConstraintsDef> securityConstraintsDefsMap;
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
    List<SecurityConstraintsDefImpl> accessConstraintsDefs()
    {
        // create initial collection if necessary
        if (constraintsDefs == null)
        {
            constraintsDefs = DatabasePageManagerUtils.createList();
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
    List<PageSecurityGlobalSecurityConstraintsRef> accessGlobalConstraintsRefs()
    {
        // create initial collection if necessary
        if (globalConstraintsRefs == null)
        {
            globalConstraintsRefs = DatabasePageManagerUtils.createList();
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
    public List<SecurityConstraintsDef> getSecurityConstraintsDefs()
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
    public void setSecurityConstraintsDefs(List<SecurityConstraintsDef> definitions)
    {
        // set constraints defs by replacing existing
        // entries with new elements if new collection
        // is specified
        List<SecurityConstraintsDef> securityConstraintsDefs = getSecurityConstraintsDefs();
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
            securityConstraintsDefsMap = new HashMap<String,SecurityConstraintsDef>((getSecurityConstraintsDefs().size() * 2) + 1);
            for (SecurityConstraintsDef definition : getSecurityConstraintsDefs())
            {
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
            return securityConstraintsDefsMap.get(name);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.PageSecurity#getGlobalSecurityConstraintsRefs()
     */
    public List<String> getGlobalSecurityConstraintsRefs()
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
    public void setGlobalSecurityConstraintsRefs(List<String> constraintsRefs)
    {
        // set constraints refs using ordered ref
        // names by replacing existing entries with
        // new elements if new collection is specified
        List<String> globalSecurityConstraintsRefs = getGlobalSecurityConstraintsRefs();
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
