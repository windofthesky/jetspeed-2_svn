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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4jFactory;
import org.apache.jetspeed.JetspeedPortalContext;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.container.PortletContainerFactory;
import org.apache.jetspeed.container.services.JetspeedContainerServices;
import org.apache.jetspeed.container.services.log.ContainerLogAdaptor;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.descriptor.PipelineDescriptor;
import org.apache.jetspeed.descriptor.XmlReader;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.pipeline.Pipeline;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.factory.FactoryManager;
import org.apache.jetspeed.services.information.InformationProviderManager;
import org.apache.jetspeed.services.information.InformationProviderServiceService;
import org.apache.jetspeed.services.jmx.JMX;
import org.apache.log4j.PropertyConfigurator;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.services.information.InformationProviderService;

/**
 * Jetspeed Engine implementation
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedEngine implements Engine
{
    private PortalContext context;
    private ServletConfig config = null;
    private Pipeline pipeline = null;

    private static final Log log = LogFactory.getLog(JetspeedEngine.class);
    private static final Log console = LogFactory.getLog(CONSOLE_LOGGER);
    /** stores the most recent RequestContext on a per thread basis */
    private ThreadLocal tlRequestContext = new ThreadLocal();
    // private final HashMap requestContextPerThread = new HashMap();

    /**
     * Initializes the engine with a commons configuration, starting all early initable services.
     *
     * @param configuration a commons <code>Configuration</code> set
     * @param applicationRoot a <code>String</code> path to the application root for resources
     * @param 
     * @throws JetspeedException when the engine fails to initilialize
     */
    public void init(Configuration configuration, String applicationRoot, ServletConfig config) throws JetspeedException
    {
        try
        {
            this.context = new JetspeedPortalContext(this);
            this.config = config;

            context.setApplicationRoot(applicationRoot);
            context.setConfiguration(configuration);

            //
            // Configure Log4J
            //
            String log4jFile = configuration.getString(LOG4J_CONFIG_FILE, LOG4J_CONFIG_FILE_DEFAULT);
            log4jFile = getRealPath(log4jFile);

            Properties p = new Properties();
            p.load(new FileInputStream(log4jFile));
            p.setProperty(APPLICATION_ROOT_KEY, context.getApplicationRoot());
            PropertyConfigurator.configure(p);
            log.info("Configured log4j from " + log4jFile);

            // Set up Commons Logging to use the Log4J Logging
            System.getProperties().setProperty(LogFactory.class.getName(), Log4jFactory.class.getName());

            // Configure java.util.prefs.PreferencesFactory
            String preferencesFactory = configuration.getString(PREFERENCES_FACTORY, PREFERENCES_FACTORY_DEFAULT);
            if ((null != preferencesFactory) && (!preferencesFactory.equals("")))
            {
                System.setProperty("java.util.prefs.PreferencesFactory", preferencesFactory);
                log.info("Configured java.util.prefs.PreferencesFactory from " + log4jFile);
            }

            //
            // bootstrap the initable services
            //
            initServices();
            log.info("Service initialization complete");

            // 
            // create the container
            //
            log.info("Creating portlet container...");
            console.info("Creating portlet container...");
            PortletContainer container = PortletContainerFactory.getPortletContainer();
            log.info("Portlet container created sucessfully usin container class: " + container.getClass().getName());

            //
            // create the pipeline
            //
            log.info("Creating Jetspeed pipline...");
            createPipeline();
            log.info("Jetspeed pipline created sucessfully.");
            // 
            // Make sure JMX is init'd
            //
            log.info("Jump starting JMX MBean services...");
            JMX.startJMX();
            log.info("JMX services sucessfully started.");
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            log.error(e.toString());
            throw new JetspeedException("Jetspeed Initialization exception!", e);
        }

    }

    /**
     * Get the servlet configuration if this engine is running under a servlet container.
     * 
     * @return config The servlet configuration
     */
    public ServletConfig getServletConfig()
    {
        return this.config;
    }

    /**
     * Initializes the portlet container given a servlet configuration.
     * 
     * @param config The servlet configuration.
     */
    public void initContainer(ServletConfig config) throws PortletContainerException
    {
        try
        {
            PortletContainer container = PortletContainerFactory.getPortletContainer();

            JetspeedContainerServices environment = new JetspeedContainerServices();
            environment.addService(ContainerLogAdaptor.getService());
            environment.addService(FactoryManager.getService());
            InformationProviderServiceService ips = InformationProviderManager.getService();
            ips.init(config, null);
            environment.addServiceForClass(InformationProviderService.class, ips);
            //TODO !!! Pluto has changed this siganture There is now a container unique id string and Properties. 
            // WE need to figure what these are really for.
            container.init("jetspeed", config, environment, new Properties());
        }
        catch (Throwable e)
        {
            console.error("Unable to initalize Engine.", e);
            log.error("Unable to initalize Engine.", e);
            if (e instanceof PortletContainerException)
            {
                throw (PortletContainerException) e;
            }
            else
            {
                throw new PortletContainerException(e);
            }
        }

    }

    public void shutdown() throws JetspeedException
    {
        CommonPortletServices.getInstance().shutdownServices();
        try
        {
            PortletContainer container = PortletContainerFactory.getPortletContainer();
            container.shutdown();
        }
        catch (PortletContainerException e)
        {
            throw new JetspeedException(e);
        }
        System.gc();
    }

    public void service(RequestContext context) throws JetspeedException
    {
       // requestContextPerThread.put(Thread.currentThread(), context);
       try
       {
           tlRequestContext.set(context);
           pipeline.invoke(context);
       }
       catch (Throwable t)
       {
           t.printStackTrace();
       }
    }

    /**
      * Returns the context associated with this engine.
      *
      * @return an <code>EngineContext</code> associated with this engine
      */
    public PortalContext getContext()
    {
        return this.context;
    }

    // ------------------------------------------------------------------------
    // H E L P E R  F U N C T I O N S
    // ------------------------------------------------------------------------

    /**
     * Given a application relative path, returns the real path relative to the application root
     *
     */
    public String getRealPath(String path)
    {
        String result = "";
        String base = context.getApplicationRoot();
        if (base.endsWith(java.io.File.separator))
        {
            if (path.startsWith("/"))
            {
                result = base.concat(path.substring(1));
                return result;
            }
        }
        else
        {
            if (!path.startsWith("/"))
            {
                result = base.concat("/").concat(path);
                return result;
            }
        }
        return base.concat(path);

    }

    private void initServices() throws CPSInitializationException
    {
        // Get the instance of the service manager        
        // ServiceManager serviceManager = JetspeedServices.getInstance();
        CommonPortletServices cps = CommonPortletServices.getInstance();

        // Set the service managers application root. In our
        // case it is the webapp context.
        cps.init(this.getContext().getConfiguration(), context.getApplicationRoot(), false);
        //serviceManager.setApplicationRoot(context.getApplicationRoot());

        //serviceManager.setConfiguration(this.getContext().getConfiguration());

        // Initialize the service manager. Services
        // that have its 'earlyInit' property set to
        // a value of 'true' will be started when
        // the service manager is initialized.
        //serviceManager.init();

    }

    private void createPipeline() throws CPSInitializationException
    {
        String clazz = this.getContext().getConfiguration().getString(PIPELINE_CLASS, null);
        String descriptorName = this.getContext().getConfiguration().getString(PIPELINE_DESCRIPTOR, null);

        if (null == clazz || null == descriptorName)
        {
            throw new CPSInitializationException("Failed to initialize pipeline, missing params");
        }

        Pipeline pipeline;
        PipelineDescriptor descriptor;

        try
        {
            pipeline = (Pipeline) Class.forName(clazz).newInstance();
        }
        catch (Exception e)
        {
            throw new CPSInitializationException("Failed to initialize pipeline, couldnt create pipeline class");
        }
        try
        {
            XmlReader reader = new XmlReader(PipelineDescriptor.class);
            File file = new File(getRealPath(descriptorName));
            descriptor = (PipelineDescriptor) reader.parse(new FileInputStream(file));
        }
        catch (Exception e)
        {
            throw new CPSInitializationException("Failed to read pipeline descriptor from deployment");
        }
        pipeline.setDescriptor(descriptor);
        try
        {
            pipeline.initialize();
        }
        catch (Exception e)
        {
            throw new CPSInitializationException("Failed to initialize pipeline: ", e);
        }

        this.pipeline = pipeline;
    }

    public Pipeline getPipeline()
    {
        return this.pipeline;
    }
    /**
     * @see org.apache.jetspeed.engine.Engine#getCurrentRequestContext()
     */
    public RequestContext getCurrentRequestContext()
    {
        return (RequestContext) tlRequestContext.get();
    }

}
