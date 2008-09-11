/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.jetspeed.JetspeedActions;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * <p>Generalized Portlet Resoure permission.</p>
 * <p>This code was partially inspired from articles from:</p>
 * <ul>
 * <li><a href="http://www-106.ibm.com/developerworks/library/j-jaas/">
 * Extend JAAS for class instance-level authorization.</a></li>
 * </ul>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 */
public abstract class PortalResourcePermission extends Permission
{
    private static class JetspeedPermissionCollection extends PermissionCollection
    {
        private static final long serialVersionUID = -3852518088847803886L;
        private ArrayList<Permission> permissions = new ArrayList<Permission>();

        @Override
        public void add(Permission permission)
        {
            permissions.add(permission);
        }

        @Override
        public Enumeration<Permission> elements()
        {
            return Collections.enumeration(permissions);
        }

        @Override
        public boolean implies(Permission permission)
        {
            for (Permission p : permissions)
            {
                if (p.implies(permission))
                {
                    return true;
                }
            }
            return false;
        }
    }
    /**
     * <p>Mask used for determining what actions are allowed or requested.</p>
     */
    protected final int mask;

    /**
     * <p>Constructor for PortletPermission.</p>
     *
     * @param name    The portlet name.
     * @param actions The actions on the portlet.
     */
    public PortalResourcePermission(String name, String actions)
    {
        super(name);
        mask = parseActions(actions);
    }

    /**
     * <p>Constructor for PortletPermission.</p>
     *
     * @param name The portlet name.
     * @param mask The mask representing actions on the portlet.
     */
    public PortalResourcePermission(String name, int mask)
    {
        super(name);
        this.mask = mask;
    }

    /**
     * @see java.security.Permission#hashCode()
     */
    public int hashCode()
    {
        StringBuffer value = new StringBuffer(getName());
        return value.toString().hashCode() ^ mask;
    }

    /**
     * @see java.security.Permission#getActions()
     */
    public String getActions()
    {
        return JetspeedActions.getContainerActions(mask);
    }

    /* (non-Javadoc)
     * @see java.security.Permission#implies(java.security.Permission)
     */
    public boolean implies(Permission permission)
    {
        throw new IllegalStateException("Permission class did not implement implies");
    }

    /**
     * <p>Parses the actions string.</p>
     * <p>Actions are separated by commas or white space.</p>
     *
     * @param actions The actions
     */
    public static int parseActions(String actions)
    {
        return JetspeedActions.getContainerActionsMask(actions);
    }

    /**
     * <p>Overrides <code>Permission.newPermissionCollection()</code>.</p>
     *
     * @see java.security.Permission#newPermissionCollection()
     */
    public PermissionCollection newPermissionCollection()
    {
        return new JetspeedPermissionCollection();
    }
}
