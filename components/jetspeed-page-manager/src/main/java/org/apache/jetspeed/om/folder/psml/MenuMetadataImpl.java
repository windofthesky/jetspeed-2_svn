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

import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;

import java.util.Collection;
import java.util.Locale;

/**
 * This class implements metadata protocols for menu
 * definition implementations.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class MenuMetadataImpl
{
    /**
     * metadata - page metadata to hold title information
     */
    private PageMetadataImpl metadata;

    /**
     * MenuDefinitionImpl - constructor
     */
    public MenuMetadataImpl()
    {
    }

    /**
     * getTitle - get default title protocol stub
     *
     * @return null
     */
    public String getTitle()
    {
        return null;
    }

    /**
     * getShortTitle - get default short title protocol stub
     *
     * @return short title text
     */
    public String getShortTitle()
    {
        return null;
    }

    /**
     * getText - get default text protocol stub
     *
     * @return text
     */
    public String getText()
    {
        return null;
    }

    /**
     * getTitle - get locale specific title from metadata
     *
     * @param locale preferred locale
     * @return title text
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

    /**
     * getShortTitle - get locale specific short title from metadata
     *
     * @param locale preferred locale
     * @return short title text
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

    /**
     * getText - get locale specific text from metadata
     *
     * @param locale preferred locale
     * @return text
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

    /**
     * getMetadata - get generic metadata instance
     *
     * @return metadata instance
     */
    public GenericMetadata getMetadata()
    {
        return getPageMetadata();
    }

    /**
     * getMetadataFields - get metadata fields collection
     *
     * @return metadata fields collection
     */
    public Collection<LocalizedField> getMetadataFields()
    {
        // return metadata fields collection that
        // may in fact be side effected on unmarshall
        return getPageMetadata().getFields();
    }

    /**
     * setMetadataFields - set metadata fields collection
     *
     * @param metadataFields metadata fields collection
     */
    public void setMetadataFields(Collection<LocalizedField> metadataFields)
    {
        // set metadata fields collection that
        // may in fact be side effected after
        // invocation on unmarshall
        getPageMetadata().setFields(metadataFields);
    }

    /**
     * getPageMetadata - get/construct page metadata instance
     *
     * @return metadata instance
     */
    private PageMetadataImpl getPageMetadata()
    {
        if (metadata == null)
        {
            metadata = new PageMetadataImpl();
        }
        return metadata;
    }

    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     */
    public void unmarshalled()
    {
        // force metadata update after unmarshalled since
        // metadata collection can be side effected by
        // unmarshalling colection accessors
        Collection<LocalizedField> metadataFields = getMetadataFields();
        if (metadataFields != null)
        {
            setMetadataFields(metadataFields);
        }
    }
}
