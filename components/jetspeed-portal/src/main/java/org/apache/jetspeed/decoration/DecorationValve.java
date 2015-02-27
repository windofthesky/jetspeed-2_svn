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
package org.apache.jetspeed.decoration;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.ContentCacheKey;
import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.decoration.caches.SessionPathResolverCache;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.Supports;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.Valve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityAccessController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Assigns decorations and page actions to all of the portlet Fragments within
 * the current request. 
 * 
 * @see org.apache.jetspeed.om.page.ContentFragment 
 * @see org.apache.jetspeed.om.page.ContentPage
 * @see org.apache.jetspeed.decoration.Decoration
 * @see org.apache.jetspeed.decoration.LayoutDecoration
 * @see org.apache.jetspeed.decoration.PortletDecoration
 * @see org.apache.jetspeed.decoration.Theme
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @author <href a="mailto:firevelocity@gmail.com">Vivek Kumar</a>
 * @version $Id$
 */
public class DecorationValve extends AbstractValve implements Valve
{
    public static final String ACTION_IMAGE_EXTENSION_ATTR = "actionImageExtension";
    public static final String IS_AJAX_DECORATION_REQUEST = "org.apache.jetspeed.decoration.ajax";
    
    protected final static Logger log = LoggerFactory.getLogger(DecorationValve.class);
    
    private final DecorationFactory decorationFactory;

    private Map<String, DecoratorActionsFactory> decoratorActionsAdapterCache = Collections.synchronizedMap(new HashMap<String, DecoratorActionsFactory>());
    
    private DecoratorActionsFactory defaultDecoratorActionsFactory;
    
    private String defaultDecoratorActionsFactoryClassName;

    private JetspeedContentCache cache = null;
    
    private boolean useSessionForThemeCaching = false;
    
    private boolean maxOnEdit = false;
    
    private boolean maxOnConfig = false;
    
    private boolean maxOnEditDefaults = false;
    
    /**
     * When edit_defaults mode is not supported by a portlet, support the mode automatically.
     */
    private boolean autoSwitchingForConfigMode = false;

    /**
     * When edit_defaults mode is not supported by a portlet, support the mode automatically.
     */
    private boolean autoSwitchingToEditDefaultsModes = true;
         
    /**
     * For security constraint checks
     */
    protected SecurityAccessController accessController;
    
    /**
     * For portlet instance helper method support checks.
     */
    private PortletFactory portletFactory;

    public DecorationValve(DecorationFactory decorationFactory, SecurityAccessController accessController)
    {
        this(decorationFactory, accessController, null);
    }
     
    public DecorationValve(DecorationFactory decorationFactory,
                           SecurityAccessController accessController, JetspeedContentCache cache)
    {    
        this(decorationFactory, accessController, cache, false);
    }
     
    public DecorationValve(DecorationFactory decorationFactory,
                           SecurityAccessController accessController, JetspeedContentCache cache,
                           boolean useSessionForThemeCaching)
    {
        this(decorationFactory, accessController, cache, useSessionForThemeCaching, null);
    }
    
    public DecorationValve(DecorationFactory decorationFactory,
                           SecurityAccessController accessController, 
                           JetspeedContentCache cache, boolean useSessionForThemeCaching,
                           PortletFactory portletFactory)
    {
        this(decorationFactory, accessController, cache, useSessionForThemeCaching, null, new DefaultDecoratorActionsFactory());
    }
    
    public DecorationValve(DecorationFactory decorationFactory,
                           SecurityAccessController accessController, 
                           JetspeedContentCache cache, boolean useSessionForThemeCaching,
                           PortletFactory portletFactory,
                           DecoratorActionsFactory defaultDecoratorActionsFactory)
    {
        this.decorationFactory = decorationFactory;
        this.defaultDecoratorActionsFactory = defaultDecoratorActionsFactory;
        this.defaultDecoratorActionsFactoryClassName = defaultDecoratorActionsFactory.getClass().getName();
        //added the accessController in portlet decorater for checking the actions
        this.accessController = accessController;        
        this.cache = cache;
        this.useSessionForThemeCaching = useSessionForThemeCaching;
        this.portletFactory = portletFactory;
    }
    
    public void invoke(RequestContext requestContext, ValveContext context) throws PipelineException
    {
        //long start = System.currentTimeMillis();
        boolean isAjaxRequest = (context == null);        
        initFragments( requestContext, isAjaxRequest, null );        
        //long end = System.currentTimeMillis();
        //System.out.println(end - start);
        if (!isAjaxRequest)
        {
            context.invokeNext(requestContext);
        }
    }


