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
package org.apache.jetspeed.container.state.impl;

import org.apache.jetspeed.cache.JetspeedContentCache;
import org.apache.jetspeed.container.PageHistoryValve;
import org.apache.jetspeed.request.RequestContext;

/**
 * SessionFullClearOnChangePageNavigationalState, stores all nav parameters in the session, including render parameters
 *
 * @author <a href="mailto:kmoh.raj@gmail.com">Mohan Kannapareddy</a>
 * @version $Id$
 */

public class SessionFullExtendedNavigationalState extends SessionFullNavigationalState 
{
	private boolean clearStateOnPageChangeEnabled = false;

	
	public SessionFullExtendedNavigationalState(NavigationalStateCodec codec,JetspeedContentCache cache)
	{
		super(codec, cache);
	}
    public SessionFullExtendedNavigationalState(NavigationalStateCodec codec, JetspeedContentCache cache, JetspeedContentCache decorationCache)
    {
        super(codec, cache, decorationCache);
    }

    public SessionFullExtendedNavigationalState(NavigationalStateCodec codec, JetspeedContentCache cache, JetspeedContentCache decorationCache, boolean clearStateOnPageChangeEnabled)
    {
        super(codec, cache, decorationCache);
        this.clearStateOnPageChangeEnabled = clearStateOnPageChangeEnabled;
    }
    
    protected boolean clearPagePortletsModeAndWindowState(RequestContext context)
    {
        String contextKey = PageHistoryValve.REQUEST_CLEAR_PORTLETS_MODE_AND_WINDOWSTATE_KEY;
        boolean result = false;
        if (clearStateOnPageChangeEnabled)
        {
            Boolean pageNavigationEvent = (Boolean) context.getAttribute(contextKey);
            if ((pageNavigationEvent != null))
            {
                result = pageNavigationEvent.booleanValue();
            }
        }
        //Just to be safe make it false
        context.setAttribute(contextKey, Boolean.FALSE);
        
    	return result;
    }
    
    public synchronized boolean sync(RequestContext context)
    {
        // JS2-806, check the session for a psuedo inter page navigation.
        boolean resetPagePortlets = false;
        if (clearStateOnPageChangeEnabled)
        {
            resetPagePortlets = clearPagePortletsModeAndWindowState(context);
            if (log.isDebugEnabled())
            {
                log.debug("resetPagePortlets:" + resetPagePortlets);
            }
        }

        // push the information up to SessionNavigationalState, so that we can handle it appropriately there
        setClearPortletsModeAndWindowStateEnabled(resetPagePortlets);
        //Inform the super
        return super.sync(context);
    }
}
