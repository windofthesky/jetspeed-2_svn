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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.security.auth.Subject;

import org.apache.jetspeed.administration.PortalConfiguration;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.locator.TemplateDescriptor;
import org.apache.jetspeed.locator.TemplateLocatorException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.portlet.HeadElement;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.util.KeyValue;

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
     * 
     * @return
     */
    ContentFragment getCurrentFragment();

    /**
     * 
     * @param f
     */
    void setCurrentFragment(ContentFragment f);

    void setCurrentLayout();

    /**
     * 
     * @return
     */
    ContentFragment getCurrentLayout();

    /**
     * 
     * @return
     */
    ContentPage getPage();

    /**
     * 
     * @return
     */
    List[] getColumns();

    List getColumnSizes();

    /**
     * 
     * @param f
     *                  Fragment whose <code>PortletWindow</code> we want to
     *                  retrieve.
     * @return The PortletWindow represented by the current fragment.
     * @throws Exception
     */
    PortletWindow getPortletWindow(ContentFragment f) throws Exception;

    /**
     * Checks the the visibilty of this fragment with respect to the current
     * RenderReqeust.
     * 
     * @param f
     *                  Fragment
     * @return whether or not the Fragment in question should be considered
     *              visible during rendering.
     */
    boolean isHidden(ContentFragment f);

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
    TemplateDescriptor getTemplate(String path, String templateType)
            throws TemplateLocatorException;

    TemplateDescriptor getDecoration(String path, String templateType)
            throws TemplateLocatorException;

    String includeTemplate(String template, String templateType)
            throws IOException;

    String includeDecoration(String template, String templateType)
            throws IOException;

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
    String decorateAndInclude(ContentFragment f) throws Exception;

    /**
     * Gets the list of decorator actions for a window. Each window (on each
     * page) has its own collection of actionAccess flags associated with it.
     * 
     * @return A list of actions available to the current window, filtered by
     *              securty access and current state.
     * @throws Exception
     */
    List getDecoratorActions();

    /**
     * Gets the list of decorator actions for a page. Each layout fragment on a
     * page has its own collection of actionAccess flags associated with it.
     * 
     * @return A list of actions available to the current window, filtered by
     *              securty access and current state.
     * @throws Exception
     */
    List getPageDecoratorActions() throws Exception;

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
    String getTitle(ContentFragment f);

    /**
     * 
     * <p>
     * getTitle
     * </p>
     * Returns the appropriate for the title based on locale prferences
     * 
     * @return
     */
    String getTitle();

    Object getComponent(String name);

    String getAbsoluteUrl(String relativePath);

    Subject getSubject();

    boolean getLoggedOn();

    String getBasePath();

    String getPageBasePath();    
    
    String renderPortletWindow(String windowId, String portletId);
    
    /**
     * Returns stringified one from the element
     * 
     * @param element
     * @return
     */
    String getElementHtmlString(HeadElement element);
    
    /**
     * Returns all the contributed head elements from the fragment and its child fragments.
     * @param f
     * @return
     * @throws Exception
     */
    List<KeyValue<String, HeadElement>> getHeadElements(ContentFragment f) throws Exception;;

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
    
    PortalConfiguration getPortalConfiguration();
    
}