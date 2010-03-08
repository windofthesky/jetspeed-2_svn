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
package org.apache.jetspeed.services.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;

/**
 * SecurityConstraintsDefBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="securityConstraintsDef")
public class SecurityConstraintsDefBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String name;
    private List<SecurityConstraintBean> securityConstraintBeans;
    
    public SecurityConstraintsDefBean()
    {
        
    }
    
    public SecurityConstraintsDefBean(SecurityConstraintsDef securityConstraintsDef)
    {
        name = securityConstraintsDef.getName();
        
        List<SecurityConstraint> securityConstraints = securityConstraintsDef.getSecurityConstraints();
        
        if (securityConstraints != null)
        {
            securityConstraintBeans = new ArrayList<SecurityConstraintBean>();
            
            for (SecurityConstraint securityConstraint : securityConstraints)
            {
                securityConstraintBeans.add(new SecurityConstraintBean(securityConstraint));
            }
        }
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @XmlElementWrapper(name="securityConstraints")
    @XmlElements(@XmlElement(name="securityConstraint"))
    public List<SecurityConstraintBean> getSecurityConstraintBeans()
    {
        return securityConstraintBeans;
    }

    public void setSecurityConstraintBeans(List<SecurityConstraintBean> securityConstraintBeans)
    {
        this.securityConstraintBeans = securityConstraintBeans;
    }
    
}
