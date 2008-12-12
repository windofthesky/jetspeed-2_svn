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
package org.apache.jetspeed.security;

/**
 * <p>Utility component used as a bridge between the login module and the security component.</p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface LoginModuleProxy
{
    /**
     * <p>Default .portal user role name</p>
     */
    String DEFAULT_PORTAL_USER_ROLE_NAME = "portal-user";

    /**
     * <p>Getter for the {@link AuthenticationProvider}.</p>
     * @return The AuthenticationProvider
     */
    AuthenticationProvider getAuthenticationProvider();

    /**
     * <p>Getter for the {@link UserManager}.</p>
     * @return The UserManager.
     */
    UserManager getUserManager();
    
    /**
     * <p>Getter for the required portal user role name.</p>
     *
     * <p>Used in web.xml authorization to detect authenticated portal users.</p>
     *
     * @return The portal user role name.
     */
    String getPortalUserRole();
}
