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

import java.util.List;

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
public class SecurityConstraintsDefImpl implements SecurityConstraintsDef
{
    private String name;

    private List constraints;

    /**
     * <p>
     * getName
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraintsDef#getName()
     * @return
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * <p>
     * setName
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraintsDef#setName(java.lang.String)
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * <p>
     * getSecurityConstraints
     * </p>
     *
     * @see org.apache.jetspeed.om.common.SecurityConstraintsDef#getSecurityConstraints()
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
     * @see org.apache.jetspeed.om.common.SecurityConstraintsDef#setSecurityConstraints(java.util.List)
     * @param constraints
     */
    public void setSecurityConstraints(List constraints)
    {
        this.constraints = constraints;
    }
}
