/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.velocity;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.container.ContainerConstants;
import org.apache.jetspeed.desktop.JetspeedDesktopContext;
import org.apache.jetspeed.locator.LocatorDescriptor;
import org.apache.jetspeed.locator.TemplateDescriptor;
import org.apache.jetspeed.locator.TemplateLocator;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.ServletRequestThreadLocalCleanupCallback;
import org.apache.portals.bridges.velocity.BridgesVelocityViewServlet;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.NullSetEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.generic.log.LogSystemCommonsLog;
import org.apache.velocity.tools.view.servlet.WebappLoader;

/**
 * @version $Id$
 */
public class JetspeedVelocityViewServlet extends BridgesVelocityViewServlet
{
    /** logging */
    private static final Logger log = LoggerFactory.getLogger(JetspeedVelocityViewServlet.class);

    /** default cache size */
    private static final long DEFAULT_CACHE_SIZE = 50;

    /** default cache validation interval */
    private static final String CACHE_SIZE_PARAMETER = "org.apache.jetspeed.cache.size";

    /** default cache validation interval */
    private static final long DEFAULT_CACHE_VALIDATION_INTERVAL = 10000;

    /** default cache validation interval */
    private static final String CACHE_VALIDATION_INTERVAL_PARAMETER = "org.apache.jetspeed.cache.validation.interval";

    /** TLS for Context propagation */
    private static ThreadLocal handlingRequestContext = new ThreadLocal();

    /** decoration locators */
    private TemplateLocator decorationLocator;

    /** velocity engine configuration caching object */
    private class VelocityEngineConfig
    {
        public String decoration;
        public String type;
        public String mediaType;
        public String language;
        public String country;

        public File macros;
        public long macrosLastModified;
        public long lastValidated;

        public VelocityEngineConfig(String decoration, String type, String mediaType, String language, String country)
        {
            this.decoration = decoration;
            this.type = type;
            this.mediaType = mediaType;
            this.language = language;
            this.country = country;
            
            this.macrosLastModified = -1;
            this.lastValidated = System.currentTimeMillis();
        }
    }

    /** VelocityEngine configuration cache by decoration */
    private Map velocityEngineConfigCache;

    /** VelocityEngine cache by macros locators */
    private Map velocityEngineCache;

    /** cache validation interval */
    private long cacheValidationInterval;

    /** default velocity engine */
    private VelocityEngine defaultVelocityEngine;
    
    /** Velocity EventCartridge for handling event */
    EventCartridge eventCartridge;

    /**
     * Initialize servlet, BridgesVelocityViewServlet, and VelocityViewServlet.
     *
     * @see org.apache.velocity.tools.view.servlet.VelocityViewServlet.init()
     *
     * @param config servlet configuation
     */
    public void init(ServletConfig config) throws ServletException
    {
        // initialize
        super.init(config);

        // get jetspeed component manager configuration for decorations
        ComponentManager cm = Jetspeed.getComponentManager();
        int count =0;
        while(cm == null) {
            try {
                Thread.sleep(200);
            } catch(InterruptedException ie) {
                
            }
            cm = Jetspeed.getComponentManager();
            if( count > 5 ) {
                if (null == cm)
                    throw new ServletException("Could not get Jetspeed Component Manager after "+count+"tries");
            }
            count++;
        
        }
        decorationLocator = cm.lookupComponent("DecorationLocator");

        // initialize thread safe velocity engine cache
        int cacheSize = (int) getLongInitParameter(config, CACHE_SIZE_PARAMETER, DEFAULT_CACHE_SIZE);
        velocityEngineConfigCache = new LRUMap(cacheSize);
        velocityEngineCache = new LRUMap(cacheSize/2);
        
        eventCartridge = new EventCartridge();
        // setup NullSetEventHandler to ignore those pesky "ERROR velocity - RHS of #set statement is null. Context will not be modified."
        eventCartridge.addEventHandler(new NullSetEventHandler()
        {
            public boolean shouldLogOnNullSet(String lhs, String rhs) { return false; }
        });

        // initialize velocity engine cache validation interval
        cacheValidationInterval = getLongInitParameter(config, CACHE_VALIDATION_INTERVAL_PARAMETER, DEFAULT_CACHE_VALIDATION_INTERVAL);
    }
    
