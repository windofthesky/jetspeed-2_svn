/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.container;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
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
import org.apache.pluto.core.CoreUtils;
import org.apache.pluto.core.InternalPortletRequest;
import org.apache.pluto.core.InternalPortletResponse;
import org.apache.pluto.om.portlet.PortletDefinition;
// import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;

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

    private static String webappRoot;

    // -------------------------------------------------------------------
    // I N I T I A L I Z A T I O N
    // -------------------------------------------------------------------
    private static final String INIT_START_MSG = "Jetspeed Container Starting Initialization...";
    private static final String INIT_DONE_MSG = "Jetspeed Container Initialization complete, Ready to service requests.";

    /**
     * Intialize Servlet.
     */
    public final void init(ServletConfig config) throws ServletException
    {
        synchronized (this.getClass())
        {
            log.info(INIT_START_MSG);
            super.init(config);

            if (!firstInit)
            {
                log.info("Double initialization of Jetspeed was attempted!");
                return;
            }
            // executing init will trigger some static initializers, so we have
            // only one chance.
            firstInit = false;

            try
            {
                ServletContext context = config.getServletContext();
                /*
                                String propertiesFilename =
                                    ServletHelper.findInitParameter(context, config,
                                                      JETSPEED_PROPERTIES_KEY,
                                                      JETSPEED_PROPERTIES_DEFAULT);
                                
                                String applicationRoot =
                                    ServletHelper.findInitParameter(context, config,
                                                  APPLICATION_ROOT_KEY,
                                                  APPLICATION_ROOT_DEFAULT);
                  */
                webappRoot = config.getServletContext().getRealPath("/");
                /*                
                                if (applicationRoot == null || applicationRoot.equals(WEB_CONTEXT))
                                {
                                    applicationRoot = webappRoot;
                                }
                                    
                                Configuration properties = (Configuration) 
                                    new PropertiesConfiguration(ServletHelper.getRealPath(config, propertiesFilename));
                                
                                properties.setProperty(APPLICATION_ROOT_KEY, applicationRoot);
                                properties.setProperty(WEBAPP_ROOT_KEY, webappRoot);
                  */
            }
            catch (Exception e)
            {
                initFailure = e;
                log.fatal("Jetspeed: init() failed: ", e);
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
        synchronized (JetspeedContainerServlet.class)
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
    static private final String PHONEY_PORTLET_WINDOW = "<P>----------------------------------</P>";

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
                init(request, response);
            }

            PortletDefinition portletDefinition = (PortletDefinition) request.getAttribute(ContainerConstants.PORTLET_ENTITY);
            Portlet portlet = JetspeedPortletFactory.getPortlet(this.getServletConfig(), portletDefinition);

            Integer method = (Integer) request.getAttribute(ContainerConstants.METHOD_ID);
            if (method == ContainerConstants.METHOD_NOOP)
            {
                return;
            }

            //res.getWriter().print("Rendering: Portlet Class = " + entity.getPortletClass() + "<BR/>");

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

                response.getWriter().print(PHONEY_PORTLET_WINDOW);
                response.getWriter().print(portletDefinition.getName());
                response.getWriter().print(PHONEY_PORTLET_WINDOW);

                portlet.render(renderRequest, renderResponse);

                response.getWriter().print(PHONEY_PORTLET_WINDOW);
            }

        }
        catch (Throwable t)
        {
            t.printStackTrace();
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
        // Allow turbine to be started back up again.
        firstInit = true;

        log.info("Done shutting down!");
    }

  

}
