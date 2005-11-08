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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;
import org.apache.jetspeed.tools.pamanager.PortletApplicationManagement;
import org.apache.jetspeed.util.DirectoryHelper;

/**
 * Jetspeed Container entry point.
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedContainerServlet extends HttpServlet 
{
    private String  contextName;
    private boolean started = false;
    private Timer   startTimer = null;

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

            if (null == contextName || contextName.length() == 0)
            {
                contextName = null; // just to make sure for the destroy method
                
                throw new ServletException(JCS + "Portlet Application contextName not supplied in Init Parameters.");
            }            
            String paDir = context.getRealPath("/");
            if ( paDir == null )
                {
              throw new ServletException(JCS + " Initialization of PortletApplication at "+contextName+" without access to its real path not supported");
                }

            context.log(INIT_START_MSG + contextName);            
            System.out.println(INIT_START_MSG + contextName);            

            try
            {                
              startPortletApplication(context, paDir, Thread.currentThread().getContextClassLoader());
            }
            catch (Exception e)
            {
                String message = INIT_FAILED_MSG + contextName;
                context.log(message, e);
                System.err.println(message);
                throw new ServletException(message, e);
            }

            context.log(INIT_DONE_MSG + contextName);
            System.out.println(INIT_DONE_MSG + contextName);
        }
    }

    private void startPortletApplication(final ServletContext context, final String paDir, final ClassLoader paClassLoader)
    throws ServletException
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
        context.log(START_DELAYED_MSG);
        startTimer = new Timer(true);
        startTimer.schedule(
                new TimerTask() {
                    public void run() {
                      synchronized(contextName)
                      {
                        if (startTimer != null)
                        {
                          if (attemptStart(context, contextName, paDir, paClassLoader)) {
                            startTimer.cancel();
                            startTimer = null;
                        } else {
                            context.log(START_DELAYED_MSG);
                          }
                        }
                        }
                    }
                },
//                10000, Setting delay to 1ms, see TODO comment above
                1,
                10000);
    }

    private boolean attemptStart(ServletContext context, String contextPath, String paDir, ClassLoader paClassLoader) 
    {
        try
        {
            context.log(TRY_START_MSG + contextPath);
            PortletServices services = JetspeedPortletServices.getSingleton();
            if (services != null)
            {
                PortletApplicationManagement pam =
                    (PortletApplicationManagement)services.getService("PAM");

                if (pam != null)
                {
                    DirectoryHelper paDirHelper = new DirectoryHelper(new File(paDir));
                    pam.startPortletApplication(contextPath, paDirHelper, paClassLoader);
                    started = true;
                    context.log(STARTED_MSG + contextPath);
                    return true;
                }
            }
        }
        catch (Exception e)
        {
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
        String portletName = null;
        Integer method = ContainerConstants.METHOD_NOOP;
        Portlet portlet = null;
        boolean destroyPortlet = false;
        
        try
        {
            method = (Integer) request.getAttribute(ContainerConstants.METHOD_ID);
            if (method == ContainerConstants.METHOD_NOOP)
            {
                return;
            }
            
            portlet = (Portlet)request.getAttribute(ContainerConstants.PORTLET);
            portletName = (String)request.getAttribute(ContainerConstants.PORTLET_NAME);
            request.removeAttribute(ContainerConstants.PORTLET);

            if (method == ContainerConstants.METHOD_ACTION)
            {
                ActionRequest actionRequest = (ActionRequest) request.getAttribute(ContainerConstants.PORTLET_REQUEST);
                ActionResponse actionResponse = (ActionResponse) request.getAttribute(ContainerConstants.PORTLET_RESPONSE);
                // inject the current request into the actionRequest handler (o.a.j.engine.servlet.ServletRequestImpl)
                ((HttpServletRequestWrapper)((HttpServletRequestWrapper)actionRequest).getRequest()).setRequest(request);

                portlet.processAction(actionRequest, actionResponse);
            }
            else if (method == ContainerConstants.METHOD_RENDER)
            {
                RenderRequest renderRequest = (RenderRequest) request.getAttribute(ContainerConstants.PORTLET_REQUEST);
                RenderResponse renderResponse = (RenderResponse) request.getAttribute(ContainerConstants.PORTLET_RESPONSE);
                // inject the current request into the renderRequest handler (o.a.j.engine.servlet.ServletRequestImpl)
                ((HttpServletRequestWrapper)((HttpServletRequestWrapper)renderRequest).getRequest()).setRequest(request);

                portlet.render(renderRequest, renderResponse);
            }

            // if we get this far we are home free
            return;
        }
        catch (Throwable t)
        {
            if ( t instanceof UnavailableException )
            {
                // destroy the portlet in the finally clause
                destroyPortlet = true;
            }
            
            if (method != ContainerConstants.METHOD_ACTION)
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
                        displayPortletNotAvailableMessage(t, response, portletName);
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
            if ( destroyPortlet )
            {
                // portlet throwed UnavailableException: take it out of service
                try
                {
                    portlet.destroy();
                }
                catch (Exception e)
                {
                    // never mind, it won't be used anymore.                 
                }
            }
        }
    }

    private void displayPortletNotAvailableMessage(Throwable t, HttpServletResponse response, String portletName)
    throws IOException
    {
        getServletContext().log(JCS + "Error rendering JetspeedContainerServlet error page: " + t.toString(), t);
        PrintWriter directError;
        try
        {
            directError = new PrintWriter(response.getWriter());
        }
        catch (IllegalStateException e)
        {
            // Happens if get writer is already been called.
            directError = new PrintWriter(new OutputStreamWriter(response.getOutputStream()));            
        }
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

    public final void destroy()
    {
      if ( contextName != null )
      {
        synchronized (contextName)
        {
          if ( startTimer != null )
          {
            startTimer.cancel();
            startTimer = null;
    }
          else if ( started )
          {
            started = false;
            PortletServices services = JetspeedPortletServices.getSingleton();
            if (services != null)
            {
                PortletApplicationManagement pam =
                    (PortletApplicationManagement)services.getService("PAM");

                if (pam != null)
    {
                    getServletContext().log(STOP_MSG + contextName);
        try
        {
                      pam.stopPortletApplication(contextName);
                    }
                    catch (Exception e)
            {
                      getServletContext().log(STOP_FAILED_MSG + contextName, e);
                    }
                }
            }
            contextName = null;
            }
        }
        }
    }
}
