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
import java.util.List;

import org.apache.jetspeed.om.page.SecurityConstraintsDef;

/**
 * SecurityConstraintsDefImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class SecurityConstraintsDefImpl implements SecurityConstraintsDef
{
    private int id;
    private String name;
    private List constraintDefs;

    private SecurityConstraintDefList securityConstraintDefs;

    /**
     * accessConstraintDefs
     *
     * Access mutable persistent collection member for List wrappers.
     *
     * @return persistent collection
     */
    List accessConstraintDefs()
    {
        // create initial collection if necessary
        if (constraintDefs == null)
        {
            constraintDefs = new ArrayList(4);
        }
        return constraintDefs;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.SecurityConstraintsDef#getName()
     */
    public String getName()
    {
        return name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.SecurityConstraintsDef#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.SecurityConstraintsDef#getSecurityConstraints()
     */
    public List getSecurityConstraints()
    {
        // return mutable constraint def list
        // by using list wrapper to manage apply order
        if (securityConstraintDefs == null)
        {
            securityConstraintDefs = new SecurityConstraintDefList(this);
        }
        return securityConstraintDefs;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.SecurityConstraintsDef#setSecurityConstraints(java.util.List)
     */
    public void setSecurityConstraints(List constraints)
    {
        // set constraint defs by replacing existing
        // entries with new elements if new collection
        // is specified
        List securityConstraintDefs = getSecurityConstraints();
        if (constraints != securityConstraintDefs)
        {
            // replace all constraints
            securityConstraintDefs.clear();
            if (constraints != null)
            {
                securityConstraintDefs.addAll(constraints);
            }
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object o)
    {
        if (o instanceof SecurityConstraintsDefImpl)
        {
            if (name != null)
            {
                return name.equals(((SecurityConstraintsDefImpl)o).getName());
            }
            return (((SecurityConstraintsDefImpl)o).getName() == null);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        if (name != null)
        {
            return name.hashCode();
        }
        return 0;
    }
}
