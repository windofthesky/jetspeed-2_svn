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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.CurrentWorkerContext;
import org.apache.jetspeed.aggregator.Worker;
import org.apache.jetspeed.userinfo.UserInfoManager;

/**
 * JetspeedRequestContextComponent
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedRequestContextComponent implements RequestContextComponent
{
    private String contextClassName = null;
    private Class contextClass = null;
    /** The user info manager. */
    private UserInfoManager userInfoMgr;
    private ThreadLocal tlRequestContext = new ThreadLocal();
    private Map requestContextObjects;
    
    private final static Log log = LogFactory.getLog(JetspeedRequestContextComponent.class);

    public JetspeedRequestContextComponent(String contextClassName)
    {
        this.contextClassName = contextClassName;
        this.requestContextObjects = new HashMap();
    }

    public JetspeedRequestContextComponent(String contextClassName, 
                                           UserInfoManager userInfoMgr)
    {
        this.contextClassName = contextClassName;
        this.userInfoMgr = userInfoMgr;
        this.requestContextObjects = new HashMap();        
    }

    public JetspeedRequestContextComponent(String contextClassName, 
            UserInfoManager userInfoMgr,
            Map requestContextObjects)
    {
        this.contextClassName = contextClassName;
        this.userInfoMgr = userInfoMgr;
        this.requestContextObjects = requestContextObjects;        
    }
    
    public RequestContext create(HttpServletRequest req, HttpServletResponse resp, ServletConfig config)
    {
        RequestContext context = null;

        try
        {
            if (null == contextClass)
            {
                contextClass = Class.forName(contextClassName);
            }

            Constructor constructor =
                contextClass.getConstructor(
                    new Class[] {
                        HttpServletRequest.class,
                        HttpServletResponse.class,
                        ServletConfig.class,
                        UserInfoManager.class,
                        Map.class});
            context = (RequestContext) constructor.newInstance(new Object[] { req, resp, config, userInfoMgr, requestContextObjects});
                    
        }
        catch (Exception e)
        {
            String msg = "JetspeedRequestContextComponent: Failed to create a Class object for RequestContext: " + e.toString();
            log.error(msg);
        }
        tlRequestContext.set(context);        
        return context;
    }

    public void release(RequestContext context)
    {
        tlRequestContext.set(null);
    }

    /**
     * The servlet request can always get you back to the Request Context if you need it
     * This static accessor provides this capability
     *
     * @param request
     * @return RequestContext
     */
    public RequestContext getRequestContext(HttpServletRequest request)
    {
        RequestContext rc = (RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        if(rc != null)
        {
            return rc;
        }
        else
        {
            log.error("Cannot call getRequestContext(HttpServletRequest request) before it has been created and set for this thread.");
            throw new IllegalStateException("Cannot call getRequestContext(HttpServletRequest request) before it has been created and set for this thread.");
        }
    }
    
    public RequestContext getRequestContext()
    {
        RequestContext rc = null;

        Thread ct = Thread.currentThread();
        if (ct instanceof Worker || CurrentWorkerContext.getCurrentWorkerContextUsed())
        {
            rc = (RequestContext) CurrentWorkerContext.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        }
        else
        {
            rc = (RequestContext) tlRequestContext.get();        
        }

        if(rc != null)
        {
            return rc;
        }
        else
        {
            log.error("Cannot call getRequestContext() before it has been created and set for this thread.");
            throw new IllegalStateException("Cannot call getRequestContext() before it has been created and set for this thread.");
        }
    }

}
