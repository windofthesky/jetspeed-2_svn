/*
 * Copyright 2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.portals.bridges.myfaces;

import java.io.IOException;
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PortletViewHandlerImpl extends ViewHandler
{
    /** The Log instance for this class. */
    private static final Log log = LogFactory.getLog(PortletViewHandlerImpl.class);

    /** The ViewHandler. */
    private ViewHandler handler;

    /**
     * <p>Construct a new <code>ViewHandler</code> instance that delegates
     * non-portlet-specific behavior to the specified implementation.
     * 
     * @param handler The <code>ViewHandler</code> instance
     */
    public PortletViewHandlerImpl(ViewHandler handler)
    {
        if (log.isInfoEnabled())
        {
            log.info("Delegating to " + handler + "");
        }
        this.handler = handler;
    }
    
    public Locale calculateLocale(FacesContext facesContext)
    {
        return handler.calculateLocale(facesContext);
    }

    public String calculateRenderKitId(FacesContext facesContext)
    {
        return handler.calculateRenderKitId(facesContext);
    }

    public UIViewRoot createView(FacesContext facesContext, String viewId)
    {
        return handler.createView(facesContext, viewId);
    }

    public String getActionURL(FacesContext facesContext, String viewId)
    {
        Object response = facesContext.getExternalContext().getResponse();
        if (!(response instanceof RenderResponse)) {
            throw new IllegalStateException("Must be a RenderResponse");
        }
        RenderResponse renderResponse = (RenderResponse) response;
        PortletURL actionURL = renderResponse.createActionURL();
        return (actionURL.toString());
    }
    
    public String getResourceURL(FacesContext facesContext, String path)
    {
        return handler.getResourceURL(facesContext, path);
    }
    
    public void renderView(FacesContext facesContext, UIViewRoot viewToRender) throws IOException, FacesException
    {
        handler.renderView(facesContext, viewToRender);
    }
    
    public UIViewRoot restoreView(FacesContext facesContext, String viewId)
    {
        return handler.restoreView(facesContext, viewId);
    }

    public void writeState(FacesContext facesContext) throws IOException
    {
        handler.writeState(facesContext);
    }

}
