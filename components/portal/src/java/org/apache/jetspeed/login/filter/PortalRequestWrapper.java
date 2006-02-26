/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.login.filter;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class PortalRequestWrapper extends HttpServletRequestWrapper
{
    private Principal userPrincipal = null;

    public PortalRequestWrapper(HttpServletRequest request,
            Principal userPrincipal)
    {
        super(request);
        this.userPrincipal = userPrincipal;
    }

    public void setUserPrincipal(Principal userPrincipal)
    {
        this.userPrincipal = userPrincipal;
    }

    public Principal getUserPrincipal()
    {
        return userPrincipal;
    }

    /**
     * Return the name of the remote user that has been authenticated
     * for this Request.
     */
    public String getRemoteUser()
    {
        if (userPrincipal != null)
        {
            return userPrincipal.getName();
        }
        else
        {
            return null;
        }
    }

}
