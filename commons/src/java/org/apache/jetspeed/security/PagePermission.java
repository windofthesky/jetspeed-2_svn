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

import java.security.Permission;

/**
 * <p>Folder permission.</p>
 * <p>This code was partially inspired from articles from:</p>
 * <ul>
 * <li><a href="http://www-106.ibm.com/developerworks/library/j-jaas/">
 * Extend JAAS for class instance-level authorization.</a></li>
 * </ul>
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 */
public class PagePermission extends PortalResourcePermission
{
    /**
     * <p>Constructor for PagePermission.</p>
     *
     * @param name    The portlet name.
     * @param actions The actions on the portlet.
     */
    public PagePermission(String name, String actions)
    {
        super(name, actions);
    }

    /**
     * <p>Constructor for PagePermission.</p>
     *
     * @param name The portlet name.
     * @param mask The mask for actions on the portlet.
     */
    public PagePermission(String name, int mask)
    {
        super(name, mask);
    }

    public boolean implies(Permission permission)
    {
        // The permission must be an instance 
        // of the PortletPermission.
        if (!(permission instanceof PagePermission))
        {
            return false;
        }

        // The page name must be the same.
        if (!(permission.getName().equals(getName())))
        {
            return false;
        }

        PagePermission pagePerm = (PagePermission) permission;

        // The action bits in PagePerm (permission)
        // must be set in the current mask permission.
        return (mask & pagePerm.mask) == pagePerm.mask;

    }

    /**
     * @see java.security.Permission#equals(Object)
     */
    public boolean equals(Object object)
    {
        if (!(object instanceof PagePermission))
            return false;

        PagePermission p = (PagePermission) object;
        return ((p.mask == mask) && (p.getName().equals(getName())));
    }

}
