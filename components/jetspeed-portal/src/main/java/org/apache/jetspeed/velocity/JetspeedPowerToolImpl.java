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
package org.apache.jetspeed.velocity;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.administration.PortalConfigurationConstants;
import org.apache.jetspeed.aggregator.PortletRenderer;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.locator.LocatorDescriptor;
import org.apache.jetspeed.locator.TemplateDescriptor;
import org.apache.jetspeed.locator.TemplateLocator;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.portlet.HeaderPhaseSupportConstants;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.UserSubjectPrincipal;
import org.apache.jetspeed.util.ArgUtil;
import org.apache.jetspeed.util.HeadElementUtils;
import org.apache.jetspeed.util.KeyValue;
import org.apache.jetspeed.util.Path;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletConfig;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.security.auth.Subject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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
public class JetspeedPowerToolImpl implements JetspeedVelocityPowerTool
{

    private static final String DECORATOR_ID_ATTR = "decoratorId";

    private static final String ACTION_IMAGE_EXTENSION_ATTR = "actionImageExtension";

    protected static final String PORTLET_CONFIG_ATTR = "javax.portlet.config";

    protected static final String RENDER_RESPONSE_ATTR = "javax.portlet.response";

    protected static final String RENDER_REQUEST_ATTR = "javax.portlet.request";

    protected static final String COLUMNS_ATTR = "columns";

    protected static final String COLUMN_SIZES = "columnSizes";
    
    protected RenderRequest renderRequest;

    protected RenderResponse renderResponse;

    protected PortletConfig portletConfig;

    protected Writer templateWriter;
    
    protected static final Logger log = LoggerFactory.getLogger(JetspeedPowerToolImpl.class);

    protected CapabilityMap capabilityMap;

    protected Locale locale;

    protected LocatorDescriptor templateLocatorDescriptor;

    protected TemplateLocator templateLocator;

    protected TemplateLocator decorationLocator;

    protected LocatorDescriptor decorationLocatorDescriptor;

    protected RequestContext requestContext;

    protected Context velocityContext;

    private BasePortalURL baseUrlAccess;
    
    private PortletRenderer renderer;

    protected boolean ajaxCustomization = false;
    protected boolean autoRefreshEnabled = true;
    
