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

import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.engine.Engine;

/**
 * Portal Context associated with running thread of the engine
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: PortalContext.java 185962 2004-03-08 01:03:33Z jford $
 */
public interface PortalContext extends javax.portlet.PortalContext
{
    public Engine getEngine();

    public Configuration getConfiguration();
    public void setConfiguration(Configuration configuration);

    public String getConfigurationProperty(String key);
    public String getConfigurationProperty(String key, String defaultValue);

    public void setAttribute(String name, Object value);
    public Object getAttribute(String name);

    /**
     * Returns the application root for this Jetspeed engine context.
     *
     * @return a <code>String</code> containing the application root path for this Jetspeed context.
     */
    public String getApplicationRoot();

    /**
     * Sets the application root path for this Jetspeed engine context.
     *
     * @param applicationRoot - the applicationRoot path on the file system.
     */
    public void setApplicationRoot(String applicationRoot);

}
