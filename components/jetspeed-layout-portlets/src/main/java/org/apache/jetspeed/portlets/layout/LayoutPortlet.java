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
package org.apache.jetspeed.portlets.layout;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.administration.PortalConfigurationConstants;
import org.apache.jetspeed.capabilities.CapabilityMap;
import org.apache.jetspeed.layout.JetspeedPowerTool;
import org.apache.jetspeed.locator.LocatorDescriptor;
import org.apache.jetspeed.locator.TemplateDescriptor;
import org.apache.jetspeed.locator.TemplateLocator;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.velocity.JetspeedPowerToolFactory;
import org.apache.jetspeed.container.PortletWindow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class LayoutPortlet extends org.apache.portals.bridges.common.GenericServletPortlet
{
    public static final String GENERIC_TEMPLATE_TYPE = "generic";

    public static final String FRAGMENT_PROCESSING_ERROR_PREFIX = "fragment.processing.error.";

    public static final String FRAGMENT_ATTR = "fragment";

    public static final String LAYOUT_ATTR = "layout";

    public static final String HIDDEN = "hidden";

    public static final String LAYOUT_TEMPLATE_TYPE = "layout";

    public static final String DECORATOR_TYPE = "decorator";
    
    public static final String PARAM_SOLO_PAGE = "SoloPage";
    
    
    /** Commons logging */
    protected final static Logger log = LoggerFactory.getLogger(LayoutPortlet.class);
    
    protected JetspeedPowerToolFactory jptFactory;
    protected TemplateLocator templateLocator;
    protected TemplateLocator decorationLocator;
    protected boolean storeViewPageInSession;
    protected boolean supportsAjax = false;
    protected String ajaxViewLayout;
    protected String ajaxMaxLayout;
    protected String ajaxSoloLayout;
    private Map layoutTemplatesCache = new HashMap();
    public static final String DEFAULT_TEMPLATE_EXT = ".vm";
    public static final String TEMPLATE_EXTENSION_KEY = "template.extension";
    public static final String DEFAULT_TEMPLATE_TYPE = "velocity";
    public static final String TEMPLATE_TYPE_KEY = "template.type";
    
    public void init( PortletConfig config ) throws PortletException
    {
        super.init(config);
        
        jptFactory = (JetspeedPowerToolFactory)getPortletContext().getAttribute(CommonPortletServices.CPS_JETSPEED_POWERTOOL_FACTORY);
        if (null == jptFactory)
        {
            throw new PortletException("Failed to find the JPT Factory on portlet initialization");
        }        
        
        PortalConfiguration portalConfiguration = (PortalConfiguration) getPortletContext().getAttribute(CommonPortletServices.CPS_PORTAL_CONFIGURATION);
        if (null == portalConfiguration)
        {
            throw new PortletException("Failed to find the Portal Configuration on portlet initialization");
        }        
        storeViewPageInSession = portalConfiguration.getBoolean("layout.page.storeViewPageInSession", true);
        // jetui configuration        
        String jetuiMode = portalConfiguration.getString(PortalConfigurationConstants.JETUI_CUSTOMIZATION_METHOD, PortalConfigurationConstants.JETUI_CUSTOMIZATION_SERVER);
        this.supportsAjax = (jetuiMode.equals(PortalConfigurationConstants.JETUI_CUSTOMIZATION_AJAX));
        this.ajaxViewLayout = portalConfiguration.getString(PortalConfigurationConstants.JETUI_LAYOUT_VIEW, "jetui");
        this.ajaxMaxLayout = portalConfiguration.getString(PortalConfigurationConstants.JETUI_LAYOUT_MAX, "maximized");
        this.ajaxSoloLayout = portalConfiguration.getString(PortalConfigurationConstants.JETUI_LAYOUT_SOLO, "solo");
        
        templateLocator = (TemplateLocator) getPortletContext().getAttribute("TemplateLocator");
        decorationLocator = (TemplateLocator) getPortletContext().getAttribute("DecorationLocator");
    }

    public void doHelp( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        RequestContext context = getRequestContext(request);
        response.setContentType(context.getMimeType());        
        JetspeedPowerTool jpt = getJetspeedPowerTool(request, response);

        String absHelpPage = "";

        // request.setAttribute(PortalReservedParameters.PAGE_ATTRIBUTE, getPage(request));
        // request.setAttribute("fragment", getFragment(request, false));        

        try
        {
            String helpPage = getCachedLayoutViewPage(request, PortalReservedParameters.PAGE_LAYOUT_HELP);                       
            if (helpPage == null)
            {
                PortletPreferences prefs = request.getPreferences();
                helpPage = prefs.getValue(PARAM_HELP_PAGE, null);
                if (helpPage == null)
                {
                    helpPage = this.getInitParameter(PARAM_HELP_PAGE);
                    if (helpPage == null)
                        helpPage = "columns";
                }
                cacheLayoutViewPage(request, PortalReservedParameters.PAGE_LAYOUT_HELP, helpPage);
            }

            String templateKey = helpPage + "/" + JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE  + "-help";
            CachedTemplate ct = (CachedTemplate)layoutTemplatesCache.get(templateKey);
            if (ct == null)
            {
                TemplateDescriptor template = null;
                Configuration props = getConfiguration(request, response, helpPage);
                String ext = (String) props.getString(TEMPLATE_EXTENSION_KEY);
                String path = helpPage + "/" + JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE + "-help" + ext;                               
                template = jpt.getTemplate(path, JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE);
                if (template == null)
                {
                    String msg = "*** FAILED getTemplate:" + path;
                    throw new TemplateLocatorException(msg);
                }
                else
                {
                    synchronized(layoutTemplatesCache)
                    {
                        ct = new CachedTemplate(templateKey, template, props);
                        layoutTemplatesCache.put(templateKey, ct);
                    }                
                }
            }
                
            absHelpPage = ct.getTemplate().getAppRelativePath();            
            log.debug("Path to help page for LayoutPortlet " + absHelpPage);
            request.setAttribute(PARAM_VIEW_PAGE, absHelpPage);
        }
        catch (TemplateLocatorException e)
        {
            throw new PortletException("Unable to locate view page " + absHelpPage, e);
        }
        super.doView(request, response);

    }
    
    /**
     * 
     */
    public void doView( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        RequestContext context = getRequestContext(request);
        response.setContentType(context.getMimeType());        
        PortletWindow window = context.getPortalURL().getNavigationalState().getMaximizedWindow();
        boolean maximized = (window != null);
        boolean solo = false;

        if (maximized)
        {
            request.setAttribute("layout", getMaximizedLayout(request));
            solo = JetspeedActions.SOLO_STATE.equals(context.getPortalURL().getNavigationalState().getMappedState(window));
            if ( solo )
            {
                maximized = false;
            }
        }
        else
        {
            request.setAttribute("layout", getFragment(request, false));
        }
        String viewPage = null;
        String absViewPage = null;
        try
        {
            JetspeedPowerTool jpt = getJetspeedPowerTool(request, response);
            if (maximized)
            {
                viewPage = getCachedLayoutViewPage(request, PortalReservedParameters.PAGE_LAYOUT_MAX);
                if (viewPage == null)
                {
                    PortletPreferences prefs = request.getPreferences();
                    viewPage = prefs.getValue(PARAM_MAX_PAGE, null);
                }
                if (viewPage == null)
                {
                    viewPage = (this.supportsAjax) ? this.ajaxMaxLayout : this.getInitParameter(PARAM_MAX_PAGE);
                    if (viewPage == null)
                        viewPage = "maximized";
                    cacheLayoutViewPage(request, PortalReservedParameters.PAGE_LAYOUT_MAX, viewPage);
                }
            }
            else if (solo)
            {
                viewPage = getCachedLayoutViewPage(request, PortalReservedParameters.PAGE_LAYOUT_SOLO);                       
                if (viewPage == null)
                {
                    PortletPreferences prefs = request.getPreferences();
                    viewPage = prefs.getValue(PARAM_SOLO_PAGE, null);
                }
                if (viewPage == null)
                {
                    viewPage = (this.supportsAjax) ? this.ajaxSoloLayout : this.getInitParameter(PARAM_SOLO_PAGE);
                    if (viewPage == null)
                    {
                        viewPage = "solo";
                    }
                    cacheLayoutViewPage(request, PortalReservedParameters.PAGE_LAYOUT_SOLO, viewPage);                    
                }
            }
            else
            {
                viewPage = getCachedLayoutViewPage(request, PortalReservedParameters.PAGE_LAYOUT_VIEW);                       
                if (viewPage == null)
                {
                    PortletPreferences prefs = request.getPreferences();
                    viewPage = prefs.getValue(PARAM_VIEW_PAGE, null);
                }
                if (viewPage == null)
                {
                    viewPage = (this.supportsAjax) ? this.ajaxViewLayout : this.getInitParameter(PARAM_VIEW_PAGE);
                    if (viewPage == null)
                        viewPage = "columns";
                    cacheLayoutViewPage(request, PortalReservedParameters.PAGE_LAYOUT_VIEW, viewPage);                    
                }
            }
            
            String templateKey = viewPage + "/" + JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE;
            CachedTemplate ct = (CachedTemplate)layoutTemplatesCache.get(templateKey);
            if (ct == null)
            {
                TemplateDescriptor template = null;
                Configuration props = getConfiguration(request, response, viewPage);
                String ext = (String) props.getString(TEMPLATE_EXTENSION_KEY);
                String path = viewPage + "/" + JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE + ext;
                
                template = jpt.getTemplate(path, JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE);
                if (template == null)
                {
                    String msg = "*** FAILED getTemplate:" + path;
                    throw new TemplateLocatorException(msg);
                }
                else
                {
                    synchronized(layoutTemplatesCache)
                    {
                        ct = new CachedTemplate(templateKey, template, props);
                        layoutTemplatesCache.put(templateKey, ct);
                    }
                
                }
            }
            absViewPage = ct.getTemplate().getAppRelativePath();
            log.debug("Path to view page for LayoutPortlet " + absViewPage);
            request.setAttribute(PARAM_VIEW_PAGE, absViewPage);
        }
        catch (TemplateLocatorException e)
        {
            throw new PortletException("Unable to locate view page " + absViewPage, e);
        }
        super.doView(request, response);

        request.removeAttribute("fragment");
        request.removeAttribute("layout");
        request.removeAttribute("dispatcher");
    }
    
    public void processAction(ActionRequest request, ActionResponse response)
    throws PortletException, IOException
    {
        RequestContext requestContext = getRequestContext(request);
        ContentPage requestPage = requestContext.getPage();       

        String page = request.getParameter("page");
        if ((page != null) && page.equals(requestPage.getId()))
        {
            String deleteFragmentId = request.getParameter("deleteId");
            String portlets = request.getParameter("portlets");
            if (deleteFragmentId != null && deleteFragmentId.length() > 0)
            {
                try
                {
                    requestPage.removeFragment(deleteFragmentId);
                }
                catch (Exception e)
                {
                    log.error("failed to remove fragment from page: " + deleteFragmentId);
                }
            }
            else if (portlets != null && portlets.length() > 0)
            {
                StringTokenizer tokenizer = new StringTokenizer(portlets, ",");            
                while (tokenizer.hasMoreTokens())
                {
                    String portlet = tokenizer.nextToken();
                    try
                    {
                        if (portlet.startsWith("box_"))
                        {
                            portlet = portlet.substring("box_".length());
                            requestPage.addPortlet(ContentFragment.PORTLET, portlet);
                        }
                    }
                    catch (Exception e)
                    {
                        log.error("failed to add portlet to page: " + portlet);
                    }
                }

            }
        }
        else
        {
            log.error("failed to process action for current page: " + page + "!=" + requestPage.getId());            
        }
    }

    /**
     * <p>
     * initJetspeedPowerTool
     * </p>
     * 
     * @param request
     * @param response
     * @return
     * @throws PortletException
     */
    protected JetspeedPowerTool getJetspeedPowerTool( RenderRequest request, RenderResponse response ) throws PortletException
    {
        JetspeedPowerTool tool = (JetspeedPowerTool) request.getAttribute(PortalReservedParameters.JETSPEED_POWER_TOOL_REQ_ATTRIBUTE);
        RequestContext requestContext = (RequestContext) request.getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);

        if (tool == null)
        {

            try
            {
                if (requestContext == null)
                {
                    throw new IllegalStateException(
                            "LayoutPortlet unable to handle request because there is no RequestContext in "
                                    + "the HttpServletRequest.");
                }

                tool = this.jptFactory.getJetspeedPowerTool(requestContext, getPortletConfig(), request, response );
                request.setAttribute(PortalReservedParameters.JETSPEED_POWER_TOOL_REQ_ATTRIBUTE, tool);
            }

            catch (Exception e1)
            {
                throw new PortletException("Unable to init JetspeedPowerTool: " + e1.toString(), e1);
            }
        }
        
        return tool;
    }
    
    /**
     * 
     * @param request
     * @param maximized
     * @return
     */
    protected ContentFragment getFragment( RenderRequest request, boolean maximized )
    {
        String attribute = (maximized)
                ? PortalReservedParameters.MAXIMIZED_FRAGMENT_ATTRIBUTE
                : PortalReservedParameters.FRAGMENT_ATTRIBUTE;
        return (ContentFragment) request.getAttribute(attribute);       
    }
   
    /**
     * 
     * @param request
     * @return
     */
    protected ContentFragment getMaximizedLayout( RenderRequest request )
    {
        return (ContentFragment) request.getAttribute(PortalReservedParameters.MAXIMIZED_LAYOUT_ATTRIBUTE);
    }
    
    /**
     * 
     * @param request
     * @return
     */
    protected RequestContext getRequestContext( RenderRequest request )
    {
        RequestContext requestContext = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        if (requestContext != null)
        {
            return requestContext;
        }
        else
        {
            throw new IllegalStateException(
                    "getRequestContext() failed as it appears that no RequestContext is available within the RenderRequest");
        }
    }

    /**
     * 
     * @param request
     * @return
     */
    protected RequestContext getRequestContext( ActionRequest request )
    {
        RequestContext requestContext = (RequestContext) request
                .getAttribute(PortalReservedParameters.REQUEST_CONTEXT_ATTRIBUTE);
        if (requestContext != null)
        {
            return requestContext;
        }
        else
        {
            throw new IllegalStateException(
                    "getRequestContext() failed as it appears that no RequestContext is available within the ActionRequest");
        }
    }

    /**
     * <p>
     * doEdit
     * </p>
     * 
     * @see javax.portlet.GenericPortlet#doEdit(javax.portlet.RenderRequest,
     *          javax.portlet.RenderResponse)
     * @param request
     * @param response
     * @throws PortletException
     * @throws IOException
     */
    public void doEdit( RenderRequest request, RenderResponse response ) throws PortletException, IOException
    {
        doView(request, response);
    }
    
    /**
     * 
     * @param request
     * @return
     * @throws TemplateLocatorException
     */
    protected LocatorDescriptor getTemplateLocatorDescriptor(RenderRequest request) throws TemplateLocatorException
    {
        RequestContext requestContext = getRequestContext(request);
        CapabilityMap capabilityMap = requestContext.getCapabilityMap();
        Locale locale = requestContext.getLocale();

        LocatorDescriptor templateLocatorDescriptor = templateLocator.createLocatorDescriptor(null);
        templateLocatorDescriptor.setMediaType(capabilityMap.getPreferredMediaType().getName());
        templateLocatorDescriptor.setCountry(locale.getCountry());
        templateLocatorDescriptor.setLanguage(locale.getLanguage());
        return templateLocatorDescriptor;     
    }
    
    
    /**
     * 
     * @param request
     * @return
     * @throws TemplateLocatorException
     */
    protected LocatorDescriptor getDecoratorLocatorDescriptor(RenderRequest request) throws TemplateLocatorException
    {
        RequestContext requestContext = getRequestContext(request);
        CapabilityMap capabilityMap = requestContext.getCapabilityMap();
        Locale locale = requestContext.getLocale();
  
        LocatorDescriptor decorationLocatorDescriptor = decorationLocator.createLocatorDescriptor(null);
        decorationLocatorDescriptor.setMediaType(capabilityMap.getPreferredMediaType().getName());
        decorationLocatorDescriptor.setCountry(locale.getCountry());
        decorationLocatorDescriptor.setLanguage(locale.getLanguage());
        
        return decorationLocatorDescriptor;
    }
    
    /**
     * 
     * @param request
     * @param fragment
     * @param page
     * @return
     * @throws TemplateLocatorException
     * @throws ConfigurationException
     */
    public String decorateAndInclude(RenderRequest request, ContentFragment fragment, ContentPage page) throws TemplateLocatorException, ConfigurationException
    {   
        String fragmentType = fragment.getType();
        String decorator = fragment.getDecorator();
        LocatorDescriptor decorationLocatorDescriptor = getDecoratorLocatorDescriptor(request);
        if (decorator == null)
        {
            decorator = page.getEffectiveDefaultDecorator(fragmentType);
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
            template = getDecoration(request, decoratorPath, fragmentType);
        }
        catch (TemplateLocatorException e)
        {
            String parent = decoConf.getString("extends");
            if (parent != null)
            {
                template = getDecoration(request, parent + "/" + DECORATOR_TYPE + ext, fragmentType);
            }
        }

        return  template.getAppRelativePath();
    }
    
    /**
     * 
     * @param request
     * @param path
     * @param templateType
     * @return
     * @throws TemplateLocatorException
     */
    protected TemplateDescriptor getDecoration( RenderRequest request, String path, String templateType ) throws TemplateLocatorException
    {        
        return getTemplate(path, templateType, decorationLocator, getDecoratorLocatorDescriptor(request));
    }
    
    /**
     * 
     * @param path
     * @param templateType
     * @param locator
     * @param descriptor
     * @return
     * @throws TemplateLocatorException
     */
    protected TemplateDescriptor getTemplate( String path, String templateType, TemplateLocator locator,
            LocatorDescriptor descriptor ) throws TemplateLocatorException
    {
        
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
            throw e;
        }
    }

    /**
     * Gets the configuration (layout.properties) object for the decoration.
     * @param name Name of the Decoration.
     * @return <code>java.util.Properties</code> representing the configuration
     * object.
     */
    protected Configuration getConfiguration( RenderRequest request, RenderResponse response, String name )
    {
        Configuration props = null;
        JetspeedPowerTool jpt = null;
        String templatePropertiesPath = null;
        String key = name;
        try
        {
            jpt = getJetspeedPowerTool(request, response);
            templatePropertiesPath = jpt.getTemplate(name + "/" + JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE + ".properties",
                    JetspeedPowerTool.LAYOUT_TEMPLATE_TYPE).getAbsolutePath();
        } 
        catch (PortletException e)
        {
            log.warn("Could not acquire JetspeedPowerTool from request",e);
        }
        catch (TemplateLocatorException e)
        {
            log.warn("Could not find templatePorpertiesPath",e);
        }
        catch (Exception e)
        {
            log.warn("Could not determine Layout template properties file",e);
        }
        // if no path then set name to "default"
        if (null == templatePropertiesPath)
        {
            key = "default";
        }
        else
        {
            key = templatePropertiesPath;
        }
        if (log.isDebugEnabled())
        {
            log.debug(
                    "Template descriptor path:<" + templatePropertiesPath + ">"
            );
        }

        // load Decoration.CONFIG_FILE_NAME (layout.properties)
        try
        {
            props = new PropertiesConfiguration(templatePropertiesPath);
            if (log.isDebugEnabled())
                log.debug("Successfully read in: <" + templatePropertiesPath + "> ");
        } 
        catch (Exception e)
        {
            props = new PropertiesConfiguration();
            log.warn( "Could not locate the " + templatePropertiesPath + " file for layout template \"" + name + "\".  This layout template may not exist.",e );
            props.setProperty( "id", name );
            props.setProperty( TEMPLATE_TYPE_KEY, DEFAULT_TEMPLATE_TYPE );
            props.setProperty( TEMPLATE_EXTENSION_KEY, DEFAULT_TEMPLATE_EXT);
        }
        finally
        {
            String templateIdPropVal = (String) props.getProperty( "id" );
            String templateNamePropVal = (String) props.getProperty( TEMPLATE_TYPE_KEY );
            String templateExtPropVal = (String) props.getProperty(TEMPLATE_EXTENSION_KEY);
            
            if ( templateIdPropVal == null )
            {
                templateIdPropVal = name;
                props.setProperty( "id", templateIdPropVal );
            }
            
            if ( templateNamePropVal == null )
            {
                props.setProperty( TEMPLATE_TYPE_KEY, DEFAULT_TEMPLATE_TYPE );
            }
            if ( templateExtPropVal == null )
            {
                props.setProperty( TEMPLATE_EXTENSION_KEY, DEFAULT_TEMPLATE_EXT );
            }
        }

        if (log.isDebugEnabled())
        {
            log.debug("Template layout.properties extension is:<" + props.getString(TEMPLATE_EXTENSION_KEY));
        }
        return props;
    }

    
    /**
     * Retrieve the cached layout view page location. This method provides an easy way to turn on/off caching of 
     * layout view page locations. By default, view page locations are stored in the portlet session. Set the Jetspeed property 
     * <code>layout.page.storeViewPageInSession</code> to <code>true</code> / <code>false</code> to turn on / off caching.
     * @param request portlet request
     * @param viewPageType the view page type, see the PortalReservedParameters.PAGE_LAYOUT_* parameters.
     * @return the cached view page location
     */
    protected String getCachedLayoutViewPage(RenderRequest request, String viewPageType){
    	return storeViewPageInSession ? (String)request.getPortletSession().getAttribute(viewPageType) : null;  
    }
    
    /**
     * Cache a layout view page location. By default, the value is stored in the portlet session. 
     * @param request portlet request
     * @param viewPageType the type of the view page (e.g. help, maximized view, solo, etc.). See the 
     * PortalReservedParameters.PAGE_LAYOUT_* parameters.
     * @param page the view page to cache
     */
    protected void cacheLayoutViewPage(RenderRequest request, String viewPageType, String page){
    	if (storeViewPageInSession){
        	request.getPortletSession().setAttribute(viewPageType, page);
    	}
    }
    
    class CachedTemplate
    {
        private String key;
        private TemplateDescriptor template;
        private Configuration config;
        
        public CachedTemplate(String key, TemplateDescriptor template, Configuration config)
        {
            this.key = key;
            this.template = template;
            this.config = config;
        }

        
        public Configuration getConfig()
        {
            return config;
        }

        
        public String getKey()
        {
            return key;
        }

        
        public TemplateDescriptor getTemplate()
        {
            return template;
        }
    }
}
