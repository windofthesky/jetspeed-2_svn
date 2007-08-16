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
 * BaseMenuOptionsDefinitionImpl
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public abstract class BaseMenuOptionsDefinitionImpl extends BaseMenuDefinitionElement implements MenuOptionsDefinition
{
    private String options;
    private int depth;
    private boolean paths;
    private boolean regexp;
    private String profile;
    private String order;
    private String skin;
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#getOptions()
     */
    public String getOptions()
    {
        return options;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#setOptions(java.lang.String)
     */
    public void setOptions(String options)
    {
        this.options = options;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#getDepth()
     */
    public int getDepth()
    {
        return depth;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#setDepth(int)
     */
    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#isPaths()
     */
    public boolean isPaths()
    {
        return paths;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#setPaths(boolean)
     */
    public void setPaths(boolean paths)
    {
        this.paths = paths;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#isRegexp()
     */
    public boolean isRegexp()
    {
        return regexp;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#setRegexp(boolean)
     */
    public void setRegexp(boolean regexp)
    {
        this.regexp = regexp;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#getProfile()
     */
    public String getProfile()
    {
        return profile;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#setProfile(java.lang.String)
     */
    public void setProfile(String locatorName)
    {
        profile = locatorName;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#getOrder()
     */
    public String getOrder()
    {
        return order;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#setOrder(java.lang.String)
     */
    public void setOrder(String order)
    {
        this.order = order;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#getSkin()
     */
    public String getSkin()
    {
        return skin;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuOptionsDefinition#setSkin(java.lang.String)
     */
    public void setSkin(String name)
    {
        skin = name;
    }
}
