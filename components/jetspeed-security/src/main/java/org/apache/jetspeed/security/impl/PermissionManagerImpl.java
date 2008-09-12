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
import java.util.List;

import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.spi.JetspeedPermissionStorageManager;
import org.apache.jetspeed.security.spi.impl.JetspeedPermissionFactory;

/**
 * @version $Id$
 *
 */
public class PermissionManagerImpl implements PermissionManager
{
    public PermissionManagerImpl(List<JetspeedPermissionFactory> factories, JetspeedPermissionStorageManager jpsm)
    {
    }
    
    public Permissions getPermissions(JetspeedPrincipal principal)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Permissions getPermissions(Principal[] principals)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<JetspeedPermission> getPermissions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<JetspeedPermission> getPermissions(String typeName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<JetspeedPermission> getPermissions(String typeName, String nameFilter)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<JetspeedPrincipal> getPrincipals(JetspeedPermission permission)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public JetspeedPermission newPermission(String typeName, String name, String actions)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean permissionExists(JetspeedPermission permission)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void addPermission(JetspeedPermission permission)
    {
        // TODO Auto-generated method stub
        
    }

    public void grantPermission(JetspeedPermission permission, JetspeedPrincipal principal)
    {
        // TODO Auto-generated method stub
        
    }

    public void grantPermissionOnlyTo(JetspeedPermission permission, List<JetspeedPrincipal> principal)
    {
        // TODO Auto-generated method stub
        
    }

    public void removePermission(JetspeedPermission permission)
    {
        // TODO Auto-generated method stub
        
    }

    public void revokeAllPermissions(JetspeedPrincipal principal)
    {
        // TODO Auto-generated method stub
        
    }

    public void revokePermission(JetspeedPermission permission, JetspeedPrincipal principal)
    {
        // TODO Auto-generated method stub
        
    }

    public void updatePermission(JetspeedPermission permission, String actions)
    {
        // TODO Auto-generated method stub
        
    }

    public JetspeedPermission newPermission(String type, String name, int mask)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int parseActions(String actions)
    {
        // TODO Auto-generated method stub
        return 0;
    }
}
