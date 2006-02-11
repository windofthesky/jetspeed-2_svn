/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.decoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.container.window.FailedToRetrievePortletWindow;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.Valve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.window.PortletWindow;

/**
 * Assigns decorations and page actions to all of the portlet Fragments within
 * the current request. 
 * 
 * @see org.apache.jetspeed.om.page.Fragment 
 * @see org.apache.jetspeed.om.page.Page
 * @see org.apache.jetspeed.decoration.Decoration
 * @see org.apache.jetspeed.decoration.LayoutDecoration
 * @see org.apache.jetspeed.decoration.PortletDecoration
 * @see org.apache.jetspeed.decoration.Theme
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class DecorationValve extends AbstractValve implements Valve
{
    public static final String ACTION_IMAGE_EXTENSION_ATTR = "actionImageExtension";
    
    protected final static Log log = LogFactory.getLog(DecorationValve.class);
    
    private final DecorationFactory decorationFactory;

    private final PortletWindowAccessor windowAccessor;

    public DecorationValve(DecorationFactory decorationFactory, PortletWindowAccessor windowAccessor)
    {
        this.decorationFactory = decorationFactory;
        this.windowAccessor = windowAccessor;
    }
    

    public void invoke(RequestContext requestContext, ValveContext context) throws PipelineException
    {
        if (requestContext.getRequest().getParameter("clearThemeCache") != null)
        {
            decorationFactory.clearCache(requestContext);
        }
        ContentPage page = requestContext.getPage();
        Theme theme = decorationFactory.getTheme(page, requestContext);

        requestContext.setAttribute(PortalReservedParameters.PAGE_THEME_ATTRIBUTE, theme);
        
        PageActionAccess pageActionAccess = (PageActionAccess)requestContext.getAttribute(PortalReservedParameters.PAGE_EDIT_ACCESS_ATTRIBUTE);
        
        ContentFragment rootFragment = page.getRootContentFragment();
        
        initFragment(requestContext, theme, rootFragment, pageActionAccess); 
        
        context.invokeNext(requestContext);
    }

    public String toString()
    {
        return "DecorationValve";
    }
    
    /**
     * Returns the current <code>PortletMode</code> for the target 
     * <code>Fragment</code> in the current portal request.
     * 
     * @param requestContext RequestContext of the current portal request.
     * @param fragment Fragment for which the PortletMode has been requested.
     * @return <code>PortletMode</code> for the target 
     * <code>Fragment</code> in the current portal request.
     * 
     * @throws FailedToRetrievePortletWindow
     * @throws PortletEntityNotStoredException
     */
    protected PortletMode getPortletMode(RequestContext requestContext, ContentFragment fragment)
            throws FailedToRetrievePortletWindow, PortletEntityNotStoredException
    {
        NavigationalState nav = requestContext.getPortalURL().getNavigationalState();
        return nav.getMode(windowAccessor.getPortletWindow(fragment));
    }
    
    /**
     * Gets the window state for the current portlet window (fragment)
     * 
     * @return The window state for the current window
     * @throws PortletEntityNotStoredException 
     * @throws FailedToRetrievePortletWindow 
     * @throws Exception
     */
    protected WindowState getWindowState(RequestContext requestContext, ContentFragment fragment) throws FailedToRetrievePortletWindow, PortletEntityNotStoredException 
    {
        NavigationalState nav = requestContext.getPortalURL().getNavigationalState();
        return nav.getState(windowAccessor.getPortletWindow(fragment));    
    }
    
    /**
     * Builds and assigns a list of available portlet modes and window states for
     * the target <code>Fragment</code>.
     * 
     * @param requestContext RequestContext of the current portal request.
     * @param fragment Fragment to initialize modes and states for.
     * @return
     * @throws PortletEntityNotStoredException 
     * @throws FailedToRetrievePortletWindow 
     */
    protected void initActionsForFragment(RequestContext requestContext, ContentFragment fragment, PageActionAccess pageActionAccess, Decoration decoration) throws FailedToRetrievePortletWindow, PortletEntityNotStoredException
    {
        PortletEntity portletEntity = windowAccessor.getPortletWindow(fragment).getPortletEntity();
        PortletDefinitionComposite portlet = (PortletDefinitionComposite) portletEntity.getPortletDefinition();
        ContentPage page = requestContext.getPage();
        
        if (null == portlet)
        {
            return; // allow nothing
        }

        List actions = new ArrayList();

        PortletMode mode = getPortletMode(requestContext, fragment);
        WindowState state = getWindowState(requestContext, fragment);

        ContentTypeSet content = portlet.getContentTypeSet();

        String portletName = portlet.getUniqueName();
        PortletWindow window = windowAccessor.getPortletWindow(fragment);        
        
        boolean isRootLayout = fragment.equals(page.getRootFragment());

        if ( isRootLayout )
        {
            List pageModeActions = getPageModes(requestContext, pageActionAccess, page);
            actions.addAll(pageModeActions);
        }
        else if ( !Fragment.LAYOUT.equals(fragment.getType()) )
        {
            List portletModeActions = getPortletModes(requestContext, pageActionAccess, mode, content, portletName, window, fragment);
            actions.addAll(portletModeActions);
            List stateActions = getWindowStates(requestContext, pageActionAccess, state, portletName, window, fragment);
            actions.addAll(stateActions);
        }
        
        decoration.setActions(actions);
    }
    
    /**
     * Builds a list of portlet modes that can be executed on the current
     * <code>fragment</code> excluding the portlet's current mode.
     * 
     * @param requestContext RequestContext of the current portal request.
     * @param pageActionAccess
     * @param mode
     * @param content
     * @param portletName
     * @param window
     * @param fragment
     * @return <code>java.util.List</code> of modes excluding the current one.
     */
    protected List getPortletModes(RequestContext requestContext, PageActionAccess pageActionAccess, PortletMode mode, ContentTypeSet content, String portletName, PortletWindow window, ContentFragment fragment)
    {
        String fragmentId = fragment.getId();
        Decoration decoration = fragment.getDecoration();
        List portletModes = new ArrayList();
        
        if (mode.equals(PortletMode.VIEW))
        {
            if (content.supportsPortletMode(PortletMode.EDIT) && pageActionAccess.isEditAllowed()
                    && pageActionAccess.checkPortletMode(fragmentId, portletName, PortletMode.EDIT))
            {
                portletModes.add(createPortletModeAction(window, JetspeedActions.EDIT, PortletMode.EDIT, requestContext, decoration));
            }
            if (content.supportsPortletMode(PortletMode.HELP)
                    && pageActionAccess.checkPortletMode(fragmentId, portletName, PortletMode.HELP))
            {
                portletModes.add(createPortletModeAction(window, JetspeedActions.HELP, PortletMode.HELP, requestContext, decoration));
            }
        }
        else if (mode.equals(PortletMode.EDIT))
        {
            if (pageActionAccess.checkPortletMode(fragmentId, portletName, PortletMode.VIEW))
            {
                portletModes.add(createPortletModeAction(window, JetspeedActions.VIEW, PortletMode.VIEW, requestContext, decoration));
            }
            if (content.supportsPortletMode(PortletMode.HELP)
                    && pageActionAccess.checkPortletMode(fragmentId, portletName, PortletMode.HELP))
            {
                portletModes.add(createPortletModeAction(window, JetspeedActions.HELP, PortletMode.HELP, requestContext, decoration));
            }
        }
        else
        // help
        {
            if (pageActionAccess.checkPortletMode(fragmentId, portletName, PortletMode.VIEW))
            {
                portletModes.add(createPortletModeAction(window, JetspeedActions.VIEW, PortletMode.VIEW, requestContext, decoration));
            }
            if (content.supportsPortletMode(PortletMode.EDIT) && pageActionAccess.isEditAllowed()
                    && pageActionAccess.checkPortletMode(fragmentId, portletName, PortletMode.EDIT))
            {
                portletModes.add(createPortletModeAction(window, JetspeedActions.EDIT, PortletMode.EDIT, requestContext, decoration));
            }
        }
        
        return portletModes;
    }
    
    
    /**
     * Builds a list of portlet modes that can be executed on the current
     * <code>fragment</code> excluding the portlet's current mode.
     * 
     * @param requestContext RequestContext of the current portal request.
     * @param pageActionAccess
     * @param mode
     * @param content
     * @param portletName
     * @param window
     * @param fragment
     * @return <code>java.util.List</code> of modes excluding the current one.
     * @throws PortletEntityNotStoredException 
     */
    protected List getPageModes(RequestContext requestContext, PageActionAccess pageActionAccess, ContentPage page)
    {
        List pageModes = new ArrayList();
        
        ContentFragment fragment = page.getRootContentFragment();
        Decoration decoration = fragment.getDecoration();
        
        try
        {
            PortletWindow window = windowAccessor.getPortletWindow(fragment);        
            PortletEntity portletEntity = window.getPortletEntity();
            PortletDefinitionComposite portlet = (PortletDefinitionComposite) portletEntity.getPortletDefinition();        
            ContentTypeSet content = portlet.getContentTypeSet();

            NavigationalState nav = requestContext.getPortalURL().getNavigationalState();
            PortletMode mode = nav.getMode(window);
            WindowState state = nav.getState(window);
            
            if (mode.equals(PortletMode.HELP) || !state.equals(WindowState.NORMAL))
            {
                // switch back to VIEW mode and NORMAL state.
                DecoratorAction action = createDecoratorAction(PortletMode.VIEW.toString(), decoration);                        
                PortalURL portalURL = requestContext.getPortalURL();
                action.setAction(portalURL.createPortletURL(window, PortletMode.VIEW, WindowState.NORMAL, portalURL.isSecure()).toString());
                pageModes.add(action);
            }
            else if ( pageActionAccess.isEditAllowed() )
            {
                String targetMode = pageActionAccess.isEditing() ? PortletMode.VIEW.toString() : PortletMode.EDIT.toString();
                DecoratorAction action = createDecoratorAction(targetMode, decoration);
                PortalURL portalURL = requestContext.getPortalURL();
                HashMap parameters = new HashMap();
                String[] paramValues = new String[]{targetMode};
                parameters.put("pageMode",paramValues);

                // Use an ActionURL to set the oposite pageMode and always set VIEW mode and state NORMAL 
                action.setAction(portalURL.createPortletURL(window, parameters, PortletMode.VIEW, WindowState.NORMAL, true, portalURL.isSecure()).toString());
                pageModes.add(action);
                
                if (content.supportsPortletMode(PortletMode.HELP))
                {
                    action = createDecoratorAction(PortletMode.HELP.toString(), decoration);
                    if ( pageActionAccess.isEditing() )
                    {
                        // force it back to VIEW mode first with an ActionURL, as well as setting HELP mode and MAXIMIZED state
                        paramValues[0] = PortletMode.VIEW.toString();
                        action.setAction(portalURL.createPortletURL(window, parameters, PortletMode.HELP, WindowState.MAXIMIZED, true, portalURL.isSecure()).toString());
                    }
                    else
                    {
                        // switch to mode HELP and state MAXIMIZED
                        action.setAction(portalURL.createPortletURL(window,PortletMode.HELP, WindowState.MAXIMIZED, portalURL.isSecure()).toString());
                    }
                    pageModes.add(action);
                }
            }
        }
        catch (Exception e)
        {
            log.warn("Unable to initalize PageLayout actions for fragment "+fragment.getId(), e);
            pageModes = null;
        }
        
        return pageModes;
    }  
    
    /**
     * Builds a list of window states that can be executed on the current
     * <code>fragment</code> excluding the portlet's current window state.
     * 
     * @param requestContext RequestContext of the current portal request.
     * @param pageActionAccess
     * @param state
     * @param portletName
     * @param window
     * @param fragment
     * @return <code>java.util.List</code> of window states excluding the current one.
     */
    protected List getWindowStates(RequestContext requestContext, PageActionAccess pageActionAccess, WindowState state, String portletName, PortletWindow window, ContentFragment fragment)
    {
        String fragmentId = fragment.getId();
        Decoration decoration = fragment.getDecoration();
        ArrayList actions = new ArrayList();
            
        if (state.equals(WindowState.NORMAL))
        {
            if (pageActionAccess.checkWindowState(fragmentId, portletName, WindowState.MINIMIZED))
            {
                actions.add(createWindowStateAction(window, JetspeedActions.MINIMIZE, WindowState.MINIMIZED,
                        requestContext, decoration));
            }
            if (pageActionAccess.checkWindowState(fragmentId, portletName, WindowState.MAXIMIZED))
            {
                actions.add(createWindowStateAction(window, JetspeedActions.MAXIMIZE, WindowState.MAXIMIZED,
                        requestContext, decoration));
            }
        }
        else if (state.equals(WindowState.MAXIMIZED))
        {
            if (pageActionAccess.checkWindowState(fragmentId, portletName, WindowState.MINIMIZED))
            {
                actions.add(createWindowStateAction(window, JetspeedActions.MINIMIZE, WindowState.MINIMIZED,
                        requestContext, decoration));
            }
            if (pageActionAccess.checkWindowState(fragmentId, portletName, JetspeedActions.RESTORED))
            {
                actions.add(createWindowStateAction(window, JetspeedActions.RESTORE, WindowState.NORMAL,
                        requestContext, decoration));
            }
        }
        else
        // minimized
        {
            if (pageActionAccess.checkWindowState(fragmentId, portletName, WindowState.MAXIMIZED))
            {
                actions.add(createWindowStateAction(window, JetspeedActions.MAXIMIZE, WindowState.MAXIMIZED,
                        requestContext, decoration));
            }
            if (pageActionAccess.checkWindowState(fragmentId, portletName, JetspeedActions.RESTORED))
            {
                actions.add(createWindowStateAction(window, JetspeedActions.RESTORE, WindowState.NORMAL,
                        requestContext, decoration));
            }
        }
        
        return actions;
    }
    
    /**
     * Creates a Decorator PortletMode Action to be added to the list of actions
     * decorating a portlet.
     */
    protected DecoratorAction createPortletModeAction(PortletWindow window, String actionName, PortletMode mode,
            RequestContext requestContext, Decoration decoration)
    {
        DecoratorAction action = createDecoratorAction(actionName, decoration);        
        
        PortalURL portalURL = requestContext.getPortalURL();
        action.setAction(portalURL.createPortletURL(window, mode, null, portalURL.isSecure())
                .toString());
        return action;
    }
    
    protected DecoratorAction createDecoratorAction(String actionName, Decoration decoration)
    {
        String imageExt = ".gif";
        if (imageExt == null)
        {
            imageExt = ".gif";
        }
        String link = decoration.getResource("images/" + actionName + ".gif");
        return new DecoratorAction(actionName, actionName, link);
    }

    /**
     * Creates a Decorator WindowState Action to be added to the list of actions
     * decorating a portlet.
     */
    protected DecoratorAction createWindowStateAction(PortletWindow window, String actionName, WindowState state,
            RequestContext requestContext, Decoration decoration)
    {
        DecoratorAction action = createDecoratorAction(actionName, decoration);
        PortalURL portalURL = requestContext.getPortalURL();
        action.setAction(portalURL.createPortletURL(window, null, state, portalURL.isSecure())
                .toString());
        return action;
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
    protected void initFragment(RequestContext requestContext, Theme theme, ContentFragment fragment, PageActionAccess pageActionAccess) 
    {
        final List contentFragments = fragment.getContentFragments();
        
        if(contentFragments != null && contentFragments.size() > 0)
        {
            Iterator itr = contentFragments.iterator();
            while(itr.hasNext())
            {
                ContentFragment aFragment = (ContentFragment) itr.next();
                initFragment(requestContext, theme, aFragment, pageActionAccess);
            }
        }
        
        try
        {
            fragment.setDecoration(theme.getDecoration(fragment));
            initActionsForFragment(requestContext, fragment, pageActionAccess, theme.getDecoration(fragment));
        }
        catch (Exception e)
        {
            log.warn("Unable to initalize actions for fragment "+fragment.getId());
        }
       
    }   

}
