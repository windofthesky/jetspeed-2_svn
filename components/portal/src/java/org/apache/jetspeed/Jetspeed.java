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
package org.apache.jetspeed;

import java.util.Locale;

import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.engine.Engine;
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
        if (engine == null)
        {
            return null;
        }
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

    public static ComponentManager getComponentManager()
    {
        if (engine == null)
            return null;
        return engine.getComponentManager();
    }

    public static void setEngine(Engine engine)
    {
        Jetspeed.engine = engine;
    }
    
}