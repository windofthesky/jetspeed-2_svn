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

import java.io.Serializable;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.ProfilingRule;

/**
 * User state.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ProfileRuleForm
       implements Serializable
{
    private transient Profiler profiler = null;
    private transient ProfilingRule rule = null;
    
    public ProfileRuleForm()
    {
        Map appMap = (Map)FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
        profiler = (Profiler)appMap.get(PortletApplicationResources.CPS_PROFILER_COMPONENT);
        Map params = (Map)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String selected = (String)params.get("selectedRule");
        if (selected != null && profiler != null)
        {
            rule = profiler.getRule(selected);
        }        
    }
    
    public void listen(ActionEvent event)
    {
    }
    
    public String getTitle()
    {        
        if (rule == null)
        {
            return "{empty}";
        }
        return rule.getTitle();
    }

    public String getId()
    {
        if (rule == null)
        {
            return "{empty}";
        }        
        return rule.getId();
    }
    
    
}