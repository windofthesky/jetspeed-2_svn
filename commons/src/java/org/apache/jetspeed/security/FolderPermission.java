/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.security;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;

import javax.security.auth.Subject;

/**
 * <p>Folder permission.</p>
 * <p>This code was partially inspired from articles from:</p>
 * <ul>
 *    <li><a href="http://www-106.ibm.com/developerworks/library/j-jaas/">
 *    Extend JAAS for class instance-level authorization.</a></li>
 * </ul>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 */
public class FolderPermission extends PortalResourcePermission
{    
    /**
     * <p>Constructor for FolderPermission.</p>
     * @param name The portlet name.
     * @param actions The actions on the portlet.
     */
    public FolderPermission(String name, String actions)
    {
        this(name, actions, null);
    }

    /**
     * <p>Constructor for FolderPermission.</p>
     * @param name The portlet name.
     * @param actions The actions on the portlet.
     */
    public FolderPermission(String name, String actions, Subject subject)
    {
        super(name, actions, subject);
    }

    public boolean implies(Permission permission)
    {
        // The permission must be an instance 
        // of the PortletPermission.
        if (!(permission instanceof FolderPermission))
        {
            return false;
        }

        // The portlet name must be the same.
        if (!(permission.getName().equals(getName())))
        {
            return false;
        }

        FolderPermission folderPerm = (FolderPermission) permission;

        // Get the subject.
        // It was either provide in the constructor.
        Subject user = folderPerm.getSubject();
        // Or we get it from the AccessControlContext.
        if (null == user)
        {
            AccessControlContext context = AccessController.getContext();
            user = Subject.getSubject(context);
        }
        // No user was passed.  The permission must be denied.
        if (null == user)
        {
            return false;
        }

        // The action bits in FolderPerm (permission) 
        // must be set in the current mask permission.
        if ((mask & folderPerm.mask) != folderPerm.mask)
        {
            return false;
        }

        return true;
    }

    /**
     * <p>Overrides <code>Permission.newPermissionCollection()</code>.</p>
     * @see java.security.Permission#newPermissionCollection()
     */
    public PermissionCollection newPermissionCollection()
    {
        return new PortalResourcePermissionCollection();
    }

}
