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

//import org.apache.commons.lang.exception.ExceptionUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.factory.JetspeedPortletFactoryProxy;
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
public class JetspeedContainerServlet extends HttpServlet 
{
 //   private final static Log log = LogFactory.getLog(JetspeedContainerServlet.class);
   // private final static Log console = LogFactory.getLog(CONSOLE_LOGGER);

    /**
     * Whether init succeeded or not.
     */
    private static Throwable initFailure = null;


    private static String webappRoot;

    // -------------------------------------------------------------------
    // I N I T I A L I Z A T I O N
    // -------------------------------------------------------------------
    private static final String JCS = "JetspeedContainerServlet: ";
    private static final String INIT_START_MSG = JCS + "starting initialization of context ";
    private static final String INIT_DONE_MSG = JCS + "Initialization complete for context ";
    
    /**
     * Intialize Servlet.
     */
    public synchronized final void init(ServletConfig config) throws ServletException
    {
        synchronized (this.getClass())
        {            
            super.init(config);
            ServletContext context = getServletContext();
            String name = context.getServletContextName();
            if (name == null || name.length() == 0)
            {
                name = context.getRealPath("/");
                if (name == null)
                {
                    name = "Jetspeed";
                }
            }
            context.log(INIT_START_MSG + name);            
            System.out.println(INIT_START_MSG + name);            

            try
            {                
                String registerAtInit = config.getInitParameter("registerAtInit");
                if (null != registerAtInit)
                {
                    context.log(JCS + "Considering PA for registration during servlet init: " + name);
                    String portletApplication = config.getInitParameter("portletApplication");
                    if (null == portletApplication)
                    {
                        throw new ServletException(JCS + "Portlet Application Name not supplied in Init Parameters.");
                    }
                    
                    registerPortletApplication(context, portletApplication);
                    
                }                
            }
            catch (Exception e)
            {
                initFailure = e;
                String message = JCS + "Initialization of servlet " + name + " failed.";
                context.log(message, e);
                System.err.println(message);
                e.printStackTrace(System.err);                
                throw new ServletException(message, e);
            }

            context.log(INIT_DONE_MSG + name);
            System.out.println(INIT_DONE_MSG + name);
        }
    }

    private void registerPortletApplication(final ServletContext context, final String portletApplicationName)
    throws ServletException
    {

        context.log(JCS + "Attempting to register portlet application: name=" + portletApplicationName);
        if (attemptRegistration(context, portletApplicationName)) 
        {
            context.log(JCS + "Registered portlet application: name=" + portletApplicationName);
        }

        context.log(JCS + "Could not registered portlet application; starting back ground thread to register when jetspeed comes online: name=" + portletApplicationName);
        final Timer timer = new Timer(true);
        timer.schedule(
                new TimerTask() {
                    public void run() {
                        context.log(JCS + "Attempting to register portlet application: name=" + portletApplicationName);
                        if (attemptRegistration(context, portletApplicationName)) {
                            context.log(JCS + "Registered portlet application: name=" + portletApplicationName);
                            timer.cancel();
                        } else {
                            context.log(JCS + "Could not register portlet application; will try again later: name=" + portletApplicationName);
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
                        context.log(JCS + "Portlet Application Registered at Servlet Init: " + portletApplicationName);
                    }
                    else
                    {
                        context.log(JCS + "Portlet Application did not change. Not Registered at Servlet Init: " + portletApplicationName);
                    }
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            context.log(JCS + "Failed to register PA: " + portletApplicationName);
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
        Integer method = ContainerConstants.METHOD_NOOP;
        try
        {
            // Check to make sure that we started up properly.
            if (initFailure != null)
            {
                throw initFailure;
            }

            // infuseClasspath();
            
            method = (Integer) request.getAttribute(ContainerConstants.METHOD_ID);
            if (method == ContainerConstants.METHOD_NOOP)
            {
                return;
            }
            
            PortletDefinition portletDefinition = JetspeedPortletFactoryProxy.getCurrentPortletDefinition();                        
            portletName = portletDefinition.getName();
            Portlet portlet = JetspeedPortletFactoryProxy.getPortlet(this.getServletConfig(), portletDefinition);

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
            ServletContext context = getServletContext();
            context.log(JCS + "Error rendering portlet \"" + portletName + "\": " + t.toString(), t);
            try
            {
                String errorTemplate = getInitParameter("portal.error.page");
                if (errorTemplate == null)
                {
                    errorTemplate = "/WEB-INF/templates/generic/html/error.vm";
                }
                if (null != context.getResource(errorTemplate))
                {
                    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(errorTemplate);                
                    request.setAttribute("e", t);
                    StringWriter stackTrace = new StringWriter();
                    t.printStackTrace(new PrintWriter(stackTrace));
                    request.setAttribute("stacktrace", stackTrace.toString());
                    dispatcher.include(request, response);
                }
                else
                {
                    if (method != ContainerConstants.METHOD_ACTION)
                    {
                        displayPortletNotAvailableMessage(t, response, portletName);
                    }
                }
            }
            catch (Throwable e)
            {
                displayPortletNotAvailableMessage(t, response, portletName);                
            }
            finally
            {
                t.printStackTrace();
            }
        }
    }

    private void displayPortletNotAvailableMessage(Throwable t, HttpServletResponse response, String portletName)
    throws IOException
    {
        getServletContext().log(JCS + "Error rendering JetspeedContainerServlet error page: " + t.toString(), t);                
        PrintWriter directError = new PrintWriter(response.getWriter());
        directError.write("Portlet is Not Available: " + portletName + "<br/>Reason: " + t.getMessage());
        //t.printStackTrace(directError); 
        directError.close();        
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
        getServletContext().log(JCS + "Shutting down portlet app context: " + getServletContext().getServletContextName());
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