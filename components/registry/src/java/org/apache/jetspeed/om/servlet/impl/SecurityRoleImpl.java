/*
 * Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.om.servlet.impl;

import java.io.Serializable;

import org.apache.jetspeed.om.common.servlet.MutableSecurityRole;
import org.apache.pluto.om.common.SecurityRole;

/**
 * MutableSecurityRoleImpl
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id$
 */
public class SecurityRoleImpl implements SecurityRole, MutableSecurityRole, Serializable {

    protected long webAppId;

    private String description;

    private String roleName;

    /**
     * Default constructor.
     */
    public SecurityRoleImpl() {
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRole#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRole#getRoleName()
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @see org.apache.jetspeed.om.common.servlet.MutableSecurityRole#setDescription(java.lang.String)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @see org.apache.jetspeed.om.common.servlet.MutableSecurityRole#setRoleName(java.lang.String)
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Convert {@link SecurityRole}to String.
     *
     * @return String value of SecurityRole.
     */
    public String toString() {
        String securityRole = "[[roleName, " + this.roleName + "], [description, " + this.description + "]]";
        return securityRole;
    }
    
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if ( obj != null && obj instanceof SecurityRoleImpl ) {
            return getRoleName().equals(((SecurityRoleImpl)obj).getRoleName());
        }
        return false;
    }

    /** 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getRoleName().hashCode();
    }
}