    public void initFragments( RequestContext requestContext, boolean isAjaxRequest, List fragments )
    {
        if (isAjaxRequest)
        {
            requestContext.setAttribute(IS_AJAX_DECORATION_REQUEST, new Boolean(true));
        }

        ContentPage page = requestContext.getPage();

        // Globally override all psml themes if override session attribute has been set
        if (requestContext
                .getSessionAttribute(PortalReservedParameters.PAGE_THEME_OVERRIDE_ATTRIBUTE) != null)
        {
            String decoratorName = (String) requestContext
                    .getSessionAttribute(PortalReservedParameters.PAGE_THEME_OVERRIDE_ATTRIBUTE);
            page.overrideDefaultDecorator(decoratorName, Fragment.LAYOUT);
        }
        
        PageActionAccess pageActionAccess = (PageActionAccess)requestContext.getAttribute(PortalReservedParameters.PAGE_EDIT_ACCESS_ATTRIBUTE);
        String themeCacheKey = null;
        ContentCacheKey themeContentCacheKey = null;
        Theme theme = null;
        
        if (useCache())
        {
            if (pageActionAccess.isEditing() == false)
            {
                // user helps us with the funky way jetspeed doesn't create  a new session on login
                if (this.useSessionForThemeCaching)
                {
                    themeCacheKey = cache.createSessionKey(requestContext);
                    theme = (Theme) requestContext.getSessionAttribute(themeCacheKey);
                }
                else
                {
                    themeContentCacheKey = cache.createCacheKey(requestContext, page.getId());
                    CacheElement themeCacheElem = cache.get(themeContentCacheKey);
                    
                    if (themeCacheElem != null)
                    {
                        theme = (Theme) themeCacheElem.getContent();
                    }
                }
            }
        }

        if (theme != null)
        {
            theme.init(page, decorationFactory, requestContext);
            requestContext.setAttribute(PortalReservedParameters.PAGE_THEME_ATTRIBUTE, theme);
            requestContext.setAttribute(PortalReservedParameters.PAGE_THEME_RESPONSIVE, new Boolean(theme.getPageLayoutDecoration().getProperty("responsive")));
            boolean solo = isSoloMode(requestContext);            
            SessionPathResolverCache sessionPathResolver = new SessionPathResolverCache( requestContext.getRequest().getSession() );
            initDepthFragmentDecorations(requestContext, theme, page.getRootFragment(),
                                                    pageActionAccess, isAjaxRequest,
                                                    ((DecorationFactoryImpl) decorationFactory).getResourceValidator(),
                                                    sessionPathResolver, (theme.isInvalidated() && !solo));
            
            if (theme.isInvalidated() && !solo)
            {
                if (this.useSessionForThemeCaching)
                {
                    requestContext.setSessionAttribute(themeCacheKey, theme);
                }
                else
                {                    
                    CacheElement themeCacheElem = cache.createElement(themeContentCacheKey, theme);
                    cache.put(themeCacheElem);
                }
                theme.setInvalidated(false);                            
            }                        
            return;
        }
        theme = decorationFactory.getTheme(page, requestContext);        
        requestContext.setAttribute(PortalReservedParameters.PAGE_THEME_ATTRIBUTE, theme);
        requestContext.setAttribute(PortalReservedParameters.PAGE_THEME_RESPONSIVE, new Boolean(theme.getPageLayoutDecoration().getProperty("responsive")));
        if ( fragments == null || fragments.size() == 0 )
        {
            ContentFragment rootFragment = page.getRootFragment();
            initDepthFragments(requestContext, theme, rootFragment, pageActionAccess, isAjaxRequest, fragments);
        }
        else
        {
            Iterator fragmentsIter = fragments.iterator();
            while ( fragmentsIter.hasNext() )
            {
                ContentFragment fragment = (ContentFragment)fragmentsIter.next();
                initFragment(requestContext, theme, fragment, pageActionAccess, isAjaxRequest);
            }
        }
        
        if (useCache() && !isSoloMode(requestContext))
        {
            if (themeContentCacheKey == null && themeCacheKey == null)
            {
                if (this.useSessionForThemeCaching)
                {
                    themeCacheKey = cache.createSessionKey(requestContext);                    
                    requestContext.getRequest().getSession().removeAttribute(themeCacheKey);
                }
                else
                {
                    themeContentCacheKey = cache.createCacheKey(requestContext, page.getId());
                    cache.remove(themeContentCacheKey);
                }                
            }
            else
            {
                if (this.useSessionForThemeCaching)
                {
                    themeContentCacheKey = cache.createCacheKey(requestContext, page.getId());
                    requestContext.setSessionAttribute(themeCacheKey, theme);
                }
                else
                {
                    CacheElement themeCacheElem = cache.createElement(themeContentCacheKey, theme);
                    cache.put(themeCacheElem);
                }
            }
        }                
    }

