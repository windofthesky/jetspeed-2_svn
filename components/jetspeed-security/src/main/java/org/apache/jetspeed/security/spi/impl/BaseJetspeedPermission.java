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

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.spi.PersistentJetspeedPermission;

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
public abstract class BaseJetspeedPermission extends Permission implements JetspeedPermission
{
    private static class JetspeedPermissionCollection extends PermissionCollection
    {
        private static final long serialVersionUID = -3852518088847803886L;
        private ArrayList<Permission> permissions = new ArrayList<Permission>();

        @Override
        public void add(Permission permission)
        {
            if (!permissions.contains(permission))
            {
                permissions.add(permission);
            }
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
    
    private final PersistentJetspeedPermission permission;

    public BaseJetspeedPermission(String type, String name, int mask)
    {
        super(name);
        this.permission = new PersistentJetspeedPermissionImpl(type, name);
        this.mask = mask;
    }
    
    public BaseJetspeedPermission(String type, String name, String actions)
    {
        super(name);
        this.permission = new PersistentJetspeedPermissionImpl(type, name);
        this.mask = JetspeedActions.getContainerActionsMask(actions);
    }
    
    public BaseJetspeedPermission(PersistentJetspeedPermission permission)
    {
        super(permission.getName());
        this.permission = permission;
        this.mask = JetspeedActions.getContainerActionsMask(permission.getActions());
    }
    
    public PersistentJetspeedPermission getPermission()
    {
        // ensure actions field is filled
        getActions();
        return permission;
    }
    
    public String getType()
    {
        return permission.getType();
    }

    /**
     * @see java.security.Permission#hashCode()
     */
    public int hashCode()
    {
        return getName().hashCode() ^ mask;
    }

    /**
     * @see java.security.Permission#getActions()
     */
    public String getActions()
    {
        if (permission.getActions() == null)
        {
            permission.setActions(JetspeedActions.getContainerActions(mask));
        }
        return permission.getActions();        
    }

    /* (non-Javadoc)
     * @see java.security.Permission#implies(java.security.Permission)
     */
    public boolean implies(Permission permission)
    {
        throw new IllegalStateException("Permission class did not implement implies");
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