    /**
     * overriding VelocityViewServlet initialization of global Velocity to properly provide our own velocity.properties
     * so to prevent an ERROR logging for not finding the default global VM_global_library.vm (which isn't available).
     */
    protected void initVelocity(ServletConfig config) throws ServletException
    {
        VelocityEngine velocity = new VelocityEngine();
        setVelocityEngine(velocity);

        // register this engine to be the default handler of log messages
        // if the user points commons-logging to the LogSystemCommonsLog
        LogSystemCommonsLog.setVelocityEngine(velocity);

        velocity.setApplicationAttribute(SERVLET_CONTEXT_KEY, getServletContext());

        // default to servletlogger, which logs to the servlet engines log
        velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.tools.view.servlet.ServletLogger");

        // by default, load resources with webapp resource loader
        velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "webapp");
        velocity.setProperty("webapp.resource.loader.class", 
                             WebappLoader.class.getName());

        // Try reading an overriding Velocity configuration
        try
        {
            ExtendedProperties p = loadConfiguration(config);
            p.addProperty("velocimacro.library", "/WEB-INF/jetspeed_macros.vm");
            p.setProperty("file.resource.loader.path", getServletContext().getRealPath("/"));
            velocity.setExtendedProperties(p);
        }
        catch(Exception e)
        {
            getServletContext().log("VelocityViewServlet: Unable to read Velocity configuration file: "+e);
            getServletContext().log("VelocityViewServlet: Using default Velocity configuration.");
        }   

