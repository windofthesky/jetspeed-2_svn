/*
 * Copyright 2004 The Apache Software Foundation.
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
 * <p>
 * Bean class holding a role tree item.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class RoleTreeItem implements Serializable
{
    /** The full path. */
    private String fullPath;

    /** The role name. */
    private String roleName;
    
    /**
     * @param fullPath The full path.
     * @param roleName The role name.
     */
    public RoleTreeItem(String fullPath, String roleName)
    {
        this.fullPath = fullPath;
        this.roleName = roleName;
    }

    /**
     * @return Returns the fullPath.
     */
    public String getFullPath()
    {
        return fullPath;
    }

    /**
     * @param fullPath The fullPath to set.
     */
    public void setFullPath(String fullPath)
    {
        this.fullPath = fullPath;
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

}