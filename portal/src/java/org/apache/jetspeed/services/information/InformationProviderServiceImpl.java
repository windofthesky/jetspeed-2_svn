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
package org.apache.jetspeed.services.information;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.engine.core.PortalContextProviderImpl;
import org.apache.jetspeed.engine.core.PortletActionProviderImpl;
import org.apache.pluto.factory.Factory;
import org.apache.pluto.services.information.DynamicInformationProvider;
import org.apache.pluto.services.information.PortalContextProvider;
import org.apache.pluto.services.information.PortletActionProvider;
import org.apache.pluto.services.information.StaticInformationProvider;

/**
 * Factory class for getting Information Provider access
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class InformationProviderServiceImpl extends BaseService implements Factory, InformationProviderServiceService
{
    private javax.servlet.ServletConfig servletConfig;
    private static final Log log = LogFactory.getLog(InformationProviderServiceImpl.class);

    public void init(javax.servlet.ServletConfig config, java.util.Map properties) throws Exception
    {
        servletConfig = config;
    }

    public void destroy() throws Exception
    {

    }

    public StaticInformationProvider getStaticProvider()
    {
        javax.servlet.ServletContext context = servletConfig.getServletContext();

        StaticInformationProvider provider =
            (StaticInformationProvider) context.getAttribute("org.apache.jetspeed.engine.core.StaticInformationProvider");

        if (provider == null)
        {
            provider = new StaticInformationProviderImpl(servletConfig);
            context.setAttribute("org.apache.engine.core.StaticInformationProvider", provider);
        }

        if (provider != null)
        {
            log.info("Static information provider " + provider.getClass().getName());
        }
        else
        {
            log.warn("A static information provider has not been defined");
        }

        return provider;
    }

    public DynamicInformationProvider getDynamicProvider(javax.servlet.http.HttpServletRequest request)
    {
        DynamicInformationProvider provider =
            (DynamicInformationProvider) request.getAttribute("org.apache.engine.core.DynamicInformationProvider");

        log.info("Dynamic information provider " + provider);

        if (provider == null)
        {
            provider = new DynamicInformationProviderImpl(request, servletConfig);
            request.setAttribute("org.apache.engine.core.DynamicInformationProvider", provider);
        }

        return provider;
    }

    public PortalContextProvider getPortalContextProvider()
    {
        javax.servlet.ServletContext context = servletConfig.getServletContext();

        PortalContextProvider provider =
            (PortalContextProvider) context.getAttribute("org.apache.engine.core.PortalContextProvider");

        if (provider == null)
        {
            provider = new PortalContextProviderImpl();
            context.setAttribute("org.apache.engine.core.PortalContextProvider", provider);
        }

        return provider;
    }

    public PortletActionProvider getPortletActionProvider(javax.servlet.http.HttpServletRequest request)
    {
        PortletActionProvider provider =
            (PortletActionProvider) request.getAttribute("org.apache.engine.core.PortletActionProvider");

        if (provider == null)
        {
            provider = new PortletActionProviderImpl(request, servletConfig);
            request.setAttribute("org.apache.engine.core.PortletActionProvider", provider);
        }

        return provider;
    }

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws InitializationException
    {
        if (!isInitialized())
        {
            setInit(true);
        }

    }

}
