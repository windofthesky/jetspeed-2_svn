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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;

/**
 * <p>
 * SecurityImpl
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:rwatler@finali.com">Randy Watler</a>
 * @version $Id$
 *
 */
public class PageSecurityImpl extends DocumentImpl implements PageSecurity
{
    private List constraintsDefsList;

    private Map constraintsDefsMap;

    private List globalConstraintsRefs;

    /**
     * <p>
     * getType
     * </p>
     *
     * @see org.apache.jetspeed.om.page.Document#getType()
     * @return
     */
    public String getType()
    {       
        return DOCUMENT_TYPE;
    }

    /**
     * <p>
     * getSecurityConstraintsDefs
     * </p>
     *
     * @see org.apache.jetspeed.om.page.PageSecurity#getSecurityConstraintsDefs()
     * @return
     */
    public List getSecurityConstraintsDefs()
    {
        return constraintsDefsList;
    }
    
    /**
     * <p>
     * setSecurityConstraintsDefs
     * </p>
     *
     * @see org.apache.jetspeed.om.page.PageSecurity#setSecurityConstraintsDefs(java.util.List)
     * @param defintions
     */
    public void setSecurityConstraintsDefs(List definitions)
    {
        constraintsDefsList = definitions;
        constraintsDefsMap = null;
    }

    /**
     * <p>
     * newSecurityConstraintsDef
     * </p>
     *
     * @see org.apache.jetspeed.om.page.PageSecurity#newSecurityConstraintsDef()
     * @return security constraints definition
     */
    public SecurityConstraintsDef newSecurityConstraintsDef()
    {
        return new SecurityConstraintsDefImpl();
    }

    /**
     * <p>
     * getSecurityConstraintsDef
     * </p>
     *
     * @see org.apache.jetspeed.om.page.PageSecurity#getSecurityConstraintsDef(java.lang.String)
     * @param name
     * @return
     */
    public SecurityConstraintsDef getSecurityConstraintsDef(String name)
    {
        if ((constraintsDefsList != null) && (constraintsDefsMap == null))
        {
            constraintsDefsMap = new HashMap((constraintsDefsList.size() * 2) + 1);
            Iterator definitionsIter = constraintsDefsList.iterator();
            while (definitionsIter.hasNext())
            {
                SecurityConstraintsDef definition = (SecurityConstraintsDef)definitionsIter.next();
                constraintsDefsMap.put(definition.getName(), definition);
            }
        }
        if (constraintsDefsMap != null)
        {
            return (SecurityConstraintsDef) constraintsDefsMap.get(name);
        }
        return null;
    }

    /**
     * <p>
     * getGlobalSecurityConstraintsRefs
     * </p>
     *
     * @see org.apache.jetspeed.om.page.PageSecurity#getGlobalSecurityConstraintsRefs()
     * @return
     */
    public List getGlobalSecurityConstraintsRefs()
    {
        return globalConstraintsRefs;
    }
    
    /**
     * <p>
     * setGlobalSecurityConstraintsRefs
     * </p>
     *
     * @see org.apache.jetspeed.om.page.PageSecurity#setGlobalSecurityConstraintsRefs(java.util.List)
     * @param constraintsRefs
     */
    public void setGlobalSecurityConstraintsRefs(List constraintsRefs)
    {
        globalConstraintsRefs = constraintsRefs;
    }
}
