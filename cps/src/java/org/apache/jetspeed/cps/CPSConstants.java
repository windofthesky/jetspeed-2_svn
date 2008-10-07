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
package org.apache.jetspeed.cps;

/**
 * This interface contains all the constants for the CPS configuration.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface CPSConstants
{
    /**
     * <p>The prefix used to denote the namespace reserved for and
     * used by Jetspeed-specific configuration parameters (such as
     * those passed in via servlet container's config file
     * (<code>server.xml</code>), or the web app deployment descriptor
     * (<code>web.xml</code>).</p>
     *
     * <p>For example, a parameter in the Jetspeed namespace would be
     * <code>org.apache.jetspeed.loggingRoot</code>.</p>
     */
    public static final String CONFIG_NAMESPACE = "org.apache.jetspeed.cps";

    /** The key for the Log4J File */
    public static final String LOG4J_CONFIG_FILE = "log4j.file";

    /** The default value for the Log4J File */
    public static final String LOG4J_CONFIG_FILE_DEFAULT = "/WEB-INF/conf/cps-logging.properties";

    /** This is the default log file to be used for logging */
    public static final String DEFAULT_LOGGER = "cps";
    public static final String CONSOLE_LOGGER = "console";
    
    /** Default Value for the Logging Directory, relative to the webroot */
    public static final String LOGGING_ROOT_DEFAULT = "/logs";
    public static final String LOGGING_ROOT = "loggingRoot";

    public static final String CPS_PROPERTIES_KEY = "properties";
    public static final String CPS_PROPERTIES_DEFAULT = "/WEB-INF/conf/cps.properties";
    public static final String CPS_CONFIGURATION_KEY = "configuration";
    public static final String CPS_CONFIGURATION_DEFAULT = "/WEB-INF/conf/cps.xml";

    /** If this value is set as applicationRoot, then the webContext is used
     * as application root
     */
    public static final String WEB_CONTEXT = "webContext";

    /** Key for the Path to the cps.properties File */ 
    public static final String APPLICATION_ROOT_KEY = "applicationRoot";

    /** Default Value for the Path to the TurbineResources.properties File */ 
    public static final String APPLICATION_ROOT_DEFAULT = WEB_CONTEXT;
   

}