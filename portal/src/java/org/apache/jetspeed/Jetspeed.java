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
package org.apache.jetspeed;

import java.util.Locale;

import javax.servlet.ServletConfig;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.engine.Engine;
import org.apache.jetspeed.engine.JetspeedEngine;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.request.RequestContext;

/**
 * Jetspeed environment
 * <br/>
 * Provides an easy way to access the current running environment 
 * of jetspeed.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class Jetspeed
{
    private static Engine engine = null;
    private static final Log log = LogFactory.getLog(Jetspeed.class);

    /**
     * Creates a Jetspeed Engine
     * 
     * @param configuration a configuration associated with this Jetspeed instance
     * @param applicationRoot the root of the servlet application
     * @param config the servlet configuration, this parameter can be null for unit tests or utilities
     * @return the newly created Engine
     * @throws JetspeedException
     */
    public static Engine createEngine(Configuration configuration, String applicationRoot, ServletConfig config)
        throws JetspeedException
    {
        try
        {
            log.info("Jetspeed environment attempting to initialize portal Engine...");
            engine = new JetspeedEngine();
            engine.init(configuration, applicationRoot, config);
            log.info("JetspeedEngine successfuly intialized.");
            log.info("Jetspeed environment successfuly intialized.");
            return engine;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            String msg = "Unable to create Engine";
            log.error(msg, e);
            throw new JetspeedException(msg, e);
        }
    }

    /**
     * Shuts down the currently running instance of the portal
     * Engine.
     * @throws JetspeedException
     */
    public static void shutdown() throws JetspeedException
    {
        engine.shutdown();
    }

    public static Engine getEngine()
    {
        return engine;
    }

    public static PortalContext getContext()
    {
        if (engine == null)
        {
            throw new NullPointerException("The engine is null, have you called createEgine() yet?");
        }
        return engine.getContext();
    }

    /**
     * Given a application relative path, returns the real path relative to the application root
     *
     */
    public static String getRealPath(String path)
    {
        return engine.getRealPath(path);
    }

    /**
     * Delegtes to the current Engine to retreive the RequestContext
     * appropriate for the current thread.
     * 
     * @see org.apache.jetspeed.engine.Engine#getCurrentRequestContext()
     * 
     * @return The RequestContext for this current Thread.
     */
    public static RequestContext getCurrentRequestContext()
    {
        return engine.getCurrentRequestContext();
    }

    // TODO We need to get this from the Engine and the engine should get it from the configuration. 

    public static Locale getDefaultLocale()
    {
        return Locale.getDefault();
    }

}