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
package org.apache.portals.bridges.myfaces;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

/**
 * <p>Loads the {@link PortletFacesContextImpl}</p>
 * @author <a href="dlestrat@apache.org">David Le Strat</a>
 */
public class FacesContextFactoryImpl extends FacesContextFactory
{
    public FacesContext getFacesContext(Object context,
                                        Object request,
                                        Object response,
                                        Lifecycle lifecycle)
    	throws FacesException
    {
        if (context instanceof PortletContext)
        {
            return new PortletFacesContextImpl((PortletContext)context,
                                               (PortletRequest)request,
                                               (PortletResponse)response);
        }
        else
        {
            throw new FacesException("Unsupported context type " + context.getClass().getName());
        }
    }
}
