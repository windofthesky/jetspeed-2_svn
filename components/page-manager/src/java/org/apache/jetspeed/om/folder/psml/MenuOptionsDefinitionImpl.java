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
package org.apache.jetspeed.om.folder.psml;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;

/**
 * This class implements the MenuOptionsDefinition
 * interface in a persistent object form for use by
 * the page manager component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class MenuOptionsDefinitionImpl implements MenuOptionsDefinition
{
    /**
     * options - comma separated option paths specification for menu
     */
    private String options;

    /**
     * depth - depth of inclusion for folder options
     */
    private int depth;

    /**
     * paths - generate ordered path options for options
     */
    private boolean paths;

    /**
     * regexp - interpret specified optionsas regexp
     */
    private boolean regexp;

    /**
     * profile - profile locator name filter for options
     */
    private String profile;
    
    /**
     * order - comma separated list of ordering patterns for options
     */
    private String order;
    
    /**
     * skin - skin name for menu
     */
    private String skin;
    
    /**
     * MenuOptionsDefinitionImpl - constructor
     */
    public MenuOptionsDefinitionImpl()
    {
    }

    /**
     * getOptions - get comma separated menu options
     *
     * @return option paths specification
     */
    public String getOptions()
    {
        return options;
    }

    /**
     * setOptions - set comma separated menu options
     *
     * @param options option paths specification
     */
    public void setOptions(String options)
    {
        this.options = options;
    }

    /**
     * getDepth - get depth of inclusion for folder options
     *
     * @return inclusion depth
     */
    public int getDepth()
    {
        return depth;
    }

    /**
     * setDepth - set depth of inclusion for folder options
     *
     * @param depth inclusion depth
     */
    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    /**
     * isPaths - get generate ordered path options
     *
     * @return paths options flag
     */
    public boolean isPaths()
    {
        return paths;
    }
    
    /**
     * setPaths - set generate ordered path options
     *
     * @param paths paths options flag
     */
    public void setPaths(boolean paths)
    {
        this.paths = paths;
    }
    
    /**
     * isRegexp - get regexp flag for interpreting options
     *
     * @return regexp flag
     */
    public boolean isRegexp()
    {
        return regexp;
    }

    /**
     * setRegexp - set regexp flag for interpreting options
     *
     * @param regexp regexp flag
     */
    public void setRegexp(boolean regexp)
    {
        this.regexp = regexp;
    }

    /**
     * getProfile - get profile locator used to filter options
     *
     * @return profile locator name
     */
    public String getProfile()
    {
        return profile;
    }

    /**
     * setProfile - set profile locator used to filter options
     *
     * @param locatorName profile locator name
     */
    public void setProfile(String locatorName)
    {
        profile = locatorName;
    }

    /**
     * getOrder - get comma separated regexp ordering patterns
     *
     * @return ordering patterns list
     */
    public String getOrder()
    {
        return order;
    }

    /**
     * setOrder - set comma separated regexp ordering patterns
     *
     * @param order ordering patterns list
     */
    public void setOrder(String order)
    {
        this.order = order;
    }

    /**
     * getSkin - get skin name for options
     *
     * @return skin name
     */
    public String getSkin()
    {
        return skin;
    }

    /**
     * setSkin - set skin name for options
     *
     * @param name skin name
     */
    public void setSkin(String name)
    {
        skin = name;
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof MenuOptionsDefinition))
        {
            return false;
        }
        else
        {
            MenuOptionsDefinition definition = (MenuOptionsDefinition) obj;
            if (!StringUtils.equals(definition.getOptions(), options) || !StringUtils.equals(definition.getProfile(), profile) ||
                !StringUtils.equals(definition.getOrder(), order) || !StringUtils.equals(definition.getSkin(), skin) || definition.getDepth() != depth ||
                definition.isPaths() != paths || definition.isRegexp() != regexp)
            {
                return false;
            }
            return true;
        }
    }
}
