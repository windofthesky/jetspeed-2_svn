/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.portals.bridges.jsf.FacesPortlet;


/**
 * This portlet is a browser over all folders and documents in the system.
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: ProfilerAdminPortlet.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class ProfilerAdminPortlet extends FacesPortlet
{
    Profiler profiler;
    
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        profiler = (Profiler) getPortletContext().getAttribute(
                CommonPortletServices.CPS_PROFILER_COMPONENT);
        if (null == profiler)
        {
            throw new PortletException("Failed to find the Profiler on portlet initialization");
        }
    }        
    
}
