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
package org.apache.jetspeed.om.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;

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
    private Collection fields = null;
    private transient MultiValueMap fieldMap = null;
    
    private MultiValueMap getFieldMap(boolean create)
    {
        if (fieldMap == null && create)
        {
            synchronized(this)
            {
                if (fieldMap == null)
                {
                    fieldMap = new MultiValueMap();
                }
            }
        }
        return fieldMap;
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
        if(fields == null)
        {
            fields = new ArrayList();
        }
        
        fields.add(field);
        getFieldMap(true).put(field.getName(), field);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#getFields(java.lang.String)
     */
    public Collection getFields(String name)
    {
    	//TODO:  return an immutable version?
        MultiValueMap fieldMap = getFieldMap(false);
        return (Collection)(fieldMap !=null ? fieldMap.get(name) : null);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#setFields(java.lang.String, java.util.Collection)
     */
    public void setFields(String name, Collection values)
    {
        MultiValueMap fieldMap = getFieldMap(false);
        if (fieldMap != null)
        {
            fieldMap.remove(name);
        }
        
        Iterator fieldIter = fields.iterator();
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
            Iterator iter = values.iterator();
            while(iter.hasNext())
            {
                LocalizedField field = (LocalizedField)iter.next();
                getFieldMap(true).put(field.getName(), field);
            }
            
            fields.addAll(values);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#getFields()
     */
    public Collection getFields() {
        return fields;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#setFields(java.util.Collection)
     */
    public void setFields(Collection fields)
    {
        this.fields = fields;

        MultiValueMap fieldMap = getFieldMap(false);
        if (fieldMap != null)
        {
            fieldMap.clear();
        }
        
        if(fields != null)
        {    
            Iterator fieldIter = fields.iterator();
            while(fieldIter.hasNext())
            {
                LocalizedField field = (LocalizedField)fieldIter.next();
                if (field.getName() != null)
                {
                    getFieldMap(true).put(field.getName(), field);
                }
            }
        }
        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#copyFields(java.util.Collection)
     */
    public void copyFields(Collection fields)
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
                this.fields = new ArrayList();
            }
            // copy unique new metadata members
            Iterator fieldIter = fields.iterator();
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
        MultiValueMap fieldMap = getFieldMap(false);
        if (fieldMap != null)
        {
            fieldMap.clear();
        }
        
        if (this.fields != null)
        {    
            Iterator fieldIter = this.fields.iterator();
            while (fieldIter.hasNext())
            {
                LocalizedField field = (LocalizedField)fieldIter.next();
                getFieldMap(true).put(field.getName(), field);
            }
        }
    }
}
