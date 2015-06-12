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
package org.apache.jetspeed.engine;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.cache.UserContentCacheManager;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.JetspeedBeanDefinitionFilter;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.factorybeans.ServletConfigFactoryBean;
import org.apache.jetspeed.components.portletpreferences.PortletPreferencesProvider;
import org.apache.jetspeed.container.session.PortalSessionsManager;
import org.apache.jetspeed.engine.servlet.ServletHelper;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.pipeline.valve.SecurityValve;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.request.RequestDiagnostics;
import org.apache.jetspeed.request.RequestDiagnosticsFactory;
import org.apache.jetspeed.request.RequestDiagnosticsHolder;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.UserSubjectPrincipal;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;
import org.apache.jetspeed.statistics.PortalStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.Properties;

/**
 * Jetspeed Servlet entry point.
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor </a>
 * @version $Id$
 */
public class JetspeedServlet extends HttpServlet implements JetspeedEngineConstants, HttpSessionListener
{
    private static Logger log;
    private static Logger console;

    /**
     * In certain situations the init() method is called more than once,
     * somtimes even concurrently. This causes bad things to happen, so we use
     * this flag to prevent it.
     */
    private static boolean firstInit = true;

    /**
     * Whether init succeeded or not.
     */
    private static Throwable initFailure = null;

    /**
     * Should initialization activities be performed during doGet() execution?
     */
    private static boolean firstDoGet = true;
    
    /**
     * The servlet init parameter name providing the portal url path mapped to a error handler servlet/jsp
     * for handling request processing exceptions thrown.
     */
    private static final String ERROR_HANDLER_PATH_PARM = "errorHandlerPath";

    /**
     * The default portal url path used as error handler servlet/jsp when the ERROR_HANDLER_PATH_PARAM
     * servlet init parameter isn't configured
     */
    private static final String DEFAULT_ERROR_HANDLER_PATH = "/diagnostics";

    /**
     * The Jetspeed Engine
     */
    private static Engine engine;
    private static RequestContextComponent contextComponent;
    
    private static String webappRoot;

    // -------------------------------------------------------------------
    // I N I T I A L I Z A T I O N
    // -------------------------------------------------------------------
    private static final String INIT_START_MSG = "Jetspeed Starting Initialization...";
    private static final String INIT_DONE_MSG = "Jetspeed Initialization complete, Ready to service requests.";

