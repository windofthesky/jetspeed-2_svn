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
package org.apache.jetspeed.request;

import java.lang.reflect.Constructor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.exception.JetspeedException;

/**
 * Jetspeed Request Context factory.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class RequestContextFactory
{
    private static String contextClassName = null;
    private static Class contextClass = null;

    private final static Log log = LogFactory.getLog(RequestContextFactory.class);

    public final static String REQUEST_CONTEXT_CLASS = "factory.request.context";

    /**
     * Factory method to create Request Context instances  
     *
     * @throws JetspeedException if failed to create context
     * @return RequestContext a new created context
     */
    public static RequestContext getInstance(HttpServletRequest req, HttpServletResponse resp, ServletConfig config)
        throws JetspeedException
    {
          
        RequestContext context = null;
        PortalContext pc = null;

        try
        {
            if (null == contextClass)
            {

                pc = Jetspeed.getContext();
                contextClassName = pc.getConfigurationProperty(REQUEST_CONTEXT_CLASS);
                contextClass = Class.forName(contextClassName);
            }

            // TODO: we could use a pooled object implementation here
            Constructor constructor = contextClass.getConstructor(new Class[] 
                          {PortalContext.class, HttpServletRequest.class, HttpServletResponse.class, ServletConfig.class});
            context = (RequestContext) constructor.newInstance(new Object[] {pc, req, resp, config});

        }
        catch(Exception e)
        {
            String msg = "RequestContextFactory: Failed to create a Class object for RequestContext: " + e.toString();
            log.error(msg);
            throw new JetspeedException(msg, e);
        }

        return context;
    } 
}