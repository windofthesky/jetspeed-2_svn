/*
 * Copyright 2000-2005 The Apache Software Foundation.
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
package org.apache.jetspeed.om.page;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.impl.GenericMetadataImpl;
import org.apache.jetspeed.util.ArgUtil;

/**
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public class PageMetadataImpl extends GenericMetadataImpl
{
    private Class fieldImplClass = PageLocalizedFieldImpl.class;

    public PageMetadataImpl()
    {
    }

    public PageMetadataImpl(Class fieldImplClass)
    {
        this();
        this.fieldImplClass = fieldImplClass;
    }

    /**
     * localizedText - cached text metadata
     */
    private Map localizedText;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#createLocalizedField()
     */
    public LocalizedField createLocalizedField()
    {
        try
        {
            return (LocalizedField)fieldImplClass.newInstance();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to create LocalizedField object: " + fieldImplClass.getName(), e);
        }
    }

    /**
     * getText - get localized text from metadata
     * 
     * @param name text name
     * @param locale preferred locale
     * @return localized text or null if not available
     */
    public String getText(String name, Locale locale)
    {
        // validate parameters
        ArgUtil.assertNotNull(String.class, name, this, "getText(String, Locale)");
        ArgUtil.assertNotNull(Locale.class, locale, this, "getText(String, Locale)");

        // populate cache for named text by locale
        Map namedLocalizedText = (Map)((localizedText != null) ? localizedText.get(name) : null);
        if ((namedLocalizedText == null) && (getFields() != null))
        {
            Collection fields = getFields(name);
            if (fields != null)
            {
                if (localizedText == null)
                {
                    localizedText = new HashMap(getFields().size());
                }
                namedLocalizedText = new HashMap(getFields().size());
                localizedText.put(name, namedLocalizedText);
                Iterator fieldsItr = fields.iterator();
                while (fieldsItr.hasNext())
                {
                    LocalizedField field = (LocalizedField)fieldsItr.next();
                    namedLocalizedText.put(field.getLocale(), field);
                }
            }
        }

        // retrieve cached named text by locale if found
        if (namedLocalizedText != null)
        {
            // test locale
            if (namedLocalizedText.containsKey(locale) )
            {
                return ((LocalizedField)namedLocalizedText.get(locale)).getValue().trim();
            }
            // test language only locale
            Locale languageOnly = new Locale(locale.getLanguage());
            if (namedLocalizedText.containsKey(languageOnly))
            {
                return ((LocalizedField)namedLocalizedText.get(languageOnly)).getValue().trim();
            }
        }
        return null;
    }
}
