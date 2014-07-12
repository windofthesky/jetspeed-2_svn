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
package org.apache.jetspeed.container;

import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Valve basically maintains the page navigation history by maintaining a previous page id in the session.
 * Required by JS2-806
 * </p>
 * 
 * @author <a href="mailto:kmoh.raj@gmail.com">Mohan Kannapareddy</a>
 * @version $Id$
 */
public class PageHistoryValve extends AbstractValve
{
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    // SessionFullExtendedNavigationalState object needs this.
    public static final String REQUEST_CLEAR_PORTLETS_MODE_AND_WINDOWSTATE_KEY = "clearPortletsModeAndWindowState";
    
    private final String SESSION_PREVIOUS_PAGEID_KEY = "PreviousPageId";
    private boolean valveDisabled = false;
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.pipeline.valve.AbstractValve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        if (valveDisabled)
        {
            if (log.isDebugEnabled())
            {
                log.debug(toString() + " is DISABLED");
            }
        }
        else
        {   //OK, the valve is enabled check and see if are a inter-page nav.
            try
            {
                // create a session if not already created, necessary for Tomcat 5
                request.getRequest().getSession(true);
                
                ContentPage page = request.getPage();
                String curPageId = page.getId();
                
                String prevPageId = (String) request.getSessionAttribute(SESSION_PREVIOUS_PAGEID_KEY);
                if (prevPageId == null)
                {
                    //First time, lets set it
                    request.setSessionAttribute(SESSION_PREVIOUS_PAGEID_KEY, curPageId);
                    if (log.isDebugEnabled())
                    {
                        log.debug("No previous page Id found in session, setting it for the first time");
                    }
                }
                else
                {
                    if (prevPageId.equalsIgnoreCase(curPageId))
                    {
                        if (request.getRequestParameter(REQUEST_CLEAR_PORTLETS_MODE_AND_WINDOWSTATE_KEY) != null) {
                            request.setSessionAttribute(SESSION_PREVIOUS_PAGEID_KEY, curPageId);
                            request.setAttribute(REQUEST_CLEAR_PORTLETS_MODE_AND_WINDOWSTATE_KEY, Boolean.TRUE);
                        }
                        else {
                            if (log.isDebugEnabled()) {
                                log.debug("Previous page id is same as current page id, not clearing page state");
                            }
                        }
                    }
                    else
                    {
                        if (log.isDebugEnabled())
                        {
                            log.debug("Page Change encountered Current Page:" + curPageId + " Prev Page:" + prevPageId);
                        }
                        // Make sure we set the prevPageId in session
                        request.setSessionAttribute(SESSION_PREVIOUS_PAGEID_KEY, curPageId);
                        // inform NavigationalState object we want to clear all Modes
                        request.setAttribute(REQUEST_CLEAR_PORTLETS_MODE_AND_WINDOWSTATE_KEY, Boolean.TRUE);
                    }
                }
            }
            catch (Exception e)
            {
                throw new PipelineException(e);
            }
        }
        // Pass control to the next Valve in the Pipeline
        context.invokeNext(request);

    }

    public String toString()
    {
        return "PageHistoryValve";
    }

    public void setValveDisabled(boolean valveDisabled)
    {
        this.valveDisabled = valveDisabled;
    }

    public boolean isValveDisabled()
    {
        return valveDisabled;
    }

}