    protected boolean isSoloMode(RequestContext requestContext)
    {
        boolean solo = false;
        PortletWindow window = requestContext.getPortalURL().getNavigationalState().getMaximizedWindow();
        boolean maximized = (window != null);
        if (maximized)
        {
            solo = JetspeedActions.SOLO_STATE.equals(requestContext.getPortalURL().getNavigationalState().getMappedState(window));
        }
        return solo;
    }
    
    protected boolean useCache()
    {
        return this.cache != null;
    }
    
    public String toString()
    {
        return "DecorationValve";
    }
    
    public DecoratorActionsFactory getDecoratorActionsAdapter(Decoration decoration)
    {
        // read custom decorator actions factory class name from the decoration properties.
        String decoratorActionsAdapterClassName = decoration.getProperty("actions.factory");
        if (decoratorActionsAdapterClassName == null || "".equals(decoratorActionsAdapterClassName) || decoratorActionsAdapterClassName.equals(defaultDecoratorActionsFactoryClassName))
        {
            return defaultDecoratorActionsFactory;
        }
        
        DecoratorActionsFactory adapter = (DecoratorActionsFactory) decoratorActionsAdapterCache.get(decoratorActionsAdapterClassName);
        
        if (adapter == null)
        {
            try
            {
                adapter = (DecoratorActionsFactory) Class.forName(decoratorActionsAdapterClassName).newInstance();
                adapter.setMaximizeOnEdit(this.maxOnEdit);
                adapter.setMaximizeOnConfig(this.maxOnConfig);
                adapter.setMaximizeOnEditDefaults(this.maxOnEditDefaults);
                decoratorActionsAdapterCache.put(decoratorActionsAdapterClassName, adapter);
            }
            catch (Exception e)
            {
                adapter = defaultDecoratorActionsFactory;
                log.error("Failed to instantiate custom DecoratorActionsAdaptor " + decoratorActionsAdapterClassName + ", falling back to default.", e);
            }
        }
        
        return adapter;
    }
    
