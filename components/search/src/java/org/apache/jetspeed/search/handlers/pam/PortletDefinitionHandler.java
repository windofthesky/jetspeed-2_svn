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
package org.apache.jetspeed.search.handlers.pam;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.collections.MultiHashMap;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.search.AbstractObjectHandler;
import org.apache.jetspeed.search.BaseParsedObject;
import org.apache.jetspeed.search.ParsedObject;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.Language;

/**
 * @author <a href="mailto: jford@apache.org">Jeremy Ford</a>
 */
public class PortletDefinitionHandler extends AbstractObjectHandler
{
    private static final String KEY_PREFIX = "PortletDefinition::";

    /* (non-Javadoc)
     * @see org.apache.jetspeed.search.ObjectHandler#parseObject(java.lang.Object)
     */
    public ParsedObject parseObject(Object o)
    {
        BaseParsedObject result = null;
        if(o instanceof PortletDefinitionComposite)
        {
            result = new BaseParsedObject();
            PortletDefinitionComposite pd = (PortletDefinitionComposite)o;
            
            //Handle descriptions
            StringBuffer descBuffer = new StringBuffer();
            Iterator descIter = pd.getDescriptionSet().iterator();
            while (descIter.hasNext())
            {
                Description desc = (Description) descIter.next();
                descBuffer.append(desc.getDescription());
                descBuffer.append(" ");
            }
            
            result.setDescription(descBuffer.toString());
            
            //Handle keywords and titles
            StringBuffer titleBuffer = new StringBuffer();
            Iterator displayNameIter = pd.getDisplayNameSet().iterator();
            while (displayNameIter.hasNext())
            {
                DisplayName displayName = (DisplayName) displayNameIter.next();
                titleBuffer.append(displayName.getDisplayName());
                titleBuffer.append(" ");
            }
            
            HashSet keywordSet = new HashSet();
            
            Iterator langIter = pd.getLanguageSet().iterator();
            while (langIter.hasNext())
            {
                Language lang = (Language) langIter.next();
                titleBuffer.append(lang.getTitle());
                titleBuffer.append(" ");
                titleBuffer.append(lang.getShortTitle());
                titleBuffer.append(" ");
                
                Iterator keywordIter = lang.getKeywords();
                while (keywordIter.hasNext())
                {
                    String keyword = (String) keywordIter.next();
                    keywordSet.add(keyword);
                }
            }
            
            result.setTitle(titleBuffer.toString());
            result.setClassName(pd.getClass().getName());
            result.setKey(KEY_PREFIX + pd.getUniqueName());
            result.setType(ParsedObject.OBJECT_TYPE_PORTLET);
            
            String[] temp = new String[keywordSet.size()];
            result.setKeywords((String[])keywordSet.toArray(temp));
            
            //TODO: this is common to PAs as well, possible refactor
            MultiHashMap fieldMap = new MultiHashMap();
            
            Collection mdFields = pd.getMetadata().getFields();
            for (Iterator fieldIter = mdFields.iterator(); fieldIter.hasNext();)
            {
                LocalizedField field = (LocalizedField) fieldIter.next();
                
                fieldMap.put(field.getName(), field.getValue());
                this.fields.add(field.getName());
            }
        }
        return result;
    }
}
