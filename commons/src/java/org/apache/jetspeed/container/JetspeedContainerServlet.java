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
package org.apache.jetspeed.container;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.factory.JetspeedPortletFactory;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;
import org.apache.jetspeed.tools.pamanager.DeploymentRegistration;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.FileSystemHelper;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * Jetspeed Container entry point.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedContainerServlet extends HttpServlet implements ServletContainerConstants
{
    private final static Log log = LogFactory.getLog(JetspeedContainerServlet.class);
    private final static Log console = LogFactory.getLog(CONSOLE_LOGGER);

    /**
     * Whether init succeeded or not.
     */
    private static Throwable initFailure = null;


    private static String webappRoot;

    // -------------------------------------------------------------------
    // I N I T I A L I Z A T I O N
    // -------------------------------------------------------------------
    private static final String INIT_START_MSG = "Jetspeed Container Starting Initialization...";
    private static final String INIT_DONE_MSG = "Jetspeed Container Initialization complete, Ready to service requests.";

    /**
     * Intialize Servlet.
     */
    public synchronized final void init(ServletConfig config) throws ServletException
    {
        synchronized (this.getClass())
        {
            log.info(INIT_START_MSG + " " + config.getServletContext().getRealPath("/"));
            super.init(config);


            try
            {                
                ServletContext context = config.getServletContext();
                webappRoot = config.getServletContext().getRealPath("/");                
                String registerAtInit = config.getInitParameter("registerAtInit");
                if (null != registerAtInit)
                {
                    log.info("Considering PA for registration during servlet init: " + context.getServletContextName());
                    String portletApplication = config.getInitParameter("portletApplication");
                    if (null == portletApplication)
                    {
                        throw new ServletException("Portlet Application Name not supplied in Init Parameters.");
                    }
                    
                    registerPortletApplication(context, portletApplication);
                    
                }
                else
                {
                    log.info("Will not register this PA during servlet init: " + context.getServletContextName());
                }
                
            }
            catch (Exception e)
            {
                initFailure = e;
                log.fatal("Jetspeed: init() failed: ", e);
                System.err.println(ExceptionUtils.getStackTrace(e));
                throw new ServletException("Jetspeed: init() failed", e);
            }

            console.info(INIT_DONE_MSG);
            log.info(INIT_DONE_MSG + " " + config.getServletContext().getRealPath("/"));
        }
    }

    private void registerPortletApplication(final ServletContext context, final String portletApplicationName)
    throws ServletException
    {

        log.info("Attempting to register portlet application: name=" + portletApplicationName);
        if (attemptRegistration(context, portletApplicationName)) {
            log.info("Registered portlet application: name=" + portletApplicationName);
        }

        log.info("Could not registered protlet application; starting back ground thread to register when jetspeed comes online: name=" + portletApplicationName);
        final Timer timer = new Timer(true);
        timer.schedule(
                new TimerTask() {
                    public void run() {
                        log.info("Attempting to register portlet application: name=" + portletApplicationName);
                        if (attemptRegistration(context, portletApplicationName)) {
                            log.info("Registered portlet application: name=" + portletApplicationName);
                            timer.cancel();
                        } else {
                            log.info("Could not register portlet application; will try again later: name=" + portletApplicationName);
                        }
                    }
                },
                10000,
                10000);
    }

    private static boolean attemptRegistration(ServletContext context, String portletApplicationName) 
    {
        try
        {
            PortletServices services = JetspeedPortletServices.getSingleton();
            if (services != null)
            {
                DeploymentRegistration registrar =
                    (DeploymentRegistration)services.getService("PAM");

                if (registrar != null)
                {
                    FileSystemHelper webapp = new DirectoryHelper(new File(context.getRealPath("/")));
                    if (registrar.registerPortletApplication(webapp, portletApplicationName))
                    {
                        log.info("Portlet Application Registered at Servlet Init: " + portletApplicationName);
                    }
                    else
                    {
                        log.info("Portlet Application did not change. Not Registered at Servlet Init: " + portletApplicationName);
                    }
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            log.error("Failed to register PA: " + portletApplicationName);
        }
        return false;
    }    
    
    // -------------------------------------------------------------------
    // R E Q U E S T  P R O C E S S I N G
    // -------------------------------------------------------------------

    /**
     * The primary method invoked when the Jetspeed servlet is executed.
     *
     * @param request Servlet request.
     * @param ressponse Servlet response.
     * @exception IOException a servlet exception.
     * @exception ServletException a servlet exception.
     */
    public final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        String portletName = null;
        try
        {
            // Check to make sure that we started up properly.
            if (initFailure != null)
            {
                throw initFailure;
            }

            // infuseClasspath();

            PortletDefinition portletDefinition = (PortletDefinition) request.getAttribute(ContainerConstants.PORTLET_ENTITY);
            Portlet portlet = JetspeedPortletFactory.getPortlet(this.getServletConfig(), portletDefinition);
            portletName = portletDefinition.getName();
            Integer method = (Integer) request.getAttribute(ContainerConstants.METHOD_ID);
            if (method == ContainerConstants.METHOD_NOOP)
            {
                return;
            }

            log.debug("Rendering: Portlet Class = " + portletDefinition.getClassName());

            if (method == ContainerConstants.METHOD_ACTION)
            {
                ActionRequest actionRequest = (ActionRequest) request.getAttribute(ContainerConstants.PORTLET_REQUEST);
                ActionResponse actionResponse = (ActionResponse) request.getAttribute(ContainerConstants.PORTLET_RESPONSE);

                portlet.processAction(actionRequest, actionResponse);
            }
            else if (method == ContainerConstants.METHOD_RENDER)
            {
                RenderRequest renderRequest = (RenderRequest) request.getAttribute(ContainerConstants.PORTLET_REQUEST);
                RenderResponse renderResponse = (RenderResponse) request.getAttribute(ContainerConstants.PORTLET_RESPONSE);

                portlet.render(renderRequest, renderResponse);
            }

            // if we get this far we are home free
            return;

        }
        catch (Throwable t)
        {

            log.error("Error rendering portlet \""+portletName+"\": " + t.toString(), t);
            try
            {
                String errorTemplate = getInitParameter("portal.error.page");
                if (errorTemplate == null)
                {
                    errorTemplate = "/WEB-INF/templates/generic/html/error.vm";
                }
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(errorTemplate);
                request.setAttribute("e", t);
                StringWriter stackTrace = new StringWriter();
                t.printStackTrace(new PrintWriter(stackTrace));
                request.setAttribute("stacktrace", stackTrace.toString());
                dispatcher.include(request, response);

            }
            catch (Exception e)
            {
                PrintWriter directError = new PrintWriter(response.getWriter());
                directError.write("Error occured process includeTemplate(): " + t.toString() + "\n\n");
                t.printStackTrace(directError);
                directError.close();
                log.error("Error rendering JetspeedContainerServlet error page: " + e.toString(), e);
            }
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
        log.info("Done shutting down!");
    }

    public static final String LOCAL_CLASSES = "/WEB-INF/classes/";
    public static final String LOCAL_JARS = "/WEB-INF/lib/";

    private void infuseClasspath()
    {
        try
        {
            ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader defaultLoader = Class.class.getClassLoader();
            
            System.out.println("thread Loader is " + oldLoader);
            System.out.println("defaultLoader is " + defaultLoader);
            /*
            ClassLoader loader; // = (ClassLoader)classLoaders.get(portletApplicationName);            
            //            if (null == loader)
            {
                StringBuffer localPath = new StringBuffer("file:");
                // localPath.append(jetspeedContext.getRealPath(JetspeedPortletContext.LOCAL_PA_ROOT));
                // localPath.append(portletApplicationName);
                String localAppPath = "file://c:/bluesunrise/apache/catalina/webapps/jetspeed";
                //localPath.toString(); 
                URL[] urls = { new URL(localAppPath + LOCAL_CLASSES), new URL(localAppPath + LOCAL_JARS)};
                loader = new URLClassLoader(urls, oldLoader);
                // classLoaders.put(portletApplicationName, loader);
            }
            Thread.currentThread().setContextClassLoader(loader);
            */
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

    }
    
 
}