    /**
     * Intialize Servlet.
     */
    public final void init( ServletConfig config ) throws ServletException
    {
        synchronized (this.getClass())
        {
            if ( log == null )
            {
                log = LoggerFactory.getLogger(JetspeedServlet.class);
                console = LoggerFactory.getLogger(CONSOLE_LOGGER);                
            }
            
            console.info(INIT_START_MSG);

            super.init(config);

            if (!firstInit)
            {
                log.info("Double initialization of Jetspeed was attempted!");
                console.info("Double initialization of Jetspeed was attempted!");
                return;
            }
            // executing init will trigger some static initializers, so we have
            // only one chance.
            firstInit = false;

            try
            {

                ServletContext context = config.getServletContext();

                String propertiesFilename = ServletHelper.findInitParameter(context, config, JETSPEED_PROPERTIES_KEY,
                        JETSPEED_PROPERTIES_DEFAULT);

                String applicationRoot = ServletHelper.findInitParameter(context, config, APPLICATION_ROOT_KEY,
                        APPLICATION_ROOT_DEFAULT);

                console.info("JetspeedServlet identifying web application root...");
                webappRoot = config.getServletContext().getRealPath("/");
                console.info("JetspeedServlet identifed web application root as " + webappRoot);

                if (applicationRoot == null || applicationRoot.equals(WEB_CONTEXT))
                {
                    applicationRoot = webappRoot;
                }

                // Using \ characters will corrupt the path when used as (Spring expanded) variables
                // making sure default (Java) path separators are used which somehow always work, even on Windows platform.
                applicationRoot = applicationRoot.replace('\\', '/');
                
                // load jetspeed.properties, override.properties and spring-filter-key.properties separately
                // and "merge" them by hand instead of relaying on Commons Configuration "include" functionality...
                // Commons Configuration performs property value *appending* if keys are encountered multiple times,
                // thereby *not* resulting in the proper override functionality we need.
                PropertiesConfiguration properties = new PropertiesConfiguration();
                File propsFile = new File(ServletHelper.getRealPath(config, propertiesFilename));
                if (!propsFile.isFile())
                {
                    throw new IOException("Jetspeed properties not found: "+propsFile.getAbsolutePath());
                }
                File jetspeedPropertiesPath = propsFile.getParentFile();
                properties.load(propsFile);
                propsFile = new File(jetspeedPropertiesPath,OVERRIDE_PROPERTIES);
                if (propsFile.exists())
                {
                    PropertiesConfiguration extraProps = new PropertiesConfiguration();
                    extraProps.load(propsFile);
                    ConfigurationUtils.copy(extraProps,properties);
                }
                propsFile = new File(jetspeedPropertiesPath,SPRING_FILTER_KEY_PROPERTIES);
                if (propsFile.exists())
                {
                    PropertiesConfiguration extraProps = new PropertiesConfiguration();
                    extraProps.load(propsFile);
                    Object springFilterKey = extraProps.getProperty(SPRING_FILTER_KEY);
                    if (springFilterKey != null)
                    {
                        properties.setProperty(SPRING_FILTER_KEY, springFilterKey);
                    }
                }
                properties.setProperty(APPLICATION_ROOT_KEY, applicationRoot);
                properties.setProperty(WEBAPP_ROOT_KEY, webappRoot);
                properties.setProperty(JETSPEED_PROPERTIES_PATH_KEY, jetspeedPropertiesPath.getAbsolutePath());
                
                console.info("JetspeedServlet attempting to create the  portlet engine...");

                engine = new JetspeedEngine(properties, applicationRoot, config, initializeComponentManager(config, applicationRoot, properties));
             
                console.info("JetspeedServlet attempting to start the Jetspeed Portal Engine...");
                Jetspeed.setEngine(engine);
                engine.start();                
                console.info("JetspeedServlet has successfuly started the Jetspeed Portal Engine....");
                contextComponent = Jetspeed.getComponentManager().lookupComponent(RequestContextComponent.class);
            }
            catch (Throwable e)
            {
                // save the exception to complain loudly later :-)
                final String msg = "Jetspeed: init() failed: ";
                initFailure = e;               
                log.error(msg, e);
                console.error(msg, e);
            }

            console.info(INIT_DONE_MSG);
            log.info(INIT_DONE_MSG);
        }
    }

    /**
     * Initializes the services which need <code>RunData</code> to initialize
     * themselves (post startup).
     * 
     * @param request The first <code>GET</code> request.
     * @param response The first <code>GET</code> response.
     *
     */
    public final void init( HttpServletRequest request, HttpServletResponse response )
    {
        synchronized (JetspeedServlet.class)
        {
            if (firstDoGet)
            {
                // initialize the Portal context path
                engine.getContext().setContextPath(request.getContextPath());
                // Mark that we're done.
                firstDoGet = false;
            }
        }
    }

    // -------------------------------------------------------------------
    // R E Q U E S T P R O C E S S I N G
    // -------------------------------------------------------------------

