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
package org.apache.jetspeed.container.invoker;

import javax.servlet.ServletConfig;

import org.apache.pluto.invoker.PortletInvoker;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * JetspeedPortletInvoker extends Pluto's portlet invoker and extends it
 * with lifecycle management. Portlet Invokers can be pooled, and activated
 * and passivated per request cycle.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface JetspeedPortletInvoker extends PortletInvoker
{
    /**
     * Activating an invoker makes it ready to invoke portlets.
     * If an invoker's state is not activated, it can not invoke.
     * 
     * @param portletDefinition The portlet's definition that is being invoked.
     * @param servletConfig The servlet configuration of the portal. 
     * @param containerServlet
     */
    void activate(PortletDefinition portletDefinition, ServletConfig servletConfig);

    /**
     * Activating an invoker makes it ready to invoke portlets.
     * If an invoker's state is not activated, it can not invoke.
     * This second signature allows for activating with an extra property.
     * 
     * @param portletDefinition The portlet's definition that is being invoked.
     * @param servletConfig The servlet configuration of the portal. 
     * @param property Implementation specific property
     * @param containerServlet
     */
    void activate(PortletDefinition portletDefinition, ServletConfig servletConfig, String property);
    
    /**
     * Passivates an invoker, freeing it back to the invoker pool.
     * If an invoker's state is passivated, it cannot be used to invoke portlets.
     */
    void passivate();
    
    /**
     * Returns true if the state of this invoke is 'activated', and false if it is 'passivated'.
     * @return True if the current state of the invoker is 'activated' otherwise false.
     */
    boolean isActivated();
}
