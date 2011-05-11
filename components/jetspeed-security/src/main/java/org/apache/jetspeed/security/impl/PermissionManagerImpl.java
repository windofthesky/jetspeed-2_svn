/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.security.impl;

import java.security.Permissions;
import java.security.Principal;
import java.security.Permission;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.JetspeedPermissionAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPermissionStorageManager;
import org.apache.jetspeed.security.spi.PersistentJetspeedPermission;
import org.apache.jetspeed.security.spi.impl.BaseJetspeedPermission;
import org.apache.jetspeed.security.spi.impl.JetspeedPermissionFactory;
import org.apache.jetspeed.util.ServletRequestThreadLocalCleanupCallback;

/**
 * @version $Id$
 *
 */
public class PermissionManagerImpl implements PermissionManager
{
    private HashMap<String, JetspeedPermissionFactory> factoryMap = new HashMap<String, JetspeedPermissionFactory>();
    private JetspeedPermissionAccessManager jpam;
    private JetspeedPermissionStorageManager jpsm;
    private static ThreadLocal<HashMap<Long,Permissions>> permissionsCache = new ThreadLocal<HashMap<Long,Permissions>>();
    
    public PermissionManagerImpl(List<JetspeedPermissionFactory> factories, JetspeedPermissionAccessManager jpam, JetspeedPermissionStorageManager jpsm)
    {
        for (JetspeedPermissionFactory pf : factories)
        {
            factoryMap.put(pf.getType(), pf);
        }
        this.jpam = jpam;
        this.jpsm = jpsm;
    }
    
    private HashMap<Long,Permissions> getPermissionsMap()
    {
        HashMap<Long,Permissions> map = permissionsCache.get();
        if (map == null)
        {
            map = new HashMap<Long,Permissions>();
            permissionsCache.set(map);
            new ServletRequestThreadLocalCleanupCallback(permissionsCache);            
        }
        return map;
    }
    
    protected PersistentJetspeedPermission getPersistentJetspeedPermission(JetspeedPermission permission)
    {
        if (permission instanceof PersistentJetspeedPermission)
        {
            return (PersistentJetspeedPermission)permission;
        }
        else
        {
            return ((BaseJetspeedPermission)permission).getPermission();
        }
    }
    
    public JetspeedPermission newPermission(String type, String name, String actions)
    {
        return factoryMap.get(type).newPermission(name, actions);
    }

    public JetspeedPermission newPermission(String type, String name, int mask)
    {
        return factoryMap.get(type).newPermission(name, mask);
    }

    public int parseActions(String actions)
    {
        return JetspeedActions.getContainerActionsMask(actions);
    }
    
    @SuppressWarnings("unchecked") 
    public Permissions getPermissions(JetspeedPrincipal principal)
    {
        Permissions permissions = null;
        if (principal instanceof PersistentJetspeedPrincipal && ((PersistentJetspeedPrincipal)principal).getId() != null)
        {
            HashMap<Long,Permissions> permissionsMap = getPermissionsMap();
            Long id = ((PersistentJetspeedPrincipal)principal).getId();
            permissions = permissionsMap.get(id);
            if (permissions == null)
            {
                permissions = new Permissions();
                List<PersistentJetspeedPermission> permList = (List<PersistentJetspeedPermission>)jpam.getPermissions((PersistentJetspeedPrincipal)principal);        
                for (PersistentJetspeedPermission p : permList)
                {
                    permissions.add(factoryMap.get(p.getType()).newPermission(p));
                }
                permissionsMap.put(id, permissions);
            }
        }
        else
        {
            permissions = new Permissions();
        }
        return permissions;
    }

    @SuppressWarnings("unchecked") 
    public Permissions getPermissions(Principal[] principals)
    {
        Permissions allPermissions = new Permissions();
        HashMap<Long,Permissions> permissionsMap = null;
        for (Principal principal : principals)
        {
            if (principal instanceof PersistentJetspeedPrincipal && ((PersistentJetspeedPrincipal)principal).getId() != null)
            {
                if (permissionsMap == null)
                {
                    permissionsMap = getPermissionsMap();
                }
                Long id = ((PersistentJetspeedPrincipal)principal).getId();
                Permissions permissions = permissionsMap.get(id);
                if (permissions == null)
                {
                    permissions = new Permissions();
                    List<PersistentJetspeedPermission> permList = (List<PersistentJetspeedPermission>)jpam.getPermissions((PersistentJetspeedPrincipal)principal);        
                    for (PersistentJetspeedPermission p : permList)
                    {
                        permissions.add(factoryMap.get(p.getType()).newPermission(p));
                    }
                    permissionsMap.put(id, permissions);
                }
                for (Enumeration<Permission> e = permissions.elements(); e.hasMoreElements(); )
                {
                    allPermissions.add(e.nextElement());
                }
            }
        }
        return allPermissions;
    }

    @SuppressWarnings("unchecked") 
    public List<JetspeedPermission> getPermissions()
    {
        return (List<JetspeedPermission>)jpam.getPermissions();
    }

    @SuppressWarnings("unchecked") 
    public List<JetspeedPermission> getPermissions(String typeName)
    {
        return (List<JetspeedPermission>)jpam.getPermissions(typeName);
    }

    @SuppressWarnings("unchecked") 
    public List<JetspeedPermission> getPermissions(String typeName, String nameFilter)
    {
        return (List<JetspeedPermission>)jpam.getPermissions(typeName, nameFilter);
    }

    public List<JetspeedPrincipal> getPrincipals(JetspeedPermission permission)
    {
        return jpam.getPrincipals(getPersistentJetspeedPermission(permission), null);
    }

    public List<JetspeedPrincipal> getPrincipals(JetspeedPermission permission, String principalType)
    {
        return jpam.getPrincipals(getPersistentJetspeedPermission(permission), principalType);
    }

    public boolean permissionExists(JetspeedPermission permission)
    {
        return jpam.permissionExists(permission);
    }

    public void addPermission(JetspeedPermission permission) throws SecurityException
    {
        jpsm.addPermission(getPersistentJetspeedPermission(permission));
    }

    public void updatePermission(JetspeedPermission permission) throws SecurityException
    {
        jpsm.updatePermission(getPersistentJetspeedPermission(permission));
        permissionsCache.remove();
    }

    public void removePermission(JetspeedPermission permission) throws SecurityException
    {
        jpsm.removePermission(getPersistentJetspeedPermission(permission));
        permissionsCache.remove();
    }

    public void grantPermission(JetspeedPermission permission, JetspeedPrincipal principal) throws SecurityException
    {
        jpsm.grantPermission(getPersistentJetspeedPermission(permission), principal);
        permissionsCache.remove();
    }

    public void grantPermissionOnlyTo(JetspeedPermission permission, List<JetspeedPrincipal> principals) throws SecurityException
    {
        jpsm.grantPermissionOnlyTo(getPersistentJetspeedPermission(permission), null, principals);
        permissionsCache.remove();
    }

    public void grantPermissionOnlyTo(JetspeedPermission permission, String principalType, List<JetspeedPrincipal> principals) throws SecurityException
    {
        jpsm.grantPermissionOnlyTo(getPersistentJetspeedPermission(permission), principalType, principals);
        permissionsCache.remove();
    }

    public void revokePermission(JetspeedPermission permission, JetspeedPrincipal principal) throws SecurityException
    {
        jpsm.revokePermission(getPersistentJetspeedPermission(permission), principal);
        permissionsCache.remove();
    }

    public void revokeAllPermissions(JetspeedPrincipal principal) throws SecurityException
    {
        jpsm.revokeAllPermissions(principal);
        permissionsCache.remove();
    }
}