    /**
     * Builds and assigns a list of available portlet modes and window states for
     * the target <code>Fragment</code>.
     * 
     * @param requestContext RequestContext of the current portal request.
     * @param fragment Fragment to initialize modes and states for.
     * @return
     */
    protected boolean initActionsForFragment(RequestContext requestContext, 
                                             ContentFragment fragment, 
                                             PageActionAccess pageActionAccess, 
                                             Decoration decoration,
                                             boolean isAjaxRequest)
    {
        boolean fragmentSupportsActions = false;
        PortletWindow window = requestContext.getPortletWindow(fragment); 
        if (!window.isValid())
        {
            return fragmentSupportsActions; // allow nothing
        }
        PortletDefinition portlet = window.getPortletDefinition();

        List actions = Collections.EMPTY_LIST;
        
        PortletMode currentMode = requestContext.getPortalURL().getNavigationalState().getMode(window);
        WindowState currentState = requestContext.getPortalURL().getNavigationalState().getState(window);
        List<Supports> supports = portlet.getSupports();
        
        if ( fragment.equals(requestContext.getPage().getRootFragment()) )
        {
            fragmentSupportsActions = true;
            actions = getPageModes(requestContext, window, supports, currentMode, currentState, pageActionAccess, decoration, isAjaxRequest);
        }
        else if ( !Fragment.LAYOUT.equals(fragment.getType()) )
        {
            fragmentSupportsActions = true;
            String fragmentId = fragment.getId();
            PortletApplication pa = window.getPortletDefinition().getApplication();

            String portletName = portlet.getUniqueName();

            PortletMode currentMappedMode = pa.getMappedPortletMode(currentMode);
            WindowState currentMappedState = pa.getMappedWindowState(currentState);

            Object action;
            PortletMode mappedMode;
            PortletMode customMode;
            WindowState mappedState;
            WindowState customState;
            
            ArrayList actionTemplates = new ArrayList();
            
            DecoratorActionsFactory actionsAdapter = getDecoratorActionsAdapter(decoration);
            
            List supportedActions = actionsAdapter.getSupportedActions(requestContext, pa, window, currentMappedMode, currentMappedState, decoration);
            Iterator iter = supportedActions.iterator();
            
            String currentModeAction = null;
            String currentStateAction = null;

            while ( iter.hasNext() )
            {
                action = iter.next();
                if ( action instanceof PortletMode )
                {
                    mappedMode = (PortletMode)action;
                    customMode = pa.getCustomPortletMode(mappedMode);
                    
                    if ( customMode != null )
                    {
                        boolean equalsCurrentMode = customMode.equals(currentMode);
                        if ( equalsCurrentMode )
                        {
                            currentModeAction = mappedMode.toString();
                        }
                        if ( ! equalsCurrentMode || isAjaxRequest )
                        {
                            if ( (supportsPortletMode(supports,customMode) || isAutoSwitchableCustomMode(window, customMode))
                                 && (!PortletMode.EDIT.equals(customMode) || pageActionAccess.isEditAllowed())
                                 && pageActionAccess.checkPortletMode(fragmentId, portletName, mappedMode)
                                 )
                            {
                                actionTemplates.add(new DecoratorActionTemplate(mappedMode, customMode));
                            }
                        }
                    }
                }
                else if ( action instanceof WindowState )
                {
                    mappedState = (WindowState)action;
                    customState = pa.getCustomWindowState(mappedState);

                    if ( customState != null )
                    {
                        boolean equalsCurrentState = customState.equals(currentState);
                        if ( equalsCurrentState )
                        {
                            currentStateAction = mappedState.toString();
                        }
                        if ( ! equalsCurrentState || isAjaxRequest )
                        {
                            if ( pageActionAccess.checkWindowState(fragmentId, portletName, mappedState ) )
                            {
                                actionTemplates.add(new DecoratorActionTemplate(mappedState, customState));
                            }
                        }
                    }
                }
            }
            actions = actionsAdapter.getDecoratorActions(requestContext, pa, window, currentMode, currentState, decoration, actionTemplates,portlet,fragment,accessController);            
            
            decoration.setCurrentModeAction( currentModeAction );
            decoration.setCurrentStateAction( currentStateAction );
        }
        
        decoration.setActions( actions );
        
        return fragmentSupportsActions;
    }
    
