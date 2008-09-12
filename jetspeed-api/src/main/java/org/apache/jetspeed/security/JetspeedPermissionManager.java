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
public interface JetspeedPermissionManager extends JetspeedPermissionsFactory
{
    boolean permissionExists(JetspeedPermission permission);
    Permissions getPermissions(JetspeedPrincipal principal);

    Permissions getPermissions(Principal[] principals);
    
    List<JetspeedPermission> getPermissions();

    List<JetspeedPermission> getPermissions(String typeName);

    List<JetspeedPermission> getPermissions(String typeName, String nameFilter);

    List<JetspeedPrincipal> getPrincipals(JetspeedPermission permission);
    
    void addPermission(JetspeedPermission permission);
    void removePermission(JetspeedPermission permission);
    void updatePermission(JetspeedPermission permission, String actions);

    void grantPermission(JetspeedPermission permission, JetspeedPrincipal principal);
    void revokePermission(JetspeedPermission permission, JetspeedPrincipal principal);
    void grantPermissionOnlyTo(JetspeedPermission permission, List<JetspeedPrincipal> principal);
    void revokeAllPermissions(JetspeedPrincipal principal);
}