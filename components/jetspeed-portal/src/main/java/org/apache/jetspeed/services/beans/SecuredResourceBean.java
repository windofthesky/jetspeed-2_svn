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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.common.SecuredResource;

/**
 * SecuredResourceBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="securedResource")
public class SecuredResourceBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private boolean constraintsEnabled;
    
    private boolean permissionsEnabled;
    
    private SecurityConstraintsBean securityConstraintsBean;
    
    public SecuredResourceBean()
    {
        
    }
    
    public SecuredResourceBean(SecuredResource securedResource)
    {
        constraintsEnabled = securedResource.getConstraintsEnabled();
        permissionsEnabled = securedResource.getPermissionsEnabled();
    }

    public boolean isConstraintsEnabled()
    {
        return constraintsEnabled;
    }

    public void setConstraintsEnabled(boolean constraintsEnabled)
    {
        this.constraintsEnabled = constraintsEnabled;
    }

    public boolean isPermissionsEnabled()
    {
        return permissionsEnabled;
    }

    public void setPermissionsEnabled(boolean permissionsEnabled)
    {
        this.permissionsEnabled = permissionsEnabled;
    }

    @XmlElement(name="securityConstraints")
    public SecurityConstraintsBean getSecurityConstraintsBean()
    {
        return securityConstraintsBean;
    }

    public void setSecurityConstraintsBean(SecurityConstraintsBean securityConstraintsBean)
    {
        this.securityConstraintsBean = securityConstraintsBean;
    }
    
}
