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
package org.apache.jetspeed.portalsite.impl;

import java.util.Locale;

import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.portalsite.MenuSeparator;
import org.apache.jetspeed.portalsite.view.AbstractSiteView;

/**
 * This class implements the portal-site menu separator
 * elements constructed and returned to decorators.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class MenuSeparatorImpl extends MenuElementImpl implements MenuSeparator, Cloneable
{
    /**
     * definition - menu separator definition
     */
    private MenuSeparatorDefinition definition;

    /**
     * MenuSeparatorImpl - constructor
     *
     * @param view site view used to construct menu option
     * @param parent containing menu implementation
     * @param definition menu separator definition
     */
    public MenuSeparatorImpl(AbstractSiteView view, MenuImpl parent, MenuSeparatorDefinition definition)
    {
        super(view, parent);
        this.definition = definition;
    }

    /**
     * getElementType - get type of menu element
     *
     * @return SEPARATOR_ELEMENT_TYPE
     */
    public String getElementType()
    {
        return SEPARATOR_ELEMENT_TYPE;
    }

    /**
     * getTitle - get default title for menu element
     *
     * @return title text
     */
    public String getTitle()
    {
        // return definition title
        String title = definition.getTitle();
        if (title != null)
        {
            return title;
        }

        // return node or default title
        return super.getTitle();
    }

    /**
     * getText - get default text for menu separator
     *
     * @return text
     */
    public String getText()
    {
        // return definition text
        return definition.getText();
    }

    /**
     * getTitle - get locale specific title for menu element
     *            from metadata
     *
     * @param locale preferred locale
     * @return title text
     */
    public String getTitle(Locale locale)
    {
        // return definition short title for preferred locale
        String title = definition.getTitle(locale);
        if (title != null)
        {
            return title;
        }

        // return node or default title for preferred locale
        return super.getTitle(locale);
    }

    /**
     * getText - get locale specific text for menu separator
     *           from metadata
     *
     * @param locale preferred locale
     * @return text
     */
    public String getText(Locale locale)
    {
        // return definition text
        return definition.getText(locale);
    }

    /**
     * getMetadata - get generic metadata for menu element
     *
     * @return metadata
     */    
    public GenericMetadata getMetadata()
    {
        // return definition metadata
        GenericMetadata metadata = definition.getMetadata();
        if ((metadata != null) && (metadata.getFields() != null) && !metadata.getFields().isEmpty())
        {
            return metadata;
        }

        // return node metadata
        return super.getMetadata();
    }

    /**
     * getSkin - get skin name for menu element
     *
     * @return skin name
     */
    public String getSkin()
    {
        // get skin from definition or inherit from parent menu
        String skin = definition.getSkin();
        if (skin == null)
        {
            skin = super.getSkin();
        }
        return skin;
    }
}
