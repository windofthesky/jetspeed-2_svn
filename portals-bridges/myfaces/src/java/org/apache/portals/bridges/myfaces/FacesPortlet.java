/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.portals.bridges.myfaces;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.RenderKitFactory;
import javax.faces.webapp.FacesServlet;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * FacesPortlet utilizes Java Server Faces to create the user interface in a
 * portlet environment.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@yahoo.com">David Le Strat </a>
 */
public class FacesPortlet extends GenericPortlet
{

    /** The Log instance for this class. */
    private static final Log log = LogFactory.getLog(FacesPortlet.class);

    /** The VIEW_ID used for externalContext.getRequestServletPath(). */
    public static final String VIEW_ID = "org.apache.portals.bridges.myfaces.VIEW_ID";

    /** Name of portlet preference for Action page. */
    public static final String PARAM_ACTION_PAGE = "ActionPage";

    /** Name of portlet preference for Custom page. */
    public static final String PARAM_CUSTOM_PAGE = "CustomPage";

    /** Name of portlet preference for Edit page. */
    public static final String PARAM_EDIT_PAGE = "EditPage";

    /** Name of portlet preference for Edit page */
    public static final String PARAM_HELP_PAGE = "HelpPage";

    /** Name of portlet preference for View page */
    public static final String PARAM_VIEW_PAGE = "ViewPage";

    /** Action request. */
    public static final String ACTION_REQUEST = "ACTION";

    /** View request. */
    public static final String VIEW_REQUEST = "VIEW";

    /** Custom request. */
    public static final String CUSTOM_REQUEST = "CUSTOM";

    /** Edit request. */
    public static final String EDIT_REQUEST = "EDIT";

    /** Help request. */
    public static final String HELP_REQUEST = "HELP";

    /** Default URL for the action page. */
    private String defaultActionPage = null;

    /** Default URL for the custom page. */
    private String defaultCustomPage = null;

    /** Default URL for the edit page. */
    private String defaultEditPage = null;

    /** Default URL for the help page. */
    private String defaultHelpPage = null;

    /** Default URL for the view page. */
    private String defaultViewPage = null;

    /**
     * <p>
     * Context initialization parameter name for the lifecycle identifier of the
     * {@link Lifecycle}instance to be utilized.
     * </p>
     */
    private static final String LIFECYCLE_ID_ATTR = FacesServlet.LIFECYCLE_ID_ATTR;

    /**
     * <p>
     * The {@link Application}instance for this web application.
     * </p>
     */
    private Application application = null;

    /**
     * <p>
     * Factory for {@link FacesContext}instances.
     * </p>
     */
    private FacesContextFactory facesContextFactory = null;

    /**
     * <p>
     * The {@link Lifecycle}instance to use for request processing.
     * </p>
     */
    private Lifecycle lifecycle = null;

    /**
     * <p>
     * The <code>PortletConfig</code> instance for this portlet.
     * </p>
     */
    private PortletConfig portletConfig = null;

    /**
     * <p>
     * Release all resources acquired at startup time.
     * </p>
     */
    public void destroy()
    {
        if (log.isTraceEnabled())
        {
            log.trace("Begin FacesPortlet.destory() ");
        }
        application = null;
        facesContextFactory = null;
        lifecycle = null;
        portletConfig = null;
        if (log.isTraceEnabled())
        {
            log.trace("End FacesPortlet.destory() ");
        }

    }

    /**
     * <p>
     * Acquire the factory instance we will require.
     * </p>
     * 
     * @exception PortletException if, for any reason, the startp of this Faces
     *                application failed. This includes errors in the config
     *                file that is parsed before or during the processing of
     *                this <code>init()</code> method.
     */
    public void init(PortletConfig portletConfig) throws PortletException
    {

        if (log.isTraceEnabled())
        {
            log.trace("Begin FacesPortlet.init() ");
        }

        super.init(portletConfig);

        // Save our PortletConfig instance
        this.portletConfig = portletConfig;
        this.defaultViewPage = portletConfig.getInitParameter(PARAM_VIEW_PAGE);
        if (null == this.defaultViewPage)
        {
            // A Faces Portlet is required to have at least the
            // defaultViewPage
            // defined!
            throw new PortletException("Portlet " + portletConfig.getPortletName()
                    + " is incorrectly configured. No default View page is defined.");
        }
        if (null == this.defaultActionPage)
        {
            this.defaultActionPage = this.defaultViewPage;
        }
        if (null == this.defaultCustomPage)
        {
            this.defaultCustomPage = this.defaultViewPage;
        }
        if (null == this.defaultHelpPage)
        {
            this.defaultHelpPage = this.defaultViewPage;
        }
        if (null == this.defaultEditPage)
        {
            this.defaultEditPage = this.defaultViewPage;
        }
        if (log.isTraceEnabled())
        {
            log.trace("End FacesPortlet.init() ");
        }
    }

