/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.collections.MultiHashMap;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.LocalizedField;

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
    private transient MultiHashMap fieldMap = new MultiHashMap();
    
    
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
        fieldMap.put(field.getName(), field);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#getFields(java.lang.String)
     */
    public Collection getFields(String name)
    {
    	//TODO:  return an immutable version?
        return (Collection)fieldMap.get(name);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#setFields(java.lang.String, java.util.Collection)
     */
    public void setFields(String name, Collection values)
    {
        fieldMap.remove(name);
        
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
                fieldMap.put(field.getName(), field);
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
     * @see org.apache.jetspeed.om.common.GenericMetadata#setField(java.util.Collection)
     */
    public void setFields(Collection fields)
    {
        this.fields = fields;
        fieldMap.clear();

        if(fields != null)
        {    
            Iterator fieldIter = fields.iterator();
            while(fieldIter.hasNext())
            {
                LocalizedField field = (LocalizedField)fieldIter.next();
                fieldMap.put(field.getName(), field);
            }
        }
        
    }
}
