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
package org.apache.jetspeed.webapp.logging.velocity;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

/**
 *  Implementation of a LogSystem using Commons Logging to route Velocity message
 *  through a IsolatedLog4JLogger setup.
 * <p>
 * Configure the following in your velocity.properties:
 * <ul>
 *   <li>runtime.log.logsystem.class=org.apache.jetspeed.webapp.logging.velocity.CommonsLoggingLog4JLogSystem</li>
 *   <li>runtime.log.logsystem.log4j.category=&lt;a Log4J Category name to capture Velocity message, default value: "velocity"&gt;</li>
 * </ul>
 * For further information about setting up and configuring velocity:
 * <a href="http://jakarta.apache.org/velocity/docs/developer-guide.html">Velocity - Developer's Guide</a>
 * </p>
 * <p>
 * If you want to use a VelocityEngine instantiated by Spring using its org.springframework.ui.velocity.VelocityEngineFactoryBean
 * then you can also configure the above properties inline in its defintion or point it to your velocity.properties file.<br/>
 * But, beware of the following: the VelocityEngineFactoryBean by default overrides logging any configuration and hooks up their own
 * CommonsLoggingLogSystem. Which works fine just as this one, but uses as (hard coded) logging category the VelocityEngine class name.
 * So, if you do want to route your Velocity logging using your own category (or our default "velocity"), then you need to override the
 * VelocityEngineFactoryBean default logging setup by setting its "overrideLogging" property to false.
 * </p>
 * <p>
 * </p>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class CommonsLoggingLog4JLogSystem implements LogSystem
{
    public static final String DEFAULT_CATEGORY = "velocity";
    
    private Log logger;
    
    /* (non-Javadoc)
     * @see org.apache.velocity.runtime.log.LogSystem#init(org.apache.velocity.runtime.RuntimeServices)
     */
    public void init(RuntimeServices rs) throws Exception
    {
        String categoryname =  (String) rs.getProperty("runtime.log.logsystem.log4j.category");

        if ( categoryname == null )
        {
            categoryname = DEFAULT_CATEGORY;
        }
        logger = LogFactory.getLog(categoryname);

        logVelocityMessage( DEBUG_ID, "CommonsLoggingLog4JLogSystem using category '" + categoryname + "'");
    }

    /* (non-Javadoc)
     * @see org.apache.velocity.runtime.log.LogSystem#logVelocityMessage(int, java.lang.String)
     */
    public void logVelocityMessage(int level, String message)
    {
        switch (level) 
        {
            case LogSystem.WARN_ID:
                logger.warn( message );
                break;
            case LogSystem.INFO_ID:
                logger.info(message);
                break;
            case LogSystem.DEBUG_ID:
                logger.debug(message);
                break;
            case LogSystem.ERROR_ID:
                logger.error(message);
                break;
            default:
                logger.debug(message);
                break;
        }
    }
}
