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
package org.apache.jetspeed.container.session;

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
     * Creates a navigational state context for the given request context.
     * Depending on the implementation, navigational state can be retrieved from 
     * a persistence store to recover the state of a page such as portlet modes 
     * and window states of portlets on a page.
     *  
     * @param context The request context for which this navigational state is valid.
     * @return A new navigational state context for the given request.
     */
    NavigationalState create(RequestContext context);

    /**
     * Release a navigational state context back to the pool.
     * 
     * @param context
     */
    void release(NavigationalState state);
    
    /**
     * Save the navigational state to persistence store for the given context.
     *   
     * @param context The request context for retrieving user and other information.
     * @param navContext The current navigational state context for the given request.
     */
    void store(RequestContext context, NavigationalState navContext);
     
    
    /**
     * Creates a Portlet URL representing the URL of the request.
     * 
     * @param context The ubiqitious request context.
     * @return A new Portal URL
     */
    PortalURL createURL(RequestContext context);
    
    /**
     * Keys for URL encoding
     * @return
     */
    static public final int PREFIX = 0;    
    static public final int ACTION = 1;
    static public final int MODE = 2;
    static public final int STATE = 3;
    static public final int RENDER_PARAM = 4;
    static public final int ID = 5;
    static public final int PREV_MODE = 6;
    static public final int PREV_STATE = 7;
    static public final int KEY_DELIM = 8;
    static public final int NAV_MAX = 9;        
    
    /**
     * All navigation strings are configurable.
     * Use this method to lookup the name of a navigation key
     * used in Portlet URLs.
     * 
     * @param key
     * @return the configured name of the navigation key in the URL
     */
    String getNavigationKey(int key);       
    
    /**
     * Given a navigational key, such as s_14 (state_windowid), return the window id portion.
     * @param key The full key with navigation type and window id (mode_windowid)
     * @return The window id from the key
     */    
    String getWindowIdFromKey(String key);
    
}
