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
 * <p>
 * Configures an authentication provider.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public interface AuthenticationProvider
{
    
    /**
     * <p>
     * Gets the authentication provider name.
     * </p>
     * 
     * @return The authentication provider name.
     */
    String getProviderName();
    
    /**
     * <p>
     * Gets the authentication provider description.
     * </p>
     * 
     * @return The authentication provider description.
     */
    String getProviderDescription();
    
    /**
     * <p>
     * Authenticate a user.
     * </p>
     * 
     * @param userName The user name.
     * @param password The user password.
     * @return the {@link AuthenticatedUser}
     */
    AuthenticatedUser authenticate(String userName, String password) throws SecurityException;
}
