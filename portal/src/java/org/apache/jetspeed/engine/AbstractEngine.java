/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.naming.NamingException;
import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4jFactory;
import org.apache.jetspeed.JetspeedPortalContext;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.jndi.JNDIComponent;
import org.apache.jetspeed.container.services.JetspeedContainerServices;
import org.apache.jetspeed.container.services.log.ContainerLogAdaptor;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.pipeline.Pipeline;
import org.apache.jetspeed.pipeline.descriptor.PipelineDescriptor;
import org.apache.jetspeed.pipeline.descriptor.XmlReader;
import org.apache.jetspeed.request.RequestContext;
import org.apache.log4j.PropertyConfigurator;
import org.apache.ojb.broker.util.ClassHelper;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.factory.Factory;
import org.apache.pluto.services.ContainerService;
import org.apache.pluto.services.factory.FactoryManagerService;
import org.apache.pluto.services.information.InformationProviderService;


/**
 * <p>
 * AbstractEngine
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public abstract class AbstractEngine implements Engine
{

    protected static final String JNDI_SUPPORT_FLAG_KEY = "portal.use.internal.jndi";
    private PortalContext context;
    private ServletConfig config = null;
    private Pipeline defaultPipeline = null;
    private Class pipelineClass = null;
    private HashMap pipelines = new HashMap();
    private ComponentManager componentManager = null;
        private static final Log log = LogFactory.getLog(PicoEngine.class);
    private static final Log console = LogFactory.getLog(CONSOLE_LOGGER);
    /** stores the most recent RequestContext on a per thread basis */
    private ThreadLocal tlRequestContext = new ThreadLocal();
    protected boolean useInternalJNDI;

    /**
     * Initializes the engine with a commons configuration, starting all early
     * initable services.
     * 
     * @param configuration
     *                  a commons <code>Configuration</code> set
     * @param applicationRoot
     *                  a <code>String</code> path to the application root for
     *                  resources
     * @param
     * @throws JetspeedException
     *                   when the engine fails to initilialize
     */
    public void init( Configuration configuration, String applicationRoot, ServletConfig config ) throws JetspeedException
    {
        try
        {
            this.context = new JetspeedPortalContext(this);
            this.config = config;
            context.setApplicationRoot(applicationRoot);
            context.setConfiguration(configuration);
            useInternalJNDI = configuration.getBoolean(JNDI_SUPPORT_FLAG_KEY,
                    true);
            
            configuration.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, applicationRoot);
            
            
            System.out.println("JNDI System Property flag "+System.getProperty(JNDI_SUPPORT_FLAG_KEY));
            if(System.getProperty(JNDI_SUPPORT_FLAG_KEY) ==  null)
            {
                 System.setProperty(JNDI_SUPPORT_FLAG_KEY, String
                    .valueOf(useInternalJNDI));
                 
            }
            else
            {
                // System property over rides the configurtaion                
                useInternalJNDI = Boolean.getBoolean(JNDI_SUPPORT_FLAG_KEY);
                log.warn("Internal JNDI has been flagged "+useInternalJNDI+" by the "+JNDI_SUPPORT_FLAG_KEY+" system  property.  This overrides the configuration setting of "+configuration.getBoolean(JNDI_SUPPORT_FLAG_KEY,
                        true));
            }
            
            //
            // Configure Log4J
            //
            String log4jFile = configuration.getString(LOG4J_CONFIG_FILE,
                    LOG4J_CONFIG_FILE_DEFAULT);
            log4jFile = getRealPath(log4jFile);
            Properties p = new Properties();
            p.load(new FileInputStream(log4jFile));
            p.setProperty(APPLICATION_ROOT_KEY, context.getApplicationRoot());
            PropertyConfigurator.configure(p);
            log.info("Configured log4j from " + log4jFile);
    
            // Set up Commons Logging to use the Log4J Logging
            System.getProperties().setProperty(LogFactory.class.getName(),
                    Log4jFactory.class.getName());
    
            //
            // bootstrap the initable services
            //
            componentManager = initComponents(configuration, config);
            log.info("Components initialization complete");
            initServices();
            log.info("Service initialization complete");
    
            // patch up OJB
            ClassLoader ploader2 = this.getClass().getClassLoader();
            ClassHelper.setClassLoader(ploader2);
            
            //
            // create the pipelines
            //
            log.info("Creating Jetspeed piplines...");
            createPipelines();
            log.info("Jetspeed piplines created sucessfully.");
            // 
            // Make sure JMX is init'd
            //
            // log.info("Jump starting JMX MBean services...");
            // JMX.startJMX();
            // log.info("JMX services sucessfully started.");
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            log.error(e.toString());
            throw new JetspeedException("Jetspeed Initialization exception!", e);
        }
    }

    /**
     * Get the servlet configuration if this engine is running under a servlet
     * container.
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
     * @param config
     *                  The servlet configuration.
     */
    public void initContainer( ServletConfig config ) throws PortletContainerException
    {
        try
        {
            PortletContainer container = (PortletContainer) componentManager
                    .getComponent(PortletContainer.class);
            JetspeedContainerServices environment = new JetspeedContainerServices();
            environment.addService(ContainerLogAdaptor.getService());
            environment.addServiceForClass(FactoryManagerService.class, this);
            environment.addServiceForClass(InformationProviderService.class,
                    (ContainerService)getComponentManager().getComponent(InformationProviderService.class));
            //TODO !!! Pluto has changed this siganture There is now a
            // container unique id string and Properties.
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
            PortletContainer container = (PortletContainer) componentManager
                    .getComponent(PortletContainer.class);
            if (container != null)
            {
                container.shutdown();
            }
    
            componentManager.stop();
        }
        catch (PortletContainerException e)
        {
            throw new JetspeedException(e);
        }
        System.gc();
    }

    public void service( RequestContext context ) throws JetspeedException
    {
        // requestContextPerThread.put(Thread.currentThread(), context);
        try
        {
            if (useInternalJNDI)
            {
                // bind the current JNDI context to this service thread.
                JNDIComponent jndi = (JNDIComponent) componentManager
                        .getComponent(JNDIComponent.class);
                if (jndi != null)
                {
                    jndi.bindToCurrentThread();
                }
            }
            String targetPipeline = context
                    .getRequestParameter(PortalReservedParameters.PIPELINE);
            if (null == targetPipeline)
            {                
                targetPipeline = (String)context.getAttribute(PortalReservedParameters.PIPELINE);                
            }
            tlRequestContext.set(context);
            Pipeline pipeline = defaultPipeline;
            if (targetPipeline != null)
            {
                Pipeline specificPipeline = (Pipeline) pipelines
                        .get(targetPipeline);
                if (specificPipeline != null)
                {
                    pipeline = specificPipeline;
                }
            }
            pipeline.invoke(context);
        }
        catch (Throwable t)
        {
            String msg = "JetspeedEngine unable to service request: "
                    + t.toString();
            log.error(msg, t);
            // throw new JetspeedException(msg, t);
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

    /**
     * Given a application relative path, returns the real path relative to the
     * application root
     *  
     */
    public String getRealPath( String path )
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
    
    /**
     * 
     * <p>
     * initComponents
     * </p>
     * Main responsibility of the subclassed implementation of this method
     * is to provide a <code>ComponentManager</code> implementation for the 
     * Engine.
     *
     * @param configuration Usually jetspeed.properties
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NamingException
     */
    protected abstract ComponentManager initComponents( Configuration configuration, ServletConfig servletConfig )
    throws IOException, ClassNotFoundException, NamingException;

    private void initServices() throws CPSInitializationException
    {
        // Get the instance of the service manager
        // ServiceManager serviceManager = JetspeedServices.getInstance();
        CommonPortletServices cps = CommonPortletServices.getInstance();
    
        // Set the service managers application root. In our
        // case it is the webapp context.
        cps.init(this.getContext().getConfiguration(), context
                .getApplicationRoot(), false);
        //serviceManager.setApplicationRoot(context.getApplicationRoot());
    
        //serviceManager.setConfiguration(this.getContext().getConfiguration());
    
        // Initialize the service manager. Services
        // that have its 'earlyInit' property set to
        // a value of 'true' will be started when
        // the service manager is initialized.
        //serviceManager.init();
    
    }

    /**
     * Creates the Jetspeed pipelines for request processing.
     * 
     * @throws CPSInitializationException
     */
    private void createPipelines() throws CPSInitializationException
    {
        String className = this.getContext().getConfiguration().getString(
                PIPELINE_CLASS, null);
        String defaultPipelineName = this.getContext().getConfiguration()
                .getString(PIPELINE_DEFAULT, "jetspeed-pipeline");
        if (null == className)
        {
            throw new CPSInitializationException(
                    "Failed to initialize pipeline, missing configuration entry: "
                            + PIPELINE_CLASS);
        }
        try
        {
            pipelineClass = Class.forName(className);
        }
        catch (Exception e)
        {
            throw new CPSInitializationException(
                    "Failed to initialize pipeline, couldnt create pipeline class");
        }
        String pipelinesDir = this.getContext().getConfiguration().getString(
                PIPELINE_DIRECTORY, "/WEB-INF/conf/pipelines/");
        File directory = new File(getRealPath(pipelinesDir));
        if (directory == null || !directory.exists())
        {
            throw new CPSInitializationException(
                    "Failed to initialize pipeline, could not find pipeline directory");
        }
        File[] pipelineDescriptors = directory.listFiles();
        for (int ix = 0; ix < pipelineDescriptors.length; ix++)
        {
            if (pipelineDescriptors[ix].isDirectory())
            {
                continue;
            }
            Pipeline pipeline = createPipeline(pipelineDescriptors[ix]);
            String name = pipelineDescriptors[ix].getName();
            int index = name.lastIndexOf(".");
            if (index > 0)
            {
                name = name.substring(0, index);
            }
            if (name.equalsIgnoreCase(defaultPipelineName))
            {
                defaultPipeline = pipeline;
            }
            pipelines.put(name, pipeline);
        }
    }

    /**
     * Creates a pipeline from a pipeline descriptor file.
     * 
     * @param file
     *                  the descriptor file describing the pipeline.
     * @return The new pipeline.
     * @throws CPSInitializationException
     */
    private Pipeline createPipeline( File file ) throws CPSInitializationException
    {
        Pipeline pipeline;
        PipelineDescriptor descriptor;
        try
        {
            System.out.println("Class loader is " + Thread.currentThread().getContextClassLoader().getClass().getName());
            pipeline = (Pipeline) pipelineClass.newInstance();
            XmlReader reader = new XmlReader(PipelineDescriptor.class);
            descriptor = (PipelineDescriptor) reader.parse(new FileInputStream(
                    file));
        }
        catch (Throwable e)
        {
            System.out.println("Failure *****************************");
            e.printStackTrace();
            throw new CPSInitializationException(
                    "Failed to read pipeline descriptor from deployment", e);
        }
        try
        {
            pipeline.setDescriptor(descriptor);
            pipeline.initialize();
        }
        catch (Exception e)
        {
            throw new CPSInitializationException(
                    "Failed to initialize pipeline: ", e);
        }
        return pipeline;
    }

    public Pipeline getPipeline( String pipelineName )
    {
        return (Pipeline) this.pipelines.get(pipelineName);
    }

    public Pipeline getPipeline()
    {
        return this.defaultPipeline;
    }

    /**
     * @see org.apache.jetspeed.engine.Engine#getCurrentRequestContext()
     */
    public RequestContext getCurrentRequestContext()
    {
        return (RequestContext) tlRequestContext.get();
    }

    public ComponentManager getComponentManager()
    {
        return this.componentManager;
    }
    /**
     * <p>
     * getFactory
     * </p>
     *
     * @see org.apache.pluto.services.factory.FactoryManagerService#getFactory(java.lang.Class)
     * @param theClass
     * @return
     */
    public Factory getFactory( Class theClass )
    {        
        return (Factory) getComponentManager().getComponent(theClass);
    }
}
