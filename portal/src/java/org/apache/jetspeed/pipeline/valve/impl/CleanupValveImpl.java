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
package org.apache.jetspeed.pipeline.valve.impl;

import java.util.Stack;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.Transaction;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.CleanupValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * <p>
 * CleanupValveImpl
 * </p>
 * 
 * All this valve does right now is look for JSP pages that were
 * pushed onto the <code>org.apache.jetspeed.renderStack</code>
 * request attribute, and attempts to includde them.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class CleanupValveImpl extends AbstractValve implements CleanupValve
{

    public static final String RENDER_STACK_ATTR = "org.apache.jetspeed.renderStack";

    private static final Log log = LogFactory.getLog(CleanupValveImpl.class);

    private PersistenceStore persistenceStore;
    
    public CleanupValveImpl(PersistenceStore persistenceStore)
    {
        this.persistenceStore = persistenceStore;
    }

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {

        // Complete any renderings that are on the rendering stack 

        // TODO: we should abstract the rendering as we will
        // want to eventually support other types of templates
        // other than JSPs.
        HttpServletRequest httpRequest = request.getRequest();
        Stack renderStack = (Stack) httpRequest.getAttribute(RENDER_STACK_ATTR);
        String fragment = null;
        try
        {
            if (renderStack != null)
            {
                while (!renderStack.empty())
                {
                    fragment = (String) renderStack.pop();
                    RequestDispatcher rd = httpRequest.getRequestDispatcher(fragment);
                    rd.include(httpRequest, request.getResponse());
                }
            }
        }
        catch (Exception e)
        {
            log.error("CleanupValveImpl: failed while trying to render fragment " + fragment);
            log.error("CleanupValveImpl: Unable to complete all renderings", e);
        }        
        
        try
        {
            if(persistenceStore != null)
            {
                Transaction tx = persistenceStore.getTransaction();
                if(tx.isOpen())
                {
                    tx.commit();
                }                
                persistenceStore.close();
            }
        }
        catch (Exception e1)
        {
            log.warn("Error closing out current request's PersistenceStore: "+e1.toString(), e1);
        }

    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "CleanupValveImpl";
    }

}
