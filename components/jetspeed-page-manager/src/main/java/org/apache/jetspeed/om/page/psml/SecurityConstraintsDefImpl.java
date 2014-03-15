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

import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;

import java.util.ArrayList;
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
public class SecurityConstraintsDefImpl implements SecurityConstraintsDef
{
    private String name;

    private List<SecurityConstraint> constraints = new ArrayList<SecurityConstraint>(4);

    /**
     * <p>
     * getName
     * </p>
     *
     * @see org.apache.jetspeed.om.page.SecurityConstraintsDef#getName()
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
     * @see org.apache.jetspeed.om.page.SecurityConstraintsDef#setName(java.lang.String)
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
     * @see org.apache.jetspeed.om.page.SecurityConstraintsDef#getSecurityConstraints()
     * @return
     */
    public List<SecurityConstraint> getSecurityConstraints()
    {
        return constraints;
    }
    
    /**
     * <p>
     * setSecurityConstraint
     * </p>
     *
     * @see org.apache.jetspeed.om.page.SecurityConstraintsDef#setSecurityConstraints(java.util.List)
     * @param constraints
     */
    public void setSecurityConstraints(List<SecurityConstraint> constraints)
    {
        this.constraints = constraints;
    }
}
