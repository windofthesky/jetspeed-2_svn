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

import java.util.Properties;
import java.io.FileInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4jFactory;
import org.apache.log4j.PropertyConfigurator;
import org.apache.commons.configuration.Configuration;
import org.apache.fulcrum.ServiceManager;
import org.apache.fulcrum.BaseServiceBroker;

/**
 * This is a singleton utility class that acts as a Services broker.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class CommonPortletServices
    extends BaseServiceBroker
    implements ServiceManager, CPSConstants
{
    /** The single instance of this class. */
    private static CommonPortletServices instance = new CommonPortletServices();

    private static final Log log = LogFactory.getLog(CommonPortletServices.class);

    /**
     * This constructor is protected to force clients to use
     * getInstance() to access this class.
     */
    protected CommonPortletServices()
    {
        super();
    }

    /**
     * The method through which this class is accessed as a broker.
     *
     * @return The single instance of this class.
     */
    public static CommonPortletServices getInstance()
    {
        return instance;
    }

    public void init(Configuration configuration, String applicationRoot)
       throws CPSInitializationException
    {
        try
        {                
            //
            // bootstrap the initable services
            //
            this.setApplicationRoot(applicationRoot);
            this.setConfiguration(configuration);

            String log4jFile = configuration.getString(LOG4J_CONFIG_FILE,
                                                       LOG4J_CONFIG_FILE_DEFAULT);
            log4jFile = this.getRealPath(log4jFile);
            Properties p = new Properties();
            p.load(new FileInputStream(log4jFile));
            p.setProperty(APPLICATION_ROOT_KEY, applicationRoot);
            PropertyConfigurator.configure(p);
            log.info("Configured log4j from " + log4jFile);

            // Set up Commons Logging to use the Log4J Logging
            System.getProperties().setProperty(LogFactory.class.getName(),
                                               Log4jFactory.class.getName());


            this.init();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            log.error(e.toString());
            throw new CPSInitializationException("CPS Initialization exception!", e);
        }

    }

    public void shutdown()
       throws CPSException
    {
        getInstance().shutdownServices();
        System.gc();
    }

    /**
     * @param String name of the service.  For Fulcrum
     * services this is easily accessible from the Service's interface
     * via <code>SERVICE_NAME</code> field.
     * @return Object a service
     */
    public static CommonService getPortalService(String name)
    {
        Object service = getInstance().getService(name);
        if (service instanceof CommonService)
        {
            return (CommonService)service;
        }
        return null;
    }

}