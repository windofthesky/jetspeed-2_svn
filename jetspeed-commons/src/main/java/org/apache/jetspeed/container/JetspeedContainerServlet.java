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
package org.apache.jetspeed.container;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.portlet.UnavailableException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.session.PortalSessionsManager;
import org.apache.jetspeed.factory.PortletInstance;
import org.apache.jetspeed.logger.JetspeedLogger;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;
import org.apache.jetspeed.tools.pamanager.PortletApplicationManagement;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.JetspeedLoggerUtil;
import org.apache.pluto.container.PortletMimeResponseContext;

/**
 * Jetspeed Container entry point.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedContainerServlet extends HttpServlet 
{
    private static final long serialVersionUID = -7900846019170204195L;
    
    private JetspeedLogger paLogger;
    
    private boolean started = false;
    private PortalSessionsManager psm;
    
    // default visibility for more optimal access by the startTimer
    Timer startTimer = null;
    String contextName;
    String contextPath;

    // -------------------------------------------------------------------
    // I N I T I A L I Z A T I O N
    // -------------------------------------------------------------------
    private static final String JCS = "JetspeedContainerServlet: ";
    private static final String INIT_START_MSG = JCS + "starting initialization of Portlet Application at: ";
    private static final String TRY_START_MSG = JCS + "attemping to start Portlet Application at: ";
    private static final String STARTED_MSG = JCS + "started Portlet Application at: ";
    private static final String INIT_FAILED_MSG = JCS + "initialization failed for Portlet Application at: ";
    private static final String INIT_DONE_MSG = JCS + "initialization done for Portlet Application at: ";
    private static final String STOP_MSG = JCS + "shutting down portlet application at: ";
    private static final String STOP_FAILED_MSG = JCS + "shutting down error for portlet application at: ";
    
    public synchronized final void init(ServletConfig config) throws ServletException
    {
        synchronized (this.getClass())
        {            
            super.init(config);
            
            ServletContext context = getServletContext();

            started = false;
            startTimer = null;            
            contextName = config.getInitParameter("contextName");            
            contextPath = config.getInitParameter("contextPath");

            if (null == contextName || contextName.length() == 0)
            {
                contextName = null; // just to make sure for the destroy method
                
                throw new ServletException(JCS + "Portlet Application contextName not supplied in Init Parameters.");
            }
            
            if (null == contextPath || contextPath.length() == 0)
            {
                contextPath = "/"+contextName;
            }
            else if(!contextPath.startsWith("/"))
            {
                throw new ServletException(JCS + "Portlet Application contextPath must start with a  \"/\"."); 
            }
            
            String paDir = context.getRealPath("/");
            if ( paDir == null )
            {
                throw new ServletException(JCS + " Initialization of PortletApplication at "+contextName+" without access to its real path not supported");
            }
            
            JetspeedLogger jsLogger = JetspeedLoggerUtil.getSharedLogger(getClass());
            jsLogger.info(INIT_START_MSG + contextName);
            context.log(INIT_START_MSG + contextName);            
            System.out.println(INIT_START_MSG + contextName);            

            try
            {                
                startPortletApplication(context, paDir, Thread.currentThread().getContextClassLoader());
            }
            catch (Exception e)
            {
                String message = INIT_FAILED_MSG + contextName;
                jsLogger.error(message, e);
                context.log(message, e);
                System.err.println(message);
                throw new ServletException(message, e);
            }
            
            jsLogger.info(INIT_DONE_MSG + contextName);
            context.log(INIT_DONE_MSG + contextName);
            System.out.println(INIT_DONE_MSG + contextName);
        }
    }

    private void startPortletApplication(final ServletContext context, final String paDir, final ClassLoader paClassLoader)
    {

/* TODO: Ate Douma, 2005-03-25
   Under fusion, this call always results in a javax.naming.NameNotFoundException: "Name jdbc is not bound in this Context"
   but when started from a separate (timer) Thread, even with only a delay of 1ms, it works again.
   I don't have any clue what is the cause of this or how to solve it, thus for now I disabled starting directly

        if (attemptStart(context, contextName, paDir, paClassLoader)) 
        {
          started = true;
            return;
        }
*/
        final String START_DELAYED_MSG = JCS + "Could not yet start portlet application at: "+contextName+". Starting back ground thread to start when the portal comes online.";
        JetspeedLogger jsLogger = JetspeedLoggerUtil.getSharedLogger(getClass());
        jsLogger.info(START_DELAYED_MSG);
        context.log(START_DELAYED_MSG);
        startTimer = new Timer(true);
        startTimer.schedule(new TimerTask()
        {
            public void run()
            {
                synchronized (contextName)
                {
                    if (startTimer != null)
                    {
                        if (attemptStart(context, contextName, contextPath, paDir, paClassLoader))
                        {
                            startTimer.cancel();
                            startTimer = null;
                        }
                        else
                        {
                            JetspeedLogger jsLogger = JetspeedLoggerUtil.getSharedLogger(getClass());
                            jsLogger.info(START_DELAYED_MSG);
                            context.log(START_DELAYED_MSG);
                        }
                    }
                }
            }
        }, 1, 10000);
    }

    boolean attemptStart(ServletContext context, String contextName, String contextPath, String paDir, ClassLoader paClassLoader) 
    {
        JetspeedLogger jsLogger = JetspeedLoggerUtil.getSharedLogger(getClass());
        
        try
        {
            jsLogger.info(TRY_START_MSG + contextPath);
            context.log(TRY_START_MSG + contextPath);
            PortletServices services = JetspeedPortletServices.getSingleton();
            if (services != null)
            {
                PortletApplicationManagement pam =
                    (PortletApplicationManagement)services.getService("PAM");

                if (pam != null && pam.isStarted())
                {
                    DirectoryHelper paDirHelper = new DirectoryHelper(new File(paDir));
                    pam.startPortletApplication(contextName, contextPath, paDirHelper, paClassLoader);
                    started = true;
                    psm = (PortalSessionsManager)services.getService(PortalSessionsManager.SERVICE_NAME);

                    jsLogger.info(STARTED_MSG + contextPath);
                    context.log(STARTED_MSG + contextPath);
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            jsLogger.error(INIT_FAILED_MSG + contextPath, e);
            context.log(INIT_FAILED_MSG + contextPath, e);
            return true; // don't try again
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
        boolean destroyPortlet = false;
        
        RequestContext rc = Jetspeed.getCurrentRequestContext();
        if (rc == null)
        {
            return;
        }
        PortletWindow window = rc.getCurrentPortletWindow();
        if (window == null)
        {
            return;
        }
        
        if (PortletWindow.Action.NOOP.equals(window.getAction()))
        {
            return;
        }
                        
        try
        {
            PortletInstance portletInstance = window.getPortletInstance();
            PortletConfig portletConfig = portletInstance.getConfig();

            window.getPortletRequestContext().init(portletConfig, getServletContext(), request, response);
            window.getPortletResponseContext().init(request, response);
            
            FilterManager filterManager = (FilterManager) window.getAttribute(PortalReservedParameters.PORTLET_FILTER_MANAGER_ATTRIBUTE);
            window.removeAttribute(PortalReservedParameters.PORTLET_FILTER_MANAGER_ATTRIBUTE);
            
            if (PortletWindow.Action.ACTION.equals(window.getAction()))
            {
                ActionRequest actionRequest = (ActionRequest)window.getPortletRequest();
                ActionResponse actionResponse = (ActionResponse)window.getPortletResponse();
                
                if (filterManager != null)
                {
                    filterManager.processFilter(actionRequest, actionResponse, portletInstance, portletConfig.getPortletContext());
                }
                else
                {
                    portletInstance.processAction(actionRequest, actionResponse);
                }
            }
            else if (PortletWindow.Action.RENDER.equals(window.getAction()))
            {
                RenderRequest renderRequest = (RenderRequest)window.getPortletRequest();
                RenderResponse renderResponse =  (RenderResponse)window.getPortletResponse();
                
                if (filterManager != null)
                {
                    filterManager.processFilter(renderRequest, renderResponse, portletInstance, portletConfig.getPortletContext());
                }
                else
                {
                    portletInstance.render(renderRequest, renderResponse);
                }
            }
            else if (PortletWindow.Action.EVENT.equals(window.getAction()))
            {
                EventRequest eventRequest = (EventRequest)window.getPortletRequest();
                EventResponse eventResponse =  (EventResponse)window.getPortletResponse();
                
                if (filterManager != null)
                {
                    filterManager.processFilter(eventRequest, eventResponse, portletInstance, portletConfig.getPortletContext());
                }
                else
                {
                    portletInstance.processEvent(eventRequest, eventResponse);
                }
            }
            else if (PortletWindow.Action.RESOURCE.equals(window.getAction()))
            {
                ResourceRequest resourceRequest = (ResourceRequest)window.getPortletRequest();
                ResourceResponse resourceResponse = (ResourceResponse)window.getPortletResponse();
                
                if (filterManager != null)
                {
                    filterManager.processFilter(resourceRequest, resourceResponse, portletInstance, portletConfig.getPortletContext());
                }
                else
                {
                    portletInstance.serveResource(resourceRequest, resourceResponse);
                }
            }

            // if we get this far we are home free
            return;
        }
        catch (Throwable t)
        {
            if (paLogger == null)
            {
                paLogger = JetspeedLoggerUtil.getLocalLogger(getClass());
                
                if (paLogger == null)
                {
                    paLogger = JetspeedLoggerUtil.getSharedLogger(getClass());
                }
            }
            
            if ( t instanceof UnavailableException )
            {
                // destroy the portlet in the finally clause
                destroyPortlet = true;
            }
            
            if (PortletWindow.Action.RENDER.equals(window.getAction())|| PortletWindow.Action.RESOURCE.equals(window.getAction()))
            {
                ServletContext context = getServletContext();
                paLogger.error(JCS + "Error rendering portlet \"" + window.getPortletDefinition().getUniqueName() + "\": " + t.toString(), t);
                PrintWriter writer = ((PortletMimeResponseContext)window.getPortletResponseContext()).getWriter();
                if (writer != null)
                {
                    Throwable cause = t;
                    while (cause.getCause() != null) cause = cause.getCause();
                    writer.write("Portlet " + window.getPortletDefinition().getUniqueName() +" not available: " + cause.getMessage());
                }
            }
            else
            {
                if ( t instanceof RuntimeException )
                {
                    throw (RuntimeException)t;
                }
                else if (t instanceof IOException )
                {
                    throw (IOException)t;
                }
                else if (t instanceof ServletException)
                {
                    throw (ServletException)t;
                }
                else
                {
                    throw new ServletException(t);
                }
            }
        }
        finally
        {
            if ( destroyPortlet)
            {
                // portlet threw UnavailableException: take it out of service
                try
                {
                    window.getPortletInstance().destroy();
                }
                catch (Exception e)
                {
                    // never mind, it won't be used anymore.                 
                }
            }
            if (psm != null)
            {
                psm.checkMonitorSession(contextName,rc.getRequest().getSession(),request.getSession(false));
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

    public final void destroy()
    {
        if (contextName != null)
        {
            synchronized (contextName)
            {
                if (startTimer != null)
                {
                    startTimer.cancel();
                    startTimer = null;
                }
                else if (started)
                {
                    started = false;
                    PortletServices services = JetspeedPortletServices.getSingleton();
                    if (services != null)
                    {
                        PortletApplicationManagement pam = (PortletApplicationManagement) services.getService("PAM");
                        if ((pam != null) && pam.isStarted())
                        {
                            JetspeedLogger jsLogger = JetspeedLoggerUtil.getSharedLogger(getClass());
                            jsLogger.info(STOP_MSG + contextName);
                            getServletContext().log(STOP_MSG + contextName);
                            try
                            {
                                pam.stopPortletApplication(contextName);
                            }
                            catch (Exception e)
                            {
                                jsLogger.error(STOP_FAILED_MSG + contextName, e);
                                getServletContext().log(STOP_FAILED_MSG + contextName, e);
                            }
                        }
                    }
                    contextName = null;
                    psm = null;
                }
            }
        }
        
        paLogger = null;
    }
}
