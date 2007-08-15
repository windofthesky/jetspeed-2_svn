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

import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;

/**
 * BaseMenuSeparatorDefinitionImpl
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public abstract class BaseMenuSeparatorDefinitionImpl extends BaseMenuDefinitionMetadata implements MenuSeparatorDefinition 
{
    private String skin;
    private String title;
    private String text;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuSeparatorDefinition#getSkin()
     */
    public String getSkin()
    {
        return skin;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuSeparatorDefinition#setSkin(java.lang.String)
     */
    public void setSkin(String name)
    {
        skin = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuSeparatorDefinition#getTitle()
     */
    public String getTitle()
    {
        return title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuSeparatorDefinition#setTitle(java.lang.String)
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuSeparatorDefinition#getText()
     */
    public String getText()
    {
        return text;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuSeparatorDefinition#setText(java.lang.String)
     */
    public void setText(String text)
    {
        this.text = text;
    }
}
