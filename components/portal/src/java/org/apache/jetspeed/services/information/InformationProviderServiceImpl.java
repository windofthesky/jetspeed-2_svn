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
package org.apache.jetspeed.services.information;

import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pluto.factory.Factory;
import org.apache.pluto.services.information.DynamicInformationProvider;
import org.apache.pluto.services.information.InformationProviderService;
import org.apache.pluto.services.information.StaticInformationProvider;

/**
 * Factory class for getting Information Provider access
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class InformationProviderServiceImpl implements Factory, InformationProviderService
{
    private javax.servlet.ServletConfig servletConfig;
    private static final Log log = LogFactory.getLog(InformationProviderServiceImpl.class);
    private final StaticInformationProvider staticInformationProvider;
    
    public InformationProviderServiceImpl(StaticInformationProvider staticInformationProvider, ServletConfig config)
    {
        this.staticInformationProvider = staticInformationProvider;
        
    }

    public void init(ServletConfig config, Map properties) throws Exception
    {
        // does nothing at all    
    }

    public StaticInformationProvider getStaticProvider()
    {        
        return staticInformationProvider;
    }

    public DynamicInformationProvider getDynamicProvider(javax.servlet.http.HttpServletRequest request)
    {
        DynamicInformationProvider provider =
            (DynamicInformationProvider) request.getAttribute("org.apache.jetspeed.engine.core.DynamicInformationProvider");

        if (provider == null)
        {
            provider = new DynamicInformationProviderImpl(request, servletConfig);
            request.setAttribute("org.apache.jetspeed.engine.core.DynamicInformationProvider", provider);
        }

        return provider;
    }

    /**
     * <p>
     * destroy
     * </p>
     *
     * @see org.apache.pluto.factory.Factory#destroy()
     * @throws java.lang.Exception
     */
    public void destroy() throws Exception
    {       
       // also does nothing
    }
}
