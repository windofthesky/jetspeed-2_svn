/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.security.rolemgt;

import java.io.Serializable;

/**
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class RoleActionForm implements Serializable
{
    /** The role action form bean. */
    public final static String ROLE_ACTION_FORM = "roleActionForm";

    /** The parent role. */
    private RoleTreeItem parentRole;

    /** The selected roles. */
    private String[] selectedRoles;

    /** The new role name. */
    private String roleName;

    /** The new role path. */
    private String rolePath;

    /**
     * <p>
     * Default Constructor.
     * </p>
     */
    public RoleActionForm()
    {
        this.parentRole = new RoleTreeItem("/role", "role");
    }

    /**
     * @return Returns the parentRole.
     */
    public RoleTreeItem getParentRole()
    {
        return parentRole;
    }

    /**
     * @param parentRole The parentRole to set.
     */
    public void setParentRole(RoleTreeItem parentRole)
    {
        this.parentRole = parentRole;
    }

    /**
     * @return Returns the selectedRoles.
     */
    public String[] getSelectedRoles()
    {
        return selectedRoles;
    }

    /**
     * @param selectedRoles The selectedRoles to set.
     */
    public void setSelectedRoles(String[] selectedRoles)
    {
        this.selectedRoles = selectedRoles;
    }

    /**
     * @return Returns the roleName.
     */
    public String getRoleName()
    {
        return roleName;
    }

    /**
     * @param roleName The roleName to set.
     */
    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

    /**
     * @return Returns the rolePath.
     */
    public String getRolePath()
    {
        return rolePath;
    }

    /**
     * @param rolePath The rolePath to set.
     */
    public void setRolePath(String rolePath)
    {
        this.rolePath = rolePath;
    }

    /**
     * <p>
     * Add a new role.
     * </p>
     */
    public void addRole()
    {
        // Call the role manager. Add the role.
        String newRoleFullPath = getParentRole().getFullPath() + "/" + getRoleName();
        System.out.println("******* New Full Path: " + newRoleFullPath);
    }   

}