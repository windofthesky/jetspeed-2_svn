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
package org.apache.jetspeed.request;

import java.lang.reflect.Constructor;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    private final static Log log = LogFactory.getLog(JetspeedRequestContextComponent.class);

    public JetspeedRequestContextComponent(String contextClassName)
    {
    }

    public JetspeedRequestContextComponent(String contextClassName, 
                                           UserInfoManager userInfoMgr)
    {
        this.contextClassName = contextClassName;
        this.userInfoMgr = userInfoMgr;
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

            // TODO: we could use a pooled object implementation here
            Constructor constructor =
                contextClass.getConstructor(
                    new Class[] {
                        HttpServletRequest.class,
                        HttpServletResponse.class,
                        ServletConfig.class,
                        UserInfoManager.class });
            context = (RequestContext) constructor.newInstance(new Object[] { req, resp, config, userInfoMgr });

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
        return (RequestContext) request.getAttribute(RequestContext.REQUEST_PORTALENV);
    }
    
    public RequestContext getRequestContext()
    {
        return (RequestContext) tlRequestContext.get();        
    }

}
