/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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