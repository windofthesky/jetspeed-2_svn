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

import java.util.List;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.request.RequestContext;

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
    Theme getTheme(Page page, RequestContext requestContext);
    
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
     * @see Page
     * @see Fragment
     * @see RequestContext
     */
    Decoration getDecoration(Page page, Fragment fragment, RequestContext requestContext);
    
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
    List getPageDecorations(RequestContext request);

    /**
     * Get the portal-wide list of portlet decorations.
     * 
     * @return A list of portlet decorations of type <code>String</code>
     */    
    List getPortletDecorations(RequestContext request);
    
    /**
     * Get the portal-wide list of available layouts.
     * 
     * @return A list of layout portlets of type <code>LayoutInfo</code>
     */    
    List getLayouts(RequestContext request);
}
