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
package org.apache.jetspeed.security;

import java.util.Collections;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PortletPermissionCollection extends PermissionCollection
{

    ArrayList perms = new ArrayList();

    /**
     * 
     */
    public PortletPermissionCollection()
    {
        super(); 
    }

    /**
     * @see java.security.PermissionCollection#add(java.security.Permission)
     */
    public void add(Permission permission)
    {
        perms.add(permission);
    }

    /**
     * @see java.security.PermissionCollection#implies(java.security.Permission)
     */
    public boolean implies(Permission permission)
    {
        for (Iterator i = perms.iterator(); i.hasNext(); ) 
        {
            if (((Permission)i.next()).implies(permission)) 
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @see java.security.PermissionCollection#elements()
     */
    public Enumeration elements()
    {
        return Collections.enumeration(perms);
    }

}
