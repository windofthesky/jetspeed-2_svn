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