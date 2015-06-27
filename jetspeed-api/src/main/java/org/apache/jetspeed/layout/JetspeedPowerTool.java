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
package org.apache.jetspeed.layout;

import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.decoration.DecoratorAction;
import org.apache.jetspeed.locator.TemplateDescriptor;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.util.KeyValue;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.security.auth.Subject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JetspeedPowerTool
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedPowerTool.java 516448 2007-03-09 16:25:47Z ate $
 */

public interface JetspeedPowerTool
{

    public static final String GENERIC_TEMPLATE_TYPE = "generic";

    public static final String FRAGMENT_PROCESSING_ERROR_PREFIX = "fragment.processing.error.";

    public static final String FRAGMENT_ATTR = "fragment";

    public static final String LAYOUT_ATTR = "layout";

    public static final String HIDDEN = "hidden";

    public static final String LAYOUT_TEMPLATE_TYPE = "layout";

    public static final String DECORATOR_TYPE = "decorator";

    /**
     * Gets the window state for the current portlet window (fragment)
     * 
     * @return The window state for the current window
     * @throws Exception
     */
    WindowState getWindowState() throws Exception;

    /**
     * Gets the internal (portal) window state for the current portlet window (fragment)
     * 
     * @return The window state for the current window
     * @throws Exception
     */
    WindowState getMappedWindowState() throws Exception;

    /**
     * Gets the portlet mode for a current portlet window (fragment)
     * 
     * @return The portlet mode of the current window
     * @throws Exception
     */
    PortletMode getPortletMode() throws Exception;

    /**
     * Gets the internal (portal) portlet mode for a current portlet window (fragment)
     * 
     * @return The portlet mode of the current window
     * @throws Exception
     */
    PortletMode getMappedPortletMode() throws Exception;

    /**
     * The Power Tool tracks which fragment is being rendered. This method returns the current fragment.
     *
     * @return the current fragment being rendered
     */
    ContentFragment getCurrentFragment();

    /**
     * The Power Tool tracks which fragment is being rendered. This method sets the current fragment
     *
     * @param fragment current fragment to render
     */
    void setCurrentFragment(ContentFragment fragment);

    /**
     * The Power Tool tracks which layout is being rendered. This method sets the current layout to the current fragment,
     * if the current fragment is a layout
     */
    void setCurrentLayout();

    /**
     * The Power Tool tracks which layout is being rendered. This method gets the current layout
     * @return the current layout
     */
    ContentFragment getCurrentLayout();

    /**
     * The Power Tool tracks which page is being rendered. This method gets the current page
     *
     * @return the current page being rendered
     */
    ContentPage getPage();

    /**
     * For tabular layouts, the Power Tool can provide the set of columns being rendered for layout
     *
     * @deprecated
     * @return an array of lists of content {@link org.apache.jetspeed.om.page.ContentFragment}
     */
    List[] getColumns();

    /**
     * For tabular layouts, return the column sizes, usually as percentages but can be any table sizing such pixels
     *
     * @return list of column sizes, usually percentages
     */
    List<String> getColumnSizes();

    /**
     * Return the associated PortletWindow for a given fragment
     *
     * @param fragment
     *                  Fragment whose <code>PortletWindow</code> we want to
     *                  retrieve.
     * @return The PortletWindow represented by the current fragment.
     * @throws Exception
     */
    PortletWindow getPortletWindow(ContentFragment fragment) throws Exception;

    /**
     * Checks the the visibilty of this fragment with respect to the current
     * RenderReqeust.
     * 
     * @param fragment
     *                  Fragment
     * @return whether or not the Fragment in question should be considered
     *              visible during rendering.
     */
    boolean isHidden(ContentFragment fragment);

    /**
     * Retrieves a template using the Jetspeed template locator algorithm
     * @see org.apache.jetspeed.locator.TemplateLocator
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
    TemplateDescriptor getTemplate(String path, String templateType)
            throws TemplateLocatorException;

    /**
     * Retrieves a decorator using the Jetspeed template locator algorithm
     * @see org.apache.jetspeed.locator.TemplateLocator
     *
     * @param path
     *                  Expected to the template. This may actually be changed by the
     *                  TL service based the capability and localization information
     *                  provided by the client.
     * @param templateType
     *                  Type off template we are interested in.
     * @return Template object containing the pertinent information required to
     *              inlcude the request template path in the current response
     * @throws TemplateLocatorException
     *                   if the <code>path</code> does not exist.
     */
    TemplateDescriptor getDecoration(String path, String templateType)
            throws TemplateLocatorException;

