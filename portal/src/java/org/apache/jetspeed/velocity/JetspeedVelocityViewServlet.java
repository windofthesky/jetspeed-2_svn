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
package org.apache.jetspeed.velocity;

import java.io.File;
import java.io.IOException;
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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.locator.LocatorDescriptor;
import org.apache.jetspeed.locator.TemplateDescriptor;
import org.apache.jetspeed.locator.TemplateLocator;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.Constants;
import org.apache.portals.bridges.velocity.BridgesVelocityViewServlet;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;

/**
 * @version $Id$
 */
public class JetspeedVelocityViewServlet extends BridgesVelocityViewServlet
{
    /** logging */
    private static final Log log = LogFactory.getLog(JetspeedVelocityViewServlet.class);

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
        decorationLocator = (TemplateLocator) cm.getComponent("DecorationLocator");

        // initialize thread safe velocity engine cache
        int cacheSize = (int) getLongInitParameter(config, CACHE_SIZE_PARAMETER, DEFAULT_CACHE_SIZE);
        velocityEngineConfigCache = new LRUMap(cacheSize);
        velocityEngineCache = new LRUMap(cacheSize/2);

        // initialize velocity engine cache validation interval
        cacheValidationInterval = getLongInitParameter(config, CACHE_VALIDATION_INTERVAL_PARAMETER, DEFAULT_CACHE_VALIDATION_INTERVAL);
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
        // configure velocity context
        PortletRequest renderRequest = (PortletRequest) request.getAttribute(Constants.PORTLET_REQUEST);
        RenderResponse renderResponse = (RenderResponse) request.getAttribute(Constants.PORTLET_RESPONSE);
        PortletConfig portletConfig = (PortletConfig) request.getAttribute(Constants.PORTLET_CONFIG);
        if (renderRequest != null)
        {
            renderRequest.setAttribute(VELOCITY_CONTEXT_ATTR, ctx);
        }                        
        ctx.put("JS2RequestContext", request.getAttribute(RequestContext.REQUEST_PORTALENV));
        ctx.put("renderRequest", renderRequest);
        ctx.put("renderResponse", renderResponse);
        ctx.put("portletConfig", portletConfig);
        ctx.put("portletModeView", PortletMode.VIEW);
        ctx.put("portletModeEdit", PortletMode.EDIT);
        ctx.put("portletModeHelp", PortletMode.HELP);
        ctx.put("windowStateNormal", WindowState.NORMAL);
        ctx.put("windowStateMinimized", WindowState.MINIMIZED);
        ctx.put("windowStateMaximized", WindowState.MAXIMIZED);
        StringBuffer appRoot = new StringBuffer(request.getScheme()).append("://")
                                   .append(request.getServerName()).append(":")
                                   .append(request.getServerPort()).append(renderRequest.getContextPath());
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

        // fallback to global velocity engine singleton
        return super.getTemplate(name);
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

