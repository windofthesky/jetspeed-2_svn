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
package org.apache.jetspeed.profiler.impl;

import org.apache.jetspeed.profiler.ProfileLocatorProperty;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;

/**
 * ProfileLocatorElement
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ProfileLocatorPropertyImpl implements ProfileLocatorProperty
{
    private String name;
    private String value;
    private String type;
    private int fallbackType;
        
    public ProfileLocatorPropertyImpl(RuleCriterion criterion, String value)
    {
        this.name = criterion.getName();
        this.value = value;
        this.type = criterion.getType();
        this.fallbackType = criterion.getFallbackType();
    }
    
    public ProfileLocatorPropertyImpl(String name, String value)
    {
        this.name = name;
        this.value = value;
        this.type = ProfilingRule.STANDARD;
        this.fallbackType = RuleCriterion.FALLBACK_CONTINUE;
    }

    /**
     * @return
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param string
     */
    public void setValue(String value)
    {
        this.value = value;
    }


    /**
     * @return
     */
    public int getFallbackType()
    {
        return fallbackType;
    }

    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param i
     */
    public void setFallbackType(int i)
    {
        fallbackType = i;
    }

    /**
     * @param string
     */
    public void setName(String string)
    {
        name = string;
    }

    /**
     * @param string
     */
    public void setType(String string)
    {
        type = string;
    }

}
