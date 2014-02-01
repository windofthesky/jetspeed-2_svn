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

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;

import java.util.List;
import java.util.Set;

/**
 * Factory class used for locating Decorations and Themes for 
 * Fragments and pages.
 * 
 * @see org.apache.jetspeed.decoration.Decoration
 * @see org.apache.jetspeed.decoration.PortletDecoration
 * @see org.apache.jetspeed.decoration.LayoutDecoration
 * @see org.apache.jetspeed.decoration.Theme
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public interface DecorationFactory
{
    /** Default nested layout portlet decorator */
    String DEFAULT_NESTED_LAYOUT_PORTLET_DECORATOR = "clear";
    
    /**
     * Returns a theme containing all of the Decorations for all of 
     * the layouts on the current page.
     * 
     * @param page Page whose theme we are requesting
     * @param requestContext Current portal request.
     * @return Theme for this page based on the current portal request.
     * 
     * @see Theme
     * @see RequestContext
     */
    Theme getTheme(ContentPage page, RequestContext requestContext);
    
    /**
     * Returns a names portlet Decoration appropriate to the 
     * current request conetext.
     * 
     * @param name Formal name of the decoration.
     * @param requestContext Current portal request.
     * 
     * @return Decoration requested.  If the decoration does not exist, an
     * empty Decoration will be created "in memory" and a message should be logged
     * informing the admin that non-existent decoration has been requested.
     * 
     * @see RequestContext
     * @see PortletDecoration
     */
    PortletDecoration getPortletDecoration(String name, RequestContext requestContext);
    
    /**
     * Returns a named layout Decoration appropriate to the 
     * current request conetext.
     * 
     * @param name Formal name of the decoration.
     * @param requestContext Current portal request.
     * 
     * @return Decoration requested.  If the decoration does not exist, an
     * empty Decoration will be created "in memory" and a message should be logged
     * informing the admin that non-existent decoration has been requested.
     * 
     * @see LayoutDecoration
     * @see RequestContext
     */
    LayoutDecoration getLayoutDecoration(String name, RequestContext requestContext);
    
    /**
     * Returns a Decoration for a specific <code>Fragment</code> contained
     * within the specified <code>Page</code>.
     * 
     * @param page Current page
     * @param fragment Fragment whose decoration we require.
     * @param requestContext Current portal request.
     * 
     * @return Decoration requested.  If the decoration does not exist, an
     * empty Decoration will be created "in memory" and a message should be logged
     * informing the admin that non-existent decoration has been requested.
     * 
     * @param page
     * @param fragment
     * @param requestContext
     */
    Decoration getDecoration(ContentPage page, ContentFragment fragment, RequestContext requestContext);
    
    /**
     * Indicates whether /desktop is enabled for the current portal request.
     * Located here due to range of jetspeed components which need this information and
     * already have a DecorationFactory reference.
     * 
     * @param requestContext current portal request.
     * 
     * @return true if /desktop is enabled for the current portal request, otherwise false
     */
    boolean isDesktopEnabled( RequestContext requestContext );
    
    /**
     * Clears the lookup cache of all previous located pathes.  This only
     * clears the cache the <code>RequestContext</code>'s current user.  This
     * will generally delegate the cache operation to the <code>PathResolverCache</code>
     * currently in use. 
     * 
     * @param requestContext Current portal request.
     * 
     * @see RequestContext
     * @see PathResolverCache
     */
    void clearCache(RequestContext requestContext);
    
    /**
     * Get the portal-wide list of page decorations.
     * 
     * @return A list of page decorations of type <code>String</code>
     */
    Set<String> getPageDecorations(RequestContext request);

    /**
     * Get the portal-wide list of portlet decorations.
     * 
     * @return A list of portlet decorations of type <code>String</code>
     */    
    Set<String> getPortletDecorations(RequestContext request);
    
    /**
     * Get the portal-wide list of available layouts.
     * 
     * @return A list of layout portlets of type <code>LayoutInfo</code>
     */    
    List<LayoutInfo> getLayouts(RequestContext request);
    
    /**
     * Get the portal-wide list of available desktop page decorations.
     * 
     * @return A list of desktop skins of type <code>String</code>
     */    
    Set<String> getDesktopPageDecorations(RequestContext request);
    
    /**
     * Get the portal-wide list of desktop portlet decorations.
     * 
     * @return A list of desktop skins of type <code>String</code>
     */
    Set<String> getDesktopPortletDecorations(RequestContext request);
    
    /**
     * Get the path to the layout decorations directory.
     * 
     * @return path to layout decorations directory
     */
    String getLayoutDecorationsBasePath();
    
    /**
     * Get the path to the portlet decorations directory.
     * 
     * @return path to portlet decorations directory
     */
    String getPortletDecorationsBasePath();
    
    /**
     * Get the default desktop layout decoration to be used when
     * selected layout decoration does not support /desktop.
     * 
     * @return default desktop layout decoration.
     */
    String getDefaultDesktopLayoutDecoration();
    
    /**
     * Set the default desktop layout decoration to be used when
     * selected layout decoration does not support /desktop.
     */
    void setDefaultDesktopLayoutDecoration( String newOne );
    
    /**
     * Get the default desktop portlet decoration to be used when
     * selected portlet decoration does not support /desktop.
     * 
     * @return default desktop portlet decoration.
     */
    String getDefaultDesktopPortletDecoration();
    
    /**
     * Set the default desktop portlet decoration to be used when
     * selected portlet decoration does not support /desktop.
     */
    void setDefaultDesktopPortletDecoration( String newOne );

    /**
     * Get the default portal portlet decoration to be used when
     * selected portlet decoration does not support /portal.
     * 
     * @return default desktop portlet decoration.
     */
    String getDefaultPortletDecoration();
    
    /**
     * Get the default portlet layout decoration to be used when
     * selected layout decoration does not support /portal.
     * 
     * @return default desktop layout decoration.
     */
    String getDefaultLayoutDecoration();
    
}
