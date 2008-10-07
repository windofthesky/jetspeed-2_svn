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
package org.apache.jetspeed.security.spi.impl;

import java.security.Permission;

import org.apache.jetspeed.security.PermissionFactory;
import org.apache.jetspeed.security.spi.PersistentJetspeedPermission;

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
public class PagePermission extends BaseJetspeedPermission
{
    private static final long serialVersionUID = -3374203894346164388L;

    public static class Factory extends JetspeedPermissionFactory
    {
        public Factory()
        {
            super(PermissionFactory.PAGE_PERMISSION);
        }

        public PagePermission newPermission(String name, String actions)
        {
            return new PagePermission(getType(), name, actions);
        }

        public PagePermission newPermission(String name, int mask)
        {
            return new PagePermission(getType(), name, mask);
        }

        public PagePermission newPermission(PersistentJetspeedPermission permission)
        {
            if (permission.getType().equals(getType()))
            {
                return new PagePermission(permission);
            }
            throw new IllegalArgumentException("Permission is not of type "+getType());
        }
    }
    
    protected PagePermission(PersistentJetspeedPermission permission)
    {
        super(permission);
    }

    protected PagePermission(String type, String name, int mask)
    {
        super(type, name, mask);
    }

    protected PagePermission(String type, String name, String actions)
    {
        super(type, name, actions);
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