    /**
     * @see javax.portlet.GenericPortlet#doEdit(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        process(request, response, defaultEditPage, FacesPortlet.EDIT_REQUEST);
    }

    /**
     * @see javax.portlet.GenericPortlet#doHelp(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doHelp(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        process(request, response, defaultHelpPage, FacesPortlet.HELP_REQUEST);
    }

    /**
     * @param request The {@link RenderRequest}.
     * @param response The {@link RenderResponse}.
     * @throws PortletException Throws a {@link PortletException}.
     * @throws IOException Throws a {@link IOException}.
     */
    public void doCustom(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        process(request, response, defaultCustomPage, FacesPortlet.CUSTOM_REQUEST);
    }

    /**
     * @see javax.portlet.GenericPortlet#doView(javax.portlet.RenderRequest,
     *      javax.portlet.RenderResponse)
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        process(request, response, defaultViewPage, FacesPortlet.VIEW_REQUEST);
    }

    /**
     * @see javax.portlet.Portlet#processAction(javax.portlet.ActionRequest,
     *      javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        process(request, response, defaultActionPage, FacesPortlet.ACTION_REQUEST);
    }

    /**
     * <p>
     * Gets the {@link FacesContextFactory}.
     * </p>
     * 
     * @return The {@link FacesContextFactory}.
     * @throws PortletException Throws a {@link PortletException}.
     */
    public FacesContextFactory getFacesContextFactory() throws PortletException
    {
        if (facesContextFactory != null)
        {
            return facesContextFactory;
        }
        try
        {
            facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
            if (log.isTraceEnabled())
            {
                log.trace("Retrieved facesContextFactory " + facesContextFactory);
            }
        }
        catch (FacesException e)
        {
            Throwable rootCause = e.getCause();
            if (rootCause == null)
            {
                throw e;
            }
            else
            {
                throw new PortletException(e.getMessage(), rootCause);
            }
        }
        return facesContextFactory;
    }

    /**
     * <p>
     * Get the faces life cycle.
     * </p>
     * 
     * @return The {@link Lifecycle}.
     * @throws PortletException Throws a {@link PortletException}.
     */
    public Lifecycle getLifecycle() throws PortletException
    {
        if (lifecycle != null)
        {
            return lifecycle;
        }
        try
        {
            LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder
                    .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            if (log.isTraceEnabled())
            {
                log.trace("Retrieved lifecycleFactory " + lifecycleFactory);
            }
            String lifecycleId = portletConfig.getPortletContext().getInitParameter(LIFECYCLE_ID_ATTR);
            if (log.isDebugEnabled())
            {
                log.debug("lifecycleId " + lifecycleId);
            }
            if (lifecycleId == null)
            {
                lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
            }
            lifecycle = lifecycleFactory.getLifecycle(lifecycleId);
            if (log.isTraceEnabled())
            {
                log.trace("Retrieved lifecycle from lifecycleFactory " + lifecycle);
            }
        }
        catch (FacesException e)
        {
            Throwable rootCause = e.getCause();
            if (rootCause == null)
            {
                throw e;
            }
            else
            {
                throw new PortletException(e.getMessage(), rootCause);
            }
        }
        return lifecycle;
    }

