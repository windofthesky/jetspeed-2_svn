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
 * PortalAdministration
 * 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @since 2.1.1
 * @version $Id: $
 */
public interface PortalAuthenticationConfiguration
{   
    /**
     * Is the session hard limit expiration feature enabled
     * @return
     */
    public boolean isMaxSessionHardLimitEnabled();
    
    /**
     * hard session timeout limit in seconds, regardless of (in)activity
     * 
     * @return
     */
    public int getMaxSessionHardLimit();
    
    
    /**
     * Get the session hard limit in milliseconds
     * 
     * @return session hard limit in milliseconds
     */
    public long getMsMaxSessionHardLimit();
    
    /**
     * redirect location for hard session expiration, must be used with Max Session Hard Limit turned on
     * 
     * @return
     */
    public String getTimeoutRedirectLocation();

    /**
     * redirect location for hard session expiration, must be used with Max Session Hard Limit turned on
     *  
     * @param timeoutRedirectLocation
     */
    public void setTimeoutRedirectLocation(String timeoutRedirectLocation);

    /**
     * Should we create new session upon authentication
     * 
     * @return
     */
    public boolean isCreateNewSessionOnLogin();
   
}

