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
 * <p>Portlet permission.</p>
 * <p>This code was partially inspired from articles from:</p>
 * <ul>
 * <li><a href="http://www-106.ibm.com/developerworks/library/j-jaas/">
 * Extend JAAS for class instance-level authorization.</a></li>
 * </ul>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PortletPermission extends BaseJetspeedPermission
{
    private static final long serialVersionUID = 3246898917185555596L;

    public static class Factory extends JetspeedPermissionFactory
    {
        public Factory()
        {
            super(PermissionFactory.PORTLET_PERMISSION);
        }

        public PortletPermission newPermission(String name, String actions)
        {
            return new PortletPermission(getType(), name, actions);
        }

        public PortletPermission newPermission(String name, int mask)
        {
            return new PortletPermission(getType(), name, mask);
        }

        public PortletPermission newPermission(PersistentJetspeedPermission permission)
        {
            if (permission.getType().equals(getType()))
            {
                return new PortletPermission(permission);
            }
            throw new IllegalArgumentException("Permission is not of type "+getType());
        }
    }
    
    protected PortletPermission(PersistentJetspeedPermission permission)
    {
        super(permission);
    }

    protected PortletPermission(String type, String name, int mask)
    {
        super(type, name, mask);
    }

    protected PortletPermission(String type, String name, String actions)
    {
        super(type, name, actions);
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
                if (!(permission.getName().startsWith(name.substring(0, index))))
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

        // The action bits in portletPerm (permission) 
        // must be set in the current mask permission.
        return (mask & portletPerm.mask) == portletPerm.mask;

    }

    /**
     * @see java.security.Permission#equals(Object)
     */
    public boolean equals(Object object)
    {
        if (!(object instanceof PortletPermission))
            return false;

        PortletPermission p = (PortletPermission) object;
        return ((p.mask == mask) && (p.getName().equals(getName())));
    }

}
