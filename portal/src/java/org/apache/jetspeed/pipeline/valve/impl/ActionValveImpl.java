/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.pipeline.valve.impl;

import java.io.IOException;

import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.container.PortletContainerFactory;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ActionValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.PortletContainer;
import org.apache.pluto.PortletContainerException;
import org.apache.pluto.om.window.PortletWindow;

/**
 * <p>
 * ActionValveImpl
 * </p>
 * 
 * Default implementation of the ActionValve interface.  Expects to be
 * called after the ContainerValve has set up the appropriate action window
 * within the request context.  This should come before ANY rendering takes
 * place.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ActionValveImpl extends AbstractValve implements ActionValve
{

    private static final Log log = LogFactory.getLog(ActionValveImpl.class);

    /**
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext, org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        PortletContainer container;
        try
        {
            container = PortletContainerFactory.getPortletContainer();
            PortletWindow actionWindow = request.getActionWindow();
            if (actionWindow != null)
            {
                container.processPortletAction(
                    actionWindow,
                    request.getRequestForWindow(actionWindow),
                    request.getResponseForWindow(actionWindow));
            }
            else
            {
                log.info("No action window defined for this request");
            }
        }
        catch (PortletContainerException e)
        {
            log.fatal("Unable to retrieve portlet container!", e);
            throw new PipelineException("Unable to retrieve portlet container!", e);
        }
        catch (PortletException e)
        {
            log.warn("Unexpected PortletException in ActionValveImpl", e);
            //  throw new PipelineException("Unexpected PortletException in ActionValveImpl", e);

        }
        catch (IOException e)
        {
            log.error("Unexpected IOException in ActionValveImpl", e);
            // throw new PipelineException("Unexpected IOException in ActionValveImpl", e);
        }
        finally
        {
            // Pass control to the next Valve in the Pipeline
            context.invokeNext(request);
        }

    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        // TODO Auto-generated method stub
        return "ActionValveImpl";
    }

}