    public JetspeedPowerToolImpl(RequestContext requestContext, PortletConfig portletConfig, RenderRequest renderRequest, RenderResponse renderResponse, PortletRenderer renderer) throws Exception
    {
        HttpServletRequest request = requestContext.getRequest();
        this.requestContext = requestContext;
        try
        {
            baseUrlAccess = (BasePortalURL) getComponent("BasePortalURL");
        }
        catch (Exception e)
        {            
            // BasePortalURL is optional: ignore (org.springframework.beans.factory.NoSuchBeanDefinitionException)
        }
        
        this.portletConfig = portletConfig;
        this.renderRequest = renderRequest;
        this.renderResponse = renderResponse;

        templateLocator = (TemplateLocator) getComponent("TemplateLocator");
        decorationLocator = (TemplateLocator) getComponent("DecorationLocator");
        String jetuiMode = Jetspeed.getConfiguration().getString(PortalConfigurationConstants.JETUI_CUSTOMIZATION_METHOD, PortalConfigurationConstants.JETUI_CUSTOMIZATION_SERVER);
        this.ajaxCustomization = (jetuiMode.equals(PortalConfigurationConstants.JETUI_CUSTOMIZATION_AJAX));
        this.autoRefreshEnabled = Jetspeed.getConfiguration().getBoolean(PortalConfigurationConstants.AUTO_REFRESH_ENABLED, true);

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
        
        this.renderer = renderer;
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
            PortletWindow window = getPortletWindow(getCurrentFragment());
            if (!window.isValid())
            {
                // return a sensible default value to allow a mimimum level of processing to continue
                return WindowState.NORMAL;
            }
            return getRequestContext().getPortalURL().getNavigationalState().getState(window);
        }
        catch (Exception e)
        {
            handleError(e, e.toString(), getCurrentFragment());
            // return a sensible default value to allow a mimimum level of processing to continue
            return WindowState.NORMAL;
        }
    }

    /**
     * Gets the internal (portal) window state for the current portlet window (fragment)
     * 
     * @return The window state for the current window
     * @throws Exception
     */
    public WindowState getMappedWindowState() throws Exception
    {
        try
        {
            PortletWindow window = getPortletWindow(getCurrentFragment());
            if (!window.isValid())
            {
                // return a sensible default value to allow a mimimum level of processing to continue
                return WindowState.NORMAL;
            }
            return getRequestContext().getPortalURL().getNavigationalState().getMappedState(window);
        }
        catch (Exception e)
        {
            handleError(e, e.toString(), getCurrentFragment());
            // return a sensible default value to allow a mimimum level of processing to continue
            return WindowState.NORMAL;
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
        try
        {
            PortletWindow window = getPortletWindow(getCurrentFragment());
            if (!window.isValid())
            {
                // return a sensible default value to allow a mimimum level of processing to continue
                return PortletMode.VIEW;
            }
            return getRequestContext().getPortalURL().getNavigationalState().getMode(window);
        }
        catch (Exception e)
        {
            handleError(e, e.toString(), getCurrentFragment());
            // return a sensible default value to allow a mimimum level of processing to continue
            return PortletMode.VIEW;
        }
    }

    /**
     * Gets the internal (portal) portlet mode for a current portlet window (fragment)
     * 
     * @return The portlet mode of the current window
     * @throws Exception
     */
    public PortletMode getMappedPortletMode() throws Exception
    {
        try
        {
            PortletWindow window = getPortletWindow(getCurrentFragment());
            if (!window.isValid())
            {
                // return a sensible default value to allow a mimimum level of processing to continue
                return PortletMode.VIEW;
            }
            return getRequestContext().getPortalURL().getNavigationalState().getMappedMode(window);
        }
        catch (Exception e)
        {
            handleError(e, e.toString(), getCurrentFragment());
            // return a sensible default value to allow a mimimum level of processing to continue
            return PortletMode.VIEW;
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
    public void setCurrentFragment(ContentFragment f)
    {
        checkState();
        setAttribute(PortalReservedParameters.FRAGMENT_ATTRIBUTE, f);

    }

    public void setCurrentLayout()
    {
        checkState();

        ContentFragment f = (ContentFragment) getRequestContext().getRequest().getAttribute(LAYOUT_ATTR);
        setAttribute(LAYOUT_ATTR, f);
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
    public ContentPage getPage()
    {
        checkState();
        // return (Page)
        // renderRequest.getAttribute(PortalReservedParameters.PAGE_ATTRIBUTE_KEY);
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
     * @param f
     *            Fragment whose <code>PortletWindow</code> we want to
     *            retrieve.
     * @return The PortletWindow represented by the current fragment.
     * @throws Exception
     */
    public PortletWindow getPortletWindow(ContentFragment f) throws Exception
    {
        return getRequestContext().getPortletWindow(f);
    }
    
    /**
     * Checks the the visibilty of this fragment with respect to the current
     * RenderReqeust.
     * 
     * @param f
     *            Fragment
     * @return whether or not the Fragment in question should be considered
     *         visible during rendering.
     */
    public boolean isHidden(ContentFragment f)
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
     *            Expected to the template. This may actually be changed by the
     *            TL service based the capability and localization information
     *            provided by the client.
     * @param templateType
     *            Type off template we are interested in.
     * @return Template object containng the pertinent information required to
     *         inlcude the request template path in the current response
     * @throws TemplateLocatorException
     *             if the <code>path</code> does not exist.
     */
    public TemplateDescriptor getTemplate(String path, String templateType) throws TemplateLocatorException
    {
        checkState();
        return getTemplate(path, templateType, templateLocator, templateLocatorDescriptor);
    }

    public Configuration getTypeConfiguration(String type, String name, String location) throws Exception
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

    public TemplateDescriptor getDecoration(String path, String templateType) throws TemplateLocatorException
    {
        checkState();
        return getTemplate(path, templateType, decorationLocator, decorationLocatorDescriptor);
    }

    public String includeTemplate(String template, String templateType) throws IOException
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
            return "";
        }
    }

    public String includeDecoration(String template, String templateType) throws IOException
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
            return "";
        }
    }

    /**
     * <p>
     * Decorate and include fragment content.
     * </p>
     * 
     * @param f
     *            Fragment to include and decorate
     * @throws Exception
     * @return String path to the decorator.
     */
    public String decorateAndInclude(ContentFragment f) throws Exception
    {
        // makes sure that any previous content has been written to
        // preserve natural HTML rendering order

        setCurrentFragment(f);
        setCurrentLayout();

        // include decorated layout or portlet fragment
        try
        {
            return decorateAndIncludePortlet(f);
        }
        catch (Exception e)
        {
            renderResponse.getWriter().write(e.toString());
            return "";
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
     *              &lt;% 
     *               JetspeedPowerTool jetspeed = new JetspeedPowerTool(renderRequest, renderResponse, portletConfig);
     *               jetspeed.include(jetspeed.getCurrentFragment());
     *              %&gt;
     *   
     * </code>
     * </pre>
     * 
     * 
     * @param f
     *            Portlet fragment to "decorate"
     * @throws Exception
     */
    protected String decorateAndIncludePortlet(ContentFragment f) throws Exception
    {
        // make sure that any previous content has been written to
        // preserve natural HTML rendering order

        // get fragment decorator; fallback to the default decorator
        // if the current fragment is not specifically decorated
        String fragmentType = f.getType();
        String decorator = f.getDecorator();
        if (decorator == null)
        {
            decorator = getPage().getEffectiveDefaultDecorator(fragmentType);
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

        setAttribute(DECORATOR_ID_ATTR, decoConf.getString("id"));
        setAttribute(ACTION_IMAGE_EXTENSION_ATTR, decoConf.getString("action.image.extension", ".gif"));
        return template.getAppRelativePath();
    }

    /**
     * 
     * 
     * @throws java.lang.IllegalStateException
     *             if the <code>PortletConfig</code>,
     *             <code>RenderRequest</code> or <code>RenderReponse</code>
     *             is null.
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

    protected TemplateDescriptor getTemplate(String path, String templateType, TemplateLocator locator,
            LocatorDescriptor descriptor) throws TemplateLocatorException
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
            // Check for defaults above the currently specified root
            if (template == null)
            {
                Path pathObject = new Path(path);
                if (pathObject.length() > 1)
                {
                    template = getTemplate(pathObject.getSegment(1).toString(), templateType, locator, descriptor);
                }
            }
            return template;
        }
        catch (TemplateLocatorException e)
        {
            log.error("Unable to locate template: " + path, e);
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
    @SuppressWarnings("unchecked")
    protected void handleError(Exception e, String msg, ContentFragment fragment)
    {
        log.error(msg, e);

        Set<Exception> exceptions = (Set<Exception>) renderRequest.getAttribute(FRAGMENT_PROCESSING_ERROR_PREFIX + fragment.getId());
        if (exceptions == null)
        {
            exceptions = new HashSet<Exception>();
            setAttribute(FRAGMENT_PROCESSING_ERROR_PREFIX + fragment.getId(), exceptions);
        }
        exceptions.add(e);
    }

    /**
     * Gets the list of decorator actions for a window. Each window (on each
     * page) has its own collection of actionAccess flags associated with it.
     * 
     * @return A list of actions available to the current window, filtered by
     *         securty access and current state.
     * @throws Exception
     * @deprecated
     */
    public List getDecoratorActions()
    {
        return getCurrentFragment().getDecoration().getActions();
    }

    /**
     * Gets the list of decorator actions for a page. Each layout fragment on a
     * page has its own collection of actionAccess flags associated with it.
     * 
     * @return A list of actions available to the current window, filtered by
     *         securty access and current state.
     * @throws Exception
     * @deprecated
     */
    public List getPageDecoratorActions() throws Exception
    {
        return getCurrentFragment().getDecoration().getActions();
    }

    /**
     * 
     * <p>
     * getTitle
     * </p>
     * Returns the appropriate for the title based on locale prferences
     * 
     * @param f
     * @return
     */
    public String getTitle(ContentFragment f)
    {
        String title = null;
        
        if (f != null)
        {
            title = f.getTitle();
            
            if (title == null && f.getPortletContent() != null)
            {
                title = f.getPortletContent().getTitle();
            }
            
            if (title == null)
            {
                PortletWindow portletWindow = requestContext.getPortletWindow(f);
                
                if (portletWindow != null)
                {
                    // When a portlet definition is not found from the registry,
                    // portlet windows do not have portlet definition.
                    // So, we have to check if the portlet definition is null or not. 
                    
                    PortletDefinition portletDef = portletWindow.getPortletDefinition();
                    
                    if (portletDef != null)
                    {
                        title = requestContext.getPreferedLanguage(portletDef).getTitle();
                        
                        if (title == null)
                        {
                            title = portletDef.getPortletName();
                        }
                    }
                }
                
                if (title == null)
                {
                    title = f.getName();
                    
                    if (title != null && title.indexOf("::") > -1)
                    {
                        title = title.substring(title.indexOf("::") + 2);
                    }
                }
            }
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
     * @return
     */
    public String getTitle()
    {
        return getTitle(getCurrentFragment());
    }

    public Object getComponent(String name)
    {
        return Jetspeed.getComponentManager().lookupComponent(name);
    }

    public String getAbsoluteUrl(String relativePath)
    {
        // only rewrite a non-absolute url
        if (relativePath != null && relativePath.indexOf("://") == -1 && relativePath.indexOf("mailto:") == -1)            
        {
            HttpServletRequest request = getRequestContext().getRequest();
            StringBuffer path = new StringBuffer();
            if ( !getRequestContext().getPortalURL().isRelativeOnly() )
            {
                if (this.baseUrlAccess == null)
                {
                    path.append(request.getScheme()).append("://").append(request.getServerName()).append(":").append(request.getServerPort());
                }
                else
                {
                    path.append(baseUrlAccess.getServerScheme()).append("://").append(baseUrlAccess.getServerName()).append(":").append(baseUrlAccess.getServerPort());
                }
            }
            return renderResponse.encodeURL(path.append(request.getContextPath()).append(request.getServletPath()).append(relativePath).toString());
              
        }
        else
        {
            return relativePath;
        }
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

    public String getBasePath()
    {
        return getRequestContext().getPortalURL().getBasePath();
    }

    public String getPageBasePath()
    {
        return getRequestContext().getPortalURL().getPageBasePath();
    }

    public void setVelocityContext(Context velocityContext)
    {
        this.velocityContext = velocityContext;
    }

    /**
     * Sets an attribute for use within your layout and decoration templates.
     * The value is always stored within the current
     * <code>javax.portlet.Renderrequest</code> and is also stored within the
     * current <code>org.apache.velocity.Context</code> if it is available.
     * 
     * @param name
     *            to store the attribute under.
     * @param object
     *            object to set.
     */
    protected void setAttribute(String name, Object object)
    {
        renderRequest.setAttribute(name, object);
        if (velocityContext != null)
        {
            velocityContext.put(name, object);
        }
    }
    
    public String renderPortletWindow(String windowId, String portletUniqueName)
    {
        try
        {
            if (windowId == null || portletUniqueName == null)
            {
                throw new IllegalArgumentException("Parameter windowId and portletUniqueName are both required");
            }
            RequestContext context = getRequestContext();
            PortletWindow window = context.getPortletWindow(windowId);
            if (window == null)
            {
                window = context.getInstantlyCreatedPortletWindow(windowId, portletUniqueName);
            }
            if (window.isValid())
            {
                PortletWindow currentPortletWindow = context.getCurrentPortletWindow();
                try
                {
                    context.setCurrentPortletWindow(window);
                    renderer.renderNow(window.getFragment(), context);
                    return window.getFragment().getRenderedContent();
                }
                finally
                {
                    context.setCurrentPortletWindow(currentPortletWindow);
                }
            }
            else
            {
                return "";
            }
        }
        catch (Exception e)
        {
            handleError(e, e.toString(), getCurrentFragment());
            return "";
        }
    }

    public String getElementHtmlString(HeadElement headElement)
    {
        return HeadElementUtils.toHtmlString(headElement);
    }

    public List<KeyValue<String, HeadElement>> getHeadElements(ContentFragment f) throws Exception
    {
        return getPortletWindow(f).getHeadElements();
    }

    public List<KeyValue<String, HeadElement>> getHeadElements() throws Exception
    {
        return requestContext.getMergedHeadElements();
    }

    public boolean isDojoEnabled(List<KeyValue<String, HeadElement>> headElements)
    {
        for (KeyValue<String, HeadElement> kvPair : headElements)
        {
            if (HeaderPhaseSupportConstants.HEAD_ELEMENT_CONTRIBUTION_ELEMENT_ID_DOJO_LIBRARY_INCLUDE.equals(kvPair.getKey()))
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean isAjaxCustomizationEnabled()
    {
        return this.ajaxCustomization;
    }

    public boolean isAutoRefreshEnabled() {
        return this.autoRefreshEnabled;
    }

    public Map<String,String> getUserAttributes()
    {
        RequestContext rc = getRequestContext();
        Map<String,String> map = null;
        Principal principal = rc.getRequest().getUserPrincipal();
        if (principal instanceof UserSubjectPrincipal)
        {
            UserSubjectPrincipal jp = (UserSubjectPrincipal)principal;
            map = jp.getUser().getInfoMap();
        }
        return map;
    }
    
    public String getUserAttribute(String attributeName, String defaultValue)
    {
        Map<String,String> infoMap = getUserAttributes();
        String value = infoMap != null ? infoMap.get(attributeName) : null;
        return value != null ? value : defaultValue;
    }

    public PortalConfiguration getPortalConfiguration()
    {
        return Jetspeed.getConfiguration();
    }
}
