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
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;

/**
 * This class implements the MenuSeparatorDefinition
 * interface in a persistent object form for use by
 * the page manager component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class MenuSeparatorDefinitionImpl extends MenuMetadataImpl implements MenuSeparatorDefinition 
{
    /**
     * skin - skin name for separator
     */
    private String skin;
    
    /**
     * title - title for separator
     */
    private String title;

    /**
     * text - text for separator
     */
    private String text;

    /**
     * MenuSeparatorDefinitionImpl - constructor
     */
    public MenuSeparatorDefinitionImpl()
    {
    }

    /**
     * getSkin - get skin name for separator
     *
     * @return skin name
     */
    public String getSkin()
    {
        return skin;
    }

    /**
     * setSkin - set skin name for separator
     *
     * @param name skin name
     */
    public void setSkin(String name)
    {
        skin = name;
    }

    /**
     * getTitle - get default title for separator
     *
     * @return title text
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * setTitle - set default title for separator
     *
     * @param title title text
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * getText - get default text for separator
     *
     * @return text
     */
    public String getText()
    {
        return text;
    }

    /**
     * setText - set default text for separator
     *
     * @param text text
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * getTextChild - get default text for separator
     *
     * @return text
     */
    public String getTextChild()
    {
        // return text as child if any other child of
        // separator is defined
        if ((getTitle() != null) || (getMetadata() != null))
        {
            return getText();
        }
        return null;
    }

    /**
     * setTextChild - set default text for separator
     *
     * @param text text
     */
    public void setTextChild(String text)
    {
        // make sure text is non-null since it is being unmarshalled
        if (text != null)
        {
            setText(text);
        }
    }

    /**
     * getTextBody - get default text for separator
     *
     * @return text
     */
    public String getTextBody()
    {
        // return text as body only if no other child of
        // separator is defined
        if ((getTitle() == null) && (getMetadata() == null))
        {
            return getText();
        }
        return null;
    }

    /**
     * setTextBody - set default text for separator
     *
     * @param text text
     */
    public void setTextBody(String text)
    {
        // make sure text is non-null and non-whitespace since
        // it is being unmarshalled
        if (text != null)
        {
            text = text.trim();
            if (text.length() > 0)
            {
                setText(text);
            }
        }
    }

    public boolean equals(Object obj)
    {
        if (!(obj instanceof MenuSeparatorDefinition))
        {
            return false;
        }
        else
        {
            MenuSeparatorDefinition definition = (MenuSeparatorDefinition) obj;
            if (!StringUtils.equals(definition.getTitle(), title)|| !StringUtils.equals(definition.getText(),text))
            {
                return false;
            }
            return true;
        }
    }    
}
