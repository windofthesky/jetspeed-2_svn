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
package org.apache.jetspeed.container;

/**
 * This interface contains Container constants
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface ServletContainerConstants
{    

    public static final String JETSPEED_PROPERTIES_KEY = "properties";
    public static final String JETSPEED_PROPERTIES_DEFAULT = "/WEB-INF/conf/jetspeed.properties";
    public static final String JETSPEED_CONFIGURATION_KEY = "configuration";
    public static final String JETSPEED_CONFIGURATION_DEFAULT = "/WEB-INF/conf/jetspeed.xml";

    /** If this value is set as applicationRoot, then the webContext is used
     * as application root
     */
    public static final String WEB_CONTEXT = "webContext";

    /** Key for the Path to the TurbineResources.properties File */ 
    public static final String APPLICATION_ROOT_KEY = "applicationRoot";

    /** Default Value for the Path to the TurbineResources.properties File */ 
    public static final String APPLICATION_ROOT_DEFAULT = WEB_CONTEXT;
  
    /** This is the key used in the Turbine.properties to access resources 
     * relative to the Web Application root. It might differ from the 
     * Application root, but the normal case is, that the webapp root
     * and the application root point to the same path.
     */
    public static final String WEBAPP_ROOT_KEY = "webappRoot";

    public static final String PIPELINE_CLASS = "pipeline.class";
    public static final String PIPELINE_DESCRIPTOR = "pipeline.descriptor";

    /** This is the default log file to be used for logging */
    public static final String DEFAULT_LOGGER = "jetspeed";
    public static final String CONSOLE_LOGGER = "console";
 
}