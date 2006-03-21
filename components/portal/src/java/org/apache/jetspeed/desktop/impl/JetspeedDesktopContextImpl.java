/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.desktop.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.desktop.JetspeedDesktopContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * Jetspeed Desktop 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedDesktopContextImpl implements JetspeedDesktopContext
{
    /** Jetspeed Request Context */
    RequestContext context;
    
    /** base portal URL to override default URL server info from servlet */
    private BasePortalURL baseUrlAccess = null;
        
    public JetspeedDesktopContextImpl(RequestContext context, BasePortalURL baseUrlAccess)
    {
        this.context = context;
        this.baseUrlAccess = baseUrlAccess;
    }
    
    public String getPortalResourceUrl(String relativePath)
    {        
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();
        
        // only rewrite a non-absolute url
        if (relativePath != null && relativePath.indexOf("://") == -1 && relativePath.indexOf("mailto:") == -1)            
        {
            StringBuffer path = new StringBuffer();
            if (this.baseUrlAccess == null)
            {
                return response.encodeURL(path.append(request.getScheme()).append("://").append(
                request.getServerName()).append(":").append(request.getServerPort()).append(
                request.getContextPath()).append(relativePath).toString());                
            }
            else
            {
                return response.encodeURL(path.append(baseUrlAccess.getServerScheme()).append("://").append(
                        baseUrlAccess.getServerName()).append(":").append(baseUrlAccess.getServerPort()).append(
                        request.getContextPath()).append(relativePath).toString());                                
            }
        }
        else
        {
            return relativePath;
        }
    }
    
    public String getPortalUrl(String relativePath)
    {        
        HttpServletRequest request = context.getRequest();
        HttpServletResponse response = context.getResponse();

        // only rewrite a non-absolute url
        if (relativePath != null && relativePath.indexOf("://") == -1 && relativePath.indexOf("mailto:") == -1)            
        {
            StringBuffer path = new StringBuffer();
            if (this.baseUrlAccess == null)
            {
                return response.encodeURL(path.append(request.getScheme()).append("://").append(
                request.getServerName()).append(":").append(request.getServerPort()).append(
                request.getContextPath()).append(request.getServletPath()).append(relativePath).toString());                
            }
            else
            {
                return response.encodeURL(path.append(baseUrlAccess.getServerScheme()).append("://").append(
                        baseUrlAccess.getServerName()).append(":").append(baseUrlAccess.getServerPort()).append(
                        request.getContextPath()).append(request.getServletPath()).append(relativePath).toString());                                
            }
        }
        else
        {
            return relativePath;
        }
    }
    
}
