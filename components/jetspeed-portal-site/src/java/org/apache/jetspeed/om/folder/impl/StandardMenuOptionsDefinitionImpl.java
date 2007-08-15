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
package org.apache.jetspeed.om.folder.impl;

import org.apache.jetspeed.om.folder.MenuOptionsDefinition;

/**
 * This abstract class implements the menu options definition
 * interface in a default manner to allow derived classes to
 * easily describe standard menu definitions.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class StandardMenuOptionsDefinitionImpl implements MenuOptionsDefinition
{
    /**
     * StandardMenuOptionsDefinitionImpl - constructor
     */
    public StandardMenuOptionsDefinitionImpl()
    {
    }

    /**
     * getOptions - get comma separated menu options
     *
     * @return option paths specification
     */
    public String getOptions()
    {
        return null;
    }

    /**
     * setOptions - set comma separated menu options
     *
     * @param options option paths specification
     */
    public void setOptions(String options)
    {
        throw new RuntimeException("StandardMenuOptionsDefinitionImpl instance immutable");
    }

    /**
     * getDepth - get depth of inclusion for folder options
     *
     * @return inclusion depth
     */
    public int getDepth()
    {
        return 0;
    }

    /**
     * setDepth - set depth of inclusion for folder options
     *
     * @param depth inclusion depth
     */
    public void setDepth(int depth)
    {
        throw new RuntimeException("StandardMenuOptionsDefinitionImpl instance immutable");
    }

    /**
     * isPaths - get generate ordered path options
     *
     * @return paths options flag
     */
    public boolean isPaths()
    {
        return false;
    }
    
    /**
     * setPaths - set generate ordered path options
     *
     * @param paths paths options flag
     */
    public void setPaths(boolean paths)
    {
        throw new RuntimeException("StandardMenuOptionsDefinitionImpl instance immutable");
    }
    
    /**
     * isRegexp - get regexp flag for interpreting options
     *
     * @return regexp flag
     */
    public boolean isRegexp()
    {
        return false;
    }

    /**
     * setRegexp - set regexp flag for interpreting options
     *
     * @param regexp regexp flag
     */
    public void setRegexp(boolean regexp)
    {
        throw new RuntimeException("StandardMenuOptionsDefinitionImpl instance immutable");
    }

    /**
     * getProfile - get profile locator used to filter options
     *
     * @return profile locator name
     */
    public String getProfile()
    {
        return null;
    }

    /**
     * setProfile - set profile locator used to filter options
     *
     * @param locatorName profile locator name
     */
    public void setProfile(String locatorName)
    {
        throw new RuntimeException("StandardMenuOptionsDefinitionImpl instance immutable");
    }

    /**
     * getOrder - get comma separated regexp ordering patterns
     *
     * @return ordering patterns list
     */
    public String getOrder()
    {
        return null;
    }

    /**
     * setOrder - set comma separated regexp ordering patterns
     *
     * @param order ordering patterns list
     */
    public void setOrder(String order)
    {
        throw new RuntimeException("StandardMenuOptionsDefinitionImpl instance immutable");
    }

    /**
     * getSkin - get skin name for options
     *
     * @return skin name
     */
    public String getSkin()
    {
        return null;
    }

    /**
     * setSkin - set skin name for options
     *
     * @param name skin name
     */
    public void setSkin(String name)
    {
        throw new RuntimeException("StandardMenuOptionsDefinitionImpl instance immutable");
    }
}
