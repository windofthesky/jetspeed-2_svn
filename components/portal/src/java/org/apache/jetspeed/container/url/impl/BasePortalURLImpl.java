/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.container.url.impl;

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.container.url.BasePortalURL;

/**
 * <p>
 * BasePortalURL defines the interface for manipulating Base URLs in a portal.
 * Base URLs contain the isSecure flag, server name, server port, and server scheme.
 * This abstraction was necessary for wiring the entire portal's base URL via another
 * mechanism than retrieving from the servlet request.
 * </p>
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: $
 *
 */
public class BasePortalURLImpl implements BasePortalURL
{
    private String serverName;    
    private String serverScheme;
    private int serverPort;    
    private boolean secure;
    
    public BasePortalURLImpl()
    {        
    }
    
    public BasePortalURLImpl(Configuration config)
    {
        this.serverName = config.getString("portal.url.name");
        this.serverScheme = config.getString("portal.url.scheme");
        this.serverPort = config.getInt("portal.url.port");
        this.secure = config.getBoolean("portal.url.secure");
    }
    
    public BasePortalURLImpl(String serverScheme, String serverName, int serverPort, boolean secure)
    {
        this.serverName = serverName;
        this.serverScheme = serverScheme;
        this.serverPort = serverPort;
        this.secure = secure;
    }
    
    public boolean isSecure()
    {
        return secure;
    }
    
    public void setSecure(boolean secure)
    {
        this.secure = secure;
    }
    
    public String getServerName()
    {
        return serverName;
    }
    
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }
    
    public int getServerPort()
    {
        return serverPort;
    }
    
    public void setServerPort(int serverPort)
    {
        this.serverPort = serverPort;
    }
    
    public String getServerScheme()
    {
        return serverScheme;
    }
    
    public void setServerScheme(String serverScheme)
    {
        this.serverScheme = serverScheme;
    }

}
