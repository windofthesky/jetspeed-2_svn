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
package org.apache.jetspeed.container.state;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.RequestContext;

/**
 * NavigationalState
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface NavigationalStateComponent
{        
    /**
     * Creates a navigational state.
     * Depending on the implementation, a navigational state context can be retrieved from 
     * a persistence store to recover the state of a page such as portlet modes 
     * and window states of portlets on a page.
     *  
     * @return A new navigational state.  This method will never return <code>null</code>
     * @throws FailedToCreateNavStateException if the nav state could not be created.  Under normal
     * circumstances, this should not happen.
     */
    NavigationalState create();

    /**
     * Creates a Portal URL representing the URL of the request.
     * 
     * @param context The ubiqitious request context.
     * @return A new Portal URL.  This method will never return <code>null</code>;
     * @throws FailedToCreatePortalUrlException if the portelt url could not be created.  Under normal
     * circumstances, this should not happen.
     */
    PortalURL createURL(RequestContext context);
    
    /**
     * Given a window state name, look up its object.
     * Ensures that we always use the same objects for WindowStates
     * allowing for comparison by value.
     * 
     * @param name The string representation of the window state.
     * @return The corresponding WindowState object
     */
    WindowState lookupWindowState(String name);

    /**
     * Given a portlet mode name, look up its object.
     * Ensures that we always use the same objects for Portlet Modes
     * allowing for comparison by value.
     * 
     * @param name The string representation of the portlet mode.
     * @return The corresponding PortletMode object
     */    
    PortletMode lookupPortletMode(String name);    
}
