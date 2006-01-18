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
package org.apache.jetspeed.security.spi.ldap;

import org.apache.jetspeed.security.GroupPrincipal;
import org.apache.jetspeed.security.RolePrincipal;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.GroupPrincipalImpl;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.spi.CredentialHandler;
import org.apache.jetspeed.security.spi.GroupSecurityHandler;
import org.apache.jetspeed.security.spi.RoleSecurityHandler;
import org.apache.jetspeed.security.spi.UserSecurityHandler;

/**
 * <p>
 * Utility class for LDAP test data.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class LdapDataHelper
{
    /** The {@link UserSecurityHandler}. */
    private static UserSecurityHandler userHandler;

    /** The {@link CredentialHandler}. */
    private static CredentialHandler crHandler;

    /** The {@link GroupSecurityHandler}. */
    private static GroupSecurityHandler grHandler;
    
    /** The {@link RoleSecurityHandler}. */
    private static RoleSecurityHandler roleHandler;    
    
    public static void seedUserData(String uid, String password) throws Exception
    {
        UserPrincipal up = new UserPrincipalImpl(uid);
        userHandler.addUserPrincipal(up);
        crHandler.setPassword(uid, "", password);
    }
    
    public static void seedGroupData(String gpUid) throws Exception
    {
        GroupPrincipal gp = new GroupPrincipalImpl(gpUid);
        grHandler.setGroupPrincipal(gp);
    }
    
    public static void seedRoleData(String roleUid) throws Exception
    {
        RolePrincipal rp = new RolePrincipalImpl(roleUid);
        roleHandler.setRolePrincipal(rp);
    }
    
    public static void removeUserData(String uid) throws Exception
    {
        UserPrincipal up = new UserPrincipalImpl(uid);
        userHandler.removeUserPrincipal(up);
    }
    
    public static void removeGroupData(String gpUid) throws Exception
    {
        GroupPrincipal gp = new GroupPrincipalImpl(gpUid);
        grHandler.removeGroupPrincipal(gp);
    }
    
    public static void removeRoleData(String roleUid) throws Exception
    {
        RolePrincipal rp = new RolePrincipalImpl(roleUid);
        roleHandler.removeRolePrincipal(rp);
    }    
    
    public static void setUserSecurityHandler(UserSecurityHandler userHandlerVar)
    {
        userHandler = userHandlerVar;
    }
    
    public static void setCredentialHandler(CredentialHandler crHandlerVar)
    {
        crHandler = crHandlerVar;
    }
    
    public static void setGroupSecurityHandler(GroupSecurityHandler grHandlerVar)
    {
        grHandler = grHandlerVar;
    }
    
    public static void setRoleSecurityHandler(RoleSecurityHandler roleHandlerVar)
    {
        roleHandler = roleHandlerVar;
    }    
}
