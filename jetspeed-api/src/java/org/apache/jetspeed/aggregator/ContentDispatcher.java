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

/**
 * <p>The ContentDispatcher allows customer classes to retrieved
 *    rendered content for a specific fragment</p>
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public interface ContentDispatcher
{
    /**
     * Include in the provided PortletResponse output stream the rendered content
     * of the request fragment.
     * If the fragment rendered content is not yet available, the method will
     * hold until it's completely rendered.
     * @throws FailedToRenderFragmentException if the Fragment to include could not be rendered.
     */
    public void include(Fragment fragment, HttpServletRequest req, HttpServletResponse rsp) throws FailedToRenderFragmentException;
    
    /**
     * Include in the provided PortletResponse output stream the rendered content
     * of the request fragment.
     * If the fragment rendered content is not yet available, the method will
     * hold until it's completely rendered.
     * @throws FailedToRenderFragmentException if the Fragment to include could not be rendered.
     */
    public void include(Fragment fragment, javax.portlet.RenderRequest req, javax.portlet.RenderResponse rsp) throws FailedToRenderFragmentException;
    
    /**
     * Sequentially wait on content generation for the given fragment.
     * 
     * @param fragment
     */
    public void sync(Fragment fragment);
    
}
