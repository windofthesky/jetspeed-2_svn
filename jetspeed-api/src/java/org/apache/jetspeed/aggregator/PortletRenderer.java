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
package org.apache.jetspeed.aggregator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.request.RequestContext;

/**
 * <h4>PortletRendererService<br />
 * Jetspeed-2 Rendering service.</h4>
 * <p>This service process all portlet rendering requests and interfaces with the portlet
 * container to generate the resulting markup</p>
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public interface PortletRenderer 
{
    /**
        Render the specified Page fragment.
        Result is returned in the PortletResponse.
     * @throws FailedToRenderFragmentException
     */
    public void renderNow(Fragment fragment, RequestContext request) throws FailedToRenderFragmentException;

    /**
        Render the specified Page fragment.
        Result is returned in the PortletResponse.
     * @throws FailedToRenderFragmentException
     */
    public void renderNow(Fragment fragment, HttpServletRequest request, HttpServletResponse response) throws FailedToRenderFragmentException;

    /** 
     * 
     * Render the specified Page fragment.
     * The method returns before rendering is complete, rendered content can be
     * accessed through the ContentDispatcher
     * @throws FailedToRenderFragmentException if the Fragment could not be rendered.
     */
    public void render(Fragment fragment, RequestContext request) throws  FailedToRenderFragmentException;

    /**
     * Retrieve the ContentDispatcher for the specified request
     */
    public ContentDispatcher getDispatcher(RequestContext request, boolean isParallel);

}