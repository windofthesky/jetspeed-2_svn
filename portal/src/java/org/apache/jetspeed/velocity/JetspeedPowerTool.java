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
package org.apache.jetspeed.velocity;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.security.AccessControlException;
import java.security.AccessController;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletentity.PortletEntityNotGeneratedException;
import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.window.FailedToRetrievePortletWindow;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.locator.LocatorDescriptor;
import org.apache.jetspeed.locator.TemplateDescriptor;
import org.apache.jetspeed.locator.TemplateLocator;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.PortletPermission;
import org.apache.jetspeed.services.information.PortletURLProviderImpl;
import org.apache.jetspeed.util.ArgUtil;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.velocity.context.Context;

/**
 * <p>
 * JetspeedPowerTool
 * </p>
 * <p>
 * The JetspeedPowerTool is meant to be used by template designers to build
 * templates for internal Jetspeed portlet applications. It hides the
 * implementation details of the more common template actions so that future
 * changes to said implementation have minimal effect on template.
 * </p>
 * <p>
 * Where applicable, methods have been marked with a <strong>BEST PRATICES
 * </strong> meaning that this method should be used instead the synonymous code
 * listed within the method docuementation.
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class JetspeedPowerTool
{
    
    protected static final String PORTLET_CONFIG_ATTR = "javax.portlet.config";
    protected static final String RENDER_RESPONSE_ATTR = "javax.portlet.response";
    protected static final String RENDER_REQUEST_ATTR = "javax.portlet.request";
    private static final String COLUMNS_ATTR = "columns";
    private static final String COLUMN_SIZES = "columnSizes";

    private RenderRequest renderRequest;

    private RenderResponse renderResponse;

    private PortletConfig portletConfig;

    private Writer templateWriter;

    private static final String POWER_TOOL_SESSION_ACTIONS = "org.apache.jetspeed.powertool.actions";

    private static final Log log = LogFactory.getLog(JetspeedPowerTool.class);

    private CapabilityMap capabilityMap;
    private Locale locale;
    private LocatorDescriptor templateLocatorDescriptor;
    private TemplateLocator templateLocator;
    private PortletEntityAccessComponent entityAccess;
    private TemplateLocator decorationLocator;
    private LocatorDescriptor decorationLocatorDescriptor;
    private PortletWindowAccessor windowAccess;

    private RequestContext requestContext;
    private Context velocityContext;
    public static final String GENERIC_TEMPLATE_TYPE = "generic";
    public static final String FRAGMENT_PROCESSING_ERROR_PREFIX = "fragment.processing.error.";
    public static final String FRAGMENT_ATTR = "fragment";
    public static final String LAYOUT_ATTR = "layout";
    public static final String HIDDEN = "hidden";
    public static final String LAYOUT_TEMPLATE_TYPE = "layout";
    public static final String DECORATOR_TYPE = "decorator";
    public JetspeedPowerTool( RequestContext requestContext ) throws Exception
    {
        HttpServletRequest request = requestContext.getRequest();
        this.requestContext = requestContext;
        windowAccess = (PortletWindowAccessor) getComponent(PortletWindowAccessor.class.getName());
        entityAccess = (PortletEntityAccessComponent) getComponent(PortletEntityAccessComponent.class.getName());
        renderRequest = (RenderRequest) request.getAttribute(RENDER_REQUEST_ATTR);
        renderResponse = (RenderResponse) request.getAttribute(RENDER_RESPONSE_ATTR);
        portletConfig = (PortletConfig) request.getAttribute(PORTLET_CONFIG_ATTR);
        
        templateLocator = (TemplateLocator) getComponent("TemplateLocator");
        decorationLocator = (TemplateLocator) getComponent("DecorationLocator");
        // By using null, we create a re-useable locator
        capabilityMap = requestContext.getCapabilityMap();
        locale = requestContext.getLocale();

        templateLocatorDescriptor = templateLocator.createLocatorDescriptor(null);
        templateLocatorDescriptor.setMediaType(capabilityMap.getPreferredMediaType().getName());
        templateLocatorDescriptor.setCountry(locale.getCountry());
        templateLocatorDescriptor.setLanguage(locale.getLanguage());

        decorationLocatorDescriptor = decorationLocator.createLocatorDescriptor(null);
        decorationLocatorDescriptor.setMediaType(capabilityMap.getPreferredMediaType().getName());
        decorationLocatorDescriptor.setCountry(locale.getCountry());
        decorationLocatorDescriptor.setLanguage(locale.getLanguage());
    }


    /**
     * <p>
     * getRequestContext
     * </p>
     * 
     * @return
     */
    protected final RequestContext getRequestContext()
    {
        return requestContext;
    }


    /**
     * Gets the window state for the current portlet window (fragment)
     * 
     * @return The window state for the current window
     * @throws Exception
     */
    public WindowState getWindowState() throws Exception
    {
        try
        {
            NavigationalState nav = getRequestContext().getPortalURL().getNavigationalState();
            return nav.getState(windowAccess.getPortletWindow(getCurrentFragment()));
        }
        catch (Exception e)
        {
            handleError(e, e.toString(), getCurrentFragment());
            return null;
        }
    }

    /**
     * Gets the portlet mode for a current portlet window (fragment)
     * 
     * @return The portlet mode of the current window
     * @throws Exception
     */
    public PortletMode getPortletMode() throws Exception
    {

        NavigationalState nav = getRequestContext().getPortalURL().getNavigationalState();
        try
        {
            return nav.getMode(windowAccess.getPortletWindow(getCurrentFragment()));
        }
        catch (FailedToRetrievePortletWindow e)
        {
            handleError(e, e.toString(), getCurrentFragment());
            return null;
        }
    }

    /**
     * 
     * @return
     */
    public Fragment getCurrentFragment()
    {
        checkState();       
       return (Fragment) renderRequest.getAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE);
    }

    /**
     * 
     * @param f
     */
    public void setCurrentFragment( Fragment f )
    {
        checkState();
        renderRequest.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, f);
        
    }

    public void setCurrentLayout()
    {
        checkState();

        Fragment f = (Fragment) getRequestContext().getRequest().getAttribute(LAYOUT_ATTR);
        renderRequest.setAttribute(LAYOUT_ATTR, f);
    }

    /**
     * 
     * @return
     */
    public Fragment getCurrentLayout()
    {
        checkState();
        return (Fragment) renderRequest.getAttribute(LAYOUT_ATTR);
    }

    /**
     * 
     * @return
     */
    public Page getPage()
    {
        checkState();
        // return (Page) renderRequest.getAttribute(PortalReservedParameters.PAGE_ATTRIBUTE_KEY);
        return requestContext.getPage();
    }

    /**
     * 
     * @return
     */
    public List[] getColumns()
    {
        checkState();
        return (List[]) renderRequest.getAttribute(COLUMNS_ATTR);
    }

    public List getColumnSizes()
    {
        checkState();
        Object o = renderRequest.getAttribute(COLUMN_SIZES);
        if (o == null)
            return null;
        return (List) renderRequest.getAttribute(COLUMN_SIZES);
    }
    
    /**
     * 
     * @return
     * @throws Exception
     */
    public PortletEntity getCurrentPortletEntity() throws Exception
    {
        try
        {
            return windowAccess.getPortletWindow(getCurrentFragment()).getPortletEntity();
        }
        catch (Exception e)
        {
            handleError(e, "JetspeedPowerTool failed to retreive the current PortletEntity.  " + e.toString(),
                    getCurrentFragment());
            return null;
        }
    }

    /**
     * 
     * @param f
     *                  Fragment whose <code>PortletEntity</code> we want to
     *                  retreive.
     * @return The PortletEntity represented by the current fragment.
     * @throws Exception
     */
    public PortletEntity getPortletEntity( Fragment f ) throws Exception
    {
        PortletEntity portletEntity = entityAccess.getPortletEntityForFragment(f);
        if (portletEntity == null)
        {
            try
            {
                portletEntity = entityAccess.generateEntityFromFragment(f);
                entityAccess.storePortletEntity(portletEntity);
            }
            catch (PortletEntityNotGeneratedException e)
            {
                String msg = "JetspeedPowerTool failed to retreive a PortletEntity for Fragment " + f.getId() + ".  "
                        + e.toString();
                handleError(e, msg, f);
            }
            catch (PortletEntityNotStoredException e)
            {
                String msg = "JetspeedPowerTool failed to store a PortletEntity for Fragment " + f.getId() + ".  "
                        + e.toString();
                handleError(e, msg, f);
            }
        }
        return portletEntity;
    }

    /**
     * Checks the the visibilty of this fragment with respect to the current
     * RenderReqeust.
     * 
     * @param f
     *                  Fragment
     * @return whether or not the Fragment in question should be considered
     *              visible during rendering.
     */
    public boolean isHidden( Fragment f )
    {
        checkState();
        if (f == null)
        {
            throw new IllegalArgumentException("Fragment cannot be null for isHidden(Fragment)");
        }
        return f.getState() != null && f.getState().equals(HIDDEN);
    }

    /**
     * Retreives a template using Jetspeed's
     * 
     * @see org.apache.jetspeed.locator.TemplateLocator
     * 
     * 
     * @param path
     *                  Expected to the template. This may actually be changed by the
     *                  TL service based the capability and localization information
     *                  provided by the client.
     * @param templateType
     *                  Type off template we are interested in.
     * @return Template object containng the pertinent information required to
     *              inlcude the request template path in the current response
     * @throws TemplateLocatorException
     *                   if the <code>path</code> does not exist.
     */
    public TemplateDescriptor getTemplate( String path, String templateType ) throws TemplateLocatorException
    {
        checkState();
        return getTemplate(path, templateType, templateLocator, templateLocatorDescriptor);
    }

    public Configuration getTypeConfiguration( String type, String name, String location ) throws Exception
    {
        ArgUtil.assertNotNull(String.class, type, this, "getTypeConfiguration(String type, String name)");
        ArgUtil.assertNotNull(String.class, name, this, "getTypeConfiguration(String type, String name)");
        try
        {
            TemplateDescriptor locator = null;
            if (location.equals("templates"))
            {
                locator = getTemplate(name + "/" + type + ".properties", type);
            }
            else if (location.equals("decorations"))
            {
                locator = getDecoration(name + "/decorator.properties", type);
            }
            else
            {
                throw new IllegalArgumentException("Location type " + location
                        + " is not supported by getTypeConfiguration().");
            }
            return new PropertiesConfiguration(locator.getAbsolutePath());
        }
        catch (TemplateLocatorException e)
        {
            log.warn(e.toString(), e);
            return null;
        }
    }

    public TemplateDescriptor getDecoration( String path, String templateType ) throws TemplateLocatorException
    {
        checkState();
        return getTemplate(path, templateType, decorationLocator, decorationLocatorDescriptor);
    }

    public String  includeTemplate( String template, String templateType ) throws IOException
    {
        checkState();
        try
        {
            TemplateDescriptor useLocator = getTemplate(template, templateType);
            return useLocator.getAppRelativePath();
        }
        catch (Exception e)
        {
            PrintWriter directError = new PrintWriter(renderResponse.getWriter());
            directError.write("Error occured process includeTemplate(): " + e.toString() + "\n\n");
            e.printStackTrace(directError);
            directError.close();
            return null;
        }
    }

    public String  includeDecoration( String template, String templateType ) throws IOException
    {
        checkState();
        try
        {
            return getDecoration(template, templateType).getAppRelativePath();        
        }
        catch (Exception e)
        {
            PrintWriter directError = new PrintWriter(renderResponse.getWriter());
            directError.write("Error occured process includeDecoration(): " + e.toString() + "\n\n");
            e.printStackTrace(directError);
            directError.close();
            return null;
        }
    }



    /**
     * <p>
     * Decorate and include fragment content.
     * </p>
     * 
     * @param f
     *                  Fragment to include and decorate
     * @throws Exception
     * @return String path to the decorator.
     */
    public String  decorateAndInclude( Fragment f ) throws Exception
    {
        // makes sure that any previous content has been written to
        // preserve natural HTML rendering order

         setCurrentFragment(f);
         setCurrentLayout();

        // include decorated layout or portlet fragment
        try
        {
            String fragmentType = f.getType();
            return decorateAndIncludePortlet(f);         
        }
        catch (Exception e)
        {
            renderResponse.getWriter().write(e.toString());
            return null;
        }

    }
    
    /**
     * <p>
     * The decorator template itself is responsible for including the content of
     * the target Fragment which is easily acheived like so: <br />
     * in Velocity:
     * 
     * <pre>
     *   <code>
     * $jetspeed.include($jetspeed.currentFragment)
     * </code>
     * </pre>
     * 
     * In JSP:
     * 
     * <pre>
     *   <code>
     * 
     *             &lt;% 
     *              JetspeedPowerTool jetspeed = new JetspeedPowerTool(renderRequest, renderResponse, portletConfig);
     *              jetspeed.include(jetspeed.getCurrentFragment());
     *             %&gt;
     *  
     * </code>
     * </pre>
     * 
     * 
     * @param f
     *                  Portlet fragment to "decorate"
     * @throws Exception
     */
    private String decorateAndIncludePortlet( Fragment f ) throws Exception
    {
        // make sure that any previous content has been written to
        // preserve natural HTML rendering order

        // get fragment decorator; fallback to the default decorator
        // if the current fragment is not specifically decorated
        String fragmentType = f.getType();
        String decorator = f.getDecorator();
        if (decorator == null)
        {
            decorator = getPage().getDefaultDecorator(fragmentType);
        }

        // get fragment properties for fragmentType or generic
        TemplateDescriptor propsTemp = getTemplate(decorator + "/" + DECORATOR_TYPE + ".properties", fragmentType,
                decorationLocator, decorationLocatorDescriptor);
        if (propsTemp == null)
        {
            fragmentType = GENERIC_TEMPLATE_TYPE;
            propsTemp = getTemplate(decorator + "/" + DECORATOR_TYPE + ".properties", fragmentType, decorationLocator,
                    decorationLocatorDescriptor);
        }

        // get decorator template
        Configuration decoConf = new PropertiesConfiguration(propsTemp.getAbsolutePath());
        String ext = decoConf.getString("template.extension");
        String decoratorPath = decorator + "/" + DECORATOR_TYPE + ext;
        TemplateDescriptor template = null;
        try
        {
            template = getDecoration(decoratorPath, fragmentType);
        }
        catch (TemplateLocatorException e)
        {
            String parent = decoConf.getString("extends");
            if (parent != null)
            {
                template = getDecoration(parent + "/" + DECORATOR_TYPE + ext, fragmentType);
            }
        }

        return  template.getAppRelativePath();
    }   
    
    

    /**
     * 
     * 
     * @throws java.lang.IllegalStateException
     *                   if the <code>PortletConfig</code>,
     *                   <code>RenderRequest</code> or <code>RenderReponse</code>
     *                   is null.
     */
    protected void checkState()
    {
        if (portletConfig == null || renderRequest == null || renderResponse == null)
        {
            throw new IllegalStateException("JetspeedPowerTool has not been properly initialized.  " + ""
                    + "The JetspeedPowerTool generally only usuable during the rendering phase of  "
                    + "internal portlet applications.");
        }
    }

    protected TemplateDescriptor getTemplate( String path, String templateType, TemplateLocator locator,
            LocatorDescriptor descriptor ) throws TemplateLocatorException
    {
        checkState();
        if (templateType == null)
        {
            templateType = GENERIC_TEMPLATE_TYPE;
        }
        try
        {

            descriptor.setName(path);
            descriptor.setType(templateType);

            TemplateDescriptor template = locator.locateTemplate(descriptor);
            return template;
        }
        catch (TemplateLocatorException e)
        {
            log.error("Unable to locate template: " + path, e);
            System.out.println("Unable to locate template: " + path);
            throw e;
        }
    }

    /**
     * <p>
     * handleError
     * </p>
     * 
     * @param e
     * @param msg
     */
    protected void handleError( Exception e, String msg, Fragment fragment )
    {
        log.error(msg, e);

        Set exceptions = (Set) renderRequest.getAttribute(FRAGMENT_PROCESSING_ERROR_PREFIX + fragment.getId());
        if (exceptions == null)
        {
            exceptions = new HashSet();
            renderRequest.setAttribute(FRAGMENT_PROCESSING_ERROR_PREFIX + fragment.getId(), exceptions);
        }
        exceptions.add(e);

    }

    /**
     * Gets the list of decorator actions for a window. Each window (on each
     * page) has its own collection of actions associated with it. The creation
     * of the decorator action list per window will only be called once per
     * session. This optimization is to avoid the expensive operation of
     * security checks and action object creation and logic on a per request
     * basis.
     * 
     * @return A list of actions available to the current window, filtered by
     *              securty access and current state.
     * @throws Exception
     */
    public List getDecoratorActions()
    {
        try
        {

            String key = getPage().getId() + ":" + this.getCurrentFragment().getId();
            Map sessionActions = (Map) getRequestContext().getSessionAttribute(POWER_TOOL_SESSION_ACTIONS);
            if (null == sessionActions)
            {
                sessionActions = new HashMap();
                getRequestContext().setSessionAttribute(POWER_TOOL_SESSION_ACTIONS, sessionActions);
            }
            PortletWindowActionState actionState = (PortletWindowActionState) sessionActions.get(key);

            String state = getWindowState().toString();
            String mode = getPortletMode().toString();

            if (null == actionState)
            {
                actionState = new PortletWindowActionState(state, mode);
                sessionActions.put(key, actionState);
            }
            else
            {
                // check to see if state or mode has changed
                if (actionState.getWindowState().equals(state))
                {
                    if (actionState.getPortletMode().equals(mode))
                    {
                        // nothing has changed
                        return actionState.getActions();
                    }
                    else
                    {
                        actionState.setPortletMode(mode);
                    }
                }
                else
                {
                    actionState.setWindowState(state);
                }
                // something has changed, rebuild the list
            }

            List actions = actionState.getActions();
            actions.clear();

            PortletDefinitionComposite portlet = (PortletDefinitionComposite) getCurrentPortletEntity()
                    .getPortletDefinition();
            if (null == portlet)
            {
                return actions; // allow nothing
            }

            ContentTypeSet content = portlet.getContentTypeSet();

            if (state.equals(WindowState.NORMAL.toString()))
            {
                createAction(actions, JetspeedActions.INDEX_MINIMIZE, portlet);
                createAction(actions, JetspeedActions.INDEX_MAXIMIZE, portlet);
            }
            else if (state.equals(WindowState.MAXIMIZED.toString()))
            {
                createAction(actions, JetspeedActions.INDEX_MINIMIZE, portlet);
                createAction(actions, JetspeedActions.INDEX_NORMAL, portlet);
            }
            else
            // minimized
            {
                createAction(actions, JetspeedActions.INDEX_MAXIMIZE, portlet);
                createAction(actions, JetspeedActions.INDEX_NORMAL, portlet);
            }

            if (mode.equals(PortletMode.VIEW.toString()))
            {
                if (content.supportsPortletMode(PortletMode.EDIT))
                {
                    createAction(actions, JetspeedActions.INDEX_EDIT, portlet);
                }
                if (content.supportsPortletMode(PortletMode.HELP))
                {
                    createAction(actions, JetspeedActions.INDEX_HELP, portlet);
                }
            }
            else if (mode.equals(PortletMode.EDIT.toString()))
            {
                createAction(actions, JetspeedActions.INDEX_VIEW, portlet);
                if (content.supportsPortletMode(PortletMode.HELP))
                {
                    createAction(actions, JetspeedActions.INDEX_HELP, portlet);
                }
            }
            else
            // help
            {
                createAction(actions, JetspeedActions.INDEX_VIEW, portlet);
                if (content.supportsPortletMode(PortletMode.EDIT))
                {
                    createAction(actions, JetspeedActions.INDEX_EDIT, portlet);
                }
            }
            return actions;
        }
        catch (Exception e)
        {
            log.warn("Unable to generate decortator actions: " + e.toString());
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Gets the list of decorator actions for a page. Each layout fragment on a
     * page has its own collection of actions associated with it. The creation
     * of the layout decorator action list per page will only be called once per
     * session. This optimization is to avoid the expensive operation of
     * security checks and action object creation and logic on a per request
     * basis.
     * 
     * @return A list of actions available to the current window, filtered by
     *              securty access and current state.
     * @throws Exception
     */
    public List getPageDecoratorActions() throws Exception
    {
        // check page access
        boolean readOnlyPageAccess = true;
        try
        {
            getPage().checkAccess(Page.EDIT_ACTION);
            readOnlyPageAccess = false;
        }
        catch (SecurityException se)
        {
        }

        // determine cached actions state key
        String key = "PAGE " + getPage().getId() + ":" + this.getCurrentFragment().getId() + ":"
                + (readOnlyPageAccess ? Page.VIEW_ACTION : Page.EDIT_ACTION);

        // get cached actions state

        Map sessionActions = (Map) getRequestContext().getSessionAttribute(POWER_TOOL_SESSION_ACTIONS);
        if (null == sessionActions)
        {
            sessionActions = new HashMap();
            getRequestContext().setSessionAttribute(POWER_TOOL_SESSION_ACTIONS, sessionActions);
        }
        PortletWindowActionState actionState = (PortletWindowActionState) sessionActions.get(key);

        String state = getWindowState().toString();
        String mode = getPortletMode().toString();

        if (null == actionState)
        {
            actionState = new PortletWindowActionState(state, mode);
            sessionActions.put(key, actionState);
        }
        else
        {
            if (actionState.getPortletMode().equals(mode))
            {
                // nothing has changed
                return actionState.getActions();
            }
            // something has changed, rebuild the list
            actionState.setPortletMode(mode);
        }

        List actions = actionState.getActions();
        actions.clear();

        // if there is no root fragment, return no actions
        PortletDefinitionComposite portlet = (PortletDefinitionComposite) getCurrentPortletEntity()
                .getPortletDefinition();
        if (null == portlet)
        {
            return actions;
        }

        // if the page is being read only accessed, return no actions
        if (readOnlyPageAccess)
        {
            return actions;
        }

        // generate standard page actions depending on
        // portlet capabilities
        ContentTypeSet content = portlet.getContentTypeSet();
        if (mode.equals(PortletMode.VIEW.toString()))
        {
            if (content.supportsPortletMode(PortletMode.EDIT))
            {
                createAction(actions, JetspeedActions.INDEX_EDIT, portlet);
            }
            if (content.supportsPortletMode(PortletMode.HELP))
            {
                createAction(actions, JetspeedActions.INDEX_HELP, portlet);
            }
        }
        else if (mode.equals(PortletMode.EDIT.toString()))
        {
            createAction(actions, JetspeedActions.INDEX_VIEW, portlet);
            if (content.supportsPortletMode(PortletMode.HELP))
            {
                createAction(actions, JetspeedActions.INDEX_HELP, portlet);
            }
        }
        else
        // help
        {
            createAction(actions, JetspeedActions.INDEX_VIEW, portlet);
            if (content.supportsPortletMode(PortletMode.EDIT))
            {
                createAction(actions, JetspeedActions.INDEX_EDIT, portlet);
            }
        }
        return actions;
    }

    /**
     * Determines whether the access request indicated by the specified
     * permission should be allowed or denied, based on the security policy
     * currently in effect.
     * 
     * @param resource
     *                  The fully qualified resource name of the portlet
     *                  (PA::portletName)
     * @param action
     *                  The action to perform on this resource (i.e. view, edit, help,
     *                  max, min...)
     * @return true if the action is allowed, false if it is not
     */
    private boolean checkPermission( String resource, String action )
    {
        try
        {
            // TODO: it may be better to check the PagePermission for the outer
            // most
            // fragment (i.e. the PSML page)
            AccessController.checkPermission(new PortletPermission(resource, action));
        }
        catch (AccessControlException e)
        {
            return false;
        }
        return true;
    }

    /**
     * Creates a Decorator Action link to be added to the list of actions
     * decorating a portlet.
     * 
     * @param actions
     * @param kind
     * @param resource
     * @return
     * @throws Exception
     */
    public DecoratorAction createAction( List actions, int actionId, PortletDefinitionComposite portlet )
            throws Exception
    {
        String resource = portlet.getUniqueName();
        String actionName = JetspeedActions.ACTIONS[actionId];
        if (checkPermission(resource, actionName)) // TODO:
                                                                          // should
                                                                          // be
                                                                          // !checkPermission
        {
            return null;
        }
        DecoratorAction action = new DecoratorAction(actionName, actionName, "content/images/" + actionName + ".gif"); // TODO:
                                                                                                                                                                                       // HARD-CODED
                                                                                                                                                                                       // .gif

        PortletEntity entity = getCurrentPortletEntity();

        PortletURLProviderImpl url = new PortletURLProviderImpl(getRequestContext(), windowAccess
                .getPortletWindow(getCurrentFragment()));
        switch (actionId)
        {
            case JetspeedActions.INDEX_MAXIMIZE :
                url.setWindowState(WindowState.MAXIMIZED);
                break;
            case JetspeedActions.INDEX_MINIMIZE :
                url.setWindowState(WindowState.MINIMIZED);
                break;
            case JetspeedActions.INDEX_NORMAL :
                url.setWindowState(WindowState.NORMAL);
                break;
            case JetspeedActions.INDEX_VIEW :
                url.setPortletMode(PortletMode.VIEW);
                break;
            case JetspeedActions.INDEX_EDIT :
                url.setPortletMode(PortletMode.EDIT);
                break;
            case JetspeedActions.INDEX_HELP :
                url.setPortletMode(PortletMode.HELP);
                break;
        }

        action.setAction(url.toString());
        actions.add(action);
        return action;

    }

    /**
     * 
     * <p>
     * getTitle
     * </p>
     * Returns the appropriate for the title based on locale prferences
     * 
     * @param entity
     * @return
     */
    public String getTitle( PortletEntity entity, Fragment f )
    {
        String title = null;

        if (f != null)
        {
            title = f.getTitle();
        }

        if(title == null)
        {
            title = getTitle(entity);
        }

        return title;
    }

    /**
     * 
     * <p>
     * getTitle
     * </p>
     * Returns the appropriate for the title based on locale prferences
     * 
     * @param entity
     * @return
     */
    public String getTitle( PortletEntity entity )
    {
        String title = null;
        if (entity != null && entity.getPortletDefinition() != null)
        {
            title = getRequestContext().getPreferedLanguage(entity.getPortletDefinition()).getTitle();
        }
        
        if (title == null && entity.getPortletDefinition() != null)
        {
            title = entity.getPortletDefinition().getName();
        }
        else if (title == null)
        {
            title = "Invalid portlet entity "+entity.getId();
        }
        return title;
    }

    public Object getComponent( String name )
    {
        return Jetspeed.getComponentManager().getComponent(name);
    }

    public String getAbsoluteUrl( String relativePath )
    {
        HttpServletRequest request = getRequestContext().getRequest();
        StringBuffer path = new StringBuffer();
        return path.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(
                request.getServerPort()).append(request.getContextPath()).append(request.getServletPath()).append(
                relativePath).toString();
    }

}
