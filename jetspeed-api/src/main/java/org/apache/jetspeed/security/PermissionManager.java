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
package org.apache.jetspeed.security;

import java.security.Permissions;
import java.security.Principal;
import java.util.List;

/**
 * @version $Id$
 */
public interface PermissionManager extends PermissionFactory
{
    boolean permissionExists(JetspeedPermission permission);
    Permissions getPermissions(JetspeedPrincipal principal);

    Permissions getPermissions(Principal[] principals);
    
    List<JetspeedPermission> getPermissions();

    List<JetspeedPermission> getPermissions(String typeName);

    List<JetspeedPermission> getPermissions(String typeName, String nameFilter);

    List<JetspeedPrincipal> getPrincipals(JetspeedPermission permission);
    List<JetspeedPrincipal> getPrincipals(JetspeedPermission permission, String principalType);
    
    void addPermission(JetspeedPermission permission) throws SecurityException;
    void removePermission(JetspeedPermission permission) throws SecurityException;
    void updatePermission(JetspeedPermission permission) throws SecurityException;

    void grantPermission(JetspeedPermission permission, JetspeedPrincipal principal) throws SecurityException;
    void revokePermission(JetspeedPermission permission, JetspeedPrincipal principal) throws SecurityException;
    void grantPermissionOnlyTo(JetspeedPermission permission, List<JetspeedPrincipal> principals) throws SecurityException;
    void grantPermissionOnlyTo(JetspeedPermission permission, String principalType, List<JetspeedPrincipal> principals) throws SecurityException;
    void revokeAllPermissions(JetspeedPrincipal principal) throws SecurityException;
}