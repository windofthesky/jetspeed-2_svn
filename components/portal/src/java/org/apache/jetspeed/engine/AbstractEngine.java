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

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;

import javax.naming.NamingException;
import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedPortalContext;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.jndi.JNDIComponent;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.pipeline.Pipeline;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.util.IsolatedLog4JLogger;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.RootCategory;
import org.apache.ojb.broker.util.ClassHelper;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.factory.Factory;
import org.apache.pluto.services.ContainerService;
import org.apache.pluto.services.factory.FactoryManagerService;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;


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

    public static final String JNDI_SUPPORT_FLAG_KEY = "portal.use.internal.jndi";
    private PortalContext context;
    private ServletConfig config = null;
    private ComponentManager componentManager = null;
    protected static final Log log = LogFactory.getLog(AbstractEngine.class);
    private static final Log console = LogFactory.getLog(CONSOLE_LOGGER);
    /** stores the most recent RequestContext on a per thread basis */
    protected boolean useInternalJNDI;
    protected String defaultPipelineName;

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
        DateFormat format = DateFormat.getInstance();
        Date startTime = new Date();
        
        
        try
        {
            this.context = new JetspeedPortalContext(this);
            this.config = config;
            context.setApplicationRoot(applicationRoot);
            context.setConfiguration(configuration);
            useInternalJNDI = configuration.getBoolean(JNDI_SUPPORT_FLAG_KEY,
                    true);
            defaultPipelineName = configuration.getString(PIPELINE_DEFAULT, "jetspeed-pipeline");
            configuration.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, applicationRoot);
            
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
            Hierarchy h = new Hierarchy(new RootCategory(Level.INFO));
            new PropertyConfigurator().doConfigure(p,h);
            IsolatedLog4JLogger.setHierarchy(h);
            
            log.info("Configured log4j from " + log4jFile);
            log.info("Starting Jetspeed Engine ("+getClass().getName()+") at "+format.format(startTime));
    
            // patch up OJB
            ClassLoader ploader2 = this.getClass().getClassLoader();
            //ClassLoader ploader2 = Thread.currentThread().getContextClassLoader();
            ClassHelper.setClassLoader(ploader2);
            
            //
            // bootstrap the initable services
            //
            componentManager = initComponents(configuration, config);
            log.info("Components initialization complete");
                
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            log.error(e.toString());
            throw new JetspeedException("Jetspeed Initialization exception!", e);
        }
        finally
        {            
            Date endTime = new Date();
            long elapsedTime = (endTime.getTime() - startTime.getTime()) / 1000;
            log.info("Finished starting Jetspeed Engine ("+getClass().getName()+") at "+format.format(endTime) 
                         +".  Elapsed time: "+elapsedTime+" seconds.");
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

            container.init("jetspeed", config, this, new Properties());
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


            if (useInternalJNDI)
            {
                // bind the current JNDI context to this service thread.
                JNDIComponent jndi = (JNDIComponent) componentManager
                        .getComponent(JNDIComponent.class);
                if (jndi != null)
                {
                    try
                    {
                        jndi.bindToCurrentThread();
                    }
                    catch (NamingException e)
                    {
                        throw new JetspeedException("Unable bind jndi: "+e.toString(), e);
                    }
                }
            }
            String targetPipeline = context
                    .getRequestParameter(PortalReservedParameters.PIPELINE);
            if (null == targetPipeline)
            {                
                targetPipeline = (String)context.getAttribute(PortalReservedParameters.PIPELINE);                
            }
            // tlRequestContext.set(context);
            Pipeline pipeline = getPipeline();
            if (targetPipeline != null)
            {
                Pipeline specificPipeline = getPipeline(targetPipeline);
                if (specificPipeline != null)
                {
                    pipeline = specificPipeline;
                }
            }
            pipeline.invoke(context);
   
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

    public Pipeline getPipeline( String pipelineName )
    {
        return (Pipeline) componentManager.getComponent(pipelineName);
    }

    public Pipeline getPipeline()
    {
        return getPipeline(defaultPipelineName);
    }

    /**
     * @see org.apache.jetspeed.engine.Engine#getCurrentRequestContext()
     */
    public RequestContext getCurrentRequestContext()
    {
        RequestContextComponent contextComponent = (RequestContextComponent) getComponentManager()
            .getComponent(RequestContextComponent.class);
        return contextComponent.getRequestContext();
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
    /**
     * <p>
     * getContainerService
     * </p>
     *
     * @see org.apache.pluto.services.PortletContainerEnvironment#getContainerService(java.lang.Class)
     * @param service
     * @return
     */
    public ContainerService getContainerService( Class service )
    {
        if(service.equals(FactoryManagerService.class))
        {
            return this;
        }

        try
        {
            return (ContainerService) getComponentManager().getComponent(service);
        }
        catch (NoSuchBeanDefinitionException e)
        {
            log.warn("No ContainerService defined for "+service.getName());
            return null;
        }
    }
}
