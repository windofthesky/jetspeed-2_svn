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
     * Keys for URL encoding
     * @return
     */
    String getActionKey();
    String getRenderParamKey();    
    String getModeKey();    
    String getPreviousModeKey();        
    String getStateKey();    
    String getPreviousStateKey();
    
}
