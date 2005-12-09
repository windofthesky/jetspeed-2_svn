/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.jetspeed.om.portlet.impl;

import java.util.Locale;

import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.impl.GenericMetadataImpl;

/**
 * PortletDefinitionMetadataImpl
 * <br/>
 * PortletDefinition specific class for Metadata 
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 *
 */
public class PortletDefinitionMetadataImpl extends GenericMetadataImpl
{

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#addField(java.util.Locale, java.lang.String, java.lang.String)
     */
    public void addField(Locale locale, String name, String value)
    {
        PortletDefinitionLocalizedFieldImpl field = new PortletDefinitionLocalizedFieldImpl();
        field.setName(name);
        field.setValue(value);
        field.setLocale(locale);
        
        addField(field);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#createLocalizedField()
     */
    public LocalizedField createLocalizedField() {
        return new PortletDefinitionLocalizedFieldImpl();
    }
}
