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
import java.util.Collection;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.ProfilerException;
import org.apache.jetspeed.profiler.rules.ProfilingRule;

/**
 * Profile state.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ProfileRuleForm
       implements Serializable
{
    private boolean isNew = false;
    private transient Profiler profiler = null;
    private transient ProfilingRule rule = null;
    private transient SelectItem[] classnames =
    {
            new SelectItem("org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule"),
            new SelectItem("org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule")            
    };
    
    public ProfileRuleForm()
    {
    }
    
    public boolean getUpdating()
    {
        return !isNew;
    }
    
    public void listen(ActionEvent event)
    {        
        Map appMap = (Map)FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
        profiler = (Profiler)appMap.get(PortletApplicationResources.CPS_PROFILER_COMPONENT);
        Map params = (Map)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String selected = (String)params.get("selectedRule");
        if (selected != null && profiler != null)
        {
            rule = profiler.getRule(selected);
            isNew = false;
        }
    }
    
    public SelectItem[] getClassnames()
    {
        return classnames;
    }
    
    public String getTitle()
    {        
        if (rule == null)
        {
            return "{empty}";
        }
        return rule.getTitle();
    }

    public void setTitle(String title)
    {
        if (rule != null)
        {
            this.rule.setTitle(title);
        }        
    }
    
    public String getClassname()
    {        
        if (rule == null)
        {
            return "{empty}";
        }
        return rule.getClassname();
    }

    public void setClassname(String classname)
    {
        if (rule != null)
        {
            this.rule.setClassname(classname);
        }        
    }
    
    public String getId()
    {
        if (rule == null)
        {
            return "{empty}";
        }        
        return rule.getId();
    }
    
    public void setId(String id)
    {
        if (rule != null)
        {
            this.rule.setId(id);
        }        
    }
    
    // actions
    
    public String saveProfile()
    {
        try
        {
            profiler.storeProfilingRule(this.rule);
            isNew = false;
        }
        catch (ProfilerException e)
        {
            System.out.println("Failed to UPDATE: rule = " + rule.getId());
            // TODO: forward to an error page
        }
        return null;
    }

    public String removeProfile()
    {
        try
        {
            profiler.deleteProfilingRule(rule);
        }
        catch (ProfilerException e)
        {
            System.out.println("Failed to REMOVE: rule = " + rule.getId());
            // TODO: forward to an error page
        }
        return null;
    }

    public String createNewProfile()
    {
        try
        {
            Class defaultClass = profiler.getClass().getClassLoader().loadClass("org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule");
            this.rule = (ProfilingRule)defaultClass.newInstance();
        }
        catch (Exception e)
        {
            System.out.println("Failed to CREATE NEW: rule = " + rule.getId());
            // TODO: forward to an error page            
        }
        this.setId("");
        this.setTitle("");
        this.setClassname("org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule");
        isNew = true;
        return null;
    }
    
    public Collection getCriteria()
    {
        return rule.getRuleCriteria();        
    }
}