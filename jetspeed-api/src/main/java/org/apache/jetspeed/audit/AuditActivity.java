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
package org.apache.jetspeed.audit;

import javax.sql.DataSource;

/**
 * Gathers information about security auditing activity
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public interface AuditActivity
{
    // user activities
    public static final String AUTHENTICATION_SUCCESS = "login-success";
    public static final String AUTHENTICATION_FAILURE = "login-failure";
    public static final String PASSWORD_CHANGE_SUCCESS = "password-success";
    public static final String PASSWORD_CHANGE_FAILURE = "password-failure";
    
    // admin activities
    public static final String USER_CREATE = "user-create";
    public static final String USER_UPDATE = "user-update";
    public static final String USER_DELETE = "user-delete";
    public static final String USER_DISABLE = "user-disable";
    public static final String USER_EXTEND = "user-extend";    
    public static final String USER_EXTEND_UNLIMITED = "user-extend-unlimited";    

    public static final String PASSWORD_EXPIRE = "password-expire";
    public static final String PASSWORD_RESET = "password-reset";
    public static final String PASSWORD_ACTIVATE  = "password-activate";
    public static final String PASSWORD_ENABLED  = "password-enabled";
    public static final String PASSWORD_DISABLED  = "password-disabled";        
    public static final String PASSWORD_UPDATE_REQUIRED = "password-update-req";
    public static final String PASSWORD_EXTEND = "password-extend";
    public static final String PASSWORD_UNLIMITED = "password-unlimited";
    
    public static final String USER_ADD_ROLE = "user-add-role";
    public static final String USER_DELETE_ROLE = "user-delete-role";
    public static final String USER_ADD_GROUP = "user-add-group";
    public static final String USER_DELETE_GROUP = "user-delete-group";
    public static final String USER_ADD_PROFILE = "user-add-profile";
    public static final String USER_DELETE_PROFILE = "user-delete-profile";

    public static final String USER_ADD_ATTRIBUTE = "user-add-attr";
    public static final String USER_DELETE_ATTRIBUTE = "user-delete-attr";
    public static final String USER_UPDATE_ATTRIBUTE = "user-update-attr";
    
    public static final String REGISTRY_DEPLOY = "registry-deploy";
    public static final String REGISTRY_UNDEPLOY = "registry-undeploy";
    public static final String REGISTRY_STOP = "registry-stop";
    public static final String REGISTRY_START = "registry-start";
    public static final String REGISTRY_DELETE = "registry-delete";
    
    // General Categories
    public static final String CAT_USER_AUTHENTICATION = "authentication";
    public static final String CAT_USER_ATTRIBUTE = "user-attribute";
    public static final String CAT_ADMIN_USER_MAINTENANCE = "user";
    public static final String CAT_ADMIN_CREDENTIAL_MAINTENANCE = "credential";
    public static final String CAT_ADMIN_ATTRIBUTE_MAINTENANCE = "attribute";
    public static final String CAT_ADMIN_AUTHORIZATION_MAINTENANCE = "authorization";    
    public static final String CAT_ADMIN_REGISTRY_MAINTENANCE = "registry";    
    
    /**
     * Enable or disable the service at runtime
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled);
    
    /**
     * Get the enabled state of this service
     * @return
     */
    public boolean getEnabled();
    
    /**
     * Log user security-audit-related activity
     * 
     * @param username
     * @param ipaddress
     * @param activity
     * @param description
     */
    public void logUserActivity(String username, String ipaddress, String activity, String description);

    /**
     * Log auditable activity by an administrator on behalf of another user
     * 
     * @param username
     * @param ipaddress
     * @param targetUser
     * @param activity
     * @param description
     */
    public void logAdminUserActivity(String username, String ipaddress, String targetUser, String activity, String description);

    /**
     * Log auditable activity by an administrator on credentials on behalf of a user
     * 
     * @param adminName
     * @param ipaddress
     * @param targetUser
     * @param activity
     * @param description
     */
    public void logAdminCredentialActivity(String username, String ipaddress, String targetUser, String activity, String description);
    
    public void logAdminAuthorizationActivity(String username, String ipaddress, String targetUser, String activity, String name, String description);
    
    /**
     * Log auditable activity by an administrator on attributes on behalf of a user
     * 
     * @param username
     * @param ipaddress
     * @param targetUser
     * @param activity
     * @param name
     * @param beforeValue
     * @param afterValue
     * @param description
     */
    public void logAdminAttributeActivity(String username, String ipaddress, String targetUser, String activity, String name, String beforeValue, String afterValue, String description);

    /**
     * Log auditable activity by an administrator on attributes on behalf of a user
     * 
     * @param username
     * @param ipaddress
     * @param activity
     * @param name
     * @param beforeValue
     * @param afterValue
     * @param description
     */
    public void logUserAttributeActivity(String username, String ipaddress, String activity, String name, String beforeValue, String afterValue, String description);

    /**
     * Log auditable activity by an administrator on registry maintenance
     * 
     * @param adminUser
     * @param ipaddress
     * @param activity
     * @param description
     */
    public void logAdminRegistryActivity(String adminUser, String ipaddress, String activity, String description);
    
    /**
     * @return DataSource in use by the logger useful for writing decent tests
     */
    public DataSource getDataSource();
    
} 