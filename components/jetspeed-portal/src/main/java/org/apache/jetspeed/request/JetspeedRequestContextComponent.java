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
package org.apache.jetspeed.request;

import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.userinfo.UserInfoManager;

/**
 * JetspeedRequestContextComponent
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedRequestContextComponent implements RequestContextComponent
{
    private PortletRegistry registry;
    private UserInfoManager userInfoMgr;
    private Map<String, Object> requestContextObjects;
    private ThreadLocal<RequestContext> tlRequestContext = new ThreadLocal<RequestContext>();
    
    private final static Logger log = LoggerFactory.getLogger(JetspeedRequestContextComponent.class);


    public JetspeedRequestContextComponent(PortletRegistry registry, UserInfoManager userInfoMgr, Map<String, Object> requestContextObjects)
    {
        this.registry = registry;
        this.userInfoMgr = userInfoMgr;
        this.requestContextObjects = requestContextObjects;        
    }
    
    public RequestContext create(HttpServletRequest request, HttpServletResponse response, ServletConfig config)
    {
        JetspeedRequestContext context = null;

        try
        {
            context = new JetspeedRequestContext(this, request, response, config, requestContextObjects);
                    
        }
        catch (Exception e)
        {
            String msg = "JetspeedRequestContextComponent: Failed to create a Class object for RequestContext: " + e.toString();
            log.error(msg);
        }
        setRequestContext(context);
        return context;
    }
    
    public void setRequestContext(RequestContext context)
    {
        tlRequestContext.set(context);
    }

    public RequestContext getRequestContext()
    {
        return tlRequestContext.get();
    }
    
    public PortletRegistry getPortletRegistry()
    {
        return registry;
    }

    public UserInfoManager getUserInfoManager()
    {
        return this.userInfoMgr;
    }
}