    /**
     * The primary method invoked when the Jetspeed servlet is executed.
     * 
     * @param req
     *            Servlet request.
     * @param res
     *            Servlet response.
     * @exception IOException
     *                a servlet exception.
     * @exception ServletException
     *                a servlet exception.
     */
    private void invoke( HttpServletRequest req, HttpServletResponse res ) throws IOException, ServletException
    {
        // define RequestContext here to allow protential usage and state processing in case of an exception
        // in outer catch errorHandler method call
        RequestContext context = null;
        try
        {
            cleanupError(req);
            
            // Check to make sure that we started up properly.
            if (initFailure != null)
            {
                throw new ServletException("Failed to initalize jetspeed.  "+initFailure.toString(), initFailure);
            }

            // If this is the first invocation, perform some late
            // initialization.
            if (firstDoGet)
            {
                init(req, res);
            }

            //If we already passed though the content filter DON'T send it to the
            // engine.  This is a crappy hack until we find a better solution.
            String wasFiltered = (String) req.getAttribute("org.apache.jetspeed.content.filtered");
            if (wasFiltered == null || !wasFiltered.equals("true"))
            {
                // ensure that no proxy or brower caching is performed
                // on dynamic responses resulting from pipeline execution
                res.setHeader("Cache-Control", "no-cache,no-store,private"); // HTTP/1.1 modern browser/proxy
                res.setHeader("Pragma", "no-cache");                         // HTTP/1.0 non-standard proxy 
                res.setHeader("Expires", "0");                               // HTTP/1.0 browser/proxy

                // send request through pipeline
                try
                {
                    context = contextComponent.create(req, res, getServletConfig());
                    engine.service(context);
                }
                finally
                {
                    contextComponent.setRequestContext(null);
                }
            }

        }
        catch (Throwable e)
        {            
            handleError(req, res, context, e);
        }
    }
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        invoke(request, response);
    }
    
    // -------------------------------------------------------------------
    // S E R V L E T S H U T D O W N
    // -------------------------------------------------------------------

    /**
     * The <code>Servlet</code> destroy method. Invokes
     * <code>ServiceBroker</code> tear down method.
     */
    public final void destroy()
    {
        try
        {
            Jetspeed.shutdown();
        }
        catch (JetspeedException e)
        {
            log.error("Jetspeed: shutdown() failed: ", e);
            System.err.println(ExceptionUtils.getStackTrace(e));
        }

        // Allow turbine to be started back up again.
        firstInit = true;

        log.info("Done shutting down!");
    }    
    
    /**
     * If you prefer to use a component manager other than Spring, you
     * can override this method to do so.  Do not explicitly call start()
     * of the ComponentManager as the JetspeedEngine will do this within its
     * own start() method.
     * 
     * @param servletConfig
     * @param appRoot
     * @param configuration
     * @return
     * @throws IOException
     */
    protected ComponentManager initializeComponentManager(ServletConfig servletConfig, String appRoot, Configuration configuration) throws IOException
    {
        ServletConfigFactoryBean.setServletConfig(servletConfig);
        final String assemblyDir = configuration.getString("assembly.dir","/WEB-INF/assembly");
        final String assemblyFileExtension = configuration.getString("assembly.extension",".xml");
        String springFilterKey = configuration.getString(SPRING_FILTER_KEY, SPRING_FILTER_KEY_DEFAULT);
        File springFilterProperties = new File(configuration.getString(JETSPEED_PROPERTIES_PATH_KEY), SPRING_FILTER_PROPERTIES);
        if (!springFilterProperties.isFile())
        {
            throw new IOException("Spring filter properties not found: "+springFilterProperties.getAbsolutePath());
        }
        String[] bootConfigs = new String[] {"/WEB-INF/assembly/boot/*.xml"};
        String[] appConfigs =  new String[] {assemblyDir+"/*"+assemblyFileExtension, assemblyDir+"/override/*"+assemblyFileExtension};
        ServletContext servletContext = servletConfig.getServletContext();
        JetspeedBeanDefinitionFilter filter = new JetspeedBeanDefinitionFilter("file:"+springFilterProperties.getAbsolutePath(), springFilterKey);
        Properties initProperties = new Properties();
        initProperties.put(JETSPEED_PROPERTIES_PATH_KEY, configuration.getString(JETSPEED_PROPERTIES_PATH_KEY));
        SpringComponentManager cm = new SpringComponentManager(filter, bootConfigs, appConfigs, servletContext, appRoot, initProperties);      
        
        return cm;        
    }
    
    public void sessionCreated(HttpSessionEvent se)
    {
        PortletServices services = JetspeedPortletServices.getSingleton();
        if (services != null)
        {
            PortalSessionsManager psm = (PortalSessionsManager)services.getService(PortalSessionsManager.SERVICE_NAME);
            if (psm != null)
            {
                psm.portalSessionCreated(se.getSession());
            }
        }
        PortletPreferencesProvider preferencesProvider = engine.getComponentManager().lookupComponent("portletPreferencesProvider");
        if (preferencesProvider != null) {
            preferencesProvider.sessionCreatedEvent(se.getSession());
        }
    }
    
    public void sessionDestroyed(HttpSessionEvent se)
    {
        Subject subject = (Subject)se.getSession().getAttribute(PortalReservedParameters.SESSION_KEY_SUBJECT);
        if (subject == null)
            return;
        if (firstInit)
        {
            // Servlet already destroyed, 
            // Can't reliably access ComponentManager (Spring) anymore
            // as for instance WAS 6.0.2 has a bug invoking this method with a wrong classLoader (not the one for the WebApp)
            return;
        }        
        Principal subjectUserPrincipal = SubjectHelper.getPrincipal(subject, UserSubjectPrincipal.class);
        PortalStatistics statistics = engine.getComponentManager().lookupComponent("PortalStatistics");
        long sessionLength = System.currentTimeMillis() - se.getSession().getCreationTime();
        String ipAddress = (String)se.getSession().getAttribute(SecurityValve.IP_ADDRESS);
        statistics.logUserLogout(ipAddress, subjectUserPrincipal.getName(), sessionLength);    
        UserContentCacheManager userContentCacheManager = engine.getComponentManager().lookupComponent("userContentCacheManager");
        userContentCacheManager.evictUserContentCache(subjectUserPrincipal.getName(), se.getSession().getId());
        // clear preferences cache
        PortletPreferencesProvider preferencesProvider = engine.getComponentManager().lookupComponent("portletPreferencesProvider");
        if (preferencesProvider != null) {
            preferencesProvider.sessionDestroyedEvent(se.getSession());
        }
    }
           
    /**
     * Extendable and overridable default main request error handling method.
     * <p>
     * Be aware: the context parameter might not (yet) be initialized, or not anymore here, so it should always
     * be checked against null. Furthermore, not all RequestContext methods might be valid to use or call (anymore) either.
     * </p>
     * @param req the original portal request
     * @param res the original portal response
     * @param context the context created for handling this request
     * @param t the exception as occurred
     * @throws IOException
     * @throws ServletException
     */
    protected void handleError(HttpServletRequest req, HttpServletResponse res, RequestContext context, Throwable t) throws IOException, ServletException
    {
        String errorHandlerPath = getInitParameter(ERROR_HANDLER_PATH_PARM);
        if (errorHandlerPath == null || errorHandlerPath.trim().length() == 0)
        {
            errorHandlerPath = DEFAULT_ERROR_HANDLER_PATH;
        }
        final String msg = "Request error encountered while processing portal request: "+t.getMessage();
        log.error(msg, t);
        
        // try to unwind and see if an Exception was thrown containing a RequestDiagnostics
        RequestDiagnostics rd = null;
        Throwable e = t;
        do
        {
            if (e instanceof RequestDiagnosticsHolder && ((RequestDiagnosticsHolder)e).getRequestDiagnostics() != null)
            {
                rd = ((RequestDiagnosticsHolder)e).getRequestDiagnostics();
                break;
            }
            e = e.getCause();
        }
        while (e != null);
        
        if (rd == null)
        {
            rd = RequestDiagnosticsFactory.newRequestDiagnostics();
        }
        RequestDiagnosticsFactory.fillInRequestContext(rd, req, context, t);
        rd.logAsError();
        req.getSession(true).setAttribute(PortalReservedParameters.REQUEST_DIAGNOSTICS_ATTRIBUTE, rd);
        res.sendRedirect(req.getContextPath() + errorHandlerPath);
    }
    
    /**
     * Cleanup errorHandler state usefull if/when the errorHandler stores error state within the session
     * @param req
     */
    protected void cleanupError(HttpServletRequest req)
    {
        HttpSession session = req.getSession(false);
        if (session != null)
        {
            session.removeAttribute(PortalReservedParameters.REQUEST_DIAGNOSTICS_ATTRIBUTE);
        }
    }
}