    /**
     * <p>
     * Processes the request.
     * </p>
     * 
     * @param request The {@link PortletRequest}.
     * @param response The {@link PortletResponse}.
     * @param defaultPage The default page.
     * @param requestType The request type.
     * @throws PortletException Throws a {@link PortletException}.
     * @throws IOException Throws an {@link IOException}.
     */
    private void process(PortletRequest request, PortletResponse response, String defaultPage, String requestType)
            throws PortletException, IOException
    {
        boolean actionRequest = (request instanceof ActionRequest);
        boolean renderRequest = (request instanceof RenderRequest);
        if (actionRequest)
        {
            log.trace("Begin FacesPortlet.processAction()");
        }

        // Acquire the FacesContext instance for this request
        FacesContext context = getFacesContextFactory().getFacesContext(portletConfig.getPortletContext(), request,
                response, getLifecycle());

        setDefaultView(context, defaultPage);
        if (log.isTraceEnabled())
        {
            log.trace("Begin Executing phases");
        }

        // Execute the pre-render request processing lifecycle for this request
        try
        {
            if (actionRequest)
            {
                getLifecycle().execute(context);
                if (log.isTraceEnabled())
                {
                    log.trace("End Executing phases");
                }
                setDefaultView(context, defaultPage);
            }
            else if (renderRequest)
            {
                getLifecycle().render(context);
                if (log.isTraceEnabled())
                {
                    log.trace("End executing RenderResponse phase ");
                }
            }
            else
            {
                throw new PortletException("Request must be of type ActionRequest or RenderRequest");
            }
        }
        catch (FacesException e)
        {
            Throwable t = ((FacesException) e).getCause();
            if (t == null)
            {
                throw new PortletException(e.getMessage(), e);
            }
            else
            {
                if (t instanceof PortletException)
                {
                    throw ((PortletException) t);
                }
                else if (t instanceof IOException)
                {
                    throw ((IOException) t);
                }
                else
                {
                    throw new PortletException(t.getMessage(), t);
                }
            }
        }
        finally
        {
            // Release the FacesContext instance for this request
            context.release();
        }
        if (log.isTraceEnabled())
        {
            log.trace("End FacesPortlet.process()");
        }
    }

    /**
     * <p>
     * Set the view identifier to a default page.
     * </p>
     * 
     * @param context The {@link FacesContext}for the current request.
     * @param defaultView The default view identifier.
     */
    private void setDefaultView(FacesContext facesContext, String defaultView)
    {
        // Need to be able to transport viewId between actionRequest and
        // renderRequest.
        // If actionRequest, the view id is obtained from the navigation, we
        // need to be able to keep that
        // value and not have it overwritten by the default view id. Putting
        // that value in the portletRequest does not
        // work. Need to use actionResponse.setRenderParameter...
        PortletRequest portletRequest = (PortletRequest) facesContext.getExternalContext().getRequest();
        if (portletRequest instanceof ActionRequest)
        {
            if ((null != facesContext.getViewRoot()) && (null != facesContext.getViewRoot().getViewId()))
            {
                ((ActionResponse) facesContext.getExternalContext().getResponse()).setRenderParameter(
                        FacesPortlet.VIEW_ID, facesContext.getViewRoot().getViewId());
            }
        }
        if ((portletRequest instanceof RenderRequest) && (null != portletRequest.getParameter(FacesPortlet.VIEW_ID)))
        {
            defaultView = portletRequest.getParameter(FacesPortlet.VIEW_ID);
        }
        if ((null != portletRequest.getAttribute(FacesPortlet.VIEW_ID))
                && (!portletRequest.getAttribute(FacesPortlet.VIEW_ID).equals(defaultView)))
        {
            return;
        }
        if (facesContext.getViewRoot() == null)
        {
            facesContext.setViewRoot(new UIViewRoot());
            if (log.isDebugEnabled())
            {
                log.debug("Created new ViewRoot" + facesContext.getViewRoot());
            }
        }
        if (null == facesContext.getViewRoot().getViewId())
        {
            facesContext.getViewRoot().setViewId(defaultView);
        }
        String viewId = facesContext.getViewRoot().getViewId().replaceAll(".jsp", ".jsf");
        portletRequest.setAttribute(FacesPortlet.VIEW_ID, viewId);
        if (log.isDebugEnabled())
        {
            log.debug("Set " + FacesPortlet.VIEW_ID + " to " + viewId);
        }
        facesContext.getViewRoot().setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
    }
}