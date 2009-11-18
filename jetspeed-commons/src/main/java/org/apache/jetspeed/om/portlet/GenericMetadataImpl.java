/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.portlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.util.ArgUtil;

/**
 * GenericMetadataImpl
 * <br/>
 * Implementation that allows retrieving localized information 
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 *
 */
public abstract class GenericMetadataImpl implements GenericMetadata
{   
    private Collection<LocalizedField> fields = null;

    private transient Map<String,Collection<LocalizedField>> fieldMap = null;

    private transient Map<String,Map<Locale,LocalizedField>> localizedText = null;
    
    private Map<String,Collection<LocalizedField>> getFieldMap(boolean create)
    {
        if (fieldMap == null && create)
        {
            synchronized(this)
            {
                if (fieldMap == null)
                {
                    fieldMap = new HashMap<String,Collection<LocalizedField>>();
                }
            }
        }
        return fieldMap;
    }
    
    private void addFieldMap(LocalizedField field)
    {
        Map<String,Collection<LocalizedField>> fieldMap = getFieldMap(true);
        String fieldMapKey = field.getName();
        Collection<LocalizedField> fields = fieldMap.get(fieldMapKey);
        if (fields == null)
        {
            fields = new ArrayList<LocalizedField>();
            fieldMap.put(fieldMapKey, fields);
        }
        fields.add(field);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#addField(java.util.Locale, java.lang.String, java.lang.String)
     */
    public void addField(Locale locale, String name, String value)
    {
        LocalizedField field = createLocalizedField();
        field.setName(name);
        field.setValue(value);
        field.setLocale(locale);
        
        addField(field);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#addField(org.apache.jetspeed.om.common.LocalizedField)
     */
    public void addField(LocalizedField field)
    {
        if (fields == null)
        {
            fields = new ArrayList<LocalizedField>();
        }        
        fields.add(field);
        
        addFieldMap(field);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#getFields(java.lang.String)
     */
    public Collection<LocalizedField> getFields(String name)
    {
    	//TODO:  return an immutable version?
        Map<String,Collection<LocalizedField>> fieldMap = getFieldMap(false);
        return (fieldMap !=null ? fieldMap.get(name) : null);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#setFields(java.lang.String, java.util.Collection)
     */
    public void setFields(String name, Collection<LocalizedField> values)
    {
        Map<String,Collection<LocalizedField>> fieldMap = getFieldMap(false);
        if (fieldMap != null)
        {
            fieldMap.remove(name);
        }
        
        Iterator<LocalizedField> fieldIter = fields.iterator();
        while(fieldIter.hasNext())
        {
            LocalizedField field = (LocalizedField)fieldIter.next();
            if(field != null && field.getName() != null && field.getName().equals(name))
            {
                fieldIter.remove();
            }
        }
        
        if(values != null)
        {    
            Iterator<LocalizedField> iter = values.iterator();
            while(iter.hasNext())
            {
                LocalizedField field = (LocalizedField)iter.next();
                addFieldMap(field);
            }
            
            fields.addAll(values);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#getFields()
     */
    public Collection<LocalizedField> getFields() 
    {
        return fields;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#setFields(java.util.Collection)
     */
    public void setFields(Collection<LocalizedField> fields)
    {
        this.fields = fields;

        Map<String,Collection<LocalizedField>> fieldMap = getFieldMap(false);
        if (fieldMap != null)
        {
            fieldMap.clear();
        }
        
        if(fields != null)
        {    
            Iterator<LocalizedField> fieldIter = fields.iterator();
            while(fieldIter.hasNext())
            {
                LocalizedField field = (LocalizedField)fieldIter.next();
                if (field.getName() != null)
                {
                    addFieldMap(field);
                }
            }
        }
        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#copyFields(java.util.Collection)
     */
    public void copyFields(Collection<LocalizedField> fields)
    {
        // preserve matching fields during copy to
        // minimize persistent store thrash and
        // field uniqueness constraint violations
        // that may occur if identical field is
        // removed and reinserted
        if ((this.fields != null) && !this.fields.isEmpty())
        {
            // remove unique existing fields
            if (fields != null)
            {
                this.fields.retainAll(fields);
            }
            else
            {
                this.fields = null;
            }
        }
        if ((fields != null) && !fields.isEmpty())
        {
            // create new fields collection if necessary
            if (this.fields == null)
            {
                this.fields = new ArrayList<LocalizedField>();
            }
            // copy unique new metadata members
            Iterator<LocalizedField> fieldIter = fields.iterator();
            while (fieldIter.hasNext())
            {
                LocalizedField field = (LocalizedField)fieldIter.next();
                if (!this.fields.contains(field))
                {
                    addField(field.getLocale(), field.getName(), field.getValue());
                }
            }
        }
        
        // update field map
        Map<String,Collection<LocalizedField>> fieldMap = getFieldMap(false);
        if (fieldMap != null)
        {
            fieldMap.clear();
        }
        
        if (this.fields != null)
        {    
            Iterator<LocalizedField> fieldIter = this.fields.iterator();
            while (fieldIter.hasNext())
            {
                LocalizedField field = (LocalizedField)fieldIter.next();
                addFieldMap(field);
            }
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
        Map<Locale,LocalizedField> namedLocalizedText = ((localizedText != null) ? localizedText.get(name) : null);
        if ((namedLocalizedText == null) && (getFields() != null))
        {
            Collection<LocalizedField> fields = getFields(name);
            if ((fields != null) && !fields.isEmpty())
            {
                namedLocalizedText = new HashMap<Locale,LocalizedField>(getFields().size());
                if (localizedText == null)
                {
                    localizedText = Collections.synchronizedMap(new HashMap<String,Map<Locale,LocalizedField>>(getFields().size()));
                }
                localizedText.put(name, namedLocalizedText);
                Iterator<LocalizedField> fieldsItr = fields.iterator();
                while (fieldsItr.hasNext())
                {
                    LocalizedField field = fieldsItr.next();
                    namedLocalizedText.put(field.getLocale(), field);
                }
            }
        }

        // retrieve cached named text by locale if found
        if ((namedLocalizedText != null) && !namedLocalizedText.isEmpty())
        {
            // test locale
            if (namedLocalizedText.containsKey(locale) )
            {
                return namedLocalizedText.get(locale).getValue().trim();
            }
            // test language only locale
            Locale languageOnly = new Locale(locale.getLanguage());
            if (namedLocalizedText.containsKey(languageOnly))
            {
                return namedLocalizedText.get(languageOnly).getValue().trim();
            }
        }
        return null;
    }
}
