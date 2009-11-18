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
package org.apache.jetspeed.om.page.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.GenericMetadataImpl;
import org.apache.jetspeed.om.portlet.LocalizedField;

/**
 * Immutable content metadata implementation.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class ContentGenericMetadataImpl extends GenericMetadataImpl implements GenericMetadata
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.GenericMetadata#addField(java.util.Locale, java.lang.String, java.lang.String)
     */
    public void addField(Locale locale, String name, String value)
    {
        throw new UnsupportedOperationException("GenericMetadata.addField()");
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.GenericMetadata#addField(org.apache.jetspeed.om.portlet.LocalizedField)
     */
    public void addField(LocalizedField field)
    {
        throw new UnsupportedOperationException("GenericMetadata.addField()");
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.GenericMetadata#copyFields(java.util.Collection)
     */
    public void copyFields(Collection fields)
    {
        throw new UnsupportedOperationException("GenericMetadata.copyFields()");
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.GenericMetadata#createLocalizedField()
     */
    public LocalizedField createLocalizedField()
    {
        throw new UnsupportedOperationException("GenericMetadata.createLocalizedField()");
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.GenericMetadataImpl#getFields()
     */
    public Collection getFields()
    {
        Collection fields = super.getFields();
        if (fields == null)
        {
            fields = new ArrayList();
            super.setFields(fields);
        }
        return fields;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.GenericMetadata#setFields(java.lang.String, java.util.Collection)
     */
    public void setFields(String name, Collection values)
    {
        throw new UnsupportedOperationException("GenericMetadata.setFields()");        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.GenericMetadata#setFields(java.util.Collection)
     */
    public void setFields(Collection fields)
    {
        throw new UnsupportedOperationException("GenericMetadata.setFields()");        
    }
}