    /**
     * Include a template into the rendering stream at the current position in layout
     *
     * @param template name of template to include
     * @param templateType the type either portlet or layout
     * @return the content of the layout
     * @throws IOException
     */
    String includeTemplate(String template, String templateType)
            throws IOException;

    /**
     * Include a decorator into the rendering stream at the current position in layout
     *
     * @param template name of template to include
     * @param templateType the type either portlet or layout
     * @return the content of the layout
     * @throws IOException
     */
    String includeDecoration(String template, String templateType)
            throws IOException;

    /**
     * Decorate and include fragment content.
     *
     * @param fragment
     *                  Fragment to include and decorate
     * @throws Exception
     * @return String path to the decorator.
     */
    String decorateAndInclude(ContentFragment fragment) throws Exception;

    /**
     * Gets the list of decorator actions for a window. Each window (on each
     * page) has its own collection of actionAccess flags associated with it.
     * 
     * @return A list of {@link org.apache.jetspeed.decoration.DecoratorAction} available to the current window, filtered by
     *              security access and current state.
     * @throws Exception
     */
    List<DecoratorAction> getDecoratorActions();

    /**
     * Gets the list of decorator actions for a page. Each layout fragment on a
     * page has its own collection of actionAccess flags associated with it.
     * 
     * @return A list of actions available to the current window, filtered by
     *              security access and current state.
     * @throws Exception
     */
    List<DecoratorAction> getPageDecoratorActions() throws Exception;

    /**
     * Returns the appropriate for the title based on locale preferences
     * 
     * @param fragment
     * @return the title of the fragment
     */
    String getTitle(ContentFragment fragment);

    /**
     * Returns the appropriate for the title based on locale preferences
     * 
     * @return the title of the current fragment
     */
    String getTitle();

    /**
     * Shortcut convenience method to lookup a Jetspeed service via {@link org.apache.jetspeed.components.ComponentManagement#lookupComponent(Class)}
     *
     * @param name the name of the component service
     * @return a component service from container
     */
    Object getComponent(String name);

    /**
     * Return the absolute portal URL from a given relative url
     *
     * @param relativePath
     * @return the full portal URL
     */
    String getAbsoluteUrl(String relativePath);

    /**
     * Returns the current secure subject for the current request thread
     *
     * @return A Java {@link javax.security.auth.Subject}
     */
    Subject getSubject();

    /**
     * Is the current request being made by a logged on user
     * @return true if user is logged on
     */
    boolean getLoggedOn();

    /**
     * Gets the Portal Base Path
     * @see {@link org.apache.jetspeed.container.url.PortalURL#getBasePath()}
     *
     * @return the base path portion of the URL
     */
    String getBasePath();

    /**
     * Returns the current Portal Page base path without possible encoded NavigationalState parameter.
     * @see {@link org.apache.jetspeed.container.url.PortalURL#getBasePath()}
     * @return
     */
    String getPageBasePath();

    /**
     * For given window and portlet ids, render a portlet window
     *
     * @param windowId
     * @param portletId
     * @return the rendered content
     */
    String renderPortletWindow(String windowId, String portletId);
    
    /**
     * Returns string representation of a head HTML DOM element
     * 
     * @param element
     * @return string representation
     */
    String getElementHtmlString(HeadElement element);
    
    /**
     * Returns all the contributed head elements from the fragment and its child fragments.
     * @param fragment
     * @return
     * @throws Exception
     */
    List<KeyValue<String, HeadElement>> getHeadElements(ContentFragment fragment) throws Exception;;

    /**
     * Returns all the contributed head elements from the current fragment and its child fragments.
     * @return
     * @throws Exception
     */
    List<KeyValue<String, HeadElement>> getHeadElements() throws Exception;
    
    /**
     * Returns true if head element has dojo library inclusion.
     * @param headElements
     * @return
     */
    boolean isDojoEnabled(List<KeyValue<String, HeadElement>> headElements);
    
    /**
     * Determine if ajax customization is enabled
     * @return true when ajax customization enabled
     */
    boolean isAjaxCustomizationEnabled();
    
    /**
     * @return an unmodifiable Map of the User "info" attributes or null if not authenticated
     */
    Map<String,String> getUserAttributes();
    
    /**
     * @return the value for the User "info" attribute or the provided defaultValue if not authenticated or if the attribute is undefined
     */
    String getUserAttribute(String attributeName, String defaultValue);

    /**
     * Helper to retrieve the {@link org.apache.jetspeed.administration.PortalConfiguration}
     * @return the portal configuration
     */
    PortalConfiguration getPortalConfiguration();

    /**
     * Is AutoRefresh support for automatic portlet rendering enabled
     * @return true when auto refresh is enabled in Jetspeed properties
     */
    boolean isAutoRefreshEnabled();
}