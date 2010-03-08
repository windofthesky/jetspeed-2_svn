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
import org.apache.jetspeed.om.common.SecurityConstraints;

/**
 * SecurityConstraintsBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="securityConstraints")
public class SecurityConstraintsBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String owner;
    
    private List<SecurityConstraintBean> securityConstraintBeans;
    
    private List<String> securityConstraintsRefs;
    
    private boolean empty;
    
    public SecurityConstraintsBean()
    {
        
    }
    
    public SecurityConstraintsBean(SecurityConstraints securityConstraints)
    {
        owner = securityConstraints.getOwner();
        
        List<SecurityConstraint> securityConstraintList = securityConstraints.getSecurityConstraints();
        
        if (securityConstraintList != null)
        {
            securityConstraintBeans = new ArrayList<SecurityConstraintBean>();
            
            for (SecurityConstraint securityConstraint : securityConstraintList)
            {
                securityConstraintBeans.add(new SecurityConstraintBean(securityConstraint));
            }
        }
        
        List<String> temp = securityConstraints.getSecurityConstraintsRefs();
        
        if (temp != null)
        {
            securityConstraintsRefs = new ArrayList<String>(temp);
        }
        
        empty = securityConstraints.isEmpty();
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
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

    @XmlElementWrapper(name="securityConstraintRefs")
    @XmlElements(@XmlElement(name="securityConstraintRef"))
    public List<String> getSecurityConstraintsRefs()
    {
        return securityConstraintsRefs;
    }

    public void setSecurityConstraintsRefs(List<String> securityConstraintsRefs)
    {
        this.securityConstraintsRefs = securityConstraintsRefs;
    }

    public boolean isEmpty()
    {
        return empty;
    }

    public void setEmpty(boolean empty)
    {
        this.empty = empty;
    }

}
