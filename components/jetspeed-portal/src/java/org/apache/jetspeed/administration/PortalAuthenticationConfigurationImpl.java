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

import org.apache.jetspeed.administration.PortalAuthenticationConfiguration;

/**
 * PasswordCredentialValve
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class PortalAuthenticationConfigurationImpl implements PortalAuthenticationConfiguration
{
    protected boolean createNewSessionOnLogin = false;
    protected int maxSessionHardLimit = 0;
    protected long msMaxSessionHardLimit = 1;
    protected String timeoutRedirectLocation = "";
    
    /**
     * Portal Authentication Configuration stored and accessed from this bean
     * 
     * @param createNewSessionOnLogin Should a new session be created upon logging on to the system 
     * @param maxSessionHardLimit The maximum session hard limit, ignores user activity, set to zero to turn off this feature
     * @param timeoutRedirectLocation Path to redirection upon logging out user on session limit experiation, only used with maxSessionHardLimit
     */
    public PortalAuthenticationConfigurationImpl(boolean createNewSessionOnLogin, int maxSessionHardLimit, String timeoutRedirectLocation)
    {
        this.createNewSessionOnLogin = createNewSessionOnLogin;
        this.maxSessionHardLimit = maxSessionHardLimit;
        this.timeoutRedirectLocation = timeoutRedirectLocation;
        this.msMaxSessionHardLimit = this.maxSessionHardLimit * 1000;
    }

    public boolean isMaxSessionHardLimitEnabled()
    {
        return this.maxSessionHardLimit > 0;
    }
    
    public int getMaxSessionHardLimit()
    {
        return maxSessionHardLimit;
    }

    
    public void setMaxSessionHardLimit(int maxSessionHardLimit)
    {
        this.maxSessionHardLimit = maxSessionHardLimit;
    }

    
    public long getMsMaxSessionHardLimit()
    {
        return msMaxSessionHardLimit;
    }

    
    public void setMsMaxSessionHardLimit(long msMaxSessionHardLimit)
    {
        this.msMaxSessionHardLimit = msMaxSessionHardLimit;
    }

    
    public String getTimeoutRedirectLocation()
    {
        return timeoutRedirectLocation;
    }

    
    public void setTimeoutRedirectLocation(String timeoutRedirectLocation)
    {
        this.timeoutRedirectLocation = timeoutRedirectLocation;
    }


    
    public boolean isCreateNewSessionOnLogin()
    {
        return createNewSessionOnLogin;
    }


    
    public void setCreateNewSessionOnLogin(boolean createNewSessionOnLogin)
    {
        this.createNewSessionOnLogin = createNewSessionOnLogin;
    }

}
