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
package org.apache.jetspeed.portlets.profiler;

// import java.io.Serializable;
import java.util.Collection;
//import java.util.LinkedList;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.profiler.Profiler;

/**
 * Profile Rules Extent
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ProfileRulesExtent // implements Serializable
{
    
    public Collection getExtent()
    {
        Map appMap = (Map)FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
        Profiler profiler = (Profiler)appMap.get(CommonPortletServices.CPS_PROFILER_COMPONENT);
        return profiler.getRules(); // TODO: optimize with cache
    }
}
