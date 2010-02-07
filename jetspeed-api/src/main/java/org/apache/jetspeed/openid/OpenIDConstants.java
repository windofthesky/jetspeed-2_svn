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
package org.apache.jetspeed.openid;

/**
 * OpenID login portlet and relaying party servlet constants.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public final class OpenIDConstants
{
    public final static String OPEN_ID_DISCOVERY = "org.apache.jetspeed.openid.discovery";
    public final static String OPEN_ID_PROVIDER = "org.apache.jetspeed.openid.provider";
    public final static String OPEN_ID_RETURN = "org.apache.jetspeed.openid.return";
    public final static String OPEN_ID_REQUEST = "org.apache.jetspeed.openid.request";
    public final static String OPEN_ID_LOGIN_REQUEST = "login";
    public final static String OPEN_ID_AUTHENTICATED_REQUEST = "authed";
    public final static String OPEN_ID_LOGOUT_REQUEST = "logout";
    public final static String OPEN_ID_ERROR = "org.apache.jetspeed.openid.error";
    public final static String OPEN_ID_ERROR_NO_PROVIDER = "ErrorNoProvider";
    public final static String OPEN_ID_ERROR_CANNOT_AUTH = "ErrorCannotAuthenticate";
    public final static String OPEN_ID_ERROR_NOT_AUTH = "ErrorNotAuthenticated";
    public final static String OPEN_ID_ERROR_NO_PORTAL_USER = "ErrorNoPortaUser";
    public final static String OPEN_ID_ERROR_CANNOT_LOGIN = "ErrorCannotLogin";

    public static final String OPEN_ID_REGISTRATION_CONFIGURATION = "org.apache.jetspeed.openid.registration.configuration";

    public static final String ENABLE_REGISTRATION_CONFIG_INIT_PARAM_NAME = "enableRegistrationConfig";
    public static final String ENABLE_REGISTRATION_INIT_PARAM_NAME = "enableRegistration";
    public static final String REGISTRATION_USER_TEMPLATE_INIT_PARAM_NAME = "newUserTemplateDirectory";
    public static final String REGISTRATION_SUBSITE_ROOT_INIT_PARAM_NAME = "subsiteRootFolder";
    public static final String REGISTRATION_ROLES_INIT_PARAM_NAME = "roles";
    public static final String REGISTRATION_GROUPS_INIT_PARAM_NAME = "groups";
    public static final String REGISTRATION_PROFILER_RULE_NAMES_INIT_PARAM_NAME = "rulesNames";
    public static final String REGISTRATION_PROFILER_RULE_VALUES_INIT_PARAM_NAME = "rulesValues";
}
