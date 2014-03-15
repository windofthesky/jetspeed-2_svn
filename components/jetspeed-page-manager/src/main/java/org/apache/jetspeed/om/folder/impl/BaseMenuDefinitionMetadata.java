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

import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.page.impl.DatabasePageManagerUtils;

import java.util.Collection;
import java.util.Locale;

/**
 * BaseMenuDefinitionMetadata
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public abstract class BaseMenuDefinitionMetadata extends BaseMenuDefinitionElement 
{
    private Collection<LocalizedField> metadataFields;

    private PageMetadataImpl pageMetadata;

    /**
     * newPageMetadata
     *
     * Construct page manager specific metadata implementation.
     *
     * @param fields mutable fields collection
     * @return page metadata
     */
    public abstract PageMetadataImpl newPageMetadata(Collection<LocalizedField> fields);

    /**
     * getPageMetadata
     *
     * Get page manager specific metadata implementation.
     *
     * @return page metadata
     */
    public PageMetadataImpl getPageMetadata()
    {
        if (pageMetadata == null)
        {
            if (metadataFields == null)
            {
                metadataFields = DatabasePageManagerUtils.createCollection();
            }
            pageMetadata = newPageMetadata(metadataFields);
        }
        return pageMetadata;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getTitle()
     * @see org.apache.jetspeed.om.folder.MenuSeparatorDefinition#getTitle()
     */
    public String getTitle()
    {
        // no title available by default
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getShortTitle()
     */
    public String getShortTitle()
    {
        // no short title available by default
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuSeparatorDefinition#getText()
     */
    public String getText()
    {
        // no text available by default
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getTitle(java.util.Locale)
     * @see org.apache.jetspeed.om.folder.MenuSeparatorDefinition#getTitle(java.util.Locale)
     */
    public String getTitle(Locale locale)
    {
        // get title from metadata or use default title
        String title = getPageMetadata().getText("title", locale);
        if (title == null)
        {
            title = getTitle();
        }
        return title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getShortTitle(java.util.Locale)
     */
    public String getShortTitle(Locale locale)
    {
        // get short title from metadata or use title from metadata,
        // default short title, or default title
        String shortTitle = getPageMetadata().getText("short-title", locale);
        if (shortTitle == null)
        {
            shortTitle = getPageMetadata().getText("title", locale);
            if (shortTitle == null)
            {
                shortTitle = getShortTitle();
                if (shortTitle == null)
                {
                    shortTitle = getTitle();
                }
            }
        }
        return shortTitle;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuSeparatorDefinition#getText(java.util.Locale)
     */
    public String getText(Locale locale)
    {
        // get title from metadata or use default title
        String text = getPageMetadata().getText("text", locale);
        if (text == null)
        {
            text = getText();
        }
        return text;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.folder.MenuDefinition#getMetadata()
     * @see org.apache.jetspeed.om.folder.MenuSeparatorDefinition#getMetadata()
     */
    public GenericMetadata getMetadata()
    {
        return getPageMetadata();
    }
}
