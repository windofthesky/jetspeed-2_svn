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
import java.security.Principal;
import java.util.ArrayList;
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
import javax.security.auth.Subject;
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
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.container.window.FailedToRetrievePortletWindow;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.locator.LocatorDescriptor;
import org.apache.jetspeed.locator.TemplateDescriptor;
import org.apache.jetspeed.locator.TemplateLocator;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.ArgUtil;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.window.PortletWindow;
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
    protected static final String COLUMNS_ATTR = "columns";
    protected static final String COLUMN_SIZES = "columnSizes";

    protected RenderRequest renderRequest;

    protected RenderResponse renderResponse;

    protected PortletConfig portletConfig;

    protected Writer templateWriter;

    protected static final String POWER_TOOL_SESSION_ACTIONS = "org.apache.jetspeed.powertool.actions";

    protected static final Log log = LogFactory.getLog(JetspeedPowerTool.class);

    protected CapabilityMap capabilityMap;
    protected Locale locale;
    protected LocatorDescriptor templateLocatorDescriptor;
    protected TemplateLocator templateLocator;
    protected PortletEntityAccessComponent entityAccess;
    protected TemplateLocator decorationLocator;
    protected LocatorDescriptor decorationLocatorDescriptor;
    protected PortletWindowAccessor windowAccess;

    protected RequestContext requestContext;
    protected Context velocityContext;
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
    public ContentFragment getCurrentFragment()
    {
        checkState();       
       return (ContentFragment) renderRequest.getAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE);
    }

    /**
     * 
     * @param f
     */
    public void setCurrentFragment( ContentFragment f )
    {
        checkState();
        renderRequest.setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, f);
        
    }

    public void setCurrentLayout()
    {
        checkState();

        ContentFragment f = (ContentFragment) getRequestContext().getRequest().getAttribute(LAYOUT_ATTR);
        renderRequest.setAttribute(LAYOUT_ATTR, f);
    }

    /**
     * 
     * @return
     */
    public ContentFragment getCurrentLayout()
    {
        checkState();
        return (ContentFragment) renderRequest.getAttribute(LAYOUT_ATTR);
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
    public PortletEntity getPortletEntity( ContentFragment f ) throws Exception
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
    public boolean isHidden( ContentFragment f )
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
    public String  decorateAndInclude( ContentFragment f ) throws Exception
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
    protected String decorateAndIncludePortlet( ContentFragment f ) throws Exception
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
    protected void handleError( Exception e, String msg, ContentFragment fragment )
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
     * page) has its own collection of actionAccess flags associated with it.
     * 
     * @return A list of actions available to the current window, filtered by
     *              securty access and current state.
     * @throws Exception
     */
    public List getDecoratorActions()
    {
        return getDecoratorActions(false);
    }

    /**
     * Gets the list of decorator actions for a page. Each layout fragment on a
     * page has its own collection of actionAccess flags associated with it.
     * 
     * @return A list of actions available to the current window, filtered by
     *              securty access and current state.
     * @throws Exception
     */
    public List getPageDecoratorActions() throws Exception
    {
        return getDecoratorActions(true);
    }

    protected List getDecoratorActions(boolean layout)
    {
        try
        {
            String key = getPage().getId();
            boolean anonymous = !getLoggedOn();
            PageActionAccess pageActionAccess = null;            
            
            synchronized (getRequestContext().getRequest().getSession())
            {
                Map sessionActions = (Map) getRequestContext().getSessionAttribute(POWER_TOOL_SESSION_ACTIONS);
                if ( sessionActions == null )
                {
                    sessionActions = new HashMap();
                    getRequestContext().setSessionAttribute(POWER_TOOL_SESSION_ACTIONS, sessionActions);
                }
                else
                {
                    pageActionAccess = (PageActionAccess)sessionActions.get(key);
                }
                if ( pageActionAccess == null )
                {
                    pageActionAccess = new PageActionAccess(anonymous, getPage());
                    sessionActions.put(key, pageActionAccess);
                }
                else
                {
                    pageActionAccess.checkReset(getLoggedOn(), getPage());
                }
            }
            
            PortletDefinitionComposite portlet = (PortletDefinitionComposite) getCurrentPortletEntity()
                    .getPortletDefinition();
            if (null == portlet)
            {
                return Collections.EMPTY_LIST; // allow nothing
            }

            List actions = new ArrayList();

            PortletMode mode = getPortletMode();
            WindowState state = getWindowState();
            
            ContentTypeSet content = portlet.getContentTypeSet();
            ContentFragment fragment = getCurrentFragment();
            String fragmentId = fragment.getId();
            String portletName = portlet.getUniqueName();
            PortletWindow window = windowAccess.getPortletWindow(fragment);
            String resourceBase = getPageBasePath();

            if ( !layout )
            {
                if (state.equals(WindowState.NORMAL))
                {
                    if ( pageActionAccess.checkWindowState(fragmentId, portletName, WindowState.MINIMIZED))
                    {
                        actions.add(createWindowStateAction(window, JetspeedActions.MINIMIZE, WindowState.MINIMIZED, resourceBase));
                    }
                    if ( pageActionAccess.checkWindowState(fragmentId, portletName, WindowState.MAXIMIZED))
                    {
                        actions.add(createWindowStateAction(window, JetspeedActions.MAXIMIZE, WindowState.MAXIMIZED, resourceBase));
                    }
                }
                else if (state.equals(WindowState.MAXIMIZED))
                {
                    if ( pageActionAccess.checkWindowState(fragmentId, portletName, WindowState.MINIMIZED))
                    {
                        actions.add(createWindowStateAction(window, JetspeedActions.MINIMIZE, WindowState.MINIMIZED, resourceBase));
                    }
                    if ( pageActionAccess.checkWindowState(fragmentId, portletName, JetspeedActions.RESTORED))
                    {
                        actions.add(createWindowStateAction(window, JetspeedActions.RESTORE, WindowState.NORMAL, resourceBase));
                    }
                }
                else
                // minimized
                {
                    if ( pageActionAccess.checkWindowState(fragmentId, portletName, WindowState.MAXIMIZED))
                    {
                        actions.add(createWindowStateAction(window, JetspeedActions.MAXIMIZE, WindowState.MAXIMIZED, resourceBase));
                    }
                    if ( pageActionAccess.checkWindowState(fragmentId, portletName, JetspeedActions.RESTORED))
                    {
                        actions.add(createWindowStateAction(window, JetspeedActions.RESTORE, WindowState.NORMAL, resourceBase));
                    }
                }
            }
            
            if ( !layout || pageActionAccess.isEditAllowed() )
            {
                if (mode.equals(PortletMode.VIEW))
                {
                    if (content.supportsPortletMode(PortletMode.EDIT) && pageActionAccess.isEditAllowed() && 
                            pageActionAccess.checkPortletMode(fragmentId, portletName, PortletMode.EDIT))
                    {
                        actions.add(createPortletModeAction(window, JetspeedActions.EDIT, PortletMode.EDIT, resourceBase));
                    }
                    if (content.supportsPortletMode(PortletMode.HELP) && 
                            pageActionAccess.checkPortletMode(fragmentId, portletName, PortletMode.HELP))
                    {
                        actions.add(createPortletModeAction(window, JetspeedActions.HELP, PortletMode.HELP, resourceBase));
                    }
                }
                else if (mode.equals(PortletMode.EDIT))
                {
                    if (pageActionAccess.checkPortletMode(fragmentId, portletName, PortletMode.VIEW))
                    {
                        actions.add(createPortletModeAction(window, JetspeedActions.VIEW, PortletMode.VIEW, resourceBase));
                    }
                    if (content.supportsPortletMode(PortletMode.HELP) && 
                            pageActionAccess.checkPortletMode(fragmentId, portletName, PortletMode.HELP))
                    {
                        actions.add(createPortletModeAction(window, JetspeedActions.HELP, PortletMode.HELP, resourceBase));
                    }
                }
                else
                // help
                {
                    if (pageActionAccess.checkPortletMode(fragmentId, portletName, PortletMode.VIEW))
                    {
                        actions.add(createPortletModeAction(window, JetspeedActions.VIEW, PortletMode.VIEW, resourceBase));
                    }
                    if (content.supportsPortletMode(PortletMode.EDIT) && pageActionAccess.isEditAllowed() && 
                            pageActionAccess.checkPortletMode(fragmentId, portletName, PortletMode.EDIT))
                    {
                        actions.add(createPortletModeAction(window, JetspeedActions.EDIT, PortletMode.EDIT, resourceBase));
                    }
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

    protected DecoratorAction createDecoratorAction( String resourceBase, String actionName )
    {
        // TODO: HARD-CODED .gif link
        String link = getRequestContext().getResponse().encodeURL(resourceBase+"/content/images/"+actionName+".gif");
        return new DecoratorAction(actionName, actionName, link); 
    }
    
    /**
     * Creates a Decorator PortletMode Action to be added to the list of actions
     * decorating a portlet.
     */
    protected DecoratorAction createPortletModeAction( PortletWindow window, String actionName, PortletMode mode, String resourceBase )
    {
        DecoratorAction action = createDecoratorAction(resourceBase, actionName);
        PortalURL portalURL = getRequestContext().getPortalURL(); 
        action.setAction(portalURL.createPortletURL(window, mode, null, portalURL.isSecure()).toString());
        return action;
    }

    /**
     * Creates a Decorator WindowState Action to be added to the list of actions
     * decorating a portlet.
     */
    protected DecoratorAction createWindowStateAction( PortletWindow window, String actionName, WindowState state, String resourceBase )
    {
        DecoratorAction action = createDecoratorAction(resourceBase, actionName);
        PortalURL portalURL = getRequestContext().getPortalURL(); 
        action.setAction(portalURL.createPortletURL(window, null, state, portalURL.isSecure()).toString());
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
    public String getTitle( PortletEntity entity, ContentFragment f )
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

    public Subject getSubject()
    {
        return requestContext.getSubject();
    }
    
    public boolean getLoggedOn()
    {
        Principal principal = requestContext.getRequest().getUserPrincipal();
        return (principal != null);
    }
    
    public String getPageBasePath()
    {
        return getRequestContext().getPortalURL().getPageBasePath();
    }
    
}
