/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.portals.bridges.jsf;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

/**
 * <p>
 * Loads the {@link PortletFacesContextImpl}
 * </p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat </a>
 */
public class FacesContextFactoryImpl extends FacesContextFactory
{
    /**
     * @see javax.faces.context.FacesContextFactory#getFacesContext(java.lang.Object,
     *      java.lang.Object, java.lang.Object, javax.faces.lifecycle.Lifecycle)
     */
    public FacesContext getFacesContext(Object config, Object request, Object response, Lifecycle lifecycle) 
            throws FacesException
    {
        if (config instanceof PortletConfig)
        {
            PortletConfig pc = (PortletConfig)config;
            PortletContext context = pc.getPortletContext();
            PortletFacesContextImpl facesContext = new PortletFacesContextImpl( 
                    (PortletContext) context,  
                    (PortletRequest) request,
                    (PortletResponse) response);
            String defaultViewName = pc.getInitParameter(FacesPortlet.PARAM_VIEW_PAGE);            
            // facesContext.resolveViewRoot(defaultViewName, (PortletRequest)request);
            return facesContext;
        }
        else
        {
            throw new FacesException("Unsupported context type " + config.getClass().getName());
        }
    }
}