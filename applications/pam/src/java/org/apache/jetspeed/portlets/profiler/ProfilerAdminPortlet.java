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
package org.apache.jetspeed.portlets.profiler;

import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.portals.bridges.jsf.FacesPortlet;


/**
 * This portlet is a browser over all folders and documents in the system.
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ProfilerAdminPortlet extends FacesPortlet
{
    Profiler profiler;
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        profiler = (Profiler) getPortletContext().getAttribute(
                PortletApplicationResources.CPS_PROFILER_COMPONENT);
        if (null == profiler)
        {
            throw new PortletException("Failed to find the Profiler on portlet initialization");
        }
    }        
    
}