        // now all is ready - init Velocity
        try
        {
            velocity.init();
        }
        catch(Exception e)
        {
            getServletContext().log("VelocityViewServlet: PANIC! unable to init() - "+e);
            throw new ServletException(e);
        }
    }
    
    /**
     * Handle the template processing request.
     *
     * @see org.apache.velocity.tools.view.servlet.VelocityViewServlet.handleRequest()
     *
     * @param request client request
     * @param response client response
     * @param ctx  VelocityContext to fill
     * @return Velocity Template object or null
     */
    protected Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctx) throws Exception
    {
        RequestContext requestContext = (RequestContext)request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        if(requestContext == null)
        {
            throw new IllegalStateException("JetspeedVelocityViewServlet unable to handle request because there is no RequestContext in "+
                   "the HttpServletRequest.");
        }
        new ServletRequestThreadLocalCleanupCallback(handlingRequestContext);
        
        // hook up eventHandlers to the context, specifically our own IgnoringNullSetEventHandling
        eventCartridge.attachToContext(ctx);
        
        JetspeedDesktopContext desktopContext = (JetspeedDesktopContext)request.getAttribute(JetspeedDesktopContext.DESKTOP_CONTEXT_ATTRIBUTE);
        if (desktopContext != null)
        {
            // standard render request and response also available in context
            ctx.put(JetspeedDesktopContext.DESKTOP_CONTEXT_ATTRIBUTE, desktopContext);
            ctx.put("JS2RequestContext", requestContext);
            
            // setup TLS for Context propagation
            handlingRequestContext.set(ctx);    
            return super.handleRequest(request, response, ctx);            
        }
        // configure velocity context
        PortletRequest renderRequest = (PortletRequest) request.getAttribute(ContainerConstants.PORTLET_REQUEST);
        RenderResponse renderResponse = (RenderResponse) request.getAttribute(ContainerConstants.PORTLET_RESPONSE);
        PortletConfig portletConfig = (PortletConfig) request.getAttribute(ContainerConstants.PORTLET_CONFIG);
        if (renderRequest != null)
        {
            renderRequest.setAttribute(VELOCITY_CONTEXT_ATTR, ctx);
        }
                
        JetspeedVelocityPowerTool jpt = (JetspeedVelocityPowerTool) renderRequest.getAttribute(PortalReservedParameters.JETSPEED_POWER_TOOL_REQ_ATTRIBUTE);
        if(jpt == null)
        {
            throw new IllegalStateException("JetspeedVelocityViewServlet unable to handle request because there is no JetspeedPowerTool in "+
                   "the HttpServletRequest.");
        }
        
        jpt.setVelocityContext(ctx);
        ctx.put("jetspeed", jpt);  
        ctx.put("JS2RequestContext", requestContext);
        ctx.put("renderRequest", renderRequest);
        ctx.put("renderResponse", renderResponse);
        ctx.put("portletConfig", portletConfig);
        ctx.put("portletModeView", PortletMode.VIEW);
        ctx.put("portletModeEdit", PortletMode.EDIT);
        ctx.put("portletModeHelp", PortletMode.HELP);
        ctx.put("windowStateNormal", WindowState.NORMAL);
        ctx.put("windowStateMinimized", WindowState.MINIMIZED);
        ctx.put("windowStateMaximized", WindowState.MAXIMIZED);
        ctx.put("rco", requestContext.getObjects());
        StringBuffer appRoot = new StringBuffer();
        if (!requestContext.getPortalURL().isRelativeOnly())
        {
            appRoot.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort());
        }
        appRoot.append(renderRequest.getContextPath());
        ctx.put("appRoot", appRoot.toString());        
        
        
        // setup TLS for Context propagation
        handlingRequestContext.set(ctx);

        // handle request normally        
        return super.handleRequest(request, response, ctx);
    }

    /**
     * Retrieves the requested template.
     *
     * @see org.apache.velocity.tools.view.servlet.VelocityViewServlet.getTemplate()
     *
     * @param name The file name of the template to retrieve relative to the template root.
     * @return The requested template.
     * @throws ResourceNotFoundException if template not found from any available source.
     * @throws ParseErrorException if template cannot be parsed due to syntax (or other) error.
     * @throws Exception if an error occurs in template initialization
     */
    public Template getTemplate(String name)
        throws ResourceNotFoundException, ParseErrorException, Exception
    {
        // retrieve Context to lookup appropriate velocity engine
        Context ctx = (Context) handlingRequestContext.get();
        if (ctx != null)
        {
            // create or lookup cached velocity engine
            VelocityEngine velocity = getVelocityEngine(ctx);
            if (velocity != null)
            {
                // get template from velocity engine
                return velocity.getTemplate(name);
            }
        }

        // no velocity engine available
        throw new Exception("No velocity engine available for request context.");
    }

    /**
     * Retrieves the requested template with the specified character encoding.
     *
     * @see org.apache.velocity.tools.view.servlet.VelocityViewServlet.getTemplate()
     *
     * @param name The file name of the template to retrieve relative to the template root.
     * @param encoding the character encoding of the template
     * @return The requested template.
     * @throws ResourceNotFoundException if template not found from any available source.
     * @throws ParseErrorException if template cannot be parsed due to syntax (or other) error.
     * @throws Exception if an error occurs in template initialization
     */
    public Template getTemplate(String name, String encoding)
        throws ResourceNotFoundException, ParseErrorException, Exception
    {
        // retrieve Context to lookup appropriate velocity engine
        Context ctx = (Context) handlingRequestContext.get();
        if (ctx != null)
        {
            // create or lookup cached velocity engine
            VelocityEngine velocity = getVelocityEngine(ctx);
            if (velocity != null)
            {
                // get template from velocity engine
                return velocity.getTemplate(name, encoding);
            }
        }

        // no velocity engine available
        throw new Exception("No velocity engine available for request context.");
    }

    /**
     * Get VelocityEngine for template access.
     *
     * @param ctx the velocity context.
     * @return The VelocityEngine or null.
     */
    private VelocityEngine getVelocityEngine(Context ctx)
    {
        RequestContext requestContext = (RequestContext) ctx.get("JS2RequestContext");        
        JetspeedDesktopContext desktopContext = (JetspeedDesktopContext)requestContext.getRequest().getAttribute(JetspeedDesktopContext.DESKTOP_CONTEXT_ATTRIBUTE);        
        if (desktopContext != null)
        {
            if (defaultVelocityEngine == null)
            {
                defaultVelocityEngine = initVelocity((TemplateDescriptor)null);
            }
            return defaultVelocityEngine;            
        }                
        // get render request and request context from Context
        RenderRequest renderRequest = (RenderRequest) ctx.get("renderRequest");
        JetspeedVelocityPowerTool jpt = (JetspeedVelocityPowerTool) ctx.get("jetspeed");
        if ((renderRequest != null) && (requestContext != null))
        {
            // get layout type and decoration, fallback to
            // page default decorations
            ContentFragment layout = (ContentFragment) renderRequest.getAttribute(JetspeedVelocityPowerTool.LAYOUT_ATTR);
            if (layout == null)
            {
               // layout = (Fragment) renderRequest.getAttribute(JetspeedPowerTool.FRAGMENT_ATTR);
                layout = jpt.getCurrentFragment();
            }
            String layoutType = layout.getType();
            String layoutDecoration = layout.getDecorator();
            if (layoutDecoration == null)
            {
                //Page page = (Page) renderRequest.getAttribute(PortalReservedParameters.PAGE_ATTRIBUTE_KEY);
                ContentPage page = requestContext.getPage();
                layoutDecoration = page.getEffectiveDefaultDecorator(layoutType);
            }
            
            // get layout capabilites and locale
            CapabilityMap capabilityMap = requestContext.getCapabilityMap();
            Locale locale = requestContext.getLocale();
            String layoutMediaType = capabilityMap.getPreferredMediaType().getName();
            String layoutLanguage = locale.getLanguage();
            String layoutCountry = locale.getCountry();
            
            // lookup cache config based on decoration cache key
            String cacheKey = layoutDecoration + ":" + layoutType + ":" + layoutMediaType + ":" + layoutLanguage + ":" + layoutCountry;
            VelocityEngineConfig config = null;
            synchronized (velocityEngineConfigCache)
            {
               config = (VelocityEngineConfig) velocityEngineConfigCache.get(cacheKey);
            }
            
            // validate cached configuration and return VelocityEngine if cached
            long now = System.currentTimeMillis();
            if ((config != null) && ((cacheValidationInterval == -1) || (now <= (config.lastValidated + cacheValidationInterval)))) 
            {
                if (config.macros != null)
                {
                    synchronized (velocityEngineCache)
                    {
                        // use cached velocity engine if available
                        VelocityEngine velocity = (VelocityEngine) velocityEngineCache.get(config.macros.getAbsolutePath());
                        if (velocity != null)
                        {
                            return velocity;
                        }
                    }
                }
                else
                {
                    // use default velocity engine
                    synchronized (this)
                    {
                        // construct and cache default velocity engine
                        if (defaultVelocityEngine == null)
                        {
                            defaultVelocityEngine = initVelocity((TemplateDescriptor)null);
                        }
                        return defaultVelocityEngine;
                    }
                }
            }
           
            // load and/or verify decorator macros configuration
            TemplateDescriptor macrosDescriptor = null;
            
            // create reusable decoration base descriptor
            LocatorDescriptor descriptor = null;
            try
            {
                descriptor = decorationLocator.createLocatorDescriptor(null);
            }
            catch (TemplateLocatorException tle)
            {
                log.error("getVelocityEngine(): unable create base descriptor", tle);
            }
            descriptor.setMediaType(layoutMediaType);
            descriptor.setCountry(layoutCountry);
            descriptor.setLanguage(layoutLanguage);
            descriptor.setType(layoutType);
            
            // get decoration configuration properties descriptor
            descriptor.setName(layoutDecoration + "/" + JetspeedVelocityPowerTool.DECORATOR_TYPE + ".properties");
            TemplateDescriptor propertiesDescriptor = null;
            try
            {
                propertiesDescriptor = decorationLocator.locateTemplate(descriptor);
            }
            catch (TemplateLocatorException tle)
            {
                // fallback to generic template type
                try
                {
                    descriptor.setType(JetspeedVelocityPowerTool.GENERIC_TEMPLATE_TYPE);
                    propertiesDescriptor = decorationLocator.locateTemplate(descriptor);
                }
                catch (TemplateLocatorException tleFallback)
                {
                }
            }
            // load configuration properties
            Configuration configuration = null;
            if (propertiesDescriptor != null)
            {
                try
                {
                    configuration = new PropertiesConfiguration(propertiesDescriptor.getAbsolutePath());
                }
                catch (ConfigurationException ce)
                {
                    log.warn("getVelocityEngine(): unable read decorator properties from " + propertiesDescriptor.getAbsolutePath(), ce);
                }
            }
            if (configuration != null)
            {
                // get decoration template macros extension and suffix
                String ext = configuration.getString("template.extension");
                String macros = configuration.getString("template.macros");
                
                // get decoration template macros descriptor if defined
                if ((ext != null) && (ext.length() > 0) && (macros != null) && (macros.length() > 0))
                {
                    descriptor.setName(layoutDecoration + "/" + JetspeedVelocityPowerTool.DECORATOR_TYPE + macros + ext);
                    try
                    {
                        macrosDescriptor = decorationLocator.locateTemplate(descriptor);
                    }
                    catch (TemplateLocatorException tle)
                    {
                        // fallback to extends decoration, (assume macros named the
                        // same in the parent decoration as configured here)
                        try
                        {
                            String parent = configuration.getString("extends");
                            if ((parent != null) && (parent.length() > 0))
                            {
                                descriptor.setName(parent + "/" + JetspeedVelocityPowerTool.DECORATOR_TYPE + macros + ext);
                                macrosDescriptor = decorationLocator.locateTemplate(descriptor);
                            }
                        }
                        catch (TemplateLocatorException tleExtends)
                        {
                        }
                    }
                }
            }
            
            // compare located macros file with cached version
            // to validate/refresh cached config and velocity engine
            boolean newVelocityEngineConfig = false;
            boolean forceVelocityEngineRefresh = false;
            if (config == null)
            {
                config = new VelocityEngineConfig(layoutDecoration, layoutType, layoutMediaType, layoutLanguage, layoutCountry);
                synchronized (velocityEngineConfigCache)
                {
                    velocityEngineConfigCache.put(cacheKey, config);
                }
                newVelocityEngineConfig = true;
            }
            if (((macrosDescriptor == null) && (config.macros != null)) ||
                ((macrosDescriptor != null) && (config.macros == null)) ||
                ((macrosDescriptor != null) && (config.macros != null) &&
                 (!macrosDescriptor.getAbsolutePath().equals(config.macros.getAbsolutePath()) ||
                  (config.macros.lastModified() != config.macrosLastModified))))
            {
                // set or reset configuration cache entry
                config.lastValidated = now;
                if (macrosDescriptor != null)
                {
                    // save macros file
                    config.macros = new File(macrosDescriptor.getAbsolutePath());
                    config.macrosLastModified = config.macros.lastModified();
                }
                else
                {
                    // clear macros file
                    config.macros = null;
                    config.macrosLastModified = -1;
                }

                // aggressively force creation of new velocity engine
                // if any configuration change detected
                forceVelocityEngineRefresh = !newVelocityEngineConfig;
            }
            else
            {
                // config validated
                config.lastValidated = now;
            }

            // get or create new velocity engine intialized with
            // validated macros configuration
            VelocityEngine velocity = null;
            if ((macrosDescriptor != null) && (config.macros != null))
            {
                synchronized (velocityEngineCache)
                {
                    if (!forceVelocityEngineRefresh)
                    {
                        // use cached velocity engine
                        velocity = (VelocityEngine) velocityEngineCache.get(config.macros.getAbsolutePath());
                    }
                    if (velocity == null)
                    {
                        // create and cache new velocity engine
                        velocity = initVelocity(macrosDescriptor);
                        if (velocity != null)
                        {
                            velocityEngineCache.put(config.macros.getAbsolutePath(), velocity);
                        }
                    }
                }
            }

            // fallback to default velocity engine
            if (velocity == null)
            {
                synchronized (this)
                {
                    // construct and cache default velocity engine
                    if (defaultVelocityEngine == null)
                    {
                        defaultVelocityEngine = initVelocity((TemplateDescriptor)null);
                    }
                    velocity = defaultVelocityEngine;
                }
            }
            
            // return velocity engine for validated configuration
            return velocity;
        }
        return null;
    }

    /**
     * Initialize new velocity instance using specified macros template.
     *
     * @see org.apache.velocity.tools.view.servlet.VelocityViewServlet.initVelocity()
     *
     * @param macros template descriptor.
     * @return new VelocityEngine instance.
     */
    private VelocityEngine initVelocity(TemplateDescriptor macros)
    {
        try
        {
            // create new instance to initialize
            VelocityEngine velocity = new VelocityEngine();
            
            // initialize new instance as is done with the default
            // velocity singleton, appending macros template to the
            // base configuration velocimacro.library property
            velocity.setApplicationAttribute(SERVLET_CONTEXT_KEY, getServletContext());
            velocity.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.tools.view.servlet.ServletLogger");
            ExtendedProperties configuration = loadConfiguration(getServletConfig());
            if (macros != null)
            {
                configuration.addProperty("velocimacro.library", macros.getAppRelativePath());
            }
            configuration.setProperty("file.resource.loader.path", getServletContext().getRealPath("/"));
            velocity.setExtendedProperties(configuration);

            // initialize and return velocity engine
            velocity.init();
            if (macros != null)
            {
                log.debug("initVelocity(): create new VelocityEngine instance to support " + macros.getAppRelativePath() + " decoration template macros");
            }
            else
            {
                log.debug("initVelocity(): create new default VelocityEngine instance");
            }
            return velocity;
        }
        catch (Exception e)
        {
            log.error("initVelocity(): unable to initialize velocity engine instance, using default singleton", e);
        }
        return null;
    }

    /**
     * Utility to get long init parameters.
     *
     * @param config servlet config
     * @param name of init parameter
     * @param defaultValue value
     * @return parameter value
     */
    private long getLongInitParameter(ServletConfig config, String name, long defaultValue)
    {
        String value = config.getInitParameter(name);
        if ((value == null) || (value.length() == 0))
        {
            value = config.getServletContext().getInitParameter(name);
        }
        if ((value != null) && (value.length() > 0))
        {
            try
            {
                return Long.parseLong(value);
            }
            catch (Exception e)
            {
            }
        }
        return defaultValue;
    }
    
    protected void error(HttpServletRequest request, 
            HttpServletResponse response, 
            Exception e)
    throws ServletException
    {
        try
        {
            StringBuffer html = new StringBuffer();
            html.append("<b>\n");
            html.append("Content is not available");
            html.append("<b>\n");
            getResponseWriter(response).write(html.toString());
            log.error("Error processing vm template ", e);
        }
        catch (Exception e2)
        {
            log.error("Error writing error message to vm template ", e2);            
        }        
    }
    
}
