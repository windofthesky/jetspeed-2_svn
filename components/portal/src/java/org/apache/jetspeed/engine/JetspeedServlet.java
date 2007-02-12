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
package org.apache.jetspeed.engine;

import java.io.File;
import java.io.IOException;
import java.security.Principal;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.components.factorybeans.ServletConfigFactoryBean;
import org.apache.jetspeed.container.session.PortalSessionsManager;
import org.apache.jetspeed.engine.servlet.ServletHelper;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.pipeline.valve.SecurityValve;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;
import org.apache.jetspeed.statistics.PortalStatistics;

/**
 * Jetspeed Servlet entry point.
 * 
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor </a>
 * @version $Id$
 */
public class JetspeedServlet 
extends HttpServlet 
implements JetspeedEngineConstants, HttpSessionListener
{
    private static Log log;
    private static Log console;

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
     * The Jetspeed Engine
     */
    private static Engine engine;

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
                log = LogFactory.getLog(JetspeedServlet.class);
                console = LogFactory.getLog(CONSOLE_LOGGER);                
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

                Configuration properties = (Configuration) new PropertiesConfiguration(ServletHelper.getRealPath(
                        config, propertiesFilename));

                properties.setProperty(APPLICATION_ROOT_KEY, applicationRoot);
                properties.setProperty(WEBAPP_ROOT_KEY, webappRoot);

                console.info("JetspeedServlet attempting to create the  portlet engine...");

                engine = new JetspeedEngine(properties, applicationRoot, config, initializeComponentManager(config, applicationRoot, properties));
             
                console.info("JetspeedServlet attempting to start the Jetspeed Portal Engine...");
                Jetspeed.setEngine(engine);
                engine.start();                
                console.info("JetspeedServlet has successfuly started the Jetspeed Portal Engine....");

            }
            catch (Throwable e)
            {
                // save the exception to complain loudly later :-)
                final String msg = "Jetspeed: init() failed: ";
                initFailure = e;               
                log.fatal(msg, e);
                console.fatal(msg, e);
            }

            console.info(INIT_DONE_MSG);
            log.info(INIT_DONE_MSG);
        }
    }

    /**
     * Initializes the services which need <code>RunData</code> to initialize
     * themselves (post startup).
     * 
     * @param data
     *            The first <code>GET</code> request.
     */
    public final void init( HttpServletRequest request, HttpServletResponse response )
    {
        synchronized (JetspeedServlet.class)
        {
            if (firstDoGet)
            {
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
    public final void doGet( HttpServletRequest req, HttpServletResponse res ) throws IOException, ServletException
    {
        try
        {
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
                RequestContextComponent contextComponent = (RequestContextComponent) Jetspeed.getComponentManager()
                        .getComponent(RequestContextComponent.class);
                RequestContext context = contextComponent.create(req, res, getServletConfig());
                engine.service(context);
                contextComponent.release(context);
            }

        }
        catch (JetspeedException e)
        {
            final String msg = "Fatal error encountered while processing portal request: "+e.toString();
            log.fatal(msg, e);
            throw new ServletException(msg, e);
        }
    }

    /**
     * In this application doGet and doPost are the same thing.
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
    public final void doPost( HttpServletRequest req, HttpServletResponse res ) throws IOException, ServletException
    {
        doGet(req, res);
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
            log.fatal("Jetspeed: shutdown() failed: ", e);
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
       // String relativeApplicationRoot = getRealPath("/");
        String relativeApplicationRoot = appRoot;
        String absApplicationRoot = new File(relativeApplicationRoot).getCanonicalPath();               
        
        final String assemblyDir = configuration.getString("assembly.dir","/WEB-INF/assembly");
        final String assemblyFileExtension = configuration.getString("assembly.extension",".xml");
            
        
        String[] bootConfigs = new String[] {"/WEB-INF/assembly/boot/*.xml"};
        String[] appConfigs =  new String[] {assemblyDir+"/*"+assemblyFileExtension};
        ServletContext servletContext = servletConfig.getServletContext();
        SpringComponentManager cm = new SpringComponentManager(bootConfigs, appConfigs, servletContext, appRoot);      
        
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
        Principal subjectUserPrincipal = SecurityHelper.getPrincipal(subject, UserPrincipal.class);
        PortalStatistics statistics = (PortalStatistics)engine.getComponentManager().getComponent("PortalStatistics");
        long sessionLength = System.currentTimeMillis() - se.getSession().getCreationTime();
        String ipAddress = (String)se.getSession().getAttribute(SecurityValve.IP_ADDRESS);
        statistics.logUserLogout(ipAddress, subjectUserPrincipal.getName(), sessionLength);    
        JetspeedCache portletContentCache = (JetspeedCache)engine.getComponentManager().getComponent("portletContentCache");
        portletContentCache.evictContentForUser(subjectUserPrincipal.getName());
    }
}
