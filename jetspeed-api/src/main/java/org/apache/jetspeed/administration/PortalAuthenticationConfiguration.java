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
package org.apache.jetspeed.administration;


/**
 * Portal Authentication Configuration for advanced settings on authentication activities going beyond default behavior.
 * Extended authentication behaviors include:
 * <ul>
 *     <li>Create new session upon logging on. The default behavior is to create a new session.</li>
 *     <li>Hard limit on session expiration. Overrides Servlet API inactivity-based session expiration
 *         with hard limit expiration (ignores inactivity)</li>
 *     <li>Configure the hard limit timeout expiration redirect URL</li>
 * </ul>
 * These settings are configurable via the portal's spring configuration
 * 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @since 2.1.2
 * @version $Id: $
 */
public interface PortalAuthenticationConfiguration
{   
    /**
     *  Determine if the hard session timeout limit setting {@link #getMaxSessionHardLimit()} is turned on or not
     *  in portal configuration. Hard limits override the Servlet API inactivity-based session expiration
     *  with hard limit expiration (ignores activity-based session invalidation)
     *
     *  This setting requires the {@link #isCreateNewSessionOnLogin()} setting being enabled.
     *
     * @return whether {@link #getMaxSessionHardLimit()} setting is enabled
     */
    public boolean isMaxSessionHardLimitEnabled();
    
    /**
     *  The max value in seconds for session invalidation. Hard limits expirations override the Servlet API inactivity-based
     *  session expiration with hard limit expiration (ignores activity-based session invalidation)
     *
     *  This setting requires the {@link #isCreateNewSessionOnLogin()} setting being enabled.
     *
     * @return the max session hard limit expiration value in seconds
     */
    public int getMaxSessionHardLimit();


    /**
     *  The max value in milliseconds for session invalidation. Hard limits expirations override the Servlet API inactivity-based
     *  session expiration with hard limit expiration (ignores activity-based session invalidation)
     *
     *  This setting requires the {@link #isCreateNewSessionOnLogin()} setting being enabled.
     *
     * @return the max session hard limit expiration value in milliseconds
     */
    public long getMsMaxSessionHardLimit();
    
    /**
     * A redirect URL location for hard session expiration,
     * must be used with Max Session Hard Limit turned on {@link #isMaxSessionHardLimitEnabled()}
     * This location is usually a logout-related URL
     * 
     * @return the configured portal-relative redirect URL location
     */
    public String getTimeoutRedirectLocation();

    /**
     * Override the configured portal-relative redirect URL location for hard session expiration,
     * must be used with Max Session Hard Limit turned on {@link #isMaxSessionHardLimitEnabled()}
     * This location is usually a logout-related URL
     *  
     * @param timeoutRedirectLocation the new timeout redirect URL
     */
    public void setTimeoutRedirectLocation(String timeoutRedirectLocation);

    /**
     * Retrieve portal configuration setting which determines whether to create new session upon authentication.
     * The default behavior is to create a new session.
     * 
     * @return the portal configuration setting determining whether to create new sessions on login
     */
    public boolean isCreateNewSessionOnLogin();
   
}

