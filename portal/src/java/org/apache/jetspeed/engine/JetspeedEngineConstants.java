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
    public static final String JETSPEED_CONFIGURATION_KEY = "configuration";
    public static final String JETSPEED_CONFIGURATION_DEFAULT = "/WEB-INF/conf/jetspeed.xml";

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
    public static final String PIPELINE_DESCRIPTOR = "pipeline.descriptor";
 
    /**
     * This specifies the factory to use the Jetspeed java.util.prefs.Preferences
     * implementation.
     */
    public static final String PREFERENCES_FACTORY = "preferences.factory";
    public static final String PREFERENCES_FACTORY_DEFAULT =
        "org.apache.jetspeed.spi.services.prefs.impl.PreferencesFactoryImpl";

}