        // fallback to global velocity engine singleton
        return super.getTemplate(name, encoding);
    }

    /** velocity engine logging adapter */
    private static class VelocityEngineLogger implements LogSystem
    {
        /** velocity log */
        private static final Log velocityLog = LogFactory.getLog("velocity");

        /**
         * init
         *
         * @see org.apache.velocity.runtime.log.LogSystem.init(org.apache.velocity.runtime.RuntimeServices)
         */
        public void init(RuntimeServices rsvc)
        {
        }
        
        /**
         * logVelocityMessage
         *
         * @see org.apache.velocity.runtime.log.LogSystem.logVelocityMessage(int, java.lang.String)
         */
        public void logVelocityMessage(int level, String message)
        {
            switch (level)
            {
                case LogSystem.DEBUG_ID :
                    velocityLog.debug(message);
                    break;
                case LogSystem.INFO_ID :
                    velocityLog.info(message);
                    break;
                case LogSystem.WARN_ID :
                    velocityLog.warn(message);
                    break;
                case LogSystem.ERROR_ID :
                    velocityLog.error(message);
                    break;
                default :
                    velocityLog.trace(message);
                    break;
            }
        }
    }

    /**
     * Loads Velocity configuration information and returns that 
     * information as an ExtendedProperties, which will be used to 
     * initialize the Velocity runtime.
     *
     * @see org.apache.velocity.tools.view.servlet.VelocityViewServlet.loadConfiguration()
     *
     * @param config ServletConfig passed to the servlets init() function.
     * @return ExtendedProperties loaded with Velocity runtime configuration values.
     * @throws IOException I/O problem accessing the specified file, if specified.
     */
    protected ExtendedProperties loadConfiguration(ServletConfig config)
        throws IOException
    {
        // configure Velocity engines for using logging adapter
        ExtendedProperties configuration = super.loadConfiguration(config);
        configuration.clearProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS);
        configuration.clearProperty("runtime.log.logsystem.log4j.category");
        configuration.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, new VelocityEngineLogger());
        return configuration;
    }

    /**
     * Get VelocityEngine for template access.
     *
     * @param ctx the velocity context.
     * @return The VelocityEngine or null.
     */
    private VelocityEngine getVelocityEngine(Context ctx)
    {
        // get render request and request context from Context
        RenderRequest renderRequest = (RenderRequest) ctx.get("renderRequest");
        RequestContext requestContext = (RequestContext) ctx.get("JS2RequestContext");
        if ((renderRequest != null) && (requestContext != null))
        {
            // get layout type and decoration, fallback to
            // page default decorations
            Fragment layout = (Fragment) renderRequest.getAttribute(JetspeedPowerTool.LAYOUT_ATTR);
            if (layout == null)
            {
                layout = (Fragment) renderRequest.getAttribute(JetspeedPowerTool.FRAGMENT_ATTR);
            }
            String layoutType = layout.getType();
            String layoutDecoration = layout.getDecorator();
            if (layoutDecoration == null)
            {
                Page page = (Page) renderRequest.getAttribute(PortalReservedParameters.PAGE_ATTRIBUTE_KEY);
                layoutDecoration = page.getDefaultDecorator(layoutType);
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
            if ((config != null) && (now <= (config.lastValidated + cacheValidationInterval))) 
            {
                if (config.macros != null)
                {
                    synchronized (velocityEngineCache)
                    {
                        // use cached velocity engine if available
                        VelocityEngine velocity = (VelocityEngine) velocityEngineCache.get(config.macros.getPath());
                        if (velocity != null)
                        {
                            return velocity;
                        }
                    }
                }
                else
                {
                    return null;
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
            descriptor.setName(layoutDecoration + "/" + JetspeedPowerTool.DECORATOR_TYPE + ".properties");
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
                    descriptor.setType(JetspeedPowerTool.GENERIC_TEMPLATE_TYPE);
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
                    descriptor.setName(layoutDecoration + "/" + JetspeedPowerTool.DECORATOR_TYPE + macros + ext);
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
                                descriptor.setName(parent + "/" + JetspeedPowerTool.DECORATOR_TYPE + macros + ext);
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
                 (!macrosDescriptor.getAbsolutePath().equals(config.macros.getPath()) ||
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
                        velocity = (VelocityEngine) velocityEngineCache.get(config.macros.getPath());
                    }
                    if (velocity == null)
                    {
                        // create and cache new velocity engine
                        velocity = initVelocity(macrosDescriptor);
                        if (velocity != null)
                        {
                            velocityEngineCache.put(config.macros.getPath(), velocity);
                        }
                    }
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
            velocity.setProperty(VelocityEngine.RESOURCE_LOADER, "webapp");
            velocity.setProperty("webapp.resource.loader.class", "org.apache.velocity.tools.view.servlet.WebappLoader");
            ExtendedProperties configuration = loadConfiguration(getServletConfig());
            configuration.addProperty("velocimacro.library", macros.getAppRelativePath());
            velocity.setExtendedProperties(configuration);

            // initialize and return velocity engine
            velocity.init();
            log.debug("initVelocity(): create new VelocityEngine instance to support " + macros.getAppRelativePath() + " decoration template macros");
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
}
