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
package org.apache.portals.bridges.struts;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.portals.bridges.common.ServletContextProvider;
import org.apache.portals.bridges.struts.config.StrutsPortletConfig;
import org.apache.portals.bridges.struts.util.EmptyHttpServletResponseWrapper;

/**
 * StrutsPortlet
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class StrutsPortlet extends GenericPortlet
{
    /**
     * Name of class implementing {@link ServletContextProvider}
     */
    public static final String PARAM_SERVLET_CONTEXT_PROVIDER = "ServletContextProvider";
    /**
     * Name of portlet preference for Struts Portlet Config Location
     */
    public static final String STRUTS_PORTLET_CONFIG_LOCATION = "StrutsPortletConfigLocation";
    /**
     * Name of portlet preference for Action page
     */
    public static final String PARAM_ACTION_PAGE = "ActionPage";
    /**
     * Name of portlet preference for Custom page
     */
    public static final String PARAM_CUSTOM_PAGE = "CustomPage";
    /**
     * Name of portlet preference for Edit page
     */
    public static final String PARAM_EDIT_PAGE = "EditPage";
    /**
     * Name of portlet preference for Edit page
     */
    public static final String PARAM_HELP_PAGE = "HelpPage";
    /**
     * Name of portlet preference for View page
     */
    public static final String PARAM_VIEW_PAGE = "ViewPage";
    /**
     * Default URL for the action page.
     */
    private String defaultActionPage = null;
    /**
     * Default URL for the custom page.
     */
    private String defaultCustomPage = null;
    /**
     * Default URL for the edit page.
     */
    private String defaultEditPage = null;
    /**
     * Default URL for the help page.
     */
    private String defaultHelpPage = null;
    /**
     * Default URL for the view page.
     */
    private String defaultViewPage = null;
    private ServletContextProvider servletContextProvider;
    private static final Log log = LogFactory.getLog(StrutsPortlet.class);
    public static final String REQUEST_TYPE = "org.apache.portals.bridges.struts.request_type";
    public static final String PAGE_URL = "org.apache.portals.bridges.struts.page_url";
    public static final String REDIRECT_PAGE_URL = "org.apache.portals.bridges.struts.redirect_page_url";
    public static final String REDIRECT_URL = "org.apache.portals.bridges.struts.redirect_url";
    public static final String RENDER_CONTEXT = "org.apache.portals.bridges.struts.render_context";
    public static final String ERROR_CONTEXT = "org.apache.portals.bridges.struts.error_context";
    public static final String PORTLET_NAME = "org.apache.portals.bridges.struts.portlet_name";
    public static final String STRUTS_PORTLET_CONFIG = "org.apache.portals.bridges.struts.portlet_config";
    public static final String DEFAULT_STRUTS_PORTLET_CONFIG_LOCATION = "WEB-INF/struts-portlet-config.xml";
    public static final String ACTION_REQUEST = "ACTION";
    public static final String VIEW_REQUEST = "VIEW";
    public static final String CUSTOM_REQUEST = "CUSTOM";
    public static final String EDIT_REQUEST = "EDIT";
    public static final String HELP_REQUEST = "HELP";
    
    private StrutsPortletConfig strutsPortletConfig;
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        String contextProviderClassName = getContextProviderClassNameParameter(config);
        if (contextProviderClassName == null)
            throw new PortletException("Portlet " + config.getPortletName()
                    + " is incorrectly configured. Init parameter "
                    + PARAM_SERVLET_CONTEXT_PROVIDER + " not specified");
        if (contextProviderClassName != null)
        {
            try
            {
                Class clazz = Class.forName(contextProviderClassName);
                if (clazz != null)
                {
                    Object obj = clazz.newInstance();
                    if (ServletContextProvider.class.isInstance(obj))
                    {
                        servletContextProvider = (ServletContextProvider) obj;
                    }
                    else
                        throw new PortletException("class not found");
                }
            } catch (Exception e)
            {
                if (e instanceof PortletException)
                    throw (PortletException) e;
                e.printStackTrace();
                throw new PortletException("Cannot load", e);
            }
        }
        if (servletContextProvider == null)
            throw new PortletException("Portlet " + config.getPortletName()
                    + " is incorrectly configured. Invalid init parameter "
                    + PARAM_SERVLET_CONTEXT_PROVIDER + " value "
                    + contextProviderClassName);
        this.defaultActionPage = getActionPageParameter(config);
        this.defaultCustomPage = getCustomPageParameter(config);
        this.defaultEditPage = getEditPageParameter(config);
        this.defaultViewPage = getViewPageParameter(config);
        this.defaultHelpPage = getHelpPageParameter(config);
        
        if (this.defaultViewPage == null)
        {
            // A Struts Portlet is required to have at least the
            // defaultViewPage
            // defined!
            throw new PortletException(
                    "Portlet "
                            + config.getPortletName()
                            + " is incorrectly configured. No default View page is defined.");
        }
        if (defaultActionPage == null)
            defaultActionPage = defaultViewPage;
        if (defaultCustomPage == null)
            defaultCustomPage = defaultViewPage;
        if (defaultHelpPage == null)
            defaultHelpPage = defaultViewPage;
        if (defaultEditPage == null)
            defaultEditPage = defaultViewPage;
        
        strutsPortletConfig = new StrutsPortletConfig();
        String strutsPortletConfigLocation = getStrutsPortletConfigLocationParameter(config);
        if ( strutsPortletConfigLocation == null )
        {
            strutsPortletConfigLocation = DEFAULT_STRUTS_PORTLET_CONFIG_LOCATION;
        }
        strutsPortletConfig.loadConfig(config.getPortletContext(),strutsPortletConfigLocation);
        config.getPortletContext().setAttribute(STRUTS_PORTLET_CONFIG,strutsPortletConfig);
    }
    
    protected String getContextProviderClassNameParameter(PortletConfig config)
    {
        return config.getInitParameter(PARAM_SERVLET_CONTEXT_PROVIDER);
    }
    
    protected ServletContextProvider getServletContextProvider()
    {
        return servletContextProvider;
    }
    
    protected ServletContext getServletContext(GenericPortlet portlet, PortletRequest request, PortletResponse response)
    {
        return getServletContextProvider().getServletContext(portlet);
    }
    
    protected HttpServletRequest getHttpServletRequest(GenericPortlet portlet, PortletRequest request, PortletResponse response)
    {
        return getServletContextProvider().getHttpServletRequest(portlet, request);
    }
    
    protected HttpServletResponse getHttpServletResponse(GenericPortlet portlet, PortletRequest request, PortletResponse response)
    {
        return getServletContextProvider().getHttpServletResponse(portlet, response);
    }
    
    protected String getActionPageParameter(PortletConfig config)
    {
        return config.getInitParameter(PARAM_ACTION_PAGE);
    }
    
    protected String getCustomPageParameter(PortletConfig config)
    {
        return config.getInitParameter(PARAM_CUSTOM_PAGE);
    }

    protected String getEditPageParameter(PortletConfig config)
    {
        return config.getInitParameter(PARAM_EDIT_PAGE);
    }

    protected String getViewPageParameter(PortletConfig config)
    {
        return config.getInitParameter(PARAM_VIEW_PAGE);
    }
    
    protected String getHelpPageParameter(PortletConfig config)
    {
        return config.getInitParameter(PARAM_HELP_PAGE);
    }
    
    protected String getStrutsPortletConfigLocationParameter(PortletConfig config)
    {
        return config.getInitParameter(STRUTS_PORTLET_CONFIG_LOCATION);
    }
    
    public void doEdit(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        processRequest(request, response, defaultEditPage, StrutsPortlet.EDIT_REQUEST);
    }
    public void doHelp(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        processRequest(request, response, defaultHelpPage, StrutsPortlet.HELP_REQUEST);
    }
    public void doCustom(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        processRequest(request, response, defaultCustomPage,
                StrutsPortlet.CUSTOM_REQUEST);
    }
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        processRequest(request, response, defaultViewPage, StrutsPortlet.VIEW_REQUEST);
    }
    public void processAction(ActionRequest request, ActionResponse response)
            throws PortletException, IOException
    {
        processRequest(request, response, defaultActionPage,
                StrutsPortlet.ACTION_REQUEST);
    }
    protected void processRequest(PortletRequest request, PortletResponse response,
            String defaultPage, String requestType) throws PortletException,
            IOException
    {
        ServletContext servletContext = getServletContext(this, request, response);
        HttpServletRequest req = getHttpServletRequest(this, request, response);
        HttpServletResponse res = getHttpServletResponse(this, request, response);
        HttpSession session = req.getSession();
        String portletName = this.getPortletConfig().getPortletName();
        req.setAttribute(PORTLET_NAME, portletName);
        boolean actionRequest = (request instanceof ActionRequest);
        
        try
        {
            StrutsPortletErrorContext errorContext = (StrutsPortletErrorContext) req
                    .getSession().getAttribute(StrutsPortlet.ERROR_CONTEXT + "_" + portletName);
            if (errorContext != null)
            {
                if (!actionRequest)
                {
                    req.getSession().removeAttribute(
                            StrutsPortlet.ERROR_CONTEXT + "_" + portletName);
                    renderError(res, errorContext);
                }
                return;
            }

            String keepRenderAttributes = null;
            
            if ( !actionRequest )
            {
                keepRenderAttributes = request.getParameter(StrutsPortletURL.KEEP_RENDER_ATTRIBUTES);
            }
            if ( keepRenderAttributes == null )
            {
                strutsPortletConfig.getRenderContextAttributes().clearAttributes(session);
            }
            else
            {
                strutsPortletConfig.getRenderContextAttributes().restoreAttributes(req);
            }
                                
            String path = null;
            String pageURL = request.getParameter(StrutsPortletURL.PAGE);
            if (pageURL == null)
                path = defaultPage;
            else
            {
                path = pageURL;
            }

            if (log.isDebugEnabled())
                log.debug("process path: " + path + ", requestType: " + requestType);

            RequestDispatcher rd = servletContext.getRequestDispatcher(path);
            if (rd != null)
            {
                if (actionRequest)
                    res = new EmptyHttpServletResponseWrapper(res);
                if (path != null)
                    req.setAttribute(StrutsPortlet.PAGE_URL, path);
                req.setAttribute(StrutsPortlet.REQUEST_TYPE, requestType);
                try
                {
                    rd.include(new PortletServletRequestWrapper(servletContext, req), res);
                } 
                catch (ServletException e)
                {
                    if (log.isErrorEnabled())
                        log.error("Include exception", e);
                    errorContext = new StrutsPortletErrorContext();
                    errorContext.setError(e);
                    req.setAttribute(StrutsPortlet.ERROR_CONTEXT + "_" + portletName, errorContext);
                    if (!actionRequest)
                        renderError(res, errorContext);
                }
                if (actionRequest)
                {
                    String renderURL;
                    if (req.getAttribute(StrutsPortlet.ERROR_CONTEXT) != null)
                    {
                        pageURL = request.getParameter(StrutsPortletURL.ORIGIN);
                        if (pageURL != null)
                            ((ActionResponse) response).setRenderParameter(
                                    StrutsPortletURL.PAGE, pageURL);
                        if (log.isDebugEnabled())
                            log.debug("action render error context");
                        try
                        {
                            req.getSession(true).setAttribute(
                                    StrutsPortlet.ERROR_CONTEXT + "_" + portletName,
                                    req.getAttribute(StrutsPortlet.ERROR_CONTEXT));
                        }
                        catch (IllegalStateException ise)
                        {
                            // catch Session already invalidated exception
                            // There isn't much we can do here other than
                            // redirecting the user to the start page
                        }
                    }
                    else
                    {
                        if ((renderURL = (String) req
                                .getAttribute(StrutsPortlet.REDIRECT_URL)) != null)
                        {
                            if (log.isDebugEnabled())
                                log.debug("action send redirect: " + renderURL);
                            ((ActionResponse) response).sendRedirect(renderURL);
                        } 
                        else
                        {
                            strutsPortletConfig.getRenderContextAttributes().saveAttributes(req);
                            ((ActionResponse) response).setRenderParameter(
                                    StrutsPortletURL.KEEP_RENDER_ATTRIBUTES, "1");

                            if ((renderURL = (String) req
                                    .getAttribute(StrutsPortlet.REDIRECT_PAGE_URL)) != null)
                            {
                                if (log.isDebugEnabled())
                                    log.debug("action render redirected page: "
                                            + renderURL);
                                pageURL = renderURL;
                            }
                            if (pageURL != null)
                            {
                                if (renderURL == null && log.isWarnEnabled())
                                    log.warn("Warning: Using the original action URL for render URL: " +pageURL+".\nA redirect should have been issued.");
                                ((ActionResponse) response).setRenderParameter(
                                        StrutsPortletURL.PAGE, pageURL);
                            }
                        }
                    }
                }
            }
        } catch (IOException e)
        {
            if (log.isErrorEnabled())
                log.error("unexpected", e);
            throw e;
        }
    }
    protected void renderError(HttpServletResponse response,
            StrutsPortletErrorContext errorContext) throws IOException
    {
        PrintWriter writer = response.getWriter();
        writer.println("<hr/><h2>Error</h2>");
        writer.println("<table border='1'>");
        if (errorContext.getErrorCode() != 0)
            writer.println("<tr><td valign='top'><b>Error Code</b></td><td>"
                    + errorContext.getErrorCode() + "</td></tr>");
        if (errorContext.getErrorMessage() != null)
            writer.println("<tr><td valign='top'><b>Error Message</b></td><td>"
                    + errorContext.getErrorMessage() + "</td></tr>");
        if (errorContext.getError() != null)
        {
            Throwable e = errorContext.getError();
            if (e instanceof ServletException
                    && ((ServletException) e).getRootCause() != null)
                e = ((ServletException) e).getRootCause();
            writer.print("<tr><td valign='top'><b>Error</b></td><td>"
                    + e.getMessage() + "</td></tr>");
            writer.print("<tr><td valign='top'><b>Error Type</b></td><td>"
                    + e.getClass().getName() + "</td></tr>");
            writer.print("<tr><td valign='top'><b>Stacktrace</b></td><td>");
            StackTraceElement[] elements = e.getStackTrace();
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < elements.length; i++)
                buf.append("  " + elements[i].toString() + "<br>");
            writer.print(buf.toString());
            writer.println("</td></tr>");
        }
        writer.println("</table>");
    }
}