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

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.engine.servlet.ServletHelper;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;

/**
 * Jetspeed Servlet entry point.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedServlet extends HttpServlet implements JetspeedEngineConstants
{
    private final static Log log = LogFactory.getLog(JetspeedServlet.class);
    private final static Log console = LogFactory.getLog(CONSOLE_LOGGER);

    /**
     * In certain situations the init() method is called more than once,
     * somtimes even concurrently. This causes bad things to happen,
     * so we use this flag to prevent it.
     */
    private static boolean firstInit = true;

    /**
     * Whether init succeeded or not.
     */
    private static Throwable initFailure = null;

    /**
     * Should initialization activities be performed during doGet()
     * execution?
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
    public final void init(ServletConfig config) throws ServletException
    {
        synchronized (this.getClass())
        {
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

                String propertiesFilename =
                    ServletHelper.findInitParameter(context, config, JETSPEED_PROPERTIES_KEY, JETSPEED_PROPERTIES_DEFAULT);

                String applicationRoot =
                    ServletHelper.findInitParameter(context, config, APPLICATION_ROOT_KEY, APPLICATION_ROOT_DEFAULT);

                console.info("JetspeedServlet identifying web application root...");
                webappRoot = config.getServletContext().getRealPath("/");
                console.info("JetspeedServlet identifed web application root as " + webappRoot);

                if (applicationRoot == null || applicationRoot.equals(WEB_CONTEXT))
                {
                    applicationRoot = webappRoot;
                }

                Configuration properties =
                    (Configuration) new PropertiesConfiguration(ServletHelper.getRealPath(config, propertiesFilename));

                properties.setProperty(APPLICATION_ROOT_KEY, applicationRoot);
                properties.setProperty(WEBAPP_ROOT_KEY, webappRoot);

                console.info("JetspeedServlet attempting to create the  portlet engine...");
                String engineClassName = config.getInitParameter("engine");
                if(engineClassName == null)
                {
                    throw new IllegalStateException("You must define the engine init-parameter org.apache.jetspeed.engine.JetspeedServlet servlet.");
                }
                Class engineClass = Class.forName(engineClassName);
                
                engine = Jetspeed.createEngine(properties, applicationRoot, config, engineClass);
                if (engine != null)
                {
                    console.info("JetspeedServlet successfuly created the portal Engine. " + engine);
                }
                else
                {
                    throw new ServletException("Unable to create Jetspeed portal engine");
                }
                console.info("JetspeedServlet attempting to initialize the  portletcontainer...");
                engine.initContainer(config);
                console.info("JetspeedServlet has successfuly initialized the portlet container...");

            }
            catch (Throwable e)
            {
                // save the exception to complain loudly later :-)
                initFailure = e;
                log.fatal("Jetspeed: init() failed: ", e);
                console.fatal("Jetspeed: init() failed: ", e);
                System.err.println(ExceptionUtils.getStackTrace(e));
                throw new ServletException("Jetspeed: init() failed", e);
            }

            console.info(INIT_DONE_MSG);
            log.info(INIT_DONE_MSG);
        }
    }

    /**
     * Initializes the services which need <code>RunData</code> to
     * initialize themselves (post startup).
     *
     * @param data The first <code>GET</code> request.
     */
    public final void init(HttpServletRequest request, HttpServletResponse response)
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
    // R E Q U E S T  P R O C E S S I N G
    // -------------------------------------------------------------------

    /**
     * The primary method invoked when the Jetspeed servlet is executed.
     *
     * @param req Servlet request.
     * @param res Servlet response.
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     */
    public final void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        try
        {
            // Check to make sure that we started up properly.
            if (initFailure != null)
            {
                throw initFailure;
            }

            // If this is the first invocation, perform some late initialization.
            if (firstDoGet)
            {
                init(req, res);
            }

            RequestContextComponent contextComponent = (RequestContextComponent)Jetspeed.getComponentManager().getComponent(RequestContextComponent.class);
            RequestContext context = contextComponent.create(req, res, getServletConfig());
            engine.service(context);
            contextComponent.release(context);

        }
        catch (Throwable t)
        {            
            t.printStackTrace();
            //handleException(data, req, res, t);
        }
    }

    /**
     * In this application doGet and doPost are the same thing.
     *
     * @param req Servlet request.
     * @param res Servlet response.
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     */
    public final void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException
    {
        doGet(req, res);
    }

    // -------------------------------------------------------------------
    // S E R V L E T  S H U T D O W N
    // -------------------------------------------------------------------

    /**
     * The <code>Servlet</code> destroy method. Invokes <code>ServiceBroker</code>
     * tear down method.
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

    private void debugHeaders(HttpServletRequest req)
    {
        java.util.Enumeration e = req.getHeaderNames();
        while (e.hasMoreElements())
        {
            String name = (String) e.nextElement();
            String value = req.getHeader(name);
            System.out.println("name = " + name);
            System.out.println("value = " + value);
        }
    }
}