    protected boolean supportsPortletMode(List<Supports> supports, PortletMode mode)
    {
        if(mode.equals(PortletMode.VIEW))
        {
            return true;
        }
        String pm = mode.toString();
        for (Supports s : supports)
        {
            if (s.getPortletModes().contains(pm))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Builds a list of portlet modes that can be executed on the current
     * <code>fragment</code> excluding the portlet's current mode.
     * 
     * @param requestContext RequestContext of the current portal request.
     * @param window
     * @param supports
     * @param mode
     * @param state
     * @param pageActionAccess     *
     * @param decoration
     * @param isAjaxRequest
     * @return <code>java.util.List</code> of modes excluding the current one.
     */
    protected List getPageModes(RequestContext requestContext, PortletWindow window, List<Supports> supports, 
                                PortletMode mode, WindowState state, PageActionAccess pageActionAccess, Decoration decoration,
                                boolean isAjaxRequest)
    {
        List pageModes = new ArrayList();
        
        
        try
        {
            if (mode.equals(PortletMode.HELP) || !state.equals(WindowState.NORMAL))
            {
                // switch back to VIEW mode and NORMAL state.
                PortalURL portalURL = requestContext.getPortalURL();
                String action = requestContext.getResponse().encodeURL( (isAjaxRequest)
                  ? portalURL.createNavigationalEncoding(window, PortletMode.VIEW, WindowState.NORMAL)                          
                  : portalURL.createPortletURL(window, PortletMode.VIEW, WindowState.NORMAL, portalURL.isSecure()).toString() );
                String actionName = PortletMode.VIEW.toString();
                pageModes.add(new DecoratorActionImpl(actionName, requestContext.getLocale(), decoration.getResource("images/" + actionName + ".gif"),action,DecoratorActionTemplate.ACTION_TYPE_MODE));
            }
            else if ( pageActionAccess.isEditAllowed() )
            {
                String targetMode = pageActionAccess.isEditing() ? PortletMode.VIEW.toString() : PortletMode.EDIT.toString();
                PortalURL portalURL = requestContext.getPortalURL();
                HashMap parameters = new HashMap();
                String[] paramValues = new String[]{targetMode};
                parameters.put("pageMode",paramValues);

                // Use an ActionURL to set the oposite pageMode and always set VIEW mode and state NORMAL 
                String action = requestContext.getResponse().encodeURL( (isAjaxRequest)
                    ? portalURL.createNavigationalEncoding(window, parameters, PortletMode.VIEW, WindowState.NORMAL, true)                                              
                    : portalURL.createPortletURL(window, parameters, PortletMode.VIEW, WindowState.NORMAL, true, portalURL.isSecure()).toString() );
                pageModes.add(new DecoratorActionImpl(targetMode, requestContext.getLocale(), decoration.getResource("images/" + targetMode + ".gif"), action,DecoratorActionTemplate.ACTION_TYPE_MODE));
                
                if (supportsPortletMode(supports,PortletMode.HELP))
                {
                    if ( pageActionAccess.isEditing() )
                    {
                        // force it back to VIEW mode first with an ActionURL, as well as setting HELP mode and MAXIMIZED state
                        paramValues[0] = PortletMode.VIEW.toString();
                        action = requestContext.getResponse().encodeURL( (isAjaxRequest)
                            ? portalURL.createNavigationalEncoding(window, parameters, PortletMode.HELP, WindowState.MAXIMIZED, true)                                                  
                            : portalURL.createPortletURL(window, parameters, PortletMode.HELP, WindowState.MAXIMIZED, true, portalURL.isSecure()).toString() );
                    }
                    else
                    {
                        // switch to mode HELP and state MAXIMIZED
                        action = requestContext.getResponse().encodeURL( (isAjaxRequest)
                            ? portalURL.createNavigationalEncoding(window, PortletMode.HELP, WindowState.MAXIMIZED)                        
                            : portalURL.createPortletURL(window,PortletMode.HELP, WindowState.MAXIMIZED, portalURL.isSecure()).toString() );
                    }
                    String actionName = PortletMode.HELP.toString();
                    pageModes.add(new DecoratorActionImpl(actionName, requestContext.getLocale(), decoration.getResource("images/" + actionName + ".gif"), action,DecoratorActionTemplate.ACTION_TYPE_MODE));
                }
            }
        }
        catch (Exception e)
        {
            log.warn("Unable to initalize PageLayout actions", e);
            pageModes = null;
        }
        
        return pageModes;
    }  
    
    /**
     * Intializes all fragments with there decorations and portlet modes 
     * and winodw states.
     * 
     * 
     * @param requestContext RequestContext of the current portal request.
     * @param theme
     * @param fragment
     * @param pageActionAccess
     */
    protected void initDepthFragments(RequestContext requestContext, 
                                      Theme theme, 
                                      ContentFragment fragment, 
                                      PageActionAccess pageActionAccess,
                                      boolean isAjaxRequest,
                                      List collectFragments )
    {
        final List contentFragments = fragment.getFragments();
        
        if(contentFragments != null && contentFragments.size() > 0)
        {
            Iterator itr = contentFragments.iterator();
            while(itr.hasNext())
            {
                ContentFragment aFragment = (ContentFragment) itr.next();
                initDepthFragments(requestContext, theme, aFragment, pageActionAccess, isAjaxRequest, collectFragments);
            }
        }
        
        if ( initFragment(requestContext, theme, fragment, pageActionAccess, isAjaxRequest) )
        {
            if ( collectFragments != null )
            {
                collectFragments.add( fragment );
            }
        }
    }

    protected boolean initFragment(RequestContext requestContext, 
                                   Theme theme, 
                                   ContentFragment fragment, 
                                   PageActionAccess pageActionAccess,
                                   boolean isAjaxRequest)
    {
        boolean fragmentSupportsActions = false;
        try
        {
            Decoration decoration = theme.getDecoration(fragment);
            fragment.setDecoration(decoration);
            fragmentSupportsActions = initActionsForFragment(requestContext, fragment, pageActionAccess, decoration, isAjaxRequest);
        }
        catch (Exception e)
        {
            log.warn("Unable to initalize actions for fragment "+fragment.getId(), e);
        }
        return fragmentSupportsActions;
    }

    /**
     * Reintializes all fragments with there decorations and portlet modes 
     * and winodw states after theme is restored from cache.
     * 
     * @param requestContext RequestContext of the current portal request.
     * @param theme
     * @param fragment
     * @param pageActionAccess
     * @param isAjaxRequest
     * @param validator
     * @param pathResolverCache
     */
    protected void initDepthFragmentDecorations(RequestContext requestContext,
                                                Theme theme,
                                                ContentFragment fragment, 
                                                PageActionAccess pageActionAccess,
                                                boolean isAjaxRequest,
                                                ResourceValidator validator,
                                                PathResolverCache pathResolverCache,
                                                boolean reloadActionList)
    {
        final List contentFragments = fragment.getFragments();
        
        if(contentFragments != null && contentFragments.size() > 0)
        {
            Iterator itr = contentFragments.iterator();
            while(itr.hasNext())
            {
                ContentFragment aFragment = (ContentFragment) itr.next();
                initDepthFragmentDecorations(requestContext, theme, aFragment,
                                             pageActionAccess, isAjaxRequest,
                                             validator, pathResolverCache, reloadActionList);
            }
        }

        try 
        {
            // PageTheme::getDecoration retrieves cached decoration only.
            Decoration decoration = theme.getDecoration(fragment);
            // re-init to set transient memebers.
            Properties config = ((DecorationFactoryImpl) decorationFactory).getConfiguration(decoration.getName(), fragment.getType());
            ((BaseDecoration) decoration).init(config, validator, pathResolverCache);
            // fragment is newly created on every request, so reset decoration for fragment.
            fragment.setDecoration(decoration);
            
            if (reloadActionList)
            {
                initActionsForFragment(requestContext, fragment, pageActionAccess, decoration, isAjaxRequest);
            }
        }
        catch (Exception e)
        {
            log.warn("Unable to initalize actions for fragment "+fragment.getId(), e);
        }
    }
    
    public void setMaximizeOnEdit(boolean maxOnEdit)
    {
        this.maxOnEdit = maxOnEdit;
        this.defaultDecoratorActionsFactory.setMaximizeOnEdit(maxOnEdit);
    }
    
    public boolean getMaximizeOnEdit()
    {
        return this.maxOnEdit;
    }
    
    public void setMaximizeOnConfig(boolean maxOnConfig)
    {
        this.maxOnConfig = maxOnConfig;
        this.defaultDecoratorActionsFactory.setMaximizeOnConfig(maxOnConfig);
    }
    
    public boolean getMaximizeOnConfig()
    {
        return this.maxOnConfig;
    }
    
    public void setMaximizeOnEditDefaults(boolean maxOnEditDefaults)
    {
        this.maxOnEditDefaults = maxOnEditDefaults;
        this.defaultDecoratorActionsFactory.setMaximizeOnEditDefaults(maxOnEditDefaults);
    }
    
    public boolean getMaximizeOnEditDefaults()
    {
        return this.maxOnEditDefaults;
    }
    
    public void setAutoSwitchingToEditDefaultsModes(boolean autoSwitchingToEditDefaultsModes)
    {
        this.autoSwitchingToEditDefaultsModes = autoSwitchingToEditDefaultsModes;
    }
    
    public boolean getAutoSwitchingToEditDefaultsModes()
    {
        return this.autoSwitchingToEditDefaultsModes;
    }
    
    public void setAutoSwitchingForConfigMode(boolean autoSwitchingForConfigMode)
    {
        this.autoSwitchingForConfigMode = autoSwitchingForConfigMode;
    }
    
    public boolean getAutoSwitchingForConfigMode()
    {
        return this.autoSwitchingForConfigMode;
    }
    
    private boolean isAutoSwitchableCustomMode(PortletWindow window, PortletMode customMode)
    {
        if (this.autoSwitchingForConfigMode && JetspeedActions.CONFIG_MODE.equals(customMode))
        {
            return true;
        }
        
        if (this.autoSwitchingToEditDefaultsModes && JetspeedActions.EDIT_DEFAULTS_MODE.equals(customMode) && portletFactory != null)
        {
            if (portletFactory.hasRenderHelperMethod(window.getPortletDefinition(), PortletMode.EDIT))
            {
                return true;
            }
        }
        
        return false;
    }
}
