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
import java.util.Iterator;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.jetspeed.portlets.pam.PortletApplicationResources;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;


/**
 * Criterion state.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ProfileCriterionForm implements Serializable
{
    private boolean isNew = false;
    private transient Profiler profiler = null;
    private transient RuleCriterion criterion = null;
    private transient ProfilingRule rule = null;
    
    private static final String FALLBACK_CONTINUE = "Continue";
    private static final String FALLBACK_STOP = "Stop";
    private static final String FALLBACK_LOOP = "Loop";
    
    private transient SelectItem[] resolvers =
    {
            new SelectItem("request"),
            new SelectItem("session"),
            new SelectItem("request.session"),
            new SelectItem("hard.coded"),
            new SelectItem("group.role.user"),
            new SelectItem("user"),
            new SelectItem("group"),
            new SelectItem("role"),
            new SelectItem("mediatype"),
            new SelectItem("country"),
            new SelectItem("language"),
            new SelectItem("roles"),
            new SelectItem("path"),
            new SelectItem("page"),
            new SelectItem("path.session"),
            new SelectItem("user.attribute"),
            new SelectItem("navigation")
    };

    private transient SelectItem[] fallbackTypes =
    {
            new SelectItem(FALLBACK_CONTINUE),
            new SelectItem(FALLBACK_LOOP),
            new SelectItem(FALLBACK_STOP)
    };
    
    public ProfileCriterionForm()
    {
    }
    
    public boolean getUpdating()
    {
        return !isNew;
    }

    public SelectItem[] getResolvers()
    {
        return resolvers;
    }

    public SelectItem[] getFallbackTypes()
    {
        return fallbackTypes;
    }
    
    public void listen(ActionEvent event)
    {        
        Map appMap = (Map)FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
        profiler = (Profiler)appMap.get(PortletApplicationResources.CPS_PROFILER_COMPONENT);
        Map params = (Map)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String selectedRule = (String)params.get("selectedRule");
        
        if (selectedRule != null && profiler != null)
        {
            rule = profiler.getRule(selectedRule);
            if (rule != null)
            {
                String selected = (String)params.get("selectedCriterion");
                if (selected == null || selected.length() == 0)
                {
                    isNew = true;
                    try
                    {
                        Class defaultClass = profiler.getClass().getClassLoader().loadClass("org.apache.jetspeed.profiler.rules.impl.RuleCriterionImpl");
                        this.criterion = (RuleCriterion)defaultClass.newInstance();
                    }
                    catch (Exception e)
                    {
                        System.out.println("Failed to CREATE NEW: rule = " + rule.getId());
                        // TODO: forward to an error page            
                    }
                }
                else
                {
                    Iterator it = rule.getRuleCriteria().iterator();
                    while (it.hasNext())
                    {
                        RuleCriterion c = (RuleCriterion)it.next();
                        if (c.getName().equals(selected))
                        {
                            criterion = c;
                            isNew = false;
                            break;
                        }                    
                    }
                }                
            }            
        }
    }

    public String getName()
    {        
        if (criterion == null)
        {
            return "{empty}";
        }
        return criterion.getName();
    }

    public void setName(String name)
    {
        if (criterion != null)
        {
            this.criterion.setName(name);
        }        
    }

    public String getValue()
    {        
        if (criterion == null)
        {
            return "{empty}";
        }
        return criterion.getValue();
    }

    public void setValue(String value)
    {
        if (criterion != null)
        {
            this.criterion.setValue(value);
        }        
    }
    
    public String getResolver()
    {        
        if (criterion == null)
        {
            return "{empty}";
        }
        return criterion.getType();
    }

    public void setResolver(String resolver)
    {
        if (criterion != null)
        {
            this.criterion.setType(resolver);
        }        
    }

    public int getFallbackOrder()
    {        
        if (criterion == null)
        {
            return 0;
        }
        return criterion.getFallbackOrder();
    }

    public void setFallbackOrder(int order)
    {
        if (criterion != null)
        {
            this.criterion.setFallbackOrder(order);
        }        
    }

    public String getFallbackType()
    {        
        if (criterion == null)
        {
            return FALLBACK_CONTINUE;
        }
        int type = criterion.getFallbackType();
        switch (type)
        {
        case RuleCriterion.FALLBACK_CONTINUE:
            return FALLBACK_CONTINUE;
        case RuleCriterion.FALLBACK_LOOP:
            return FALLBACK_LOOP;
        default:
            return FALLBACK_STOP;
        }
    }
    
    public void setFallbackType(String type)
    {
        if (criterion != null)
        {
            if (type.equals(FALLBACK_CONTINUE))
            {
                this.criterion.setFallbackType(RuleCriterion.FALLBACK_CONTINUE);                    
            }
            else if (type.equals(FALLBACK_LOOP))
            {
                this.criterion.setFallbackType(RuleCriterion.FALLBACK_LOOP);                    
            }
            else
            {
                this.criterion.setFallbackType(RuleCriterion.FALLBACK_STOP);                                    
            }            
        }        
    }
    
    // actions
    public String saveCriterion()
    {
        try
        {
            if (isNew)
            {                
                criterion.setRuleId(rule.getId());
                rule.getRuleCriteria().add(criterion);
            }
           profiler.storeProfilingRule(rule);
           isNew = false;
        }
        catch (Exception e)
        {
            // TODO: handle errors better
            System.out.println("Failed to UPDATE: rule = " + rule.getId());
            return "gotoCriterionForm";
        }
        return "returnFromCriterion";        
    }

    public String removeCriterion()
    {
        try
        {
            if (!isNew)
            {                
                rule.getRuleCriteria().remove(criterion);
                profiler.storeProfilingRule(rule);
                isNew = true;
            }
        }
        catch (Exception e)
        {
            // TODO: handle errors better
            System.out.println("Failed to UPDATE: rule = " + rule.getId());
            return "gotoCriterionForm";
        }
        return "returnFromCriterion";        
    }
    
}
