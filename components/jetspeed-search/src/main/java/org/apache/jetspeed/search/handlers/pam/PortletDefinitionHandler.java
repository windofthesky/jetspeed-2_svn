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
package org.apache.jetspeed.search.handlers.pam;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.collections.MultiHashMap;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.search.AbstractObjectHandler;
import org.apache.jetspeed.search.BaseParsedObject;
import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.DisplayName;

/**
 * @author <a href="mailto: jford@apache.org">Jeremy Ford</a>
 */
public class PortletDefinitionHandler extends AbstractObjectHandler
{
    private static final String KEY_PREFIX = "PortletDefinition::";
    private static final String PORTLET_APPLICATION = "portlet_application";
    
    {
        fields.add(ParsedObject.ID);
        fields.add(PORTLET_APPLICATION);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.ObjectHandler#parseObject(java.lang.Object)
     */
    public ParsedObject parseObject(Object o)
    {
        BaseParsedObject result = null;
        if(o instanceof PortletDefinition)
        {
            result = new BaseParsedObject();
            PortletDefinition pd = (PortletDefinition)o;
            
            //need to get Locale here
            String displayNameText = pd.getDisplayNameText(JetspeedLocale.getDefaultLocale());
            result.setTitle(displayNameText);
            
            String description = pd.getDescriptionText(JetspeedLocale.getDefaultLocale());
            result.setDescription(description);
            
            result.setClassName(pd.getClass().getName());
            result.setKey(KEY_PREFIX + pd.getUniqueName());
            result.setType(ParsedObject.OBJECT_TYPE_PORTLET);
            
            //TODO: this is common to PAs as well, possible refactor
            MultiHashMap fieldMap = new MultiHashMap();
            fieldMap.put(ParsedObject.ID, pd.getPortletName());
            
            PortletApplication pa = (PortletApplication)pd.getApplication();
            fieldMap.put(PORTLET_APPLICATION, pa.getName()); 
            
            Collection mdFields = pd.getMetadata().getFields();
            for (Iterator fieldIter = mdFields.iterator(); fieldIter.hasNext();)
            {
                LocalizedField field = (LocalizedField) fieldIter.next();                
                fieldMap.put(field.getName(), field.getValue());
            }
            
            //Handle descriptions
            for (Description d : pd.getDescriptions())
            {
                fieldMap.put(ParsedObject.FIELDNAME_DESCRIPTION, d.getDescription());
            }
            
            //Handle keywords and titles
            for (DisplayName d : pd.getDisplayNames())
            {
                fieldMap.put(ParsedObject.FIELDNAME_TITLE, d.getDisplayName());
            }
            
            HashSet keywordSet = new HashSet();
            
            for (Language lang : pd.getLanguages())
            {
                fieldMap.put(ParsedObject.FIELDNAME_TITLE, lang.getTitle());
                fieldMap.put(ParsedObject.FIELDNAME_TITLE, lang.getShortTitle());
                for (String keyword : lang.getKeywordList())
                {
                    keywordSet.add(keyword);
                }
            }
            
            String[] temp = new String[keywordSet.size()];
            result.setKeywords((String[])keywordSet.toArray(temp));
            result.setFields(fieldMap);
        }
        return result;
    }
}
