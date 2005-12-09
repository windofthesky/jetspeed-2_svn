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
package org.apache.jetspeed.engine;

/**
 * This interface contains all the constants for the engine.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface JetspeedEngineConstants
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
    public static final String CONFIG_NAMESPACE = "org.apache.jetspeed";

    /** The key for the Log4J File */
    public static final String LOG4J_CONFIG_FILE = "log4j.file";

    /** The default value for the Log4J File */
    public static final String LOG4J_CONFIG_FILE_DEFAULT = "/WEB-INF/conf/Log4j.properties";

    /** This is the default log file to be used for logging */
    public static final String DEFAULT_LOGGER = "jetspeed";
    public static final String CONSOLE_LOGGER = "console";

    /**
     * The logging facility which captures output from the SchedulerService.
     */
    public static final String SCHEDULER_LOG_FACILITY = "scheduler";

    /**
     * SMTP server uses to send mail.
     */
    public static final String MAIL_SERVER_KEY = "mail.server";


    /** Default Value for the Logging Directory, relative to the webroot */
    public static final String LOGGING_ROOT_DEFAULT = "/logs";
    public static final String LOGGING_ROOT = "loggingRoot";

    public static final String JETSPEED_PROPERTIES_KEY = "properties";
    public static final String JETSPEED_PROPERTIES_DEFAULT = "/WEB-INF/conf/jetspeed.properties";

    /** If this value is set as applicationRoot, then the webContext is used
     * as application root
     */
    public static final String WEB_CONTEXT = "webContext";

    /** Key for the Path to the Resources.properties File */
    public static final String APPLICATION_ROOT_KEY = "applicationRoot";

    /** Default Value for the Path to the Resources.properties File */
    public static final String APPLICATION_ROOT_DEFAULT = WEB_CONTEXT;

    /** This is the key used in the jetspeed.properties to access resources
     * relative to the Web Application root. It might differ from the
     * Application root, but the normal case is, that the webapp root
     * and the application root point to the same path.
     */
    public static final String WEBAPP_ROOT_KEY = "webappRoot";

    public static final String PIPELINE_CLASS = "pipeline.class";
    public static final String PIPELINE_DEFAULT = "pipeline.default";
    public static final String PIPELINE_DIRECTORY = "pipeline.directory";

    /**
     * This specifies the factory to use the Jetspeed java.util.prefs.Preferences
     * implementation.
     */
    public static final String PREFERENCES_FACTORY = "preferences.factory";
    public static final String PREFERENCES_FACTORY_DEFAULT =
        "org.apache.jetspeed.spi.services.prefs.impl.PreferencesFactoryImpl";

}
