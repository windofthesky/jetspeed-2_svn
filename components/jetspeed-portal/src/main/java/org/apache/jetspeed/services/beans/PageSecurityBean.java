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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;

/**
 * PageSecurityBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="pageSecurity")
public class PageSecurityBean extends DocumentBean
{
    private static final long serialVersionUID = 1L;
    
    private List<SecurityConstraintsDefBean> securityConstraintsDefBeans;
    private List<String> globalSecurityConstraintsRefs;
    
    public PageSecurityBean()
    {
        
    }
    
    public PageSecurityBean(PageSecurity pageSecurity)
    {
        super(pageSecurity);
        
        List<SecurityConstraintsDef> securityConstraintsDefs = pageSecurity.getSecurityConstraintsDefs();
        
        if (securityConstraintsDefs != null)
        {
            securityConstraintsDefBeans = new ArrayList<SecurityConstraintsDefBean>();
            
            for (SecurityConstraintsDef securityConstraintsDef : securityConstraintsDefs)
            {
                securityConstraintsDefBeans.add(new SecurityConstraintsDefBean(securityConstraintsDef));
            }
        }
        
        List<String> temp = pageSecurity.getGlobalSecurityConstraintsRefs();
        
        if (temp != null)
        {
            globalSecurityConstraintsRefs = new ArrayList<String>(temp);
        }
    }

    @XmlElementWrapper(name="securityConstraintsDefs")
    @XmlElements(@XmlElement(name="securityConstraintsDef"))
    public List<SecurityConstraintsDefBean> getSecurityConstraintsDefBeans()
    {
        return securityConstraintsDefBeans;
    }

    public void setSecurityConstraintsDefBeans(List<SecurityConstraintsDefBean> securityConstraintsDefBeans)
    {
        this.securityConstraintsDefBeans = securityConstraintsDefBeans;
    }

    @XmlElementWrapper(name="globalSecurityConstraintsRefs")
    @XmlElements(@XmlElement(name="globalSecurityConstraintsRef"))
    public List<String> getGlobalSecurityConstraintsRefs()
    {
        return globalSecurityConstraintsRefs;
    }

    public void setGlobalSecurityConstraintsRefs(List<String> globalSecurityConstraintsRefs)
    {
        this.globalSecurityConstraintsRefs = globalSecurityConstraintsRefs;
    }
    
}
