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

import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.Permission;
import java.security.PermissionCollection;

import javax.security.auth.Subject;

/**
 * <p>Portlet permission.</p>
 * <p>This code was partially inspired from articles from:</p>
 * <ul>
 *    <li><a href="http://www-106.ibm.com/developerworks/library/j-jaas/">
 *    Extend JAAS for class instance-level authorization.</a></li>
 * </ul>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PortletPermission extends PortalResourcePermission
{

    /**
     * <p>Constructor for PortletPermission.</p>
     * @param name The portlet name.
     * @param actions The actions on the portlet.
     */
    public PortletPermission(String name, String actions)
    {
        this(name, actions, null);
    }

    /**
     * <p>Constructor for PortletPermission.</p>
     * @param name The portlet name.
     * @param actions The actions on the portlet.
     */
    public PortletPermission(String name, String actions, Subject subject)
    {
        super(name, actions, subject);
    }


    public boolean implies(Permission permission)
    {
        // The permission must be an instance 
        // of the PortletPermission.
        if (!(permission instanceof PortletPermission))
        {
            return false;
        }

        String name = getName(); 
        if (name != null)            
        {
            int index = name.indexOf('*');            
            if (index > -1)
            {
                if (!(permission.getName().startsWith(name.substring (0, index)))) 
                {
                    return false;
                }
            } 
            else if (!(permission.getName().equals(name)))
            {
                // The portlet name must be the same.
                return false;
            }            
        }
        
        PortletPermission portletPerm = (PortletPermission) permission;

        // Get the subject.
        // It was either provide in the constructor.
        Subject user = portletPerm.getSubject();
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

        // The action bits in portletPerm (permission) 
        // must be set in the current mask permission.
        if ((mask & portletPerm.mask) != portletPerm.mask)
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
